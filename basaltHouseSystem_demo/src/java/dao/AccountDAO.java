/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.Account;

/**
 *
 * @author admin
 */
public class AccountDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    public String getPassordById(int accountID) {
        try {
            String sql = """
                       select top 1 PasswordHash from Accounts
                         where AccountId = ?
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, accountID);
            rs = st.executeQuery();
            if(rs.next()){
               Account s = new Account(rs.getString("PasswordHash"));
               return s.getPasswordHash();
            
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
    
    public boolean updatePassword(int accountId ,String passNew){
        try {
            String sql = """
                         UPDATE [dbo].[Accounts]
                            SET 
                               [PasswordHash] = ?
                               
                          WHERE AccountId = ?
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, passNew);
            st.setObject(2, accountId);
            st.executeUpdate();
            return true;
            
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
   
}
