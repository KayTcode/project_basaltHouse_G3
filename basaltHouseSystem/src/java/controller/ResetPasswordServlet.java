/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import services.AuthService;

/**
 *
 * @author KayT
 */
public class ResetPasswordServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

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
            out.println("<title>Servlet ResetPasswordServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ResetPasswordServlet at " + request.getContextPath() + "</h1>");
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
        if (session == null || session.getAttribute("fpEmail") == null || !Boolean.TRUE.equals(session.getAttribute("otpVerified"))) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }
        request.getRequestDispatcher("views/Authentication/reset-password.jsp").forward(request, response);
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
        if (session == null || session.getAttribute("fpEmail") == null || !Boolean.TRUE.equals(session.getAttribute("otpVerified"))) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }
        String email = (String) session.getAttribute("fpEmail");
        int accountId = ((Number) session.getAttribute("fpAccountId")).intValue();
        String newPassword = request.getParameter("newPassword") != null ? request.getParameter("newPassword").trim() : "";
        String confirmPassword = request.getParameter("confirmPassword") != null ? request.getParameter("confirmPassword").trim() : "";

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            request.setAttribute("error", "Vui lòng điền đầy đủ cả hai thông tin mật khẩu");
            request.getRequestDispatcher("views/Authentication/reset-password.jsp").forward(request, response);
            return;
        }
        if (newPassword.length() < 8) {
            request.setAttribute("error", "Mật khẩu phải có tối thiểu 8 kí tự.");
            request.getRequestDispatcher("views/Authentication/reset-password.jsp").forward(request, response);
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không trùng khớp");
            request.getRequestDispatcher("views/Authentication/reset-password.jsp").forward(request, response);
            return;
        }

        Map<String, Object> result = authService.resetPassword(accountId, newPassword);
        Boolean success = (Boolean) result.get("success");
        if (success == null || !success) {
            request.setAttribute("error", result.get("error"));
            request.getRequestDispatcher("views/Authentication/reset-password.jsp").forward(request, response);
            return;
        }
        session.removeAttribute("fpEmail");
        session.removeAttribute("fpAccountId");
        session.removeAttribute("otpVerified");

        session.setAttribute("loginSuccess", "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
        response.sendRedirect(request.getContextPath() + "/login");
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
