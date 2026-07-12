<%-- Cart.jsp - BasaltHouse Team --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Giỏ Hàng - BasaltHouse</title>
        <meta name="description" content="Xem và xác nhận đơn hàng của bạn tại BasaltHouse">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700;900&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/CartCss/Cart.css?v=20260709-1" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/Customer/CustomerTheme.css?v=20260709-1" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/CartCss/Cart.css?v=1.2" rel="stylesheet">
        <link rel="stylesheet"
              href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

    </head>
    <body>

        <!-- Toast Stack -->
        <div class="toast-stack" id="toastStack"></div>

        <%-- Header chung --%>
        <jsp:include page="/views/HomePage/Header.jsp"/>

        <!-- ── Page Header ── -->
        <div class="page-header">
            <div class="container">
                <div class="d-flex justify-content-between align-items-start flex-wrap gap-3">
                    <div>
                        <div class="page-header-badge">
                            <span class="material-symbols-outlined page-header-badge-icon">shopping_cart</span>
                            Giỏ hàng
                        </div>
                        <h1>Xác Nhận Đơn Hàng</h1>
                        <p>Kiểm tra lại sản phẩm và xác nhận để đặt hàng</p>
                    </div>
                    <div class="page-header-meta">
                        <div class="meta-chip">
                            <span class="material-symbols-outlined">event</span>
                            <span id="currentDateTime">--</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- ── Main Content ── -->
        <main class="order-main">
            <div class="container">
                <c:choose>
                    <c:when test="${param.checkoutSuccess == '1'}">
                        <!-- Success Screen -->
                        <div class="cart-success-screen" style="text-align:center; padding:60px 20px; background:#fff; border-radius:16px; border:1px solid rgba(0,0,0,0.06); max-width:500px; margin:40px auto; box-shadow:0 8px 24px rgba(0,0,0,0.10); display:flex; flex-direction:column; align-items:center; justify-content:center;">
                            <span class="material-symbols-outlined cart-success-icon" style="font-size:64px; color:#22c55e; margin-bottom:12px; display:block;">check_circle</span>
                            <h3 class="cart-success-title" style="font-family:'Montserrat',sans-serif; font-weight:700; margin-bottom:8px; color:#191c1e;">Đặt Hàng Thành Công!</h3>
                            <p class="cart-success-desc" style="font-size:14px; color:#3d4a3d; margin-bottom:20px;">Đơn hàng của bạn đã được gửi đến nhà hàng. Vui lòng chờ phục vụ.</p>
                            <div class="cart-success-code" style="background:#f0fdf4; border-radius:8px; padding:12px; font-weight:700; color:#006e2f; font-family:'Montserrat',sans-serif; margin-bottom:24px; font-size:16px;">
                                Mã đơn hàng: <c:out value="${param.code}"/>
                            </div>
                            <div style="display: flex; justify-content: center; margin-top: 20px;">
                                <a href="${pageContext.request.contextPath}/category" 
                                   style="display:inline-flex;align-items:center;gap:8px;justify-content:center;width:auto;padding:12px 32px;border:none;border-radius:8px;background:#006e2f;color:#fff;font-family:'Montserrat',sans-serif;font-size:14px;font-weight:700;cursor:pointer;text-decoration:none;transition:background .2s;"
                                   onmouseover="this.style.background='#005321'" onmouseout="this.style.background='#006e2f'">
                                    <span class="material-symbols-outlined">storefront</span>Quay lại Thực đơn
                                </a>
                            </div>
                        </div>
                    </c:when>
                    <c:when test="${empty cartItems}">
                        <!-- Empty State -->
                        <div class="cart-empty-screen" style="text-align:center; padding:80px 20px; background:#fff; border-radius:16px; border:1px solid rgba(0,0,0,0.06); max-width:600px; margin:40px auto; box-shadow:0 8px 24px rgba(0,0,0,0.10); display:flex; flex-direction:column; align-items:center; justify-content:center;">
                            <span class="material-symbols-outlined cart-empty-icon" style="font-size:64px; display:block; margin-bottom:14px; color:#d1d5db;">shopping_cart</span>
                            <h3 class="cart-empty-title" style="font-family:'Montserrat',sans-serif; font-weight:700; margin-bottom:8px; color:#191c1e;">Giỏ hàng trống</h3>
                            <p class="cart-empty-desc" style="font-size:14px; color:#3d4a3d; margin-bottom:24px;">Bạn chưa thêm sản phẩm nào vào giỏ hàng.</p>
                            <div style="display: flex; justify-content: center; margin-top: 20px;">
                                <a href="${pageContext.request.contextPath}/category"
                                   style="display:inline-flex;align-items:center;gap:8px;justify-content:center;width:auto;padding:12px 32px;border:none;border-radius:8px;background:#006e2f;color:#fff;font-family:'Montserrat',sans-serif;font-size:14px;font-weight:700;cursor:pointer;text-decoration:none;transition:background .2s;"
                                   onmouseover="this.style.background='#005321'" onmouseout="this.style.background='#006e2f'">
                                    <span class="material-symbols-outlined">storefront</span>
                                    Xem thực đơn
                                </a>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <!-- Filled Cart Grid -->
                         <form action="${pageContext.request.contextPath}/Cart" method="GET">
                             <input type="hidden" name="action" value="checkout-form"/>
                            <div class="row g-4">

                                <!-- ── Left Column: Bảng sản phẩm ── -->
                                <div class="col-lg-8">
                                    <div class="cart-page-panel">
                                        <div class="panel-label">Đơn hàng của bạn</div>
                                        <div class="panel-title" id="cartPageTitle">Giỏ Hàng (${totalQty} sản phẩm)</div>

                                        <div class="cart-table-wrap" id="cartTableWrap">
                                            <table class="cart-table">
                                                <thead>
                                                    <tr>
                                                        <th>Sản phẩm</th>
                                                        <th style="text-align:center">Số lượng</th>
                                                        <th style="text-align:right">Đơn giá</th>
                                                        <th style="text-align:right">Thành tiền</th>
                                                        <th></th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="item" items="${cartItems}">
                                                        <tr>
                                                            <td>
                                                                <div class="ct-product-wrap">
                                                                    <div class="ct-icon"><span class="material-symbols-outlined">fastfood</span></div>
                                                                    <div>
                                                                        <div class="ct-name"><c:out value="${item.productName}"/></div>
                                                                        <div class="ct-cat" style="display:flex;align-items:center;gap:6px;flex-wrap:wrap;margin-top:4px;">
                                                                            <c:if test="${not empty item.sizeName}">
                                                                                <span style="display:inline-flex;align-items:center;gap:3px;background:#f0f4ff;color:#4f46e5;border-radius:20px;padding:2px 8px;font-size:11px;font-weight:600;">
                                                                                    <span class="material-symbols-outlined" style="font-size:12px;">straighten</span>
                                                                                    Size ${item.sizeName}
                                                                                </span>
                                                                            </c:if>
                                                                            <c:if test="${item.stock > 0}">
                                                                                <span style="display:inline-flex;align-items:center;gap:3px;background:#f0fdf4;color:#16a34a;border-radius:20px;padding:2px 8px;font-size:11px;font-weight:600;">
                                                                                    <span class="material-symbols-outlined" style="font-size:12px;">coffee</span>
                                                                                    ${item.stock} cốc
                                                                                </span>
                                                                            </c:if>
                                                                            <c:if test="${item.stock <= 0 and not empty item.sizeName}">
                                                                                <span style="display:inline-flex;align-items:center;gap:3px;background:#fef2f2;color:#dc2626;border-radius:20px;padding:2px 8px;font-size:11px;font-weight:600;">
                                                                                    <span class="material-symbols-outlined" style="font-size:12px;">coffee</span>
                                                                                    Hết hàng
                                                                                </span>
                                                                            </c:if>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </td>
                                                            <td style="text-align:center">
                                                                <div class="ct-qty-wrap" style="justify-content:center">
                                                                    <%-- Nút trừ: disable khi qty = 1 --%>
                                                                    <c:choose>
                                                                        <c:when test="${item.quantity <= 1}">
                                                                            <span class="ct-qty-btn ct-qty-btn--disabled" title="Số lượng tối thiểu">−</span>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <a href="${pageContext.request.contextPath}/Cart?action=update&productId=${empty item.cartKey ? item.productId : item.cartKey}&delta=-1" class="ct-qty-btn" style="text-decoration:none;">−</a>
                                                                        </c:otherwise>
                                                                    </c:choose>

                                                                    <span class="ct-qty-num">${item.quantity}</span>

                                                                    <%-- Nút cộng: disable khi qty >= stock --%>
                                                                    <c:choose>
                                                                        <c:when test="${item.stock > 0 and item.quantity >= item.stock}">
                                                                            <span class="ct-qty-btn ct-qty-btn--disabled" title="Đã đạt giới hạn tồn kho (${item.stock} cốc)">+</span>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <a href="${pageContext.request.contextPath}/Cart?action=update&productId=${empty item.cartKey ? item.productId : item.cartKey}&delta=1" class="ct-qty-btn" style="text-decoration:none;">+</a>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </div>
                                                            </td>
                                                            <td style="text-align:right"><span class="ct-price"><fmt:formatNumber value="${item.price}" pattern="#,###"/>₫</span></td>
                                                            <td style="text-align:right"><span class="ct-subtotal"><fmt:formatNumber value="${item.subtotal}" pattern="#,###"/>₫</span></td>
                                                            <td>
                                                                <a href="${pageContext.request.contextPath}/Cart?action=remove&productId=${empty item.cartKey ? item.productId : item.cartKey}" class="ct-remove" title="Xóa sản phẩm" style="text-decoration:none;">
                                                                    <span class="material-symbols-outlined" style="font-size:20px">delete</span>
                                                                </a>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>

                                        <div style="margin-top:16px;display:flex;gap:10px;justify-content:flex-end;align-items:stretch;">
                                            <a href="${pageContext.request.contextPath}/Cart?action=clear"
                                               style="display:inline-flex;align-items:center;gap:6px;padding:9px 20px;border-radius:8px;font-size:13px;font-weight:600;font-family:'Inter',sans-serif;text-decoration:none;background:#fff;border:1.5px solid #ef4444;color:#ef4444;cursor:pointer;transition:background .2s,color .2s;"
                                               onmouseover="this.style.background='#ef4444';this.style.color='#fff'" onmouseout="this.style.background='#fff';this.style.color='#ef4444'">
                                                <span class="material-symbols-outlined" style="font-size:16px">delete_sweep</span>
                                                Xóa tất cả
                                            </a>
                                            <a href="${pageContext.request.contextPath}/category"
                                               style="display:inline-flex;align-items:center;gap:6px;padding:9px 20px;border-radius:8px;font-size:13px;font-weight:600;font-family:'Inter',sans-serif;text-decoration:none;background:#fff;border:1.5px solid #006e2f;color:#006e2f;cursor:pointer;transition:background .2s,color .2s;"
                                               onmouseover="this.style.background='#006e2f';this.style.color='#fff'" onmouseout="this.style.background='#fff';this.style.color='#006e2f'">
                                                <span class="material-symbols-outlined" style="font-size:16px">add</span>
                                                Thêm sản phẩm
                                            </a>
                                        </div>
                                    </div>
                                </div>

                                <!-- ── Right Column: Tóm tắt + Thanh toán + Ghi chú ── -->
                                <div class="col-lg-4">

                                    <%-- Panel 1: Tóm tắt đơn hàng (giữ nguyên) --%>
                                    <div class="cart-summary-card">
                                        <div class="cart-summary-header">
                                            <h3>Tóm Tắt Đơn Hàng</h3>
                                        </div>
                                        <div class="cart-summary-body">
                                            <c:forEach var="item" items="${cartItems}">
                                                <div class="summary-row">
                                                    <span class="summary-row-label">
                                                        <c:out value="${item.productName}"/>
                                                        <c:if test="${not empty item.sizeName}">
                                                            <span class="summary-size-tag">${item.sizeName}</span>
                                                        </c:if>
                                                        <span class="summary-qty-tag">×${item.quantity}</span>
                                                    </span>
                                                    <span class="summary-row-val"><fmt:formatNumber value="${item.subtotal}" pattern="#,###"/>₫</span>
                                                </div>
                                            </c:forEach>

                                            <%-- Ô nhập mã giảm giá --%>
                                            <div class="summary-divider"></div>
                                            <div class="discount-section">
                                                <div class="discount-section-label">Mã giảm giá</div>
                                                <div class="discount-input-row">
                                                    <input type="text" id="discountCodeInput"
                                                           class="discount-input"
                                                           placeholder="Nhập mã giảm giá..."
                                                           value="${discountCode}"
                                                           autocomplete="off">
                                                    <button type="button" class="btn-apply-discount" id="btnApplyDiscount"
                                                            onclick="applyDiscount()">Áp dụng</button>
                                                     <button type="button" id="btnRemoveDiscount"
                                                             onclick="removeDiscount()"
                                                             class="btn-remove-discount">Xóa</button>
                                                </div>
                                                <div id="discountMsg" class="discount-msg"></div>
                                                <%-- Nút kích hoạt Modal chọn voucher --%>
                                                <div class="voucher-trigger-row">
                                                    <button type="button" onclick="openVoucherModal()" class="voucher-trigger-btn">
                                                        <span class="material-symbols-outlined">confirmation_number</span>
                                                        <span style="flex: 1;">Chọn hoặc nhập mã giảm giá khác</span>
                                                        <c:if test="${not empty myVouchers}">
                                                            <span class="voucher-count-badge">${myVouchers.size()}</span>
                                                        </c:if>
                                                        <span class="material-symbols-outlined" style="font-size: 16px; opacity: 0.6;">chevron_right</span>
                                                    </button>
                                                </div>
                                            </div>

                                            <%-- Totals block --%>
                                            <div class="summary-totals-block">
                                                <div class="summary-subtotal-row">
                                                    <span class="summary-subtotal-label">Tạm tính</span>
                                                    <span class="summary-subtotal-val" id="summaryTotal"><fmt:formatNumber value="${totalAmount}" pattern="#,###"/>₫</span>
                                                </div>
                                                <div class="summary-discount-row" id="discountRow" style="display:none;">
                                                    <span class="summary-discount-label">Giảm giá</span>
                                                    <span class="summary-discount-val" id="discountVal">−0₫</span>
                                                </div>
                                                <div class="summary-divider"></div>
                                                <div class="summary-grand-row" id="finalRow">
                                                    <span class="summary-grand-label" style="font-weight: 900 !important;">Thành tiền</span>
                                                    <span class="summary-grand-val" id="finalVal" style="font-weight: 900 !important;"><fmt:formatNumber value="${totalAmount}" pattern="#,###"/>₫</span>
                                                </div>
                                            </div>
                                        </div>
                                        <%-- Nút đặt hàng --%>
                                        <div class="cart-checkout-footer">
                                            <input type="hidden" name="discountCode" id="discountCodeHidden" value="${discountCode}">
                                            <button type="submit" class="btn-checkout">
                                                <span class="material-symbols-outlined">shopping_bag</span>
                                                Đặt Hàng Ngay
                                            </button>
                                        </div>
                                    </div>


                                    <%-- Panel 3: Ghi chú (giữ nguyên) --%>
                                    <div class="cart-page-panel" style="margin-top:16px;">
                                        <div class="panel-label">Thông tin</div>
                                        <div class="panel-title" style="font-size:16px;margin-bottom:16px;">Ghi chú đơn hàng</div>
                                        <textarea name="orderNote" rows="3" placeholder="Ghi chú thêm (tuỳ chọn)..."
                                                  style="width:100%;border:1.5px solid rgba(0,0,0,0.06);border-radius:8px;padding:10px 12px;font-size:13px;font-family:'Inter',sans-serif;resize:none;outline:none;color:#191c1e;background:#f7f9fb;box-sizing:border-box;"
                                                  onfocus="this.style.borderColor='#006e2f'" onblur="this.style.borderColor='rgba(0,0,0,0.06)'"></textarea>
                                    </div>

                                </div><%-- /col-lg-4 --%>
                            </div><%-- /row --%>
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>
        </main>

        <%-- Modal Chọn Voucher --%>
        <div id="voucherModal" class="voucher-modal">
            <div class="voucher-modal-content">
                <%-- Header --%>
                <div class="voucher-modal-header">
                    <h3 class="voucher-modal-title">Khuyến mãi của bạn</h3>
                    <button type="button" onclick="closeVoucherModal()" class="voucher-modal-close">
                        <span class="material-symbols-outlined">close</span>
                    </button>
                </div>
                <%-- Body (scrollable list) --%>
                <div class="voucher-modal-body">
                    <c:choose>
                        <c:when test="${not empty myVouchers}">
                            <c:forEach var="v" items="${myVouchers}">
                                <div class="voucher-card">
                                    <%-- Left stamp column --%>
                                    <div class="voucher-stamp">
                                        <span class="material-symbols-outlined voucher-stamp-icon">confirmation_number</span>
                                        <div class="voucher-stamp-text">Voucher</div>
                                    </div>
                                    <%-- Right info column --%>
                                    <div class="voucher-info">
                                        <div class="voucher-code">${v.code}</div>
                                        <div class="voucher-desc">
                                            <c:out value="${v.description}"/>
                                        </div>
                                        <div class="voucher-meta">
                                            <span class="voucher-value">
                                                <c:choose>
                                                    <c:when test="${v.discountPercent != null and v.discountPercent > 0}">Giảm ${v.discountPercent}%</c:when>
                                                    <c:otherwise>Giảm <fmt:formatNumber value="${v.discountAmount}" pattern="#,###"/>₫</c:otherwise>
                                                </c:choose>
                                            </span>
                                            <c:if test="${v.dayTotal >= 0}">
                                                <span class="voucher-expiry">Còn ${v.dayTotal} ngày</span>
                                            </c:if>
                                        </div>
                                    </div>
                                    <%-- Apply action link --%>
                                    <div class="voucher-action">
                                        <button type="button" onclick="useVoucherFromModal('${v.code}')" class="btn-use-voucher">
                                            Dùng
                                        </button>
                                    </div>
                                </div>
                            </c:forEach>
                            <c:if test="${empty sessionScope.currentUser}">
                                <div class="voucher-login-prompt">
                                    <a href="${pageContext.request.contextPath}/login">Đăng nhập</a> để mở khóa thêm voucher cá nhân của bạn.
                                </div>
                            </c:if>
                        </c:when>
                        <c:otherwise>
                            <div class="voucher-empty-msg">
                                <span class="material-symbols-outlined">sentiment_dissatisfied</span>
                                <div class="voucher-empty-text">
                                    <c:choose>
                                        <c:when test="${empty sessionScope.currentUser}">
                                            Vui lòng <a href="${pageContext.request.contextPath}/login">Đăng nhập</a> để xem các voucher cá nhân.
                                        </c:when>
                                        <c:otherwise>Bạn không có mã giảm giá nào khả dụng lúc này.</c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            /* ── DateTime ── */
            function updateClock() {
                const el = document.getElementById('currentDateTime');
                if (el) {
                    el.textContent = new Date().toLocaleString('vi-VN', {
                        weekday: 'short', year: 'numeric', month: '2-digit',
                        day: '2-digit', hour: '2-digit', minute: '2-digit'
                    });
                }
            }
            updateClock();
            setInterval(updateClock, 30000);

            /* ── Mã giảm giá ── */
            function formatVND(n) {
                return new Intl.NumberFormat('vi-VN').format(n) + '₫';
            }

            function applyDiscount() {
                const code = document.getElementById('discountCodeInput').value.trim();
                const msg  = document.getElementById('discountMsg');
                const btn  = document.getElementById('btnApplyDiscount');

                if (!code) {
                    msg.style.color = '#ef4444';
                    msg.textContent = 'Vui lòng nhập mã giảm giá.';
                    return;
                }

                btn.disabled = true;
                btn.textContent = 'Đang kiểm tra...';
                msg.textContent = '';

                const ctx = '${pageContext.request.contextPath}';
                fetch(ctx + '/Cart?action=applyDiscount&discountCode=' + encodeURIComponent(code))
                    .then(r => r.json())
                    .then(data => {
                        btn.disabled = false;
                        btn.textContent = 'Áp dụng';

                        if (data.success) {
                            msg.style.color = '#16a34a';
                            msg.textContent = ' Áp dụng thành công: ' + data.codeName;

                            document.getElementById('discountRow').style.display = '';
                            document.getElementById('discountVal').textContent    = '−' + formatVND(data.discountAmount);
                            document.getElementById('finalVal').textContent       = formatVND(data.finalAmount);
                            document.getElementById('discountCodeHidden').value   = code;
                            document.getElementById('btnRemoveDiscount').style.display = 'inline-block';
                        } else {
                            msg.style.color = '#ef4444';
                            msg.textContent = ' ' + (data.error || 'Mã không hợp lệ hoặc đã hết hạn.');
                            document.getElementById('discountRow').style.display = 'none';
                            // Reset finalVal về tổng gốc
                            const origTotal = document.getElementById('summaryTotal').textContent;
                            document.getElementById('finalVal').textContent = origTotal;
                            document.getElementById('discountCodeHidden').value = '';
                            document.getElementById('btnRemoveDiscount').style.display = 'none';
                        }
                    })
                    .catch(() => {
                        btn.disabled = false;
                        btn.textContent = 'Áp dụng';
                        msg.style.color = '#ef4444';
                        msg.textContent = 'Lỗi kết nối, vui lòng thử lại.';
                        document.getElementById('btnRemoveDiscount').style.display = 'none';
                    });
            }

            function removeDiscount() {
                document.getElementById('discountCodeInput').value = '';
                document.getElementById('discountCodeHidden').value = '';
                document.getElementById('discountRow').style.display = 'none';
                
                // Reset finalVal về tổng gốc
                const origTotal = document.getElementById('summaryTotal').textContent;
                document.getElementById('finalVal').textContent = origTotal;
                
                // Xóa tin nhắn và ẩn nút xóa
                const msg = document.getElementById('discountMsg');
                msg.textContent = '';
                document.getElementById('btnRemoveDiscount').style.display = 'none';
                
                // Kích hoạt lại input và nút áp dụng nếu cần
                document.getElementById('discountCodeInput').disabled = false;
                document.getElementById('btnApplyDiscount').disabled = false;
            }

            /* Enter để áp dụng mã */
            const dcInput = document.getElementById('discountCodeInput');
            if (dcInput) {
                dcInput.addEventListener('keydown', e => {
                    if (e.key === 'Enter') { e.preventDefault(); applyDiscount(); }
                });
            }

            /* Click chọn voucher từ danh sách */
            function useVoucher(code) {
                const dcInput = document.getElementById('discountCodeInput');
                if (dcInput) {
                    dcInput.value = code;
                    applyDiscount();
                }
            }

            /* Modal voucher */
            function openVoucherModal() {
                const modal = document.getElementById('voucherModal');
                modal.style.display = 'flex';
                setTimeout(() => {
                    modal.querySelector('.voucher-modal-content').style.transform = 'scale(1)';
                }, 10);
            }

            function closeVoucherModal() {
                const modal = document.getElementById('voucherModal');
                modal.querySelector('.voucher-modal-content').style.transform = 'scale(0.9)';
                setTimeout(() => {
                    modal.style.display = 'none';
                }, 200);
            }

            function useVoucherFromModal(code) {
                closeVoucherModal();
                useVoucher(code);
            }

            /* ── Auto-apply nếu mã voucher đã được điền sẵn từ session ── */
            window.addEventListener('DOMContentLoaded', function () {
                const preFilledCode = document.getElementById('discountCodeInput');
                if (preFilledCode && preFilledCode.value.trim() !== '') {
                    // Delay nhỏ để đảm bảo các element khác đã render xong
                    setTimeout(function () {
                        applyDiscount();
                    }, 300);
                }
            });
        </script>
    </body>
</html>
