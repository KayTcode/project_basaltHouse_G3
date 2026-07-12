/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.CustomersProfileDAO;
import java.util.HashMap;
import model.CustomerProfile;

/**
 *
 * @author admin
 */
public class CustomerService {

    private static final CustomersProfileDAO dao = new CustomersProfileDAO();

    public HashMap<String, Object> getCustomerById(int id) {
        HashMap<String, Object> s = new HashMap<>();
        try {
            CustomerProfile c = dao.getCustomerById(id);
            if (c == null) {
                s.put("error", "Không tìm thấy người dùng");
            } else {
                s.put("success", c);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return s;
    }
}
