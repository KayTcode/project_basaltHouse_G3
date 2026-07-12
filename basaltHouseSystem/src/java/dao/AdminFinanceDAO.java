package dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminFinanceDAO extends DBContext {

    // Helper: build điều kiện WHERE theo kỳ
    private String periodCondition(String period) {
        return switch (period) {
            case "week" ->
                "CreatedAt >= DATEADD(DAY, -6, CAST(GETDATE() AS DATE))";
            case "year" ->
                "YEAR(CreatedAt) = YEAR(GETDATE())";
            default ->
                "YEAR(CreatedAt) = YEAR(GETDATE()) AND MONTH(CreatedAt) = MONTH(GETDATE())";
        };
    }

    // 1. Tổng doanh thu kỳ hiện tại
    public BigDecimal getTotalRevenue(String period) {
        BigDecimal result = BigDecimal.ZERO;
        try {
            String sql = """
                         SELECT ISNULL(SUM(FinalAmount), 0) AS Revenue
                         FROM Orders
                         WHERE %s
                           AND PaymentStatus = 'Paid'
                           AND IsDeleted = 0
                         """.formatted(periodCondition(period));
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                result = rs.getBigDecimal("Revenue");
            }
        } catch (Exception e) {
            System.err.println("[FinanceDAO] getTotalRevenue: " + e.getMessage());
        }
        return result;
    }

    // 2. Tổng doanh thu kỳ trước (để tính % tăng trưởng)
    public BigDecimal getPrevRevenue(String period) {
        BigDecimal result = BigDecimal.ZERO;
        try {
            String sql = switch (period) {
                case "week" ->
                    """
                               SELECT ISNULL(SUM(FinalAmount), 0) AS Revenue
                               FROM Orders
                               WHERE CreatedAt >= DATEADD(DAY, -13, CAST(GETDATE() AS DATE))
                                 AND CreatedAt <  DATEADD(DAY, -6,  CAST(GETDATE() AS DATE))
                                 AND PaymentStatus = 'Paid'
                                 AND IsDeleted = 0
                               """;
                case "year" ->
                    """
                               SELECT ISNULL(SUM(FinalAmount), 0) AS Revenue
                               FROM Orders
                               WHERE YEAR(CreatedAt) = YEAR(GETDATE()) - 1
                                 AND PaymentStatus = 'Paid'
                                 AND IsDeleted = 0
                               """;
                default ->
                    """
                               SELECT ISNULL(SUM(FinalAmount), 0) AS Revenue
                               FROM Orders
                               WHERE YEAR(CreatedAt)  = YEAR(GETDATE())
                                 AND MONTH(CreatedAt) = MONTH(GETDATE()) - 1
                                 AND PaymentStatus = 'Paid'
                                 AND IsDeleted = 0
                               """;
            };
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                result = rs.getBigDecimal("Revenue");
            }
        } catch (Exception e) {
            System.err.println("[FinanceDAO] getPrevRevenue: " + e.getMessage());
        }
        return result;
    }

    // 3. Tổng chi phí nhập kho kỳ hiện tại
    public BigDecimal getTotalImportCost(String period) {
        BigDecimal result = BigDecimal.ZERO;
        try {
            String dateCond = switch (period) {
                case "week" ->
                    "ReceivedDate >= DATEADD(DAY, -6, CAST(GETDATE() AS DATE))";
                case "year" ->
                    "YEAR(ReceivedDate) = YEAR(GETDATE())";
                default ->
                    "YEAR(ReceivedDate) = YEAR(GETDATE()) AND MONTH(ReceivedDate) = MONTH(GETDATE())";
            };
            String sql = """
                         SELECT ISNULL(SUM(TotalReceivedAmount), 0) AS Cost
                         FROM ImportInvoices
                         WHERE %s
                           AND Status = 'Received'
                           AND IsDeleted = 0
                         """.formatted(dateCond);
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                result = rs.getBigDecimal("Cost");
            }
        } catch (Exception e) {
            System.err.println("[FinanceDAO] getTotalImportCost: " + e.getMessage());
        }
        return result;
    }

    // 4. Tổng số đơn hàng kỳ hiện tại
    public int getTotalOrders(String period) {
        int result = 0;
        try {
            String sql = """
                         SELECT COUNT(*) AS Total
                         FROM Orders
                         WHERE %s
                           AND IsDeleted = 0
                         """.formatted(periodCondition(period));
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                result = rs.getInt("Total");
            }
        } catch (Exception e) {
            System.err.println("[FinanceDAO] getTotalOrders: " + e.getMessage());
        }
        return result;
    }

    // 5. Doanh thu & chi phí theo tuần trong tháng (cho biểu đồ cột)
    public List<Map<String, Object>> getWeeklyBreakdown() {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            // Doanh thu từng tuần trong tháng hiện tại
            String sqlRev = """
                            SELECT 'Tuần ' + CAST(
                                       DATEPART(WEEK, CreatedAt)
                                     - DATEPART(WEEK, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))
                                     + 1 AS NVARCHAR) AS WeekLabel,
                                   ISNULL(SUM(FinalAmount), 0) AS Revenue
                            FROM Orders
                            WHERE YEAR(CreatedAt)  = YEAR(GETDATE())
                              AND MONTH(CreatedAt) = MONTH(GETDATE())
                              AND PaymentStatus = 'Paid'
                              AND IsDeleted = 0
                            GROUP BY DATEPART(WEEK, CreatedAt)
                            ORDER BY DATEPART(WEEK, CreatedAt)
                            """;
            Map<String, BigDecimal> revenueMap = new HashMap<>();
            List<String> weekOrder = new ArrayList<>();
            PreparedStatement stRev = connection.prepareStatement(sqlRev);
            ResultSet rsRev = stRev.executeQuery();
            while (rsRev.next()) {
                String lbl = rsRev.getString("WeekLabel");
                revenueMap.put(lbl, rsRev.getBigDecimal("Revenue"));
                weekOrder.add(lbl);
            }

            // Chi phí nhập kho từng tuần
            String sqlCost = """
                             SELECT 'Tuần ' + CAST(
                                        DATEPART(WEEK, ReceivedDate)
                                      - DATEPART(WEEK, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))
                                      + 1 AS NVARCHAR) AS WeekLabel,
                                    ISNULL(SUM(TotalReceivedAmount), 0) AS Cost
                             FROM ImportInvoices
                             WHERE YEAR(ReceivedDate)  = YEAR(GETDATE())
                               AND MONTH(ReceivedDate) = MONTH(GETDATE())
                               AND Status = 'Received'
                               AND IsDeleted = 0
                             GROUP BY DATEPART(WEEK, ReceivedDate)
                             ORDER BY DATEPART(WEEK, ReceivedDate)
                             """;
            Map<String, BigDecimal> costMap = new HashMap<>();
            PreparedStatement stCost = connection.prepareStatement(sqlCost);
            ResultSet rsCost = stCost.executeQuery();
            while (rsCost.next()) {
                costMap.put(rsCost.getString("WeekLabel"), rsCost.getBigDecimal("Cost"));
            }

            // Gộp lại
            for (String lbl : weekOrder) {
                BigDecimal rev = revenueMap.getOrDefault(lbl, BigDecimal.ZERO);
                BigDecimal cost = costMap.getOrDefault(lbl, BigDecimal.ZERO);
                Map<String, Object> row = new HashMap<>();
                row.put("label", lbl);
                row.put("revenue", rev);
                row.put("cost", cost);
                row.put("profit", rev.subtract(cost));
                list.add(row);
            }
        } catch (Exception e) {
            System.err.println("[FinanceDAO] getWeeklyBreakdown: " + e.getMessage());
        }
        return list;
    }

    // 6. Cơ cấu doanh thu: Tại quầy vs Online (Delivery)
    public List<Map<String, Object>> getRevenueByChannel(String period) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            String sql = """
                         SELECT OrderType, ISNULL(SUM(FinalAmount), 0) AS Revenue
                         FROM Orders
                         WHERE %s
                           AND PaymentStatus = 'Paid'
                           AND IsDeleted = 0
                         GROUP BY OrderType
                         """.formatted(periodCondition(period));
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            BigDecimal inStore = BigDecimal.ZERO;
            BigDecimal online = BigDecimal.ZERO;
            while (rs.next()) {
                String type = rs.getString("OrderType");
                BigDecimal rev = rs.getBigDecimal("Revenue");
                // Online = Delivery; còn lại = Tại quầy
                if ("Delivery".equalsIgnoreCase(type)) {
                    online = online.add(rev);
                } else {
                    inStore = inStore.add(rev);
                }
            }
            Map<String, Object> r1 = new HashMap<>();
            r1.put("channel", "Tại quầy");
            r1.put("revenue", inStore);
            Map<String, Object> r2 = new HashMap<>();
            r2.put("channel", "Online");
            r2.put("revenue", online);
            list.add(r1);
            list.add(r2);
        } catch (Exception e) {
            System.err.println("[FinanceDAO] getRevenueByChannel: " + e.getMessage());
        }
        return list;
    }

    // 7. Phương thức thanh toán: Cash vs MoMo / Chuyển khoản
    public List<Map<String, Object>> getPaymentMethodStats(String period) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            // Tiền mặt
            String sqlCash = """
                             SELECT COUNT(*) AS Cnt, ISNULL(SUM(FinalAmount), 0) AS Amount
                             FROM Orders
                             WHERE %s
                               AND PaymentMethod = 'Cash'
                               AND PaymentStatus = 'Paid'
                               AND IsDeleted = 0
                             """.formatted(periodCondition(period));
            PreparedStatement stCash = connection.prepareStatement(sqlCash);
            ResultSet rsCash = stCash.executeQuery();
            if (rsCash.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("method", "Tiền mặt");
                row.put("count", rsCash.getInt("Cnt"));
                row.put("amount", rsCash.getBigDecimal("Amount"));
                list.add(row);
            }

            // MoMo / Chuyển khoản (mọi phương thức != Cash)
            String sqlMomo = """
                             SELECT COUNT(*) AS Cnt, ISNULL(SUM(FinalAmount), 0) AS Amount
                             FROM Orders
                             WHERE %s
                               AND PaymentMethod <> 'Cash'
                               AND PaymentStatus = 'Paid'
                               AND IsDeleted = 0
                             """.formatted(periodCondition(period));
            PreparedStatement stMomo = connection.prepareStatement(sqlMomo);
            ResultSet rsMomo = stMomo.executeQuery();
            if (rsMomo.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("method", "MoMo / Chuyển khoản");
                row.put("count", rsMomo.getInt("Cnt"));
                row.put("amount", rsMomo.getBigDecimal("Amount"));
                list.add(row);
            }
        } catch (Exception e) {
            System.err.println("[FinanceDAO] getPaymentMethodStats: " + e.getMessage());
        }
        return list;
    }

    // 8. Top N sản phẩm theo doanh thu kỳ hiện tại
    public List<Map<String, Object>> getTopProductsByRevenue(String period, int limit) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            String sql = """
                         SELECT TOP (?) p.ProductName,
                                SUM(od.Quantity) AS TotalQty,
                                SUM(od.Quantity * od.UnitPrice) AS TotalRevenue
                         FROM OrderDetails od
                         JOIN Products p ON p.ProductId = od.ProductId
                         JOIN Orders   o ON o.OrderId   = od.OrderId
                         WHERE o.%s
                           AND o.PaymentStatus = 'Paid'
                           AND o.IsDeleted  = 0
                           AND od.IsDeleted = 0
                           AND p.IsDeleted  = 0
                         GROUP BY p.ProductId, p.ProductName
                         ORDER BY TotalRevenue DESC
                         """.formatted(periodCondition(period));
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, limit);
            ResultSet rs = st.executeQuery();
            BigDecimal maxRev = null;
            List<Map<String, Object>> raw = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                BigDecimal rev = rs.getBigDecimal("TotalRevenue");
                row.put("productName", rs.getString("ProductName"));
                row.put("totalQty", rs.getInt("TotalQty"));
                row.put("totalRevenue", rev);
                if (maxRev == null) {
                    maxRev = rev;
                }
                raw.add(row);
            }
            // Tính % so với sản phẩm đứng đầu (cho progress bar)
            for (Map<String, Object> row : raw) {
                BigDecimal rev = (BigDecimal) row.get("totalRevenue");
                int pct = (maxRev != null && maxRev.compareTo(BigDecimal.ZERO) > 0)
                        ? rev.multiply(BigDecimal.valueOf(100))
                                .divide(maxRev, 0, RoundingMode.HALF_UP).intValue()
                        : 0;
                row.put("barPct", pct);
                list.add(row);
            }
        } catch (Exception e) {
            System.err.println("[FinanceDAO] getTopProductsByRevenue: " + e.getMessage());
        }
        return list;
    }

    // 9. Phiếu nhập kho gần nhất
    public List<Map<String, Object>> getRecentImports(int limit) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            String sql = """
                         SELECT TOP (?) ii.ImportCode,
                                FORMAT(ii.ReceivedDate, 'dd/MM/yyyy') AS ReceivedDate,
                                s.SupplierName,
                                ii.TotalReceivedAmount
                         FROM ImportInvoices ii
                         JOIN Suppliers s ON s.SupplierId = ii.SupplierId
                         WHERE ii.Status = 'Received'
                           AND ii.IsDeleted = 0
                         ORDER BY ii.ReceivedDate DESC
                         """;
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, limit);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("importCode", rs.getString("ImportCode"));
                row.put("receivedDate", rs.getString("ReceivedDate"));
                row.put("supplierName", rs.getString("SupplierName"));
                row.put("amount", rs.getBigDecimal("TotalReceivedAmount"));
                list.add(row);
            }
        } catch (Exception e) {
            System.err.println("[FinanceDAO] getRecentImports: " + e.getMessage());
        }
        return list;
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
