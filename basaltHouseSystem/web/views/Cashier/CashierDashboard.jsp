<%-- CashierDashboard_New.jsp - Màn hình Dashboard Thu Ngân --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@page import="java.util.List"%>
<%@page import="model.Order"%>
<%@page import="dao.OrderDAO"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%
    OrderDAO oDao = new OrderDAO();
    List<Order> recentOrders = oDao.getAllOrdersWithCustomerName();
    if(recentOrders.size() > 5) recentOrders = recentOrders.subList(0, 5);
    request.setAttribute("recentOrders", recentOrders);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Thu Ngân | Basalt House</title>
    <meta name="description" content="Màn hình Dashboard Thu Ngân - Basalt House POS">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/CashierCss/CashierNew.css?v=2" rel="stylesheet">
</head>
<body>

<!-- ── SIDEBAR ── -->
<aside class="sidebar">
    <div class="sidebar-logo">
        <div class="logo-icon">☕</div>
        <div class="logo-text">
            Basalt
            <span>House Coffee</span>
        </div>
    </div>

    <nav class="sidebar-nav">
        <a href="${pageContext.request.contextPath}/DashBoard" class="nav-item active" id="nav-dashboard">
            <span class="nav-icon material-symbols-outlined">dashboard</span>
            Dashboard
        </a>
        <a href="${pageContext.request.contextPath}/OrderView" class="nav-item" id="nav-orders">
            <span class="nav-icon material-symbols-outlined">receipt_long</span>
            Orders
        </a>
        <a href="${pageContext.request.contextPath}/PosOrder" class="nav-item" id="nav-create">
            <span class="nav-icon material-symbols-outlined">point_of_sale</span>
            POS Order
        </a>
        <a href="#" class="nav-item" id="nav-reports">
            <span class="nav-icon material-symbols-outlined">bar_chart</span>
            Reports
        </a>
        <a href="#" class="nav-item" id="nav-settings">
            <span class="nav-icon material-symbols-outlined">settings</span>
            Settings
        </a>
    </nav>

    <div class="sidebar-footer">
        <div class="staff-card">
            <div class="staff-avatar">
                <span class="material-symbols-outlined" style="font-size:18px">person</span>
            </div>
            <div class="staff-info">
                <div class="staff-name">Cashier</div>
                <div class="staff-status">
                    <div class="status-dot"></div>
                    Online
                </div>
            </div>
        </div>
    </div>
</aside>

<!-- ── CONTENT AREA ── -->
<main class="content-area">

    <!-- Page Header -->
    <div class="page-header">
        <div class="page-title">
            <h1>Dashboard</h1>
            <p>Overview of today's business</p>
        </div>
        <div class="page-date">
            <span class="material-symbols-outlined" style="font-size:16px;color:#8b5e3c">calendar_month</span>
            <span id="todayDate">20 May 2025</span>
        </div>
    </div>

    <!-- Stats Grid -->
    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-label">Doanh thu hôm nay</div>
            <div class="stat-value"><fmt:formatNumber value="${dashboardStats.todayRevenue}" type="number" maxFractionDigits="0"/> đ</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Đơn hàng hôm nay</div>
            <div class="stat-value">${dashboardStats.todayOrders}</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Đơn chờ xử lý</div>
            <div class="stat-value">${dashboardStats.pendingOrders}</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Khách hàng mới</div>
            <div class="stat-value">${dashboardStats.newCustomers}</div>
        </div>
    </div>

    <!-- Recent Orders Table -->
    <div class="card">
        <div class="card-header">
            <span class="card-title">Đơn hàng gần đây</span>
            <a href="${pageContext.request.contextPath}/views/Cashier/OrderViews.jsp" class="card-link">Xem tất cả</a>
        </div>
        <table class="data-table">
            <thead>
                <tr>
                    <th>Mã đơn</th>
                    <th>Loại đơn</th>
                    <th>Khách hàng</th>
                    <th>Trạng thái</th>
                    <th>Thời gian</th>
                    <th>Tổng tiền</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty recentOrders}">
                        <tr><td colspan="6" style="text-align:center;">Chưa có đơn hàng nào</td></tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${recentOrders}" var="o">
                            <%
                                Order currentO = (Order) pageContext.getAttribute("o");
                                String fTime = "00:00";
                                if (currentO.getCreatedAt() != null) {
                                    fTime = currentO.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm"));
                                }
                            %>
                            <tr>
                                <td><strong>ORD00${o.orderId}</strong></td>
                                <td><span class="badge badge-${o.orderType != null ? o.orderType.toLowerCase() : 'offline'}">${o.orderType != null ? o.orderType : 'Offline'}</span></td>
                                <td>${o.customerName != null ? o.customerName : 'Walk-in'}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${o.orderStatus == 'Pending Payment'}"><span class="badge badge-pending-payment">Pending Payment</span></c:when>
                                        <c:when test="${o.orderStatus == 'Preparing'}"><span class="badge badge-preparing">Preparing</span></c:when>
                                        <c:when test="${o.orderStatus == 'In_Progress'}"><span class="badge badge-preparing">In Progress</span></c:when>
                                        <c:when test="${o.orderStatus == 'Ready'}"><span class="badge badge-ready">Ready</span></c:when>
                                        <c:when test="${o.orderStatus == 'Completed'}"><span class="badge badge-completed">Completed</span></c:when>
                                        <c:otherwise><span class="badge">${o.orderStatus}</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td><%=fTime%></td>
                                <td><strong><fmt:formatNumber value="${o.finalAmount}" type="number" maxFractionDigits="0"/> đ</strong></td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>

</main>

<script>
// Update today date
(function() {
    const d = new Date();
    const months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
    document.getElementById('todayDate').textContent =
        d.getDate() + ' ' + months[d.getMonth()] + ' ' + d.getFullYear();
})();
</script>
</body>
</html>
