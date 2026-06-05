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

@WebServlet(name = "AddTableServlet", urlPatterns = {"/AddTable"})
public class AddTableServlet extends HttpServlet {

    private final TableService tableService = new TableService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String base      = request.getContextPath() + "/TableSession";
        String tableCode = request.getParameter("tableCode");
        String area      = request.getParameter("area");
        String capStr    = request.getParameter("capacity");

       
        int capacity;
        try {
            capacity = Integer.parseInt(capStr == null ? "" : capStr.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(base + "?err=" + enc("Sức chứa phải là số nguyên."));
            return;
        }

        // Giao toàn bộ validate + persist cho Service
        String error = tableService.addTable(tableCode, area, capacity);
        if (error == null) {
            response.sendRedirect(base + "?addOk=1&code=" + enc(tableCode.trim()));
        } else {
            response.sendRedirect(base + "?err=" + enc(error));
        }
    }

    private static String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}
