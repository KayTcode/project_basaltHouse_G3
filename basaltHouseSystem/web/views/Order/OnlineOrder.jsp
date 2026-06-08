<%-- OnlineOrder.jsp - BasaltHouse Customer Order Page --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt Hàng Online - BasaltHouse</title>
    <meta name="description" content="Đặt hàng online tại BasaltHouse - Menu thức uống và thực phẩm">
    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700;900&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <!-- Material Symbols -->
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/OrderCss/Order.css" rel="stylesheet">
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
                    <li class="nav-item"><a class="nav-link nav-link-coffeely active" href="${pageContext.request.contextPath}/Order">Thực Đơn</a></li>
                    <li class="nav-item"><a class="nav-link nav-link-coffeely" href="${pageContext.request.contextPath}/Cart">Giỏ Hàng</a></li>
                </ul>
                <div class="d-flex align-items-center gap-2">
                    <button class="btn-nav-icon cart-nav-btn" title="Giỏ hàng" onclick="goToCart()">
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
                    <span class="material-symbols-outlined" style="font-size:14px">storefront</span>
                    Đặt hàng online
                </div>
                <h1>Menu Thực Đơn</h1>
                <p>Chọn sản phẩm và thêm vào giỏ hàng để đặt đơn</p>
            </div>
            <div class="page-header-meta">
                <div class="meta-chip">
                    <span class="material-symbols-outlined">event</span>
                    <span id="currentDateTime">--</span>
                </div>
                <div class="meta-chip">
                    <span class="material-symbols-outlined">shopping_cart</span>
                    <span id="cartCountChip">0 sản phẩm</span>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- ── Main Content ── -->
