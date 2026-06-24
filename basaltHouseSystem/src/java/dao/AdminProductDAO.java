/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author MSI
 */
public class AdminProductDAO extends DBContext{
    public List<Product> getProducts(String search, String categoryId, int offset, int limit) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT p.*, c.CategoryName FROM Products p " +
            "LEFT JOIN Categories c ON p.CategoryId = c.CategoryId " +
            "WHERE p.IsDeleted = 0 "
        );

        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND p.ProductName LIKE ? ");
        }
        if (categoryId != null && !categoryId.trim().isEmpty()) {
            sql.append(" AND p.CategoryId = ? ");
        }
        sql.append(" ORDER BY p.CreatedAt DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (PreparedStatement st = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (search != null && !search.trim().isEmpty()) {
                st.setString(paramIndex++, "%" + search + "%");
            }
            if (categoryId != null && !categoryId.trim().isEmpty()) {
                st.setInt(paramIndex++, Integer.parseInt(categoryId));
            }
            st.setInt(paramIndex++, offset);
            st.setInt(paramIndex, limit);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
//                list.add(new Product(
//                    rs.getInt("ProductId"),
//                    rs.getString("ProductName"),
//                    rs.getInt("CategoryId"),
//                    rs.getString("CategoryName"),
//                    rs.getDouble("Price"),
//                    rs.getString("Description"),
//                    rs.getString("ImageUrl"),
//                    rs.getBoolean("IsActive"),
//                    rs.getTimestamp("CreatedAt")
//                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Đếm tổng số lượng sản phẩm (để phân trang & làm thẻ KPI)
    public int countTotalProducts(String search, String categoryId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Products WHERE IsDeleted = 0 ");
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND ProductName LIKE ? ");
        }
        if (categoryId != null && !categoryId.trim().isEmpty()) {
            sql.append(" AND CategoryId = ? ");
        }

        try (PreparedStatement st = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (search != null && !search.trim().isEmpty()) {
                st.setString(paramIndex++, "%" + search + "%");
            }
            if (categoryId != null && !categoryId.trim().isEmpty()) {
                st.setInt(paramIndex++, Integer.parseInt(categoryId));
            }
            ResultSet rs = st.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
