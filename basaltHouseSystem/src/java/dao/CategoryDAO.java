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
                Category c = new Category(
                        rs.getInt("CategoryId"),
                        rs.getString("CategoryName"),
                        rs.getString("Description"),
                        rs.getBoolean("IsDeleted")
                );
                list.add(c);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return list;
    }
}
