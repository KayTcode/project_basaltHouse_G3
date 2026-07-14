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

    private final ActiveLogDAO dao = new ActiveLogDAO();

    public HashMap<String, Object> ctreatActiveLog(ActivityLog activityLog) {
        HashMap<String, Object> result = new HashMap<>();

        if (activityLog == null) {
            result.put("error", "Activity Log không hợp lệ");
            return result;
        }

      
        try {
            if (dao.ctreatActiveLog(activityLog)) {
                result.put("success", true);
            } else {
                result.put("error", "Tạo Activity Log không thành công");
            }
        } catch (Exception e) {
            System.err.println("Activity Log insert failed: " + e.getMessage());
            result.put("error", "Tạo Activity Log không thành công");
        }

        return result;
    }
}
