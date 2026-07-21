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
public class Order {
    private int orderId;
    private Integer customerId;
    private Integer cashierId;
    private Integer shipperId;
    private Integer tableSessionId;
    private Integer orderAddressId;
    private Integer discountId;
    private String orderType;
    private String orderStatus;
    private String paymentMethod;
    private String paymentStatus;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private LocalDateTime createdAt;
    private boolean isDeleted;
    private String customerName;
    private String tableName;
    private String cashierName;
    private String shipperName;
    private String note;
    private java.util.List<OrderDetail> orderDetails = new java.util.ArrayList<>();

    public java.util.List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(java.util.List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public void addOrderDetail(OrderDetail detail) {
        if (this.orderDetails == null) {
            this.orderDetails = new java.util.ArrayList<>();
        }
        this.orderDetails.add(detail);
    }

    public Order() {
    }

    public Order(int orderId, String orderStatus) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
    }

    public Order(int orderId, Integer customerId, Integer cashierId, Integer shipperId, Integer tableSessionId, Integer orderAddressId, Integer discountId, String orderType, String orderStatus, String paymentMethod, String paymentStatus, BigDecimal totalAmount, BigDecimal discountAmount, BigDecimal finalAmount, LocalDateTime createdAt, boolean isDeleted) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.cashierId = cashierId;
        this.shipperId = shipperId;
        this.tableSessionId = tableSessionId;
        this.orderAddressId = orderAddressId;
        this.discountId = discountId;
        this.orderType = orderType;
        this.orderStatus = orderStatus;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getCashierId() {
        return cashierId;
    }

    public void setCashierId(Integer cashierId) {
        this.cashierId = cashierId;
    }

    public Integer getShipperId() {
        return shipperId;
    }

    public void setShipperId(Integer shipperId) {
        this.shipperId = shipperId;
    }

    public Integer getTableSessionId() {
        return tableSessionId;
    }

    public void setTableSessionId(Integer tableSessionId) {
        this.tableSessionId = tableSessionId;
    }

    public Integer getOrderAddressId() {
        return orderAddressId;
    }

    public void setOrderAddressId(Integer orderAddressId) {
        this.orderAddressId = orderAddressId;
    }

    public Integer getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Integer discountId) {
        this.discountId = discountId;
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public String getFormattedCreatedAt() {
        if (createdAt == null) return "";
        return createdAt.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
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
    
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public String getCashierName() {
        return cashierName;
    }

    public void setCashierName(String cashierName) {
        this.cashierName = cashierName;
    }

  
    public String getShipperName() {
        return shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
    
    public java.util.Date getCreatedAtDate() {
        if (createdAt == null) {
            return null;
        }
        return java.util.Date.from(createdAt
                .atZone(java.time.ZoneId.systemDefault())
                .toInstant());
    }
}

