package dao;

import dto.IngredientDTO;
import dto.ProductDTO;
import dto.RecipeDTO;
import dto.SizeDTO;
import model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.Category;

public class AdminProductDAO extends DBContext {

    public List<ProductDTO> getProductsWithFullDetails(String search, String categoryId, int offset, int limit) {
        Map<Integer, ProductDTO> productMap = new LinkedHashMap<>();

        StringBuilder sql = new StringBuilder();
        sql.append("WITH PagedProducts AS ( ");
        sql.append("    SELECT ProductId FROM Products ");
        sql.append("    WHERE IsDeleted = 0 ");

        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND ProductName LIKE ? ");
        }
        if (categoryId != null && !categoryId.trim().isEmpty()) {
            sql.append(" AND CategoryId = ? ");
        }

        sql.append("    ORDER BY CreatedAt DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ");
        sql.append(") ");
        sql.append("SELECT ");
        sql.append("    p.ProductId, p.ProductName, p.CategoryId, p.Price AS BasePrice, p.Description, p.ImageUrl, p.IsActive, p.CreatedAt, p.IsDeleted, ");
        sql.append("    c.CategoryName, ");
        sql.append("    ps.SizeId, ps.Price AS SizePrice, s.SizeName, ");
        sql.append("    r.RecipeId, r.IngredientId, ing.IngredientName, r.QuantityNeeded, ing.Unit ");
        sql.append("FROM Products p ");
        sql.append("INNER JOIN PagedProducts pp ON p.ProductId = pp.ProductId ");
        sql.append("LEFT JOIN Categories c ON p.CategoryId = c.CategoryId ");
        sql.append("LEFT JOIN ProductSizes ps ON p.ProductId = ps.ProductId AND ps.IsDeleted = 0 ");
        sql.append("LEFT JOIN Sizes s ON ps.SizeId = s.SizeId ");
        sql.append("LEFT JOIN Recipes r ON p.ProductId = r.ProductId AND r.SizeId = ps.SizeId AND r.IsDeleted = 0 ");
        sql.append("LEFT JOIN Ingredients ing ON r.IngredientId = ing.IngredientId ");
        sql.append("ORDER BY p.CreatedAt DESC, p.ProductId, ps.SizeId, r.RecipeId");

