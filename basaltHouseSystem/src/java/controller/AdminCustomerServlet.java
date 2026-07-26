package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import model.ActivityLog;
import model.DiscountCode;
import services.ActivityLogService;
import services.AdminCustomerService;


@WebServlet(name = "AdminCustomerServlet", urlPatterns = {"/admin/customers"})
public class AdminCustomerServlet extends HttpServlet {

    private final AdminCustomerService customerService = new AdminCustomerService();
    private final ActivityLogService logService = new ActivityLogService();
    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if ("history".equals(action)) {
            handleOrderHistory(request, response);
            return;
        }

        try {
            String search  = request.getParameter("search");
            String rankStr = request.getParameter("rankId");
            String status  = request.getParameter("status");
            String pageStr = request.getParameter("page");

            Map<String, Object> customerData = customerService
                    .getCustomerDashboardPage(search, rankStr, status, pageStr, PAGE_SIZE);

            request.setAttribute("customerData", customerData);

            // Load danh sách mã đang kích hoạt cho dropdown tặng mã
            List<DiscountCode> activeDiscounts = customerService.getActiveDiscountsForGift();
            request.setAttribute("activeDiscounts", activeDiscounts);

        } catch (Exception e) {
            System.err.println("[AdminCustomerServlet.doGet] " + e.getMessage());
        }

        request.getRequestDispatcher("/views/admin/admin_customer.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) action = "";

        switch (action) {
            case "add"          -> handleAddCustomer(request, response);
            case "update"       -> handleUpdateCustomer(request, response);
            case "giftDiscount" -> handleGiftDiscount(request, response);
            default             -> response.sendRedirect(request.getContextPath() + "/admin/customers");
        }
    }

    private void handleOrderHistory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accountIdStr = request.getParameter("accountId");
        java.util.Map<String, Object> historyData = customerService.processGetOrderHistoryPage(accountIdStr);
        request.setAttribute("historyData", historyData);
        request.getRequestDispatcher("/views/admin/admin_customer_history.jsp").forward(request, response);
    }


    private void handleAddCustomer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email     = request.getParameter("email");
        String password  = request.getParameter("password");
        String fullName  = request.getParameter("fullName");
        String phone     = request.getParameter("phone");
        String rankIdStr = request.getParameter("rankId");
        String spentStr  = request.getParameter("totalSpent");

        boolean ok = customerService.processAddCustomer(email, password, fullName, phone, rankIdStr, spentStr);
        if (ok) {
            request.getSession().setAttribute("toastMessage", "Thêm khách hàng thành công!");
            writeLog(getAdminId(request), "ADD", "Customer", 0,
                    null, "Thêm khách hàng: " + email, "SUCCESS");
        } else {
            request.getSession().setAttribute("errorMessage", "Thêm thất bại! Email có thể đã tồn tại.");
            writeLog(getAdminId(request), "ADD", "Customer", 0,
                    null, "Thêm khách hàng thất bại: " + email, "FAIL");
        }

        response.sendRedirect(request.getContextPath() + "/admin/customers");
    }

    private void handleUpdateCustomer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String accountIdStr = request.getParameter("accountId");
        String email        = request.getParameter("email");
        String fullName     = request.getParameter("fullName");
        String phone        = request.getParameter("phone");
        String rankIdStr    = request.getParameter("rankId");
        String spentStr     = request.getParameter("totalSpent");
        String isLockedStr  = request.getParameter("isLocked");

        boolean ok = customerService.processUpdateCustomer(accountIdStr, email, fullName, phone, rankIdStr, spentStr, isLockedStr);
        int accountId = parseIntSafe(accountIdStr);
        if (ok) {
            request.getSession().setAttribute("toastMessage", "Cập nhật thông tin thành công!");
            writeLog(getAdminId(request), "UPDATE", "Customer", accountId,
                    "AccountId=" + accountId, "Cập nhật khách hàng: " + email, "SUCCESS");
        } else {
            request.getSession().setAttribute("errorMessage", "Cập nhật thất bại. Vui lòng thử lại!");
            writeLog(getAdminId(request), "UPDATE", "Customer", accountId,
                    "AccountId=" + accountId, "Cập nhật khách hàng thất bại: " + email, "FAIL");
        }

        response.sendRedirect(request.getContextPath() + "/admin/customers");
    }

    private void handleGiftDiscount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String accountIdStr  = request.getParameter("accountId");
        String discountIdStr = request.getParameter("discountId");
        int accountId  = parseIntSafe(accountIdStr);
        int discountId = parseIntSafe(discountIdStr);

        String result = customerService.processGiftDiscount(accountIdStr, discountIdStr);
        switch (result) {
            case "success" -> {
                request.getSession().setAttribute("toastMessage", "✅ Tặng mã giảm giá thành công!");
                writeLog(getAdminId(request), "GIFT_VOUCHER", "Voucher", accountId,
                        "AccountId=" + accountId, "Tặng mã DiscountId=" + discountId + " cho AccountId=" + accountId, "SUCCESS");
            }
            case "already_gifted" -> {
                request.getSession().setAttribute("errorMessage", "⚠️ Khách hàng này đã có mã giảm giá này rồi!");
                writeLog(getAdminId(request), "GIFT_VOUCHER", "Voucher", accountId,
                        "AccountId=" + accountId, "Tặng mã trùng (already_gifted) DiscountId=" + discountId, "WARNING");
            }
            default -> {
                request.getSession().setAttribute("errorMessage", "❌ Tặng mã thất bại. Vui lòng thử lại!");
                writeLog(getAdminId(request), "GIFT_VOUCHER", "Voucher", accountId,
                        "AccountId=" + accountId, "Tặng mã thất bại DiscountId=" + discountId, "FAIL");
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin/customers");
    }

    // ── Helpers ──────────────────────────────────────────────────────────
    private int getAdminId(HttpServletRequest request) {
        Object obj = request.getSession(false) != null
                ? request.getSession(false).getAttribute("currentUser") : null;
        if (obj instanceof dto.UserLoginDTO) return ((dto.UserLoginDTO) obj).getAccountId();
        return 0;
    }

    private int parseIntSafe(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    }

    private void writeLog(int accountId, String action, String module,
                          int targetId, String oldValue, String newValue, String status) {
        try {
            ActivityLog log = new ActivityLog(accountId, action, module,
                    targetId, oldValue, newValue, status, 0, LocalDateTime.now());
            logService.ctreatActiveLog(log);
        } catch (Exception e) {
            System.err.println("[AdminCustomerServlet] writeLog error: " + e.getMessage());
        }
    }
}
