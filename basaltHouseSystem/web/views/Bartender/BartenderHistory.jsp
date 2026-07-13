<%-- BartenderHistory.jsp - Lịch sử pha chế --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@page import="java.util.List"%>
<%@page import="model.Order"%>
<%@page import="model.OrderDetail"%>
<%@page import="model.Product"%>
<%@page import="dao.OrderDAO"%>
<%@page import="dao.ProductDAO"%>
<%@page import="dao.SizeDAO"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%
    // Lấy DAO để truy xuất order detail
    OrderDAO oDao = new OrderDAO();
    
    // Lấy danh sách sản phẩm và size được truyền từ Controller
    HashMap<Integer, Product> products = (HashMap<Integer, Product>) request.getAttribute("products");
    HashMap<Integer, String> sizes     = (HashMap<Integer, String>) request.getAttribute("sizes");

    int historyPage  = request.getAttribute("historyPage") != null ? (Integer) request.getAttribute("historyPage") : 1;
    int historyPages = request.getAttribute("historyPages") != null ? (Integer) request.getAttribute("historyPages") : 1;
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>History | Basalt House Coffee</title>
    <meta name="description" content="Lịch sử pha chế Bartender">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/BartenderCss/BartenderNew.css?v=6" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/BartenderCss/BartenderViews.css?v=9" rel="stylesheet">
</head>
<body>

<!-- ── CONTENT ── -->
<div class="content-area">

    <!-- ── TOP ACTIONS ── -->
    <div class="bartender-top-actions">
        <a href="${pageContext.request.contextPath}/cashier/pos" class="btn-top-action pos">
            <span class="material-symbols-outlined">point_of_sale</span>
            Quay lại máy POS
        </a>
        <a href="${pageContext.request.contextPath}/bartender/view" class="btn-top-action history">
            <span class="material-symbols-outlined">view_kanban</span>
            Bảng pha chế (Prep Board)
        </a>
    </div>

    <!-- Stats bar -->
    <div class="stats-bar">
        <div class="stat-chip done" style="max-width: 320px;">
            <span class="stat-icon">&#127881;</span>
            <div>
                <div class="stat-label">Tổng đơn đã hoàn thành</div>
                <div class="stat-value">${totalHistory}</div>
            </div>
        </div>
    </div>

    <!-- History Grid — chỉ render items của trang hiện tại từ pageResult -->
    <div class="history-grid">
        <c:forEach items="${orderList}" var="o">
            <%
                Order currentOrder = (Order) pageContext.getAttribute("o");
                String fTime = "00:00";
                if (currentOrder.getCreatedAt() != null) {
                    fTime = currentOrder.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy"));
                }
            %>
            <div class="order-card completed">
                <div class="card-top completed">
                    <div>
                        <div class="card-id">#ORD00${o.orderId}</div>
                        <div class="card-meta">
                            <span class="card-loc"><span class="material-symbols-outlined" style="font-size:12px">location_on</span>
                                ${o.tableName != null && !o.tableName.isEmpty() ? o.tableName : (o.orderType != null && o.orderType.equalsIgnoreCase("online") ? "Online" : "Walk-in")}
                            </span>
                            <span class="card-type-badge ${o.orderType != null ? o.orderType.toLowerCase() : 'offline'}">
                                ${o.orderType != null ? o.orderType : 'Offline'}
                            </span>
                        </div>
                    </div>
                    <div><span class="wait-badge ok" style="background:#dcfce7;color:#166534;border:none;">&#10003; <%=fTime%></span></div>
                </div>

                <div class="card-items">
                    <%
                       List<OrderDetail> detailsList = oDao.getOrderDetailsByOrderId(currentOrder.getOrderId());
                       for (OrderDetail d : detailsList) {
                           Product p = products.get(d.getProductId());
                           String pName = p != null ? p.getProductName() : "Unknown";
                           String sName = sizes.get(d.getSizeId());
                    %>
                    <div class="item-row">
                        <span class="item-emoji">&#9749;</span>
                        <div class="item-info">
                            <div class="item-name"><%=pName%></div>
                            <div class="item-badges">
                                <% if (sName != null && !"-".equals(sName) && !sName.isEmpty()) { %>
                                    <span class="size-tag <%=sName%>">Size <%=sName%></span>
                                <% } %>
                            </div>
                        </div>
                        <span class="item-qty">x<%=d.getQuantity()%></span>
                    </div>
                    <% } %>
                </div>

                <c:if test="${not empty o.note}">
                    <div class="card-note">
                        <span class="material-symbols-outlined" style="font-size:13px;flex-shrink:0">edit_note</span>
                        ${o.note}
                    </div>
                </c:if>
            </div>
        </c:forEach>

        <c:if test="${empty orderList}">
            <div class="empty-history">
                <span class="material-symbols-outlined" style="font-size:48px;margin-bottom:10px;color:#cbd5e1;">history</span>
                Chưa có đơn hàng nào hoàn thành
            </div>
        </c:if>
    </div>

    <!-- Phân trang History — render từ server, dùng cùng CSS .pagination / .btn-page -->
    <c:if test="${historyPages > 1}">
    <div class="pagination" style="padding:20px 0;">
        <a href="?page=<%=historyPage - 1%>"
           class="btn-page <%= historyPage <= 1 ? "disabled" : "" %>">&#171;</a>

        <c:forEach begin="1" end="${historyPages}" var="pg">
            <a href="?page=${pg}"
               class="btn-page ${pg == historyPage ? 'active' : ''}">${pg}</a>
        </c:forEach>

        <a href="?page=<%=historyPage + 1%>"
           class="btn-page <%= historyPage >= historyPages ? "disabled" : "" %>">&#187;</a>
    </div>
    </c:if>

</div><!-- /content-area -->

</body>
</html>
