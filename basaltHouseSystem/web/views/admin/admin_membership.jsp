<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="rankList" value="${requestScope.rankList}" />
<c:if test="${empty rankList and not empty membershipRanks}">
    <c:set var="rankList" value="${membershipRanks}" />
</c:if>
<c:if test="${empty rankList and not empty ranks}">
    <c:set var="rankList" value="${ranks}" />
</c:if>
<c:if test="${empty rankList and not empty data.ranks}">
    <c:set var="rankList" value="${data.ranks}" />
</c:if>
<c:if test="${empty rankList and not empty data.membershipRanks}">
    <c:set var="rankList" value="${data.membershipRanks}" />
</c:if>

<c:set var="memberList" value="${requestScope.memberList}" />
<c:if test="${empty memberList and not empty membershipMembers}">
    <c:set var="memberList" value="${membershipMembers}" />
</c:if>
<c:if test="${empty memberList and not empty memberships}">
    <c:set var="memberList" value="${memberships}" />
</c:if>
<c:if test="${empty memberList and not empty data.memberships}">
    <c:set var="memberList" value="${data.memberships}" />
</c:if>
<c:if test="${empty memberList and not empty data.members}">
    <c:set var="memberList" value="${data.members}" />
</c:if>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Membership - BasaltHouse</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_common.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_membership.css">
    </head>
    <body class="admin-membership-body">
        <%@ include file="/views/admin/header.jsp" %>

        <div class="app-container">
            <%@ include file="/views/admin/sidebar.jsp" %>

            <main class="main-content membership-main">
                <section class="membership-hero">
                    <div class="membership-hero__copy">
                        <span class="section-eyebrow">Membership control</span>
                        <h1>Quản lý Membership</h1>
                        <p>Theo dõi hội viên, cấu hình hạng thành viên, ưu đãi và mốc chi tiêu tích lũy của khách hàng BasaltHouse.</p>
                    </div>
                </section>

                <c:if test="${not empty sessionScope.toastMessage}">
                    <div class="membership-alert alert-success">
                        <i class="fa-solid fa-circle-check"></i>
                        <span>${sessionScope.toastMessage}</span>
                    </div>
                    <c:remove var="toastMessage" scope="session" />
                </c:if>
                <c:if test="${not empty sessionScope.toastError}">
                    <div class="membership-alert alert-error">
                        <i class="fa-solid fa-circle-exclamation"></i>
                        <span>${sessionScope.toastError}</span>
                    </div>
                    <c:remove var="toastError" scope="session" />
                </c:if>

                <section class="membership-kpi-grid" aria-label="Tổng quan membership">
                    <article class="membership-kpi-card">
                        <div class="kpi-icon kpi-icon-member">
                            <i class="fa-solid fa-users"></i>
                        </div>
                        <div>
                            <span class="kpi-label">Tổng hội viên</span>
                            <strong class="kpi-value">${empty totalMembers ? 0 : totalMembers}</strong>
                            <span class="kpi-note">Đang có hồ sơ tích điểm</span>
                        </div>
                    </article>

                    <article class="membership-kpi-card">
                        <div class="kpi-icon kpi-icon-spend">
                            <i class="fa-solid fa-chart-line"></i>
                        </div>
                        <div>
                            <span class="kpi-label">Tổng chi tiêu</span>
                            <strong class="kpi-value">
                                <fmt:formatNumber value="${empty totalSpent ? 0 : totalSpent}" type="currency" currencySymbol="đ" maxFractionDigits="0" />
                            </strong>
                            <span class="kpi-note">Giá trị tích lũy hợp lệ</span>
                        </div>
                    </article>

                    <article class="membership-kpi-card">
                        <div class="kpi-icon kpi-icon-rank">
                            <i class="fa-solid fa-gem"></i>
                        </div>
                        <div>
                            <span class="kpi-label">Hạng cao nhất</span>
                            <strong class="kpi-value">${empty topRank ? 'Chưa có' : topRank}</strong>
                            <span class="kpi-note">Ưu đãi tối đa ${empty topDiscount ? 0 : topDiscount}%</span>
                        </div>
                    </article>
                </section>

                <section class="rank-board">
                    <div class="section-heading-row">
                        <div>
                            <span class="section-eyebrow">Tier setup</span>
                            <h2>Hạng thành viên</h2>
                        </div>
                        <form action="${pageContext.request.contextPath}/admin/memberships" method="POST" onsubmit="openRankModal(this); return false;">
                            <input type="hidden" name="action" value="createRank">
                            <button type="submit" name="submitAction" value="openCreateRank" class="btn-compact">
                                <i class="fa-solid fa-layer-group"></i>
                                Tạo hạng mới
                            </button>
                        </form>
                    </div>

                    <div class="rank-card-grid">
                        <c:choose>
                            <c:when test="${not empty pagedRankList}">
                                <c:forEach var="rank" items="${pagedRankList}" varStatus="status">
                                    <article class="rank-card rank-card-${status.index % 4}">
                                        <div class="rank-card__top">
                                            <span class="rank-card__kicker">Hạng ${rankPageStart + status.index}</span>
                                            <c:choose>
                                                <c:when test="${rank.isDeleted}">
                                                    <span class="rank-status is-paused">Tạm ẩn</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="rank-status is-live">Đang dùng</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <h3>${rank.rankName}</h3>
                                        <div class="rank-discount">${rank.discountValue}%</div>
                                        <p>Giảm trực tiếp cho đơn hợp lệ khi khách đạt mốc chi tiêu.</p>
                                        <div class="rank-meta">
                                            <span>Mốc tối thiểu</span>
                                            <strong>
                                                <fmt:formatNumber value="${rank.minTotalSpent}" type="currency" currencySymbol="đ" maxFractionDigits="0" />
                                            </strong>
                                        </div>
                                        <div class="rank-actions">
                                            <form action="${pageContext.request.contextPath}/admin/memberships" method="POST" onsubmit="openRankModal(this); return false;">
                                                <input type="hidden" name="action" value="updateRank">
                                                <input type="hidden" name="rankId" value="${rank.rankId}">
                                                <input type="hidden" name="rankName" value="${fn:escapeXml(rank.rankName)}">
                                                <input type="hidden" name="minTotalSpent" value="${rank.minTotalSpent}">
                                                <input type="hidden" name="discountValue" value="${rank.discountValue}">
                                                <input type="hidden" name="isDeleted" value="${rank.isDeleted}">
                                                <button type="submit"
                                                        name="submitAction"
                                                        value="editRank"
                                                        class="btn-text-action">
                                                    <i class="fa-solid fa-pen"></i>
                                                    Sửa
                                                </button>
                                            </form>
                                        </div>
                                    </article>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <p class="rank-empty">Chưa có hạng thành viên.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <c:if test="${totalRankPages > 1}">
                        <div class="membership-pagination rank-pagination">
                            <span class="pagination-summary">
                                Hiển thị ${rankPageStart}-${rankPageEnd} trên ${totalRanks} hạng
                            </span>
                            <div class="pagination-controls">
                                <form action="${pageContext.request.contextPath}/admin/memberships" method="GET">
                                    <button type="submit" name="rankPage" value="${currentRankPage - 1}"
                                            class="pagination-button" ${currentRankPage == 1 ? 'disabled' : ''}
                                            title="Trang trước">
                                        <i class="fa-solid fa-chevron-left"></i>
                                    </button>
                                </form>
                                <c:forEach begin="1" end="${totalRankPages}" var="rankPageNumber">
                                    <form action="${pageContext.request.contextPath}/admin/memberships" method="GET">
                                        <button type="submit" name="rankPage" value="${rankPageNumber}"
                                                class="pagination-button ${rankPageNumber == currentRankPage ? 'is-active' : ''}"
                                                ${rankPageNumber == currentRankPage ? 'aria-current="page"' : ''}>
                                            ${rankPageNumber}
                                        </button>
                                    </form>
                                </c:forEach>
                                <form action="${pageContext.request.contextPath}/admin/memberships" method="GET">
                                    <button type="submit" name="rankPage" value="${currentRankPage + 1}"
                                            class="pagination-button" ${currentRankPage == totalRankPages ? 'disabled' : ''}
                                            title="Trang sau">
                                        <i class="fa-solid fa-chevron-right"></i>
                                    </button>
                                </form>
                            </div>
                        </div>
                    </c:if>
                </section>

                <section class="membership-panel">
                    <div class="membership-panel__header">
                        <div>
                            <span class="section-eyebrow">Member list</span>
                            <h2>Danh sách hội viên</h2>
                        </div>
                        <div class="panel-count">
                            ${empty totalMembers ? 0 : totalMembers} hồ sơ
                        </div>
                    </div>

                    <form action="${pageContext.request.contextPath}/admin/memberships" method="GET" class="membership-filter-bar">
                        <label class="filter-search">
                            <i class="fa-solid fa-magnifying-glass"></i>
                            <input id="membershipSearch" type="text" name="search" placeholder="Tìm tên, số điện thoại hoặc mã khách..." value="${fn:escapeXml(searchValue)}">
                        </label>

                        <label class="filter-select">
                            <span>Hạng</span>
                            <select name="rankId">
                                <option value="">Tất cả</option>
                                <c:forEach var="rank" items="${rankList}">
                                    <option value="${rank.rankId}" ${selectedRankId == rank.rankId ? 'selected' : ''}>${rank.rankName}</option>
                                </c:forEach>
                                <c:if test="${empty rankList}">
                                    <option value="" disabled>Chưa có hạng</option>
                                </c:if>
                            </select>
                        </label>

                        <button type="submit" name="submitAction" value="filterMemberships" class="btn-filter">
                            <i class="fa-solid fa-filter"></i>
                            Lọc
                        </button>
                    </form>

                    <div class="membership-table-wrap">
                        <table class="membership-table">
                            <thead>
                                <tr>
                                    <th>Khách hàng</th>
                                    <th>Số điện thoại</th>
                                    <th>Hạng</th>
                                    <th>Chi tiêu tích lũy</th>
                                    <th>Ưu đãi</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty memberList}">
                                        <c:forEach var="member" items="${memberList}">
                                            <tr>
                                                <td>
                                                    <div class="member-cell">
                                                        <div class="member-avatar">${fn:substring(empty member.customerName ? 'KH' : member.customerName, 0, 1)}</div>
                                                        <div>
                                                            <strong>${empty member.customerName ? 'Khách hàng' : member.customerName}</strong>
                                                            <span>#KH${member.customerId}</span>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${not empty member.phone}">${member.phone}</c:when>
                                                        <c:otherwise>Chưa có SĐT</c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <span class="rank-pill">${empty member.rankName ? 'Chưa xếp hạng' : member.rankName}</span>
                                                </td>
                                                <td class="money-cell">
                                                    <fmt:formatNumber value="${empty member.totalSpent ? 0 : member.totalSpent}" type="currency" currencySymbol="đ" maxFractionDigits="0" />
                                                </td>
                                                <td><strong>${empty member.discountValue ? 0 : member.discountValue}%</strong></td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="5">
                                                <div class="member-cell">
                                                    <div class="member-avatar">0</div>
                                                    <div>
                                                        <strong>Chưa có hội viên</strong>
                                                        <span>Không có dữ liệu membership để hiển thị</span>
                                                    </div>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                    <c:if test="${totalPages > 1}">
                        <div class="membership-pagination">
                            <span class="pagination-summary">Hiển thị ${pageStart}-${pageEnd} trên ${totalMembers} hội viên</span>
                            <div class="pagination-controls">
                                <form action="${pageContext.request.contextPath}/admin/memberships" method="GET">
                                    <input type="hidden" name="search" value="${fn:escapeXml(searchValue)}">
                                    <input type="hidden" name="rankId" value="${selectedRankId == 0 ? '' : selectedRankId}">
                                    <button type="submit" name="page" value="${currentPage - 1}" class="pagination-button" ${currentPage == 1 ? 'disabled' : ''} title="Trang trước">
                                        <i class="fa-solid fa-chevron-left"></i>
                                    </button>
                                </form>

                                <c:forEach begin="1" end="${totalPages}" var="pageNumber">
                                    <form action="${pageContext.request.contextPath}/admin/memberships" method="GET">
                                        <input type="hidden" name="search" value="${fn:escapeXml(searchValue)}">
                                        <input type="hidden" name="rankId" value="${selectedRankId == 0 ? '' : selectedRankId}">
                                        <button type="submit" name="page" value="${pageNumber}" class="pagination-button ${pageNumber == currentPage ? 'is-active' : ''}" ${pageNumber == currentPage ? 'aria-current="page"' : ''}>${pageNumber}</button>
                                    </form>
                                </c:forEach>

                                <form action="${pageContext.request.contextPath}/admin/memberships" method="GET">
                                    <input type="hidden" name="search" value="${fn:escapeXml(searchValue)}">
                                    <input type="hidden" name="rankId" value="${selectedRankId == 0 ? '' : selectedRankId}">
                                    <button type="submit" name="page" value="${currentPage + 1}" class="pagination-button" ${currentPage == totalPages ? 'disabled' : ''} title="Trang sau">
                                        <i class="fa-solid fa-chevron-right"></i>
                                    </button>
                                </form>
                            </div>
                        </div>
                    </c:if>
                </section>
            </main>
        </div>

        <div id="rankModal" class="membership-modal-backdrop" aria-hidden="true">
            <div class="membership-modal-card" role="dialog" aria-modal="true" aria-labelledby="rankModalTitle">
                <div class="membership-modal-header">
                    <div>
                        <span class="section-eyebrow">Tier editor</span>
                        <h3 id="rankModalTitle">Tạo hạng thành viên</h3>
                    </div>
                    <button type="button" class="btn-modal-close" onclick="closeRankModal()" aria-label="Đóng">
                        <i class="fa-solid fa-xmark"></i>
                    </button>
                </div>
                <form action="${pageContext.request.contextPath}/admin/memberships" method="POST">
                    <input type="hidden" id="rankFormAction" name="action" value="createRank">
                    <input type="hidden" id="rankIdInput" name="rankId">

                    <div class="membership-modal-body">
                        <label class="form-field-group">
                            <span>Tên hạng</span>
                            <input id="rankNameInput" type="text" name="rankName" class="form-input-text" placeholder="Tên hạng" required>
                        </label>

                        <label class="form-field-group">
                            <span>Mốc chi tiêu tối thiểu</span>
                            <input id="rankMinInput" type="number" name="minTotalSpent" class="form-input-text" placeholder="Mốc chi tiêu" min="0" step="1000" required>
                        </label>

                        <label class="form-field-group">
                            <span>Giá trị giảm giá (%)</span>
                            <input id="rankDiscountInput" type="number" name="discountValue" class="form-input-text" min="0" max="100" placeholder="Phần trăm giảm" required>
                        </label>

                        <label class="form-field-group">
                            <span>Trạng thái</span>
                            <select id="rankStatusInput" name="isDeleted" class="form-select">
                                <option value="false">Đang dùng</option>
                                <option value="true">Tạm ẩn</option>
                            </select>
                        </label>
                    </div>

                    <div class="membership-modal-footer">
                        <button type="button" class="btn-cancel" onclick="closeRankModal()">Hủy</button>
                        <button type="submit" name="submitAction" value="saveRank" class="btn-primary-action">
                            <i class="fa-solid fa-floppy-disk"></i>
                            Lưu hạng
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <script>
            function openRankModal(form) {
                const fields = form && form.elements ? form.elements : {};
                const formAction = fields.namedItem ? fields.namedItem('action') : null;
                const isEdit = formAction && formAction.value === 'updateRank';
                const modal = document.getElementById('rankModal');

                document.getElementById('rankModalTitle').innerText = isEdit ? 'Chỉnh sửa hạng thành viên' : 'Tạo hạng thành viên';
                document.getElementById('rankFormAction').value = isEdit ? 'updateRank' : 'createRank';
                document.getElementById('rankIdInput').value = isEdit && fields.namedItem('rankId') ? fields.namedItem('rankId').value : '';
                document.getElementById('rankNameInput').value = isEdit && fields.namedItem('rankName') ? fields.namedItem('rankName').value : '';
                document.getElementById('rankMinInput').value = isEdit && fields.namedItem('minTotalSpent') ? fields.namedItem('minTotalSpent').value : '';
                document.getElementById('rankDiscountInput').value = isEdit && fields.namedItem('discountValue') ? fields.namedItem('discountValue').value : '';
                document.getElementById('rankStatusInput').value = isEdit && fields.namedItem('isDeleted') ? fields.namedItem('isDeleted').value : 'false';

                modal.classList.add('is-visible');
                modal.setAttribute('aria-hidden', 'false');
                document.getElementById('rankNameInput').focus();
            }

            function closeRankModal() {
                const modal = document.getElementById('rankModal');
                modal.classList.remove('is-visible');
                modal.setAttribute('aria-hidden', 'true');
            }

            document.addEventListener('keydown', function (event) {
                if (event.key === 'Escape') {
                    closeRankModal();
                }
            });

            document.querySelectorAll('.membership-modal-backdrop').forEach(function (modal) {
                modal.addEventListener('click', function (event) {
                    if (event.target === modal) {
                        modal.classList.remove('is-visible');
                        modal.setAttribute('aria-hidden', 'true');
                    }
                });
            });
        </script>
    </body>
</html>
