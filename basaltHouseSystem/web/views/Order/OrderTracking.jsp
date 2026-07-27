<%-- OrderTracking.jsp — BasaltHouse Customer Order Tracking --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<%
    request.setAttribute("pageTitle", "Theo Dõi Đơn Hàng - BasaltHouse");
    request.setAttribute("pageDescription", "Xem lịch sử và trạng thái các đơn hàng online của bạn tại BasaltHouse.");
    request.setAttribute("pageStylesheet", "/css/CartCss/OrderTracking.css?v=20260709-1");
%>

<%-- Shared Header --%>
<jsp:include page="/views/HomePage/Header.jsp"/>

<%-- ── Page Header (same pattern as Cart.jsp) ── --%>
<div class="page-header">
    <div class="container">
        <div class="d-flex justify-content-between align-items-start flex-wrap gap-3">
            <div>
                <div class="page-header-badge">
                    <span class="material-symbols-outlined">local_shipping</span>
                    Đơn hàng
                </div>
                <h1>Theo Dõi Đơn Hàng</h1>
                <p>Xem trạng thái và lịch sử tất cả đơn hàng online của bạn</p>
            </div>
            <c:if test="${not empty sessionScope.currentUser}">
                <div class="page-header-meta">
                    <div class="meta-chip">
                        <span class="material-symbols-outlined">receipt_long</span>
                        <span>${totalOrders} đơn hàng</span>
                    </div>
                    <div class="meta-chip">
                        <span class="material-symbols-outlined">check_circle</span>
                        <span>${completedCount} hoàn thành</span>
                    </div>
                </div>
            </c:if>
        </div>
    </div>
</div>

