/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.IngredientDAO;
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
import model.OrderDetail;
import model.Product;
import model.Recipe;
import model.SalesAuditContext;
import model.StockNumbers;
import model.StockStatus;

public class StockService {

    private  final BigDecimal WARNING_RATE = new BigDecimal("1.2");
    private  final DateTimeFormatter DATE_TIME_INPUT_FORMAT
            = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private  final DateTimeFormatter DISPLAY_DATE_FORMAT
            = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private  final DateTimeFormatter DATE_INPUT_FORMAT
            = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final IngredientCheckService ingredientService = new IngredientCheckService();
    private final ProductDAO p = new ProductDAO();
    private final SizeDAO s = new SizeDAO();
    private final    ImportVoiceDAO staffDAO = new ImportVoiceDAO();
    public HashMap<Product, HashMap<String, Integer>> calculateProduct() {
        ProductDAO p = new ProductDAO();
        

        HashMap<Product, HashMap<String, Integer>> result = new HashMap<>();
        HashMap<Integer, Product> productMap = p.getProduct();
        HashMap<Integer, String> sizeMap = s.getSize();
        HashMap<Integer, Ingredient> ingredientMap = loadIngredientMap();
        HashMap<Integer, HashMap<Integer, List<Recipe>>> recipeMap = loadRecipeMap();

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

    public List<Ingredient> getWarnings() {
        IngredientDAO i = new IngredientDAO();
        return i.getIngredientsBelowWarning();
    }

    
 public HashMap<String, Object> getStaffDashboardData(String key ,boolean includeImportOptions) {
     List<HashMap<String, Object>> rows = new ArrayList<>();
       if(key==null|| key.trim().isEmpty()){
           rows = staffDAO.getIngredientStockRows();
      
       }else{
           rows = staffDAO.getIngredientStockRows(key);
       }
        
       
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
        if (includeImportOptions) {
            data.put("suppliers", staffDAO.getSupplierOptions());
            data.put("currentDateInput", LocalDateTime.now().format(DATE_TIME_INPUT_FORMAT));
        }
        return data;
    }

    public List<HashMap<String, Object>> getImportIngredientOptionsBySupplier(int supplierId) {
        List<HashMap<String, Object>> options = new ArrayList<>();
        if (supplierId <= 0) {
            return options;
        }

        for (HashMap<String, Object> row : staffDAO.getIngredientStockRowsBySupplier(supplierId)) {
            HashMap<String, Object> item = new HashMap<>();
            item.put("id", row.get("ingredientId"));
            item.put("name", row.get("ingredientName"));
            item.put("unit", row.get("unit"));
            item.put("stockText", formatDecimal(toBigDecimal(row.get("stockQuantity"))));
            item.put("supplierId", row.get("supplierId"));
            item.put("supplierName", row.get("supplierName"));
            options.add(item);
        }
        return options;
    }

    // Dựng dữ liệu kiểm kê bán hàng theo ngày được chọn.
    public HashMap<String, Object> getSalesAuditData(LocalDate selectedDate) {
        SalesAuditContext context = new SalesAuditContext(
                loadIngredientMap(),
                loadRecipeMap()
        );
        context.selectedDate = selectedDate;
        if (selectedDate == null) {
            context.auditDate = LocalDate.now();
        } else {
            context.auditDate = selectedDate;
        }

        List<HashMap<String, Object>> soldRows = loadSoldRows(context, selectedDate);
        if (selectedDate == null) {
            context.auditDate = resolveAuditDate(soldRows);
        }
        context.showingLatestSaleDate = selectedDate == null
                && !soldRows.isEmpty()
                && !LocalDate.now().equals(context.auditDate);
        context.importedByIngredient = getImportedQuantityByIngredient(context.auditDate);
        context.stockSnapshotByIngredient = loadStockSnapshotByIngredient(context.auditDate);

        List<HashMap<String, Object>> productSales = buildProductSales(soldRows, context);
        List<HashMap<String, Object>> ingredientAudit = buildIngredientAudit(context);

        return buildSalesAuditData(productSales, ingredientAudit, context);
    }

    public void updateStockForOrder(List<OrderDetail> details) {
        IngredientCheckService ingredientService = new IngredientCheckService();

        HashMap<Integer, HashMap<Integer, List<Recipe>>> recipeMap = loadRecipeMap();

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
                ingredientService.updateIngredientQuantity(recipe.getIngredientId(), quantityNeed);
            }
        }
    }

    // Lấy danh sách nguyên liệu qua service để hạn chế gọi IngredientDAO trực tiếp.
    private HashMap<Integer, Ingredient> loadIngredientMap() {
        HashMap<String, Object> result = new IngredientCheckService().getAllIngredients();
        Object success = result.get("success");
        if (success instanceof HashMap<?, ?>) {
            return (HashMap<Integer, Ingredient>) success;
        }
        return new HashMap<>();
    }

    // Lấy map công thức qua RecipeService để StockService không gọi RecipeDAO trực tiếp.
    private HashMap<Integer, HashMap<Integer, List<Recipe>>> loadRecipeMap() {
        HashMap<String, Object> result = new RecipeService().getRecipeMap();
        Object success = result.get("success");
        if (success instanceof HashMap<?, ?>) {
            return (HashMap<Integer, HashMap<Integer, List<Recipe>>>) success;
        }
        return new HashMap<>();
    }

    // Lấy tồn đầu/tồn cuối theo ngày từ log kho.
    private Map<Integer, HashMap<String, Object>> loadStockSnapshotByIngredient(LocalDate auditDate) {
        Map<Integer, HashMap<String, Object>> map = new HashMap<>();
        HashMap<String, Object> result = new IngredientCheckService().getStockSnapshotByDate(auditDate);
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
        RecipeService recipeService = new RecipeService();

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
                    && imported.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            if (expectedUsed.compareTo(BigDecimal.ZERO) > 0) {
                context.usedIngredientCount++;
            }

            StockNumbers stockNumbers = getStockNumbers(ingredientId, ingredient, expectedUsed, imported, context);
            StockStatus status = getStockStatus(ingredient, stockNumbers.closingStock, stockNumbers.difference);
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
        data.put("missingRecipeCount", context.missingRecipeCount);
        data.put("productSales", productSales);
        data.put("ingredientAudit", ingredientAudit);
        data.put("auditDateOnlyText", formatDate(context.auditDate));
        data.put("auditDateInput", context.auditDate.format(DATE_INPUT_FORMAT));
        data.put("todayDateInput", LocalDate.now().format(DATE_INPUT_FORMAT));

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
        BigDecimal expectedUsed = quantityPerCup.multiply(BigDecimal.valueOf(soldQuantity));

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
        String statusLabel = "Du cong thuc";
        if (missingRecipe) {
            statusLabel = "Thieu cong thuc";
        }
        sale.put("statusLabel", statusLabel);
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
        audit.put("unit", ingredient.getUnit());
        audit.put("expectedUsedText", formatDecimal(expectedUsed) + " " + ingredient.getUnit());
        audit.put("importedTodayText", formatDecimal(imported) + " " + ingredient.getUnit());
        audit.put("currentStockText", formatDecimal(stockNumbers.closingStock) + " " + ingredient.getUnit());
        audit.put("openingEstimateText", formatDecimal(stockNumbers.openingStock) + " " + ingredient.getUnit());
        audit.put("expectedClosingText", formatDecimal(stockNumbers.expectedClosingStock) + " " + ingredient.getUnit());
        audit.put("differenceText", formatDecimal(stockNumbers.difference) + " " + ingredient.getUnit());
        audit.put("cupsText", context.cupsByIngredient
                .getOrDefault(ingredientId, "Không có sản phẩm bán trong ngày này"));
        audit.put("usageDetails", context.usageDetailsByIngredient
                .getOrDefault(ingredientId, new ArrayList<>()));
        audit.put("stockBalanceText", formatDecimal(stockNumbers.openingStock) + " " + ingredient.getUnit()
                + " + " + formatDecimal(imported) + " " + ingredient.getUnit()
                + " - " + formatDecimal(expectedUsed) + " " + ingredient.getUnit()
                + " = " + formatDecimal(stockNumbers.expectedClosingStock) + " " + ingredient.getUnit());
        audit.put("statusClass", status.statusClass);
        audit.put("statusIcon", status.statusIcon);
        audit.put("statusLabel", status.statusLabel);
        return audit;
    }

    // Lấy tồn đầu/tồn cuối từ log; nếu thiếu log thì quay về cách ước tính cũ.
    private StockNumbers getStockNumbers(
            int ingredientId,
            Ingredient ingredient,
            BigDecimal expectedUsed,
            BigDecimal imported,
            SalesAuditContext context) {
        HashMap<String, Object> snapshot = context.stockSnapshotByIngredient.get(ingredientId);
        BigDecimal currentStock = ingredient.getStockQuantity();
        if (currentStock == null) {
            currentStock = BigDecimal.ZERO;
        }
        boolean hasStockLog = snapshot != null && Boolean.TRUE.equals(snapshot.get("hasStockLog"));

        BigDecimal openingStock;
        BigDecimal closingStock;
        if (hasStockLog) {
            openingStock = toBigDecimal(snapshot.get("openingStock"));
            closingStock = toBigDecimal(snapshot.get("closingStock"));
        } else {
            closingStock = currentStock;
            openingStock = closingStock.add(expectedUsed).subtract(imported);
        }

        BigDecimal expectedClosingStock = openingStock.add(imported).subtract(expectedUsed);
        BigDecimal difference = expectedClosingStock.subtract(closingStock);
        return new StockNumbers(openingStock, closingStock, expectedClosingStock, difference);
    }

    // Xác định trạng thái kiểm kê của nguyên liệu để hiển thị cảnh báo.
    private StockStatus getStockStatus(Ingredient ingredient, BigDecimal closingStock, BigDecimal difference) {
        BigDecimal minStock = ingredient.getMinStockQuantity();
        if (minStock == null) {
            minStock = BigDecimal.ZERO;
        }

        if (difference.compareTo(BigDecimal.ZERO) != 0) {
            return new StockStatus("danger", "error", "Lệch kho");
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
        LocalDate targetDate = auditDate;
        if (targetDate == null) {
            targetDate = LocalDate.now();
        }
        return new ImportVoiceDAO().getReceivedQuantityByIngredient(targetDate);
    }

    // Format số lượng nguyên liệu, bỏ số 0 dư phía sau.
    private String formatDecimal(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        return value.stripTrailingZeros().toPlainString();
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
