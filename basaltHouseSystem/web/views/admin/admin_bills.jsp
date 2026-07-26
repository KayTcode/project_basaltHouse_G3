<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản lý Hóa Đơn - Admin</title>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/meyer-reset/2.0/reset.min.css" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/admin_common.css?v=4">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/admin_bills.css?v=4">
    </head>
    <body class="admin-dashboard-body">
        <jsp:include page="header.jsp"/>

        <div class="app-container">
            <jsp:include page="sidebar.jsp"/>

            <main class="main-content">
                <div class="content-wrapper">
                    <div class="page-header-bar">
                        <div>
                            <h2 class="page-title">Quản lý hóa đơn</h2>
                            <p class="page-sub">Theo dõi tất cả hóa đơn đã thanh toán.</p>
                        </div>

                    </div>

                    <form class="filter-form" action="${pageContext.request.contextPath}/admin/bills" method="get">
                        <div class="form-group">
                            <label>Ngày thanh toán</label>
                            <input type="date" name="filterDate" value="${filterDate}">
                        </div>
                        <div class="form-group">
                            <label>Thanh toán</label>
                            <select name="filterPayment">
                                <option value="">Tất cả</option>
                                <option value="Cash"    ${filterPayment == 'Cash'    ? 'selected' : ''}>Tiền mặt (Cash)</option>
                                <option value="QR Code" ${filterPayment == 'QR Code' ? 'selected' : ''}>Chuyển khoản (QR Code / MOMO)</option>
                                <option value="COD"     ${filterPayment == 'COD'     ? 'selected' : ''}>Khi nhận hàng (COD)</option>
                            </select>
                        </div>
                        <button type="submit">Lọc hóa đơn</button>
                        <a href="${pageContext.request.contextPath}/admin/bills" class="btn-secondary">Xoá lọc</a>
                    </form>

                    <div class="panel">
                        <c:choose>
                            <c:when test="${empty bills}">
                                <div class="empty-state">
                                    <i class="fas fa-receipt fa-3x" style="color: #d1d5db; margin-bottom: 15px;"></i>
                                    <p>Không tìm thấy hóa đơn nào.</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <table class="data-table" id="billsTable">
                                    <thead>
                                        <tr>
                                            <th>Mã Bill</th>
                                            <th>Ngày giờ</th>
                                            <th>Loại đơn</th>
                                            <th>Thu ngân</th>
                                            <th>Tiền gốc</th>
                                            <th>Giảm giá</th>
                                            <th>Thành tiền</th>
                                            <th>Thanh toán</th>
                                            <th>Hành động</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="b" items="${bills}">
                                            <tr>
                                                <td><strong>#${b.billId}</strong>
                                                    <c:if test="${not empty b.billCode}">
                                                        <div style="font-size:11px;color:#6b7280;">${b.billCode}</div>
                                                    </c:if>
                                                </td>
                                                <td>${b.formattedPrintedAt}</td>
                                                <td>
                                                    <span class="badge ${b.orderType == 'Dine-in' || b.orderType == 'Dine-In' ? 'badge-info' : 'badge-warning'}">
                                                        ${b.orderType == 'Offline' || b.orderType == 'offline' ? 'POS' : b.orderType}
                                                    </span>
                                                </td>
                                                <td>${b.cashierName}</td>
                                                <td class="currency"><fmt:formatNumber value="${b.subTotal}" type="currency" currencySymbol="đ" maxFractionDigits="0"/></td>
                                                <td class="currency" style="color:#ef4444;">
                                                    <c:choose>
                                                        <c:when test="${b.discountAmount != null && b.discountAmount.doubleValue() > 0}">
                                                            -<fmt:formatNumber value="${b.discountAmount}" type="currency" currencySymbol="đ" maxFractionDigits="0"/>
                                                        </c:when>
                                                        <c:otherwise>---</c:otherwise>
                                                    </c:choose>
                                                </td>
                                                 <td class="currency"><strong><fmt:formatNumber value="${b.finalAmount}" type="currency" currencySymbol="đ" maxFractionDigits="0"/></strong></td>
                                                 <td style="white-space:nowrap;"><strong>${b.paymentMethod}</strong></td>
                                                 <td style="white-space:nowrap;">
                                                     <a href="${pageContext.request.contextPath}/admin/bill-detail?id=${b.billId}" style="text-decoration:none;color:#3b82f6;font-weight:600;font-size:14px;display:inline-flex;align-items:center;gap:4px;white-space:nowrap;">
                                                         <i class="fas fa-eye"></i> Xem chi tiết
                                                     </a>
                                                 </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>


                                <c:if test="${totalPages > 1}">
                                    <div class="pagination" style="padding: 20px; display: flex; justify-content: center; gap: 8px;">
                                        <c:url value="/admin/bills" var="prevUrl">
                                            <c:param name="page" value="${currentPage - 1}"/>
                                            <c:if test="${not empty filterDate}"><c:param name="filterDate" value="${filterDate}"/></c:if>
                                            <c:if test="${not empty filterPayment}"><c:param name="filterPayment" value="${filterPayment}"/></c:if>
                                        </c:url>
                                        <a href="${prevUrl}" class="btn-secondary ${currentPage <= 1 ? 'disabled' : ''}" style="padding: 6px 12px; height: auto;">&#171;</a>

                                        <c:forEach begin="1" end="${totalPages}" var="pg">
                                            <c:url value="/admin/bills" var="pageUrl">
                                                <c:param name="page" value="${pg}"/>
                                                <c:if test="${not empty filterDate}"><c:param name="filterDate" value="${filterDate}"/></c:if>
                                                <c:if test="${not empty filterPayment}"><c:param name="filterPayment" value="${filterPayment}"/></c:if>
                                            </c:url>
                                            <a href="${pageUrl}" class="btn-secondary ${pg == currentPage ? 'active' : ''}"
                                               style="padding: 6px 12px; height: auto; ${pg == currentPage ? 'background:#6366f1;color:white;border-color:#6366f1;' : ''}">${pg}</a>
                                        </c:forEach>

                                        <c:url value="/admin/bills" var="nextUrl">
                                            <c:param name="page" value="${currentPage + 1}"/>
                                            <c:if test="${not empty filterDate}"><c:param name="filterDate" value="${filterDate}"/></c:if>
                                            <c:if test="${not empty filterPayment}"><c:param name="filterPayment" value="${filterPayment}"/></c:if>
                                        </c:url>
                                        <a href="${nextUrl}" class="btn-secondary ${currentPage >= totalPages ? 'disabled' : ''}" style="padding: 6px 12px; height: auto;">&#187;</a>
                                    </div>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </main>
        </div>
    </body>
    <script>
    </script>
</html>
