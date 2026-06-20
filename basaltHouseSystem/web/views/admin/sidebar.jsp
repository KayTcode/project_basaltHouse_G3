<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- Tự động lấy Request URI hiện tại để kiểm tra active menu --%>
<c:set var="currentURI" value="${requestScope['jakarta.servlet.forward.request_uri']}" />
<c:if test="${empty currentURI}">
    <c:set var="currentURI" value="${pageContext.request.requestURI}" />
</c:if>

<aside class="sidebar">
    <div class="sidebar-menu">
        <div class="user-section">
            <div class="user-avatar">A</div>
            <div class="user-info">
                <strong>viethacker_admin</strong>
                <span class="status-online">● Hệ Thống Trực Tuyến</span>
            </div>
        </div>

        <div class="menu-section">
            <div class="section-header">TỔNG QUAN</div>
            <a href="${pageContext.request.contextPath}/admin/dashboard" 
               class="menu-item ${currentURI.contains('/admin/dashboard') ? 'active' : ''}">
                📊 Dashboard
            </a>
        </div>

        <div class="menu-section">
            <div class="section-header">HỒ SƠ & TÀI KHOẢN</div>
            <a href="${pageContext.request.contextPath}/admin/accounts" 
               class="menu-item ${currentURI.contains('/admin/accounts') ? 'active' : ''}">
                👤 Tài khoản
            </a>
            <a href="${pageContext.request.contextPath}/admin/staffs" 
               class="menu-item ${currentURI.contains('/admin/staffs') ? 'active' : ''}">
                👥 Nhân sự
            </a>
            <a href="${pageContext.request.contextPath}/admin/customers" 
               class="menu-item ${currentURI.contains('/admin/customers') ? 'active' : ''}">
                🛍️ Khách hàng
            </a>
        </div>

        <div class="menu-section">
            <div class="section-header">MENU & GIAO DỊCH</div>
            <a href="${pageContext.request.contextPath}/admin/products" 
               class="menu-item ${currentURI.contains('/admin/products') ? 'active' : ''}">
                ☕ Sản phẩm
            </a>
            <a href="${pageContext.request.contextPath}/admin/orders" 
               class="menu-item ${currentURI.contains('/admin/orders') ? 'active' : ''}">
                🛒 Đơn hàng
            </a>
            <a href="${pageContext.request.contextPath}/admin/tables" 
               class="menu-item ${currentURI.contains('/admin/tables') ? 'active' : ''}">
                🪑 Bàn & Phiên
            </a>
            <a href="${pageContext.request.contextPath}/admin/bills" 
               class="menu-item ${currentURI.contains('/admin/bills') ? 'active' : ''}">
                📋 Bills
            </a>
        </div>

        <div class="menu-section">
            <div class="section-header">KHO VẬN & TÀI CHÍNH</div>
            <a href="${pageContext.request.contextPath}/admin/ingredients" 
               class="menu-item ${currentURI.contains('/admin/ingredients') ? 'active' : ''}">
                📦 Kho hàng
            </a>
            <a href="${pageContext.request.contextPath}/admin/finance" 
               class="menu-item ${currentURI.contains('/admin/finance') ? 'active' : ''}">
                💰 Tài chính
            </a>
            <a href="${pageContext.request.contextPath}/admin/discounts" 
               class="menu-item ${currentURI.contains('/admin/discounts') ? 'active' : ''}">
                🎁 Khuyến mãi
            </a>
        </div>

        <div class="menu-section">
            <div class="section-header">THÀNH VIÊN & ĐÁNH GIÁ</div>
            <a href="${pageContext.request.contextPath}/admin/memberships" 
               class="menu-item ${currentURI.contains('/admin/memberships') ? 'active' : ''}">
                💎 Membership
            </a>
            <a href="${pageContext.request.contextPath}/admin/reviews" 
               class="menu-item ${currentURI.contains('/admin/reviews') ? 'active' : ''}">
                ⭐ Đánh giá
            </a>
            <a href="${pageContext.request.contextPath}/admin/logs" 
               class="menu-item ${currentURI.contains('/admin/logs') ? 'active' : ''}">
                🔔 Logs & Noti
            </a>
        </div>

        <div class="menu-section">
            <div class="section-header">CẤU HÌNH & VẬN CHUYỂN</div>
            <a href="${pageContext.request.contextPath}/admin/shipping-zones" 
               class="menu-item ${currentURI.contains('/admin/shipping-zones') ? 'active' : ''}">
                🚚 Vùng giao hàng
            </a>
            <a href="${pageContext.request.contextPath}/admin/settings" 
               class="menu-item ${currentURI.contains('/admin/settings') ? 'active' : ''}">
                ⚙️ Cài đặt hệ thống
            </a>
        </div>
    </div>
</aside>