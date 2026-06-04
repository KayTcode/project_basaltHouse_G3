/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dal.IngredientDAO;
import dal.ProductDAO;
import dal.RecipeDAO;
import dal.SizeDAO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Product;
import model.Recipe;

public class StockService {

    public HashMap<Product, HashMap<String, Integer>> calculateProduct() {
        ProductDAO p = new ProductDAO();
        IngredientDAO i = new IngredientDAO();
        RecipeDAO r = new RecipeDAO();
        SizeDAO s = new SizeDAO();

        HashMap<Product, HashMap<String, Integer>> result = new HashMap<>();
        HashMap<Integer, Product> productMap = p.getProductWithImage();
        HashMap<Integer, String> sizeMap = s.getSize();
        HashMap<Integer, BigDecimal> stockMap = i.getIngredient();
        HashMap<Integer, HashMap<Integer, List<Recipe>>> recipeMap = r.getRecipeMap();

        for (Map.Entry<Integer, HashMap<Integer, List<Recipe>>> entry : recipeMap.entrySet()) {
            int productId = entry.getKey();
            Product product = productMap.get(productId);
            HashMap<Integer, List<Recipe>> bySize = entry.getValue();
            HashMap<String, Integer> sizeResult = new HashMap<>();

            for (Map.Entry<Integer, List<Recipe>> sizeEntry : bySize.entrySet()) {
                int sizeId = sizeEntry.getKey();
                String sizeName = sizeMap.get(sizeId);
                List<Recipe> ingredients = sizeEntry.getValue();
                int minCoc = Integer.MAX_VALUE;

                for (Recipe recipe : ingredients) {
                    BigDecimal stock = stockMap.getOrDefault(recipe.getIngredientId(), BigDecimal.ZERO);
                    BigDecimal needed = recipe.getQuantityNeeded();
                    if (needed == null || needed.compareTo(BigDecimal.ZERO) <= 0) {
                        minCoc = 0;
                        break;
                    }
                    int coc = stock.divide(needed, 0, RoundingMode.FLOOR).intValue();
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
        List<Recipe> recipes = bySize.get(sizeId);

        for (Recipe recipe : recipes) {
            int ingredientId = recipe.getIngredientId();
            BigDecimal quantityNeed = recipe.getQuantityNeeded()
                    .multiply(BigDecimal.valueOf(quantity));
            i.updateIngredientQuantity(ingredientId, quantityNeed);
        }

        List<String> warnings = i.getIngredientsBelowMin();
        return warnings;
    }

}
