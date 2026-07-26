package controller;

import dao.OrderDAO;
import dto.UserLoginDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.Order;


public class CancelOrderServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ctx = request.getContextPath();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(ctx + "/login");
            return;
        }

        UserLoginDTO user = (UserLoginDTO) session.getAttribute("currentUser");

        String orderIdStr = request.getParameter("orderId");
        if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
            session.setAttribute("cancelError", "Thiếu thông tin đơn hàng.");
            response.sendRedirect(ctx + "/my-orders");
            return;
        }

        int orderId;
        try {
            orderId = Integer.parseInt(orderIdStr.trim());
        } catch (NumberFormatException e) {
            session.setAttribute("cancelError", "Mã đơn hàng không hợp lệ.");
            response.sendRedirect(ctx + "/my-orders");
            return;
        }

        OrderDAO orderDAO = new OrderDAO();

        Order order = orderDAO.getOrderById(orderId);
        if (order == null) {
            session.setAttribute("cancelError", "Không tìm thấy đơn hàng #BH-" + orderId + ".");
            response.sendRedirect(ctx + "/my-orders");
            return;
        }

        int customerId = orderDAO.getCustomerIdByAccountId(user.getAccountId());
        if (customerId <= 0 || order.getCustomerId() == null || order.getCustomerId() != customerId) {
            session.setAttribute("cancelError", "Bạn không có quyền hủy đơn hàng này.");
            response.sendRedirect(ctx + "/my-orders");
            return;
        }

        String status = order.getOrderStatus();
        boolean cancellable = "Pending".equalsIgnoreCase(status);

        if (!cancellable) {
            String msg;
            switch (status) {
                case "Preparing":
                case "In_Progress":
                    msg = "Bartender đã nhận đơn, không thể hủy.";
                    break;
                case "Ready":
                case "Waiting_Shipper":
                    msg = "Đơn hàng đã pha chế xong, không thể hủy.";
                    break;
                case "Delivering":
                    msg = "Đơn hàng đang được giao, không thể hủy.";
                    break;
                case "Completed":
                    msg = "Đơn hàng đã hoàn thành, không thể hủy.";
                    break;
                case "Cancelled":
                    msg = "Đơn hàng đã bị hủy trước đó.";
                    break;
                default:
                    msg = "Không thể hủy đơn hàng ở trạng thái hiện tại.";
                    break;
            }
            session.setAttribute("cancelError", msg);
            response.sendRedirect(ctx + "/my-orders");
            return;
        }

        //  Thực hiện hủy
        try {
            orderDAO.updateOrderStatus(orderId, "Cancelled");
            if ("Pending".equalsIgnoreCase(status)) {
                try {
                    orderDAO.restoreStockForOrder(orderId);
                } catch (Exception ex) {
                    System.err.println("[CancelOrderServlet] restoreStock failed for order "
                            + orderId + ": " + ex.getMessage());
                }
            }

            session.setAttribute("cancelSuccess", "Đơn hàng #BH-" + orderId + " đã được hủy thành công.");
            response.sendRedirect(ctx + "/my-orders?tab=cancelled");
            return;
        } catch (Exception e) {
            System.err.println("[CancelOrderServlet] Lỗi khi hủy đơn " + orderId + ": " + e.getMessage());
            session.setAttribute("cancelError", "Lỗi hệ thống, vui lòng thử lại sau.");
        }

        response.sendRedirect(ctx + "/my-orders");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/my-orders");
    }
}