<%-- ── Main Content ── --%>
<main class="order-main">
    <div class="container">

        <%-- ── NOT LOGGED IN ─────────────────────────────────────── --%>
        <c:if test="${empty sessionScope.currentUser}">
            <div class="ot-no-login">
                <span class="material-symbols-outlined">person_off</span>
                <h2>Vui lòng đăng nhập</h2>
                <p>Bạn cần đăng nhập để xem lịch sử đơn hàng của mình.</p>
                <a href="${pageContext.request.contextPath}/login" class="ot-login-btn">
                    <span class="material-symbols-outlined">login</span>
                    Đăng nhập ngay
                </a>
            </div>
        </c:if>

        <%-- ── LOGGED IN ──────────────────────────────────────────── --%>
        <c:if test="${not empty sessionScope.currentUser}">

            <%-- Stats Bar --%>
            <div class="ot-stats">
                <div class="ot-stat-card">
                    <div class="ot-stat-value">${totalOrders}</div>
                    <div class="ot-stat-label">Tổng đơn hàng</div>
                </div>
                <div class="ot-stat-card">
                    <div class="ot-stat-value">${pendingCount}</div>
                    <div class="ot-stat-label">Đang xử lý</div>
                </div>
                <div class="ot-stat-card">
                    <div class="ot-stat-value">${completedCount}</div>
                    <div class="ot-stat-label">Hoàn thành</div>
                </div>
                <div class="ot-stat-card">
                    <div class="ot-stat-value" style="font-size:18px;">
                        <fmt:formatNumber value="${totalSpent}" pattern="#,###"/>₫
                    </div>
                    <div class="ot-stat-label">Tổng chi tiêu</div>
                </div>
            </div>

            <%-- Toast thông báo hủy đơn từ server --%>
            <c:if test="${not empty sessionScope.cancelSuccess}">
                <div class="ot-toast ot-toast-success">
                    <span class="material-symbols-outlined">check_circle</span>
                    ${sessionScope.cancelSuccess}
                </div>
                <c:remove var="cancelSuccess" scope="session"/>
            </c:if>
            <c:if test="${not empty sessionScope.cancelError}">
                <div class="ot-toast ot-toast-error">
                    <span class="material-symbols-outlined">error</span>
                    ${sessionScope.cancelError}
                </div>
                <c:remove var="cancelError" scope="session"/>
            </c:if>

            <%-- Filter Tabs --%>
            <div class="ot-filter-bar">
                <div class="ot-filter-tab active" data-filter="all" onclick="filterOrders(this,'all')">
                    Tất cả
                    <span class="ot-filter-count">${totalOrders}</span>
                </div>
                <div class="ot-filter-tab" data-filter="preparing" onclick="filterOrders(this,'preparing')">
                    <span class="material-symbols-outlined">coffee_maker</span>
                    Đang pha chế
                </div>
                <div class="ot-filter-tab" data-filter="shipping" onclick="filterOrders(this,'shipping')">
                    <span class="material-symbols-outlined">local_shipping</span>
                    Đang giao
                </div>
                <div class="ot-filter-tab" data-filter="completed" onclick="filterOrders(this,'completed')">
                    <span class="material-symbols-outlined">check_circle</span>
                    Hoàn thành
                </div>
                <div class="ot-filter-tab" data-filter="cancelled" onclick="filterOrders(this,'cancelled')">
                    <span class="material-symbols-outlined">cancel</span>
                    Đã hủy
                </div>
            </div>

            <%-- No Orders --%>
            <c:if test="${empty orders}">
                <div class="ot-empty visible">
                    <span class="material-symbols-outlined">shopping_bag</span>
                    <h3>Chưa có đơn hàng nào</h3>
                    <p>Bạn chưa đặt hàng online. Hãy khám phá thực đơn của chúng tôi!</p>
                    <a href="${pageContext.request.contextPath}/category" class="ot-empty-btn">
                        <span class="material-symbols-outlined">storefront</span>
                        Khám phá thực đơn
                    </a>
                </div>
            </c:if>

            <%-- Order Cards --%>
            <c:if test="${not empty orders}">
                <div class="ot-order-list" id="orderList">

                    <c:forEach var="orderInfo" items="${orders}">
                        <c:set var="order"   value="${orderInfo.order}"/>
                        <c:set var="details" value="${orderInfo.details}"/>
                        <c:set var="address" value="${orderInfo.address}"/>

                        <c:set var="placedTime" value=""/>
                        <c:set var="confirmedTime" value=""/>
                        <c:set var="deliveringTime" value=""/>
                        <c:set var="completedTime" value=""/>

                        <c:if test="${not empty order.createdAt}">
                            <c:set var="dtStr" value="${order.createdAt.toString()}"/>
                            <c:set var="placedTime" value="${fn:substring(dtStr, 11, 16)} ${fn:substring(dtStr, 8, 10)}/${fn:substring(dtStr, 5, 7)}"/>
                        </c:if>

                        <c:forEach var="log" items="${orderInfo.deliveryLogs}">
                            <c:if test="${not empty log.shipperConfirmedAt}">
                                <c:set var="cfStr" value="${log.shipperConfirmedAt.toString()}"/>
                                <c:set var="confirmedTime" value="${fn:substring(cfStr, 11, 16)} ${fn:substring(cfStr, 8, 10)}/${fn:substring(cfStr, 5, 7)}"/>
                            </c:if>
                            <c:if test="${not empty log.pickedUpAt}">
                                <c:set var="puStr" value="${log.pickedUpAt.toString()}"/>
                                <c:set var="deliveringTime" value="${fn:substring(puStr, 11, 16)} ${fn:substring(puStr, 8, 10)}/${fn:substring(puStr, 5, 7)}"/>
                            </c:if>
                            <c:if test="${not empty log.deliveredAt}">
                                <c:set var="dvStr" value="${log.deliveredAt.toString()}"/>
                                <c:set var="completedTime" value="${fn:substring(dvStr, 11, 16)} ${fn:substring(dvStr, 8, 10)}/${fn:substring(dvStr, 5, 7)}"/>
                            </c:if>
                            <c:if test="${not empty log.customerConfirmedAt}">
                                <c:set var="dvStr" value="${log.customerConfirmedAt.toString()}"/>
                                <c:set var="completedTime" value="${fn:substring(dvStr, 11, 16)} ${fn:substring(dvStr, 8, 10)}/${fn:substring(dvStr, 5, 7)}"/>
                            </c:if>
                        </c:forEach>

                        <%-- Map status → CSS class --%>
                        <c:set var="sc" value="pending"/>
                        <c:choose>
                            <c:when test="${order.orderStatus == 'Pending'}">     <c:set var="sc" value="pending"/></c:when>
                            <c:when test="${order.orderStatus == 'Preparing'}">   <c:set var="sc" value="preparing"/></c:when>
                            <c:when test="${order.orderStatus == 'In_Progress'}"> <c:set var="sc" value="preparing"/></c:when>
                            <c:when test="${order.orderStatus == 'Ready' || order.orderStatus == 'Waiting_Shipper'}"> <c:set var="sc" value="preparing"/></c:when>
                            <c:when test="${order.orderStatus == 'Delivering' || order.orderStatus == 'Delivered'}">  <c:set var="sc" value="shipping"/></c:when>
                            <c:when test="${order.orderStatus == 'Completed'}">   <c:set var="sc" value="completed"/></c:when>
                            <c:when test="${order.orderStatus == 'Cancelled'}">   <c:set var="sc" value="cancelled"/></c:when>
                        </c:choose>

                        <%-- Status badge class --%>
                        <c:set var="sb" value="${sc}"/>
                        <c:if test="${order.orderStatus == 'Delivered'}">
                            <c:set var="sb" value="delivered"/>
                        </c:if>

                        <%-- Status label --%>
                        <c:set var="sl" value="Đang xử lý"/>
                        <c:choose>
                            <c:when test="${order.orderStatus == 'Pending'}">     <c:set var="sl" value="Chờ xác nhận"/></c:when>
                            <c:when test="${order.orderStatus == 'Preparing'}">   <c:set var="sl" value="Đang pha chế"/></c:when>
                            <c:when test="${order.orderStatus == 'In_Progress'}"> <c:set var="sl" value="Đang pha chế"/></c:when>
                            <c:when test="${order.orderStatus == 'Ready' || order.orderStatus == 'Waiting_Shipper'}">       <c:set var="sl" value="Sẵn sàng giao"/></c:when>
                            <c:when test="${order.orderStatus == 'Delivering'}">  <c:set var="sl" value="Đang giao hàng"/></c:when>
                            <c:when test="${order.orderStatus == 'Delivered'}">   <c:set var="sl" value="Đã giao - Chờ xác nhận"/></c:when>
                            <c:when test="${order.orderStatus == 'Completed'}">   <c:set var="sl" value="Hoàn thành"/></c:when>
                            <c:when test="${order.orderStatus == 'Cancelled'}">   <c:set var="sl" value="Đã hủy"/></c:when>
                        </c:choose>

                        <%-- Status icon --%>
                        <c:set var="si" value="receipt"/>
                        <c:choose>
                            <c:when test="${order.orderStatus == 'Pending'}">                                        <c:set var="si" value="hourglass_empty"/></c:when>
                            <c:when test="${order.orderStatus == 'Preparing' || order.orderStatus == 'In_Progress'}"><c:set var="si" value="coffee_maker"/></c:when>
                            <c:when test="${order.orderStatus == 'Ready' || order.orderStatus == 'Waiting_Shipper'}">                                      <c:set var="si" value="inventory_2"/></c:when>
                            <c:when test="${order.orderStatus == 'Delivering'}">                                     <c:set var="si" value="local_shipping"/></c:when>
                            <c:when test="${order.orderStatus == 'Delivered'}">                                      <c:set var="si" value="done_all"/></c:when>
                            <c:when test="${order.orderStatus == 'Completed'}">                                      <c:set var="si" value="task_alt"/></c:when>
                            <c:when test="${order.orderStatus == 'Cancelled'}">                                      <c:set var="si" value="cancel"/></c:when>
                        </c:choose>

                        <div class="ot-order-card" data-status="${sc}" id="order-card-${order.orderId}">

                            <%-- Card Header --%>
                            <div class="ot-card-header">
                                <div class="ot-card-header-left">
                                    <div class="ot-order-icon">
                                        <span class="material-symbols-outlined">${si}</span>
                                    </div>
                                    <div class="ot-order-meta">
                                        <h3>#BH-${order.orderId}</h3>
                                        <div class="ot-order-date">
                                            <span class="material-symbols-outlined">calendar_today</span>
                                            <c:if test="${not empty order.createdAt}">
                                                ${fn:substring(order.createdAt.toString(), 0, 16)}
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                                <div class="ot-card-header-right">
                                    <span class="ot-badge ot-badge-${sb}">
                                        <span class="material-symbols-outlined">${si}</span>
                                        ${sl}
                                    </span>
                                    <c:choose>
                                        <c:when test="${order.paymentStatus == 'Paid'}">
                                            <span class="ot-badge ot-badge-paid">
                                                <span class="material-symbols-outlined">check_circle</span>
                                                Đã thanh toán
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="ot-badge ot-badge-unpaid">
                                                <span class="material-symbols-outlined">schedule</span>
                                                Chưa thanh toán
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>

                            <%-- Timeline --%>
                            <c:if test="${order.orderStatus != 'Cancelled'}">
                                <div class="ot-timeline-wrap">
                                    <div class="ot-timeline">

                                        <%-- Step 1: Đặt hàng --%>
                                        <c:set var="s1" value="done"/>
                                        <c:if test="${order.orderStatus == 'Pending'}"><c:set var="s1" value="active"/></c:if>
                                        <div class="ot-tl-step ${s1}">
                                            <div class="ot-tl-dot"><span class="material-symbols-outlined">receipt</span></div>
                                            <div class="ot-tl-label">Đặt hàng</div>
                                            <c:if test="${not empty placedTime}">
                                                <div class="ot-tl-time">${placedTime}</div>
                                            </c:if>
                                        </div>

                                        <%-- Step 2: Pha chế --%>
                                        <c:set var="s2" value=""/>
                                        <c:choose>
                                            <c:when test="${order.orderStatus == 'Preparing' || order.orderStatus == 'In_Progress'}">
                                                <c:set var="s2" value="active"/>
                                            </c:when>
                                            <c:when test="${order.orderStatus == 'Ready' || order.orderStatus == 'Waiting_Shipper' || order.orderStatus == 'Delivering' || order.orderStatus == 'Delivered' || order.orderStatus == 'Completed'}">
                                                <c:set var="s2" value="done"/>
                                            </c:when>
                                        </c:choose>
                                        <div class="ot-tl-step ${s2}">
                                            <div class="ot-tl-dot"><span class="material-symbols-outlined">coffee_maker</span></div>
                                            <div class="ot-tl-label">Pha chế</div>
                                            <c:if test="${not empty confirmedTime}">
                                                <div class="ot-tl-time">${confirmedTime}</div>
                                            </c:if>
                                        </div>

                                        <%-- Step 3: Giao hàng --%>
                                        <c:set var="s3" value=""/>
                                        <c:choose>
                                            <c:when test="${order.orderStatus == 'Ready' || order.orderStatus == 'Waiting_Shipper' || order.orderStatus == 'Delivering'}">
                                                <c:set var="s3" value="active"/>
                                            </c:when>
                                            <c:when test="${order.orderStatus == 'Delivered' || order.orderStatus == 'Completed'}">
                                                <c:set var="s3" value="done"/>
                                            </c:when>
                                        </c:choose>
                                        <div class="ot-tl-step ${s3}">
                                            <div class="ot-tl-dot"><span class="material-symbols-outlined">local_shipping</span></div>
                                            <div class="ot-tl-label">Giao hàng</div>
                                            <c:if test="${not empty deliveringTime}">
                                                <div class="ot-tl-time">${deliveringTime}</div>
                                            </c:if>
                                        </div>

                                        <%-- Step 4: Hoàn thành --%>
                                        <c:set var="s4" value=""/>
                                        <c:if test="${order.orderStatus == 'Completed'}"><c:set var="s4" value="done active"/></c:if>
                                        <div class="ot-tl-step ${s4}">
                                            <div class="ot-tl-dot"><span class="material-symbols-outlined">task_alt</span></div>
                                            <div class="ot-tl-label">Hoàn thành</div>
                                            <c:if test="${not empty completedTime}">
                                                <div class="ot-tl-time">${completedTime}</div>
                                            </c:if>
                                        </div>

                                    </div>
                                </div>
                            </c:if>

                            <%-- Cancelled banner --%>
                            <c:if test="${order.orderStatus == 'Cancelled'}">
                                <div class="ot-cancelled-banner">
                                    <span class="material-symbols-outlined">cancel</span>
                                    Đơn hàng này đã bị hủy
                                </div>
                            </c:if>

                            <%-- Toggle detail button --%>
                            <c:if test="${not empty details}">
                                <button class="ot-toggle-btn" id="toggle-${order.orderId}"
                                        onclick="toggleDetails('${order.orderId}')">
                                    <span class="material-symbols-outlined">expand_more</span>
                                    Xem chi tiết (${fn:length(details)} sản phẩm)
                                </button>

                                <div class="ot-collapsible" id="detail-${order.orderId}">
                                    <div class="ot-card-divider"></div>
                                    <div class="ot-card-items">
                                        <c:forEach var="item" items="${details}">
                                            <div class="ot-item-row">
                                                <div class="ot-item-left">
                                                    <span class="ot-item-qty-badge">x${item.quantity}</span>
                                                    <div>
                                                        <div class="ot-item-name">${item.productName}</div>
                                                        <c:if test="${not empty item.sizeName}">
                                                            <div class="ot-item-size">Size ${item.sizeName}</div>
                                                        </c:if>
                                                    </div>
                                                </div>
                                                <div class="ot-item-price">
                                                    <fmt:formatNumber value="${item.unitPrice * item.quantity}" pattern="#,###"/>₫
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                            </c:if>

                            <%-- Card Footer --%>
                            <div class="ot-card-divider"></div>
                            <div class="ot-card-footer">
                                <div class="ot-card-footer-left">
                                    <div class="ot-payment-info">
                                        <span class="material-symbols-outlined">payments</span>
                                        <c:choose>
                                            <c:when test="${order.paymentMethod == 'COD'}">Tiền mặt khi nhận hàng (COD)</c:when>
                                            <c:when test="${order.paymentMethod == 'MOMO'}">Ví MoMo</c:when>
                                            <c:otherwise>${order.paymentMethod}</c:otherwise>
                                        </c:choose>
                                    </div>
                                    <c:if test="${not empty address}">
                                        <div class="ot-delivery-info">
                                            <span class="material-symbols-outlined">location_on</span>
                                            <span>${address.recipientName} · ${address.recipientPhone} · ${address.addressDetail}</span>
                                        </div>
                                    </c:if>
                                </div>
                                <div class="ot-footer-right-block">
                                    <div class="ot-total-block">
                                        <div class="ot-total-label">Thành tiền</div>
                                        <div class="ot-total-amount">
                                            <fmt:formatNumber value="${order.finalAmount != null ? order.finalAmount : order.totalAmount}" pattern="#,###"/>₫
                                        </div>
                                        <c:if test="${order.discountAmount != null && order.discountAmount > 0}">
                                            <div class="ot-discount-note">
                                                Tiết kiệm <fmt:formatNumber value="${order.discountAmount}" pattern="#,###"/>₫
                                            </div>
                                        </c:if>
                                     </div>
                                     <%-- Nút Hủy đơn --%>
                                     <c:if test="${order.orderStatus == 'Pending'}">
                                         <form method="post" action="${pageContext.request.contextPath}/cancel-order"
                                               style="display:inline;"
                                               onsubmit="return confirm('Hủy đơn #BH-${order.orderId}? Hành động này không thể hoàn tác.')">
                                             <input type="hidden" name="orderId" value="${order.orderId}"/>
                                             <button type="submit" class="ot-cancel-btn">
                                                 <span class="material-symbols-outlined">cancel</span>
                                                 Hủy đơn
                                             </button>
                                         </form>
                                     </c:if>             
                                     <c:if test="${order.orderStatus == 'Preparing' || order.orderStatus == 'In_Progress'}">
                                         <div class="ot-no-cancel-note">
                                             <span class="material-symbols-outlined">info</span>
                                             Đang pha chế, không thể hủy
                                         </div>
                                     </c:if>
                                     <%-- Nút Xác nhận đã nhận hàng - chỉ hiện khi đã giao (Delivered) --%>
                                     <c:if test="${order.orderStatus == 'Delivered'}">
                                         <form method="post" action="${pageContext.request.contextPath}/confirm-delivery"
                                               style="display:inline;"
                                               onsubmit="return confirm('Xác nhận bạn đã nhận được đơn hàng #BH-${order.orderId}?')">
                                             <input type="hidden" name="orderId" value="${order.orderId}"/>
                                             <button type="submit" class="ot-confirm-btn">
                                                 <span class="material-symbols-outlined">done_all</span>
                                                 Đã nhận hàng
                                             </button>
                                         </form>
                                     </c:if>
                                     <%-- Nút Đánh giá - chỉ hiện khi hoàn thành --%>
                                     <c:if test="${order.orderStatus == 'Completed'}">
                                         <c:set var="reviewKey" value=",${order.orderId},"/>
                                         <c:choose>
                                             <c:when test="${fn:contains(reviewedOrderIds, reviewKey)}">
                                                 <div class="ot-reviewed-badge">
                                                     <span class="material-symbols-outlined">star</span>
                                                     Đã đánh giá
                                                 </div>
                                             </c:when>
                                             <c:otherwise>
                                                 <button class="ot-review-btn"
                                                         data-order-id="${order.orderId}"
                                                         onclick="openReviewModal(${order.orderId}, '#BH-${order.orderId}')">
                                                     <span class="material-symbols-outlined">star_rate</span>
                                                     Đánh giá
                                                 </button>
                                             </c:otherwise>
                                         </c:choose>
                                     </c:if>
                                 </div>
                            </div>

                        </div><%-- end ot-order-card --%>
                    </c:forEach>

                </div><%-- end ot-order-list --%>

                <%-- Empty state for filter --%>
                <div class="ot-empty" id="filterEmpty">
                    <span class="material-symbols-outlined">filter_list_off</span>
                    <h3>Không có đơn hàng</h3>
                    <p>Không tìm thấy đơn hàng ở trạng thái này.</p>
                </div>
            </c:if>

        </c:if><%-- end logged in --%>

    </div>
