<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<%
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    dto.UserLoginDTO _user = (dto.UserLoginDTO) session.getAttribute("currentUser");
    String _initials = "C";
    if (_user != null && _user.getFullName() != null && !_user.getFullName().trim().isEmpty()) {
        String[] _parts = _user.getFullName().trim().split("\\s+");
        if (_parts.length >= 2) {
            _initials = String.valueOf(_parts[0].charAt(0)).toUpperCase()
                      + String.valueOf(_parts[_parts.length - 1].charAt(0)).toUpperCase();
        } else {
            _initials = _user.getFullName().substring(0, Math.min(2, _user.getFullName().length())).toUpperCase();
        }
    }
    pageContext.setAttribute("initials", _initials);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Thu Ngân — BasaltHouse</title>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700;900&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/cashier/cashier.css" rel="stylesheet" type="text/css">
</head>
<body>
<div class="cashier-page">

    <%-- Nav Bar --%>
    <nav class="cashier-nav">
        <a href="${pageContext.request.contextPath}/cashier/dashboard" class="cashier-nav-brand">BasaltHouse</a>
        <div class="cashier-nav-links">
            <a href="${pageContext.request.contextPath}/cashier/dashboard" class="cashier-nav-link active">
                <span class="material-symbols-outlined">dashboard</span>Dashboard
            </a>
            <a href="${pageContext.request.contextPath}/cashier/pos" class="cashier-nav-link">
                <span class="material-symbols-outlined">point_of_sale</span>Thanh toán
            </a>
        </div>
        <div class="cashier-nav-user">
            <div class="cashier-nav-avatar"><c:out value="${initials}"/></div>
            <span><c:out value="${sessionScope.currentUser.fullName}"/></span>
            <form method="POST" action="${pageContext.request.contextPath}/logout" style="margin:0; display:flex;">
                <button type="submit" class="btn-nav-logout" title="Đăng xuất">
                    <span class="material-symbols-outlined">logout</span>
                </button>
            </form>
        </div>
    </nav>

    <%-- Main Content --%>
    <main class="cashier-main">
        <h1 class="section-title">
            <span class="material-symbols-outlined">dashboard</span>
            Tổng quan ngày hôm nay
        </h1>

        <%-- Flash Alert --%>
        <c:if test="${not empty flashMsg}">
            <div class="flash-alert ${flashType}" id="flashMsg">
                <span class="material-symbols-outlined">
                    <c:choose>
                        <c:when test="${flashType eq 'success'}">check_circle</c:when>
                        <c:otherwise>error</c:otherwise>
                    </c:choose>
                </span>
                <span><c:out value="${flashMsg}"/></span>
            </div>
        </c:if>

        <%-- Stat Grid --%>
        <div class="stat-grid">
            <div class="stat-card">
                <div class="stat-icon"><span class="material-symbols-outlined">receipt_long</span></div>
                <div class="stat-info">
                    <div class="stat-label">Đơn chờ thanh toán</div>
                    <div class="stat-value"><c:out value="${unpaidCount != null ? unpaidCount : 0}"/></div>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon"><span class="material-symbols-outlined">monetization_on</span></div>
                <div class="stat-info">
                    <div class="stat-label">Doanh thu tại quầy</div>
                    <div class="stat-value">
                        <%-- Giải pháp sửa lỗi 500: Ép kiểu sang Double bằng nhân với 1.0 trước khi Format --%>
                        <c:out value="${String.format('%,.0f', (todayRevenue != null ? todayRevenue : 0) * 1.0)}"/>đ
                    </div>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon"><span class="material-symbols-outlined">check_circle</span></div>
                <div class="stat-info">
                    <div class="stat-label">Đơn đã hoàn tất</div>
                    <div class="stat-value"><c:out value="${completedCount != null ? completedCount : 0}"/></div>
                </div>
            </div>
        </div>

        <%-- Quick Action Buttons --%>
        <div class="quick-actions">
            <a href="${pageContext.request.contextPath}/cashier/pos" class="quick-action-btn">
                <div class="action-icon"><span class="material-symbols-outlined">point_of_sale</span></div>
                <div class="quick-action-btn-info">
                    <span>Mở Màn Hình POS</span>
                    <span class="quick-action-btn-desc">Tiến hành thanh toán đơn cho khách hàng</span>
                </div>
            </a>
        </div>

        <%-- Đơn hàng mới cập nhật gần đây --%>
        <div class="cashier-card">
            <h2 class="card-title-primary">
                <span class="material-symbols-outlined">history</span>
                Đơn hàng mới cập nhật gần đây
            </h2>
            <c:choose>
                <c:when test="${empty recentOrders}">
                    <p class="text-muted">Chưa ghi nhận đơn hàng nào trong ca làm việc.</p>
                </c:when>
                <c:otherwise>
                    <table class="dashboard-table">
                        <thead>
                            <tr>
                                <th>Mã đơn</th>
                                <th>Thời gian</th>
                                <th>Loại đơn</th>
                                <th>Trạng thái</th>
                                <th style="text-align: right;">Tổng thanh toán</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="o" items="${recentOrders}">
                                <tr>
                                    <td><strong>#<c:out value="${o.orderId}"/></strong></td>
                                    <td><c:out value="${o.createdAt}"/></td>
                                    <td>
                                        <span class="badge-type ${o.orderType eq 'POS' ? 'pos' : 'online'}">
                                            <c:out value="${o.orderType}"/>
                                        </span>
                                    </td>
                                    <td>
                                        <span class="status-pill ${o.paymentStatus eq 'Paid' ? 'paid' : 'unpaid'}">
                                            <c:out value="${o.paymentStatus eq 'Paid' ? 'Đã thanh toán' : 'Chưa thanh toán'}"/>
                                        </span>
                                    </td>
                                    <td style="text-align: right; font-weight: 600;">
                                        <c:out value="${String.format('%,.0f', o.finalAmount.doubleValue())}"/>đ
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </main>
</div>

<script>
    var flash = document.getElementById('flashMsg');
    if (flash) {
        setTimeout(function () {
            flash.style.transition = 'opacity .5s, transform .5s';
            flash.style.opacity = '0';
            flash.style.transform = 'translateY(-10px)';
            setTimeout(function () { flash.remove(); }, 500);
        }, 4000);
    }
</script>
</body>
</html>