/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Category;

// Mục đích: Lọc danh sách Danh mục (Coffee, Tea, Cake...) từ Database để hiển thị Menu động trên màn hình Thu ngân (POSOrders.jsp).
public class CategoryDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        try {
            String sql = "SELECT CategoryId, CategoryName, Description, IsDeleted FROM Categories WHERE IsDeleted = 0";
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                list.add(mapCategory(rs));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return list;
    }

    public List<Category> getAllCategoriesForPOS() {
        List<Category> list = new ArrayList<>();
        try {
            String sql = "SELECT DISTINCT c.CategoryId, c.CategoryName, c.Description, c.IsDeleted "
                       + "FROM Categories c "
                       + "JOIN Products p ON c.CategoryId = p.CategoryId "
                       + "WHERE c.IsDeleted = 0 AND p.IsDeleted = 0 AND p.IsActive = 1";
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                list.add(mapCategory(rs));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return list;
    }
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

    private Category mapCategory(ResultSet result) throws SQLException {
        return new Category(
                result.getInt("CategoryId"),
                result.getString("CategoryName"),
                result.getString("Description"),
                result.getBoolean("IsDeleted"));
    }
}
