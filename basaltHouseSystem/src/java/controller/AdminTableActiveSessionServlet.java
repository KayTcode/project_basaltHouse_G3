package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import dto.TableSessionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.AdminTableService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/admin/tables/active-session")
public class AdminTableActiveSessionServlet extends HttpServlet {

    private final AdminTableService tableService = new AdminTableService();

    // Đăng ký adapter cho LocalDateTime để Gson không reflect vào field nội bộ của JDK
    // (tránh lỗi InaccessibleObjectException / JsonIOException trên Java 9+)
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, type, ctx) ->
                    src == null ? null : new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tableId = request.getParameter("tableId");
        TableSessionDTO sessionDetails = tableService.getActiveSessionDetails(tableId);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (sessionDetails != null) {
            try {
                response.getWriter().write(gson.toJson(sessionDetails));
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\": \"Serialize error: " + e.getMessage() + "\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Session not found\"}");
        }
    }
}