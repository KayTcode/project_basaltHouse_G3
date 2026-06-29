package dto;

import model.Product;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ProductDTO {
    private Product product;
    private String categoryName;
    private String createdAtFormatted;
    
    // Hứng danh sách Size và Recipe từ phép JOIN
    private List<SizeDTO> sizes = new ArrayList<>();
    private List<RecipeDTO> recipes = new ArrayList<>();

    public ProductDTO(Product product, String categoryName) {
        this.product = product;
        this.categoryName = categoryName;
        if (product.getCreatedAt() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            this.createdAtFormatted = product.getCreatedAt().format(formatter);
        } else {
            this.createdAtFormatted = "Chưa xác định";
        }
    }

    // Các hàm thêm Size và Recipe tránh trùng lặp
    public void addSize(SizeDTO size) {
        boolean exists = sizes.stream().anyMatch(s -> s.getSizeId() == size.getSizeId());
        if (!exists) this.sizes.add(size);
    }

    public void addRecipe(RecipeDTO recipe) {
        boolean exists = recipes.stream().anyMatch(r -> r.getRecipeId() == recipe.getRecipeId());
        if (!exists) this.recipes.add(recipe);
    }

    // Getters & Setters
    public Product getProduct() { return product; }
    public String getCategoryName() { return categoryName; }
    public String getCreatedAtFormatted() { return createdAtFormatted; }
    public List<SizeDTO> getSizes() { return sizes; }
    public List<RecipeDTO> getRecipes() { return recipes; }

    // ĐÃ THÊM: build JSON string thủ công để JS đọc trực tiếp khi bấm nút Sửa (Edit)
    // Dùng để truyền nguyên list Size/Recipe của sản phẩm vào hàm openEditModal() bên JSP
    public String getSizesJson() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < sizes.size(); i++) {
            if (i > 0) sb.append(",");
            SizeDTO s = sizes.get(i);
            sb.append("{\"sizeId\":").append(s.getSizeId())
              .append(",\"price\":").append(s.getPrice() != null ? s.getPrice() : 0)
              .append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    public String getRecipesJson() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < recipes.size(); i++) {
            if (i > 0) sb.append(",");
            RecipeDTO r = recipes.get(i);
            String ingName = r.getIngredientName() == null ? "" : r.getIngredientName().replace("\"", "");
            String unit = r.getUnit() == null ? "" : r.getUnit().replace("\"", "");
            sb.append("{\"ingredientId\":").append(r.getIngredientId())
              .append(",\"ingredientName\":\"").append(ingName)
              .append("\",\"quantity\":").append(r.getQuantity())
              .append(",\"unit\":\"").append(unit)
              .append("\"}");
        }
        sb.append("]");
        return sb.toString();
    }
}