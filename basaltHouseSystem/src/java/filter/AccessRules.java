package filter;

import java.util.Set;

/** Central route-to-role policy shared by both security filters. */
final class AccessRules {

    static final int PUBLIC = 0;
    static final int ADMIN = 1;
    static final int CUSTOMER = 2;
    static final int STAFF = 3;
    static final int CASHIER = 4;
    static final int SHIPPER = 5;

    private static final Set<String> CUSTOMER_ROUTES = Set.of(
            "/profile",
            "/voucher",
            "/membership",
            "/my-orders",
            "/review",
            "/momo/payment",
            "/confirm-delivery"
    );

    private static final Set<String> CASHIER_ROUTES = Set.of(
            "/TableSession",
            "/CheckoutSession",
            "/DeleteTable",
            "/CheckPromotion"
    );

    private AccessRules() {
    }

    static boolean requiresAuthentication(String path, String method, String action) {
        return "/logout".equals(path) || requiredRole(path, method, action) != PUBLIC;
    }

    static int requiredRole(String path, String method, String action) {
        if (isArea(path, "/admin") || isArea(path, "/views/admin")) {
            return ADMIN;
        }
        if (isArea(path, "/staff")
                || isArea(path, "/views/Staff")
                || "/viewimportvoice".equals(path)) {
            return STAFF;
        }
        if (isArea(path, "/cashier")|| isArea(path, "/bartender")
                || isArea(path, "/views/Cashier")
                || isArea(path, "/views/Bartender")
                || isArea(path, "/views/TableSession")
                || CASHIER_ROUTES.contains(path)) {
            return CASHIER;
        }
        if (isArea(path, "/shipper") || isArea(path, "/views/shipper")) {
            return SHIPPER;
        }
        if (CUSTOMER_ROUTES.contains(path)|| isArea(path, "/views/AccountProfile")
                || isArea(path, "/views/Voucher")
                || isArea(path, "/views/MembershipRanks")
                || isArea(path, "/views/Payment")
                || "/views/Order/OrderTracking.jsp".equals(path)
                || "/views/Order/Checkout.jsp".equals(path)
                || isCustomerCheckout(path, method, action)) {
            return CUSTOMER;
        }
        return PUBLIC;
    }

    private static boolean isCustomerCheckout(String path, String method, String action) {
        return "/Cart".equals(path)
                && (("GET".equalsIgnoreCase(method) && "checkout-form".equals(action))
                || ("POST".equalsIgnoreCase(method) && "checkout".equals(action)));
    }

    private static boolean isArea(String path, String area) {
        return path.equals(area) || path.startsWith(area + "/");
    }
}
