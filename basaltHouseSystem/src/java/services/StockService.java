/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.ProductDAO;
import dao.SizeDAO;
import dao.ImportVoiceDAO;
import dto.IngredientAuditDTO;
import dto.IngredientStockDTO;
import dto.IngredientStockSnapshotDTO;
import dto.ProductSaleAuditDTO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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
import model.SalesAuditContext;
import model.StockNumbers;
import model.StockStatus;

public class StockService {

    private static final BigDecimal WARNING_RATE = new BigDecimal("1.2");
    private static final BigDecimal AVAILABILITY_RATE = new BigDecimal("0.85");
    private static final DateTimeFormatter DATE_TIME_INPUT_FORMAT
            = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT
            = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_INPUT_FORMAT
            = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final ProductDAO productDAO = new ProductDAO();
    private final SizeDAO sizeDAO = new SizeDAO();
    private final ImportVoiceDAO importVoiceDAO = new ImportVoiceDAO();
    private final IngredientCheckService ingredientCheckService = new IngredientCheckService();
    private final RecipeService recipeService = new RecipeService();

    public HashMap<Product, HashMap<String, Integer>> calculateProduct() {
        HashMap<Product, HashMap<String, Integer>> result = new HashMap<>();
        HashMap<Integer, Product> productMap = productDAO.getProduct();
        HashMap<Integer, String> sizeMap = sizeDAO.getSize();
        HashMap<Integer, Ingredient> ingredientMap = loadIngredientMap();
        HashMap<Integer, HashMap<Integer, List<Recipe>>> recipeMap = loadRecipeMap();

        for (Map.Entry<Integer, HashMap<Integer, List<Recipe>>> productEntry
                : recipeMap.entrySet()) {
            int productId = productEntry.getKey();
            Product product = productMap.get(productId);
            if (product == null) {
                continue;
            }
            HashMap<String, Integer> sizeResult = new HashMap<>();

            for (Map.Entry<Integer, List<Recipe>> sizeEntry
                    : productEntry.getValue().entrySet()) {
                int sizeId = sizeEntry.getKey();
                String sizeName = sizeMap.get(sizeId);
                if (sizeName == null) {
                    continue;
                }
                int minCoc = Integer.MAX_VALUE;

                Map<Integer, BigDecimal> neededByIngredient = new HashMap<>();
                for (Recipe recipe : sizeEntry.getValue()) {
                    BigDecimal needed = recipe.getQuantityNeeded();
                    if (needed == null || needed.compareTo(BigDecimal.ZERO) <= 0) {
                        minCoc = 0;
                        break;
                    }
                    neededByIngredient.merge(recipe.getIngredientId(), needed, BigDecimal::add);
                }

                if (minCoc != 0) {
                    for (Map.Entry<Integer, BigDecimal> requirement : neededByIngredient.entrySet()) {
                        Ingredient ingredient = ingredientMap.get(requirement.getKey());
                        if (ingredient == null) {
                            minCoc = 0;
                            break;
                        }
                        int cups = calculateAvailableCups(
                                ingredient.getStockQuantity(), requirement.getValue());
                        if (cups == 0) {
                            minCoc = 0;
                            break;
                        }
                        minCoc = Math.min(minCoc, cups);
                    }
                }
                int availableCups = minCoc;
                if (availableCups == Integer.MAX_VALUE) {
                    availableCups = 0;
                }
                sizeResult.put(sizeName, availableCups);
            }
            result.put(product, sizeResult);
        }
        return result;
    }

