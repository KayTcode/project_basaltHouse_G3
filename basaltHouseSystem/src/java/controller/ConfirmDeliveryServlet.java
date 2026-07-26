package controller;

import dao.OrderDAO;
import dto.UserLoginDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import services.OrderService;

@WebServlet(name = "ConfirmDeliveryServlet", urlPatterns = {"/confirm-delivery"})
public class ConfirmDeliveryServlet extends HttpServlet {

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
        int customerId = orderDAO.getCustomerIdByAccountId(user.getAccountId());
        if (customerId <= 0) {
            session.setAttribute("cancelError", "Tài khoản của bạn không được liên kết với hồ sơ khách hàng.");
            response.sendRedirect(ctx + "/my-orders");
            return;
        }

        OrderService orderService = new OrderService();
        String result = orderService.confirmDelivery(orderId, customerId);

        if ("OK".equals(result)) {
            session.setAttribute("cancelSuccess", "Xác nhận nhận đơn hàng #BH-" + orderId + " thành công.");
            response.sendRedirect(ctx + "/my-orders?tab=completed");
        } else {
            session.setAttribute("cancelError", result);
            response.sendRedirect(ctx + "/my-orders");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/my-orders");
    }
}
