package dao;

import model.Account;
import java.sql.*;
import java.time.LocalDateTime;
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

    /*
    Method for forgot-password
     */
    public Map<String, Object> findActiveAccountByEmail(String email) {
        sql = """
              SELECT [AccountId]
                    ,[Email]
                FROM [dbo].[Accounts]
              WHERE Email = ? AND IsActive = 1 AND IsDeleted = 0
              """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setObject(1, email);
            rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> account = new HashMap<>();
                account.put("accountId", rs.getInt("AccountId"));
                account.put("email", rs.getString("Email"));
                return account;
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
        return null;
    }

//    public static void main(String[] args) {
//        AuthDAO dao = new AuthDAO();
//        String email = "anhiuemmatrui@gmail.com";
//        Map<String, Object> result = dao.findActiveAccountByEmail(email);
//        int accountId = (int) result.get("accountId");
//        String emailoutput = (String) result.get("email");
//        System.out.println(accountId + " " +emailoutput);
//    }
    public void updatePassword(int accountId, String newPasswordHash) {
        sql = """
             UPDATE [dbo].[Accounts]
                 SET [PasswordHash] = ?
               WHERE [AccountId] = ? AND [IsDeleted] = 0
              """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setObject(1, newPasswordHash);
            ps.setObject(2, accountId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveEmailOtp(int accountId, String otpCode, String purpose, LocalDateTime expiredAt) {
        String deleteSql = """
                           UPDATE [dbo].[EmailOtps]
                              SET [IsDeleted] = 1
                            WHERE AccountId = ? AND Purpose = ? AND IsDeleted = 0
                           """;
        String insertSql = """
                           INSERT INTO [dbo].[EmailOtps]
                                      ([AccountId]
                                      ,[OtpCode]
                                      ,[Purpose]
                                      ,[ExpiredAt]
                                      ,[CreatedAt]
                                      ,[IsDeleted])
                                VALUES
                                      (?,?,?,?,?,0)
                            """;
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement psDelete = connection.prepareStatement(deleteSql)) {
                psDelete.setObject(1, accountId);
                psDelete.setObject(2, purpose);
                psDelete.executeUpdate();
            }
            try (PreparedStatement psInsert = connection.prepareCall(insertSql)) {
                psInsert.setObject(1, accountId);
                psInsert.setObject(2, otpCode);
                psInsert.setObject(3, purpose);
                psInsert.setObject(4, Timestamp.valueOf(expiredAt));
                psInsert.setObject(5, Timestamp.valueOf(LocalDateTime.now()));
                psInsert.executeUpdate();
            }
            connection.commit();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> getLastOtp(int accountId, String purpose) {
        sql = """
        SELECT TOP 1 OtpId, OtpCode, ExpiredAt
        FROM EmailOtps
        WHERE AccountId = ? AND Purpose = ? AND IsDeleted = 0
        ORDER BY CreatedAt DESC
        """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setObject(1, accountId);
            ps.setObject(2, purpose);
            rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> otp = new HashMap<>();
                otp.put("otpId", rs.getInt("OtpId"));
                otp.put("otpCode", rs.getString("OtpCode"));
                otp.put("purpose", purpose);
                Timestamp ts = rs.getTimestamp("ExpiredAt");
                otp.put("expiredAt", ts != null ? ts.toLocalDateTime() : null);
                return otp;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
//    public static void main(String[] args) {
//        AuthDAO dao = new AuthDAO();
//        int accountId = 4;
//        String purpose = "FORGOT_PASSWORD";
//        Map<String, Object> rs = dao.getLastOtp(accountId, purpose);
//        System.out.println(rs);
//    }

    public void markOtpUsed(int otpId) {
        sql = """
              UPDATE EmailOtps 
              SET IsDeleted = 1
              WHERE OtpId = ?  AND IsDeleted = 0
              """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setObject(1, otpId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