<main class="order-main">
    <div class="container">
        <div class="row g-4">

            <!-- ── Left: Product Grid ── -->
            <div class="col-lg-8">
                <div class="products-panel">
                    <div class="panel-label">Thực đơn</div>
                    <div class="panel-title">Chọn Sản Phẩm</div>

                    <!-- Category Filter -->
                    <div class="category-bar" id="categoryBar">
                        <button class="cat-btn active" data-cat="all">Tất cả</button>
                        <button class="cat-btn" data-cat="Đồ uống">Đồ uống</button>
                        <button class="cat-btn" data-cat="Cà phê">Cà phê</button>
                        <button class="cat-btn" data-cat="Trà">Trà &amp; Matcha</button>
                        <button class="cat-btn" data-cat="Bánh">Bánh &amp; Snack</button>
                        <button class="cat-btn" data-cat="Combo">Combo</button>
                    </div>

                    <!-- Products Grid -->
                    <div class="products-grid" id="productsGrid">

                        <!-- Sản phẩm mẫu - thay bằng dữ liệu từ DB khi tích hợp -->
                        <div class="product-card" data-cat="Cà phê" data-id="1" data-name="Cà Phê Đen Đá" data-price="29000">
                            <div class="product-img-wrap">
                                <div class="product-img-placeholder">
                                    <span class="material-symbols-outlined">local_cafe</span>
                                </div>
                            </div>
                            <div class="product-body">
                                <div class="product-name">Cà Phê Đen Đá</div>
                                <div class="product-desc">Cà phê phin truyền thống, đậm vị, pha đá mát lạnh</div>
                                <div class="product-price">29.000₫</div>
                                <button class="btn-add-cart" onclick="addToCart(this)">
                                    <span class="material-symbols-outlined">add_shopping_cart</span>Thêm vào giỏ
                                </button>
                            </div>
                        </div>

                        <div class="product-card" data-cat="Cà phê" data-id="2" data-name="Cà Phê Sữa Đá" data-price="35000">
                            <div class="product-img-wrap">
                                <div class="product-img-placeholder">
                                    <span class="material-symbols-outlined">coffee</span>
                                </div>
                            </div>
                            <div class="product-body">
                                <div class="product-name">Cà Phê Sữa Đá</div>
                                <div class="product-desc">Cà phê phin kết hợp sữa đặc thơm ngon, béo ngậy</div>
                                <div class="product-price">35.000₫</div>
                                <button class="btn-add-cart" onclick="addToCart(this)">
                                    <span class="material-symbols-outlined">add_shopping_cart</span>Thêm vào giỏ
                                </button>
                            </div>
                        </div>

                        <div class="product-card" data-cat="Cà phê" data-id="3" data-name="Cappuccino" data-price="55000">
                            <div class="product-img-wrap">
                                <div class="product-img-placeholder">
                                    <span class="material-symbols-outlined">emoji_food_beverage</span>
                                </div>
                            </div>
                            <div class="product-body">
                                <div class="product-name">Cappuccino</div>
                                <div class="product-desc">Espresso kết hợp sữa tươi và bọt sữa mịn màng</div>
                                <div class="product-price">55.000₫</div>
                                <button class="btn-add-cart" onclick="addToCart(this)">
                                    <span class="material-symbols-outlined">add_shopping_cart</span>Thêm vào giỏ
                                </button>
                            </div>
                        </div>

                        <div class="product-card" data-cat="Trà" data-id="4" data-name="Trà Sữa Truyền Thống" data-price="45000">
                            <div class="product-img-wrap">
                                <div class="product-img-placeholder">
                                    <span class="material-symbols-outlined">local_bar</span>
                                </div>
                            </div>
                            <div class="product-body">
                                <div class="product-name">Trà Sữa Truyền Thống</div>
                                <div class="product-desc">Trà Đài Loan kết hợp sữa tươi và trân châu đen</div>
                                <div class="product-price">45.000₫</div>
                                <button class="btn-add-cart" onclick="addToCart(this)">
                                    <span class="material-symbols-outlined">add_shopping_cart</span>Thêm vào giỏ
                                </button>
                            </div>
                        </div>

                        <div class="product-card" data-cat="Trà" data-id="5" data-name="Matcha Latte" data-price="65000">
                            <div class="product-img-wrap">
                                <div class="product-img-placeholder">
                                    <span class="material-symbols-outlined">eco</span>
                                </div>
                            </div>
                            <div class="product-body">
                                <div class="product-name">Matcha Latte</div>
                                <div class="product-desc">Bột matcha Nhật thượng hạng pha với sữa tươi nguyên kem</div>
                                <div class="product-price">65.000₫</div>
                                <button class="btn-add-cart" onclick="addToCart(this)">
                                    <span class="material-symbols-outlined">add_shopping_cart</span>Thêm vào giỏ
                                </button>
                            </div>
                        </div>

                        <div class="product-card" data-cat="Đồ uống" data-id="6" data-name="Nước Chanh Tươi" data-price="30000">
                            <div class="product-img-wrap">
                                <div class="product-img-placeholder">
                                    <span class="material-symbols-outlined">water_drop</span>
                                </div>
                            </div>
                            <div class="product-body">
                                <div class="product-name">Nước Chanh Tươi</div>
                                <div class="product-desc">Chanh vắt tươi, mát lạnh, thanh mát ngày hè</div>
                                <div class="product-price">30.000₫</div>
                                <button class="btn-add-cart" onclick="addToCart(this)">
                                    <span class="material-symbols-outlined">add_shopping_cart</span>Thêm vào giỏ
                                </button>
                            </div>
                        </div>

                        <div class="product-card" data-cat="Bánh" data-id="7" data-name="Bánh Croissant" data-price="40000">
                            <div class="product-img-wrap">
                                <div class="product-img-placeholder">
                                    <span class="material-symbols-outlined">bakery_dining</span>
                                </div>
                            </div>
                            <div class="product-body">
                                <div class="product-name">Bánh Croissant</div>
                                <div class="product-desc">Bánh bơ Pháp xốp giòn lớp ngoài, mềm thơm bên trong</div>
                                <div class="product-price">40.000₫</div>
                                <button class="btn-add-cart" onclick="addToCart(this)">
                                    <span class="material-symbols-outlined">add_shopping_cart</span>Thêm vào giỏ
                                </button>
                            </div>
                        </div>

                        <div class="product-card" data-cat="Combo" data-id="8" data-name="Combo Sáng" data-price="75000">
                            <div class="product-img-wrap">
                                <div class="product-img-placeholder">
                                    <span class="material-symbols-outlined">breakfast_dining</span>
                                </div>
                            </div>
                            <div class="product-body">
                                <div class="product-name">Combo Sáng</div>
                                <div class="product-desc">1 Cà Phê Sữa + 1 Bánh Croissant - tiết kiệm 10.000₫</div>
                                <div class="product-price">75.000₫</div>
                                <button class="btn-add-cart" onclick="addToCart(this)">
                                    <span class="material-symbols-outlined">add_shopping_cart</span>Thêm vào giỏ
                                </button>
                            </div>
                        </div>

                    </div><!-- /products-grid -->
                </div><!-- /products-panel -->
            </div><!-- /col-lg-8 -->

            <!-- ── Right: Cart Sidebar ── -->
            <div class="col-lg-4">
                <div class="cart-sidebar">
                    <div class="cart-header">
                        <div class="cart-header-left">
                            <span class="material-symbols-outlined">shopping_cart</span>
                            <h3>Giỏ hàng</h3>
                        </div>
                        <span class="cart-count-badge" id="cartCountBadge">${totalQty} món</span>
                    </div>
                    <div class="cart-body">
                        <c:choose>
                            <c:when test="${empty cartItems}">
                                <!-- Empty state -->
                                <div class="cart-empty" id="cartEmpty">
                                    <span class="material-symbols-outlined">shopping_cart</span>
                                    Chưa có sản phẩm nào.<br>Hãy chọn từ menu bên trái!
                                </div>
                            </c:when>
                            <c:otherwise>
                                <!-- Tính tổng tiền phía server -->
                                <c:set var="totalAmount" value="0"/>
                                <c:forEach var="item" items="${cartItems}">
                                    <c:set var="totalAmount" value="${totalAmount + item.subtotal}"/>
                                </c:forEach>

                                <!-- Cart list -->
                                <ul class="cart-list" id="cartList">
                                    <c:forEach var="item" items="${cartItems}">
                                        <li class="cart-item">
                                            <div class="cart-item-icon"><span class="material-symbols-outlined">fastfood</span></div>
                                            <div class="cart-item-info">
                                                <div class="cart-item-name"><c:out value="${item.productName}"/></div>
                                                <div class="cart-item-price">
                                                    <fmt:formatNumber value="${item.price}" pattern="#,###"/>₫ / món
                                                </div>
                                            </div>
                                            <div class="cart-item-qty-wrap">
                                                <a href="${pageContext.request.contextPath}/Cart?action=update&productId=${item.productId}&delta=-1" class="qty-btn" style="text-decoration:none;">−</a>
                                                <span class="qty-num">${item.quantity}</span>
                                                <a href="${pageContext.request.contextPath}/Cart?action=update&productId=${item.productId}&delta=1" class="qty-btn" style="text-decoration:none;">+</a>
                                            </div>
                                            <a href="${pageContext.request.contextPath}/Cart?action=remove&productId=${item.productId}" class="btn-remove-item" title="Xóa" style="text-decoration:none;">
                                                <span class="material-symbols-outlined" style="font-size:18px">close</span>
                                            </a>
                                        </li>
                                    </c:forEach>
                                </ul>

                                <!-- Footer -->
                                <div class="cart-footer" id="cartFooter">
                                    <div class="cart-subtotal">
                                        <span class="cart-subtotal-label">Tạm tính</span>
                                        <span class="cart-subtotal-val"><fmt:formatNumber value="${totalAmount}" pattern="#,###"/>₫</span>
                                    </div>
                                    <div class="cart-total-row">
                                        <span class="cart-total-label">Tổng cộng</span>
                                        <span class="cart-total-val"><fmt:formatNumber value="${totalAmount}" pattern="#,###"/>₫</span>
                                    </div>
                                    <button class="btn-checkout" id="btnCheckout" onclick="goToCart()">
                                        <span class="material-symbols-outlined">shopping_bag</span>
                                        Xem Giỏ &amp; Đặt Hàng
                                    </button>
                                    <a href="${pageContext.request.contextPath}/Cart?action=clear" class="btn-clear-cart" style="text-decoration:none;">
                                        <span class="material-symbols-outlined" style="font-size:16px">delete_sweep</span>
                                        Xóa tất cả
                                    </a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div><!-- /col-lg-4 -->

        </div><!-- /row -->
    </div><!-- /container -->
