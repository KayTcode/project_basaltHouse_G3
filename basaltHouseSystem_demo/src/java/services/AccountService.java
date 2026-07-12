/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.AccountDAO;
import java.util.HashMap;

/**
 *
 * @author admin
 */
public class AccountService {

    private static final AccountDAO dao = new AccountDAO();

    public HashMap<String, Object> updatePassword(int accountId ,String passNew) {
        HashMap<String, Object> s = new HashMap<>();
        try {
            boolean exits = dao.updatePassword(accountId, passNew);
            if (!exits) {
                s.put("error", "Cập nhật thất bại");
            } else {
                s.put("success", true);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return s;
    }
    
    public HashMap<String, Object> getPassordById(int accountID) {
        HashMap<String, Object> s = new HashMap<>();
        try {
            String pass = dao.getPassordById(accountID);
            if (pass==null) {
                s.put("error", "Không tìm thấy ngời dùng");
            } else {
                s.put("success", pass);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return s;
    }
}