    static int calculateAvailableCups(BigDecimal stockQuantity, BigDecimal quantityNeeded) {
        if (stockQuantity == null || stockQuantity.compareTo(BigDecimal.ZERO) <= 0
                || quantityNeeded == null || quantityNeeded.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        return stockQuantity.multiply(AVAILABILITY_RATE)
                .divide(quantityNeeded, 0, RoundingMode.FLOOR)
                .intValue();
    }

    public HashMap<String, Object> getStaffDashboardData(String key, boolean includeImportOptions) {
        List<IngredientStockDTO> rows = importVoiceDAO.getIngredientStockRows(key);

        List<IngredientStockDTO> ingredients = new ArrayList<>();
        List<IngredientStockDTO> warnings = new ArrayList<>();

        int warningCount = 0;
        int outCount = 0;
        int okCount = 0;

        for (IngredientStockDTO row : rows) {
            BigDecimal stock = toBigDecimal(row.getStockQuantity());
            BigDecimal minStock = toBigDecimal(row.getMinStockQuantity());
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

            row.setStockText(formatDecimal(stock));
            row.setMinStockText(formatDecimal(minStock));
            row.setStatus(status);
            row.setStatusLabel(statusLabel);
            row.setStatusIcon(statusIcon);
            row.setBarPercent(calculateBarPercent(stock, minStock));

            ingredients.add(row);
            if (!"ok".equals(status)) {
                warnings.add(row);
            }
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put("ingredients", ingredients);
        data.put("warnings", warnings);
        data.put("warningCount", warningCount);
        data.put("outCount", outCount);
        data.put("okCount", okCount);
        if (includeImportOptions) {
            data.put("suppliers", importVoiceDAO.getSupplierOptions());
            data.put("currentDateInput", LocalDateTime.now().format(DATE_TIME_INPUT_FORMAT));
        }
        return data;
    }

    
    public HashMap<String, Object> getSalesAuditData(LocalDate selectedDate) {
        SalesAuditContext context = new SalesAuditContext(
                loadIngredientMap(),
                loadRecipeMap()
        );
        if (selectedDate == null) {
            context.auditDate = LocalDate.now();
        } else {
            context.auditDate = selectedDate;
        }

        List<ProductSaleAuditDTO> soldRows = loadSoldRows(context, selectedDate);
        if (selectedDate == null) {
            context.auditDate = resolveAuditDate(soldRows);
        }
        context.importedByIngredient = getImportedQuantityByIngredient(context.auditDate);
        context.stockSnapshotByIngredient = loadStockSnapshotByIngredient(context.auditDate);

        List<ProductSaleAuditDTO> productSales = buildProductSales(soldRows, context);
        List<IngredientAuditDTO> ingredientAudit = buildIngredientAudit(context);

        return buildSalesAuditData(productSales, ingredientAudit, context);
    }

   
    private HashMap<Integer, Ingredient> loadIngredientMap() {
        HashMap<String, Object> result = ingredientCheckService.getAllIngredients();
        Object success = result.get("success");
        if (success instanceof HashMap<?, ?>) {
            return (HashMap<Integer, Ingredient>) success;
        }
        return new HashMap<>();
    }

  
    private HashMap<Integer, HashMap<Integer, List<Recipe>>> loadRecipeMap() {
        HashMap<String, Object> result = recipeService.getRecipeMap();
        Object success = result.get("success");
        if (success instanceof HashMap<?, ?>) {
            return (HashMap<Integer, HashMap<Integer, List<Recipe>>>) success;
        }
        return new HashMap<>();
    }

    private Map<Integer, IngredientStockSnapshotDTO> loadStockSnapshotByIngredient(
            LocalDate auditDate) {
        Map<Integer, IngredientStockSnapshotDTO> map = new HashMap<>();
        HashMap<String, Object> result = ingredientCheckService.getStockSnapshotByDate(auditDate);
        Object success = result.get("success");
        if (!(success instanceof List<?>)) {
            return map;
        }

        for (Object item : (List<?>) success) {
            if (item instanceof IngredientStockSnapshotDTO) {
                IngredientStockSnapshotDTO snapshot
                        = (IngredientStockSnapshotDTO) item;
                map.put(snapshot.getIngredientId(), snapshot);
            }
        }
        return map;
    }

    private List<ProductSaleAuditDTO> loadSoldRows(
            SalesAuditContext context, LocalDate selectedDate) {
        OrderService orderService = new OrderService();
        HashMap<String, Object> soldResult
                = orderService.getSoldProductSizeRowsByDate(context.auditDate);
        if (soldResult.containsKey("error")) {
            context.dataError = stringValue(soldResult.get("error"));
            return new ArrayList<>();
        }

        Object success = soldResult.get("success");
        if (success instanceof List<?>) {
            List<ProductSaleAuditDTO> rows = new ArrayList<>();
            for (Object item : (List<?>) success) {
                if (item instanceof ProductSaleAuditDTO) {
                    rows.add((ProductSaleAuditDTO) item);
                }
            }
            return rows;
        }

        context.dataError = "Không đọc được dữ liệu bán hàng theo ngày đã chọn.";
        return new ArrayList<>();
    }

  
    private List<ProductSaleAuditDTO> buildProductSales(
            List<ProductSaleAuditDTO> soldRows,
            SalesAuditContext context) {
        List<ProductSaleAuditDTO> productSales = new ArrayList<>();
        for (ProductSaleAuditDTO row : soldRows) {
            int productId = row.getProductId();
            int sizeId = row.getSizeId();
            int soldQuantity = row.getSoldQuantity();
            String productName = row.getProductName();
            String sizeName = row.getSizeName();
            BigDecimal unitPrice = toBigDecimal(row.getUnitPrice());
            BigDecimal revenue = toBigDecimal(row.getRevenue());

            context.totalSoldCups += soldQuantity;
            context.totalRevenue = context.totalRevenue.add(revenue);

            List<String> recipeParts = new ArrayList<>();
            List<String> expectedParts = new ArrayList<>();
            List<Recipe> recipes = recipeService.getRecipes(context.recipeMap, productId, sizeId);
            boolean missingRecipe = recipes == null || recipes.isEmpty();

            if (missingRecipe) {
                context.missingRecipeCount++;
                recipeParts.add("Chưa có công thức cho size này");
                expectedParts.add("Không tính được");
            } else {
                for (Recipe recipe : recipes) {
                    addRecipeUsage(recipe, productName, sizeName, soldQuantity,
                            recipeParts, expectedParts, context);
                }
            }

            productSales.add(prepareProductSaleRow(
                    row, unitPrice, revenue, recipeParts, expectedParts, missingRecipe));
        }

        return productSales;
    }


    private List<IngredientAuditDTO> buildIngredientAudit(SalesAuditContext context) {
        List<IngredientAuditDTO> ingredientAudit = new ArrayList<>();
        for (Map.Entry<Integer, Ingredient> entry : context.ingredientMap.entrySet()) {
            int ingredientId = entry.getKey();
            Ingredient ingredient = entry.getValue();
            BigDecimal expectedUsed = context.expectedByIngredient
                    .getOrDefault(ingredientId, BigDecimal.ZERO);
            BigDecimal imported = context.importedByIngredient
                    .getOrDefault(ingredientId, BigDecimal.ZERO);

            if (expectedUsed.compareTo(BigDecimal.ZERO) <= 0
                    && imported.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            if (expectedUsed.compareTo(BigDecimal.ZERO) > 0) {
                context.usedIngredientCount++;
            }

            StockNumbers stockNumbers = getStockNumbers(ingredientId, ingredient, expectedUsed, imported, context);
            StockStatus status = getStockStatus(ingredient, stockNumbers);
            if (!"ok".equals(status.statusClass)) {
                context.stockWarningCount++;
            }

            ingredientAudit.add(createIngredientAuditRow(
                    ingredientId, ingredient, expectedUsed, imported, stockNumbers, status, context));
        }
        return ingredientAudit;
    }

   
    private HashMap<String, Object> buildSalesAuditData(
            List<ProductSaleAuditDTO> productSales,
            List<IngredientAuditDTO> ingredientAudit,
            SalesAuditContext context) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("totalSoldCups", context.totalSoldCups);
        data.put("totalRevenueText", formatMoney(context.totalRevenue));
        data.put("usedIngredientCount", context.usedIngredientCount);
        data.put("auditWarningCount", context.missingRecipeCount + context.stockWarningCount);
        data.put("productSales", productSales);
        data.put("ingredientAudit", ingredientAudit);
        data.put("auditDateOnlyText", formatDate(context.auditDate));
        data.put("auditDateInput", context.auditDate.format(DATE_INPUT_FORMAT));
        data.put("todayDateInput", LocalDate.now().format(DATE_INPUT_FORMAT));
        if (context.dataError != null && !context.dataError.trim().isEmpty()) {
            data.put("dataError", context.dataError);
        }

        return data;
    }

  
    private void addRecipeUsage(
            Recipe recipe,
            String productName,
            String sizeName,
            int soldQuantity,
            List<String> recipeParts,
            List<String> expectedParts,
            SalesAuditContext context) {
        Ingredient ingredient = context.ingredientMap.get(recipe.getIngredientId());
        String ingredientName = "Ingredient #" + recipe.getIngredientId();
        if (ingredient != null) {
            ingredientName = ingredient.getIngredientName();
        }
        String unit = "";
        if (ingredient != null) {
            unit = ingredient.getUnit();
        }

        BigDecimal quantityPerCup = recipe.getQuantityNeeded();
        if (quantityPerCup == null) {
            quantityPerCup = BigDecimal.ZERO;
        }
        BigDecimal expectedUsed = calculateExpectedUsage(quantityPerCup, soldQuantity);

        context.expectedByIngredient.merge(recipe.getIngredientId(), expectedUsed, BigDecimal::add);
        appendCupText(context.cupsByIngredient, recipe.getIngredientId(),
                productName + " " + sizeName + ": " + soldQuantity + " ly");
        appendUsageDetail(context.usageDetailsByIngredient, recipe.getIngredientId(),
                productName + " " + sizeName + ": "
                + soldQuantity + " ly x "
                + formatDecimal(quantityPerCup) + " " + unit
                + " = " + formatDecimal(expectedUsed) + " " + unit);

        recipeParts.add(ingredientName + " " + formatDecimal(quantityPerCup) + " " + unit + "/ly");
        expectedParts.add(formatDecimal(expectedUsed) + " " + unit + " " + ingredientName);
    }

    
    private ProductSaleAuditDTO prepareProductSaleRow(
            ProductSaleAuditDTO sale,
            BigDecimal unitPrice,
            BigDecimal revenue,
            List<String> recipeParts,
            List<String> expectedParts,
            boolean missingRecipe) {
        sale.setUnitPriceText(formatMoney(unitPrice));
        sale.setRevenueText(formatMoney(revenue));
        sale.setRecipeText(String.join("; ", recipeParts));
        sale.setExpectedUsageText(String.join("; ", expectedParts));
        String statusClass = "ok";
        String statusIcon = "check_circle";
        if (missingRecipe) {
            statusClass = "danger";
            statusIcon = "error";
        }
        sale.setStatusClass(statusClass);
        sale.setStatusIcon(statusIcon);
        return sale;
    }

  
    private IngredientAuditDTO createIngredientAuditRow(
            int ingredientId,
            Ingredient ingredient,
            BigDecimal expectedUsed,
            BigDecimal imported,
            StockNumbers stockNumbers,
            StockStatus status,
            SalesAuditContext context) {
        IngredientAuditDTO audit = new IngredientAuditDTO();
        audit.setIngredientName(ingredient.getIngredientName());
        audit.setExpectedUsedText(formatDecimal(expectedUsed) + " " + ingredient.getUnit());
        audit.setImportedTodayText(formatDecimal(imported) + " " + ingredient.getUnit());
        audit.setCurrentStockText(formatStockValue(stockNumbers.closingStock, ingredient.getUnit()));
        audit.setOpeningEstimateText(formatStockValue(stockNumbers.openingStock, ingredient.getUnit()));
        audit.setExpectedClosingText(formatStockValue(stockNumbers.expectedClosingStock, ingredient.getUnit()));
        audit.setCupsText(context.cupsByIngredient
                .getOrDefault(ingredientId, "Không có sản phẩm bán trong ngày này"));
        audit.setUsageDetails(context.usageDetailsByIngredient
                .getOrDefault(ingredientId, new ArrayList<>()));
        audit.setStatusClass(status.statusClass);
        audit.setStatusIcon(status.statusIcon);
        audit.setStatusLabel(status.statusLabel);
        return audit;
    }


    private StockNumbers getStockNumbers(
            int ingredientId,
            Ingredient ingredient,
            BigDecimal expectedUsed,
            BigDecimal imported,
            SalesAuditContext context) {
        IngredientStockSnapshotDTO snapshot
                = context.stockSnapshotByIngredient.get(ingredientId);
        boolean hasStockLog = snapshot != null && snapshot.isHasStockLog();

        if (!hasStockLog) {
            BigDecimal closingStock = null;
            if (LocalDate.now().equals(context.auditDate)) {
                closingStock = ingredient.getStockQuantity();
            }
            return reconcileStock(null, closingStock, imported, expectedUsed);
        }
        return reconcileStock(
                snapshot.getOpeningStock(),
                snapshot.getClosingStock(),
                imported,
                expectedUsed);
    }

    static BigDecimal calculateExpectedUsage(BigDecimal quantityPerCup, int soldQuantity) {
        if (quantityPerCup == null || quantityPerCup.compareTo(BigDecimal.ZERO) <= 0
                || soldQuantity <= 0) {
            return BigDecimal.ZERO;
        }
        return quantityPerCup.multiply(BigDecimal.valueOf(soldQuantity));
    }

    static StockNumbers reconcileStock(
            BigDecimal openingStock,
            BigDecimal closingStock,
            BigDecimal imported,
            BigDecimal expectedUsed) {
        if (openingStock == null || closingStock == null) {
            return new StockNumbers(null, closingStock, null, null, false);
        }
        BigDecimal safeImported = imported == null ? BigDecimal.ZERO : imported;
        BigDecimal safeExpectedUsed = expectedUsed == null ? BigDecimal.ZERO : expectedUsed;
        BigDecimal expectedClosingStock = openingStock.add(safeImported).subtract(safeExpectedUsed);
        return new StockNumbers(
                openingStock,
                closingStock,
                expectedClosingStock,
                expectedClosingStock.subtract(closingStock),
                true);
    }


    private StockStatus getStockStatus(Ingredient ingredient, StockNumbers stockNumbers) {
        if (!stockNumbers.hasStockLog) {
            return new StockStatus("warning", "help", "Không đủ dữ liệu log");
        }
        BigDecimal closingStock = stockNumbers.closingStock;
        BigDecimal difference = stockNumbers.difference;
        BigDecimal minStock = ingredient.getMinStockQuantity();
        if (minStock == null) {
            minStock = BigDecimal.ZERO;
        }

        if (difference.compareTo(BigDecimal.ZERO) > 0) {
            return new StockStatus("danger", "error",
                    "Thiếu " + formatDecimal(difference) + " " + ingredient.getUnit());
        }
        if (difference.compareTo(BigDecimal.ZERO) < 0) {
            return new StockStatus("danger", "error",
                    "Dư " + formatDecimal(difference.abs()) + " " + ingredient.getUnit());
        }
        if (closingStock.compareTo(BigDecimal.ZERO) <= 0) {
            return new StockStatus("danger", "error", "Hết kho");
        }
        if (minStock.compareTo(BigDecimal.ZERO) > 0
                && closingStock.compareTo(minStock.multiply(WARNING_RATE)) <= 0) {
            return new StockStatus("warning", "warning", "Sắp hết");
        }
        return new StockStatus("ok", "check_circle", "Khớp");
    }

    
    private LocalDate resolveAuditDate(List<ProductSaleAuditDTO> soldRows) {
        if (soldRows == null || soldRows.isEmpty()) {
            return LocalDate.now();
        }
        LocalDate auditDate = soldRows.get(0).getAuditDate();
        return auditDate == null ? LocalDate.now() : auditDate;
    }

   
    private String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DISPLAY_DATE_FORMAT);
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

   
    private String stringValue(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }


    private void appendCupText(Map<Integer, String> map, int ingredientId, String text) {
        String current = map.get(ingredientId);
        if (current == null || current.isEmpty()) {
            map.put(ingredientId, text);
            return;
        }
        map.put(ingredientId, current + "; " + text);
    }

    private void appendUsageDetail(Map<Integer, List<String>> map, int ingredientId, String text) {
        map.putIfAbsent(ingredientId, new ArrayList<>());
        map.get(ingredientId).add(text);
    }

    private Map<Integer, BigDecimal> getImportedQuantityByIngredient(LocalDate auditDate) {
        return importVoiceDAO.getReceivedQuantityByIngredient(auditDate);
    }

    
    private String formatDecimal(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        return value.stripTrailingZeros().toPlainString();
    }

    private String formatStockValue(BigDecimal value, String unit) {
        return value == null ? "—" : formatDecimal(value) + " " + unit;
    }

 
    private String formatMoney(BigDecimal value) {
        if (value == null) {
            return "0 đ";
        }
        return String.format("%,.0f đ", value).replace(",", ".");
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
