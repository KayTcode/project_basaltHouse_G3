/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

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

    public int savePendingRegistration(String email, String passwordHash, String fullName, String phone, String registerRole,
            String otpCode, LocalDateTime otpExpiredAt) {
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
            PreparedStatement psDelete = connection.prepareStatement(deleteSql);
            psDelete.setObject(1, email);
            psDelete.executeUpdate();

            PreparedStatement psInsert = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            psInsert.setObject(1, email);
            psInsert.setObject(2, passwordHash);
            psInsert.setObject(3, fullName);
            psInsert.setObject(4, phone);
            psInsert.setObject(5, otpCode);
            psInsert.setObject(6, Timestamp.valueOf(otpExpiredAt));
            psInsert.setObject(7, Timestamp.valueOf(LocalDateTime.now()));
            psInsert.executeUpdate();
            connection.commit();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    public Map<String, Object> getPendingByEmail(String email) {
        sql = """
              SELECT TOP 1 PendingId, Email, PasswordHash, FullName,
              Phone, RegisterRole, OtpCode, OtpEpiredAt, IsUsed, AttemptCount
              FROM PendingRegistration
              WHERE Email = ? AND IsUsed = 0 AND IsDeleted = 0
              ORDER BY CreateAt DESC
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
                Timestamp ts = rs.getTimestamp("OtpEpiredAt");
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
              UPDATE PendingRegistration
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

    private void markPendingAsUsed(int pendingId) {
        sql = """
              UPDATE PendingRegistration
              SET IsUsed = 1
              WHERE PendingId = ?
              """;
        try {
            ps = connection.prepareStatement(sql);
            ps.setObject(1, pendingId);
            ps.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
}
