<%-- 
    Document   : verify-otp
    Created on : Jun 7, 2026, 2:24:14 AM
    Author     : KayT
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Xác thực OTP - Coffeely</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,600;1,400&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet">
    <link href="css/auth/verify-otp.css" rel="stylesheet">
</head>
<body>

<div class="card">
    <div class="card-accent"></div>
    <div class="card-body">

        <!-- Logo -->
        <div class="card-logo">
            <span class="logo-text">☕ Coffeely</span>
        </div>

        <!-- Icon -->
        <div class="otp-icon-wrapper">
            <div class="otp-icon">✉️</div>
        </div>

        <h1 class="card-title">Xác thực Email</h1>
        <div class="card-subtitle">
            Chúng tôi đã gửi mã gồm 6 chữ số đến<br>
            <span class="email-chip">${maskedEmail}</span>
        </div>

        <%-- === THÔNG BÁO LỖI (JSTL) === --%>
        <c:if test="${not empty error}">
            <div class="alert alert-error" id="alertBox">
                <span class="alert-icon">
                    <c:choose>
                        <c:when test="${errorType == 'EXPIRED_OTP'}">⏰</c:when>
                        <c:otherwise>⚠️</c:otherwise>
                    </c:choose>
                </span>
                <span>${error}</span>
            </div>
        </c:if>

        <%-- === THÔNG BÁO THÀNH CÔNG (khi gửi lại OTP) === --%>
        <c:if test="${not empty success}">
            <div class="alert alert-success">
                <span class="alert-icon">✅</span>
                <span>${success}</span>
            </div>
        </c:if>

        <!-- FORM XÁC THỰC OTP -->
        <form action="${pageContext.request.contextPath}/verify-otp" method="post" id="otpForm">

            <%-- Input ẩn chứa giá trị OTP thật để submit --%>
            <input type="hidden" name="otp" id="otpHidden" class="otp-hidden-input">

            <!-- 6 ô nhập OTP riêng lẻ (UI đẹp) -->
            <div class="otp-input-group" id="otpInputGroup">
                <input class="otp-digit" type="text" maxlength="1" inputmode="numeric" pattern="[0-9]" autocomplete="off">
                <input class="otp-digit" type="text" maxlength="1" inputmode="numeric" pattern="[0-9]" autocomplete="off">
                <input class="otp-digit" type="text" maxlength="1" inputmode="numeric" pattern="[0-9]" autocomplete="off">
                <input class="otp-digit" type="text" maxlength="1" inputmode="numeric" pattern="[0-9]" autocomplete="off">
                <input class="otp-digit" type="text" maxlength="1" inputmode="numeric" pattern="[0-9]" autocomplete="off">
                <input class="otp-digit" type="text" maxlength="1" inputmode="numeric" pattern="[0-9]" autocomplete="off">
            </div>

            <!-- Đồng hồ đếm ngược 5 phút -->
            <div class="otp-timer">
                Mã hết hạn sau: <span class="timer-count" id="timerDisplay">05:00</span>
            </div>

            <button type="submit" class="btn-verify" id="submitBtn">
                Xác nhận →
            </button>
        </form>

        <!-- Nút gửi lại mã (form riêng) -->
        <div class="resend-section">
            <p>Không nhận được mã?</p>
            <form action="${pageContext.request.contextPath}/verify-otp" method="post" style="display:inline;">
                <input type="hidden" name="action" value="resend">
                <button type="submit" class="btn-resend" id="resendBtn" disabled>
                    Gửi lại mã (<span id="resendCountdown">60</span>s)
                </button>
            </form>
        </div>

        <!-- Quay lại đăng ký -->
        <a href="${pageContext.request.contextPath}/register" class="back-link">
            ← Quay lại đăng ký
        </a>

    </div>
</div>

