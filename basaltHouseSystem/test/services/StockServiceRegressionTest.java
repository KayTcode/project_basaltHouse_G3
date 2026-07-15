package services;

import java.math.BigDecimal;
import model.StockNumbers;

public final class StockServiceRegressionTest {

    private StockServiceRegressionTest() {
    }

    public static void main(String[] args) {
        expectInt(14, StockService.calculateAvailableCups(
                new BigDecimal("100"), new BigDecimal("6")),
                "85% stock reserve must be applied before the single floor");
        expectInt(1, StockService.calculateAvailableCups(
                new BigDecimal("19"), new BigDecimal("10")),
                "cup calculation must not floor twice");
        expectInt(0, StockService.calculateAvailableCups(
                BigDecimal.ZERO, BigDecimal.TEN),
                "empty stock must return zero cups");

        BigDecimal expectedUsage = StockService.calculateExpectedUsage(
                new BigDecimal("12.5"), 12);
        expectDecimal("150.0", expectedUsage,
                "audit usage must use the recipe quantity without the 15% reserve");

        StockNumbers matching = StockService.reconcileStock(
                new BigDecimal("1000"),
                new BigDecimal("950"),
                new BigDecimal("100"),
                new BigDecimal("150"));
        expectDecimal("0", matching.difference, "matching stock must have zero difference");

        StockNumbers shortage = StockService.reconcileStock(
                new BigDecimal("1000"),
                new BigDecimal("920"),
                new BigDecimal("100"),
                new BigDecimal("150"));
        expectDecimal("30", shortage.difference, "lower actual closing stock must be a shortage");

        StockNumbers missingLog = StockService.reconcileStock(
                null, new BigDecimal("920"), BigDecimal.ZERO, new BigDecimal("150"));
        if (missingLog.hasStockLog || missingLog.difference != null
                || missingLog.expectedClosingStock != null) {
            throw new AssertionError("missing stock logs must not be reported as a match");
        }
    }

    private static void expectInt(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + ": expected=" + expected + ", actual=" + actual);
        }
    }

    private static void expectDecimal(String expected, BigDecimal actual, String message) {
        if (actual == null || actual.compareTo(new BigDecimal(expected)) != 0) {
            throw new AssertionError(message + ": expected=" + expected + ", actual=" + actual);
        }
    }
}
