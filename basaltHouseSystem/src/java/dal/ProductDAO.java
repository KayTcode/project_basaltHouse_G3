/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import model.Product;

/**
 *
 * @author admin
 */
public class ProductDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    public HashMap<Integer, Product> getProductWithImage() {
        HashMap<Integer, Product> productMap = new HashMap<>();
        try {
            String sql = "SELECT ProductId, ProductName,Description, ImageUrl FROM Products WHERE IsDeleted = 0";
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("ProductId"),
                        rs.getString("ProductName"),
                        rs.getString("Description"),
                        rs.getString("ImageUrl")
                );
                productMap.put(p.getProductId(), p);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return productMap;
    }
}