</main>




<%-- ── Review Modal ── --%>
<div class="ot-review-overlay" id="reviewOverlay" onclick="closeReviewModal(event)" style="display:none;">
    <div class="ot-review-modal">
        <button class="ot-review-close" onclick="closeReviewModal(null)" aria-label="Đóng">
            <span class="material-symbols-outlined">close</span>
        </button>
        <div class="ot-review-header">
            <span class="material-symbols-outlined ot-review-header-icon">star_rate</span>
            <h3>Đánh Giá Đơn Hàng</h3>
            <p id="reviewOrderLabel">Mã đơn: --</p>
        </div>
        <div class="ot-star-group">
            <span class="ot-star" data-val="1">&#9733;</span>
            <span class="ot-star" data-val="2">&#9733;</span>
            <span class="ot-star" data-val="3">&#9733;</span>
            <span class="ot-star" data-val="4">&#9733;</span>
            <span class="ot-star" data-val="5">&#9733;</span>
        </div>
        <div class="ot-star-hint" id="starHint">Chạm vào sao để chọn điểm</div>
        <textarea class="ot-review-comment" id="reviewComment" rows="3" maxlength="500"
                  placeholder="Nhận xét của bạn (tuỳ chọn)..."></textarea>
        <div class="ot-review-msg" id="reviewMsg"></div>
        <div class="ot-review-actions">
            <button class="ot-review-cancel" onclick="closeReviewModal(null)">Hủy</button>
            <button class="ot-review-submit" id="btnSubmitReview" onclick="submitReview()">
                <span class="material-symbols-outlined">send</span>
                Gửi đánh giá
            </button>
        </div>
    </div>
