package services;

import dao.AdminReviewDAO;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminReviewService {

    private final AdminReviewDAO dao = new AdminReviewDAO();

    private static final int PAGE_SIZE = 10;
    public Map<String, Object> getPageData(String search, Integer ratingFilter,
            String status, int page) {
        Map<String, Object> data = new HashMap<>();

        // 1. KPI & phân bổ điểm sao (luôn tính trên toàn bộ bảng, không theo filter)
        Map<String, Object> stats = dao.getReviewStats();
        data.putAll(stats);

        // 2. Đếm tổng theo bộ lọc hiện tại (cho phân trang)
        int totalFiltered = dao.countReviews(search, ratingFilter, status);
        int totalPages = (int) Math.ceil((double) totalFiltered / PAGE_SIZE);
        if (totalPages < 1) {
            totalPages = 1;
        }
        if (page > totalPages) {
            page = totalPages;
        }

        // 3. Lấy danh sách theo trang
        List<Map<String, Object>> reviews
                = dao.getReviews(search, ratingFilter, status, page, PAGE_SIZE);

        data.put("reviews", reviews);
        data.put("totalFiltered", totalFiltered);
        data.put("currentPage", page);
        data.put("totalPages", totalPages);

        return data;
    }

    public boolean toggleVisibility(int reviewId, boolean visible) {
        return dao.toggleVisibility(reviewId, visible);
    }

    public boolean deleteReview(int reviewId) {
        return dao.softDelete(reviewId);
    }
}
