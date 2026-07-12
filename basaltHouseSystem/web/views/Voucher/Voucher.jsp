<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%
    request.setAttribute("pageTitle", "Mã giảm giá - BasaltHouse");
%>

<jsp:include page="/views/HomePage/Header.jsp"/>

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/Voucher/Voucher.css?v=20260709-1">
<link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/Customer/CustomerTheme.css?v=20260709-1">

<main class="voucher-page">
    <c:if test="${not empty success}">
        <div class="voucher-success-backdrop" data-voucher-success>
            <div class="voucher-success-modal" role="alertdialog" aria-modal="true">
                <div class="voucher-success-icon">
                    <span class="material-symbols-outlined">check_circle</span>
                </div>
                <div>
                    <h2>Thành công</h2>
                    <p><c:out value="${success}"/></p>
                </div>
                <button type="button" class="voucher-success-close" data-voucher-success-close aria-label="Đóng thông báo">
                    <span class="material-symbols-outlined">close</span>
                </button>
            </div>
        </div>
    </c:if>

    <section class="voucher-hero">
        <div class="container">
            <div class="voucher-hero-inner">
                <div class="voucher-hero-copy">
                    <div class="voucher-kicker">
                        <span class="material-symbols-outlined">local_offer</span>
                        BasaltHouse Voucher
                    </div>
                    <h1>Mã giảm giá của bạn</h1>
                    <p>Danh sách gồm voucher toàn hệ thống và voucher riêng của tài khoản.</p>
                </div>

                <div class="voucher-hero-card">
                    <span class="material-symbols-outlined">redeem</span>
                    <small>Đang khả dụng</small>
                    <strong>0 mã</strong>
                    <p>Dùng ngay mã phù hợp để chuyển sang menu và chọn sản phẩm.</p>
                </div>
            </div>
        </div>
    </section>

    <section class="voucher-content-section">
        <div class="container">
            <div class="voucher-layout">
                <section class="voucher-list-panel">
                    <div class="voucher-panel-head">
                        <div>
                            <p>Ưu đãi hiện có</p>
                            <h2>Chọn mã phù hợp</h2>
                        </div>
                        <span class="voucher-panel-icon material-symbols-outlined">confirmation_number</span>
                    </div>

                    <div class="voucher-filter-row" aria-label="Bộ lọc mã giảm giá">
                        <button type="button" class="voucher-filter is-active" data-filter="all" aria-pressed="true">Tất cả</button>
                        <button type="button" class="voucher-filter" data-filter="available" aria-pressed="false">Khả dụng</button>
                        <button type="button" class="voucher-filter" data-filter="expiring" aria-pressed="false">Sắp hết hạn</button>
                        <button type="button" class="voucher-filter" data-filter="expired" aria-pressed="false">Hết hạn</button>
                    </div>

                    <div class="voucher-list">
                        <c:if test="${empty publicVouchers and empty listP}">
                            <p>Chưa có voucher nào để hiển thị.</p>
                        </c:if>

                        <c:forEach var="p" items="${publicVouchers}">
                            <article class="voucher-card voucher-card--featured" data-status="${publicVoucherStatus[p.discountId]}">
                                <div class="voucher-ticket-edge" aria-hidden="true"></div>
                                <div class="voucher-card-main">
                                    <div class="voucher-card-icon">
                                        <span class="material-symbols-outlined">percent</span>
                                    </div>
                                    <div class="voucher-card-content">
                                        <div class="voucher-card-top">
                                            <span class="voucher-status ${publicVoucherStatusClass[p.discountId]}">${publicVoucherStatusText[p.discountId]}</span>
                                            <span class="voucher-expire">${p.endDateFormatted}</span>
                                        </div>
                                        <h3><c:out value="${p.description}"/></h3>
                                        <p>Áp dụng cho tất cả khách hàng BasaltHouse.</p>
                                        <div class="voucher-meta">
                                            <span>
                                                <span class="material-symbols-outlined">payments</span>
                                                Giảm ${p.discountValueFormatted}
                                            </span>
                                            <span>
                                                <span class="material-symbols-outlined">event_available</span>
                                                Còn ${p.totalDay} ngày
                                            </span>
                                        </div>
                                    </div>
                                </div>
                                <div class="voucher-code-box">
                                    <strong><c:out value="${p.code}"/></strong>
                                    <a href="${pageContext.request.contextPath}/apply-voucher?code=${p.code}" class="voucher-use-btn">
                                        <span class="material-symbols-outlined">shopping_cart_checkout</span>
                                        Dùng ngay
                                    </a>
                                </div>
                            </article>
                        </c:forEach>

                        <c:forEach var="c" items="${listP}">
                            <article class="voucher-card voucher-card--featured" data-status="${voucherStatus[c.customerDiscountId]}">
                                <div class="voucher-ticket-edge" aria-hidden="true"></div>
                                <div class="voucher-card-main">
                                    <div class="voucher-card-icon">
                                        <span class="material-symbols-outlined">percent</span>
                                    </div>
                                    <div class="voucher-card-content">
                                        <div class="voucher-card-top">
                                            <span class="voucher-status ${voucherStatusClass[c.customerDiscountId]}">${voucherStatusText[c.customerDiscountId]}</span>
                                            <span class="voucher-expire">${c.endDateFormatted}</span>
                                        </div>
                                        <h3><c:out value="${c.description}"/></h3>
                                        <p>Áp dụng cho tài khoản của bạn tại BasaltHouse.</p>
                                        <div class="voucher-meta">
                                            <span>
                                                <span class="material-symbols-outlined">payments</span>
                                                Giảm ${c.discountValueFormatted}
                                            </span>
                                            <span>
                                                <span class="material-symbols-outlined">event_available</span>
                                                Còn ${c.dayTotal} ngày
                                            </span>
                                        </div>
                                    </div>
                                </div>
                                <div class="voucher-code-box">
                                    <strong><c:out value="${c.code}"/></strong>
                                    <a href="${pageContext.request.contextPath}/apply-voucher?code=${c.code}" class="voucher-use-btn">
                                        <span class="material-symbols-outlined">shopping_cart_checkout</span>
                                        Dùng ngay
                                    </a>
                                </div>
                            </article>
                        </c:forEach>
                    </div>
                </section>

                <aside class="voucher-side-panel">
                    <div class="voucher-apply-card">
                        <div class="voucher-side-head">
                            <span class="material-symbols-outlined">sell</span>
                            <div>
                                <p>Nhập mã</p>
                                <h2>Kiểm tra ưu đãi</h2>
                            </div>
                        </div>
                        <form class="voucher-apply-form" action="${pageContext.request.contextPath}/voucher" method="post">
                            <label for="voucherCodeInput">Mã giảm giá</label>
                            <div class="voucher-apply-row">
                                <input id="voucherCodeInput" type="text" name="voucherCode" placeholder="Nhập mã voucher">
                                <button type="submit">Áp dụng</button>
                            </div>
                            <c:if test="${not empty error}">
                                <div class="voucher-form-error" role="alert">
                                    <span class="material-symbols-outlined">error</span>
                                    <c:out value="${error}"/>
                                </div>
                            </c:if>
                        </form>
                    </div>

                    <div class="voucher-rule-card">
                        <h3>Điều kiện sử dụng</h3>
                        <ul>
                            <li>Mỗi đơn hàng chỉ áp dụng một mã giảm giá.</li>
                            <li>Mã có thể hết hạn trước khi được sử dụng.</li>
                            <li>Ưu đãi không quy đổi thành tiền mặt.</li>
                        </ul>
                    </div>
                </aside>
            </div>
        </div>
    </section>
