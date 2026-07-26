/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author admin
 */
public class CustomerDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    public int getCustomerId(int accountId) {
        try {
            String sql = """
                         SELECT CustomerId
                         FROM Customers
                         WHERE AccountId = ?
                           AND IsDeleted = 0
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, accountId);
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt("CustomerId");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            
        }
        return 0;

    }
    public int getAccountIdByCustomerId(int customerId) {
        try {
            String sql = "SELECT AccountId FROM Customers WHERE CustomerId = ? AND IsDeleted = 0";
            st = connection.prepareStatement(sql);
            st.setObject(1, customerId);
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt("AccountId");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return 0;
    }
}
