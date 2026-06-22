/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;

/**
 *
 * @author KayT
 */
public class ImportDetail {
    private int importDetailId;
    private int importId;
    private int ingredientId;
    private BigDecimal orderedQuantity;
    private BigDecimal receivedQuantity;
    private BigDecimal unitPrice;
    private String discrepancyNote;
    private String note;
    private boolean isDeleted;

    public ImportDetail() {
    }

    public ImportDetail(int importDetailId, int importId, int ingredientId, BigDecimal orderedQuantity, BigDecimal receivedQuantity, BigDecimal unitPrice, String discrepancyNote, String note, boolean isDeleted) {
        this.importDetailId = importDetailId;
        this.importId = importId;
        this.ingredientId = ingredientId;
        this.orderedQuantity = orderedQuantity;
        this.receivedQuantity = receivedQuantity;
        this.unitPrice = unitPrice;
        this.discrepancyNote = discrepancyNote;
        this.note = note;
        this.isDeleted = isDeleted;
    }

    public ImportDetail(int importId, int ingredientId, BigDecimal orderedQuantity, BigDecimal receivedQuantity, BigDecimal unitPrice, String discrepancyNote, String note) {
        this.importId = importId;
        this.ingredientId = ingredientId;
        this.orderedQuantity = orderedQuantity;
        this.receivedQuantity = receivedQuantity;
        this.unitPrice = unitPrice;
        this.discrepancyNote = discrepancyNote;
        this.note = note;
    }

    public int getImportDetailId() {
        return importDetailId;
    }

    public void setImportDetailId(int importDetailId) {
        this.importDetailId = importDetailId;
    }

    public int getImportId() {
        return importId;
    }

    public void setImportId(int importId) {
        this.importId = importId;
    }

    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public BigDecimal getOrderedQuantity() {
        return orderedQuantity;
    }

    public void setOrderedQuantity(BigDecimal orderedQuantity) {
        this.orderedQuantity = orderedQuantity;
    }

    public BigDecimal getReceivedQuantity() {
        return receivedQuantity;
    }

    public void setReceivedQuantity(BigDecimal receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getDiscrepancyNote() {
        return discrepancyNote;
    }

    public void setDiscrepancyNote(String discrepancyNote) {
        this.discrepancyNote = discrepancyNote;
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
    
    
}
