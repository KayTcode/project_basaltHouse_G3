/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Order;
import model.OrderDetail;

/**
 *
 * @author admin
 */
public class OrderDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    public Order getOrderById(int orderId) {
        try {
            String sql = """
                         SELECT OrderId, OrderStatus
                         FROM Orders
                         WHERE OrderId = ? AND IsDeleted = 0
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, orderId);
            rs = st.executeQuery();
            if (rs.next()) {
                return new Order(
                        rs.getInt("OrderId"),
                        rs.getString("OrderStatus")
                );
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {
        List<OrderDetail> details = new ArrayList<>();
        try {
            String sql = """
                         SELECT OrderDetailId, OrderId, ProductId, SizeId, Quantity
                         FROM OrderDetails
                         WHERE OrderId = ? AND IsDeleted = 0
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, orderId);
            rs = st.executeQuery();
            while (rs.next()) {
                details.add(new OrderDetail(
                        rs.getInt("OrderDetailId"),
                        rs.getInt("OrderId"),
                        rs.getInt("ProductId"),
                        rs.getInt("SizeId"),
                        rs.getInt("Quantity")
                ));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return details;
    }

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
            System.err.println(e.getMessage());
        }
    }
     // ===== THÊM MỚI CHO BARTENDER MODULE =====

    // Lấy danh sách đơn đã Paid + đang chờ bartender nhận (Preparing)
    // Dùng cho preparation queue — issue 29
    public List<Order> getPaidPreparingOrders() {
        List<Order> list = new ArrayList<>();
        try {
            String sql = """
                         SELECT OrderId, OrderStatus, PaymentStatus, CreatedAt
                         FROM Orders
                         WHERE PaymentStatus = 'Paid'
                           AND OrderStatus = 'Preparing'
                           AND IsDeleted = 0
                         ORDER BY CreatedAt ASC
                         """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
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
            System.err.println(e.getMessage());
        }
        return list;
    }

    // Lấy danh sách đơn bartender đang pha chế (In_Progress)
    // Dùng cho preparation tracking — issue 30
    public List<Order> getInProgressOrders() {
        List<Order> list = new ArrayList<>();
        try {
            String sql = """
                         SELECT OrderId, OrderStatus, PaymentStatus, CreatedAt
                         FROM Orders
                         WHERE PaymentStatus = 'Paid'
                           AND OrderStatus = 'In_Progress'
                           AND IsDeleted = 0
                         ORDER BY CreatedAt ASC
                         """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
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
            System.err.println(e.getMessage());
        }
        return list;
    }
}
