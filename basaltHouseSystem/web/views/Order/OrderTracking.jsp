<%-- OrderTracking.jsp — BasaltHouse Customer Order Tracking --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Theo Dõi Đơn Hàng - BasaltHouse</title>
    <meta name="description" content="Xem lịch sử và trạng thái các đơn hàng online của bạn tại BasaltHouse.">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700;900&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link href="${pageContext.request.contextPath}/css/CartCss/OrderTracking.css" rel="stylesheet">
</head>
<body>

<%-- Shared Header --%>
<jsp:include page="/views/HomePage/Header.jsp"/>

<%-- ── Page Header (same pattern as Cart.jsp) ── --%>
<div class="page-header">
    <div class="container">
        <div class="d-flex justify-content-between align-items-start flex-wrap gap-3">
            <div>
                <div class="page-header-badge">
                    <span class="material-symbols-outlined">local_shipping</span>
                    Đơn hàng
                </div>
                <h1>Theo Dõi Đơn Hàng</h1>
                <p>Xem trạng thái và lịch sử tất cả đơn hàng online của bạn</p>
            </div>
            <c:if test="${not empty sessionScope.currentUser}">
                <div class="page-header-meta">
                    <div class="meta-chip">
                        <span class="material-symbols-outlined">receipt_long</span>
                        <span>${totalOrders} đơn hàng</span>
                    </div>
                    <div class="meta-chip">
                        <span class="material-symbols-outlined">check_circle</span>
                        <span>${completedCount} hoàn thành</span>
                    </div>
                </div>
            </c:if>
        </div>
    </div>
</div>

