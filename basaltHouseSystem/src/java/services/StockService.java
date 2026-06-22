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
import dao.ImportVoiceDAO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Ingredient;
import model.OrderDetail;
import model.Product;
import model.Recipe;

public class StockService {

    private static final BigDecimal WARNING_RATE = new BigDecimal("1.2");
    private static final DateTimeFormatter DATE_TIME_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

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

    public HashMap<String, Object> getStaffDashboardData() {
        ImportVoiceDAO staffDAO = new ImportVoiceDAO();
        List<HashMap<String, Object>> rows = staffDAO.getIngredientStockRows();
        List<HashMap<String, Object>> ingredients = new ArrayList<>();
        List<HashMap<String, Object>> warnings = new ArrayList<>();

        int warningCount = 0;
        int outCount = 0;
        int okCount = 0;

        for (HashMap<String, Object> row : rows) {
            BigDecimal stock = toBigDecimal(row.get("stockQuantity"));
            BigDecimal minStock = toBigDecimal(row.get("minStockQuantity"));
            String status;
            String statusLabel;
            String statusIcon;

            if (stock.compareTo(BigDecimal.ZERO) <= 0) {
                status = "danger";
                statusLabel = "Hết hàng";
                statusIcon = "error";
                outCount++;
            } else if (stock.compareTo(minStock.multiply(WARNING_RATE)) <= 0) {
                status = "warning";
                statusLabel = "Sắp hết";
                statusIcon = "warning";
                warningCount++;
            } else {
                status = "ok";
                statusLabel = "Đủ hàng";
                statusIcon = "task_alt";
                okCount++;
            }

            HashMap<String, Object> item = new HashMap<>();
            item.put("id", row.get("ingredientId"));
            item.put("name", row.get("ingredientName"));
            item.put("unit", row.get("unit"));
            item.put("supplierId", row.get("supplierId"));
            item.put("supplierName", row.get("supplierName"));
            item.put("stockText", formatDecimal(stock));
            item.put("minStockText", formatDecimal(minStock));
            item.put("status", status);
            item.put("statusLabel", statusLabel);
            item.put("statusIcon", statusIcon);
            item.put("barPercent", calculateBarPercent(stock, minStock));

            ingredients.add(item);
            if (!"ok".equals(status)) {
                warnings.add(item);
            }
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put("ingredients", ingredients);
        data.put("warnings", warnings);
        data.put("warningCount", warningCount);
        data.put("outCount", outCount);
        data.put("okCount", okCount);
        data.put("suppliers", staffDAO.getSupplierOptions());
        data.put("currentDateInput", LocalDateTime.now().format(DATE_TIME_INPUT_FORMAT));
        return data;
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

   

    private BigDecimal toBigDecimal(Object value) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        return BigDecimal.ZERO;
    }

    private String formatDecimal(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        return value.stripTrailingZeros().toPlainString();
    }

    private int calculateBarPercent(BigDecimal stock, BigDecimal minStock) {
        if (stock == null || stock.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        if (minStock == null || minStock.compareTo(BigDecimal.ZERO) <= 0) {
            return 100;
        }
        BigDecimal percent = stock
                .multiply(BigDecimal.valueOf(100))
                .divide(minStock.multiply(WARNING_RATE), 0, RoundingMode.HALF_UP);
        return Math.max(4, Math.min(100, percent.intValue()));
    }
}
