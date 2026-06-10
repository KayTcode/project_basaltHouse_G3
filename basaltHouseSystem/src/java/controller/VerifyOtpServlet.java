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
import services.RegisterService;

/**
 *
 * @author KayT
 */
public class VerifyOtpServlet extends HttpServlet {

    private final RegisterService registerService = new RegisterService();
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
            out.println("<title>Servlet VerifyOtpServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet VerifyOtpServlet at " + request.getContextPath() + "</h1>");
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
        String purpose = getPurpose(session);
        if (!isSessionValid(session, purpose)) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }
        request.setAttribute("maskedEmail", maskEmail(getEmail(session, purpose)));
        request.setAttribute("otpPurpose", purpose);
        request.getRequestDispatcher("views/Authentication/verify-otp.jsp").forward(request, response);
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
        String purpose = getPurpose(session);
        if (!isSessionValid(session, purpose)) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }
        String email = getEmail(session, purpose);
        String action = trimParam(request.getParameter("action"));
        if ("resend".equals(action)) {
            Map<String, Object> resendResult;
            if ("FORGOT_PASSWORD".equals(purpose)) {
                resendResult = authService.resendOtp(email);
                if (Boolean.TRUE.equals(resendResult.get("success"))) {
                    session.setAttribute("fpAccountId", resendResult.get("accountId"));
                }
            } else {
                resendResult = registerService.resendOtp(email);
                if (Boolean.TRUE.equals(resendResult.get("success"))) {
                    session.setAttribute("pendingId", resendResult.get("pendingId"));
                }
            }
            Boolean ok = (Boolean) resendResult.get("success");
            if (ok == null) {
                request.setAttribute("error", resendResult.get("error"));
            } else {
                request.setAttribute("success", "Mã OTP mới đã được gửi về mail của bạn");
            }
            request.setAttribute("maskedEmail", maskEmail(email));
            request.setAttribute("otpPurpose", purpose);
            request.getRequestDispatcher("views/Authentication/verify-otp.jsp").forward(request, response);
            return;
        }
        String inputOtp = trimParam(request.getParameter("otp"));
        if (inputOtp.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập mã OTP");
            request.setAttribute("maskedEmail", maskEmail(email));
            request.setAttribute("otpPurpose", purpose);
            request.getRequestDispatcher("views/Authentication/verify-otp.jsp").forward(request, response);
            return;
        }
        if ("FOTGOT_PASSWORD".equals(purpose)) {
            handleForgotPasswordVerify(request, response, session, email, inputOtp);
        } else {
            handleRegisterVerify(request, response, session, email, inputOtp);
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

    private Object maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String local = parts[0];
        String domain = parts[1];
        if (local.length() <= 2) {
            return email;
        }
        String masked = local.substring(0, 2) + "*".repeat(Math.max(0, local.length() - 3)) + local.charAt(local.length() - 1);
        return masked + "@" + domain;
    }

    private String getPurpose(HttpSession session) {
        if (session == null) {
            return "REGISTER";
        }
        String p = (String) session.getAttribute("otpPurpose");
        return p != null ? p : "REGISTER";
    }

    private boolean isSessionValid(HttpSession session, String purpose) {
        if (session == null) {
            return false;
        }
        if ("FORGOT_PASSWORD".equals(purpose)) {
            return session.getAttribute("fpEmail") != null
                    && session.getAttribute("fpAccountId") != null;
        }
        return session.getAttribute("pendingEmail") != null;
    }

    private String getEmail(HttpSession session, String purpose) {
        if (purpose.equals("FORGOT_PASSWORD")) {
            return (String) session.getAttribute("fpEmail");
        }
        return (String) session.getAttribute("pendingEmail");
    }

    private String trimParam(String parameter) {
        return (parameter != null) ? parameter.trim() : "";
    }

    private void handleForgotPasswordVerify(HttpServletRequest request, HttpServletResponse response, HttpSession session, String email, String inputOtp) throws ServletException, IOException {
        int accountId = (int) session.getAttribute("fpAccountId");
        Map<String, Object> result = authService.verifyOtp(accountId, inputOtp);
        Boolean success = (Boolean) result.get("success");
        if (success == null || !success) {
            request.setAttribute("error", result.get("error"));
            request.setAttribute("errorType", result.get("errorType"));
            request.setAttribute("maskedEmail", maskEmail(email));
            request.setAttribute("otpPurpose", "FORGOT_PASSWORD");
            request.getRequestDispatcher("views/Authentication/verify-otp.jsp").forward(request, response);
            return;
        }
        session.setAttribute("otpVerified", true);
        response.sendRedirect(request.getContextPath() + "/reset-password");

    }

    private void handleRegisterVerify(HttpServletRequest request, HttpServletResponse response, HttpSession session, String email, String inputOtp) throws IOException, ServletException {
        Map<String, Object> result = registerService.verifyOtp(email, inputOtp);
        Boolean success = (Boolean) result.get("success");
        if (success == null || !success) {
            request.setAttribute("error", result.get("error"));
            request.setAttribute("errorType", result.get("errorType"));
            request.setAttribute("maskedEmail", maskEmail(email));
            request.setAttribute("otpPurpose", "REGISTER");
            request.getRequestDispatcher("views/Authentication/verify-otp.jsp").forward(request, response);
            return;
        }
        session.removeAttribute("pendingEmail");
        session.removeAttribute("pendingId");
        session.removeAttribute("otpPurpose");
        session.setAttribute("registerSuccess", "Đăng kí thành công! Chào mừng bạn đến với BasaltHouse. Hãy đăng nhập.");
        response.sendRedirect(request.getContextPath() + "/login");
    }

}
