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

@WebServlet(name = "DeleteTableServlet", urlPatterns = {"/DeleteTable"})
public class DeleteTableServlet extends HttpServlet {

    private final TableService tableService = new TableService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String base       = request.getContextPath() + "/TableSession";
        String tableIdStr = request.getParameter("tableId");
        String tableCode  = request.getParameter("tableCode");


        int tableId;
        try {
            tableId = Integer.parseInt(tableIdStr == null ? "" : tableIdStr.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(base + "?err=" + enc("ID bàn không hợp lệ."));
            return;
        }

   
        int result = tableService.deleteTable(tableId);
        switch (result) {
            case 1  -> response.sendRedirect(base + "?delOk=1&code=" + enc(tableCode != null ? tableCode : ""));
            case 0  -> response.sendRedirect(base + "?err=" + enc("Không thể xóa: bàn \"" + tableCode + "\" đang có khách."));
            default -> response.sendRedirect(base + "?err=" + enc("Xóa bàn thất bại. Vui lòng thử lại."));
        }
    }

    private static String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}