</div>



<script>
function filterOrders(tabEl, filter) {
    document.querySelectorAll('.ot-filter-tab').forEach(t => t.classList.remove('active'));
    tabEl.classList.add('active');
    const cards = document.querySelectorAll('.ot-order-card');
    const empty = document.getElementById('filterEmpty');
    let visible = 0;
    cards.forEach(card => {
        const show = filter === 'all' || card.dataset.status === filter;
        card.style.display = show ? '' : 'none';
        if (show) visible++;
    });
    if (empty) empty.classList.toggle('visible', visible === 0);
}

document.addEventListener('DOMContentLoaded', function () {
    const tab = new URLSearchParams(window.location.search).get('tab') || 'all';
    const tabEl = document.querySelector('.ot-filter-tab[data-filter="' + tab + '"]');
    if (tabEl) filterOrders(tabEl, tab);
});

function toggleDetails(orderId) {
    const panel = document.getElementById('detail-' + orderId);
    const btn   = document.getElementById('toggle-' + orderId);
    if (!panel) return;
    const open = panel.classList.toggle('open');
    btn.classList.toggle('open', open);
    btn.querySelector('.material-symbols-outlined').textContent = open ? 'expand_less' : 'expand_more';
    const match = btn.textContent.match(/\d+/);
    const count = match ? match[0] : '';
    btn.childNodes[btn.childNodes.length - 1].textContent = open
        ? ' Ẩn chi tiết'
        : ' Xem chi tiết (' + count + ' sản phẩm)';
}

