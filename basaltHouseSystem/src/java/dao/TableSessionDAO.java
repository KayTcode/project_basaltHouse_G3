package dao;

import dao.DBContext;
import model.TableSession;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class TableSessionDAO extends DBContext {


    public int getActiveGuestCount(int tableId) {
        if (connection == null) {
            System.err.println("[TableSessionDAO] getActiveGuestCount: connection is NULL");
            return 0;
        }
        String sql = """
            SELECT ISNULL(SUM(GuestCount), 0)
            FROM TableSessions
            WHERE TableId = ? AND Status IN ('ACTIVE', 'Open') AND IsDeleted = 0
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tableId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
 public int getActiveSessionId(int tableId) {
        if (connection == null) return -1;
        String sql = "SELECT TOP 1 SessionId FROM TableSessions WHERE TableId = ? AND Status IN ('ACTIVE', 'Open') AND IsDeleted = 0 ORDER BY OpenedAt DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tableId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }
    public String createSession(int tableId, String tableCode, int guestCount, Integer cashierId) {
        String sessionCode = "TS-" + tableCode + "-"
                + LocalDateTime.now().toString().replaceAll("[^0-9]", "").substring(0, 14);

        System.err.println("[TableSessionDAO] createSession called: tableId=" + tableId
                + ", tableCode=" + tableCode + ", guestCount=" + guestCount
                + ", cashierId=" + cashierId + ", sessionCode=" + sessionCode);

        if (connection == null) {
            System.err.println("[TableSessionDAO] ERROR: connection is NULL — ConfigLoader may have failed.");
            return null;
        }

        String sql = """
            INSERT INTO TableSessions (SessionCode, TableId, CashierId, GuestCount, Status, OpenedAt, IsDeleted)
            VALUES (?, ?, ?, ?, 'ACTIVE', GETDATE(), 0)
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, sessionCode);
            ps.setInt(2, tableId);
            if (cashierId != null) ps.setInt(3, cashierId);
            else                   ps.setNull(3, Types.INTEGER);
            ps.setInt(4, guestCount);

            int rows = ps.executeUpdate();
            System.err.println("[TableSessionDAO] INSERT rows affected: " + rows);
            return rows > 0 ? sessionCode : null;
        } catch (SQLException ex) {
            System.err.println("[TableSessionDAO] SQLException: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }


    public List<TableSession> getAllActiveSessions() {
        List<TableSession> list = new ArrayList<>();
        if (connection == null) {
            System.err.println("[TableSessionDAO] getAllActiveSessions: connection is NULL");
            return list;
        }
        String sql = "SELECT * FROM TableSessions WHERE Status IN ('ACTIVE', 'Open') AND IsDeleted = 0 ORDER BY OpenedAt DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TableSession s = new TableSession();
                s.setSessionId(rs.getInt("SessionId"));
                s.setSessionCode(rs.getString("SessionCode"));
                s.setTableId(rs.getInt("TableId"));
                Object cid = rs.getObject("CashierId");
                s.setCashierId(cid != null ? (Integer) cid : null);
                s.setGuestCount(rs.getInt("GuestCount"));
                s.setStatus(rs.getString("Status"));
                Timestamp opened = rs.getTimestamp("OpenedAt");
                if (opened != null) s.setOpenedAt(opened.toLocalDateTime());
                s.setIsDeleted(rs.getBoolean("IsDeleted"));
                list.add(s);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }



    public TableSession getSessionById(int sessionId) {
        if (connection == null) {
            System.err.println("[TableSessionDAO] getSessionById: connection is NULL");
            return null;
        }
        String sql = "SELECT * FROM TableSessions WHERE SessionId = ? AND IsDeleted = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TableSession s = new TableSession();
                    s.setSessionId(rs.getInt("SessionId"));
                    s.setSessionCode(rs.getString("SessionCode"));
                    s.setTableId(rs.getInt("TableId"));
                    Object cid = rs.getObject("CashierId");
                    s.setCashierId(cid != null ? (Integer) cid : null);
                    s.setGuestCount(rs.getInt("GuestCount"));
                    s.setStatus(rs.getString("Status"));
                    Timestamp opened = rs.getTimestamp("OpenedAt");
                    if (opened != null) s.setOpenedAt(opened.toLocalDateTime());
                    Timestamp closed = rs.getTimestamp("ClosedAt");
                    if (closed != null) s.setClosedAt(closed.toLocalDateTime());
                    s.setIsDeleted(rs.getBoolean("IsDeleted"));
                    return s;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public boolean closeSession(int sessionId) {
        if (connection == null) {
            System.err.println("[TableSessionDAO] closeSession: connection is NULL");
            return false;
        }
        String sql = "UPDATE TableSessions SET Status = 'COMPLETED', ClosedAt = GETDATE() WHERE SessionId = ? AND Status IN ('ACTIVE', 'Open') AND IsDeleted = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public String moveSession(int sessionId, int newTableId) {
        if (connection == null) return "ERR:Không có kết nối DB.";

        // 1. Lấy session hiện tại
        TableSession session = getSessionById(sessionId);
        if (session == null) return "ERR:Session không tồn tại.";
        if (session.getTableId() == newTableId) return "ERR:Bàn mới phải khác bàn hiện tại.";

        // 2. Kiểm tra bàn mới có session đang hoạt động không (không cho chuyển vào bàn đang bận)
        String checkSql = "SELECT COUNT(*) FROM TableSessions WHERE TableId = ? AND Status IN ('ACTIVE','Open') AND IsDeleted = 0";
        try (PreparedStatement ps = connection.prepareStatement(checkSql)) {
            ps.setInt(1, newTableId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return "ERR:Bàn đích đang có khách, không thể chuyển.";
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "ERR:Lỗi kiểm tra bàn đích.";
        }

        // 3. Cập nhật TableId trong session
        String updateSql = "UPDATE TableSessions SET TableId = ? WHERE SessionId = ? AND Status IN ('ACTIVE','Open') AND IsDeleted = 0";
        try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
            ps.setInt(1, newTableId);
            ps.setInt(2, sessionId);
            int rows = ps.executeUpdate();
            return rows > 0 ? "OK" : "ERR:Không thể cập nhật session.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "ERR:Lỗi cập nhật session: " + ex.getMessage();
        }
    }
}
