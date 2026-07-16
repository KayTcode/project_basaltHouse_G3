package controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.AdminReviewService;

@WebServlet(name = "AdminReviewsServlet", urlPatterns = {"/admin/reviews"})
public class AdminReviewsServlet extends HttpServlet {

    private final AdminReviewService reviewService = new AdminReviewService();

    // ── GET: load trang danh sách ─────────────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Đọc tham số bộ lọc từ URL
        String search    = request.getParameter("search");
        String ratingStr = request.getParameter("rating");
        String status    = request.getParameter("status");
        String pageStr   = request.getParameter("page");

        Integer ratingFilter = null;
        if (ratingStr != null && !ratingStr.isBlank()) {
            try { ratingFilter = Integer.parseInt(ratingStr.trim()); }
            catch (NumberFormatException ignored) {}
        }
        int page = 1;
        if (pageStr != null && !pageStr.isBlank()) {
            try { page = Math.max(1, Integer.parseInt(pageStr.trim())); }
            catch (NumberFormatException ignored) {}
        }

        try {
            Map<String, Object> pageData =
                    reviewService.getPageData(search, ratingFilter, status, page);

            // Đẩy toàn bộ dữ liệu vào request scope
            for (Map.Entry<String, Object> entry : pageData.entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }

        } catch (Exception e) {
            System.err.println("[AdminReviewsServlet] Lỗi load dữ liệu: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("loadError", "Không thể tải dữ liệu đánh giá. Vui lòng thử lại.");
            // Giá trị mặc định để JSP không bị lỗi NullPointer
            request.setAttribute("totalReviews",  0);
            request.setAttribute("avgRating",     "0.0");
            request.setAttribute("positiveRate",  0);
            request.setAttribute("hiddenReviews", 0);
            for (int i = 1; i <= 5; i++) {
                request.setAttribute("star" + i + "Count", 0);
                request.setAttribute("star" + i + "Pct",   0);
            }
            request.setAttribute("reviews",     java.util.Collections.emptyList());
            request.setAttribute("currentPage", 1);
            request.setAttribute("totalPages",  1);
        }

        request.getRequestDispatcher("/views/admin/admin_reviews.jsp").forward(request, response);
    }

    // ── POST: xử lý form submit toggle / delete ──────────────────────────────
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        String idStr  = request.getParameter("id");

        if (action != null && idStr != null) {
            try {
                int reviewId = Integer.parseInt(idStr.trim());
                if ("toggle".equals(action)) {
                    boolean visible = Boolean.parseBoolean(request.getParameter("visible"));
                    reviewService.toggleVisibility(reviewId, visible);
                } else if ("delete".equals(action)) {
                    reviewService.deleteReview(reviewId);
                }
            } catch (NumberFormatException e) {
                System.err.println("[AdminReviewsServlet] ID không hợp lệ: " + idStr);
            }
        }

        // Xây dựng redirect URL để giữ nguyên bộ lọc và phân trang hiện tại
        StringBuilder redirectUrl = new StringBuilder(request.getContextPath() + "/admin/reviews?");
        String search = request.getParameter("search");
        String rating = request.getParameter("rating");
        String status = request.getParameter("status");
        String page = request.getParameter("page");

        if (search != null && !search.isBlank()) {
            redirectUrl.append("search=").append(URLEncoder.encode(search.trim(), StandardCharsets.UTF_8)).append("&");
        }
        if (rating != null && !rating.isBlank()) {
            redirectUrl.append("rating=").append(rating.trim()).append("&");
        }
        if (status != null && !status.isBlank()) {
            redirectUrl.append("status=").append(status.trim()).append("&");
        }
        if (page != null && !page.isBlank()) {
            redirectUrl.append("page=").append(page.trim()).append("&");
        }

        // Loại bỏ ký tự thừa ở cuối URL
        if (redirectUrl.charAt(redirectUrl.length() - 1) == '&' || redirectUrl.charAt(redirectUrl.length() - 1) == '?') {
            redirectUrl.setLength(redirectUrl.length() - 1);
        }

        response.sendRedirect(redirectUrl.toString());
    }

    @Override
    public String getServletInfo() {
        return "Admin Reviews Controller";
    }
}