<%-- ── Main Content ── --%>
<main class="order-main">
    <div class="container">

        <%-- ── NOT LOGGED IN ─────────────────────────────────────── --%>
        <c:if test="${empty sessionScope.currentUser}">
            <div class="ot-no-login">
                <span class="material-symbols-outlined">person_off</span>
                <h2>Vui lòng đăng nhập</h2>
                <p>Bạn cần đăng nhập để xem lịch sử đơn hàng của mình.</p>
                <a href="${pageContext.request.contextPath}/login" class="ot-login-btn">
                    <span class="material-symbols-outlined">login</span>
                    Đăng nhập ngay
                </a>
            </div>
        </c:if>

        <%-- ── LOGGED IN ──────────────────────────────────────────── --%>
        <c:if test="${not empty sessionScope.currentUser}">

            <%-- Stats Bar --%>
            <div class="ot-stats">
                <div class="ot-stat-card">
                    <div class="ot-stat-value">${totalOrders}</div>
                    <div class="ot-stat-label">Tổng đơn hàng</div>
                </div>
                <div class="ot-stat-card">
                    <div class="ot-stat-value">${pendingCount}</div>
                    <div class="ot-stat-label">Đang xử lý</div>
                </div>
                <div class="ot-stat-card">
                    <div class="ot-stat-value">${completedCount}</div>
                    <div class="ot-stat-label">Hoàn thành</div>
                </div>
                <div class="ot-stat-card">
                    <div class="ot-stat-value" style="font-size:18px;">
                        <fmt:formatNumber value="${totalSpent}" pattern="#,###"/>₫
                    </div>
                    <div class="ot-stat-label">Tổng chi tiêu</div>
                </div>
            </div>

            <%-- Filter Tabs --%>
            <div class="ot-filter-bar">
                <div class="ot-filter-tab active" onclick="filterOrders(this,'all')">
                    Tất cả
                    <span class="ot-filter-count">${totalOrders}</span>
                </div>
                <div class="ot-filter-tab" onclick="filterOrders(this,'preparing')">
                    <span class="material-symbols-outlined">coffee_maker</span>
                    Đang pha chế
                </div>
                <div class="ot-filter-tab" onclick="filterOrders(this,'shipping')">
                    <span class="material-symbols-outlined">local_shipping</span>
                    Đang giao
                </div>
                <div class="ot-filter-tab" onclick="filterOrders(this,'completed')">
                    <span class="material-symbols-outlined">check_circle</span>
                    Hoàn thành
                </div>
                <div class="ot-filter-tab" onclick="filterOrders(this,'cancelled')">
                    <span class="material-symbols-outlined">cancel</span>
                    Đã hủy
                </div>
            </div>

            <%-- No Orders --%>
            <c:if test="${empty orders}">
                <div class="ot-empty visible">
                    <span class="material-symbols-outlined">shopping_bag</span>
                    <h3>Chưa có đơn hàng nào</h3>
                    <p>Bạn chưa đặt hàng online. Hãy khám phá thực đơn của chúng tôi!</p>
                    <a href="${pageContext.request.contextPath}/category" class="ot-empty-btn">
                        <span class="material-symbols-outlined">storefront</span>
                        Khám phá thực đơn
                    </a>
                </div>
            </c:if>

            <%-- Order Cards --%>
            <c:if test="${not empty orders}">
                <div class="ot-order-list" id="orderList">

                    <c:forEach var="orderInfo" items="${orders}">
                        <c:set var="order"   value="${orderInfo.order}"/>
                        <c:set var="details" value="${orderInfo.details}"/>
                        <c:set var="address" value="${orderInfo.address}"/>

                        <%-- Map status → CSS class --%>
                        <c:set var="sc" value="pending"/>
                        <c:choose>
                            <c:when test="${order.orderStatus == 'Pending'}">     <c:set var="sc" value="pending"/></c:when>
                            <c:when test="${order.orderStatus == 'Preparing'}">   <c:set var="sc" value="preparing"/></c:when>
                            <c:when test="${order.orderStatus == 'In_Progress'}"> <c:set var="sc" value="preparing"/></c:when>
                            <c:when test="${order.orderStatus == 'Ready'}">       <c:set var="sc" value="shipping"/></c:when>
                            <c:when test="${order.orderStatus == 'Delivering'}">  <c:set var="sc" value="shipping"/></c:when>
                            <c:when test="${order.orderStatus == 'Completed'}">   <c:set var="sc" value="completed"/></c:when>
                            <c:when test="${order.orderStatus == 'Cancelled'}">   <c:set var="sc" value="cancelled"/></c:when>
                        </c:choose>

                        <%-- Status label --%>
                        <c:set var="sl" value="Đang xử lý"/>
                        <c:choose>
                            <c:when test="${order.orderStatus == 'Pending'}">     <c:set var="sl" value="Chờ xác nhận"/></c:when>
                            <c:when test="${order.orderStatus == 'Preparing'}">   <c:set var="sl" value="Đang pha chế"/></c:when>
                            <c:when test="${order.orderStatus == 'In_Progress'}"> <c:set var="sl" value="Đang pha chế"/></c:when>
                            <c:when test="${order.orderStatus == 'Ready'}">       <c:set var="sl" value="Sẵn sàng giao"/></c:when>
                            <c:when test="${order.orderStatus == 'Delivering'}">  <c:set var="sl" value="Đang giao hàng"/></c:when>
                            <c:when test="${order.orderStatus == 'Completed'}">   <c:set var="sl" value="Hoàn thành"/></c:when>
                            <c:when test="${order.orderStatus == 'Cancelled'}">   <c:set var="sl" value="Đã hủy"/></c:when>
                        </c:choose>

                        <%-- Status icon --%>
                        <c:set var="si" value="receipt"/>
                        <c:choose>
                            <c:when test="${order.orderStatus == 'Pending'}">                                        <c:set var="si" value="hourglass_empty"/></c:when>
                            <c:when test="${order.orderStatus == 'Preparing' || order.orderStatus == 'In_Progress'}"><c:set var="si" value="coffee_maker"/></c:when>
                            <c:when test="${order.orderStatus == 'Ready'}">                                          <c:set var="si" value="inventory_2"/></c:when>
                            <c:when test="${order.orderStatus == 'Delivering'}">                                     <c:set var="si" value="local_shipping"/></c:when>
                            <c:when test="${order.orderStatus == 'Completed'}">                                      <c:set var="si" value="task_alt"/></c:when>
                            <c:when test="${order.orderStatus == 'Cancelled'}">                                      <c:set var="si" value="cancel"/></c:when>
                        </c:choose>

                        <div class="ot-order-card" data-status="${sc}" id="order-card-${order.orderId}">

                            <%-- Card Header --%>
                            <div class="ot-card-header">
                                <div class="ot-card-header-left">
                                    <div class="ot-order-icon">
                                        <span class="material-symbols-outlined">${si}</span>
                                    </div>
                                    <div class="ot-order-meta">
                                        <h3>#BH-${order.orderId}</h3>
                                        <div class="ot-order-date">
                                            <span class="material-symbols-outlined">calendar_today</span>
                                            <c:if test="${not empty order.createdAt}">
                                                ${fn:substring(order.createdAt.toString(), 0, 16)}
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                                <div class="ot-card-header-right">
                                    <span class="ot-badge ot-badge-${sc}">
                                        <span class="material-symbols-outlined">${si}</span>
                                        ${sl}
                                    </span>
                                    <c:choose>
                                        <c:when test="${order.paymentStatus == 'Paid'}">
                                            <span class="ot-badge ot-badge-paid">
                                                <span class="material-symbols-outlined">check_circle</span>
                                                Đã thanh toán
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="ot-badge ot-badge-unpaid">
                                                <span class="material-symbols-outlined">schedule</span>
                                                Chưa thanh toán
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>

                            <%-- Timeline --%>
                            <c:if test="${order.orderStatus != 'Cancelled'}">
                                <div class="ot-timeline-wrap">
                                    <div class="ot-timeline">

                                        <%-- Step 1: Đặt hàng --%>
                                        <c:set var="s1" value="done"/>
                                        <c:if test="${order.orderStatus == 'Pending'}"><c:set var="s1" value="active"/></c:if>
                                        <div class="ot-tl-step ${s1}">
                                            <div class="ot-tl-dot"><span class="material-symbols-outlined">receipt</span></div>
                                            <div class="ot-tl-label">Đặt hàng</div>
                                        </div>

                                        <%-- Step 2: Pha chế --%>
                                        <c:set var="s2" value=""/>
                                        <c:choose>
                                            <c:when test="${order.orderStatus == 'Preparing' || order.orderStatus == 'In_Progress'}">
                                                <c:set var="s2" value="active"/>
                                            </c:when>
                                            <c:when test="${order.orderStatus == 'Ready' || order.orderStatus == 'Delivering' || order.orderStatus == 'Completed'}">
                                                <c:set var="s2" value="done"/>
                                            </c:when>
                                        </c:choose>
                                        <div class="ot-tl-step ${s2}">
                                            <div class="ot-tl-dot"><span class="material-symbols-outlined">coffee_maker</span></div>
                                            <div class="ot-tl-label">Pha chế</div>
                                        </div>

                                        <%-- Step 3: Giao hàng --%>
                                        <c:set var="s3" value=""/>
                                        <c:choose>
                                            <c:when test="${order.orderStatus == 'Ready' || order.orderStatus == 'Delivering'}">
                                                <c:set var="s3" value="active"/>
                                            </c:when>
                                            <c:when test="${order.orderStatus == 'Completed'}">
                                                <c:set var="s3" value="done"/>
                                            </c:when>
                                        </c:choose>
                                        <div class="ot-tl-step ${s3}">
                                            <div class="ot-tl-dot"><span class="material-symbols-outlined">local_shipping</span></div>
                                            <div class="ot-tl-label">Giao hàng</div>
                                        </div>

                                        <%-- Step 4: Hoàn thành --%>
                                        <c:set var="s4" value=""/>
                                        <c:if test="${order.orderStatus == 'Completed'}"><c:set var="s4" value="done active"/></c:if>
                                        <div class="ot-tl-step ${s4}">
                                            <div class="ot-tl-dot"><span class="material-symbols-outlined">task_alt</span></div>
                                            <div class="ot-tl-label">Hoàn thành</div>
                                        </div>

                                    </div>
                                </div>
                            </c:if>

                            <%-- Cancelled banner --%>
                            <c:if test="${order.orderStatus == 'Cancelled'}">
                                <div class="ot-cancelled-banner">
                                    <span class="material-symbols-outlined">cancel</span>
                                    Đơn hàng này đã bị hủy
                                </div>
                            </c:if>

                            <%-- Toggle detail button --%>
                            <c:if test="${not empty details}">
                                <button class="ot-toggle-btn" id="toggle-${order.orderId}"
                                        onclick="toggleDetails('${order.orderId}')">
                                    <span class="material-symbols-outlined">expand_more</span>
                                    Xem chi tiết (${fn:length(details)} sản phẩm)
                                </button>

                                <div class="ot-collapsible" id="detail-${order.orderId}">
                                    <div class="ot-card-divider"></div>
                                    <div class="ot-card-items">
                                        <c:forEach var="item" items="${details}">
                                            <div class="ot-item-row">
                                                <div class="ot-item-left">
                                                    <span class="ot-item-qty-badge">x${item.quantity}</span>
                                                    <div>
                                                        <div class="ot-item-name">${item.productName}</div>
                                                        <c:if test="${not empty item.sizeName}">
                                                            <div class="ot-item-size">Size ${item.sizeName}</div>
                                                        </c:if>
                                                    </div>
                                                </div>
                                                <div class="ot-item-price">
                                                    <fmt:formatNumber value="${item.unitPrice * item.quantity}" pattern="#,###"/>₫
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                            </c:if>

                            <%-- Card Footer --%>
                            <div class="ot-card-divider"></div>
                            <div class="ot-card-footer">
                                <div class="ot-card-footer-left">
                                    <div class="ot-payment-info">
                                        <span class="material-symbols-outlined">payments</span>
                                        <c:choose>
                                            <c:when test="${order.paymentMethod == 'COD'}">Tiền mặt khi nhận hàng (COD)</c:when>
                                            <c:when test="${order.paymentMethod == 'MOMO'}">Ví MoMo</c:when>
                                            <c:otherwise>${order.paymentMethod}</c:otherwise>
                                        </c:choose>
                                    </div>
                                    <c:if test="${not empty address}">
                                        <div class="ot-delivery-info">
                                            <span class="material-symbols-outlined">location_on</span>
                                            <span>${address.recipientName} · ${address.recipientPhone} · ${address.addressDetail}</span>
                                        </div>
                                    </c:if>
                                </div>
                                <div class="ot-total-block">
                                    <div class="ot-total-label">Thành tiền</div>
                                    <div class="ot-total-amount">
                                        <fmt:formatNumber value="${order.finalAmount != null ? order.finalAmount : order.totalAmount}" pattern="#,###"/>₫
                                    </div>
                                    <c:if test="${order.discountAmount != null && order.discountAmount > 0}">
                                        <div class="ot-discount-note">
                                            Tiết kiệm <fmt:formatNumber value="${order.discountAmount}" pattern="#,###"/>₫
                                        </div>
                                    </c:if>
                                </div>
                            </div>

                        </div><%-- end ot-order-card --%>
                    </c:forEach>

                </div><%-- end ot-order-list --%>

                <%-- Empty state for filter --%>
                <div class="ot-empty" id="filterEmpty">
                    <span class="material-symbols-outlined">filter_list_off</span>
                    <h3>Không có đơn hàng</h3>
                    <p>Không tìm thấy đơn hàng ở trạng thái này.</p>
                </div>
            </c:if>

        </c:if><%-- end logged in --%>

    </div>
