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

// [Sửa code gốc]:
// Trước đây, file này chứa nguyên một mảng logic khổng lồ để nhận giỏ hàng, tính toán tiền, xử lý mã giảm giá, và insert thẳng vào Database.
// [Mới]:
// Tôi đã gỡ bỏ hoàn toàn đống code tính toán cồng kềnh đó.
// Bây giờ file này chỉ đóng vai trò là một Controller mỏng (chuẩn MVC), nhận Request từ giao diện và ném toàn bộ dữ liệu thô cho OrderService.createOfflineOrder() xử lý.
@WebServlet(name = "POSOrderServlet", urlPatterns = {"/POSOrder"})
public class POSOrderServlet extends HttpServlet {

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

        if (cartData == null || cartData.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Cart is empty");
            return;
        }

        try {
            OrderService orderService = new OrderService();
            int orderId = orderService.createOfflineOrder(cartData, totalAmountStr, discountAmountStr, finalAmountStr,
                                                          paymentMethod, tableName, note, customerIdStr, discountCode);
                                                          
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
