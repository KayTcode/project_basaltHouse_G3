package dal;

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


    public List<TableSession> getActiveSessions(int tableId) {
        List<TableSession> list = new ArrayList<>();
        if (connection == null) {
            System.err.println("[TableSessionDAO] getActiveSessions: connection is NULL");
            return list;
        }
        String sql = tableId > 0
            ? "SELECT * FROM TableSessions WHERE Status IN ('ACTIVE', 'Open') AND IsDeleted = 0 AND TableId = ? ORDER BY OpenedAt DESC"
            : "SELECT * FROM TableSessions WHERE Status IN ('ACTIVE', 'Open') AND IsDeleted = 0 ORDER BY OpenedAt DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (tableId > 0) ps.setInt(1, tableId);
            try (ResultSet rs = ps.executeQuery()) {
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
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
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
}
