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

    // Chuyển từ StringBuilder sang String nối chuỗi (+) cho giống phong cách của bạn
    String sql = "WITH PagedProducts AS ( "
            + "    SELECT ProductId FROM Products WHERE IsDeleted = 0 ";

    if (search != null && !search.trim().isEmpty()) {
        sql += " AND ProductName LIKE ? ";
    }
    if (categoryId != null && !categoryId.trim().isEmpty()) {
        sql += " AND CategoryId = ? ";
    }

    sql += "    ORDER BY CreatedAt DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY "
            + ") "
            + "SELECT p.ProductId, p.ProductName, p.CategoryId, p.Price AS BasePrice, p.Description, p.ImageUrl, p.IsActive, p.CreatedAt, p.IsDeleted, "
            + "       c.CategoryName, "
            + "       ps.SizeId, ps.Price AS SizePrice, s.SizeName, "
            + "       r.RecipeId, r.IngredientId, ing.IngredientName, r.QuantityNeeded, ing.Unit "
            + "FROM Products p "
            + "INNER JOIN PagedProducts pp ON p.ProductId = pp.ProductId "
            + "LEFT JOIN Categories c ON p.CategoryId = c.CategoryId "
            + "LEFT JOIN ProductSizes ps ON p.ProductId = ps.ProductId AND ps.IsDeleted = 0 "
            + "LEFT JOIN Sizes s ON ps.SizeId = s.SizeId "
            + "LEFT JOIN Recipes r ON p.ProductId = r.ProductId AND r.SizeId = ps.SizeId AND r.IsDeleted = 0 "
            + "LEFT JOIN Ingredients ing ON r.IngredientId = ing.IngredientId "
            + "ORDER BY p.CreatedAt DESC, p.ProductId, ps.SizeId, r.RecipeId";

    try (PreparedStatement st = connection.prepareStatement(sql)) {
        int paramIndex = 1;

        // Set parameters
        if (search != null && !search.trim().isEmpty()) {
            st.setString(paramIndex++, "%" + search + "%");
        }
        if (categoryId != null && !categoryId.trim().isEmpty()) {
            st.setInt(paramIndex++, Integer.parseInt(categoryId));
        }
        st.setInt(paramIndex++, offset);
        st.setInt(paramIndex, limit);

        try (ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                int productId = rs.getInt("ProductId");

                // Khởi tạo Product & ProductDTO nếu chưa có trong Map
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

                    java.sql.Timestamp ts = rs.getTimestamp("CreatedAt");
                    if (ts != null) {
                        p.setCreatedAt(ts.toLocalDateTime());
                    }

                    productMap.put(productId, new ProductDTO(p, rs.getString("CategoryName")));
                }

                ProductDTO currentDTO = productMap.get(productId);

                // Add SizeDTO
                int sizeId = rs.getInt("SizeId");
                if (!rs.wasNull()) {
                    currentDTO.addSize(new SizeDTO(sizeId, rs.getString("SizeName"), rs.getBigDecimal("SizePrice")));
                }

                // Add RecipeDTO
                int recipeId = rs.getInt("RecipeId");
                if (!rs.wasNull()) {
                    currentDTO.addRecipe(new RecipeDTO(recipeId, rs.getInt("IngredientId"), rs.getDouble("QuantityNeeded"), rs.getString("Unit"), rs.getString("IngredientName")));
                }
            }
        }
    } catch (SQLException e) {
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
                    // SỬA: Recipes.SizeId nhiều khả năng là NOT NULL (có FK tới Sizes) nên không thể setNull.
                    // Nếu sản phẩm có cấu hình Size thì insert 1 dòng Recipe cho MỖI Size đã chọn.
                    // Nếu sản phẩm không có Size nào (trường hợp hiếm), mới fallback insert NULL.
                    if (productSizes != null && !productSizes.isEmpty()) {
                        try (PreparedStatement pstRecipe = connection.prepareStatement(insertRecipeSQL)) {
                            for (SizeDTO size : productSizes) {
                                pstRecipe.setInt(1, newProductId);
                                pstRecipe.setInt(2, currentIngredientId);
                                pstRecipe.setInt(3, size.getSizeId());        // SizeId thật, không còn NULL
                                pstRecipe.setDouble(4, recipe.getQuantity()); // QuantityNeeded
                                pstRecipe.setString(5, recipe.getUnit());     // Note
                                pstRecipe.addBatch();
                            }
                            pstRecipe.executeBatch();
                        }
                    } else {
                        try (PreparedStatement pstRecipe = connection.prepareStatement(insertRecipeSQL)) {
                            pstRecipe.setInt(1, newProductId);
                            pstRecipe.setInt(2, currentIngredientId);
                            pstRecipe.setNull(3, java.sql.Types.INTEGER);
                            pstRecipe.setDouble(4, recipe.getQuantity());
                            pstRecipe.setString(5, recipe.getUnit());
                            pstRecipe.executeUpdate();
                        }
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
        // 1. SQL Cập nhật bảng Products
        String updateProductSQL = "UPDATE [dbo].[Products] "
                + "SET [ProductName] = ?, [CategoryId] = ?, [Price] = ?, [Description] = ?, [ImageUrl] = ?, [IsActive] = ? "
                + "WHERE [ProductId] = ?";

        // 2. Ẩn tất cả Sizes và Recipes cũ của sản phẩm này
        String resetSizesSQL = "UPDATE ProductSizes SET IsDeleted = 1 WHERE ProductId = ?";
        String resetRecipesSQL = "UPDATE Recipes SET IsDeleted = 1 WHERE ProductId = ?";

        // 3. SQL THÔNG MINH (UPSERT): Nếu Size đã từng tồn tại -> Mở khóa & Update. Nếu chưa từng có -> Insert
        String upsertSizeSQL = 
            "IF EXISTS (SELECT 1 FROM ProductSizes WHERE ProductId = ? AND SizeId = ?) " +
            "  UPDATE ProductSizes SET Price = ?, IsDeleted = 0, IsAvailable = 1 WHERE ProductId = ? AND SizeId = ? " +
            "ELSE " +
            "  INSERT INTO ProductSizes (ProductId, SizeId, Price, IsAvailable, IsDeleted) VALUES (?, ?, ?, 1, 0)";

        // 4. SQL THÔNG MINH (UPSERT) cho Recipe
        String upsertRecipeSQL = 
            "IF EXISTS (SELECT 1 FROM Recipes WHERE ProductId = ? AND IngredientId = ?) " +
            "  UPDATE Recipes SET QuantityNeeded = ?, Note = ?, IsDeleted = 0 WHERE ProductId = ? AND IngredientId = ? " +
            "ELSE " +
            "  INSERT INTO Recipes (ProductId, IngredientId, SizeId, QuantityNeeded, Note, IsDeleted) VALUES (?, ?, NULL, ?, ?, 0)";
            
        String insertIngredientsSQL = "INSERT INTO Ingredients(IngredientName, Unit, StockQuantity, MinStockQuantity, SupplierId, IsActive, IsDeleted) VALUES (?, ?, 0, 0, 1, 1, 0)";

        try {
            connection.setAutoCommit(false);
            int productId = p.getProductId();

            // BƯỚC 1: Cập nhật thông tin chính của sản phẩm
            try (PreparedStatement pst = connection.prepareStatement(updateProductSQL)) {
                pst.setString(1, p.getProductName());
                pst.setInt(2, p.getCategoryId());
                pst.setBigDecimal(3, p.getPrice());
                pst.setString(4, p.getDescription());
                pst.setString(5, p.getImageUrl());
                pst.setBoolean(6, p.isIsActive()); // Đổi thành p.getIsActive() nếu Model của bạn quy định vậy
                pst.setInt(7, productId);
                pst.executeUpdate();
            }

            // BƯỚC 2: Xóa mềm toàn bộ dữ liệu Size & Công thức cũ
            try (PreparedStatement pstDelSize = connection.prepareStatement(resetSizesSQL)) {
                pstDelSize.setInt(1, productId);
                pstDelSize.executeUpdate();
            }
            try (PreparedStatement pstDelRecipe = connection.prepareStatement(resetRecipesSQL)) {
                pstDelRecipe.setInt(1, productId);
                pstDelRecipe.executeUpdate();
            }

            // BƯỚC 3: Cập nhật lại danh sách Size mới (Dùng UPSERT)
            if (productSizes != null && !productSizes.isEmpty()) {
                try (PreparedStatement pstSize = connection.prepareStatement(upsertSizeSQL)) {
                    for (SizeDTO size : productSizes) {
                        pstSize.setInt(1, productId);
                        pstSize.setInt(2, size.getSizeId());
                        pstSize.setBigDecimal(3, size.getPrice());
                        pstSize.setInt(4, productId);
                        pstSize.setInt(5, size.getSizeId());
                        pstSize.setInt(6, productId);
                        pstSize.setInt(7, size.getSizeId());
                        pstSize.setBigDecimal(8, size.getPrice());
                        pstSize.addBatch();
                    }
                    pstSize.executeBatch();
                }
            }

            // BƯỚC 4: Cập nhật lại công thức (Dùng UPSERT)
            if (recipes != null && !recipes.isEmpty()) {
                for (RecipeDTO recipe : recipes) {
                    int currentIngredientId = recipe.getIngredientId();

                    // Nếu nguyên liệu mới tinh (Tạo tại chỗ)
                    if (currentIngredientId == 0) {
                        try (PreparedStatement pstIng = connection.prepareStatement(insertIngredientsSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                            pstIng.setString(1, recipe.getIngredientName() != null ? recipe.getIngredientName() : "Nguyên liệu mới");
                            pstIng.setString(2, recipe.getUnit());
                            pstIng.executeUpdate();
                            try (ResultSet rsIng = pstIng.getGeneratedKeys()) {
                                if (rsIng.next()) {
                                    currentIngredientId = rsIng.getInt(1);
                                }
                            }
                        }
                    }

                    // Chạy lệnh Upsert Recipe an toàn
                    try (PreparedStatement pstRecipe = connection.prepareStatement(upsertRecipeSQL)) {
                        pstRecipe.setInt(1, productId);
                        pstRecipe.setInt(2, currentIngredientId);
                        pstRecipe.setDouble(3, recipe.getQuantity());
                        pstRecipe.setString(4, recipe.getUnit());
                        pstRecipe.setInt(5, productId);
                        pstRecipe.setInt(6, currentIngredientId);
                        pstRecipe.setInt(7, productId);
                        pstRecipe.setInt(8, currentIngredientId);
                        pstRecipe.setDouble(9, recipe.getQuantity());
                        pstRecipe.setString(10, recipe.getUnit());
                        pstRecipe.executeUpdate();
                    }
                }
            }

            // Hoàn tất không một vết xước
            connection.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("===> BẮT ĐƯỢC LỖI SQL KHI CẬP NHẬT: " + e.getMessage());
            e.printStackTrace();
            try { if (connection != null) connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try { if (connection != null) connection.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
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