<%-- 
    Document   : HomePage
    Created on : Jun 2, 2026, 8:12:18 PM
    Author     : admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<%
    String _initials = "?";
    Object _userObj = session.getAttribute("currentUser");

    if (_userObj != null) {
        dto.UserLoginDTO _u = (dto.UserLoginDTO) _userObj;

        String _displayName = _u.getFullName();

        if (_displayName == null || _displayName.trim().isEmpty()) {
            String _email = _u.getEmail();
            if (_email != null && !_email.trim().isEmpty()) {
                int _atIndex = _email.indexOf("@");
                _displayName = _atIndex > 0 ? _email.substring(0, _atIndex) : _email;
            }
        }

        if (_displayName != null && !_displayName.trim().isEmpty()) {
            String[] _parts = _displayName.trim().split("\\s+");
            if (_parts.length >= 2) {
                _initials = String.valueOf(_parts[0].charAt(0)).toUpperCase()
                        + String.valueOf(_parts[_parts.length - 1].charAt(0)).toUpperCase();
            } else {
                _initials = _displayName.substring(0, Math.min(2, _displayName.length())).toUpperCase();
            }
        }
    }

    pageContext.setAttribute("initials", _initials);
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title><c:out value="${empty pageTitle ? 'BasaltHouse - Good Coffee, Good Mood' : pageTitle}"/></title>
        <c:if test="${not empty pageDescription}">
            <meta name="description" content="<c:out value='${pageDescription}'/>">
        </c:if>
        <!-- Bootstrap 5 CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700;900&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
        <!-- Material Symbols Outlined -->
        <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/HomePageCss/HomePage.css?v=20260709-1" rel="stylesheet">
        <c:if test="${not empty pageStylesheet and not pageStylesheetAfterTheme}">
            <link href="${pageContext.request.contextPath}${pageStylesheet}" rel="stylesheet">
        </c:if>
        <link href="${pageContext.request.contextPath}/css/Customer/CustomerTheme.css?v=20260709-2" rel="stylesheet">
        <c:if test="${not empty pageStylesheet and pageStylesheetAfterTheme}">
            <link href="${pageContext.request.contextPath}${pageStylesheet}" rel="stylesheet">
        </c:if>
    </head>
    <body>

        <!-- TopNavBar -->
        <header class="sticky-top">
            <nav class="navbar navbar-expand-md navbar-light navbar-coffeely py-3">
                <div class="container">
                    <a class="navbar-brand navbar-brand-coffeely" href="${pageContext.request.contextPath}/home">BasaltHouse</a>

                    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#coffeelyNav" aria-controls="coffeelyNav" aria-expanded="false" aria-label="Toggle navigation">
                        <span class="navbar-toggler-icon"></span>
                    </button>

                    <div class="collapse navbar-collapse justify-content-between" id="coffeelyNav">
                        <ul class="navbar-nav mx-auto mb-2 mb-lg-0">
                            <li class="nav-item">
                                <a class="nav-link nav-link-coffeely active" href="${pageContext.request.contextPath}/category">Menu</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link nav-link-coffeely" href="${pageContext.request.contextPath}/benefit">Ưu đãi</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link nav-link-coffeely" href="${pageContext.request.contextPath}/about-us">Về chúng tôi</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link nav-link-coffeely" href="contact">Liên hệ</a>
                            </li>
                        </ul>

                        <div class="d-flex align-items-center gap-2">
                            <form action="category"
                                  method="get"
                                  class="d-flex align-items-center"
                                  role="search">
                                <input class="form-control form-control-sm"
                                       type="search"
                                       name="keyword"
                                       value="${fn:escapeXml(currentKeyword)}"
                                       placeholder="Tìm kiếm"
                                       aria-label="Tìm kiếm">
                                <button class="btn-nav-icon" type="submit" title="Tìm kiếm" aria-label="Tìm kiếm">
                                    <span class="material-symbols-outlined">search</span>
                                </button>
                            </form>
                            <c:set var="headerCartQty" value="0" />
                            <c:if test="${not empty sessionScope.cart}">
                                <c:forEach var="item" items="${sessionScope.cart.values()}">
                                    <c:set var="headerCartQty" value="${headerCartQty + item.quantity}" />
                                </c:forEach>
                            </c:if>
                            <a href="${pageContext.request.contextPath}/Cart"
                               class="btn-nav-icon" title="Giỏ hàng" style="text-decoration:none;position:relative;">
                                <span class="material-symbols-outlined">shopping_cart</span>
                                <c:if test="${headerCartQty > 0}">
                                    <span class="badge-cart cart-badge visible" id="navCartBadge">${headerCartQty}</span>
                                </c:if>
                            </a>

                            <%-- ── Bell thông báo voucher (chỉ hiện khi là Customer) ──── --%>
                            <c:if test="${not empty sessionScope.currentUser and sessionScope.currentUser.roleId == 2}">
                                <button type="button" class="btn-nav-icon bell-btn" id="bellBtn"
                                        title="Mã giảm giá của bạn" style="position:relative;background:none;border:none;cursor:pointer;">
                                    <span class="material-symbols-outlined">notifications</span>
                                    <c:if test="${voucherCount > 0}">
                                        <span class="bell-badge" id="bellBadge">${voucherCount}</span>
                                    </c:if>
                                </button>
                            </c:if>

                            <%-- ── Khu vực tài khoản: 2 trạng thái ──── --%>
                            <c:choose>

                                <%-- ★ GUEST: chưa đăng nhập --%>
                                <c:when test="${empty sessionScope.currentUser}">
                                    <a href="${pageContext.request.contextPath}/login"
                                       class="btn-login-header"
                                       title="Đăng nhập">
                                        <span class="material-symbols-outlined">login</span>
                                        <span class="btn-login-text">Đăng nhập</span>
                                    </a>
                                </c:when>

                                <%-- ★ LOGGED IN: đã đăng nhập --%>
                                <c:otherwise>
                                    <c:set var="user" value="${sessionScope.currentUser}"/>
                                    <c:set var="displayName" value="${user.fullName}"/>
                                    <c:if test="${empty displayName}">
                                        <c:set var="displayName" value="${user.username}"/>
                                    </c:if>
                                    <c:if test="${empty displayName}">
                                        <c:set var="displayName" value="${fn:substringBefore(user.email, '@')}"/>
                                    </c:if>

                                    <div class="user-menu-wrapper" id="userMenuWrapper">

                                     

                                        <%-- Trigger button --%>
                                        <button class="user-trigger"
                                                id="userMenuTrigger"
                                                type="button"
                                                aria-haspopup="true"
                                                aria-expanded="false"
                                                aria-controls="userDropdown">

                                            <%-- Avatar: ảnh thật hoặc initials --%>
                                            <c:choose>
                                                <c:when test="${not empty user.avatarUrl}">
                                                    <img src="<c:out value='${user.avatarUrl}'/>"
                                                         alt="Avatar"
                                                         class="user-avatar user-avatar--img">
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="user-avatar user-avatar--initials"
                                                          aria-hidden="true">
                                                        <c:out value="${initials}"/>
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>

                                            <%-- Tên + role (ẩn trên mobile) --%>
                                            <span class="user-trigger-info">
                                                <span class="user-trigger-name">
                                                    <c:out value="${displayName}"/>
                                                </span>
                                                <span class="user-trigger-role">
                                                    <c:choose>
                                                        <c:when test="${user.roleName eq 'Admin'}">Quản trị viên</c:when>
                                                        <c:when test="${user.roleName eq 'Staff'}">Nhân viên</c:when>
                                                        <c:when test="${user.roleName eq 'Shipper'}">Shipper</c:when>
                                                        <c:when test="${user.roleName eq 'Cashier'}">Thu ngân</c:when>
                                                        <c:otherwise>Khách hàng</c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </span>

                                            <span class="material-symbols-outlined chevron-icon"
                                                  aria-hidden="true">expand_more</span>
                                        </button>

                                        <%-- ── Dropdown panel ───────────── --%>
                                        <div class="user-dropdown"
                                             id="userDropdown"
                                             role="menu"
                                             aria-hidden="true">

                                            <%-- Header thông tin user --%>
                                            <div class="dropdown-user-header">
                                                <c:choose>
                                                    <c:when test="${not empty user.avatarUrl}">
                                                        <img src="<c:out value='${user.avatarUrl}'/>"
                                                             alt="Avatar"
                                                             class="dropdown-avatar dropdown-avatar--img">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="dropdown-avatar dropdown-avatar--initials"
                                                              aria-hidden="true">
                                                            <c:out value="${initials}"/>
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                                <div class="dropdown-user-detail">
                                                    <p class="dropdown-fullname">
                                                        <c:out value="${displayName}"/>
                                                    </p>
                                                    <p class="dropdown-email">
                                                        <c:out value="${user.email}"/>
                                                    </p>
                                                </div>
                                            </div>

                                            <div class="dropdown-divider"></div>

                                            <%-- Menu items theo Role --%>
                                            <c:choose>
                                                <c:when test="${user.roleName eq 'Admin'}">
                                                    <a href="${pageContext.request.contextPath}/admin/dashboard"
                                                       class="dropdown-item-link" role="menuitem">
                                                        <span class="material-symbols-outlined">dashboard</span>
                                                        Dashboard Admin
                                                    </a>
                                                    <a href="${pageContext.request.contextPath}/admin/customers"
                                                        class="dropdown-item-link" role="menuitem">
                                                         <span class="material-symbols-outlined">manage_accounts</span>
                                                         Quản lý người dùng
                                                     </a>
                                                    <a href="${pageContext.request.contextPath}/admin/products"
                                                       class="dropdown-item-link" role="menuitem">
                                                        <span class="material-symbols-outlined">inventory_2</span>
                                                        Quản lý sản phẩm
                                                    </a>
                                                </c:when>
                                                <c:when test="${user.roleName eq 'Staff'}">
                                                    <a href="${pageContext.request.contextPath}/staff/dashboard"
                                                       class="dropdown-item-link" role="menuitem">
                                                        <span class="material-symbols-outlined">dashboard</span>
                                                        Dashboard
                                                    </a>
                                                    <a href="${pageContext.request.contextPath}/staff/orders"
                                                       class="dropdown-item-link" role="menuitem">
                                                        <span class="material-symbols-outlined">receipt_long</span>
                                                        Quản lý đơn hàng
                                                    </a>
                                                </c:when>
                                                <c:when test="${user.roleName eq 'Shipper'}">
                                                    <a href="${pageContext.request.contextPath}/shipper/dashboard"
                                                       class="dropdown-item-link" role="menuitem">
                                                        <span class="material-symbols-outlined">dashboard</span>
                                                        Dashboard
                                                    </a>
                                                    <a href="${pageContext.request.contextPath}/shipper/deliveries"
                                                       class="dropdown-item-link" role="menuitem">
                                                        <span class="material-symbols-outlined">local_shipping</span>
                                                        Đơn cần giao hôm nay
                                                    </a>
                                                </c:when>
                                                <c:when test="${user.roleName eq 'Cashier'}">
                                                    <a href="${pageContext.request.contextPath}/cashier/dashboard"
                                                       class="dropdown-item-link" role="menuitem">
                                                        <span class="material-symbols-outlined">dashboard</span>
                                                        Dashboard
                                                    </a>
                                                    <a href="${pageContext.request.contextPath}/cashier/pos"
                                                       class="dropdown-item-link" role="menuitem">
                                                        <span class="material-symbols-outlined">point_of_sale</span>
                                                        Thanh toán tại quầy
                                                    </a>
                                                </c:when>
                                                <c:otherwise>
                                                    <%-- Customer --%>
                                                    <a href="${pageContext.request.contextPath}/profile"
                                                       class="dropdown-item-link" role="menuitem">
                                                        <span class="material-symbols-outlined">person</span>
                                                        Thông tin tài khoản
                                                    </a>
                                                    <a href="${pageContext.request.contextPath}/my-orders"
                                                       class="dropdown-item-link" role="menuitem">
                                                        <span class="material-symbols-outlined">shopping_bag</span>
                                                        Đơn hàng
                                                    </a>
                                                    <a href="${pageContext.request.contextPath}/voucher"
                                                       class="dropdown-item-link" role="menuitem">
                                                        <span class="material-symbols-outlined">local_offer</span>
                                                        Mã giảm giá
                                                    </a>
                                                    <a href="${pageContext.request.contextPath}/membership"
                                                       class="dropdown-item-link" role="menuitem">
                                                        <span class="material-symbols-outlined">workspace_premium</span>
                                                        Membership
                                                    </a>
                                                </c:otherwise>
                                            </c:choose>

                                            <div class="dropdown-divider"></div>

                                            <%-- Đăng xuất — POST để tránh CSRF --%>
                                            <form method="POST"
                                                  action="${pageContext.request.contextPath}/logout"
                                                  style="margin:0;padding:0;">
                                                <button type="submit"
                                                        class="dropdown-item-link dropdown-item-link--danger"
                                                        role="menuitem">
                                                    <span class="material-symbols-outlined">logout</span>
                                                    Đăng xuất
                                                </button>
                                            </form>

                                        </div>
                                        <%-- end dropdown --%>

                                    </div>
                                    <%-- end user-menu-wrapper --%>

                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </nav>
        </header>
        <%-- ══ POPUP Voucher ══ --%>
        <c:if test="${not empty sessionScope.currentUser and sessionScope.currentUser.roleId == 2}">
        <div class="voucher-popup-overlay" id="voucherOverlay" onclick="closeBellPopup(event)"></div>
        <div class="voucher-popup" id="voucherPopup">
            <div class="voucher-popup-header">
                <span class="voucher-popup-title">🎁 Mã giảm giá của bạn</span>
                <button class="voucher-popup-close" onclick="closeBellPopupBtn()" type="button">&times;</button>
            </div>
            <div class="voucher-popup-body">
                <c:choose>
                    <c:when test="${empty customerVouchers}">
                        <div class="voucher-empty">
                            <span class="material-symbols-outlined" style="font-size:40px;color:#d1d5db;">local_offer</span>
                            <p>Bạn chưa có mã giảm giá nào.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="v" items="${customerVouchers}">
                            <div class="voucher-card">
                                <div class="voucher-card-left">
                                    <span class="voucher-icon">🎟️</span>
                                </div>
                                <div class="voucher-card-body">
                                    <span class="voucher-code">${v.code}</span>
                                    <span class="voucher-desc">${not empty v.description ? v.description : 'Mã giảm giá'}</span>
                                    <span class="voucher-value">${v.discountValueFormatted}</span>
                                    <c:if test="${not empty v.endDateFormatted}">
                                        <span class="voucher-exp">HSD: ${v.endDateFormatted}</span>
                                    </c:if>
                                </div>
                                <div class="voucher-card-copy">
                                    <button class="voucher-copy-btn" onclick="copyVoucherCode('${v.code}', this)" type="button"
                                            title="Copy mã">
                                        <span class="material-symbols-outlined" style="font-size:16px;">content_copy</span>
                                    </button>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        </c:if>

        <style>
        /* ── Bell Button ─────────────────────────────────────────── */
        .bell-btn { position: relative; }
        .bell-badge {
            position: absolute;
            top: -4px; right: -4px;
            background: #ef4444;
            color: #fff;
            font-size: 10px;
            font-weight: 700;
            min-width: 17px;
            height: 17px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            border: 2px solid #fff;
            pointer-events: none;
        }
        /* ── Overlay ─────────────────────────────────────────────── */
        .voucher-popup-overlay {
            display: none;
            position: fixed;
            inset: 0;
            z-index: 9998;
        }
        .voucher-popup-overlay.open { display: block; }
        /* ── Popup box ───────────────────────────────────────────── */
        .voucher-popup {
            display: none;
            position: fixed;
            top: 70px; right: 16px;
            width: 340px;
            max-height: 480px;
            background: #fff;
            border-radius: 16px;
            box-shadow: 0 8px 40px rgba(0,0,0,.18);
            z-index: 9999;
            overflow: hidden;
            flex-direction: column;
            animation: fadeSlideDown .22s ease;
        }
        .voucher-popup.open { display: flex; }
        @keyframes fadeSlideDown {
            from { opacity:0; transform:translateY(-12px); }
            to   { opacity:1; transform:translateY(0); }
        }
        .voucher-popup-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 14px 18px 12px;
            border-bottom: 1px solid #f1f5f9;
            background: linear-gradient(135deg,#7c3aed,#4f46e5);
            color: #fff;
        }
        .voucher-popup-title {
            font-weight: 700;
            font-size: 15px;
            letter-spacing: .02em;
        }
        .voucher-popup-close {
            background: rgba(255,255,255,.2);
            border: none;
            color: #fff;
            width: 26px; height: 26px;
            border-radius: 50%;
            font-size: 18px;
            line-height: 1;
            cursor: pointer;
            display: flex; align-items: center; justify-content: center;
            transition: background .2s;
        }
        .voucher-popup-close:hover { background: rgba(255,255,255,.35); }
        .voucher-popup-body {
            overflow-y: auto;
            flex: 1;
            padding: 10px 0;
        }
        .voucher-empty {
            display: flex; flex-direction: column;
            align-items: center; justify-content: center;
            padding: 30px 20px;
            color: #9ca3af;
            font-size: 14px;
            gap: 8px;
        }
        /* ── Voucher card ────────────────────────────────────────── */
        .voucher-card {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 10px 16px;
            margin: 4px 10px;
            background: linear-gradient(135deg,#f5f3ff,#ede9fe);
            border: 1.5px dashed #c4b5fd;
            border-radius: 12px;
            transition: box-shadow .2s;
        }
        .voucher-card:hover { box-shadow: 0 2px 12px rgba(124,58,237,.15); }
        .voucher-icon { font-size: 22px; flex-shrink: 0; }
        .voucher-card-body {
            flex: 1;
            display: flex; flex-direction: column; gap: 2px;
            min-width: 0;
        }
        .voucher-code {
            font-weight: 700;
            font-size: 13px;
            color: #4f46e5;
            letter-spacing: .05em;
            font-family: 'Courier New', monospace;
        }
        .voucher-desc {
            font-size: 11px;
            color: #6b7280;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        .voucher-value {
            font-size: 13px;
            font-weight: 600;
            color: #16a34a;
        }
        .voucher-exp {
            font-size: 10px;
            color: #f59e0b;
        }
        .voucher-card-copy { flex-shrink: 0; }
        .voucher-copy-btn {
            background: #ede9fe;
            border: none;
            border-radius: 8px;
            padding: 5px;
            cursor: pointer;
            color: #7c3aed;
            display: flex; align-items: center; justify-content: center;
            transition: background .2s, transform .1s;
        }
        .voucher-copy-btn:hover { background: #c4b5fd; transform: scale(1.1); }
        </style>

        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const trigger = document.getElementById("userMenuTrigger");
                const dropdown = document.getElementById("userDropdown");

                /* ── Bell popup ─────────────────────────────── */
                const bellBtn     = document.getElementById('bellBtn');
                const voucherPopup  = document.getElementById('voucherPopup');
                const voucherOverlay= document.getElementById('voucherOverlay');

                if (bellBtn && voucherPopup) {
                    bellBtn.addEventListener('click', function(e) {
                        e.stopPropagation();
                        const isOpen = voucherPopup.classList.toggle('open');
                        voucherOverlay && voucherOverlay.classList.toggle('open', isOpen);
                    });
                }

                if (!trigger || !dropdown) {
                    return;
                }

                function setMenuOpen(open) {
                    dropdown.classList.toggle("is-open", open);
                    trigger.setAttribute("aria-expanded", String(open));
                    dropdown.setAttribute("aria-hidden", String(!open));
                }

                trigger.addEventListener("click", function (event) {
                    event.stopPropagation();
                    setMenuOpen(trigger.getAttribute("aria-expanded") !== "true");
                });

                document.addEventListener("click", function (event) {
                    if (!dropdown.contains(event.target) && !trigger.contains(event.target)) {
                        setMenuOpen(false);
                    }
                });

                document.addEventListener("keydown", function (event) {
                    if (event.key === "Escape") {
                        setMenuOpen(false);
                        trigger.focus();
                    }
                });
            });
        </script>
        <script>
            function closeBellPopup(e) {
                if (e.target.id === 'voucherOverlay') closeBellPopupBtn();
            }
            function closeBellPopupBtn() {
                const p = document.getElementById('voucherPopup');
                const o = document.getElementById('voucherOverlay');
                if (p) p.classList.remove('open');
                if (o) o.classList.remove('open');
            }
            function copyVoucherCode(code, btn) {
                navigator.clipboard.writeText(code).then(function() {
                    const original = btn.innerHTML;
                    btn.innerHTML = '<span class="material-symbols-outlined" style="font-size:16px;color:#16a34a;">check</span>';
                    setTimeout(function() { btn.innerHTML = original; }, 1800);
                }).catch(function() {
                    // fallback
                    const ta = document.createElement('textarea');
                    ta.value = code; document.body.appendChild(ta);
                    ta.select(); document.execCommand('copy');
                    document.body.removeChild(ta);
                });
            }
            document.addEventListener('keydown', function(e) {
                if (e.key === 'Escape') closeBellPopupBtn();
            });
        </script>
