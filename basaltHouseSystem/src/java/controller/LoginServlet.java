/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dto.UserLoginDTO;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import services.AuthService;

/**
 *
 * @author KayT
 */
public class LoginServlet extends HttpServlet {

    private AuthService authService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        authService = new AuthService();
    }

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
            out.println("<title>Servlet LoginServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet LoginServlet at " + request.getContextPath() + "</h1>");
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
        HttpSession existingSession = request.getSession(false);
        if(existingSession != null && existingSession.getAttribute(authService.JWT_SESSION_KEY)!= null){
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        request.getRequestDispatcher("views/Authentication/login.jsp").forward(request, response);
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
        response.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        email = (email != null) ? email.trim() : "";
        password = (password != null) ? password.trim() : "";

        if (email.isEmpty() || password.isEmpty()) {
            forwardWithError(request, response, email, "Vui lòng điền đủ email và mật khẩu");
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            forwardWithError(request, response, email, "Địa chỉ email không hợp lệ.");
            return;
        }
        UserLoginDTO result = authService.login(email, password);
        if (result.isSuccess()) {
            handleLoginSuccess(request, response, result);
        } else {
            forwardWithError(request, response, email, result.getErrorMessage());
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

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response, String email, String errorMessage) throws ServletException, IOException {
        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("submittedEmail", email);
        request.getRequestDispatcher("views/Authentication/login.jsp").forward(request, response);
    }

    private void handleLoginSuccess(HttpServletRequest request, HttpServletResponse response, UserLoginDTO result) throws IOException {
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(7 * 24 * 60 * 60);
        session.setAttribute(AuthService.JWT_SESSION_KEY, result.getJwtToken());

        session.setAttribute(AuthService.USER_SESSION_KEY, result);

        Cookie jwtCookie = new Cookie(AuthService.JWT_COOKIE_NAME, result.getJwtToken());
        jwtCookie.setMaxAge(7 * 24 * 60 * 60);
        jwtCookie.setPath("/");
        jwtCookie.setHttpOnly(true);
        response.addCookie(jwtCookie);

        String redirectUrl = determineRedirectUrl(request, result.getRoleName());
        response.sendRedirect(redirectUrl);
    }

    private String determineRedirectUrl(HttpServletRequest request, String roleName) {
        String contextPath = request.getContextPath();
        if (roleName == null) {
            return contextPath + "/home";
        }
        switch (roleName.trim().toLowerCase()) {
            case "admin":
                return contextPath + "/admin/dashboard";
            case "staff":
                return contextPath + "/staff/dashboard";
            case "shipper":
                return contextPath + "/shipper/dashboard";
            case "cashier":
                return contextPath + "/cashier/dashbroad";
            case "customer":
            default:
                return contextPath + "/home";
        }
    }
}
