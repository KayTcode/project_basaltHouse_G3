package model;

public class StockStatus {

    public final String statusClass;
    public final String statusIcon;
    public final String statusLabel;

    public StockStatus(String statusClass, String statusIcon, String statusLabel) {
        this.statusClass = statusClass;
        this.statusIcon = statusIcon;
        this.statusLabel = statusLabel;
    }
}
