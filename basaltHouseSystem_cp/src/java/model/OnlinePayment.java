/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * @author KayT
 */
public class OnlinePayment {
    private int paymentId;
    private String paymentCode;
    private int orderId;
    private BigDecimal amount;
    private boolean isActive;
    private LocalDateTime paidAt;
    private String note;
    private boolean isDeleted;
    private LocalDateTime createdAt;

    public OnlinePayment() {
    }

    public OnlinePayment(int paymentId, String paymentCode, int orderId, BigDecimal amount, boolean isActive, LocalDateTime paidAt, String note, boolean isDeleted, LocalDateTime createdAt) {
        this.paymentId = paymentId;
        this.paymentCode = paymentCode;
        this.orderId = orderId;
        this.amount = amount;
        this.isActive = isActive;
        this.paidAt = paidAt;
        this.note = note;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    
}
