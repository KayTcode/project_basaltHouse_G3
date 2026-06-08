package services;

import dao.PaymentDAO;
import java.util.List;
import model.Order;
import model.OrderDetail;

public class PaymentService {

    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final PreparationService preparationService = new PreparationService();

    public boolean confirmPayment(int orderId) {
        try {
            // Bước 1: Verify — kiểm tra order tồn tại và chưa được confirm trùng
            Order order = paymentDAO.getOrderById(orderId);
            if (order == null) return false;

            // Chống duplicate: nếu đã PAID rồi (bất kể 'Paid'/'PAID') thì không confirm lại
            if ("Paid".equalsIgnoreCase(order.getPaymentStatus())) return false;

            // Bước 2: Đổi PaymentStatus → PAID
            boolean paymentUpdated = paymentDAO.updatePaymentStatus(orderId, "Paid");
            if (!paymentUpdated) return false;

            // Bước 3: Đổi OrderStatus → PREPARING
            boolean orderUpdated = paymentDAO.updateOrderStatus(orderId, "Preparing");
            if (!orderUpdated) return false;

            // Bước 4: Lấy danh sách món và đẩy vào hàng đợi pha chế
            List<OrderDetail> details = paymentDAO.findDetailsByOrderId(orderId);
            if (details.isEmpty()) return false;

            preparationService.enqueueItems(details);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}