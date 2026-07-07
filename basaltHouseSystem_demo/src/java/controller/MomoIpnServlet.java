/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.OrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import utils.Momoconfig;

/**
 *
 * @author KayT
 */
public class MomoIpnServlet extends HttpServlet {

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
            out.println("<title>Servlet MomoIpnServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet MomoIpnServlet at " + request.getContextPath() + "</h1>");
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
        processRequest(request, response);
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
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        String requestBody = sb.toString();
        System.out.println("[MomoIPN] Received: " + requestBody);

        // ── 2. Parse các trường cần thiết từ JSON body ───────────────────
        String partnerCode = parseJsonValue(requestBody, "partnerCode");
        String orderId = parseJsonValue(requestBody, "orderId");
        String requestId = parseJsonValue(requestBody, "requestId");
        String amount = parseJsonValue(requestBody, "amount");
        String orderInfo = parseJsonValue(requestBody, "orderInfo");
        String orderType = parseJsonValue(requestBody, "orderType");
        String transId = parseJsonValue(requestBody, "transId");
        String resultCode = parseJsonValue(requestBody, "resultCode");
        String message = parseJsonValue(requestBody, "message");
        String payType = parseJsonValue(requestBody, "payType");
        String responseTime = parseJsonValue(requestBody, "responseTime");
        String extraData = parseJsonValue(requestBody, "extraData");
        String signature = parseJsonValue(requestBody, "signature");

        // ── 3. Xác thực chữ ký ───────────────────────────────────────────
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

        String calculatedSig = Momoconfig.hmacSHA256(rawSignature);
        boolean signatureValid = calculatedSig.equals(signature);

        System.out.println("[MomoIPN] orderId=" + orderId
                + " | resultCode=" + resultCode + " | sigOK=" + signatureValid);

        // ── 4. Cập nhật DB nếu hợp lệ và thành công ─────────────────────
        if (signatureValid && "0".equals(resultCode)) {
            String orderCode = extractOrderCode(orderId);
            int dbOrderId = parseOrderId(orderCode);

            if (dbOrderId > 0) {
                OrderDAO orderDAO = new OrderDAO();
                boolean ok = orderDAO.updatePaymentStatus(
                        dbOrderId, "MOMO", "Paid", "Processing");
                System.out.println("[MomoIPN] updatePaymentStatus → " + ok
                        + " | dbOrderId=" + dbOrderId + " | transId=" + transId);
            } else {
                System.err.println("[MomoIPN] Không parse được orderId từ: " + orderId);
            }
        } else if (!signatureValid) {
            System.err.println("[MomoIPN] Chữ ký không hợp lệ! Có thể bị giả mạo.");
        } else {
            System.out.println("[MomoIPN] Giao dịch thất bại: resultCode=" + resultCode);
        }

        // ── 5. Trả về HTTP 200 để MoMo biết đã nhận (BẮT BUỘC) ──────────
        // Nếu không trả 200, MoMo sẽ retry nhiều lần
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        try (PrintWriter out = response.getWriter()) {
            out.write("{\"status\":0,\"message\":\"success\"}");
        }
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

    private String parseJsonValue(String json, String key) {
        if (json == null || key == null) {
            return null;
        }
        // String: "key":"value"
        String strPattern = "\"" + key + "\":\"";
        int idx = json.indexOf(strPattern);
        if (idx >= 0) {
            int start = idx + strPattern.length();
            int end = json.indexOf("\"", start);
            if (end > start) {
                return json.substring(start, end);
            }
        }
        // Number: "key":123
        String numPattern = "\"" + key + "\":";
        idx = json.indexOf(numPattern);
        if (idx >= 0) {
            int start = idx + numPattern.length();
            int end = start;
            while (end < json.length() && (Character.isDigit(json.charAt(end))
                    || json.charAt(end) == '-')) {
                end++;
            }
            if (end > start) {
                return json.substring(start, end);
            }
        }
        return null;
    }

    private String extractOrderCode(String momoOrderId) {
        if (momoOrderId == null) {
            return "";
        }
        int idx = momoOrderId.lastIndexOf('_');
        return idx > 0 ? momoOrderId.substring(0, idx) : momoOrderId;
    }

    private int parseOrderId(String orderCode) {
        try {
            if (orderCode != null && orderCode.startsWith("BH-")) {
                return Integer.parseInt(orderCode.substring(3));
            }
        } catch (NumberFormatException ignored) {
        }
        return -1;
    }

}
