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


<div class="content-area">

   
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

   
    <div class="stats-bar" style="display:flex; align-items:center; justify-content:flex-start; flex-wrap:wrap; gap:24px;">
        <div class="stat-chip done" style="max-width: 400px;">
            <span class="stat-icon">&#127881;</span>
            <div>
                <div class="stat-label">
                    Tổng đơn đã hoàn thành 
                    <span style="font-weight:500; color:#64748b; font-size:12.5px;">
                        <c:choose>
                            <c:when test="${selectedHistoryDate eq todayDateStr}">(Hôm nay)</c:when>
                            <c:otherwise>(${selectedHistoryDate})</c:otherwise>
                        </c:choose>
                    </span>
                </div>
                <div class="stat-value">${totalHistory}</div>
            </div>
        </div>

        <form method="GET" action="${pageContext.request.contextPath}/bartender/history" style="display:flex; align-items:center; gap:12px; flex-wrap:wrap;">
            <div style="display:flex; align-items:center; gap:6px;">
                <label style="font-size:13px; font-weight:600; color:#374151; display:flex; align-items:center; gap:6px;">
                    <span class="material-symbols-outlined" style="font-size:18px; color:#006e2f;">filter_list</span> Loại đơn:
                </label>
                <select name="orderType" onchange="this.form.submit()"
                        style="padding:7.5px 14px; border:1.5px solid #cbd5e1; border-radius:12px; font-size:13.5px; font-family:'Inter',sans-serif; outline:none; cursor:pointer; background:#fff; color:#1e293b; font-weight:600; box-shadow:0 2px 6px rgba(0,0,0,0.04); transition:all 0.2s ease;">
                    <option value="all" ${selectedOrderType eq 'all' or empty selectedOrderType ? 'selected' : ''}>Tất cả loại đơn</option>
                    <option value="POS" ${selectedOrderType eq 'POS' ? 'selected' : ''}>Đơn máy POS</option>
                    <option value="Online" ${selectedOrderType eq 'Online' ? 'selected' : ''}>Đơn Online</option>
                </select>
            </div>

            <div style="display:flex; align-items:center; gap:6px;">
                <label style="font-size:13px; font-weight:600; color:#374151; display:flex; align-items:center; gap:6px;">
                    <span class="material-symbols-outlined" style="font-size:18px; color:#006e2f;">calendar_month</span> Chọn ngày:
                </label>
                <input type="date" name="historyDate" value="${selectedHistoryDate}" onchange="this.form.submit()" 
                       style="padding:7px 14px; border:1.5px solid #cbd5e1; border-radius:12px; font-size:13.5px; font-family:'Inter',sans-serif; outline:none; cursor:pointer; background:#fff; color:#1e293b; font-weight:600; box-shadow:0 2px 6px rgba(0,0,0,0.04); transition:all 0.2s ease;" 
                       onfocus="this.style.borderColor='#006e2f'" onblur="this.style.borderColor='#cbd5e1'" />
            </div>
        </form>
    </div>

    
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
                        <%
                            String bHistoryType = currentOrder.getOrderType() != null ? currentOrder.getOrderType().toLowerCase() : "pos";
                            String bHistoryCode = String.format("%s%03d", "pos".equals(bHistoryType) ? "POS" : "ONL", currentOrder.getOrderId());
                        %>
                        <div class="card-id">#<%=bHistoryCode%></div>
                        <div class="card-meta">
                            <span class="card-loc"><span class="material-symbols-outlined" style="font-size:12px">location_on</span>
                                ${o.tableName != null && !o.tableName.isEmpty() ? o.tableName : (o.orderType != null && o.orderType.equalsIgnoreCase("online") ? "Online" : "Walk-in")}
                            </span>
                            <span class="card-type-badge ${o.orderType != null ? o.orderType.toLowerCase() : 'pos'}">
                                ${o.orderType != null ? o.orderType : 'POS'}
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
            <div class="empty-history" style="grid-column: 1 / -1; text-align:center; padding:48px 20px; background:#fff; border-radius:16px; border:1px dashed #cbd5e1;">
                <span class="material-symbols-outlined" style="font-size:48px;margin-bottom:10px;color:#cbd5e1;">history</span>
                <div>Chưa có đơn hàng nào hoàn thành vào ngày ${selectedHistoryDate}</div>
            </div>
        </c:if>
    </div>

   
    <c:if test="${historyPages >= 1 and not empty orderList}">
    <div class="pagination" style="padding:20px 0; display:flex; justify-content:center; align-items:center; gap:8px;">
        <a href="?page=<%=historyPage - 1%><c:if test="${not empty selectedHistoryDate}">&historyDate=${selectedHistoryDate}</c:if><c:if test="${not empty selectedOrderType}">&orderType=${selectedOrderType}</c:if>"
           class="btn-page <%= historyPage <= 1 ? "disabled" : "" %>">&#171;</a>

        <c:forEach begin="1" end="${historyPages}" var="pg">
            <a href="?page=${pg}<c:if test="${not empty selectedHistoryDate}">&historyDate=${selectedHistoryDate}</c:if><c:if test="${not empty selectedOrderType}">&orderType=${selectedOrderType}</c:if>"
               class="btn-page ${pg == historyPage ? 'active' : ''}">${pg}</a>
        </c:forEach>

        <a href="?page=<%=historyPage + 1%><c:if test="${not empty selectedHistoryDate}">&historyDate=${selectedHistoryDate}</c:if><c:if test="${not empty selectedOrderType}">&orderType=${selectedOrderType}</c:if>"
           class="btn-page <%= historyPage >= historyPages ? "disabled" : "" %>">&#187;</a>
    </div>
    </c:if>

</div>

</body>
</html>
