package dto;

import model.Table;
import model.TableSession;
import model.Order;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO cho hiển thị Sơ đồ bàn
 */
public class TableDTO {
    private Table table;
    private TableSession currentSession; // Phiên đang active (nếu bàn Occupied)
    private String cashierName;          // Tên thu ngân đang phụ trách (nếu có)
    
    public TableDTO(Table table) {
        this.table = table;
    }

    public Table getTable() { return table; }
    public void setTable(Table table) { this.table = table; }

    public TableSession getCurrentSession() { return currentSession; }
    public void setCurrentSession(TableSession currentSession) { this.currentSession = currentSession; }

    public String getCashierName() { return cashierName; }
    public void setCashierName(String cashierName) { this.cashierName = cashierName; }
}
