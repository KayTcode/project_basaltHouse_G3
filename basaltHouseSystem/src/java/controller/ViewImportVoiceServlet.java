/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dao.IngredientDAO;
import dao.ImportVoiceDAO;
import dto.UserLoginDTO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import model.ActivityLog;
import model.ImportDetail;
import model.ImportInvoice;
import model.ImportInvoicesDetail;
import model.Ingredient;
import services.ActivityLogService;
import static services.AuthService.USER_SESSION_KEY;
import services.ImportVoiceService;
import services.IngredientCheckService;

/**
 *
 * @author admin
 */
public class ViewImportVoiceServlet extends HttpServlet {
   private static final IngredientCheckService iService = new IngredientCheckService();
   private static final ImportVoiceService importService = new ImportVoiceService();
   private static final ActivityLogService activeService = new ActivityLogService();
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ViewImportVoiceServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ViewImportVoiceServlet at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            idParam = request.getParameter("importId");
        }
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/staff");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/staff");
            return;
        }
        
        List<HashMap<String, Object>> listS = null;
        HashMap<Integer, Ingredient>ingre = null;
        HashMap<String , Object>s1 = importService.getSupplierOptions();
        if(s1.containsKey("error")){
        request.setAttribute("error", s1.get("error").toString());
        }else{
          listS = ( List<HashMap<String, Object>> )s1.get("success");
        }
        HashMap<String,Object> s2 = iService.getAllIngredients();
         if(s2.containsKey("error")){
          request.setAttribute("error", s2.get("error").toString());
        
        }else{
          ingre = (HashMap<Integer, Ingredient>)s2.get("success");
        }
        ImportInvoicesDetail im = null;
        HashMap<String,Object> s3 = importService.getImportInvoicesDetailById(id);
        if(s3.containsKey("error")){
        request.setAttribute("error", s3.get("error").toString());
        
        }else{
        im = (ImportInvoicesDetail) s3.get("success");
        }
        request.setAttribute("ingredients", new ArrayList<>(ingre.values()));
        request.setAttribute("suppliers", listS);
        request.setAttribute("invoiceDetail", im);
        request.getRequestDispatcher("views/Staff/ViewImportVoice.jsp").forward(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
           HttpSession session = request.getSession(false);
        UserLoginDTO user = session != null
                ? (UserLoginDTO) session.getAttribute(USER_SESSION_KEY)
                : null;
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int importId = Integer.parseInt(request.getParameter("importId"));
        HashMap<String, Object> oldResult = importService.getImportInvoicesDetailById(importId);
        if (oldResult.containsKey("error")) {
            request.setAttribute("errorMessage", oldResult.get("error").toString());
            doGet(request, response);
            return;
        }
        ImportInvoicesDetail oldInvoice = (ImportInvoicesDetail) oldResult.get("success");

        String importCode = request.getParameter("importCode");
        String supplierInvoiceCode = request.getParameter("supplierInvoiceCode");
        String status = request.getParameter("status");
        int supplierId = Integer.parseInt(request.getParameter("supplierId"));
        LocalDateTime orderedDate = parseDateTime(request.getParameter("orderedDate"));
        LocalDateTime expectedDate = parseDateTime(request.getParameter("expectedDate"));
        LocalDateTime receivedDate = parseDateTime(request.getParameter("receivedDate"));
        String note = request.getParameter("note");
        String rejectReason = request.getParameter("rejectReason");
        int ingredientId = Integer.parseInt(request.getParameter("ingredientId"));
        BigDecimal orderedQuantityInput = parseBigDecimal(request.getParameter("orderedQuantity"));
        BigDecimal receivedQuantityInput = parseBigDecimal(request.getParameter("receivedQuantity"));
        BigDecimal unitPriceInput = parseBigDecimal(request.getParameter("unitPrice"));
        BigDecimal totalOrderedAmount = orderedQuantityInput.multiply(unitPriceInput);
        BigDecimal totalReceivedAmount = receivedQuantityInput.multiply(unitPriceInput);
        String discrepancyNote = request.getParameter("discrepancyNote");
        String detailNote= request.getParameter("detailNote");
        ImportInvoice v = new ImportInvoice(importId, 
                importCode, 
                supplierId, 
                status, 
                orderedDate, 
                expectedDate, 
                receivedDate, 
                supplierInvoiceCode, 
                totalOrderedAmount, 
                totalReceivedAmount, 
                note, 
                rejectReason);
        ImportDetail detail = new ImportDetail(importId,
                ingredientId, 
                orderedQuantityInput, 
                receivedQuantityInput, 
                unitPriceInput,
                discrepancyNote,
                detailNote);
         HashMap<String, Object> s = importService.updateImportInVoce(v,detail);
         if (s.containsKey("error")) {
             request.setAttribute("errorMessage", s.get("error").toString());
             doGet(request, response);
             return;
         }
         StringBuilder oldValue = new StringBuilder();
         StringBuilder newValue = new StringBuilder();
         appendChange(oldValue, newValue, "importCode", oldInvoice.getImportCode(), importCode);
         appendChange(oldValue, newValue, "supplierId", oldInvoice.getSupplierId(), supplierId);
         appendChange(oldValue, newValue, "supplierInvoiceCode", oldInvoice.getSupplierInvoiceCode(), supplierInvoiceCode);
         appendChange(oldValue, newValue, "status", oldInvoice.getStatus(), status);
         appendChange(oldValue, newValue, "orderedDate", oldInvoice.getOrderedDate(), orderedDate);
         appendChange(oldValue, newValue, "expectedDate", oldInvoice.getExpectedDate(), expectedDate);
         appendChange(oldValue, newValue, "receivedDate", oldInvoice.getReceivedDate(), receivedDate);
         appendChange(oldValue, newValue, "note", oldInvoice.getInvoiceNote(), note);
         appendChange(oldValue, newValue, "rejectReason", oldInvoice.getRejectReason(), rejectReason);
         appendChange(oldValue, newValue, "ingredientId", oldInvoice.getIngredientId(), ingredientId);
         appendChange(oldValue, newValue, "orderedQuantity", oldInvoice.getOrderedQuantity(), orderedQuantityInput);
         appendChange(oldValue, newValue, "receivedQuantity", oldInvoice.getReceivedQuantity(), receivedQuantityInput);
         appendChange(oldValue, newValue, "unitPrice", oldInvoice.getUnitPrice(), unitPriceInput);
         appendChange(oldValue, newValue, "discrepancyNote", oldInvoice.getDiscrepancyNote(), discrepancyNote);
         appendChange(oldValue, newValue, "detailNote", oldInvoice.getDetailNote(), detailNote);

         if (oldValue.length() > 0) {
             HashMap<String,Object>s2 = activeService.ctreatActiveLog(new ActivityLog(user.getAccountId(),
                    "Update Inport in voice  ",
                    "ImportInVoice",
                    importId,
                    oldValue.toString(),
                    newValue.toString(),
                    "Success",
                    0,
                    LocalDateTime.now()));
             if (s2.containsKey("error")) {
                 request.setAttribute("errorMessage", s2.get("error").toString());
                 doGet(request, response);
                 return;
             }
         }
         response.sendRedirect(request.getContextPath() + "/viewimportvoice?id=" + importId);
        
        
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(value.trim());
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value.trim());
    }

    private void appendChange(StringBuilder oldValue, StringBuilder newValue, String field, Object oldData, Object newData) {
        if (oldData instanceof BigDecimal || newData instanceof BigDecimal) {
            BigDecimal oldNumber = oldData == null ? null : (BigDecimal) oldData;
            BigDecimal newNumber = newData == null ? null : (BigDecimal) newData;
            if (sameNumber(oldNumber, newNumber)) {
                return;
            }
        } else if (Objects.equals(normalize(oldData), normalize(newData))) {
            return;
        }

        oldValue.append(field).append("=").append(formatLogValue(oldData)).append("; ");
        newValue.append(field).append("=").append(formatLogValue(newData)).append("; ");
    }

    private boolean sameNumber(BigDecimal oldNumber, BigDecimal newNumber) {
        if (oldNumber == null && newNumber == null) {
            return true;
        }
        if (oldNumber == null || newNumber == null) {
            return false;
        }
        return oldNumber.compareTo(newNumber) == 0;
    }

    private Object normalize(Object value) {
        if (value instanceof String) {
            String text = ((String) value).trim();
            return text.isEmpty() ? null : text;
        }
        return value;
    }

    private String formatLogValue(Object value) {
        Object normalized = normalize(value);
        if (normalized == null) {
            return "null";
        }
        if (normalized instanceof BigDecimal) {
            return ((BigDecimal) normalized).stripTrailingZeros().toPlainString();
        }
        return normalized.toString();
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
