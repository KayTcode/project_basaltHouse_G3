<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"      prefix="c"   %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"       prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"  %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Quản lý Bàn & Phiên — Admin | BasaltHouse</title>

    <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />

    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_common.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_table.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_order.css" /> <%-- Dùng lại style pagination, toast, page-header --%>
</head>
<body class="admin-dashboard-body">

    <%@ include file="/views/admin/header.jsp" %>

    <div class="app-container">
        <%@ include file="/views/admin/sidebar.jsp" %>

        <main class="main-content">

            <%-- Toast --%>
            <c:if test="${not empty sessionScope.toastMessage}">
                <div class="toast toast-ok" id="toastBox">${fn:escapeXml(sessionScope.toastMessage)}</div>
                <c:remove var="toastMessage" scope="session" />
            </c:if>
            <c:if test="${not empty sessionScope.toastError}">
                <div class="toast toast-err" id="toastBox">${fn:escapeXml(sessionScope.toastError)}</div>
                <c:remove var="toastError" scope="session" />
            </c:if>

            <div class="page-header">
                <div>
                    <h1>🪑 Quản lý Bàn & Phiên</h1>
                    <p class="page-desc">Giám sát sơ đồ bàn thực tế và các phiên phục vụ khách hàng</p>
                </div>
            </div>

            <%-- KPI Cards --%>
            <div class="kpi-row">
                <div class="kpi-card kpi-all">
                    <div class="kpi-label">Tổng số bàn</div>
                    <div class="kpi-value">${dashboard.statTotal}</div>
                </div>
                <div class="kpi-card kpi-delivering">
                    <div class="kpi-label">Đang có khách</div>
                    <div class="kpi-value">${dashboard.statOccupied}</div>
                </div>
                <div class="kpi-card kpi-pending">
                    <div class="kpi-label">Đã đặt trước</div>
                    <div class="kpi-value">${dashboard.statReserved}</div>
                </div>
                <div class="kpi-card kpi-done">
                    <div class="kpi-label">Trống sẵn sàng</div>
                    <div class="kpi-value">${dashboard.statAvailable}</div>
                </div>
            </div>

            <h3 class="section-title">SƠ ĐỒ BÀN THỰC TẾ</h3>
            
            <%-- Chú thích --%>
            <div style="display:flex; gap:16px; margin-bottom:20px; font-size:0.85rem; font-weight:600;">
                <span style="color:var(--table-green)"><i class="fa-solid fa-circle"></i> Trống (Available)</span>
                <span style="color:var(--table-blue)"><i class="fa-solid fa-circle"></i> Đang có khách (Occupied)</span>
                <span style="color:var(--table-yellow)"><i class="fa-solid fa-circle"></i> Đã đặt trước (Reserved)</span>
            </div>

            <%-- Sơ đồ bàn theo Area --%>
            <c:forEach var="entry" items="${dashboard.tablesByArea}">
                <div class="area-group">
                    <h4 class="area-title">${entry.key}</h4>
                    <div class="table-grid">
                        <c:forEach var="dto" items="${entry.value}">
                            
                            <%-- Thẻ bàn --%>
                            <div class="tbl-card st-${dto.table.status}" 
                                 onclick="handleTableClick('${dto.table.status}', ${dto.table.tableId}, '${dto.table.tableCode}', '${dto.table.area}', ${dto.table.capacity})">
                                
                                <div class="tbl-header">
                                    <i class="fa-solid fa-chair"></i>
                                    <span class="tbl-code">${dto.table.tableCode}</span>
                                </div>
                                <div class="tbl-desc">${dto.table.area} • ${dto.table.capacity} chỗ</div>
                                
                                <div class="tbl-status">
                                    <i class="fa-solid fa-users"></i>
                                    <c:choose>
                                        <c:when test="${dto.table.status eq 'Occupied'}">
                                            ${dto.table.currentGuests} / ${dto.table.capacity} khách
                                        </c:when>
                                        <c:otherwise>
                                            0 / ${dto.table.capacity} khách
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                
                                <div class="tbl-substatus">
                                    <c:choose>
                                        <c:when test="${dto.table.status eq 'Available'}">✓ Trống</c:when>
                                        <c:when test="${dto.table.status eq 'Occupied'}">• Đang có khách</c:when>
                                        <c:when test="${dto.table.status eq 'Reserved'}">◷ Đã đặt trước</c:when>
                                    </c:choose>
                                </div>
                            </div>
                            
                        </c:forEach>
                    </div>
                </div>
            </c:forEach>

            <%-- Lịch sử phiên làm việc --%>
            <h3 class="section-title">LỊCH SỬ PHIÊN LÀM VIỆC</h3>
            <div class="table-card">
                <table>
                    <thead>
                        <tr>
                            <th>Mã phiên</th>
                            <th>Bàn</th>
                            <th>Thu ngân</th>
                            <th>Khách</th>
                            <th>Giờ mở</th>
                            <th>Giờ đóng</th>
                            <th>Trạng thái</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="h" items="${historyData.history}">
                            <tr>
                                <td><strong>${h.session.sessionCode}</strong></td>
                                <td>${h.tableCode} • ${h.area}</td>
                                <td>${empty h.cashierName ? '—' : h.cashierName}</td>
                                <td>${h.session.guestCount}</td>
                                <td>${h.openedAtFormatted}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty h.closedAtFormatted}">${h.closedAtFormatted}</c:when>
                                        <c:otherwise><em style="color:#94a3b8">Đang mở</em></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${h.session.status eq 'Open'}">
                                            <span class="badge bd-open">Open</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bd-closed">Closed</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                
                <%-- Phân trang --%>
                <c:if test="${historyData.totalPages > 1}">
                    <div class="pagination">
                        <c:forEach begin="1" end="${historyData.totalPages}" var="p">
                            <a href="${pageContext.request.contextPath}/admin/tables?page=${p}"
                               class="page-btn ${p == historyData.currentPage ? 'active' : ''}">${p}</a>
                        </c:forEach>
                    </div>
                </c:if>
            </div>
            
        </main>
    </div>

    <%-- ══════════════════════════════════════════════════════════════════
         MODAL: MỞ PHIÊN MỚI
         ══════════════════════════════════════════════════════════════════ --%>
    <div id="openSessionModal" class="modal-overlay" onclick="closeModalOnOverlay(event, 'openSessionModal')">
        <div class="modal-box">
            <div class="modal-header">
                <h2>Mở phiên — Bàn <span id="open-tbl-code"></span></h2>
                <span class="badge-header avail">Available</span>
            </div>
            <form action="${pageContext.request.contextPath}/admin/tables" method="post" class="modal-body">
                <input type="hidden" name="action" value="openSession" />
                <input type="hidden" name="tableId" id="open-tbl-id" />
                
                <div style="display:flex; gap:16px;">
                    <div class="form-group" style="flex:2;">
                        <label>Chọn bàn *</label>
                        <select id="open-tbl-select" disabled>
                            <option value="">— Đang chọn —</option>
                        </select>
                    </div>
                    <div class="form-group" style="flex:1;">
                        <label>Số khách *</label>
                        <input type="number" name="guestCount" id="open-guest-count" min="1" required />
                    </div>
                </div>
                
                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary"><i class="fa-solid fa-check"></i> Mở phiên</button>
                    <button type="button" class="btn btn-secondary" onclick="closeModal('openSessionModal')">Hủy</button>
                </div>
            </form>
        </div>
    </div>

    <%-- ══════════════════════════════════════════════════════════════════
         MODAL: CHI TIẾT PHIÊN ĐANG MỞ (OCCUPIED)
         ══════════════════════════════════════════════════════════════════ --%>
    <div id="activeSessionModal" class="modal-overlay" onclick="closeModalOnOverlay(event, 'activeSessionModal')">
        <div class="modal-box">
            <div class="modal-header">
                <h2>Chi tiết phiên đang mở — <span id="act-tbl-code"></span></h2>
                <span class="badge-header occ">Đang mở</span>
            </div>
            <div class="modal-body">
                
                <h3 style="font-size:1.05rem; font-weight:700; margin-bottom:16px; color:#1e293b;" id="act-session-code">Phiên SES-...</h3>
                
                <div class="info-grid">
                    <div class="info-col">
                        <p>Bàn</p>
                        <h4 id="act-table-info">—</h4>
                    </div>
                    <div class="info-col">
                        <p>Thu ngân phụ trách</p>
                        <h4 id="act-cashier">—</h4>
                    </div>
                    <div class="info-col">
                        <p>Số khách</p>
                        <h4 id="act-guests">—</h4>
                    </div>
                    <div class="info-col">
                        <p>Giờ mở</p>
                        <h4 id="act-opened">—</h4>
                    </div>
                    <div class="info-col">
                        <p>Thời gian đã ngồi</p>
                        <h4 id="act-duration">—</h4>
                    </div>
                    <div class="info-col">
                        <p>Đơn hàng gắn</p>
                        <h4 id="act-latest-order">—</h4>
                    </div>
                </div>
                
                <div class="modal-footer" style="margin-top:32px;">
                    <form action="${pageContext.request.contextPath}/admin/tables" method="post" id="formCloseSession">
                        <input type="hidden" name="action" value="closeSession" />
                        <input type="hidden" name="tableId" id="close-tbl-id" />
                        <input type="hidden" name="sessionId" id="close-ses-id" />
                        <button type="button" class="btn btn-danger" onclick="confirmCloseSession()">Đóng phiên & In hóa đơn</button>
                    </form>
                    
                    <a href="#" id="act-btn-view-order" class="btn btn-secondary" style="display:none;">Xem đơn hàng</a>
                </div>
            </div>
        </div>
    </div>

    <script>
        const toast = document.getElementById('toastBox');
        if (toast) setTimeout(() => toast.style.display = 'none', 3500);

        function closeModal(id) { document.getElementById(id).classList.remove('open'); }
        function closeModalOnOverlay(e, id) { if (e.target === document.getElementById(id)) closeModal(id); }

        function handleTableClick(status, tableId, tableCode, area, capacity) {
            if (status === 'Available') {
                // Mở modal tạo phiên mới
                document.getElementById('open-tbl-code').textContent = tableCode;
                document.getElementById('open-tbl-id').value = tableId;
                
                const select = document.getElementById('open-tbl-select');
                select.innerHTML = `<option>${tableCode} — ${area} (${capacity} chỗ)</option>`;
                document.getElementById('open-guest-count').max = capacity;
                document.getElementById('open-guest-count').value = 1;
                
                document.getElementById('openSessionModal').classList.add('open');
                
            } else if (status === 'Occupied') {
                // Mở modal chi tiết phiên (cần call AJAX lấy data)
                document.getElementById('act-tbl-code').textContent = tableCode;
                fetchSessionData(tableId);
            }
        }
        
        function fetchSessionData(tableId) {
            fetch('${pageContext.request.contextPath}/admin/tables/active-session?tableId=' + tableId)
            .then(res => res.json())
            .then(data => {
                if (data.error) {
                    alert('Lỗi: ' + data.error);
                    return;
                }
                
                // Fill modal info
                document.getElementById('act-session-code').textContent = 'Phiên ' + data.session.sessionCode;
                document.getElementById('act-table-info').textContent = data.tableCode + ' — ' + data.area;
                document.getElementById('act-cashier').textContent = data.cashierName || '—';
                document.getElementById('act-guests').textContent = data.session.guestCount + ' người';
                document.getElementById('act-opened').textContent = data.openedAtFormatted || '—';
                document.getElementById('act-duration').textContent = data.durationStr || '—';
                
                // Latest order info
                const latestOrder = data.orders.length > 0 ? data.orders[data.orders.length - 1] : null;
                const viewBtn = document.getElementById('act-btn-view-order');
                
                if (latestOrder) {
                    document.getElementById('act-latest-order').textContent = '#' + latestOrder.orderId + ' (' + Number(latestOrder.finalAmount).toLocaleString('vi-VN') + 'đ)';
                    viewBtn.style.display = 'block';
                    viewBtn.href = '${pageContext.request.contextPath}/admin/orders?search=' + latestOrder.orderId;
                    viewBtn.textContent = 'Xem đơn hàng #' + latestOrder.orderId;
                } else {
                    document.getElementById('act-latest-order').innerHTML = '<em style="color:#94a3b8">Chưa có đơn</em>';
                    viewBtn.style.display = 'none';
                }
                
                // Fill form ids cho lúc Submit đóng phiên
                document.getElementById('close-tbl-id').value = data.session.tableId;
                document.getElementById('close-ses-id').value = data.session.sessionId;
                
                document.getElementById('activeSessionModal').classList.add('open');
            })
            .catch(err => {
                console.error(err);
                alert('Không thể tải thông tin phiên làm việc.');
            });
        }
        
        function confirmCloseSession() {
            if (confirm("Bạn có chắc chắn muốn đóng phiên làm việc này? Mọi hóa đơn sẽ được tính toán chốt.")) {
                document.getElementById('formCloseSession').submit();
            }
        }
    </script>

</body>
</html>
