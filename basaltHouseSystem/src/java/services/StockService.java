/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.ProductDAO;
import dao.SizeDAO;
import dao.ImportVoiceDAO;
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

    public HashMap<String, Object> getStaffDashboardData(String key) {
        List<HashMap<String, Object>> rows = key == null || key.trim().isEmpty()
                ? importVoiceDAO.getIngredientStockRows()
                : importVoiceDAO.getIngredientStockRows(key);

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
       
        return data;
    }

    // Dựng dữ liệu kiểm kê bán hàng theo ngày được chọn.
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

        List<HashMap<String, Object>> soldRows = loadSoldRows(context, selectedDate);
        if (selectedDate == null) {
            context.auditDate = resolveAuditDate(soldRows);
        }
        context.importedByIngredient = getImportedQuantityByIngredient(context.auditDate);
        context.stockSnapshotByIngredient = loadStockSnapshotByIngredient(context.auditDate);

        List<HashMap<String, Object>> productSales = buildProductSales(soldRows, context);
        List<HashMap<String, Object>> ingredientAudit = buildIngredientAudit(context);

        return buildSalesAuditData(productSales, ingredientAudit, context);
    }

    // Lấy danh sách nguyên liệu qua service để hạn chế gọi IngredientDAO trực tiếp.
    private HashMap<Integer, Ingredient> loadIngredientMap() {
        HashMap<String, Object> result = ingredientCheckService.getAllIngredients();
        Object success = result.get("success");
        if (success instanceof HashMap<?, ?>) {
            return (HashMap<Integer, Ingredient>) success;
        }
        return new HashMap<>();
    }

    // Lấy map công thức qua RecipeService để StockService không gọi RecipeDAO trực tiếp.
    private HashMap<Integer, HashMap<Integer, List<Recipe>>> loadRecipeMap() {
        HashMap<String, Object> result = recipeService.getRecipeMap();
        Object success = result.get("success");
        if (success instanceof HashMap<?, ?>) {
            return (HashMap<Integer, HashMap<Integer, List<Recipe>>>) success;
        }
        return new HashMap<>();
    }

    // Lấy tồn đầu/tồn cuối theo ngày từ log kho.
    private Map<Integer, HashMap<String, Object>> loadStockSnapshotByIngredient(LocalDate auditDate) {
        Map<Integer, HashMap<String, Object>> map = new HashMap<>();
        HashMap<String, Object> result = ingredientCheckService.getStockSnapshotByDate(auditDate);
        Object success = result.get("success");
        if (!(success instanceof List<?>)) {
            return map;
        }

        for (Object item : (List<?>) success) {
            if (!(item instanceof HashMap<?, ?>)) {
                continue;
            }
            HashMap<String, Object> row = (HashMap<String, Object>) item;
            map.put(toInt(row.get("ingredientId")), row);
        }
        return map;
    }

    // Lấy dữ liệu sản phẩm đã bán trong ngày kiểm kê từ OrderService.
    private List<HashMap<String, Object>> loadSoldRows(SalesAuditContext context, LocalDate selectedDate) {
        OrderService orderService = new OrderService();
        HashMap<String, Object> soldResult;
        if (selectedDate == null) {
            soldResult = orderService.getTodaySoldProductSizeRows();
        } else {
            soldResult = orderService.getSoldProductSizeRowsByDate(selectedDate);
        }
        if (soldResult.containsKey("error")) {
            context.dataError = stringValue(soldResult.get("error"));
            return new ArrayList<>();
        }

        Object success = soldResult.get("success");
        if (success instanceof List<?>) {
            return (List<HashMap<String, Object>>) success;
        }

        context.dataError = "Không đọc được dữ liệu bán hàng hôm nay.";
        return new ArrayList<>();
    }

    // Gom doanh thu và định mức nguyên liệu theo từng sản phẩm + size đã bán.
    private List<HashMap<String, Object>> buildProductSales(
            List<HashMap<String, Object>> soldRows,
            SalesAuditContext context) {
        List<HashMap<String, Object>> productSales = new ArrayList<>();
        for (HashMap<String, Object> row : soldRows) {
            int productId = toInt(row.get("productId"));
            int sizeId = toInt(row.get("sizeId"));
            int soldQuantity = toInt(row.get("soldQuantity"));
            String productName = stringValue(row.get("productName"));
            String sizeName = stringValue(row.get("sizeName"));
            BigDecimal unitPrice = toBigDecimal(row.get("unitPrice"));
            BigDecimal revenue = toBigDecimal(row.get("revenue"));

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

            productSales.add(createProductSaleRow(productName, sizeName, soldQuantity,
                    unitPrice, revenue, recipeParts, expectedParts, missingRecipe));
        }

        return productSales;
    }

    // Gom nguyên liệu đã dùng, nhập trong ngày và tồn đầu/tồn cuối theo ngày để kiểm kê.
    private List<HashMap<String, Object>> buildIngredientAudit(SalesAuditContext context) {
        List<HashMap<String, Object>> ingredientAudit = new ArrayList<>();
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

    // Đóng gói toàn bộ dữ liệu cuối cùng để JSP có thể đọc bằng salesAudit.
    private HashMap<String, Object> buildSalesAuditData(
            List<HashMap<String, Object>> productSales,
            List<HashMap<String, Object>> ingredientAudit,
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

    // Cộng lượng nguyên liệu dự kiến theo công thức của một sản phẩm/size.
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

    // Tạo một dòng hiển thị cho bảng sản phẩm bán ra.
    private HashMap<String, Object> createProductSaleRow(
            String productName,
            String sizeName,
            int soldQuantity,
            BigDecimal unitPrice,
            BigDecimal revenue,
            List<String> recipeParts,
            List<String> expectedParts,
            boolean missingRecipe) {
        HashMap<String, Object> sale = new HashMap<>();
        sale.put("productName", productName);
        sale.put("sizeName", sizeName);
        sale.put("soldQuantity", soldQuantity);
        sale.put("unitPriceText", formatMoney(unitPrice));
        sale.put("revenueText", formatMoney(revenue));
        sale.put("recipeText", String.join("; ", recipeParts));
        sale.put("expectedUsageText", String.join("; ", expectedParts));
        String statusClass = "ok";
        String statusIcon = "check_circle";
        if (missingRecipe) {
            statusClass = "danger";
            statusIcon = "error";
        }
        sale.put("statusClass", statusClass);
        sale.put("statusIcon", statusIcon);
        return sale;
    }

    // Tạo một dòng hiển thị cho bảng đối chiếu nguyên liệu.
    private HashMap<String, Object> createIngredientAuditRow(
            int ingredientId,
            Ingredient ingredient,
            BigDecimal expectedUsed,
            BigDecimal imported,
            StockNumbers stockNumbers,
            StockStatus status,
            SalesAuditContext context) {
        HashMap<String, Object> audit = new HashMap<>();
        audit.put("ingredientName", ingredient.getIngredientName());
        audit.put("expectedUsedText", formatDecimal(expectedUsed) + " " + ingredient.getUnit());
        audit.put("importedTodayText", formatDecimal(imported) + " " + ingredient.getUnit());
        audit.put("currentStockText", formatStockValue(stockNumbers.closingStock, ingredient.getUnit()));
        audit.put("openingEstimateText", formatStockValue(stockNumbers.openingStock, ingredient.getUnit()));
        audit.put("expectedClosingText", formatStockValue(stockNumbers.expectedClosingStock, ingredient.getUnit()));
        audit.put("cupsText", context.cupsByIngredient
                .getOrDefault(ingredientId, "Không có sản phẩm bán trong ngày này"));
        audit.put("usageDetails", context.usageDetailsByIngredient
                .getOrDefault(ingredientId, new ArrayList<>()));
        audit.put("statusClass", status.statusClass);
        audit.put("statusIcon", status.statusIcon);
        audit.put("statusLabel", status.statusLabel);
        return audit;
    }

    // Lấy tồn đầu/tồn cuối từ log; thiếu log thì đánh dấu không đủ dữ liệu đối chiếu.
    private StockNumbers getStockNumbers(
            int ingredientId,
            Ingredient ingredient,
            BigDecimal expectedUsed,
            BigDecimal imported,
            SalesAuditContext context) {
        HashMap<String, Object> snapshot = context.stockSnapshotByIngredient.get(ingredientId);
        boolean hasStockLog = snapshot != null && Boolean.TRUE.equals(snapshot.get("hasStockLog"));

        if (!hasStockLog) {
            return reconcileStock(null, ingredient.getStockQuantity(), imported, expectedUsed);
        }
        return reconcileStock(
                toBigDecimal(snapshot.get("openingStock")),
                toBigDecimal(snapshot.get("closingStock")),
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

    // Xác định trạng thái kiểm kê của nguyên liệu để hiển thị cảnh báo.
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

    // Lấy ngày kiểm kê từ dữ liệu bán hàng; nếu rỗng thì dùng ngày hiện tại.
    private LocalDate resolveAuditDate(List<HashMap<String, Object>> soldRows) {
        if (soldRows == null || soldRows.isEmpty()) {
            return LocalDate.now();
        }
        return toLocalDate(soldRows.get(0).get("auditDate"), LocalDate.now());
    }

    // Ép các kiểu ngày khác nhau về LocalDate để xử lý thống nhất.
    private LocalDate toLocalDate(Object value, LocalDate defaultDate) {
        if (value instanceof LocalDate) {
            return (LocalDate) value;
        }
        if (value instanceof java.sql.Date) {
            return ((java.sql.Date) value).toLocalDate();
        }
        if (value instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) value).toLocalDateTime().toLocalDate();
        }
        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).toLocalDate();
        }
        if (value == null) {
            return defaultDate;
        }

        String text = value.toString();
        try {
            String dateText = text;
            if (dateText.length() > 10) {
                dateText = dateText.substring(0, 10);
            }
            return LocalDate.parse(dateText);
        } catch (Exception e) {
            return defaultDate;
        }
    }

    // Format ngày theo kiểu dd/MM/yyyy cho giao diện.
    private String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DISPLAY_DATE_FORMAT);
    }

    // Giữ các biến trung gian trong quá trình dựng dữ liệu kiểm kê.
    // Gói các số tồn kho theo ngày để đối chiếu.
    // Gói trạng thái tồn kho để không phải truyền nhiều chuỗi riêng lẻ.
    // Ép dữ liệu dạng Object về BigDecimal an toàn.
    private BigDecimal toBigDecimal(Object value) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        return BigDecimal.ZERO;
    }

    // Ép dữ liệu dạng Object về int an toàn.
    private int toInt(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value == null) {
            return 0;
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // Trả về chuỗi rỗng nếu dữ liệu null.
    private String stringValue(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    // Nối danh sách sản phẩm/size đã dùng cùng một nguyên liệu.
    private void appendCupText(Map<Integer, String> map, int ingredientId, String text) {
        String current = map.get(ingredientId);
        if (current == null || current.isEmpty()) {
            map.put(ingredientId, text);
            return;
        }
        map.put(ingredientId, current + "; " + text);
    }

    // Thêm một dòng chi tiết công thức đã dùng cho từng nguyên liệu.
    private void appendUsageDetail(Map<Integer, List<String>> map, int ingredientId, String text) {
        map.putIfAbsent(ingredientId, new ArrayList<>());
        map.get(ingredientId).add(text);
    }

    // Tính tổng lượng nguyên liệu đã nhập theo ngày kiểm kê.
    private Map<Integer, BigDecimal> getImportedQuantityByIngredient(LocalDate auditDate) {
        LocalDate targetDate = auditDate == null ? LocalDate.now() : auditDate;
        return importVoiceDAO.getReceivedQuantityByIngredient(targetDate);
    }

    // Format số lượng nguyên liệu, bỏ số 0 dư phía sau.
    private String formatDecimal(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        return value.stripTrailingZeros().toPlainString();
    }

    private String formatStockValue(BigDecimal value, String unit) {
        return value == null ? "—" : formatDecimal(value) + " " + unit;
    }

    // Format tiền Việt Nam để hiển thị trên JSP.
    private String formatMoney(BigDecimal value) {
        if (value == null) {
            return "0 đ";
        }
        return String.format("%,.0f đ", value).replace(",", ".");
    }

    // Tính phần trăm thanh tồn kho trên dashboard nguyên liệu.
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
