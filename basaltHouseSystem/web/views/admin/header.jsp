<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/admin_header.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

<header class="top-header" id="adminTopHeader">

    <%-- ── LOGO / BRAND ── --%>
    <a href="${pageContext.request.contextPath}/admin/dashboard" class="header-brand">
        <div class="header-logo-icon">☕</div>
        <div class="header-logo-text">
            <span class="logo-name">Basalt <span>House</span></span>
            <span class="logo-tagline">Admin Panel</span>
        </div>
    </a>


    <%-- ── RIGHT ACTIONS ── --%>
    <div class="header-actions">

        <%-- Server time chip --%>
        <div class="server-time-chip">
            <span class="clock-dot"></span>
            <span id="liveTime">
                <fmt:formatDate value="<%= new java.util.Date() %>" pattern="dd/MM/yyyy – HH:mm" />
            </span>
        </div>

        <div class="header-divider"></div>

        <%-- Refresh page --%>
        <button class="hdr-btn" title="Làm mới trang" onclick="location.reload()">
            <i class="fa-solid fa-rotate-right"></i>
        </button>

        <%-- Notifications --%>
        <a href="${pageContext.request.contextPath}/admin/logs?tab=notifications"
           class="hdr-btn" title="Thông báo">
            <i class="fa-solid fa-bell"></i>
            <span class="notif-badge" id="notiCount">0</span>
        </a>


        <%-- Profile / Account --%>
        <a href="${pageContext.request.contextPath}/home" class="hdr-profile">
            <div class="hdr-profile-avatar">🏠️</div>

        </a>

    </div>
</header>

<script>
    /* Live clock – cập nhật mỗi giây */
    (function () {
        const el = document.getElementById('liveTime');
        if (!el) return;
        function pad(n) { return n < 10 ? '0' + n : n; }
        function tick() {
            const now = new Date();
            const d   = pad(now.getDate()) + '/' + pad(now.getMonth() + 1) + '/' + now.getFullYear();
            const t   = pad(now.getHours()) + ':' + pad(now.getMinutes()) + ':' + pad(now.getSeconds());
            el.textContent = d + ' – ' + t;
        }
        tick();
        setInterval(tick, 1000);
    })();
</script>