<%-- AddShipper.jsp - Danh sách Shipper đang hoạt động --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@page import="java.util.List"%>
<%@page import="model.Shipper"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chọn Shipper | Basalt House</title>
        <meta name="description" content="Danh sách shipper đang hoạt động - Basalt House Cashier">
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/CashierCss/CashierNew.css?v=7" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/CashierCss/AddShipper.css?v=5" rel="stylesheet">
    </head>
    <body>

        <main class="content-area">

            <div class="page-header">
                <div class="page-title">
                    <h1>
                        Shipper hoạt động
                        <c:if test="${not empty activeShippers}">
                            <span class="count-badge">${activeShippers.size()}</span>
                        </c:if>
                    </h1>

                    <c:if test="${not empty orderId}">
                        <p class="assign-hint">
                            <span class="material-symbols-outlined">local_shipping</span>
                            Đang gán shipper cho đơn hàng <strong>#${orderId}</strong> — chọn 1 shipper bên dưới
                        </p>
                    </c:if>
                </div>
                <div class="page-actions">
                    <a href="${pageContext.request.contextPath}/cashier/oderview" class="btn-back">
                        <span class="material-symbols-outlined">arrow_back</span>
                        Quay lại
                    </a>
                    <div class="page-date">
                        <span class="material-symbols-outlined" style="font-size:16px">calendar_month</span>
                        <span id="todayDate"></span>
                    </div>
                </div>
            </div>

            <div class="card">
                <div class="card-header">
                    <span class="card-title">Danh sách shipper đang sẵn sàng nhận đơn</span>
                </div>

                <div class="shipper-grid">
                    <c:choose>
                        <c:when test="${empty activeShippers}">
                            <div class="empty-state">
                                <span class="material-symbols-outlined">delivery_dining</span>
                                <p>Hiện không có shipper nào đang hoạt động</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach items="${activeShippers}" var="s">
                                <div class="shipper-card">
                                    <div class="shipper-card-header">
                                        <div class="shipper-avatar">
                                            <c:choose>
                                                <c:when test="${not empty s.avatarUrl}">
                                                    <img src="${s.avatarUrl}" alt="${s.fullName}">
                                                </c:when>
                                                <c:otherwise>
                                                    ${s.fullName.substring(0,1).toUpperCase()}
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div>
                                            <div class="shipper-name">${s.fullName}</div>
                                            <div class="shipper-id">#SHP${s.shipperId}</div>
                                        </div>
                                    </div>
                                    <div class="shipper-meta">
                                        <c:if test="${not empty s.phone}">
                                            <div class="shipper-meta-row">
                                                <span class="material-symbols-outlined">call</span>
                                                ${s.phone}
                                            </div>
                                        </c:if>
                                        <c:if test="${not empty s.address}">
                                            <div class="shipper-meta-row">
                                                <span class="material-symbols-outlined">location_on</span>
                                                ${s.address}
                                            </div>
                                        </c:if>
                                    </div>
                                    <div class="shipper-card-footer">
                                        <div class="available-badge">Sẵn sàng</div>
                                        <%-- Nút Chọn chỉ hiện khi có orderId --%>
                                        <c:if test="${not empty orderId}">
                                            <form method="post" action="${pageContext.request.contextPath}/cashier/shippers" style="margin:0;">
                                                <input type="hidden" name="orderId"   value="${orderId}">
                                                <input type="hidden" name="shipperId" value="${s.shipperId}">
                                                <button type="submit" class="btn-select-shipper">
                                                    <span class="material-symbols-outlined">check_circle</span>
                                                    Chọn
                                                </button>
                                            </form>
                                        </c:if>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

        </main>

        <script>
            (function () {
                const d = new Date();
                const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
                document.getElementById('todayDate').textContent =
                        d.getDate() + ' ' + months[d.getMonth()] + ' ' + d.getFullYear();
            })();
        </script>
    </body>
</html>
