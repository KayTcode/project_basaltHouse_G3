/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import services.AuthService;
import utils.ConfigLoader;

/**
 *
 * @author KayT
 */
public class GoogleLoginServlet extends HttpServlet {

    private static final String CLIENT_ID = ConfigLoader.get("google.clientId");
    private static final String CLIENT_SECRET = ConfigLoader.get("google.clientSecret");
    private static final String REDIRECT_URI = ConfigLoader.get("google.redirectUri");

    // Google OAuth endpoints
    private static final String AUTH_URL = ConfigLoader.get("google.authUrl");
    private static final String TOKEN_URL = ConfigLoader.get("google.tokenUrl");
    private static final String USERINFO_URL = ConfigLoader.get("google.userInforUrl");

    private final AuthService authService = new AuthService();

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
            out.println("<title>Servlet GoogleLoginServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet GoogleLoginServlet at " + request.getContextPath() + "</h1>");
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
        String code = request.getParameter("code");
        if (code == null || code.isBlank()) {
            redirectToGoogle(response, request);
        } else {
            handleGoogleCallBack(request, response, code);
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

    private void redirectToGoogle(HttpServletResponse response, HttpServletRequest request) throws IOException {
        System.out.println("[DEBUG] REDIRECT_URI = " + REDIRECT_URI);
        System.out.println("[DEBUG] Context path  = " + request.getContextPath());
        String scope = URLEncoder.encode("email profile", StandardCharsets.UTF_8);
        String redirectUri = URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8);

        String googleAuthUrl = AUTH_URL
                + "?client_id=" + CLIENT_ID
                + "&redirect_uri=" + redirectUri
                + "&response_type=code"
                + "&scope=" + scope
                + "&access_type=offline"
                + "&prompt=select_account";
        response.sendRedirect(googleAuthUrl);
    }

    private void handleGoogleCallBack(HttpServletRequest request, HttpServletResponse response, String code) throws IOException {
        try {
            String accessToken = exchangeCodeForToken(code);
            System.out.println("[Google] accessToken = " + accessToken);
            if (accessToken == null) {
                setErorrAndRedirect(request, response, "Không thể xác thực với Google. Vui lòng thử lại.");
                return;
            }
            JsonObject userInfo = fethUserInfo(accessToken);
            System.out.println("[Google] userIno = " + userInfo);
            if (userInfo == null) {
                setErorrAndRedirect(request, response, "Không thể lấy thông tin tài khoản Google. Vui lòng thử lại.");
                return;
            }
            String email = userInfo.has("email") ? userInfo.get("email").getAsString() : null;
            String fullName = userInfo.has("name") ? userInfo.get("name").getAsString() : "";
            String avatarUrl = userInfo.has("picture") ? userInfo.get("picture").getAsString() : null;
            System.out.println("[Google] email = " + email + " name=" + fullName + " avatarUrl= " + avatarUrl);
            if (email == null || email.isBlank()) {
                setErorrAndRedirect(request, response, "Không thể lấy địa chỉ email từ tài khoản Google");
                return;
            }
            Map<String, Object> result = authService.loginOrRegisterWithGoogle(email, fullName, avatarUrl);
            System.out.println("[google] result = " + result.toString());
            Boolean success = (Boolean) result.get("success");

            if (success == null || !success) {
                setErorrAndRedirect(request, response, (String) result.get("error"));
                return;
            }
            String jwtToken = (String) result.get("token");
            saveTokenAndRedirect(request, response, jwtToken, result);
        } catch (Exception e) {
            e.printStackTrace();
            setErorrAndRedirect(request, response, "Đã xảy ra lỗi khi đăng nhập bằng Google. Vui lòng thử lại.");
        }
    }

    private String exchangeCodeForToken(String code) throws IOException, InterruptedException {
        String body = "code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)
                + "&client_id=" + URLEncoder.encode(CLIENT_ID, StandardCharsets.UTF_8)
                + "&client_secret=" + URLEncoder.encode(CLIENT_SECRET, StandardCharsets.UTF_8)
                + "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8)
                + "&grant_type=authorization_code";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(TOKEN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        JsonObject json = JsonParser.parseString(res.body()).getAsJsonObject();

        if (!json.has("access_token")) {
            System.err.println("[GoogleLogin] Token exchange failed: " + res.body());
            return null;
        }
        return json.get("access_token").getAsString();
    }

    private void setErorrAndRedirect(HttpServletRequest request, HttpServletResponse response, String message) throws IOException {
       HttpSession session = request.getSession(true);
        session.setAttribute("loginError", message);
        response.sendRedirect(request.getContextPath() + "/login");
    }

    private JsonObject fethUserInfo(String accessToken) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(USERINFO_URL))
                // ✅ Fix: "Bearer  " (2 space) → "Bearer " (1 space)
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        JsonObject json = JsonParser.parseString(res.body()).getAsJsonObject();
        if (!json.has("email")) {
            System.err.println("[GoogleLogin] UserInfo fetch failed: " + res.body());
            return null;
        }
        return json;
    }

    private void saveTokenAndRedirect(HttpServletRequest request, HttpServletResponse response, String jwtToken, Map<String, Object> result) throws IOException {
        Cookie jwtCookie = new Cookie("jwt", jwtToken);
    jwtCookie.setHttpOnly(true);
    jwtCookie.setPath("/");
    jwtCookie.setMaxAge(7 * 24 * 60 * 60);
    response.addCookie(jwtCookie);

    HttpSession session = request.getSession(true);
    session.setAttribute("currentUser", result.get("currentUser"));
    session.setAttribute("roleName",    result.get("roleName"));

    response.sendRedirect(request.getContextPath() + "/home");
    }

}
