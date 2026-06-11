/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author KayT
 */
public class RegisterDAO extends DBContext {

    private static String sql = "";
    private static PreparedStatement ps;
    private static ResultSet rs;

    public boolean isEmailExitsed(String email) {
        sql = """
              SELECT COUNT(1)
              FROM Accounts
              WHERE Email = ? AND IsDeleted = 0
              """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setObject(1, email);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public int savePendingRegistration(String email, String passwordHash, String fullName, String phone, String otpCode, LocalDateTime otpExpiredAt) {
        String deleteSql = """
                           UPDATE PendingRegistrations
                           SET IsDeleted = 1
                           WHERE Email = ? AND IsUsed = 0 and IsDeleted = 0
                           """;
        String insertSql = """
                            INSERT INTO PendingRegistrations
                                       (Email
                                       ,PasswordHash
                                       ,FullName
                                       ,Phone
                                       ,RegisterRole
                                       ,OtpCode
                                       ,OtpExpiredAt
                                       ,IsUsed
                                       ,AttemptCount
                                       ,CreatedAt
                                       ,IsDeleted)
                                 VALUES
                                       (?,?,?,?,'Customer',?,?,0,0,?,0)
                           """;
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement psDelete = connection.prepareStatement(deleteSql)) {
                psDelete.setObject(1, email);
                psDelete.executeUpdate();
            }
            try (PreparedStatement psInsert = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);) {
                psInsert.setObject(1, email);
                psInsert.setObject(2, passwordHash);
                psInsert.setObject(3, fullName);
                psInsert.setObject(4, phone);
                psInsert.setObject(5, otpCode);
                psInsert.setObject(6, Timestamp.valueOf(otpExpiredAt));
                psInsert.setObject(7, Timestamp.valueOf(LocalDateTime.now()));
                psInsert.executeUpdate();
                connection.commit();
                try (ResultSet rs = psInsert.getGeneratedKeys()) {
                    boolean hasKey = rs.next();
                    if (hasKey) {
                        int id = rs.getInt(1);
                        System.out.println("[DEBUG] Generated PendingId = " + id);
                        return id;
                    } else {
                        System.err.println("[DEBUG] WARN: executeUpdate thành công nhưng getGeneratedKeys() rỗng!");
                        // Fallback: Truy vấn lại PendingId vừa insert
                        return getLastPendingId(connection, email);
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> getPendingByEmail(String email) {
        sql = """
              SELECT TOP 1 PendingId, Email, PasswordHash, FullName,
              Phone, RegisterRole, OtpCode, OtpExpiredAt, IsUsed, AttemptCount
              FROM PendingRegistrations
              WHERE Email = ? AND IsUsed = 0 AND IsDeleted = 0
              ORDER BY CreatedAt DESC
              """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setObject(1, email);
            rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> pending = new HashMap<>();
                pending.put("pendingId", rs.getInt("PendingId"));
                pending.put("email", rs.getString("Email"));
                pending.put("passwordHash", rs.getString("PasswordHash"));
                pending.put("fullName", rs.getString("FullName"));
                pending.put("phone", rs.getString("Phone"));
                pending.put("registerRole", rs.getString("RegisterRole"));
                pending.put("otpCode", rs.getString("OtpCode"));
                Timestamp ts = rs.getTimestamp("OtpExpiredAt");
                pending.put("otpExpiredAt", ts != null ? ts.toLocalDateTime() : null);
                pending.put("isUsed", rs.getBoolean("IsUsed"));
                pending.put("attemptCount", rs.getInt("AttemptCount"));
                return pending;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void increaseAttemptCount(int pendingId) {
        sql = """
              UPDATE PendingRegistrations
              SET AttemptCount = AttemptCount + 1
              WHERE PendingId = ?
              """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setObject(1, pendingId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void markPendingAsUsed(int pendingId, Connection connection) {
        sql = """
              UPDATE PendingRegistrations
              SET IsUsed = 1
              WHERE PendingId = ?
              """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setObject(1, pendingId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void completeRegistration(Map<String, Object> pendingData) throws SQLException {
        int pendingId = (int) pendingData.get("pendingId");
        String email = (String) pendingData.get("email");
        String passwordHash = (String) pendingData.get("passwordHash");
        String fullName = (String) pendingData.get("fullName");
        String phone = (String) pendingData.get("phone");

        String sqlRole = """
                         SELECT RoleId
                         FROM Roles
                         WHERE RoleName = 'Customer' AND IsDeleted = 0
                         """;
        String sqlAccount = """
                            INSERT INTO [dbo].[Accounts]
                                       ([RoleId]
                                       ,[Email]
                                       ,[PasswordHash]
                                       ,[IsEmailVerified]
                                       ,[IsActive]
                                       ,[CreatedAt]
                                       ,[IsDeleted])
                                 VALUES
                                       (?,?,?,1,1,?,0)
                            """;
        String sqlCustomer = """
                             INSERT INTO [dbo].[Customers]
                                        ([AccountId]
                                        ,[FullName]
                                        ,[Phone]
                                        ,[CreatedAt]
                                        ,[IsDeleted])
                                  VALUES
                                        (?,?,?,?,0)
                             """;
        try {
            connection.setAutoCommit(false);
            int roleId = -1;
            ps = connection.prepareStatement(sqlRole);
            rs = ps.executeQuery();
            if (rs.next()) {
                roleId = rs.getInt("RoleId");
            }
            if (roleId == -1) {
                throw new SQLException("Không tìm thấy role Customer trong hệ thống");
            }
            int newAccountId;
            PreparedStatement psAccount = connection.prepareStatement(sqlAccount, Statement.RETURN_GENERATED_KEYS);
            psAccount.setObject(1, roleId);
            psAccount.setObject(2, email);
            psAccount.setObject(3, passwordHash);
            psAccount.setObject(4, Timestamp.valueOf(LocalDateTime.now()));
            psAccount.executeUpdate();
            rs = psAccount.getGeneratedKeys();
            if (rs.next()) {
                newAccountId = rs.getInt(1);
            } else {
                throw new SQLException("Không lấy được AccountId sau khi INSERT vào Accounts");
            }

            PreparedStatement psCustomer = connection.prepareStatement(sqlCustomer);
            psCustomer.setObject(1, newAccountId);
            psCustomer.setObject(2, fullName);
            psCustomer.setObject(3, phone);
            psCustomer.setObject(4, Timestamp.valueOf(LocalDateTime.now()));
            psCustomer.executeUpdate();
            markPendingAsUsed(pendingId, connection);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new RuntimeException(e);
        }
    }

    private int getLastPendingId(Connection connection, String email) {
        sql = "SELECT TOP 1 PendingId FROM [PendingRegistrations] "
                + "WHERE Email = ? AND IsDeleted = 0 ORDER BY CreatedAt DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("PendingId");
                    System.out.println("[DEBUG] Fallback PendingId = " + id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }
}
