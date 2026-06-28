package services;

import dao.RecipeDAO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.Recipe;

public class RecipeService {

    private static final RecipeDAO dao = new RecipeDAO();

    // Lấy toàn bộ công thức và gom theo ProductId -> SizeId.
    public HashMap<String, Object> getRecipeMap() {
        HashMap<String, Object> result = new HashMap<>();
        try {
            HashMap<Integer, HashMap<Integer, List<Recipe>>> map = dao.getRecipeMap();
            if (map == null) {
                result.put("error", "Danh sách công thức lỗi");
            } else {
                result.put("success", map);
            }
        } catch (Exception e) {
            result.put("error", e.getMessage());
            System.err.println(e.getMessage());
        }
        return result;
    }

    // Lấy danh sách nguyên liệu công thức theo đúng sản phẩm và size.
    public List<Recipe> getRecipes(
            HashMap<Integer, HashMap<Integer, List<Recipe>>> recipeMap,
            int productId,
            int sizeId) {
        if (recipeMap == null) {
            return new ArrayList<>();
        }

        HashMap<Integer, List<Recipe>> bySize = recipeMap.get(productId);
        if (bySize == null || bySize.get(sizeId) == null) {
            return new ArrayList<>();
        }
        return bySize.get(sizeId);
    }
}
