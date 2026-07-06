package dao;

import dto.CustomerViewDTO;
import model.Account;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
}
