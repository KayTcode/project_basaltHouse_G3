/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dto.UserLoginDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import model.ProcessOrderResult;
import model.Shipper;
import services.ShipperService;

/**
 *
 * @author KayT
 */
public class AcceptOrderServlet extends HttpServlet {

    private final ShipperService shipperService = new ShipperService();

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
            out.println("<title>Servlet AcceptOrderServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AcceptOrderServlet at " + request.getContextPath() + "</h1>");
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
        response.sendRedirect(request.getContextPath() + "/shipper/dashboard");
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
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserLoginDTO currentUser = (UserLoginDTO) session.getAttribute("currentUser");
        Shipper shipper = shipperService.getShipperByAccountId(currentUser.getAccountId());
        if (shipper == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int orderId = parseIntParam(request.getParameter("orderId"), 0);
        if (orderId <= 0) {
            flash(session, false, "Mã đơn hàng không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/shipper-dashboard");
            return;
        }

        ProcessOrderResult result = shipperService.accecptOrder(orderId, shipper.getShipperId());

        if (result.isSuccess()) {
            flash(session, true, "Đã nhận đơn #" + orderId + ". Hãy giao ngay!");
        } else {
            String err = (result.getErrors() != null && !result.getErrors().isEmpty())
                    ? result.getErrors().get(0) : "Không thể nhận đơn. Thử lại.";
            flash(session, false, err);
        }
        response.sendRedirect(request.getContextPath() + "/shipper-dashboard");
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

    private void flash(HttpSession s, boolean success, String msg) {
        s.setAttribute("flashMessage", msg);
        s.setAttribute("flashSuccess", success);
    }

    private int parseIntParam(String val, int def) {
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            return def;
        }
    }

}
