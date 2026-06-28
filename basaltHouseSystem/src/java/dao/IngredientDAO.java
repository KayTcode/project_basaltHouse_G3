/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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


    public boolean updateIngredientQuantity(int id, BigDecimal quantityNeed) {
        if (quantityNeed == null || quantityNeed.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        try {
            String sql = """
                         UPDATE Ingredients
                         SET StockQuantity = StockQuantity - ?
                         OUTPUT deleted.StockQuantity AS QuantityBefore,
                                inserted.StockQuantity AS QuantityAfter
                         WHERE IngredientId = ?
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, quantityNeed);
            st.setObject(2, id);
            rs = st.executeQuery();
            if (!rs.next()) {
                return false;
            }

            BigDecimal quantityBefore = rs.getBigDecimal("QuantityBefore");
            BigDecimal quantityAfter = rs.getBigDecimal("QuantityAfter");
            String logSql = """
                            INSERT INTO IngredientStockLogs
                                        (IngredientId, ChangeType, QuantityBefore,
                                         QuantityChanged, QuantityAfter, RefType,
                                         RefId, StaffId, IsDeleted)
                            VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0)
                            """;
            st = connection.prepareStatement(logSql);
            st.setObject(1, id);
            st.setObject(2, "Sold");
            st.setObject(3, quantityBefore);
            st.setObject(4, quantityNeed.negate());
            st.setObject(5, quantityAfter);
            st.setObject(6, "Sale");
            st.setNull(7, java.sql.Types.INTEGER);
            st.setNull(8, java.sql.Types.INTEGER);
            st.executeUpdate();
            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public List<HashMap<String, Object>> getStockSnapshotByDate(LocalDate auditDate) {
        List<HashMap<String, Object>> rows = new ArrayList<>();
        String sql = """
                     DECLARE @AuditDate DATE = ?;
                     DECLARE @Start DATETIME2 = CAST(@AuditDate AS DATETIME2);
                     DECLARE @End DATETIME2 = DATEADD(DAY, 1, @Start);

                     SELECT i.IngredientId,
                            COALESCE(firstLog.QuantityBefore, prevLog.QuantityAfter, i.StockQuantity) AS OpeningStock,
                            CASE
                                WHEN @AuditDate = CAST(GETDATE() AS DATE) THEN i.StockQuantity
                                ELSE COALESCE(endLog.QuantityAfter, i.StockQuantity)
                            END AS ClosingStock,
                            CASE WHEN endLog.LogId IS NULL THEN 0 ELSE 1 END AS HasStockLog
                     FROM Ingredients i
                     OUTER APPLY (
                         SELECT TOP 1 LogId, QuantityAfter
                         FROM IngredientStockLogs
                         WHERE IngredientId = i.IngredientId
                           AND IsDeleted = 0
                           AND CreatedAt < @Start
                         ORDER BY CreatedAt DESC, LogId DESC
                     ) prevLog
                     OUTER APPLY (
                         SELECT TOP 1 QuantityBefore
                         FROM IngredientStockLogs
                         WHERE IngredientId = i.IngredientId
                           AND IsDeleted = 0
                           AND CreatedAt >= @Start
                           AND CreatedAt < @End
                         ORDER BY CreatedAt ASC, LogId ASC
                     ) firstLog
                     OUTER APPLY (
                         SELECT TOP 1 LogId, QuantityAfter
                         FROM IngredientStockLogs
                         WHERE IngredientId = i.IngredientId
                           AND IsDeleted = 0
                           AND CreatedAt < @End
                         ORDER BY CreatedAt DESC, LogId DESC
                     ) endLog
                     WHERE i.IsDeleted = 0 AND i.IsActive = 1
                     """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(auditDate));
            try (ResultSet rs2 = ps.executeQuery()) {
                while (rs2.next()) {
                    HashMap<String, Object> row = new HashMap<>();
                    row.put("ingredientId", rs2.getInt("IngredientId"));
                    row.put("openingStock", rs2.getBigDecimal("OpeningStock"));
                    row.put("closingStock", rs2.getBigDecimal("ClosingStock"));
                    row.put("hasStockLog", rs2.getBoolean("HasStockLog"));
                    rows.add(row);
                }
            }
        } catch (Exception e) {
            System.err.println("getStockSnapshotByDate Error: " + e.getMessage());
        }
        return rows;
    }
      public boolean updateIngredientQuantity2(int id, BigDecimal quantityNeed) {
        try {
            String sql = """
                         UPDATE Ingredients
                         SET StockQuantity = StockQuantity + ?
                         WHERE IngredientId = ?
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, quantityNeed);
            st.setObject(2, id);
            st.executeUpdate();
            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
   
    }

    public List<Ingredient> getIngredientsBelowWarning() {
        List<Ingredient> list = new ArrayList<>();
        try {
            String sql = """
                     SELECT IngredientId, IngredientName, 
                            StockQuantity, MinStockQuantity
                     FROM Ingredients
                     WHERE StockQuantity <= MinStockQuantity * 1.2
                     AND IsDeleted = 0 AND IsActive = 1
                     """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                list.add(new Ingredient(
                        rs.getInt("IngredientId"),
                        rs.getString("IngredientName"),
                        rs.getBigDecimal("StockQuantity"),
                        rs.getBigDecimal("MinStockQuantity")
                ));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;
    }
}
