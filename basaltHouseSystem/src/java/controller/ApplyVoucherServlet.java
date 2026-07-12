package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;


public class ApplyVoucherServlet extends HttpServlet {

    /** Session key dùng chung với CartServlet */
    public static final String PENDING_VOUCHER_KEY = "pendingVoucherCode";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String code = request.getParameter("code");

        if (code != null && !code.trim().isEmpty()) {
            HttpSession session = request.getSession(true);
            // Lưu mã vào session để CartServlet đọc sau
            session.setAttribute(PENDING_VOUCHER_KEY, code.trim().toUpperCase());
        }

        // Redirect sang trang menu để khách hàng chọn món
        response.sendRedirect(request.getContextPath() + "/category");
    }
}
