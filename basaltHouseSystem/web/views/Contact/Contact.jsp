<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    request.setAttribute("pageTitle", "Liên hệ - BasaltHouse");
%>

<jsp:include page="/views/HomePage/Header.jsp"/>

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/Contact/Contact.css?v=20260709-1">
<link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/Customer/CustomerTheme.css?v=20260709-1">

<main class="contact-page">
    <section class="contact-hero">
        <div class="container">
            <div class="contact-hero-inner">
                <div class="contact-hero-copy">
                    <div class="contact-kicker">
                        <span class="material-symbols-outlined">support_agent</span>
                        BasaltHouse Contact
                    </div>
                    <h1>Liên hệ với BasaltHouse</h1>
                    <p>
                        Gửi góp ý, câu hỏi hoặc yêu cầu hỗ trợ. Đội ngũ BasaltHouse sẽ phản hồi
                        và đồng hành cùng bạn trong thời gian sớm nhất.
                    </p>
                </div>

                <div class="contact-hero-card">
                    <span class="material-symbols-outlined">verified</span>
                    <strong>Phản hồi tận tâm</strong>
                    <small>Thông tin rõ ràng, hỗ trợ nhanh, luôn giữ trải nghiệm khách hàng là trung tâm.</small>
                </div>
            </div>
        </div>
    </section>

    <section class="contact-content-section">
        <div class="container">
            <div class="contact-layout">
                <section class="contact-form-panel">
                    <div class="contact-panel-head">
                        <div>
                            <p>Gửi thông tin</p>
                            <h2>Chúng tôi luôn lắng nghe</h2>
                        </div>
                        <span class="contact-panel-icon material-symbols-outlined">mail</span>
                    </div>

                    <form action="${pageContext.request.contextPath}/contact"
                          method="post"
                          class="contact-form"
                          data-contact-form>

                        <div class="contact-field-grid">
                            <label class="contact-field">
                                <span>Họ và tên</span>
                                <input type="text" name="fullName" placeholder="Nhập họ và tên của bạn" required>
                            </label>

                            <label class="contact-field">
                                <span>Email</span>
                                <input type="email" name="email" placeholder="example@email.com" required>
                            </label>
                        </div>

                        <label class="contact-field">
                            <span>Số điện thoại</span>
                            <input type="text" name="phone" placeholder="Số điện thoại liên hệ">
                        </label>

                        <label class="contact-field">
                            <span>Nội dung</span>
                            <textarea name="message" placeholder="Bạn muốn trao đổi điều gì với BasaltHouse?" required></textarea>
                        </label>

                        <button type="submit" class="contact-submit-btn">
                            <span class="material-symbols-outlined">send</span>
                            Gửi liên hệ
                        </button>
                    </form>
                </section>

                <aside class="contact-info-panel">
                    <div class="contact-brand-card">
                        <div class="contact-logo-mark">
                            <img src="${pageContext.request.contextPath}/assets/img/logo.png"
                                 alt="BasaltHouse Logo">
                        </div>
                        <div>
                            <p>Good Coffee, Good Mood</p>
                            <h2>BasaltHouse</h2>
                        </div>
                    </div>

                    <div class="contact-info-list">
                        <div class="contact-info-item">
                            <span class="material-symbols-outlined">location_on</span>
                            <div>
                                <strong>Địa chỉ</strong>
                                <p>123 Đường Cà Phê, Quận 1, TP. Hồ Chí Minh</p>
                            </div>
                        </div>

                        <div class="contact-info-item">
                            <span class="material-symbols-outlined">call</span>
                            <div>
                                <strong>Điện thoại</strong>
                                <p>1900 1234</p>
                            </div>
                        </div>

                        <div class="contact-info-item">
                            <span class="material-symbols-outlined">alternate_email</span>
                            <div>
                                <strong>Email</strong>
                                <p>support@basalthouse.vn</p>
                            </div>
                        </div>
                    </div>

                    <div class="contact-map-card" aria-label="Khu vực bản đồ minh họa">
                        <div class="contact-map-pin">
                            <span class="material-symbols-outlined">storefront</span>
                        </div>
                        <div class="contact-map-line contact-map-line--one"></div>
                        <div class="contact-map-line contact-map-line--two"></div>
                        <div class="contact-map-line contact-map-line--three"></div>
                    </div>

                    <div class="contact-hours-card">
                        <div>
                            <span class="material-symbols-outlined">schedule</span>
                            <strong>Giờ hỗ trợ</strong>
                        </div>
                        <p>Thứ 2 - Chủ nhật</p>
                        <p>07:00 - 22:00</p>
                    </div>
                </aside>
            </div>
        </div>
    </section>
</main>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        const form = document.querySelector("[data-contact-form]");
        const shouldOpenOnLoad = "${contactSuccess}" === "true";

        if (form) {
            form.addEventListener("submit", function (event) {
                event.preventDefault();

                if (!form.checkValidity()) {
                    form.reportValidity();
                    return;
                }

                form.reset();
                window.alert("Cảm ơn bạn đã liên hệ");
            });
        }

        if (shouldOpenOnLoad) {
            window.alert("Cảm ơn bạn đã liên hệ");
        }
    });
</script>

<jsp:include page="/views/HomePage/Footer.jsp"/>
