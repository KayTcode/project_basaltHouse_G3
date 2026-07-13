<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- ══════════════════════════════════════════════════════════════════════════
     MOCK DATA FALLBACK (Khi chưa chạy qua Servlet)
     ══════════════════════════════════════════════════════════════════════════ --%>
<%
    if (request.getAttribute("reviews") == null) {
        java.util.List<java.util.Map<String, Object>> mockReviews = new java.util.ArrayList<>();
        
        java.util.Map<String, Object> r1 = new java.util.HashMap<>();
        r1.put("reviewedId", 101);
        r1.put("customerName", "Nguyễn Văn A");
        r1.put("orderId", 1245);
        r1.put("productName", "Cà phê sữa đá");
        r1.put("rating", 5);
        r1.put("comment", "Cà phê rất đậm vị, giao hàng nhanh chóng và đóng gói cẩn thận. Sẽ tiếp tục ủng hộ quán!");
        r1.put("createdAt", java.time.LocalDateTime.now().minusHours(2));
        r1.put("isVisible", true);
        mockReviews.add(r1);
        
        java.util.Map<String, Object> r2 = new java.util.HashMap<>();
        r2.put("reviewedId", 102);
        r2.put("customerName", "Trần Thị B");
        r2.put("orderId", 1240);
        r2.put("productName", "Trà đào cam sả");
        r2.put("rating", 4);
        r2.put("comment", "Trà đào thơm ngon, nhiều đào. Tuy nhiên hơi ngọt so với khẩu vị của mình.");
        r2.put("createdAt", java.time.LocalDateTime.now().minusHours(5));
        r2.put("isVisible", true);
        mockReviews.add(r2);

        java.util.Map<String, Object> r3 = new java.util.HashMap<>();
        r3.put("reviewedId", 103);
        r3.put("customerName", "Phạm Minh C");
        r3.put("orderId", 1238);
        r3.put("productName", "Bạc xỉu");
        r3.put("rating", 2);
        r3.put("comment", "Đồ uống quá nhạt, đá tan hết khi giao tới nơi. Đề nghị quán cải thiện chất lượng.");
        r3.put("createdAt", java.time.LocalDateTime.now().minusDays(1));
        r3.put("isVisible", false);
        mockReviews.add(r3);

        java.util.Map<String, Object> r4 = new java.util.HashMap<>();
        r4.put("reviewedId", 104);
        r4.put("customerName", "Lê Hoàng D");
        r4.put("orderId", 1235);
        r4.put("productName", "Bánh sừng bò");
        r4.put("rating", 5);
        r4.put("comment", "Bánh giòn, thơm bơ. Ăn kèm cà phê rất hợp vị.");
        r4.put("createdAt", java.time.LocalDateTime.now().minusDays(2));
        r4.put("isVisible", true);
        mockReviews.add(r4);
        
        request.setAttribute("reviews", mockReviews);
        request.setAttribute("avgRating", 4.0);
        request.setAttribute("totalReviews", 4);
        request.setAttribute("positiveRate", 75);
        request.setAttribute("hiddenReviews", 1);
        
        request.setAttribute("star5Count", 2);
        request.setAttribute("star4Count", 1);
        request.setAttribute("star3Count", 0);
        request.setAttribute("star2Count", 1);
        request.setAttribute("star1Count", 0);
        
        request.setAttribute("star5Pct", 50);
        request.setAttribute("star4Pct", 25);
        request.setAttribute("star3Pct", 0);
        request.setAttribute("star2Pct", 25);
        request.setAttribute("star1Pct", 0);
        request.setAttribute("currentPage", 1);
        request.setAttribute("totalPages", 1);
    }
