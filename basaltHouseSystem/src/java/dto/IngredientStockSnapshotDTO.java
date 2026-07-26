package dto;

import java.math.BigDecimal;

public class IngredientStockSnapshotDTO {

    private int ingredientId;
    private BigDecimal openingStock;
    private BigDecimal closingStock;
    private boolean hasStockLog;

    public IngredientStockSnapshotDTO() {
    }

    public IngredientStockSnapshotDTO(
            int ingredientId,
            BigDecimal openingStock,
            BigDecimal closingStock,
            boolean hasStockLog) {
        this.ingredientId = ingredientId;
        this.openingStock = openingStock;
        this.closingStock = closingStock;
        this.hasStockLog = hasStockLog;
    }

    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public BigDecimal getOpeningStock() {
        return openingStock;
    }

    public void setOpeningStock(BigDecimal openingStock) {
        this.openingStock = openingStock;
    }

    public BigDecimal getClosingStock() {
        return closingStock;
    }

    public void setClosingStock(BigDecimal closingStock) {
        this.closingStock = closingStock;
    }

    public boolean isHasStockLog() {
        return hasStockLog;
    }

    public void setHasStockLog(boolean hasStockLog) {
        this.hasStockLog = hasStockLog;
    }
}
