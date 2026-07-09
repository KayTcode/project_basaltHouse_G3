<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

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

                <!-- Thống kê nhanh Khuyến mãi -->
                <div class="account-overview-grid">
                    <div class="overview-card-item">
                        <div class="card-icon-wrapper" style="background-color: var(--basalt-purple-light); color: var(--basalt-purple);">
                            <i class="fa-solid fa-ticket"></i>
                        </div>
                        <div class="card-metrics-data">
                            <span class="metric-label">TỔNG SỐ MÃ</span>
                            <span class="metric-count">3</span>
                        </div>
                    </div>

                    <div class="overview-card-item">
                        <div class="card-icon-wrapper" style="background-color: var(--basalt-emerald-light); color: var(--basalt-emerald);">
                            <i class="fa-solid fa-circle-check"></i>
                        </div>
                        <div class="card-metrics-data">
                            <span class="metric-label">ĐANG KÍCH HOẠT</span>
                            <span class="metric-count" style="color: var(--basalt-emerald);">2</span>
                        </div>
                    </div>

                    <div class="overview-card-item">
                        <div class="card-icon-wrapper" style="background-color: var(--basalt-amber-light); color: var(--basalt-amber);">
                            <i class="fa-solid fa-hourglass-half"></i>
                        </div>
                        <div class="card-metrics-data">
                            <span class="metric-label">SẮP HẾT HẠN</span>
                            <span class="metric-count" style="color: var(--basalt-amber);">1</span>
                        </div>
                    </div>

                    <div class="overview-card-item">
                        <div class="card-icon-wrapper" style="background-color: var(--basalt-red-light); color: var(--basalt-red);">
                            <i class="fa-solid fa-clock-rotate-left"></i>
                        </div>
                        <div class="card-metrics-data">
                            <span class="metric-label">HẾT HẠN / KHÓA</span>
                            <span class="metric-count" style="color: var(--basalt-red);">1</span>
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

                    <!-- Tìm kiếm & Lọc -->
                    <div class="search-filter-belt">
                        <div class="search-input-box">
                            <input type="text" id="searchDiscountInput" placeholder="Tìm mã code hoặc mô tả...">
                        </div>

                        <div class="filter-controls-group">
                            <div class="select-wrapper">
                                <label for="filterTypeSelect">Loại giảm giá:</label>
                                <select id="filterTypeSelect" class="filter-select">
                                    <option value="ALL">-- Tất cả loại --</option>
                                    <option value="PERCENT">Phần trăm (%)</option>
                                    <option value="AMOUNT">Số tiền (đ)</option>
                                </select>
                            </div>

                            <div class="select-wrapper">
                                <label for="filterStatusSelect">Trạng thái:</label>
                                <select id="filterStatusSelect" class="filter-select">
                                    <option value="ALL">-- Tất cả trạng thái --</option>
                                    <option value="ACTIVE">Đang hoạt động</option>
                                    <option value="INACTIVE">Ngừng hoạt động</option>
                                    <option value="EXPIRED">Đã hết hạn</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    <!-- Bảng danh sách mã khuyến mãi -->
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
                            <tbody id="discountsTableBody">
                                <!-- Mock Row 1 -->
                                <tr class="discount-row-item">
                                    <td>
                                        <div class="code-chip">
                                            <span>WELCOME10</span>
                                            <i class="fa-regular fa-copy copy-hint"></i>
                                        </div>
                                    </td>
                                    <td>
                                        <span class="discount-type-badge type-percent">
                                            <i class="fa-solid fa-percent"></i> Phần trăm
                                        </span>
                                    </td>
                                    <td class="discount-value-cell">10%</td>
                                    <td>
                                        <div class="history-time">Bắt đầu: 29/05/2026 20:39</div>
                                        <div class="history-time">Kết thúc: 28/07/2026 20:39</div>
                                        <div class="expiry-countdown expiry-ok"><i class="fa-regular fa-clock"></i> Còn 19 ngày</div>
                                    </td>
                                    <td>
                                        <div class="toggle-switch-wrap">
                                            <label class="toggle-switch">
                                                <input type="checkbox" checked>
                                                <span class="toggle-slider"></span>
                                            </label>
                                            <span class="status-badge badge-active">Kích hoạt</span>
                                        </div>
                                    </td>
                                    <td style="max-width: 280px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;" title="Giảm 10% tổng hóa đơn cho khách hàng mới đăng ký tài khoản.">
                                        Giảm 10% tổng hóa đơn cho khách hàng mới đăng ký tài khoản.
                                    </td>
                                    <td class="row-actions-cell" style="padding-right: 24px;">
                                        <button class="btn-icon-action action-edit" onclick="openEditModal('WELCOME10', 'PERCENT', 10, '2026-05-29T20:39', '2026-07-28T20:39', 'Giảm 10% tổng hóa đơn cho khách hàng mới đăng ký tài khoản.', true)" title="Chỉnh sửa mã">
                                            <i class="fa-solid fa-pen-to-square"></i>
                                        </button>
                                        <button class="btn-icon-action action-delete" onclick="openDeleteModal('WELCOME10')" title="Xóa mã">
                                            <i class="fa-solid fa-trash-can"></i>
                                        </button>
                                    </td>
                                </tr>

                                <!-- Mock Row 2 -->
                                <tr class="discount-row-item">
                                    <td>
                                        <div class="code-chip">
                                            <span>FREESHIP20K</span>
                                            <i class="fa-regular fa-copy copy-hint"></i>
                                        </div>
                                    </td>
                                    <td>
                                        <span class="discount-type-badge type-amount">
                                            <i class="fa-solid fa-money-bill-1-wave"></i> Số tiền mặt
                                        </span>
                                    </td>
                                    <td class="discount-value-cell">20,000đ</td>
                                    <td>
                                        <div class="history-time">Bắt đầu: 01/06/2026 00:00</div>
                                        <div class="history-time">Kết thúc: 11/07/2026 23:59</div>
                                        <div class="expiry-countdown expiry-danger"><i class="fa-solid fa-triangle-exclamation"></i> Hết hạn sau 2 ngày</div>
                                    </td>
                                    <td>
                                        <div class="toggle-switch-wrap">
                                            <label class="toggle-switch">
                                                <input type="checkbox" checked>
                                                <span class="toggle-slider"></span>
                                            </label>
                                            <span class="status-badge badge-active">Kích hoạt</span>
                                        </div>
                                    </td>
                                    <td style="max-width: 280px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;" title="Tặng voucher 20k ship cho khách hàng VIP của tháng.">
                                        Tặng voucher 20k ship cho khách hàng VIP của tháng.
                                    </td>
                                    <td class="row-actions-cell" style="padding-right: 24px;">
                                        <button class="btn-icon-action action-edit" onclick="openEditModal('FREESHIP20K', 'AMOUNT', 20000, '2026-06-01T00:00', '2026-07-11T23:59', 'Tặng voucher 20k ship cho khách hàng VIP của tháng.', true)" title="Chỉnh sửa mã">
                                            <i class="fa-solid fa-pen-to-square"></i>
                                        </button>
                                        <button class="btn-icon-action action-delete" onclick="openDeleteModal('FREESHIP20K')" title="Xóa mã">
                                            <i class="fa-solid fa-trash-can"></i>
                                        </button>
                                    </td>
                                </tr>

                                <!-- Mock Row 3 -->
                                <tr class="discount-row-item">
                                    <td>
                                        <div class="code-chip">
                                            <span>SUMMER2026</span>
                                            <i class="fa-regular fa-copy copy-hint"></i>
                                        </div>
                                    </td>
                                    <td>
                                        <span class="discount-type-badge type-percent">
                                            <i class="fa-solid fa-percent"></i> Phần trăm
                                        </span>
                                    </td>
                                    <td class="discount-value-cell">15%</td>
                                    <td>
                                        <div class="history-time">Bắt đầu: 15/07/2026 00:00</div>
                                        <div class="history-time">Kết thúc: 31/08/2026 23:59</div>
                                        <div class="expiry-countdown expiry-warn"><i class="fa-regular fa-calendar"></i> Chưa diễn ra</div>
                                    </td>
                                    <td>
                                        <div class="toggle-switch-wrap">
                                            <label class="toggle-switch">
                                                <input type="checkbox">
                                                <span class="toggle-slider"></span>
                                            </label>
                                            <span class="status-badge badge-inactive">Ngừng hoạt động</span>
                                        </div>
                                    </td>
                                    <td style="max-width: 280px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;" title="Sự kiện chào hè giảm 15% tổng bill từ ngày 15/07.">
                                        Sự kiện chào hè giảm 15% tổng bill từ ngày 15/07.
                                    </td>
                                    <td class="row-actions-cell" style="padding-right: 24px;">
                                        <button class="btn-icon-action action-edit" onclick="openEditModal('SUMMER2026', 'PERCENT', 15, '2026-07-15T00:00', '2026-08-31T23:59', 'Sự kiện chào hè giảm 15% tổng bill từ ngày 15/07.', false)" title="Chỉnh sửa mã">
                                            <i class="fa-solid fa-pen-to-square"></i>
                                        </button>
                                        <button class="btn-icon-action action-delete" onclick="openDeleteModal('SUMMER2026')" title="Xóa mã">
                                            <i class="fa-solid fa-trash-can"></i>
                                        </button>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <!-- Pagination -->
                    <div class="pagination-bar">
                        <a href="#" class="prev-page"><i class="fa-solid fa-angle-left"></i> Trước</a>
                        <a href="#" class="active">1</a>
                        <a href="#">Sau <i class="fa-solid fa-angle-right"></i></a>
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
                                    <input type="text" id="formCode" name="code" class="form-input-text" placeholder="Ví dụ: COFFEE50" required maxlength="50" style="text-transform: uppercase;">
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

                            <div class="form-field-group">
                                <!-- Group Percent Value -->
                                <div class="form-field-group type-section shown" id="groupPercentValue">
                                    <label for="formDiscountPercent">Phần trăm giảm (%) <span style="color:var(--basalt-red);">*</span></label>
                                    <input type="number" id="formDiscountPercent" name="discountPercent" class="form-input-text" min="1" max="100" placeholder="Từ 1 đến 100" value="10">
                                </div>

                                <!-- Group Amount Value -->
                                <div class="form-field-group type-section" id="groupAmountValue">
                                    <label for="formDiscountAmount">Số tiền giảm (VND) <span style="color:var(--basalt-red);">*</span></label>
                                    <input type="number" id="formDiscountAmount" name="discountAmount" class="form-input-text" min="1000" step="1000" placeholder="Ví dụ: 20000">
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
                                <textarea id="formDescription" name="description" class="form-input-text" rows="3" placeholder="Chi tiết nội dung khuyến mãi..." style="resize: none;" maxlength="250"></textarea>
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

        <%-- Delete Confirmation Modal --%>
        <div class="modal-backdrop" id="deleteConfirmModal">
            <div class="modal-card modal-sm">
                <div class="delete-confirm-body">
                    <div class="delete-confirm-icon">
                        <i class="fa-solid fa-trash-can"></i>
                    </div>
                    <h3>Xác nhận xóa mã khuyến mãi?</h3>
                    <p>Bạn có chắc chắn muốn xóa mã <strong id="deleteCodeLabel">CODE</strong> không? Hành động này sẽ thực hiện ẩn hoặc xóa mềm mã này khỏi danh sách sử dụng.</p>
                    <div class="delete-confirm-footer">
                        <button class="btn-cancel" onclick="closeDeleteModal()">Hủy</button>
                        <button class="btn-danger" onclick="submitDeleteDiscount()">Xác nhận xóa</button>
                    </div>
                </div>
            </div>
        </div>

        <script>
            const discountModal = document.getElementById('discountModal');
            const deleteConfirmModal = document.getElementById('deleteConfirmModal');

            function switchDiscountType(type) {
                const btnPercent = document.getElementById('btnTypePercent');
                const btnAmount = document.getElementById('btnTypeAmount');
                const formType = document.getElementById('formDiscountType');
                
                const groupPercent = document.getElementById('groupPercentValue');
                const groupAmount = document.getElementById('groupAmountValue');
                
                const inputPercent = document.getElementById('formDiscountPercent');
                const inputAmount = document.getElementById('formDiscountAmount');

                if (type === 'PERCENT') {
                    btnPercent.classList.add('active');
                    btnAmount.classList.remove('active');
                    formType.value = 'PERCENT';
                    
                    groupPercent.classList.add('shown');
                    groupAmount.classList.remove('shown');
                    
                    inputPercent.required = true;
                    inputAmount.required = false;
                } else {
                    btnPercent.classList.remove('active');
                    btnAmount.classList.add('active');
                    formType.value = 'AMOUNT';
                    
                    groupPercent.classList.remove('shown');
                    groupAmount.classList.add('shown');
                    
                    inputPercent.required = false;
                    inputAmount.required = true;
                }
            }

            function openAddModal() {
                document.getElementById('modalTitleText').innerHTML = '<i class="fa-solid fa-ticket-simple"></i> Tạo mã khuyến mãi mới';
                document.getElementById('formAction').value = 'add';
                document.getElementById('discountForm').reset();
                
                const now = new Date();
                const localNow = new Date(now.getTime() - now.getTimezoneOffset() * 60000).toISOString().slice(0, 16);
                const oneMonthLater = new Date(now.getTime() + 30 * 24 * 60 * 60 * 1000);
                const localOneMonthLater = new Date(oneMonthLater.getTime() - oneMonthLater.getTimezoneOffset() * 60000).toISOString().slice(0, 16);
                
                document.getElementById('formStartDate').value = localNow;
                document.getElementById('formEndDate').value = localOneMonthLater;

                switchDiscountType('PERCENT');
                openModal(discountModal);
            }

            function openEditModal(code, type, value, startDate, endDate, desc, isActive) {
                document.getElementById('modalTitleText').innerHTML = '<i class="fa-solid fa-pen-to-square"></i> Chỉnh sửa mã khuyến mãi';
                document.getElementById('formAction').value = 'update';

                document.getElementById('formCode').value = code;
                document.getElementById('formStartDate').value = startDate;
                document.getElementById('formEndDate').value = endDate;
                document.getElementById('formDescription').value = desc;
                document.getElementById('formIsActive').checked = isActive;

                if (type === 'PERCENT') {
                    switchDiscountType('PERCENT');
                    document.getElementById('formDiscountPercent').value = value;
                } else {
                    switchDiscountType('AMOUNT');
                    document.getElementById('formDiscountAmount').value = value;
                }

                openModal(discountModal);
            }

            // Close Main Modal
            function closeDiscountModal() {
                closeModal(discountModal);
            }

            // Open Delete Modal
            function openDeleteModal(code) {
                document.getElementById('deleteCodeLabel').textContent = code;
                openModal(deleteConfirmModal);
            }

            // Close Delete Modal
            function closeDeleteModal() {
                closeModal(deleteConfirmModal);
            }

            function openModal(modalEl) {
                modalEl.style.display = 'flex';
                modalEl.offsetHeight; // trigger reflow
                modalEl.classList.add('active');
            }

            function closeModal(modalEl) {
                modalEl.classList.remove('active');
                setTimeout(() => {
                    modalEl.style.display = 'none';
                }, 250);
            }

            function submitDeleteDiscount() {
                closeDeleteModal();
            }
        </script>
    </body>
</html>
