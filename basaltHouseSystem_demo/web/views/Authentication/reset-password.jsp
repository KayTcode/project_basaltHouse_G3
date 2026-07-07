<%-- 
    Document   : reset-password
    Created on : Jun 10, 2026, 8:40:05 PM
    Author     : KayT
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt lại mật khẩu - Coffeely</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&family=Inter:wght@300;400;500&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/auth/reset-password.css" rel="stylesheet">
</head>
<body>
<div class="auth-card">

    <!-- LEFT -->
    <div class="panel-left">
        <div class="brand-logo">☕ Coffeely</div>
        <div class="brand-sub">BasaltHouse</div>
        <div class="brand-divider"></div>
        <p class="brand-slogan">"Mật khẩu mới, khởi đầu mới."</p>
        <div class="step-badge">
            <span class="material-symbols-outlined" style="font-size:16px;">lock_reset</span>
            Bước 3 / 3 — Đặt mật khẩu mới
        </div>
    </div>

    <!-- RIGHT -->
    <div class="panel-right">
        <div class="form-wrapper">
            <div class="lock-icon-wrap">
                <div class="lock-icon-circle">
                    <span class="material-symbols-outlined">lock_reset</span>
                </div>
            </div>
            <div class="form-heading">
                <h2>Mật khẩu mới</h2>
                <p>Tạo mật khẩu mạnh để bảo vệ tài khoản của bạn.</p>
            </div>
            <div class="divider-line"></div>

            <%-- Alert lỗi --%>
            <c:if test="${not empty error}">
                <div class="alert-custom alert-error">
                    <span class="material-symbols-outlined alert-icon">error</span>
                    <span>${error}</span>
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/reset-password" method="post" id="resetForm">

                <!-- Mật khẩu mới -->
                <label class="field-label" for="newPassword">Mật khẩu mới</label>
                <div class="input-group-custom">
                    <span class="material-symbols-outlined input-icon">lock</span>
                    <input type="password" id="newPassword" name="newPassword"
                           class="form-control-custom"
                           placeholder="Tối thiểu 8 ký tự"
                           required autocomplete="new-password"
                           oninput="checkStrength(this.value); checkMatch()">
                    <button type="button" class="eye-toggle" onclick="toggleEye('newPassword','eye1')">
                        <span class="material-symbols-outlined" id="eye1">visibility</span>
                    </button>
                </div>
                <!-- Thanh độ mạnh -->
                <div class="strength-bars">
                    <div class="s-bar" id="sb1"></div>
                    <div class="s-bar" id="sb2"></div>
                    <div class="s-bar" id="sb3"></div>
                    <div class="s-bar" id="sb4"></div>
                </div>
                <div class="strength-label" id="slabel"></div>

                <!-- Requirements -->
                <ul class="req-list" style="margin-top:10px;">
                    <li id="req-len">
                        <span class="material-symbols-outlined">radio_button_unchecked</span>
                        Ít nhất 8 ký tự
                    </li>
                    <li id="req-upper">
                        <span class="material-symbols-outlined">radio_button_unchecked</span>
                        Có chữ hoa
                    </li>
                    <li id="req-num">
                        <span class="material-symbols-outlined">radio_button_unchecked</span>
                        Có chữ số
                    </li>
                </ul>

                <!-- Xác nhận mật khẩu -->
                <label class="field-label" for="confirmPassword">Nhập lại mật khẩu</label>
                <div class="input-group-custom">
                    <span class="material-symbols-outlined input-icon">lock_clock</span>
                    <input type="password" id="confirmPassword" name="confirmPassword"
                           class="form-control-custom"
                           placeholder="Nhập lại mật khẩu mới"
                           required autocomplete="new-password"
                           oninput="checkMatch()">
                    <button type="button" class="eye-toggle" onclick="toggleEye('confirmPassword','eye2')">
                        <span class="material-symbols-outlined" id="eye2">visibility</span>
                    </button>
                </div>
                <div id="matchMsg" style="font-size:12px;margin-top:-14px;margin-bottom:16px;"></div>

                <button type="submit" class="btn-primary-green">
                    <span class="material-symbols-outlined" style="font-size:18px;">check_circle</span>
                    Xác nhận đổi mật khẩu
                </button>
            </form>
        </div>
    </div>
</div>

<script>
    function toggleEye(inputId, iconId) {
        const inp  = document.getElementById(inputId);
        const icon = document.getElementById(iconId);
        if (inp.type === 'password') {
            inp.type = 'text';
            icon.textContent = 'visibility_off';
        } else {
            inp.type = 'password';
            icon.textContent = 'visibility';
        }
    }

    function checkStrength(pw) {
        let score = 0;
        const colors = ['#ef5350','#ffa726','#66bb6a','#2e7d32'];
        const labels = ['','Yếu','Trung bình','Mạnh','Rất mạnh'];

        if (pw.length >= 8)           { score++; setReq('req-len',   true); } else { setReq('req-len',   false); }
        if (/[A-Z]/.test(pw))         { score++; setReq('req-upper', true); } else { setReq('req-upper', false); }
        if (/[0-9]/.test(pw))         { score++; setReq('req-num',   true); } else { setReq('req-num',   false); }
        if (/[^A-Za-z0-9]/.test(pw))    score++;

        ['sb1','sb2','sb3','sb4'].forEach((id, i) => {
            document.getElementById(id).style.background = i < score ? colors[Math.max(0,score-1)] : '#dde8dd';
        });
        const lbl = document.getElementById('slabel');
        lbl.textContent = pw.length > 0 ? labels[score] : '';
        lbl.style.color = score > 0 ? colors[score-1] : '';
    }

    function setReq(id, valid) {
        const el   = document.getElementById(id);
        const icon = el.querySelector('.material-symbols-outlined');
        if (valid) { el.classList.add('valid');   icon.textContent = 'check_circle'; }
        else       { el.classList.remove('valid'); icon.textContent = 'radio_button_unchecked'; }
    }

    function checkMatch() {
        const pw  = document.getElementById('newPassword').value;
        const cpw = document.getElementById('confirmPassword').value;
        const msg = document.getElementById('matchMsg');
        if (!cpw) { msg.textContent = ''; return; }
        if (pw === cpw) {
            msg.textContent = '✓ Mật khẩu trùng khớp';
            msg.style.color = '#2e7d32';
        } else {
            msg.textContent = '✗ Mật khẩu chưa khớp';
            msg.style.color = '#c62828';
        }
    }

    document.getElementById('resetForm').addEventListener('submit', function(e) {
        const pw  = document.getElementById('newPassword').value;
        const cpw = document.getElementById('confirmPassword').value;
        if (pw !== cpw) {
            e.preventDefault();
            alert('Mật khẩu xác nhận không trùng khớp.');
        }
    });
</script>
</body>
</html>

