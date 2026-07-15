/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.ImportVoiceDAO;
import java.math.BigDecimal;
import java.util.ArrayList;
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

            List<HashMap<String, Object>> ingredients = new ArrayList<>();
            List<HashMap<String, Object>> rows = dao.getIngredientStockRowsBySupplier(supplierId);
            for (HashMap<String, Object> row : rows) {
                HashMap<String, Object> item = new HashMap<>();
                item.put("id", row.get("ingredientId"));
                item.put("name", row.get("ingredientName"));
                item.put("unit", row.get("unit"));
                item.put("stockText", formatStock(row.get("stockQuantity")));
                ingredients.add(item);
            }
            s.put("success", ingredients);
        } catch (Exception e) {
            s.put("error", "Không thể tải nguyên liệu của nhà cung cấp");
            System.err.println(e.getMessage());
        }
        return s;
    }

    private String formatStock(Object value) {
        if (value == null) {
            return "0";
        }
        BigDecimal number = value instanceof BigDecimal
                ? (BigDecimal) value : new BigDecimal(value.toString());
        return number.stripTrailingZeros().toPlainString();
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
