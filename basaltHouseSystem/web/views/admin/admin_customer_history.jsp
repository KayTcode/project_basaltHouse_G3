<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Lịch Sử Mua Hàng - BasaltHouse Admin</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_common.css?v=2">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_account.css?v=2">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_customer.css?v=4">
    </head>
    <body class="admin-dashboard-body">

        <jsp:include page="header.jsp" />

        <div class="app-container">
            <jsp:include page="sidebar.jsp" />

            <main class="main-content">

                <!-- Breadcrumb quay lại -->
                <div class="viewport-headline-bar">
                    <div class="headline-left">
                        <h1 class="page-title">
                            <a href="${pageContext.request.contextPath}/admin/customers"
                               style="color: inherit; text-decoration: none; margin-right: 8px;">
                                <i class="fa-solid fa-arrow-left" style="font-size: 18px;"></i>
                            </a>
                            Lịch Sử Mua Hàng
                        </h1>
                        <p class="page-desc">
                            Khách hàng: <strong>${historyData.customerName}</strong>
                            &mdash; Email: <strong>${historyData.email}</strong>
                        </p>
                    </div>
                </div>

                <div class="data-management-panel">
                    <div class="panel-header-toolbar">
                        <h2><i class="fa-solid fa-clock-rotate-left"></i> Danh Sách Đơn Hàng</h2>
                    </div>

                    <div class="responsive-table-wrapper">
                        <table class="basalt-custom-table">
                            <thead>
                                <tr>
                                    <th>Mã Đơn</th>
                                    <th>Loại</th>
                                    <th style="text-align:center;">Trạng Thái</th>
                                    <th style="text-align:center;">Thanh Toán</th>
                                    <th style="text-align:right;">Giảm Giá</th>
                                    <th style="text-align:right;">Thành Tiền</th>
                                    <th>Thời Gian</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty historyData.orders}">
                                        <tr>
                                            <td colspan="7" style="text-align:center; padding: 50px 0; color: #aaa;">
                                                <i class="fa-regular fa-face-frown" style="font-size: 28px; display: block; margin-bottom: 10px;"></i>
                                                Khách hàng chưa có đơn hàng nào.
                                            </td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="order" items="${historyData.orders}">
                                            <tr class="account-row-item">
                                                <td><code>#${order.orderId}</code></td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${order.orderType == 'Online'}">Online</c:when>
                                                        <c:when test="${order.orderType == 'Dine-In'}">Tại quầy</c:when>
                                                        <c:otherwise>${order.orderType}</c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td style="text-align:center;">
                                                    <c:choose>
                                                        <c:when test="${order.orderStatus == 'Completed'}">
                                                            <span class="history-status-badge order-status--completed">Hoàn tất</span>
                                                        </c:when>
                                                        <c:when test="${order.orderStatus == 'Cancelled'}">
                                                            <span class="history-status-badge order-status--cancelled">Hủy</span>
                                                        </c:when>
                                                        <c:when test="${order.orderStatus == 'Pending'}">
                                                            <span class="history-status-badge order-status--pending">Chờ xử lý</span>
                                                        </c:when>
                                                        <c:when test="${order.orderStatus == 'Preparing'}">
                                                            <span class="history-status-badge order-status--progress">Chuẩn bị</span>
                                                        </c:when>
                                                        <c:when test="${order.orderStatus == 'Delivering'}">
                                                            <span class="history-status-badge order-status--progress">Giao hàng</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="history-status-badge">${order.orderStatus}</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td style="text-align:center;">
                                                    <c:choose>
                                                        <c:when test="${order.paymentStatus == 'Paid'}">
                                                            <span class="history-status-badge pay-status--paid">Đã TT</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="history-status-badge pay-status--unpaid">Chưa TT</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td class="cell-right" style="color: #666;">
                                                    <fmt:formatNumber value="${order.discountAmount}" type="currency" currencySymbol="đ" maxFractionDigits="0"/>
                                                </td>
                                                <td class="cell-right history-amount">
                                                    <fmt:formatNumber value="${order.finalAmount}" type="currency" currencySymbol="đ" maxFractionDigits="0"/>
                                                </td>
                                                <td class="history-time">
                                                    <c:catch var="err">
                                                        <fmt:formatDate value="${order.createdAtDate}" pattern="dd/MM/yyyy HH:mm"/>
                                                    </c:catch>
                                                    <c:if test="${not empty err}">${order.createdAt}</c:if>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>

            </main>
        </div>
    </body>
</html>
