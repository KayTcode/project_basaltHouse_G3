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
        return orderId;
    }
}
