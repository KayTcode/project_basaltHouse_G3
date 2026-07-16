/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.ImportInvoice;
import services.ActivityLogService;
import services.ImportVoiceService;
import services.StockService;

/**
 *
 * @author ADMIN
 */
public class AdminInventoryServlet extends HttpServlet {
   
    private static final DateTimeFormatter IMPORT_CODE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private final StockService stockService = new StockService();
    private final ImportVoiceService importService = new ImportVoiceService();
    private static final ActivityLogService activityService = new ActivityLogService();

    private <T> void paginateList(HttpServletRequest request, List<T> list, int limit, String pageParam, 
            String listAttr, String pageAttr, String totalPagesAttr, String totalItemsAttr) {
        int pageNum = 1;
        String ps = request.getParameter(pageParam);
        if (ps != null && !ps.isEmpty()) {
            try { pageNum = Integer.parseInt(ps); } catch (Exception ignored) {}
        }
        
        if (pageNum < 1) pageNum = 1;
        int total = list.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) total / limit));
        if (pageNum > totalPages) pageNum = totalPages;
        int from = (pageNum - 1) * limit;
        int to   = Math.min(from + limit, total);

        request.setAttribute(listAttr, list.subList(from, to));
        request.setAttribute(totalPagesAttr, totalPages);
        request.setAttribute(pageAttr, pageNum);
        if (totalItemsAttr != null) {
            request.setAttribute(totalItemsAttr, total);
        }
    }
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet AdminInventoryServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AdminInventoryServlet at " + request.getContextPath () + "</h1>");
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
        try {
            String key = request.getParameter("search");
            request.setAttribute("key", key);
            HashMap<String, Object> dashboardData = stockService.getStaffDashboardData(key, true);
            for (Map.Entry<String, Object> entry : dashboardData.entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }
            
           
            List<?> ingredients = (List<?>) dashboardData.get("ingredients");
            if (ingredients != null) {
                paginateList(request, ingredients, 5, "pageIngredient", "ingredients", "currentIngredientPage", "totalIngredientPages", null);
            }

           
            List<Map<String, Object>> todayUsage = new dao.IngredientDAO().getTodayIngredientUsage();
            paginateList(request, todayUsage, 5, "pageUsage", "todayUsageList", "currentUsagePage", "totalUsagePages", null);


            
            HashMap<String, Object> importResult = importService.getImportInvoicesDetail(null);
            List<ImportInvoice> importList = (List<ImportInvoice>) importResult.getOrDefault("success", new ArrayList<>());
            paginateList(request, importList, 5, "pageImport", "importList", "currentImportPage", "totalImportPages", null);


        } catch (Exception e) {
            request.setAttribute("dataError", e.getMessage());
        }
        request.getRequestDispatcher("/views/admin/admin_inventory.jsp").forward(request, response);
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
        processRequest(request, response);
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
