/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dto.UserLoginDTO;
import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Order;
import model.OrderAddress;
import model.Shipper;
import services.AuthService;
import services.ShipperService;

/**
 *
 * @author KayT
 */
public class ShipperDashboardServlet extends HttpServlet {

    private final ShipperService shipperService = new ShipperService();

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
            out.println("<title>Servlet ShipperDashboardServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ShipperDashboardServlet at " + request.getContextPath() + "</h1>");
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
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserLoginDTO currentUser = (UserLoginDTO) session.getAttribute("currentUser");
        int accountId = currentUser.getAccountId();
        Shipper currentShipper = shipperService.getShipperByAccountId(accountId);
        if (currentShipper == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        int shipperId = currentShipper.getShipperId();
        List<Order> pendingOrders = shipperService.getPendingShipperOrders(shipperId);

        Order currentOrder = shipperService.getCurrentShippingOrder(shipperId);

        OrderAddress deliveryAddress = null;
        if (currentOrder != null && currentOrder.getOrderAddressId() > 0) {
            deliveryAddress = shipperService.getOrderAddress(currentOrder.getOrderAddressId());
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        Map<Integer, String> orderTimeMap = new HashMap<>();
        for (Order o : pendingOrders) {
            if (o.getCreatedAt() != null) {
                orderTimeMap.put(o.getOrderId(), o.getCreatedAt().format(fmt));
            }
        }
        if (currentOrder != null && currentOrder.getCreatedAt() != null) {
            request.setAttribute("currentOrderTime", currentOrder.getCreatedAt().format(fmt));
        }
        request.setAttribute("orderTimeMap", orderTimeMap);
        request.setAttribute("currentShipper", currentShipper);
        request.setAttribute("pendingOrders", pendingOrders);
        request.setAttribute("currentOrder", currentOrder);
        request.setAttribute("deliveryAddress", deliveryAddress);
        request.getRequestDispatcher("/views/shipper/shipper-dashboard.jsp").forward(request, response);
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
        processRequest(request, response);
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

    private String extractJwtFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object tokenInSession = session.getAttribute("jwtToken");
            if (tokenInSession != null) {
                return tokenInSession.toString();
            }
        }
        return null;
    }
}
