<%-- 
    Document   : forgot-password
    Created on : Jun 10, 2026, 6:32:20 PM
    Author     : KayT
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quên mật khẩu - Coffeely</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,600;1,400&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet">
        <%-- Dùng chung CSS với register.jsp để đồng bộ giao diện --%>
        <link href="${pageContext.request.contextPath}/css/auth/register.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/auth/forgot-password.css" rel="stylesheet">
    </head>
    <body>
        <div class="page-wrapper">

            <!-- ===== LEFT: BRAND PANEL (dùng chung class với register) ===== -->
            <div class="brand-panel">
                <div class="ring ring-1"></div>
                <div class="ring ring-2"></div>
                <div class="ring ring-3"></div>
                <div class="brand-content">
                    <div class="brand-logo">☕ Coffeely</div>
                    <div class="brand-sub">BasaltHouse</div>
                    <div class="brand-divider"></div>
                    <p class="brand-tagline">"Đặt lại mật khẩu để tiếp tục hành trình cà phê."</p>
                    <div class="brand-features">
                        <div class="brand-feature">
                            <span class="feature-dot"></span>
                            <span>Nhập email đã đăng ký</span>
                        </div>
                        <div class="brand-feature">
                            <span class="feature-dot"></span>
                            <span>Nhận mã OTP trong vài giây</span>
                        </div>
                        <div class="brand-feature">
                            <span class="feature-dot"></span>
                            <span>Tạo mật khẩu mới an toàn</span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- ===== RIGHT: FORM PANEL ===== -->
            <div class="form-panel">
                <div class="form-container">

                    <!-- Bước tiến trình: 1 - 2 - 3 -->
                    <div class="step-indicator">
                        <div class="step-dot active"></div>
                        <div class="step-dot"></div>
                        <div class="step-dot"></div>
                    </div>

                    <!-- Icon -->
                    <div class="fp-icon-wrap">
                        <div class="fp-icon">🔑</div>
                    </div>

                    <!-- Tiêu đề -->
                    <div class="form-header">
                        <h1 class="form-title">Quên mật khẩu?</h1>
                        <p class="form-subtitle">
                            Nhập email đã đăng ký. Chúng tôi sẽ gửi mã xác thực OTP về hộp thư của bạn.
                        </p>
                    </div>

                    <%-- ── Thông báo lỗi (JSTL) ── --%>
                    <c:if test="${not empty error}">
                        <div class="alert alert-error" role="alert">
                            <span class="alert-icon">⚠️</span>
                            <span>${error}</span>
                        </div>
                    </c:if>

                    <%-- ── Thông báo thành công (ví dụ: gửi lại OTP) ── --%>
                    <c:if test="${not empty success}">
                        <div class="alert alert-success" role="alert">
                            <span class="alert-icon">✅</span>
                            <span>${success}</span>
                        </div>
                    </c:if>

                    <!-- FORM -->
                    <form action="${pageContext.request.contextPath}/forgot-password"
                          method="post" id="forgotForm" novalidate>

                        <!-- Email -->
                        <div class="form-group">
                            <label class="form-label" for="email">Địa chỉ Email</label>
                            <div class="input-wrapper">
                                <span class="input-icon">✉️</span>
                                <input type="email" id="email" name="email"
                                       class="form-input"
                                       placeholder="example@gmail.com"
                                       value="${not empty email ? email : ''}"
                                       required autocomplete="email"
                                       autofocus>
                            </div>
                        </div>

                        <!-- Nút gửi -->
                        <button type="submit" class="btn-register" id="submitBtn">
                            Gửi mã xác thực →
                        </button>

                        <p class="fp-note">
                            Mã OTP có hiệu lực trong <strong>5 phút</strong> sau khi gửi.
                        </p>

                    </form>

                    <!-- Quay lại đăng nhập -->
                    <a href="${pageContext.request.contextPath}/login" class="back-to-login">
                        Quay lại đăng nhập
                    </a>

                </div>
            </div>

        </div>

        <script>
            document.getElementById('forgotForm').addEventListener('submit', function (e) {
                const email = document.getElementById('email').value.trim();
                if (!email) {
                    e.preventDefault();
                    alert('Vui lòng nhập địa chỉ email.');
                    return;
                }
                if (!/^[\w.-]+@[\w.-]+\.[a-zA-Z]{2,}$/.test(email)) {
                    e.preventDefault();
                    alert('Địa chỉ email không hợp lệ.');
                    return;
                }
                // Disable nút để tránh submit 2 lần
                const btn = document.getElementById('submitBtn');
                btn.textContent = 'Đang gửi...';
                btn.disabled = true;
            });
        </script>
    </body>
</html>
