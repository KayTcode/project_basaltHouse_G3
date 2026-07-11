package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.AdminOrderService;

import java.io.IOException;
import java.util.Map;

/**
 * AdminOrderServlet — điều phối module Quản lý Đơn hàng (Admin).
 * URL mapping: /admin/orders
 * Pattern giống AdminProductServlet.
 */
@WebServlet("/admin/orders")
public class AdminOrderServlet extends HttpServlet {

    private final AdminOrderService orderService = new AdminOrderService();
    private static final int PAGE_SIZE = 10;

    // ══════════════════════════════════════════════════════════════════
    // GET — Hiển thị danh sách đơn hàng
    // ══════════════════════════════════════════════════════════════════
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Thu thập tham số từ URL
        String search        = request.getParameter("search");
        String orderType     = request.getParameter("orderType");
        String orderStatus   = request.getParameter("orderStatus");
        String paymentStatus = request.getParameter("paymentStatus");
        String pageStr       = request.getParameter("page");

        // 2. Gọi Service lấy toàn bộ data
        Map<String, Object> dashboardData = orderService.getOrderDashboardData(
                search, orderType, orderStatus, paymentStatus, pageStr, PAGE_SIZE);

        // 3. Đẩy vào request attribute
        request.setAttribute("data", dashboardData);

        System.out.println("[AdminOrderServlet] data loaded: " + dashboardData.get("totalRecords") + " đơn");

        // 4. Forward sang JSP
        request.getRequestDispatcher("/views/admin/admin_order.jsp").forward(request, response);
    }

    // ══════════════════════════════════════════════════════════════════
    // POST — Xử lý hành động (cập nhật trạng thái / xóa đơn)
    // ══════════════════════════════════════════════════════════════════
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action  = request.getParameter("action");
        String orderId = request.getParameter("orderId");

        if (action != null) {
            switch (action) {
                case "updateStatus":
                    String newStatus = request.getParameter("newStatus");
                    boolean statusOk = orderService.processUpdateStatus(orderId, newStatus);
                    if (statusOk) {
                        request.getSession().setAttribute("toastMessage",
                                "Cập nhật trạng thái đơn #" + orderId + " thành công!");
                    } else {
                        request.getSession().setAttribute("toastError",
                                "Lỗi: Không thể cập nhật trạng thái đơn #" + orderId + "!");
                    }
                    break;

                case "delete":
                    boolean deleteOk = orderService.processDeleteOrder(orderId);
                    if (deleteOk) {
                        request.getSession().setAttribute("toastMessage",
                                "Đã xóa đơn hàng #" + orderId + "!");
                    } else {
                        request.getSession().setAttribute("toastError",
                                "Lỗi: Không thể xóa đơn hàng #" + orderId + "!");
                    }
                    break;

                default:
                    break;
            }
        }

        // Sau POST, redirect về GET để tránh double-submit
        response.sendRedirect(request.getContextPath() + "/admin/orders");
    }
}