<script>
    // =====================================================================
    // 1. LOGIC 6 Ô NHẬP OTP
    // =====================================================================
    const digits       = document.querySelectorAll('.otp-digit');
    const otpHidden    = document.getElementById('otpHidden');
    const submitBtn    = document.getElementById('submitBtn');
    const otpForm      = document.getElementById('otpForm');
    const alertBox     = document.getElementById('alertBox');

    // Xử lý khi nhập vào từng ô
    digits.forEach((input, idx) => {
        input.addEventListener('input', function(e) {
            // Chỉ cho phép số
            this.value = this.value.replace(/[^0-9]/g, '');

            if (this.value) {
                this.classList.add('filled');
                this.classList.remove('error');
                // Tự động chuyển sang ô tiếp theo
                if (idx < digits.length - 1) {
                    digits[idx + 1].focus();
                }
            } else {
                this.classList.remove('filled');
            }
            syncHiddenInput();
        });

        // Xử lý phím Backspace
        input.addEventListener('keydown', function(e) {
            if (e.key === 'Backspace' && !this.value && idx > 0) {
                digits[idx - 1].focus();
                digits[idx - 1].value = '';
                digits[idx - 1].classList.remove('filled');
                syncHiddenInput();
            }
            // Enter để submit nếu đủ 6 số
            if (e.key === 'Enter') {
                otpForm.dispatchEvent(new Event('submit'));
            }
        });

        // Xử lý paste (dán mã OTP từ clipboard)
        input.addEventListener('paste', function(e) {
            e.preventDefault();
            const pasted = e.clipboardData.getData('text').replace(/[^0-9]/g, '');
            if (pasted.length === 6) {
                digits.forEach((d, i) => {
                    d.value = pasted[i] || '';
                    d.classList.toggle('filled', !!d.value);
                });
                digits[5].focus();
                syncHiddenInput();
            }
        });
    });

    // Đồng bộ giá trị từ 6 ô vào hidden input
    function syncHiddenInput() {
        let otp = '';
        digits.forEach(d => otp += d.value);
        otpHidden.value = otp;
        submitBtn.disabled = otp.length < 6;
    }

    // Khởi tạo: disable nút nếu chưa đủ 6 số
    submitBtn.disabled = true;

    // Validate trước khi submit
    otpForm.addEventListener('submit', function(e) {
        const otp = otpHidden.value;
        if (otp.length !== 6) {
            e.preventDefault();
            digits.forEach(d => d.classList.add('error'));
            return;
        }
        submitBtn.textContent = 'Đang xác thực...';
        submitBtn.disabled = true;
    });

    // Focus ô đầu tiên khi load trang
    window.addEventListener('load', () => {
        digits[0].focus();

        // Highlight ô lỗi nếu có thông báo lỗi OTP sai
        <c:if test="${errorType == 'WRONG_OTP'}">
            digits.forEach(d => { d.classList.add('error'); d.value = ''; });
            digits[0].focus();
        </c:if>
    });

    // =====================================================================
    // 2. ĐỒNG HỒ ĐẾM NGƯỢC 5 PHÚT
    // =====================================================================
    let totalSeconds = 5 * 60;
    const timerDisplay = document.getElementById('timerDisplay');

    const countdownInterval = setInterval(function () {
        totalSeconds--;
        const min = String(Math.floor(totalSeconds / 60)).padStart(2, '0');
        const sec = String(totalSeconds % 60).padStart(2, '0');
        timerDisplay.textContent = min + ':' + sec;

        // Đổi màu đỏ khi còn dưới 1 phút
        if (totalSeconds <= 60) {
            timerDisplay.classList.add('urgent');
        }

        if (totalSeconds <= 0) {
            clearInterval(countdownInterval);
            timerDisplay.textContent = 'Hết hạn';
            submitBtn.disabled = true;
        }
    }, 1000);

    // =====================================================================
    // 3. ĐẾM NGƯỢC NÚT "GỬI LẠI MÃ" (60 giây)
    // =====================================================================
    let resendSeconds = 60;
    const resendBtn          = document.getElementById('resendBtn');
    const resendCountdownEl  = document.getElementById('resendCountdown');

    const resendInterval = setInterval(function () {
        resendSeconds--;
        resendCountdownEl.textContent = resendSeconds;
        if (resendSeconds <= 0) {
            clearInterval(resendInterval);
            resendBtn.disabled = false;
            resendBtn.innerHTML = 'Gửi lại mã OTP';
        }
    }, 1000);
</script>
</body>
</html>

