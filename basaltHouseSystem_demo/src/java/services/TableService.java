package services;

import dao.TableDAO;
import dao.TableSessionDAO;
import model.Table;
import model.TableSession;

import java.util.HashMap;


public class TableService {

    private final TableDAO tableDAO;
    private final TableSessionDAO sessionDAO;

    public TableService() {
        this.tableDAO  = new TableDAO();
        this.sessionDAO = new TableSessionDAO();
    }


    public HashMap<Integer, Table> getTablesMap() {
        HashMap<Integer, Table> map = new HashMap<>();
        for (Table t : tableDAO.getAllTablesWithOccupancy()) {
            map.put(t.getTableId(), t);
        }
        return map;
    }


    public Table getTableById(int tableId) {
        return tableDAO.getTableById(tableId);
    }


    public String addTable(String tableCode, String area, int capacity) {
        if (tableCode == null || tableCode.isBlank()){
            return "Mã bàn không được để trống.";
        }
        if (area == null || area.isBlank())  {
            return "Khu vực không được để trống.";
        }
        if (capacity < 1 || capacity > 20) {
            return "Sức chứa phải từ 1 đến 20.";
        }
        if (tableDAO.isTableCodeExists(tableCode)){
            return "Mã bàn \"" + tableCode + "\" đã tồn tại.";
        }

        boolean ok = tableDAO.addTable(tableCode, area, capacity);
        return ok ? null : "Không thể thêm bàn. Vui lòng thử lại.";
    }


    public int deleteTable(int tableId) {
        return tableDAO.deleteTable(tableId);
    }


    public HashMap<Integer, TableSession> getActiveSessionsMap() {
        HashMap<Integer, TableSession> map = new HashMap<>();
        for (TableSession s : sessionDAO.getAllActiveSessions()) {
            map.put(s.getSessionId(), s);
        }
        return map;
    }



    public String createSession(int tableId, int guestCount, Integer cashierId) {
        if (guestCount < 1) {
            return "ERR:Số lượng khách phải ít nhất là 1.";
        }

        Table table = tableDAO.getTableById(tableId);
        if (table == null) {
            return "ERR:Bàn không tồn tại (ID=" + tableId + ").";
        }

        int activeGuests = sessionDAO.getActiveGuestCount(tableId);
        int remaining    = table.getCapacity() - activeGuests;
        if (guestCount > remaining) {
            return "ERR:Bàn " + table.getTableCode() + " chỉ còn " + remaining
                + " ghế trống (hiện có " + activeGuests + "/" + table.getCapacity() + " khách).";
        }

        String sessionCode = sessionDAO.createSession(tableId, table.getTableCode(), guestCount, cashierId);
        return sessionCode != null ? sessionCode : "ERR:Không thể tạo session. Vui lòng thử lại.";
    }



    public boolean closeSession(int sessionId) {
        return sessionDAO.closeSession(sessionId);
    }


}
