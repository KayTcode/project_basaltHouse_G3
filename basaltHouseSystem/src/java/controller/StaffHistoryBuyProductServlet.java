package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.StockService;

public class StaffHistoryBuyProductServlet extends HttpServlet {

    private final StockService stockService = new StockService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        StaffServlet.prepareStaffPage(request, "sales-history");
        try {
            LocalDate selectedDate = parseLocalDate(request.getParameter("auditDate"));
            if (selectedDate != null && selectedDate.isAfter(LocalDate.now())) {
                request.setAttribute("errorMessage",
                        "Không thể tìm lịch sử bán hàng ở ngày tương lai.");
                selectedDate = null;
            }
            HashMap<String, Object> salesAudit = stockService.getSalesAuditData(selectedDate);
            request.setAttribute("salesAudit", salesAudit);
            if (salesAudit.containsKey("dataError")) {
                request.setAttribute("dataError", salesAudit.get("dataError"));
            }
        } catch (Exception e) {
            request.setAttribute("dataError", e.getMessage());
        }
        request.getRequestDispatcher("/views/Staff/Staff.jsp").forward(request, response);
    }

    private LocalDate parseLocalDate(String value) {
        String normalized = trimToNull(value);
        if (normalized == null) {
            return null;
        }
        try {
            return LocalDate.parse(normalized);
        } catch (Exception e) {
            return null;
        }
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
