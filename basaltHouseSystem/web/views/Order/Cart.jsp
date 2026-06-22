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
        <link rel="stylesheet"
              href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

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
                            <button class="btn-nav-icon cart-nav-btn" title="Giỏ hàng" onclick="window.location = '${pageContext.request.contextPath}/Cart'">
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
                            <span class="material-symbols-outlined" style="font-size:14px">shopping_cart</span>
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
                        <button class="meta-chip" style="cursor:pointer;border:none;" onclick="window.location = '${pageContext.request.contextPath}/category'">
                            <span class="material-symbols-outlined">arrow_back</span>
                            <span>Tiếp tục mua</span>
                        </button>
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
                        <div style="text-align:center;padding:60px 20px;background:#fff;border-radius:var(--radius-md);border:1px solid var(--border-subtle);max-width:500px;margin:40px auto;box-shadow:var(--shadow-sm);">
                            <span class="material-symbols-outlined" style="font-size:64px;color:#22c55e;margin-bottom:12px;">check_circle</span>
                            <h3 style="font-family:var(--font-montserrat);font-weight:700;margin-bottom:8px;color:var(--text-dark);">Đặt Hàng Thành Công!</h3>
                            <p style="font-size:14px;color:var(--text-muted);margin-bottom:20px;">Đơn hàng của bạn đã được gửi đến nhà hàng. Vui lòng chờ phục vụ.</p>
                            <div style="background:var(--secondary-bg);border-radius:var(--radius-sm);padding:12px;font-weight:700;color:var(--primary-color);font-family:var(--font-montserrat);margin-bottom:24px;font-size:16px;">
                                Mã đơn hàng: <c:out value="${param.code}"/>
                            </div>
                            <a href="${pageContext.request.contextPath}/category" class="btn-checkout" style="text-decoration:none;display:inline-flex;width:auto;padding:12px 32px;justify-content:center;align-items:center;margin:0 auto;">
                                <span class="material-symbols-outlined">storefront</span>Quay lại Thực đơn
                            </a>
                        </div>
                    </c:when>
                    <c:when test="${empty cartItems}">
                        <!-- Empty State -->
                        <div style="text-align:center;padding:80px 20px;background:#fff;border-radius:var(--radius-md);border:1px solid var(--border-subtle);max-width:600px;margin:40px auto;box-shadow:var(--shadow-sm);">
                            <span class="material-symbols-outlined" style="font-size:64px;display:block;margin-bottom:14px;color:#d1d5db;">shopping_cart</span>
                            <h3 style="font-family:var(--font-montserrat);font-weight:700;margin-bottom:8px;color:var(--text-dark);">Giỏ hàng trống</h3>
                            <p style="font-size:14px;color:var(--text-muted);margin-bottom:24px;">Bạn chưa thêm sản phẩm nào vào giỏ hàng.</p>
                            <a class="btn-checkout" style="width:auto;padding:12px 28px;text-decoration:none;display:inline-flex;justify-content:center;align-items:center;margin:0 auto;" href="${pageContext.request.contextPath}/category">
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
                                                                    <a href="${pageContext.request.contextPath}/Cart?action=update&productId=${item.productId}&delta=-1" class="ct-qty-btn" style="text-decoration:none;">−</a>
                                                                    <span class="ct-qty-num">${item.quantity}</span>
                                                                    <a href="${pageContext.request.contextPath}/Cart?action=update&productId=${item.productId}&delta=1" class="ct-qty-btn" style="text-decoration:none;">+</a>
                                                                </div>
                                                            </td>
                                                            <td style="text-align:right"><span class="ct-price"><fmt:formatNumber value="${item.price}" pattern="#,###"/>₫</span></td>
                                                            <td style="text-align:right"><span class="ct-subtotal"><fmt:formatNumber value="${item.subtotal}" pattern="#,###"/>₫</span></td>
                                                            <td>
                                                                <a href="${pageContext.request.contextPath}/Cart?action=remove&productId=${item.productId}" class="ct-remove" title="Xóa sản phẩm" style="text-decoration:none;">
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
                                               style="display:inline-flex;align-items:center;gap:6px;padding:9px 20px;
                                               border:1.5px solid #ef4444;color:#ef4444;
                                               border-radius:8px;font-size:13px;font-weight:600;
                                               text-decoration:none;background:#fff;
                                               transition:background .2s,color .2s;"
                                               onmouseover="this.style.background = '#ef4444';this.style.color = '#fff';"
                                               onmouseout="this.style.background = '#fff';this.style.color = '#ef4444';">
                                                <span class="material-symbols-outlined" style="font-size:16px">delete_sweep</span>
                                                Xóa tất cả
                                            </a>
                                            <a href="${pageContext.request.contextPath}/category"
                                               style="display:inline-flex;align-items:center;gap:6px;padding:9px 20px;
                                               border:1.5px solid var(--primary-color);color:var(--primary-color);
                                               border-radius:8px;font-size:13px;font-weight:600;
                                               text-decoration:none;background:#fff;
                                               transition:background .2s,color .2s;"
                                               onmouseover="this.style.background = 'var(--primary-color)';this.style.color = '#fff';"
                                               onmouseout="this.style.background = '#fff';this.style.color = 'var(--primary-color)';">
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
                                                    <span class="summary-row-label"><c:out value="${item.productName}"/> × ${item.quantity}</span>
                                                    <span class="summary-row-val"><fmt:formatNumber value="${item.subtotal}" pattern="#,###"/>₫</span>
                                                </div>
                                            </c:forEach>
                                            <div class="summary-divider"></div>
                                            <div class="summary-total-row">
                                                <span class="summary-total-label">Tổng cộng</span>
                                                <span class="summary-total-val"><fmt:formatNumber value="${totalAmount}" pattern="#,###"/>₫</span>
                                            </div>
                                        </div>
                                        <%-- Nút đặt hàng nằm trong summary-card để giữ layout cũ --%>
                                        <div style="padding: 16px;">
                                            <button type="submit" class="btn-checkout">
                                                <span class="material-symbols-outlined">shopping_bag</span>
                                                Đặt Hàng Ngay
                                            </button>
                                        </div>
                                    </div>

                                    <%-- Panel 2: Phương thức thanh toán — TÁCH RIÊNG khỏi summary-card --%>
                                
                                    <div class="cart-page-panel payment-section">
                                        <div class="panel-label">Thanh toán</div>
                                        <div class="panel-title" style="font-size:15px;margin-bottom:14px;">Phương thức thanh toán</div>

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

                                    <%-- Panel 3: Ghi chú (giữ nguyên) --%>
                                    <div class="cart-page-panel" style="margin-top:16px;">
                                        <div class="panel-label">Thông tin</div>
                                        <div class="panel-title" style="font-size:16px;margin-bottom:16px;">Ghi chú đơn hàng</div>
                                        <textarea name="note" rows="3" placeholder="Ghi chú thêm (tuỳ chọn)..."
                                                  style="width:100%;border:1.5px solid var(--border-subtle);border-radius:var(--radius-sm);
                                                  padding:10px 12px;font-size:13px;font-family:var(--font-inter);
                                                  resize:none;outline:none;color:var(--text-dark);background:var(--bg-light);"></textarea>
                                    </div>

                                </div><%-- /col-lg-4 --%>
                            </div><%-- /row --%>
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
                    el.textContent = new Date().toLocaleString('vi-VN', {
                        weekday: 'short', year: 'numeric', month: '2-digit',
                        day: '2-digit', hour: '2-digit', minute: '2-digit'
                    });
                }
            }
            updateClock();
            setInterval(updateClock, 30000);
        </script>
    </body>
</html>
