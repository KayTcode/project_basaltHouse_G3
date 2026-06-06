/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.IngredientDAO;
import dao.OrderDAO;
import dao.ProductDAO;
import dao.RecipeDAO;
import dao.SizeDAO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Ingredient;
import model.Product;
import model.Recipe;

public class StockService {

    public HashMap<Product, HashMap<String, Integer>> calculateProduct() {
        ProductDAO p = new ProductDAO();
        IngredientDAO i = new IngredientDAO();
        RecipeDAO r = new RecipeDAO();
        SizeDAO s = new SizeDAO();

        HashMap<Product, HashMap<String, Integer>> result = new HashMap<>();
        HashMap<Integer, Product> productMap = p.getProduct();
        HashMap<Integer, String> sizeMap = s.getSize();
        HashMap<Integer, Ingredient> ingredientMap = i.getAllIngredients();
        HashMap<Integer, HashMap<Integer, List<Recipe>>> recipeMap = r.getRecipeMap();

        for (Map.Entry<Integer, HashMap<Integer, List<Recipe>>> productEntry
                : recipeMap.entrySet()) {
            int productId = productEntry.getKey();
            Product product = productMap.get(productId);
            HashMap<String, Integer> sizeResult = new HashMap<>();

            for (Map.Entry<Integer, List<Recipe>> sizeEntry
                    : productEntry.getValue().entrySet()) {
                int sizeId = sizeEntry.getKey();
                String sizeName = sizeMap.get(sizeId);
                int minCoc = Integer.MAX_VALUE;

                for (Recipe recipe : sizeEntry.getValue()) {
                    Ingredient ig = ingredientMap.get(recipe.getIngredientId());
                    if (ig == null) {
                        continue;
                    }

                    BigDecimal needed = recipe.getQuantityNeeded();
                    if (needed == null || needed.compareTo(BigDecimal.ZERO) <= 0) {
                        minCoc = 0;
                        break;
                    }
                    int coc = ig.getStockQuantity()
                            .divide(needed, 0, RoundingMode.FLOOR)
                            .intValue();
                    coc = (int) (coc * (1 - 0.15));
                    minCoc = Math.min(minCoc, coc);
                }
                sizeResult.put(sizeName, minCoc == Integer.MAX_VALUE ? 0 : minCoc);
            }
            result.put(product, sizeResult);
        }
        return result;
    }

    public List<String> updateStockQuantity(int productId, int sizeId, int quantity) {
        RecipeDAO r = new RecipeDAO();
        IngredientDAO i = new IngredientDAO();

        HashMap<Integer, HashMap<Integer, List<Recipe>>> recipeMap = r.getRecipeMap();
        if (!recipeMap.containsKey(productId)) {
            return new ArrayList<>();
        }

        HashMap<Integer, List<Recipe>> bySize = recipeMap.get(productId);
        if (!bySize.containsKey(sizeId)) {
            return new ArrayList<>();
        }

        for (Recipe recipe : bySize.get(sizeId)) {
            BigDecimal quantityNeed = recipe.getQuantityNeeded()
                    .multiply(BigDecimal.valueOf(quantity));
            i.updateIngredientQuantity(recipe.getIngredientId(), quantityNeed);
        }

        return i.getIngredientsBelowMin();
    }

    public List<String> processOrder(int orderId) {
        OrderValidationService os = new OrderValidationService();
        OrderDAO o = new OrderDAO();
        List<String> errors = os.validate(orderId);
        OrderValidationService validationService = new OrderValidationService();
        errors = validationService.validate(orderId);
        if (!errors.isEmpty()) {
            return errors;
        }

        IngredientCheckService checkService = new IngredientCheckService();
        errors = checkService.check(orderId);
        if (!errors.isEmpty()) {
            return errors;
        }

        List<model.OrderDetail> details = o.getOrderDetailsByOrderId(orderId);
        List<String> warnings = new ArrayList<>();
        for (model.OrderDetail detail : details) {
            warnings.addAll(updateStockQuantity(
                    detail.getProductId(),
                    detail.getSizeId(),
                    detail.getQuantity()
            ));
        }
        return warnings;
    }
}
