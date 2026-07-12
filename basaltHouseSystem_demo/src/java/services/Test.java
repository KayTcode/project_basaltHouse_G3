/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.ProductDAO;
import java.util.List;
import model.Product;

/**
 *
 * @author admin
 */
public class Test {
    public static void main(String[] args) {
        ProductDAO d = new ProductDAO();
        List<Product>list = d.getProductByCategory();
        for (Product product : list) {
            System.out.println(product);
        }
    }
}
