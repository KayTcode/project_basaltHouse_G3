/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.Recipe;

/**
 *
 * @author admin
 */
public class RecipeDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    public HashMap<Integer, HashMap<Integer, List<Recipe>>> getRecipeMap() {
        HashMap<Integer, HashMap<Integer, List<Recipe>>> recipes = new HashMap<>();
        try {
            String sql = "SELECT ProductId, IngredientId, SizeId, QuantityNeeded FROM Recipes WHERE IsDeleted = 0";
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                int productId = rs.getInt("ProductId");
                int ingredientId = rs.getInt("IngredientId");
                int sizeId = rs.getInt("SizeId");
                BigDecimal qty = rs.getBigDecimal("QuantityNeeded");

                recipes.putIfAbsent(productId, new HashMap<>());
                recipes.get(productId).putIfAbsent(sizeId, new ArrayList<>());
                recipes.get(productId).get(sizeId)
                        .add(new Recipe(productId, ingredientId, sizeId, qty));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return recipes;
    }
}