        try (PreparedStatement st = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;

            if (search != null && !search.trim().isEmpty()) {
                st.setString(paramIndex++, "%" + search + "%");
            }
            if (categoryId != null && !categoryId.trim().isEmpty()) {
                st.setInt(paramIndex++, Integer.parseInt(categoryId));
            }
            st.setInt(paramIndex++, offset);
            st.setInt(paramIndex, limit);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                int productId = rs.getInt("ProductId");

                if (!productMap.containsKey(productId)) {
                    Product p = new Product();
                    p.setProductId(productId);
                    p.setProductName(rs.getString("ProductName"));
                    p.setCategoryId(rs.getInt("CategoryId"));
                    p.setPrice(rs.getBigDecimal("BasePrice"));
                    p.setDescription(rs.getString("Description"));
                    p.setImageUrl(rs.getString("ImageUrl"));
                    p.setIsActive(rs.getBoolean("IsActive"));
                    p.setIsDeleted(rs.getBoolean("IsDeleted"));

                    Timestamp ts = rs.getTimestamp("CreatedAt");
                    if (ts != null) {
                        p.setCreatedAt(ts.toLocalDateTime());
                    }

                    ProductDTO dto = new ProductDTO(p, rs.getString("CategoryName"));
                    productMap.put(productId, dto);
                }

                ProductDTO currentDTO = productMap.get(productId);

                int sizeId = rs.getInt("SizeId");
                if (!rs.wasNull()) {
                    SizeDTO sizeDTO = new SizeDTO(
                            sizeId,
                            rs.getString("SizeName"),
                            rs.getBigDecimal("SizePrice")
                    );
                    currentDTO.addSize(sizeDTO);
                }

                int recipeId = rs.getInt("RecipeId");
                if (!rs.wasNull()) {
                    RecipeDTO recipeDTO = new RecipeDTO(
                            recipeId,
                            rs.getInt("IngredientId"),
                            rs.getDouble("QuantityNeeded"),
                            rs.getString("Unit"),
                            rs.getString("IngredientName")
                    );
                    currentDTO.addRecipe(recipeDTO);
                }
            }
        } catch (SQLException e) {
            // ĐÃ SỬA: in lỗi rõ ràng ra console — bản gốc chỉ printStackTrace,
            // dễ bị bỏ lỡ khi tên cột sai (đây chính là lý do JSP cũ luôn trống dữ liệu)
            System.err.println("Lỗi SQL khi lấy danh sách sản phẩm: " + e.getMessage());
            e.printStackTrace();
        }

        return new ArrayList<>(productMap.values());
    }

    /**
     * Đếm tổng số lượng sản phẩm dựa trên các bộ lọc (Phục vụ cho phân trang và
     * thống kê thẻ KPI)
     */
    public int countProducts(String search, String categoryId, Boolean isActive) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Products WHERE IsDeleted = 0 ");

        // Nối thêm điều kiện tìm kiếm nếu có
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND ProductName LIKE ? ");
        }

        // Nối thêm điều kiện danh mục nếu có
        if (categoryId != null && !categoryId.trim().isEmpty()) {
            sql.append(" AND CategoryId = ? ");
        }

        // Nối thêm điều kiện trạng thái (Dùng cho thẻ thống kê KPI)
        if (isActive != null) {
            sql.append(" AND IsActive = ? ");
        }

        try (PreparedStatement st = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;

            // Set giá trị cho các tham số tương ứng với câu SQL đã nối
            if (search != null && !search.trim().isEmpty()) {
                st.setString(paramIndex++, "%" + search + "%");
            }

            if (categoryId != null && !categoryId.trim().isEmpty()) {
                st.setInt(paramIndex++, Integer.parseInt(categoryId));
            }

            if (isActive != null) {
                st.setBoolean(paramIndex++, isActive);
            }

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi đếm số lượng sản phẩm: " + e.getMessage());
            e.printStackTrace();
        }
        return 0; // Trả về 0 nếu có lỗi hoặc không có dữ liệu
    }

    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT CategoryId, CategoryName, Description, ImageUrl, IsDeleted "
                + "FROM Categories WHERE IsDeleted = 0 ORDER BY CategoryName";
        try (PreparedStatement st = connection.prepareStatement(sql); ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                Category c = new Category();
                c.setCategoryId(rs.getInt("CategoryId"));
                c.setCategoryName(rs.getString("CategoryName"));
                c.setDescription(rs.getString("Description"));
                c.setImage(rs.getString("ImageUrl"));
                c.setIsDeleted(rs.getBoolean("IsDeleted"));
                list.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách danh mục: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // 1. Lấy danh sách các Size có sẵn trong hệ thống để đổ ra Form
    public List<SizeDTO> getAllSizes() {
        List<SizeDTO> list = new ArrayList<>();
        String sql = "SELECT SizeId, SizeName FROM Sizes";
        try (PreparedStatement st = connection.prepareStatement(sql); ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                // Giá price ở đây để tạm là 0 vì bảng Sizes gốc không có giá, giá nằm ở ProductSizes
                list.add(new SizeDTO(rs.getInt("SizeId"), rs.getString("SizeName"), java.math.BigDecimal.ZERO));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Lấy danh sách nguyên liệu (Ingredients) để đổ ra Form
    public List<IngredientDTO> getAllIngredients() {
        List<IngredientDTO> list = new ArrayList<>();
        String sql = "SELECT ig.IngredientId, IngredientName,QuantityNeeded, Unit \n"
                + "FROM Ingredients ig inner join Recipes r\n"
                + "on ig.IngredientId = r.IngredientId\n"
                + "WHERE ig.IsDeleted = 0";
        try (PreparedStatement st = connection.prepareStatement(sql); ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                // Giá price ở đây để tạm là 0 vì bảng Sizes gốc không có giá, giá nằm ở ProductSizes
                list.add(new IngredientDTO(rs.getInt("IngredientId"), rs.getString("IngredientName"),
                        rs.getDouble("QuantityNeeded"), rs.getString("Unit")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 3. THÊM SẢN PHẨM MỚI (Sử dụng Transaction để đảm bảo tính toàn vẹn dữ liệu)
    public boolean addProductTransaction(Product p, List<SizeDTO> productSizes, List<RecipeDTO> recipes) {

        // GIỮ NGUYÊN 100% SQL CỦA BẠN
        String insertProductSQL = "INSERT INTO [dbo].[Products]\n"
                + "           ([ProductName]\n"
                + "           ,[CategoryId]\n"
                + "           ,[Price]\n"
                + "           ,[Description]\n"
                + "           ,[ImageUrl]\n"
                + "           ,[IsActive]\n"
                + "           ,[CreatedAt]\n"
                + "           ,[IsDeleted])\n"
                + "     VALUES (?, ?, ?, ?, ?, 0, GETDATE(), 0)"; // 5 Dấu ?

        String insertSizeSQL = "INSERT INTO ProductSizes (ProductId, SizeId, Price, IsAvailable, IsDeleted) VALUES (?, ?, ?, 0, 0)"; // 3 Dấu ?

        String insertRecipeSQL = "INSERT INTO Recipes (ProductId, IngredientId, SizeId, QuantityNeeded, Note, IsDeleted ) VALUES (?, ?, ?, ?, ?, 0)"; // 5 Dấu ?

        String insertIngredientsSQL = "INSERT INTO Ingredients( IngredientName, Unit, StockQuantity, MinStockQuantity, SupplierId, IsActive, IsDeleted) VALUES (?, ?, ?, ?, ?, 0, 0)"; // 5 Dấu ?

        try {
            // Tắt auto commit để bắt đầu Transaction
            connection.setAutoCommit(false);

            // =================================================================
            // BƯỚC 1: INSERT PRODUCT (Với 5 tham số dấu ?)
            // =================================================================
            int newProductId = 0;
            try (PreparedStatement pst = connection.prepareStatement(insertProductSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pst.setString(1, p.getProductName());
                pst.setInt(2, p.getCategoryId());
                pst.setBigDecimal(3, p.getPrice());
                pst.setString(4, p.getDescription());
                pst.setString(5, p.getImageUrl());
                // Không truyền IsActive vì trong SQL của bạn đã fix cứng là 0 ở vị trí thứ 6

                pst.executeUpdate();
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    newProductId = rs.getInt(1);
                }
            }

            if (newProductId == 0) {
                connection.rollback();
                return false;
            }

            // =================================================================
            // BƯỚC 2: INSERT PRODUCT SIZES (Với 3 tham số dấu ?)
            // =================================================================
            if (productSizes != null && !productSizes.isEmpty()) {
                try (PreparedStatement pstSize = connection.prepareStatement(insertSizeSQL)) {
                    for (SizeDTO size : productSizes) {
                        pstSize.setInt(1, newProductId);
                        pstSize.setInt(2, size.getSizeId());
                        pstSize.setBigDecimal(3, size.getPrice());
                        pstSize.addBatch();
                    }
                    pstSize.executeBatch();
                }
            }

            // =================================================================
            // BƯỚC 3: INSERT INGREDIENTS VÀ RECIPES
            // =================================================================
            if (recipes != null && !recipes.isEmpty()) {
                for (RecipeDTO recipe : recipes) {
                    int currentIngredientId = recipe.getIngredientId();

                    // 3.1: Nếu là nguyên liệu mới chưa có trong kho (Ví dụ ID = 0), tiến hành chạy insertIngredientsSQL
                    if (currentIngredientId == 0) {
                        try (PreparedStatement pstIng = connection.prepareStatement(insertIngredientsSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                            // Truyền 5 tham số cho Ingredients
                            pstIng.setString(1, recipe.getIngredientName() != null ? recipe.getIngredientName() : "Nguyên liệu mới"); // IngredientName
                            pstIng.setString(2, recipe.getUnit()); // Unit
                            pstIng.setDouble(3, 0); // StockQuantity (Mặc định 0)
                            pstIng.setDouble(4, 0); // MinStockQuantity (Mặc định 0)
                            pstIng.setInt(5, 1);    // SupplierId (Tạm set là 1, tùy logic hệ thống của bạn)

                            pstIng.executeUpdate();
                            ResultSet rsIng = pstIng.getGeneratedKeys();
                            if (rsIng.next()) {
                                currentIngredientId = rsIng.getInt(1); // Lấy ID nguyên liệu mới sinh ra
                            }
                        }
                    }

                    // 3.2: Insert vào bảng Recipes (Với 5 tham số dấu ?)
                    try (PreparedStatement pstRecipe = connection.prepareStatement(insertRecipeSQL)) {
                        pstRecipe.setInt(1, newProductId);
                        pstRecipe.setInt(2, currentIngredientId);
                        pstRecipe.setNull(3, java.sql.Types.INTEGER); // SizeId tạm set null vì công thức chung
                        pstRecipe.setDouble(4, recipe.getQuantity()); // QuantityNeeded
                        pstRecipe.setString(5, recipe.getUnit());     // Note

                        pstRecipe.executeUpdate(); // Chạy thẳng (không dùng batch vì nằm trong vòng lặp có xử lý IngredientId)
                    }
                }
            }

            // =================================================================
            // HOÀN TẤT GIAO DỊCH
            // =================================================================
            connection.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean updateProductTransaction(Product p, List<SizeDTO> productSizes, List<RecipeDTO> recipes) {

        String updateProductSQL = "UPDATE Products SET ProductName=?, CategoryId=?, Price=?, Description=?, ImageUrl=?, IsActive=? WHERE ProductId=?";

        // Xóa mềm size & recipe cũ rồi insert lại cho gọn (đơn giản & an toàn nhất với cấu trúc hiện tại)
        String deleteOldSizesSQL = "UPDATE ProductSizes SET IsDeleted = 1 WHERE ProductId = ?";
        String deleteOldRecipesSQL = "UPDATE Recipes SET IsDeleted = 1 WHERE ProductId = ?";

        String insertSizeSQL = "INSERT INTO ProductSizes (ProductId, SizeId, Price, IsAvailable, IsDeleted) VALUES (?, ?, ?, 1, 0)";
        String insertRecipeSQL = "INSERT INTO Recipes (ProductId, IngredientId, SizeId, QuantityNeeded, Note, IsDeleted) VALUES (?, ?, ?, ?, ?, 0)";
        String insertIngredientsSQL = "INSERT INTO Ingredients(IngredientName, Unit, StockQuantity, MinStockQuantity, SupplierId, IsActive, IsDeleted) VALUES (?, ?, ?, ?, ?, 0, 0)";

        try {
            connection.setAutoCommit(false);

            // BƯỚC 1: UPDATE PRODUCT
            try (PreparedStatement pst = connection.prepareStatement(updateProductSQL)) {
                pst.setString(1, p.getProductName());
                pst.setInt(2, p.getCategoryId());
                pst.setBigDecimal(3, p.getPrice());
                pst.setString(4, p.getDescription());
                pst.setString(5, p.getImageUrl());
                pst.setBoolean(6, p.isIsActive());
                pst.setInt(7, p.getProductId());
                pst.executeUpdate();
            }

            int productId = p.getProductId();

            // BƯỚC 2: XÓA MỀM SIZE/RECIPE CŨ
            try (PreparedStatement pst = connection.prepareStatement(deleteOldSizesSQL)) {
                pst.setInt(1, productId);
                pst.executeUpdate();
            }
            try (PreparedStatement pst = connection.prepareStatement(deleteOldRecipesSQL)) {
                pst.setInt(1, productId);
                pst.executeUpdate();
            }

            // BƯỚC 3: INSERT LẠI SIZE MỚI
            if (productSizes != null && !productSizes.isEmpty()) {
                try (PreparedStatement pstSize = connection.prepareStatement(insertSizeSQL)) {
                    for (SizeDTO size : productSizes) {
                        pstSize.setInt(1, productId);
                        pstSize.setInt(2, size.getSizeId());
                        pstSize.setBigDecimal(3, size.getPrice());
                        pstSize.addBatch();
                    }
                    pstSize.executeBatch();
                }
            }

            // BƯỚC 4: INSERT LẠI RECIPE MỚI (kèm tạo nguyên liệu mới nếu cần)
            if (recipes != null && !recipes.isEmpty()) {
                for (RecipeDTO recipe : recipes) {
                    int currentIngredientId = recipe.getIngredientId();

                    if (currentIngredientId == 0) {
                        try (PreparedStatement pstIng = connection.prepareStatement(insertIngredientsSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                            pstIng.setString(1, recipe.getIngredientName() != null ? recipe.getIngredientName() : "Nguyên liệu mới");
                            pstIng.setString(2, recipe.getUnit());
                            pstIng.setDouble(3, 0);
                            pstIng.setDouble(4, 0);
                            pstIng.setInt(5, 1);
                            pstIng.executeUpdate();
                            ResultSet rsIng = pstIng.getGeneratedKeys();
                            if (rsIng.next()) {
                                currentIngredientId = rsIng.getInt(1);
                            }
                        }
                    }

                    try (PreparedStatement pstRecipe = connection.prepareStatement(insertRecipeSQL)) {
                        pstRecipe.setInt(1, productId);
                        pstRecipe.setInt(2, currentIngredientId);
                        pstRecipe.setNull(3, java.sql.Types.INTEGER);
                        pstRecipe.setDouble(4, recipe.getQuantity());
                        pstRecipe.setString(5, recipe.getUnit());
                        pstRecipe.executeUpdate();
                    }
                }
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // 5. XÓA MỀM SẢN PHẨM
    public boolean softDeleteProduct(int productId) {
        String sql = "UPDATE Products SET IsDeleted = 1 WHERE ProductId = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, productId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
