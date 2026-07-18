<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Quản lý Vùng Giao Hàng — Admin | BasaltHouse</title>

    <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />

    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_common.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_order.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_delivery_zone.css" />
</head>
<body class="admin-dashboard-body">

    <%@ include file="/views/admin/header.jsp" %>

    <div class="app-container">
        <%@ include file="/views/admin/sidebar.jsp" %>

        <main class="main-content">
            <c:if test="${not empty sessionScope.successMsg}">
                <div class="toast toast-ok" id="toastBox">${sessionScope.successMsg}</div>
                <c:remove var="successMsg" scope="session" />
            </c:if>
            <c:if test="${not empty sessionScope.errorMsg}">
                <div class="toast toast-err" id="toastBox">${sessionScope.errorMsg}</div>
                <c:remove var="errorMsg" scope="session" />
            </c:if>

            <div class="page-header">
                <div>
                    <h1>📍 Quản lý Vùng Giao Hàng</h1>
                    <p class="page-desc">Thiết lập các phường/xã, quận/huyện được hỗ trợ giao hàng</p>
                </div>
            </div>

            <!-- KPI Cards -->
            <div class="kpi-row">
                <div class="kpi-card">
                    <div class="kpi-label">Tổng vùng giao</div>
                    <div class="kpi-value">${stats.total}</div>
                </div>
                <div class="kpi-card kpi-active">
                    <div class="kpi-label">Đang hoạt động</div>
                    <div class="kpi-value">${stats.active}</div>
                </div>
                <div class="kpi-card">
                    <div class="kpi-label">Tạm ngừng</div>
                    <div class="kpi-value">${stats.inactive}</div>
                </div>
            </div>

            <!-- Filters -->
            <form action="${pageContext.request.contextPath}/admin/shipping-zones" method="get" class="filter-bar">
                <select name="province" class="filter-select" onchange="this.form.submit()">
                    <option value="">Tất cả tỉnh/thành</option>
                    <c:forEach var="p" items="${provinces}">
                        <option value="${p}" ${oldProvince eq p ? 'selected' : ''}>${p}</option>
                    </c:forEach>
                </select>
                <select name="district" class="filter-select" onchange="this.form.submit()">
                    <option value="">Tất cả quận/huyện</option>
                    <c:forEach var="d" items="${districts}">
                        <option value="${d}" ${oldDistrict eq d ? 'selected' : ''}>${d}</option>
                    </c:forEach>
                </select>
                <select name="isActive" class="filter-select" onchange="this.form.submit()">
                    <option value="">Tất cả trạng thái</option>
                    <option value="true" ${oldIsActive eq 'true' ? 'selected' : ''}>Đang hoạt động</option>
                    <option value="false" ${oldIsActive eq 'false' ? 'selected' : ''}>Tạm ngừng</option>
                </select>
                <button type="button" class="btn-add" onclick="openAddModal()">
                    <i class="fa-solid fa-plus"></i> Thêm vùng mới
                </button>
            </form>

            <!-- Table -->
            <div class="table-card">
                <table>
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Phường/Xã</th>
                            <th>Quận/Huyện</th>
                            <th>Tỉnh/Thành</th>
                            <th>Trạng thái</th>
                            <th>Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="z" items="${zones}" varStatus="st">
                            <tr>
                                <td>${st.index + 1}</td>
                                <td><strong>${z.wardName}</strong></td>
                                <td>${z.district}</td>
                                <td>${z.province}</td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/admin/shipping-zones" method="post" style="margin:0;">
                                        <input type="hidden" name="action" value="toggleActive">
                                        <input type="hidden" name="zoneId" value="${z.zoneId}">
                                        <input type="hidden" name="filterProvince" value="${oldProvince}">
                                        <input type="hidden" name="filterDistrict" value="${oldDistrict}">
                                        <input type="hidden" name="filterIsActive" value="${oldIsActive}">
                                        <label class="switch">
                                            <input type="checkbox" onchange="this.form.submit()" ${z.isActive ? 'checked' : ''}>
                                            <span class="slider"></span>
                                        </label>
                                    </form>
                                </td>
                                <td>
                                    <div class="action-btns">
                                        <button class="btn-icon btn-edit" type="button"
                                                onclick="editZone(${z.zoneId}, '${z.wardName}', '${z.district}', '${z.province}', ${z.isActive})" title="Sửa">
                                            <i class="fa-solid fa-pen"></i>
                                        </button>
                                        <form action="${pageContext.request.contextPath}/admin/shipping-zones" method="post" style="margin:0;" onsubmit="return confirm('Bạn có chắc muốn xóa vùng này?');">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="zoneId" value="${z.zoneId}">
                                            <input type="hidden" name="filterProvince" value="${oldProvince}">
                                            <input type="hidden" name="filterDistrict" value="${oldDistrict}">
                                            <input type="hidden" name="filterIsActive" value="${oldIsActive}">
                                            <button type="submit" class="btn-icon btn-del" title="Xóa">
                                                <i class="fa-solid fa-trash-can"></i>
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty zones}">
                            <tr><td colspan="6" style="text-align:center; padding: 30px; color:#94a3b8;">Không có dữ liệu vùng giao hàng phù hợp.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <!-- View by Group -->
            <h3 class="section-title">Xem theo nhóm Quận/Huyện</h3>
            <c:forEach var="entry" items="${groupedZones}">
                <div class="group-card">
                    <div class="group-header">
                        <i class="fa-solid fa-location-dot"></i>
                        ${entry.key} — ${fn:length(entry.value)} phường
                    </div>
                    <div class="group-list">
                        <c:forEach var="item" items="${entry.value}">
                            <div class="ward-item">
                                <span>${item.wardName}</span>
                                <c:choose>
                                    <c:when test="${item.isActive}">
                                        <span class="status-badge badge-active">Hoạt động</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="status-badge badge-inactive">Tạm ngừng</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </c:forEach>
            <c:if test="${empty groupedZones}">
                <p style="color: #64748b; margin-bottom: 40px;">Không có dữ liệu.</p>
            </c:if>

        </main>
    </div>

    <%-- ══════════════════════════════════════════════════════
         MODAL: THÊM / SỬA VÙNG GIAO HÀNG
         ══════════════════════════════════════════════════════ --%>
    <div id="zoneModal" class="modal-overlay" onclick="closeModalOnOverlay(event)">
        <div class="modal-box">
            <div class="modal-header">
                <div class="form-title" id="formTitle">
                    <i class="fa-solid fa-plus" style="color:#6366f1;"></i> Thêm vùng giao hàng mới
                </div>
                <button type="button" class="modal-close" onclick="closeZoneModal()"><i class="fa-solid fa-xmark"></i></button>
            </div>
            <div class="modal-body">
                <form action="${pageContext.request.contextPath}/admin/shipping-zones" method="post" id="zoneForm">
                    <input type="hidden" name="action" value="add" id="formAction">
                    <input type="hidden" name="zoneId" id="formZoneId">
                    <input type="hidden" name="filterProvince" value="${oldProvince}">
                    <input type="hidden" name="filterDistrict" value="${oldDistrict}">
                    <input type="hidden" name="filterIsActive" value="${oldIsActive}">

                    <div class="form-grid">
                        <div class="form-group">
                            <label>Phường / Xã *</label>
                            <input type="text" name="wardName" id="formWard" class="form-control" placeholder="VD: Phường Cát Linh" required>
                        </div>
                        <div class="form-group">
                            <label>Quận / Huyện *</label>
                            <input type="text" name="district" id="formDistrict" class="form-control" placeholder="VD: Đống Đa" required>
                        </div>
                        <div class="form-group">
                            <label>Tỉnh / Thành phố *</label>
                            <input type="text" name="province" id="formProvince" class="form-control" placeholder="VD: Hà Nội" required>
                        </div>
                        <div class="form-group">
                            <label>Trạng thái</label>
                            <select name="isActive" id="formStatus" class="form-control">
                                <option value="1">Hoạt động (nhận đơn giao)</option>
                                <option value="0">Tạm ngừng (không nhận)</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn-submit"><i class="fa-solid fa-check"></i> Lưu vùng giao hàng</button>
                        <button type="button" class="btn-cancel" onclick="closeZoneModal()">Hủy</button>
                    </div>
                    <div class="form-note">
                        ⚠️ Mỗi cặp (Phường + Quận + Tỉnh) phải là duy nhất trong hệ thống.
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script>
        const toast = document.getElementById('toastBox');
        if (toast) setTimeout(() => toast.style.display = 'none', 3500);

        function openZoneModal() {
            document.getElementById('zoneModal').classList.add('open');
        }
        function closeZoneModal() {
            document.getElementById('zoneModal').classList.remove('open');
        }
        function closeModalOnOverlay(e) {
            if (e.target === document.getElementById('zoneModal')) closeZoneModal();
        }

        function openAddModal() {
            resetForm();
            openZoneModal();
        }

        function editZone(id, ward, district, province, isActive) {
            document.getElementById('formAction').value = 'update';
            document.getElementById('formZoneId').value = id;
            document.getElementById('formWard').value = ward;
            document.getElementById('formDistrict').value = district;
            document.getElementById('formProvince').value = province;
            document.getElementById('formStatus').value = isActive ? "1" : "0";

            document.getElementById('formTitle').innerHTML = '<i class="fa-solid fa-pen" style="color:#f97316;"></i> Sửa vùng giao hàng';
            openZoneModal();
        }

        function resetForm() {
            document.getElementById('formAction').value = 'add';
            document.getElementById('formZoneId').value = '';
            document.getElementById('formWard').value = '';
            document.getElementById('formDistrict').value = '';
            document.getElementById('formProvince').value = '';
            document.getElementById('formStatus').value = '1';

            document.getElementById('formTitle').innerHTML = '<i class="fa-solid fa-plus" style="color:#6366f1;"></i> Thêm vùng giao hàng mới';
        }
    </script>
</body>
</html>
