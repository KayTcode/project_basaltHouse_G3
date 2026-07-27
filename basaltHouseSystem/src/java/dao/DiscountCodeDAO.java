package dao;

import model.Customer;
import model.DiscountCode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.CustomerDiscountCode;

public class DiscountCodeDAO extends DBContext {

    private static final String CUSTOMER_MEMBERSHIP_SELECT = """
            SELECT customer.CustomerId,
                   customer.FullName,
                   customer.Phone,
                   customer.AccountId,
                   rank.RankName,
                   rank.DiscountValue
            FROM Customers customer
            LEFT JOIN CustomerMemberships membership
              ON membership.CustomerId = customer.CustomerId
            LEFT JOIN MembershipRanks rank
              ON rank.RankId = membership.RankId
            """;

    public int markExpiredVouchersAsDeleted() {
        try {
            String sql = """
                         UPDATE DiscountCodes
                         SET IsDeleted = 1,
                             IsActive = 0
                         WHERE IsDeleted = 0
                           AND EndDate IS NOT NULL
                           AND EndDate < GETDATE()
                         """;
            PreparedStatement st = connection.prepareStatement(sql);
            return st.executeUpdate();
        } catch (Exception e) {
            System.err.println("markExpiredVouchersAsDeleted Error: " + e.getMessage());
        }

        return 0;
    }

