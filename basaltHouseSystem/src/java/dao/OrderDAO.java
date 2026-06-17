package dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.Order;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
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
     * Lấy danh sách tất cả đơn hàng chưa thanh toán (Unpaid). Dùng cho màn hình
     * POS của Thu ngân.
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

    // Thêm lấy dữ liệu DiscountAmount, TotalAmount (nếu có) để hiển thị đầy đủ chi tiết đơn hàng ở màn hình modal.
    public List<Order> getAllOrdersWithCustomerName() {
        List<Order> list = new ArrayList<>();
        try {
            String sql = "SELECT o.OrderId, o.OrderType, o.OrderStatus, o.TotalAmount, o.DiscountAmount, o.FinalAmount, o.CreatedAt, o.PaymentMethod, c.FullName "
                    + "FROM Orders o "
                    + "LEFT JOIN Customers c ON o.CustomerId = c.CustomerId "
                    + "WHERE o.IsDeleted = 0 "
                    + "ORDER BY o.CreatedAt DESC";
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
                list.add(o);
            }
        } catch (Exception e) {
            System.err.println("getAllOrdersWithCustomerName Error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Áp dụng mã giảm giá cho đơn hàng: cập nhật DiscountId, DiscountAmount,
     * FinalAmount.
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
     * Xác nhận thanh toán đơn hàng: cập nhật PaymentStatus, PaymentMethod,
     * OrderStatus.
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

    public List<Order> getBartenderOrders() {
        List<Order> list = new ArrayList<>();
        try {
            String sql = "SELECT o.OrderId, o.OrderType, o.OrderStatus, o.CreatedAt, c.FullName "
                    + "FROM Orders o "
                    + "LEFT JOIN Customers c ON o.CustomerId = c.CustomerId "
                    + "WHERE o.IsDeleted = 0 AND o.OrderStatus IN ('Preparing', 'In_Progress', 'Ready') "
                    + "ORDER BY CASE o.OrderStatus WHEN 'Preparing' THEN 1 WHEN 'In_Progress' THEN 2 WHEN 'Ready' THEN 3 ELSE 4 END, o.CreatedAt ASC";
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
            String sql = "SELECT o.OrderId, o.OrderType, o.OrderStatus, o.CreatedAt, c.FullName "
                    + "FROM Orders o "
                    + "LEFT JOIN Customers c ON o.CustomerId = c.CustomerId "
                    + "WHERE o.IsDeleted = 0 AND o.OrderStatus = 'Completed' "
                    + "ORDER BY o.CreatedAt DESC";
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

            String sqlOrder = "INSERT INTO Orders (CustomerId, OrderAddressId, DiscountId, OrderType, OrderStatus, PaymentStatus, TotalAmount, DiscountAmount, FinalAmount, "
                    + "PaymentMethod, Note, CreatedAt, IsDeleted) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), 0)";
            st = connection.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            if (order.getCustomerId() != null) {
                st.setInt(1, order.getCustomerId());
            } else {
                st.setNull(1, java.sql.Types.INTEGER);
            }
            if (order.getOrderAddressId() != null) {
                st.setInt(2, order.getOrderAddressId());
            } else {
                st.setNull(2, java.sql.Types.INTEGER);
            }
            if (order.getDiscountId() != null) {
                st.setInt(3, order.getDiscountId());
            } else {
                st.setNull(3, java.sql.Types.INTEGER);
            }
            st.setString(4, order.getOrderType() != null ? order.getOrderType() : "Offline");
            st.setString(5, order.getOrderStatus() != null ? order.getOrderStatus() : "Preparing");
            st.setString(6, order.getPaymentStatus() != null ? order.getPaymentStatus() : "Paid");
            st.setBigDecimal(7, order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO);
            st.setBigDecimal(8, order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO);
            st.setBigDecimal(9, order.getFinalAmount() != null ? order.getFinalAmount() : BigDecimal.ZERO);
            st.setString(10, order.getPaymentMethod());
            st.setString(11, order.getNote());
            st.executeUpdate();


            rs = st.getGeneratedKeys();
            if (rs.next()) {
                orderId = rs.getInt(1);
            }

            if (orderId != -1 && details != null && !details.isEmpty()) {
                String sqlDetail = "INSERT INTO OrderDetails (OrderId, ProductId, SizeId, Quantity, UnitPrice, CreatedAt, IsDeleted) "
                        + "VALUES (?, ?, ?, ?, ?, GETDATE(), 0)";
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
                if (connection != null) {
                    connection.rollback();
                }
            } catch (Exception ex) {
                System.err.println("Rollback error: " + ex.getMessage());
            }
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (Exception ex) {
            }
        }
        return orderId;
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


    public int getCustomerIdByAccountId(int accountId) {
        String sql = "SELECT CustomerId FROM Customers WHERE AccountId = ? AND IsDeleted = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs2 = ps.executeQuery()) {
                if (rs2.next()) {
                    return rs2.getInt("CustomerId");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getCustomerIdByAccountId: " + e.getMessage());
        }
        return -1;
    }
}
