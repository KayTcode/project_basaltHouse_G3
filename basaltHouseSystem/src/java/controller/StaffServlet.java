/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

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
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.ActivityLog;
import model.ImportDetail;
import model.ImportInvoice;
import model.ImportInvoicesDetail;
import services.ActivityLogService;
import static services.AuthService.USER_SESSION_KEY;
import services.ImportVoiceService;
import services.StaffService;
import services.StockService;

/**
 *
 * @author admin
 */
public class StaffServlet extends HttpServlet {

    private static final DateTimeFormatter IMPORT_CODE_FORMAT
            = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final ImportVoiceService importService = new ImportVoiceService();
    private static final StaffService staService = new StaffService();
    private static final ActivityLogService activeService = new ActivityLogService();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
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
            out.println("<title>Servlet StaffServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet StaffServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UserLoginDTO user = session != null
                ? (UserLoginDTO) session.getAttribute(USER_SESSION_KEY)
                : null;

        request.setAttribute("staffName", user != null && user.getFullName() != null
                ? user.getFullName()
                : "Staff");
        ImportVoiceDAO dao = new ImportVoiceDAO();
        List<ImportInvoicesDetail> list = dao.getImportInvoicesDetail();
        try {
            StockService stockService = new StockService();
            HashMap<String, Object> dashboardData = stockService.getStaffDashboardData();
            for (Map.Entry<String, Object> entry : dashboardData.entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            request.setAttribute("dataError", e.getMessage());
        }
        request.setAttribute("listP", list);
        request.getRequestDispatcher("views/Staff/Staff.jsp").forward(request, response);

    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
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

        String action = request.getParameter("action");
        if (!"importIngredient".equals(action)) {
            response.sendRedirect(request.getContextPath() + "/staff");
            return;
        }

        try {
            
             HashMap<String, Object> s = staService.getStaffIdByAccountId(user.getAccountId());
             int staffId = (Integer)s.get("success");
            if (s.containsKey("error")) {
                String error = s.get("error").toString();
              request.setAttribute("errorMessage", error);
            }

            String importCode = trimToNull(request.getParameter("importCode"));
            if (importCode == null) {
                importCode = "IMP-" + LocalDateTime.now().format(IMPORT_CODE_FORMAT);
            }

            String status = trimToNull(request.getParameter("status"));
            if (status == null) {
                status = "Confirmed";
            }

            int supplierId = Integer.parseInt(request.getParameter("supplierId"));
            int ingredientId = Integer.parseInt(request.getParameter("ingredientId"));
            BigDecimal orderedQuantity = parseBigDecimal(request.getParameter("orderedQuantity"));
            BigDecimal receivedQuantity = parseBigDecimal(request.getParameter("receivedQuantity"));
            BigDecimal unitPrice = parseBigDecimal(request.getParameter("unitPrice"));
            BigDecimal totalOrderedAmount = orderedQuantity.multiply(unitPrice);
            BigDecimal totalReceivedAmount = receivedQuantity.multiply(unitPrice);

            ImportInvoice invoice = new ImportInvoice(
                    0,
                    importCode,
                    supplierId,
                    staffId,
                    "Pending".equalsIgnoreCase(status) ? null : staffId,
                    status,
                    parseDateTime(request.getParameter("orderedDate")),
                    parseDateTime(request.getParameter("expectedDate")),
                    parseDateTime(request.getParameter("receivedDate")),
                    trimToNull(request.getParameter("supplierInvoiceCode")),
                    totalOrderedAmount,
                    totalReceivedAmount,
                    trimToNull(request.getParameter("note")),
                    null,
                    false
            );

            ImportDetail detail = new ImportDetail(
                    0,
                    0,
                    ingredientId,
                    orderedQuantity,
                    receivedQuantity,
                    unitPrice,
                    trimToNull(request.getParameter("discrepancyNote")),
                    trimToNull(request.getParameter("note")),
                    false
            );
            HashMap<String, Object> s1 = importService.creatImportvoice(invoice, detail);
            if (s1.containsKey("error")) {
                String error = s1.get("error").toString();
                request.setAttribute("errorMessage", error);
            }
            HashMap<String,Object>s2 = activeService.ctreatActiveLog(new ActivityLog(user.getAccountId(),
                    "Create new Import In Voice ",
                    "ImportInVoice",
                    user.getAccountId(),
                    null,
                    importCode,
                    "Success",
                    0,
                    LocalDateTime.now()));
            response.sendRedirect(request.getContextPath() + "/staff");
        } catch (Exception e) {
            request.setAttribute("errorMessage", e.getMessage());
            doGet(request, response);
        }
    }

    private LocalDateTime parseDateTime(String value) {
        String normalized = trimToNull(value);
        return normalized == null ? null : LocalDateTime.parse(normalized);
    }

    private BigDecimal parseBigDecimal(String value) {
        String normalized = trimToNull(value);
        return normalized == null ? BigDecimal.ZERO : new BigDecimal(normalized);
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
