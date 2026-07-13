<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core"     %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"      %>
<%@ taglib prefix="fn"  uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>Coffeely – Shipper</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/shipper/shipper.css">
</head>
<body>

<%-- ══════════════════════════════════════════════════════════════
     HEADER
     ══════════════════════════════════════════════════════════════ --%>
<header class="s-header">
    <div class="s-header__left">
        <c:choose>
            <c:when test="${not empty currentShipper.avatarUrl}">
                <img src="${fn:escapeXml(currentShipper.avatarUrl)}" alt="avatar" class="s-avatar">
            </c:when>
            <c:otherwise>
                <div class="s-avatar-ph"><i class="bi bi-person-fill"></i></div>
            </c:otherwise>
        </c:choose>
        <div>
            <div class="s-header__name"><c:out value="${currentShipper.fullName}"/></div>
            <div class="s-header__role"><i class="bi bi-bicycle me-1"></i>Tài xế giao hàng</div>
        </div>
    </div>
    <a href="${pageContext.request.contextPath}/logout" class="btn-logout">
        <i class="bi bi-box-arrow-right me-1"></i>Thoát
    </a>
</header>


<%-- ══════════════════════════════════════════════════════════════
     FLASH MESSAGE  (đọc từ Session, xóa ngay sau khi hiển thị)
     ══════════════════════════════════════════════════════════════ --%>
<c:if test="${not empty sessionScope.flashMessage}">
    <div class="flash-wrap">
        <c:choose>
            <c:when test="${sessionScope.flashSuccess == true}">
                <div class="alert alert-success flash-alert" role="alert">
                    <i class="bi bi-check-circle-fill"></i>
                    <span><c:out value="${sessionScope.flashMessage}"/></span>
                    <button type="button" class="btn-close ms-auto p-0" data-bs-dismiss="alert"
                            style="font-size:.8rem"></button>
                </div>
            </c:when>
            <c:otherwise>
                <div class="alert alert-danger flash-alert" role="alert">
                    <i class="bi bi-exclamation-triangle-fill"></i>
                    <span><c:out value="${sessionScope.flashMessage}"/></span>
                    <button type="button" class="btn-close ms-auto p-0" data-bs-dismiss="alert"
                            style="font-size:.8rem"></button>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
    <%-- Xóa flash khỏi Session ngay sau khi render --%>
    <c:remove var="flashMessage" scope="session"/>
    <c:remove var="flashSuccess"  scope="session"/>
</c:if>


<%-- ══════════════════════════════════════════════════════════════
     TAB BAR
     ══════════════════════════════════════════════════════════════ --%>
<div class="tab-bar">
    <button class="tab-btn active" id="btn-tab1" onclick="switchTab(1)">
        <i class="bi bi-inbox-fill"></i>
        Đơn mới
        <c:if test="${not empty pendingOrders}">
            <span class="badge-cnt">${fn:length(pendingOrders)}</span>
        </c:if>
    </button>
    <button class="tab-btn" id="btn-tab2" onclick="switchTab(2)">
        <i class="bi bi-geo-alt-fill"></i>
        Đang giao
        <c:if test="${not empty currentOrder}">
            <span class="badge-on">1</span>
        </c:if>
    </button>
</div>


<%-- ══════════════════════════════════════════════════════════════
     PANEL 1 – ĐƠN CHỜ NHẬN
     ══════════════════════════════════════════════════════════════ --%>
