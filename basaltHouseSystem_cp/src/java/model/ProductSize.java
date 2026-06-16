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
public class ProductSize {
    private int productSizeId;
    private int productId;
    private int sizeId;
    private BigDecimal price;
    private boolean isAvailable;
    private boolean isDeleted;

    public ProductSize() {
    }

    public ProductSize(int productSizeId, int productId, int sizeId, BigDecimal price, boolean isAvailable, boolean isDeleted) {
        this.productSizeId = productSizeId;
        this.productId = productId;
        this.sizeId = sizeId;
        this.price = price;
        this.isAvailable = isAvailable;
        this.isDeleted = isDeleted;
    }

    public int getProductSizeId() {
        return productSizeId;
    }

    public void setProductSizeId(int productSizeId) {
        this.productSizeId = productSizeId;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    
}
