package controller;

import dao.AdminDiscountDAO;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DiscountCode;

@WebServlet(name = "AdminDiscountServlet", urlPatterns = {"/admin/discounts"})
public class AdminDiscountServlet extends HttpServlet {

    private final AdminDiscountDAO discountDAO = new AdminDiscountDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            // Lấy tham số tìm kiếm và bộ lọc từ request
            String search       = request.getParameter("search");
            String filterType   = request.getParameter("filterType");
            String filterStatus = request.getParameter("filterStatus");

            // Chuẩn hoá giá trị mặc định cho bộ lọc
            if (filterType == null || filterType.isEmpty())   filterType   = "ALL";
            if (filterStatus == null || filterStatus.isEmpty()) filterStatus = "ALL";

            // Load danh sách mã khuyến mãi từ DB với tìm kiếm & lọc bằng SQL LIKE
            List<DiscountCode> discounts = discountDAO.getAllDiscounts(search, filterType, filterStatus);

            // Load thống kê nhanh
            Map<String, Integer> stats = discountDAO.getDiscountStats();

            // Gửi dữ liệu sang JSP
            request.setAttribute("discounts",    discounts);
            request.setAttribute("stats",        stats);
            request.setAttribute("search",       search);
            request.setAttribute("filterType",   filterType);
            request.setAttribute("filterStatus", filterStatus);

        } catch (Exception e) {
            System.err.println("[AdminDiscountServlet.doGet] Lỗi: " + e.getMessage());
            e.printStackTrace();
        }

        request.getRequestDispatcher("/views/admin/admin_discount.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
