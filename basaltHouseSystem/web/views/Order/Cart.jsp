<%-- Cart.jsp - BasaltHouse Team --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
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
    <link href="${pageContext.request.contextPath}/css/CartCss/Cart.css" rel="stylesheet">
</head>
<body>

<!-- Toast Stack -->
<div class="toast-stack" id="toastStack"></div>

<!-- ── Navbar Khách Hàng ── -->
<header class="sticky-top">
    <nav class="navbar navbar-expand-md navbar-light navbar-coffeely py-3">
        <div class="container">
            <a class="navbar-brand navbar-brand-coffeely" href="${pageContext.request.contextPath}/Order">BasaltHouse</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse justify-content-between" id="mainNav">
                <ul class="navbar-nav mx-auto mb-2 mb-lg-0">
                    <li class="nav-item"><a class="nav-link nav-link-coffeely" href="${pageContext.request.contextPath}/category">Menu</a></li>
                    <li class="nav-item"><a class="nav-link nav-link-coffeely active" href="${pageContext.request.contextPath}/Cart">Giỏ Hàng</a></li>
                </ul>
                <div class="d-flex align-items-center gap-2">
                    <button class="btn-nav-icon cart-nav-btn" title="Giỏ hàng" onclick="window.location='${pageContext.request.contextPath}/Cart'">
                        <span class="material-symbols-outlined">shopping_cart</span>
                        <span class="cart-badge ${totalQty > 0 ? 'visible' : ''}" id="navCartBadge">${totalQty}</span>
                    </button>
                    <button class="btn-nav-icon" title="Tài khoản">
                        <span class="material-symbols-outlined">account_circle</span>
                    </button>
                </div>
            </div>
        </div>
    </nav>
</header>

