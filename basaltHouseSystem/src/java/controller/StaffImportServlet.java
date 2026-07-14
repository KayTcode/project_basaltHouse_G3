package controller;

import dto.UserLoginDTO;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.ActivityLog;
import model.ImportDetail;
import model.ImportInvoice;
import services.ActivityLogService;
import static services.AuthService.USER_SESSION_KEY;
import services.ImportVoiceService;
import services.StaffService;
import services.StockService;

public class StaffImportServlet extends HttpServlet {

    private static final DateTimeFormatter IMPORT_CODE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private final ImportVoiceService importService = new ImportVoiceService();
    private final StaffService staffService = new StaffService();
    private final ActivityLogService activityService = new ActivityLogService();
    private final StockService stockService = new StockService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        StaffServlet.prepareStaffPage(request, "import");
        try {
            HashMap<String, Object> dashboardData = stockService.getStaffDashboardData(null, true);
            for (Map.Entry<String, Object> entry : dashboardData.entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }

            request.setAttribute("importIngredients", new ArrayList<HashMap<String, Object>>());
            String supplierValue = trimToNull(request.getParameter("supplierId"));
            if (supplierValue != null) {
                int selectedSupplierId = Integer.parseInt(supplierValue);
                if (selectedSupplierId <= 0) {
                    throw new IllegalArgumentException("Nhà cung cấp không hợp lệ.");
                }
                request.setAttribute("selectedSupplierId", selectedSupplierId);
                HashMap<String, Object> ingredientResult
                        = staffService.getIngredientsBySupplier(selectedSupplierId);
                if (ingredientResult.containsKey("error")) {
                    request.setAttribute("dataError", ingredientResult.get("error"));
                } else {
                    request.setAttribute("importIngredients", ingredientResult.get("success"));
                }
            }
        } catch (Exception e) {
            request.setAttribute("dataError", e.getMessage());
        }
        request.getRequestDispatcher("/views/Staff/Staff.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        UserLoginDTO user = session != null
                ? (UserLoginDTO) session.getAttribute(USER_SESSION_KEY)
                : null;
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (!"importIngredient".equals(request.getParameter("action"))) {
            response.sendRedirect(request.getContextPath() + "/staff/ingredient");
            return;
        }

        try {
            HashMap<String, Object> staffResult = staffService.getStaffIdByAccountId(user.getAccountId());
            if (staffResult.containsKey("error")) {
                request.setAttribute("errorMessage", staffResult.get("error").toString());
                doGet(request, response);
                return;
            }
            int staffId = (Integer) staffResult.get("success");

            String importCode = trimToNull(request.getParameter("importCode"));
            if (importCode == null) {
                importCode = "IMP-" + LocalDateTime.now().format(IMPORT_CODE_FORMAT);
            }

            String status = trimToNull(request.getParameter("status"));
            if (status == null) {
                status = "Confirmed";
            }
            String rejectReason = trimToNull(request.getParameter("rejectReason"));
            if ("Rejected".equalsIgnoreCase(status) && rejectReason == null) {
                throw new IllegalArgumentException("Vui lòng nhập lý do từ chối phiếu hàng.");
            }
            if (!"Rejected".equalsIgnoreCase(status)) {
                rejectReason = null;
            }

            int supplierId = Integer.parseInt(request.getParameter("supplierId"));
            String[] ingredientIds = request.getParameterValues("ingredientId");
            String[] orderedQuantities = request.getParameterValues("orderedQuantity");
            String[] receivedQuantities = request.getParameterValues("receivedQuantity");
            String[] unitPrices = request.getParameterValues("unitPrice");
            String[] discrepancyNotes = request.getParameterValues("discrepancyNote");
            validateDetailParameterCounts(
                    ingredientIds, orderedQuantities, receivedQuantities, unitPrices, discrepancyNotes);

            List<ImportDetail> details = new ArrayList<>();
            Set<Integer> uniqueIngredientIds = new HashSet<>();
            BigDecimal totalOrderedAmount = BigDecimal.ZERO;
            BigDecimal totalReceivedAmount = BigDecimal.ZERO;
            for (int i = 0; i < ingredientIds.length; i++) {
                int ingredientId = Integer.parseInt(ingredientIds[i]);
                if (!uniqueIngredientIds.add(ingredientId)) {
                    throw new IllegalArgumentException("Mỗi nguyên liệu chỉ được thêm một lần trong phiếu nhập.");
                }

                BigDecimal orderedQuantity = parseBigDecimal(orderedQuantities[i]);
                BigDecimal receivedQuantity = parseBigDecimal(receivedQuantities[i]);
                BigDecimal unitPrice = parseBigDecimal(unitPrices[i]);
                if (orderedQuantity.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Số lượng đặt phải lớn hơn 0.");
                }
                if (receivedQuantity.compareTo(BigDecimal.ZERO) < 0
                        || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Số lượng nhận và đơn giá không được âm.");
                }

                totalOrderedAmount = totalOrderedAmount.add(orderedQuantity.multiply(unitPrice));
                totalReceivedAmount = totalReceivedAmount.add(receivedQuantity.multiply(unitPrice));
                details.add(new ImportDetail(
                        0,
                        0,
                        ingredientId,
                        orderedQuantity,
                        receivedQuantity,
                        unitPrice,
                        trimToNull(discrepancyNotes[i]),
                        null,
                        false
                ));
            }

            ImportInvoice invoice = new ImportInvoice(
                    0,
                    importCode,
                    supplierId,
                    staffId,
                    "Pending".equalsIgnoreCase(status) ? null : staffId,
                    status,
                    parseDateTime(request.getParameter("orderedDate")),
                    parseDateTime(request.getParameter("expectedDate")),
                    parseDateTime(request.getParameter("receivedDate")),
                    trimToNull(request.getParameter("supplierInvoiceCode")),
                    totalOrderedAmount,
                    totalReceivedAmount,
                    trimToNull(request.getParameter("note")),
                    rejectReason,
                    false
            );

            HashMap<String, Object> createResult = importService.creatImportvoice(invoice, details);
            if (createResult.containsKey("error")) {
                request.setAttribute("errorMessage", createResult.get("error").toString());
                doGet(request, response);
                return;
            }

            activityService.ctreatActiveLog(new ActivityLog(user.getAccountId(),
                    "Create new Import In Voice ",
                    "ImportInVoice",
                    user.getAccountId(),
                    null,
                    importCode,
                    "Success",
                    0,
                    LocalDateTime.now()));
            response.sendRedirect(request.getContextPath() + "/staff/history?created=1");
        } catch (Exception e) {
            request.setAttribute("errorMessage", e.getMessage());
            doGet(request, response);
        }
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

    private LocalDateTime parseDateTime(String value) {
        String normalized = trimToNull(value);
        return normalized == null ? null : LocalDateTime.parse(normalized);
    }

    private BigDecimal parseBigDecimal(String value) {
        String normalized = trimToNull(value);
        return normalized == null ? BigDecimal.ZERO : new BigDecimal(normalized);
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
