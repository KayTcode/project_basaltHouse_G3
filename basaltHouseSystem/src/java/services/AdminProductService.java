package services;

import dao.AdminProductDAO;
import dto.ProductDTO;
import dto.RecipeDTO;
import dto.SizeDTO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Product;

public class AdminProductService {

    // Khởi tạo DAO
    private final AdminProductDAO productDAO = new AdminProductDAO();

    /**
     * Hàm chính xử lý lấy toàn bộ dữ liệu cho trang Danh sách Sản phẩm
     */
    public Map<String, Object> getProductDashboardData(String search, String categoryId, String pageStr, int pageSize) {
        Map<String, Object> data = new HashMap<>();

        // 1. Xử lý logic phân trang an toàn
        int page = 1;
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
                if (page < 1) {
                    page = 1;
                }
            } catch (NumberFormatException ignored) {
                // Bỏ qua nếu user nhập bậy bạ lên URL (VD: ?page=abc)
            }
        }
        int offset = (page - 1) * pageSize;

        // 2. Gọi DAO lấy danh sách Sản phẩm (Đã JOIN 5 bảng: Product, Category, Size, ProductSize, Recipe)
        List<ProductDTO> products = productDAO.getProductsWithFullDetails(search, categoryId, offset, pageSize);

        // 3. Tính toán tổng số trang theo kết quả Search / Lọc
        int totalProducts = productDAO.countProducts(search, categoryId, null);
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
        if (totalPages == 0) {
            totalPages = 1; // Mặc định luôn có ít nhất 1 trang trống
        }
        // 4. Đóng gói dữ liệu hiển thị (Cho Table & Phân trang JSP)
        data.put("products", products);
        data.put("currentPage", page);
        data.put("totalPages", totalPages);
        data.put("oldSearch", search != null ? search : "");
        data.put("oldCategoryId", categoryId != null ? categoryId : "");

        // 5. Thống kê dữ liệu cho các thẻ KPI phía trên cùng giao diện
        Map<String, Integer> stats = new HashMap<>();
        // Truyền null để bỏ qua bộ lọc, đếm tổng quan toàn bộ hệ thống
        stats.put("total", productDAO.countProducts(null, null, null));
        stats.put("active", productDAO.countProducts(null, null, true));
        stats.put("inactive", productDAO.countProducts(null, null, false));

        data.put("stats", stats);
        data.put("categories", productDAO.getAllCategories());
        data.put("formSizes", productDAO.getAllSizes());
        data.put("formIngredients", productDAO.getAllIngredients());
        return data;
    }

