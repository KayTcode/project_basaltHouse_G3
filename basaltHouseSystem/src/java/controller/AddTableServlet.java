package controller;

import dal.TableDAO;
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String base      = request.getContextPath() + "/TableSession";
        String tableCode = request.getParameter("tableCode");
        String area      = request.getParameter("area");
        String capStr    = request.getParameter("capacity");

        // Validate
        if (tableCode == null || tableCode.isBlank()) {
            response.sendRedirect(base + "?err=" + enc("Mã bàn không được để trống."));
            return;
        }
        if (area == null || area.isBlank()) {
            response.sendRedirect(base + "?err=" + enc("Khu vực không hợp lệ."));
            return;
        }
        if (capStr == null || capStr.isBlank()) {
            response.sendRedirect(base + "?err=" + enc("Sức chứa không được để trống."));
            return;
        }
        int capacity;
        try {
            capacity = Integer.parseInt(capStr.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(base + "?err=" + enc("Sức chứa phải là số nguyên."));
            return;
        }
        if (capacity < 1 || capacity > 20) {
            response.sendRedirect(base + "?err=" + enc("Sức chứa phải từ 1 đến 20."));
            return;
        }

        TableDAO dao = new TableDAO();
        if (dao.isTableCodeExists(tableCode.trim())) {
            response.sendRedirect(base + "?err=" + enc("Mã bàn \"" + tableCode.trim() + "\" đã tồn tại."));
            return;
        }

        boolean ok = dao.addTable(tableCode.trim(), area.trim(), capacity);
        if (ok) {
            response.sendRedirect(base + "?addOk=1&code=" + enc(tableCode.trim()));
        } else {
            response.sendRedirect(base + "?err=" + enc("Thêm bàn thất bại. Vui lòng thử lại."));
        }
    }

    private static String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}
