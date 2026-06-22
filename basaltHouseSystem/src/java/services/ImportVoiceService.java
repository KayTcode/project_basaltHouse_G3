/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.ImportVoiceDAO;
import java.math.BigDecimal;
import java.util.HashMap;
import model.ImportDetail;
import model.ImportInvoice;
import model.ImportInvoicesDetail;
import model.Ingredient;

/**
 *
 * @author admin
 */
public class ImportVoiceService {

    private static final ImportVoiceDAO dao = new ImportVoiceDAO();

    public HashMap<String, Object> creatImportvoice(ImportInvoice v, ImportDetail detail) {
        HashMap<String, Object> s = new HashMap<>();
        try {
            boolean exits = dao.inseartImportInvoices(v, detail);
            if (exits) {
                s.put("Success", true);
            } else {
                s.put("error", "Có lỗi xảy ra khi tạo đơn");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return s;

    }
    
    public HashMap<String, Object> updateImportInVoce(ImportInvoice v, ImportDetail detail) {
        HashMap<String, Object> s = new HashMap<>();
        try {
            boolean exits = dao.updateImportVoice(v);
            if (exits) {
                s.put("Success", true);
            } else {
                s.put("error", "Cập nhật không thành công");
            }
            boolean exits2 = dao.updateImportVoiceDetail(detail);
             if (exits) {
                s.put("Success", true);
            } else {
                s.put("error", "Cập nhật không thành công");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return s;

    }
  public HashMap<String, Object> getImportInvoicesDetailById(int id){
      HashMap<String, Object> s = new HashMap<>();
      try {
          ImportInvoicesDetail  i = dao.getImportInvoicesDetailById(id);
          if(i==null){
              s.put("error", "Không tìm thấy Import Voice");
          
          }else{
              s.put("success", i);
          
          }
      } catch (Exception e) {
          System.err.println(e.getMessage());
      }
        return s;
  
  }
  
  
   
   
}
