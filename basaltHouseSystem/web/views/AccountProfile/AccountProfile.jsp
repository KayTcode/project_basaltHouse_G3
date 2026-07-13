<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<%
    request.setAttribute("pageTitle", "Thông tin tài khoản - BasaltHouse");
%>

<c:set var="fullNameVal" value="${not empty cusr.fullName ? cusr.fullName : fullName}"/>
<c:set var="phoneVal" value="${not empty cusr.phone ? cusr.phone : phone}"/>
<c:set var="avatarUrlVal" value="${not empty cusr.avatarUrl ? cusr.avatarUrl : avatarUrl}"/>
<c:set var="emailVal" value="${not empty cusr.email ? cusr.email : email}"/>

<jsp:include page="/views/HomePage/Header.jsp"/>

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/Customer/CustomerTheme.css?v=20260709-1">
<link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/AccountProfile/AccountProfile.css?v=20260709-2">

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
            <div class="account-layout">
                <aside class="account-profile-card">
                    <details class="account-avatar-editor">
                        <summary class="account-avatar-wrap" title="Cập nhật URL ảnh đại diện">
                            <c:choose>
                                <c:when test="${not empty avatarUrlVal}">
                                    <img src="${avatarUrlVal}" alt="Avatar"
                                         onerror="this.style.display='none';this.nextElementSibling.style.display='flex';">
                                    <span class="account-avatar-fallback material-symbols-outlined">account_circle</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="account-avatar-fallback material-symbols-outlined">account_circle</span>
                                </c:otherwise>
                            </c:choose>
                            <span class="account-avatar-edit">
                                <span class="material-symbols-outlined">edit</span>
                                Cập nhật URL
                            </span>
                        </summary>

                        <div class="account-avatar-url-panel">
                            <label for="avatarUrlInput">URL ảnh đại diện</label>
                            <div class="account-avatar-url-row">
                                <input id="avatarUrlInput"
                                       type="url"
                                       name="avatarUrl"
                                       value="${avatarUrlVal}"
                                       placeholder="https://example.com/avatar.jpg">
                                <button type="button">
                                    <span class="material-symbols-outlined">image</span>
                                    Cập nhật
                                </button>
                            </div>
                            <small>Phần này mới là giao diện nhập URL, chưa gắn xử lý lưu dữ liệu.</small>
                        </div>
                    </details>

                    <div class="account-profile-info">
                        <span class="account-status-chip">
                            <span class="material-symbols-outlined">check_circle</span>
                            Đang hoạt động
                        </span>
                        <h2>
                            <c:choose>
                                <c:when test="${not empty fullNameVal}">
                                    <c:out value="${fullNameVal}"/>
                                </c:when>
                                <c:otherwise>Chưa cập nhật họ tên</c:otherwise>
                            </c:choose>
                        </h2>
                        <p>
                            <c:choose>
                                <c:when test="${not empty emailVal}">
                                    <c:out value="${emailVal}"/>
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
                                    <c:when test="${not empty phoneVal}"><c:out value="${phoneVal}"/></c:when>
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
                                        <c:when test="${not empty fullNameVal}">
                                            <c:out value="${fullNameVal}"/>
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
                                        <c:when test="${not empty phoneVal}">
                                            <c:out value="${phoneVal}"/>
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
                                        <c:when test="${not empty emailVal}">
                                            <c:out value="${emailVal}"/>
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
                                    <c:if test="${not empty error}">
                                        <div class="account-form-alert account-form-alert--error account-form-alert--form" role="alert">
                                            <span class="material-symbols-outlined">error</span>
                                            <c:out value="${error}"/>
                                        </div>
                                    </c:if>
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

<c:if test="${not empty passwordSuccess}">
    <script>
        window.addEventListener("DOMContentLoaded", function () {
            alert("Đổi mật khẩu thành công");
        });
    </script>
</c:if>

<jsp:include page="/views/HomePage/Footer.jsp"/>
