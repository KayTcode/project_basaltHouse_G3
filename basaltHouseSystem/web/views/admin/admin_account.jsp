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
        <link rel="stylesheet" type="text/css" href="/basaltHouseSystem/css/admin/admin_account.css">
    </head>
    <body class="admin-dashboard-body">

        <!-- TOP HEADER -->
        <header class="top-header">
            <div class="logo">Basalt <span>House</span></div>
            <div class="header-buttons">
                <span class="sys-time-badge" id="current-time-txt">00:00:00 - Hôm nay</span>
            </div>
        </header>

        <!-- KHUNG CHÍNH (SIDEBAR + MAIN CONTENT) -->
        <div class="app-container">

            <jsp:include page="sidebar.jsp" />

            <main class="main-content">

                <!-- Toast thông báo (nếu Service redirect kèm lỗi) -->
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

                <!-- THẺ KPI THỐNG KÊ TÀI KHOẢN TỔNG QUAN -->
                <%-- ĐÃ SỬA: Servlet gói cả Map vào 1 attribute "accountData",
                     nên mọi giá trị phải đọc qua accountData.<key> --%>
                <div class="kpi-cards-grid">
                    <!-- Thẻ 1: Tổng số tài khoản -->
                    <div class="kpi-card-item border-l-green">
                        <div class="kpi-card-header">
                            <span class="kpi-label">TỔNG SỐ TÀI KHOẢN</span>
                            <div class="kpi-icon"><i class="fa-solid fa-users"></i></div>
                        </div>
                        <div class="kpi-value">
                            <c:out value="${accountData.stats.total}" />
                        </div>
                        <div class="kpi-footer">
                            <span class="text-green-trend">
                                <i class="fa-solid fa-shield-halved"></i> Đã đồng bộ từ Database
                            </span>
                        </div>
                    </div>

                    <!-- Thẻ 2: Quản trị viên (Admin) -->
                    <div class="kpi-card-item border-l-blue">
                        <div class="kpi-card-header">
                            <span class="kpi-label">QUẢN TRỊ VIÊN</span>
                            <div class="kpi-icon"><i class="fa-solid fa-user-gear"></i></div>
                        </div>
                        <div class="kpi-value">
                            <%-- Đếm Admin trong DANH SÁCH ĐÃ LỌC (accountData.accounts).
                                 Nếu muốn đếm Admin trên TOÀN BỘ DB (không phụ thuộc filter/trang),
                                 cần thêm dòng stats.put("admin", ...) trong Service. --%>
                            <c:set var="adminCount" value="0" />
                            <c:forEach var="acc" items="${accountData.accounts}">
                                <c:if test="${acc.account.roleId == 1}">
                                    <c:set var="adminCount" value="${adminCount + 1}" />
                                </c:if>
                            </c:forEach>
                            <c:out value="${adminCount}" />
                        </div>
                        <div class="kpi-footer">
                            <span>Toàn quyền kiểm soát hệ thống</span>
                        </div>
                    </div>

                    <!-- Thẻ 3: Nhân viên cửa hàng (Staff + Cashier) -->
                    <div class="kpi-card-item border-l-emerald">
                        <div class="kpi-card-header">
                            <span class="kpi-label">NHÂN VIÊN CỬA HÀNG</span>
                            <div class="kpi-icon"><i class="fa-solid fa-user-tie"></i></div>
                        </div>
                        <div class="kpi-value">
                            <c:out value="${accountData.stats.staffCashier}" />
                        </div>
                        <div class="kpi-footer">
                            <span>Vận hành quầy và kiểm kho</span>
                        </div>
                    </div>

                    <!-- Thẻ 4: Tài khoản bị khóa -->
                    <div class="kpi-card-item border-l-red">
                        <div class="kpi-card-header">
                            <span class="kpi-label">TÀI KHOẢN ĐANG KHÓA</span>
                            <div class="kpi-icon"><i class="fa-solid fa-user-lock"></i></div>
                        </div>
                        <div class="kpi-value text-red-strong">
                            <c:out value="${accountData.stats.locked}" />
                        </div>
                        <div class="kpi-footer">
                            <span class="text-red-strong"><i class="fa-solid fa-ban"></i> Tạm dừng truy cập bảo mật</span>
                        </div>
                    </div>
                </div>

                <!-- KHU VỰC BẢNG QUẢN LÝ VÀ BỘ LỌC ĐA NĂNG -->
                <div class="dashboard-visual-card">
                    <div class="card-title-header">
                        <div>
                            <span class="card-title">Danh Sách Thành Viên & Phân Quyền</span>
                            <span class="card-subtitle">Hiển thị, chỉnh sửa trực tiếp, thay đổi trạng thái và cấp quyền nhân sự</span>
                        </div>
                        <button class="btn-primary" onclick="openAddModal()">
                            <i class="fa-solid fa-plus-circle"></i> Thêm Tài Khoản Mới
                        </button>
                    </div>

                    <!-- ═══════════════════════════════════════════════════════════
                         BỘ LỌC TÌM KIẾM — ĐÃ CHUYỂN SANG SERVER-SIDE
                         Lý do: Service đã code sẵn xử lý search/roleId/status/page
                         qua query string GET. JS lọc DOM cũ bị xóa vì so sánh sai
                         kiểu dữ liệu (roleId số "3" với tên chữ "Employee" không bao giờ khớp).
                         ═══════════════════════════════════════════════════════════ -->
                    <form method="get" action="${pageContext.request.contextPath}/admin/accounts" class="search-filter-belt">
                        <div class="input-search-wrapper">
                            <i class="fa-solid fa-magnifying-glass search-icon"></i>
                            <input type="text" name="search" id="searchAccountInput"
                                   placeholder="Tìm kiếm tên, email, sđt..."
                                   value="${accountData.oldSearch}">
                        </div>

                        <div class="filter-dropdown-group">
                            <div class="select-wrapper">
                                <label for="filterRoleSelect">Vai trò:</label>
                                <%-- value dùng RoleId số (1-5) để khớp đúng cách Service so sánh --%>
                                <select id="filterRoleSelect" name="roleId" onchange="this.form.submit()">
                                    <option value="" ${empty accountData.oldRoleId ? 'selected' : ''}>-- Tất cả vai trò --</option>
                                    <option value="1" ${accountData.oldRoleId == '1' ? 'selected' : ''}>Admin (Quản trị viên)</option>
                                    <option value="2" ${accountData.oldRoleId == '2' ? 'selected' : ''}>Customer (Khách hàng)</option>
                                    <option value="3" ${accountData.oldRoleId == '3' ? 'selected' : ''}>Staff (Nhân viên)</option>
                                    <option value="4" ${accountData.oldRoleId == '4' ? 'selected' : ''}>Cashier (Thu ngân)</option>
                                    <option value="5" ${accountData.oldRoleId == '5' ? 'selected' : ''}>Shipper (Giao hàng)</option>
                                </select>
                            </div>

                            <div class="select-wrapper">
                                <label for="filterStatusSelect">Trạng thái:</label>
                                <%-- value khớp đúng case trong Service: Active / Locked / Inactive --%>
                                <select id="filterStatusSelect" name="status" onchange="this.form.submit()">
                                    <option value="" ${empty accountData.oldStatus ? 'selected' : ''}>-- Tất cả trạng thái --</option>
                                    <option value="Active" ${accountData.oldStatus == 'Active' ? 'selected' : ''}>Hoạt động (Active)</option>
                                    <option value="Locked" ${accountData.oldStatus == 'Locked' ? 'selected' : ''}>Đã khóa (Locked)</option>
                                    <option value="Inactive" ${accountData.oldStatus == 'Inactive' ? 'selected' : ''}>Vô hiệu hoá (Inactive)</option>
                                </select>
                            </div>

                            <button type="submit" class="btn-primary" style="background:#555">
                                <i class="fa-solid fa-filter"></i> Lọc
                            </button>
                        </div>
                    </form>

                    <!-- BẢNG DỮ LIỆU CHÍNH -->
                    <table class="dashboard-simple-table" id="accountsMainTable">
                        <thead>
                            <tr>
                                <th>Mã KH/NV</th>
                                <th>Họ &amp; Tên</th>
                                <th>Thông Tin Liên Hệ</th>
                                <th class="text-center">Vai Trò</th>
                                <th class="text-center">Trạng Thái</th>
                                <th>Ngày Đăng Ký</th>
                                <th class="text-right" style="padding-right: 24px;">Hành Động</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%-- ĐÃ SỬA: accountData.list -> accountData.accounts
                                 (Service trả Map với key "accounts", không có key "list") --%>
                            <c:forEach var="item" items="${accountData.accounts}" varStatus="status">
                                <tr class="account-row-item" 
                                    data-id="${item.account.accountId}" 
                                    data-fullname="${item.fullName}" 
                                    data-phone="${item.phone}" 
                                    data-email="${item.account.email}" 
                                    data-role="${item.account.roleId}" 
                                    data-status="${item.account.isLocked ? 'Locked' : 'Active'}"
                                    data-created="${item.account.createdAt}">

                                    <td><code>#${item.account.accountId}</code></td>

                                    <td>
                                        <div class="user-cell-name">
                                            <div class="symbol-avatar">☕</div>
                                            <span class="font-bold text-dark-green">${item.fullName}</span>
                                        </div>
                                    </td>

                                    <td>
                                        <div class="contact-card-cell">
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

                                    <td class="text-center">
                                        <c:choose>
                                            <c:when test="${item.account.roleId == 1}">
                                                <span class="role-badge role-admin"><i class="fa-solid fa-user-shield"></i> ${item.roleName}</span>
                                            </c:when>
                                            <c:when test="${item.account.roleId == 3 || item.account.roleId == 4 || item.account.roleId == 5}">
                                                <span class="role-badge role-employee"><i class="fa-solid fa-user-tie"></i> ${item.roleName}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="role-badge role-customer"><i class="fa-solid fa-user"></i> ${item.roleName}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td class="text-center">
                                        <c:choose>
                                            <c:when test="${item.account.isLocked}">
                                                <span class="status-badge status-cancelled">Đã khóa</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-badge status-completed">Hoạt động</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td class="text-slate font-medium">
                                        <fmt:formatDate value="${item.createdAtDate}" pattern="dd/MM/yyyy HH:mm"/>
                                    </td>

                                    <td class="text-right action-column-buttons" style="padding-right: 24px;">
                                        <button class="action-btn btn-edit" title="Sửa thông tin" 
                                                onclick="openEditModal('${item.account.accountId}', '${item.fullName}', '${item.phone}', '${item.account.email}', '${item.account.roleId}', '${item.account.isLocked}')">
                                            <i class="fa-regular fa-pen-to-square"></i>
                                        </button>

                                        <c:choose>
                                            <c:when test="${!item.account.isLocked}">
                                                <%-- LƯU Ý: action=delete trong Servlet thật gọi dao.deleteAccount()
                                                     => set IsDeleted=1 (XOÁ MỀM), không phải "khóa" (IsLocked).
                                                     Đổi đúng icon/label/method (POST thay vì <a href> GET) cho khớp Servlet --%>
                                                <form method="post" action="${pageContext.request.contextPath}/admin/accounts" style="display:inline">
                                                    <input type="hidden" name="action" value="delete" />
                                                    <input type="hidden" name="accountId" value="${item.account.accountId}" />
                                                    <input type="hidden" name="roleId" value="${item.account.roleId}" />
                                                    <button type="submit" class="action-btn btn-lock" title="Xoá tài khoản (xoá mềm)"
                                                            onclick="return confirm('Bạn chắc chắn muốn XOÁ tài khoản này? Hành động này sẽ ẩn tài khoản khỏi hệ thống.')">
                                                        <i class="fa-solid fa-trash"></i>
                                                    </button>
                                                </form>
                                            </c:when>
                                            <c:otherwise>
                                                <%-- ĐÃ SỬA: action=update phải POST (doGet trong Servlet chỉ render dashboard) --%>
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
                                                    <button type="submit" class="action-btn btn-unlock" title="Mở khóa tài khoản">
                                                        <i class="fa-solid fa-lock"></i>
                                                    </button>
                                                </form>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>

                            <c:if test="${empty accountData.accounts}">
                                <tr>
                                    <td colspan="7" style="text-align:center;color:#aaa;padding:30px 0">
                                        Không tìm thấy tài khoản thành viên nào khớp với bộ lọc điều kiện!
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>

                    <%-- PHÂN TRANG — giữ nguyên search/roleId/status hiện tại khi đổi trang --%>
                    <c:if test="${accountData.totalPages > 1}">
                        <div style="display:flex;gap:6px;justify-content:flex-end;margin:14px 0;font-size:13px">
                            <c:forEach begin="1" end="${accountData.totalPages}" var="p">
                                <a href="${pageContext.request.contextPath}/admin/accounts?page=${p}&search=${accountData.oldSearch}&roleId=${accountData.oldRoleId}&status=${accountData.oldStatus}"
                                   style="padding:6px 12px;border:1px solid #e2e2e2;border-radius:6px;text-decoration:none;
                                          ${p == accountData.currentPage ? 'background:#006644;color:#fff;border-color:#006644;' : 'color:#555;'}">
                                    ${p}
                                </a>
                            </c:forEach>
                        </div>
                    </c:if>

                    <div class="table-results-status" id="noResultsText" style="display:none; padding: 30px; text-align: center; color: #79857f; font-weight: 600;">
                        <i class="fa-solid fa-face-frown" style="font-size: 24px; margin-bottom: 10px; display: block; color: var(--accent-gold);"></i>
                        Không tìm thấy tài khoản thành viên nào khớp với bộ lọc điều kiện!
                    </div>
                </div>
            </main>
        </div>

        <!-- =======================================================
             1. MODAL BOX: THÊM TÀI KHOẢN MỚI
             ======================================================= -->
        <div class="modal-backdrop" id="addAccountModal">
            <div class="modal-card">
                <div class="modal-card-header">
                    <h3><i class="fa-solid fa-user-plus text-primary-nav"></i> Thêm Thành Viên Mới</h3>
                    <span class="close-modal-x" onclick="closeAddModal()">&times;</span>
                </div>
                <form action="${pageContext.request.contextPath}/admin/accounts" method="POST">
                    <input type="hidden" name="action" value="add">

                    <div class="modal-card-body">
                        <div class="form-group-row">
                            <div class="form-field">
                                <label class="form-label">Họ &amp; Tên <span class="required">*</span></label>
                                <input type="text" name="fullName" placeholder="Nhập tên đầy đủ (ví dụ: Lê Anh Tuấn)" required class="form-input">
                            </div>
                        </div>

                        <div class="form-group-row grid-2-col">
                            <div class="form-field">
                                <label class="form-label">Tên Đăng Nhập / Email <span class="required">*</span></label>
                                <input type="email" name="email" placeholder="email@basalthouse.com" required class="form-input">
                            </div>
                            <div class="form-field">
                                <label class="form-label">Số Điện Thoại <span class="required">*</span></label>
                                <input type="tel" name="phone" placeholder="Nhập số điện thoại di động" required class="form-input" pattern="[0-9]{10,}">
                            </div>
                        </div>

                        <div class="form-group-row grid-2-col">
                            <div class="form-field">
                                <label class="form-label">Mật Khẩu <span class="required">*</span></label>
                                <input type="password" name="password" placeholder="Tối thiểu 6 ký tự" required class="form-input">
                            </div>
                            <div class="form-field">
                                <label class="form-label">Phân Vai Trò <span class="required">*</span></label>
                                <%-- ĐÃ SỬA: value phải đúng RoleId thật trong DB (1=Admin,2=Customer,3=Staff,4=Cashier,5=Shipper).
                                     Bản gốc có value="3" gắn nhãn "Employee" dễ gây hiểu lầm khi addAccount thực sự
                                     ghi vào bảng Staffs (RoleId=3), không phải 1 role "Employee" chung. --%>
                                <select name="roleId" required class="form-select">
                                    <option value="2">Customer (Khách hàng)</option>
                                    <option value="3">Staff (Nhân viên)</option>
                                    <option value="4">Cashier (Thu ngân)</option>
                                    <option value="5">Shipper (Giao hàng)</option>
                                    <option value="1">Admin (Quản trị viên)</option>
                                </select>
                            </div>
                        </div>

                        <div class="form-group-row">
                            <div class="form-field">
                                <label class="form-label">Trạng Thái Truy Cập <span class="required">*</span></label>
                                <select name="isActive" required class="form-select">
                                    <option value="true">Hoạt động (Cho phép login)</option>
                                    <option value="false">Đã khóa (Tạm ngừng login)</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    <div class="modal-card-footer">
                        <button type="button" class="btn-cancel" onclick="closeAddModal()">Hủy Bỏ</button>
                        <button type="submit" class="btn-submit-action">Lưu Tài Khoản</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- =======================================================
             2. MODAL BOX: CẬP NHẬT TÀI KHOẢN (EDIT)
             ======================================================= -->
        <div class="modal-backdrop" id="editAccountModal">
            <div class="modal-card">
                <div class="modal-card-header">
                    <h3><i class="fa-solid fa-user-pen text-primary-nav"></i> Cập Nhật Thông Tin</h3>
                    <span class="close-modal-x" onclick="closeEditModal()">&times;</span>
                </div>
                <form action="${pageContext.request.contextPath}/admin/accounts" method="POST">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="accountId" id="edit_id">
                    <%-- ĐÃ THÊM: oldRoleId bị thiếu trong bản gốc — Service cần oldRoleId
                         để biết có đổi role hay không (so sánh oldRoleId với roleId mới) --%>
                    <input type="hidden" name="oldRoleId" id="edit_oldRoleId">
                    <input type="hidden" name="isActive" value="true">

                    <div class="modal-card-body">
                        <div class="form-group-row">
                            <p class="edit-account-notice">Đang chỉnh sửa cho thành viên: <strong id="edit_notice_title">#ID</strong></p>
                        </div>

                        <div class="form-group-row">
                            <div class="form-field">
                                <label class="form-label">Họ &amp; Tên <span class="required">*</span></label>
                                <input type="text" name="fullName" id="edit_fullName" required class="form-input">
                            </div>
                        </div>

                        <div class="form-group-row grid-2-col">
                            <div class="form-field">
                                <label class="form-label">Tên Đăng Nhập / Email <span class="required">*</span></label>
                                <input type="email" name="email" id="edit_email" required class="form-input">
                            </div>
                            <div class="form-field">
                                <label class="form-label">Số Điện Thoại <span class="required">*</span></label>
                                <input type="tel" name="phone" id="edit_phone" required class="form-input" pattern="[0-9]{10,}">
                            </div>
                        </div>

                        <div class="form-group-row grid-2-col">
                            <div class="form-field">
                                <label class="form-label">Mật Khẩu <span class="text-slate">(Để trống nếu giữ nguyên)</span></label>
                                <input type="password" name="password" placeholder="Nhập mật khẩu mới" class="form-input">
                            </div>
                            <div class="form-field">
                                <label class="form-label">Phân Vai Trò <span class="required">*</span></label>
                                <select name="roleId" id="edit_role" required class="form-select">
                                    <option value="2">Customer (Khách hàng)</option>
                                    <option value="3">Staff (Nhân viên)</option>
                                    <option value="4">Cashier (Thu ngân)</option>
                                    <option value="5">Shipper (Giao hàng)</option>
                                    <option value="1">Admin (Quản trị viên)</option>
                                </select>
                            </div>
                        </div>

                        <div class="form-group-row">
                            <div class="form-field">
                                <label class="form-label">Trạng Thái Truy Cập <span class="required">*</span></label>
                                <select name="isLocked" id="edit_status" required class="form-select">
                                    <option value="false">Hoạt động (Cho phép login)</option>
                                    <option value="true">Đã khóa (Tạm ngừng login)</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    <div class="modal-card-footer">
                        <button type="button" class="btn-cancel" onclick="closeEditModal()">Hủy Bỏ</button>
                        <button type="submit" class="btn-submit-action">Lưu Thay Đổi</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- JAVASCRIPT ĐIỀU HÀNH THAO TÁC NGOÀI FRONTEND -->
        <script>
            // Cập nhật đồng hồ ở góc phải màn hình
            function updateTime() {
                const now = new Date();
                const hours = String(now.getHours()).padStart(2, '0');
                const minutes = String(now.getMinutes()).padStart(2, '0');
                const seconds = String(now.getSeconds()).padStart(2, '0');
                document.getElementById('current-time-txt').innerText = hours + ":" + minutes + ":" + seconds + " - Hôm nay";
            }
            setInterval(updateTime, 1000);
            updateTime();

            // Mở / Đóng modal Thêm tài khoản mới
            function openAddModal() {
                document.getElementById('addAccountModal').classList.add('active');
            }
            function closeAddModal() {
                document.getElementById('addAccountModal').classList.remove('active');
            }

            // Mở / Đóng modal Sửa tài khoản
            function openEditModal(id, fullName, phone, email, roleId, isLocked) {
                document.getElementById('edit_id').value = id;
                document.getElementById('edit_oldRoleId').value = roleId; // ĐÃ THÊM: lưu role hiện tại để Service so sánh
                document.getElementById('edit_notice_title').innerText = "Thành viên #" + id;
                document.getElementById('edit_fullName').value = fullName;
                document.getElementById('edit_phone').value = phone;
                document.getElementById('edit_email').value = email;
                document.getElementById('edit_role').value = roleId;

                let lockStatus = isLocked === true || isLocked === 'true' || isLocked === 1 ? 'true' : 'false';
                document.getElementById('edit_status').value = lockStatus;

                document.getElementById('editAccountModal').classList.add('active');
            }
            function closeEditModal() {
                document.getElementById('editAccountModal').classList.remove('active');
            }

            // ĐÃ XOÁ: hàm filterAccountsTable() cũ (lọc client-side bị sai logic so sánh).
            // Tìm kiếm/lọc giờ chạy SERVER-SIDE qua <form method="get"> submit thẳng
            // tới Servlet — Service đã xử lý đúng search/roleId/status/page.
        </script>
    </body>
</html>
