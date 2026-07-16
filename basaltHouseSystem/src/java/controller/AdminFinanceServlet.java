package controller;

import java.io.IOException;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.AdminFinanceService;

@WebServlet(name = "AdminFinanceServlet", urlPatterns = {"/admin/finance"})
public class AdminFinanceServlet extends HttpServlet {

    private final AdminFinanceService financeService = new AdminFinanceService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Đọc tham số kỳ báo cáo, mặc định là tháng này
        String period = request.getParameter("period");
        if (period == null || period.isBlank()) period = "month";
        if (!period.equals("week") && !period.equals("month") && !period.equals("year")) {
            period = "month";
        }

        try {
            Map<String, Object> data = financeService.getFinanceData(period);
            request.setAttribute("data", data);
        } catch (Exception e) {
            System.err.println("[AdminFinanceServlet] Lỗi load dữ liệu: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("loadError", "Không thể tải dữ liệu tài chính. Vui lòng thử lại.");
        }

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

