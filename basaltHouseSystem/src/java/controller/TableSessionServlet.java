package controller;

import dal.TableDAO;
import dal.TableSessionDAO;
import model.Table;
import model.TableSession;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;


@WebServlet(name = "TableSessionServlet", urlPatterns = {"/TableSession"})
public class TableSessionServlet extends HttpServlet {

   
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        
        String ok    = request.getParameter("ok");
        String addOk = request.getParameter("addOk");
        String delOk = request.getParameter("delOk");
        String err   = request.getParameter("err");
        String code  = request.getParameter("code");

        if ("1".equals(ok) && code != null) {
            request.setAttribute("successMsg", "Session " + code + " đã được tạo thành công!");
        } else if ("1".equals(addOk) && code != null) {
            request.setAttribute("addTableMsg", "Bàn \"" + code + "\" đã được thêm thành công!");
        } else if ("1".equals(delOk) && code != null) {
            request.setAttribute("delTableMsg", "Bàn \"" + code + "\" đã được xóa!");
        } else if (err != null && !err.isBlank()) {
            request.setAttribute("errorMsg", err);
        }

        TableDAO tableDAO = new TableDAO();
        TableSessionDAO sessionDAO = new TableSessionDAO();
        List<Table> tables = tableDAO.getAllTablesWithOccupancy();
        List<TableSession> activeSessions = sessionDAO.getActiveSessions(0);

        request.setAttribute("tables", tables);
        request.setAttribute("activeSessions", activeSessions);

        request.getRequestDispatcher("views/TableSession/CreateTableSession.jsp")
               .forward(request, response);
    }

   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (!"create".equals(action)) {
            redirect(request, response, false, null, "Hành động không hợp lệ.");
            return;
        }

        String tableIdStr    = request.getParameter("tableId");
        String guestCountStr = request.getParameter("guestCount");

        if (tableIdStr == null || tableIdStr.isBlank()
                || guestCountStr == null || guestCountStr.isBlank()) {
            redirect(request, response, false, null, "Thiếu thông tin bàn hoặc số khách.");
            return;
        }

        int tableId, guestCount;
        try {
            tableId    = Integer.parseInt(tableIdStr.trim());
            guestCount = Integer.parseInt(guestCountStr.trim());
        } catch (NumberFormatException e) {
            redirect(request, response, false, null, "Dữ liệu không hợp lệ.");
            return;
        }

        if (guestCount < 1) {
            redirect(request, response, false, null, "Số lượng khách phải ít nhất là 1.");
            return;
        }

        TableDAO tableDAO = new TableDAO();
        TableSessionDAO sessionDAO = new TableSessionDAO();

        Table table = tableDAO.getTableById(tableId);
        if (table == null) {
            redirect(request, response, false, null, "Bàn không tồn tại (ID=" + tableId + ").");
            return;
        }

        int activeGuests = sessionDAO.getActiveGuestCount(tableId);
        int remaining    = table.getCapacity() - activeGuests;

        if (guestCount > remaining) {
            redirect(request, response, false, null,
                "Bàn " + table.getTableCode() + " chỉ còn " + remaining
                + " ghế trống (hiện có " + activeGuests + "/" + table.getCapacity() + " khách).");
            return;
        }

        Integer cashierId = null;
        Object cashierAttr = request.getSession(false) != null
                ? request.getSession().getAttribute("cashierId") : null;
        if (cashierAttr instanceof Integer) cashierId = (Integer) cashierAttr;

        String sessionCode = sessionDAO.createSession(tableId, table.getTableCode(), guestCount, cashierId);

        if (sessionCode != null) {
            redirect(request, response, true, sessionCode, null);
        } else {
            redirect(request, response, false, null, "Không thể tạo session. Vui lòng thử lại.");
        }
    }

    /** Redirect back to GET /TableSession with result encoded in query string */
    private void redirect(HttpServletRequest request, HttpServletResponse response,
                          boolean success, String sessionCode, String errorMsg) throws IOException {
        String base = request.getContextPath() + "/TableSession";
        String location;
        if (success) {
            location = base + "?ok=1&code=" + enc(sessionCode);
        } else {
            location = base + "?err=" + enc(errorMsg != null ? errorMsg : "Lỗi không xác định.");
        }
        response.sendRedirect(location);
    }

    private static String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}
