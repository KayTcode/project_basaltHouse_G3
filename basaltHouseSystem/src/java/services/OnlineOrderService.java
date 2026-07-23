package services;

import dao.OrderDAO;
import model.Order;
import model.OrderDetail;
import java.util.List;

public class OnlineOrderService {

    public int createOnlineOrderFromCart(Order order, List<OrderDetail> details) {
        OrderDAO orderDAO = new OrderDAO();

        int orderId = orderDAO.insertOfflineOrder(order, details);
        System.out.println("[OnlineOrderService] insertOfflineOrder → orderId=" + orderId);

        if (orderId <= 0) {
            return -1;
        }

        boolean deducted = orderDAO.deductStockForOrderWithTransaction(orderId);
        if (!deducted) {
            System.err.println("[OnlineOrderService] Hết nguyên liệu, hủy đơn " + orderId);
            try { orderDAO.updateOrderStatus(orderId, "Cancelled"); } catch (Exception ignored) {}
            return -1; 
        }

        return orderId;
    }
}
