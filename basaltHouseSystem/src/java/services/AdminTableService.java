package services;

import dao.AdminTableDAO;
import dto.TableDTO;
import dto.TableSessionDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminTableService {
    
    private final AdminTableDAO tableDAO = new AdminTableDAO();
    
    // ══════════════════════════════════════════════════════════════════
    // 1. DATA CHO DASHBOARD BÀN & PHIÊN
    // Trả về map gồm: danh sách bàn group theo khu vực, thống kê KPI
    // ══════════════════════════════════════════════════════════════════
    public Map<String, Object> getTableDashboardData() {
        Map<String, Object> data = new HashMap<>();
        
        List<TableDTO> allTables = tableDAO.getAllTablesWithCurrentSession();
        
        // Nhóm bàn theo Area (Tầng 1, Tầng 2, Ban công...)
        Map<String, List<TableDTO>> tablesByArea = allTables.stream()
                .collect(Collectors.groupingBy(dto -> dto.getTable().getArea()));
        
        // Thống kê KPI
        int total = allTables.size();
        int occupied = 0, reserved = 0, available = 0;
        
        for (TableDTO dto : allTables) {
            String status = dto.getTable().getStatus();
            if ("Occupied".equals(status)) occupied++;
            else if ("Reserved".equals(status)) reserved++;
            else if ("Available".equals(status)) available++;
        }
        
        data.put("tablesByArea", tablesByArea);
        data.put("statTotal", total);
        data.put("statOccupied", occupied);
        data.put("statReserved", reserved);
        data.put("statAvailable", available);
        
        // Trả về danh sách bàn available để render dropdown Mở phiên nhanh
        List<TableDTO> availableTables = allTables.stream()
                .filter(t -> "Available".equals(t.getTable().getStatus()))
                .collect(Collectors.toList());
        data.put("availableTables", availableTables);
                
        return data;
    }
    
    // ══════════════════════════════════════════════════════════════════
    // 2. CÁC ACTION (MỞ/ĐÓNG PHIÊN)
    // ══════════════════════════════════════════════════════════════════
    public boolean openSession(String tableIdStr, int cashierId, String guestCountStr) {
        try {
            int tableId = Integer.parseInt(tableIdStr);
            int guestCount = Integer.parseInt(guestCountStr);
            if (guestCount <= 0) return false;
            return tableDAO.openTableSession(tableId, cashierId, guestCount);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean closeSession(String sessionIdStr, String tableIdStr) {
        try {
            int sessionId = Integer.parseInt(sessionIdStr);
            int tableId = Integer.parseInt(tableIdStr);
            return tableDAO.closeTableSession(sessionId, tableId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ══════════════════════════════════════════════════════════════════
    // 3. CHI TIẾT PHIÊN ĐANG MỞ (ĐƯA VÀO MODAL AJAX/JSP)
    // ══════════════════════════════════════════════════════════════════
    public TableSessionDTO getActiveSessionDetails(String tableIdStr) {
        try {
            int tableId = Integer.parseInt(tableIdStr);
            return tableDAO.getActiveSessionDetails(tableId);
        } catch (Exception e) {
            return null;
        }
    }
    
    // ══════════════════════════════════════════════════════════════════
    // 4. LỊCH SỬ PHIÊN (PHÂN TRANG)
    // ══════════════════════════════════════════════════════════════════
    public Map<String, Object> getHistoryData(String pageStr, int pageSize) {
        Map<String, Object> data = new HashMap<>();
        int page = 1;
        if (pageStr != null && !pageStr.isEmpty()) {
            try { page = Math.max(1, Integer.parseInt(pageStr)); } catch (Exception ignored) {}
        }
        int offset = (page - 1) * pageSize;
        
        List<TableSessionDTO> history = tableDAO.getSessionHistory(offset, pageSize);
        int total = tableDAO.countSessions();
        int totalPages = Math.max(1, (int) Math.ceil((double) total / pageSize));
        
        data.put("history", history);
        data.put("currentPage", page);
        data.put("totalPages", totalPages);
        return data;
    }
}
