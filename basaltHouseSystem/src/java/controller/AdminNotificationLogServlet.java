package controller;

import dao.AdminNotificationLogDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Servlet quản lý module Thông báo &amp; Nhật ký hoạt động.
 * URL: /admin/logs
 *
 * @author KayT
 */
@WebServlet(name = "AdminNotificationLogServlet", urlPatterns = {"/admin/logs"})
public class AdminNotificationLogServlet extends HttpServlet {

    private static final int PAGE_SIZE = 15;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ── Tab đang hiển thị: notifications | activitylogs ──────────────
        String tab = request.getParameter("tab");
        if (tab == null || tab.isBlank()) tab = "notifications";
        request.setAttribute("tab", tab);

        AdminNotificationLogDAO dao = new AdminNotificationLogDAO();

        if ("activitylogs".equals(tab)) {
            loadActivityLogs(request, dao);
        } else {
            loadNotifications(request, dao);
        }

        request.getRequestDispatcher("/views/admin/admin_logs.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String tab    = request.getParameter("tab");
        if (tab == null || tab.isBlank()) tab = "notifications";

        AdminNotificationLogDAO dao = new AdminNotificationLogDAO();

        switch (action != null ? action : "") {
            // ── Notifications ─────────────────────────────────────────────
            case "deleteNoti" -> {
                int id = parseInt(request.getParameter("id"), 0);
                if (id > 0) dao.toggleDeleteNotification(id, true);
            }
            case "restoreNoti" -> {
                int id = parseInt(request.getParameter("id"), 0);
                if (id > 0) dao.toggleDeleteNotification(id, false);
            }
            case "hardDeleteNoti" -> {
                int id = parseInt(request.getParameter("id"), 0);
                if (id > 0) dao.hardDeleteNotification(id);
            }
            // ── Activity Logs ─────────────────────────────────────────────
            case "deleteLog" -> {
                int id = parseInt(request.getParameter("id"), 0);
                if (id > 0) dao.softDeleteLog(id);
            }
        }

        // Redirect giữ nguyên tab & bộ lọc
        StringBuilder redirect = new StringBuilder(
                request.getContextPath() + "/admin/logs?tab=" + tab);
        appendParam(redirect, "search",    request.getParameter("search"));
        appendParam(redirect, "module",    request.getParameter("module"));
        appendParam(redirect, "status",    request.getParameter("status"));
        appendParam(redirect, "deleted",   request.getParameter("deleted"));
        appendParam(redirect, "page",      request.getParameter("page"));

        response.sendRedirect(redirect.toString());
    }

    // ────────────────────────────────────────────────────────
    //  Private helpers
    // ────────────────────────────────────────────────────────

    private void loadNotifications(HttpServletRequest request, AdminNotificationLogDAO dao) {
        String search        = request.getParameter("search");
        String deletedFilter = request.getParameter("deleted");
        int page             = parseInt(request.getParameter("page"), 1);

        int total      = dao.countNotifications(search, deletedFilter);
        int totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
        if (page > totalPages) page = totalPages;

        List<Map<String, Object>> notifications =
                dao.getNotifications(search, deletedFilter, page, PAGE_SIZE);
        Map<String, Object> stats = dao.getNotificationStats();

        request.setAttribute("notifications", notifications);
        request.setAttribute("notiStats",     stats);
        request.setAttribute("currentPage",   page);
        request.setAttribute("totalPages",    totalPages);
        request.setAttribute("totalRecords",  total);
    }

    private void loadActivityLogs(HttpServletRequest request, AdminNotificationLogDAO dao) {
        String search       = request.getParameter("search");
        String moduleFilter = request.getParameter("module");
        String statusFilter = request.getParameter("status");
        int page            = parseInt(request.getParameter("page"), 1);

        int total      = dao.countActivityLogs(search, moduleFilter, statusFilter);
        int totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
        if (page > totalPages) page = totalPages;

        List<Map<String, Object>> logs =
                dao.getActivityLogs(search, moduleFilter, statusFilter, page, PAGE_SIZE);
        Map<String, Object> stats   = dao.getActivityLogStats();
        List<String>        modules = dao.getDistinctModules();

        request.setAttribute("activityLogs",  logs);
        request.setAttribute("logStats",      stats);
        request.setAttribute("moduleOptions", modules);
        request.setAttribute("currentPage",   page);
        request.setAttribute("totalPages",    totalPages);
        request.setAttribute("totalRecords",  total);
    }

    private int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }

    private void appendParam(StringBuilder sb, String key, String value) {
        if (value != null && !value.isBlank()) {
            sb.append("&").append(key).append("=").append(value);
        }
    }
}
