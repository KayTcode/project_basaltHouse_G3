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
public class IngredientStockLog {
    private int logId;
    private int ingredientId;
    private String changeType;
    private BigDecimal quantityBefore;
    private BigDecimal quantityChanged;
    private BigDecimal quantityAfter;
    private String refType;
    private String refId;
    private int staffId;
    private LocalDateTime createdAt;
    private boolean isDeleted;

    public IngredientStockLog() {
    }

    public IngredientStockLog(int logId, int ingredientId, String changeType, BigDecimal quantityBefore, BigDecimal quantityChanged, BigDecimal quantityAfter, String refType, String refId, int staffId, LocalDateTime createdAt, boolean isDeleted) {
        this.logId = logId;
        this.ingredientId = ingredientId;
        this.changeType = changeType;
        this.quantityBefore = quantityBefore;
        this.quantityChanged = quantityChanged;
        this.quantityAfter = quantityAfter;
        this.refType = refType;
        this.refId = refId;
        this.staffId = staffId;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public BigDecimal getQuantityBefore() {
        return quantityBefore;
    }

    public void setQuantityBefore(BigDecimal quantityBefore) {
        this.quantityBefore = quantityBefore;
    }

    public BigDecimal getQuantityChanged() {
        return quantityChanged;
    }

    public void setQuantityChanged(BigDecimal quantityChanged) {
        this.quantityChanged = quantityChanged;
    }

    public BigDecimal getQuantityAfter() {
        return quantityAfter;
    }

    public void setQuantityAfter(BigDecimal quantityAfter) {
        this.quantityAfter = quantityAfter;
    }

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
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
