package dto;

public class RecipeDTO {
    private int recipeId;
    private int ingredientId;
    private String ingredientName; // Nếu bạn join thêm bảng Ingredients (Tùy chọn)
    private double quantity;
    private String unit;

    public RecipeDTO(int recipeId, int ingredientId, double quantity, String unit,String IngredientName) {
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
        this.quantity = quantity;
        this.unit = unit;
        this.ingredientName = IngredientName; 
    }
    // Getter & Setter...
    public int getRecipeId() { return recipeId; }
    public int getIngredientId() { return ingredientId; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public String getIngredientName() { return ingredientName; }
}