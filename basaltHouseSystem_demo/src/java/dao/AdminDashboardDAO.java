/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author MSI
 */
public class AdminDashboardDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    // ─────────────────────────────────────────────────────────────────────
    // 1. Doanh thu hôm nay (chỉ đơn đã thanh toán)
    // ─────────────────────────────────────────────────────────────────────
    public BigDecimal getRevenueToday() {
        BigDecimal revenue = BigDecimal.ZERO;
        try {
            String sql = """
                         SELECT ISNULL(SUM(FinalAmount), 0) AS Revenue
                         FROM Orders
                         WHERE CAST(CreatedAt AS DATE) = CAST(GETDATE() AS DATE)
                           AND PaymentStatus = 'Paid'
                           AND IsDeleted = 0
                         """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            if (rs.next()) {
                revenue = rs.getBigDecimal("Revenue");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return revenue;
    }
    
     // ─────────────────────────────────────────────────────────────────────
    // 2. Tổng đơn hôm nay
    // ─────────────────────────────────────────────────────────────────────
    public int getTotalOrdersToday() {
        int total = 0;
        try {
            String sql = """
                         SELECT COUNT(*) AS Total
                         FROM Orders
                         WHERE CAST(CreatedAt AS DATE) = CAST(GETDATE() AS DATE)
                           AND IsDeleted = 0
                         """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            if (rs.next()) {
                total = rs.getInt("Total");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return total;
    }
     // ─────────────────────────────────────────────────────────────────────
    // 3. Đơn đang giao
    // ─────────────────────────────────────────────────────────────────────
    public int getDeliveringOrders() {
        int total = 0;
        try {
            String sql = """
                         SELECT COUNT(*) AS Total
                         FROM Orders
                         WHERE OrderStatus = 'Delivering'
                           AND IsDeleted = 0
                         """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            if (rs.next()) {
                total = rs.getInt("Total");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return total;
    }
     // ─────────────────────────────────────────────────────────────────────
    // 4. Số nguyên liệu dưới mức tối thiểu
    // ─────────────────────────────────────────────────────────────────────
    public int getLowStockCount() {
        int total = 0;
        try {
            String sql = """
                         SELECT COUNT(*) AS Total
                         FROM Ingredients
                         WHERE StockQuantity <= MinStockQuantity
                           AND IsActive  = 1
                           AND IsDeleted = 0
                         """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            if (rs.next()) {
                total = rs.getInt("Total");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return total;
    }
    
     // ─────────────────────────────────────────────────────────────────────
    // 5. Doanh thu 7 ngày gần nhất (mỗi ngày 1 dòng)
    // ─────────────────────────────────────────────────────────────────────
    public List<Map<String, Object>> getLast7DaysRevenue() {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            String sql = """
                         SELECT CAST(CreatedAt AS DATE)      AS Day,
                                ISNULL(SUM(FinalAmount), 0)  AS Revenue
                         FROM Orders
                         WHERE CreatedAt >= DATEADD(DAY, -6, CAST(GETDATE() AS DATE))
                           AND PaymentStatus = 'Paid'
                           AND IsDeleted = 0
                         GROUP BY CAST(CreatedAt AS DATE)
                         ORDER BY Day ASC
                         """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("day", rs.getString("Day"));
                row.put("revenue", rs.getBigDecimal("Revenue"));
                list.add(row);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;
    }
     // ─────────────────────────────────────────────────────────────────────
    // 6. Tổng doanh thu 7 ngày trước (để tính % tăng trưởng)
    // ─────────────────────────────────────────────────────────────────────
    public BigDecimal getPrev7DaysRevenue() {
        BigDecimal revenue = BigDecimal.ZERO;
        try {
            String sql = """
                         SELECT ISNULL(SUM(FinalAmount), 0) AS Revenue
                         FROM Orders
                         WHERE CreatedAt >= DATEADD(DAY, -13, CAST(GETDATE() AS DATE))
                           AND CreatedAt <  DATEADD(DAY, -6,  CAST(GETDATE() AS DATE))
                           AND PaymentStatus = 'Paid'
                           AND IsDeleted = 0
                         """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            if (rs.next()) {
                revenue = rs.getBigDecimal("Revenue");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return revenue;
    }
    // ─────────────────────────────────────────────────────────────────────
    // 7. Top 5 sản phẩm bán chạy (mọi thời gian)
    // ─────────────────────────────────────────────────────────────────────
    public List<Map<String, Object>> getTopProducts(int limit) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            String sql = """
                         SELECT TOP (?) p.ProductName,
                                SUM(od.Quantity)               AS TotalSold,
                                SUM(od.Quantity * od.UnitPrice) AS TotalRevenue
                         FROM OrderDetails od
                         JOIN Products p ON p.ProductId = od.ProductId
                         JOIN Orders   o ON o.OrderId   = od.OrderId
                         WHERE o.IsDeleted  = 0
                           AND od.IsDeleted = 0
                           AND p.IsDeleted  = 0
                         GROUP BY p.ProductId, p.ProductName
                         ORDER BY TotalSold DESC
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, limit);
            rs = st.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("productName", rs.getString("ProductName"));
                row.put("totalSold", rs.getInt("TotalSold"));
                row.put("totalRevenue", rs.getBigDecimal("TotalRevenue"));
                list.add(row);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;
    }
     // ─────────────────────────────────────────────────────────────────────
    // 8. Đơn hàng mới nhất (10 đơn)
    // ─────────────────────────────────────────────────────────────────────
    public List<Map<String, Object>> getRecentOrders(int limit) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            String sql = """
                         SELECT TOP (?) o.OrderId, o.OrderType, o.OrderStatus,
                                o.PaymentStatus, o.FinalAmount,
                                FORMAT(o.CreatedAt, 'dd/MM HH:mm') AS CreatedAt,
                                c.FullName AS CustomerName
                         FROM Orders o
                         LEFT JOIN Customers c ON c.CustomerId = o.CustomerId
                         WHERE o.IsDeleted = 0
                         ORDER BY o.CreatedAt DESC
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, limit);
            rs = st.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("orderId", rs.getInt("OrderId"));
                row.put("orderType", rs.getString("OrderType"));
                row.put("orderStatus", rs.getString("OrderStatus"));
                row.put("paymentStatus", rs.getString("PaymentStatus"));
                row.put("finalAmount", rs.getBigDecimal("FinalAmount"));
                row.put("createdAt", rs.getString("CreatedAt"));
                row.put("customerName", rs.getString("CustomerName"));
                list.add(row);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;
    }
    // ─────────────────────────────────────────────────────────────────────
    // 9. Danh sách nguyên liệu dưới mức tối thiểu
    // ─────────────────────────────────────────────────────────────────────
    public List<Map<String, Object>> getLowStockAlerts() {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            String sql = """
                         SELECT IngredientName, StockQuantity, MinStockQuantity, Unit
                         FROM Ingredients
                         WHERE StockQuantity <= MinStockQuantity
                           AND IsActive  = 1
                           AND IsDeleted = 0
                         ORDER BY (StockQuantity - MinStockQuantity) ASC
                         """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("ingredientName", rs.getString("IngredientName"));
                row.put("stockQuantity", rs.getBigDecimal("StockQuantity"));
                row.put("minStockQuantity", rs.getBigDecimal("MinStockQuantity"));
                row.put("unit", rs.getString("Unit"));
                list.add(row);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;
    }
     // ─────────────────────────────────────────────────────────────────────
    // 10. Phiếu nhập hàng đang Pending
    // ─────────────────────────────────────────────────────────────────────
    public int getPendingImportCount() {
        int total = 0;
        try {
            String sql = """
                         SELECT COUNT(*) AS Total
                         FROM ImportInvoices
                         WHERE Status    = 'Pending'
                           AND IsDeleted = 0
                         """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            if (rs.next()) {
                total = rs.getInt("Total");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return total;
    }
      // ─────────────────────────────────────────────────────────────────────
    // 11. PendingRegistrations chưa xử lý
    // ─────────────────────────────────────────────────────────────────────
    public int getPendingRegistrationCount() {
        int total = 0;
        try {
            String sql = """
                         SELECT COUNT(*) AS Total
                         FROM PendingRegistrations
                         WHERE IsUsed    = 0
                           AND IsDeleted = 0
                           AND OtpExpiredAt > GETDATE()
                         """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            if (rs.next()) {
                total = rs.getInt("Total");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return total;
    }
    
    // Helper: tính % tăng trưởng 
    public double calcGrowthPercent(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return current.subtract(previous)
                      .divide(previous, 4, RoundingMode.HALF_UP)
                      .multiply(BigDecimal.valueOf(100))
                      .doubleValue();
    }
}
