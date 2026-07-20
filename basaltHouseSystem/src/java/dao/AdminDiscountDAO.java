package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.DiscountCode;
import java.math.BigDecimal;
import java.sql.Connection;

public class AdminDiscountDAO extends DBContext {

    public List<DiscountCode> getAllDiscounts(String search, String filterType, String filterStatus) {
        List<DiscountCode> list = new ArrayList<>();
        
        String sql = """
            SELECT DiscountId, Code, DiscountPercent, DiscountAmount,
                   StartDate, EndDate, IsActive, IsDeleted, IsPublic, Description,
                   CreatedBy, CreatedAt,
                   DATEDIFF(DAY, GETDATE(), EndDate) AS DayTime
            FROM DiscountCodes
            WHERE IsDeleted = 0
            """;

        // 1. Lọc theo ô tìm kiếm sử dụng LIKE
        if (search != null && !search.trim().isEmpty()) {
            sql += """
                   AND (Code LIKE ? OR Description LIKE ?)
                   """;
        }

        // 2. Lọc theo loại giảm giá
        if ("PERCENT".equals(filterType)) {
            sql += """
                   AND DiscountPercent IS NOT NULL AND DiscountPercent > 0
                   """;
        } else if ("AMOUNT".equals(filterType)) {
            sql += """
                   AND DiscountAmount IS NOT NULL AND DiscountAmount > 0
                   """;
        }

        // 3. Lọc theo trạng thái hoạt động
        if ("ACTIVE".equals(filterStatus)) {
            sql += """
                   AND IsActive = 1 AND (EndDate IS NULL OR EndDate >= GETDATE())
                   """;
        } else if ("INACTIVE".equals(filterStatus)) {
            sql += """
                   AND IsActive = 0
                   """;
        } else if ("EXPIRED".equals(filterStatus)) {
            sql += """
                   AND EndDate IS NOT NULL AND EndDate < GETDATE()
                   """;
        }

        // Sắp xếp giảm dần theo ngày tạo
        sql += """
               ORDER BY CreatedAt DESC
               """;

        try {
            PreparedStatement st = connection.prepareStatement(sql);
            int index = 1;

            // Truyền tham số cho mệnh đề LIKE ? (nếu có)
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim() + "%";
                st.setString(index++, searchPattern);
                st.setString(index++, searchPattern);
            }

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                DiscountCode d = new DiscountCode();
                d.setDiscountId(rs.getInt("DiscountId"));
                d.setCode(rs.getString("Code"));
                d.setDiscountPercent(rs.getBigDecimal("DiscountPercent"));
                d.setDiscountAmount(rs.getBigDecimal("DiscountAmount"));
                d.setStartDate(rs.getObject("StartDate", LocalDateTime.class));
                d.setEndDate(rs.getObject("EndDate", LocalDateTime.class));
                d.setIsActive(rs.getBoolean("IsActive"));
                d.setIsDeleted(rs.getBoolean("IsDeleted"));
                d.setIsPublic(rs.getBoolean("IsPublic"));
                d.setDescription(rs.getString("Description"));
                d.setCreatedBy(rs.getInt("CreatedBy"));
                d.setCreatedAt(rs.getObject("CreatedAt", LocalDateTime.class));
                d.setTotalDay(rs.getInt("DayTime"));
                list.add(d);
            }
        } catch (Exception e) {
            System.err.println("[AdminDiscountDAO.getAllDiscounts] Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }


    public Map<String, Integer> getDiscountStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", 0);
        stats.put("active", 0);
        stats.put("expiringSoon", 0);
        stats.put("expiredOrInactive", 0);
        
        String sql = """
            SELECT
                COUNT(*) AS total,
                SUM(CASE WHEN IsActive = 1 AND (EndDate IS NULL OR EndDate >= GETDATE()) THEN 1 ELSE 0 END) AS active,
                SUM(CASE WHEN IsActive = 1 AND EndDate IS NOT NULL AND DATEDIFF(DAY, GETDATE(), EndDate) BETWEEN 0 AND 7 THEN 1 ELSE 0 END) AS expiringSoon,
                SUM(CASE WHEN IsActive = 0 OR (EndDate IS NOT NULL AND EndDate < GETDATE()) THEN 1 ELSE 0 END) AS expiredOrInactive
            FROM DiscountCodes
            WHERE IsDeleted = 0
            """;
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                stats.put("total", rs.getInt("total"));
                stats.put("active", rs.getInt("active"));
                stats.put("expiringSoon", rs.getInt("expiringSoon"));
                stats.put("expiredOrInactive", rs.getInt("expiredOrInactive"));
            }
        } catch (Exception e) {
            System.err.println("[AdminDiscountDAO.getDiscountStats] Lỗi: " + e.getMessage());
        }
        return stats;
    }

    public boolean addDiscount(String code, String discountType,
                               BigDecimal discountPercent, BigDecimal discountAmount,
                               LocalDateTime startDate, LocalDateTime endDate,
                               String description, boolean isActive, boolean isPublic, int createdBy) {
        String sql = """
            INSERT INTO DiscountCodes
                (Code, DiscountPercent, DiscountAmount, StartDate, EndDate,
                 Description, IsActive, IsDeleted, IsPublic, CreatedBy, CreatedAt)
            VALUES (?, ?, ?, ?, ?, ?, ?, 0, ?, ?, GETDATE())
            """;
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, code.trim().toUpperCase());
            if ("PERCENT".equals(discountType)) {
                st.setBigDecimal(2, discountPercent);
                st.setNull(3, java.sql.Types.DECIMAL);
            } else {
                st.setNull(2, java.sql.Types.DECIMAL);
                st.setBigDecimal(3, discountAmount);
            }
            st.setObject(4, startDate);
            st.setObject(5, endDate);
            st.setString(6, description);
            st.setBoolean(7, isActive);
            st.setBoolean(8, isPublic);
            // Nếu không có id admin hợp lệ thì để NULL (tránh FK violation)
            if (createdBy > 0) {
                st.setInt(9, createdBy);
            } else {
                st.setNull(9, java.sql.Types.INTEGER);
            }
            return st.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[AdminDiscountDAO.addDiscount] Lỗi: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDiscount(int discountId, String code, String discountType,
                                  BigDecimal discountPercent, BigDecimal discountAmount,
                                  LocalDateTime startDate, LocalDateTime endDate,
                                  String description, boolean isActive, boolean isPublic) {
        String sql = """
            UPDATE DiscountCodes
            SET Code = ?, DiscountPercent = ?, DiscountAmount = ?,
                StartDate = ?, EndDate = ?, Description = ?, IsActive = ?, IsPublic = ?
            WHERE DiscountId = ? AND IsDeleted = 0
            """;
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, code.trim().toUpperCase());
            if ("PERCENT".equals(discountType)) {
                st.setBigDecimal(2, discountPercent);
                st.setNull(3, java.sql.Types.DECIMAL);
            } else {
                st.setNull(2, java.sql.Types.DECIMAL);
                st.setBigDecimal(3, discountAmount);
            }
            st.setObject(4, startDate);
            st.setObject(5, endDate);
            st.setString(6, description);
            st.setBoolean(7, isActive);
            st.setBoolean(8, isPublic);
            st.setInt(9, discountId);
            return st.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[AdminDiscountDAO.updateDiscount] Lỗi: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

   
    public boolean deleteDiscount(int discountId) {
        String sql = "UPDATE DiscountCodes SET IsDeleted = 1 WHERE DiscountId = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, discountId);
            return st.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[AdminDiscountDAO.deleteDiscount] Lỗi: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public List<DiscountCode> getActiveDiscountsForGift() {
        List<DiscountCode> list = new ArrayList<>();
        String sql = """
            SELECT DiscountId, Code, DiscountPercent, DiscountAmount, StartDate, EndDate, Description
            FROM DiscountCodes
            WHERE IsDeleted = 0
              AND IsActive = 1
              AND (StartDate IS NULL OR StartDate <= GETDATE())
              AND (EndDate IS NULL OR EndDate >= GETDATE())
            ORDER BY CreatedAt DESC
            """;
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                DiscountCode d = new DiscountCode();
                d.setDiscountId(rs.getInt("DiscountId"));
                d.setCode(rs.getString("Code"));
                d.setDiscountPercent(rs.getBigDecimal("DiscountPercent"));
                d.setDiscountAmount(rs.getBigDecimal("DiscountAmount"));
                d.setStartDate(rs.getObject("StartDate", LocalDateTime.class));
                d.setEndDate(rs.getObject("EndDate", LocalDateTime.class));
                d.setDescription(rs.getString("Description"));
                list.add(d);
            }
        } catch (Exception e) {
            System.err.println("[AdminDiscountDAO.getActiveDiscountsForGift] Lỗi: " + e.getMessage());
        }
        return list;
    }

    public String giftDiscountToCustomer(int accountId, int discountId) {
        // 1. Kiểm tra trùng
        String checkSql = """
            SELECT COUNT(*) FROM CustomerDiscountCodes
            WHERE AccountId = ? AND DiscountId = ?
            """;
        try {
            PreparedStatement checkSt = connection.prepareStatement(checkSql);
            checkSt.setInt(1, accountId);
            checkSt.setInt(2, discountId);
            ResultSet rs = checkSt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return "already_gifted";
            }
        } catch (Exception e) {
            System.err.println("[AdminDiscountDAO.giftDiscountToCustomer] Kiểm tra trùng lỗi: " + e.getMessage());
            return "error";
        }

        String insertSql = """
            INSERT INTO CustomerDiscountCodes (AccountId, DiscountId, IsUsed, [Status])
            VALUES (?, ?, 0, 1)
            """;
        try {
            PreparedStatement st = connection.prepareStatement(insertSql);
            st.setInt(1, accountId);
            st.setInt(2, discountId);
            int rows = st.executeUpdate();
            return rows > 0 ? "success" : "error";
        } catch (Exception e) {
            System.err.println("[AdminDiscountDAO.giftDiscountToCustomer] Tặng mã lỗi: " + e.getMessage());
            e.printStackTrace();
            return "error";
        }
    }
}
