/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.ActiveLogDAO;
import java.util.HashMap;
import model.ActivityLog;

/**
 *
 * @author admin
 */
public class ActivityLogService {
    private static final ActiveLogDAO dao = new ActiveLogDAO();
    
    public HashMap<String,Object> ctreatActiveLog(ActivityLog a){
        HashMap<String,Object> s = new HashMap<>();
        try {
            boolean exits = dao.ctreatActiveLog(a);
            if(exits){
               s.put("success", true);
            }else{
               s.put("error", "Tạo Activity Log không thành công");
            }
            
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return s;
              
    }
}
