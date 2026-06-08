/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dao.DBContext;
import model.Account;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author KayT
 */
public class AuthDAO extends DBContext {

    private static String sql = "";
    private static PreparedStatement ps;
    private static ResultSet rs;

    /*
    Method for login
     */
    public Account findByEmail(String email) {
        sql = """
              SELECT AccountId, RoleId, Email, PasswordHash, IsEmailVerified, IsActive, IsDeleted, CreatedAt, FailedAttempts, LockoutEnd
               FROM Accounts
               WHERE Email = ? and IsDeleted = 0 
              """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setObject(1, email);
            rs = ps.executeQuery();
            if (rs.next()) {
                Account account = new Account();
                account.setAccountId(rs.getInt("AccountId"));
                account.setRoleId(rs.getInt("RoleId"));
                account.setEmail(rs.getString("Email"));
                account.setPasswordHash(rs.getString("PasswordHash"));
                account.setIsEmailVerified(rs.getBoolean("IsEmailVerified"));
                account.setIsActive(rs.getBoolean("IsActive"));

                Timestamp createAt = rs.getTimestamp("CreatedAt");
                if (createAt != null) {
                    account.setCreatedAt(createAt.toLocalDateTime());//Set time when create account
                }
                account.setIsDeleted(rs.getBoolean("IsDeleted"));
                account.setFailedAttempts(rs.getInt("FailedAttempts")); //Number of failed login attempts
                Timestamp lockoutEnd = rs.getTimestamp("LockoutEnd");
                if (lockoutEnd != null) {
                    account.setLockoutEnd(lockoutEnd.toLocalDateTime()); //Set a timeout period after 5 unsuccessful login attempts.
                }
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
            ps.setObject(1, roleId);
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
        Map<String, String> result = new HashMap<>(); //Set map 
        result.put("fullName", "");
        result.put("avatarUrl", null);
        int roleId = account.getRoleId();
        boolean hasAvatar;
        switch (roleId) {
            case 1:
                sql = """
                      SELECT FullName, AvatarUrl
                      FROM Customers
                      WHERE AccountId = ?
                      """;
                hasAvatar = true;
                break;
            case 2:
                sql = """
                      SELECT FullName, AvatarUrl
                      FROM Staffs
                      WHERE AccountId = ?
                      """;
                hasAvatar = false;
                break;
            case 4:
                sql = """
                      SELECT FullName, AvatarUrl
                      FROM Shippers
                      WHERE AccountId = ?
                      """;
                hasAvatar = false;
                break;
            case 5:
                sql = """
                      SELECT FullName, AvatarUrl
                      FROM Cashiers
                      WHERE AccountId = ?
                      """;
                hasAvatar = false;
                break;
            default:
                result.put("fullName", account.getEmail());
                return result;
        }
        try {
            ps = connection.prepareStatement(sql);
            ps.setObject(1, account.getAccountId());
            rs = ps.executeQuery();
            if (rs.next()) {
                String fullName = rs.getString("FullName");
                result.put("fullName", fullName != null ? fullName : account.getEmail());
                if (hasAvatar) {
                    result.put("avatarUrl", rs.getString("AvatarUrl"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public void incrementFailedAttempts(int accountId) {
        sql = """
              SET NOCOUNT ON;
              UPDATE Accounts 
              SET FailedAttempts = FailedAttempts + 1 
              WHERE AccountId = ?
              """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, accountId);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void lockAccount(int accountId, LocalDateTime lockoutEnd) {
        sql = """
              SET NOCOUNT ON;
              UPDATE Accounts
              SET IsActive = 0, LockoutEnd = ? 
              WHERE AccountId = ?
              """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setTimestamp(1, Timestamp.valueOf(lockoutEnd));
            ps.setInt(2, accountId);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void unlockAccount(int accountId) {
        sql = """
              SET NOCOUNT ON;
              UPDATE Accounts
              SET IsActive = 1, FailedAttempts = 0, LockoutEnd = NULL 
              WHERE AccountId = ?
              """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, accountId);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void resetFailedAttempts(int accountId) {
        sql = """
              SET NOCOUNT ON;
              UPDATE Accounts
              SET FailedAttempts = 0, LockoutEnd = NULL
              WHERE AccountId = ?
              """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, accountId);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
//    public static void main(String[] args) {
//        AuthDAO dao = new AuthDAO();
//        Account account = dao.findByEmail("kayt1206.js@gmail.com");
//        String roleName = dao.getRoleNameById(account.getRoleId());
//        if (account != null) {
//            System.out.println(account.getEmail());
//        }
//        System.out.println(account.getRoleId());
//    }
}
