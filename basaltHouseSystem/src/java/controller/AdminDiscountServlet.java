package controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DiscountCode;
import services.AdminDiscountService;

@WebServlet(name = "AdminDiscountServlet", urlPatterns = {"/admin/discounts"})
public class AdminDiscountServlet extends HttpServlet {

    private final AdminDiscountService discountService = new AdminDiscountService();

    // ── GET: hiển thị danh sách mã giảm giá ────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            String search       = request.getParameter("search");
            String filterType   = request.getParameter("filterType");
            String filterStatus = request.getParameter("filterStatus");

            if (filterType   == null || filterType.isEmpty())   filterType   = "ALL";
            if (filterStatus == null || filterStatus.isEmpty()) filterStatus = "ALL";

            List<DiscountCode> discounts = discountService.getAllDiscounts(search, filterType, filterStatus);
            Map<String, Integer> stats   = discountService.getDiscountStats();

            request.setAttribute("discounts",    discounts);
            request.setAttribute("stats",        stats);
            request.setAttribute("search",       search);
            request.setAttribute("filterType",   filterType);
            request.setAttribute("filterStatus", filterStatus);

        } catch (Exception e) {
            System.err.println("[AdminDiscountServlet.doGet] Lỗi: " + e.getMessage());
            e.printStackTrace();
        }

        request.getRequestDispatcher("/views/admin/admin_discount.jsp").forward(request, response);
    }

    // ── POST: xử lý add / update / delete ──────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action      = request.getParameter("action");
        String redirectUrl = request.getContextPath() + "/admin/discounts";
        String result;

        try {
            switch (action != null ? action : "") {

                case "add":
                    result = discountService.addDiscount(
                            request.getParameter("code"),
                            request.getParameter("discountType"),
                            request.getParameter("discountPercent"),
                            request.getParameter("discountAmount"),
                            request.getParameter("startDate"),
                            request.getParameter("endDate"),
                            request.getParameter("description"),
                            "on".equals(request.getParameter("isActive")),
                            getAdminId(request)
                    );
                    redirectUrl += "success".equals(result) ? "?toast=add_success" : "?toast=add_fail";
                    break;

                case "update":
                    result = discountService.updateDiscount(
                            request.getParameter("discountId"),
                            request.getParameter("code"),
                            request.getParameter("discountType"),
                            request.getParameter("discountPercent"),
                            request.getParameter("discountAmount"),
                            request.getParameter("startDate"),
                            request.getParameter("endDate"),
                            request.getParameter("description"),
                            "on".equals(request.getParameter("isActive"))
                    );
                    redirectUrl += "success".equals(result) ? "?toast=update_success" : "?toast=update_fail";
                    break;

                case "delete":
                    result = discountService.deleteDiscount(request.getParameter("discountId"));
                    redirectUrl += "success".equals(result) ? "?toast=delete_success" : "?toast=delete_fail";
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

    /** Lấy accountId của admin đang đăng nhập từ session. Trả về 0 nếu không có. */
    private int getAdminId(HttpServletRequest request) {
        Object obj = request.getSession(false) != null
                ? request.getSession(false).getAttribute("currentUser")
                : null;
        if (obj instanceof dto.UserLoginDTO) {
            return ((dto.UserLoginDTO) obj).getAccountId();
        }
        return 0;
    }
}
