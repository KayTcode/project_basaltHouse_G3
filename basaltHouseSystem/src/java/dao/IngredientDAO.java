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
                     SELECT IngredientId, IngredientName, 
                            StockQuantity, MinStockQuantity
                     FROM Ingredients
                     WHERE IsDeleted = 0
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
    public List<String> getIngredientsBelowMin() {
    List<String> warnings = new ArrayList<>();
    try {
        String sql = """
                     SELECT IngredientName
                     FROM Ingredients
                     WHERE StockQuantity <= MinStockQuantity
                     AND IsDeleted = 0
                     """;
        st = connection.prepareStatement(sql);
        rs = st.executeQuery();
        while (rs.next()) {
            warnings.add(rs.getString("IngredientName"));
        }
    } catch (Exception e) {
        System.err.println(e.getMessage());
    }
    return warnings;
}
}
