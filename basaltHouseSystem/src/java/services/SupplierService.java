/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.SupplierDAO;
import java.util.HashMap;
import java.util.List;
import model.Supplier;

/**
 *
 * @author admin
 */
public class SupplierService {
    private static final SupplierDAO dao = new SupplierDAO();
    
    public HashMap<String,Object> getSupplier(){
       HashMap<String,Object> s = new HashMap<>();
        try {
            List<Supplier>list = dao.getSupplier();
            if(list==null){
            s.put("error", "Lỗi danh sách");
            
            }else{
            s.put("success", list);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return s;
    
    }
}
