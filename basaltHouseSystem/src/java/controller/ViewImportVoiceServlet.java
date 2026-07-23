package controller;

import dto.UserLoginDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.ActivityLog;
import model.ImportDetail;
import model.ImportInvoicesDetail;
import services.ActivityLogService;
import static services.AuthService.USER_SESSION_KEY;
import services.ImportVoiceService;
import services.StaffService;

public class ViewImportVoiceServlet extends HttpServlet {

    private static final Set<String> ALLOWED_STATUSES = Set.of(
            "Pending", "Confirmed", "Rejected");

    private final ImportVoiceService importService = new ImportVoiceService();
    private final StaffService staffService = new StaffService();
    private final ActivityLogService activityService = new ActivityLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = trimToNull(request.getParameter("id"));
        if (idParam == null) {
            idParam = trimToNull(request.getParameter("importId"));
        }
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/staff/history");
            return;
        }

        int importId;
        try {
            importId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/staff/history");
            return;
        }

        HashMap<String, Object> result = importService.getImportInvoiceDetailsById(importId);
        if (result.containsKey("error")) {
            request.setAttribute("errorMessage", result.get("error"));
            request.getRequestDispatcher("/views/Staff/ViewImportVoice.jsp")
                    .forward(request, response);
            return;
        }

        List<ImportInvoicesDetail> invoiceDetails = getInvoiceDetails(result);
        ImportInvoicesDetail invoice = invoiceDetails.get(0);
        invoice.setIngredientCount(invoiceDetails.size());
        request.setAttribute("invoiceDetail", invoice);
        request.setAttribute("invoiceDetails", invoiceDetails);
        request.getRequestDispatcher("/views/Staff/ViewImportVoice.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        UserLoginDTO user = (UserLoginDTO) request.getSession(false)
                .getAttribute(USER_SESSION_KEY);

        try {
            int importId = Integer.parseInt(request.getParameter("importId"));
            List<ImportInvoicesDetail> oldDetails = loadInvoiceDetails(importId);
            ImportInvoicesDetail oldInvoice = oldDetails.get(0);

            String status = getValidStatus(request.getParameter("status"));
            String note = trimToNull(request.getParameter("note"));
            String rejectReason = trimToNull(request.getParameter("rejectReason"));
            validateRejectReason(status, rejectReason);
            if (!"Rejected".equals(status)) {
                rejectReason = null;
            }

            int staffId = getStaffId(user.getAccountId());
            List<ImportDetail> details = buildEditableDetails(request, importId, oldDetails);
            HashMap<String, Object> updateResult = importService.updateImportInvoice(
                    importId, status, note, rejectReason, staffId, details);
            if (updateResult.containsKey("error")) {
                request.setAttribute("errorMessage", updateResult.get("error"));
                doGet(request, response);
                return;
            }

            writeActivityLog(user, importId, oldInvoice, oldDetails,
                    status, note, rejectReason, details);
            response.sendRedirect(request.getContextPath() + "/viewimportvoice?id=" + importId);
        } catch (Exception e) {
            request.setAttribute("errorMessage", e.getMessage());
            doGet(request, response);
        }
    }

    private List<ImportInvoicesDetail> loadInvoiceDetails(int importId) {
        HashMap<String, Object> result = importService.getImportInvoiceDetailsById(importId);
        if (result.containsKey("error")) {
            throw new IllegalArgumentException(result.get("error").toString());
        }
        return getInvoiceDetails(result);
    }

    @SuppressWarnings("unchecked")
    private List<ImportInvoicesDetail> getInvoiceDetails(HashMap<String, Object> result) {
        return (List<ImportInvoicesDetail>) result.get("success");
    }

    private int getStaffId(int accountId) {
        HashMap<String, Object> result = staffService.getStaffIdByAccountId(accountId);
        if (result.containsKey("error")) {
            throw new IllegalArgumentException(result.get("error").toString());
        }
        return (Integer) result.get("success");
    }

    private String getValidStatus(String value) {
        String status = trimToNull(value);
        if (status != null) {
            for (String allowedStatus : ALLOWED_STATUSES) {
                if (allowedStatus.equalsIgnoreCase(status)) {
                    return allowedStatus;
                }
            }
        }
        throw new IllegalArgumentException("Trạng thái phiếu nhập không hợp lệ.");
    }

    private void validateRejectReason(String status, String rejectReason) {
        if ("Rejected".equals(status) && rejectReason == null) {
            throw new IllegalArgumentException("Vui lòng nhập lý do từ chối phiếu hàng.");
        }
    }

    private List<ImportDetail> buildEditableDetails(HttpServletRequest request,
            int importId, List<ImportInvoicesDetail> oldDetails) {
        String[] importDetailIds = request.getParameterValues("importDetailId");
        String[] discrepancyNotes = request.getParameterValues("discrepancyNote");
        String[] detailNotes = request.getParameterValues("detailNote");
        validateDetailParameterCounts(importDetailIds, discrepancyNotes, detailNotes);

        HashMap<Integer, ImportInvoicesDetail> oldById = indexDetails(oldDetails);
        Set<Integer> submittedIds = new HashSet<>();
        List<ImportDetail> details = new ArrayList<>();
        for (int index = 0; index < importDetailIds.length; index++) {
            int detailId = Integer.parseInt(importDetailIds[index]);
            if (!oldById.containsKey(detailId) || !submittedIds.add(detailId)) {
                throw new IllegalArgumentException("Chi tiết phiếu nhập không hợp lệ.");
            }

            ImportDetail detail = new ImportDetail();
            detail.setImportDetailId(detailId);
            detail.setImportId(importId);
            detail.setDiscrepancyNote(trimToNull(discrepancyNotes[index]));
            detail.setNote(trimToNull(detailNotes[index]));
            details.add(detail);
        }

        if (details.size() != oldDetails.size()) {
            throw new IllegalArgumentException("Không được thêm hoặc xóa nguyên liệu của phiếu nhập.");
        }
        return details;
    }

    private HashMap<Integer, ImportInvoicesDetail> indexDetails(
            List<ImportInvoicesDetail> details) {
        HashMap<Integer, ImportInvoicesDetail> indexed = new HashMap<>();
        for (ImportInvoicesDetail detail : details) {
            indexed.put(detail.getImportDetailId(), detail);
        }
        return indexed;
    }

    private void validateDetailParameterCounts(String[]... values) {
        if (values.length == 0 || values[0] == null || values[0].length == 0) {
            throw new IllegalArgumentException("Phiếu nhập phải có ít nhất một nguyên liệu.");
        }
        int detailCount = values[0].length;
        for (String[] value : values) {
            if (value == null || value.length != detailCount) {
                throw new IllegalArgumentException("Dữ liệu chi tiết phiếu nhập không hợp lệ.");
            }
        }
    }

    private void writeActivityLog(UserLoginDTO user, int importId,
            ImportInvoicesDetail oldInvoice, List<ImportInvoicesDetail> oldDetails,
            String status, String note, String rejectReason, List<ImportDetail> details) {
        StringBuilder oldValue = new StringBuilder();
        StringBuilder newValue = new StringBuilder();
        appendChange(oldValue, newValue, "status", oldInvoice.getStatus(), status);
        appendChange(oldValue, newValue, "note", oldInvoice.getInvoiceNote(), note);
        appendChange(oldValue, newValue, "rejectReason",
                oldInvoice.getRejectReason(), rejectReason);

        HashMap<Integer, ImportInvoicesDetail> oldById = indexDetails(oldDetails);
        for (ImportDetail detail : details) {
            ImportInvoicesDetail oldDetail = oldById.get(detail.getImportDetailId());
            String prefix = "detail[" + detail.getImportDetailId() + "].";
            appendChange(oldValue, newValue, prefix + "discrepancyNote",
                    oldDetail.getDiscrepancyNote(), detail.getDiscrepancyNote());
            appendChange(oldValue, newValue, prefix + "detailNote",
                    oldDetail.getDetailNote(), detail.getNote());
        }

        if (oldValue.length() == 0) {
            return;
        }

        HashMap<String, Object> logResult = activityService.ctreatActiveLog(
                new ActivityLog(
                        user.getAccountId(),
                        "Update import invoice",
                        "ImportInvoice",
                        importId,
                        oldValue.toString(),
                        newValue.toString(),
                        "Success",
                        0,
                        LocalDateTime.now()));
        if (logResult.containsKey("error")) {
            throw new IllegalStateException(logResult.get("error").toString());
        }
    }

    private void appendChange(StringBuilder oldValue, StringBuilder newValue,
            String field, String oldData, String newData) {
        String normalizedOld = trimToNull(oldData);
        String normalizedNew = trimToNull(newData);
        if (normalizedOld == null && normalizedNew == null) {
            return;
        }
        if (normalizedOld != null && normalizedOld.equals(normalizedNew)) {
            return;
        }

        oldValue.append(field).append("=").append(formatLogValue(normalizedOld)).append("; ");
        newValue.append(field).append("=").append(formatLogValue(normalizedNew)).append("; ");
    }

    private String formatLogValue(String value) {
        if (value == null) {
            return "null";
        }
        return value;
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
