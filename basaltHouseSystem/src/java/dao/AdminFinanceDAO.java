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
        return periodCondition(period, "");
    }

    private String periodCondition(String period, String alias) {
        String prefix = (alias == null || alias.isEmpty()) ? "" : alias + ".";
        return switch (period) {
            case "week" ->
                prefix + "CreatedAt >= DATEADD(DAY, -6, CAST(GETDATE() AS DATE))";
            case "year" ->
                "YEAR(" + prefix + "CreatedAt) = YEAR(GETDATE())";
            default ->
                "YEAR(" + prefix + "CreatedAt) = YEAR(GETDATE()) AND MONTH(" + prefix + "CreatedAt) = MONTH(GETDATE())";
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

    // 5. Doanh thu & chi phí theo tuần/tháng/ngày tùy theo period (cho biểu đồ cột/đường)
    public List<Map<String, Object>> getChartData(String period) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            String sql = switch (period) {
                case "week" ->
                    """
                               WITH Rev AS (
                                   SELECT FORMAT(CreatedAt, 'dd/MM') AS Label,
                                          CAST(CreatedAt AS DATE) AS SortDate,
                                          ISNULL(SUM(FinalAmount), 0) AS Revenue
                                   FROM Orders
                                   WHERE CreatedAt >= DATEADD(DAY, -6, CAST(GETDATE() AS DATE))
                                     AND PaymentStatus = 'Paid'
                                     AND IsDeleted = 0
                                   GROUP BY FORMAT(CreatedAt, 'dd/MM'), CAST(CreatedAt AS DATE)
                               ),
                               Cost AS (
                                   SELECT FORMAT(ReceivedDate, 'dd/MM') AS Label,
                                          CAST(ReceivedDate AS DATE) AS SortDate,
                                          ISNULL(SUM(TotalReceivedAmount), 0) AS Cost
                                   FROM ImportInvoices
                                   WHERE ReceivedDate >= DATEADD(DAY, -6, CAST(GETDATE() AS DATE))
                                     AND Status = 'Received'
                                     AND IsDeleted = 0
                                   GROUP BY FORMAT(ReceivedDate, 'dd/MM'), CAST(ReceivedDate AS DATE)
                               )
                               SELECT COALESCE(r.Label, c.Label) AS Label,
                                      COALESCE(r.SortDate, c.SortDate) AS SortOrder,
                                      ISNULL(r.Revenue, 0) AS Revenue,
                                      ISNULL(c.Cost, 0) AS Cost
                               FROM Rev r
                               FULL OUTER JOIN Cost c ON r.Label = c.Label
                               ORDER BY SortOrder ASC
                               """;
                case "year" ->
                    """
                               WITH Rev AS (
                                   SELECT N'Tháng ' + CAST(MONTH(CreatedAt) AS NVARCHAR) AS Label,
                                          MONTH(CreatedAt) AS SortMonth,
                                          ISNULL(SUM(FinalAmount), 0) AS Revenue
                                   FROM Orders
                                   WHERE YEAR(CreatedAt) = YEAR(GETDATE())
                                     AND PaymentStatus = 'Paid'
                                     AND IsDeleted = 0
                                   GROUP BY MONTH(CreatedAt)
                               ),
                               Cost AS (
                                   SELECT N'Tháng ' + CAST(MONTH(ReceivedDate) AS NVARCHAR) AS Label,
                                          MONTH(ReceivedDate) AS SortMonth,
                                          ISNULL(SUM(TotalReceivedAmount), 0) AS Cost
                                   FROM ImportInvoices
                                   WHERE YEAR(ReceivedDate) = YEAR(GETDATE())
                                     AND Status = 'Received'
                                     AND IsDeleted = 0
                                   GROUP BY MONTH(ReceivedDate)
                               )
                               SELECT COALESCE(r.Label, c.Label) AS Label,
                                      COALESCE(r.SortMonth, c.SortMonth) AS SortOrder,
                                      ISNULL(r.Revenue, 0) AS Revenue,
                                      ISNULL(c.Cost, 0) AS Cost
                               FROM Rev r
                               FULL OUTER JOIN Cost c ON r.Label = c.Label
                               ORDER BY SortOrder ASC
                               """;
                default ->
                    """
                           WITH Rev AS (
                               SELECT N'Tuần ' + CAST(
                                          DATEPART(WEEK, CreatedAt)
                                        - DATEPART(WEEK, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))
                                        + 1 AS NVARCHAR) AS Label,
                                      DATEPART(WEEK, CreatedAt) AS SortWeek,
                                      ISNULL(SUM(FinalAmount), 0) AS Revenue
                               FROM Orders
                               WHERE YEAR(CreatedAt)  = YEAR(GETDATE())
                                 AND MONTH(CreatedAt) = MONTH(GETDATE())
                                 AND PaymentStatus = 'Paid'
                                 AND IsDeleted = 0
                               GROUP BY DATEPART(WEEK, CreatedAt)
                           ),
                           Cost AS (
                               SELECT N'Tuần ' + CAST(
                                          DATEPART(WEEK, ReceivedDate)
                                        - DATEPART(WEEK, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))
                                        + 1 AS NVARCHAR) AS Label,
                                      DATEPART(WEEK, ReceivedDate) AS SortWeek,
                                      ISNULL(SUM(TotalReceivedAmount), 0) AS Cost
                               FROM ImportInvoices
                               WHERE YEAR(ReceivedDate)  = YEAR(GETDATE())
                                 AND MONTH(ReceivedDate) = MONTH(GETDATE())
                                 AND Status = 'Received'
                                 AND IsDeleted = 0
                               GROUP BY DATEPART(WEEK, ReceivedDate)
                           )
                           SELECT COALESCE(r.Label, c.Label) AS Label,
                                  COALESCE(r.SortWeek, c.SortWeek) AS SortOrder,
                                  ISNULL(r.Revenue, 0) AS Revenue,
                                  ISNULL(c.Cost, 0) AS Cost
                           FROM Rev r
                           FULL OUTER JOIN Cost c ON r.Label = c.Label
                           ORDER BY SortOrder ASC
                           """;
            };
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                String lbl = rs.getString("Label");
                BigDecimal rev = rs.getBigDecimal("Revenue");
                BigDecimal cost = rs.getBigDecimal("Cost");
                Map<String, Object> row = new HashMap<>();
                row.put("label", lbl);
                row.put("revenue", rev);
                row.put("cost", cost);
                row.put("profit", rev.subtract(cost));
                list.add(row);
            }
        } catch (Exception e) {
            System.err.println("[FinanceDAO] getChartData: " + e.getMessage());
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
                          WHERE %s
                            AND o.PaymentStatus = 'Paid'
                            AND o.IsDeleted  = 0
                            AND od.IsDeleted = 0
                            AND p.IsDeleted  = 0
                          GROUP BY p.ProductId, p.ProductName
                          ORDER BY TotalRevenue DESC
                          """.formatted(periodCondition(period, "o"));
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
