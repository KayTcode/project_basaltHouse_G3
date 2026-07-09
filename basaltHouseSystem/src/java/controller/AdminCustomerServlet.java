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
            String search = request.getParameter("search");
            String rankStr = request.getParameter("rankId");
            String status = request.getParameter("status");
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

        request.getRequestDispatcher("/views/admin/admin_customer.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }

        switch (action) {
            case "add" ->
                handleAddCustomer(request, response);
            case "update" ->
                handleUpdateCustomer(request, response);
            default ->
                response.sendRedirect(request.getContextPath() + "/admin/customers");
        }
    }

    private void handleAddCustomer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String rankIdStr = request.getParameter("rankId");
        String spentStr = request.getParameter("totalSpent");

        // Đẩy toàn bộ tham số thô sang Service tự ép kiểu và xử lý DB
        boolean ok = customerService.processAddCustomer(email, password, fullName, phone, rankIdStr, spentStr);
        if (ok) {
            request.getSession().setAttribute("toastMessage", "Thêm khách hàng thành công!");
        } else {
            request.getSession().setAttribute("errorMessage", "Thêm thất bại! Email có thể đã tồn tại.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/customers");
    }

    private void handleUpdateCustomer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String accountIdStr = request.getParameter("accountId");
        String email = request.getParameter("email");
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String rankIdStr = request.getParameter("rankId");
        String spentStr = request.getParameter("totalSpent");
        String isLockedStr = request.getParameter("isLocked");

        // Giao Service xử lý logic cập nhật thông tin và membership
        boolean ok = customerService.processUpdateCustomer(accountIdStr, email, fullName, phone, rankIdStr, spentStr, isLockedStr);
        if (ok) {
            request.getSession().setAttribute("toastMessage", "Cập nhật thông tin thành công!");
        } else {
            request.getSession().setAttribute("errorMessage", "Cập nhật thất bại. Vui lòng thử lại!");
        }

        response.sendRedirect(request.getContextPath() + "/admin/customers");
    }
}
