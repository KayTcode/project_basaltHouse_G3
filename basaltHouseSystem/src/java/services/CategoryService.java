/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.CategoryDAO;
import java.util.HashMap;
import java.util.List;
import model.Category;

/**
 *
 * @author admin
 */
public class CategoryService {

    private static final CategoryDAO dao = new CategoryDAO();

    public HashMap<String, Object> getCategory() {
        HashMap<String, Object> s = new HashMap<>();
        try {
            List<Category> list = dao.getCategory();
            if (list == null) {
                s.put("error", "Danh sách Category lỗi");

            } else {
                s.put("success", list);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return s;

    }

    public HashMap<String, Object> getAllCategories() {
        HashMap<String, Object> s = new HashMap<>();
        try {
            List<Category> list = dao.getAllCategories();
            if (list == null) {
                s.put("error", "Danh sách Category lỗi");

            } else {
                s.put("success", list);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return s;

    }
}
