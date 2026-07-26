package dto;

import java.util.List;

public class IngredientAuditDTO {

    private String ingredientName;
    private String expectedUsedText;
    private String importedTodayText;
    private String currentStockText;
    private String openingEstimateText;
    private String expectedClosingText;
    private String cupsText;
    private List<String> usageDetails;
    private String statusClass;
    private String statusIcon;
    private String statusLabel;

    public IngredientAuditDTO() {
    }

    public IngredientAuditDTO(
            String ingredientName,
            String expectedUsedText,
            String importedTodayText,
            String currentStockText,
            String openingEstimateText,
            String expectedClosingText,
            String cupsText,
            List<String> usageDetails,
            String statusClass,
            String statusIcon,
            String statusLabel) {
        this.ingredientName = ingredientName;
        this.expectedUsedText = expectedUsedText;
        this.importedTodayText = importedTodayText;
        this.currentStockText = currentStockText;
        this.openingEstimateText = openingEstimateText;
        this.expectedClosingText = expectedClosingText;
        this.cupsText = cupsText;
        this.usageDetails = usageDetails;
        this.statusClass = statusClass;
        this.statusIcon = statusIcon;
        this.statusLabel = statusLabel;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public String getExpectedUsedText() {
        return expectedUsedText;
    }

    public void setExpectedUsedText(String expectedUsedText) {
        this.expectedUsedText = expectedUsedText;
    }

    public String getImportedTodayText() {
        return importedTodayText;
    }

    public void setImportedTodayText(String importedTodayText) {
        this.importedTodayText = importedTodayText;
    }

    public String getCurrentStockText() {
        return currentStockText;
    }

    public void setCurrentStockText(String currentStockText) {
        this.currentStockText = currentStockText;
    }

    public String getOpeningEstimateText() {
        return openingEstimateText;
    }

    public void setOpeningEstimateText(String openingEstimateText) {
        this.openingEstimateText = openingEstimateText;
    }

    public String getExpectedClosingText() {
        return expectedClosingText;
    }

    public void setExpectedClosingText(String expectedClosingText) {
        this.expectedClosingText = expectedClosingText;
    }

    public String getCupsText() {
        return cupsText;
    }

    public void setCupsText(String cupsText) {
        this.cupsText = cupsText;
    }

    public List<String> getUsageDetails() {
        return usageDetails;
    }

    public void setUsageDetails(List<String> usageDetails) {
        this.usageDetails = usageDetails;
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

    public String getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }
}