/* ====== Review Modal ====== */
let _reviewOrderId = null;
let _selectedRating = 0;
const STAR_HINTS = ['', 'Không hài lòng', 'Tạm ổn', 'Bình thường', 'Hài lòng', 'Tuyệt vời!'];

function openReviewModal(orderId, orderCode) {
    _reviewOrderId = orderId;
    _selectedRating = 0;
    document.getElementById('reviewOrderLabel').textContent = 'Mã đơn: ' + orderCode;
    document.getElementById('reviewComment').value = '';
    document.getElementById('reviewMsg').textContent = '';
    document.getElementById('starHint').textContent = 'Chạm vào sao để chọn điểm';
    setStars(0);
    const overlay = document.getElementById('reviewOverlay');
    document.body.appendChild(overlay);   // đảm bảo nằm trong body → CSS apply
    overlay.style.display        = 'flex';
    overlay.style.position       = 'fixed';
    overlay.style.top            = '0';
    overlay.style.left           = '0';
    overlay.style.width          = '100vw';
    overlay.style.height         = '100vh';
    overlay.style.background     = 'rgba(0,0,0,0.55)';
    overlay.style.zIndex         = '99999';
    overlay.style.alignItems     = 'center';
    overlay.style.justifyContent = 'center';
    overlay.style.padding        = '20px';
    overlay.style.boxSizing      = 'border-box';
    document.body.style.overflow = 'hidden';
}

