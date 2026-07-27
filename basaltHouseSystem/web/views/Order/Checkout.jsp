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
    <link href="${pageContext.request.contextPath}/css/CartCss/Cart.css?v=20260709-1" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/CartCss/Checkout.css?v=20260709-1" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/Customer/CustomerTheme.css?v=20260709-1" rel="stylesheet">
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

        <form action="${pageContext.request.contextPath}/Cart" method="POST" id="checkoutForm" onsubmit="return validateForm()">
            <input type="hidden" name="action"        value="checkout">
            <input type="hidden" name="discountCode"  value="${discountCode}">
            <input type="hidden" name="orderNote"     value="${orderNote}">
            <!-- Hidden: combined deliveryAddress built by JS -->
            <input type="hidden" name="deliveryAddress" id="deliveryAddressInput" value="">

            <div class="row g-4">
                
                <!-- ── Left Column: Thông tin giao hàng ── -->
                <div class="col-lg-7">
                    <div class="co-card-panel">
                        <!-- Header -->
                        <div class="co-header">
                            <span class="material-symbols-outlined">local_shipping</span>
                            <div class="co-header-text">
                                <h2>Thông tin giao hàng</h2>
                                <p>Điền địa chỉ giao hàng và thông tin liên hệ của bạn</p>
                            </div>
                        </div>

                        <div class="co-body">
                            <!-- Zone notice -->
                            <div class="co-zone">
                                <span class="material-symbols-outlined">info</span>
                                <div>
                                    <div class="co-zone-title">Khu vực giao hàng</div>
                                    <div class="co-zone-desc">Chúng tôi chỉ giao hàng trong khu vực — Vui lòng nhập địa chỉ trong phạm vi phục vụ của chúng tôi.</div>
                                </div>
                            </div>

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
                                    <label>Khu vực <span class="req">*</span></label>
                                    <select name="district" id="district" class="co-select">
                                        <option value=""> Chọn khu vực </option>
                                        <c:forEach var="zone" items="${activeZones}">
                                            <option value="${zone.wardName}, ${zone.district}, ${zone.province}">
                                                ${zone.wardName}, ${zone.district}, ${zone.province}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="co-field full">
                                    <label>Ghi chú giao hàng (tuỳ chọn)</label>
                                    <input type="text" name="deliveryNote" id="deliveryNote" placeholder="Gọi trước khi đến, giao giờ hành chính...">
                                </div>
                            </div>

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
                            
                            <!-- Submit button -->
                            <button type="submit" class="co-submit" id="submitBtn">
                                <span class="material-symbols-outlined">check_circle</span>
                                Xác nhận đặt hàng
                            </button>
                        </div>
                    </div>
                </div>

                <!-- ── Right Column: Đơn hàng của bạn ── -->
                <div class="col-lg-5">
                    <div class="co-summary-card">
                        <!-- Header -->
                        <div class="co-summary-header">
                            <span class="material-symbols-outlined">receipt_long</span>
                            <h3>Đơn Hàng Của Bạn</h3>
                        </div>

                        <c:if test="${not empty memberTier}">
                            <div class="co-member-badge-container">
                                <span class="material-symbols-outlined member-icon">stars</span>
                                <span class="member-text">
                                    Thành viên: <span class="badge">${memberTier}</span>
                                    <c:if test="${memberDiscountPercent > 0}">
                                        (Ưu đãi giảm <fmt:formatNumber value="${memberDiscountPercent}" pattern="0.#"/>%)
                                    </c:if>
                                </span>
                            </div>
                        </c:if>

                        <div class="co-summary-body">
                            <!-- Items list -->
                            <div class="co-summary-items-list">
                                <c:forEach var="item" items="${cartItems}">
                                    <div class="co-summary-item-row">
                                        <div class="co-summary-item-info">
                                            <span class="co-summary-item-name"><c:out value="${item.productName}"/></span>
                                            <div class="co-summary-item-meta">
                                                <c:if test="${not empty item.sizeName}">
                                                    <span class="co-summary-item-size">Size ${item.sizeName}</span>
                                                </c:if>
                                                <span class="co-summary-item-qty">x${item.quantity}</span>
                                            </div>
                                        </div>
                                        <div class="co-summary-item-price">
                                            <fmt:formatNumber value="${item.subtotal}" pattern="#,###"/>₫
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>

                            <div class="co-summary-divider"></div>

                            <!-- Calculations breakdown -->
                            <div class="co-summary-pricing">
                                <div class="co-pricing-row">
                                    <span>Tạm tính</span>
                                    <span><fmt:formatNumber value="${totalAmount}" pattern="#,###"/>₫</span>
                                </div>
                                <c:if test="${memberDiscountAmount > 0}">
                                    <div class="co-pricing-row discount member-disc">
                                        <span class="co-pricing-row-label-with-icon">
                                            <span class="material-symbols-outlined">stars</span>
                                            Ưu đãi hội viên (${memberTier})
                                        </span>
                                        <span>- <fmt:formatNumber value="${memberDiscountAmount}" pattern="#,###"/>₫</span>
                                    </div>
                                </c:if>
                                <c:if test="${couponDiscountAmount > 0}">
                                    <div class="co-pricing-row discount">
                                        <span class="co-pricing-row-label-with-icon">
                                            <span class="material-symbols-outlined">sell</span>
                                            Voucher giảm giá
                                        </span>
                                        <span>- <fmt:formatNumber value="${couponDiscountAmount}" pattern="#,###"/>₫</span>
                                    </div>
                                </c:if>
                                <div class="co-summary-divider"></div>
                                <div class="co-pricing-row total">
                                    <span>Thành tiền</span>
                                    <span><fmt:formatNumber value="${finalAmount}" pattern="#,###"/>₫</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        </form>
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
        // Scroll to the error message smoothly
        err.scrollIntoView({ behavior: 'smooth', block: 'center' });
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
