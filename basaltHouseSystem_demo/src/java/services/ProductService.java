/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.ProductDAO;
import java.util.HashMap;
import java.util.List;
import model.Product;
import model.ProductDetail;

/**
 *
 * @author admin
 */
public class ProductService {
    private static final ProductDAO dao = new ProductDAO();
    
    
    public HashMap<String , Object> getBestSellingProducts(int limit){
     HashMap<String , Object> s = new HashMap<>();
        try {
            List<Product>list = dao.getBestSellingProducts(limit);
            if(list==null){
                s.put("error", "Không tìm thấy danh sách sản phẩm");
            }else{
            s.put("success", list);
            
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return s;
    
    }
    
    public HashMap<String , Object> getProductByCategory(int categoryId){
     HashMap<String , Object> s = new HashMap<>();
        try {
            List<Product>list = dao.getProductByCategory(categoryId);
            if(list==null){
                s.put("error", "Không tìm thấy danh sách sản phẩm");
            }else{
            s.put("success", list);
            
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return s;
    
    }
    
     public HashMap<String , Object> getProductByName(String keyword){
     HashMap<String , Object> s = new HashMap<>();
        try {
            List<Product>list = dao.getProductByName( keyword);
            if(list==null){
                s.put("error", "Không tìm thấy danh sách sản phẩm");
            }else{
            s.put("success", list);
            
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return s;
    
    }
     
      public HashMap<String , Object> getProductDetailById(int id){
     HashMap<String , Object> s = new HashMap<>();
        try {
            List<ProductDetail>list = dao.getProductDetailById( id);
            if(list==null){
                s.put("error", "Không tìm thấy danh sách sản phẩm");
            }else{
            s.put("success", list);
            
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return s;
    
    }
}