</main>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

<script>
/* ── Add to Cart (Redirect to Servlet) ── */
function addToCart(btn) {
    const card = btn.closest('.product-card');
    const id   = card.dataset.id;
    const name = card.dataset.name;
    const price = card.dataset.price;
    
    // Đưa yêu cầu thêm giỏ hàng về servlet xử lý
    window.location.href = "${pageContext.request.contextPath}/Cart?action=add&productId=" + id + "&productName=" + encodeURIComponent(name) + "&price=" + price;
}

function goToCart() {
    window.location.href = '${pageContext.request.contextPath}/Cart';
}

/* ── Category Filter ── */
document.getElementById('categoryBar').addEventListener('click', function(e) {
    const btn = e.target.closest('.cat-btn');
    if (!btn) return;
    document.querySelectorAll('.cat-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
    const cat = btn.dataset.cat;
    document.querySelectorAll('.product-card').forEach(card => {
        card.style.display = (cat === 'all' || card.dataset.cat === cat) ? '' : 'none';
    });
});

/* ── DateTime ── */
function updateClock() {
    const now = new Date();
    document.getElementById('currentDateTime').textContent =
        now.toLocaleString('vi-VN', { weekday:'short', year:'numeric', month:'2-digit', day:'2-digit', hour:'2-digit', minute:'2-digit' });
}
updateClock(); setInterval(updateClock, 30000);

/* ── Toast ── */
function showToast(type, title, msg) {
    const stack = document.getElementById('toastStack');
    const el = document.createElement('div');
    el.className = 'ts-toast ' + type;
    el.innerHTML =
        '<span class="material-symbols-outlined ts-toast-icon">' +
        (type === 'success' ? 'check_circle' : 'error') + '</span>' +
        '<div><div class="ts-toast-title">' + title + '</div>' +
        '<div class="ts-toast-msg">' + msg + '</div></div>';
    stack.appendChild(el);
    setTimeout(() => { el.classList.add('hide'); setTimeout(() => el.remove(), 350); }, 2500);
}

/* ── Hiển thị thông báo khi có tham số addSuccess từ server ── */
document.addEventListener('DOMContentLoaded', () => {
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has('addSuccess')) {
        showToast('success', 'Thành công', 'Đã thêm sản phẩm vào giỏ hàng!');
        // Xóa tham số trên URL mà không reload trang để tránh lặp lại thông báo khi F5
        window.history.replaceState({}, document.title, window.location.pathname);
    }
});
</script>
</body>
</html>
