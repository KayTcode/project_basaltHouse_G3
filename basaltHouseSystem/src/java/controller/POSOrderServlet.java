package controller;

import dao.OrderDAO;
import dao.ProductDAO;
import dao.SizeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import services.OrderService;


@WebServlet(name = "POSOrderServlet", urlPatterns = { "/PosOrder","/OrderView","/DashBoard"})
public class POSOrderServlet extends HttpServlet {
    
   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      String action = request.getServletPath();
        
       
        switch (action) {
            case "/PosOrder":
                request.getRequestDispatcher("/views/Cashier/POSOrders.jsp").forward(request, response);
                break;
                
            case "/OrderView":
                request.getRequestDispatcher("/views/Cashier/OrderViews.jsp").forward(request, response);
                break;
                
            case "/DashBoard":
                request.getRequestDispatcher("/views/Cashier/CashierDashboard.jsp").forward(request, response);
                break;
                
            default:
              
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }
   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String cartData = request.getParameter("cartData");
        String totalAmountStr = request.getParameter("totalAmount");
        String paymentMethod = request.getParameter("paymentMethod");
        String tableName = request.getParameter("tableName");
        String note = request.getParameter("note");
        
        String customerIdStr = request.getParameter("customerId");
        String discountCode = request.getParameter("discountCode");
        String discountAmountStr = request.getParameter("discountAmount");
        String finalAmountStr = request.getParameter("finalAmount");
        String tableIdStr = request.getParameter("tableId");

        if (cartData == null || cartData.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Cart is empty");
            return;
        }

        try {
            OrderService orderService = new OrderService();
            int orderId = orderService.createOfflineOrder(cartData, totalAmountStr, discountAmountStr, finalAmountStr,
                                                          paymentMethod, tableName, note, customerIdStr, discountCode, tableIdStr);
                                                          
            if (orderId != -1) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Order created successfully: " + orderId);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Failed to create order");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Failed to create order: " + e.getMessage());
        }
    }
}
