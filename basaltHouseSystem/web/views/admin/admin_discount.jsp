<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản Lý Khuyến Mãi - BasaltHouse</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_common.css?v=2">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_account.css?v=2">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_discount.css?v=1.0">
    </head>
    <body class="admin-dashboard-body">

        <jsp:include page="header.jsp" />

        <div class="app-container">

            <jsp:include page="sidebar.jsp" />

            <main class="main-content">

                <div class="viewport-headline-bar">
                    <div class="headline-left">
                        <h1 class="page-title">Quản Lý Khuyến Mãi</h1>
                        <p class="page-desc">Tạo và quản lý các chương trình ưu đãi, mã giảm giá phần trăm hoặc số tiền trực tiếp áp dụng cho cửa hàng.</p>
                    </div>
                    <div class="user-avatar-wrapper">
                        <div class="avatar-letter">A</div>
                        <div class="user-info">
                            <span class="user-name">${sessionScope.adminName != null ? sessionScope.adminName : "Quản trị viên"}</span>
                            <span class="user-status-dot">● Trực tuyến</span>
                        </div>
                    </div>
                </div>

                <!-- Thống kê nhanh -->
                <div class="account-overview-grid">
                    <div class="overview-card-item">
                        <div class="card-icon-wrapper" style="background-color: var(--basalt-purple-light); color: var(--basalt-purple);">
                            <i class="fa-solid fa-ticket"></i>
                        </div>
                        <div class="card-metrics-data">
                            <span class="metric-label">TỔNG SỐ MÃ</span>
                            <span class="metric-count">${stats['total']}</span>
                        </div>
                    </div>
                    <div class="overview-card-item">
                        <div class="card-icon-wrapper" style="background-color: var(--basalt-emerald-light); color: var(--basalt-emerald);">
                            <i class="fa-solid fa-circle-check"></i>
                        </div>
                        <div class="card-metrics-data">
                            <span class="metric-label">ĐANG KÍCH HOẠT</span>
                            <span class="metric-count" style="color: var(--basalt-emerald);">${stats['active']}</span>
                        </div>
                    </div>
                    <div class="overview-card-item">
                        <div class="card-icon-wrapper" style="background-color: var(--basalt-amber-light); color: var(--basalt-amber);">
                            <i class="fa-solid fa-hourglass-half"></i>
                        </div>
                        <div class="card-metrics-data">
                            <span class="metric-label">SẮP HẾT HẠN</span>
                            <span class="metric-count" style="color: var(--basalt-amber);">${stats['expiringSoon']}</span>
                        </div>
                    </div>
                    <div class="overview-card-item">
                        <div class="card-icon-wrapper" style="background-color: var(--basalt-red-light); color: var(--basalt-red);">
                            <i class="fa-solid fa-clock-rotate-left"></i>
                        </div>
                        <div class="card-metrics-data">
                            <span class="metric-label">HẾT HẠN / KHÓA</span>
                            <span class="metric-count" style="color: var(--basalt-red);">${stats['expiredOrInactive']}</span>
                        </div>
                    </div>
                </div>

                <div class="data-management-panel">
                    <div class="panel-header-toolbar">
                        <h2>Danh Sách Mã Khuyến Mãi (Discount Codes)</h2>
                        <div class="toolbar-actions-group">
                            <button class="btn-primary-action" onclick="openAddModal()">
                                <i class="fa-solid fa-plus-circle"></i> Thêm Mã Khuyến Mãi Mới
                            </button>
                        </div>
                    </div>

                    <!-- Bộ lọc và Tìm kiếm -->
                    <form method="GET" action="${pageContext.request.contextPath}/admin/discounts" id="filterForm">
                        <div class="search-filter-belt">
                            <div class="search-input-box">
                                <input type="text" id="searchDiscountInput" name="search" placeholder="Tìm mã code hoặc mô tả..." value="${search != null ? search : ''}">
                            </div>

                            <div class="filter-controls-group">
                                <div class="select-wrapper">
                                    <label for="filterTypeSelect">Loại giảm giá:</label>
                                    <select id="filterTypeSelect" name="filterType" class="filter-select" onchange="document.getElementById('filterForm').submit()">
                                        <option value="ALL" ${filterType == 'ALL' ? 'selected' : ''}>-- Tất cả loại --</option>
                                        <option value="PERCENT" ${filterType == 'PERCENT' ? 'selected' : ''}>Phần trăm (%)</option>
                                        <option value="AMOUNT" ${filterType == 'AMOUNT' ? 'selected' : ''}>Số tiền (đ)</option>
                                    </select>
                                </div>
                                <div class="select-wrapper">
                                    <label for="filterStatusSelect">Trạng thái:</label>
                                    <select id="filterStatusSelect" name="filterStatus" class="filter-select" onchange="document.getElementById('filterForm').submit()">
                                        <option value="ALL" ${filterStatus == 'ALL' ? 'selected' : ''}>-- Tất cả trạng thái --</option>
                                        <option value="ACTIVE" ${filterStatus == 'ACTIVE' ? 'selected' : ''}>Đang hoạt động</option>
                                        <option value="INACTIVE" ${filterStatus == 'INACTIVE' ? 'selected' : ''}>Ngừng hoạt động</option>
                                        <option value="EXPIRED" ${filterStatus == 'EXPIRED' ? 'selected' : ''}>Đã hết hạn</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                    </form>

                    <!-- Bảng danh sách -->
                    <div class="responsive-table-wrapper">
                        <table class="basalt-custom-table" id="discountsMainTable">
                            <thead>
                                <tr>
                                    <th>Mã giảm giá</th>
                                    <th>Loại ưu đãi</th>
                                    <th>Giá trị giảm</th>
                                    <th>Thời gian áp dụng</th>
                                    <th>Trạng thái</th>
                                    <th>Mô tả</th>
                                    <th style="text-align: right; padding-right: 24px;">Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty discounts}">
                                        <tr>
                                            <td colspan="7" style="text-align: center; padding: 48px 0; color: var(--text-secondary);">
                                                <i class="fa-solid fa-ticket" style="font-size: 2rem; margin-bottom: 12px; display: block; opacity: 0.3;"></i>
                                                Chưa có mã khuyến mãi nào phù hợp.
                                            </td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="d" items="${discounts}">
                                            <tr class="discount-row-item">
                                                <td>
                                                    <div class="code-chip">
                                                        <span>${d.code}</span>
                                                        <i class="fa-regular fa-copy copy-hint"></i>
                                                    </div>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${d.discountTypeName == 'PERCENT'}">
                                                            <span class="discount-type-badge type-percent">
                                                                <i class="fa-solid fa-percent"></i> Phần trăm
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="discount-type-badge type-amount">
                                                                <i class="fa-solid fa-money-bill-1-wave"></i> Số tiền mặt
                                                            </span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td class="discount-value-cell">
                                                    ${d.discountValueFormatted}
                                                </td>
                                                <td>
                                                    <div class="history-time">Bắt đầu: ${d.startDateFormatted}</div>
                                                    <div class="history-time">Kết thúc: ${d.endDateFormatted}</div>
                                                    <c:if test="${d.endDate != null}">
                                                        <c:choose>
                                                            <c:when test="${d.totalDay < 0}">
                                                                <div class="expiry-countdown expiry-danger">
                                                                    <i class="fa-solid fa-circle-xmark"></i> Đã hết hạn
                                                                </div>
                                                            </c:when>
                                                            <c:when test="${d.totalDay <= 7}">
                                                                <div class="expiry-countdown expiry-danger">
                                                                    <i class="fa-solid fa-triangle-exclamation"></i> Còn ${d.totalDay} ngày
                                                                </div>
                                                            </c:when>
                                                            <c:when test="${d.totalDay <= 30}">
                                                                <div class="expiry-countdown expiry-warn">
                                                                    <i class="fa-regular fa-clock"></i> Còn ${d.totalDay} ngày
                                                                </div>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <div class="expiry-countdown expiry-ok">
                                                                    <i class="fa-regular fa-clock"></i> Còn ${d.totalDay} ngày
                                                                </div>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:if>
                                                </td>
                                                <td>
                                                    <div class="toggle-switch-wrap">
                                                        <label class="toggle-switch">
                                                            <input type="checkbox" ${d.isActive ? 'checked' : ''} disabled>
                                                            <span class="toggle-slider"></span>
                                                        </label>
                                                        <c:choose>
                                                            <c:when test="${d.isActive}">
                                                                <span class="status-badge badge-active">Kích hoạt</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="status-badge badge-inactive">Ngừng hoạt động</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </td>
                                                <td style="max-width: 280px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;" title="${d.description}">
                                                    <c:out value="${d.description}" default="--"/>
                                                </td>
                                                <td class="row-actions-cell" style="padding-right: 24px;">
                                                    <button class="btn-icon-action action-edit" title="Chỉnh sửa mã"
                                                            onclick="openEditModal(
                                                                '${d.discountId}',
                                                                '${d.code}',
                                                                '${d.discountTypeName}',
                                                                '${d.discountTypeName == "PERCENT" ? d.discountPercent : d.discountAmount}',
                                                                '${d.startDate}',
                                                                '${d.endDate}',
                                                                '${d.isActive}'
                                                            )">
                                                        <i class="fa-solid fa-pen-to-square"></i>
                                                    </button>
                                                    <button class="btn-icon-action action-delete" title="Xóa mã"
                                                            onclick="openDeleteModal('${d.discountId}', '${d.code}')">
                                                        <i class="fa-solid fa-trash-can"></i>
                                                    </button>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>

            </main>
        </div>

        <%-- Modal Add / Edit Discount --%>
        <div class="modal-backdrop" id="discountModal">
            <div class="modal-card">
                <div class="modal-card-header">
                    <h3 id="modalTitleText"><i class="fa-solid fa-ticket-simple"></i> Tạo mã khuyến mãi mới</h3>
                    <span class="close-modal-x" onclick="closeDiscountModal()">&times;</span>
                </div>
                <form id="discountForm" method="POST" action="${pageContext.request.contextPath}/admin/discounts">
                    <input type="hidden" name="action" id="formAction" value="add">
                    <input type="hidden" name="discountId" id="formDiscountId" value="">
                    <div class="modal-card-body">
                        <div class="modal-form-flex-stack">

                            <div class="form-grid-2">
                                <div class="form-field-group">
                                    <label for="formCode">Mã Code <span style="color:var(--basalt-red);">*</span></label>
                                    <input type="text" id="formCode" name="code" class="form-input-text"
                                           placeholder="Ví dụ: COFFEE50" required maxlength="50" style="text-transform: uppercase;">
                                </div>

                                <div class="form-field-group">
                                    <label>Loại hình ưu đãi <span style="color:var(--basalt-red);">*</span></label>
                                    <div class="discount-type-toggle">
                                        <button type="button" id="btnTypePercent" class="active" onclick="switchDiscountType('PERCENT')">
                                            <i class="fa-solid fa-percent"></i> Phần trăm (%)
                                        </button>
                                        <button type="button" id="btnTypeAmount" onclick="switchDiscountType('AMOUNT')">
                                            <i class="fa-solid fa-money-bill-1-wave"></i> Tiền mặt (đ)
                                        </button>
                                    </div>
                                    <input type="hidden" name="discountType" id="formDiscountType" value="PERCENT">
                                </div>
                            </div>

                            <div class="form-grid-2">
                                <div class="form-field-group type-section shown" id="groupPercentValue">
                                    <label for="formDiscountPercent">Phần trăm giảm (%) <span style="color:var(--basalt-red);">*</span></label>
                                    <input type="number" id="formDiscountPercent" name="discountPercent" class="form-input-text"
                                           min="1" max="100" placeholder="Từ 1 đến 100">
                                </div>
                                <div class="form-field-group type-section" id="groupAmountValue">
                                    <label for="formDiscountAmount">Số tiền giảm (VND) <span style="color:var(--basalt-red);">*</span></label>
                                    <input type="number" id="formDiscountAmount" name="discountAmount" class="form-input-text"
                                           min="1000" step="1000" placeholder="Ví dụ: 20000">
                                </div>
                            </div>

                            <div class="form-grid-2">
                                <div class="form-field-group">
                                    <label for="formStartDate">Thời gian bắt đầu <span style="color:var(--basalt-red);">*</span></label>
                                    <input type="datetime-local" id="formStartDate" name="startDate" class="form-input-text" required>
                                </div>
                                <div class="form-field-group">
                                    <label for="formEndDate">Thời gian kết thúc <span style="color:var(--basalt-red);">*</span></label>
                                    <input type="datetime-local" id="formEndDate" name="endDate" class="form-input-text" required>
                                </div>
                            </div>

                            <div class="form-field-group">
                                <label for="formDescription">Mô tả chương trình</label>
                                <textarea id="formDescription" name="description" class="form-input-text"
                                          rows="3" placeholder="Chi tiết nội dung khuyến mãi..." style="resize: none;" maxlength="250"></textarea>
                            </div>

                            <div class="form-field-group">
                                <div class="toggle-switch-wrap">
                                    <label class="toggle-switch">
                                        <input type="checkbox" id="formIsActive" name="isActive" checked>
                                        <span class="toggle-slider"></span>
                                    </label>
                                    <label for="formIsActive" style="font-weight: 600; cursor: pointer;">Cho phép hoạt động ngay</label>
                                </div>
                            </div>

                        </div>
                    </div>
                    <div class="modal-card-footer">
                        <button type="button" class="btn-cancel" onclick="closeDiscountModal()">Hủy</button>
                        <button type="submit" class="btn-submit">
                            <i class="fa-solid fa-circle-check"></i> Lưu thông tin
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <%-- Delete Modal --%>
        <div class="modal-backdrop" id="deleteConfirmModal">
            <div class="modal-card modal-sm">
                <div class="delete-confirm-body">
                    <div class="delete-confirm-icon">
                        <i class="fa-solid fa-trash-can"></i>
                    </div>
                    <h3>Xác nhận xóa mã khuyến mãi?</h3>
                    <p>Bạn có chắc chắn muốn xóa mã <strong id="deleteCodeLabel">CODE</strong> không?</p>
                    <form method="POST" action="${pageContext.request.contextPath}/admin/discounts">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="discountId" id="deleteDiscountId" value="">
                        <div class="delete-confirm-footer">
                            <button type="button" class="btn-cancel" onclick="closeDeleteModal()">Hủy</button>
                            <button type="submit" class="btn-danger">Xác nhận xóa</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script>
            const discountModal       = document.getElementById('discountModal');
            const deleteConfirmModal  = document.getElementById('deleteConfirmModal');

            function switchDiscountType(type) {
                const btnPercent  = document.getElementById('btnTypePercent');
                const btnAmount   = document.getElementById('btnTypeAmount');
                const formType    = document.getElementById('formDiscountType');
                const gPercent    = document.getElementById('groupPercentValue');
                const gAmount     = document.getElementById('groupAmountValue');
                const iPercent    = document.getElementById('formDiscountPercent');
                const iAmount     = document.getElementById('formDiscountAmount');

                const isPercent = type === 'PERCENT';
                btnPercent.classList.toggle('active', isPercent);
                btnAmount.classList.toggle('active', !isPercent);
                formType.value = type;
                gPercent.classList.toggle('shown', isPercent);
                gAmount.classList.toggle('shown', !isPercent);
                iPercent.required = isPercent;
                iAmount.required  = !isPercent;
            }

            function openAddModal() {
                document.getElementById('modalTitleText').innerHTML = '<i class="fa-solid fa-ticket-simple"></i> Tạo mã khuyến mãi mới';
                document.getElementById('formAction').value = 'add';
                document.getElementById('formDiscountId').value = '';
                document.getElementById('discountForm').reset();

                const now = new Date();
                const toLocal = d => new Date(d.getTime() - d.getTimezoneOffset() * 60000).toISOString().slice(0, 16);
                document.getElementById('formStartDate').value = toLocal(now);
                document.getElementById('formEndDate').value   = toLocal(new Date(now.getTime() + 30 * 86400000));

                switchDiscountType('PERCENT');
                openModal(discountModal);
            }

            function openEditModal(id, code, type, value, startDate, endDate, isActive) {
                document.getElementById('modalTitleText').innerHTML = '<i class="fa-solid fa-pen-to-square"></i> Chỉnh sửa mã khuyến mãi';
                document.getElementById('formAction').value     = 'update';
                document.getElementById('formDiscountId').value = id;
                document.getElementById('formCode').value       = code;
                document.getElementById('formIsActive').checked = (isActive === 'true');

                // Format LocalDateTime to datetime-local compatible string
                const fmtDate = s => s ? s.replace('T', 'T').substring(0, 16) : '';
                document.getElementById('formStartDate').value  = fmtDate(startDate);
                document.getElementById('formEndDate').value    = fmtDate(endDate);

                if (type === 'PERCENT') {
                    switchDiscountType('PERCENT');
                    document.getElementById('formDiscountPercent').value = value;
                } else {
                    switchDiscountType('AMOUNT');
                    document.getElementById('formDiscountAmount').value = value;
                }
                openModal(discountModal);
            }

            function closeDiscountModal() { closeModal(discountModal); }

            function openDeleteModal(id, code) {
                document.getElementById('deleteCodeLabel').textContent  = code;
                document.getElementById('deleteDiscountId').value       = id;
                openModal(deleteConfirmModal);
            }

            function closeDeleteModal() { closeModal(deleteConfirmModal); }

            function openModal(el) {
                el.style.display = 'flex';
                el.offsetHeight;
                el.classList.add('active');
            }

            function closeModal(el) {
                el.classList.remove('active');
                setTimeout(() => { el.style.display = 'none'; }, 250);
            }
        </script>
    </body>
</html>
