package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Order;
import model.OrderDetail;

public class PaymentDAO extends DBContext {
    private static String sql = "";
    private static PreparedStatement ps;
    private static ResultSet rs;
    

    // Lấy thông tin order theo id (dùng để verify trước khi confirm)
    public Order getOrderById(int orderId) {
         sql = """
                SELECT *
                FROM Orders
                WHERE OrderId = ?
                """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, orderId);
            rs = ps.executeQuery();
            if (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("OrderId"));
                order.setOrderStatus(rs.getString("OrderStatus"));
                order.setPaymentMethod(rs.getString("PaymentMethod"));
                order.setPaymentStatus(rs.getString("PaymentStatus"));
                return order;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Kiểm tra đơn có record trong OnlinePayments không (thông tin phụ, không dùng để chống duplicate)
    public boolean hasOnlinePayment(int orderId) {
         sql = """
                SELECT PaymentId
                FROM OnlinePayments
                WHERE OrderId = ?
                """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, orderId);
            rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật PaymentStatus của đơn (VD: Unpaid → PAID)
    public boolean updatePaymentStatus(int orderId, String paymentStatus) {
         sql = """
                UPDATE Orders
                SET PaymentStatus = ?
                WHERE OrderId = ?
                """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, paymentStatus);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật OrderStatus của đơn (VD: Pending → PREPARING → IN_PROGRESS → COMPLETED)
    public boolean updateOrderStatus(int orderId, String orderStatus) {
        sql = """
                UPDATE Orders
                SET OrderStatus = ?
                WHERE OrderId = ?
                """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, orderStatus);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy danh sách món của đơn hàng để đẩy vào hàng đợi pha chế
    public List<OrderDetail> findDetailsByOrderId(int orderId) {
        sql = """
                SELECT OrderDetailId, OrderId, ProductId, SizeId, Quantity, UnitPrice, Note
                FROM OrderDetails
                WHERE OrderId = ? AND IsDeleted = 0
                """;
        List<OrderDetail> list = new ArrayList<>();
        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, orderId);
            rs = ps.executeQuery();
            while (rs.next()) {
                OrderDetail d = new OrderDetail();
                d.setOrderDetailId(rs.getInt("OrderDetailId"));
                d.setOrderId(rs.getInt("OrderId"));
                d.setProductId(rs.getInt("ProductId"));
                d.setSizeId(rs.getInt("SizeId"));
                d.setQuantity(rs.getInt("Quantity"));
                d.setUnitPrice(rs.getBigDecimal("UnitPrice"));
                d.setNote(rs.getString("Note"));
                list.add(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy danh sách order cho bartender queue:
    // PaymentStatus = PAID (case-insensitive qua SQL Server CI collation)
    // OrderStatus còn đang cần xử lý: PREPARING hoặc IN_PROGRESS
    public List<Order> getOrdersByPaymentStatus(String paymentStatus) {
         sql = """
            SELECT OrderId, OrderStatus, PaymentStatus, CreatedAt
            FROM Orders
            WHERE PaymentStatus = ?
              AND OrderStatus IN ('PREPARING', 'IN_PROGRESS')
              AND IsDeleted = 0
            ORDER BY CreatedAt ASC
            """;
        List<Order> list = new ArrayList<>();
        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, paymentStatus);
            rs = ps.executeQuery();
            while (rs.next()) {
                Order o = new Order();
                o.setOrderId(rs.getInt("OrderId"));
                o.setOrderStatus(rs.getString("OrderStatus"));
                o.setPaymentStatus(rs.getString("PaymentStatus"));
                java.sql.Timestamp ts = rs.getTimestamp("CreatedAt");
                if (ts != null) {
                    o.setCreatedAt(ts.toLocalDateTime());
                }
                list.add(o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
