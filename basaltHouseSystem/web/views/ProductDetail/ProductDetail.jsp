<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<%
    request.setAttribute("pageTitle", "Chi tiết sản phẩm - BasaltHouse");
%>

<jsp:include page="/views/HomePage/Header.jsp"/>

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/ProductDetail/ProductDetail.css">

<main class="product-detail-page">
    <div class="container">

        <c:choose>
            <c:when test="${not empty ListP}">
                <c:set var="product" value="${ListP[0]}"/>
                <c:set var="firstCup" value="${cupsBySize[product.sizeName]}"/>

                <section class="product-detail-box">

                    <div class="product-detail-left">
                        <div class="product-image-card">
                            <img src="${product.imageUrl}"
                                 alt="${product.productName}"
                                 class="product-main-image">
                        </div>
                    </div>

                    <div class="product-detail-right">

                        <p class="brand-name">BasaltHouse Coffee</p>

                        <h1 class="product-name">
                            ${product.productName}
                        </h1>

                        <div class="product-meta">
                            <span>Tình trạng:</span>

                            <strong id="stockStatus"
                                    class="${firstCup <= 0 ? 'status-out-stock' : 'status-in-stock'}">
                                <c:choose>
                                    <c:when test="${firstCup <= 0}">
                                        Hết hàng size này
                                    </c:when>
                                    <c:otherwise>
                                        Còn hàng
                                    </c:otherwise>
                                </c:choose>
                            </strong>

                            <span class="meta-divider">|</span>

                            <span>Mã SP:</span>
                            <strong>SP${product.productId}</strong>
                        </div>

                        <div class="available-stock">
                            Số lượng còn lại:
                            <strong id="cupText">
                                ${firstCup} cốc
                            </strong>
                        </div>

                        <div class="price-box">
                            <span id="displayPrice" class="product-price">
                                <fmt:formatNumber value="${product.price}" pattern="#,###"/>đ
                            </span>
                        </div>

                        <div class="option-group">
                            <label>Kích thước:</label>

                            <div class="size-list">
                                <c:forEach items="${ListP}" var="s" varStatus="loop">
                                    <c:set var="cup" value="${cupsBySize[s.sizeName]}"/>

                                    <button type="button"
                                            class="size-btn ${loop.first ? 'active' : ''}"
                                            data-size-id="${s.sizeId}"
                                            data-size-name="${s.sizeName}"
                                            data-price="${s.price}"
                                            data-cup="${cup}">
                                        ${s.sizeName}
                                    </button>
                                </c:forEach>
                            </div>
                        </div>

                        <div class="option-group quantity-group">
                            <label>Số lượng:</label>

                            <div class="quantity-control">
                                <button type="button" id="minusBtn">−</button>
                                <input type="text" id="quantityInput" value="1" readonly>
                                <button type="button" id="plusBtn">+</button>
                            </div>
                        </div>

                        <div class="action-row">

                            <form action="${pageContext.request.contextPath}/checkout" method="post">
                                <input type="hidden" name="productId" value="${product.productId}">
                                <input type="hidden" name="price" value="${product.price}">
                                <input type="hidden" name="sizeName" value="${product.sizeName}">
                                <input type="hidden" id="buySizeId" name="sizeId" value="${product.sizeId}">
                                <input type="hidden" id="buyQuantity" name="quantity" value="1">

                                <button type="submit" class="btn-buy-now">
                                    Mua ngay
                                </button>
                            </form>

                            <form action="${pageContext.request.contextPath}/cart/add" method="post">
                                <input type="hidden" name="productId" value="${product.productId}">
                                <input type="hidden" id="cartSizeId" name="sizeId" value="${product.sizeId}">
                                <input type="hidden" id="cartQuantity" name="quantity" value="1">

                                <button type="submit" class="btn-add-cart-detail">
                                    Thêm vào giỏ
                                </button>
                            </form>

                        </div>

                        <div class="product-description-box">
                            <h3>Mô tả sản phẩm</h3>
                            <p>${product.description}</p>
                        </div>

                    </div>
                </section>
            </c:when>

            <c:otherwise>
                <div class="product-empty">
                    <h2>Không tìm thấy sản phẩm</h2>
                    <p>Sản phẩm này không tồn tại hoặc đã bị xóa.</p>
                </div>
            </c:otherwise>
        </c:choose>

    </div>
</main>

<script>
    const sizeButtons = document.querySelectorAll(".size-btn");
    const displayPrice = document.getElementById("displayPrice");
    const stockStatus = document.getElementById("stockStatus");
    const cupText = document.getElementById("cupText");

    const quantityInput = document.getElementById("quantityInput");
    const minusBtn = document.getElementById("minusBtn");
    const plusBtn = document.getElementById("plusBtn");

    const buySizeId = document.getElementById("buySizeId");
    const cartSizeId = document.getElementById("cartSizeId");
    const buyQuantity = document.getElementById("buyQuantity");
    const cartQuantity = document.getElementById("cartQuantity");

    const buyBtn = document.querySelector(".btn-buy-now");
    const cartBtn = document.querySelector(".btn-add-cart-detail");

    function updateStockBySize(btn) {
        const price = Number(btn.dataset.price);
        const sizeId = btn.dataset.sizeId;
        const cup = Number(btn.dataset.cup || 0);

        displayPrice.innerText = price.toLocaleString("vi-VN") + "đ";

        buySizeId.value = sizeId;
        cartSizeId.value = sizeId;

        cupText.innerText = cup + " cốc";

        if (cup <= 0) {
            stockStatus.innerText = "Hết hàng size này";
            stockStatus.className = "status-out-stock";

            buyBtn.disabled = true;
            cartBtn.disabled = true;
            buyBtn.innerText = "Hết hàng";
        } else {
            stockStatus.innerText = "Còn hàng";
            stockStatus.className = "status-in-stock";

            buyBtn.disabled = false;
            cartBtn.disabled = false;
            buyBtn.innerText = "Mua ngay";
        }
    }

    sizeButtons.forEach(btn => {
        btn.addEventListener("click", function () {
            sizeButtons.forEach(b => b.classList.remove("active"));
            this.classList.add("active");
            updateStockBySize(this);
        });
    });

    if (sizeButtons.length > 0) {
        updateStockBySize(sizeButtons[0]);
    }

    plusBtn?.addEventListener("click", function () {
        let qty = Number(quantityInput.value);
        qty++;

        quantityInput.value = qty;
        buyQuantity.value = qty;
        cartQuantity.value = qty;
    });

    minusBtn?.addEventListener("click", function () {
        let qty = Number(quantityInput.value);

        if (qty > 1) {
            qty--;
        }

        quantityInput.value = qty;
        buyQuantity.value = qty;
        cartQuantity.value = qty;
    });
</script>

<jsp:include page="/views/HomePage/Footer.jsp"/>