function closeReviewModal(e) {
    if (e && e.target !== document.getElementById('reviewOverlay')) return;
    document.getElementById('reviewOverlay').style.display = 'none';
    document.body.style.overflow = '';
}

function setStars(val) {
    document.querySelectorAll('.ot-star').forEach(s => {
        s.classList.toggle('active', parseInt(s.dataset.val) <= val);
    });
}

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.ot-star').forEach(star => {
        star.addEventListener('mouseover', () => setStars(parseInt(star.dataset.val)));
        star.addEventListener('mouseleave', () => setStars(_selectedRating));
        star.addEventListener('click', () => {
            _selectedRating = parseInt(star.dataset.val);
            setStars(_selectedRating);
            document.getElementById('starHint').textContent = STAR_HINTS[_selectedRating];
        });
    });
});

function submitReview() {
    const msg = document.getElementById('reviewMsg');
    if (_selectedRating === 0) {
        msg.style.color = '#dc2626';
        msg.textContent = '• Vui lòng chọn số sao đánh giá.';
        return;
    }
    const btn = document.getElementById('btnSubmitReview');
    btn.disabled = true;
    btn.innerHTML = '<span class="material-symbols-outlined">hourglass_empty</span> Đang gửi...';
    msg.textContent = '';

    const ctx = '${pageContext.request.contextPath}';
    const body = new URLSearchParams({
        orderId:  _reviewOrderId,
        rating:   _selectedRating,
        comment:  document.getElementById('reviewComment').value.trim()
    });

    fetch(ctx + '/review', { method: 'POST', body })
        .then(r => r.json())
        .then(data => {
            if (data.success) {
                msg.style.color = '#16a34a';
                msg.textContent = '✓ ' + data.message;
                btn.disabled = true;
                btn.innerHTML = '<span class="material-symbols-outlined">check</span> Đã gửi';
                setTimeout(() => {
                    const oldBtn = document.querySelector('.ot-review-btn[data-order-id="' + _reviewOrderId + '"]');
                    if (oldBtn) {
                        const badge = document.createElement('div');
                        badge.className = 'ot-reviewed-badge';
                        badge.innerHTML = '<span class="material-symbols-outlined">star</span> Đã đánh giá';
                        oldBtn.replaceWith(badge);
                    }
                    document.getElementById('reviewOverlay').style.display = 'none';
                    document.body.style.overflow = '';
                }, 900);
            } else {
                msg.style.color = '#dc2626';
                msg.textContent = '• ' + data.message;
                btn.disabled = false;
                btn.innerHTML = '<span class="material-symbols-outlined">send</span> Gửi đánh giá';
            }
        })
        .catch(() => {
            msg.style.color = '#dc2626';
            msg.textContent = '• Lỗi kết nối, vui lòng thử lại.';
            btn.disabled = false;
            btn.innerHTML = '<span class="material-symbols-outlined">send</span> Gửi đánh giá';
        });
}
</script>

<jsp:include page="/views/HomePage/Footer.jsp"/>


