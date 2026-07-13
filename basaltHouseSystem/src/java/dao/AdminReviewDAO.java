package dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdminReviewDAO extends DBContext {


    public List<Map<String, Object>> getReviews(String search, Integer rating, String status, int page, int pageSize) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            int offset = (page - 1) * pageSize;

            StringBuilder sql = new StringBuilder("""
                SELECT r.ReviewId,
                       c.FullName       AS customerName,
                       r.OrderId,
                       p.ProductName,
                       r.Rating,
                       r.Comment,
                       r.IsVisible,
                       r.CreatedAt
                FROM Reviews r
                LEFT JOIN Customers c  ON r.CustomerId = c.CustomerId
                LEFT JOIN Products  p  ON r.ProductId  = p.ProductId
                WHERE r.IsDeleted = 0
                """);

            if (search != null && !search.isBlank()) {
                sql.append(" AND (c.FullName LIKE ? OR p.ProductName LIKE ? OR r.Comment LIKE ?)");
            }
            if (rating != null) {
                sql.append(" AND r.Rating = ?");
            }
            if ("visible".equals(status)) {
                sql.append(" AND r.IsVisible = 1");
            } else if ("hidden".equals(status)) {
                sql.append(" AND r.IsVisible = 0");
            }

            sql.append(" ORDER BY r.CreatedAt DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

            PreparedStatement ps = connection.prepareStatement(sql.toString());
            int idx = 1;
            if (search != null && !search.isBlank()) {
                String like = "%" + search.trim() + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }
            if (rating != null) {
                ps.setInt(idx++, rating);
            }
            ps.setInt(idx++, offset);
            ps.setInt(idx, pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("reviewedId", rs.getInt("ReviewId"));
                row.put("customerName", rs.getString("customerName") != null ? rs.getString("customerName") : "Ẩn danh");
                row.put("orderId", rs.getInt("OrderId"));
                row.put("productName", rs.getString("ProductName") != null ? rs.getString("ProductName") : "—");
                row.put("rating", rs.getInt("Rating"));
                row.put("comment", rs.getString("Comment") != null ? rs.getString("Comment") : "");
                row.put("isVisible", rs.getBoolean("IsVisible"));
                row.put("createdAt", rs.getTimestamp("CreatedAt") != null
                        ? rs.getTimestamp("CreatedAt").toLocalDateTime()
                        : null);
                list.add(row);
            }
        } catch (Exception e) {
            System.err.println("[AdminReviewDAO] getReviews: " + e.getMessage());
        }
        return list;
    }

    public int countReviews(String search, Integer rating, String status) {
        try {
            StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*) AS cnt
                FROM Reviews r
                LEFT JOIN Customers c ON r.CustomerId = c.CustomerId
                LEFT JOIN Products  p ON r.ProductId  = p.ProductId
                WHERE r.IsDeleted = 0
                """);

            if (search != null && !search.isBlank()) {
                sql.append(" AND (c.FullName LIKE ? OR p.ProductName LIKE ? OR r.Comment LIKE ?)");
            }
            if (rating != null) {
                sql.append(" AND r.Rating = ?");
            }
            if ("visible".equals(status)) {
                sql.append(" AND r.IsVisible = 1");
            } else if ("hidden".equals(status)) {
                sql.append(" AND r.IsVisible = 0");
            }

            PreparedStatement ps = connection.prepareStatement(sql.toString());
            int idx = 1;
            if (search != null && !search.isBlank()) {
                String like = "%" + search.trim() + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }
            if (rating != null) {
                ps.setInt(idx++, rating);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("cnt");
            }
        } catch (Exception e) {
            System.err.println("[AdminReviewDAO] countReviews: " + e.getMessage());
        }
        return 0;
    }

    public Map<String, Object> getReviewStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalReviews", 0);
        stats.put("avgRating", 0.0);
        stats.put("positiveRate", 0);
        stats.put("hiddenReviews", 0);
        for (int i = 1; i <= 5; i++) {
            stats.put("star" + i + "Count", 0);
            stats.put("star" + i + "Pct", 0);
        }

        try {
            // Tổng + trung bình + số ẩn
            String sql = """
                SELECT COUNT(*)                                        AS total,
                       ISNULL(AVG(CAST(Rating AS FLOAT)), 0)           AS avg,
                       SUM(CASE WHEN IsVisible = 0 THEN 1 ELSE 0 END)  AS hidden,
                       SUM(CASE WHEN Rating >= 4    THEN 1 ELSE 0 END)  AS positive,
                       SUM(CASE WHEN Rating = 5     THEN 1 ELSE 0 END)  AS s5,
                       SUM(CASE WHEN Rating = 4     THEN 1 ELSE 0 END)  AS s4,
                       SUM(CASE WHEN Rating = 3     THEN 1 ELSE 0 END)  AS s3,
                       SUM(CASE WHEN Rating = 2     THEN 1 ELSE 0 END)  AS s2,
                       SUM(CASE WHEN Rating = 1     THEN 1 ELSE 0 END)  AS s1
                FROM Reviews
                WHERE IsDeleted = 0
                """;

            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int total = rs.getInt("total");
                double avg = rs.getDouble("avg");
                int hidden = rs.getInt("hidden");
                int positive = rs.getInt("positive");
                int[] counts = {
                    rs.getInt("s1"), rs.getInt("s2"), rs.getInt("s3"),
                    rs.getInt("s4"), rs.getInt("s5")
                };

                stats.put("totalReviews", total);
                stats.put("avgRating", BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP));
                stats.put("hiddenReviews", hidden);
                stats.put("positiveRate", total > 0
                        ? (int) Math.round((double) positive / total * 100)
                        : 0);

                for (int i = 0; i < 5; i++) {
                    int star = i + 1;
                    int cnt = counts[i];
                    int pct = total > 0 ? (int) Math.round((double) cnt / total * 100) : 0;
                    stats.put("star" + star + "Count", cnt);
                    stats.put("star" + star + "Pct", pct);
                }
            }
        } catch (Exception e) {
            System.err.println("[AdminReviewDAO] getReviewStats: " + e.getMessage());
        }
        return stats;
    }

    public boolean toggleVisibility(int reviewId, boolean visible) {
        try {
            String sql = "UPDATE Reviews SET IsVisible = ? WHERE ReviewId = ? AND IsDeleted = 0";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setBoolean(1, visible);
            ps.setInt(2, reviewId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[AdminReviewDAO] toggleVisibility: " + e.getMessage());
        }
        return false;
    }

    public boolean softDelete(int reviewId) {
        try {
            String sql = "UPDATE Reviews SET IsDeleted = 1 WHERE ReviewId = ? AND IsDeleted = 0";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, reviewId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[AdminReviewDAO] softDelete: " + e.getMessage());
        }
        return false;
    }
}
