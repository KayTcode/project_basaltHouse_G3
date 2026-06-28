/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;



/**
 *
 * @author MSI
 */
public class IngredientDTO {
    private int IngredientId;
    private String IngredientName;
    private double QuantityNeeded;
    private String Unit;

    public IngredientDTO(int IngredientId, String IngredientName, double QuantityNeeded, String Unit) {
        this.IngredientId = IngredientId;
        this.IngredientName = IngredientName;
        this.QuantityNeeded = QuantityNeeded;
        this.Unit = Unit;
    }

    public IngredientDTO() {
    }

    public int getIngredientId() {
        return IngredientId;
    }

    public void setIngredientId(int IngredientId) {
        this.IngredientId = IngredientId;
    }

    public String getIngredientName() {
        return IngredientName;
    }

    public void setIngredientName(String IngredientName) {
        this.IngredientName = IngredientName;
    }

    public double getQuantityNeeded() {
        return QuantityNeeded;
    }

    public void setQuantityNeeded(double QuantityNeeded) {
        this.QuantityNeeded = QuantityNeeded;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String Unit) {
        this.Unit = Unit;
    }
    
    
}
