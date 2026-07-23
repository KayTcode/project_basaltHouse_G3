/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dto.UserLoginDTO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    private static final CustomerService cusService = new CustomerService();
    private static final ActivityLogService activeService = new ActivityLogService();
    private static final AccountService accService = new AccountService();

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

        UserLoginDTO user1 = getCurrentUser(request);

        CustomerProfile p = null;
        HashMap<String, Object> s = cusService.getCustomerById(user1.getAccountId());
        if (s.containsKey("error")) {
            request.setAttribute("profileError", s.get("error").toString());
        } else {
            p = (CustomerProfile) s.get("success");
        }
        request.setAttribute("cusr", p);
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
        UserLoginDTO user1 = getCurrentUser(request);


        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        if (oldPassword == null || oldPassword.isBlank()
                || newPassword == null || newPassword.isBlank()) {
            request.setAttribute("passwordError", "Vui lòng nhập đầy đủ mật khẩu cũ và mật khẩu mới.");
            doGet(request, response);
            return;
        }

        String oldPasswordHash = PasswordUtils.hashSHA256(oldPassword);
        String newPasswordHash = PasswordUtils.hashSHA256(newPassword);
        HashMap<String, Object> passwordResult = accService.getPassordById(user1.getAccountId());
        if (passwordResult.containsKey("error")) {
            request.setAttribute("passwordError", passwordResult.get("error").toString());
            doGet(request, response);
            return;
        }

        String storedPasswordHash = (String) passwordResult.get("success");
        if (storedPasswordHash == null || !oldPasswordHash.equalsIgnoreCase(storedPasswordHash)) {
            request.setAttribute("passwordError", "Mật khẩu cũ không chính xác.");
            doGet(request, response);
            return;
        }
        if (oldPasswordHash.equalsIgnoreCase(newPasswordHash)) {
            request.setAttribute("passwordError", "Mật khẩu mới không được giống mật khẩu cũ.");
            doGet(request, response);
            return;
        }

        HashMap<String, Object> updateResult = accService.updatePassword(
                user1.getAccountId(), newPasswordHash);
        if (updateResult.containsKey("error")) {
            request.setAttribute("passwordError", updateResult.get("error").toString());
            doGet(request, response);
            return;
        }

        HashMap<String, Object> logResult = activeService.ctreatActiveLog(
                new ActivityLog(user1.getAccountId(),
                        "Change password",
                        "Accounts",
                        user1.getAccountId(),
                        null,
                        null,
                        "Success",
                        0,
                        LocalDateTime.now()));
        if (logResult.containsKey("error")) {
            System.err.println("Could not write password-change activity log: "
                    + logResult.get("error"));
        }

        request.setAttribute("passwordSuccess", "Đổi mật khẩu thành công");
        doGet(request, response);
    }

    private UserLoginDTO getCurrentUser(HttpServletRequest request) {
        return (UserLoginDTO) request.getSession(false)
                .getAttribute(AuthService.USER_SESSION_KEY);
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
