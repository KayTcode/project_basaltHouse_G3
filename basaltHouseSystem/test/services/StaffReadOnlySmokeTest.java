package services;

import dao.AuthDAO;
import dao.OrderDAO;
import dto.IngredientStockDTO;
import dto.IngredientStockSnapshotDTO;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Account;
import model.ImportInvoicesDetail;

public final class StaffReadOnlySmokeTest {

    private StaffReadOnlySmokeTest() {
    }

    public static void main(String[] args) {
        verifyDashboardAndSupplierLookup();
        verifyImportHistoryAndDetail();
        verifySalesAudit();
        verifyCashierDashboard();
        verifyEmptyDayHasNoStockLog();
        verifyStaffProfileLookup();
    }

    @SuppressWarnings("unchecked")
    private static void verifyDashboardAndSupplierLookup() {
        HashMap<String, Object> dashboard =
                new StockService().getStaffDashboardData(null, true);
        List<IngredientStockDTO> ingredients =
                (List<IngredientStockDTO>) dashboard.get("ingredients");
        require(ingredients != null, "staff dashboard ingredients are missing");

        int warningCount = (Integer) dashboard.get("warningCount");
        int outCount = (Integer) dashboard.get("outCount");
        int okCount = (Integer) dashboard.get("okCount");
        require(
                warningCount + outCount + okCount == ingredients.size(),
                "staff dashboard stock counters do not match the ingredient list");

        for (IngredientStockDTO ingredient : ingredients) {
            if (ingredient.getSupplierId() != null) {
                HashMap<String, Object> result =
                        new StaffService().getIngredientsBySupplier(
                                ingredient.getSupplierId());
                require(
                        result.containsKey("success"),
                        "supplier ingredient lookup failed");
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void verifyImportHistoryAndDetail() {
        ImportVoiceService service = new ImportVoiceService();
        HashMap<String, Object> history = service.getImportInvoicesDetail(null);
        require(history.containsKey("success"), "import history lookup failed");

        List<ImportInvoicesDetail> invoices =
                (List<ImportInvoicesDetail>) history.get("success");
        if (invoices.isEmpty()) {
            return;
        }
        HashMap<String, Object> detail =
                service.getImportInvoiceDetailsById(invoices.get(0).getImportId());
        require(detail.containsKey("success"), "import detail lookup failed");

        HashMap<String, Object> search =
                service.getImportInvoicesDetail(invoices.get(0).getImportCode());
        require(search.containsKey("success"), "import-code search failed");
        List<ImportInvoicesDetail> matches =
                (List<ImportInvoicesDetail>) search.get("success");
        require(
                matches.stream().anyMatch(
                        invoice -> invoice.getImportId() == invoices.get(0).getImportId()),
                "import-code search did not return the matching invoice");
    }

    private static void verifySalesAudit() {
        OrderService orderService = new OrderService();
        require(
                orderService.getTodaySoldProductSizeRows().containsKey("success"),
                "today sales lookup failed");
        require(
                orderService.getSoldProductSizeRowsByDate(LocalDate.now())
                        .containsKey("success"),
                "sales lookup by date failed");

        HashMap<String, Object> audit =
                new StockService().getSalesAuditData(LocalDate.now());
        require(audit.containsKey("productSales"), "sales rows are missing");
        require(audit.containsKey("ingredientAudit"), "ingredient audit is missing");
        require(audit.containsKey("auditWarningCount"), "audit warning count is missing");
    }

    private static void verifyCashierDashboard() {
        Map<String, Object> dashboard = new OrderDAO().getCashierDashboard();
        require(dashboard.get("todayRevenue") != null,
                "cashier revenue is missing");
        require(dashboard.get("todayOrders") != null,
                "cashier order count is missing");
        require(dashboard.get("pendingOrders") != null,
                "cashier pending count is missing");
        require(dashboard.get("newCustomers") != null,
                "cashier customer count is missing");
    }

    @SuppressWarnings("unchecked")
    private static void verifyEmptyDayHasNoStockLog() {
        HashMap<String, Object> result =
                new IngredientCheckService().getStockSnapshotByDate(
                        LocalDate.of(2000, 1, 1));
        require(result.containsKey("success"), "stock snapshot lookup failed");
        List<IngredientStockSnapshotDTO> snapshots =
                (List<IngredientStockSnapshotDTO>) result.get("success");
        require(
                snapshots.stream().noneMatch(
                        IngredientStockSnapshotDTO::isHasStockLog),
                "a day without stock activity was marked as having stock logs");
    }

    private static void verifyStaffProfileLookup() {
        Account staff = new Account();
        staff.setAccountId(2);
        staff.setRoleId(3);
        staff.setEmail("staff01@bathcoffee.vn");
        Map<String, String> profile =
                new AuthDAO().getFullNameAndAvatarByAccount(staff);
        require(
                profile.get("fullName") != null
                && !staff.getEmail().equals(profile.get("fullName")),
                "staff full name was not loaded from the Staffs table");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
