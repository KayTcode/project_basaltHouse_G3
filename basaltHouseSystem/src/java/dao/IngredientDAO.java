/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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


    public void updateIngredientQuantity(int id, BigDecimal quantityNeed) {
        try {
            String sql = """
                         UPDATE Ingredients
                         SET StockQuantity = StockQuantity - ?
                         WHERE IngredientId = ?
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, quantityNeed);
            st.setObject(2, id);
            st.executeUpdate();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
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
