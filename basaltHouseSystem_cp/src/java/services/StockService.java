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
import model.OrderDetail;
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

    public List<Ingredient> getWarnings() {
        IngredientDAO i = new IngredientDAO();
        return i.getIngredientsBelowWarning();
    }

    public void updateStockForOrder(List<OrderDetail> details) {
        RecipeDAO r = new RecipeDAO();
        IngredientDAO i = new IngredientDAO();

        HashMap<Integer, HashMap<Integer, List<Recipe>>> recipeMap = r.getRecipeMap();

        for (OrderDetail detail : details) {
            HashMap<Integer, List<Recipe>> bySize = recipeMap.get(detail.getProductId());
            if (bySize == null) {
                continue;
            }

            List<Recipe> recipes = bySize.get(detail.getSizeId());
            if (recipes == null) {
                continue;
            }

            for (Recipe recipe : recipes) {
                BigDecimal quantityNeed = recipe.getQuantityNeeded()
                        .multiply(BigDecimal.valueOf(detail.getQuantity()));
                i.updateIngredientQuantity(recipe.getIngredientId(), quantityNeed);
            }
        }
    }

   

}
