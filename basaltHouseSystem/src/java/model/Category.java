/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author KayT
 */
public class Category {
    private int categoryId;
    private String categoryName;
    private String description;
    private boolean isDeleted;
    private String image;
    

    public Category() {
    }

    public Category(int categoryId, String categoryName, String description, boolean isDeleted, String image) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
        this.isDeleted = isDeleted;
        this.image = image;
    }

    public Category(int categoryId, String categoryName, String image) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.image = image;
    }

   

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    
    
}