</main>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        const filterButtons = document.querySelectorAll(".voucher-filter");
        const voucherCards = document.querySelectorAll(".voucher-card");
        const voucherCountText = document.querySelector(".voucher-hero-card strong");
        const successModal = document.querySelector("[data-voucher-success]");

        if (voucherCountText) {
            const availableVoucherCount = Array.from(voucherCards).filter(function (card) {
                return card.getAttribute("data-status") !== "expired";
            }).length;
            voucherCountText.textContent = availableVoucherCount + " mã";
        }

        filterButtons.forEach(function (button) {
            button.addEventListener("click", function () {
                const filter = button.getAttribute("data-filter");

                filterButtons.forEach(function (item) {
                    const isActive = item === button;
                    item.classList.toggle("is-active", isActive);
                    item.setAttribute("aria-pressed", isActive ? "true" : "false");
                });

                voucherCards.forEach(function (card) {
                    const isVisible = filter === "all" || card.getAttribute("data-status") === filter;
                    card.classList.toggle("is-hidden", !isVisible);
                });
            });
        });

        if (successModal) {
            const closeButton = successModal.querySelector("[data-voucher-success-close]");
            const closeSuccessModal = function () {
                successModal.classList.add("is-hidden");
            };

            if (closeButton) {
                closeButton.addEventListener("click", closeSuccessModal);
            }

            successModal.addEventListener("click", function (event) {
                if (event.target === successModal) {
                    closeSuccessModal();
                }
            });

            setTimeout(closeSuccessModal, 2600);
        }
    });
</script>

<jsp:include page="/views/HomePage/Footer.jsp"/>
