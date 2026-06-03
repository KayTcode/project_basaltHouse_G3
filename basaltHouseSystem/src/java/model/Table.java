/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author KayT
 */
public class Table {
    private int tableId;
    private String tableCode;
    private String area;
    private int capacity;
    private int currentGuests;
    private String status;
    private boolean isDeleted;

    public Table() {
    }

    public Table(int tableId, String tableCode, String area, int capacity, int currentGuests, String status, boolean isDeleted) {
        this.tableId = tableId;
        this.tableCode = tableCode;
        this.area = area;
        this.capacity = capacity;
        this.currentGuests = currentGuests;
        this.status = status;
        this.isDeleted = isDeleted;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public String getTableCode() {
        return tableCode;
    }

    public void setTableCode(String tableCode) {
        this.tableCode = tableCode;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCurrentGuests() {
        return currentGuests;
    }

    public void setCurrentGuests(int currentGuests) {
        this.currentGuests = currentGuests;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    
}
