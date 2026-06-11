<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<%
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    dto.UserLoginDTO _user = (dto.UserLoginDTO) session.getAttribute("currentUser");
    String _initials = "C";
    if (_user != null && _user.getFullName() != null && !_user.getFullName().trim().isEmpty()) {
        String[] _parts = _user.getFullName().trim().split("\\s+");
        if (_parts.length >= 2) {
            _initials = String.valueOf(_parts[0].charAt(0)).toUpperCase()
                      + String.valueOf(_parts[_parts.length - 1].charAt(0)).toUpperCase();
        } else {
            _initials = _user.getFullName().substring(0, Math.min(2, _user.getFullName().length())).toUpperCase();
        }
    }
    pageContext.setAttribute("initials", _initials);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh Toán Tại Quầy (POS) — BasaltHouse</title>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700;900&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/cashier/cashier.css" rel="stylesheet" type="text/css">
</head>
<body>
<div class="cashier-page">

    <%-- Top Nav --%>
    <nav class="cashier-nav">
        <a href="${pageContext.request.contextPath}/cashier/dashboard" class="cashier-nav-brand">BasaltHouse</a>
        <div class="cashier-nav-links">
            <a href="${pageContext.request.contextPath}/cashier/dashboard" class="cashier-nav-link">
                <span class="material-symbols-outlined">dashboard</span>Dashboard
            </a>
            <a href="${pageContext.request.contextPath}/cashier/pos" class="cashier-nav-link active">
                <span class="material-symbols-outlined">point_of_sale</span>Thanh toán
            </a>
        </div>
        <div class="cashier-nav-user">
            <div class="cashier-nav-avatar"><c:out value="${initials}"/></div>
            <span><c:out value="${sessionScope.currentUser.fullName}"/></span>
            <form method="POST" action="${pageContext.request.contextPath}/logout" style="margin:0; display:flex;">
                <button type="submit" class="btn-nav-logout" title="Đăng xuất">
                    <span class="material-symbols-outlined">logout</span>
                </button>
            </form>
        </div>
    </nav>

    <%-- Main Container --%>
    <main class="cashier-main">
        <h1 class="section-title">
            <span class="material-symbols-outlined">point_of_sale</span>
            Thanh Toán Tại Quầy
        </h1>

        <c:if test="${not empty flashMsg}">
            <div class="flash-alert ${flashType}" id="flashMsg">
                <span class="material-symbols-outlined">
                    <c:choose>
                        <c:when test="${flashType eq 'success'}">check_circle</c:when>
                        <c:otherwise>error</c:otherwise>
                    </c:choose>
                </span>
                <span><c:out value="${flashMsg}"/></span>
            </div>
        </c:if>

        <div class="pos-layout">
            <%-- LEFT PANEL --%>
            <aside class="order-list-panel">
                <div class="panel-header">
                    <div style="display: flex; align-items: center; gap: 0.5rem;">
                        <span class="material-symbols-outlined">receipt_long</span>
                        <span>Đơn chờ thanh toán</span>
                    </div>
                    <span class="order-count-badge"><c:out value="${fn:length(unpaidOrders)}"/></span>
                </div>

                <div class="order-list">
                    <c:choose>
                        <c:when test="${empty unpaidOrders}">
                            <div class="no-orders">
                                <span class="material-symbols-outlined">coffee</span>
                                <p>Không có đơn hàng nào<br>đang chờ thanh toán</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="ord" items="${unpaidOrders}">
                                <a href="${pageContext.request.contextPath}/cashier/pos?orderId=${ord.orderId}"
                                   class="order-item ${selectedOrder != null && selectedOrder.orderId == ord.orderId ? 'active' : ''}">
                                    <div class="order-item-id">#<c:out value="${ord.orderId}"/></div>
                                    <div class="order-item-type">
                                        <span class="badge-type ${ord.orderType eq 'POS' ? 'pos' : 'online'}">
                                            <c:out value="${ord.orderType}"/>
                                        </span>
                                    </div>
                                    <div class="order-item-amount">
                                        <c:out value="${String.format('%,.0f', ord.finalAmount.doubleValue())}"/>đ
                                    </div>
                                </a>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </aside>

            <%-- RIGHT PANEL --%>
            <section class="order-detail-panel">
                <c:choose>
                    <c:when test="${empty selectedOrder}">
                        <div class="cashier-card empty-state" style="flex:1;">
                            <span class="material-symbols-outlined">touch_app</span>
                            <p>Chọn một đơn hàng từ danh sách bên trái<br>để xem chi tiết và tiến hành thanh toán.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="cashier-card">
                            <div class="detail-header">
                                <div>
                                    <div class="detail-order-id">Đơn hàng #<c:out value="${selectedOrder.orderId}"/></div>
                                    <div class="detail-order-time"><c:out value="${selectedOrder.createdAt}"/></div>
                                </div>
                                <div class="detail-badges">
                                    <span class="status-badge unpaid">Chưa thanh toán</span>
                                    <span class="status-badge type-info"><c:out value="${selectedOrder.orderType}"/></span>
                                </div>
                            </div>
                        </div>

                        <div class="cashier-card">
                            <table class="items-table">
                                <thead>
                                    <tr>
                                        <th>Sản phẩm</th>
                                        <th>Size</th>
                                        <th style="text-align:center;">SL</th>
                                        <th style="text-align:right;">Đơn giá</th>
                                        <th style="text-align:right;">Thành tiền</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="item" items="${orderDetails}">
                                        <tr>
                                            <td>
                                                <div class="product-cell"><c:out value="${item.productName}"/></div>
                                                <c:if test="${not empty item.note}"><div class="note-cell"><c:out value="${item.note}"/></div></c:if>
                                            </td>
                                            <td><span class="size-cell"><c:out value="${item.sizeName}"/></span></td>
                                            <td style="text-align:center; font-weight:600;"><c:out value="${item.quantity}"/></td>
                                            <td class="price-cell"><c:out value="${String.format('%,.0f', item.unitPrice.doubleValue())}"/>đ</td>
                                            <td class="subtotal-cell"><c:out value="${String.format('%,.0f', item.unitPrice * item.quantity)}"/>đ</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <div class="cashier-card">
                            <h2 class="card-title-secondary">Mã khuyến mãi</h2>
                            <form method="POST" action="${pageContext.request.contextPath}/cashier/pos">
                                <input type="hidden" name="action" value="applyDiscount">
                                <input type="hidden" name="orderId" value="${selectedOrder.orderId}">
                                <div class="discount-form">
                                    <input type="text" id="discountCode" name="discountCode" class="input-discount" placeholder="Nhập mã ưu đãi..." autocomplete="off">
                                    <button type="submit" class="btn-apply">Áp dụng</button>
                                </div>
                            </form>
                            <c:if test="${selectedOrder.discountId != null}">
                                <p class="discount-success-msg">Hệ thống đã ghi nhận mã giảm giá thành công!</p>
                            </c:if>
                        </div>

                        <div class="cashier-card">
                            <div class="payable-summary">
                                <div class="payable-row"><span>Tổng tiền gốc</span><span><c:out value="${String.format('%,.0f', selectedOrder.totalAmount.doubleValue())}"/>đ</span></div>
                                <div class="payable-row"><span>Chiết khấu</span><span class="payable-value discount">−<c:out value="${String.format('%,.0f', selectedOrder.discountAmount.doubleValue())}"/>đ</span></div>
                                <div class="payable-row total"><span>Số tiền thực thu</span><span class="payable-value final"><c:out value="${String.format('%,.0f', selectedOrder.finalAmount.doubleValue())}"/>đ</span></div>
                            </div>
                            <form method="POST" action="${pageContext.request.contextPath}/cashier/pos">
                                <input type="hidden" name="action" value="checkout">
                                <input type="hidden" name="orderId" value="${selectedOrder.orderId}">
                                <div class="payment-methods">
                                    <input type="radio" name="paymentMethod" id="pm-cash" value="Cash" class="payment-radio" checked>
                                    <label for="pm-cash" class="payment-label">Tiền mặt</label>
                                    <input type="radio" name="paymentMethod" id="pm-qr" value="QR Banking" class="payment-radio">
                                    <label for="pm-qr" class="payment-label">Chuyển khoản QR</label>
                                </div>
                                <button type="submit" class="btn-checkout" onclick="return confirm('Xác nhận thanh toán đơn này?')">Xác nhận thanh toán</button>
                            </form>
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>
        </div>
    </main>
</div>
</body>
</html>