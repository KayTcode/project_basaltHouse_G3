package dao;

import model.Account;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AuthDAO extends DBContext {

    private static String sql = "";
    private static PreparedStatement ps;
    private static ResultSet rs;

    /*
     Method for login
     */
    public Account findByEmail(String email) {
        sql = """
              SELECT AccountId, RoleId, Email, PasswordHash,
                     IsEmailVerified, IsActive, IsDeleted, CreatedAt,
                     FailedAttempts, IsLocked
              FROM Accounts
              WHERE Email = ? AND IsDeleted = 0
              """;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            rs = ps.executeQuery();

            if (rs.next()) {
                Account account = new Account();

                account.setAccountId(rs.getInt("AccountId"));
                account.setRoleId(rs.getInt("RoleId"));
                account.setEmail(rs.getString("Email"));
                account.setPasswordHash(rs.getString("PasswordHash"));
                account.setIsEmailVerified(rs.getBoolean("IsEmailVerified"));
                account.setIsActive(rs.getBoolean("IsActive"));
                account.setIsDeleted(rs.getBoolean("IsDeleted"));

                Timestamp createdAt = rs.getTimestamp("CreatedAt");
                if (createdAt != null) {
                    account.setCreatedAt(createdAt.toLocalDateTime());
                }

                account.setFailedAttempts(rs.getInt("FailedAttempts"));
                account.setIsLocked(rs.getBoolean("IsLocked"));

                return account;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public String getRoleNameById(int roleId) {
        sql = """
              SELECT RoleName
              FROM Roles
              WHERE RoleId = ? AND IsDeleted = 0
              """;

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, roleId);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("RoleName");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return "Unknown";
    }

    public Map<String, String> getFullNameAndAvatarByAccount(Account account) {
        Map<String, String> result = new HashMap<>();
        result.put("fullName", account.getEmail());
        result.put("avatarUrl", null);

        int roleId = account.getRoleId();

        switch (roleId) {
            case 1:
                sql = """
                      SELECT FullName, AvatarUrl
                      FROM Customers
                      WHERE AccountId = ?
                      """;
                break;

            case 2:
                sql = """
                      SELECT FullName, AvatarUrl
                      FROM Staffs
                      WHERE AccountId = ?
                      """;
                break;

            case 4:
                sql = """
                      SELECT FullName, AvatarUrl
                      FROM Shippers
                      WHERE AccountId = ?
                      """;
                break;

            case 5:
                sql = """
                      SELECT FullName, AvatarUrl
                      FROM Cashiers
                      WHERE AccountId = ?
                      """;
                break;

            default:
                return result;
        }

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, account.getAccountId());
            rs = ps.executeQuery();

            if (rs.next()) {
                String fullName = rs.getString("FullName");
                String avatarUrl = rs.getString("AvatarUrl");

                result.put("fullName", fullName != null ? fullName : account.getEmail());
                result.put("avatarUrl", avatarUrl);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public void incrementFailedAttempts(int accountId) {
        sql = """
              UPDATE Accounts
              SET FailedAttempts = FailedAttempts + 1
              WHERE AccountId = ?
              """;

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, accountId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void lockAccount(int accountId) {
        sql = """
              UPDATE Accounts
              SET IsActive = 0,
                  IsLocked = 1
              WHERE AccountId = ?
              """;

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, accountId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void unlockAccount(int accountId) {
        sql = """
              UPDATE Accounts
              SET IsActive = 1,
                  IsLocked = 0,
                  FailedAttempts = 0
              WHERE AccountId = ?
              """;

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, accountId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void resetFailedAttempts(int accountId) {
        sql = """
              UPDATE Accounts
              SET FailedAttempts = 0
              WHERE AccountId = ?
              """;

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, accountId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
