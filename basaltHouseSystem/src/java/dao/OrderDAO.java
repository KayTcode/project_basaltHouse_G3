/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
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


    // OFFLINE ORDER FLOW METHODS


    public int createOfflineOrder(Order order) {
        String sql = """
                     INSERT INTO Orders (TableSessionId, OrderStatus, TotalAmount, CreatedAt, OrderType, PaymentStatus, IsDeleted, FinalAmount, DiscountAmount)
                     VALUES (?, ?, ?, GETDATE(), ?, ?, 0, ?, 0)
                     """;
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setObject(1, order.getTableSessionId());
            ps.setString(2, order.getOrderStatus() != null ? order.getOrderStatus() : "Pending");
            ps.setBigDecimal(3, order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO);
            ps.setString(4, order.getOrderType() != null ? order.getOrderType() : "Dine-In");
            ps.setString(5, order.getPaymentStatus() != null ? order.getPaymentStatus() : "Unpaid");
            ps.setBigDecimal(6, order.getFinalAmount() != null ? order.getFinalAmount() : BigDecimal.ZERO);
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating offline order: " + e.getMessage());
        }
        return -1;
    }

    public List<Order> getOfflineOrdersBySessionId(int sessionId) {
        List<Order> list = new ArrayList<>();
        String sql = """
                     SELECT OrderId, TableSessionId, OrderStatus, TotalAmount, CreatedAt, PaymentStatus
                     FROM Orders
                     WHERE TableSessionId = ? AND IsDeleted = 0
                     ORDER BY CreatedAt DESC
                     """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            try (ResultSet rs2 = ps.executeQuery()) {
                while (rs2.next()) {
                    Order o = new Order();
                    o.setOrderId(rs2.getInt("OrderId"));
                    o.setTableSessionId(rs2.getInt("TableSessionId"));
                    o.setOrderStatus(rs2.getString("OrderStatus"));
                    o.setTotalAmount(rs2.getBigDecimal("TotalAmount"));
                    o.setPaymentStatus(rs2.getString("PaymentStatus"));
                    Timestamp ts = rs2.getTimestamp("CreatedAt");
                    if (ts != null) {
                        o.setCreatedAt(ts.toLocalDateTime());
                    }
                    list.add(o);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting offline orders by sessionId: " + e.getMessage());
        }
        return list;
    }

    public Order getOfflineOrderById(int orderId) {
        try {
            String sql = """
                         SELECT OrderId, OrderStatus, TableSessionId, TotalAmount, CreatedAt
                         FROM Orders
                         WHERE OrderId = ? AND IsDeleted = 0
                         """;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, orderId);
                try (ResultSet rs2 = ps.executeQuery()) {
                    if (rs2.next()) {
                        Order order = new Order();
                        order.setOrderId(rs2.getInt("OrderId"));
                        order.setOrderStatus(rs2.getString("OrderStatus"));
                        order.setTableSessionId((Integer) rs2.getObject("TableSessionId"));
                        order.setTotalAmount(rs2.getBigDecimal("TotalAmount"));
                        Timestamp ts = rs2.getTimestamp("CreatedAt");
                        if (ts != null) {
                            order.setCreatedAt(ts.toLocalDateTime());
                        }
                        return order;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public List<OrderDetail> getOfflineOrderDetailsByOrderId(int orderId) {
        List<OrderDetail> details = new ArrayList<>();
        try {
            String sql = """
                         SELECT OrderDetailId, OrderId, ProductId, SizeId, Quantity, UnitPrice
                         FROM OrderDetails
                         WHERE OrderId = ? AND IsDeleted = 0
                         """;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, orderId);
                try (ResultSet rs2 = ps.executeQuery()) {
                    while (rs2.next()) {
                        OrderDetail detail = new OrderDetail();
                        detail.setOrderDetailId(rs2.getInt("OrderDetailId"));
                        detail.setOrderId(rs2.getInt("OrderId"));
                        detail.setProductId(rs2.getInt("ProductId"));
                        detail.setSizeId(rs2.getInt("SizeId"));
                        detail.setQuantity(rs2.getInt("Quantity"));
                        detail.setUnitPrice(rs2.getBigDecimal("UnitPrice"));
                        details.add(detail);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return details;
    }

    public boolean addOrderDetail(OrderDetail detail) {
        String sql = """
                     INSERT INTO OrderDetails (OrderId, ProductId, SizeId, Quantity, UnitPrice, CreatedAt, IsDeleted)
                     VALUES (?, ?, ?, ?, ?, GETDATE(), 0)
                     """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, detail.getOrderId());
            ps.setInt(2, detail.getProductId());
            ps.setInt(3, detail.getSizeId());
            ps.setInt(4, detail.getQuantity());
            ps.setBigDecimal(5, detail.getUnitPrice());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding order detail: " + e.getMessage());
        }
        return false;
    }

    public boolean updateOrderDetailQuantity(int orderDetailId, int quantity) {
        String sql = """
                     UPDATE OrderDetails
                     SET Quantity = ?
                     WHERE OrderDetailId = ? AND IsDeleted = 0
                     """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, orderDetailId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order detail quantity: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteOrderDetail(int orderDetailId) {
        String sql = """
                     UPDATE OrderDetails
                     SET IsDeleted = 1
                     WHERE OrderDetailId = ? AND IsDeleted = 0
                     """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderDetailId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting order detail: " + e.getMessage());
        }
        return false;
    }

    public boolean updateOrderTotal(int orderId, BigDecimal totalAmount) {
        String sql = """
                     UPDATE Orders
                     SET TotalAmount = ?, FinalAmount = ?
                     WHERE OrderId = ? AND IsDeleted = 0
                     """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBigDecimal(1, totalAmount);
            ps.setBigDecimal(2, totalAmount);
            ps.setInt(3, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order total: " + e.getMessage());
        }
        return false;
    }

    public boolean updateOrderType(int orderId, String orderType) {
        String sql = """
                     UPDATE Orders
                     SET OrderType = ?
                     WHERE OrderId = ? AND IsDeleted = 0
                     """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, orderType);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order type: " + e.getMessage());
        }
        return false;
    }
}


