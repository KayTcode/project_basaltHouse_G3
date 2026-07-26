<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Shipper Dashboard</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/shipper/shipper.css" />
</head>
<body>

    <%-- ───────── Flash message ───────── --%>
    <c:if test="${not empty sessionScope.flashMessage}">
        <div class="flash-alert ${sessionScope.flashSuccess ? 'ok' : 'err'}">
            <i class="bi ${sessionScope.flashSuccess ? 'bi-check-circle' : 'bi-exclamation-triangle'}"></i>
            <c:out value="${sessionScope.flashMessage}"/>
        </div>
        <c:remove var="flashMessage" scope="session"/>
        <c:remove var="flashSuccess" scope="session"/>
    </c:if>

    <%-- ───────── Header ───────── --%>
    <div class="shipper-header">
        <div class="d-flex align-items-center gap-3">
            <img class="avatar"
                 src="${not empty currentShipper.avatarUrl ? currentShipper.avatarUrl : 'https://api.dicebear.com/7.x/initials/svg?seed='.concat(currentShipper.fullName)}"
                 alt="avatar">
            <div>
                <div style="font-weight:700;font-size:16px"><c:out value="${currentShipper.fullName}"/></div>
                <div style="font-size:13px;opacity:.85"><c:out value="${currentShipper.phone}"/></div>
                <span class="avail-badge">
                    <span class="avail-dot ${currentShipper.isAvailable ? '' : 'off'}"></span>
                    ${currentShipper.isAvailable ? 'Đang nhận đơn' : 'Tạm ngưng nhận đơn'}
                </span>
            </div>
        </div>
    </div>

    <%-- ───────── Tabs ───────── --%>
    <div class="tab-bar">
        <button type="button" class="tab-btn active" data-tab="pending" onclick="switchTab('pending')">
            Đơn chờ nhận <c:if test="${not empty pendingOrders}">(${pendingOrders.size()})</c:if>
        </button>
        <button type="button" class="tab-btn" data-tab="current" onclick="switchTab('current')">
            Đang giao ${not empty currentOrder ? '(1)' : ''}
        </button>
    </div>

    <%-- ───────── Tab: Đơn chờ nhận ───────── --%>
    <div id="tab-pending" class="tab-panel active">
        <c:choose>
            <c:when test="${empty pendingOrders}">
                <div class="empty-state">
                    <i class="bi bi-inbox"></i>
                    Chưa có đơn nào chờ bạn xác nhận.
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="o" items="${pendingOrders}">
                    <div class="order-card">
                        <div class="order-top">
                            <div>
                                <div class="order-id">Đơn #${o.orderId}</div>
                                <div class="order-time">${orderTimeMap[o.orderId]}</div>
                            </div>
                            <div class="order-amount"><fmt:formatNumber value="${o.finalAmount}" type="number"/>đ</div>
                        </div>
                        <div class="order-meta">
                            <div><i class="bi bi-person"></i> <c:out value="${not empty o.customerName ? o.customerName : 'Khách lẻ'}"/></div>
                            <div><i class="bi bi-credit-card"></i> ${o.paymentMethod} · ${o.paymentStatus}</div>
                        </div>

                        <c:if test="${empty currentOrder}">
                            <div class="d-flex gap-2" style="margin-top:12px">
                                <form action="${pageContext.request.contextPath}/shipper/accept-order"
                                      method="post" style="margin:0;flex:1">
                                    <input type="hidden" name="orderId" value="${o.orderId}">
                                    <button type="submit" class="btn-accept" style="width:100%">
                                        <i class="bi bi-check2-circle"></i> Nhận đơn này
                                    </button>
                                </form>
                                <form action="${pageContext.request.contextPath}/shipper/reject-order"
                                      method="post" style="margin:0;flex:1"
                                      onsubmit="return confirm('Huỷ nhận đơn #${o.orderId}?');">
                                    <input type="hidden" name="orderId" value="${o.orderId}">
                                    <button type="submit" class="btn-reject" style="width:100%">
                                        <i class="bi bi-x-circle"></i> Huỷ đơn
                                    </button>
                                </form>
                            </div>
                        </c:if>
                        <c:if test="${not empty currentOrder}">
                            <div class="order-meta" style="margin-top:10px;color:#b8860b">
                                <i class="bi bi-info-circle"></i> Hoàn thành đơn đang giao trước khi nhận đơn mới.
                            </div>
                        </c:if>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>

    <%-- ───────── Tab: Đang giao ───────── --%>
    <div id="tab-current" class="tab-panel">
        <c:choose>
            <c:when test="${empty currentOrder}">
                <div class="empty-state">
                    <i class="bi bi-truck"></i>
                    Bạn chưa nhận đơn nào để giao.
                </div>
            </c:when>
            <c:otherwise>
                <div class="order-card">
                    <div class="order-top">
                        <div>
                            <div class="order-id">Đơn #${currentOrder.orderId}</div>
                            <div class="order-time">${currentOrderTime}</div>
                        </div>
                        <div class="order-amount"><fmt:formatNumber value="${currentOrder.finalAmount}" type="number"/>đ</div>
                    </div>

                    <div class="order-meta">
                        <div><i class="bi bi-person"></i> <c:out value="${not empty currentOrder.customerName ? currentOrder.customerName : 'Khách lẻ'}"/></div>
                        <div><i class="bi bi-credit-card"></i> ${currentOrder.paymentMethod} · ${currentOrder.paymentStatus}</div>

                        <c:if test="${not empty deliveryAddress}">
                            <div style="margin-top:8px;padding-top:8px;border-top:1px dashed var(--card-border)">
                                <div><i class="bi bi-geo-alt"></i> <c:out value="${deliveryAddress.recipientName}"/> — <c:out value="${deliveryAddress.recipientPhone}"/></div>
                                <div><i class="bi bi-signpost"></i> <c:out value="${deliveryAddress.addressDetail}"/></div>
                                <c:if test="${not empty deliveryAddress.note}">
                                    <div><i class="bi bi-chat-left-text"></i> <c:out value="${deliveryAddress.note}"/></div>
                                </c:if>
                            </div>
                        </c:if>
                    </div>

                    <div class="d-flex gap-2" style="margin-top:14px">
                        <button type="button" class="btn-success-deliver" style="flex:1"
                                onclick="openModal('modal-success')">
                            <i class="bi bi-check2-circle"></i> Giao thành công
                        </button>
                        <button type="button" class="btn-fail-deliver" style="flex:1"
                                onclick="openModal('modal-fail')">
                            <i class="bi bi-x-octagon"></i> Giao thất bại
                        </button>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <%-- ───────── Modal: xác nhận giao thành công ───────── --%>
    <div class="modal-backdrop-custom" id="modal-success">
        <div class="modal-box">
            <h5><i class="bi bi-check2-circle text-success"></i> Xác nhận giao thành công</h5>
            <form action="${pageContext.request.contextPath}/shipper/update-delivery" method="post"
                  enctype="multipart/form-data">
                <input type="hidden" name="orderId" value="${currentOrder.orderId}">
                <input type="hidden" name="isSuccess" value="true">
                <label style="font-size:13px;color:#666">Ảnh xác nhận giao hàng (tuỳ chọn)</label>
                <input type="file" name="proofImage" accept="image/*" capture="environment">
                <label style="font-size:13px;color:#666;margin-top:8px;display:block">Ghi chú (tuỳ chọn)</label>
                <textarea name="note" rows="2" placeholder="Ví dụ: đã giao tận tay khách"></textarea>
                <div class="modal-actions">
                    <button type="button" onclick="closeModal('modal-success')">Huỷ</button>
                    <button type="submit" class="primary btn-success-deliver">Xác nhận</button>
                </div>
            </form>
        </div>
    </div>

    <%-- ───────── Modal: báo giao thất bại ───────── --%>
    <div class="modal-backdrop-custom" id="modal-fail">
        <div class="modal-box">
            <h5><i class="bi bi-x-octagon text-danger"></i> Báo giao thất bại</h5>
            <form action="${pageContext.request.contextPath}/shipper/update-delivery" method="post"
                  onsubmit="return validateFailForm(this);">
                <input type="hidden" name="orderId" value="${currentOrder.orderId}">
                <input type="hidden" name="isSuccess" value="false">
                <label style="font-size:13px;color:#666">Lí do giao thất bại (bắt buộc)</label>
                <textarea name="failReason" rows="3" required
                          placeholder="Ví dụ: khách không nghe máy, sai địa chỉ..."></textarea>
                <div class="modal-actions">
                    <button type="button" onclick="closeModal('modal-fail')">Huỷ</button>
                    <button type="submit" class="primary btn-fail-deliver">Xác nhận thất bại</button>
                </div>
            </form>
        </div>
    </div>

    <script>
        function switchTab(name) {
            document.querySelectorAll('.tab-btn').forEach(b => b.classList.toggle('active', b.dataset.tab === name));
            document.querySelectorAll('.tab-panel').forEach(p => p.classList.remove('active'));
            document.getElementById('tab-' + name).classList.add('active');
        }
        function openModal(id) { document.getElementById(id).classList.add('show'); }
        function closeModal(id) { document.getElementById(id).classList.remove('show'); }
        function validateFailForm(form) {
            const reason = form.querySelector('[name=failReason]').value.trim();
            if (!reason) {
                alert('Vui lòng nhập lí do giao hàng thất bại!');
                return false;
            }
            return true;
        }
        // Đóng modal khi bấm ra ngoài
        document.querySelectorAll('.modal-backdrop-custom').forEach(bd => {
            bd.addEventListener('click', e => { if (e.target === bd) bd.classList.remove('show'); });
        });

        <c:if test="${not empty currentOrder}">
        // Nếu đang có đơn giao, mặc định mở tab "Đang giao" cho tiện thao tác
        switchTab('current');
        </c:if>
    </script>
</body>
</html>
