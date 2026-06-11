package services;

import dao.PaymentDAO;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import model.Order;
import model.OrderDetail;

public class PaymentService {

    private final PaymentDAO paymentDAO = new PaymentDAO();
    private static final Map<Integer, OrderDetail> queueMap = new ConcurrentHashMap<>();

    public void enqueueItems(List<OrderDetail> details) {
        for (OrderDetail detail : details) {
            queueMap.put(detail.getOrderDetailId(), detail);
        }
    }

    public boolean confirmPayment(int orderId) {
        try {
            // Bước 1: Verify — kiểm tra order tồn tại và chưa được confirm trùng
            Order order = paymentDAO.getOrderById(orderId);
            if (order == null) {
                return false;
            }

            // Chống duplicate: nếu đã PAID rồi (bất kể 'Paid'/'PAID') thì không confirm lại
            if ("Paid".equalsIgnoreCase(order.getPaymentStatus())) {
                return false;
            }
            // BẢO VỆ HỆ THỐNG: Kiểm tra xem thực sự đã có giao dịch QR/Online cho đơn này chưa
            // Nếu chưa có record trong OnlinePayments -> Nghĩa là chưa nhận được tiền -> KHÔNG CHO CONFIRM
            if (!paymentDAO.hasOnlinePayment(orderId)) {
                return false;
            }
            // Bước 2: Đổi PaymentStatus → PAID
            boolean paymentUpdated = paymentDAO.updatePaymentStatus(orderId, "Paid");
            if (!paymentUpdated) {
                return false;
            }

            // Bước 3: Đổi OrderStatus → PREPARING
            boolean orderUpdated = paymentDAO.updateOrderStatus(orderId, "Preparing");
            if (!orderUpdated) {
                return false;
            }

            // Bước 4: Lấy danh sách món và đẩy vào hàng đợi pha chế
            List<OrderDetail> details = paymentDAO.findDetailsByOrderId(orderId);
            if (details.isEmpty()) {
                return false;
            }

            enqueueItems(details);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
