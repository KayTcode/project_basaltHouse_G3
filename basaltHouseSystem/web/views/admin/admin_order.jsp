<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"      prefix="c"   %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"       prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"  %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Quản lý Đơn hàng — Admin | BasaltHouse</title>

    <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&display=swap" rel="stylesheet" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />

    <%-- CSS dùng chung (reset, layout, header, sidebar) --%>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_common.css" />
    <%-- CSS riêng cho trang đơn hàng --%>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_order.css"  />
</head>
<body>

    <%-- ════ HEADER DÙNG CHUNG ════════════════════════════════════════ --%>
    <jsp:include page="header.jsp" />

    <div class="app-container">

        <%-- ════ SIDEBAR DÙNG CHUNG ══════════════════════════════════ --%>
        <jsp:include page="sidebar.jsp" />

        <%-- ════ MAIN CONTENT ════════════════════════════════════════ --%>
        <main class="main-content">

            <%-- ── Toast session ──────────────────────────────────── --%>
            <c:if test="${not empty sessionScope.toastMessage}">
                <div class="toast toast-ok" id="toastBox">${fn:escapeXml(sessionScope.toastMessage)}</div>
                <c:remove var="toastMessage" scope="session" />
            </c:if>
            <c:if test="${not empty sessionScope.toastError}">
                <div class="toast toast-err" id="toastBox">${fn:escapeXml(sessionScope.toastError)}</div>
                <c:remove var="toastError" scope="session" />
            </c:if>

            <%-- ── Page Header ─────────────────────────────────────── --%>
            <div class="page-header">
                <div>
                    <h1>🛒 Quản lý Đơn hàng</h1>
                    <p class="page-desc">Theo dõi và xử lý toàn bộ đơn hàng trong hệ thống</p>
                </div>
            </div>

            <%-- ── KPI Cards ───────────────────────────────────────── --%>
            <div class="kpi-row">
                <div class="kpi-card kpi-all">
                    <div class="kpi-label">Tổng đơn</div>
                    <div class="kpi-value">${data.stats.total}</div>
                </div>
                <div class="kpi-card kpi-pending">
                    <div class="kpi-label">Chờ xử lý</div>
                    <div class="kpi-value">${data.stats.pending}</div>
                </div>
                <div class="kpi-card kpi-delivering">
                    <div class="kpi-label">Đang giao</div>
                    <div class="kpi-value">${data.stats.delivering}</div>
                </div>
                <div class="kpi-card kpi-done">
                    <div class="kpi-label">Đã hoàn thành</div>
                    <div class="kpi-value">${data.stats.done}</div>
                </div>
                <div class="kpi-card kpi-cancelled">
                    <div class="kpi-label">Đã hủy</div>
                    <div class="kpi-value">${data.stats.cancelled}</div>
                </div>
            </div>

            <%-- ── Filter Bar ──────────────────────────────────────── --%>
            <form method="get"
                  action="${pageContext.request.contextPath}/admin/orders"
                  class="filter-bar">
                <input type="text"
                       name="search"
                       placeholder="🔍 Tìm mã đơn, tên khách hàng"
                       value="${fn:escapeXml(data.oldSearch)}" />

                <select name="orderType">
                    <option value="">Tất cả loại</option>
                    <option value="Online" ${data.oldOrderType eq 'Online' ? 'selected' : ''}>Online</option>
                    <option value="POS"    ${data.oldOrderType eq 'POS'    ? 'selected' : ''}>Tại quầy (POS)</option>
                </select>

                <select name="paymentStatus">
                    <option value="">Tất cả thanh toán</option>
                    <option value="Paid"   ${data.oldPaymentStatus eq 'Paid'   ? 'selected' : ''}>Đã thanh toán</option>
                    <option value="Unpaid" ${data.oldPaymentStatus eq 'Unpaid' ? 'selected' : ''}>Chưa thanh toán</option>
                </select>

                <button type="submit" class="btn-search">Tìm kiếm</button>
                <a href="${pageContext.request.contextPath}/admin/orders" class="btn-reset">Đặt lại</a>
            </form>

            <%-- ── Tab Pills (lọc theo OrderStatus) ───────────────── --%>
            <c:set var="baseFilter"
                   value="?search=${fn:escapeXml(data.oldSearch)}&orderType=${data.oldOrderType}&paymentStatus=${data.oldPaymentStatus}" />
            <div class="tab-pills">
                <a href="${pageContext.request.contextPath}/admin/orders${baseFilter}"
                   class="tab-pill ${empty data.oldOrderStatus ? 'active' : ''}">
                    Tất cả (${data.stats.total})
                </a>
                <a href="${pageContext.request.contextPath}/admin/orders${baseFilter}&orderStatus=Pending"
                   class="tab-pill tp-pending ${data.oldOrderStatus eq 'Pending' ? 'active' : ''}">
                    Pending (${data.stats.pending})
                </a>
                <a href="${pageContext.request.contextPath}/admin/orders${baseFilter}&orderStatus=Delivering"
                   class="tab-pill tp-delivering ${data.oldOrderStatus eq 'Delivering' ? 'active' : ''}">
                    Delivering (${data.stats.delivering})
                </a>
                <a href="${pageContext.request.contextPath}/admin/orders${baseFilter}&orderStatus=Done"
                   class="tab-pill tp-done ${data.oldOrderStatus eq 'Done' ? 'active' : ''}">
                    Done (${data.stats.done})
                </a>
                <a href="${pageContext.request.contextPath}/admin/orders${baseFilter}&orderStatus=Cancelled"
                   class="tab-pill tp-cancelled ${data.oldOrderStatus eq 'Cancelled' ? 'active' : ''}">
                    Cancelled (${data.stats.cancelled})
                </a>
            </div>

            <%-- ── Bảng danh sách đơn hàng ─────────────────────────── --%>
            <div class="table-card">
                <table>
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Loại</th>
                            <th>Khách hàng</th>
                            <th>Trạng thái</th>
                            <th>Thanh toán</th>
                            <th>Tổng tiền</th>
                            <th>Ngày tạo</th>
                            <th>Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty data.orders}">
                                <tr>
                                    <td colspan="8">
                                        <div class="empty-state">
                                            <div class="empty-icon">🛒</div>
                                            Không có đơn hàng nào phù hợp
                                        </div>
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="dto" items="${data.orders}">
                                    <%-- ── Hàng hiển thị ─────────────────────────────── --%>
                                    <tr>
                                        <td><strong>#${dto.order.orderId}</strong></td>

                                        <td>
                                            <span class="badge badge-type-${dto.order.orderType}">
                                                ${dto.order.orderType eq 'POS' ? 'Tại quầy' : dto.order.orderType}
                                            </span>
                                        </td>

                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty dto.customerName}">
                                                    ${fn:escapeXml(dto.customerName)}
                                                </c:when>
                                                <c:otherwise>
                                                    <em style="color:#94a3b8;">Tại quầy</em>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <td>
                                            <span class="badge badge-${dto.order.orderStatus}">
                                                ${dto.order.orderStatus}
                                            </span>
                                        </td>

                                        <td>
                                            <span class="badge badge-${dto.order.paymentStatus}">
                                                ${dto.order.paymentStatus}
                                            </span>
                                        </td>

                                        <td>
                                            <strong>
                                                <fmt:formatNumber value="${dto.order.finalAmount}"
                                                                  type="number" groupingUsed="true"/>đ
                                            </strong>
                                        </td>

                                        <td>${dto.createdAtFormatted}</td>

                                        <td>
                                            <button class="btn-icon btn-view"
                                                    title="Xem chi tiết"
                                                    onclick="openDetailModal(${dto.order.orderId})">
                                                <i class="fa-regular fa-eye"></i>
                                            </button>
                                        </td>
                                    </tr>

                                    <%-- ── Hàng data ẩn — JS đọc để fill modal ──────── --%>
                                    <tr class="data-row" id="order-meta-${dto.order.orderId}"
                                        data-id="${dto.order.orderId}"
                                        data-type="${dto.order.orderType}"
                                        data-status="${dto.order.orderStatus}"
                                        data-pay-status="${dto.order.paymentStatus}"
                                        data-pay-method="${dto.order.paymentMethod}"
                                        data-customer="${fn:escapeXml(dto.customerName)}"
                                        data-customer-phone="${fn:escapeXml(dto.customerPhone)}"
                                        data-shipper="${fn:escapeXml(dto.shipperName)}"
                                        data-discount="${fn:escapeXml(dto.discountDisplay)}"
                                        data-address="${fn:escapeXml(dto.addressDetail)}"
                                        data-ward="${fn:escapeXml(dto.wardDistrict)}"
                                        data-table="${fn:escapeXml(dto.tableCode)}"
                                        data-created="${fn:escapeXml(dto.createdAtFormatted)}"
                                        data-total="${dto.order.totalAmount}"
                                        data-discount-amt="${dto.order.discountAmount}"
                                        data-final="${dto.order.finalAmount}">
                                        <td colspan="8"></td>
                                    </tr>

                                    <%-- OrderDetails --%>
                                    <tr class="data-row" id="order-details-${dto.order.orderId}">
                                        <td colspan="8">
                                            <table>
                                                <c:forEach var="od" items="${dto.orderDetails}">
                                                    <tr data-name="${fn:escapeXml(od.productName)}"
                                                        data-size="${fn:escapeXml(od.sizeName)}"
                                                        data-qty="${od.quantity}"
                                                        data-price="${od.unitPrice}"
                                                        data-sub="${od.subtotal}"
                                                        data-note="${fn:escapeXml(od.note)}">
                                                    </tr>
                                                </c:forEach>
                                            </table>
                                        </td>
                                    </tr>

                                    <%-- DeliveryLogs --%>
                                    <tr class="data-row" id="order-delivery-${dto.order.orderId}">
                                        <td colspan="8">
                                            <ul>
                                                <c:forEach var="dl" items="${dto.deliveryLogs}">
                                                    <li data-status="${dl.status}"
                                                        data-note="${fn:escapeXml(dl.note)}"
                                                        data-estimated="${dl.estimatedDeliveryAt}"
                                                        data-shipconf="${dl.shipperConfirmedAt}"
                                                        data-custconf="${dl.customerConfirmedAt}"
                                                        data-delivered="${dl.deliveredAt}">
                                                    </li>
                                                </c:forEach>
                                            </ul>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>

                <%-- ── Phân trang ────────────────────────────────────── --%>
                <c:if test="${data.totalPages > 1}">
                    <c:set var="pageBase"
                           value="?search=${fn:escapeXml(data.oldSearch)}&orderType=${data.oldOrderType}&orderStatus=${data.oldOrderStatus}&paymentStatus=${data.oldPaymentStatus}&page=" />
                    <div class="pagination">
                        <a href="${pageContext.request.contextPath}/admin/orders${pageBase}${data.currentPage - 1}"
                           class="page-btn ${data.currentPage <= 1 ? 'disabled' : ''}">&#8249;</a>

                        <c:forEach begin="1" end="${data.totalPages}" var="p">
                            <a href="${pageContext.request.contextPath}/admin/orders${pageBase}${p}"
                               class="page-btn ${p == data.currentPage ? 'active' : ''}">${p}</a>
                        </c:forEach>

                        <a href="${pageContext.request.contextPath}/admin/orders${pageBase}${data.currentPage + 1}"
                           class="page-btn ${data.currentPage >= data.totalPages ? 'disabled' : ''}">&#8250;</a>
                    </div>
                </c:if>
            </div>
            <%-- ── end table-card ──────────────────────────────────── --%>

        </main>
    </div>
    <%-- ════ end app-container ═══════════════════════════════════════ --%>

    <%-- ══════════════════════════════════════════════════════════════
         MODAL CHI TIẾT ĐƠN HÀNG
         ══════════════════════════════════════════════════════════════ --%>
    <div id="orderDetailModal" class="modal-overlay" onclick="closeModalOnOverlay(event)">
        <div class="modal-box">

            <div class="modal-header">
                <h2>Chi tiết đơn hàng #<span id="m-orderId"></span></h2>
                <button class="modal-close" onclick="closeModal()" title="Đóng">✕</button>
            </div>

            <div class="modal-body">

                <%-- Badges --%>
                <div class="status-row">
                    <span id="m-typeBadge"   class="badge"></span>
                    <span id="m-statusBadge" class="badge"></span>
                    <span id="m-payBadge"    class="badge"></span>
                </div>

                <%-- Info Grid --%>
                <div class="info-grid">
                    <div class="info-group">
                        <label>Khách hàng</label>
                        <span id="m-customerName">—</span>
                    </div>
                    <div class="info-group">
                        <label>SĐT</label>
                        <span id="m-customerPhone">—</span>
                    </div>
                    <div class="info-group">
                        <label>Địa chỉ giao / Bàn</label>
                        <span id="m-address">—</span>
                    </div>
                    <div class="info-group">
                        <label>Shipper</label>
                        <span id="m-shipper">—</span>
                    </div>
                    <div class="info-group">
                        <label>Mã giảm giá</label>
                        <span id="m-discount">—</span>
                    </div>
                    <div class="info-group">
                        <label>Hình thức thanh toán</label>
                        <span id="m-payMethod">—</span>
                    </div>
                    <div class="info-group">
                        <label>Ngày tạo</label>
                        <span id="m-createdAt">—</span>
                    </div>
                </div>

                <%-- Sản phẩm đặt --%>
                <p class="modal-section-title">📦 Sản phẩm đặt</p>
                <div class="product-table-wrap">
                    <table>
                        <thead>
                            <tr>
                                <th>Sản phẩm</th>
                                <th>Size</th>
                                <th>SL</th>
                                <th class="text-right">Đơn giá</th>
                                <th class="text-right">Thành tiền</th>
                            </tr>
                        </thead>
                        <tbody id="m-productRows">
                            <tr><td colspan="5" class="empty-state">—</td></tr>
                        </tbody>
                    </table>
                </div>

                <%-- Tổng kết --%>
                <div class="modal-summary">
                    <div class="summary-item">
                        <div class="s-label">Tổng</div>
                        <div class="s-val" id="m-total">—</div>
                    </div>
                    <div class="summary-item">
                        <div class="s-label">Giảm</div>
                        <div class="s-val s-discount" id="m-discountAmt">—</div>
                    </div>
                    <div class="summary-item">
                        <div class="s-label">Thực thu</div>
                        <div class="s-val s-final" id="m-final">—</div>
                    </div>
                </div>

                <%-- Lịch sử giao hàng --%>
                <div id="m-deliverySection" style="display:none">
                    <p class="modal-section-title">🚚 Lịch sử giao hàng</p>
                    <ul class="timeline" id="m-deliveryTimeline"></ul>
                    <div class="section-divider"></div>
                </div>

                <%-- Cập nhật trạng thái --%>
                <form method="post"
                      action="${pageContext.request.contextPath}/admin/orders"
                      class="modal-footer">
                    <input type="hidden" name="action"   value="updateStatus" />
                    <input type="hidden" name="orderId"  id="f-orderId" value="" />
                    <label>Đổi trạng thái:</label>
                    <select name="newStatus" id="f-newStatus">
                        <option value="Pending">Pending</option>
                        <option value="Delivering">Delivering</option>
                        <option value="Done">Done</option>
                        <option value="Cancelled">Cancelled</option>
                    </select>
                    <button type="submit" class="btn-search">
                        <i class="fa-solid fa-floppy-disk"></i> Cập nhật
                    </button>
                </form>

            </div><%-- /.modal-body --%>
        </div><%-- /.modal-box --%>
    </div><%-- /.modal-overlay --%>

    <%-- ════ JAVASCRIPT ═══════════════════════════════════════════════ --%>
    <script>
        /* ── Auto-hide toast ─────────────────────────────────────────── */
        const toast = document.getElementById('toastBox');
        if (toast) setTimeout(() => toast.style.display = 'none', 3500);

        /* ── Format tiền ─────────────────────────────────────────────── */
        function fmtMoney(v) {
            return Number(v || 0).toLocaleString('vi-VN') + 'đ';
        }

        /* ── Mở Modal ────────────────────────────────────────────────── */
        function openDetailModal(orderId) {
            // 1. Đọc metadata từ hàng ẩn
            const meta = document.getElementById('order-meta-' + orderId);
            if (!meta) return;

            const d = meta.dataset;

            // 2. Fill header + badges
            document.getElementById('m-orderId').textContent = orderId;
            document.getElementById('f-orderId').value       = orderId;

            const typeBadge = document.getElementById('m-typeBadge');
            typeBadge.textContent = d.type === 'POS' ? 'Tại quầy' : d.type;
            typeBadge.className   = 'badge badge-type-' + d.type;

            const statusBadge = document.getElementById('m-statusBadge');
            statusBadge.textContent = d.status;
            statusBadge.className   = 'badge badge-' + d.status;

            const payBadge = document.getElementById('m-payBadge');
            payBadge.textContent = d.payStatus;
            payBadge.className   = 'badge badge-' + d.payStatus;

            // Sync select với trạng thái hiện tại
            document.getElementById('f-newStatus').value = d.status;

            // 3. Fill info
            document.getElementById('m-customerName').textContent  = d.customer  || '—';
            document.getElementById('m-customerPhone').textContent = d.customerPhone || '—';
            document.getElementById('m-shipper').textContent       = d.shipper   || '—';
            document.getElementById('m-discount').textContent      = d.discount  || 'Không có';
            document.getElementById('m-payMethod').textContent     = d.payMethod || '—';
            document.getElementById('m-createdAt').textContent     = d.created   || '—';

            // Địa chỉ: Online → địa chỉ, POS → bàn
            let addr = '';
            if (d.type === 'POS') {
                addr = d.table ? 'Bàn ' + d.table : '—';
            } else {
                addr = [d.address, d.ward].filter(Boolean).join(', ') || '—';
            }
            document.getElementById('m-address').textContent = addr;

            // 4. Amounts
            document.getElementById('m-total').textContent       = fmtMoney(d.total);
            document.getElementById('m-discountAmt').textContent = '-' + fmtMoney(d.discountAmt);
            document.getElementById('m-final').textContent       = fmtMoney(d.final);

            // 5. Products
            const tbody    = document.getElementById('m-productRows');
            const detailEl = document.getElementById('order-details-' + orderId);
            const prodRows = detailEl ? detailEl.querySelectorAll('table tr') : [];

            tbody.innerHTML = '';
            if (prodRows.length > 0) {
                prodRows.forEach(row => {
                    const tr = document.createElement('tr');
                    tr.innerHTML =
                        '<td>' + (row.dataset.name  || '—') + '</td>' +
                        '<td>' + (row.dataset.size  || '—') + '</td>' +
                        '<td>' + (row.dataset.qty   ||  0 ) + '</td>' +
                        '<td class="text-right">' + fmtMoney(row.dataset.price) + '</td>' +
                        '<td class="text-right"><strong>' + fmtMoney(row.dataset.sub) + '</strong></td>';
                    tbody.appendChild(tr);
                });
            } else {
                tbody.innerHTML = '<tr><td colspan="5" class="empty-state">Không có sản phẩm</td></tr>';
            }

            // 6. Delivery logs
            const deliverySection  = document.getElementById('m-deliverySection');
            const deliveryTimeline = document.getElementById('m-deliveryTimeline');
            const deliveryEl       = document.getElementById('order-delivery-' + orderId);
            const dlItems          = deliveryEl ? deliveryEl.querySelectorAll('ul li') : [];

            deliveryTimeline.innerHTML = '';
            if (dlItems.length > 0) {
                deliverySection.style.display = 'block';
                dlItems.forEach(li => {
                    const status = li.dataset.status || '';
                    const isDone = ['Delivered', 'Done', 'CustomerConfirmed'].includes(status);
                    const item   = document.createElement('li');
                    item.className  = isDone ? 'tl-done' : (status ? '' : 'tl-pending');
                    item.innerHTML  =
                        '<div>' +
                        '  <div class="timeline-label">' + (status || 'Chờ xác nhận') + '</div>' +
                        '  <div class="timeline-note">'  + (li.dataset.note || '') + '</div>' +
                        '</div>';
                    deliveryTimeline.appendChild(item);
                });
            } else {
                deliverySection.style.display = 'none';
            }

            // 7. Open
            document.getElementById('orderDetailModal').classList.add('open');
            document.body.style.overflow = 'hidden';
        }

        /* ── Đóng Modal ──────────────────────────────────────────────── */
        function closeModal() {
            document.getElementById('orderDetailModal').classList.remove('open');
            document.body.style.overflow = '';
        }

        function closeModalOnOverlay(e) {
            if (e.target === document.getElementById('orderDetailModal')) closeModal();
        }

        document.addEventListener('keydown', e => { if (e.key === 'Escape') closeModal(); });
    </script>

</body>
</html>
