package controller;

import dao.OrderDAO;
import dao.ReviewDAO;
import dto.UserLoginDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import model.Order;

/**
 * Servlet xử lý gửi đánh giá đơn hàng đã hoàn thành.
 * POST /review  →  JSON response { success, message }
 */
public class ReviewServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            out.print("{\"success\":false,\"message\":\"Vui lòng đăng nhập.\"}");
            return;
        }

        UserLoginDTO user = (UserLoginDTO) session.getAttribute("currentUser");

        // Parse params
        String orderIdStr = request.getParameter("orderId");
        String ratingStr  = request.getParameter("rating");
        String comment    = request.getParameter("comment");

        if (orderIdStr == null || ratingStr == null) {
            out.print("{\"success\":false,\"message\":\"Thiếu thông tin đánh giá.\"}");
            return;
        }

        int orderId, rating;
        try {
            orderId = Integer.parseInt(orderIdStr.trim());
            rating  = Integer.parseInt(ratingStr.trim());
        } catch (NumberFormatException e) {
            out.print("{\"success\":false,\"message\":\"Dữ liệu không hợp lệ.\"}");
            return;
        }

        if (rating < 1 || rating > 5) {
            out.print("{\"success\":false,\"message\":\"Điểm đánh giá phải từ 1 đến 5 sao.\"}");
            return;
        }

        // Lấy customerId từ accountId
        OrderDAO orderDAO = new OrderDAO();
        int customerId = orderDAO.getCustomerIdByAccountId(user.getAccountId());
        if (customerId <= 0) {
            out.print("{\"success\":false,\"message\":\"Không tìm thấy thông tin khách hàng.\"}");
            return;
        }

        // Kiểm tra đơn hàng có thuộc khách hàng này và đã Completed chưa
        Order order = orderDAO.getOrderById(orderId);
        if (order == null
                || order.getCustomerId() == null
                || order.getCustomerId() != customerId) {
            out.print("{\"success\":false,\"message\":\"Đơn hàng không hợp lệ.\"}");
            return;
        }
        if (!"Completed".equals(order.getOrderStatus())) {
            out.print("{\"success\":false,\"message\":\"Chỉ có thể đánh giá đơn hàng đã hoàn thành.\"}");
            return;
        }

        ReviewDAO reviewDAO = new ReviewDAO();

        // Kiểm tra đã đánh giá chưa
        if (reviewDAO.hasReviewed(orderId, customerId)) {
            out.print("{\"success\":false,\"message\":\"Bạn đã đánh giá đơn hàng này rồi.\"}");
            return;
        }

        // Lưu đánh giá
        boolean ok = reviewDAO.submitReview(orderId, customerId, rating, comment);
        if (ok) {
            out.print("{\"success\":true,\"message\":\"Cảm ơn bạn đã đánh giá!\"}");
        } else {
            out.print("{\"success\":false,\"message\":\"Có lỗi xảy ra, vui lòng thử lại.\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/my-orders");
    }
}
