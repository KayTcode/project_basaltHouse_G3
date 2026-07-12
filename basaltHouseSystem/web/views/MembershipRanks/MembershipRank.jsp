<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%
    request.setAttribute("pageTitle", "Membership Rank - BasaltHouse");
%>




<jsp:include page="/views/HomePage/Header.jsp"/>

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/MembershipRank/MembershipRank.css?v=20260709-1">
<link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/Customer/CustomerTheme.css?v=20260709-1">

<main class="membership-page">
    <section class="membership-hero">
        <div class="container">
            <div class="membership-hero-inner">
                <div class="membership-hero-copy">
                    <div class="membership-kicker">
                        <span class="material-symbols-outlined">workspace_premium</span>
                        BasaltHouse Membership
                    </div>
                    <h1>Hạng thành viên</h1>
                    <p>
                        Theo dõi hạng hiện tại, tổng chi tiêu và lộ trình nâng hạng
                        dành riêng cho khách hàng BasaltHouse.
                    </p>
                </div>

                <aside class="membership-pass">
                    <div class="membership-pass-top">
                        <span class="material-symbols-outlined">verified</span>
                        <small>Current Rank</small>
                    </div>
                    <strong>${cus.name}</strong>
                    <p>${name}</p>
                </aside>
            </div>
        </div>
    </section>

    <section class="membership-summary-section">
        <div class="container">
            <div class="membership-summary-grid">
                <article class="membership-stat-card">
                    <span class="material-symbols-outlined">payments</span>
                    <small>Tổng chi tiêu</small>
                    <strong><fmt:formatNumber value="${cus.totalSpent}" pattern="#,###"/> đ</strong>
                </article>
                <article class="membership-stat-card">
                    <span class="material-symbols-outlined">local_offer</span>
                    <small>Ưu đãi hạng</small>
                    <strong><fmt:formatNumber value="${cus.discount}" pattern="#,###"/>%</strong>
                </article>
                <article class="membership-stat-card">
                    <span class="material-symbols-outlined">trending_up</span>
                    <small>Còn lại đến <c:out value="${cus.nextRank}"/></small>
                    <strong><fmt:formatNumber value="${cus.nextRankMinSpent}" pattern="#,###"/> đ</strong>
                </article>
            </div>

            <section class="membership-progress-panel" style="--membership-progress: ${progressValue}%;">
                <div class="membership-panel-head">
                    <div>
                        <p>Lộ trình nâng hạng</p>
                        <h2><c:out value="${cus.name}"/> → <c:out value="${cus.nextRank}"/></h2>
                    </div>
                    <span class="membership-panel-icon material-symbols-outlined">moving</span>
                </div>

                <div class="membership-progress-row">
                    <span><fmt:formatNumber value="${cus.totalSpent}" pattern="#,###"/> đ</span>
                    <span><fmt:formatNumber value="${cus.nextRankMinSpent}" pattern="#,###"/> đ</span>
                </div>
                <div class="membership-progress-track" aria-label="Membership progress">
                    <div class="membership-progress-fill"></div>
                </div>
                <div class="membership-milestones">
                    <span>Bronze</span>
                    <span>Silver</span>
                    <span>Gold</span>
                    <span>Diamond</span>
                </div>
            </section>
        </div>
    </section>

    <section class="membership-content-section">
        <div class="container">
            <div class="membership-layout membership-layout--single">
                <section class="membership-rank-panel">
                    <div class="membership-panel-head">
                        <div>
                            <p>Bảng hạng</p>
                            <h2>Các mốc thành viên</h2>
                        </div>
                        <span class="membership-panel-icon material-symbols-outlined">leaderboard</span>
                    </div>

                    <div class="membership-rank-grid">
                        <c:choose>
                            <c:when test="${not empty rankList}">
                                <c:forEach var="rank" items="${rankList}">
                                    <article class="membership-rank-card ${rank.rankName eq rankName ? 'is-current' : ''}">
                                        <span class="membership-rank-icon material-symbols-outlined">workspace_premium</span>
                                        <div>
                                            <small>Rank</small>
                                            <h3><c:out value="${rank.rankName}"/></h3>
                                            <p>Từ <fmt:formatNumber value="${rank.minTotalSpent}" pattern="#,###"/> đ</p>
                                            <strong><fmt:formatNumber value="${rank.discountValue}" pattern="#,###"/>% off</strong>
                                        </div>
                                    </article>
                                </c:forEach>
                            </c:when>
                            
                        </c:choose>
                    </div>
                </section>

            </div>

            <aside class="membership-action-panel">
                <span class="material-symbols-outlined">coffee</span>
                <h2>Tiến gần hạng <c:out value="${nextRankName}"/></h2>
                <p>Mỗi đơn hàng tại BasaltHouse đều được cộng vào tổng chi tiêu để xét hạng thành viên.</p>
                <div class="membership-action-row">
                    <a href="${pageContext.request.contextPath}/category">Xem menu</a>
                    <a href="${pageContext.request.contextPath}/voucher" class="membership-link-secondary">Voucher</a>
                </div>
            </aside>
        </div>
    </section>
</main>

<jsp:include page="/views/HomePage/Footer.jsp"/>
