package dao;

import model.Table;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Layer for Table entity.
 * @author BasaltHouse Team
 */
public class TableDAO extends DBContext {


    public List<Table> getAllTablesWithOccupancy() {
        List<Table> list = new ArrayList<>();
        if (connection == null) {
            System.err.println("[TableDAO] getAllTablesWithOccupancy: connection is NULL");
            return list;
        }
        String sql = """
            SELECT t.TableId, t.TableCode, t.Area, t.Capacity, t.Status, t.IsDeleted,
                   ISNULL(SUM(CASE WHEN ts.Status IN ('ACTIVE', 'Open') THEN ts.GuestCount ELSE 0 END), 0) AS CurrentGuests
            FROM [Tables] t
            LEFT JOIN TableSessions ts ON ts.TableId = t.TableId AND ts.IsDeleted = 0
            WHERE t.IsDeleted = 0
            GROUP BY t.TableId, t.TableCode, t.Area, t.Capacity, t.Status, t.IsDeleted
            ORDER BY t.TableCode
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Table t = new Table();
                t.setTableId(rs.getInt("TableId"));
                t.setTableCode(rs.getString("TableCode"));
                t.setArea(rs.getString("Area"));
                t.setCapacity(rs.getInt("Capacity"));
                t.setCurrentGuests(rs.getInt("CurrentGuests"));
                t.setStatus(rs.getString("Status"));
                t.setIsDeleted(rs.getBoolean("IsDeleted"));
                list.add(t);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }



    public Table getTableById(int tableId) {
        if (connection == null) {
            System.err.println("[TableDAO] getTableById: connection is NULL");
            return null;
        }
        String sql = """
            SELECT t.TableId, t.TableCode, t.Area, t.Capacity, t.Status, t.IsDeleted,
                   ISNULL(SUM(CASE WHEN ts.Status IN ('ACTIVE', 'Open') THEN ts.GuestCount ELSE 0 END), 0) AS CurrentGuests
            FROM [Tables] t
            LEFT JOIN TableSessions ts ON ts.TableId = t.TableId AND ts.IsDeleted = 0
            WHERE t.TableId = ? AND t.IsDeleted = 0
            GROUP BY t.TableId, t.TableCode, t.Area, t.Capacity, t.Status, t.IsDeleted
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tableId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Table t = new Table();
                    t.setTableId(rs.getInt("TableId"));
                    t.setTableCode(rs.getString("TableCode"));
                    t.setArea(rs.getString("Area"));
                    t.setCapacity(rs.getInt("Capacity"));
                    t.setCurrentGuests(rs.getInt("CurrentGuests"));
                    t.setStatus(rs.getString("Status"));
                    t.setIsDeleted(rs.getBoolean("IsDeleted"));
                    return t;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public boolean addTable(String tableCode, String area, int capacity) {
        if (connection == null) {
            System.err.println("[TableDAO] addTable: connection is NULL");
            return false;
        }
        String sql = "INSERT INTO [Tables] (TableCode, Area, Capacity, Status, IsDeleted) "
                   + "VALUES (?, ?, ?, 'Available', 0)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tableCode.trim());
            ps.setString(2, area.trim());
            ps.setInt(3, capacity);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }


    public boolean isTableCodeExists(String tableCode) {
        if (connection == null) return false;
        String sql = "SELECT 1 FROM [Tables] WHERE TableCode = ? AND IsDeleted = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tableCode.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }


    public int deleteTable(int tableId) {
        if (connection == null) {
            System.err.println("[TableDAO] deleteTable: connection is NULL");
            return -1;
        }
        // Kiểm tra session đang hoạt động
        String checkSql = "SELECT COUNT(*) FROM TableSessions "
                        + "WHERE TableId = ? AND Status IN ('ACTIVE','Open') AND IsDeleted = 0";
        try (PreparedStatement ps = connection.prepareStatement(checkSql)) {
            ps.setInt(1, tableId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) return 0; // đang có khách
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }
        // Soft-delete
        String deleteSql = "UPDATE [Tables] SET IsDeleted = 1 WHERE TableId = ? AND IsDeleted = 0";
        try (PreparedStatement ps = connection.prepareStatement(deleteSql)) {
            ps.setInt(1, tableId);
            return ps.executeUpdate() > 0 ? 1 : -1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }
    }
}