</main>

<jsp:include page="/views/HomePage/Footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
function filterOrders(tabEl, filter) {
    document.querySelectorAll('.ot-filter-tab').forEach(t => t.classList.remove('active'));
    tabEl.classList.add('active');
    const cards = document.querySelectorAll('.ot-order-card');
    const empty = document.getElementById('filterEmpty');
    let visible = 0;
    cards.forEach(card => {
        const show = filter === 'all' || card.dataset.status === filter;
        card.style.display = show ? '' : 'none';
        if (show) visible++;
    });
    if (empty) empty.classList.toggle('visible', visible === 0);
}

function toggleDetails(orderId) {
    const panel = document.getElementById('detail-' + orderId);
    const btn   = document.getElementById('toggle-' + orderId);
    if (!panel) return;
    const open = panel.classList.toggle('open');
    btn.classList.toggle('open', open);
    btn.querySelector('.material-symbols-outlined').textContent = open ? 'expand_less' : 'expand_more';
    const txt = btn.querySelector('span:last-child') || btn.childNodes[btn.childNodes.length - 1];
    // update text label
    const match = btn.textContent.match(/\d+/);
    const count = match ? match[0] : '';
    btn.childNodes[btn.childNodes.length - 1].textContent = open
        ? ' Ẩn chi tiết'
        : ' Xem chi tiết (' + count + ' sản phẩm)';
}
</script>
</body>
</html>
