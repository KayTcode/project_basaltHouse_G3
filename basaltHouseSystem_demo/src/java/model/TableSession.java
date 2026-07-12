/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDateTime;

/**
 *
 * @author KayT
 */
public class TableSession {
    private int sessionId;
    private String sessionCode;
    private int tableId;
    private Integer cashierId;
    private int guestCount;
    private String status;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private boolean isDeleted;

    public TableSession() {
    }

    public TableSession(int sessionId, String sessionCode, int tableId, Integer cashierId, int guestCount, String status, LocalDateTime openedAt, LocalDateTime closedAt, boolean isDeleted) {
        this.sessionId = sessionId;
        this.sessionCode = sessionCode;
        this.tableId = tableId;
        this.cashierId = cashierId;
        this.guestCount = guestCount;
        this.status = status;
        this.openedAt = openedAt;
        this.closedAt = closedAt;
        this.isDeleted = isDeleted;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public Integer getCashierId() {
        return cashierId;
    }

    public void setCashierId(Integer cashierId) {
        this.cashierId = cashierId;
    }

    public int getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(int guestCount) {
        this.guestCount = guestCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(LocalDateTime openedAt) {
        this.openedAt = openedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    
}
