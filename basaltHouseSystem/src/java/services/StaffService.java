/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.ImportVoiceDAO;
import java.math.BigDecimal;
import java.util.HashMap;

/**
 *
 * @author admin
 */
public class StaffService {
    private static final ImportVoiceDAO dao = new ImportVoiceDAO();
     public HashMap<String, Object> getStaffIdByAccountId(int id) {

        HashMap<String, Object> s = new HashMap<>();
        try {
            Integer  staffId = dao.getStaffIdByAccountId(id);
              if(staffId!=null){
              s.put("success",staffId );
              
              }else{
              s.put("error", "Không tìm thấy id ");
              }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return s;
    }
}
