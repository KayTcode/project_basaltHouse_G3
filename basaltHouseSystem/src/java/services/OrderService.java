package services;

import dao.OrderDAO;
import dao.TableSessionDAO;
import model.Order;
import model.OrderDetail;
import model.TableSession;
import java.math.BigDecimal;
import java.util.List;

public class OrderService {

    private final OrderDAO orderDAO = new OrderDAO();
    private final TableSessionDAO sessionDAO = new TableSessionDAO();

    public List<Order> getOfflineOrdersBySessionId(int sessionId) {
        return orderDAO.getOfflineOrdersBySessionId(sessionId);
    }

    public Order getOfflineOrderById(int orderId) {
        return orderDAO.getOfflineOrderById(orderId);
    }

    public List<OrderDetail> getOfflineOrderDetailsByOrderId(int orderId) {
        return orderDAO.getOfflineOrderDetailsByOrderId(orderId);
    }

    public int createOfflineOrderForSession(int sessionId) {
        // Validate: session phải tồn tại và đang ACTIVE
        TableSession session = sessionDAO.getSessionById(sessionId);
        if (session == null) {
            System.err.println("[OrderService] Session " + sessionId + " không tồn tại.");
            return -1;
        }
        String status = session.getStatus();
        if (!"ACTIVE".equalsIgnoreCase(status) && !"Open".equalsIgnoreCase(status)) {
            System.err.println("[OrderService] Session " + sessionId + " đã đóng (status=" + status + "), không thể tạo đơn mới.");
            return -1;
        }

        Order order = new Order();
        order.setTableSessionId(sessionId);
        order.setOrderStatus("Pending");
        order.setTotalAmount(BigDecimal.ZERO);
        order.setFinalAmount(BigDecimal.ZERO);
        order.setOrderType("Dine-In");
        order.setPaymentStatus("Unpaid");
        return orderDAO.createOfflineOrder(order);
    }

    public boolean updateOfflineOrderStatus(int orderId, String status) {
        orderDAO.updateOrderStatus(orderId, status);
        return true;
    }

    public boolean addOfflineProductToOrder(int orderId, int productId, int sizeId, int quantity, BigDecimal unitPrice) {
        List<OrderDetail> details = orderDAO.getOfflineOrderDetailsByOrderId(orderId);
        for (OrderDetail d : details) {
            if (d.getProductId() == productId && d.getSizeId() == sizeId) {
                int newQty = d.getQuantity() + quantity;
                boolean updated = orderDAO.updateOrderDetailQuantity(d.getOrderDetailId(), newQty);
                if (updated) {
                    recalculateOfflineOrderTotal(orderId);
                }
                return updated;
            }
        }

        OrderDetail detail = new OrderDetail();
        detail.setOrderId(orderId);
        detail.setProductId(productId);
        detail.setSizeId(sizeId);
        detail.setQuantity(quantity);
        detail.setUnitPrice(unitPrice);

        boolean added = orderDAO.addOrderDetail(detail);
        if (added) {
            recalculateOfflineOrderTotal(orderId);
        }
        return added;
    }

    public boolean updateOfflineDetailQuantity(int orderId, int orderDetailId, int quantity) {
        if (quantity <= 0) {
            return removeOfflineDetailFromOrder(orderId, orderDetailId);
        }
        boolean updated = orderDAO.updateOrderDetailQuantity(orderDetailId, quantity);
        if (updated) {
            recalculateOfflineOrderTotal(orderId);
        }
        return updated;
    }

    public boolean removeOfflineDetailFromOrder(int orderId, int orderDetailId) {
        boolean deleted = orderDAO.deleteOrderDetail(orderDetailId);
        if (deleted) {
            recalculateOfflineOrderTotal(orderId);
        }
        return deleted;
    }

    public boolean recalculateOfflineOrderTotal(int orderId) {
        List<OrderDetail> details = orderDAO.getOfflineOrderDetailsByOrderId(orderId);
        BigDecimal total = BigDecimal.ZERO;
        for (OrderDetail d : details) {
            BigDecimal qty = new BigDecimal(d.getQuantity());
            total = total.add(d.getUnitPrice().multiply(qty));
        }
        return orderDAO.updateOrderTotal(orderId, total);
    }
}
