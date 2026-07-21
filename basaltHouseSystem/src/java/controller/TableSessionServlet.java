package controller;

import model.Table;
import model.TableSession;
import services.TableService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;


@WebServlet(name = "TableSessionServlet", urlPatterns = {"/TableSession"})
public class TableSessionServlet extends HttpServlet {

   
    private final TableService tableService = new TableService();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ok         = request.getParameter("ok");
        String addOk      = request.getParameter("addOk");
        String delOk      = request.getParameter("delOk");
        String checkoutOk = request.getParameter("checkoutOk");
        String moveOk     = request.getParameter("moveOk");
        String err        = request.getParameter("err");
        String code       = request.getParameter("code");

        if ("1".equals(ok) && code != null) {
            request.setAttribute("successMsg", "Session " + code + " đã được tạo thành công!");
        } else if ("1".equals(addOk) && code != null) {
            request.setAttribute("addTableMsg", "Bàn \"" + code + "\" đã được thêm thành công!");
        } else if ("1".equals(delOk) && code != null) {
            request.setAttribute("delTableMsg", "Bàn \"" + code + "\" đã được xóa!");
        } else if ("1".equals(checkoutOk) && code != null) {
            request.setAttribute("checkoutSuccessMsg", "Session \"" + code + "\" đã thanh toán thành công!");
        } else if ("1".equals(moveOk) && code != null) {
            request.setAttribute("moveTableMsg", "Session \"" + code + "\" đã được chuyển bàn thành công!");
        } else if (err != null && !err.isBlank()) {
            request.setAttribute("errorMsg", err);
        }


        HashMap<Integer, Table>        tablesMap   = tableService.getTablesMap();
        HashMap<Integer, TableSession> sessionsMap = tableService.getActiveSessionsMap();

        request.setAttribute("tablesMap",   tablesMap);
        request.setAttribute("sessionsMap", sessionsMap);

        request.getRequestDispatcher("views/TableSession/CreateTableSession.jsp")
               .forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        switch (action == null ? "" : action) {
            case "create"      -> handleCreate(request, response);
            case "addTable"    -> handleAddTable(request, response);
            case "deleteTable" -> handleDeleteTable(request, response);
            case "checkout"    -> handleCheckout(request, response);
            case "moveTable"   -> handleMoveTable(request, response);
            default            -> redirect(request, response, "err", null, "Hành động không hợp lệ.");
        }
    }


    private void handleCreate(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String tableIdStr    = request.getParameter("tableId");
        String guestCountStr = request.getParameter("guestCount");

        // Parse — lỗi format dừng ngay tại controller
        int tableId, guestCount;
        try {
            tableId    = Integer.parseInt(tableIdStr == null ? "" : tableIdStr.trim());
            guestCount = Integer.parseInt(guestCountStr == null ? "" : guestCountStr.trim());
        } catch (NumberFormatException e) {
            redirect(request, response, "err", null, "Dữ liệu không hợp lệ.");
            return;
        }
        Integer cashierId  = getCashierId(request);
        String  result     = tableService.createSession(tableId, guestCount, cashierId);

        if (result.startsWith("ERR:")) {
            redirect(request, response, "err", null, result.substring(4));
        } else {
            redirect(request, response, "ok", result, null); // result = sessionCode
        }
    }


    private void handleAddTable(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String tableCode     = request.getParameter("tableCode");
        String area          = request.getParameter("area");
        String capacityStr   = request.getParameter("capacity");

        int capacity;
        try {
            capacity = Integer.parseInt(capacityStr == null ? "" : capacityStr.trim());
        } catch (NumberFormatException e) {
            redirect(request, response, "err", null, "Sức chứa không hợp lệ.");
            return;
        }

        String error = tableService.addTable(tableCode, area, capacity);
        if (error == null) {
            redirect(request, response, "addOk", tableCode.trim(), null);
        } else {
            redirect(request, response, "err", null, error);
        }
    }


    private void handleDeleteTable(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String tableIdStr  = request.getParameter("tableId");
        String tableCode   = request.getParameter("tableCode");

        if (isBlank(tableIdStr)) {
            redirect(request, response, "err", null, "Thiếu ID bàn.");
            return;
        }

        int tableId;
        try {
            tableId = Integer.parseInt(tableIdStr.trim());
        } catch (NumberFormatException e) {
            redirect(request, response, "err", null, "ID bàn không hợp lệ.");
            return;
        }

        int result = tableService.deleteTable(tableId);
        switch (result) {
            case 1  -> redirect(request, response, "delOk", tableCode, null);
            case 0  -> redirect(request, response, "err",   null, "Bàn đang có khách, không thể xóa.");
            default -> redirect(request, response, "err",   null, "Không thể xóa bàn. Vui lòng thử lại.");
        }
    }


    private void handleCheckout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String sessionIdStr  = request.getParameter("sessionId");
        String sessionCode   = request.getParameter("sessionCode");

        if (isBlank(sessionIdStr)) {
            redirect(request, response, "err", null, "Thiếu ID session.");
            return;
        }

        int sessionId;
        try {
            sessionId = Integer.parseInt(sessionIdStr.trim());
        } catch (NumberFormatException e) {
            redirect(request, response, "err", null, "ID session không hợp lệ.");
            return;
        }

        boolean ok = tableService.closeSession(sessionId);
        if (ok) {
            redirect(request, response, "checkoutOk", sessionCode, null);
        } else {
            redirect(request, response, "err", null, "Không thể thanh toán session. Vui lòng thử lại.");
        }
    }


    private void handleMoveTable(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String sessionIdStr  = request.getParameter("sessionId");
        String newTableIdStr = request.getParameter("newTableId");
        String sessionCode   = request.getParameter("sessionCode");

        if (isBlank(sessionIdStr) || isBlank(newTableIdStr)) {
            redirect(request, response, "err", null, "Thiếu thông tin đổi bàn.");
            return;
        }

        int sessionId, newTableId;
        try {
            sessionId   = Integer.parseInt(sessionIdStr.trim());
            newTableId  = Integer.parseInt(newTableIdStr.trim());
        } catch (NumberFormatException e) {
            redirect(request, response, "err", null, "Dữ liệu không hợp lệ.");
            return;
        }

        String result = tableService.moveSession(sessionId, newTableId);
        if ("OK".equals(result)) {
            redirect(request, response, "moveOk", sessionCode != null ? sessionCode : "", null);
        } else {
            String errMsg = result.startsWith("ERR:") ? result.substring(4) : result;
            redirect(request, response, "err", null, errMsg);
        }
    }


    private void redirect(HttpServletRequest request, HttpServletResponse response,
                          String paramKey, String code, String errorMsg) throws IOException {
        String base = request.getContextPath() + "/TableSession";
        String location;
        if ("err".equals(paramKey)) {
            location = base + "?err=" + enc(errorMsg != null ? errorMsg : "Lỗi không xác định.");
        } else {
            location = base + "?" + paramKey + "=1&code=" + enc(code != null ? code : "");
        }
        response.sendRedirect(location);
    }

    private static String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private static Integer getCashierId(HttpServletRequest request) {
        var session = request.getSession(false);
        if (session == null) return null;
        Object attr = session.getAttribute("cashierId");
        return attr instanceof Integer ? (Integer) attr : null;
    }
}
