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
                    COALESCE(mr.RankId, 0)                  AS RankId,
                    COALESCE(mr.RankName, N'Chưa xếp hạng') AS RankName,
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
            String sql = """
                         SELECT RankId, RankName
                         FROM MembershipRanks
                         WHERE IsDeleted = 0
                         ORDER BY MinTotalSpent, RankId
                         """;
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
            if (!isActiveRank(rankId)) {
                return false;
            }

            Account acc = new Account();
            acc.setRoleId(2); 
            acc.setEmail(email);
            acc.setPasswordHash(PasswordUtils.hashSHA256(rawPassword));
            acc.setIsEmailVerified(true);
            acc.setIsActive(true);

            boolean created = accountDAO.addAccount(acc, fullName, phone);
            if (!created) return false;

            
            int accountId = getAccountIdByEmail(email);
            if (accountId == -1) return false;

        
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
            return st.executeUpdate() == 1;
        } catch (Exception e) {
            System.err.println("[AdminCustomerDAO.addCustomer] " + e.getMessage());
            return false;
        }
    }

    public boolean updateCustomer(int accountId, String email, String fullName, String phone,
                                   int rankId, double totalSpent, boolean isLocked) {
        AdminAccountDAO accountDAO = new AdminAccountDAO();
        try {
            if (!isActiveRank(rankId) && !isCurrentCustomerRank(accountId, rankId)) {
                return false;
            }

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
                return st.executeUpdate() == 1;
            }
            return true;
        } catch (Exception e) {
            System.err.println("[AdminCustomerDAO.updateCustomer] " + e.getMessage());
            return false;
        }
    }

    private boolean isActiveRank(int rankId) {
        try {
            String sql = """
                         SELECT 1
                         FROM MembershipRanks
                         WHERE RankId = ?
                           AND IsDeleted = 0
                         """;
            st = connection.prepareStatement(sql);
            st.setInt(1, rankId);
            rs = st.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.err.println("[AdminCustomerDAO.isActiveRank] " + e.getMessage());
        }
        return false;
    }

    private boolean isCurrentCustomerRank(int accountId, int rankId) {
        try {
            String sql = """
                         SELECT 1
                         FROM CustomerMemberships cm
                         JOIN Customers c ON c.CustomerId = cm.CustomerId
                         WHERE c.AccountId = ?
                           AND cm.RankId = ?
                           AND c.IsDeleted = 0
                         """;
            st = connection.prepareStatement(sql);
            st.setInt(1, accountId);
            st.setInt(2, rankId);
            rs = st.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.err.println("[AdminCustomerDAO.isCurrentCustomerRank] " + e.getMessage());
        }
        return false;
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

    public List<CustomerViewDTO> getAllCustomersFiltered(String search, Integer rankId, String status) {
        List<CustomerViewDTO> list = new ArrayList<>();
        String sql = """
            SELECT
                a.AccountId, a.RoleId, a.Email, a.PasswordHash,
                a.IsEmailVerified, a.IsActive, a.CreatedAt,
                a.IsDeleted, a.FailedAttempts, a.IsLocked,
                c.FullName, c.Phone, c.AvatarUrl,
                COALESCE(mr.RankId, 0)                  AS RankId,
                COALESCE(mr.RankName, N'Chưa xếp hạng') AS RankName,
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
            """;

        // 1. Lọc theo tên, email hoặc số điện thoại sử dụng LIKE
        if (search != null && !search.trim().isEmpty()) {
            sql += """
                   AND (c.FullName LIKE ? OR a.Email LIKE ? OR c.Phone LIKE ?)
                   """;
        }

        // 2. Lọc theo hạng thành viên
        if (rankId != null) {
            sql += """
                   AND COALESCE(mr.RankId, 0) = ?
                   """;
        }

        // 3. Lọc theo trạng thái tài khoản
        if ("Active".equalsIgnoreCase(status)) {
            sql += """
                   AND a.IsLocked = 0
                   """;
        } else if ("Locked".equalsIgnoreCase(status)) {
            sql += """
                   AND a.IsLocked = 1
                   """;
        }

        // Sắp xếp giảm dần theo ngày tạo tài khoản
        sql += """
               ORDER BY a.CreatedAt DESC
               """;

        try {
            st = connection.prepareStatement(sql);
            int index = 1;

            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim() + "%";
                st.setString(index++, searchPattern);
                st.setString(index++, searchPattern);
                st.setString(index++, searchPattern);
            }

            if (rankId != null) {
                st.setInt(index++, rankId);
            }

            rs = st.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            System.err.println("[AdminCustomerDAO.getAllCustomersFiltered] LỖI: " + e.getMessage());
            e.printStackTrace();
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
