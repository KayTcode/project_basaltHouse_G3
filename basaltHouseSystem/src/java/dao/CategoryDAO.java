/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Category;

/**
 *
 * @author admin
 */
public class CategoryDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    public List<Category> getCategory() {
        List<Category> list = new ArrayList<>();
        try {
            String sql = """
                         select CategoryId,CategoryName,ImageUrl from Categories 
                         where IsDeleted = 0
                         """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                Category c = new Category(rs.getInt("CategoryId"),
                        rs.getString("CategoryName"), rs.getString("ImageUrl"));
                list.add(c);
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;
    }
    
    
}
