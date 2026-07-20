/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.*;
import dto.UserLoginDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import model.ProcessOrderResult;
import model.Shipper;
import services.ShipperService;
import utils.ConfigLoader;

/**
 *
 * @author KayT
 */
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 5 * 1024 * 1024, // 5 MB
        maxRequestSize = 10 * 1024 * 1024 // 10 MB
)
public class UpdateDeliveryServlet extends HttpServlet {

    private final ShipperService shipperService = new ShipperService();
    private static final String CLOUD_NAME = ConfigLoader.get("cloudinary.cloudName");
    private static final String API_KEY = ConfigLoader.get("cloudinary.apiKey");
    private static final String API_SECRET = ConfigLoader.get("cloudinary.apiSecret");
    private static final String UPLOAD_FOLDER = "coffeely/delivery_proof";

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
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        UserLoginDTO currentUser = (UserLoginDTO) session.getAttribute("currentUser");
        Shipper shipper = shipperService.getShipperByAccountId(currentUser.getAccountId());
        if (shipper == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // ── Đọc tham số form ────────────────────────────────────
        int orderId = parseIntParam(request.getParameter("orderId"), 0);
        boolean isSuccess = "true".equalsIgnoreCase(request.getParameter("isSuccess"));
        String note = trimOrEmpty(request.getParameter("note"));
        String failReason = trimOrEmpty(request.getParameter("failReason"));

        if (orderId <= 0) {
            flashAndRedirect(request, response, false, "Mã đơn hàng không hợp lệ.");
            return;
        }

        // ── Upload ảnh lên Cloudinary (chỉ khi thành công) ─────
        String proofImageUrl = null;
        if (isSuccess) {
            Part imagePart = request.getPart("proofImage");
            if (imagePart == null || imagePart.getSize() == 0) {
                flashAndRedirect(request, response, false, "Vui lòng chụp/chọn ảnh xác nhận giao hàng.");
                return;
            }
            try {
                proofImageUrl = uploadToCloudinary(imagePart);
            } catch (Exception e) {
                System.err.println("[UpdateDelivery] Cloudinary upload error: " + e.getMessage());
                e.printStackTrace();
                String debugMsg = "Lỗi: " + e.getClass().getSimpleName() + " - " + e.getMessage();
                flashAndRedirect(request, response, false, debugMsg);
                return;
            }
        }

        // ── Gọi service cập nhật DB ─────────────────────────────
        ProcessOrderResult result = shipperService.updateDeliveryStatus(
                orderId, shipper.getShipperId(), isSuccess, note, proofImageUrl, failReason);

        if (result.isSuccess()) {
            String msg = isSuccess
                    ? "Giao hàng thành công! Đơn #" + orderId + " đã hoàn tất."
                    : "Đã ghi nhận giao hàng thất bại cho đơn #" + orderId + ".";
            flashAndRedirect(request, response, true, msg);
        } else {
            String err = result.getErrors() != null && !result.getErrors().isEmpty()
                    ? result.getErrors().get(0) : "Cập nhật thất bại. Vui lòng thử lại.";
            flashAndRedirect(request, response, false, err);
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

    private void setFlashMessage(HttpSession session, boolean isSuccess, String message) {
        session.setAttribute("flashSuccess", isSuccess);
        session.setAttribute("flashMessage", message);
    }

    private String uploadToCloudinary(Part imagePart) throws Exception {
        long timestamp = System.currentTimeMillis() / 1000;
        String toSign = "folder=" + UPLOAD_FOLDER + "&timestamp=" + timestamp + API_SECRET;
        String signature = sha1Hex(toSign);

        String boundary = "---BoundaryXYZ" + timestamp;
        ByteArrayOutputStream bodyStream = new ByteArrayOutputStream();
        writeField(bodyStream, boundary, "api_key", API_KEY);
        writeField(bodyStream, boundary, "timestamp", String.valueOf(timestamp));
        writeField(bodyStream, boundary, "folder", UPLOAD_FOLDER);
        writeField(bodyStream, boundary, "signature", signature);

        String fileName = getSubmittedFileName(imagePart);
        String contentType = imagePart.getContentType() != null
                ? imagePart.getContentType() : "image/jpeg";

        bodyStream.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
        bodyStream.write(("Content-Disposition: form-data; name=\"file\"; filename=\""
                + fileName + "\"\r\n").getBytes(StandardCharsets.UTF_8));
        bodyStream.write(("Content-Type: " + contentType + "\r\n\r\n")
                .getBytes(StandardCharsets.UTF_8));
        try (InputStream is = imagePart.getInputStream()) {
            byte[] buf = new byte[4096];
            int len;
            while ((len = is.read(buf)) != -1) {
                bodyStream.write(buf, 0, len);
            }
        }
        bodyStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
        bodyStream.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
        byte[] body = bodyStream.toByteArray();

        if (CLOUD_NAME == null || API_KEY == null || API_SECRET == null) {
            throw new IllegalStateException("Cloudinary config chưa được load (kiểm tra ConfigLoader / cloudinary.cloudName, apiKey, apiSecret)");
        }

        String uploadUrl = "https://api.cloudinary.com/v1_1/" + CLOUD_NAME + "/image/upload";
        HttpURLConnection conn = (HttpURLConnection) new URL(uploadUrl).openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        conn.setRequestProperty("Content-Length", String.valueOf(body.length));
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(30000);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body);
            os.flush();
        }

        int status = conn.getResponseCode();
        InputStream respStream = (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream();
        String responseBody;
        try (InputStream is = respStream) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int len;
            while ((len = is.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            responseBody = out.toString(StandardCharsets.UTF_8);
        }

        System.out.println("[Cloudinary] status=" + status + " response: " + responseBody);

        JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
        if (status < 200 || status >= 300 || !json.has("secure_url")) {
            String errMsg = json.has("error")
                    ? json.getAsJsonObject("error").get("message").getAsString()
                    : responseBody;
            throw new RuntimeException("Cloudinary upload thất bại (HTTP " + status + "): " + errMsg);
        }
        return json.get("secure_url").getAsString();
    }

    // ── Helpers ─────────────────────────────────────────────────
    private void writeField(ByteArrayOutputStream out, String boundary,
            String name, String value) throws IOException {
        out.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
        out.write(("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n")
                .getBytes(StandardCharsets.UTF_8));
        out.write((value + "\r\n").getBytes(StandardCharsets.UTF_8));
    }

    private String sha1Hex(String input) throws Exception {
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-1");
        byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private String getSubmittedFileName(Part part) {
        String cd = part.getHeader("content-disposition");
        if (cd != null) {
            for (String token : cd.split(";")) {
                token = token.trim();
                if (token.startsWith("filename")) {
                    return token.substring(token.indexOf('=') + 1).trim()
                            .replace("\"", "");
                }
            }
        }
        return "proof_" + System.currentTimeMillis() + ".jpg";
    }

    private void flashAndRedirect(HttpServletRequest req, HttpServletResponse res,
            boolean success, String message) throws IOException {
        HttpSession session = req.getSession(true);
        session.setAttribute("flashMessage", message);
        session.setAttribute("flashSuccess", success);
        res.sendRedirect(req.getContextPath() + "/shipper/dashboard");
    }

    private int parseIntParam(String val, int defaultVal) {
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            return defaultVal;
        }
    }

    private String trimOrEmpty(String val) {
        return val != null ? val.trim() : "";
    }

}