    public DiscountCode checkDiscountCode(String code) {
        DiscountCode dto = null;
        try {
            String sql = """
                         SELECT DiscountId, Code, DiscountPercent, DiscountAmount 
                         FROM DiscountCodes 
                         WHERE Code = ? AND IsActive = 1 AND IsDeleted = 0 
                         AND (StartDate IS NULL OR StartDate <= GETDATE())
                         AND (EndDate IS NULL OR EndDate >= GETDATE())
                         """;
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, code);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                dto = new DiscountCode();
                dto.setDiscountId(rs.getInt("DiscountId"));
                dto.setCode(rs.getString("Code"));
                dto.setDiscountPercent(rs.getBigDecimal("DiscountPercent"));
                dto.setDiscountAmount(rs.getBigDecimal("DiscountAmount"));
            }
        } catch (Exception e) {
            System.err.println("checkDiscountCode Error: " + e.getMessage());
        }
        return dto;
    }

    public Customer getCustomerMembership(String phone) {
        String sql = CUSTOMER_MEMBERSHIP_SELECT + """
                WHERE customer.IsDeleted = 0
                  AND customer.Phone = ?
                """;
        return queryCustomerMembership(sql, phone);
    }

    public List<Customer> searchCustomerMembershipByName(String name) {
        List<Customer> list = new ArrayList<>();
        String keyword = name == null ? "" : name.trim();
        String sql = CUSTOMER_MEMBERSHIP_SELECT + """
                WHERE customer.IsDeleted = 0
                  AND (customer.FullName LIKE ? OR customer.Phone LIKE ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            statement.setString(1, pattern);
            statement.setString(2, pattern);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    list.add(mapCustomerMembership(result));
                }
            }
        } catch (Exception e) {
            System.err.println("searchCustomerMembershipByName Error: " + e.getMessage());
        }
        return list;
    }

    public Customer getCustomerMembershipByAccountId(int accountId) {
        String sql = CUSTOMER_MEMBERSHIP_SELECT + """
                WHERE customer.IsDeleted = 0
                  AND customer.AccountId = ?
                """;
        return queryCustomerMembership(sql, accountId);
    }

    public Customer getCustomerMembershipByCustomerId(int customerId) {
        String sql = CUSTOMER_MEMBERSHIP_SELECT + """
                WHERE customer.IsDeleted = 0
                  AND customer.CustomerId = ?
                """;
        return queryCustomerMembership(sql, customerId);
    }

    private Customer queryCustomerMembership(String sql, Object value) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, value);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? mapCustomerMembership(result) : null;
            }
        } catch (Exception e) {
            System.err.println("queryCustomerMembership Error: "
                    + e.getMessage());
            return null;
        }
    }

    private Customer mapCustomerMembership(ResultSet result)
            throws Exception {
        Customer customer = new Customer();
        customer.setCustomerId(result.getInt("CustomerId"));
        customer.setFullName(result.getString("FullName"));
        customer.setPhone(result.getString("Phone"));
        customer.setAccountId(result.getInt("AccountId"));
        customer.setRankName(result.getString("RankName"));
        customer.setDiscountValue(result.getBigDecimal("DiscountValue"));
        return customer;
    }

    public List<DiscountCode> getDiscountCode() {
        List<DiscountCode> list = new ArrayList<>();
        try {
            String sql = """
                         SELECT DiscountId,
                                Code,
                                DiscountPercent,
                                DiscountAmount,
                                Description,
                                StartDate,
                                EndDate,
                                DATEDIFF(
                                    DAY, GETDATE(), EndDate) AS DayTime
                         FROM DiscountCodes
                         WHERE IsActive = 1
                           AND IsPublic = 1
                           AND IsDeleted = 0
                           AND (StartDate IS NULL
                                OR StartDate <= GETDATE())
                           AND (EndDate IS NULL
                                OR EndDate >= GETDATE())
                         """;
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                DiscountCode d = new DiscountCode(rs.getInt("DiscountId"),
                        rs.getString("Code"),
                        rs.getBigDecimal("DiscountPercent"),
                        rs.getBigDecimal("DiscountAmount"),
                        rs.getObject("StartDate", LocalDateTime.class),
                        rs.getObject("EndDate", LocalDateTime.class),
                        rs.getString("Description"),
                        rs.getInt("DayTime"));
                list.add(d);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;

    }

    public List<CustomerDiscountCode> getVoucherById(int accountId) {
        List<CustomerDiscountCode> list = new ArrayList<>();
        try {
            String sql = """
                         SELECT cd.CustomerDiscountId,
                                cd.AccountId,
                                d.DiscountId,
                                d.Code,
                                d.DiscountAmount,
                                d.DiscountPercent,
                                d.StartDate,
                                d.EndDate,
                                cd.IsUsed,
                                cd.UsedDate,
                                d.Description,
                                DATEDIFF(DAY, GETDATE(), d.EndDate) AS DayTime
                         FROM CustomerDiscountCodes cd
                         JOIN DiscountCodes d ON cd.DiscountId = d.DiscountId
                         WHERE d.IsActive = 1
                           AND d.IsDeleted = 0
                           AND cd.AccountId = ?
                           AND ISNULL(cd.IsUsed, 0) = 0
                           AND (d.StartDate IS NULL
                                OR d.StartDate <= GETDATE())
                           AND (d.EndDate IS NULL
                                OR d.EndDate >= GETDATE())
                         ORDER BY d.EndDate ASC
                         """;

            PreparedStatement st = connection.prepareStatement(sql);
            st.setObject(1, accountId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                list.add(mapCustomerVoucher(rs));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;
    }

    public CustomerDiscountCode getCustomerVoucherByCode(int accountId, String code) {
        try {
            String sql = """
                         SELECT cd.CustomerDiscountId,
                                cd.AccountId,
                                d.DiscountId,
                                d.Code,
                                d.DiscountAmount,
                                d.DiscountPercent,
                                d.StartDate,
                                d.EndDate,
                                cd.IsUsed,
                                cd.UsedDate,
                                d.Description,
                                DATEDIFF(DAY, GETDATE(), d.EndDate) AS DayTime
                         FROM CustomerDiscountCodes cd
                         JOIN DiscountCodes d ON cd.DiscountId = d.DiscountId
                         WHERE d.IsActive = 1
                           AND d.IsDeleted = 0
                           AND d.IsPublic = 0
                           AND cd.AccountId = ?
                           AND UPPER(d.Code) = UPPER(?)
                           AND (d.EndDate IS NULL OR d.EndDate >= GETDATE())
                         """;
            PreparedStatement st = connection.prepareStatement(sql);
            st.setObject(1, accountId);
            st.setString(2, code);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return mapCustomerVoucher(rs);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    private CustomerDiscountCode mapCustomerVoucher(ResultSet result)
            throws Exception {
        boolean used = result.getBoolean("IsUsed");
        return new CustomerDiscountCode(
                result.getInt("CustomerDiscountId"),
                result.getInt("AccountId"),
                result.getInt("DiscountId"),
                result.getBigDecimal("DiscountPercent"),
                result.getBigDecimal("DiscountAmount"),
                result.getObject("StartDate", LocalDateTime.class),
                result.getObject("EndDate", LocalDateTime.class),
                used,
                result.getObject("UsedDate", LocalDateTime.class),
                result.getString("Description"),
                result.getInt("DayTime"),
                result.getString("Code"),
                used ? 0 : 1);
    }

    public boolean updateCustomerVoucherStatus(int customerDiscountId, int status) {
        try {
            String sql = """
                         UPDATE CustomerDiscountCodes
                         SET [Status] = ?
                         WHERE CustomerDiscountId = ?
                         """;
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, status);
            st.setInt(2, customerDiscountId);
            return st.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return false;
    }

    public boolean markVoucherAsUsed(int accountId, String code) {
        try {
            String sql = """
                         UPDATE CustomerDiscountCodes
                         SET IsUsed   = 1,
                             UsedDate = GETDATE()
                         WHERE AccountId  = ?
                           AND DiscountId IN (
                                   SELECT DiscountId FROM DiscountCodes
                                   WHERE UPPER(Code) = UPPER(?)
                               )
                           AND ISNULL(IsUsed, 0) = 0
                         """;
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, accountId);
            st.setString(2, code);
            return st.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("markVoucherAsUsed Error: " + e.getMessage());
        }
        return false;
    }

    public boolean hasCustomerUsedDiscount(int customerId, String code) {
        if (customerId <= 0 || code == null || code.trim().isEmpty()) {
            return false;
        }
        String sql = """
                     SELECT TOP 1 1
                     FROM Orders orders
                     JOIN DiscountCodes discount
                       ON discount.DiscountId = orders.DiscountId
                     WHERE orders.CustomerId = ?
                       AND UPPER(discount.Code) = UPPER(?)
                       AND orders.OrderStatus <> 'Cancelled'
                       AND orders.IsDeleted = 0
                     """;
        return exists(sql, customerId, code.trim());
    }

    public boolean hasAccountUsedDiscount(int accountId, String code) {
        if (accountId <= 0 || code == null || code.trim().isEmpty()) {
            return false;
        }
        String sql = """
                     SELECT TOP 1 1
                     FROM Orders orders
                     JOIN Customers customer
                       ON customer.CustomerId = orders.CustomerId
                     JOIN DiscountCodes discount
                       ON discount.DiscountId = orders.DiscountId
                     WHERE customer.AccountId = ?
                       AND UPPER(discount.Code) = UPPER(?)
                       AND orders.OrderStatus <> 'Cancelled'
                       AND orders.IsDeleted = 0
                     """;
        return exists(sql, accountId, code.trim());
    }

    public boolean hasTableSessionUsedDiscount(int tableSessionId) {
        if (tableSessionId <= 0) {
            return false;
        }
        String sql = """
                     SELECT TOP 1 1
                     FROM Orders
                     WHERE TableSessionId = ?
                       AND DiscountId IS NOT NULL
                       AND DiscountAmount > 0
                       AND OrderStatus <> 'Cancelled'
                       AND IsDeleted = 0
                     """;
        return exists(sql, tableSessionId);
    }

    public boolean hasTableUsedDiscount(int tableId) {
        if (tableId <= 0) {
            return false;
        }
        String sql = """
                     SELECT TOP 1 1
                     FROM Orders orders
                     JOIN TableSessions session
                       ON session.SessionId = orders.TableSessionId
                     WHERE session.TableId = ?
                       AND session.Status IN ('ACTIVE', 'Open')
                       AND session.IsDeleted = 0
                       AND orders.DiscountId IS NOT NULL
                       AND orders.DiscountAmount > 0
                       AND orders.OrderStatus <> 'Cancelled'
                       AND orders.IsDeleted = 0
                     """;
        return exists(sql, tableId);
    }

    private boolean exists(String sql, Object... parameters) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        } catch (Exception e) {
            System.err.println("exists Error: " + e.getMessage());
            return false;
        }
    }

    public void updateActiveAt1(int id) {
        try {
            String sql = """
                         UPDATE DiscountCodes
                         SET IsActive = 1
                         WHERE DiscountId = ?
                         """;
            PreparedStatement st = connection.prepareStatement(sql);
            st.setObject(1, id);
            st.executeUpdate();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }
}
