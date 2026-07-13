/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

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
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
            response.sendRedirect(request.getContextPath() + "/staff/history");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/staff/history");
            return;
        }
        
        HashMap<String, Object> s3 = importService.getImportInvoiceDetailsById(id);
        if (s3.containsKey("error")) {
            request.setAttribute("error", s3.get("error").toString());
            request.getRequestDispatcher("views/Staff/ViewImportVoice.jsp").forward(request, response);
            return;
        }
        List<ImportInvoicesDetail> invoiceDetails =
                (List<ImportInvoicesDetail>) s3.get("success");
        ImportInvoicesDetail im = invoiceDetails.get(0);
        im.setIngredientCount(invoiceDetails.size());

        List<HashMap<String, Object>> listS = new ArrayList<>();
        HashMap<Integer, Ingredient> ingre = new HashMap<>();
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
        request.setAttribute("ingredients", new ArrayList<>(ingre.values()));
        request.setAttribute("suppliers", listS);
        request.setAttribute("invoiceDetail", im);
        request.setAttribute("invoiceDetails", invoiceDetails);
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

        try {
        int importId = Integer.parseInt(request.getParameter("importId"));
        HashMap<String, Object> oldResult = importService.getImportInvoiceDetailsById(importId);
        if (oldResult.containsKey("error")) {
            request.setAttribute("errorMessage", oldResult.get("error").toString());
            doGet(request, response);
            return;
        }
        List<ImportInvoicesDetail> oldDetails =
                (List<ImportInvoicesDetail>) oldResult.get("success");
        ImportInvoicesDetail oldInvoice = oldDetails.get(0);

        String importCode = request.getParameter("importCode");
        String supplierInvoiceCode = request.getParameter("supplierInvoiceCode");
        String status = request.getParameter("status");
        int supplierId = Integer.parseInt(request.getParameter("supplierId"));
        LocalDateTime orderedDate = parseDateTime(request.getParameter("orderedDate"));
        LocalDateTime expectedDate = parseDateTime(request.getParameter("expectedDate"));
        LocalDateTime receivedDate = parseDateTime(request.getParameter("receivedDate"));
        String note = request.getParameter("note");
        String rejectReason = trimToNull(request.getParameter("rejectReason"));
        if ("Rejected".equalsIgnoreCase(status) && rejectReason == null) {
            throw new IllegalArgumentException("Vui lòng nhập lý do từ chối phiếu hàng.");
        }
        if (!"Rejected".equalsIgnoreCase(status)) {
            rejectReason = null;
        }
        String[] importDetailIds = request.getParameterValues("importDetailId");
        String[] ingredientIds = request.getParameterValues("ingredientId");
        String[] orderedQuantities = request.getParameterValues("orderedQuantity");
        String[] receivedQuantities = request.getParameterValues("receivedQuantity");
        String[] unitPrices = request.getParameterValues("unitPrice");
        String[] discrepancyNotes = request.getParameterValues("discrepancyNote");
        String[] detailNotes = request.getParameterValues("detailNote");
        validateDetailParameterCounts(importDetailIds, ingredientIds, orderedQuantities,
                receivedQuantities, unitPrices, discrepancyNotes, detailNotes);
   

        HashMap<Integer, ImportInvoicesDetail> oldByDetailId = new HashMap<>();
        for (ImportInvoicesDetail oldDetail : oldDetails) {
            oldByDetailId.put(oldDetail.getImportDetailId(), oldDetail);
        }

        List<ImportDetail> details = new ArrayList<>();
        Set<Integer> uniqueIngredientIds = new HashSet<>();
        Set<Integer> uniqueDetailIds = new HashSet<>();
        BigDecimal totalOrderedAmount = BigDecimal.ZERO;
        BigDecimal totalReceivedAmount = BigDecimal.ZERO;
        for (int index = 0; index < importDetailIds.length; index++) {
            int importDetailId = Integer.parseInt(importDetailIds[index]);
            int ingredientId = Integer.parseInt(ingredientIds[index]);
            if (!oldByDetailId.containsKey(importDetailId) || !uniqueDetailIds.add(importDetailId)) {
                throw new IllegalArgumentException("Chi tiết phiếu nhập không hợp lệ.");
            }
            if (!uniqueIngredientIds.add(ingredientId)) {
                throw new IllegalArgumentException("Mỗi nguyên liệu chỉ được xuất hiện một lần trong phiếu nhập.");
            }

            BigDecimal orderedQuantity = parseBigDecimal(orderedQuantities[index]);
            BigDecimal receivedQuantity = parseBigDecimal(receivedQuantities[index]);
            BigDecimal unitPrice = parseBigDecimal(unitPrices[index]);
            if (orderedQuantity.compareTo(BigDecimal.ZERO) <= 0
                    || receivedQuantity.compareTo(BigDecimal.ZERO) < 0
                    || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Số lượng và đơn giá không hợp lệ.");
            }

            totalOrderedAmount = totalOrderedAmount.add(orderedQuantity.multiply(unitPrice));
            totalReceivedAmount = totalReceivedAmount.add(receivedQuantity.multiply(unitPrice));
            details.add(new ImportDetail(
                    importDetailId,
                    importId,
                    ingredientId,
                    orderedQuantity,
                    receivedQuantity,
                    unitPrice,
                    discrepancyNotes[index],
                    detailNotes[index],
                    false
            ));
        }

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
         HashMap<String, Object> s = importService.updateImportInVoice(v, details);
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
         appendChange(oldValue, newValue, "totalOrderedAmount",
                 oldInvoice.getTotalOrderedAmount(), totalOrderedAmount);
         appendChange(oldValue, newValue, "totalReceivedAmount",
                 oldInvoice.getTotalReceivedAmount(), totalReceivedAmount);
         for (ImportDetail detail : details) {
             ImportInvoicesDetail oldDetail = oldByDetailId.get(detail.getImportDetailId());
             String prefix = "detail[" + detail.getImportDetailId() + "].";
             appendChange(oldValue, newValue, prefix + "ingredientId",
                     oldDetail.getIngredientId(), detail.getIngredientId());
             appendChange(oldValue, newValue, prefix + "orderedQuantity",
                     oldDetail.getOrderedQuantity(), detail.getOrderedQuantity());
             appendChange(oldValue, newValue, prefix + "receivedQuantity",
                     oldDetail.getReceivedQuantity(), detail.getReceivedQuantity());
             appendChange(oldValue, newValue, prefix + "unitPrice",
                     oldDetail.getUnitPrice(), detail.getUnitPrice());
             appendChange(oldValue, newValue, prefix + "discrepancyNote",
                     oldDetail.getDiscrepancyNote(), detail.getDiscrepancyNote());
             appendChange(oldValue, newValue, prefix + "detailNote",
                     oldDetail.getDetailNote(), detail.getNote());
         }

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
        } catch (Exception e) {
            request.setAttribute("errorMessage", e.getMessage());
            doGet(request, response);
        }
        
    }

    private void validateDetailParameterCounts(String[]... values) {
        if (values.length == 0 || values[0] == null || values[0].length == 0) {
            throw new IllegalArgumentException("Phiếu nhập phải có ít nhất một nguyên liệu.");
        }
        int detailCount = values[0].length;
        for (String[] value : values) {
            if (value == null || value.length != detailCount) {
                throw new IllegalArgumentException("Dữ liệu chi tiết phiếu nhập không hợp lệ.");
            }
        }
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(value.trim());
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
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
