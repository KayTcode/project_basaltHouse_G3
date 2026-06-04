/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Order;
import model.OrderDetail;

/**
 *
 * @author admin
 */
public class OrderDAO extends DBContext{
    PreparedStatement st;
    ResultSet rs;
    
    public Order getOrderById(int orderId) {
        try {
            String sql = """
                         SELECT OrderId, OrderStatus
                         FROM Orders
                         WHERE OrderId = ? AND IsDeleted = 0
                         """;
            st = connection.prepareStatement(sql);
            st.setInt(1, orderId);
            rs = st.executeQuery();
            if (rs.next()) {
                return new Order(
                    rs.getInt("OrderId"),
                    rs.getString("OrderStatus")
                );
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
    
     public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {
        List<OrderDetail> details = new ArrayList<>();
        try {
            String sql = """
                         SELECT OrderDetailId, OrderId, ProductId, SizeId, Quantity
                         FROM OrderDetails
                         WHERE OrderId = ? AND IsDeleted = 0
                         """;
            st = connection.prepareStatement(sql);
            st.setInt(1, orderId);
            rs = st.executeQuery();
            while (rs.next()) {
                details.add(new OrderDetail(
                    rs.getInt("OrderDetailId"),
                    rs.getInt("OrderId"),
                    rs.getInt("ProductId"),
                    rs.getInt("SizeId"),
                    rs.getInt("Quantity")
                ));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return details;
    }

}
