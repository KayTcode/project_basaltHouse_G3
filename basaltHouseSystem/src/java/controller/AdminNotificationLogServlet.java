package controller;

import dao.AdminCustomerDAO;
import dao.AdminDiscountDAO;
import dao.AdminNotificationLogDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.ActivityLog;
import model.DiscountCode;
import model.MembershipRank;
import services.ActivityLogService;

/**
 * Servlet quản lý module Thông báo &amp; Nhật ký hoạt động.
 * URL: /admin/logs
 *
 * @author KayT
 */
@WebServlet(name = "AdminNotificationLogServlet", urlPatterns = {"/admin/logs"})
public class AdminNotificationLogServlet extends HttpServlet {

    private static final int PAGE_SIZE = 15;
    private final ActivityLogService logService = new ActivityLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ── Tab đang hiển thị: notifications | activitylogs | sendvoucher ────
        String tab = request.getParameter("tab");
        if (tab == null || tab.isBlank()) tab = "notifications";
        request.setAttribute("tab", tab);

        AdminNotificationLogDAO dao = new AdminNotificationLogDAO();

        if ("activitylogs".equals(tab)) {
            loadActivityLogs(request, dao);
        } else if ("sendvoucher".equals(tab)) {
            loadSendVoucherData(request, dao);
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
            // ── Send Voucher & Notification ───────────────────────────────
            case "sendVoucher" -> {
                handleSendVoucher(request, dao);
                tab = "sendvoucher";
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

    private void handleSendVoucher(HttpServletRequest request, AdminNotificationLogDAO dao) {
        int discountId = parseInt(request.getParameter("discountId"), 0);
        String title = request.getParameter("title");
        String message = request.getParameter("message");
        String targetMode = request.getParameter("targetMode"); // "selected", "all", "rank"
        int targetRankId = parseInt(request.getParameter("targetRankId"), 0);
        String[] accountIdStrs = request.getParameterValues("accountIds");

        if (discountId <= 0) {
            request.getSession().setAttribute("errorMessage", "Vui lòng chọn mã giảm giá!");
            return;
        }

        if (title == null || title.isBlank()) {
            title = "Bạn nhận được mã giảm giá mới! 🎁";
        }
        if (message == null || message.isBlank()) {
            message = "Chúc mừng bạn đã nhận được mã giảm giá từ Basalt House. Hãy kiểm tra và sử dụng ngay!";
        }

        List<Integer> targetAccountIds = new ArrayList<>();
        if ("all".equals(targetMode)) {
            List<Map<String, Object>> customers = dao.getCustomersForVoucherSending(null);
            for (Map<String, Object> c : customers) {
                targetAccountIds.add((Integer) c.get("accountId"));
            }
        } else if ("rank".equals(targetMode)) {
            List<Map<String, Object>> customers = dao.getCustomersForVoucherSending(targetRankId);
            for (Map<String, Object> c : customers) {
                targetAccountIds.add((Integer) c.get("accountId"));
            }
        } else { // "selected"
            if (accountIdStrs != null) {
                for (String s : accountIdStrs) {
                    int accId = parseInt(s, 0);
                    if (accId > 0) targetAccountIds.add(accId);
                }
            }
        }

        if (targetAccountIds.isEmpty()) {
            request.getSession().setAttribute("errorMessage", "Không có khách hàng nào được chọn!");
            return;
        }

        AdminDiscountDAO discountDAO = new AdminDiscountDAO();
        int successCount = 0;
        int alreadyCount = 0;

        for (int accId : targetAccountIds) {
            String giftRes = discountDAO.giftDiscountToCustomer(accId, discountId);
            if ("success".equals(giftRes)) {
                successCount++;
                dao.insertNotification(accId, title, message);
            } else if ("already_gifted".equals(giftRes)) {
                alreadyCount++;
            }
        }

        int adminId = getAdminId(request);
        try {
            ActivityLog log = new ActivityLog(adminId, "GIFT_VOUCHER", "Voucher", discountId,
                    "TargetMode=" + targetMode,
                    "Tặng mã ID=" + discountId + " cho " + successCount + " khách hàng (Thành công: " + successCount + ", Đã có: " + alreadyCount + ")",
                    "SUCCESS", 0, LocalDateTime.now());
            logService.ctreatActiveLog(log);
        } catch (Exception ignored) {}

        if (successCount > 0) {
            String msg = "✅ Đã tặng mã cho " + successCount + " khách hàng thành công!";
            if (alreadyCount > 0) msg += " (" + alreadyCount + " người đã có mã này từ trước).";
            request.getSession().setAttribute("toastMessage", msg);
        } else if (alreadyCount > 0) {
            request.getSession().setAttribute("errorMessage", "⚠️ Tất cả khách hàng được chọn đều đã sở hữu mã giảm giá này!");
        } else {
            request.getSession().setAttribute("errorMessage", "❌ Có lỗi xảy ra khi tặng mã giảm giá.");
        }
    }

    private void loadSendVoucherData(HttpServletRequest request, AdminNotificationLogDAO dao) {
        AdminDiscountDAO discountDAO = new AdminDiscountDAO();
        AdminCustomerDAO customerDAO = new AdminCustomerDAO();

        int filterRankId = parseInt(request.getParameter("filterRankId"), 0);

        List<DiscountCode> activeDiscounts = discountDAO.getActiveDiscountsForGift();
        List<MembershipRank> ranks = customerDAO.getAllRanks();
        List<Map<String, Object>> customers = dao.getCustomersForVoucherSending(filterRankId > 0 ? filterRankId : null);

        request.setAttribute("activeDiscounts", activeDiscounts);
        request.setAttribute("ranks", ranks);
        request.setAttribute("customers", customers);
        request.setAttribute("filterRankId", filterRankId);
    }

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

    private int getAdminId(HttpServletRequest request) {
        Object obj = request.getSession(false) != null
                ? request.getSession(false).getAttribute("currentUser") : null;
        if (obj instanceof dto.UserLoginDTO) return ((dto.UserLoginDTO) obj).getAccountId();
        return 0;
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
