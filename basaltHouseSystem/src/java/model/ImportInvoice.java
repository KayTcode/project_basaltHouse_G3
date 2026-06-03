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
public class ImportInvoice {
    private int importId;
    private String importCode;
    private int supplierId;
    private int createdByStaffId;
    private Integer confirmedByStaffId;
    private String status;
    private LocalDateTime orderedDate;
    private LocalDateTime expectedDate;
    private LocalDateTime receivedDate;
    private String supplierInvoiceCode;
    private BigDecimal totalOrderedAmount;
    private BigDecimal totalReceivedAmount;
    private String note;
    private String rejectReason;
    private boolean isDeleted;

    public ImportInvoice() {
    }

    public ImportInvoice(int importId, String importCode, int supplierId, int createdByStaffId, Integer confirmedByStaffId, String status, LocalDateTime orderedDate, LocalDateTime expectedDate, LocalDateTime receivedDate, String supplierInvoiceCode, BigDecimal totalOrderedAmount, BigDecimal totalReceivedAmount, String note, String rejectReason, boolean isDeleted) {
        this.importId = importId;
        this.importCode = importCode;
        this.supplierId = supplierId;
        this.createdByStaffId = createdByStaffId;
        this.confirmedByStaffId = confirmedByStaffId;
        this.status = status;
        this.orderedDate = orderedDate;
        this.expectedDate = expectedDate;
        this.receivedDate = receivedDate;
        this.supplierInvoiceCode = supplierInvoiceCode;
        this.totalOrderedAmount = totalOrderedAmount;
        this.totalReceivedAmount = totalReceivedAmount;
        this.note = note;
        this.rejectReason = rejectReason;
        this.isDeleted = isDeleted;
    }

    public int getImportId() {
        return importId;
    }

    public void setImportId(int importId) {
        this.importId = importId;
    }

    public String getImportCode() {
        return importCode;
    }

    public void setImportCode(String importCode) {
        this.importCode = importCode;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int getCreatedByStaffId() {
        return createdByStaffId;
    }

    public void setCreatedByStaffId(int createdByStaffId) {
        this.createdByStaffId = createdByStaffId;
    }

    public Integer getConfirmedByStaffId() {
        return confirmedByStaffId;
    }

    public void setConfirmedByStaffId(Integer confirmedByStaffId) {
        this.confirmedByStaffId = confirmedByStaffId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getOrderedDate() {
        return orderedDate;
    }

    public void setOrderedDate(LocalDateTime orderedDate) {
        this.orderedDate = orderedDate;
    }

    public LocalDateTime getExpectedDate() {
        return expectedDate;
    }

    public void setExpectedDate(LocalDateTime expectedDate) {
        this.expectedDate = expectedDate;
    }

    public LocalDateTime getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDateTime receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getSupplierInvoiceCode() {
        return supplierInvoiceCode;
    }

    public void setSupplierInvoiceCode(String supplierInvoiceCode) {
        this.supplierInvoiceCode = supplierInvoiceCode;
    }

    public BigDecimal getTotalOrderedAmount() {
        return totalOrderedAmount;
    }

    public void setTotalOrderedAmount(BigDecimal totalOrderedAmount) {
        this.totalOrderedAmount = totalOrderedAmount;
    }

    public BigDecimal getTotalReceivedAmount() {
        return totalReceivedAmount;
    }

    public void setTotalReceivedAmount(BigDecimal totalReceivedAmount) {
        this.totalReceivedAmount = totalReceivedAmount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    
}
