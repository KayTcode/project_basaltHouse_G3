package controller;

import dao.OrderDAO;
import dto.OrderTrackingDTO;
import dto.UserLoginDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import model.Order;
import model.OrderAddress;
import model.OrderDetail;


public class OrderTrackingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Nếu chưa đăng nhập → forward thẳng vào JSP để hiển thị thông báo (không redirect)
        if (session == null || session.getAttribute("currentUser") == null) {
            request.getRequestDispatcher("views/Order/OrderTracking.jsp").forward(request, response);
            return;
        }

        UserLoginDTO user = (UserLoginDTO) session.getAttribute("currentUser");

        // Lấy customerId từ accountId
        OrderDAO orderDAO = new OrderDAO();
        int customerId = orderDAO.getCustomerIdByAccountId(user.getAccountId());

        if (customerId <= 0) {
            // Tài khoản chưa liên kết customer → show rỗng
            request.setAttribute("orders", new ArrayList<>());
            setStats(request, new ArrayList<>(), BigDecimal.ZERO);
            request.getRequestDispatcher("views/Order/OrderTracking.jsp").forward(request, response);
            return;
        }

        List<Order> rawOrders = orderDAO.getOnlineOrdersByCustomerId(customerId);

        // Build DTO list kèm details + address
        List<OrderTrackingDTO> orders = new ArrayList<>();
        BigDecimal totalSpent = BigDecimal.ZERO;
        int pendingCount   = 0;
        int completedCount = 0;

        for (Order o : rawOrders) {
            List<OrderDetail> details = orderDAO.getOrderDetailsByOrderId(o.getOrderId());

            OrderAddress addr = null;
            if (o.getOrderAddressId() != null && o.getOrderAddressId() > 0) {
                addr = orderDAO.getOrderAddressByOrderAddressId(o.getOrderAddressId());
            }

            orders.add(new OrderTrackingDTO(o, details, addr));

            // Tính stats
            String status = o.getOrderStatus();
            if ("Pending".equals(status) || "Preparing".equals(status)
                    || "In_Progress".equals(status) || "Ready".equals(status)
                    || "Delivering".equals(status)) {
                pendingCount++;
            }
            if ("Completed".equals(status)) {
                completedCount++;
                BigDecimal spent = o.getFinalAmount() != null ? o.getFinalAmount() : o.getTotalAmount();
                if (spent != null) totalSpent = totalSpent.add(spent);
            }
        }

        request.setAttribute("orders",         orders);
        request.setAttribute("totalOrders",    orders.size());
        request.setAttribute("pendingCount",   pendingCount);
        request.setAttribute("completedCount", completedCount);
        request.setAttribute("totalSpent",     totalSpent);

        request.getRequestDispatcher("views/Order/OrderTracking.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private void setStats(HttpServletRequest request, List<?> orders, BigDecimal totalSpent) {
        request.setAttribute("totalOrders",    orders.size());
        request.setAttribute("pendingCount",   0);
        request.setAttribute("completedCount", 0);
        request.setAttribute("totalSpent",     totalSpent);
    }
}
