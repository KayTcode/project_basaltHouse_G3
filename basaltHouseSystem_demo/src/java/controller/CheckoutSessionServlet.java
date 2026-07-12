package controller;

import services.TableService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "CheckoutSessionServlet", urlPatterns = {"/CheckoutSession"})
public class CheckoutSessionServlet extends HttpServlet {

    private final TableService tableService = new TableService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String base         = request.getContextPath() + "/TableSession";
        String sessionIdStr = request.getParameter("sessionId");
        String sessionCode  = request.getParameter("sessionCode");


        int sessionId;
        try {
            sessionId = Integer.parseInt(sessionIdStr == null ? "" : sessionIdStr.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(base + "?err=" + enc("ID session không hợp lệ."));
            return;
        }

        // Giao toàn bộ logic đóng session cho Service
        boolean ok = tableService.closeSession(sessionId);
        if (ok) {
            response.sendRedirect(base + "?checkoutOk=1&code=" + enc(sessionCode != null ? sessionCode.trim() : ""));
        } else {
            response.sendRedirect(base + "?err=" + enc("Thanh toán session thất bại. Vui lòng thử lại."));
        }
    }

    private static String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}
