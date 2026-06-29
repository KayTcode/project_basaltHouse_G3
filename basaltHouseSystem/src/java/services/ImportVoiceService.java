/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.ImportVoiceDAO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.ImportDetail;
import model.ImportInvoice;
import model.ImportInvoicesDetail;

/**
 *
 * @author admin
 */
public class ImportVoiceService {

    private static final ImportVoiceDAO dao = new ImportVoiceDAO();

    public HashMap<String, Object> creatImportvoice(ImportInvoice v, List<ImportDetail> details) {
        HashMap<String, Object> s = new HashMap<>();
        try {
            boolean exits = dao.inseartImportInvoices(v, details);
            if (exits) {
                s.put("Success", true);
            } else {
                s.put("error", "Có lỗi xảy ra khi tạo đơn");
            }
        } catch (Exception e) {
            s.put("error", e.getMessage());
            System.err.println(e.getMessage());
        }
        return s;

    }
    
    public HashMap<String, Object> updateImportInVoice(ImportInvoice invoice, List<ImportDetail> details) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            if (dao.updateImportVoice(invoice, details)) {
                result.put("Success", true);
            } else {
                result.put("error", "Cập nhật phiếu nhập không thành công.");
            }
        } catch (Exception e) {
            result.put("error", e.getMessage());
            System.err.println(e.getMessage());
        }
        return result;
    }
  public HashMap<String, Object> getImportInvoiceDetailsById(int id) {
      HashMap<String, Object> result = new HashMap<>();
      try {
          List<ImportInvoicesDetail> details = dao.getImportInvoiceDetailsById(id);
          if (details == null || details.isEmpty()) {
              result.put("error", "Không tìm thấy phiếu nhập.");
          } else {
              result.put("success", details);
          }
      } catch (Exception e) {
          result.put("error", e.getMessage());
          System.err.println(e.getMessage());
      }
      return result;
  }
  
  public HashMap<String , Object> getImportInvoicesDetail(String key){
   HashMap<String,Object>s = new HashMap<>();
      try {
          List<ImportInvoicesDetail> list = new ArrayList<>();
          if(key == null || key.trim().isEmpty()){
             list = dao.getImportInvoicesDetail();
          }else{
            list = dao.getImportInvoicesDetail(key);
          }
          if(list==null){
              s.put("error", "Danh sách lỗi");
          }else{
              s.put("success", groupInvoices(list));
          }
      } catch (Exception e) {
          s.put("error", e.getMessage());
          System.err.println(e.getMessage());
      }
   
  return s;
  
  }

  private List<ImportInvoicesDetail> groupInvoices(List<ImportInvoicesDetail> details) {
      Map<Integer, ImportInvoicesDetail> grouped = new LinkedHashMap<>();
      for (ImportInvoicesDetail detail : details) {
          ImportInvoicesDetail invoice = grouped.get(detail.getImportId());
          if (invoice == null) {
              detail.setIngredientCount(1);
              grouped.put(detail.getImportId(), detail);
              continue;
          }

          invoice.setIngredientCount(invoice.getIngredientCount() + 1);
          invoice.setIngredientName(invoice.getIngredientName() + ", " + detail.getIngredientName());
          invoice.setOrderedQuantity(add(invoice.getOrderedQuantity(), detail.getOrderedQuantity()));
          invoice.setReceivedQuantity(add(invoice.getReceivedQuantity(), detail.getReceivedQuantity()));
      }
      return new ArrayList<>(grouped.values());
  }

  private java.math.BigDecimal add(java.math.BigDecimal left, java.math.BigDecimal right) {
      java.math.BigDecimal safeLeft = left == null ? java.math.BigDecimal.ZERO : left;
      java.math.BigDecimal safeRight = right == null ? java.math.BigDecimal.ZERO : right;
      return safeLeft.add(safeRight);
  }
  
   public HashMap<String , Object> getSupplierOptions(){
   HashMap<String,Object>s = new HashMap<>();
      try {
          List<HashMap<String, Object>> list = dao.getSupplierOptions();
          if(list==null){
              s.put("error", "Danh sách lỗi");
          }else{
          
          s.put("success", list);
          }
      } catch (Exception e) {
          s.put("error", e.getMessage());
          System.err.println(e.getMessage());
      }
   
  return s;
  
  }
   
   
}
