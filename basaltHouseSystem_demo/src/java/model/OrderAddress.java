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
public class OrderAddress {
    private int orderAddressId;
    private int customerId;
    private int zoneId;
    private String recipientName;
    private String recipientPhone;
    private String addressDetail;
    private String note;
    private boolean isDefault;
    private LocalDateTime createdAt;
    private boolean isDeleted;

    public OrderAddress() {
    }

    public OrderAddress(int orderAddressId, int customerId, int zoneId, String recipientName, String recipientPhone, String addressDetail, String note, boolean isDefault, LocalDateTime createdAt, boolean isDeleted) {
        this.orderAddressId = orderAddressId;
        this.customerId = customerId;
        this.zoneId = zoneId;
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
        this.addressDetail = addressDetail;
        this.note = note;
        this.isDefault = isDefault;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
    }

    public int getOrderAddressId() {
        return orderAddressId;
    }

    public void setOrderAddressId(int orderAddressId) {
        this.orderAddressId = orderAddressId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getZoneId() {
        return zoneId;
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    
}
