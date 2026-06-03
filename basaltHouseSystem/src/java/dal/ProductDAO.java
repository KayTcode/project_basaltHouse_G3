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
    public HashMap<Integer, String> getProduct(){
        HashMap<Integer, String> product = new HashMap<>();
          try {
            String sql = """
                         select ProductId , ProductName from Products
                         """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
              while (rs.next()) {                  
                  Product ps = new Product(rs.getInt("ProductId"),
                          rs.getString("ProductName"));
                  product.put(ps.getProductId(), ps.getProductName());
              }
        } catch (Exception e) {
              System.err.println(e.getMessage());
        }
          return product;
    }
}
