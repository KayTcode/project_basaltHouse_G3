package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ImportInvoicesDetail {

    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private static final DateTimeFormatter TEXT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private int importDetailId;
    private int importId;
    private LocalDateTime orderedDate;
    private LocalDateTime expectedDate;
    private LocalDateTime receivedDate;
    private String importCode;
    private int ingredientId;
    private String ingredientName;
    private String unit;
    private int supplierId;
    private String supplierName;
    private BigDecimal orderedQuantity;
    private BigDecimal receivedQuantity;
    private BigDecimal stockQuantity;
    private BigDecimal unitPrice;
    private BigDecimal totalOrderedAmount;
    private BigDecimal totalReceivedAmount;
    private String status;
    private String staffName;
    private String supplierInvoiceCode;
    private String invoiceNote;
    private String rejectReason;
    private String discrepancyNote;
    private String detailNote;

    public ImportInvoicesDetail() {
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

    public String getImportCode() {
        return importCode;
    }

    public void setImportCode(String importCode) {
        this.importCode = importCode;
    }

    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSppliendName() {
        return supplierName;
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

    public BigDecimal getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(BigDecimal stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getSupplierInvoiceCode() {
        return supplierInvoiceCode;
    }

    public void setSupplierInvoiceCode(String supplierInvoiceCode) {
        this.supplierInvoiceCode = supplierInvoiceCode;
    }

    public String getInvoiceNote() {
        return invoiceNote;
    }

    public void setInvoiceNote(String invoiceNote) {
        this.invoiceNote = invoiceNote;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public String getDiscrepancyNote() {
        return discrepancyNote;
    }

    public void setDiscrepancyNote(String discrepancyNote) {
        this.discrepancyNote = discrepancyNote;
    }

    public String getDetailNote() {
        return detailNote;
    }

    public void setDetailNote(String detailNote) {
        this.detailNote = detailNote;
    }

    public String getOrderedDateInput() {
        return format(orderedDate, INPUT_FORMAT);
    }

    public String getExpectedDateInput() {
        return format(expectedDate, INPUT_FORMAT);
    }

    public String getReceivedDateInput() {
        return format(receivedDate, INPUT_FORMAT);
    }

    public String getOrderedDateText() {
        return format(orderedDate, TEXT_FORMAT);
    }

    private String format(LocalDateTime value, DateTimeFormatter formatter) {
        return value == null ? "" : value.format(formatter);
    }
}
