/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.AccountDAO;
import dao.AuthDAO;
import dao.ActiveLogDAO;
import dao.CustomersProfileDAO;
import dto.UserLoginDTO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import model.ActivityLog;
import model.CustomerProfile;
import services.AccountService;
import services.ActivityLogService;
import services.AuthService;
import services.CustomerService;
import utils.PasswordUtils;

/**
 *
 * @author admin
 */
public class AccountServlet extends HttpServlet {

    private final AuthService autheService = new AuthService();
    private static final CustomerService cusService = new CustomerService();
    private static final ActivityLogService activeService = new ActivityLogService();
    private static final AccountService accService = new AccountService();

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
            out.println("<title>Servlet AccountServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AccountServlet at " + request.getContextPath() + "</h1>");
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
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserLoginDTO user1 = (UserLoginDTO) session.getAttribute(AuthService.USER_SESSION_KEY);
        if (user1 == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        CustomerProfile p = null;
        HashMap<String, Object> s = cusService.getCustomerById(user1.getAccountId());
        if (s.containsKey("error")) {
            request.setAttribute("error", s.get("error").toString());
        } else {
            p = (CustomerProfile) s.get("success");
        }
        request.setAttribute("cusr", p);
        if (p != null) {
            request.setAttribute("phone", p.getPhone());
            request.setAttribute("email", p.getEmail());
            request.setAttribute("fullName", p.getFullName());
            request.setAttribute("avatarUrl", p.getAvatarUrl());
        }
        request.getRequestDispatcher("views/AccountProfile/AccountProfile.jsp").forward(request, response);
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
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        UserLoginDTO user1 = (UserLoginDTO) session.getAttribute(AuthService.USER_SESSION_KEY);
        if (user1 == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }


        String passOld = PasswordUtils.hashSHA256(request.getParameter("oldPassword"));
        String PasNew = PasswordUtils.hashSHA256(request.getParameter("newPassword"));
        String passHard = null;
        HashMap<String, Object> s = accService.getPassordById(user1.getAccountId());
        if (s.containsKey("error")) {
            request.setAttribute("error", s.get("error").toString());
        } else {
            passHard = (String) s.get("success");
        }

        if (passOld.equals(passHard) && !passOld.equals(PasNew)) {
            HashMap<String, Object> s2 = accService.updatePassword(user1.getAccountId(), PasNew);
            if (s2.containsKey("error")) {
                request.setAttribute("error", s2.get("error").toString());
            }
            HashMap<String, Object> s3 = activeService.ctreatActiveLog(new ActivityLog(user1.getAccountId(),
                    "Change password",
                    "Accounts",
                    user1.getAccountId(),
                    passOld,
                    PasNew,
                    "Success",
                    0,
                    LocalDateTime.now()));
            if (s3.containsKey("error")) {
                request.setAttribute("error", s3.get("error"));
            } else {
                request.setAttribute("passwordSuccess", "Đổi mật khẩu thành công");
                doGet(request, response);
                return;
            }

        } else if (passOld.equals(passHard) && passOld.equals(PasNew)) {
            request.setAttribute("error", "Mật khẩu mới không được giống mật khẩu cũ ");
            doGet(request, response);
            return;

        } else {
            request.setAttribute("error", "Mật khẩu sai ");
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