// Bổ sung vào cuối hàm getProductDashboardData() hiện tại của bạn:
    // data.put("formSizes", productDAO.getAllSizes());
    // data.put("formIngredients", productDAO.getAllIngredients());
    /**
     * XỬ LÝ POST: Thêm sản phẩm cùng size và recipe
     */
    //1 hàm thêm sản phẩm
    public boolean processAddProduct(String name, String description, String catIdStr, String priceStr, String imgUrl,
            String[] sizeIds, String[] sizePrices,
            String[] ingredientIds, String[] ingredientNames,
            String[] quantities, String[] units) {
        try {
            // 1. Build đối tượng Product
            Product product = new Product();
            product.setProductName(name);
            product.setDescription(description);
            product.setCategoryId(Integer.parseInt(catIdStr));
            product.setPrice(new BigDecimal(priceStr));
            product.setImageUrl(imgUrl);
            // Lưu ý: SQL của bạn đã fix cứng IsActive = 0, nên thuộc tính này ở Java không ảnh hưởng tới lệnh INSERT, nhưng vẫn khởi tạo cho đúng logic Model.
            product.setIsActive(false);

            // 2. Build danh sách Size
            List<SizeDTO> productSizes = new ArrayList<>();
            if (sizeIds != null && sizePrices != null) {
                for (int i = 0; i < sizeIds.length; i++) {
                    if (i < sizePrices.length && !sizeIds[i].trim().isEmpty() && !sizePrices[i].trim().isEmpty()) {
                        int sId = Integer.parseInt(sizeIds[i]);
                        BigDecimal sPrice = new BigDecimal(sizePrices[i]);
                        productSizes.add(new SizeDTO(sId, "", sPrice));
                    }
                }
            }

            // 3. Build danh sách Recipe (Công thức)
            List<RecipeDTO> recipes = new ArrayList<>();
            if (ingredientIds != null && quantities != null && units != null) {
                for (int i = 0; i < ingredientIds.length; i++) {
                    if (i < quantities.length && i < units.length && !quantities[i].trim().isEmpty()) {

                        int ingId = 0;
                        try {
                            ingId = Integer.parseInt(ingredientIds[i]);
                        } catch (NumberFormatException e) {
                            ingId = 0; // Nếu parse lỗi (VD: string rỗng), gán = 0 để tạo nguyên liệu mới
                        }

                        // Nếu ID = 0, DAO sẽ dùng tên này để tạo nguyên liệu mới vào DB
                        String ingName = (ingredientNames != null && i < ingredientNames.length) ? ingredientNames[i] : "Nguyên liệu mới";

                        double qty = Double.parseDouble(quantities[i]);
                        String unit = units[i];

                        recipes.add(new RecipeDTO(0, ingId, qty, unit, ingName));
                    }
                }
            }

            // 4. Gọi DAO thực thi Transaction
            return productDAO.addProductTransaction(product, productSizes, recipes);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

     public boolean processEditProduct(String productIdStr, String name, String description, String catIdStr, String priceStr, String imgUrl, String isActiveStr,
                                       String[] sizeIds, String[] sizePrices,
                                       String[] ingredientIds, String[] ingredientNames,
                                       String[] quantities, String[] units) {
        try {
            Product product = new Product();
            product.setProductId(Integer.parseInt(productIdStr));
            product.setProductName(name);
            product.setDescription(description);
            product.setCategoryId(Integer.parseInt(catIdStr));
            product.setPrice(new BigDecimal(priceStr));
            product.setImageUrl(imgUrl);
            product.setIsActive(isActiveStr != null && Boolean.parseBoolean(isActiveStr));

            List<SizeDTO> productSizes = new ArrayList<>();
            if (sizeIds != null && sizePrices != null) {
                for (int i = 0; i < sizeIds.length; i++) {
                    if (i < sizePrices.length && !sizeIds[i].trim().isEmpty() && !sizePrices[i].trim().isEmpty()) {
                        int sId = Integer.parseInt(sizeIds[i]);
                        BigDecimal sPrice = new BigDecimal(sizePrices[i]);
                        productSizes.add(new SizeDTO(sId, "", sPrice));
                    }
                }
            }

            List<RecipeDTO> recipes = new ArrayList<>();
            if (ingredientIds != null && quantities != null && units != null) {
                for (int i = 0; i < ingredientIds.length; i++) {
                    if (i < quantities.length && i < units.length && !quantities[i].trim().isEmpty()) {
                        int ingId = 0;
                        try {
                            ingId = Integer.parseInt(ingredientIds[i]);
                        } catch (NumberFormatException e) {
                            ingId = 0;
                        }
                        String ingName = (ingredientNames != null && i < ingredientNames.length) ? ingredientNames[i] : "Nguyên liệu mới";
                        double qty = Double.parseDouble(quantities[i]);
                        String unit = units[i];
                        recipes.add(new RecipeDTO(0, ingId, qty, unit, ingName));
                    }
                }
            }

            return productDAO.updateProductTransaction(product, productSizes, recipes);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * XỬ LÝ POST: Xóa mềm sản phẩm
     */
    public boolean processDeleteProduct(String productIdStr) {
        try {
            int productId = Integer.parseInt(productIdStr);
            return productDAO.softDeleteProduct(productId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}