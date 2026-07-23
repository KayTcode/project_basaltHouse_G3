/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jdk.jfr.Timespan;
import model.Order;
import model.OrderAddress;
import model.ProcessOrderResult;
import model.Shipper;

/**
 *
 * @author KayT
 */
public class ShipperDAO extends DBContext {

    private String sql = "";
    private static PreparedStatement ps;
    private static ResultSet rs;

    public Shipper getShipperByAccountId(int accountId) {
        sql = """
              SELECT [ShipperId]
                    ,[AccountId]
                    ,[FullName]
                    ,[Phone]
                    ,[Address]
                    ,[AvatarUrl]
                    ,[DriverLicenseImg]
                    ,[VehicleRegistrationImg]
                    ,[IsAvailable]
                    ,[CreatedAt]
                    ,[IsDeleted]
                FROM [dbo].[Shippers]
                WHERE AccountId = ? AND IsDeleted = 0
              """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setObject(1, accountId);
            rs = ps.executeQuery();
            if (rs.next()) {
                Shipper s = new Shipper();
                s.setShipperId(rs.getInt("ShipperId"));
                s.setAccountId(rs.getInt("AccountId"));
                s.setFullName(rs.getString("FullName"));
                s.setPhone(rs.getString("Phone"));
                s.setAddress(rs.getString("Address"));
                s.setAvatarUrl(rs.getString("AvatarUrl"));
                s.setDriverLicenseImg(rs.getString("DriverLicenseImg"));
                s.setVehicleRegistrationImg(rs.getString("VehicleRegistrationImg"));
                s.setIsAvailable(rs.getBoolean("IsAvailable"));
                java.sql.Timestamp createAt = rs.getTimestamp("CreatedAt");
                if (createAt != null) {
                    s.setCreatedAt(createAt.toLocalDateTime());
                }
                s.setIsDeleted(rs.getBoolean("IsDeleted"));
                return s;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public List<Order> getPendingShipperOrders(int shipperId) {
        sql = """
              SELECT o.[OrderId], o.[CustomerId], o.[CashierId], o.[ShipperId],
                                     o.[TableSessionId], o.[OrderAddressId], o.[DiscountId],
                                     o.[OrderType], o.[OrderStatus], o.[PaymentMethod], o.[PaymentStatus],
                                     o.[TotalAmount], o.[DiscountAmount], o.[FinalAmount],
                                     o.[CreatedAt], o.[IsDeleted],
                                     ISNULL(c.fullName, 'Khách tại quán') AS customerName
              FROM [Orders] o
              LEFT JOIN [Customers] c ON o.[CustomerId] = c.[CustomerId]
              WHERE o.orderStatus = 'Preparing'
                                AND o.isDeleted = 0
                                AND (o.ShipperId = ? OR o.ShipperId IS NULL)
                              ORDER BY o.createdAt ASC
              """;
        List<Order> list = new ArrayList<>();
        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, shipperId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Order o = new Order();
                o.setOrderId(rs.getInt("OrderId"));
                o.setCustomerId(rs.getInt("CustomerId"));
                o.setCashierId(rs.getInt("CashierId"));
                o.setShipperId(rs.getInt("ShipperId"));
                o.setTableSessionId(rs.getInt("TableSessionId"));
                o.setOrderAddressId(rs.getInt("OrderAddressId"));
                o.setDiscountId(rs.getInt("DiscountId"));
                o.setOrderType(rs.getString("OrderType"));
                o.setOrderStatus(rs.getString("OrderStatus"));
                o.setPaymentMethod(rs.getString("PaymentMethod"));
                o.setPaymentStatus(rs.getString("PaymentStatus"));
                o.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                o.setDiscountAmount(rs.getBigDecimal("DiscountAmount"));
                o.setFinalAmount(rs.getBigDecimal("FinalAmount"));
                Timestamp createAt = rs.getTimestamp("CreatedAt");
                if (createAt != null) {
                    o.setCreatedAt(createAt.toLocalDateTime());
                }
                o.setIsDeleted(rs.getBoolean("IsDeleted"));
                o.setCustomerName(rs.getString("customerName"));
                list.add(o);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
    public Order getCurrentShippingOrder(int shipperId) {
        sql = """
              SELECT o.orderId, o.CustomerId, o.CashierId, o.ShipperId,
                                     o.TableSessionId, o.OrderAddressId, o.DiscountId,
                                     o.OrderType, o.OrderStatus, o.PaymentMethod, o.PaymentStatus,
                                     o.TotalAmount, o.DiscountAmount, o.FinalAmount,
                                     o.CreatedAt, o.IsDeleted,
                                     ISNULL(c.fullName, 'Khách tại quán') AS customerName
                              FROM Orders o
                              LEFT JOIN Customers c ON o.customerId = c.customerId
                              WHERE o.shipperId = ?
                                AND o.orderStatus = 'Delivering'
                                AND o.isDeleted = 0
              """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setObject(1, shipperId);
            rs = ps.executeQuery();
            if (rs.next()) {
                Order o = new Order();
                o.setOrderId(rs.getInt("OrderId"));
                o.setCustomerId(rs.getInt("CustomerId"));
                o.setCashierId(rs.getInt("CashierId"));
                o.setShipperId(rs.getInt("ShipperId"));
                o.setTableSessionId(rs.getInt("TableSessionId"));
                o.setOrderAddressId(rs.getInt("OrderAddressId"));
                o.setDiscountId(rs.getInt("DiscountId"));
                o.setOrderType(rs.getString("OrderType"));
                o.setOrderStatus(rs.getString("OrderStatus"));
                o.setPaymentMethod(rs.getString("PaymentMethod"));
                o.setPaymentStatus(rs.getString("PaymentStatus"));
                o.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                o.setDiscountAmount(rs.getBigDecimal("DiscountAmount"));
                o.setFinalAmount(rs.getBigDecimal("FinalAmount"));
                Timestamp createAt = rs.getTimestamp("CreatedAt");
                if (createAt != null) {
                    o.setCreatedAt(createAt.toLocalDateTime());
                }
                o.setIsDeleted(rs.getBoolean("IsDeleted"));
                o.setCustomerName(rs.getString("customerName"));
                return o;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public OrderAddress getOrderAddressById(int orderAddressId) {
        sql = """
              SELECT [OrderAddressId]
                    ,[CustomerId]
                    ,[ZoneId]
                    ,[RecipientName]
                    ,[RecipientPhone]
                    ,[AddressDetail]
                    ,[Note]
                    ,[IsDefault]
                    ,[CreatedAt]
                    ,[IsDeleted]
                FROM [dbo].[OrderAddresses]
                WHERE [OrderAddressId] = ? AND [IsDeleted] = 0
              """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setObject(1, orderAddressId);
            rs = ps.executeQuery();
            if (rs.next()) {
                OrderAddress oa = new OrderAddress();
                oa.setOrderAddressId(rs.getInt("OrderAddressId"));
                oa.setCustomerId(rs.getInt("CustomerId"));
                oa.setZoneId(rs.getInt("ZoneId"));
                oa.setRecipientName(rs.getString("RecipientName"));
                oa.setRecipientPhone(rs.getString("RecipientPhone"));
                oa.setAddressDetail(rs.getString("AddressDetail"));
                oa.setNote(rs.getString("Note"));
                oa.setIsDefault(rs.getBoolean("IsDefault"));
                Timestamp createAt = rs.getTimestamp("CreatedAt");
                if (createAt != null) {
                    oa.setCreatedAt(createAt.toLocalDateTime());
                }
                oa.setIsDeleted(rs.getBoolean("IsDeleted"));
                return oa;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public ProcessOrderResult acceptOrder(int orderId, int shipperId) throws SQLException {
        ProcessOrderResult result = new ProcessOrderResult();
        try {
            connection.setAutoCommit(false);
            if (hasActiveShippingOrder(connection, shipperId)) {
                result.addError("Bạn phải hoàn thành đơn hàng trước khi nhận đơn");
                connection.rollback();
                return result;
            }
            String updateOrderSql = """
                                UPDATE Orders
                                SET OrderStatus = 'Delivering'
                                WHERE OrderId = ? AND ShipperId = ? AND OrderStatus = 'Preparing'
                                """;
            try (PreparedStatement ps = connection.prepareStatement(updateOrderSql)) {
                ps.setObject(1, orderId);
                ps.setObject(2, shipperId);
                int rows = ps.executeUpdate();
                if (rows == 0) {
                    result.addError("Đơn hàng không hợp lệ hoặc không được gán cho bạn.");
                    connection.rollback();
                    return result;
                }
            }
            String insertLogSql = """
                              INSERT INTO DeliveryLogs
                              (OrderId, ShipperId, Status, PickedUpAt, CreatedAt, IsDeleted)
                              VALUES (?,?,'Delivering', ?, ?, 0)
                              """;
            try (PreparedStatement ps = connection.prepareStatement(insertLogSql)) {
                LocalDateTime now = LocalDateTime.now();
                ps.setObject(1, orderId);
                ps.setObject(2, shipperId);
                ps.setTimestamp(3, Timestamp.valueOf(now));
                ps.setTimestamp(4, Timestamp.valueOf(now));
                ps.executeUpdate();
            }
            connection.commit();
            result.setSuccess(true);
        } catch (SQLException e) {
            connection.rollback();
            throw new RuntimeException(e);
        } finally {
            connection.setAutoCommit(true);
        }
        return result;
    }

   public boolean assignShipper(int orderId, int shipperId, Integer cashierId) {
        sql = """
              UPDATE [dbo].[Orders]
              SET [ShipperId] = ?, [OrderStatus] = 'Delivering', [CashierId] = COALESCE([CashierId], ?)
              WHERE [OrderId] = ? AND [IsDeleted] = 0
              """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, shipperId);
            if (cashierId != null) {
                ps.setInt(2, cashierId);
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setInt(3, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public ProcessOrderResult updateDeliveryStatus(int orderId, int shipperId, boolean isSuccess, String note, String proofImageUrl, String failReasion) {
        ProcessOrderResult result = new ProcessOrderResult();
        try {
            connection.setAutoCommit(false);
            try {
                String newOrderStatus = isSuccess ? "Delivered" : "Failed";
                String newLogStatus = isSuccess ? "Delivered" : "Failed";

                String updateOrderSql = """
                                        UPDATE Orders
                                        SET 
                                        OrderStatus = ?,
                                        PaymentStatus = 'Paid'
                                        WHERE OrderId = ? AND ShipperId = ? AND OrderStatus = 'Delivering'
                                        """;
                try (PreparedStatement ps = connection.prepareStatement(updateOrderSql)) {
                    ps.setObject(1, newLogStatus);
                    ps.setObject(2, orderId);
                    ps.setObject(3, shipperId);
                    int rows = ps.executeUpdate();
                    if (rows == 0) {
                        result.addError("Không tìm thấy đơn hàng hợp lệ để cập nhật. Vui lòng thử lại");
                        connection.rollback();
                        return result;
                    }
                }
                if (isSuccess) {
                    String updateLogSql = """
                                              UPDATE DeliveryLogs
                                              SET 
                                              Status = ?,
                                              DeliveredAt = ?,
                                              ProofImageUrl = ?,
                                              Note = ?
                                              WHERE OrderId = ? AND ShipperId = ? AND Status = 'Delivering'
                                              """;
                    try (PreparedStatement ps = connection.prepareStatement(updateLogSql)) {
                        ps.setObject(1, newLogStatus);
                        ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                        ps.setObject(3, proofImageUrl);
                        ps.setObject(4, note);
                        ps.setObject(5, orderId);
                        ps.setObject(6, shipperId);
                        ps.executeUpdate();
                    }
                } else {
                    String updateLogSql = """
                                          UPDATE DeliveryLogs
                                          SET
                                          Status = ?,
                                          FailReason = ?
                                          WHERE OrderId = ? AND ShipperId = ? AND Status = 'Delivering'
                                          """;
                    try (PreparedStatement ps = connection.prepareStatement(updateLogSql)) {
                        ps.setObject(1, newLogStatus);
                        ps.setObject(2, failReasion);
                        ps.setObject(3, orderId);
                        ps.setObject(4, shipperId);
                        ps.executeUpdate();
                    }
                }
                connection.commit();
                result.setSuccess(true);
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
        }
        return result;
    }

    private boolean hasActiveShippingOrder(Connection connection, int shipperId) {
        sql = """
               SELECT COUNT(1) 
               FROM [Orders] WHERE [ShipperId] = ? AND [OrderStatus] = 'Delivering' AND [IsDeleted] = 0
               """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setObject(1, shipperId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Shipper> getActiveShippers() {
        sql = """
              SELECT [ShipperId],[AccountId],[FullName],[Phone],[Address],
                     [AvatarUrl],[IsAvailable],[CreatedAt],[IsDeleted]
              FROM [dbo].[Shippers]
              WHERE [IsAvailable] = 1 AND [IsDeleted] = 0
              ORDER BY [FullName] ASC
              """;
        List<Shipper> list = new ArrayList<>();
        try {
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Shipper s = new Shipper();
                s.setShipperId(rs.getInt("ShipperId"));
                s.setAccountId(rs.getInt("AccountId"));
                s.setFullName(rs.getString("FullName"));
                s.setPhone(rs.getString("Phone"));
                s.setAddress(rs.getString("Address"));
                s.setAvatarUrl(rs.getString("AvatarUrl"));
                s.setIsAvailable(rs.getBoolean("IsAvailable"));
                java.sql.Timestamp createAt = rs.getTimestamp("CreatedAt");
                if (createAt != null) {
                    s.setCreatedAt(createAt.toLocalDateTime());
                }
                s.setIsDeleted(rs.getBoolean("IsDeleted"));
                list.add(s);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    private boolean isOrderPendingShipper(Connection connection, int orderId) {
        sql = """
              SELECT COUNT(1) FROM Orders
              WHERE OrderId = ? AND OrderStatus = 'Preparing' AND IsDeleted = 0
              """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setObject(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}