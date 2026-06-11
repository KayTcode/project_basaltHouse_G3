/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.OrderDAO;
import dto.UserLoginDTO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import model.Order;
import model.OrderDetail;
import services.AuthService;
import services.DiscountSevice;

/**
 *
 * @author MSI
 */
public class CashierPOSServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();
    private final DiscountSevice discountSevice = new DiscountSevice();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private UserLoginDTO getAuthorizedUser(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(AuthService.USER_SESSION_KEY) == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        UserLoginDTO user = (UserLoginDTO) session.getAttribute(AuthService.USER_SESSION_KEY);
        if (!"Cashier".equalsIgnoreCase(user.getRoleName())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này.");
            return null;
        }
        return user;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet CashierPOSServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CashierPOSServlet at " + request.getContextPath() + "</h1>");
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

        if (getAuthorizedUser(request, response) == null) {
            return;
        }

        // Danh sách tất cả đơn chưa thanh toán
        List<Order> unpaidOrders = orderDAO.getUnpaidOrders();
        request.setAttribute("unpaidOrders", unpaidOrders);

        // Nếu có orderId → tải chi tiết đơn đó
        String orderIdParam = request.getParameter("orderId");
        if (orderIdParam != null && !orderIdParam.isEmpty()) {
            try {
                int orderId = Integer.parseInt(orderIdParam);
                Order selectedOrder = orderDAO.getOrderById(orderId);
                if (selectedOrder != null) {
                    List<OrderDetail> orderDetails = orderDAO.getOrderDetailsByOrderId(orderId);
                    request.setAttribute("selectedOrder", selectedOrder);
                    request.setAttribute("orderDetails", orderDetails);
                }
            } catch (NumberFormatException ignored) {
            }
        }

        // Flash message từ redirect sau POST
        HttpSession session = request.getSession(false);
        if (session != null) {
            String flashMsg = (String) session.getAttribute("flashMsg");
            String flashType = (String) session.getAttribute("flashType");
            if (flashMsg != null) {
                request.setAttribute("flashMsg", flashMsg);
                request.setAttribute("flashType", flashType);
                session.removeAttribute("flashMsg");
                session.removeAttribute("flashType");
            }
        }

        request.getRequestDispatcher("/views/Cashier/pos.jsp").forward(request, response);
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
        if (getAuthorizedUser(request, response) == null) {
            return;
        }

        String action = request.getParameter("action");
        String orderIdStr = request.getParameter("orderId");

        if (orderIdStr == null || orderIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cashier/pos");
            return;
        }

        int orderId;
        try {
            orderId = Integer.parseInt(orderIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/cashier/pos");
            return;
        }

        HttpSession session = request.getSession(true);

        // ── Áp mã giảm giá ──────────────────────────────────────────
        if ("applyDiscount".equals(action)) {
            String code = request.getParameter("discountCode");
            if (code == null || code.trim().isEmpty()) {
                session.setAttribute("flashMsg", "Vui lòng nhập mã giảm giá!");
                session.setAttribute("flashType", "error");
            } else {
                String result = discountSevice.applyDiscountToOrder(orderId, code.trim().toUpperCase());
                if (result.startsWith("Thành công")) {
                    session.setAttribute("flashMsg", result);
                    session.setAttribute("flashType", "success");
                } else {
                    session.setAttribute("flashMsg", result);
                    session.setAttribute("flashType", "error");
                }
            }
            response.sendRedirect(request.getContextPath() + "/cashier/pos?orderId=" + orderId);
            return;
        }

        // ── Xác nhận thanh toán (checkout) ──────────────────────────
        if ("checkout".equals(action)) {
            String paymentMethod = request.getParameter("paymentMethod");
            if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
                session.setAttribute("flashMsg", "Vui lòng chọn phương thức thanh toán!");
                session.setAttribute("flashType", "error");
                response.sendRedirect(request.getContextPath() + "/cashier/pos?orderId=" + orderId);
                return;
            }

            boolean updated = orderDAO.updatePaymentStatus(orderId, paymentMethod, "Paid", "Done");
            if (updated) {
                session.setAttribute("flashMsg", "✔ Thanh toán đơn hàng #" + orderId + " thành công!");
                session.setAttribute("flashType", "success");
                response.sendRedirect(request.getContextPath() + "/cashier/pos");
            } else {
                session.setAttribute("flashMsg", "Lỗi: Không thể xác nhận thanh toán. Vui lòng thử lại!");
                session.setAttribute("flashType", "error");
                response.sendRedirect(request.getContextPath() + "/cashier/pos?orderId=" + orderId);
            }
            return;
        }

        // Fallback
        response.sendRedirect(request.getContextPath() + "/cashier/pos");
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
