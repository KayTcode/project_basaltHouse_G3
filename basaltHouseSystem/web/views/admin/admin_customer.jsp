<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản Lý Khách Hàng - BasaltHouse</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_common.css?v=2">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_account.css?v=2">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_customer.css?v=3">
    </head>
    <body class="admin-dashboard-body">

        <jsp:include page="header.jsp" />

        <div class="app-container">

            <jsp:include page="sidebar.jsp" />

            <main class="main-content">

                <c:if test="${not empty sessionScope.toastMessage}">
                    <div style="background:#e6f5ea;color:#2eb872;border:1px solid #c3edd5;padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px; font-weight: 600;">
                        <i class="fa-solid fa-circle-check"></i> ${sessionScope.toastMessage}
                    </div>
                    <c:remove var="toastMessage" scope="session" />
                </c:if>
                
                <c:if test="${not empty sessionScope.errorMessage}">
                    <div style="background:#fdecea;color:#dc3545;border:1px solid #f5b5b5;padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px; font-weight: 600;">
                        <i class="fa-solid fa-triangle-exclamation"></i> ${sessionScope.errorMessage}
                    </div>
                    <c:remove var="errorMessage" scope="session" />
                </c:if>

                <div class="viewport-headline-bar">
                    <div class="headline-left">
                        <h1 class="page-title">Quản Lý Khách Hàng</h1>
                        <p class="page-desc">Quản lý thông tin khách hàng, hạng thành viên, chi tiêu tích lũy và kiểm soát trạng thái tài khoản.</p>
                    </div>
      
                    <div class="user-avatar-wrapper">
                        <div class="avatar-letter">A</div>
                        <div class="user-info">
                            <span class="user-name">${sessionScope.adminName != null ? sessionScope.adminName : "Quản trị viên"}</span>
                            <span class="user-status-dot">● Trực tuyến</span>
                        </div>
                    </div>
                </div>

                <!-- Thống kê nhanh khách hàng -->
                <div class="account-overview-grid">
                    <div class="overview-card-item">
                        <div class="card-icon-wrapper" style="background-color: #edf2ff; color: #4c6ef5;">
                            <i class="fa-solid fa-users"></i>
                        </div>
                        <div class="card-metrics-data">
                            <span class="metric-label">TỔNG SỐ KHÁCH HÀNG</span>
                            <span class="metric-count"><c:out value="${customerData.stats.total}" default="0" /></span>
                        </div>
                    </div>

                    <div class="overview-card-item">
                        <div class="card-icon-wrapper" style="background-color: var(--basalt-emerald-light); color: var(--basalt-emerald);">
                            <i class="fa-solid fa-user-check"></i>
                        </div>
                        <div class="card-metrics-data">
                            <span class="metric-label">ĐANG HOẠT ĐỘNG</span>
                            <span class="metric-count"><c:out value="${customerData.stats.active}" default="0" /></span>
                        </div>
                    </div>

                    <div class="overview-card-item">
                        <div class="card-icon-wrapper" style="background-color: #fff9db; color: #f59f00;">
                            <i class="fa-solid fa-crown"></i>
                        </div>
                        <div class="card-metrics-data">
                            <span class="metric-label">KHÁCH HÀNG VIP</span>
                            <span class="metric-count"><c:out value="${customerData.stats.vip}" default="0" /></span>
                        </div>
                    </div>

                    <div class="overview-card-item">
                        <div class="card-icon-wrapper" style="background-color: var(--basalt-red-light); color: var(--basalt-red);">
                            <i class="fa-solid fa-user-lock"></i>
                        </div>
                        <div class="card-metrics-data">
                            <span class="metric-label">TÀI KHOẢN KHÓA</span>
                            <span class="metric-count" style="color: var(--basalt-red);"><c:out value="${customerData.stats.locked}" default="0" /></span>
                        </div>
                    </div>
                </div>

                <div class="data-management-panel">
                    <div class="panel-header-toolbar">
                        <h2>Danh Sách Khách Hàng &amp; Hạng Thành Viên</h2>
                        <div class="toolbar-actions-group">
                            <button class="btn-primary-action" onclick="openAddModal()">
                                <i class="fa-solid fa-plus-circle"></i> Thêm Khách Hàng Mới
                            </button>
                        </div>
                    </div>

                    <!-- Tìm kiếm & Lọc -->
                    <form method="get" action="${pageContext.request.contextPath}/admin/customers" class="search-filter-belt">
                        <div class="search-input-box">
                            <input type="text" name="search" id="searchCustomerInput" placeholder="Tìm kiếm tên, email, sđt..." value="${customerData.oldSearch}">
                        </div>

                        <div class="filter-controls-group">
                            <div class="select-wrapper">
                                <label for="filterRankSelect">Hạng thành viên:</label>
                                <select id="filterRankSelect" name="rankId" onchange="this.form.submit()" class="filter-select">
                                    <option value="" ${empty customerData.oldRankId ? 'selected' : ''}>-- Tất cả hạng --</option>
                                    <c:forEach var="rank" items="${customerData.ranks}">
                                        <option value="${rank.rankId}" ${customerData.oldRankId == rank.rankId ? 'selected' : ''}>${rank.rankName}</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="select-wrapper">
                                <label for="filterStatusSelect">Trạng thái:</label>
                                <select id="filterStatusSelect" name="status" onchange="this.form.submit()" class="filter-select">
                                    <option value="" ${empty customerData.oldStatus ? 'selected' : ''}>-- Tất cả trạng thái --</option>
                                    <option value="Active" ${customerData.oldStatus == 'Active' ? 'selected' : ''}>Hoạt động (Active)</option>
                                    <option value="Locked" ${customerData.oldStatus == 'Locked' ? 'selected' : ''}>Đã khóa (Locked)</option>
                                </select>
                            </div>

                            <button type="submit" class="btn-filter-submit">
                                <i class="fa-solid fa-filter"></i> Lọc kết quả
                            </button>
                        </div>
                    </form>

                    <!-- Bảng danh sách khách hàng -->
                    <div class="responsive-table-wrapper">
                        <table class="basalt-custom-table" id="customersMainTable">
                            <thead>
                                <tr>
                                    <th>Mã KH</th>
                                    <th>Khách Hàng</th>
                                    <th>Liên Hệ</th>
                                    <th style="text-align: center;">Hạng Thành Viên</th>
                                    <th style="text-align: right;">Tổng Chi Tiêu</th>
                                    <th style="text-align: center;">Trạng Thái</th>
                                    <th>Ngày Đăng Ký</th>
                                    <th style="text-align: right; padding-right: 24px;">Hành Động</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${customerData.customers}">
                                    <tr class="account-row-item">
                                        <td><code>#${item.account.accountId}</code></td>
                                        <td>
                                            <div class="user-cell-name" style="display:flex; align-items:center; gap:10px;">
                                                <c:choose>
                                                    <c:when test="${not empty item.avatarUrl}">
                                                        <img src="${item.avatarUrl}" alt="avatar" style="width:32px; height:32px; border-radius:50%; object-fit:cover; border:1px solid #ddd;">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="customer-avatar-circle">
                                                            ${fn:substring(item.fullName, 0, 1)}
                                                        </div>
                                                    </c:otherwise>
                                                </c:choose>
                                                <span style="font-weight:700; color:var(--basalt-green);">${item.fullName}</span>
                                            </div>
                                        </td>
                                        <td>
                                            <div class="contact-card-cell" style="display:flex; flex-direction:column; gap:2px; font-size:12px;">
                                                <span class="cell-email"><i class="fa-regular fa-envelope"></i> ${item.account.email}</span>
                                                <span class="cell-phone">
                                                    <i class="fa-solid fa-phone"></i> 
                                                    <c:choose>
                                                        <c:when test="${not empty item.phone}">${item.phone}</c:when>
                                                        <c:otherwise><em style="color:#a0a0a0;">Chưa cập nhật</em></c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </div>
                                        </td>
                                        <td style="text-align: center;">
                                            <span class="rank-badge rank-${item.rankId}">
                                                <i class="fa-solid fa-gem"></i> ${item.rankName}
                                            </span>
                                        </td>
                                        <td style="text-align: right;" class="spent-text">
                                            <fmt:formatNumber value="${item.totalSpent}" type="currency" currencySymbol="đ" maxFractionDigits="0"/>
                                        </td>
                                        <td style="text-align: center;">
                                            <c:choose>
                                                <c:when test="${item.account.isLocked}">
                                                    <span class="status-badge badge-suspended">Đã khóa</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="status-badge badge-active">Hoạt động</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td style="color:#666; font-weight: 500;">
                                            <c:catch var="formatError">
                                                <fmt:formatDate value="${item.createdAtDate}" pattern="dd/MM/yyyy HH:mm"/>
                                            </c:catch>
                                            <c:if test="${not empty formatError}">
                                                ${item.account.createdAt}
                                            </c:if>
                                        </td>
                                        <td style="text-align: right; padding-right: 24px;">
                                            <div class="row-actions-cell" style="justify-content: flex-end; display: flex; gap: 8px; align-items: center;">
                                                <!-- Xem lịch sử mua hàng -->
                                                <a href="${pageContext.request.contextPath}/admin/customers?action=history&accountId=${item.account.accountId}"
                                                   class="btn-icon-action btn-icon-history" title="Lịch sử mua hàng">
                                                    <i class="fa-solid fa-clock-rotate-left"></i>
                                                </a>
                                                <!-- Chi tiết & Cập nhật -->
                                                <button class="btn-icon-action action-edit" title="Xem &amp; Sửa thông tin" 
                                                        onclick="openEditModal('${item.account.accountId}', '${item.fullName}', '${item.phone}', '${item.account.email}', '${item.rankId}', '${item.totalSpent}', '${item.account.isLocked}')">
                                                    <i class="fa-regular fa-pen-to-square"></i>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty customerData.customers}">
                                    <tr>
                                        <td colspan="8" style="text-align:center;color:#aaa;padding:45px 0">
                                            <i class="fa-regular fa-face-frown" style="font-size: 24px; margin-bottom: 8px; display: block;"></i>
                                            Không tìm thấy khách hàng nào phù hợp!
                                        </td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>

                    <!-- Phân trang -->
                    <c:if test="${customerData.totalPages > 1}">
                        <div style="display:flex;gap:6px;justify-content:flex-end;margin:20px 24px;font-size:13px">
                            <c:forEach begin="1" end="${customerData.totalPages}" var="p">
                                <a href="${pageContext.request.contextPath}/admin/customers?page=${p}&search=${customerData.oldSearch}&rankId=${customerData.oldRankId}&status=${customerData.oldStatus}" style="padding:6px 12px;border:1px solid #e2e2e2;border-radius:6px;text-decoration:none; ${p == customerData.currentPage ? 'background:var(--basalt-green);color:#fff;border-color:var(--basalt-green);' : 'color:#555;'}">
                                    ${p}
                                </a>
                            </c:forEach>
                        </div>
                    </c:if>
                </div>
            </main>
        </div>

        <!-- MODAL THÊM KHÁCH HÀNG MỚI -->
        <div class="modal-backdrop" id="addCustomerModal">
            <div class="modal-card">
                <div class="modal-card-header">
                    <h3><i class="fa-solid fa-user-plus"></i> Đăng Ký Khách Hàng Mới</h3>
                    <span class="close-modal-x" onclick="closeAddModal()">&times;</span>
                </div>
                <form action="${pageContext.request.contextPath}/admin/customers" method="POST">
                    <input type="hidden" name="action" value="add">
                    <div class="modal-card-body">
                        <div class="modal-form-flex-stack">
                            <div class="form-field-group">
                                <label>Họ &amp; Tên <span style="color:var(--basalt-red);">*</span></label>
                                <input type="text" name="fullName" placeholder="Nhập tên đầy đủ của khách hàng" required class="form-input-text">
                            </div>
                            <div class="form-field-group">
                                <label>Email / Tên Đăng Nhập <span style="color:var(--basalt-red);">*</span></label>
                                <input type="email" name="email" placeholder="example@gmail.com" required class="form-input-text">
                            </div>
                            <div class="form-field-group">
                                <label>Số Điện Thoại <span style="color:var(--basalt-red);">*</span></label>
                                <input type="tel" name="phone" placeholder="Nhập số điện thoại khách hàng" required class="form-input-text" pattern="[0-9]{10,}">
                            </div>
                            <div class="form-field-group">
                                <label>Mật Khẩu <span style="color:var(--basalt-red);">*</span></label>
                                <input type="password" name="password" placeholder="Tối thiểu 6 ký tự" required class="form-input-text" minlength="6">
                            </div>
                            <div class="form-field-group">
                                <label>Hạng Thành Viên Ban Đầu</label>
                                <select name="rankId" class="form-select">
                                    <c:forEach var="rank" items="${customerData.ranks}">
                                        <option value="${rank.rankId}">${rank.rankName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="form-field-group">
                                <label>Tổng Chi Tiêu Ban Đầu (đ)</label>
                                <input type="number" name="totalSpent" value="0" min="0" class="form-input-text">
                            </div>
                        </div>
                    </div>
                    <div class="modal-card-footer">
                        <button type="button" class="btn-cancel" onclick="closeAddModal()">Hủy Bỏ</button>
                        <button type="submit" class="btn-primary-action">Tạo Tài Khoản</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- MODAL XEM CHI TIẾT & CẬP NHẬT THÔNG TIN -->
        <div class="modal-backdrop" id="editCustomerModal">
            <div class="modal-card">
                <div class="modal-card-header">
                    <h3><i class="fa-solid fa-user-pen"></i> Chi Tiết Hồ Sơ Khách Hàng</h3>
                    <span class="close-modal-x" onclick="closeEditModal()">&times;</span>
                </div>
                <form action="${pageContext.request.contextPath}/admin/customers" method="POST">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="accountId" id="edit_id">
                    <div class="modal-card-body">
                        <div class="modal-form-flex-stack">
                            <p class="edit-account-notice">
                                <i class="fa-solid fa-circle-info"></i> Đang cập nhật hồ sơ khách hàng: <strong id="edit_notice_title">#ID</strong>
                            </p>

                            <div class="form-field-group">
                                <label>Họ &amp; Tên <span style="color:var(--basalt-red);">*</span></label>
                                <input type="text" name="fullName" id="edit_fullName" required class="form-input-text">
                            </div>
                            
                            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px;">
                                <div class="form-field-group">
                                    <label>Email liên hệ <span style="color:var(--basalt-red);">*</span></label>
                                    <input type="email" name="email" id="edit_email" required class="form-input-text">
                                </div>
                                <div class="form-field-group">
                                    <label>Số Điện Thoại <span style="color:var(--basalt-red);">*</span></label>
                                    <input type="text" name="phone" id="edit_phone" required class="form-input-text" pattern="[0-9]{10,}">
                                </div>
                            </div>

                            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px;">
                                <div class="form-field-group">
                                    <label>Hạng Thành Viên</label>
                                    <select name="rankId" id="edit_rank" class="form-select">
                                        <c:forEach var="rank" items="${customerData.ranks}">
                                            <option value="${rank.rankId}">${rank.rankName}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="form-field-group">
                                    <label>Tổng Chi Tiêu Tích Lũy (đ)</label>
                                    <input type="number" name="totalSpent" id="edit_totalSpent" min="0" class="form-input-text">
                                </div>
                            </div>

                            <div class="form-field-group">
                                <label>Trạng Thái Tài Khoản</label>
                                <select name="isLocked" id="edit_status" class="form-select">
                                    <option value="false">Hoạt động (Active)</option>
                                    <option value="true">Đang bị khóa (Locked)</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="modal-card-footer">
                        <button type="button" class="btn-cancel" onclick="closeEditModal()">Đóng</button>
                        <button type="submit" class="btn-primary-action">Lưu Cập Nhật</button>
                    </div>
                </form>
            </div>
        </div>

        <script>
            function openAddModal()  { document.getElementById('addCustomerModal').classList.add('active'); }
            function closeAddModal() { document.getElementById('addCustomerModal').classList.remove('active'); }

            function openEditModal(id, fullName, phone, email, rankId, totalSpent, isLocked) {
                document.getElementById('edit_id').value              = id;
                document.getElementById('edit_notice_title').innerText = 'Khách hàng #' + id;
                document.getElementById('edit_fullName').value        = fullName;
                document.getElementById('edit_phone').value           = phone;
                document.getElementById('edit_email').value           = email;
                document.getElementById('edit_rank').value            = rankId;
                document.getElementById('edit_totalSpent').value      = Math.round(parseFloat(totalSpent) || 0);
                document.getElementById('edit_status').value          = (isLocked === 'true' || isLocked === '1') ? 'true' : 'false';
                document.getElementById('editCustomerModal').classList.add('active');
            }
            function closeEditModal() { document.getElementById('editCustomerModal').classList.remove('active'); }
        </script>
    </body>
</html>
