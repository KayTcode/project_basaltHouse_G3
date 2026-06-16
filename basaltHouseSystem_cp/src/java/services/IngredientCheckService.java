/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.IngredientDAO;
import dao.OrderDAO;
import dao.RecipeDAO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Ingredient;
import model.OrderDetail;
import model.Recipe;

/**
 *
 * @author admin
 */
public class IngredientCheckService {

    public List<String> check(int orderId) {
        OrderDAO o = new OrderDAO();
        RecipeDAO r = new RecipeDAO();
        IngredientDAO i = new IngredientDAO();
        List<String> errors = new ArrayList<>();

        List<OrderDetail> details = o.getOrderDetailsByOrderId(orderId);
        if (details.isEmpty()) {
            errors.add("Đơn hàng id=" + orderId + " không có sản phẩm nào");
            return errors;
        }

        HashMap<Integer, HashMap<Integer, List<Recipe>>> recipeMap = r.getRecipeMap();
        HashMap<Integer, Ingredient> ingredientMap = i.getAllIngredients();

        HashMap<Integer, BigDecimal> totalNeeded = new HashMap<>();
        for (OrderDetail detail : details) {
            HashMap<Integer, List<Recipe>> bySize = recipeMap.get(detail.getProductId());
            if (bySize == null) {
                errors.add("Sản phẩm id=" + detail.getProductId() + " không có công thức");
                continue;
            }

            List<Recipe> ingredients = bySize.get(detail.getSizeId());
            if (ingredients == null || ingredients.isEmpty()) {
                errors.add("Sản phẩm id=" + detail.getProductId()
                        + " không có công thức cho size id=" + detail.getSizeId());
                continue;
            }

            for (Recipe rec : ingredients) {
                BigDecimal needed = rec.getQuantityNeeded()
                        .multiply(BigDecimal.valueOf(detail.getQuantity()))
                        .multiply(new BigDecimal("1.15"));
                totalNeeded.merge(rec.getIngredientId(), needed, BigDecimal::add);
            }
        }

        for (Map.Entry<Integer, BigDecimal> entry : totalNeeded.entrySet()) {
            int ingredientId = entry.getKey();
            BigDecimal needed = entry.getValue();
            Ingredient ig = ingredientMap.get(ingredientId);
            if (ig == null) {
                continue;
            }

            BigDecimal remaining = ig.getStockQuantity().subtract(needed);
            if (remaining.compareTo(ig.getMinStockQuantity()) < 0) {
                errors.add("Nguyên liệu '" + ig.getIngredientName()
                        + "' không đủ (cần " + needed.setScale(2, RoundingMode.HALF_UP)
                        + ", còn " + ig.getStockQuantity()
                        + ", ngưỡng tối thiểu " + ig.getMinStockQuantity() + ")");
            }
        }

        return errors;
    }
}
