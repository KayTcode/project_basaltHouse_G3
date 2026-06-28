/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dto.UserLoginDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import model.ProcessOrderResult;
import model.Shipper;
import services.ShipperService;

/**
 *
 * @author KayT
 */
public class UpdateDeliveryServlet extends HttpServlet {
    
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
            out.println("<title>Servlet UpdateDeliveryServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UpdateDeliveryServlet at " + request.getContextPath() + "</h1>");
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
        response.sendRedirect(request.getContextPath() + "/shipper/dashboard");
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
 
        // Guard: chưa đăng nhập
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
 
        // Lấy shipperId từ currentUser → ShipperService (giống ShipperDashboardServlet)
        UserLoginDTO currentUser = (UserLoginDTO) session.getAttribute("currentUser");
        Shipper currentShipper = shipperService.getShipperByAccountId(currentUser.getAccountId());
        if (currentShipper == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        int shipperId = currentShipper.getShipperId();
 
        // Đọc các tham số từ form POST
        String orderIdParam  = request.getParameter("orderId");
        String action        = request.getParameter("action");        // "success" | "failed"
        String note          = request.getParameter("note");
        String proofImageUrl = request.getParameter("proofImageUrl");
        String failReason    = request.getParameter("failReason");
 
        // Validate orderId
        int orderId;
        try {
            orderId = Integer.parseInt(orderIdParam);
        } catch (NumberFormatException e) {
            setFlashMessage(session, false, "Mã đơn hàng không hợp lệ!");
            response.sendRedirect(request.getContextPath() + "/shipper/dashboard");
            return;
        }
 
        // Validate action
        if (!"success".equals(action) && !"failed".equals(action)) {
            setFlashMessage(session, false, "Hành động không hợp lệ!");
            response.sendRedirect(request.getContextPath() + "/shipper/dashboard");
            return;
        }
 
        boolean isSuccess = "success".equals(action);
 
        // Gọi Service xử lý nghiệp vụ
        ProcessOrderResult result = shipperService.updateDeliveryStatus(
                orderId, shipperId, isSuccess, note, proofImageUrl, failReason);
 
        if (result.isSuccess()) {
            String msg = isSuccess
                    ? "Đơn hàng #" + orderId + " đã được giao thành công!"
                    : "Đã ghi nhận đơn hàng #" + orderId + " giao hàng thất bại!";
            setFlashMessage(session, true, msg);
        } else {
            setFlashMessage(session, false, String.join(" | ", result.getErrors()));
        }
 
        response.sendRedirect(request.getContextPath() + "/shipper/dashboard");
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

    private void setFlashMessage(HttpSession session, boolean isSuccess, String message) {
        session.setAttribute("flashSuccess", isSuccess);
        session.setAttribute("flashMessage", message);
    }
    
}
