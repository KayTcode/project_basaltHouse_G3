package dao;

import dto.CustomerViewDTO;
import model.Account;
import model.MembershipRank;
import model.Order;
import utils.PasswordUtils;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AdminCustomerDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    public List<CustomerViewDTO> getAllCustomers() {
        List<CustomerViewDTO> list = new ArrayList<>();
        try {
            String sql = """
                SELECT
                    a.AccountId, a.RoleId, a.Email, a.PasswordHash,
                    a.IsEmailVerified, a.IsActive, a.CreatedAt,
                    a.IsDeleted, a.FailedAttempts, a.IsLocked,
                    c.FullName, c.Phone, c.AvatarUrl,
                    COALESCE(mr.RankId, 1)                  AS RankId,
                    COALESCE(mr.RankName, N'Đồng (Bronze)')  AS RankName,
                    COALESCE(cm.TotalSpent, 0)              AS TotalSpent
                FROM Accounts a
                JOIN Customers c
                    ON c.AccountId = a.AccountId
                    AND c.IsDeleted = 0
                LEFT JOIN CustomerMemberships cm
                    ON cm.CustomerId = c.CustomerId
                LEFT JOIN MembershipRanks mr
                    ON mr.RankId = cm.RankId
                WHERE a.RoleId = 2
                  AND a.IsDeleted = 0
                ORDER BY a.CreatedAt DESC
                """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (Exception e) {
            System.err.println("[AdminCustomerDAO.getAllCustomers] LỖI: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    private CustomerViewDTO mapRow(ResultSet rs) throws Exception {
        Account acc = new Account();
        acc.setAccountId(rs.getInt("AccountId"));
        acc.setRoleId(rs.getInt("RoleId"));
        acc.setEmail(rs.getString("Email"));
        acc.setPasswordHash(rs.getString("PasswordHash"));
        acc.setIsEmailVerified(rs.getBoolean("IsEmailVerified"));
        acc.setIsActive(rs.getBoolean("IsActive"));
        acc.setIsDeleted(rs.getBoolean("IsDeleted"));
        acc.setFailedAttempts(rs.getInt("FailedAttempts"));
        acc.setIsLocked(rs.getBoolean("IsLocked"));

        java.sql.Timestamp ts = rs.getTimestamp("CreatedAt");
        if (ts != null) {
            acc.setCreatedAt(ts.toLocalDateTime());
        }

        BigDecimal totalSpent = rs.getBigDecimal("TotalSpent");
        if (totalSpent == null) {
            totalSpent = BigDecimal.ZERO;
        }

        return new CustomerViewDTO(
                acc,
                rs.getNString("FullName"),
                rs.getString("Phone"),
                rs.getString("AvatarUrl"),
                rs.getInt("RankId"),
                rs.getNString("RankName"),
                totalSpent
        );
    }

    public List<MembershipRank> getAllRanks() {
        List<MembershipRank> list = new ArrayList<>();
        try {
            String sql = "SELECT RankId, RankName FROM MembershipRanks WHERE IsDeleted = 0 ORDER BY RankId";
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                MembershipRank r = new MembershipRank();
                r.setRankId(rs.getInt("RankId"));
                r.setRankName(rs.getString("RankName"));
                list.add(r);
            }
        } catch (Exception e) {
            System.err.println("[AdminCustomerDAO.getAllRanks] " + e.getMessage());
        }
        return list;
    }

    public boolean addCustomer(String email, String rawPassword, String fullName, String phone, int rankId, double totalSpent) {
        AdminAccountDAO accountDAO = new AdminAccountDAO();
        try {
            Account acc = new Account();
            acc.setRoleId(2); // Customer
            acc.setEmail(email);
            acc.setPasswordHash(PasswordUtils.hashSHA256(rawPassword));
            acc.setIsEmailVerified(true);
            acc.setIsActive(true);

            boolean created = accountDAO.addAccount(acc, fullName, phone);
            if (!created) return false;

            // Lấy accountId vừa tạo
            int accountId = getAccountIdByEmail(email);
            if (accountId == -1) return false;

            // Tạo membership với rank và totalSpent
            String sqlMs = """
                INSERT INTO CustomerMemberships (CustomerId, RankId, TotalSpent)
                SELECT c.CustomerId, ?, ?
                FROM Customers c
                WHERE c.AccountId = ? AND c.IsDeleted = 0
                """;
            st = connection.prepareStatement(sqlMs);
            st.setInt(1, rankId);
            st.setDouble(2, totalSpent);
            st.setInt(3, accountId);
            st.executeUpdate();
            return true;
        } catch (Exception e) {
            System.err.println("[AdminCustomerDAO.addCustomer] " + e.getMessage());
            return false;
        }
    }

    public boolean updateCustomer(int accountId, String email, String fullName, String phone,
                                   int rankId, double totalSpent, boolean isLocked) {
        AdminAccountDAO accountDAO = new AdminAccountDAO();
        try {
            Account acc = new Account();
            acc.setAccountId(accountId);
            acc.setRoleId(2);
            acc.setEmail(email);
            acc.setIsActive(true);
            acc.setIsLocked(isLocked);

            boolean updated = accountDAO.updateAccount(acc, fullName, phone, 2);
            if (!updated) return false;

            // Cập nhật membership (rank + totalSpent)
            String sqlMs = """
                UPDATE cm SET cm.RankId = ?, cm.TotalSpent = ?
                FROM CustomerMemberships cm
                JOIN Customers c ON c.CustomerId = cm.CustomerId
                WHERE c.AccountId = ? AND c.IsDeleted = 0
                """;
            st = connection.prepareStatement(sqlMs);
            st.setInt(1, rankId);
            st.setDouble(2, totalSpent);
            st.setInt(3, accountId);
            int rows = st.executeUpdate();

            // Nếu chưa có membership record (LEFT JOIN null) thì tạo mới
            if (rows == 0) {
                String sqlInsert = """
                    INSERT INTO CustomerMemberships (CustomerId, RankId, TotalSpent)
                    SELECT c.CustomerId, ?, ?
                    FROM Customers c
                    WHERE c.AccountId = ? AND c.IsDeleted = 0
                    """;
                st = connection.prepareStatement(sqlInsert);
                st.setInt(1, rankId);
                st.setDouble(2, totalSpent);
                st.setInt(3, accountId);
                st.executeUpdate();
            }
            return true;
        } catch (Exception e) {
            System.err.println("[AdminCustomerDAO.updateCustomer] " + e.getMessage());
            return false;
        }
    }

    private int getAccountIdByEmail(String email) {
        try {
            String sql = "SELECT AccountId FROM Accounts WHERE Email = ? AND IsDeleted = 0";
            st = connection.prepareStatement(sql);
            st.setString(1, email);
            rs = st.executeQuery();
            if (rs.next()) return rs.getInt("AccountId");
        } catch (Exception e) {
            System.err.println("[AdminCustomerDAO.getAccountIdByEmail] " + e.getMessage());
        }
        return -1;
    }

 
    public List<Order> getOrderHistoryByAccountId(int accountId) {
        List<Order> list = new ArrayList<>();
        try {
            String sql = """
                SELECT o.OrderId, o.OrderType, o.OrderStatus,
                       o.PaymentMethod, o.PaymentStatus,
                       o.TotalAmount, o.DiscountAmount, o.FinalAmount,
                       o.CreatedAt
                FROM Orders o
                JOIN Customers c ON c.CustomerId = o.CustomerId
                WHERE c.AccountId = ? AND o.IsDeleted = 0
                ORDER BY o.CreatedAt DESC
                """;
            st = connection.prepareStatement(sql);
            st.setInt(1, accountId);
            rs = st.executeQuery();
            while (rs.next()) {
                Order o = new Order();
                o.setOrderId(rs.getInt("OrderId"));
                o.setOrderType(rs.getString("OrderType"));
                o.setOrderStatus(rs.getString("OrderStatus"));
                o.setPaymentMethod(rs.getString("PaymentMethod"));
                o.setPaymentStatus(rs.getString("PaymentStatus"));
                o.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                o.setDiscountAmount(rs.getBigDecimal("DiscountAmount"));
                o.setFinalAmount(rs.getBigDecimal("FinalAmount"));
                Timestamp ts = rs.getTimestamp("CreatedAt");
                if (ts != null) o.setCreatedAt(ts.toLocalDateTime());
                list.add(o);
            }
        } catch (Exception e) {
            System.err.println("[AdminCustomerDAO.getOrderHistoryByAccountId] " + e.getMessage());
        }
        return list;
    }

    // Lấy nhanh tên + email khách hàng theo accountId để hiển thị tiêu đề trang history
    public String[] getCustomerBasicInfo(int accountId) {
        try {
            String sql = """
                SELECT c.FullName, a.Email
                FROM Customers c
                JOIN Accounts a ON a.AccountId = c.AccountId
                WHERE c.AccountId = ? AND c.IsDeleted = 0
                """;
            st = connection.prepareStatement(sql);
            st.setInt(1, accountId);
            rs = st.executeQuery();
            if (rs.next()) {
                return new String[]{ rs.getString("FullName"), rs.getString("Email") };
            }
        } catch (Exception e) {
            System.err.println("[AdminCustomerDAO.getCustomerBasicInfo] " + e.getMessage());
        }
        return new String[]{ "Khách hàng #" + accountId, "" };
    }
}
