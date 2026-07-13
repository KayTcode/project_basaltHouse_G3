/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dto.AccountViewDTO;
import model.Account;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author MSI
 */
public class AdminAccountDAO extends DBContext{

   // Khai báo các đối tượng dùng chung theo đúng form của bạn
    PreparedStatement st;
    ResultSet rs;

    // ─────────────────────────────────────────────────────────────────────
    // 1. Lấy danh sách tài khoản hiển thị bảng (Gộp dữ liệu từ 4 bảng profile)
    // ─────────────────────────────────────────────────────────────────────
    public List<AccountViewDTO> getAllAccounts() {
        List<AccountViewDTO> list = new ArrayList<>();
        try {
            String sql = """
                         SELECT a.*, r.RoleName, 
                                COALESCE(c.FullName, st.FullName, ca.FullName, sh.FullName, N'Hệ thống Admin') AS FullName, 
                                COALESCE(c.Phone, st.Phone, ca.Phone, sh.Phone, '') AS Phone 
                         FROM Accounts a 
                         JOIN Roles r ON a.RoleId = r.RoleId 
                         LEFT JOIN Customers c ON a.AccountId = c.AccountId AND a.RoleId = 2 
                         LEFT JOIN Staffs st ON a.AccountId = st.AccountId AND a.RoleId = 3 
                         LEFT JOIN Cashiers ca ON a.AccountId = ca.AccountId AND a.RoleId = 4 
                         LEFT JOIN Shippers sh ON a.AccountId = sh.AccountId AND a.RoleId = 5 
                         WHERE a.IsDeleted = 0 
                         ORDER BY a.CreatedAt DESC
                         """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                Account acc = mapResultSetToAccount(rs);
                AccountViewDTO dto = new AccountViewDTO(
                    acc,
                    rs.getNString("RoleName"),
                    rs.getNString("FullName"),
                    rs.getString("Phone")
                );
                list.add(dto);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;
    }

    // ─────────────────────────────────────────────────────────────────────
    // 2. Thêm tài khoản mới và tự động phân bổ vào bảng profile tương ứng
    // ─────────────────────────────────────────────────────────────────────
    public boolean addAccount(Account acc, String fullName, String phone) {
        try {
            connection.setAutoCommit(false); // Bật Transaction bảo toàn dữ liệu hai bên băn bản
            
            String insertAccSql = """
                                  INSERT INTO Accounts (RoleId, Email, PasswordHash, IsEmailVerified, IsActive, CreatedAt, IsDeleted, FailedAttempts, IsLocked) 
                                  VALUES (?, ?, ?, ?, ?, ?, 0, 0, 0)
                                  """;
            st = connection.prepareStatement(insertAccSql, Statement.RETURN_GENERATED_KEYS);
            st.setInt(1, acc.getRoleId());
            st.setString(2, acc.getEmail());
            st.setString(3, acc.getPasswordHash());
            st.setBoolean(4, acc.isIsEmailVerified());
            st.setBoolean(5, acc.isIsActive());
            st.setObject(6, LocalDateTime.now());
            st.executeUpdate();

            int accountId = -1;
            rs = st.getGeneratedKeys();
            if (rs.next()) {
                accountId = rs.getInt(1);
            }

            // Nếu tạo tài khoản thành công và không phải là Super Admin (RoleId = 1)
            if (accountId != -1 && acc.getRoleId() != 1) {
                insertProfile(accountId, acc.getRoleId(), fullName, phone);
            }

            connection.commit();
            return true;
        } catch (Exception e) {
            try { connection.rollback(); } catch (SQLException ex) { System.err.println(ex.getMessage()); }
            System.err.println(e.getMessage());
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) { System.err.println(e.getMessage()); }
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // 3. Sửa thông tin tài khoản + Hỗ trợ chuyển đổi Role dữ liệu động
    // ─────────────────────────────────────────────────────────────────────
    public boolean updateAccount(Account acc, String fullName, String phone, int oldRoleId) {
        try {
            connection.setAutoCommit(false);
            
            String updateAccSql = """
                                  UPDATE Accounts 
                                  SET RoleId = ?, Email = ?, IsActive = ?, IsLocked = ? 
                                  WHERE AccountId = ?
                                  """;
            st = connection.prepareStatement(updateAccSql);
            st.setInt(1, acc.getRoleId());
            st.setString(2, acc.getEmail());
            st.setBoolean(3, acc.isIsActive());
            st.setBoolean(4, acc.isIsLocked());
            st.setInt(5, acc.getAccountId());
            st.executeUpdate();

            // Xử lý chuyển đổi vai trò (Nếu admin thay quyền từ nhóm này sang nhóm khác)
            if (oldRoleId != acc.getRoleId()) {
                deleteProfile(acc.getAccountId(), oldRoleId);
                if (acc.getRoleId() != 1) {
                    insertProfile(acc.getAccountId(), acc.getRoleId(), fullName, phone);
                }
            } else if (acc.getRoleId() != 1) {
                updateProfile(acc.getAccountId(), acc.getRoleId(), fullName, phone);
            }

            connection.commit();
            return true;
        } catch (Exception e) {
            try { connection.rollback(); } catch (SQLException ex) { System.err.println(ex.getMessage()); }
            System.err.println(e.getMessage());
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) { System.err.println(e.getMessage()); }
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // 4. Xóa mềm tài khoản (IsDeleted = 1) bảo toàn dữ liệu hóa đơn cũ
    // ─────────────────────────────────────────────────────────────────────
    public boolean deleteAccount(int accountId, int roleId) {
        try {
            connection.setAutoCommit(false);
            
            String deleteAccSql = """
                                  UPDATE Accounts 
                                  SET IsDeleted = 1 
                                  WHERE AccountId = ?
                                  """;
            st = connection.prepareStatement(deleteAccSql);
            st.setInt(1, accountId);
            st.executeUpdate();
            
            deleteProfile(accountId, roleId);

            connection.commit();
            return true;
        } catch (Exception e) {
            try { connection.rollback(); } catch (SQLException ex) { System.err.println(ex.getMessage()); }
            System.err.println(e.getMessage());
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) { System.err.println(e.getMessage()); }
        }
    }

    // ═════════════════ CÁC HÀM TRỢ GIÚP NỘI BỘ (INTERNAL HELPERS) ═════════════════

    private void insertProfile(int accId, int roleId, String name, String phone) throws Exception {
        String tableName = getTableNameByRole(roleId);
        if (tableName == null) return;
        
        String sql = "INSERT INTO " + tableName + " (AccountId, FullName, Phone, CreatedAt, IsDeleted) VALUES (?, ?, ?, ?, 0)";
        st = connection.prepareStatement(sql);
        st.setInt(1, accId);
        st.setNString(2, name);
        st.setString(3, phone);
        st.setObject(4, LocalDateTime.now());
        st.executeUpdate();
    }

    private void updateProfile(int accId, int roleId, String name, String phone) throws Exception {
        String tableName = getTableNameByRole(roleId);
        if (tableName == null) return;
        
        String sql = "UPDATE " + tableName + " SET FullName = ?, Phone = ? WHERE AccountId = ? AND IsDeleted = 0";
        st = connection.prepareStatement(sql);
        st.setNString(1, name);
        st.setString(2, phone);
        st.setInt(3, accId);
        st.executeUpdate();
    }

    private void deleteProfile(int accId, int roleId) throws Exception {
        String tableName = getTableNameByRole(roleId);
        if (tableName == null) return;
        
        String sql = "UPDATE " + tableName + " SET IsDeleted = 1 WHERE AccountId = ?";
        st = connection.prepareStatement(sql);
        st.setInt(1, accId);
        st.executeUpdate();
    }

    private String getTableNameByRole(int roleId) {
        return switch (roleId) {
            case 2 -> "Customers";
            case 3 -> "Staffs";
            case 4 -> "Cashiers";
            case 5 -> "Shippers";
            default -> null;
        };
    }

    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        Account acc = new Account();
        acc.setAccountId(rs.getInt("AccountId"));
        acc.setRoleId(rs.getInt("RoleId"));
        acc.setEmail(rs.getString("Email"));
        acc.setPasswordHash(rs.getString("PasswordHash"));
        acc.setIsEmailVerified(rs.getBoolean("IsEmailVerified"));
        acc.setIsActive(rs.getBoolean("IsActive"));
        acc.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
        acc.setIsDeleted(rs.getBoolean("IsDeleted"));
        acc.setFailedAttempts(rs.getInt("FailedAttempts"));
        acc.setIsLocked(rs.getBoolean("IsLocked"));
        return acc;
    }
}