%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Quản Lý Đánh Giá - Basalt House</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_common.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_reviews.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    </head>
    <body>
        <jsp:include page="header.jsp" />

        <div class="app-container">
            <jsp:include page="sidebar.jsp" />

            <main class="main-content">
                <div style="margin-bottom: 24px;">
                    <h2 style="font-size: 20px; font-weight: 800; color: #1e293b;">⭐ Quản Lý Đánh Giá Khách Hàng</h2>
                    <p style="font-size: 13px; color: #64748b;">Xem, phê duyệt hiển thị và quản lý phản hồi của khách hàng về đồ uống.</p>
                </div>

                <%-- ── KPI CARDS ── --%>
                <div class="rev-kpi-grid">
                    <div class="rev-kpi-card kpi-total">
                        <div class="kpi-header">
                            <span class="kpi-title">Tổng đánh giá</span>
                            <span class="kpi-icon">📝</span>
                        </div>
                        <div class="kpi-value-row">
                            <div class="kpi-value">${totalReviews}</div>
                        </div>
                        <div class="kpi-desc">Đánh giá từ khách mua hàng</div>
                    </div>

                    <div class="rev-kpi-card kpi-rating">
                        <div class="kpi-header">
                            <span class="kpi-title">Điểm trung bình</span>
                            <span class="kpi-icon">⭐</span>
                        </div>
                        <div class="kpi-value-row">
                            <div class="kpi-value">
                                <fmt:formatNumber value="${avgRating}" maxFractionDigits="1" minFractionDigits="1" />
                            </div>
                            <div class="kpi-stars">★/5</div>
                        </div>
                        <div class="kpi-desc">Chất lượng đồ uống tổng thể</div>
                    </div>

                    <div class="rev-kpi-card kpi-positive">
                        <div class="kpi-header">
                            <span class="kpi-title">Tỷ lệ hài lòng</span>
                            <span class="kpi-icon">👍</span>
                        </div>
                        <div class="kpi-value-row">
                            <div class="kpi-value">${positiveRate}%</div>
                        </div>
                        <div class="kpi-desc">Đánh giá từ 4 sao trở lên</div>
                    </div>

                    <div class="rev-kpi-card kpi-hidden">
                        <div class="kpi-header">
                            <span class="kpi-title">Đang ẩn</span>
                            <span class="kpi-icon">👁️</span>
                        </div>
                        <div class="kpi-value-row">
                            <div class="kpi-value">${hiddenReviews}</div>
                        </div>
                        <div class="kpi-desc">Đánh giá không hiển thị công khai</div>
                    </div>
                </div>

                <%-- ── DISTRIBUTION GRID ── --%>
                <div class="rev-distribution-panel">
                    <div class="dist-title">Phân Phối Điểm Đánh Giá</div>
                    <div class="dist-grid">
                        <div class="dist-summary-box">
                            <div class="big-rating">
                                <fmt:formatNumber value="${avgRating}" maxFractionDigits="1" minFractionDigits="1" />
                            </div>
                            <div class="big-stars">
                                <c:forEach var="i" begin="1" end="5">
                                    <c:choose>
                                        <c:when test="${i <= avgRating}">★</c:when>
                                        <c:otherwise>☆</c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </div>
                            <div class="total-reviews-lbl">Dựa trên ${totalReviews} phản hồi</div>
                        </div>

                        <div class="dist-bars-stack">
                            <div class="dist-row">
                                <span class="dist-label">5 ★</span>
                                <div class="dist-bar-bg">
                                    <div class="dist-bar-fill green" style="width: ${star5Pct}%;"></div>
                                </div>
                                <span class="dist-count">${star5Count} đơn</span>
                            </div>
                            <div class="dist-row">
                                <span class="dist-label">4 ★</span>
                                <div class="dist-bar-bg">
                                    <div class="dist-bar-fill" style="width: ${star4Pct}%;"></div>
                                </div>
                                <span class="dist-count">${star4Count} đơn</span>
                            </div>
                            <div class="dist-row">
                                <span class="dist-label">3 ★</span>
                                <div class="dist-bar-bg">
                                    <div class="dist-bar-fill" style="width: ${star3Pct}%;"></div>
                                </div>
                                <span class="dist-count">${star3Count} đơn</span>
                            </div>
                            <div class="dist-row">
                                <span class="dist-label">2 ★</span>
                                <div class="dist-bar-bg">
                                    <div class="dist-bar-fill" style="width: ${star2Pct}%;"></div>
                                </div>
                                <span class="dist-count">${star2Count} đơn</span>
                            </div>
                            <div class="dist-row">
                                <span class="dist-label">1 ★</span>
                                <div class="dist-bar-bg">
                                    <div class="dist-bar-fill" style="width: ${star1Pct}%;"></div>
                                </div>
                                <span class="dist-count">${star1Count} đơn</span>
                            </div>
                        </div>
                    </div>
                </div>

                <%-- ── FILTER & ACTION BAR ── --%>
                <div class="rev-filter-bar">
                    <form method="GET" action="${pageContext.request.contextPath}/admin/reviews" id="filterForm" class="filter-left-group">
                        <span class="filter-label">Bộ lọc:</span>
                        <select name="rating" class="filter-select" onchange="document.getElementById('filterForm').submit()">
                            <option value="">-- Tất cả số sao --</option>
                            <option value="5" ${param.rating == '5' ? 'selected' : ''}>5 Sao</option>
                            <option value="4" ${param.rating == '4' ? 'selected' : ''}>4 Sao</option>
                            <option value="3" ${param.rating == '3' ? 'selected' : ''}>3 Sao</option>
                            <option value="2" ${param.rating == '2' ? 'selected' : ''}>2 Sao</option>
                            <option value="1" ${param.rating == '1' ? 'selected' : ''}>1 Sao</option>
                        </select>

                        <select name="status" class="filter-select" onchange="document.getElementById('filterForm').submit()">
                            <option value="">-- Tất cả trạng thái --</option>
                            <option value="visible" ${param.status == 'visible' ? 'selected' : ''}>Đang hiện</option>
                            <option value="hidden" ${param.status == 'hidden' ? 'selected' : ''}>Đang ẩn</option>
                        </select>

                        <div class="search-box-wrapper">
                            <i class="fa-solid fa-magnifying-glass search-icon-inside"></i>
                            <input type="text" name="search" class="search-input" placeholder="Tìm tên khách, sản phẩm, bình luận..." value="${param.search}">
                        </div>
                        <button type="submit" style="display: none;"></button>
                    </form>
                </div>

                <%-- ── REVIEWS TABLE ── --%>
                <div class="rev-table-container">
                    <table class="rev-table">
                        <thead>
                            <tr>
                                <th style="width: 20%;">Khách hàng</th>
                                <th style="width: 20%;">Sản phẩm</th>
                                <th style="width: 12%;">Đánh giá</th>
                                <th style="width: 33%;">Nội dung phản hồi</th>
                                <th style="width: 8%; text-align: center;">Hiện</th>
                                <th style="width: 7%; text-align: center;">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="r" items="${reviews}">
                                <tr id="review-row-${r.reviewedId}">
                                    <td>
                                        <div class="customer-cell">
                                            <span class="customer-name">${r.customerName}</span>
                                            <span class="order-id-sub">Mã đơn: #${r.orderId}</span>
                                        </div>
                                    </td>
                                    <td>
                                        <div>
                                            <span class="prod-badge">${r.productName}</span>
                                        </div>
                                    </td>
                                    <td>
                                        <div class="stars-display">
                                            <c:forEach var="star" begin="1" end="5">
                                                <span class="${star <= r.rating ? 'filled-star' : ''}">★</span>
                                            </c:forEach>
                                        </div>
                                    </td>
                                    <td>
                                        <div class="comment-text">${r.comment}</div>
                                        <div class="date-text">
                                            <c:choose>
                                                <c:when test="${fn:contains(r.createdAt, '/')}">
                                                    ${r.createdAt}
                                                </c:when>
                                                <c:otherwise>
                                                    <fmt:parseDate value="${r.createdAt}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both"/>
                                                    <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy HH:mm"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </td>
                                    <td style="text-align: center; vertical-align: middle;">
                                        <label class="toggle-switch-label">
                                            <input type="checkbox" ${r.isVisible ? 'checked' : ''} onchange="toggleReviewVisibility(${r.reviewedId}, this.checked)">
                                            <span class="toggle-slider"></span>
                                        </label>
                                    </td>
                                    <td style="text-align: center; vertical-align: middle;">
                                        <div class="action-btn-group">
                                            <button type="button" class="btn-icon-only btn-delete-rev" onclick="deleteReview(${r.reviewedId})" title="Xóa đánh giá">
                                                <i class="fa-solid fa-trash-can"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty reviews}">
                                <tr>
                                    <td colspan="6" style="text-align: center; padding: 40px; color: #94a3b8;">
                                        <i class="fa-regular fa-comment-dots" style="font-size: 32px; margin-bottom: 12px; display: block;"></i>
                                        Không tìm thấy phản hồi hoặc đánh giá nào phù hợp với bộ lọc.
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>

                <%-- ── PAGINATION ── --%>
                <div class="rev-pagination">
                    <span class="pag-info">Trang ${currentPage} / ${totalPages}</span>
                    <div class="pag-buttons">
                        <button class="pag-btn" ${currentPage <= 1 ? 'disabled' : ''} onclick="changePage(${currentPage - 1})">
                            <i class="fa-solid fa-angle-left"></i> Trước
                        </button>
                        <c:forEach var="p" begin="1" end="${totalPages}">
                            <button class="pag-btn ${p == currentPage ? 'active' : ''}" onclick="changePage(${p})">${p}</button>
                        </c:forEach>
                        <button class="pag-btn" ${currentPage >= totalPages ? 'disabled' : ''} onclick="changePage(${currentPage + 1})">
                            Sau <i class="fa-solid fa-angle-right"></i>
                        </button>
                    </div>
                </div>
            </main>
        </div>

        <%-- ── ACTIONS JAVASCRIPT ── --%>
        <script>
            function toggleReviewVisibility(reviewId, isChecked) {
                // Gửi request AJAX/Fetch đến servlet để cập nhật trạng thái hiển thị
                const url = '${pageContext.request.contextPath}/admin/reviews?action=toggle';
                
                fetch(url, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: `id=${reviewId}&visible=${isChecked}`
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        console.log('Cập nhật trạng thái hiển thị thành công.');
                    } else {
                        alert(data.message || 'Cập nhật thất bại. Vui lòng thử lại.');
                        // Revert trạng thái checkbox nếu lỗi
                        window.location.reload();
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Lỗi kết nối máy chủ!');
                    window.location.reload();
                });
            }

            function deleteReview(reviewId) {
                if (!confirm('Bạn có chắc chắn muốn xóa đánh giá này không? Đánh giá bị xóa sẽ không xuất hiện lại.')) {
                    return;
                }
                
                const url = '${pageContext.request.contextPath}/admin/reviews?action=delete';
                
                fetch(url, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: `id=${reviewId}`
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        // Xóa dòng hiển thị trên UI
                        const row = document.getElementById(`review-row-${reviewId}`);
                        if (row) {
                            row.style.opacity = '0';
                            row.style.transform = 'scale(0.9)';
                            row.style.transition = 'all 0.3s ease';
                            setTimeout(() => {
                                row.remove();
                                // Tự động reload để cập nhật các chỉ số KPI
                                window.location.reload();
                            }, 300);
                        }
                    } else {
                        alert(data.message || 'Xóa đánh giá thất bại.');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Lỗi kết nối máy chủ!');
                });
            }

            function changePage(page) {
                const urlParams = new URLSearchParams(window.location.search);
                urlParams.set('page', page);
                window.location.search = urlParams.toString();
            }
        </script>
    </body>
</html>
