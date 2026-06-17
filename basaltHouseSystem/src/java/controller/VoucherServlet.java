/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.CustomerCodeDAO;
import dao.DiscountCodeDAO;
import dto.UserLoginDTO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.CustomerDiscountCode;
import model.DiscountCode;
import services.AuthService;

/**
 *
 * @author admin
 */
public class VoucherServlet extends HttpServlet {

    private static final AuthService auth = new AuthService();

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
            out.println("<title>Servlet VoucherServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet VoucherServlet at " + request.getContextPath() + "</h1>");
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
        UserLoginDTO user = (UserLoginDTO) session.getAttribute(auth.USER_SESSION_KEY);
        CustomerCodeDAO dao = new CustomerCodeDAO();
        List<CustomerDiscountCode> list = dao.getCustomerCode(user.getAccountId());
        LocalDateTime now = LocalDateTime.now();
        Map<Integer, String> voucherStatus = new HashMap<>();
        Map<Integer, String> voucherStatusText = new HashMap<>();
        Map<Integer, String> voucherStatusClass = new HashMap<>();

        for (CustomerDiscountCode item : list) {
            String status = "available";
            String statusText = "Khả dụng";
            String statusClass = "";

            if (item.getEndDate() != null) {
                long daysLeft = ChronoUnit.DAYS.between(now.toLocalDate(), item.getEndDate().toLocalDate());

                if (daysLeft < 0) {
                    status = "expired";
                    statusText = "Hết hạn";
                    statusClass = "voucher-status--expired";
                } else if (daysLeft <= 7) {
                    status = "expiring";
                    statusText = "Sắp hết hạn";
                    statusClass = "voucher-status--warning";
                }
            }

            voucherStatus.put(item.getCustomerDiscountId(), status);
            voucherStatusText.put(item.getCustomerDiscountId(), statusText);
            voucherStatusClass.put(item.getCustomerDiscountId(), statusClass);
        }
        if (list != null) {
            for (CustomerDiscountCode c : list) {
                request.setAttribute("customerDiscountId", c.getCustomerDiscountId());
                request.setAttribute("endDateFormatted", c.getEndDateFormatted());
                request.setAttribute("discountPercentFormatted", c.getDiscountPercentFormatted());
                request.setAttribute("dayTotal", c.getDayTotal());
            }
        }

        request.setAttribute("listP", list);
        request.setAttribute("voucherStatus", voucherStatus);
        request.setAttribute("voucherStatusText", voucherStatusText);
        request.setAttribute("voucherStatusClass", voucherStatusClass);
        request.getRequestDispatcher("views/Voucher/Voucher.jsp").forward(request, response);
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
        HttpSession session = request.getSession(false);
        UserLoginDTO user = (UserLoginDTO) session.getAttribute(auth.USER_SESSION_KEY);
        String code = request.getParameter("voucherCode");
        if (code != null) {
            code = code.trim();
        }
        DiscountCodeDAO dao = new DiscountCodeDAO();
        DiscountCode vouchercode = dao.getvoucher(user.getAccountId());
        session.setAttribute("voucher", vouchercode);
        if (code != null && vouchercode != null && code.equals(vouchercode.getCode())) {
            
            dao.updateActiveAt1(vouchercode.getDiscountId());
            request.setAttribute("success", "Thêm mã giảm giá thành công");
            doGet(request, response);
            return;
        } else {
            request.setAttribute("error", "Mã code không tồn tại");
            doGet(request, response);
            return;
        }
        
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
