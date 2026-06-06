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
import services.RegisterService;

/**
 *
 * @author KayT
 */
public class VerifyOtpServlet extends HttpServlet {
    
    private final RegisterService registerService = new RegisterService();

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
        if (session == null || session.getAttribute("pendingEmail") == null) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }
        String email = (String) session.getAttribute("pendingEmail");
        request.setAttribute("maskedEmail", maskEmail(email));
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
        
        if (session == null || session.getAttribute("pendindEmail") == null) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }
        String email = (String) session.getAttribute("pendingEmail");
        String action = request.getParameter("action").trim();
        if ("resend".equals(action)) {
            Map<String, Object> resendResult = registerService.resendOtp(email);
            if (!(boolean) resendResult.get("success")) {
                request.setAttribute("error", resendResult.get("error"));
                request.setAttribute("maskedEmail", maskEmail(email));
                request.getRequestDispatcher("views/Authentication/verify-otp.jsp").forward(request, response);
                return;
            }
            session.setAttribute("pendingId", resendResult.get("pendingId"));
            request.setAttribute("success", "Mã OTP mới đã được gửi về mail của bạn.");
            request.setAttribute("maskedEmail", maskEmail(email));
            request.getRequestDispatcher("views/Authentication/verify-otp.jsp").forward(request, response);
            return;
        }
        String inputOtp = request.getParameter("otp");
        if (inputOtp.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập mã OTP");
            request.setAttribute("maskedEmail", maskEmail(email));
            request.getRequestDispatcher("views/Authentication/verify-otp.jsp").forward(request, response);
            return;
        }
        Map<String, Object> verifyResult = registerService.verifyOtp(email, inputOtp);
        if (!(boolean) verifyResult.get("success")) {
            request.setAttribute("error", verifyResult.get("error"));
            request.setAttribute("errorType", verifyResult.get("errorType"));
            request.setAttribute("maskedEmail", maskEmail(email));
            request.getRequestDispatcher("views/Authentication/verify-otp").forward(request, response);
            return;
        }
        session.removeAttribute("pendingEmail");
        session.removeAttribute("pendingId");
        session.setAttribute("registerSuccess", "Đăng kí thành công! Chào mừng bạn đến với BasaltHouse Cafe. Hãy đăng nhập để tiếp tục.");
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
    
}
