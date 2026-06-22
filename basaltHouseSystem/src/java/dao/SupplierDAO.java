/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Supplier;

/**
 *
 * @author admin
 */
public class SupplierDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    public List<Supplier> getSupplier(){
        List<Supplier> list = new ArrayList<>();
        try {
            String sql =  """
                          select SupplierId ,SupplierName from Suppliers
                          """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while(rs.next()){
                   Supplier s = new Supplier(rs.getInt("SupplierId"), 
                           rs.getString("SupplierName"));
                   list.add(s);
            
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
     
    return list;
    }
    
}
