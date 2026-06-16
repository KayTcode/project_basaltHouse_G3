/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.CustomerProfile;

/**
 *
 * @author admin
 */
public class CustomersProfileDAO extends DBContext{
      
    PreparedStatement st;
    ResultSet rs;
    
    public CustomerProfile getCustomerById(int accountId){
              try {
            String sql = """
                        select top 1 a.AccountId , c.FullName,c. Phone,c.AvatarUrl,a.Email,a.PasswordHash   from Accounts a 
                        join Customers c on a.AccountId = c.AccountId
                        where a.AccountId =?
                        """;
            st =  connection.prepareStatement(sql);
            st.setObject(1, accountId);
            rs = st.executeQuery();
            if(rs.next()){
                 return new CustomerProfile(rs.getInt("AccountId"), 
                         rs.getString("FullName"),
                         rs.getString("Phone"),
                         rs.getString("AvatarUrl"),
                         rs.getString("Email"),
                         rs.getString("PasswordHash"));
            
            }
                  
        } catch (Exception e) {
                  System.err.println(e.getMessage());
        }
    return null;
    }
}
