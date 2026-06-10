package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.Order;

public class PaymentDAO extends DBContext {

    private static String sql = "";
    private static PreparedStatement ps;
    private static ResultSet rs;

    // Lấy thông tin order theo id (dùng để verify trước khi confirm)
    public Order getOrderById(int orderId) {
        sql = """
              SELECT OrderId, OrderStatus, PaymentMethod, PaymentStatus
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

    // Kiểm tra đơn QR Banking đã được thanh toán thực tế chưa
    // Chỉ pass khi có record VÀ PaidAt != null (tức là tiền đã vào)
    public boolean hasOnlinePayment(int orderId) {
        sql = """
              SELECT PaymentId
              FROM OnlinePayments
              WHERE OrderId = ?
                AND PaidAt IS NOT NULL
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

    // Cập nhật PaymentStatus của đơn (VD: Unpaid → Paid)
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

    // Cập nhật OrderStatus của đơn (VD: Pending → Preparing)
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

}
