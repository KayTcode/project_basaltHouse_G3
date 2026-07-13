/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.Product;
import model.ProductDetail;

/**
 *
 * @author admin
 */
public class ProductDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    public Product getProductById(int productId) {
        try {
            String sql = """
                      SELECT ProductId, ProductName, Description, Price, ImageUrl, IsActive
                      FROM Products
                      WHERE IsDeleted = 0 AND ProductId = ?
                         """;
            st = connection.prepareStatement(sql);
            st.setInt(1, productId);
            rs = st.executeQuery();
            while (rs.next()) {
                return new Product(
                        rs.getInt("ProductId"),
                        rs.getString("ProductName"),
                        rs.getString("Description"),
                        rs.getBigDecimal("Price"),
                        rs.getString("ImageUrl"),
                        rs.getBoolean("IsActive")
                );
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public List<Product> getBestSellingProducts(int limit) {
        List<Product> list = new ArrayList<>();
        int safeLimit = Math.max(1, Math.min(limit, 5));
        try {
            String sql = """
                      SELECT p.ProductId,
                             p.CategoryId,
                             p.ProductName,
                             p.Description,
                             p.Price,
                             p.ImageUrl,
                             p.IsActive,
                             COALESCE(s.TotalSold, 0) AS TotalSold,
                             COALESCE(r.AverageRating, 0) AS AverageRating,
                             COALESCE(r.ReviewCount, 0) AS ReviewCount
                      FROM Products p
                      LEFT JOIN (
                          SELECT od.ProductId, SUM(od.Quantity) AS TotalSold
                          FROM OrderDetails od
                          LEFT JOIN Orders o
                                  ON o.OrderId = od.OrderId
                                 AND o.IsDeleted = 0
                          WHERE od.IsDeleted = 0
                            AND o.OrderId IS NOT NULL
                          GROUP BY od.ProductId
                      ) s ON s.ProductId = p.ProductId
                      LEFT JOIN (
                          SELECT ProductId,
                                 AVG(CAST(Rating AS DECIMAL(10, 2))) AS AverageRating,
                                 COUNT(*) AS ReviewCount
                          FROM Reviews
                          WHERE IsDeleted = 0
                            AND IsVisible = 1
                          GROUP BY ProductId
                      ) r ON r.ProductId = p.ProductId
                      WHERE p.IsDeleted = 0
                        AND p.IsActive = 1
                      ORDER BY COALESCE(s.TotalSold, 0) DESC,
                               COALESCE(r.AverageRating, 0) DESC,
                               COALESCE(r.ReviewCount, 0) DESC,
                               p.ProductId ASC
                         """;
            st = connection.prepareStatement(sql);
            st.setMaxRows(safeLimit);
            rs = st.executeQuery();
            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("ProductId"),
                        rs.getString("ProductName"),
                        rs.getString("Description"),
                        rs.getBigDecimal("Price"),
                        rs.getString("ImageUrl"),
                        rs.getBoolean("IsActive"),
                        rs.getInt("TotalSold"),
                        rs.getDouble("AverageRating"),
                        rs.getInt("ReviewCount")
                );
                p.setCategoryId(rs.getInt("CategoryId"));
                list.add(p);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;
    }

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

    public List<Product> getProductByCategory() {
        return getProductByCategory(1);
    }

    public List<Product> getProductByCategory(int categoryId) {
        List<Product> list = new ArrayList<>();
        try {
            String sql = """
                      SELECT ProductId, ProductName, Description, Price, ImageUrl, IsActive
                      FROM Products
                      WHERE IsDeleted = 0 AND CategoryId = ?
                         """;
            st = connection.prepareStatement(sql);
            st.setInt(1, categoryId);
            rs = st.executeQuery();
            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("ProductId"),
                        rs.getString("ProductName"),
                        rs.getString("Description"),
                        rs.getBigDecimal("Price"),
                        rs.getString("ImageUrl"),
                        rs.getBoolean("IsActive")
                );
                list.add(p);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;
    }

    public List<Product> getProductByName(String keyword) {
        List<Product> list = new ArrayList<>();
        try {
            String sql = """
                         SELECT ProductId, ProductName, Description, Price, ImageUrl, IsActive
                         FROM Products
                         WHERE IsDeleted = 0
                           AND IsActive = 1
                           AND ProductName LIKE ?
                         """;
            st = connection.prepareStatement(sql);
            st.setString(1, "%" + keyword.trim() + "%");
            rs = st.executeQuery();
            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("ProductId"),
                        rs.getString("ProductName"),
                        rs.getString("Description"),
                        rs.getBigDecimal("Price"),
                        rs.getString("ImageUrl"),
                        rs.getBoolean("IsActive")
                );
                list.add(p);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;
    }

    public List<ProductDetail> getProductDetailById(int productId) {
        List<ProductDetail> list = new ArrayList<>();
        try {
            String sql = """
                         select p.ProductId,ProductName,Description,ImageUrl,ps.Price ,s.SizeId , s.SizeName from  Products p
                         join ProductSizes ps on ps.ProductId = p.ProductId
                         join Sizes s on ps.SizeId = s.SizeId
                         where p.ProductId = ?
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, productId);
            rs = st.executeQuery();
            while (rs.next()) {
                ProductDetail p = new ProductDetail(rs.getInt("ProductId"),
                        rs.getString("ProductName"),
                        rs.getString("Description"),
                        rs.getString("ImageUrl"),
                        rs.getBigDecimal("Price"),
                        rs.getInt("SizeId"),
                        rs.getString("SizeName"));
                list.add(p);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;
    }

}
