/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.IngredientDAO;
import dao.RecipeDAO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import model.Ingredient;

/**
 *
 * @author admin
 */
public class IngredientCheckService {

    private final IngredientDAO ingredientDAO = new IngredientDAO();
    private static final RecipeDAO r = new RecipeDAO();
    private static final IngredientDAO i = new IngredientDAO();
    public HashMap<String, Object> getStockSnapshotByDate(LocalDate auditDate) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            List<HashMap<String, Object>> list = ingredientDAO.getStockSnapshotByDate(auditDate);
            if (list == null) {
                result.put("error", "Không đọc được tồn kho theo ngày.");
            } else {
                result.put("success", list);
            }
        } catch (Exception e) {
            result.put("error", e.getMessage());
            System.err.println(e.getMessage());
        }
        return result;
    }

    public HashMap<String, Object> getAllIngredients() {
        HashMap<String, Object> result = new HashMap<>();
        try {
            HashMap<Integer, Ingredient> map = ingredientDAO.getAllIngredients();
            if (map == null) {
                result.put("error", "Không đọc được danh sách nguyên liệu.");
            } else {
                result.put("success", map);
            }
        } catch (Exception e) {
            result.put("error", e.getMessage());
            System.err.println(e.getMessage());
        }
        return result;
    }
}