<div class="panel active" id="panel-1">
    <c:choose>
        <c:when test="${empty pendingOrders}">
            <div class="empty">
                <i class="bi bi-box-seam"></i>
                <strong>Chưa có đơn hàng mới</strong>
                <span>Các đơn chờ tài xế nhận sẽ xuất hiện tại đây.</span>
            </div>
        </c:when>
        <c:otherwise>
            <c:forEach var="order" items="${pendingOrders}">
                <div class="o-card">
                    <%-- Dòng meta: mã đơn + giờ tạo --%>
                    <div class="o-card__meta">
                        <span class="o-card__id">
                            <i class="bi bi-hash"></i>Đơn&nbsp;${order.orderId}
                        </span>
                        <%-- LocalDateTime.toString() → "2024-06-18T14:30:00" → cắt lấy phần giờ --%>
                        <span class="o-card__time">
                            ${fn:substring(order.createdAt.toString(), 11, 16)}
                            &nbsp;${fn:substring(order.createdAt.toString(), 8, 10)}/${fn:substring(order.createdAt.toString(), 5, 7)}
                        </span>
                    </div>

                    <%-- Tên khách --%>
                    <div class="o-card__customer">
                        <i class="bi bi-person-circle me-1"></i>
                        <c:out value="${order.customerName}"/>
                    </div>

                    <%-- Số tiền + phương thức --%>
                    <div class="o-card__bottom">
                        <div>
                            <div class="o-card__amount-label">Cần thu</div>
                            <div class="o-card__amount">
                                <fmt:formatNumber value="${order.finalAmount}"
                                                  type="number" groupingUsed="true"/>đ
                            </div>
                        </div>
                        <span class="badge-pay"><c:out value="${order.paymentMethod}"/></span>
                    </div>

                    <%-- Form nhận đơn — POST tới AcceptOrderServlet --%>
                    <form method="post"
                          action="${pageContext.request.contextPath}/shipper/accept-order"
                          onsubmit="return submitOnce(this)">
                        <input type="hidden" name="orderId" value="${order.orderId}">
                        <button type="submit" class="btn-accept">
                            <span class="spin"></span>
                            <i class="bi bi-lightning-charge-fill"></i>
                            Nhận đơn này
                        </button>
                    </form>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</div>


<%-- ══════════════════════════════════════════════════════════════
     PANEL 2 – ĐƠN ĐANG GIAO
     ══════════════════════════════════════════════════════════════ --%>
