package services;

import dao.PaymentDAO;
import model.Order;

public class PaymentService {

    private final PaymentDAO paymentDAO = new PaymentDAO();

    public boolean confirmPayment(int orderId) {
        try {
            // Bước 1: Verify — kiểm tra order tồn tại
            Order order = paymentDAO.getOrderById(orderId);
            if (order == null) return false;

            // Chống duplicate: nếu đã Paid rồi thì không confirm lại
            if ("Paid".equalsIgnoreCase(order.getPaymentStatus())) return false;

            // Bước 1b: Verify theo PaymentMethod
            String method = order.getPaymentMethod();
            if ("QRBanking".equalsIgnoreCase(method)) {
                // QR Banking: phải có giao dịch thực tế trong OnlinePayments (PaidAt != null)
                if (!paymentDAO.hasOnlinePayment(orderId)) return false;
            } else if ("Cash".equalsIgnoreCase(method)) {
                // Cash: cashier xác nhận trực tiếp tại quầy, không cần OnlinePayments
            } else {
                // PaymentMethod không hợp lệ hoặc null
                return false;
            }

            // Bước 2: Đổi PaymentStatus → Paid
            boolean paymentUpdated = paymentDAO.updatePaymentStatus(orderId, "Paid");
            if (!paymentUpdated) return false;

            // Bước 3: Đổi OrderStatus → Preparing
            // "Notify preparation workflow": bartender sẽ query DB thấy đơn này trong queue
            boolean orderUpdated = paymentDAO.updateOrderStatus(orderId, "Preparing");
            if (!orderUpdated) return false;

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}