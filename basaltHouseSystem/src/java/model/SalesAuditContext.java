package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalesAuditContext {

    public final HashMap<Integer, Ingredient> ingredientMap;
    public final HashMap<Integer, HashMap<Integer, List<Recipe>>> recipeMap;
    public final Map<Integer, BigDecimal> expectedByIngredient = new HashMap<>();
    public final Map<Integer, String> cupsByIngredient = new HashMap<>();
    public final Map<Integer, List<String>> usageDetailsByIngredient = new HashMap<>();
    public Map<Integer, HashMap<String, Object>> stockSnapshotByIngredient = new HashMap<>();
    public Map<Integer, BigDecimal> importedByIngredient = new HashMap<>();
    public LocalDate auditDate = LocalDate.now();
    public String dataError;
    public int totalSoldCups;
    public BigDecimal totalRevenue = BigDecimal.ZERO;
    public int missingRecipeCount;
    public int usedIngredientCount;
    public int stockWarningCount;

    public SalesAuditContext(
            HashMap<Integer, Ingredient> ingredientMap,
            HashMap<Integer, HashMap<Integer, List<Recipe>>> recipeMap) {
        this.ingredientMap = ingredientMap;
        this.recipeMap = recipeMap;
    }
}
