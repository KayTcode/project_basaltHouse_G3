/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import model.Account;

/**
 *
 * @author KayT
 */
public class ForgetPasswordDAO extends DBContext {

    private static String sql = "";
    private static PreparedStatement ps;
    private static ResultSet rs;

    public Account findAccountActiveByEmail(String email) {
        sql = """
              SELECT AccountId, Email, PasswordHash, IsActive, IsDedelted
              FROM Accounts
              WHERE Email = ? AND IsActive = 1 AND IsDeleted = 0
              """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setObject(1, email);
            rs = ps.executeQuery();
            if (rs.next()) {
                Account account = new Account();
                account.setAccountId(rs.getInt("AccountId"));
                account.setEmail(rs.getString("Email"));
                account.setPasswordHash(rs.getString("PasswordHash"));
                account.setIsActive(rs.getBoolean("IsActive"));
                account.setIsDeleted(rs.getBoolean("IsDeleted"));
                return account;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public int getAccountIdByEmail(String email) {
        sql = """
              SELECT AccountId
              FROM Accounts
              WHERE Email = ? AND IsDeleted = 0
              """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setObject(1, email);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("AccountId");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    public void insertOtp(int accountId, String otpCode, String purpose, LocalDateTime expriedAt) {
        sql = """
              INSERT INTO EmailOtps
              (AccountId, OtpCode, Purpose, ExpiredAt, CreateAt, IsDeleted)
              VALUES(?,?,?,?,?,0)
              """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setObject(1, accountId);
            ps.setObject(2, otpCode);
            ps.setObject(3, purpose);
            ps.setTimestamp(4, Timestamp.valueOf(expriedAt));
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Object[] findLastestValidOtp(int accountId, String purpose) {
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
                int otpId = rs.getInt("OtpId");
                String code = rs.getString("OtpCode");
                LocalDateTime expiredAt = rs.getTimestamp("ExpiredAt").toLocalDateTime();
                return new Object[]{code, expiredAt, otpId};
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void markOtpUsed(int otpId) {
        sql = """
              UPDATE EmailOtps SET IsDeleted = 1 WHERE OtpId = ?
              """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setObject(1, otpId);
            ps.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updatePasswordByEmail(String email, String newPassword) {
        sql = """
               UPDATE Accounts
               SET 
               PasswordHash = ?,
               FailedAttempts = 0,
               LockoutEnd = NULL,
               WHERE Email = ? AND IsDeleted = 0
               """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setObject(1, newPassword);
            ps.setObject(2, email);
           return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