<div class="panel" id="panel-2">
    <c:choose>
        <c:when test="${empty currentOrder}">
            <div class="empty">
                <i class="bi bi-truck"></i>
                <strong>Chưa có đơn đang giao</strong>
                <span>Nhận đơn ở tab "Đơn mới" để bắt đầu giao hàng.</span>
            </div>
        </c:when>
        <c:otherwise>

            <%-- ── Thẻ thông tin đơn hàng ────────────────────── --%>
            <div class="cur-card">
                <div class="cur-card__head">
                    <div class="cur-card__head-top">
                        <span class="cur-id-badge">
                            <i class="bi bi-hash"></i>Đơn&nbsp;${currentOrder.orderId}
                        </span>
                        <span class="cur-status-badge">
                            <i class="bi bi-truck me-1"></i>Đang giao
                        </span>
                    </div>
                    <div class="cur-card__label">Số tiền cần thu</div>
                    <div class="cur-card__amount">
                        <fmt:formatNumber value="${currentOrder.finalAmount}"
                                          type="number" groupingUsed="true"/>đ
                    </div>
                    <div class="cur-card__method">
                        <i class="bi bi-credit-card me-1"></i>
                        <c:out value="${currentOrder.paymentMethod}"/>
                    </div>
                </div>

                <div class="cur-card__body">
                    <div class="info-section-title">Thông tin giao hàng</div>

                    <c:choose>
                        <c:when test="${not empty deliveryAddress}">

                            <%-- Người nhận --%>
                            <div class="info-row">
                                <div class="info-icon"><i class="bi bi-person-fill"></i></div>
                                <div>
                                    <div class="info-label">Người nhận</div>
                                    <div class="info-val"><c:out value="${deliveryAddress.recipientName}"/></div>
                                </div>
                            </div>

                            <%-- Điện thoại + nút gọi nhanh --%>
                            <div class="info-row">
                                <div class="info-icon"><i class="bi bi-telephone-fill"></i></div>
                                <div>
                                    <div class="info-label">Điện thoại</div>
                                    <div class="info-val" style="display:flex;align-items:center;flex-wrap:wrap;gap:4px">
                                        <c:out value="${deliveryAddress.recipientPhone}"/>
                                        <a href="tel:${deliveryAddress.recipientPhone}" class="btn-call">
                                            <i class="bi bi-telephone-fill"></i>Gọi ngay
                                        </a>
                                    </div>
                                </div>
                            </div>

                            <%-- Địa chỉ --%>
                            <div class="info-row">
                                <div class="info-icon"><i class="bi bi-geo-alt-fill"></i></div>
                                <div>
                                    <div class="info-label">Địa chỉ</div>
                                    <div class="info-val"><c:out value="${deliveryAddress.addressDetail}"/></div>
                                </div>
                            </div>

                            <%-- Ghi chú (nếu có) --%>
                            <c:if test="${not empty deliveryAddress.note}">
                                <div class="info-row">
                                    <div class="info-icon"><i class="bi bi-sticky-fill"></i></div>
                                    <div>
                                        <div class="info-label">Ghi chú</div>
                                        <div class="info-note"><c:out value="${deliveryAddress.note}"/></div>
                                    </div>
                                </div>
                            </c:if>

                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-warning small mb-0">
                                <i class="bi bi-exclamation-triangle me-1"></i>
                                Không tìm thấy thông tin địa chỉ giao hàng.
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>


            <%-- ── Form xác nhận giao thành công ─────────────── --%>
            <div class="act-card">
                <div class="act-card__title">
                    <i class="bi bi-check-circle-fill text-success"></i>
                    Xác nhận giao thành công
                </div>

                <%-- POST tới UpdateDeliveryServlet, action=success --%>
                <form method="post"
                      action="${pageContext.request.contextPath}/shipper/update-delivery"
                      onsubmit="return submitOnce(this)">
                    <input type="hidden" name="orderId" value="${currentOrder.orderId}">
                    <input type="hidden" name="action"  value="success">

                    <div class="mb-3">
                        <label class="lbl">
                            <i class="bi bi-image me-1"></i>Link ảnh bằng chứng giao hàng
                        </label>
                        <input type="url" name="proofImageUrl" class="inp"
                               placeholder="https://... (dán link ảnh chụp khi giao)">
                    </div>

                    <div class="mb-3">
                        <label class="lbl">
                            <i class="bi bi-chat-text me-1"></i>Ghi chú (tùy chọn)
                        </label>
                        <textarea name="note" class="inp" rows="2"
                                  placeholder="VD: Khách đã ký nhận, để trước cửa..."></textarea>
                    </div>

                    <button type="submit" class="btn-delivered">
                        <span class="spin"></span>
                        <i class="bi bi-check-lg"></i>
                        Xác nhận đã giao thành công
                    </button>
                </form>
            </div>


            <%-- ── Nút báo giao thất bại ──────────────────────── --%>
            <div class="act-card">
                <div class="act-card__title">
                    <i class="bi bi-x-circle-fill text-danger"></i>
                    Báo giao thất bại
                </div>
                <button type="button" class="btn-failed"
                        data-bs-toggle="modal" data-bs-target="#failModal">
                    <i class="bi bi-exclamation-triangle"></i>
                    Không giao được — Báo thất bại
                </button>
            </div>

        </c:otherwise>
    </c:choose>
</div><%-- end panel-2 --%>


<%-- ══════════════════════════════════════════════════════════════
     MODAL – CHỌN LÝ DO THẤT BẠI
     (chỉ render khi có đơn đang giao để tránh lỗi EL null)
     ══════════════════════════════════════════════════════════════ --%>
