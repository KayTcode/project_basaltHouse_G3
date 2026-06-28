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

    <style>
        /* ── BRAND ────────────────────────────────────────────── */
        :root {
            --green:       #005c25;
            --green-dark:  #003d18;
            --green-light: #e8f5ee;
            --green-mid:   #1a7d3e;
            --amber:       #f59e0b;
            --red:         #dc2626;
            --red-light:   #fef2f2;
            --gray-bg:     #f1f3f1;
            --card-shadow: 0 1px 5px rgba(0,0,0,0.09);
        }

        /* ── BASE ─────────────────────────────────────────────── */
        * { box-sizing: border-box; }
        body {
            background: var(--gray-bg);
            font-family: 'Segoe UI', system-ui, sans-serif;
            margin: 0;
            padding-bottom: 24px;
            -webkit-tap-highlight-color: transparent;
        }

        /* ── HEADER ───────────────────────────────────────────── */
        .s-header {
            background: linear-gradient(135deg, var(--green-dark), var(--green));
            color: #fff;
            padding: 10px 16px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            position: sticky;
            top: 0;
            z-index: 1000;
            box-shadow: 0 2px 8px rgba(0,0,0,0.28);
            min-height: 60px;
        }
        .s-header__left  { display: flex; align-items: center; gap: 10px; }
        .s-avatar {
            width: 42px; height: 42px;
            border-radius: 50%; object-fit: cover;
            border: 2px solid rgba(255,255,255,0.55);
            flex-shrink: 0;
        }
        .s-avatar-ph {
            width: 42px; height: 42px;
            border-radius: 50%;
            background: var(--green-mid);
            display: flex; align-items: center; justify-content: center;
            font-size: 1.2rem; color: #fff;
            border: 2px solid rgba(255,255,255,0.4);
            flex-shrink: 0;
        }
        .s-header__name { font-weight: 700; font-size: .95rem; line-height: 1.2; }
        .s-header__role { font-size: .72rem; opacity: .78; }
        .btn-logout {
            background: rgba(255,255,255,.15);
            border: 1px solid rgba(255,255,255,.3);
            color: #fff;
            border-radius: 20px;
            padding: 5px 13px;
            font-size: .78rem;
            white-space: nowrap;
            text-decoration: none;
            transition: background .2s;
        }
        .btn-logout:hover { background: rgba(255,255,255,.28); color: #fff; }

        /* ── FLASH ────────────────────────────────────────────── */
        .flash-wrap { padding: 12px 14px 0; }
        .flash-alert {
            border-radius: 10px;
            font-size: .88rem;
            display: flex;
            align-items: flex-start;
            gap: 8px;
            padding: 12px 14px;
        }
        .flash-alert i { font-size: 1.1rem; flex-shrink: 0; margin-top: 1px; }

        /* ── TABS ─────────────────────────────────────────────── */
        .tab-bar {
            background: #fff;
            border-bottom: 2px solid #e5e7eb;
            position: sticky;
            top: 60px;
            z-index: 900;
            display: flex;
        }
        .tab-btn {
            flex: 1;
            border: none;
            background: none;
            padding: 13px 8px;
            font-size: .82rem;
            font-weight: 600;
            color: #6b7280;
            border-bottom: 3px solid transparent;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 6px;
            cursor: pointer;
            transition: color .15s, border-color .15s;
        }
        .tab-btn.active {
            color: var(--green);
            border-bottom-color: var(--green);
        }
        .badge-cnt {
            background: var(--amber); color: #000;
            font-size: .65rem; border-radius: 10px;
            padding: 1px 6px; font-weight: 700;
        }
        .badge-on {
            background: var(--green); color: #fff;
            font-size: .65rem; border-radius: 10px;
            padding: 1px 6px; font-weight: 700;
        }

        /* ── PANELS ───────────────────────────────────────────── */
        .panel { display: none; padding: 14px; }
        .panel.active { display: block; }

        /* ── ORDER CARD (Tab 1) ───────────────────────────────── */
        .o-card {
            background: #fff;
            border-radius: 12px;
            border-left: 4px solid var(--amber);
            padding: 14px;
            margin-bottom: 12px;
            box-shadow: var(--card-shadow);
        }
        .o-card__meta {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8px;
        }
        .o-card__id {
            font-size: .74rem; font-weight: 700;
            color: #9ca3af; text-transform: uppercase; letter-spacing: .4px;
        }
        .o-card__time { font-size: .74rem; color: #9ca3af; }
        .o-card__customer {
            font-weight: 700; font-size: .96rem;
            color: #111827; margin-bottom: 10px;
        }
        .o-card__bottom {
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        .o-card__amount {
            font-size: 1.15rem; font-weight: 800; color: var(--green);
        }
        .o-card__amount-label { font-size: .7rem; color: #9ca3af; }
        .badge-pay {
            font-size: .72rem; padding: 4px 9px; border-radius: 6px;
            background: #fef3c7; color: #92400e; font-weight: 700;
        }
        .btn-accept {
            width: 100%; margin-top: 12px;
            background: var(--green); color: #fff;
            border: none; border-radius: 9px;
            padding: 11px; font-weight: 700; font-size: .9rem;
            cursor: pointer; transition: background .2s, transform .1s;
            display: flex; align-items: center; justify-content: center; gap: 6px;
        }
        .btn-accept:hover   { background: var(--green-dark); }
        .btn-accept:active  { transform: scale(.97); }
        .btn-accept:disabled { opacity: .6; cursor: not-allowed; }

        /* ── CURRENT ORDER (Tab 2) ────────────────────────────── */
        .cur-card {
            background: #fff;
            border-radius: 14px;
            overflow: hidden;
            box-shadow: var(--card-shadow);
            margin-bottom: 14px;
        }
        .cur-card__head {
            background: linear-gradient(135deg, var(--green-dark), var(--green));
            color: #fff; padding: 16px;
        }
        .cur-card__head-top {
            display: flex; justify-content: space-between;
            align-items: flex-start; margin-bottom: 10px;
        }
        .cur-id-badge {
            background: rgba(255,255,255,.2);
            border-radius: 20px; padding: 3px 10px;
            font-size: .74rem; font-weight: 700;
        }
        .cur-status-badge {
            background: var(--amber); color: #000;
            border-radius: 6px; padding: 3px 9px;
            font-size: .74rem; font-weight: 700;
        }
        .cur-card__label { font-size: .72rem; opacity: .75; margin-bottom: 2px; }
        .cur-card__amount { font-size: 1.55rem; font-weight: 900; }
        .cur-card__method { font-size: .8rem; opacity: .8; margin-top: 3px; }

        .cur-card__body { padding: 14px; }
        .info-section-title {
            font-size: .7rem; font-weight: 700; color: #9ca3af;
            text-transform: uppercase; letter-spacing: .5px;
            margin-bottom: 12px;
        }
        .info-row {
            display: flex; gap: 10px;
            padding: 10px 0;
            border-bottom: 1px solid #f3f4f6;
            align-items: flex-start;
        }
        .info-row:last-child { border-bottom: none; }
        .info-icon {
            width: 32px; height: 32px;
            background: var(--green-light);
            border-radius: 8px;
            display: flex; align-items: center; justify-content: center;
            color: var(--green); font-size: .95rem; flex-shrink: 0;
        }
        .info-label {
            font-size: .7rem; font-weight: 600;
            color: #9ca3af; text-transform: uppercase;
            letter-spacing: .3px; margin-bottom: 3px;
        }
        .info-val { font-size: .93rem; font-weight: 600; color: #111827; }
        .info-note { font-size: .84rem; color: #6b7280; font-style: italic; }
        .btn-call {
            display: inline-flex; align-items: center; gap: 4px;
            background: #dcfce7; color: var(--green);
            border: none; border-radius: 20px;
            padding: 4px 11px; font-size: .76rem; font-weight: 700;
            text-decoration: none; margin-left: 8px; white-space: nowrap;
        }

        /* ── ACTION CARDS (Tab 2) ─────────────────────────────── */
        .act-card {
            background: #fff;
            border-radius: 14px;
            padding: 16px;
            margin-bottom: 14px;
            box-shadow: var(--card-shadow);
        }
        .act-card__title {
            font-weight: 700; font-size: .88rem;
            color: #374151; margin-bottom: 14px;
            display: flex; align-items: center; gap: 7px;
        }
        .lbl {
            display: block; font-size: .76rem; font-weight: 600;
            color: #6b7280; margin-bottom: 5px;
        }
        .inp {
            width: 100%; border: 1.5px solid #e5e7eb;
            border-radius: 8px; padding: 10px 12px;
            font-size: .88rem; font-family: inherit;
            transition: border-color .2s;
            resize: vertical;
        }
        .inp:focus { border-color: var(--green); outline: none;
            box-shadow: 0 0 0 3px rgba(0,92,37,.1); }

        .btn-delivered {
            width: 100%; border: none; border-radius: 10px;
            padding: 13px; font-weight: 800; font-size: .96rem;
            background: var(--green); color: #fff; cursor: pointer;
            display: flex; align-items: center; justify-content: center; gap: 7px;
            transition: background .2s, transform .1s;
        }
        .btn-delivered:hover  { background: var(--green-dark); }
        .btn-delivered:active { transform: scale(.97); }
        .btn-delivered:disabled { opacity: .6; cursor: not-allowed; }

        .btn-failed {
            width: 100%; border-radius: 10px; padding: 12px;
            font-weight: 700; font-size: .92rem; cursor: pointer;
            display: flex; align-items: center; justify-content: center; gap: 7px;
            background: var(--red-light); color: var(--red);
            border: 2px solid var(--red);
            transition: background .2s, color .2s, transform .1s;
        }
        .btn-failed:hover  { background: var(--red); color: #fff; }
        .btn-failed:active { transform: scale(.97); }

        /* ── SPINNER ──────────────────────────────────────────── */
        .spin {
            display: none; width: 17px; height: 17px;
            border: 2px solid rgba(255,255,255,.4);
            border-top-color: #fff; border-radius: 50%;
            animation: rot .6s linear infinite;
        }
        .spin-red { border-color: rgba(220,38,38,.3); border-top-color: var(--red); }
        @keyframes rot { to { transform: rotate(360deg); } }

        /* ── EMPTY STATE ──────────────────────────────────────── */
        .empty {
            text-align: center; padding: 56px 24px;
            color: #9ca3af;
        }
        .empty i { font-size: 3.2rem; display: block; margin-bottom: 14px; opacity: .35; }
        .empty strong { display: block; color: #6b7280; font-size: .95rem; margin-bottom: 6px; }
        .empty span { font-size: .83rem; }

        /* ── MODAL FAIL REASON ────────────────────────────────── */
        .reason-opt {
            background: #fff; border: 2px solid #e5e7eb;
            border-radius: 10px; padding: 11px 13px;
            margin-bottom: 8px; cursor: pointer;
            display: flex; align-items: center; gap: 10px;
            transition: border-color .15s, background .15s;
            font-size: .88rem;
        }
        .reason-opt:has(input:checked) {
            border-color: var(--red); background: var(--red-light);
        }
        .reason-opt input { accent-color: var(--red); flex-shrink: 0; }
    </style>
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
