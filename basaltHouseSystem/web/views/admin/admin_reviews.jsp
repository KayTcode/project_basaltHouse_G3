<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ page import="java.time.LocalDateTime, java.time.format.DateTimeFormatter" %>

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
                                <tr id="review-row-${r.reviewedId}" data-review-id="${r.reviewedId}">
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
                                            <%
                                                Object rawDate = ((java.util.Map<?,?>)pageContext.findAttribute("r")).get("createdAt");
                                                if (rawDate instanceof java.time.LocalDateTime) {
                                                    java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                                                    out.print(((java.time.LocalDateTime) rawDate).format(dtf));
                                                } else if (rawDate != null) {
                                                    out.print(rawDate.toString());
                                                } else {
                                                    out.print("—");
                                                }
                                            %>
                                        </div>
                                    </td>
                                    <td style="text-align: center; vertical-align: middle;">
                                        <form action="${pageContext.request.contextPath}/admin/reviews" method="POST" style="margin: 0;">
                                            <input type="hidden" name="action" value="toggle">
                                            <input type="hidden" name="id" value="${r.reviewedId}">
                                            <input type="hidden" name="visible" value="${!r.isVisible}">
                                            <input type="hidden" name="search" value="<c:out value='${param.search}'/>">
                                            <input type="hidden" name="rating" value="<c:out value='${param.rating}'/>">
                                            <input type="hidden" name="status" value="<c:out value='${param.status}'/>">
                                            <input type="hidden" name="page" value="<c:out value='${param.page}'/>">
                                            <label class="toggle-switch-label">
                                                <input type="checkbox" ${r.isVisible ? 'checked' : ''} onchange="this.form.submit()">
                                                <span class="toggle-slider"></span>
                                            </label>
                                        </form>
                                    </td>
                                    <td style="text-align: center; vertical-align: middle;">
                                        <form action="${pageContext.request.contextPath}/admin/reviews" method="POST" style="margin: 0;" onsubmit="return confirm('Bạn có chắc chắn muốn xóa đánh giá này?')">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="id" value="${r.reviewedId}">
                                            <input type="hidden" name="search" value="<c:out value='${param.search}'/>">
                                            <input type="hidden" name="rating" value="<c:out value='${param.rating}'/>">
                                            <input type="hidden" name="status" value="<c:out value='${param.status}'/>">
                                            <input type="hidden" name="page" value="<c:out value='${param.page}'/>">
                                            <div class="action-btn-group">
                                                <button type="submit" class="btn-icon-only btn-delete-rev" title="Xóa đánh giá">
                                                    <i class="fa-solid fa-trash-can"></i>
                                                </button>
                                            </div>
                                        </form>
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
            // ── Phân trang ────────────────────────────────────────────
            function changePage(page) {
                const params = new URLSearchParams(window.location.search);
                params.set('page', page);
                window.location.search = params.toString();
            }
        </script>
    </body>
</html>
