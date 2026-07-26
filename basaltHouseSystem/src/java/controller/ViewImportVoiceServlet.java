package controller;

import dto.UserLoginDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import model.ActivityLog;
import model.ImportInvoicesDetail;
import services.ActivityLogService;
import services.ImportVoiceService;
import services.StaffService;
import static services.AuthService.USER_SESSION_KEY;

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

        HashMap<String, Object> result =
                importService.getImportInvoiceDetailsById(importId);
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

        try {
            UserLoginDTO user = (UserLoginDTO) request.getSession(false)
                    .getAttribute(USER_SESSION_KEY);
            int importId = Integer.parseInt(request.getParameter("importId"));
            ImportInvoicesDetail oldInvoice = getImportInvoice(importId);
            int staffId = getStaffId(user.getAccountId());
            String status = getValidStatus(request.getParameter("status"));
            String note = trimToNull(request.getParameter("note"));
            String rejectReason = trimToNull(request.getParameter("rejectReason"));
            if ("Rejected".equals(status) && rejectReason == null) {
                throw new IllegalArgumentException(
                        "Vui lòng nhập lý do từ chối phiếu hàng.");
            }
            if (!"Rejected".equals(status)) {
                rejectReason = null;
            }

            HashMap<String, Object> result = importService.updateImportInvoice(
                    importId, status, staffId, note, rejectReason);
            if (result.containsKey("error")) {
                request.setAttribute("errorMessage", result.get("error"));
                doGet(request, response);
                return;
            }

            writeActivityLog(
                    user, importId, oldInvoice, status, note, rejectReason);
            response.sendRedirect(
                    request.getContextPath() + "/viewimportvoice?id=" + importId);
        } catch (Exception e) {
            request.setAttribute("errorMessage", e.getMessage());
            doGet(request, response);
        }
    }

    private ImportInvoicesDetail getImportInvoice(int importId) {
        HashMap<String, Object> result =
                importService.getImportInvoiceDetailsById(importId);
        if (result.containsKey("error")) {
            throw new IllegalArgumentException(result.get("error").toString());
        }
        return getInvoiceDetails(result).get(0);
    }

    private void writeActivityLog(UserLoginDTO user, int importId,
            ImportInvoicesDetail oldInvoice, String status,
            String note, String rejectReason) {
        String oldValue = buildLogValue(
                oldInvoice.getStatus(),
                oldInvoice.getInvoiceNote(),
                oldInvoice.getRejectReason());
        String newValue = buildLogValue(status, note, rejectReason);

        HashMap<String, Object> result = activityService.ctreatActiveLog(
                new ActivityLog(
                        user.getAccountId(),
                        "Update import invoice",
                        "ImportInvoice",
                        importId,
                        oldValue,
                        newValue,
                        "Success",
                        0,
                        LocalDateTime.now()));
        if (result.containsKey("error")) {
            throw new IllegalStateException(result.get("error").toString());
        }
    }

    private String buildLogValue(
            String status, String note, String rejectReason) {
        return "status=" + status
                + "; note=" + note
                + "; rejectReason=" + rejectReason;
    }

    @SuppressWarnings("unchecked")
    private List<ImportInvoicesDetail> getInvoiceDetails(
            HashMap<String, Object> result) {
        return (List<ImportInvoicesDetail>) result.get("success");
    }

    private int getStaffId(int accountId) {
        HashMap<String, Object> result =
                staffService.getStaffIdByAccountId(accountId);
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
        throw new IllegalArgumentException(
                "Trạng thái phiếu nhập không hợp lệ.");
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
