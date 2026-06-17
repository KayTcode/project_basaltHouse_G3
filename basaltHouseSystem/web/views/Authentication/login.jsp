<%-- 
    Document   : Login
    Created on : Jun 3, 2026, 1:19:05 PM
    Author     : KayT
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đăng Nhập — BasaltHouse</title>

        <!-- Bootstrap 5 CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Google Fonts: Montserrat + Inter (khớp HomePage) -->
        <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700;900&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
        <!-- Material Symbols -->
        <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet">
        <link href="css/auth/login.css" rel="stylesheet">
    </head>
    <body>

        <div class="login-page">

            <%-- ── Header ──────────────────────────────────────────────────────── --%>
            <header class="login-header">
                <a href="${pageContext.request.contextPath}/home" class="login-header-brand">
                    BasaltHouse
                </a>
                <a href="${pageContext.request.contextPath}/home" class="login-header-back">
                    <span class="material-symbols-outlined">arrow_back</span>
                    Về trang chủ
                    <span class="material-symbols-outlined">arrow_forward</span>
                </a>
            </header>

            <%-- ── Main ────────────────────────────────────────────────────────── --%>
            <main class="login-main">
                <div class="login-wrapper">

                    <%-- ── Cột trái: Brand panel ─────────────────────────────── --%>
                    <div class="login-brand-panel">
                        <div class="brand-panel-top">
                            <span class="brand-panel-logo">BasaltHouse</span>
                            <h2 class="brand-panel-title">
                                <span class="highlight">Good Coffee,</span><br>
                                Good Mood.
                            </h2>
                            <p class="brand-panel-desc">
                                Đăng nhập để đặt hàng, theo dõi đơn giao hàng
                                và nhận ưu đãi dành riêng cho thành viên.
                            </p>
                        </div>

                        <%-- 3 tính năng — giống feature-badge trên HomePage --%>
                        <div class="brand-features">
                            <div class="brand-feature-item">
                                <div class="brand-feature-icon">
                                    <span class="material-symbols-outlined">local_shipping</span>
                                </div>
                                <span class="brand-feature-text">Giao hàng nhanh 30 phút</span>
                            </div>
                            <div class="brand-feature-item">
                                <div class="brand-feature-icon">
                                    <span class="material-symbols-outlined">workspace_premium</span>
                                </div>
                                <span class="brand-feature-text">Tích điểm đổi quà hấp dẫn</span>
                            </div>
                            <div class="brand-feature-item">
                                <div class="brand-feature-icon">
                                    <span class="material-symbols-outlined">eco</span>
                                </div>
                                <span class="brand-feature-text">100% cà phê nguyên chất</span>
                            </div>
                        </div>
                    </div>

                    <%-- ── Cột phải: Form đăng nhập ───────────────────────────── --%>
                    <div class="login-form-panel">

                        <p class="login-form-eyebrow">Chào mừng trở lại</p>
                        <h1 class="login-form-title">Đăng nhập</h1>
                        <p class="login-form-subtitle">
                            Nhập email và mật khẩu để tiếp tục
                        </p>

                        <%-- ── Thông báo đăng xuất thành công (từ ?logout=1) ─── --%>
                        <c:if test="${param.logout eq '1'}">
                            <div class="alert-success-coffeely" role="status">
                                <span class="material-symbols-outlined">check_circle</span>
                                <span class="msg">Bạn đã đăng xuất thành công. Hẹn gặp lại!</span>
                            </div>
                        </c:if>

                        <%-- ── Thông báo lỗi từ LoginServlet ─────────────────── --%>
                        <%--
                            errorMessage được set bởi LoginServlet.forwardWithError().
                            c:out tự escape HTML → chống XSS.
                        --%>
                        <c:if test="${not empty errorMessage}">
                            <div class="alert-error-coffeely" role="alert">
                                <span class="material-symbols-outlined">error</span>
                                <span class="msg"><c:out value="${errorMessage}"/></span>
                            </div>
                        </c:if>

                        <%-- ── Form POST /login ────────────────────────────────── --%>
                        <form method="POST"
                              action="${pageContext.request.contextPath}/login"
                              id="loginForm"
                              novalidate>

                            <%-- Email --%>
                            <div class="form-field">
                                <label class="form-label-coffeely" for="email">
                                    Địa chỉ Email
                                </label>
                                <div class="input-wrapper">
                                    <input type="email"
                                           id="email"
                                           name="email"
                                           class="form-input-coffeely"
                                           placeholder="you@example.com"
                                           value="<c:out value='${submittedEmail}' default=''/>"
                                           autocomplete="email"
                                           autofocus
                                           required>
                                    <span class="material-symbols-outlined input-icon">mail</span>
                                </div>
                            </div>

                            <%-- Password --%>
                            <div class="form-field">
                                <label class="form-label-coffeely" for="password">
                                    Mật khẩu
                                </label>
                                <div class="input-wrapper">
                                    <input type="password"
                                           id="password"
                                           name="password"
                                           class="form-input-coffeely input-password"
                                           placeholder="••••••••"
                                           autocomplete="current-password"
                                           required>
                                    <span class="material-symbols-outlined input-icon">lock</span>
                                    <%-- Toggle hiện/ẩn mật khẩu --%>
                                    <button type="button"
                                            class="btn-toggle-password"
                                            id="togglePassword"
                                            aria-label="Hiện/ẩn mật khẩu"
                                            title="Hiện/ẩn mật khẩu">
                                        <span class="material-symbols-outlined" id="eyeIcon">
                                            visibility
                                        </span>
                                    </button>
                                </div>
                            </div>

                            <%-- Remember me + Forgot password --%>
                            <div class="form-meta">
                                <label class="remember-label">
                                    <input type="checkbox"
                                           name="rememberMe"
                                           id="rememberMe"
                                           class="remember-checkbox">
                                    <span class="remember-text">Ghi nhớ đăng nhập</span>
                                </label>
                                <a href="${pageContext.request.contextPath}/forgot-password"
                                   class="forgot-link">
                                    Quên mật khẩu?
                                </a>
                            </div>

                            <%-- Submit button --%>
                            <button type="submit" class="btn-submit-login" id="submitBtn">
                                <span class="material-symbols-outlined">login</span>
                                Đăng nhập
                            </button>
                            <c:if test="${not empty sessionScope.loginError}">
                                <div class="alert alert-error">
                                    <span>⚠️</span> ${sessionScope.loginError}
                                </div>
                                <c:remove var="loginError" scope="session"/>
                            </c:if>

                            <!-- Divider -->
                            <div class="oauth-divider">
                                <span class="oauth-divider-line"></span>
                                <span class="oauth-divider-text">hoặc</span>
                                <span class="oauth-divider-line"></span>
                            </div>

                            <!-- Nút Google -->
                            <a href="${pageContext.request.contextPath}/google-login" class="btn-google">
                                <svg class="google-icon" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"/>
                                <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/>
                                <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l3.66-2.84z" fill="#FBBC05"/>
                                <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/>
                                </svg>
                                <span>Đăng nhập bằng Google</span>
                            </a>
                        </form>

                        <%-- Divider --%>
                        <div class="divider-or">
                            <span>Chưa có tài khoản?</span>
                        </div>

                        <%-- Link đăng ký --%>
                        <p class="register-prompt">
                            <a href="${pageContext.request.contextPath}/register">
                                Tạo tài khoản mới ngay →
                            </a>
                        </p>

                    </div>
                    <%-- end login-form-panel --%>

                </div>
                <%-- end login-wrapper --%>
            </main>

            <%-- ── Footer ──────────────────────────────────────────────────────── --%>
            <footer class="login-footer">
                © 2024 BasaltHouse. All rights reserved. Crafted for coffee lovers.
            </footer>

        </div>
        <%-- end login-page --%>

        <!-- Bootstrap 5 JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

        <script>
            (function () {
                'use strict';

                /* ── Toggle hiện/ẩn mật khẩu ────────────────────────────────────── */
                var toggleBtn = document.getElementById('togglePassword');
                var passwordEl = document.getElementById('password');
                var eyeIcon = document.getElementById('eyeIcon');

                if (toggleBtn && passwordEl) {
                    toggleBtn.addEventListener('click', function () {
                        var isHidden = passwordEl.type === 'password';
                        passwordEl.type = isHidden ? 'text' : 'password';
                        eyeIcon.textContent = isHidden ? 'visibility_off' : 'visibility';
                        toggleBtn.setAttribute('aria-label',
                                isHidden ? 'Ẩn mật khẩu' : 'Hiện mật khẩu');
                    });
                }

                /* ── Loading state khi submit ───────────────────────────────────── */
                var form = document.getElementById('loginForm');
                var submitBtn = document.getElementById('submitBtn');

                if (form && submitBtn) {
                    form.addEventListener('submit', function () {
                        // Validate HTML5 trước — nếu invalid thì không loading
                        if (!form.checkValidity())
                            return;

                        submitBtn.classList.add('loading');
                        submitBtn.innerHTML =
                                '<span class="material-symbols-outlined" '
                                + 'style="animation:spin .8s linear infinite">progress_activity</span>'
                                + 'Đang xử lý...';
                    });
                }
            }());
        </script>

        <style>
            /* Spin animation cho loading icon */
            @keyframes spin {
                from {
                    transform: rotate(0deg);
                }
                to   {
                    transform: rotate(360deg);
                }
            }
        </style>

    </body>
</html>
