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
public class Recipe {
    private int recipeId;
    private int productId;
    private int ingredientId;
    private int sizeId;
    private BigDecimal quantityNeeded;
    private String note;
    private boolean isDeleted;

    public Recipe() {
    }

    
    public Recipe(int recipeId, int productId, int ingredientId, int sizeId, BigDecimal quantityNeeded, String note, boolean isDeleted) {
        this.recipeId = recipeId;
        this.productId = productId;
        this.ingredientId = ingredientId;
        this.sizeId = sizeId;
        this.quantityNeeded = quantityNeeded;
        this.note = note;
        this.isDeleted = isDeleted;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public int getSizeId() {
        return sizeId;
    }

    public void setSizeId(int sizeId) {
        this.sizeId = sizeId;
    }

    public BigDecimal getQuantityNeeded() {
        return quantityNeeded;
    }

    public void setQuantityNeeded(BigDecimal quantityNeeded) {
        this.quantityNeeded = quantityNeeded;
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
