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
public class Bill {

    private int billId;
    private String billCode;
    private int orderId;
    private Integer tableId;
    private Integer cashierId;
    private BigDecimal subTotal;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String paymentMethod;
    private String note;
    private java.time.LocalDateTime printedAt;
    private boolean isDeleted;
    private String orderType;
    private String orderStatus;
    private String customerName;
    private String cashierName;

    public Bill() {
    }

    public Bill(int billId, String billCode, int orderId, Integer tableId, Integer cashierId, BigDecimal subTotal, BigDecimal discountAmount, BigDecimal finalAmount, String paymentMethod, String note, LocalDateTime printedAt, boolean isDeleted) {
        this.billId = billId;
        this.billCode = billCode;
        this.orderId = orderId;
        this.tableId = tableId;
        this.cashierId = cashierId;
        this.subTotal = subTotal;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.paymentMethod = paymentMethod;
        this.note = note;
        this.printedAt = printedAt;
        this.isDeleted = isDeleted;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }

    public Integer getCashierId() {
        return cashierId;
    }

    public void setCashierId(Integer cashierId) {
        this.cashierId = cashierId;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getPrintedAt() {
        return printedAt;
    }

    public void setPrintedAt(LocalDateTime printedAt) {
        this.printedAt = printedAt;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCashierName() {
        return cashierName;
    }

    public void setCashierName(String cashierName) {
        this.cashierName = cashierName;
    }

    public String getFormattedPrintedAt() {
        if (printedAt == null) {
            return "";
        }
        return printedAt.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getStatusDisplayName() {
        if (orderStatus == null) return "Đang xử lý";
        String st = orderStatus.toLowerCase().trim();
        if ("completed".equals(st) || "paid".equals(st)) {
            return "Hoàn thành";
        }
        if ("cancelled".equals(st)) {
            return "Đã hủy";
        }
        if ("pending".equals(st)) {
            return "Chờ xác nhận";
        }
        if ("preparing".equals(st) || "in_progress".equals(st)) {
            return "Đang chuẩn bị";
        }
        if ("ready".equals(st) || "waiting_shipper".equals(st)) {
            return "Sẵn sàng";
        }
        if ("delivering".equals(st)) {
            return "Đang giao";
        }
        return orderStatus;
    }

    public String getStatusBadgeClass() {
        if (orderStatus == null) return "status-pending";
        String st = orderStatus.toLowerCase().trim();
        if ("completed".equals(st) || "paid".equals(st)) {
            return "status-completed";
        }
        if ("cancelled".equals(st)) {
            return "status-cancelled";
        }
        return "status-pending";
    }
}
