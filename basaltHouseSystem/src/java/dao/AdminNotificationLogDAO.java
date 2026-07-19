package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO quản lý Thông báo (Notifications) và Nhật ký hoạt động (ActivityLogs)
 * cho module Admin.
 *
 * @author KayT
 */
public class AdminNotificationLogDAO extends DBContext {

    // ════════════════════════════════════════════════════════
    //  NOTIFICATIONS
    // ════════════════════════════════════════════════════════

    /**
     * Lấy danh sách thông báo có lọc, phân trang.
     */
    public List<Map<String, Object>> getNotifications(String search, String deletedFilter,
            int page, int pageSize) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            int offset = (page - 1) * pageSize;

            StringBuilder sql = new StringBuilder("""
                SELECT n.NotificationId, n.AccountId,
                       a.Username, a.Email,
                       n.Title, n.Message, n.IsDeleted, n.CreatedAt
                FROM Notifications n
                LEFT JOIN Accounts a ON n.AccountId = a.AccountId
                WHERE 1=1
                """);

            if (search != null && !search.isBlank()) {
                sql.append(" AND (n.Title LIKE ? OR n.Message LIKE ? OR a.Username LIKE ?)");
            }
            if ("active".equals(deletedFilter)) {
                sql.append(" AND n.IsDeleted = 0");
            } else if ("deleted".equals(deletedFilter)) {
                sql.append(" AND n.IsDeleted = 1");
            }

