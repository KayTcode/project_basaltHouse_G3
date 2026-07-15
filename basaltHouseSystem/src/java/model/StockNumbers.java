package model;

import java.math.BigDecimal;

public class StockNumbers {

    public final BigDecimal openingStock;
    public final BigDecimal closingStock;
    public final BigDecimal expectedClosingStock;
    public final BigDecimal difference;
    public final boolean hasStockLog;

    public StockNumbers(
            BigDecimal openingStock,
            BigDecimal closingStock,
            BigDecimal expectedClosingStock,
            BigDecimal difference,
            boolean hasStockLog) {
        this.openingStock = openingStock;
        this.closingStock = closingStock;
        this.expectedClosingStock = expectedClosingStock;
        this.difference = difference;
        this.hasStockLog = hasStockLog;
    }
}
