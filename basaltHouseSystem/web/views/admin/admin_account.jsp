<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản Lý Tài Khoản - BasaltHouse</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_common.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_account.css">
    </head>
    <body class="admin-dashboard-body">

        <jsp:include page="header.jsp" />

        <div class="app-container">

            <jsp:include page="sidebar.jsp" />

            <main class="main-content">

                <c:if test="${not empty sessionScope.toastMessage}">
                    <div style="background:#fdecea;color:#dc3545;border:1px solid #f5b5b5;padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px">
                        ${sessionScope.toastMessage}
                    </div>
                    <c:remove var="toastMessage" scope="session" />
                </c:if>

                <div class="viewport-headline-bar">
                    <div class="headline-left">
                        <h1 class="page-title">Quản Lý Tài Khoản Thành Viên</h1>
                        <p class="page-desc">Phân quyền, quản trị bảo mật thông tin và giám sát hoạt động hệ thống BasaltHouse.</p>
                    </div>

                    <div class="user-avatar-wrapper">
                        <div class="avatar-letter">A</div>
                        <div class="user-info">
                            <span class="user-name">${sessionScope.adminName != null ? sessionScope.adminName : "Quản trị viên"}</span>
                            <span class="user-status-dot">● Trực tuyến</span>
                        </div>
                    </div>
                </div>

                <div class="account-overview-grid">
                    <div class="overview-card-item">
                        <div class="card-icon-wrapper">
                            <i class="fa-solid fa-users"></i>
                        </div>
                        <div class="card-metrics-data">
                            <span class="metric-label">TỔNG SỐ TÀI KHOẢN</span>
                            <span class="metric-count"><c:out value="${accountData.stats.total}" /></span>
                        </div>
                    </div>

                    <div class="overview-card-item">
                        <div class="card-icon-wrapper">
                            <i class="fa-solid fa-user-gear"></i>
                        </div>
                        <div class="card-metrics-data">
                            <span class="metric-label">QUẢN TRỊ VIÊN</span>
                            <span class="metric-count">
                                <c:set var="adminCount" value="0" />
                                <c:forEach var="acc" items="${accountData.accounts}">
                                    <c:if test="${acc.account.roleId == 1}">
                                        <c:set var="adminCount" value="${adminCount + 1}" />
                                    </c:if>
                                </c:forEach>
                                <c:out value="${adminCount}" />
                            </span>
                        </div>
                    </div>

                    <div class="overview-card-item">
                        <div class="card-icon-wrapper">
                            <i class="fa-solid fa-user-tie"></i>
                        </div>
                        <div class="card-metrics-data">
                            <span class="metric-label">NHÂN VIÊN CỬA HÀNG</span>
                            <span class="metric-count"><c:out value="${accountData.stats.staffCashier}" /></span>
                        </div>
                    </div>

                    <div class="overview-card-item">
                        <div class="card-icon-wrapper">
                            <i class="fa-solid fa-user-lock"></i>
                        </div>
                        <div class="card-metrics-data">
                            <span class="metric-label">TÀI KHOẢN ĐANG KHÓA</span>
                            <span class="metric-count" style="color: var(--basalt-red);"><c:out value="${accountData.stats.locked}" /></span>
                        </div>
                    </div>
                </div>

                <div class="data-management-panel">
                    <div class="panel-header-toolbar">
                        <h2>Danh Sách Thành Viên & Phân Quyền</h2>
                        <div class="toolbar-actions-group">
                            <button class="btn-primary-action" onclick="openAddModal()">
                                <i class="fa-solid fa-plus-circle"></i> Thêm Tài Khoản Mới
                            </button>
                        </div>
                    </div>

                    <form method="get" action="${pageContext.request.contextPath}/admin/accounts" class="search-filter-belt" style="padding: 16px 24px; display: flex; gap: 16px; align-items: center; border-bottom: 1px solid var(--basalt-border); background: #fafafa;">
                        <div class="search-input-box">
                            <input type="text" name="search" id="searchAccountInput" placeholder="Tìm kiếm tên, email, sđt..." value="${accountData.oldSearch}" style="padding-left: 14px; width: 280px; height: 38px; border: 1px solid #ccc; border-radius: 6px;">
                        </div>

                        <div style="display:flex; gap:12px; align-items:center;">
                            <div class="select-wrapper">
                                <label for="filterRoleSelect" style="font-size:13px; font-weight:600; margin-right:6px;">Vai trò:</label>
                                <select id="filterRoleSelect" name="roleId" onchange="this.form.submit()" style="padding: 8px 12px; border: 1px solid var(--basalt-border); border-radius: 6px; height: 38px;">
                                    <option value="" ${empty accountData.oldRoleId ? 'selected' : ''}>-- Tất cả vai trò --</option>
                                    <option value="1" ${accountData.oldRoleId == '1' ? 'selected' : ''}>Admin (Quản trị viên)</option>
                                    <option value="2" ${accountData.oldRoleId == '2' ? 'selected' : ''}>Customer (Khách hàng)</option>
                                    <option value="3" ${accountData.oldRoleId == '3' ? 'selected' : ''}>Staff (Nhân viên)</option>
                                    <option value="4" ${accountData.oldRoleId == '4' ? 'selected' : ''}>Cashier (Thu ngân)</option>
                                    <option value="5" ${accountData.oldRoleId == '5' ? 'selected' : ''}>Shipper (Giao hàng)</option>
                                </select>
                            </div>

                            <div class="select-wrapper">
                                <label for="filterStatusSelect" style="font-size:13px; font-weight:600; margin-right:6px;">Trạng thái:</label>
                                <select id="filterStatusSelect" name="status" onchange="this.form.submit()" style="padding: 8px 12px; border: 1px solid var(--basalt-border); border-radius: 6px; height: 38px;">
                                    <option value="" ${empty accountData.oldStatus ? 'selected' : ''}>-- Tất cả trạng thái --</option>
                                    <option value="Active" ${accountData.oldStatus == 'Active' ? 'selected' : ''}>Hoạt động (Active)</option>
                                    <option value="Locked" ${accountData.oldStatus == 'Locked' ? 'selected' : ''}>Đã khóa (Locked)</option>
                                    <option value="Inactive" ${accountData.oldStatus == 'Inactive' ? 'selected' : ''}>Vô hiệu hoá (Inactive)</option>
                                </select>
                            </div>

                            <button type="submit" class="btn-primary-action" style="background:#495057; padding: 0 14px; height: 38px;">
                                <i class="fa-solid fa-filter"></i> Lọc
                            </button>
                        </div>
                    </form>

                    <div class="responsive-table-wrapper">
                        <table class="basalt-custom-table" id="accountsMainTable">
                            <thead>
                                <tr>
                                    <th>Mã KH/NV</th>
                                    <th>Họ &amp; Tên</th>
                                    <th>Thông Tin Liên Hệ</th>
                                    <th style="text-align: center;">Vai Trò</th>
                                    <th style="text-align: center;">Trạng Thái</th>
                                    <th>Ngày Đăng Ký</th>
                                    <th style="text-align: right; padding-right: 24px;">Hành Động</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${accountData.accounts}">
                                    <tr class="account-row-item">
                                        <td><code>#${item.account.accountId}</code></td>
                                        <td>
                                            <div class="user-cell-name" style="display:flex; align-items:center; gap:8px;">
                                                <div class="symbol-avatar" style="width:28px; height:28px; background:#e8fbee; border-radius:50%; display:flex; align-items:center; justify-content:center;">☕</div>
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
                                            <c:choose>
                                                <c:when test="${item.account.roleId == 1}">
                                                    <span class="role-badge role-admin" style="background:#fff0f6; color:#d6336c; padding:4px 8px; border-radius:4px; font-size:11px; font-weight:700;"><i class="fa-solid fa-user-shield"></i> ${item.roleName}</span>
                                                </c:when>
                                                <c:when test="${item.account.roleId == 3 || item.account.roleId == 4 || item.account.roleId == 5}">
                                                    <span class="role-badge role-employee" style="background:#e8f7ff; color:#1c7ed6; padding:4px 8px; border-radius:4px; font-size:11px; font-weight:700;"><i class="fa-solid fa-user-tie"></i> ${item.roleName}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="role-badge role-customer" style="background:#f1f3f5; color:#495057; padding:4px 8px; border-radius:4px; font-size:11px; font-weight:700;"><i class="fa-solid fa-user"></i> ${item.roleName}</span>
                                                </c:otherwise>
                                            </c:choose>
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
                                            <div class="row-actions-cell" style="justify-content: flex-end; display: flex; gap: 8px;">
                                                <button class="btn-icon-action action-edit" title="Sửa thông tin" 
                                                        onclick="openEditModal('${item.account.accountId}', '${item.fullName}', '${item.phone}', '${item.account.email}', '${item.account.roleId}', '${item.account.isLocked}')">
                                                    <i class="fa-regular fa-pen-to-square"></i>
                                                </button>

                                                <c:choose>
                                                    <c:when test="${!item.account.isLocked}">
                                                        <form method="post" action="${pageContext.request.contextPath}/admin/accounts" style="display:inline">
                                                            <input type="hidden" name="action" value="delete" />
                                                            <input type="hidden" name="accountId" value="${item.account.accountId}" />
                                                            <input type="hidden" name="roleId" value="${item.account.roleId}" />
                                                            <button type="submit" class="btn-icon-action" style="color:var(--basalt-red);" title="Xoá tài khoản"
                                                                    onclick="return confirm('Bạn chắc chắn muốn XOÁ tài khoản này?')">
                                                                <i class="fa-solid fa-trash"></i>
                                                            </button>
                                                        </form>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <form method="post" action="${pageContext.request.contextPath}/admin/accounts" style="display:inline">
                                                            <input type="hidden" name="action" value="update" />
                                                            <input type="hidden" name="accountId" value="${item.account.accountId}" />
                                                            <input type="hidden" name="email" value="${item.account.email}" />
                                                            <input type="hidden" name="roleId" value="${item.account.roleId}" />
                                                            <input type="hidden" name="oldRoleId" value="${item.account.roleId}" />
                                                            <input type="hidden" name="fullName" value="${item.fullName}" />
                                                            <input type="hidden" name="phone" value="${item.phone}" />
                                                            <input type="hidden" name="isActive" value="true" />
                                                            <input type="hidden" name="isLocked" value="false" />
                                                            <button type="submit" class="btn-icon-action" style="color:var(--basalt-emerald);" title="Mở khóa tài khoản">
                                                                <i class="fa-solid fa-lock"></i>
                                                            </button>
                                                        </form>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty accountData.accounts}">
                                    <tr>
                                        <td colspan="7" style="text-align:center;color:#aaa;padding:30px 0">
                                            Không tìm thấy thành viên nào phù hợp!
                                        </td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>

                    <c:if test="${accountData.totalPages > 1}">
                        <div style="display:flex;gap:6px;justify-content:flex-end;margin:20px 24px;font-size:13px">
                            <c:forEach begin="1" end="${accountData.totalPages}" var="p">
                                <a href="${pageContext.request.contextPath}/admin/accounts?page=${p}&search=${accountData.oldSearch}&roleId=${accountData.oldRoleId}&status=${accountData.oldStatus}" style="padding:6px 12px;border:1px solid #e2e2e2;border-radius:6px;text-decoration:none; ${p == accountData.currentPage ? 'background:var(--basalt-green);color:#fff;border-color:var(--basalt-green);' : 'color:#555;'}">
                                    ${p}
                                </a>
                            </c:forEach>
                        </div>
                    </c:if>
                </div>
            </main>
        </div>

        <div class="modal-backdrop" id="addAccountModal">
            <div class="modal-card">
                <div class="modal-card-header">
                    <h3><i class="fa-solid fa-user-plus"></i> Thêm Thành Viên Mới</h3>
                    <span class="close-modal-x" onclick="closeAddModal()">&times;</span>
                </div>
                <form action="${pageContext.request.contextPath}/admin/accounts" method="POST">
                    <input type="hidden" name="action" value="add">
                    <div class="modal-card-body">
                        <div class="modal-form-flex-stack">
                            <div class="form-field-group">
                                <label>Họ &amp; Tên <span style="color:var(--basalt-red);">*</span></label>
                                <input type="text" name="fullName" placeholder="Nhập tên đầy đủ" required class="form-input-text">
                            </div>
                            <div class="form-field-group">
                                <label>Tên Đăng Nhập / Email <span style="color:var(--basalt-red);">*</span></label>
                                <input type="email" name="email" placeholder="email@basalthouse.com" required class="form-input-text">
                            </div>
                            <div class="form-field-group">
                                <label>Số Điện Thoại <span style="color:var(--basalt-red);">*</span></label>
                                <input type="tel" name="phone" placeholder="Nhập số điện thoại" required class="form-input-text" pattern="[0-9]{10,}">
                            </div>
                            <div class="form-field-group">
                                <label>Mật Khẩu <span style="color:var(--basalt-red);">*</span></label>
                                <input type="password" name="password" placeholder="Tối thiểu 6 ký tự" required class="form-input-text">
                            </div>
                            <div class="form-field-group">
                                <label>Phân Vai Trò <span style="color:var(--basalt-red);">*</span></label>
                                <select name="roleId" required class="form-select">
                                    <option value="2">Customer (Khách hàng)</option>
                                    <option value="3">Staff (Nhân viên)</option>
                                    <option value="4">Cashier (Thu ngân)</option>
                                    <option value="5">Shipper (Giao hàng)</option>
                                    <option value="1">Admin (Quản trị viên)</option>
                                </select>
                            </div>
                            <div class="form-field-group">
                                <label>Trạng Thái Truy Cập <span style="color:var(--basalt-red);">*</span></label>
                                <select name="isActive" required class="form-select">
                                    <option value="true">Hoạt động (Cho phép login)</option>
                                    <option value="false">Đã khóa (Tạm ngừng login)</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="modal-card-footer">
                        <button type="button" class="btn-cancel" onclick="closeAddModal()">Hủy Bỏ</button>
                        <button type="submit" class="btn-primary-action">Lưu Tài Khoản</button>
                    </div>
                </form>
            </div>
        </div>

        <div class="modal-backdrop" id="editAccountModal">
            <div class="modal-card">
                <div class="modal-card-header">
                    <h3><i class="fa-solid fa-user-pen"></i> Cập Nhật Thông Tin</h3>
                    <span class="close-modal-x" onclick="closeEditModal()">&times;</span>
                </div>
                <form action="${pageContext.request.contextPath}/admin/accounts" method="POST">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="accountId" id="edit_id">
                    <input type="hidden" name="oldRoleId" id="edit_oldRoleId">
                    <input type="hidden" name="isActive" value="true">
                    <div class="modal-card-body">
                        <div class="modal-form-flex-stack">
                            <p class="edit-account-notice">Đang chỉnh sửa cho thành viên: <strong id="edit_notice_title">#ID</strong></p>
                            
                            <div class="form-field-group">
                                <label>Họ &amp; Tên <span style="color:var(--basalt-red);">*</span></label>
                                <input type="text" name="fullName" id="edit_fullName" required class="form-input-text">
                            </div>
                            <div class="form-field-group">
                                <label>Email liên hệ <span style="color:var(--basalt-red);">*</span></label>
                                <input type="email" name="email" id="edit_email" required class="form-input-text">
                            </div>
                            <div class="form-field-group">
                                <label>Số Điện Thoại <span style="color:var(--basalt-red);">*</span></label>
                                <input type="text" name="phone" id="edit_phone" required class="form-input-text">
                            </div>
                            <div class="form-field-group">
                                <label>Thay Đổi Vai Trò</label>
                                <select name="roleId" id="edit_role" class="form-select">
                                    <option value="1">Admin (Quản trị viên)</option>
                                    <option value="2">Customer (Khách hàng)</option>
                                    <option value="3">Staff (Nhân viên)</option>
                                    <option value="4">Cashier (Thu ngân)</option>
                                    <option value="5">Shipper (Giao hàng)</option>
                                </select>
                            </div>
                            <div class="form-field-group">
                                <label>Trạng Thái Tài Khoản</label>
                                <select name="isLocked" id="edit_status" class="form-select">
                                    <option value="false">Hoạt động (Active)</option>
                                    <option value="true">Khóa tài khoản (Locked)</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="modal-card-footer">
                        <button type="button" class="btn-cancel" onclick="closeEditModal()">Đóng</button>
                        <button type="submit" class="btn-primary-action" style="background-color: var(--basalt-blue, #1c7ed6);">Cập Nhật</button>
                    </div>
                </form>
            </div>
        </div>

        <script>
            function openAddModal() {
                document.getElementById('addAccountModal').classList.add('active');
            }
            function closeAddModal() {
                document.getElementById('addAccountModal').classList.remove('active');
            }

            function openEditModal(id, fullName, phone, email, roleId, isLocked) {
                document.getElementById('edit_id').value = id;
                document.getElementById('edit_oldRoleId').value = roleId;
                document.getElementById('edit_notice_title').innerText = "Thành viên #" + id;
                document.getElementById('edit_fullName').value = fullName;
                document.getElementById('edit_phone').value = phone;
                document.getElementById('edit_email').value = email;
                document.getElementById('edit_role').value = roleId;

                let lockStatus = (isLocked === true || isLocked === 'true' || isLocked === 1 || isLocked === '1') ? 'true' : 'false';
                document.getElementById('edit_status').value = lockStatus;

                document.getElementById('editAccountModal').classList.add('active');
            }
            
            function closeEditModal() {
                document.getElementById('editAccountModal').classList.remove('active');
            }
        </script>
    </body>
</html>