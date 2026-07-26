package dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProductSaleAuditDTO {

    private int productId;
    private int sizeId;
    private String productName;
    private String sizeName;
    private int soldQuantity;
    private BigDecimal unitPrice;
    private BigDecimal revenue;
    private LocalDate auditDate;
    private String unitPriceText;
    private String revenueText;
    private String recipeText;
    private String expectedUsageText;
    private String statusClass;
    private String statusIcon;

    public ProductSaleAuditDTO() {
    }

    public ProductSaleAuditDTO(
            int productId,
            int sizeId,
            String productName,
            String sizeName,
            int soldQuantity,
            BigDecimal unitPrice,
            BigDecimal revenue,
            LocalDate auditDate) {
        this.productId = productId;
        this.sizeId = sizeId;
        this.productName = productName;
        this.sizeName = sizeName;
        this.soldQuantity = soldQuantity;
        this.unitPrice = unitPrice;
        this.revenue = revenue;
        this.auditDate = auditDate;
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

    public int getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(int soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public LocalDate getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(LocalDate auditDate) {
        this.auditDate = auditDate;
    }

    public String getUnitPriceText() {
        return unitPriceText;
    }

    public void setUnitPriceText(String unitPriceText) {
        this.unitPriceText = unitPriceText;
    }

    public String getRevenueText() {
        return revenueText;
    }

    public void setRevenueText(String revenueText) {
        this.revenueText = revenueText;
    }

    public String getRecipeText() {
        return recipeText;
    }

    public void setRecipeText(String recipeText) {
        this.recipeText = recipeText;
    }

    public String getExpectedUsageText() {
        return expectedUsageText;
    }

    public void setExpectedUsageText(String expectedUsageText) {
        this.expectedUsageText = expectedUsageText;
    }

    public String getStatusClass() {
        return statusClass;
    }

    public void setStatusClass(String statusClass) {
        this.statusClass = statusClass;
    }

    public String getStatusIcon() {
        return statusIcon;
    }

    public void setStatusIcon(String statusIcon) {
        this.statusIcon = statusIcon;
    }
}
