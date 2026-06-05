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
public class Ingredient {
    private int ingredientId;
    private String ingredientName;
    private String unit;
    private BigDecimal stockQuantity;
    private BigDecimal minStockQuantity;
    private Integer supplierId;
    private boolean isActive;
    private boolean isDeleted;

    public Ingredient() {
    }

    public Ingredient(int ingredientId, String ingredientName, String unit, BigDecimal stockQuantity, BigDecimal minStockQuantity, Integer supplierId, boolean isActive, boolean isDeleted) {
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.unit = unit;
        this.stockQuantity = stockQuantity;
        this.minStockQuantity = minStockQuantity;
        this.supplierId = supplierId;
        this.isActive = isActive;
        this.isDeleted = isDeleted;
    }

    public Ingredient(int ingredientId, BigDecimal stockQuantity) {
        this.ingredientId = ingredientId;
        this.stockQuantity = stockQuantity;
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

    public BigDecimal getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(BigDecimal stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public BigDecimal getMinStockQuantity() {
        return minStockQuantity;
    }

    public void setMinStockQuantity(BigDecimal minStockQuantity) {
        this.minStockQuantity = minStockQuantity;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    
}