<!-- ── Page Header ── -->
<div class="page-header">
    <div class="container">
        <div class="d-flex justify-content-between align-items-start flex-wrap gap-3">
            <div>
                <div class="page-header-badge">
                    <span class="material-symbols-outlined">shopping_cart</span>
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
                <div class="cart-status-card success">
                    <span class="material-symbols-outlined cart-status-icon">check_circle</span>
                    <h3>Đặt Hàng Thành Công!</h3>
                    <p>Đơn hàng của bạn đã được gửi đến nhà hàng. Vui lòng chờ phục vụ.</p>
                    <div class="cart-order-code">Mã đơn hàng: <c:out value="${param.code}"/></div>
                    <a href="${pageContext.request.contextPath}/category" class="btn-checkout btn-checkout-inline">
                        <span class="material-symbols-outlined">storefront</span>Quay lại Thực đơn
                    </a>
                </div>
            </c:when>
            <c:when test="${empty cartItems}">
                <!-- Empty State -->
                <div class="cart-status-card empty">
                    <span class="material-symbols-outlined cart-status-icon">shopping_cart</span>
                    <h3>Giỏ hàng trống</h3>
                    <p>Bạn chưa thêm sản phẩm nào vào giỏ hàng.</p>
                    <a class="btn-checkout btn-checkout-inline" href="${pageContext.request.contextPath}/category">
                        <span class="material-symbols-outlined">storefront</span>
                        Xem thực đơn
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <!-- Filled Cart Grid -->
                <form action="${pageContext.request.contextPath}/Cart" method="POST">
                    <input type="hidden" name="action" value="checkout"/>
                    <div class="row g-4">
                        <!-- Left Column: Table -->
                        <div class="col-lg-8">
                            <div class="cart-page-panel">
                                <div class="panel-label">Đơn hàng của bạn</div>
                                <div class="panel-title" id="cartPageTitle">Giỏ Hàng (${totalQty} sản phẩm)</div>

                                <div class="cart-table-wrap" id="cartTableWrap">
                                    <table class="cart-table">
                                        <thead>
                                            <tr>
                                                <th>Sản phẩm</th>
                                                <th class="text-center">Số lượng</th>
                                                <th class="text-right">Đơn giá</th>
                                                <th class="text-right">Thành tiền</th>
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
                                                                <div class="ct-cat-row">
                                                                    <c:if test="${not empty item.sizeName}">
                                                                        <span class="badge-size">
                                                                            <span class="material-symbols-outlined">straighten</span>
                                                                            Size ${item.sizeName}
                                                                        </span>
                                                                    </c:if>
                                                                    <c:if test="${item.stock > 0}">
                                                                        <span class="badge-stock">
                                                                            <span class="material-symbols-outlined">coffee</span>
                                                                            ${item.stock} cốc
                                                                        </span>
                                                                    </c:if>
                                                                    <c:if test="${item.stock <= 0 and not empty item.sizeName}">
                                                                        <span class="badge-out">
                                                                            <span class="material-symbols-outlined">coffee</span>
                                                                            Hết hàng
                                                                        </span>
                                                                    </c:if>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </td>
                                                    <td class="text-center">
                                                        <div class="ct-qty-wrap-center">
                                                            <a href="${pageContext.request.contextPath}/Cart?action=update&productId=${item.cartKey}&delta=-1" class="ct-qty-btn">−</a>
                                                            <span class="ct-qty-num">${item.quantity}</span>
                                                            <a href="${pageContext.request.contextPath}/Cart?action=update&productId=${item.cartKey}&delta=1" class="ct-qty-btn">+</a>
                                                        </div>
                                                    </td>
                                                    <td class="text-right"><span class="ct-price"><fmt:formatNumber value="${item.price}" pattern="#,###"/>₫</span></td>
                                                    <td class="text-right"><span class="ct-subtotal"><fmt:formatNumber value="${item.subtotal}" pattern="#,###"/>₫</span></td>
                                                    <td>
                                                        <a href="${pageContext.request.contextPath}/Cart?action=remove&productId=${item.cartKey}" class="ct-remove" title="Xóa sản phẩm">
                                                            <span class="material-symbols-outlined">delete</span>
                                                        </a>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>

                                <div class="cart-actions-row">
                                    <a href="${pageContext.request.contextPath}/Cart?action=clear" class="btn-cart-danger">
                                        <span class="material-symbols-outlined">delete_sweep</span>
                                        Xóa tất cả
                                    </a>
                                    <a href="${pageContext.request.contextPath}/category" class="btn-cart-primary">
                                        <span class="material-symbols-outlined">add</span>
                                        Thêm sản phẩm
                                    </a>
                                </div>
                            </div>
                        </div>

                        <!-- Right Column: Summary -->
                        <div class="col-lg-4">
                            <div class="cart-summary-card">
                                <div class="cart-summary-header">
                                    <h3>Tóm Tắt Đơn Hàng</h3>
                                </div>
                                <div class="cart-summary-body">
                                    <c:forEach var="item" items="${cartItems}">
                                        <div class="summary-row">
                                            <span class="summary-row-label"><c:out value="${item.productName}"/> × ${item.quantity}</span>
                                            <span class="summary-row-val"><fmt:formatNumber value="${item.subtotal}" pattern="#,###"/>₫</span>
                                        </div>
                                    </c:forEach>

                                    <%-- Ô nhập mã giảm giá --%>
                                    <div class="summary-divider"></div>
                                    <div class="discount-section">
                                        <div class="discount-section-label">Mã giảm giá</div>
                                        <div class="discount-input-row">
                                            <input type="text" id="discountInput" class="discount-input" placeholder="Nhập mã...">
                                            <button type="button" id="applyDiscountBtn" class="btn-apply-discount" onclick="applyDiscount()">
                                                Áp dụng
                                            </button>
                                        </div>
                                        <div id="discountMsg" class="discount-msg"></div>
                                    </div>

                                    <%-- Dòng giảm giá (ẩn khi chưa áp dụng) --%>
                                    <div id="discountRow" class="summary-row-discount" style="display:none;">
                                        <span class="label">
                                            <span class="material-symbols-outlined">sell</span>
                                            Giảm giá
                                            <button type="button" class="btn-remove-discount" onclick="removeDiscount()" title="Bỏ mã">
                                                <span class="material-symbols-outlined">cancel</span>
                                            </button>
                                        </span>
                                        <span id="discountAmt" class="val">-0₫</span>
                                    </div>

                                    <div class="summary-divider"></div>
                                    <div class="summary-total-row">
                                        <span class="summary-total-label">Tổng cộng</span>
                                        <span class="summary-total-val" id="finalTotal"><fmt:formatNumber value="${totalAmount}" pattern="#,###"/>₫</span>
                                    </div>

                                    <%-- Field ẩn truyền discountCode khi submit --%>
                                    <input type="hidden" name="discountCode" id="discountCodeHidden" value="">

                                    <button type="button" class="btn-checkout" onclick="goToCheckout()">
                                        <span class="material-symbols-outlined">local_shipping</span>
                                        Đặt Hàng Ngay
                                    </button>
                                </div>
                            </div>

                            <div class="cart-page-panel cart-note-panel">
                                <div class="panel-label">Thông tin</div>
                                <div class="panel-title panel-title-sm">Ghi chú đơn hàng</div>
                                <textarea id="cartNoteInput" name="cartNote" rows="3" class="note-textarea" placeholder="Ghi chú thêm (tuỳ chọn)..."></textarea>
                            </div>
                        </div>
                    </div>
                </form>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>

