package dao;

import java.math.BigDecimal;

public final class ImportVoiceDAORegressionTest {

    private ImportVoiceDAORegressionTest() {
    }

    public static void main(String[] args) {
        expectDelta("10", "Pending", "Confirmed", "10");
        expectDelta("-10", "Confirmed", "Pending", "10");
        expectDelta("-10", "Confirmed", "Rejected", "10");
        expectDelta("0", "Confirmed", "Confirmed", "10");
        expectDelta("0", "Pending", "Rejected", "10");
        expectDelta("0", "Pending", "Confirmed", null);
    }

    private static void expectDelta(String expected, String oldStatus,
            String newStatus, String receivedQuantity) {
        BigDecimal quantity = null;
        if (receivedQuantity != null) {
            quantity = new BigDecimal(receivedQuantity);
        }
        BigDecimal actual = ImportVoiceDAO.calculateStockDelta(
                oldStatus, newStatus, quantity);
        if (actual.compareTo(new BigDecimal(expected)) != 0) {
            throw new AssertionError(
                    oldStatus + " -> " + newStatus
                    + ": expected=" + expected + ", actual=" + actual);
        }
    }
}
