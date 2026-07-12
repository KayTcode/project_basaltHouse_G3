<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    request.setAttribute("pageTitle", "Về chúng tôi - BasaltHouse");
%>

<jsp:include page="/views/HomePage/Header.jsp"/>

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/AboutUs/AboutUs.css">

<main class="about-page">
    <section class="about-hero">
        <div class="container">
            <div class="about-hero-inner">
                <div class="about-hero-copy">
                    <div class="about-kicker">
                        <span class="material-symbols-outlined">local_cafe</span>
                        BasaltHouse Story
                    </div>
                    <h1>Chúng tôi pha cà phê cho những khoảnh khắc tử tế mỗi ngày</h1>
                    <p>
                        BasaltHouse được xây dựng như một điểm dừng quen thuộc: cà phê rõ vị,
                        phục vụ gọn gàng và trải nghiệm đủ ấm để khách hàng muốn quay lại.
                    </p>
                    <div class="about-hero-actions">
                        <a href="${pageContext.request.contextPath}/category" class="about-btn about-btn--primary">
                            <span class="material-symbols-outlined">menu_book</span>
                            Khám phá menu
                        </a>
                        <a href="${pageContext.request.contextPath}/contact" class="about-btn about-btn--secondary">
                            <span class="material-symbols-outlined">support_agent</span>
                            Liên hệ
                        </a>
                    </div>
                </div>

                <div class="about-hero-media">
                    <img src="https://lh3.googleusercontent.com/aida-public/AB6AXuCHvKrOcXMbNmK34jL7gZ3EVtJ2KZigFRnn7O6xqeo8z_Eel3d-E7fEYSA9BmBbf4j-GHWESgdjfW2RTgN7yG1-SgjRwiZKst4yokTdB7w7VyuYaS5KcO8RHoeGYK-KnMzkuDR6ZUgliQeH_d1BPASXBpcQKHLSZ2GdRabH_U6xeobW4djCt6UgOaqYoKrx6QbpbLh-DPcyunDA78KmRC-OlnJVOYFbqVu0ez_3zsQPx1v6g_SFFuZ6uvIOPOOiGsdlsQU3KC5NnNE"
                         alt="Không gian cà phê BasaltHouse"
                         loading="lazy">
                    <div class="about-hero-note">
                        <span class="material-symbols-outlined">verified</span>
                        <div>
                            <strong>Good Coffee, Good Mood</strong>
                            <small>Chất lượng ổn định trong từng ly cà phê</small>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <section class="about-stat-section">
        <div class="container">
            <div class="about-stat-grid">
                <article class="about-stat-card">
                    <span class="material-symbols-outlined">coffee</span>
                    <small>Hương vị</small>
                    <strong>Pha mới mỗi ngày</strong>
                </article>
                <article class="about-stat-card">
                    <span class="material-symbols-outlined">eco</span>
                    <small>Nguyên liệu</small>
                    <strong>Chọn lọc rõ nguồn</strong>
                </article>
                <article class="about-stat-card">
                    <span class="material-symbols-outlined">groups</span>
                    <small>Dịch vụ</small>
                    <strong>Lấy khách hàng làm trung tâm</strong>
                </article>
                <article class="about-stat-card">
                    <span class="material-symbols-outlined">workspace_premium</span>
                    <small>Thành viên</small>
                    <strong>Tích lũy quyền lợi</strong>
                </article>
            </div>
        </div>
    </section>

    <section class="about-story-section">
        <div class="container">
            <div class="about-story-layout">
                <section class="about-story-panel">
                    <div class="about-panel-head">
                        <div>
                            <p>Câu chuyện thương hiệu</p>
                            <h2>Từ một ly cà phê ngon đến một thói quen đáng tin</h2>
                        </div>
                        <span class="about-panel-icon material-symbols-outlined">auto_stories</span>
                    </div>
                    <p>
                        BasaltHouse không chỉ bán đồ uống. Chúng tôi muốn tạo ra một quy trình
                        rõ ràng từ chọn nguyên liệu, pha chế, phục vụ đến chăm sóc sau đơn hàng.
                        Mỗi chi tiết nhỏ đều hướng đến sự ổn định: khách hàng biết mình sẽ nhận
                        được gì, nhân viên biết mình cần làm tốt điều gì.
                    </p>
                    <p>
                        Tên BasaltHouse gợi cảm giác chắc chắn và mộc mạc. Đó cũng là cách chúng
                        tôi xây dựng thương hiệu: bền, rõ, ít phô trương nhưng có chất lượng thật.
                    </p>
                </section>

                <aside class="about-timeline-panel">
                    <div class="about-timeline-item">
                        <span>01</span>
                        <div>
                            <h3>Chọn nguyên liệu</h3>
                            <p>Ưu tiên hạt cà phê và thành phần có chất lượng ổn định.</p>
                        </div>
                    </div>
                    <div class="about-timeline-item">
                        <span>02</span>
                        <div>
                            <h3>Pha chế nhất quán</h3>
                            <p>Quy trình được chuẩn hóa để giữ vị quen thuộc trong từng đơn.</p>
                        </div>
                    </div>
                    <div class="about-timeline-item">
                        <span>03</span>
                        <div>
                            <h3>Phục vụ gọn gàng</h3>
                            <p>Menu rõ, giá minh bạch và thao tác đặt món dễ theo dõi.</p>
                        </div>
                    </div>
                </aside>
            </div>
        </div>
    </section>

    <section class="about-value-section">
        <div class="container">
            <div class="about-section-head">
                <div>
                    <p>Giá trị vận hành</p>
                    <h2>Những điều BasaltHouse theo đuổi</h2>
                </div>
                <span class="about-section-icon material-symbols-outlined">favorite</span>
            </div>

            <div class="about-value-grid">
                <article class="about-value-card">
                    <span class="material-symbols-outlined">task_alt</span>
                    <h3>Rõ ràng</h3>
                    <p>Thông tin sản phẩm, giá bán, voucher và quyền lợi được trình bày dễ hiểu.</p>
                </article>
                <article class="about-value-card">
                    <span class="material-symbols-outlined">speed</span>
                    <h3>Nhanh gọn</h3>
                    <p>Trải nghiệm đặt món được thiết kế để khách hàng thao tác nhanh và ít chờ.</p>
                </article>
                <article class="about-value-card">
                    <span class="material-symbols-outlined">handshake</span>
                    <h3>Tận tâm</h3>
                    <p>Phản hồi của khách hàng là dữ liệu quan trọng để cải thiện dịch vụ.</p>
                </article>
                <article class="about-value-card">
                    <span class="material-symbols-outlined">verified_user</span>
                    <h3>Ổn định</h3>
                    <p>Chúng tôi ưu tiên chất lượng lặp lại được hơn những thay đổi nhất thời.</p>
                </article>
            </div>
        </div>
    </section>

    <section class="about-experience-section">
        <div class="container">
            <div class="about-experience-card">
                <div>
                    <p>Trải nghiệm khách hàng</p>
                    <h2>Một hệ sinh thái nhỏ cho người yêu cà phê</h2>
                    <span>
                        Từ menu, voucher, tích lũy thành viên đến hỗ trợ sau đơn hàng,
                        BasaltHouse đang từng bước hoàn thiện trải nghiệm mua cà phê trực tuyến
                        và tại quầy.
                    </span>
                </div>
                <div class="about-experience-list">
                    <div>
                        <span class="material-symbols-outlined">local_offer</span>
                        <strong>Voucher rõ điều kiện</strong>
                    </div>
                    <div>
                        <span class="material-symbols-outlined">payments</span>
                        <strong>Giá bán minh bạch</strong>
                    </div>
                    <div>
                        <span class="material-symbols-outlined">support_agent</span>
                        <strong>Hỗ trợ dễ liên hệ</strong>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <section class="about-cta-section">
        <div class="container">
            <div class="about-cta">
                <div>
                    <p>BasaltHouse</p>
                    <h2>Ghé menu và chọn ly cà phê hợp tâm trạng hôm nay</h2>
                </div>
                <a href="${pageContext.request.contextPath}/category" class="about-btn about-btn--light">
                    <span class="material-symbols-outlined">arrow_forward</span>
                    Xem menu
                </a>
            </div>
        </div>
    </section>
</main>

<jsp:include page="/views/HomePage/Footer.jsp"/>
