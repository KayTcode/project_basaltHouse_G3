/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.IngredientDAO;
import dto.IngredientStockSnapshotDTO;
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

    public HashMap<String, Object> getStockSnapshotByDate(LocalDate auditDate) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            List<IngredientStockSnapshotDTO> list
                    = ingredientDAO.getStockSnapshotByDate(auditDate);
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
