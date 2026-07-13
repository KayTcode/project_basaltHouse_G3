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
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import model.Order;
import utils.Momoconfig;

/**
 *
 * @author KayT
 */
public class MonoPaymentServlet extends HttpServlet {

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
            out.println("<title>Servlet MonoPaymentServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet MonoPaymentServlet at " + request.getContextPath() + "</h1>");
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

        String orderCode = request.getParameter("orderCode");
        if ((orderCode == null || orderCode.isBlank()) && session != null) {
            orderCode = (String) session.getAttribute("pendingOrderCode");
        }
        if (orderCode == null || orderCode.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/Cart?error=no_order");
            return;
        }
        int orderId = parseOrderId(orderCode);
        if (orderId <= 0) {
            response.sendRedirect(request.getContextPath() + "/Cart?error=invalid_order");
            return;
        }
        OrderDAO orderDao = new OrderDAO();
        Order order = orderDao.getOrderById(orderId);
        if (order == null) {
            response.sendRedirect(request.getContextPath() + "/Cart?error=order_not_found");
            return;
        }

        BigDecimal amount = order.getFinalAmount() != null ? order.getFinalAmount() : order.getTotalAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            response.sendRedirect(request.getContextPath() + "/Cart?error=invalid_amount");
            return;
        }

        long momoAmount = amount.longValue();
        try {
            String partnerCode = Momoconfig.PARTNER_CODE;
            String accessKey = Momoconfig.ACCESS_KEY;
            String requestId = orderCode + "_" + System.currentTimeMillis();
            String momoOrderId = requestId;
            String orderInfo = "Thanh toán đơn hàng BasaltHouse" + orderCode;
            String redirectUrl = Momoconfig.REDIRECT_URL;
            String ipnUrl = Momoconfig.IPN_URL;
            String requestType = Momoconfig.REQUEST_TYPE;
            String extraData = "";
            String lang = Momoconfig.LANG;

            String rawSignature = "accessKey=" + accessKey
                    + "&amount=" + momoAmount
                    + "&extraData=" + extraData
                    + "&ipnUrl=" + ipnUrl
                    + "&orderId=" + momoOrderId
                    + "&orderInfo=" + orderInfo
                    + "&partnerCode=" + partnerCode
                    + "&redirectUrl=" + redirectUrl
                    + "&requestId=" + requestId
                    + "&requestType=" + requestType;
            String signature = Momoconfig.hmacSHA256(rawSignature);

            System.out.println("[MomoPayment] rawSignature: " + rawSignature);
            System.out.println("[MomoPayment] signature: " + signature);

            String jsonBody = "{"
                    + "\"partnerCode\":\"" + partnerCode + "\","
                    + "\"partnerName\":\"BasaltHouse\","
                    + "\"storeId\":\"BasaltHouseOnline\","
                    + "\"requestId\":\"" + requestId + "\","
                    + "\"amount\":" + momoAmount + ","
                    + "\"orderId\":\"" + momoOrderId + "\","
                    + "\"orderInfo\":\"" + orderInfo + "\","
                    + "\"redirectUrl\":\"" + redirectUrl + "\","
                    + "\"ipnUrl\":\"" + ipnUrl + "\","
                    + "\"lang\":\"" + lang + "\","
                    + "\"extraData\":\"" + extraData + "\","
                    + "\"requestType\":\"" + requestType + "\","
                    + "\"orderExpireTime\":" + Momoconfig.ORDER_EXPIRE_MINUTES + ","
                    + "\"signature\":\"" + signature + "\""
                    + "}";
            String momoResponse = callMomoApi(Momoconfig.MOMO_API_URL, jsonBody);
            System.out.println("[MomoPayment] MoMo response: " + momoResponse);
            String payUrl = extractJsonValue(momoResponse, "payUrl");
            String resultCode = extractJsonValue(momoResponse, "resultCode");
            String message = extractJsonValue(momoResponse, "message");
            if ("0".equals(resultCode) && payUrl != null && !payUrl.isBlank()) {
                // ✅ Lưu requestId vào session để MomoReturnServlet đối chiếu
                if (session != null) {
                    session.setAttribute("momoRequestId", requestId);
                    session.setAttribute("momoOrderId", momoOrderId);
                }
                // Redirect khách sang trang thanh toán MoMo
                response.sendRedirect(payUrl);
            } else {
                System.err.println("[MomoPayment] MoMo API lỗi — resultCode=" + resultCode
                        + " | message=" + message);
                request.setAttribute("errorMessage",
                        "Không thể kết nối cổng MoMo: " + message + " (Code: " + resultCode + ")");
                request.getRequestDispatcher("/views/Payment/payment-fail.jsp")
                        .forward(request, response);
            }
        } catch (Exception e) {
            System.err.println("[MomoPayment] Exception: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi hệ thống khi kết nối MoMo: " + e.getMessage());
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

    private int parseOrderId(String orderCode) {
        try {
            if (orderCode != null && orderCode.startsWith("BH-")) {
                return Integer.parseInt(orderCode.substring(3));
            }
        } catch (NumberFormatException ignored) {
        }
        return -1;
    }

    private String callMomoApi(String MOMO_API_URL, String jsonBody) throws MalformedURLException, IOException {
        URL url = new URL(MOMO_API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(30_000); // 30 giây — yêu cầu tối thiểu của MoMo
        conn.setReadTimeout(30_000);
        conn.setDoOutput(true);

        // Ghi request body
        try (OutputStream os = conn.getOutputStream(); OutputStreamWriter writer = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
            writer.write(jsonBody);
            writer.flush();
        }

        // Đọc response
        int statusCode = conn.getResponseCode();
        InputStream is = (statusCode >= 200 && statusCode < 300)
                ? conn.getInputStream()
                : conn.getErrorStream();

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private String extractJsonValue(String json, String key) {
        if (json == null || key == null) {
            return null;
        }
        // Tìm "key":"value" (string)
        String stringPattern = "\"" + key + "\":\"";
        int idx = json.indexOf(stringPattern);
        if (idx >= 0) {
            int start = idx + stringPattern.length();
            int end = json.indexOf("\"", start);
            if (end > start) {
                return json.substring(start, end);
            }
        }
        // Tìm "key":number
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

}
