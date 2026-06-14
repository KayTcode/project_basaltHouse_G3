package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Model class for OrderDetail.
 * @author KayT
 */
public class OrderDetail {
    private int orderDetailId;
    private int orderId;
    private int productId;
    private int sizeId;
    private int quantity;
    private BigDecimal unitPrice;
    private String note;
    private LocalDateTime createdAt;
    private boolean isDeleted;

    // ── Display fields (joined from Products & Sizes) ──────────────────
    private String productName;
    private String sizeName;

    public OrderDetail() {
    }

    public OrderDetail(int orderDetailId, int orderId, int productId, int sizeId, int quantity) {
        this.orderDetailId = orderDetailId;
        this.orderId = orderId;
        this.productId = productId;
        this.sizeId = sizeId;
        this.quantity = quantity;
    }

    public OrderDetail(int orderDetailId, int orderId, int productId, int sizeId, int quantity, BigDecimal unitPrice, String note, LocalDateTime createdAt, boolean isDeleted) {
        this.orderDetailId = orderDetailId;
        this.orderId = orderId;
        this.productId = productId;
        this.sizeId = sizeId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.note = note;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
    }

    public int getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(int orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getSizeId() {
        return sizeId;
    }

    public void setSizeId(int sizeId) {
        this.sizeId = sizeId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }
    
    public BigDecimal getSubtotal() {
        if (unitPrice == null) {
            return BigDecimal.ZERO;
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
