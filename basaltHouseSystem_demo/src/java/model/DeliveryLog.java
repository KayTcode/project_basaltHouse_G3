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
public class DeliveryLog {
    private int deliveryLogId;
    private int orderId;
    private int shipperId;
    private String status;
    private String failReason;
    private LocalDateTime estimatedDeliveryAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime shipperConfirmedAt;
    private LocalDateTime customerConfirmedAt;
    private LocalDateTime deliveredAt;
    private Boolean isOverdue;
    private String proofImageUrl;
    private String note;
    private LocalDateTime createdAt;
    private boolean isDeleted;

    public DeliveryLog() {
    }

    public DeliveryLog(int deliveryLogId, int orderId, int shipperId, String status, String failReason, LocalDateTime estimatedDeliveryAt, LocalDateTime pickedUpAt, LocalDateTime shipperConfirmedAt, LocalDateTime customerConfirmedAt, LocalDateTime deliveredAt, Boolean isOverdue, String proofImageUrl, String note, LocalDateTime createdAt, boolean isDeleted) {
        this.deliveryLogId = deliveryLogId;
        this.orderId = orderId;
        this.shipperId = shipperId;
        this.status = status;
        this.failReason = failReason;
        this.estimatedDeliveryAt = estimatedDeliveryAt;
        this.pickedUpAt = pickedUpAt;
        this.shipperConfirmedAt = shipperConfirmedAt;
        this.customerConfirmedAt = customerConfirmedAt;
        this.deliveredAt = deliveredAt;
        this.isOverdue = isOverdue;
        this.proofImageUrl = proofImageUrl;
        this.note = note;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
    }

    public int getDeliveryLogId() {
        return deliveryLogId;
    }

    public void setDeliveryLogId(int deliveryLogId) {
        this.deliveryLogId = deliveryLogId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getShipperId() {
        return shipperId;
    }

    public void setShipperId(int shipperId) {
        this.shipperId = shipperId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public LocalDateTime getEstimatedDeliveryAt() {
        return estimatedDeliveryAt;
    }

    public void setEstimatedDeliveryAt(LocalDateTime estimatedDeliveryAt) {
        this.estimatedDeliveryAt = estimatedDeliveryAt;
    }

    public LocalDateTime getPickedUpAt() {
        return pickedUpAt;
    }

    public void setPickedUpAt(LocalDateTime pickedUpAt) {
        this.pickedUpAt = pickedUpAt;
    }

    public LocalDateTime getShipperConfirmedAt() {
        return shipperConfirmedAt;
    }

    public void setShipperConfirmedAt(LocalDateTime shipperConfirmedAt) {
        this.shipperConfirmedAt = shipperConfirmedAt;
    }

    public LocalDateTime getCustomerConfirmedAt() {
        return customerConfirmedAt;
    }

    public void setCustomerConfirmedAt(LocalDateTime customerConfirmedAt) {
        this.customerConfirmedAt = customerConfirmedAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public Boolean getIsOverdue() {
        return isOverdue;
    }

    public void setIsOverdue(Boolean isOverdue) {
        this.isOverdue = isOverdue;
    }

    public String getProofImageUrl() {
        return proofImageUrl;
    }

    public void setProofImageUrl(String proofImageUrl) {
        this.proofImageUrl = proofImageUrl;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
