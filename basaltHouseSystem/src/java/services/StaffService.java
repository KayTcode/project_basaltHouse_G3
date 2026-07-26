/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.ImportVoiceDAO;
import dto.IngredientStockDTO;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author admin
 */
public class StaffService {
    private static final ImportVoiceDAO dao = new ImportVoiceDAO();

    public HashMap<String, Object> getIngredientsBySupplier(int supplierId) {
        HashMap<String, Object> s = new HashMap<>();
        try {
            if (supplierId <= 0) {
                s.put("error", "Nhà cung cấp không hợp lệ");
                return s;
            }

            List<IngredientStockDTO> ingredients
                    = dao.getIngredientStockRowsBySupplier(supplierId);
            for (IngredientStockDTO ingredient : ingredients) {
                ingredient.setStockText(formatStock(ingredient.getStockQuantity()));
            }
            s.put("success", ingredients);
        } catch (Exception e) {
            s.put("error", "Không thể tải nguyên liệu của nhà cung cấp");
            System.err.println(e.getMessage());
        }
        return s;
    }

    private String formatStock(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        return value.stripTrailingZeros().toPlainString();
    }

     public HashMap<String, Object> getStaffIdByAccountId(int id) {

        HashMap<String, Object> s = new HashMap<>();
        try {
            Integer  staffId = dao.getStaffIdByAccountId(id);
              if(staffId != null && staffId > 0){
              s.put("success",staffId );
              
              }else{
              s.put("error", "Không tìm thấy id ");
              }
        } catch (Exception e) {
            s.put("error", e.getMessage());
            System.err.println(e.getMessage());
        }
        return s;
    }
}
