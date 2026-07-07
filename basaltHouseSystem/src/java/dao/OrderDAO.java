package dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.Order;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            PreparedStatement stUpdate = connection.prepareStatement(sql);
            stUpdate.setObject(1, status);
            stUpdate.setObject(2, orderId);
            stUpdate.executeUpdate();
            stUpdate.close();

            // Auto-maintain DeliveryLogs table
            boolean hasLog = false;
            String checkSql = "SELECT COUNT(*) FROM DeliveryLogs WHERE OrderId = ? AND IsDeleted = 0";
            try (PreparedStatement psCheck = connection.prepareStatement(checkSql)) {
                psCheck.setInt(1, orderId);
                try (ResultSet rsCheck = psCheck.executeQuery()) {
                    if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                        hasLog = true;
                    }
                }
            }

            if (!hasLog) {
                String insertSql = """
                    INSERT INTO DeliveryLogs (OrderId, ShipperId, Status, CreatedAt, IsDeleted)
                    VALUES (?, 1, 'Pending', GETDATE(), 0)
                    """;
                try (PreparedStatement psInsert = connection.prepareStatement(insertSql)) {
                    psInsert.setInt(1, orderId);
                    psInsert.executeUpdate();
                }
            }

            if ("Preparing".equals(status) || "In_Progress".equals(status)) {
                String updateLogSql = """
                    UPDATE DeliveryLogs 
                    SET ShipperConfirmedAt = GETDATE(), Status = 'ShipperConfirmed'
                    WHERE OrderId = ? AND IsDeleted = 0 AND ShipperConfirmedAt IS NULL
                    """;
                try (PreparedStatement psUpdate = connection.prepareStatement(updateLogSql)) {
                    psUpdate.setInt(1, orderId);
                    psUpdate.executeUpdate();
                }
            } else if ("Ready".equals(status) || "Delivering".equals(status)) {
                String updateLogSql = """
                    UPDATE DeliveryLogs 
                    SET PickedUpAt = GETDATE(), Status = 'Delivering'
                    WHERE OrderId = ? AND IsDeleted = 0 AND PickedUpAt IS NULL
                    """;
                try (PreparedStatement psUpdate = connection.prepareStatement(updateLogSql)) {
                    psUpdate.setInt(1, orderId);
                    psUpdate.executeUpdate();
                }
                // Make sure ShipperConfirmedAt is also populated if it was skipped
                String backfillSql = """
                    UPDATE DeliveryLogs 
                    SET ShipperConfirmedAt = GETDATE()
                    WHERE OrderId = ? AND IsDeleted = 0 AND ShipperConfirmedAt IS NULL
                    """;
                try (PreparedStatement psBackfill = connection.prepareStatement(backfillSql)) {
                    psBackfill.setInt(1, orderId);
                    psBackfill.executeUpdate();
                }
            } else if ("Completed".equals(status)) {
                String updateLogSql = """
                    UPDATE DeliveryLogs 
                    SET DeliveredAt = GETDATE(), Status = 'Delivered'
                    WHERE OrderId = ? AND IsDeleted = 0 AND DeliveredAt IS NULL
                    """;
                try (PreparedStatement psUpdate = connection.prepareStatement(updateLogSql)) {
                    psUpdate.setInt(1, orderId);
                    psUpdate.executeUpdate();
                }
                // Make sure all previous timestamps are backfilled if skipped
                String backfillSql = """
                    UPDATE DeliveryLogs 
                    SET ShipperConfirmedAt = COALESCE(ShipperConfirmedAt, GETDATE()),
                        PickedUpAt = COALESCE(PickedUpAt, GETDATE())
                    WHERE OrderId = ? AND IsDeleted = 0
                    """;
                try (PreparedStatement psBackfill = connection.prepareStatement(backfillSql)) {
                    psBackfill.setInt(1, orderId);
                    psBackfill.executeUpdate();
                }
            }

        } catch (Exception e) {
            System.err.println("Lỗi updateOrderStatus: " + e.getMessage());
        }
    }

    public List<Order> getAllOrdersWithCustomerName() {
        List<Order> list = new ArrayList<>();
        try {
            String sql = """
                    SELECT o.OrderId, o.OrderType, o.OrderStatus, o.TotalAmount, o.DiscountAmount, o.FinalAmount, o.CreatedAt, o.PaymentMethod, o.ShipperId, tb.TableCode AS TableName, o.Note, c.FullName, sh.FullName AS ShipperName
                    FROM Orders o 
                    LEFT JOIN Customers c ON o.CustomerId = c.CustomerId 
                    LEFT JOIN TableSessions ts ON o.TableSessionId = ts.SessionId
                    LEFT JOIN Tables tb ON ts.TableId = tb.TableId
                    LEFT JOIN Shippers sh ON o.ShipperId = sh.ShipperId
                    WHERE o.IsDeleted = 0 
                    ORDER BY o.CreatedAt DESC""";
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
                o.setShipperId(rs.getObject("ShipperId") != null ? rs.getInt("ShipperId") : null);
                o.setShipperName(rs.getString("ShipperName"));
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
            String sql = "SELECT o.OrderId, o.OrderType, o.OrderStatus, o.CreatedAt, tb.TableCode AS TableName, o.Note, c.FullName "
                    + "FROM Orders o "
                    + "LEFT JOIN Customers c ON o.CustomerId = c.CustomerId "
                    + "LEFT JOIN TableSessions ts ON o.TableSessionId = ts.SessionId "
                    + "LEFT JOIN Tables tb ON ts.TableId = tb.TableId "
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
            String sql = "SELECT o.OrderId, o.OrderType, o.OrderStatus, o.CreatedAt, "
                    + "o.Note, tb.TableCode AS TableName, c.FullName "
                    + "FROM Orders o "
                    + "LEFT JOIN Customers c ON o.CustomerId = c.CustomerId "
                    + "LEFT JOIN TableSessions ts ON o.TableSessionId = ts.SessionId "
                    + "LEFT JOIN Tables tb ON ts.TableId = tb.TableId "
                    + "WHERE o.IsDeleted = 0 AND o.OrderStatus = 'Completed' "
                    + "ORDER BY o.CreatedAt DESC";
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                Order o = new Order();
                o.setOrderId(rs.getInt("OrderId"));
                o.setOrderType(rs.getString("OrderType"));
                o.setOrderStatus(rs.getString("OrderStatus"));
                o.setNote(rs.getString("Note"));
                o.setTableName(rs.getString("TableName"));
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
                    + "PaymentMethod, Note, TableSessionId, CreatedAt, IsDeleted) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), 0)";
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
            if (order.getTableSessionId() != null) {
                st.setInt(12, order.getTableSessionId());
            } else {
                st.setNull(12, java.sql.Types.INTEGER);
            }
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

    /**
     * Lấy tất cả đơn hàng Online của một khách hàng, sắp xếp mới nhất trước.
     * Dùng cho màn Theo dõi đơn hàng.
     */
    public List<Order> getOnlineOrdersByCustomerId(int customerId) {
        List<Order> list = new ArrayList<>();
        try {
            String sql = """
                         SELECT OrderId, CustomerId, ShipperId, OrderAddressId, DiscountId,
                                OrderType, OrderStatus, PaymentMethod, PaymentStatus,
                                TotalAmount, DiscountAmount, FinalAmount, CreatedAt
                         FROM Orders
                         WHERE CustomerId = ? AND OrderType = 'Online' AND IsDeleted = 0
                         ORDER BY CreatedAt DESC
                         """;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, customerId);
                try (ResultSet rs2 = ps.executeQuery()) {
                    while (rs2.next()) {
                        Order o = new Order();
                        o.setOrderId(rs2.getInt("OrderId"));
                        o.setCustomerId(rs2.getObject("CustomerId") != null ? rs2.getInt("CustomerId") : null);
                        o.setShipperId(rs2.getObject("ShipperId") != null ? rs2.getInt("ShipperId") : null);
                        o.setOrderAddressId(rs2.getObject("OrderAddressId") != null ? rs2.getInt("OrderAddressId") : null);
                        o.setDiscountId(rs2.getObject("DiscountId") != null ? rs2.getInt("DiscountId") : null);
                        o.setOrderType(rs2.getString("OrderType"));
                        o.setOrderStatus(rs2.getString("OrderStatus"));
                        o.setPaymentMethod(rs2.getString("PaymentMethod"));
                        o.setPaymentStatus(rs2.getString("PaymentStatus"));
                        o.setTotalAmount(rs2.getBigDecimal("TotalAmount"));
                        o.setDiscountAmount(rs2.getBigDecimal("DiscountAmount"));
                        o.setFinalAmount(rs2.getBigDecimal("FinalAmount"));
                        Timestamp ts = rs2.getTimestamp("CreatedAt");
                        if (ts != null) {
                            o.setCreatedAt(ts.toLocalDateTime());
                        }
                        list.add(o);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("getOnlineOrdersByCustomerId Error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lấy địa chỉ giao hàng theo OrderAddressId.
     */
    public model.OrderAddress getOrderAddressByOrderAddressId(int orderAddressId) {
        try {
            String sql = """
                         SELECT OrderAddressId, CustomerId, ZoneId, RecipientName,
                                RecipientPhone, AddressDetail, Note
                         FROM OrderAddresses
                         WHERE OrderAddressId = ? AND IsDeleted = 0
                         """;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, orderAddressId);
                try (ResultSet rs2 = ps.executeQuery()) {
                    if (rs2.next()) {
                        model.OrderAddress addr = new model.OrderAddress();
                        addr.setOrderAddressId(rs2.getInt("OrderAddressId"));
                        addr.setCustomerId(rs2.getInt("CustomerId"));
                        addr.setZoneId(rs2.getInt("ZoneId"));
                        addr.setRecipientName(rs2.getString("RecipientName"));
                        addr.setRecipientPhone(rs2.getString("RecipientPhone"));
                        addr.setAddressDetail(rs2.getString("AddressDetail"));
                        addr.setNote(rs2.getString("Note"));
                        return addr;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("getOrderAddressByOrderAddressId Error: " + e.getMessage());
        }
        return null;
    }

    public List<HashMap<String, Object>> getTodaySoldProductSizeRows() {
        List<HashMap<String, Object>> rows = new ArrayList<>();
        String sql = """
                     SET NOCOUNT ON;

                     DECLARE @Today DATE = CAST(GETDATE() AS DATE);
                     DECLARE @AuditDate DATE = @Today;
                     DECLARE @Start DATETIME2;
                     DECLARE @End DATETIME2;

                     IF NOT EXISTS (
                         SELECT 1
                         FROM Orders
                         WHERE IsDeleted = 0
                           AND PaymentStatus = 'Paid'
                           AND CreatedAt >= CAST(@AuditDate AS DATETIME2)
                           AND CreatedAt < DATEADD(DAY, 1, CAST(@AuditDate AS DATETIME2))
                     )
                     BEGIN
                         SELECT TOP 1 @AuditDate = CAST(CreatedAt AS DATE)
                         FROM Orders
                         WHERE IsDeleted = 0
                           AND PaymentStatus = 'Paid'
                         ORDER BY CreatedAt DESC;
                     END

                     SET @Start = CAST(@AuditDate AS DATETIME2);
                     SET @End = DATEADD(DAY, 1, @Start);

                     SELECT od.ProductId,
                            od.SizeId,
                            p.ProductName,
                            s.SizeName,
                            SUM(od.Quantity) AS SoldQuantity,
                            MAX(od.UnitPrice) AS UnitPrice,
                            SUM(od.Quantity * od.UnitPrice) AS Revenue,
                            @AuditDate AS AuditDate
                     FROM OrderDetails od
                     JOIN Orders o ON o.OrderId = od.OrderId
                     JOIN Products p ON p.ProductId = od.ProductId
                     JOIN Sizes s ON s.SizeId = od.SizeId
                     WHERE od.IsDeleted = 0
                       AND o.IsDeleted = 0
                       AND o.PaymentStatus = 'Paid'
                       AND o.CreatedAt >= @Start
                       AND o.CreatedAt < @End
                     GROUP BY od.ProductId, od.SizeId, p.ProductName, s.SizeName
                     ORDER BY p.ProductName ASC, s.SizeName ASC
                     """;
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs2 = ps.executeQuery()) {
            while (rs2.next()) {
                HashMap<String, Object> row = new HashMap<>();
                row.put("productId", rs2.getInt("ProductId"));
                row.put("sizeId", rs2.getInt("SizeId"));
                row.put("productName", rs2.getString("ProductName"));
                row.put("sizeName", rs2.getString("SizeName"));
                row.put("soldQuantity", rs2.getInt("SoldQuantity"));
                row.put("unitPrice", rs2.getBigDecimal("UnitPrice"));
                row.put("revenue", rs2.getBigDecimal("Revenue"));
                row.put("auditDate", rs2.getDate("AuditDate"));
                rows.add(row);
            }
        } catch (Exception e) {
            System.err.println("getTodaySoldProductSizeRows Error: " + e.getMessage());
        }
        return rows;
    }

    public List<HashMap<String, Object>> getSoldProductSizeRowsByDate(LocalDate auditDate) {
        List<HashMap<String, Object>> rows = new ArrayList<>();
        String sql = """
                     DECLARE @AuditDate DATE = ?;
                     DECLARE @Start DATETIME2 = CAST(@AuditDate AS DATETIME2);
                     DECLARE @End DATETIME2 = DATEADD(DAY, 1, @Start);

                     SELECT od.ProductId,
                            od.SizeId,
                            p.ProductName,
                            s.SizeName,
                            SUM(od.Quantity) AS SoldQuantity,
                            MAX(od.UnitPrice) AS UnitPrice,
                            SUM(od.Quantity * od.UnitPrice) AS Revenue,
                            @AuditDate AS AuditDate
                     FROM OrderDetails od
                     JOIN Orders o ON o.OrderId = od.OrderId
                     JOIN Products p ON p.ProductId = od.ProductId
                     JOIN Sizes s ON s.SizeId = od.SizeId
                     WHERE od.IsDeleted = 0
                       AND o.IsDeleted = 0
                       AND o.PaymentStatus = 'Paid'
                       AND o.CreatedAt >= @Start
                       AND o.CreatedAt < @End
                     GROUP BY od.ProductId, od.SizeId, p.ProductName, s.SizeName
                     ORDER BY p.ProductName ASC, s.SizeName ASC
                     """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(auditDate));
            try (ResultSet rs2 = ps.executeQuery()) {
                while (rs2.next()) {
                    HashMap<String, Object> row = new HashMap<>();
                    row.put("productId", rs2.getInt("ProductId"));
                    row.put("sizeId", rs2.getInt("SizeId"));
                    row.put("productName", rs2.getString("ProductName"));
                    row.put("sizeName", rs2.getString("SizeName"));
                    row.put("soldQuantity", rs2.getInt("SoldQuantity"));
                    row.put("unitPrice", rs2.getBigDecimal("UnitPrice"));
                    row.put("revenue", rs2.getBigDecimal("Revenue"));
                    row.put("auditDate", rs2.getDate("AuditDate"));
                    rows.add(row);
                }
            }
        } catch (Exception e) {
            System.err.println("getSoldProductSizeRowsByDate Error: " + e.getMessage());
        }
        return rows;
    }

    public Map<String, Object> getCashierDashboard() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("todayRevenue", BigDecimal.ZERO);
        stats.put("todayOrders", 0);
        stats.put("pendingOrders", 0);
        stats.put("newCustomers", 0);

        try {
            // Doanh thu hom nay
            String sqlRevenue = """
                    SELECT SUM(FinalAmount) AS Revenue 
                    FROM Orders 
                    WHERE CAST(CreatedAt AS DATE) = CAST(GETDATE() AS DATE) AND IsDeleted = 0
                                """;
            PreparedStatement st1 = connection.prepareStatement(sqlRevenue);
            ResultSet rs1 = st1.executeQuery();
            if (rs1.next()) {
                BigDecimal rev = rs1.getBigDecimal("Revenue");
                if (rev != null) {
                    stats.put("todayRevenue", rev);
                }
            }

            // Don hang hom nay
            String sqlOrders = """
                 SELECT COUNT(OrderId) AS OrdersCount 
                 FROM Orders 
                 WHERE CAST(CreatedAt AS DATE) = CAST(GETDATE() AS DATE) AND IsDeleted = 0
                               """;
            PreparedStatement st2 = connection.prepareStatement(sqlOrders);
            ResultSet rs2 = st2.executeQuery();
            if (rs2.next()) {
                stats.put("todayOrders", rs2.getInt("OrdersCount"));
            }

            // Don cho xu ly (Pending / Preparing) hom nay
            String sqlPending = """
            SELECT COUNT(OrderId) AS PendingCount 
            FROM Orders 
            WHERE OrderStatus IN ('Pending', 'Preparing') 
            AND CAST(CreatedAt AS DATE) = CAST(GETDATE() AS DATE) AND IsDeleted = 0
                                """;
            PreparedStatement st3 = connection.prepareStatement(sqlPending);
            ResultSet rs3 = st3.executeQuery();
            if (rs3.next()) {
                stats.put("pendingOrders", rs3.getInt("PendingCount"));
            }

            // Khach hang moi hom nay
            String sqlCust = """
            SELECT COUNT(CustomerId) AS CustCount 
            FROM Customers 
            WHERE CAST(CreatedAt AS DATE) = CAST(GETDATE() AS DATE) AND IsDeleted = 0
                             """;
            PreparedStatement st4 = connection.prepareStatement(sqlCust);
            ResultSet rs4 = st4.executeQuery();
            if (rs4.next()) {
                stats.put("newCustomers", rs4.getInt("CustCount"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }

    public List<model.DeliveryLog> getDeliveryLogsByOrderId(int orderId) {
        List<model.DeliveryLog> list = new ArrayList<>();
        String sql = """
                     SELECT DeliveryLogId, OrderId, ShipperId, Status, FailReason,
                            EstimatedDeliveryAt, PickedUpAt, ShipperConfirmedAt,
                            CustomerConfirmedAt, DeliveredAt, IsOverdue, ProofImageUrl,
                            Note, CreatedAt, IsDeleted
                     FROM DeliveryLogs
                     WHERE OrderId = ? AND IsDeleted = 0
                     ORDER BY CreatedAt ASC
                     """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs2 = ps.executeQuery()) {
                while (rs2.next()) {
                    model.DeliveryLog log = new model.DeliveryLog();
                    log.setDeliveryLogId(rs2.getInt("DeliveryLogId"));
                    log.setOrderId(rs2.getInt("OrderId"));
                    log.setShipperId(rs2.getInt("ShipperId"));
                    log.setStatus(rs2.getString("Status"));
                    log.setFailReason(rs2.getString("FailReason"));
                    if (rs2.getTimestamp("EstimatedDeliveryAt") != null) {
                        log.setEstimatedDeliveryAt(rs2.getTimestamp("EstimatedDeliveryAt").toLocalDateTime());
                    }
                    if (rs2.getTimestamp("PickedUpAt") != null) {
                        log.setPickedUpAt(rs2.getTimestamp("PickedUpAt").toLocalDateTime());
                    }
                    if (rs2.getTimestamp("ShipperConfirmedAt") != null) {
                        log.setShipperConfirmedAt(rs2.getTimestamp("ShipperConfirmedAt").toLocalDateTime());
                    }
                    if (rs2.getTimestamp("CustomerConfirmedAt") != null) {
                        log.setCustomerConfirmedAt(rs2.getTimestamp("CustomerConfirmedAt").toLocalDateTime());
                    }
                    if (rs2.getTimestamp("DeliveredAt") != null) {
                        log.setDeliveredAt(rs2.getTimestamp("DeliveredAt").toLocalDateTime());
                    }
                    log.setIsOverdue(rs2.getBoolean("IsOverdue"));
                    log.setProofImageUrl(rs2.getString("ProofImageUrl"));
                    log.setNote(rs2.getString("Note"));
                    if (rs2.getTimestamp("CreatedAt") != null) {
                        log.setCreatedAt(rs2.getTimestamp("CreatedAt").toLocalDateTime());
                    }
                    list.add(log);
                }
            }
        } catch (SQLException e) {
            System.err.println("getDeliveryLogsByOrderId Error: " + e.getMessage());
        }
        return list;
    }
}
