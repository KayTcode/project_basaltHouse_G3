package controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ActivityLog;
import model.DiscountCode;
import services.ActivityLogService;
import services.AdminDiscountService;
import java.time.LocalDateTime;

@WebServlet(name = "AdminDiscountServlet", urlPatterns = {"/admin/discounts"})
public class AdminDiscountServlet extends HttpServlet {

    private final AdminDiscountService discountService = new AdminDiscountService();
    private final ActivityLogService logService = new ActivityLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            String search = request.getParameter("search");
            String filterType = request.getParameter("filterType");
            String filterStatus = request.getParameter("filterStatus");

            if (filterType == null || filterType.isEmpty()) {
                filterType = "ALL";
            }
            if (filterStatus == null || filterStatus.isEmpty()) {
                filterStatus = "ALL";
            }

            List<DiscountCode> discounts = discountService.getAllDiscounts(search, filterType, filterStatus);
            Map<String, Integer> stats = discountService.getDiscountStats();

            request.setAttribute("discounts", discounts);
            request.setAttribute("stats", stats);
            request.setAttribute("search", search);
            request.setAttribute("filterType", filterType);
            request.setAttribute("filterStatus", filterStatus);

        } catch (Exception e) {
            System.err.println("[AdminDiscountServlet.doGet] Lỗi: " + e.getMessage());
            e.printStackTrace();
        }

        request.getRequestDispatcher("/views/admin/admin_discount.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String redirectUrl = request.getContextPath() + "/admin/discounts";
        String result;
        int adminId = getAdminId(request);

        try {
            switch (action != null ? action : "") {

                case "add":
                    String addCode = request.getParameter("code");
                    result = discountService.addDiscount(
                            addCode,
                            request.getParameter("discountType"),
                            request.getParameter("discountPercent"),
                            request.getParameter("discountAmount"),
                            request.getParameter("startDate"),
                            request.getParameter("endDate"),
                            request.getParameter("description"),
                            "on".equals(request.getParameter("isActive")),
                            "on".equals(request.getParameter("isPublic")),
                            adminId
                    );
                    if ("success".equals(result)) {
                        redirectUrl += "?toast=add_success";
                        writeLog(adminId, "ADD", "Discount", 0,
                                null, "Thêm mã giảm giá: " + addCode, "SUCCESS");
                    } else {
                        redirectUrl += "?toast=add_fail";
                        writeLog(adminId, "ADD", "Discount", 0,
                                null, "Thêm mã giảm giá thất bại: " + addCode, "FAIL");
                    }
                    break;

                case "update":
                    String updateCode = request.getParameter("code");
                    String updateIdStr = request.getParameter("discountId");
                    result = discountService.updateDiscount(
                            updateIdStr,
                            updateCode,
                            request.getParameter("discountType"),
                            request.getParameter("discountPercent"),
                            request.getParameter("discountAmount"),
                            request.getParameter("startDate"),
                            request.getParameter("endDate"),
                            request.getParameter("description"),
                            "on".equals(request.getParameter("isActive")),
                            "on".equals(request.getParameter("isPublic"))
                    );
                    int updateId = parseIntSafe(updateIdStr);
                    if ("success".equals(result)) {
                        redirectUrl += "?toast=update_success";
                        writeLog(adminId, "UPDATE", "Discount", updateId,
                                "ID=" + updateId, "Cập nhật mã: " + updateCode, "SUCCESS");
                    } else {
                        redirectUrl += "?toast=update_fail";
                        writeLog(adminId, "UPDATE", "Discount", updateId,
                                "ID=" + updateId, "Cập nhật mã thất bại: " + updateCode, "FAIL");
                    }
                    break;

                case "delete":
                    String deleteIdStr = request.getParameter("discountId");
                    result = discountService.deleteDiscount(deleteIdStr);
                    int deleteId = parseIntSafe(deleteIdStr);
                    if ("success".equals(result)) {
                        redirectUrl += "?toast=delete_success";
                        writeLog(adminId, "DELETE", "Discount", deleteId,
                                "ID=" + deleteId, "Xóa mã giảm giá ID=" + deleteId, "SUCCESS");
                    } else {
                        redirectUrl += "?toast=delete_fail";
                        writeLog(adminId, "DELETE", "Discount", deleteId,
                                "ID=" + deleteId, "Xóa mã thất bại ID=" + deleteId, "FAIL");
                    }
                    break;

                default:
                    redirectUrl += "?toast=error";
            }

        } catch (Exception e) {
            System.err.println("[AdminDiscountServlet.doPost] Lỗi: " + e.getMessage());
            e.printStackTrace();
            redirectUrl += "?toast=error";
        }

        response.sendRedirect(redirectUrl);
    }

    private int getAdminId(HttpServletRequest request) {
        Object obj = request.getSession(false) != null
                ? request.getSession(false).getAttribute("currentUser")
                : null;
        if (obj instanceof dto.UserLoginDTO) {
            return ((dto.UserLoginDTO) obj).getAccountId();
        }
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
            System.err.println("[AdminDiscountServlet] writeLog error: " + e.getMessage());
        }
    }
}

