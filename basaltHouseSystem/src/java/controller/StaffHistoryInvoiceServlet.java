package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ImportInvoicesDetail;
import services.ImportVoiceService;

public class StaffHistoryInvoiceServlet extends HttpServlet {

    private final ImportVoiceService importService = new ImportVoiceService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        StaffServlet.prepareStaffPage(request, "history");
        if ("1".equals(request.getParameter("created"))) {
            request.setAttribute("successMessage", "Tạo phiếu nhập thành công.");
        }
        String key = request.getParameter("search");
        List<ImportInvoicesDetail> list = null;
        HashMap<String, Object> result = importService.getImportInvoicesDetail(key);
        if (result.containsKey("error")) {
            request.setAttribute("errorMessage", result.get("error").toString());
        } else {
            list = (List<ImportInvoicesDetail>) result.get("success");
        }
        int totalIngredientCount = 0;
        if (list != null) {
            for (ImportInvoicesDetail invoice : list) {
                totalIngredientCount += invoice.getIngredientCount();
            }
        }
        request.setAttribute("key", key);
        request.setAttribute("listP", list);
        request.setAttribute("totalIngredientCount", totalIngredientCount);
        request.getRequestDispatcher("/views/Staff/Staff.jsp").forward(request, response);
    }
}
