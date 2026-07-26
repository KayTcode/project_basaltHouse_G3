package dto;

import java.math.BigDecimal;

public class IngredientStockDTO {

    private int ingredientId;
    private String ingredientName;
    private String unit;
    private BigDecimal stockQuantity;
    private BigDecimal minStockQuantity;
    private Integer supplierId;
    private String supplierName;
    private String stockText;
    private String minStockText;
    private String status;
    private String statusLabel;
    private String statusIcon;
    private int barPercent;

    public IngredientStockDTO() {
    }

    public IngredientStockDTO(
            int ingredientId,
            String ingredientName,
            String unit,
            BigDecimal stockQuantity,
            BigDecimal minStockQuantity,
            Integer supplierId,
            String supplierName) {
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.unit = unit;
        this.stockQuantity = stockQuantity;
        this.minStockQuantity = minStockQuantity;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
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

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getStockText() {
        return stockText;
    }

    public void setStockText(String stockText) {
        this.stockText = stockText;
    }

    public String getMinStockText() {
        return minStockText;
    }

    public void setMinStockText(String minStockText) {
        this.minStockText = minStockText;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }

    public String getStatusIcon() {
        return statusIcon;
    }

    public void setStatusIcon(String statusIcon) {
        this.statusIcon = statusIcon;
    }

    public int getBarPercent() {
        return barPercent;
    }

    public void setBarPercent(int barPercent) {
        this.barPercent = barPercent;
    }
}
