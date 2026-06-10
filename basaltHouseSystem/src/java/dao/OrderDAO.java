package dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Order;
import model.OrderDetail;

/**
 * @author admin
 */
public class OrderDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    /**
     * Lấy thông tin đầy đủ của một đơn hàng theo OrderId.
     */
    public Order getOrderById(int orderId) {
        try {
            String sql = """
                         SELECT OrderId, CustomerId, CashierId, ShipperId,
                                TableSessionId, OrderAddressId, DiscountId,
                                OrderType, OrderStatus, PaymentMethod,
                                PaymentStatus, TotalAmount, DiscountAmount, FinalAmount,
                                CreatedAt, IsDeleted
                         FROM Orders
                         WHERE OrderId = ? AND IsDeleted = 0
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, orderId);
            rs = st.executeQuery();
            if (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("OrderId"));
                order.setCustomerId(rs.getObject("CustomerId") != null ? rs.getInt("CustomerId") : null);
                order.setCashierId(rs.getObject("CashierId") != null ? rs.getInt("CashierId") : null);
                order.setShipperId(rs.getObject("ShipperId") != null ? rs.getInt("ShipperId") : null);
                order.setTableSessionId(rs.getObject("TableSessionId") != null ? rs.getInt("TableSessionId") : null);
                order.setOrderAddressId(rs.getObject("OrderAddressId") != null ? rs.getInt("OrderAddressId") : null);
                order.setDiscountId(rs.getObject("DiscountId") != null ? rs.getInt("DiscountId") : null);
                order.setOrderType(rs.getString("OrderType"));
                order.setOrderStatus(rs.getString("OrderStatus"));
                order.setPaymentMethod(rs.getString("PaymentMethod"));
                order.setPaymentStatus(rs.getString("PaymentStatus"));
                order.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                order.setDiscountAmount(rs.getBigDecimal("DiscountAmount"));
                order.setFinalAmount(rs.getBigDecimal("FinalAmount"));
                if (rs.getTimestamp("CreatedAt") != null) {
                    order.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
                }
                order.setIsDeleted(rs.getBoolean("IsDeleted"));
                return order;
            }
        } catch (Exception e) {
            System.err.println("Lỗi getOrderById: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lấy danh sách tất cả đơn hàng chưa thanh toán (Unpaid).
     * Dùng cho màn hình POS của Thu ngân.
     */
    public List<Order> getUnpaidOrders() {
        List<Order> orders = new ArrayList<>();
        try {
            String sql = """
                         SELECT OrderId, CustomerId, CashierId, TableSessionId,
                                OrderType, OrderStatus, PaymentMethod,
                                PaymentStatus, TotalAmount, DiscountAmount, FinalAmount, CreatedAt
                         FROM Orders
                         WHERE PaymentStatus = 'Unpaid' AND IsDeleted = 0
                         ORDER BY CreatedAt DESC
                         """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("OrderId"));
                order.setCustomerId(rs.getObject("CustomerId") != null ? rs.getInt("CustomerId") : null);
                order.setCashierId(rs.getObject("CashierId") != null ? rs.getInt("CashierId") : null);
                order.setTableSessionId(rs.getObject("TableSessionId") != null ? rs.getInt("TableSessionId") : null);
                order.setOrderType(rs.getString("OrderType"));
                order.setOrderStatus(rs.getString("OrderStatus"));
                order.setPaymentMethod(rs.getString("PaymentMethod"));
                order.setPaymentStatus(rs.getString("PaymentStatus"));
                order.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                order.setDiscountAmount(rs.getBigDecimal("DiscountAmount"));
                order.setFinalAmount(rs.getBigDecimal("FinalAmount"));
                if (rs.getTimestamp("CreatedAt") != null) {
                    order.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
                }
                orders.add(order);
            }
        } catch (Exception e) {
            System.err.println("Lỗi getUnpaidOrders: " + e.getMessage());
        }
        return orders;
    }

    /**
     * Lấy chi tiết các sản phẩm trong một đơn hàng.
     */
    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {
        List<OrderDetail> details = new ArrayList<>();
        try {
            String sql = """
                         SELECT od.OrderDetailId, od.OrderId, od.ProductId, od.SizeId,
                                od.Quantity, od.UnitPrice, od.Note,
                                p.ProductName, s.SizeName
                         FROM OrderDetails od
                         INNER JOIN Products p ON od.ProductId = p.ProductId
                         INNER JOIN Sizes s ON od.SizeId = s.SizeId
                         WHERE od.OrderId = ? AND od.IsDeleted = 0
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, orderId);
            rs = st.executeQuery();
            while (rs.next()) {
                OrderDetail detail = new OrderDetail();
                detail.setOrderDetailId(rs.getInt("OrderDetailId"));
                detail.setOrderId(rs.getInt("OrderId"));
                detail.setProductId(rs.getInt("ProductId"));
                detail.setSizeId(rs.getInt("SizeId"));
                detail.setQuantity(rs.getInt("Quantity"));
                detail.setUnitPrice(rs.getBigDecimal("UnitPrice"));
                detail.setNote(rs.getString("Note"));
                detail.setProductName(rs.getString("ProductName"));
                detail.setSizeName(rs.getString("SizeName"));
                details.add(detail);
            }
        } catch (Exception e) {
            System.err.println("Lỗi getOrderDetailsByOrderId: " + e.getMessage());
        }
        return details;
    }

    /**
     * Cập nhật trạng thái đơn hàng.
     */
    public void updateOrderStatus(int orderId, String status) {
        try {
            String sql = """
                     UPDATE Orders SET OrderStatus = ?
                     WHERE OrderId = ?
                     """;
            st = connection.prepareStatement(sql);
            st.setObject(1, status);
            st.setObject(2, orderId);
            st.executeUpdate();
        } catch (Exception e) {
            System.err.println("Lỗi updateOrderStatus: " + e.getMessage());
        }
    }

    /**
     * Áp dụng mã giảm giá cho đơn hàng: cập nhật DiscountId, DiscountAmount, FinalAmount.
     */
    public boolean updateOrderDiscount(int orderId, int discountId, BigDecimal discountAmount, BigDecimal finalAmount) {
        try {
            String sql = """
                         UPDATE Orders
                         SET DiscountId = ?,
                             DiscountAmount = ?,
                             FinalAmount = ?
                         WHERE OrderId = ? AND IsDeleted = 0
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, discountId);
            st.setObject(2, discountAmount);
            st.setObject(3, finalAmount);
            st.setObject(4, orderId);
            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Lỗi updateOrderDiscount: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xác nhận thanh toán đơn hàng: cập nhật PaymentStatus, PaymentMethod, OrderStatus.
     */
    public boolean updatePaymentStatus(int orderId, String paymentMethod, String paymentStatus, String orderStatus) {
        try {
            String sql = """
                         UPDATE Orders
                         SET PaymentMethod = ?,
                             PaymentStatus = ?,
                             OrderStatus   = ?
                         WHERE OrderId = ? AND IsDeleted = 0
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, paymentMethod);
            st.setObject(2, paymentStatus);
            st.setObject(3, orderStatus);
            st.setObject(4, orderId);
            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Lỗi updatePaymentStatus: " + e.getMessage());
            return false;
        }
    }
}
