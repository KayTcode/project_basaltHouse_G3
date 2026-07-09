<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%
    request.setAttribute("pageTitle", "BasaltHouse - Good Coffee, Good Mood");
%>
<jsp:include page="/views/HomePage/Header.jsp" />
<link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/HomePageCss/HomePage.css?v=20260709-1">
<link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/Customer/CustomerTheme.css?v=20260709-1">
<main>
    <!-- Hero Section -->
    <section class="hero-section">
        <div class="hero-pattern"></div>
        <div class="container position-relative">
            <div class="row align-items-center">
                <div class="col-lg-6 mb-5 mb-lg-0">
                    <h1 class="hero-title">
                        <span>Good Coffee</span><br>Good Mood
                    </h1>
                    <p class="hero-subtitle">
                        100% hạt cà phê nguyên chất. Pha chế mỗi ngày, giao hàng tận nơi cho niềm vui trọn vẹn.
                    </p>
                    <div class="d-sm-flex gap-3 mb-5">
                        <a class="btn-coffeely-primary mb-3 mb-sm-0 w-100 w-sm-auto" href="url" target="target">Đặt Ngay</a>
                        <a class="btn-coffeely-secondary w-100 w-sm-auto" href="category" target="target">Xem Menu</a>
                    </div>

                    <!-- Feature Badges -->
                    <div class="row g-3">
                        <div class="col-6 col-md-3">
                            <div class="feature-badge">
                                <div class="feature-icon-wrapper">
                                    <span class="material-symbols-outlined">local_shipping</span>
                                </div>
                                <span class="feature-text">Giao nhanh 30'</span>
                            </div>
                        </div>
                        <div class="col-6 col-md-3">
                            <div class="feature-badge">
                                <div class="feature-icon-wrapper">
                                    <span class="material-symbols-outlined">eco</span>
                                </div>
                                <span class="feature-text">100% Tự nhiên</span>
                            </div>
                        </div>
                        <div class="col-6 col-md-3">
                            <div class="feature-badge">
                                <div class="feature-icon-wrapper">
                                    <span class="material-symbols-outlined">assignment_return</span>
                                </div>
                                <span class="feature-text">Đổi trả dễ dàng</span>
                            </div>
                        </div>
                        <div class="col-6 col-md-3">
                            <div class="feature-badge">
                                <div class="feature-icon-wrapper">
                                    <span class="material-symbols-outlined">workspace_premium</span>
                                </div>
                                <span class="feature-text">Tích điểm</span>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-lg-6">
                    <div class="hero-img-wrapper text-center">
                        <img src="https://lh3.googleusercontent.com/aida-public/AB6AXuCHvKrOcXMbNmK34jL7gZ3EVtJ2KZigFRnn7O6xqeo8z_Eel3d-E7fEYSA9BmBbf4j-GHWESgdjfW2RTgN7yG1-SgjRwiZKst4yokTdB7w7VyuYaS5KcO8RHoeGYK-KnMzkuDR6ZUgliQeH_d1BPASXBpcQKHLSZ2GdRabH_U6xeobW4djCt6UgOaqYoKrx6QbpbLh-DPcyunDA78KmRC-OlnJVOYFbqVu0ez_3zsQPx1v6g_SFFuZ6uvIOPOOiGsdlsQU3KC5NnNE" alt="Premium Coffee Coffee" class="hero-img img-fluid">
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Category Section -->
    <section class="category-section">
        <div class="bg-dot-pattern"></div>
        <div class="container position-relative">
            <div class="section-header d-flex justify-content-between align-items-end">
                <div>
                    <h2 class="section-title">
                        <span class="material-symbols-outlined section-title-dot">fiber_manual_record</span>Danh mục nổi bật
                    </h2>
                    <div class="section-line"></div>
                </div>
                <a href="${pageContext.request.contextPath}/category" class="btn-see-all">
                    Xem tất cả <span class="material-symbols-outlined">arrow_forward</span>
                </a>
            </div>

            <div class="row g-4 row-cols-2 row-cols-md-5 justify-content-center">
                <!-- Category 1 -->
                <c:forEach  var="c" items="${ListP}">
                    <c:set var="categoryImage" value="${fn:trim(c.image)}" />
                    <c:set var="placeholderImage" value="${pageContext.request.contextPath}/assets/img/product-placeholder.svg" />


                    <div class="col">
                        <a href="${pageContext.request.contextPath}/category?category=${c.categoryId}" class="category-card">
                            <div class="category-img-wrapper">
                                <c:choose>
                                    <c:when test="${empty categoryImage}">
                                        <img src="${placeholderImage}" alt="${c.categoryName}" class="category-img">
                                    </c:when>
                                    <c:when test="${fn:startsWith(categoryImage, 'http://') or fn:startsWith(categoryImage, 'https://')}">
                                        <img src="${categoryImage}" alt="${c.categoryName}" class="category-img" onerror="this.onerror=null;this.src='${placeholderImage}'">
                                    </c:when>
                                    <c:when test="${fn:startsWith(categoryImage, '/')}">
                                        <img src="${pageContext.request.contextPath}${categoryImage}" alt="${c.categoryName}" class="category-img" onerror="this.onerror=null;this.src='${placeholderImage}'">
                                    </c:when>
                                    <c:otherwise>
                                        <img src="${pageContext.request.contextPath}/${categoryImage}" alt="${c.categoryName}" class="category-img" onerror="this.onerror=null;this.src='${placeholderImage}'">
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <span class="category-name">${c.categoryName}</span>
                        </a>
                    </div>
                </c:forEach>

            </div>
        </div>
    </section>

    <!-- Best Sellers Section -->
    <section class="products-section">
        <div class="container">
            <div class="section-header d-flex justify-content-between align-items-end">
                <div>
                    <h2 class="section-title">
                        <span class="material-symbols-outlined section-title-dot">fiber_manual_record</span>Sản phẩm bán chạy
                    </h2>
                    <div class="section-line"></div>
                </div>
                <a href="${pageContext.request.contextPath}/category" class="btn-see-all">
                    Xem tất cả <span class="material-symbols-outlined">arrow_forward</span>
                </a>
            </div>

            <div class="row g-4 justify-content-center">
                <c:forEach var="p" items="${featuredProducts}" varStatus="status">
                    <c:set var="productImage" value="${fn:trim(p.imageUrl)}" />
                    <c:set var="placeholderImage" value="${pageContext.request.contextPath}/assets/img/product-placeholder.svg" />
                    <c:set var="productUrl" value="${pageContext.request.contextPath}/productdetail?id=${p.productId}" />

                    <div class="product-col col-sm-6 col-md-4 col-lg-3 d-flex align-items-stretch">
                        <div class="product-card w-100">
                            <c:if test="${status.index == 0}">
                                <div class="product-badge-group">
                                    <span class="product-badge-best">Bán chạy</span>
                                </div>
                            </c:if>
                            <c:if test="${status.index == 1}">
                                <div class="product-badge-group">
                                    <span class="product-badge-hot">Hot</span>
                                </div>
                            </c:if>
                            <div class="product-rating">
                                <span class="material-symbols-outlined">star</span>
                                <c:choose>
                                    <c:when test="${p.reviewCount > 0}">
                                        <fmt:formatNumber value="${p.averageRating}" minFractionDigits="1" maxFractionDigits="1" />
                                    </c:when>
                                    <c:otherwise>0.0</c:otherwise>
                                </c:choose>
                            </div>
                            <a href="${productUrl}" class="product-card-link">
                                <div class="product-img-wrapper">
                                    <c:choose>
                                        <c:when test="${empty productImage}">
                                            <img src="${placeholderImage}" alt="${p.productName}" class="product-img">
                                        </c:when>
                                        <c:when test="${fn:startsWith(productImage, 'http://') or fn:startsWith(productImage, 'https://')}">
                                            <img src="${productImage}" alt="${p.productName}" class="product-img" onerror="this.onerror=null;this.src='${placeholderImage}'">
                                        </c:when>
                                        <c:when test="${fn:startsWith(productImage, '/')}">
                                            <img src="${pageContext.request.contextPath}${productImage}" alt="${p.productName}" class="product-img" onerror="this.onerror=null;this.src='${placeholderImage}'">
                                        </c:when>
                                        <c:otherwise>
                                            <img src="${pageContext.request.contextPath}/${productImage}" alt="${p.productName}" class="product-img" onerror="this.onerror=null;this.src='${placeholderImage}'">
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </a>
                            <div class="product-body">
                                <a href="${productUrl}" class="product-title-link">
                                    <h3 class="product-title text-truncate">${p.productName}</h3>
                                </a>
                                <div class="product-footer">
                                    <span class="product-price">${p.price} đ</span>
                                    <c:choose>
                                        <c:when test="${not empty sessionScope.currentUser}">
                                            <button class="btn-add-cart" onclick="addToCart('${p.productName}', ${p.price})" title="Thêm vào giỏ hàng">
                                                <span class="material-symbols-outlined">add</span>
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <button class="btn-add-cart" onclick="requireLogin()" title="Bạn cần đăng nhập để thêm sản phẩm">
                                                <span class="material-symbols-outlined">add</span>
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>

            </div>
        </div>
    </section>

    <!-- Promotional Banner Grid -->
    <section class="promo-section">
        <div class="container">
            <div class="promo-banner shadow-lg">
                <img src="https://lh3.googleusercontent.com/aida-public/AB6AXuDUl9N329Y-es6rDobOip67H-IqnPHwWCYvOvHO2j_ASMcxct1R8iVMBZDEaLofT8IztPVXflHQ-HSDup2abEK6qB8Sr3q27tFVgSgjwt7L5kFLwGYfj8NEcFDDPt16vEluJhbYFlNla-skGXNNatQQHfxqk1QDJOwX1kHDNydcbcmgPhub_p_Gl39QAuMtOk6x9oc390UoHVROHlwj-rQgQiiEb-S4NRwpIqsGww1v_nQPZCf6KnpxkP5lf4xFovD7gZLIfCAMmdQ" alt="Banner background" class="promo-bg-img">
                <div class="promo-content">
                    <div class="row align-items-center g-4 justify-content-between">
                        <div class="col-md-7 text-center text-md-start">
                            <h2 class="promo-title">Ưu đãi hôm nay</h2>
                            <c:forEach var="c" items="${Listd}">
                                
                            
                                <p class="promo-desc text-white-50" style="font-size: 30px">${c.description} </p>
                        </div>
                        <div class="col-md-5">
                            <div class="d-flex align-items-center justify-content-center justify-content-md-end gap-3 flex-wrap">
                                <div class="promo-code-container">
                                    <div class="promo-code-label">Mã giảm giá</div>
                                    <div class="promo-code-value">${c.code}</div>
                                </div>
                                </c:forEach>
                                <a class="btn-use-code" href="${pageContext.request.contextPath}/category">Sử dụng ngay</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
</main>

<jsp:include page="/views/HomePage/Footer.jsp" />
