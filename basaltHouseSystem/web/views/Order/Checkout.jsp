<%-- Checkout.jsp - BasaltHouse Team --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt Hàng - BasaltHouse</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;600;700;900&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/CartCss/Cart.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/CartCss/Checkout.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body>

<%-- Header chung --%>
<jsp:include page="/views/HomePage/Header.jsp"/>

<div class="co-page">
    <div class="container">

        <!-- Back link -->
        <a href="${pageContext.request.contextPath}/Cart" class="co-back">
            <span class="material-symbols-outlined">arrow_back</span>
            Quay lại giỏ hàng
        </a>

        <div class="co-card">
            <!-- Header -->
            <div class="co-header">
                <span class="material-symbols-outlined">local_shipping</span>
                <div class="co-header-text">
                    <h2>Thông tin giao hàng</h2>
                    <p>Điền địa chỉ và chọn phương thức thanh toán</p>
                </div>
            </div>

            <div class="co-body">
                <!-- Zone notice -->
                <div class="co-zone">
                    <span class="material-symbols-outlined">info</span>
                    <div>
                        <div class="co-zone-title">Khu vực giao hàng</div>
                        <div class="co-zone-desc">Chúng tôi chỉ giao hàng trong khu vực Hòa Lạc — Vui lòng nhập địa chỉ trong phạm vi phục vụ của chúng tôi.</div>
                    </div>
                </div>

                <!-- Order summary -->
                <div class="co-summary">
                    <div style="flex:1;">
                        <div class="co-summary-label">Đơn hàng của bạn</div>
                        <c:forEach var="item" items="${cartItems}">
                            <div class="co-summary-item-line">
                                <c:out value="${item.productName}"/>
                                <c:if test="${not empty item.sizeName}"> (${item.sizeName})</c:if>
                                &times; ${item.quantity}
                            </div>
                        </c:forEach>
                    </div>
                    <div class="text-end">
                        <c:if test="${discountAmount > 0}">
                            <div style="font-size:13px;color:var(--text-muted);text-decoration:line-through;">
                                <fmt:formatNumber value="${totalAmount}" pattern="#,###"/>₫
                            </div>
                            <div style="font-size:11px;color:#16a34a;font-weight:600;margin-bottom:2px;">
                                − <fmt:formatNumber value="${discountAmount}" pattern="#,###"/>₫ giảm giá
                            </div>
                        </c:if>
                        <div class="co-summary-total"><fmt:formatNumber value="${finalAmount > 0 ? finalAmount : totalAmount}" pattern="#,###"/>₫</div>
                    </div>
                </div>

                <form action="${pageContext.request.contextPath}/Cart" method="POST" id="checkoutForm" onsubmit="return validateForm()">
                    <input type="hidden" name="action"        value="checkout">
                    <input type="hidden" name="discountCode"  value="${discountCode}">
                    <input type="hidden" name="orderNote"     value="${orderNote}">

                    <!-- Address section -->
                    <div class="co-section-title">
                        <span class="material-symbols-outlined">location_on</span>
                        Địa chỉ giao hàng
                    </div>

                    <div class="co-grid">
                        <div class="co-field full">
                            <label>Họ và tên người nhận <span class="req">*</span></label>
                            <input type="text" name="fullname" id="fullname" placeholder="Nguyễn Văn A" autocomplete="name">
                        </div>
                        <div class="co-field full">
                            <label>Số điện thoại <span class="req">*</span></label>
                            <input type="tel" name="phone" id="phone" placeholder="0912 345 678" autocomplete="tel">
                        </div>
                        <div class="co-field full">
                            <label>Số nhà / Tên đường <span class="req">*</span></label>
                            <input type="text" name="street" id="street" placeholder="VD: Khu A FPT, Đường số 3..." autocomplete="street-address">
                        </div>
                        <div class="co-field full">
                            <label>Khu vực (Hòa Lạc) <span class="req">*</span></label>
                            <select name="district" id="district" class="co-select">
                                <option value=""> Chọn khu vực </option>
                                <option value="Thạch Hòa, Huyện Thạch Thất, Hà Nội">Thạch Hòa, Thạch Thất, Hà Nội</option>
                                <option value="Tân Xã, Huyện Thạch Thất, Hà Nội">Tân Xã, Thạch Thất, Hà Nội</option>
                                <option value="Hạ Bằng, Huyện Thạch Thất, Hà Nội">Hạ Bằng, Thạch Thất, Hà Nội</option>
                                <option value="Bình Yên, Huyện Thạch Thất, Hà Nội">Bình Yên, Thạch Thất, Hà Nội</option>
                                <option value="Đồng Trúc, Huyện Thạch Thất, Hà Nội">Đồng Trúc, Thạch Thất, Hà Nội</option>
                                <option value="Cẩm Yên, Huyện Thạch Thất, Hà Nội">Cẩm Yên, Thạch Thất, Hà Nội</option>
                                <option value="Liên Quan, Huyện Thạch Thất, Hà Nội">Liên Quan, Thạch Thất, Hà Nội</option>
                            </select>
                        </div>
                        <div class="co-field full">
                            <label>Ghi chú giao hàng (tuỳ chọn)</label>
                            <input type="text" name="deliveryNote" id="deliveryNote" placeholder="Gọi trước khi đến, giao giờ hành chính...">
                        </div>
                    </div>

                    <!-- Hidden: combined deliveryAddress built by JS -->
                    <input type="hidden" name="deliveryAddress" id="deliveryAddressInput" value="">

                    <div class="co-error" id="formError">
                        <span class="material-symbols-outlined">error</span>
                        Vui lòng điền đầy đủ họ tên, số điện thoại, số nhà và phường/quận.
                    </div>

                    <div class="co-divider"></div>

                    <!-- Payment section -->
                    <div class="co-section-title">
                        <span class="material-symbols-outlined">payments</span>
                        Phương thức thanh toán
                    </div>

                    <div class="cart-page-panel payment-section" style="margin-top: 0; border: none; padding: 0; background: transparent; box-shadow: none;">
                        <%-- Option 1: COD --%>
                        <div class="payment-option mb-3">
                            <input type="radio"
                                   name="paymentMethod"
                                   id="pay_cod"
                                   value="COD"
                                   checked>
                            <label class="payment-option-label" for="pay_cod">
                                <div class="payment-icon" style="background:#e8f5e9;">
                                    <i class="bi bi-cash-coin fs-5" style="color:#005c25;"></i>
                                </div>
                                <div>
                                    <div style="font-weight:600;font-size:14px;color:#1a1a1a;">Tiền mặt khi nhận hàng</div>
                                    <div style="font-size:12px;color:#6b7280;margin-top:2px;">COD — Thanh toán khi shipper giao</div>
                                </div>
                            </label>
                        </div>

                        <%-- Option 2: MoMo --%>
                        <div class="payment-option">
                            <input type="radio"
                                   name="paymentMethod"
                                   id="pay_momo"
                                   value="MOMO">
                            <label class="payment-option-label" for="pay_momo">
                                <div class="payment-icon fw-bold text-white"
                                     style="background:#a50064;font-size:12px;letter-spacing:-0.5px;">
                                    MoMo
                                </div>
                                <div>
                                    <div style="font-weight:600;font-size:14px;color:#1a1a1a;">Ví MoMo / QR / ATM</div>
                                    <div style="font-size:12px;color:#6b7280;margin-top:2px;">Quét QR hoặc chọn ngân hàng trên MoMo</div>
                                </div>
                            </label>
                        </div>
                    </div>

                    <button type="submit" class="co-submit" id="submitBtn">
                        <span class="material-symbols-outlined">check_circle</span>
                        Xác nhận đặt hàng
                    </button>
                </form>
            </div>
        </div>

    </div>
</div>

<script>


function validateForm() {
    const fn  = document.getElementById('fullname').value.trim();
    const ph  = document.getElementById('phone').value.trim();
    const st  = document.getElementById('street').value.trim();
    const dis = document.getElementById('district').value.trim();
    const err = document.getElementById('formError');

    if (!fn || !ph || !st || !dis) {
        err.style.display = 'flex';
        return false;
    }
    err.style.display = 'none';

    // Build combined address string
    const note = document.getElementById('deliveryNote').value.trim();
    document.getElementById('deliveryAddressInput').value =
        fn + ' | ' + ph + ' | ' + st + ', ' + dis + (note ? ' | ' + note : '');

    const btn = document.getElementById('submitBtn');
    btn.disabled = true;
    btn.innerHTML = '<span class="material-symbols-outlined">hourglass_top</span> Đang xử lý...';
    return true;
}
</script>
</body>
</html>
