/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.Customer;

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
                        select top 1 c.CustomerId from Customers c
                        join Accounts a  on c.AccountId = a.AccountId
                        where c.AccountId = ?
                        """;
            st = connection.prepareStatement(sql);
            st.setObject(1, accountId);
            rs = st.executeQuery();
            if(rs.next()){
            Customer p = new Customer(rs.getInt("CustomerId"));
            return p.getAccountId();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            
        }
        return 0;

    }
}