<c:if test="${not empty currentOrder}">
<div class="modal fade" id="failModal" tabindex="-1"
     aria-labelledby="failModalLbl" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content" style="border-radius:16px;overflow:hidden">

            <div class="modal-header border-0"
                 style="background:var(--red);color:#fff">
                <h5 class="modal-title fw-bold" id="failModalLbl">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i>Báo giao thất bại
                </h5>
                <button type="button" class="btn-close btn-close-white"
                        data-bs-dismiss="modal"></button>
            </div>

            <%-- POST tới UpdateDeliveryServlet, action=failed --%>
            <form method="post"
                  action="${pageContext.request.contextPath}/shipper/update-delivery"
                  id="failForm"
                  onsubmit="return prepareAndSubmit(this)">
                <input type="hidden" name="orderId" value="${currentOrder.orderId}">
                <input type="hidden" name="action"  value="failed">
                <%-- Input ẩn chứa lý do thực sự sẽ gửi đi --%>
                <input type="hidden" name="failReason" id="finalReason">

                <div class="modal-body p-3">
                    <p class="small text-muted mb-3">
                        Chọn hoặc nhập lý do không giao được:
                    </p>

                    <label class="reason-opt">
                        <input type="radio" name="reasonChoice"
                               value="Khách không nghe máy, không ra nhận hàng">
                        <span>📵&nbsp;Khách không nghe máy, không ra nhận hàng</span>
                    </label>
                    <label class="reason-opt">
                        <input type="radio" name="reasonChoice"
                               value="Địa chỉ không tìm thấy hoặc không chính xác">
                        <span>📍&nbsp;Địa chỉ không tìm thấy hoặc không chính xác</span>
                    </label>
                    <label class="reason-opt">
                        <input type="radio" name="reasonChoice"
                               value="Khách hủy đơn khi đang giao">
                        <span>❌&nbsp;Khách hủy đơn khi đang giao</span>
                    </label>
                    <label class="reason-opt">
                        <input type="radio" name="reasonChoice"
                               value="Sự cố xe, không thể tiếp tục giao">
                        <span>🔧&nbsp;Sự cố xe, không thể tiếp tục giao</span>
                    </label>

                    <div class="mt-3">
                        <label class="lbl">Hoặc nhập lý do khác:</label>
                        <textarea id="customReason" class="inp" rows="2"
                                  placeholder="Mô tả lý do cụ thể..."></textarea>
                    </div>
                </div>

                <div class="modal-footer border-0 pt-0 gap-2">
                    <button type="button" class="btn btn-light flex-grow-1"
                            data-bs-dismiss="modal">Hủy bỏ</button>
                    <button type="submit" class="btn btn-danger flex-grow-1" id="btnConfirmFail">
                        <span class="spin spin-red" id="spinFail"></span>
                        <i class="bi bi-send me-1"></i>Xác nhận thất bại
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>
</c:if>


<%-- ══════════════════════════════════════════════════════════════
     SCRIPTS
     ══════════════════════════════════════════════════════════════ --%>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    /* ── Tab switching ─────────────────────────────────────── */
    function switchTab(n) {
        document.querySelectorAll('.tab-btn').forEach((b, i) =>
            b.classList.toggle('active', i + 1 === n));
        document.querySelectorAll('.panel').forEach((p, i) =>
            p.classList.toggle('active', i + 1 === n));
    }

    /* ── Sau khi redirect về, nếu vừa nhận đơn thành công
         → tự động chuyển sang Tab 2 "Đang giao" ─────────── */
    (function () {
        const flash = document.querySelector('.flash-alert');
        if (flash) {
            // Auto-dismiss sau 5 giây
            setTimeout(() => bootstrap.Alert.getOrCreateInstance(flash)?.close(), 5000);

            // Nếu flash là success và có badge trên tab Đang giao → chuyển tab
            const hasCurrent = document.querySelector('.badge-on');
            if (flash.classList.contains('alert-success') && hasCurrent) {
                switchTab(2);
            }
        }
    })();

    /* ── Chặn double-submit (form nhận đơn + form giao thành công) ── */
    function submitOnce(form) {
        const btn = form.querySelector('button[type="submit"]');
        if (!btn || btn.disabled) return false;
        btn.disabled = true;
        const spin = btn.querySelector('.spin');
        if (spin) spin.style.display = 'inline-block';
        return true;
    }

    /* ── Modal thất bại: ghép lý do rồi submit ─────────────── */
    function prepareAndSubmit(form) {
        const custom  = document.getElementById('customReason')?.value?.trim();
        const checked = document.querySelector('input[name="reasonChoice"]:checked');
        const target  = document.getElementById('finalReason');

        if (custom) {
            target.value = custom;
        } else if (checked) {
            target.value = checked.value;
        } else {
            alert('Vui lòng chọn hoặc nhập lý do giao thất bại!');
            return false;
        }

        // Hiện loading
        const btn = document.getElementById('btnConfirmFail');
        btn.disabled = true;
        document.getElementById('spinFail').style.display = 'inline-block';
        return true;
    }
</script>

</body>
</html>
