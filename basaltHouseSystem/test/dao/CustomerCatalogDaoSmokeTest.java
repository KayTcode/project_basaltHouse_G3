package dao;

import java.util.List;
import java.util.Map;
import model.Category;
import model.Customer;
import model.CustomerDiscountCode;
import model.CustomerMembership;
import model.CustomerRanking;
import model.Product;

public final class CustomerCatalogDaoSmokeTest {

    private CustomerCatalogDaoSmokeTest() {
    }

    public static void main(String[] args) {
        verifyCustomerAndMembership();
        verifyVouchers();
        verifyProductsAndCategories();
    }

    private static void verifyCustomerAndMembership() {
        CustomerMembershipDAO membershipDAO = new CustomerMembershipDAO();
        List<CustomerMembership> memberships =
                membershipDAO.getAllListMembershipRank();
        require(!memberships.isEmpty(), "customer membership list is empty");
        require(
                !membershipDAO.searchByName("").isEmpty(),
                "customer membership search failed");

        CustomerMembership selected = memberships.stream()
                .filter(item -> item.getRankName() != null)
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError("no ranked customer found"));

        CustomerDAO customerDAO = new CustomerDAO();
        int customerId = selected.getCustomerId();
        int accountId = customerDAO.getAccountIdByCustomerId(customerId);
        require(accountId > 0, "customer account lookup failed");
        require(
                customerDAO.getCustomerId(accountId) == customerId,
                "account-to-customer lookup returned the wrong ID");

        CustomerRanking ranking =
                membershipDAO.getCustomeRankingById(accountId);
        require(ranking != null, "customer ranking lookup failed");

        DiscountCodeDAO discountDAO = new DiscountCodeDAO();
        Customer byAccount =
                discountDAO.getCustomerMembershipByAccountId(accountId);
        Customer byCustomer =
                discountDAO.getCustomerMembershipByCustomerId(customerId);
        require(
                byAccount != null && byAccount.getCustomerId() == customerId,
                "membership lookup by account failed");
        require(
                byCustomer != null && byCustomer.getAccountId() == accountId,
                "membership lookup by customer failed");
    }

    private static void verifyVouchers() {
        CustomerMembershipDAO membershipDAO = new CustomerMembershipDAO();
        CustomerDAO customerDAO = new CustomerDAO();
        CustomerCodeDAO customerCodeDAO = new CustomerCodeDAO();
        DiscountCodeDAO discountDAO = new DiscountCodeDAO();

        boolean checked = false;
        for (CustomerMembership membership
                : membershipDAO.getAllListMembershipRank()) {
            int accountId = customerDAO.getAccountIdByCustomerId(
                    membership.getCustomerId());
            List<CustomerDiscountCode> codes =
                    customerCodeDAO.getCustomerCode(accountId);
            if (!codes.isEmpty()) {
                require(
                        discountDAO.getVoucherById(accountId) != null,
                        "voucher lookup returned null");
                checked = true;
                break;
            }
        }
        require(checked, "no customer voucher was available for testing");
    }

    private static void verifyProductsAndCategories() {
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categories = categoryDAO.getAllCategories();
        require(!categories.isEmpty(), "category list is empty");
        require(
                !categoryDAO.getAllCategoriesForPOS().isEmpty(),
                "POS category list is empty");

        ProductDAO productDAO = new ProductDAO();
        Map<Integer, Product> products = productDAO.getProduct();
        require(!products.isEmpty(), "product map is empty");

        Product selected = products.values().iterator().next();
        Product detail = productDAO.getProductById(selected.getProductId());
        require(detail != null, "product lookup by ID failed");
        require(
                !productDAO.getProductByName(detail.getProductName()).isEmpty(),
                "product lookup by name failed");
        require(
                !productDAO.getAllProductsForPOS().isEmpty(),
                "POS product list is empty");
        require(
                !productDAO.getBestSellingProducts(5).isEmpty(),
                "best-selling product list is empty");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
