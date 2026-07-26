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
 
 */
public class AdminNotificationLogDAO extends DBContext {

    /**
     * Đoạn JOIN dùng chung để lấy "tên hiển thị" của tài khoản.
     * Bảng Accounts KHÔNG có cột Username, chỉ có Email, nên ta lấy
     * FullName từ các bảng con (Customers/Staffs/Cashiers/Shippers)
     * tương ứng với vai trò của tài khoản; nếu không có, fallback về Email.
     */
    private static final String ACCOUNT_JOIN = """
            LEFT JOIN Accounts a  ON x.AccountId = a.AccountId
            LEFT JOIN Customers c ON a.AccountId = c.AccountId
            LEFT JOIN Staffs   s  ON a.AccountId = s.AccountId
            LEFT JOIN Cashiers ca ON a.AccountId = ca.AccountId
            LEFT JOIN Shippers sh ON a.AccountId = sh.AccountId
            """;

    private static final String DISPLAY_NAME_EXPR =
            "COALESCE(c.FullName, s.FullName, ca.FullName, sh.FullName, a.Email)";

    // ════════════════════════════════════════════════════════
    //  NOTIFICATIONS
    // ════════════════════════════════════════════════════════

    /**
     * Lấy danh sách thông báo có lọc, phân trang.
     */
    public List<Map<String, Object>> getNotifications(String search, String deletedFilter,
            int page, int pageSize) {
        List<Map<String, Object>> list = new ArrayList<>();
        int offset = Math.max(0, (page - 1) * pageSize);

        StringBuilder sql = new StringBuilder("""
            SELECT x.NotificationId, x.AccountId,
                   %s AS DisplayName,
                   a.Email,
                   x.Title, x.Message, x.IsDeleted, x.CreatedAt
            FROM Notifications x
            %s
            WHERE 1=1
            """.formatted(DISPLAY_NAME_EXPR, ACCOUNT_JOIN));

        if (search != null && !search.isBlank()) {
            sql.append(" AND (x.Title LIKE ? OR x.Message LIKE ? OR ")
               .append(DISPLAY_NAME_EXPR).append(" LIKE ?)");
        }
        if ("active".equals(deletedFilter)) {
            sql.append(" AND x.IsDeleted = 0");
        } else if ("deleted".equals(deletedFilter)) {
            sql.append(" AND x.IsDeleted = 1");
        }

        sql.append(" ORDER BY x.CreatedAt DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            if (search != null && !search.isBlank()) {
                String like = "%" + search.trim() + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }
            ps.setInt(idx++, offset);
            ps.setInt(idx, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("notificationId", rs.getInt("NotificationId"));
                    row.put("accountId",      rs.getInt("AccountId"));
                    row.put("username",       rs.getString("DisplayName") != null ? rs.getString("DisplayName") : "—");
                    row.put("email",          rs.getString("Email")       != null ? rs.getString("Email")       : "—");
                    row.put("title",          rs.getString("Title")       != null ? rs.getString("Title")       : "");
                    row.put("message",        rs.getString("Message")     != null ? rs.getString("Message")     : "");
                    row.put("isDeleted",      rs.getBoolean("IsDeleted"));
                    row.put("createdAt",      rs.getTimestamp("CreatedAt") != null
                            ? rs.getTimestamp("CreatedAt").toLocalDateTime() : null);
                    list.add(row);
                }
            }
        } catch (Exception e) {
            System.err.println("[AdminNotificationLogDAO] getNotifications: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Đếm tổng số thông báo theo bộ lọc.
     */
    public int countNotifications(String search, String deletedFilter) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*) AS cnt
            FROM Notifications x
            %s
            WHERE 1=1
            """.formatted(ACCOUNT_JOIN));

        if (search != null && !search.isBlank()) {
            sql.append(" AND (x.Title LIKE ? OR x.Message LIKE ? OR ")
               .append(DISPLAY_NAME_EXPR).append(" LIKE ?)");
        }
        if ("active".equals(deletedFilter)) {
            sql.append(" AND x.IsDeleted = 0");
        } else if ("deleted".equals(deletedFilter)) {
            sql.append(" AND x.IsDeleted = 1");
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            if (search != null && !search.isBlank()) {
                String like = "%" + search.trim() + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("cnt");
            }
        } catch (Exception e) {
            System.err.println("[AdminNotificationLogDAO] countNotifications: " + e.getMessage());
            e.printStackTrace();
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
        String sql = """
            SELECT
                COUNT(*) AS total,
                SUM(CASE WHEN IsDeleted = 0 THEN 1 ELSE 0 END) AS active,
                SUM(CASE WHEN IsDeleted = 1 THEN 1 ELSE 0 END) AS deleted,
                SUM(CASE WHEN CAST(CreatedAt AS DATE) = CAST(GETDATE() AS DATE) THEN 1 ELSE 0 END) AS todayCount
            FROM Notifications
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stats.put("total",      rs.getInt("total"));
                stats.put("active",     rs.getInt("active"));
                stats.put("deleted",    rs.getInt("deleted"));
                stats.put("todayCount", rs.getInt("todayCount"));
            }
        } catch (Exception e) {
            System.err.println("[AdminNotificationLogDAO] getNotificationStats: " + e.getMessage());
            e.printStackTrace();
        }
        return stats;
    }

    /**
     * Soft-delete hoặc restore một thông báo.
     */
    public boolean toggleDeleteNotification(int notificationId, boolean isDeleted) {
        String sql = "UPDATE Notifications SET IsDeleted = ? WHERE NotificationId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, isDeleted);
            ps.setInt(2, notificationId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[AdminNotificationLogDAO] toggleDeleteNotification: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xóa vĩnh viễn một thông báo.
     */
    public boolean hardDeleteNotification(int notificationId) {
        String sql = "DELETE FROM Notifications WHERE NotificationId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[AdminNotificationLogDAO] hardDeleteNotification: " + e.getMessage());
            e.printStackTrace();
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
        int offset = Math.max(0, (page - 1) * pageSize);

        StringBuilder sql = new StringBuilder("""
            SELECT x.LogId, x.AccountId,
                   %s AS DisplayName,
                   a.Email,
                   x.Action, x.Module, x.TargetId,
                   x.OldValue, x.NewValue, x.Status,
                   x.IsDeleted, x.CreatedAt
            FROM ActivityLogs x
            %s
            WHERE x.IsDeleted = 0
            """.formatted(DISPLAY_NAME_EXPR, ACCOUNT_JOIN));

        if (search != null && !search.isBlank()) {
            sql.append(" AND (x.Action LIKE ? OR x.Module LIKE ? OR ")
               .append(DISPLAY_NAME_EXPR).append(" LIKE ? OR x.Status LIKE ?)");
        }
        if (module != null && !module.isBlank()) {
            sql.append(" AND x.Module = ?");
        }
        if (statusFilter != null && !statusFilter.isBlank()) {
            sql.append(" AND x.Status = ?");
        }

        sql.append(" ORDER BY x.CreatedAt DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
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

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("logId",     rs.getInt("LogId"));
                    row.put("accountId", rs.getInt("AccountId"));
                    row.put("username",  rs.getString("DisplayName") != null ? rs.getString("DisplayName") : "System");
                    row.put("email",     rs.getString("Email")       != null ? rs.getString("Email")       : "—");
                    row.put("action",    rs.getString("Action")      != null ? rs.getString("Action")      : "—");
                    row.put("module",    rs.getString("Module")      != null ? rs.getString("Module")      : "—");
                    row.put("targetId",  rs.getInt("TargetId"));
                    row.put("oldValue",  rs.getString("OldValue")    != null ? rs.getString("OldValue")    : "");
                    row.put("newValue",  rs.getString("NewValue")    != null ? rs.getString("NewValue")    : "");
                    row.put("status",    rs.getString("Status")      != null ? rs.getString("Status")      : "—");
                    row.put("isDeleted", rs.getInt("IsDeleted"));
                    row.put("createdAt", rs.getTimestamp("CreatedAt") != null
                            ? rs.getTimestamp("CreatedAt").toLocalDateTime() : null);
                    list.add(row);
                }
            }
        } catch (Exception e) {
            System.err.println("[AdminNotificationLogDAO] getActivityLogs: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Đếm tổng số activity logs theo bộ lọc.
     */
    public int countActivityLogs(String search, String module, String statusFilter) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*) AS cnt
            FROM ActivityLogs x
            %s
            WHERE x.IsDeleted = 0
            """.formatted(ACCOUNT_JOIN));

        if (search != null && !search.isBlank()) {
            sql.append(" AND (x.Action LIKE ? OR x.Module LIKE ? OR ")
               .append(DISPLAY_NAME_EXPR).append(" LIKE ? OR x.Status LIKE ?)");
        }
        if (module != null && !module.isBlank()) {
            sql.append(" AND x.Module = ?");
        }
        if (statusFilter != null && !statusFilter.isBlank()) {
            sql.append(" AND x.Status = ?");
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
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
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("cnt");
            }
        } catch (Exception e) {
            System.err.println("[AdminNotificationLogDAO] countActivityLogs: " + e.getMessage());
            e.printStackTrace();
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
        String sql = """
            SELECT
                COUNT(*) AS total,
                SUM(CASE WHEN Status = 'SUCCESS' THEN 1 ELSE 0 END) AS successCount,
                SUM(CASE WHEN Status = 'FAIL'    THEN 1 ELSE 0 END) AS failCount,
                SUM(CASE WHEN CAST(CreatedAt AS DATE) = CAST(GETDATE() AS DATE) THEN 1 ELSE 0 END) AS todayCount
            FROM ActivityLogs
            WHERE IsDeleted = 0
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stats.put("total",        rs.getInt("total"));
                stats.put("successCount", rs.getInt("successCount"));
                stats.put("failCount",    rs.getInt("failCount"));
                stats.put("todayCount",   rs.getInt("todayCount"));
            }
        } catch (Exception e) {
            System.err.println("[AdminNotificationLogDAO] getActivityLogStats: " + e.getMessage());
            e.printStackTrace();
        }
        return stats;
    }

    /**
     * Lấy danh sách tên module riêng biệt để dùng trong dropdown filter.
     */
    public List<String> getDistinctModules() {
        List<String> modules = new ArrayList<>();
        String sql = "SELECT DISTINCT Module FROM ActivityLogs WHERE IsDeleted = 0 AND Module IS NOT NULL ORDER BY Module";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modules.add(rs.getString("Module"));
            }
        } catch (Exception e) {
            System.err.println("[AdminNotificationLogDAO] getDistinctModules: " + e.getMessage());
            e.printStackTrace();
        }
        return modules;
    }

    /**
     * Soft-delete một activity log.
     */
    public boolean softDeleteLog(int logId) {
        String sql = "UPDATE ActivityLogs SET IsDeleted = 1 WHERE LogId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, logId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[AdminNotificationLogDAO] softDeleteLog: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Thêm thông báo mới cho khách hàng.
     */
    public boolean insertNotification(int accountId, String title, String message) {
        String sql = """
            INSERT INTO Notifications (AccountId, Title, Message, IsDeleted, CreatedAt)
            VALUES (?, ?, ?, 0, GETDATE())
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ps.setString(2, title);
            ps.setString(3, message);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[AdminNotificationLogDAO] insertNotification: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lấy danh sách khách hàng để phục vụ việc chọn và tặng voucher trong tab Send Voucher.
     */
    public List<Map<String, Object>> getCustomersForVoucherSending(Integer rankId) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT c.CustomerId, c.AccountId, c.FullName, c.Phone, a.Email,
                   r.RankId, r.RankName
            FROM Customers c
            JOIN Accounts a ON c.AccountId = a.AccountId
            LEFT JOIN CustomerMemberships cm ON c.CustomerId = cm.CustomerId
            LEFT JOIN MembershipRanks r ON cm.RankId = r.RankId
            WHERE c.IsDeleted = 0 AND a.IsActive = 1
            """);

        if (rankId != null && rankId > 0) {
            sql.append(" AND r.RankId = ?");
        }
        sql.append(" ORDER BY c.FullName ASC");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            if (rankId != null && rankId > 0) {
                ps.setInt(1, rankId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("customerId", rs.getInt("CustomerId"));
                    map.put("accountId", rs.getInt("AccountId"));
                    map.put("fullName", rs.getString("FullName") != null ? rs.getString("FullName") : "—");
                    map.put("phone", rs.getString("Phone") != null ? rs.getString("Phone") : "—");
                    map.put("email", rs.getString("Email") != null ? rs.getString("Email") : "—");
                    map.put("rankId", rs.getInt("RankId"));
                    map.put("rankName", rs.getString("RankName") != null ? rs.getString("RankName") : "—");
                    list.add(map);
                }
            }
        } catch (Exception e) {
            System.err.println("[AdminNotificationLogDAO] getCustomersForVoucherSending: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}