/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dto.IngredientStockSnapshotDTO;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Ingredient;

/**
 *
 * @author admin
 */
public class IngredientDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    public HashMap<Integer, Ingredient> getAllIngredients() {
        HashMap<Integer, Ingredient> map = new HashMap<>();
        try {
            String sql = """
                     SELECT IngredientId, IngredientName, Unit,
                            StockQuantity, MinStockQuantity, SupplierId
                     FROM Ingredients
                     WHERE IsDeleted = 0 AND IsActive = 1
                     """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                Ingredient ig = new Ingredient(
                        rs.getInt("IngredientId"),
                        rs.getString("IngredientName"),
                        rs.getBigDecimal("StockQuantity"),
                        rs.getBigDecimal("MinStockQuantity")
                );
                ig.setUnit(rs.getString("Unit"));
                Object supplierId = rs.getObject("SupplierId");
                ig.setSupplierId(supplierId == null ? null : rs.getInt("SupplierId"));
                map.put(ig.getIngredientId(), ig);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return map;
    }


    public List<IngredientStockSnapshotDTO> getStockSnapshotByDate(LocalDate auditDate) {
        List<IngredientStockSnapshotDTO> rows = new ArrayList<>();
        String sql = """
                     DECLARE @AuditDate DATE = ?;

                     SELECT i.IngredientId,
                            COALESCE(firstLog.QuantityBefore, prevLog.QuantityAfter, i.StockQuantity) AS OpeningStock,
                            CASE
                                WHEN @AuditDate = CAST(GETDATE() AS DATE) THEN i.StockQuantity
                                ELSE COALESCE(endLog.QuantityAfter, i.StockQuantity)
                            END AS ClosingStock,
                            CASE WHEN firstLog.LogId IS NULL THEN 0 ELSE 1 END AS HasStockLog
                     FROM Ingredients i
                     LEFT JOIN IngredientStockLogs prevLog
                       ON prevLog.LogId = (
                           SELECT TOP 1 log.LogId
                           FROM IngredientStockLogs log
                           WHERE log.IngredientId = i.IngredientId
                             AND log.IsDeleted = 0
                             AND log.CreatedAt < @AuditDate
                           ORDER BY log.CreatedAt DESC, log.LogId DESC
                       )
                     LEFT JOIN IngredientStockLogs firstLog
                       ON firstLog.LogId = (
                           SELECT TOP 1 log.LogId
                           FROM IngredientStockLogs log
                           WHERE log.IngredientId = i.IngredientId
                             AND log.IsDeleted = 0
                             AND log.CreatedAt >= @AuditDate
                             AND log.CreatedAt
                                 < DATEADD(DAY, 1, @AuditDate)
                           ORDER BY log.CreatedAt, log.LogId
                       )
                     LEFT JOIN IngredientStockLogs endLog
                       ON endLog.LogId = (
                           SELECT TOP 1 log.LogId
                           FROM IngredientStockLogs log
                           WHERE log.IngredientId = i.IngredientId
                             AND log.IsDeleted = 0
                             AND log.CreatedAt
                                 < DATEADD(DAY, 1, @AuditDate)
                           ORDER BY log.CreatedAt DESC, log.LogId DESC
                       )
                     WHERE i.IsDeleted = 0 AND i.IsActive = 1
                     """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(auditDate));
            try (ResultSet rs2 = ps.executeQuery()) {
                while (rs2.next()) {
                    rows.add(new IngredientStockSnapshotDTO(
                            rs2.getInt("IngredientId"),
                            rs2.getBigDecimal("OpeningStock"),
                            rs2.getBigDecimal("ClosingStock"),
                            rs2.getBoolean("HasStockLog")));
                }
            }
        } catch (Exception e) {
            System.err.println("getStockSnapshotByDate Error: " + e.getMessage());
        }
        return rows;
    }
    public List<Map<String, Object>> getIngredientUsageByDate(String selectedDate) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            if (selectedDate == null || selectedDate.trim().isEmpty()) {
                selectedDate = java.time.LocalDate.now().toString();
            }
            String sql = """
                         SELECT p.ProductName,
                                s.SizeName,
                                SUM(od.Quantity) AS TotalCups,
                                i.IngredientName,
                                i.Unit,
                                SUM(od.Quantity * r.QuantityNeeded)
                                    AS UsedQuantity
                         FROM Orders o
                         JOIN OrderDetails od ON o.OrderId = od.OrderId
                         JOIN Products p ON od.ProductId = p.ProductId
                         JOIN Sizes s ON od.SizeId = s.SizeId
                         JOIN Recipes r
                           ON od.ProductId = r.ProductId
                          AND od.SizeId = r.SizeId
                         JOIN Ingredients i ON r.IngredientId = i.IngredientId
                         WHERE CAST(o.CreatedAt AS DATE) = ?
                           AND o.IsDeleted = 0
                           AND o.OrderStatus NOT IN ('Cancelled', 'Pending')
                         GROUP BY p.ProductId, p.ProductName,
                                  s.SizeId, s.SizeName,
                                  i.IngredientId, i.IngredientName, i.Unit
                         ORDER BY p.ProductName, s.SizeName, UsedQuantity DESC
                         """;
            st = connection.prepareStatement(sql);
            st.setString(1, selectedDate);
            rs = st.executeQuery();
            
            Map<String, Map<String, Object>> productGroups = new java.util.LinkedHashMap<>();
            
            while (rs.next()) {
                addIngredientUsageRow(productGroups, rs);
            }
            list.addAll(productGroups.values());
        } catch (Exception e) {
            System.err.println("Error in getIngredientUsageByDate: " + e.getMessage());
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    private void addIngredientUsageRow(
            Map<String, Map<String, Object>> productGroups,
            ResultSet result) throws SQLException {
        String productName = result.getString("ProductName");
        String sizeName = result.getString("SizeName");
        String key = productName + "-" + sizeName;

        Map<String, Object> productGroup = productGroups.get(key);
        if (productGroup == null) {
            productGroup = new HashMap<>();
            productGroup.put("productName", productName);
            productGroup.put("sizeName", sizeName);
            productGroup.put("totalCups", result.getInt("TotalCups"));
            productGroup.put("ingredients",
                    new ArrayList<Map<String, Object>>());
            productGroups.put(key, productGroup);
        }

        Map<String, Object> ingredient = new HashMap<>();
        ingredient.put("ingredientName",
                result.getString("IngredientName"));
        ingredient.put("unit", result.getString("Unit"));
        ingredient.put("usedQuantity",
                result.getBigDecimal("UsedQuantity"));
        ((List<Map<String, Object>>) productGroup.get("ingredients"))
                .add(ingredient);
    }

    public List<Map<String, Object>> getTodayIngredientUsage() {
        return getIngredientUsageByDate(java.time.LocalDate.now().toString());
    }
}
