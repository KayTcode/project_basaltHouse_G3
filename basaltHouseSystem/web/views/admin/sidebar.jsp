<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/admin_sidebar.css">

<c:set var="currentURI" value="${requestScope['jakarta.servlet.forward.request_uri']}" />
<c:if test="${empty currentURI}">
    <c:set var="currentURI" value="${pageContext.request.requestURI}" />
</c:if>

<aside class="sidebar" id="adminSidebar">
    <div class="sidebar-menu">

        <%-- ── USER PROFILE SECTION ── --%>
        <div class="user-section">
            <div class="user-avatar">A</div>
            <div class="user-info">
                <strong>viethacker_admin</strong>
                <span class="status-online">
                    <span class="status-dot"></span>
                    Hệ Thống Trực Tuyến
                </span>
            </div>
        </div>

        <%-- ══ TỔNG QUAN ══ --%>
        <div class="menu-section">
            <div class="section-header">Tổng Quan</div>
            <a href="${pageContext.request.contextPath}/admin/dashboard"
               class="menu-item ${currentURI.contains('/admin/dashboard') ? 'active' : ''}">
                <span class="menu-item-icon">📊</span>
                Dashboard
            </a>
        </div>

        <%-- ══ HỒ SƠ & TÀI KHOẢN ══ --%>
        <div class="menu-section">
            <div class="section-header">Hồ Sơ &amp; Tài Khoản</div>
            <a href="${pageContext.request.contextPath}/admin/accounts"
               class="menu-item ${currentURI.contains('/admin/accounts') ? 'active' : ''}">
                <span class="menu-item-icon">👤</span>
                Tài khoản
            </a>
<%--            <a href="${pageContext.request.contextPath}/admin/staffs"
               class="menu-item ${currentURI.contains('/admin/staffs') ? 'active' : ''}">
                <span class="menu-item-icon">👥</span>
                Nhân sự
            </a>--%>
            <a href="${pageContext.request.contextPath}/admin/customers"
               class="menu-item ${currentURI.contains('/admin/customers') ? 'active' : ''}">
                <span class="menu-item-icon">🛍️</span>
                Khách hàng
            </a>
        </div>

        <%-- ══ MENU & GIAO DỊCH ══ --%>
        <div class="menu-section">
            <div class="section-header">Menu &amp; Giao Dịch</div>
            <a href="${pageContext.request.contextPath}/admin/products"
               class="menu-item ${currentURI.contains('/admin/products') ? 'active' : ''}">
                <span class="menu-item-icon">☕</span>
                Sản phẩm
            </a>
            <a href="${pageContext.request.contextPath}/admin/orders"
               class="menu-item ${currentURI.contains('/admin/orders') ? 'active' : ''}">
                <span class="menu-item-icon">🛒</span>
                Đơn hàng
            </a>
            <a href="${pageContext.request.contextPath}/admin/tables"
               class="menu-item ${currentURI.contains('/admin/tables') ? 'active' : ''}">
                <span class="menu-item-icon">🪑</span>
                Bàn &amp; Phiên
            </a>
            <a href="${pageContext.request.contextPath}/admin/bills"
               class="menu-item ${currentURI.contains('/admin/bills') ? 'active' : ''}">
                <span class="menu-item-icon">📋</span>
                Bills
            </a>
        </div>

        <%-- ══ KHO VẬN & TÀI CHÍNH ══ --%>
        <div class="menu-section">
            <div class="section-header">Kho Vận &amp; Tài Chính</div>
            <a href="${pageContext.request.contextPath}/admin/ingredients"
               class="menu-item ${currentURI.contains('/admin/ingredients') ? 'active' : ''}">
                <span class="menu-item-icon">📦</span>
                Kho hàng
            </a>
            <a href="${pageContext.request.contextPath}/admin/finance"
               class="menu-item ${currentURI.contains('/admin/finance') ? 'active' : ''}">
                <span class="menu-item-icon">💰</span>
                Tài chính
            </a>
            <a href="${pageContext.request.contextPath}/admin/discounts"
               class="menu-item ${currentURI.contains('/admin/discounts') ? 'active' : ''}">
                <span class="menu-item-icon">🎁</span>
                Khuyến mãi
            </a>
        </div>

        <%-- ══ THÀNH VIÊN & ĐÁNH GIÁ ══ --%>
        <div class="menu-section">
            <div class="section-header">Thành Viên &amp; Đánh Giá</div>
            <a href="${pageContext.request.contextPath}/admin/memberships"
               class="menu-item ${currentURI.contains('/admin/memberships') ? 'active' : ''}">
                <span class="menu-item-icon">💎</span>
                Membership
            </a>
            <a href="${pageContext.request.contextPath}/admin/reviews"
               class="menu-item ${currentURI.contains('/admin/reviews') ? 'active' : ''}">
                <span class="menu-item-icon">⭐</span>
                Đánh giá
            </a>
            <a href="${pageContext.request.contextPath}/admin/logs"
               class="menu-item ${currentURI.contains('/admin/logs') ? 'active' : ''}">
                <span class="menu-item-icon">🔔</span>
                Logs &amp; Thông báo
            </a>
        </div>

        <%-- ══ CẤU HÌNH & VẬN CHUYỂN ══ --%>
        <div class="menu-section">
            <div class="section-header">Cấu Hình &amp; Vận Chuyển</div>
            <a href="${pageContext.request.contextPath}/admin/shipping-zones"
               class="menu-item ${currentURI.contains('/admin/shipping-zones') ? 'active' : ''}">
                <span class="menu-item-icon">🚚</span>
                Vùng giao hàng
            </a>

        </div>

    </div><%-- end sidebar-menu --%>



</aside>

<script>
    /* Highlight active menu item with slight delay for smooth entry */
    (function () {
        const items = document.querySelectorAll('#adminSidebar .menu-item');
        items.forEach(function (el, i) {
            el.style.animationDelay = (i * 0.03) + 's';
        });
    })();
</script>