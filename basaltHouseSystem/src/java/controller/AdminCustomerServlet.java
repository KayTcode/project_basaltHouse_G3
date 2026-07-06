package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import services.AdminCustomerService;


@WebServlet(name = "AdminCustomerServlet", urlPatterns = {"/admin/customers"})
public class AdminCustomerServlet extends HttpServlet {

    private final AdminCustomerService customerService = new AdminCustomerService();
    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            // Nhận tham số bộ lọc từ query string
            String search  = request.getParameter("search");
            String rankStr = request.getParameter("rankId");
            String status  = request.getParameter("status");
            String pageStr = request.getParameter("page");

            // Giao toàn bộ xử lý nghiệp vụ cho Service
            Map<String, Object> customerData = customerService
                    .getCustomerDashboardPage(search, rankStr, status, pageStr, PAGE_SIZE);

            request.setAttribute("customerData", customerData);

        } catch (Exception e) {
            System.err.println("[AdminCustomerServlet.doGet] " + e.getMessage());
            request.getSession().setAttribute("errorMessage",
                    "Không thể tải danh sách khách hàng. Vui lòng thử lại sau!");
        }

        // Forward sang View
        request.getRequestDispatcher("/views/admin/admin_customer.jsp")
               .forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        response.sendRedirect(request.getContextPath() + "/admin/customers");
    }
}
