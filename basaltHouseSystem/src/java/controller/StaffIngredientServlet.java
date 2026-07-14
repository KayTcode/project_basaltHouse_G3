package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.StockService;

public class StaffIngredientServlet extends HttpServlet {

    private final StockService stockService = new StockService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        StaffServlet.prepareStaffPage(request, "ingredient");
        try {

            String key = request.getParameter("search");
            HashMap<String, Object> dashboardData = stockService.getStaffDashboardData(key, false);
            request.setAttribute("key", key);
            for (Map.Entry<String, Object> entry : dashboardData.entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            request.setAttribute("dataError", e.getMessage());
        }
        
        request.getRequestDispatcher("/views/Staff/Staff.jsp").forward(request, response);
    }
}