            sql.append(" ORDER BY n.CreatedAt DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

            PreparedStatement ps = connection.prepareStatement(sql.toString());
            int idx = 1;
            if (search != null && !search.isBlank()) {
                String like = "%" + search.trim() + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }
            ps.setInt(idx++, offset);
            ps.setInt(idx, pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("notificationId", rs.getInt("NotificationId"));
                row.put("accountId",      rs.getInt("AccountId"));
                row.put("username",       rs.getString("Username") != null ? rs.getString("Username") : "—");
                row.put("email",          rs.getString("Email")    != null ? rs.getString("Email")    : "—");
                row.put("title",          rs.getString("Title")    != null ? rs.getString("Title")    : "");
                row.put("message",        rs.getString("Message")  != null ? rs.getString("Message")  : "");
                row.put("isDeleted",      rs.getBoolean("IsDeleted"));
                row.put("createdAt",      rs.getTimestamp("CreatedAt") != null
                        ? rs.getTimestamp("CreatedAt").toLocalDateTime() : null);
                list.add(row);
            }
        } catch (Exception e) {
            System.err.println("[AdminNotificationLogDAO] getNotifications: " + e.getMessage());
        }
        return list;
    }

    /**
     * Đếm tổng số thông báo theo bộ lọc.
     */
    public int countNotifications(String search, String deletedFilter) {
        try {
            StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*) AS cnt
                FROM Notifications n
                LEFT JOIN Accounts a ON n.AccountId = a.AccountId
                WHERE 1=1
                """);
            if (search != null && !search.isBlank()) {
                sql.append(" AND (n.Title LIKE ? OR n.Message LIKE ? OR a.Username LIKE ?)");
            }
            if ("active".equals(deletedFilter)) {
                sql.append(" AND n.IsDeleted = 0");
            } else if ("deleted".equals(deletedFilter)) {
                sql.append(" AND n.IsDeleted = 1");
            }

            PreparedStatement ps = connection.prepareStatement(sql.toString());
            int idx = 1;
            if (search != null && !search.isBlank()) {
                String like = "%" + search.trim() + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx, like);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("cnt");
        } catch (Exception e) {
            System.err.println("[AdminNotificationLogDAO] countNotifications: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Thống kê nhanh cho Notifications.
     */
    public Map<String, Object> getNotificationStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", 0);
        stats.put("active", 0);
        stats.put("deleted", 0);
        stats.put("todayCount", 0);
        try {
            String sql = """
                SELECT
                    COUNT(*) AS total,
                    SUM(CASE WHEN IsDeleted = 0 THEN 1 ELSE 0 END) AS active,
                    SUM(CASE WHEN IsDeleted = 1 THEN 1 ELSE 0 END) AS deleted,
                    SUM(CASE WHEN CAST(CreatedAt AS DATE) = CAST(GETDATE() AS DATE) THEN 1 ELSE 0 END) AS todayCount
                FROM Notifications
                """;
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                stats.put("total",      rs.getInt("total"));
                stats.put("active",     rs.getInt("active"));
                stats.put("deleted",    rs.getInt("deleted"));
                stats.put("todayCount", rs.getInt("todayCount"));
            }
        } catch (Exception e) {
            System.err.println("[AdminNotificationLogDAO] getNotificationStats: " + e.getMessage());
        }
        return stats;
    }

    /**
     * Soft-delete hoặc restore một thông báo.
     */
    public boolean toggleDeleteNotification(int notificationId, boolean isDeleted) {
        try {
            String sql = "UPDATE Notifications SET IsDeleted = ? WHERE NotificationId = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setBoolean(1, isDeleted);
            ps.setInt(2, notificationId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[AdminNotificationLogDAO] toggleDeleteNotification: " + e.getMessage());
        }
        return false;
    }

    /**
     * Xóa vĩnh viễn một thông báo.
     */
    public boolean hardDeleteNotification(int notificationId) {
        try {
            String sql = "DELETE FROM Notifications WHERE NotificationId = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, notificationId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[AdminNotificationLogDAO] hardDeleteNotification: " + e.getMessage());
        }
        return false;
    }

    // ════════════════════════════════════════════════════════
    //  ACTIVITY LOGS
    // ════════════════════════════════════════════════════════

    /**
     * Lấy danh sách activity logs có lọc, phân trang.
     */
    public List<Map<String, Object>> getActivityLogs(String search, String module,
            String statusFilter, int page, int pageSize) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            int offset = (page - 1) * pageSize;

            StringBuilder sql = new StringBuilder("""
                SELECT al.LogId, al.AccountId,
                       a.Username, a.Email,
                       al.Action, al.Module, al.TargetId,
                       al.OldValue, al.NewValue, al.Status,
                       al.IsDeleted, al.CreatedAt
                FROM ActivityLogs al
                LEFT JOIN Accounts a ON al.AccountId = a.AccountId
                WHERE al.IsDeleted = 0
                """);

            if (search != null && !search.isBlank()) {
                sql.append(" AND (al.Action LIKE ? OR al.Module LIKE ? OR a.Username LIKE ? OR al.Status LIKE ?)");
            }
            if (module != null && !module.isBlank()) {
                sql.append(" AND al.Module = ?");
            }
            if (statusFilter != null && !statusFilter.isBlank()) {
                sql.append(" AND al.Status = ?");
            }

            sql.append(" ORDER BY al.CreatedAt DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

            PreparedStatement ps = connection.prepareStatement(sql.toString());
            int idx = 1;
            if (search != null && !search.isBlank()) {
                String like = "%" + search.trim() + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }
            if (module != null && !module.isBlank()) {
                ps.setString(idx++, module);
            }
            if (statusFilter != null && !statusFilter.isBlank()) {
                ps.setString(idx++, statusFilter);
            }
            ps.setInt(idx++, offset);
            ps.setInt(idx, pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("logId",     rs.getInt("LogId"));
                row.put("accountId", rs.getInt("AccountId"));
                row.put("username",  rs.getString("Username") != null ? rs.getString("Username") : "System");
                row.put("email",     rs.getString("Email")    != null ? rs.getString("Email")    : "—");
                row.put("action",    rs.getString("Action")   != null ? rs.getString("Action")   : "—");
                row.put("module",    rs.getString("Module")   != null ? rs.getString("Module")   : "—");
                row.put("targetId",  rs.getInt("TargetId"));
                row.put("oldValue",  rs.getString("OldValue") != null ? rs.getString("OldValue") : "");
                row.put("newValue",  rs.getString("NewValue") != null ? rs.getString("NewValue") : "");
                row.put("status",    rs.getString("Status")   != null ? rs.getString("Status")   : "—");
                row.put("isDeleted", rs.getInt("IsDeleted"));
                row.put("createdAt", rs.getTimestamp("CreatedAt") != null
                        ? rs.getTimestamp("CreatedAt").toLocalDateTime() : null);
                list.add(row);
            }
        } catch (Exception e) {
            System.err.println("[AdminNotificationLogDAO] getActivityLogs: " + e.getMessage());
        }
        return list;
    }

    /**
     * Đếm tổng số activity logs theo bộ lọc.
     */
    public int countActivityLogs(String search, String module, String statusFilter) {
        try {
            StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*) AS cnt
                FROM ActivityLogs al
                LEFT JOIN Accounts a ON al.AccountId = a.AccountId
                WHERE al.IsDeleted = 0
                """);
            if (search != null && !search.isBlank()) {
                sql.append(" AND (al.Action LIKE ? OR al.Module LIKE ? OR a.Username LIKE ? OR al.Status LIKE ?)");
            }
            if (module != null && !module.isBlank()) {
                sql.append(" AND al.Module = ?");
            }
            if (statusFilter != null && !statusFilter.isBlank()) {
                sql.append(" AND al.Status = ?");
            }

            PreparedStatement ps = connection.prepareStatement(sql.toString());
            int idx = 1;
            if (search != null && !search.isBlank()) {
                String like = "%" + search.trim() + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }
            if (module != null && !module.isBlank()) {
                ps.setString(idx++, module);
            }
            if (statusFilter != null && !statusFilter.isBlank()) {
                ps.setString(idx, statusFilter);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("cnt");
        } catch (Exception e) {
            System.err.println("[AdminNotificationLogDAO] countActivityLogs: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Thống kê nhanh cho ActivityLogs.
     */
    public Map<String, Object> getActivityLogStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", 0);
        stats.put("successCount", 0);
        stats.put("failCount", 0);
        stats.put("todayCount", 0);
        try {
            String sql = """
                SELECT
                    COUNT(*) AS total,
                    SUM(CASE WHEN Status = 'SUCCESS' THEN 1 ELSE 0 END) AS successCount,
                    SUM(CASE WHEN Status = 'FAIL'    THEN 1 ELSE 0 END) AS failCount,
                    SUM(CASE WHEN CAST(CreatedAt AS DATE) = CAST(GETDATE() AS DATE) THEN 1 ELSE 0 END) AS todayCount
                FROM ActivityLogs
                WHERE IsDeleted = 0
                """;
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                stats.put("total",        rs.getInt("total"));
                stats.put("successCount", rs.getInt("successCount"));
                stats.put("failCount",    rs.getInt("failCount"));
                stats.put("todayCount",   rs.getInt("todayCount"));
            }
        } catch (Exception e) {
            System.err.println("[AdminNotificationLogDAO] getActivityLogStats: " + e.getMessage());
        }
        return stats;
    }

    /**
     * Lấy danh sách tên module riêng biệt để dùng trong dropdown filter.
     */
    public List<String> getDistinctModules() {
        List<String> modules = new ArrayList<>();
        try {
            String sql = "SELECT DISTINCT Module FROM ActivityLogs WHERE IsDeleted = 0 AND Module IS NOT NULL ORDER BY Module";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modules.add(rs.getString("Module"));
            }
        } catch (Exception e) {
            System.err.println("[AdminNotificationLogDAO] getDistinctModules: " + e.getMessage());
        }
        return modules;
    }

    /**
     * Soft-delete một activity log.
     */
    public boolean softDeleteLog(int logId) {
        try {
            String sql = "UPDATE ActivityLogs SET IsDeleted = 1 WHERE LogId = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, logId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[AdminNotificationLogDAO] softDeleteLog: " + e.getMessage());
        }
        return false;
    }
}
