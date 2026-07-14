<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%
    request.setAttribute("pageTitle", "Ưu đãi - BasaltHouse");
    request.setAttribute("pageStylesheet", "/css/Benefit/Benefit.css?v=20260709-1");
%>

<jsp:include page="/views/HomePage/Header.jsp"/>

<main class="benefit-page">
    <section class="benefit-hero">
        <div class="container">
            <div class="benefit-hero-inner">
                <div class="benefit-hero-copy">
                    <div class="benefit-kicker">
                        <span class="material-symbols-outlined">local_offer</span>
                        BasaltHouse Offers
                    </div>
                    <h1>Ưu đãi dành riêng cho khách hàng BasaltHouse</h1>
                    <p>
                        Khám phá voucher, quà tặng thành viên và các đặc quyền giúp mỗi lần
                        thưởng thức cà phê trở nên tiết kiệm hơn.
                    </p>
                    <div class="benefit-hero-actions">
                        <a href="${pageContext.request.contextPath}/voucher" class="benefit-btn benefit-btn--primary">
                            <span class="material-symbols-outlined">redeem</span>
                            Xem voucher
                        </a>
                        <a href="${pageContext.request.contextPath}/membership" class="benefit-btn benefit-btn--secondary">
                            <span class="material-symbols-outlined">workspace_premium</span>
                            Hạng thành viên
                        </a>
                    </div>
                </div>

                <aside class="benefit-hero-card">
                    <span class="material-symbols-outlined">confirmation_number</span>
                    <small>Deal nổi bật</small>
                    <c:if test="${not empty publicVouchers}">
                        <c:set var="featuredVoucher" value="${publicVouchers[0]}"/>
                    </c:if>
                    <c:choose>
                        <c:when test="${not empty featuredVoucher}">
                            <strong>Giảm ${featuredVoucher.discountValueFormatted}</strong>
                            <p>
                                <c:choose>
                                    <c:when test="${not empty featuredVoucher.description}">
                                        <c:out value="${featuredVoucher.description}"/>
                                    </c:when>
                                    <c:otherwise>
                                        Áp dụng theo điều kiện của voucher đang khả dụng.
                                    </c:otherwise>
                                </c:choose>
                            </p>
                            <div class="benefit-hero-code">
                                <span>Mã voucher</span>
                                <b><c:out value="${featuredVoucher.code}"/></b>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <strong>Ưu đãi đang cập nhật</strong>
                            <p>BasaltHouse sẽ hiển thị deal nổi bật khi có voucher public khả dụng.</p>
                        </c:otherwise>
                    </c:choose>
                </aside>
            </div>
        </div>
    </section>

    <section class="benefit-highlight-section">
        <div class="container">
            <div class="benefit-highlight-grid">
                <article class="benefit-highlight-card">
                    <span class="material-symbols-outlined">sell</span>
                    <small>Voucher</small>
                    <strong>Mã giảm giá</strong>
                </article>
                <article class="benefit-highlight-card">
                    <span class="material-symbols-outlined">cake</span>
                    <small>Birthday</small>
                    <strong>Quà sinh nhật</strong>
                </article>
                <article class="benefit-highlight-card">
                    <span class="material-symbols-outlined">trending_up</span>
                    <small>Membership</small>
                    <strong>Tích lũy nâng hạng</strong>
                </article>
                <article class="benefit-highlight-card">
                    <span class="material-symbols-outlined">local_shipping</span>
                    <small>Order</small>
                    <strong>Đặt món tiện lợi</strong>
                </article>
            </div>
        </div>
    </section>

    <section class="benefit-offer-section">
        <div class="container">
            <div class="benefit-section-head">
                <div>
                    <p>Ưu đãi hiện có</p>
                    <h2>Chọn quyền lợi phù hợp với bạn</h2>
                </div>
                <span class="benefit-section-icon material-symbols-outlined">verified</span>
            </div>

            <div class="benefit-offer-grid">
                <article class="benefit-offer-card benefit-offer-card--featured">
                    <div class="benefit-offer-top">
                        <span class="benefit-offer-icon material-symbols-outlined">percent</span>
                        <span class="benefit-offer-tag">Phổ biến</span>
                    </div>
                    <h3>Voucher giảm giá</h3>
                    <p>Dùng mã khuyến mãi đang khả dụng để giảm trực tiếp trên đơn hàng.</p>
                    <ul>
                        <li>Áp dụng cho đơn đủ điều kiện</li>
                        <li>Kiểm tra nhanh trong trang voucher</li>
                        <li>Có thể thay đổi theo từng chiến dịch</li>
                    </ul>
                    <a href="${pageContext.request.contextPath}/voucher">
                        Lấy mã ngay
                        <span class="material-symbols-outlined">arrow_forward</span>
                    </a>
                </article>

                <article class="benefit-offer-card">
                    <div class="benefit-offer-top">
                        <span class="benefit-offer-icon material-symbols-outlined">workspace_premium</span>
                        <span class="benefit-offer-tag">Member</span>
                    </div>
                    <h3>Ưu đãi theo hạng</h3>
                    <p>Tích lũy chi tiêu để nâng hạng và nhận mức giảm tốt hơn ở các mốc thành viên.</p>
                    <ul>
                        <li>Theo dõi tổng chi tiêu</li>
                        <li>Xem hạng hiện tại</li>
                        <li>Mở khóa quyền lợi mới</li>
                    </ul>
                    <a href="${pageContext.request.contextPath}/membership">
                        Xem hạng của tôi
                        <span class="material-symbols-outlined">arrow_forward</span>
                    </a>
                </article>

                <article class="benefit-offer-card">
                    <div class="benefit-offer-top">
                        <span class="benefit-offer-icon material-symbols-outlined">celebration</span>
                        <span class="benefit-offer-tag">Special</span>
                    </div>
                    <h3>Quà dịp đặc biệt</h3>
                    <p>Khách hàng thân thiết có cơ hội nhận ưu đãi sinh nhật hoặc quà tặng theo sự kiện.</p>
                    <ul>
                        <li>Ưu đãi theo tài khoản</li>
                        <li>Nhắc dùng trước khi hết hạn</li>
                        <li>Cập nhật theo từng mùa</li>
                    </ul>
                    <a href="${pageContext.request.contextPath}/category">
                        Chọn món yêu thích
                        <span class="material-symbols-outlined">arrow_forward</span>
                    </a>
                </article>
            </div>
        </div>
    </section>

    <section class="benefit-guide-section">
        <div class="container">
            <div class="benefit-guide-layout">
                <section class="benefit-guide-panel">
                    <div class="benefit-panel-head">
                        <div>
                            <p>Cách nhận ưu đãi</p>
                            <h2>Chỉ cần vài bước đơn giản</h2>
                        </div>
                        <span class="benefit-panel-icon material-symbols-outlined">route</span>
                    </div>

                    <div class="benefit-step-list">
                        <article class="benefit-step-item">
                            <span>1</span>
                            <div>
                                <h3>Đăng nhập tài khoản</h3>
                                <p>Đăng nhập để hệ thống nhận diện voucher riêng và tiến độ thành viên.</p>
                            </div>
                        </article>
                        <article class="benefit-step-item">
                            <span>2</span>
                            <div>
                                <h3>Chọn voucher phù hợp</h3>
                                <p>Kiểm tra điều kiện, hạn dùng và chọn mã tốt nhất cho đơn hàng.</p>
                            </div>
                        </article>
                        <article class="benefit-step-item">
                            <span>3</span>
                            <div>
                                <h3>Đặt món và thanh toán</h3>
                                <p>Hoàn tất đơn hàng để sử dụng ưu đãi và tiếp tục tích lũy chi tiêu.</p>
                            </div>
                        </article>
                    </div>
                </section>

                <aside class="benefit-rule-panel">
                    <span class="material-symbols-outlined">info</span>
                    <h2>Lưu ý khi dùng ưu đãi</h2>
                    <ul>
                        <li>Mỗi đơn hàng có thể chỉ áp dụng một mã giảm giá.</li>
                        <li>Mã ưu đãi cần còn hạn và đúng điều kiện chương trình.</li>
                        <li>Ưu đãi không quy đổi thành tiền mặt.</li>
                        <li>Quyền lợi thành viên được cập nhật theo dữ liệu đơn hàng.</li>
                    </ul>
                </aside>
            </div>
        </div>
    </section>

    <section class="benefit-cta-section">
        <div class="container">
            <div class="benefit-cta">
                <div>
                    <p>BasaltHouse Rewards</p>
                    <h2>Sẵn sàng dùng ưu đãi hôm nay?</h2>
                    <span>Chọn voucher, mở menu và bắt đầu đơn hàng tiếp theo của bạn.</span>
                </div>
                <div class="benefit-cta-actions">
                    <a href="${pageContext.request.contextPath}/voucher" class="benefit-btn benefit-btn--light">
                        <span class="material-symbols-outlined">redeem</span>
                        Voucher
                    </a>
                    <a href="${pageContext.request.contextPath}/category" class="benefit-btn benefit-btn--outline">
                        <span class="material-symbols-outlined">shopping_cart_checkout</span>
                        Đặt món
                    </a>
                </div>
            </div>
        </div>
    </section>
</main>

<jsp:include page="/views/HomePage/Footer.jsp"/>
