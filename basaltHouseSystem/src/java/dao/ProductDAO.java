/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.Product;

/**
 *
 * @author admin
 */
public class ProductDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    public HashMap<Integer, Product> getProduct() {
        HashMap<Integer, Product> productMap = new HashMap<>();
        try {
            String sql = "SELECT ProductId, ProductName,Description, ImageUrl,IsActive FROM Products WHERE IsDeleted = 0";
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("ProductId"),
                        rs.getString("ProductName"),
                        rs.getString("Description"),
                        rs.getString("ImageUrl"),
                        rs.getBoolean("IsActive")
                );
                productMap.put(p.getProductId(), p);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return productMap;
    }
    // Mục đích: Lấy danh sách sản phẩm theo dạng List (kèm giá tiền, hình ảnh, CategoryId) để thuận tiện cho việc in ra danh sách thẻ (card) món ăn trên giao diện Thu Ngân (POS).
    public List<Product> getAllProductsForPOS() {
        List<Product> list = new ArrayList<>();
        try {
            String sql = "SELECT ProductId, ProductName, CategoryId, Price, Description, ImageUrl, IsActive FROM Products WHERE IsDeleted = 0";
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setProductId(rs.getInt("ProductId"));
                p.setProductName(rs.getString("ProductName"));
                p.setCategoryId(rs.getInt("CategoryId"));
                p.setPrice(rs.getBigDecimal("Price"));
                p.setDescription(rs.getString("Description"));
                p.setImageUrl(rs.getString("ImageUrl"));
                p.setIsActive(rs.getBoolean("IsActive"));
                list.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error getAllProductsForPOS: " + e.getMessage());
        }
        return list;
    }
}
