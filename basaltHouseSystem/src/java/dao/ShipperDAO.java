/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Order;
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

    public List<Order> getPendingShipperOrders() {
        sql = """
              SELECT o.[OrderId], o.[CustomerId], o.[CashierId], o.[ShipperId],
                                     o.[TableSessionId], o.[OrderAddressId], o.[DiscountId],
                                     o.[OrderType], o.[OrderStatus], o.[PaymentMethod], o.[PaymentStatus],
                                     o.[TotalAmount], o.[DiscountAmount], o.[FinalAmount],
                                     o.[CreatedAt], o.[IsDeleted],
                                     ISNULL(c.fullName, 'Khách vãng lai') AS customerName
              FROM [Orders] o
              LEFT JOIN [Customers] c ON o.[CustomerId] = c.[CustomerId]
              WHERE o.orderStatus = 'Pending'
                                AND o.isDeleted = 0
                              ORDER BY o.createdAt ASC
              """;
        List<Order> list = new ArrayList<>();
        try {
            ps = connection.prepareStatement(sql);
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
//    public Order
}
