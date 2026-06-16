<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%
    request.setAttribute("pageTitle", "Danh mục sản phẩm - BasaltHouse");
%>
<jsp:include page="/views/HomePage/Header.jsp" />
<link href="${pageContext.request.contextPath}/css/Categoty/Categorie.css" rel="stylesheet">

<main class="category-page-main">
    <section class="category-hero">
        <div class="container">
            <div class="category-hero-inner">
                <div class="category-hero-copy">
                    <div class="category-hero-badge">
                        <span class="material-symbols-outlined">storefront</span>
                        BasaltHouse Menu
                    </div>
                    <h1>Danh mục sản phẩm</h1>
                    <p>
                        Chọn danh mục yêu thích và khám phá nhanh các món đồ uống, bánh ngọt
                        cùng những lựa chọn phù hợp cho từng khoảnh khắc.
                    </p>
                </div>

                <div class="category-hero-stats" aria-label="Thống kê danh mục">
                    <div class="category-stat-card">
                        <span class="material-symbols-outlined">category</span>
                        <strong>${fn:length(ListC)}</strong>
                        <small>Danh mục</small>
                    </div>
                    <div class="category-stat-card">
                        <span class="material-symbols-outlined">local_cafe</span>
                        <strong>${productCount}</strong>
                        <small>Sản phẩm đang xem</small>
                    </div>
                    <div class="category-hero-promise">
                        <span class="material-symbols-outlined">verified</span>
                        <div>
                            <strong>Chuẩn vị BasaltHouse</strong>
                            <small>Hình ảnh rõ, giá minh bạch, dễ chọn món</small>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <section class="category-page-section">
        <div class="container">
            <div class="category-layout">
                <aside class="category-sidebar category-sidebar--page" aria-label="Danh mục sản phẩm">
                    <div class="category-sidebar-head">
                        <span class="material-symbols-outlined">menu_book</span>
                        <div>
                            <p>Khám phá menu</p>
                            <strong>Chọn danh mục</strong>
                        </div>
                    </div>

                    <nav class="category-list">
                        <c:forEach var="c" items="${ListC}">
                            <a href="${pageContext.request.contextPath}/category?category=${c.categoryId}"
                               class="category-item ${currentCategory == c.categoryId ? 'active' : ''}">
                                <span class="category-item-icon">
                                    <span class="material-symbols-outlined">local_cafe</span>
                                </span>
                                <span class="category-item-name"><c:out value="${c.categoryName}"/></span>
                            </a>
                        </c:forEach>
                    </nav>
                </aside>

                <section class="category-product-panel">
                    <div class="category-panel-header">
                        <div>
                            <p class="category-panel-kicker">Danh mục đang xem</p>
                            <h2><c:out value="${categoryTitle}"/></h2>
                        </div>
                        <span class="category-count-chip">
                            <span class="material-symbols-outlined">inventory_2</span>
                            ${productCount} sản phẩm
                        </span>
                    </div>

                    <div class="category-toolbar" aria-label="Bộ lọc hiển thị">
                        <div class="category-current-filter">
                            <span class="material-symbols-outlined">tune</span>
                            <span>Đang hiển thị <strong><c:out value="${categoryTitle}"/></strong></span>
                        </div>
                        <div class="category-toolbar-chips">
                            <span><span class="material-symbols-outlined">bolt</span> Phục vụ nhanh</span>
                            <span><span class="material-symbols-outlined">eco</span> Nguyên liệu tốt</span>
                            <span><span class="material-symbols-outlined">payments</span> Giá rõ ràng</span>
                        </div>
                    </div>

                    <div class="category-product-grid">
                        <c:choose>
                            <c:when test="${empty listP}">
                                <div class="category-empty">
                                    <span class="material-symbols-outlined">search_off</span>
                                    <h3>Chưa có sản phẩm</h3>
                                    <p>Danh mục này hiện chưa có món nào. Hãy thử chọn một danh mục khác trong menu.</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="p" items="${listP}">
                                    <c:set var="imageUrl" value="${p.imageUrl}" />
                                    <c:set var="placeholderImage" value="${pageContext.request.contextPath}/assets/img/product-placeholder.svg" />

                                    <a href="${pageContext.request.contextPath}/productdetail?id=${p.productId}"
                                       class="category-product-card-link">
                                        <article class="category-product-card">
                                            <div class="category-product-image">
                                                <span class="category-image-tag">
                                                    <span class="material-symbols-outlined">workspace_premium</span>
                                                    Chọn lọc
                                                </span>
                                                <c:choose>
                                                    <c:when test="${empty imageUrl}">
                                                        <img src="${placeholderImage}" alt="${p.productName}">
                                                    </c:when>
                                                    <c:when test="${fn:startsWith(imageUrl, 'http://') or fn:startsWith(imageUrl, 'https://')}">
                                                        <img src="${imageUrl}" alt="${p.productName}" onerror="this.onerror=null;this.src='${placeholderImage}'">
                                                    </c:when>
                                                    <c:when test="${fn:startsWith(imageUrl, '/')}">
                                                        <img src="${pageContext.request.contextPath}${imageUrl}" alt="${p.productName}" onerror="this.onerror=null;this.src='${placeholderImage}'">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="${pageContext.request.contextPath}/${imageUrl}" alt="${p.productName}" onerror="this.onerror=null;this.src='${placeholderImage}'">
                                                    </c:otherwise>
                                                </c:choose>
                                                <span class="category-image-cta">
                                                    Xem chi tiết
                                                    <span class="material-symbols-outlined">arrow_forward</span>
                                                </span>
                                            </div>

                                            <div class="category-product-info">
                                                <span class="category-product-badge">Sẵn sàng phục vụ</span>
                                                <h3><c:out value="${p.productName}"/></h3>
                                                <div class="category-product-meta">
                                                    <span>
                                                        <span class="material-symbols-outlined">schedule</span>
                                                        Pha chế nhanh
                                                    </span>
                                                    <span>
                                                        <span class="material-symbols-outlined">favorite</span>
                                                        Được yêu thích
                                                    </span>
                                                </div>
                                                <div class="category-product-footer">
                                                    <p><fmt:formatNumber value="${p.price}" pattern="#,###"/> đ</p>
                                                    <span class="material-symbols-outlined">arrow_forward</span>
                                                </div>
                                            </div>
                                        </article>
                                    </a>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </section>

                <aside class="category-info-rail" aria-label="Gợi ý trải nghiệm">
                    <div class="category-rail-card category-rail-card--featured">
                        <span class="material-symbols-outlined">local_fire_department</span>
                        <p>Gợi ý hôm nay</p>
                        <strong><c:out value="${categoryTitle}"/></strong>
                        <small>Danh mục này đang có ${productCount} lựa chọn để bạn khám phá.</small>
                    </div>

                    <div class="category-rail-card">
                        <h3>Điểm nổi bật</h3>
                        <ul class="category-rail-list">
                            <li>
                                <span class="material-symbols-outlined">photo_camera</span>
                                <div>
                                    <strong>Hình ảnh rõ ràng</strong>
                                    <small>Dễ xem trực quan từng món</small>
                                </div>
                            </li>
                            <li>
                                <span class="material-symbols-outlined">sell</span>
                                <div>
                                    <strong>Giá hiển thị gọn</strong>
                                    <small>Canh chọn món nhanh hơn</small>
                                </div>
                            </li>
                            <li>
                                <span class="material-symbols-outlined">touch_app</span>
                                <div>
                                    <strong>Một chạm xem chi tiết</strong>
                                    <small>Mở nhanh trang sản phẩm</small>
                                </div>
                            </li>
                        </ul>
                    </div>
                </aside>
            </div>
        </div>
    </section>
</main>

<jsp:include page="/views/HomePage/Footer.jsp" />
