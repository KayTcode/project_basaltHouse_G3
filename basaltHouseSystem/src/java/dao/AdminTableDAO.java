package dao;

import dto.TableDTO;
import dto.TableSessionDTO;
import model.Order;
import model.Table;
import model.TableSession;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdminTableDAO extends DBContext {

    // ══════════════════════════════════════════════════════════════════
    // 1. LẤY SƠ ĐỒ BÀN (KÈM PHIÊN ĐANG MỞ & THU NGÂN)
    // ══════════════════════════════════════════════════════════════════
    public List<TableDTO> getAllTablesWithCurrentSession() {
        List<TableDTO> list = new ArrayList<>();
        // Lấy tất cả bàn, left join với TableSessions đang 'Open' và thu ngân
        String sql = "SELECT t.*, "
                + "  ts.SessionId, ts.SessionCode, ts.CashierId, ts.GuestCount, ts.OpenedAt, "
                + "  a.FullName AS CashierName "
                + "FROM [Tables] t "
                + "LEFT JOIN TableSessions ts ON t.TableId = ts.TableId AND ts.Status = 'Open' AND ts.IsDeleted = 0 "
                + "LEFT JOIN Cashiers a ON ts.CashierId = a.CashierId "
                + "WHERE t.IsDeleted = 0 "
                + "ORDER BY t.Area, t.TableCode";

        try (PreparedStatement st = connection.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
             
            while (rs.next()) {
                Table t = new Table();
                t.setTableId(rs.getInt("TableId"));
                t.setTableCode(rs.getString("TableCode"));
                t.setArea(rs.getString("Area"));
                t.setCapacity(rs.getInt("Capacity"));
                t.setCurrentGuests(rs.getInt("CurrentGuests"));
                t.setStatus(rs.getString("Status"));
                
                TableDTO dto = new TableDTO(t);
                
                // Nếu có phiên đang mở
                int sessionId = rs.getInt("SessionId");
                if (!rs.wasNull()) {
                    TableSession ts = new TableSession();
                    ts.setSessionId(sessionId);
                    ts.setSessionCode(rs.getString("SessionCode"));
                    ts.setTableId(t.getTableId());
                    ts.setCashierId(rs.getInt("CashierId"));
                    if (rs.wasNull()) ts.setCashierId(null);
                    ts.setGuestCount(rs.getInt("GuestCount"));
                    ts.setStatus("Open");
                    Timestamp opened = rs.getTimestamp("OpenedAt");
                    if (opened != null) ts.setOpenedAt(opened.toLocalDateTime());
                    
                    dto.setCurrentSession(ts);
                    dto.setCashierName(rs.getString("CashierName"));
                }
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ══════════════════════════════════════════════════════════════════
    // 2. MỞ PHIÊN MỚI (TRANSACTION)
    // Yêu cầu: Bàn phải đang 'Available'. 
    // Insert TableSessions (Open) + Update Tables (Occupied, CurrentGuests)
    // ══════════════════════════════════════════════════════════════════
    public boolean openTableSession(int tableId, int cashierId, int guestCount) {
        String checkSql = "SELECT Status FROM [Tables] WHERE TableId = ? AND IsDeleted = 0";
        String insertSql = "INSERT INTO TableSessions (SessionCode, TableId, CashierId, GuestCount, Status, OpenedAt) "
                         + "VALUES (?, ?, ?, ?, 'Open', GETDATE())";
        String updateSql = "UPDATE [Tables] SET Status = 'Occupied', CurrentGuests = ? WHERE TableId = ?";
        
        try {
            connection.setAutoCommit(false);
            
            // 1. Kiểm tra trạng thái bàn (lock row for update nếu có thể, hoặc ít nhất check)
            try (PreparedStatement checkSt = connection.prepareStatement(checkSql)) {
                checkSt.setInt(1, tableId);
                ResultSet rs = checkSt.executeQuery();
                if (rs.next()) {
                    if (!"Available".equals(rs.getString("Status"))) {
                        connection.rollback();
                        return false; // Bàn không trống
                    }
                } else {
                    connection.rollback();
                    return false; // Không tìm thấy bàn
                }
            }
            
            // 2. Insert Session
            String sessionCode = "SES-" + System.currentTimeMillis(); // Generate mã tạm
            try (PreparedStatement insSt = connection.prepareStatement(insertSql)) {
                insSt.setString(1, sessionCode);
                insSt.setInt(2, tableId);
                insSt.setInt(3, cashierId);
                insSt.setInt(4, guestCount);
                insSt.executeUpdate();
            }
            
            // 3. Update Table
            try (PreparedStatement updSt = connection.prepareStatement(updateSql)) {
                updSt.setInt(1, guestCount);
                updSt.setInt(2, tableId);
                updSt.executeUpdate();
            }
            
            connection.commit();
            return true;
            
        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // 3. ĐÓNG PHIÊN (TRANSACTION)
    // Update TableSessions (Closed) + Update Tables (Available, CurrentGuests=0)
    // ══════════════════════════════════════════════════════════════════
    public boolean closeTableSession(int sessionId, int tableId) {
        String updSessionSql = "UPDATE TableSessions SET Status = 'Closed', ClosedAt = GETDATE() WHERE SessionId = ?";
        String updTableSql   = "UPDATE [Tables] SET Status = 'Available', CurrentGuests = 0 WHERE TableId = ?";
        
        try {
            connection.setAutoCommit(false);
            
            try (PreparedStatement st1 = connection.prepareStatement(updSessionSql)) {
                st1.setInt(1, sessionId);
                st1.executeUpdate();
            }
            
            try (PreparedStatement st2 = connection.prepareStatement(updTableSql)) {
                st2.setInt(1, tableId);
                st2.executeUpdate();
            }
            
            connection.commit();
            return true;
        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // 4. LẤY CHI TIẾT 1 PHIÊN ĐANG MỞ KÈM CÁC ĐƠN HÀNG
    // ══════════════════════════════════════════════════════════════════
    public TableSessionDTO getActiveSessionDetails(int tableId) {
        String sql = "SELECT ts.*, t.TableCode, t.Area, a.FullName AS CashierName, "
                   + "o.OrderId, o.TotalAmount, o.DiscountAmount, o.FinalAmount, o.OrderStatus, o.CreatedAt AS OrderCreatedAt "
                   + "FROM TableSessions ts "
                   + "JOIN [Tables] t ON ts.TableId = t.TableId "
                   + "LEFT JOIN Cashiers a ON ts.CashierId = a.CashierId "
                   + "LEFT JOIN Orders o ON ts.SessionId = o.TableSessionId AND o.IsDeleted = 0 "
                   + "WHERE ts.TableId = ? AND ts.Status = 'Open' AND ts.IsDeleted = 0 "
                   + "ORDER BY o.CreatedAt ASC";
                   
        TableSessionDTO dto = null;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, tableId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    if (dto == null) {
                        TableSession ts = new TableSession();
                        ts.setSessionId(rs.getInt("SessionId"));
                        ts.setSessionCode(rs.getString("SessionCode"));
                        ts.setTableId(tableId);
                        ts.setCashierId(rs.getInt("CashierId"));
                        ts.setGuestCount(rs.getInt("GuestCount"));
                        ts.setStatus(rs.getString("Status"));
                        Timestamp opened = rs.getTimestamp("OpenedAt");
                        if (opened != null) ts.setOpenedAt(opened.toLocalDateTime());
                        
                        dto = new TableSessionDTO(ts);
                        dto.setTableCode(rs.getString("TableCode"));
                        dto.setArea(rs.getString("Area"));
                        dto.setCashierName(rs.getString("CashierName"));
                    }
                    
                    int orderId = rs.getInt("OrderId");
                    if (!rs.wasNull()) {
                        Order o = new Order();
                        o.setOrderId(orderId);
                        o.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                        o.setDiscountAmount(rs.getBigDecimal("DiscountAmount"));
                        o.setFinalAmount(rs.getBigDecimal("FinalAmount"));
                        o.setOrderStatus(rs.getString("OrderStatus"));
                        Timestamp oct = rs.getTimestamp("OrderCreatedAt");
                        if (oct != null) o.setCreatedAt(oct.toLocalDateTime());
                        dto.addOrder(o);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dto;
    }

    // ══════════════════════════════════════════════════════════════════
    // 5. LẤY LỊCH SỬ CÁC PHIÊN (PHÂN TRANG)
    // ══════════════════════════════════════════════════════════════════
    public List<TableSessionDTO> getSessionHistory(int offset, int limit) {
        List<TableSessionDTO> list = new ArrayList<>();
        String sql = "SELECT ts.*, t.TableCode, t.Area, a.FullName AS CashierName "
                   + "FROM TableSessions ts "
                   + "JOIN [Tables] t ON ts.TableId = t.TableId "
                   + "LEFT JOIN Cashiers a ON ts.CashierId = a.CashierId "
                   + "WHERE ts.IsDeleted = 0 "
                   + "ORDER BY ts.OpenedAt DESC "
                   + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
                   
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, offset);
            st.setInt(2, limit);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    TableSession ts = new TableSession();
                    ts.setSessionId(rs.getInt("SessionId"));
                    ts.setSessionCode(rs.getString("SessionCode"));
                    ts.setTableId(rs.getInt("TableId"));
                    ts.setGuestCount(rs.getInt("GuestCount"));
                    ts.setStatus(rs.getString("Status"));
                    Timestamp opened = rs.getTimestamp("OpenedAt");
                    if (opened != null) ts.setOpenedAt(opened.toLocalDateTime());
                    Timestamp closed = rs.getTimestamp("ClosedAt");
                    if (closed != null) ts.setClosedAt(closed.toLocalDateTime());
                    
                    TableSessionDTO dto = new TableSessionDTO(ts);
                    dto.setTableCode(rs.getString("TableCode"));
                    dto.setArea(rs.getString("Area"));
                    dto.setCashierName(rs.getString("CashierName"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public int countSessions() {
        String sql = "SELECT COUNT(*) FROM TableSessions WHERE IsDeleted = 0";
        try (PreparedStatement st = connection.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}
