package dto;

import model.TableSession;
import model.Order;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * DTO cho chi tiết Phiên làm việc
 */
public class TableSessionDTO {
    private TableSession session;
    private String tableCode;
    private String area;
    private String cashierName;
    
    // Đơn hàng gắn với phiên
    private List<Order> orders = new ArrayList<>();
    
    // Helpers định dạng thời gian
    private String openedAtFormatted;
    private String closedAtFormatted;
    private String durationStr;

    public TableSessionDTO(TableSession session) {
        this.session = session;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        if (session.getOpenedAt() != null) {
            this.openedAtFormatted = session.getOpenedAt().format(fmt);
        }
        if (session.getClosedAt() != null) {
            this.closedAtFormatted = session.getClosedAt().format(fmt);
        }
        calculateDuration();
    }

    private void calculateDuration() {
        if (session.getOpenedAt() == null) return;
        LocalDateTime end = session.getClosedAt() != null ? session.getClosedAt() : LocalDateTime.now();
        Duration d = Duration.between(session.getOpenedAt(), end);
        long hours = d.toHours();
        long minutes = d.toMinutesPart();
        
        if (hours > 0) {
            this.durationStr = "~" + hours + " giờ " + (minutes > 0 ? minutes + " phút" : "");
        } else {
            this.durationStr = minutes + " phút";
        }
    }

    public void addOrder(Order o) {
        if (o != null) this.orders.add(o);
    }
    
    public Order getLatestOrder() {
        if (orders.isEmpty()) return null;
        return orders.get(orders.size() - 1); // Lấy đơn mới nhất (vì sort ASC)
    }

    // Getters & Setters
    public TableSession getSession() { return session; }
    public String getTableCode() { return tableCode; }
    public void setTableCode(String tableCode) { this.tableCode = tableCode; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public String getCashierName() { return cashierName; }
    public void setCashierName(String cashierName) { this.cashierName = cashierName; }
    public List<Order> getOrders() { return orders; }
    public String getOpenedAtFormatted() { return openedAtFormatted; }
    public String getClosedAtFormatted() { return closedAtFormatted; }
    public String getDurationStr() { return durationStr; }
}
