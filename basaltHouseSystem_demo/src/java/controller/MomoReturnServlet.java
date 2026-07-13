/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import utils.Momoconfig;

/**
 *
 * @author KayT
 */
public class MomoReturnServlet extends HttpServlet {

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
            out.println("<title>Servlet MomoReturnServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet MomoReturnServlet at " + request.getContextPath() + "</h1>");
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
        String partnerCode = request.getParameter("partnerCode");
        String orderId = request.getParameter("orderId");       // momoOrderId
        String requestId = request.getParameter("requestId");
        String amount = request.getParameter("amount");
        String orderInfo = request.getParameter("orderInfo");
        String orderType = request.getParameter("orderType");
        String transId = request.getParameter("transId");       // Mã GD MoMo
        String resultCode = request.getParameter("resultCode");
        String message = request.getParameter("message");
        String payType = request.getParameter("payType");       // qr, webApp, credit
        String responseTime = request.getParameter("responseTime");
        String extraData = request.getParameter("extraData");
        String signature = request.getParameter("signature");

        // ── 2. Xác thực chữ ký ───────────────────────────────────────────
        // Thứ tự cố định theo tài liệu MoMo response params
        String rawSignature = "accessKey=" + Momoconfig.ACCESS_KEY
                + "&amount=" + amount
                + "&extraData=" + (extraData != null ? extraData : "")
                + "&message=" + message
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&orderType=" + orderType
                + "&partnerCode=" + partnerCode
                + "&payType=" + payType
                + "&requestId=" + requestId
                + "&responseTime=" + responseTime
                + "&resultCode=" + resultCode
                + "&transId=" + transId;

        String calculatedSignature = Momoconfig.hmacSHA256(rawSignature);
        boolean signatureValid = calculatedSignature.equals(signature);

        System.out.println("[MomoReturn] orderId=" + orderId
                + " | resultCode=" + resultCode
                + " | transId=" + transId
                + " | signatureValid=" + signatureValid);

        // ── 3. Phân luồng kết quả ─────────────────────────────────────────
        if (signatureValid && "0".equals(resultCode)) {
            // ✅ THANH TOÁN THÀNH CÔNG
            // DB được cập nhật chắc chắn hơn qua IPN (MomoIpnServlet).
            // Ở đây chỉ hiển thị kết quả cho khách và clear session/cart.

            HttpSession session = request.getSession(false);
            if (session != null) {
                // Clear giỏ hàng
                @SuppressWarnings("unchecked")
                java.util.Map<String, model.CartItem> cart
                        = (java.util.Map<String, model.CartItem>) session.getAttribute("cart");
                if (cart != null) {
                    cart.clear();
                }
                session.removeAttribute("pendingOrderCode");
                session.removeAttribute("momoRequestId");
                session.removeAttribute("momoOrderId");
            }

            // Lấy orderCode từ momoOrderId (dạng "BH-123_timestamp" → lấy phần "BH-123")
            String orderCode = extractOrderCode(orderId);

            // Format amount để hiển thị
            String amountDisplay = formatAmount(amount);

            request.setAttribute("orderCode", orderCode);
            request.setAttribute("transId", transId);
            request.setAttribute("payType", formatPayType(payType));
            request.setAttribute("amountDisplay", amountDisplay);
            request.getRequestDispatcher("/views/Payment/payment-success.jsp")
                    .forward(request, response);

        } else {
            // ❌ THANH TOÁN THẤT BẠI HOẶC HỦY
            String orderCode = extractOrderCode(orderId);
            String failReason = signatureValid
                    ? mapResultCode(resultCode)
                    : "Phản hồi không hợp lệ (chữ ký không khớp).";

            request.setAttribute("orderCode", orderCode);
            request.setAttribute("resultCode", resultCode);
            request.setAttribute("failReason", failReason);
            request.getRequestDispatcher("/views/Payment/payment-fail.jsp")
                    .forward(request, response);
        }
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

    private String extractOrderCode(String orderId) {
        if (orderId == null) {
            return "";
        }
        int underscoreIdx = orderId.lastIndexOf('_');
        return underscoreIdx > 0 ? orderId.substring(0, underscoreIdx) : orderId;
    }

    private String formatAmount(String amount) {
        if (amount == null) {
            return "0 VNĐ";
        }
        try {
            return String.format("%,d VNĐ", Long.parseLong(amount));
        } catch (NumberFormatException e) {
            return amount + " VNĐ";
        }
    }

    private Object formatPayType(String payType) {
        if (payType == null) {
            return "MoMo";
        }
        return switch (payType) {
            case "qr" ->
                "Quét mã QR";
            case "webApp" ->
                "Ví MoMo";
            case "credit" ->
                "Thẻ tín dụng/ghi nợ";
            case "napas" ->
                "Thẻ ATM nội địa";
            default ->
                "MoMo (" + payType + ")";
        };
    }

    private String mapResultCode(String code) {
        if (code == null) {
            return "Lỗi không xác định.";
        }
        return switch (code) {
            case "0" ->
                "Giao dịch thành công.";
            case "10" ->
                "Hệ thống đang được bảo trì.";
            case "11" ->
                "Truy cập bị từ chối.";
            case "12" ->
                "Phiên bản API không được hỗ trợ.";
            case "13" ->
                "Xác thực merchant thất bại.";
            case "20" ->
                "Yêu cầu không hợp lệ.";
            case "21" ->
                "Số tiền không hợp lệ (tối thiểu 1.000 VNĐ).";
            case "22" ->
                "Số tiền vượt quá hạn mức tối đa.";
            case "40" ->
                "RequestId hoặc OrderId đã tồn tại.";
            case "41" ->
                "OrderId không tồn tại.";
            case "42" ->
                "OrderId hoặc RequestId không hợp lệ.";
            case "43" ->
                "Giao dịch đã được xử lý.";
            case "1000" ->
                "Giao dịch đang khởi tạo, chờ người dùng xác nhận.";
            case "1001" ->
                "Giao dịch thất bại do tài khoản không đủ số dư.";
            case "1002" ->
                "Giao dịch bị từ chối bởi nhà cung cấp thanh toán.";
            case "1003" ->
                "Giao dịch đã bị hủy.";
            case "1004" ->
                "Số tiền vượt quá hạn mức ngày của tài khoản.";
            case "1005" ->
                "URL thanh toán đã hết hạn.";
            case "1006" ->
                "Giao dịch bị hủy bởi người dùng.";
            case "1007" ->
                "Tài khoản MoMo không tồn tại.";
            case "1026" ->
                "Giao dịch bị hạn chế theo chương trình khuyến mãi.";
            case "1080" ->
                "Hoàn tiền thất bại.";
            case "1081" ->
                "Số tiền hoàn vượt quá số tiền gốc.";
            case "2001" ->
                "Giao dịch thất bại do sai mã PIN.";
            case "2007" ->
                "Tài khoản chưa đăng ký dịch vụ thanh toán.";
            case "3001" ->
                "Liên kết tài khoản thất bại do người dùng từ chối.";
            case "3002" ->
                "Liên kết bị từ chối do không đủ điều kiện.";
            case "4001" ->
                "Tài khoản bị hạn chế giao dịch.";
            case "4010" ->
                "OTP xác thực thất bại.";
            case "4011" ->
                "OTP hết hạn.";
            case "4100" ->
                "Người dùng chưa đăng nhập.";
            case "9000" ->
                "Giao dịch được xác nhận thành công.";
            default ->
                "Giao dịch thất bại (Mã lỗi: " + code + ").";
        };
    }

}
