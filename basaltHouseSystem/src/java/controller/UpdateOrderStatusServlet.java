package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import services.OrderService;


@WebServlet(name = "UpdateOrderStatusServlet", urlPatterns = {"/UpdateOrderStatus"})
public class UpdateOrderStatusServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String orderIdStr = request.getParameter("orderId");
        String action = request.getParameter("action");

        if (orderIdStr == null || action == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdStr.replace("ORD00", "").trim());
            OrderService orderService = new OrderService();
            orderService.updateOrderStatus(orderId, action);
            
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Success");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error: " + e.getMessage());
        }
    }
}