/* ── DateTime ── */
function updateClock() {
    const el = document.getElementById('currentDateTime');
    if (el) {
        el.textContent = new Date().toLocaleString('vi-VN', { weekday:'short', year:'numeric', month:'2-digit', day:'2-digit', hour:'2-digit', minute:'2-digit' });
    }
}
updateClock(); setInterval(updateClock, 30000);

/* ── Discount Code ── */
const BASE_TOTAL = ${totalAmount};
let appliedDiscount = 0;

function fmtVND(n) {
    return new Intl.NumberFormat('vi-VN').format(Math.round(n)) + '₫';
}

function updateTotalDisplay(discountAmt) {
    const finalVal = Math.max(BASE_TOTAL - discountAmt, 0);
    document.getElementById('finalTotal').textContent = fmtVND(finalVal);
    document.getElementById('discountAmt').textContent = '-' + fmtVND(discountAmt);
    document.getElementById('discountRow').style.display = discountAmt > 0 ? 'flex' : 'none';
}

function applyDiscount() {
    const code  = document.getElementById('discountInput').value.trim();
    const msgEl = document.getElementById('discountMsg');
    const btn   = document.getElementById('applyDiscountBtn');

    if (!code) {
        msgEl.className = 'discount-msg error';
        msgEl.textContent = 'Vui lòng nhập mã giảm giá.';
        return;
    }

    btn.disabled = true;
    btn.textContent = '...';
    msgEl.className = 'discount-msg';
    msgEl.textContent = '';

    fetch('${pageContext.request.contextPath}/CheckDiscount?code=' + encodeURIComponent(code))
        .then(r => r.json())
        .then(data => {
            if (data.valid) {
                let discountAmt = 0;
                if (data.amount && data.amount > 0) {
                    discountAmt = Math.min(data.amount, BASE_TOTAL);
                } else if (data.pct && data.pct > 0) {
                    discountAmt = Math.round(BASE_TOTAL * data.pct / 100);
                }
                appliedDiscount = discountAmt;
                document.getElementById('discountCodeHidden').value = code;
                document.getElementById('discountInput').disabled = true;
                btn.style.display = 'none';
                updateTotalDisplay(discountAmt);
                msgEl.className = 'discount-msg success';
                const label = data.amount > 0 ? fmtVND(data.amount) : data.pct + '%';
                msgEl.textContent = ' Áp dụng thành công — giảm ' + label;
            } else {
                msgEl.className = 'discount-msg error';
                msgEl.textContent = data.msg || 'Mã không hợp lệ.';
                document.getElementById('discountCodeHidden').value = '';
            }
        })
        .catch(() => {
            msgEl.className = 'discount-msg error';
            msgEl.textContent = 'Không thể kiểm tra mã. Vui lòng thử lại.';
        })
        .finally(() => {
            btn.disabled = false;
            btn.textContent = 'Áp dụng';
        });
}

function removeDiscount() {
    appliedDiscount = 0;
    document.getElementById('discountCodeHidden').value = '';
    document.getElementById('discountInput').value = '';
    document.getElementById('discountInput').disabled = false;
    document.getElementById('applyDiscountBtn').style.display = '';
    document.getElementById('discountMsg').className = 'discount-msg';
    document.getElementById('discountMsg').textContent = '';
    updateTotalDisplay(0);
}

const discountInput = document.getElementById('discountInput');
if (discountInput) {
    discountInput.addEventListener('keydown', function(e) {
        if (e.key === 'Enter') { e.preventDefault(); applyDiscount(); }
    });
}

function goToCheckout() {
    const code      = document.getElementById('discountCodeHidden').value.trim();
    const orderNote = document.getElementById('cartNoteInput').value.trim();
    let url = '${pageContext.request.contextPath}/Cart?action=checkout-form';
    if (code)      url += '&discountCode=' + encodeURIComponent(code);
    if (orderNote) url += '&orderNote='    + encodeURIComponent(orderNote);
    window.location.href = url;
}
</script>
</body>
</html>
