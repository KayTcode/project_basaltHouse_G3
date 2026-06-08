/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.OrderDAO;
import dao.ProductDAO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.Order;
import model.OrderDetail;
import model.Product;

/**
 *
 * @author admin
 */
public class OrderValidationService {

     public List<String> validate(int orderId) {
        List<String> errors = new ArrayList<>();
        OrderDAO o = new OrderDAO();
        ProductDAO p = new ProductDAO();

        Order order = o.getOrderById(orderId);
        if (order == null) {
            errors.add("order id=" + orderId + " does not exist");
            return errors;
        }

        if (!"Pending".equals(order.getOrderStatus())) {
            errors.add("order id=" + orderId
                    + " invalid status: " + order.getOrderStatus());
            return errors;
        }

        List<OrderDetail> details = o.getOrderDetailsByOrderId(orderId);
        if (details.isEmpty()) {
            errors.add("order id=" + orderId + " no products");
            return errors;
        }
        HashMap<Integer, Product> productMap = p.getProduct();
        for (OrderDetail detail : details) {

            Product product = productMap.get(detail.getProductId());
            if (product == null) {
                errors.add("product id=" + detail.getProductId() + " does not exist");
                continue;
            }
            if (detail.getQuantity() <= 0) {
                errors.add("product '" + product.getProductName()
                        + "' invalid quantity: " + detail.getQuantity());
                continue;
            }
            if (!product.isIsActive()) {
                errors.add("product '" + product.getProductName()
                        + "' currently not available for sale");
            }
        }

        return errors;
    }
}
