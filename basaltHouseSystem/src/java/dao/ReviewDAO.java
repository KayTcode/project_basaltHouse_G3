package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Review;


public class ReviewDAO extends DBContext {

    public boolean hasReviewed(int orderId, int customerId) {
        String sql = """
                     SELECT COUNT(*) AS cnt
                     FROM Reviews
                     WHERE OrderId = ? AND CustomerId = ? AND IsDeleted = 0
                     """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("cnt") > 0;
            }
        } catch (SQLException e) {
            System.err.println("ReviewDAO.hasReviewed Error: " + e.getMessage());
        }
        return false;
    }


    public boolean submitReview(int orderId, int customerId, int rating, String comment) {
        
        String sql = """
                     INSERT INTO Reviews (CustomerId, OrderId, ProductId, Rating, Comment,
                                         IsVisible, CreatedAt, IsDeleted)
                     VALUES (?, ?, 0, ?, ?, 1, GETDATE(), 0)
                     """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setInt(2, orderId);
            ps.setInt(3, rating);
            ps.setString(4, comment != null ? comment.trim() : "");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ReviewDAO.submitReview Error: " + e.getMessage());
        }
        return false;
    }

    public Review getReviewByOrder(int orderId, int customerId) {
        String sql = """
                     SELECT ReviewId, CustomerId, OrderId, ProductId,
                            Rating, Comment, IsVisible, CreatedAt, IsDeleted
                     FROM Reviews
                     WHERE OrderId = ? AND CustomerId = ? AND IsDeleted = 0
                     """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Review r = new Review();
                    r.setReviewedId(rs.getInt("ReviewId"));  // map DB col ReviewId → model field reviewedId
                    r.setCustomerId(rs.getInt("CustomerId"));
                    r.setOrderId(rs.getInt("OrderId"));
                    r.setProductId(rs.getInt("ProductId"));
                    r.setRating(rs.getInt("Rating"));
                    r.setComment(rs.getString("Comment"));
                    r.setIsVisible(rs.getBoolean("IsVisible"));
                    r.setIsDeleted(rs.getBoolean("IsDeleted"));
                    if (rs.getTimestamp("CreatedAt") != null) {
                        r.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
                    }
                    return r;
                }
            }
        } catch (SQLException e) {
            System.err.println("ReviewDAO.getReviewByOrder Error: " + e.getMessage());
        }
        return null;
    }
}
