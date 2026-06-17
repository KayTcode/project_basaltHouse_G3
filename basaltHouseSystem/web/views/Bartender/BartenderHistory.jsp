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
    OrderDAO oDao = new OrderDAO();
    List<Order> orderList = oDao.getCompletedOrders();
    request.setAttribute("orderList", orderList);
    
    ProductDAO pDao = new ProductDAO();
    HashMap<Integer, Product> products = pDao.getProduct();
    request.setAttribute("products", products);
    
    SizeDAO sDao = new SizeDAO();
    HashMap<Integer, String> sizes = sDao.getSize();
    request.setAttribute("sizes", sizes);
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
    <link href="${pageContext.request.contextPath}/css/BartenderCss/BartenderNew.css?v=4" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/BartenderCss/BartenderViews.css?v=4" rel="stylesheet">
    <style>
        .history-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
            gap: 20px;
            padding: 20px;
        }
        .order-card.completed {
            border: 1px solid #d1d5db;
            background: #fff;
        }
        .order-card.completed .card-top {
            background: #f3f4f6;
            color: #374151;
        }
        .empty-history {
            grid-column: 1 / -1;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            padding: 60px 0;
            color: #9ca3af;
            font-size: 16px;
        }
    </style>
</head>
<body>

<!-- ── SIDEBAR ── -->
<aside class="sidebar">
    <div class="sidebar-logo">
        <div class="logo-icon">&#9749;</div>
        <div class="logo-text">Basalt<span>House Coffee</span></div>
    </div>
    <nav class="sidebar-nav">
        <a href="${pageContext.request.contextPath}/views/Bartender/BartenderViews.jsp" class="nav-item">
            <span class="nav-icon material-symbols-outlined">view_kanban</span>Prep Board
        </a>
        <a href="${pageContext.request.contextPath}/views/Bartender/BartenderHistory.jsp" class="nav-item active">
            <span class="nav-icon material-symbols-outlined">history</span>History
        </a>
        <a href="#" class="nav-item">
            <span class="nav-icon material-symbols-outlined">settings</span>Settings
        </a>
    </nav>
    <div class="sidebar-footer">
        <div class="staff-card">
            <div class="staff-avatar"><span class="material-symbols-outlined" style="font-size:18px">person</span></div>
            <div class="staff-info">
                <div class="staff-name">Bartender</div>
                <div class="staff-status"><div class="status-dot"></div>Online</div>
            </div>
        </div>
    </div>
</aside>

<!-- ── CONTENT ── -->
<div class="content-area">

    <!-- Stats bar -->
    <div class="stats-bar">
        <div class="stat-chip done" style="width:250px;">
            <span class="stat-icon">&#127881;</span>
            <div>
                <div class="stat-label">Tổng đơn đã hoàn thành</div>
                <div class="stat-value"><%=orderList.size()%></div>
            </div>
        </div>
    </div>

    <!-- History Grid -->
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
        
        <% if (orderList.isEmpty()) { %>
            <div class="empty-history">
                <span class="material-symbols-outlined" style="font-size: 48px; margin-bottom: 10px; color:#cbd5e1;">history</span>
                Chưa có đơn hàng nào hoàn thành
            </div>
        <% } %>
    </div>
</div><!-- /content-area -->

</body>
</html>
