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
public class Product {

    private int productId;
    private String productName;
    private int categoryId;
    private BigDecimal price;
    private String description;
    private String imageUrl;
    private boolean isActive;
    private LocalDateTime createdAt;
    private boolean isDeleted;
    private int totalSold;
    private double averageRating;
    private int reviewCount;

    public Product() {
    }

    public Product(int productId, String productName, String description,BigDecimal price, String imageUrl,boolean isActive) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isActive= isActive;
    }
       public Product(int productId, String productName, String description,String imageUrl,boolean isActive) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isActive= isActive;
    }

    public Product(int productId, String productName, int categoryId, BigDecimal price, String description, String imageUrl, boolean isActive, LocalDateTime createdAt, boolean isDeleted) {
        this.productId = productId;
        this.productName = productName;
        this.categoryId = categoryId;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
    }

    public Product(int productId, String productName, String description, BigDecimal price, String imageUrl, boolean isActive, int totalSold, double averageRating, int reviewCount) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
        this.totalSold = totalSold;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
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

    public int getTotalSold() {
        return totalSold;
    }

    public void setTotalSold(int totalSold) {
        this.totalSold = totalSold;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

}
