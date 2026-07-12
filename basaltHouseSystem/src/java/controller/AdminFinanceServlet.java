package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * AdminFinanceServlet — Tài Chính
 * Hiện tại chỉ forward sang JSP (UI tĩnh).
 * Khi làm backend, inject FinanceService và setAttribute dữ liệu ở đây.
 */
@WebServlet(name = "AdminFinanceServlet", urlPatterns = {"/admin/finance"})
public class AdminFinanceServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO: gọi FinanceService và setAttribute khi làm backend
        request.getRequestDispatcher("/views/admin/admin_finance.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Admin Finance Controller";
    }
}
