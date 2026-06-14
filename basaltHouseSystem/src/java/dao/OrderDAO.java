/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
    
     // Thêm lấy dữ liệu DiscountAmount, TotalAmount (nếu có) để hiển thị đầy đủ chi tiết đơn hàng ở màn hình modal.
    public List<Order> getAllOrdersWithCustomerName() {
        List<Order> list = new ArrayList<>();
        try {
            String sql = "SELECT o.OrderId, o.OrderType, o.OrderStatus, o.TotalAmount, o.DiscountAmount, o.FinalAmount, o.CreatedAt, o.PaymentMethod, o.TableName, o.Note, c.FullName " +
                         "FROM Orders o " +
                         "LEFT JOIN Customers c ON o.CustomerId = c.CustomerId " +
                         "WHERE o.IsDeleted = 0 " +
                         "ORDER BY o.CreatedAt DESC";
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                Order o = new Order();
                o.setOrderId(rs.getInt("OrderId"));
                o.setOrderType(rs.getString("OrderType"));
                o.setOrderStatus(rs.getString("OrderStatus"));
                o.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                o.setDiscountAmount(rs.getBigDecimal("DiscountAmount"));
                o.setFinalAmount(rs.getBigDecimal("FinalAmount"));
                java.sql.Timestamp ts = rs.getTimestamp("CreatedAt");
                if (ts != null) {
                    o.setCreatedAt(ts.toLocalDateTime());
                }
                String cName = rs.getString("FullName");
                o.setCustomerName(cName != null ? cName : "Walk-in");
                o.setPaymentMethod(rs.getString("PaymentMethod"));
                o.setTableName(rs.getString("TableName"));
                o.setNote(rs.getString("Note"));
                list.add(o);
            }
        } catch (Exception e) {
            System.err.println("getAllOrdersWithCustomerName Error: " + e.getMessage());
        }
        return list;
    }

    public List<Order> getBartenderOrders() {
        List<Order> list = new ArrayList<>();
        try {
            String sql = "SELECT o.OrderId, o.OrderType, o.OrderStatus, o.CreatedAt, o.TableName, o.Note, c.FullName " +
                         "FROM Orders o " +
                         "LEFT JOIN Customers c ON o.CustomerId = c.CustomerId " +
                         "WHERE o.IsDeleted = 0 AND o.OrderStatus IN ('Preparing', 'In_Progress', 'Ready') " +
                         "ORDER BY CASE o.OrderStatus WHEN 'Preparing' THEN 1 WHEN 'In_Progress' THEN 2 WHEN 'Ready' THEN 3 ELSE 4 END, o.CreatedAt ASC";
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                Order o = new Order();
                o.setOrderId(rs.getInt("OrderId"));
                o.setOrderType(rs.getString("OrderType"));
                o.setOrderStatus(rs.getString("OrderStatus"));
                java.sql.Timestamp ts = rs.getTimestamp("CreatedAt");
                if (ts != null) {
                    o.setCreatedAt(ts.toLocalDateTime());
                }
                String cName = rs.getString("FullName");
                o.setCustomerName(cName != null ? cName : "Walk-in");
                o.setTableName(rs.getString("TableName"));
                o.setNote(rs.getString("Note"));
                list.add(o);
            }
        } catch (Exception e) {
            System.err.println("getBartenderOrders Error: " + e.getMessage());
        }
        return list;
    }

    public List<Order> getCompletedOrders() {
        List<Order> list = new ArrayList<>();
        try {
            String sql = "SELECT o.OrderId, o.OrderType, o.OrderStatus, o.CreatedAt, o.TableName, o.Note, c.FullName " +
                         "FROM Orders o " +
                         "LEFT JOIN Customers c ON o.CustomerId = c.CustomerId " +
                         "WHERE o.IsDeleted = 0 AND o.OrderStatus = 'Completed' " +
                         "ORDER BY o.CreatedAt DESC";
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                Order o = new Order();
                o.setOrderId(rs.getInt("OrderId"));
                o.setOrderType(rs.getString("OrderType"));
                o.setOrderStatus(rs.getString("OrderStatus"));
                java.sql.Timestamp ts = rs.getTimestamp("CreatedAt");
                if (ts != null) {
                    o.setCreatedAt(ts.toLocalDateTime());
                }
                String cName = rs.getString("FullName");
                o.setCustomerName(cName != null ? cName : "Walk-in");
                o.setTableName(rs.getString("TableName"));
                o.setNote(rs.getString("Note"));
                list.add(o);
            }
        } catch (Exception e) {
            System.err.println("getCompletedOrders Error: " + e.getMessage());
        }
        return list;
    }

    public int insertOfflineOrder(Order order, List<OrderDetail> details) {
        int orderId = -1;
        try {
            connection.setAutoCommit(false);
            
            String sqlOrder = "INSERT INTO Orders (CustomerId, DiscountId, OrderType, OrderStatus, PaymentStatus, TotalAmount, DiscountAmount, FinalAmount, " +
                              "PaymentMethod, TableName, Note, CreatedAt, IsDeleted) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), 0)";
            st = connection.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            if (order.getCustomerId() != null) {
                st.setInt(1, order.getCustomerId());
            } else {
                st.setNull(1, java.sql.Types.INTEGER);
            }
            if (order.getDiscountId() != null) {
                st.setInt(2, order.getDiscountId());
            } else {
                st.setNull(2, java.sql.Types.INTEGER);
            }
            st.setString(3, order.getOrderType() != null ? order.getOrderType() : "Offline");
            st.setString(4, order.getOrderStatus() != null ? order.getOrderStatus() : "Preparing");
            st.setString(5, order.getPaymentStatus() != null ? order.getPaymentStatus() : "Paid");
            st.setBigDecimal(6, order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO);
            st.setBigDecimal(7, order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO);
            st.setBigDecimal(8, order.getFinalAmount() != null ? order.getFinalAmount() : BigDecimal.ZERO);
            st.setString(9, order.getPaymentMethod());
            st.setString(10, order.getTableName());
            st.setString(11, order.getNote());
            st.executeUpdate();
            
            rs = st.getGeneratedKeys();
            if (rs.next()) {
                orderId = rs.getInt(1);
            }
            
            if (orderId != -1 && details != null && !details.isEmpty()) {
                String sqlDetail = "INSERT INTO OrderDetails (OrderId, ProductId, SizeId, Quantity, UnitPrice, CreatedAt, IsDeleted) " +
                                   "VALUES (?, ?, ?, ?, ?, GETDATE(), 0)";
                st = connection.prepareStatement(sqlDetail);
                for (OrderDetail d : details) {
                    st.setInt(1, orderId);
                    st.setInt(2, d.getProductId());
                    st.setInt(3, d.getSizeId());
                    st.setInt(4, d.getQuantity());
                    st.setBigDecimal(5, d.getUnitPrice());
                    st.addBatch();
                }
                st.executeBatch();
            }
            
            connection.commit();
        } catch (Exception e) {
            System.err.println("Error insertOfflineOrder: " + e.getMessage());
            try {
                if (connection != null) connection.rollback();
            } catch (Exception ex) {
                System.err.println("Rollback error: " + ex.getMessage());
            }
        } finally {
            try {
                if (connection != null) connection.setAutoCommit(true);
            } catch (Exception ex) {}
        }
        return orderId;
    }
}
