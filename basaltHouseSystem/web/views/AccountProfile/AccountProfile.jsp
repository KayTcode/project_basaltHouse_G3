<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<%
    request.setAttribute("pageTitle", "Thông tin tài khoản - BasaltHouse");
    request.setAttribute("pageStylesheet", "/css/AccountProfile/AccountProfile.css?v=20260709-2");
    request.setAttribute("pageStylesheetAfterTheme", true);
%>

<jsp:include page="/views/HomePage/Header.jsp"/>

<main class="account-inform-page">
    <section class="account-hero">
        <div class="container">
            <div class="account-hero-inner">
                <div class="account-hero-copy">
                    <div class="account-kicker">
                        <span class="material-symbols-outlined">manage_accounts</span>
                        Account Information
                    </div>
                    <h1>Thông tin tài khoản</h1>
                    <p>
                        Theo dõi thông tin cá nhân, liên hệ và trạng thái bảo mật của tài khoản
                        khách hàng BasaltHouse trong một màn hình gọn gàng.
                    </p>
                </div>

            </div>
        </div>
    </section>

    <section class="account-content-section">
        <div class="container">
            <c:if test="${not empty profileError}">
                <div class="account-form-alert account-form-alert--error account-profile-error" role="alert">
                    <span class="material-symbols-outlined">error</span>
                    <c:out value="${profileError}"/>
                </div>
            </c:if>

            <div class="account-layout">
                <aside class="account-profile-card">
                    <div class="account-avatar-wrap">
                        <c:choose>
                            <c:when test="${not empty cusr.avatarUrl}">
                                <img src="<c:out value='${cusr.avatarUrl}'/>" alt="Avatar"
                                     onerror="this.style.display='none';this.nextElementSibling.style.display='flex';">
                                <span class="account-avatar-fallback material-symbols-outlined">account_circle</span>
                            </c:when>
                            <c:otherwise>
                                <span class="account-avatar-fallback material-symbols-outlined">account_circle</span>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="account-profile-info">
                        <span class="account-status-chip">
                            <span class="material-symbols-outlined">check_circle</span>
                            Đang hoạt động
                        </span>
                        <h2>
                            <c:choose>
                                <c:when test="${not empty cusr.fullName}">
                                    <c:out value="${cusr.fullName}"/>
                                </c:when>
                                <c:otherwise>Chưa cập nhật họ tên</c:otherwise>
                            </c:choose>
                        </h2>
                        <p>
                            <c:choose>
                                <c:when test="${not empty cusr.email}">
                                    <c:out value="${cusr.email}"/>
                                </c:when>
                                <c:otherwise>Chưa cập nhật email</c:otherwise>
                            </c:choose>
                        </p>
                    </div>

                    <div class="account-mini-stats">
                        <div>
                            <span class="material-symbols-outlined">phone_iphone</span>
                            <small>Liên hệ</small>
                            <strong>
                                <c:choose>
                                    <c:when test="${not empty cusr.phone}"><c:out value="${cusr.phone}"/></c:when>
                                    <c:otherwise>Chưa có</c:otherwise>
                                </c:choose>
                            </strong>
                        </div>
                    </div>
                </aside>

                <section class="account-detail-panel">
                    <div class="account-panel-head">
                        <div>
                            <p>Chi tiết hồ sơ</p>
                            <h2>Thông tin cá nhân</h2>
                        </div>
                        <span class="account-panel-icon material-symbols-outlined">person</span>
                    </div>

                    <div class="account-detail-grid">
                        <div class="account-detail-item">
                            <span class="material-symbols-outlined">id_card</span>
                            <div>
                                <small>FullName</small>
                                <strong>
                                    <c:choose>
                                        <c:when test="${not empty cusr.fullName}">
                                            <c:out value="${cusr.fullName}"/>
                                        </c:when>
                                        <c:otherwise>Chưa cập nhật</c:otherwise>
                                    </c:choose>
                                </strong>
                            </div>
                        </div>

                        <div class="account-detail-item">
                            <span class="material-symbols-outlined">call</span>
                            <div>
                                <small>Phone</small>
                                <strong>
                                    <c:choose>
                                        <c:when test="${not empty cusr.phone}">
                                            <c:out value="${cusr.phone}"/>
                                        </c:when>
                                        <c:otherwise>Chưa cập nhật</c:otherwise>
                                    </c:choose>
                                </strong>
                            </div>
                        </div>

                        <div class="account-detail-item">
                            <span class="material-symbols-outlined">alternate_email</span>
                            <div>
                                <small>Email</small>
                                <strong>
                                    <c:choose>
                                        <c:when test="${not empty cusr.email}">
                                            <c:out value="${cusr.email}"/>
                                        </c:when>
                                        <c:otherwise>Chưa cập nhật</c:otherwise>
                                    </c:choose>
                                </strong>
                            </div>
                        </div>

                        <div class="account-detail-item account-detail-item--wide account-password-item">
                            <span class="material-symbols-outlined">lock</span>
                            <div>
                                <small>Mật khẩu</small>
                                <strong>Cập nhật mật khẩu đăng nhập</strong>

                                <c:if test="${not empty passwordError}">
                                    <div class="account-form-alert account-form-alert--error" role="alert">
                                        <span class="material-symbols-outlined">error</span>
                                        <c:out value="${passwordError}"/>
                                    </div>
                                </c:if>

                                <c:if test="${not empty passwordSuccess}">
                                    <div class="account-form-alert account-form-alert--success" role="status">
                                        <span class="material-symbols-outlined">check_circle</span>
                                        <c:out value="${passwordSuccess}"/>
                                    </div>
                                </c:if>

                                <form class="account-password-form"
                                      method="post"
                                      action="${pageContext.request.contextPath}/profile">
                                    <input type="hidden" name="action" value="updatePassword">
                                    <label>
                                        <span>Mật khẩu cũ</span>
                                        <input type="password" name="oldPassword" placeholder="Nhập mật khẩu cũ" required>
                                    </label>
                                    <label>
                                        <span>Mật khẩu mới</span>
                                        <input type="password" name="newPassword" placeholder="Nhập mật khẩu mới" required>
                                    </label>
                                    <button type="submit">
                                        <span class="material-symbols-outlined">lock_reset</span>
                                        Cập nhật mật khẩu
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                </section>
            </div>
        </div>
    </section>
</main>

<jsp:include page="/views/HomePage/Footer.jsp"/>
