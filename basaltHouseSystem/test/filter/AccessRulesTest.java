package filter;

public final class AccessRulesTest {

    private AccessRulesTest() {
    }

    public static void main(String[] args) {
        expectRole(AccessRules.ADMIN, "/admin/dashboard", "GET", null);
        expectRole(AccessRules.ADMIN, "/admin/memberships", "GET", null);
        expectRole(AccessRules.ADMIN, "/views/admin/Admin.jsp", "GET", null);
        expectRole(AccessRules.STAFF, "/staff", "GET", null);
        expectRole(AccessRules.STAFF, "/staff/dashboard", "GET", null);
        expectRole(AccessRules.STAFF, "/staff/ingredient", "GET", null);
        expectRole(AccessRules.STAFF, "/staff/import", "POST", null);
        expectRole(AccessRules.STAFF, "/staff/history", "GET", null);
        expectRole(AccessRules.STAFF, "/staff/sales-history", "GET", null);
        expectRole(AccessRules.STAFF, "/viewimportvoice", "POST", null);
        expectRole(AccessRules.STAFF, "/views/Staff/Staff.jsp", "GET", null);
        expectRole(AccessRules.CASHIER, "/cashier/pos", "GET", null);
        expectRole(AccessRules.CASHIER, "/bartender/view", "GET", null);
        expectRole(AccessRules.CASHIER, "/TableSession", "POST", "create");
        expectRole(AccessRules.SHIPPER, "/shipper/dashboard", "GET", null);
        expectRole(AccessRules.CUSTOMER, "/profile", "GET", null);
        expectRole(AccessRules.CUSTOMER, "/voucher", "POST", null);
        expectRole(AccessRules.CUSTOMER, "/membership", "GET", null);
        expectRole(AccessRules.CUSTOMER, "/my-orders", "GET", null);
        expectRole(AccessRules.CUSTOMER, "/review", "POST", null);
        expectRole(AccessRules.CUSTOMER, "/momo/payment", "GET", null);
        expectRole(AccessRules.CUSTOMER, "/views/AccountProfile/AccountProfile.jsp", "GET", null);
        expectRole(AccessRules.CUSTOMER, "/views/Voucher/Voucher.jsp", "GET", null);
        expectRole(AccessRules.CUSTOMER, "/views/MembershipRanks/MembershipRank.jsp", "GET", null);
        expectRole(AccessRules.CUSTOMER, "/views/Order/OrderTracking.jsp", "GET", null);
        expectRole(AccessRules.CUSTOMER, "/views/Order/Checkout.jsp", "GET", null);
        expectRole(AccessRules.CUSTOMER, "/Cart", "GET", "checkout-form");
        expectRole(AccessRules.CUSTOMER, "/Cart", "POST", "checkout");
        expectRole(AccessRules.PUBLIC, "/home", "GET", null);
        expectRole(AccessRules.PUBLIC, "/Cart", "GET", null);
        expectRole(AccessRules.PUBLIC, "/momo/ipn", "POST", null);

        expectAuthentication(true, "/logout", "GET", null);
        expectAuthentication(false, "/login", "GET", null);
    }

    private static void expectRole(int expected, String path, String method, String action) {
        int actual = AccessRules.requiredRole(path, method, action);
        if (actual != expected) {
            throw new AssertionError(path + ": expected role=" + expected + ", actual=" + actual);
        }
    }

    private static void expectAuthentication(boolean expected, String path,
            String method, String action) {
        boolean actual = AccessRules.requiresAuthentication(path, method, action);
        if (actual != expected) {
            throw new AssertionError(path + ": expected authentication="
                    + expected + ", actual=" + actual);
        }
    }
}
