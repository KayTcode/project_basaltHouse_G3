<%-- 
    Document   : register
    Created on : Jun 7, 2026, 2:20:26 AM
    Author     : KayT
--%>

<<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đăng ký - Coffeely</title>

        <!-- Google Fonts: Playfair Display + DM Sans -->
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,600;1,400&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet">
        <link href="css/auth/register.css" rel="stylesheet">
    </head>
    <body>
        <div class="page-wrapper">

            <!-- ===== LEFT: BRAND PANEL ===== -->
            <div class="brand-panel">
                <div class="ring ring-1"></div>
                <div class="ring ring-2"></div>
                <div class="ring ring-3"></div>
                <div class="brand-content">
                    <div class="brand-logo">☕ Coffeely</div>
                    <div class="brand-sub">BasaltHouse</div>
                    <div class="brand-divider"></div>
                    <p class="brand-tagline">"Mỗi tách cà phê là một khoảnh khắc đáng nhớ."</p>
                    <div class="brand-features">
                        <div class="brand-feature">
                            <span class="feature-dot"></span>
                            <span>Đặt hàng online nhanh chóng, tiện lợi</span>
                        </div>
                        <div class="brand-feature">
                            <span class="feature-dot"></span>
                            <span>Tích điểm và nhận ưu đãi độc quyền</span>
                        </div>
                        <div class="brand-feature">
                            <span class="feature-dot"></span>
                            <span>Theo dõi lịch sử đơn hàng mọi lúc</span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- ===== RIGHT: FORM PANEL ===== -->
            <div class="form-panel">
                <div class="form-container">

                    <div class="form-header">
                        <h1 class="form-title">Tạo tài khoản</h1>
                        <p class="form-subtitle">
                            Đã có tài khoản?
                            <a href="${pageContext.request.contextPath}/login">Đăng nhập ngay</a>
                        </p>
                    </div>

                    <%-- === HIỂN THỊ THÔNG BÁO LỖI (JSTL) === --%>
                    <c:if test="${not empty error}">
                        <div class="alert alert-error" role="alert">
                            <span class="alert-icon">⚠️</span>
                            <span>${error}</span>
                        </div>
                    </c:if>

                    <!-- FORM ĐĂNG KÝ -->
                    <form action="${pageContext.request.contextPath}/register" method="post" id="registerForm" novalidate>

                        <!-- Họ và Tên -->
                        <div class="form-group">
                            <label class="form-label" for="fullName">Họ và tên</label>
                            <div class="input-wrapper">
                                <span class="input-icon">👤</span>
                                <input type="text" id="fullName" name="fullName"
                                       class="form-input"
                                       placeholder="Nguyễn Văn A"
                                       value="${not empty fullName ? fullName : ''}"
                                       required autocomplete="name">
                            </div>
                        </div>

                        <!-- Email -->
                        <div class="form-group">
                            <label class="form-label" for="email">Email</label>
                            <div class="input-wrapper">
                                <span class="input-icon">✉️</span>
                                <input type="email" id="email" name="email"
                                       class="form-input"
                                       placeholder="example@gmail.com"
                                       value="${not empty email ? email : ''}"
                                       required autocomplete="email">
                            </div>
                        </div>

                        <!-- Số điện thoại + Mật khẩu (2 cột) -->
                        <div class="form-row">
                            <div class="form-group">
                                <label class="form-label" for="phone">Số điện thoại</label>
                                <div class="input-wrapper">
                                    <span class="input-icon">📱</span>
                                    <input type="tel" id="phone" name="phone"
                                           class="form-input"
                                           placeholder="0912345678"
                                           value="${not empty phone ? phone : ''}"
                                           required autocomplete="tel">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="form-label" for="password">Mật khẩu</label>
                                <div class="input-wrapper">
                                    <span class="input-icon">🔒</span>
                                    <input type="password" id="password" name="password"
                                           class="form-input"
                                           placeholder="Tối thiểu 8 ký tự"
                                           required autocomplete="new-password"
                                           oninput="checkStrength(this.value)">
                                    <button type="button" class="password-toggle"
                                            onclick="togglePassword()" title="Hiện/ẩn mật khẩu">
                                        <span id="eyeIcon">👁️</span>
                                    </button>
                                </div>
                                <!-- Thanh độ mạnh mật khẩu -->
                                <div class="password-strength" id="strengthBars">
                                    <div class="strength-bar" id="bar1"></div>
                                    <div class="strength-bar" id="bar2"></div>
                                    <div class="strength-bar" id="bar3"></div>
                                    <div class="strength-bar" id="bar4"></div>
                                </div>
                                <div class="strength-label" id="strengthLabel"></div>
                            </div>
                        </div>

                        <!-- Nút đăng ký -->
                        <button type="submit" class="btn-register" id="submitBtn">
                            Tạo tài khoản →
                        </button>

                        <p class="terms-text">
                            Bằng cách đăng ký, bạn đồng ý với
                            <a href="#">Điều khoản dịch vụ</a> và
                            <a href="#">Chính sách bảo mật</a> của chúng tôi.
                        </p>

                    </form>

                </div>
            </div>
        </div>

        <script>
            // ===== Toggle hiện/ẩn mật khẩu =====
            function togglePassword() {
                const input = document.getElementById('password');
                const icon = document.getElementById('eyeIcon');
                if (input.type === 'password') {
                    input.type = 'text';
                    icon.textContent = '🙈';
                } else {
                    input.type = 'password';
                    icon.textContent = '👁️';
                }
            }

            // ===== Kiểm tra độ mạnh mật khẩu =====
            function checkStrength(password) {
                let score = 0;
                if (password.length >= 8)
                    score++;
                if (/[A-Z]/.test(password))
                    score++;
                if (/[0-9]/.test(password))
                    score++;
                if (/[^A-Za-z0-9]/.test(password))
                    score++;

                const bars = ['bar1', 'bar2', 'bar3', 'bar4'];
                const colors = ['#e07a5f', '#f4a261', '#52b788', '#2d6a4f'];
                const labels = ['', 'Yếu', 'Trung bình', 'Mạnh', 'Rất mạnh'];

                bars.forEach((id, i) => {
                    const el = document.getElementById(id);
                    el.style.background = i < score ? colors[score - 1] : 'var(--border)';
                });

                const labelEl = document.getElementById('strengthLabel');
                labelEl.textContent = password.length > 0 ? labels[score] : '';
                labelEl.style.color = score > 0 ? colors[score - 1] : '';
            }

            // ===== Client-side validation trước khi submit =====
            document.getElementById('registerForm').addEventListener('submit', function (e) {
                const fullName = document.getElementById('fullName').value.trim();
                const email = document.getElementById('email').value.trim();
                const phone = document.getElementById('phone').value.trim();
                const password = document.getElementById('password').value;

                if (!fullName || !email || !phone || !password) {
                    e.preventDefault();
                    alert('Vui lòng điền đầy đủ tất cả các trường.');
                    return;
                }
                if (password.length < 8) {
                    e.preventDefault();
                    alert('Mật khẩu phải có ít nhất 8 ký tự.');
                    return;
                }
                if (!/^0[0-9]{9}$/.test(phone)) {
                    e.preventDefault();
                    alert('Số điện thoại không hợp lệ (10 chữ số, bắt đầu bằng 0).');
                    return;
                }
                // Disable nút để tránh submit 2 lần
                document.getElementById('submitBtn').textContent = 'Đang xử lý...';
                document.getElementById('submitBtn').disabled = true;
            });
        </script>
    </body>
</html>

