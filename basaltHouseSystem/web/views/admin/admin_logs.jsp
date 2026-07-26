<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="java.time.LocalDateTime, java.time.format.DateTimeFormatter" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thông báo &amp; Nhật ký – Basalt House Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/admin_common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/admin_logs.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<jsp:include page="header.jsp"/>

<div class="app-container">
    <jsp:include page="sidebar.jsp"/>

    <main class="main-content">

        <%-- ══ PAGE HEADER ══ --%>
        <div class="logs-page-header">
            <h2>🔔 Thông Báo &amp; Nhật Ký Hoạt Động</h2>
            <p>Quản lý hệ thống thông báo và xem lịch sử thao tác của tài khoản trong hệ thống.</p>
        </div>

        <%-- ══ TAB SWITCHER ══ --%>
        <div class="logs-tab-bar">
            <a href="${pageContext.request.contextPath}/admin/logs?tab=notifications"
               class="tab-btn ${tab == 'notifications' ? 'active' : ''}">
                <i class="fa-solid fa-bell"></i>
                Thông Báo
                <span class="tab-badge">${notiStats.total}</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/logs?tab=activitylogs"
               class="tab-btn ${tab == 'activitylogs' ? 'active' : ''}">
                <i class="fa-solid fa-clock-rotate-left"></i>
                Nhật Ký Hoạt Động
                <span class="tab-badge">${logStats.total}</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/logs?tab=sendvoucher"
               class="tab-btn ${tab == 'sendvoucher' ? 'active' : ''}">
                <i class="fa-solid fa-gift"></i>
                Gửi Voucher &amp; Thông Báo
            </a>
        </div>

        <%-- ══════════════════════════════════════════ --%>
        <%-- ══  TAB 1: THÔNG BÁO  ══                  --%>
        <%-- ══════════════════════════════════════════ --%>
        <c:if test="${tab == 'notifications'}">

            <%-- KPI CARDS --%>
            <div class="logs-kpi-grid">
                <div class="logs-kpi-card kpi-blue">
                    <div class="logs-kpi-header">
                        <span class="logs-kpi-title">Tổng thông báo</span>
                        <span class="logs-kpi-icon">🔔</span>
                    </div>
                    <div class="logs-kpi-value">${notiStats.total}</div>
                    <div class="logs-kpi-desc">Toàn bộ hệ thống</div>
                </div>
                <div class="logs-kpi-card kpi-green">
                    <div class="logs-kpi-header">
                        <span class="logs-kpi-title">Đang hiển thị</span>
                        <span class="logs-kpi-icon">✅</span>
                    </div>
                    <div class="logs-kpi-value">${notiStats.active}</div>
                    <div class="logs-kpi-desc">Chưa bị xóa</div>
                </div>
                <div class="logs-kpi-card kpi-red">
                    <div class="logs-kpi-header">
                        <span class="logs-kpi-title">Đã xóa mềm</span>
                        <span class="logs-kpi-icon">🗑️</span>
                    </div>
                    <div class="logs-kpi-value">${notiStats.deleted}</div>
                    <div class="logs-kpi-desc">Có thể khôi phục</div>
                </div>
                <div class="logs-kpi-card kpi-orange">
                    <div class="logs-kpi-header">
                        <span class="logs-kpi-title">Hôm nay</span>
                        <span class="logs-kpi-icon">📅</span>
                    </div>
                    <div class="logs-kpi-value">${notiStats.todayCount}</div>
                    <div class="logs-kpi-desc">Thông báo mới ngày hôm nay</div>
                </div>
            </div>

            <%-- FILTER BAR --%>
            <form method="GET" action="${pageContext.request.contextPath}/admin/logs"
                  id="notiFilterForm" class="logs-filter-bar">
                <input type="hidden" name="tab" value="notifications">

                <span class="logs-filter-label">Bộ lọc:</span>

                <select name="deleted" class="logs-filter-select"
                        onchange="document.getElementById('notiFilterForm').submit()">
                    <option value="">-- Tất cả trạng thái --</option>
                    <option value="active"   ${param.deleted == 'active'   ? 'selected' : ''}>Đang hiển thị</option>
                    <option value="deleted"  ${param.deleted == 'deleted'  ? 'selected' : ''}>Đã xóa mềm</option>
                </select>

                <div class="logs-search-wrapper">
                    <i class="fa-solid fa-magnifying-glass search-icon"></i>
                    <input type="text" name="search" class="logs-search-input"
                           placeholder="Tìm tiêu đề, nội dung, tên tài khoản..."
                           value="${param.search}">
                </div>

                <button type="submit" class="logs-filter-btn submit">
                    <i class="fa-solid fa-filter"></i> Lọc
                </button>
                <a href="${pageContext.request.contextPath}/admin/logs?tab=notifications"
                   class="logs-filter-btn clear">
                    <i class="fa-solid fa-xmark"></i> Xóa lọc
                </a>
            </form>

            <%-- NOTIFICATION TABLE --%>
            <div class="logs-table-container">
                <div class="logs-table-head-bar">
                    <span class="logs-table-title">
                        <i class="fa-solid fa-list"></i>
                        Danh sách thông báo
                    </span>
                    <span class="logs-table-count">${totalRecords} thông báo</span>
                </div>
                <table class="logs-table">
                    <thead>
                        <tr>
                            <th class="th-w-5">#</th>
                            <th class="th-w-16">Tài khoản</th>
                            <th class="th-w-35">Tiêu đề &amp; Nội dung</th>
                            <th class="th-w-12 cell-center">Trạng thái</th>
                            <th class="th-w-15">Thời gian</th>
                            <th class="th-w-17 cell-center">Thao tác</th>
                        </tr>
                    </thead>

                    <tbody>
                        <c:forEach var="n" items="${notifications}">
                            <tr>
                                <td class="cell-id">#${n.notificationId}</td>
                                <td>
                                    <div class="cell-user">
                                        <span class="username">${n.username}</span>
                                        <span class="email-sub">${n.email}</span>
                                    </div>
                                </td>
                                <td>
                                    <div class="cell-title-wrap">
                                        <span class="noti-title">${n.title}</span>
                                        <span class="noti-msg">${n.message}</span>
                                    </div>
                                </td>
                                <td class="cell-center">

                                    <c:choose>
                                        <c:when test="${n.isDeleted}">
                                            <span class="badge badge-deleted">🗑️ Đã xóa</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-active">✅ Hiển thị</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="cell-date">
                                    <%
                                        Object rawNotiDate = ((java.util.Map<?,?>)pageContext.findAttribute("n")).get("createdAt");
                                        if (rawNotiDate instanceof LocalDateTime) {
                                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                                            out.print(((LocalDateTime)rawNotiDate).format(dtf));
                                        } else {
                                            out.print("—");
                                        }
                                    %>
                                </td>
                                <td>
                                    <div class="action-cell">
                                        <%-- Nút xem chi tiết --%>
                                        <button type="button"
                                                class="act-btn"
                                                title="Xem chi tiết"
                                                onclick='openNotiModal(
                                                    ${n.notificationId},
                                                    "${fn:escapeXml(n.username)}",
                                                    "${fn:escapeXml(n.email)}",
                                                    "${fn:escapeXml(n.title)}",
                                                    `${fn:escapeXml(n.message)}`,
                                                    ${n.isDeleted}
                                                )'>
                                            <i class="fa-solid fa-eye"></i>
                                        </button>

                                        <%-- Soft-delete / Restore --%>
                                        <c:choose>
                                            <c:when test="${!n.isDeleted}">
                                                <form method="POST"
                                                      action="${pageContext.request.contextPath}/admin/logs"
                                                      class="form-inline"
                                                      onsubmit="return confirm('Xóa mềm thông báo này?')">
                                                    <input type="hidden" name="action"  value="deleteNoti">
                                                    <input type="hidden" name="id"      value="${n.notificationId}">
                                                    <input type="hidden" name="tab"     value="notifications">
                                                    <input type="hidden" name="search"  value="${fn:escapeXml(param.search)}">
                                                    <input type="hidden" name="deleted" value="${fn:escapeXml(param.deleted)}">
                                                    <input type="hidden" name="page"    value="${currentPage}">
                                                    <button type="submit" class="act-btn delete" title="Xóa mềm">
                                                        <i class="fa-solid fa-trash"></i>
                                                    </button>
                                                </form>
                                            </c:when>
                                            <c:otherwise>
                                                <form method="POST"
                                                      action="${pageContext.request.contextPath}/admin/logs"
                                                      class="form-inline">
                                                    <input type="hidden" name="action"  value="restoreNoti">
                                                    <input type="hidden" name="id"      value="${n.notificationId}">
                                                    <input type="hidden" name="tab"     value="notifications">
                                                    <input type="hidden" name="search"  value="${fn:escapeXml(param.search)}">
                                                    <input type="hidden" name="deleted" value="${fn:escapeXml(param.deleted)}">
                                                    <input type="hidden" name="page"    value="${currentPage}">
                                                    <button type="submit" class="act-btn restore" title="Khôi phục">
                                                        <i class="fa-solid fa-rotate-left"></i>
                                                    </button>
                                                </form>
                                                <form method="POST"
                                                      action="${pageContext.request.contextPath}/admin/logs"
                                                      class="form-inline"
                                                      onsubmit="return confirm('Xóa vĩnh viễn thông báo này? Không thể hoàn tác!')">

                                                    <input type="hidden" name="action"  value="hardDeleteNoti">
                                                    <input type="hidden" name="id"      value="${n.notificationId}">
                                                    <input type="hidden" name="tab"     value="notifications">
                                                    <input type="hidden" name="search"  value="${fn:escapeXml(param.search)}">
                                                    <input type="hidden" name="deleted" value="${fn:escapeXml(param.deleted)}">
                                                    <input type="hidden" name="page"    value="${currentPage}">
                                                    <button type="submit" class="act-btn danger" title="Xóa vĩnh viễn">
                                                        <i class="fa-solid fa-fire"></i>
                                                    </button>
                                                </form>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty notifications}">
                            <tr>
                                <td colspan="6">
                                    <div class="logs-empty">
                                        <div class="empty-icon">🔔</div>
                                        <p>Không tìm thấy thông báo nào phù hợp.</p>
                                    </div>
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <%-- PAGINATION --%>
            <div class="logs-pagination">
                <span class="pag-info-txt">
                    Trang <strong>${currentPage}</strong> / <strong>${totalPages}</strong>
                    &nbsp;•&nbsp; Tổng <strong>${totalRecords}</strong> bản ghi
                </span>
                <div class="pag-btns">
                    <button class="pag-b" ${currentPage <= 1 ? 'disabled' : ''}
                            onclick="goNotiPage(${currentPage - 1})">
                        <i class="fa-solid fa-angle-left"></i>
                    </button>
                    <c:forEach var="p" begin="1" end="${totalPages}">
                        <button class="pag-b ${p == currentPage ? 'active' : ''}"
                                onclick="goNotiPage(${p})">${p}</button>
                    </c:forEach>
                    <button class="pag-b" ${currentPage >= totalPages ? 'disabled' : ''}
                            onclick="goNotiPage(${currentPage + 1})">
                        <i class="fa-solid fa-angle-right"></i>
                    </button>
                </div>
            </div>

        </c:if><%-- end notifications tab --%>

        <%-- ══════════════════════════════════════════ --%>
        <%-- ══  TAB 2: NHẬT KÝ HOẠT ĐỘNG  ══          --%>
        <%-- ══════════════════════════════════════════ --%>
        <c:if test="${tab == 'activitylogs'}">

            <%-- KPI CARDS --%>
            <div class="logs-kpi-grid">
                <div class="logs-kpi-card kpi-purple">
                    <div class="logs-kpi-header">
                        <span class="logs-kpi-title">Tổng nhật ký</span>
                        <span class="logs-kpi-icon">📋</span>
                    </div>
                    <div class="logs-kpi-value">${logStats.total}</div>
                    <div class="logs-kpi-desc">Toàn bộ hệ thống</div>
                </div>
                <div class="logs-kpi-card kpi-green">
                    <div class="logs-kpi-header">
                        <span class="logs-kpi-title">Thành công</span>
                        <span class="logs-kpi-icon">✅</span>
                    </div>
                    <div class="logs-kpi-value">${logStats.successCount}</div>
                    <div class="logs-kpi-desc">Thao tác SUCCESS</div>
                </div>
                <div class="logs-kpi-card kpi-red">
                    <div class="logs-kpi-header">
                        <span class="logs-kpi-title">Thất bại</span>
                        <span class="logs-kpi-icon">❌</span>
                    </div>
                    <div class="logs-kpi-value">${logStats.failCount}</div>
                    <div class="logs-kpi-desc">Thao tác FAIL</div>
                </div>
                <div class="logs-kpi-card kpi-teal">
                    <div class="logs-kpi-header">
                        <span class="logs-kpi-title">Hôm nay</span>
                        <span class="logs-kpi-icon">📅</span>
                    </div>
                    <div class="logs-kpi-value">${logStats.todayCount}</div>
                    <div class="logs-kpi-desc">Nhật ký trong ngày</div>
                </div>
            </div>

            <%-- FILTER BAR --%>
            <form method="GET" action="${pageContext.request.contextPath}/admin/logs"
                  id="logFilterForm" class="logs-filter-bar">
                <input type="hidden" name="tab" value="activitylogs">

                <span class="logs-filter-label">Bộ lọc:</span>

                <select name="module" class="logs-filter-select"
                        onchange="document.getElementById('logFilterForm').submit()">
                    <option value="">-- Tất cả module --</option>
                    <c:forEach var="m" items="${moduleOptions}">
                        <option value="${m}" ${param.module == m ? 'selected' : ''}>${m}</option>
                    </c:forEach>
                </select>

                <select name="status" class="logs-filter-select"
                        onchange="document.getElementById('logFilterForm').submit()">
                    <option value="">-- Tất cả trạng thái --</option>
                    <option value="SUCCESS" ${param.status == 'SUCCESS' ? 'selected' : ''}>✅ SUCCESS</option>
                    <option value="FAIL"    ${param.status == 'FAIL'    ? 'selected' : ''}>❌ FAIL</option>
                    <option value="WARNING" ${param.status == 'WARNING' ? 'selected' : ''}>⚠️ WARNING</option>
                    <option value="INFO"    ${param.status == 'INFO'    ? 'selected' : ''}>ℹ️ INFO</option>
                </select>

                <div class="logs-search-wrapper">
                    <i class="fa-solid fa-magnifying-glass search-icon"></i>
                    <input type="text" name="search" class="logs-search-input"
                           placeholder="Tìm tên tài khoản, module, hành động..."
                           value="${param.search}">
                </div>

                <button type="submit" class="logs-filter-btn submit">
                    <i class="fa-solid fa-filter"></i> Lọc
                </button>
                <a href="${pageContext.request.contextPath}/admin/logs?tab=activitylogs"
                   class="logs-filter-btn clear">
                    <i class="fa-solid fa-xmark"></i> Xóa lọc
                </a>
            </form>

            <%-- ACTIVITY LOG TABLE --%>
            <div class="logs-table-container">
                <div class="logs-table-head-bar">
                    <span class="logs-table-title">
                        <i class="fa-solid fa-timeline"></i>
                        Nhật ký hoạt động hệ thống
                    </span>
                    <span class="logs-table-count">${totalRecords} bản ghi</span>
                </div>
                <table class="logs-table">
                    <thead>
                        <tr>
                            <th class="th-w-5">#</th>
                            <th class="th-w-13">Tài khoản</th>
                            <th class="th-w-10">Hành động</th>
                            <th class="th-w-10">Module</th>
                            <th class="th-w-7 cell-center">Target</th>
                            <th class="th-w-20">Thay đổi (Cũ → Mới)</th>
                            <th class="th-w-10 cell-center">Trạng thái</th>
                            <th class="th-w-14">Thời gian</th>
                            <th class="th-w-11 cell-center">Thao tác</th>
                        </tr>
                    </thead>

                    <tbody>
                        <c:forEach var="lg" items="${activityLogs}">
                            <tr>
                                <td class="cell-id">#${lg.logId}</td>
                                <td>
                                    <div class="cell-user">
                                        <span class="username">${lg.username}</span>
                                        <span class="email-sub">${lg.email}</span>
                                    </div>
                                </td>
                                <td>
                                    <span class="badge badge-action">${lg.action}</span>
                                </td>
                                <td>
                                    <span class="badge badge-module">${lg.module}</span>
                                </td>
                                <td class="cell-center">
                                    <span class="cell-id">${lg.targetId > 0 ? lg.targetId : '—'}</span>
                                </td>
                                <td>
                                    <div class="value-diff">
                                        <c:choose>
                                            <c:when test="${not empty lg.oldValue}">
                                                <span class="val-old" title="${fn:escapeXml(lg.oldValue)}">
                                                    <i class="fa-solid fa-minus icon-sm"></i>
                                                    ${fn:length(lg.oldValue) > 30 ? fn:substring(lg.oldValue,0,30).concat('…') : lg.oldValue}
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="val-empty">— (không có)</span>
                                            </c:otherwise>
                                        </c:choose>
                                        <c:choose>
                                            <c:when test="${not empty lg.newValue}">
                                                <span class="val-new" title="${fn:escapeXml(lg.newValue)}">
                                                    <i class="fa-solid fa-plus icon-sm"></i>
                                                    ${fn:length(lg.newValue) > 30 ? fn:substring(lg.newValue,0,30).concat('…') : lg.newValue}
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="val-empty">— (không có)</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </td>
                                <td class="cell-center">

                                    <c:choose>
                                        <c:when test="${lg.status == 'SUCCESS'}">
                                            <span class="badge badge-success">✅ SUCCESS</span>
                                        </c:when>
                                        <c:when test="${lg.status == 'FAIL'}">
                                            <span class="badge badge-fail">❌ FAIL</span>
                                        </c:when>
                                        <c:when test="${lg.status == 'WARNING'}">
                                            <span class="badge badge-warning">⚠️ WARNING</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-info">${lg.status}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="cell-date">
                                    <%
                                        Object rawLogDate = ((java.util.Map<?,?>)pageContext.findAttribute("lg")).get("createdAt");
                                        if (rawLogDate instanceof LocalDateTime) {
                                            DateTimeFormatter dtfLog = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                                            out.print(((LocalDateTime)rawLogDate).format(dtfLog));
                                        } else {
                                            out.print("—");
                                        }
                                    %>
                                </td>
                                <td>
                                    <div class="action-cell">
                                        <%-- Nút xem chi tiết log --%>
                                        <button type="button"
                                                class="act-btn"
                                                title="Xem chi tiết"
                                                onclick='openLogModal(
                                                    ${lg.logId},
                                                    "${fn:escapeXml(lg.username)}",
                                                    "${fn:escapeXml(lg.email)}",
                                                    "${fn:escapeXml(lg.action)}",
                                                    "${fn:escapeXml(lg.module)}",
                                                    ${lg.targetId},
                                                    `${fn:escapeXml(lg.oldValue)}`,
                                                    `${fn:escapeXml(lg.newValue)}`,
                                                    "${fn:escapeXml(lg.status)}"
                                                )'>
                                            <i class="fa-solid fa-eye"></i>
                                        </button>

                                        <%-- Soft-delete log --%>
                                        <form method="POST"
                                              action="${pageContext.request.contextPath}/admin/logs"
                                              class="form-inline"
                                              onsubmit="return confirm('Xóa nhật ký này khỏi danh sách?')">

                                            <input type="hidden" name="action"  value="deleteLog">
                                            <input type="hidden" name="id"      value="${lg.logId}">
                                            <input type="hidden" name="tab"     value="activitylogs">
                                            <input type="hidden" name="search"  value="${fn:escapeXml(param.search)}">
                                            <input type="hidden" name="module"  value="${fn:escapeXml(param.module)}">
                                            <input type="hidden" name="status"  value="${fn:escapeXml(param.status)}">
                                            <input type="hidden" name="page"    value="${currentPage}">
                                            <button type="submit" class="act-btn delete" title="Xóa nhật ký">
                                                <i class="fa-solid fa-trash"></i>
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty activityLogs}">
                            <tr>
                                <td colspan="9">
                                    <div class="logs-empty">
                                        <div class="empty-icon">📋</div>
                                        <p>Không tìm thấy nhật ký hoạt động nào phù hợp.</p>
                                    </div>
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <%-- PAGINATION --%>
            <div class="logs-pagination">
                <span class="pag-info-txt">
                    Trang <strong>${currentPage}</strong> / <strong>${totalPages}</strong>
                    &nbsp;•&nbsp; Tổng <strong>${totalRecords}</strong> bản ghi
                </span>
                <div class="pag-btns">
                    <button class="pag-b" ${currentPage <= 1 ? 'disabled' : ''}
                            onclick="goLogPage(${currentPage - 1})">
                        <i class="fa-solid fa-angle-left"></i>
                    </button>
                    <c:forEach var="p" begin="1" end="${totalPages}">
                        <button class="pag-b ${p == currentPage ? 'active' : ''}"
                                onclick="goLogPage(${p})">${p}</button>
                    </c:forEach>
                    <button class="pag-b" ${currentPage >= totalPages ? 'disabled' : ''}
                            onclick="goLogPage(${currentPage + 1})">
                        <i class="fa-solid fa-angle-right"></i>
                    </button>
                </div>
            </div>

        </c:if><%-- end activitylogs tab --%>

        <%-- ══════════════════════════════════════════ --%>
        <%-- ══  TAB 3: GỬI VOUCHER & THÔNG BÁO  ══     --%>
        <%-- ══════════════════════════════════════════ --%>
        <c:if test="${tab == 'sendvoucher'}">

            <%-- Toast Alert Messages --%>
            <c:if test="${not empty sessionScope.toastMessage}">
                <div class="logs-alert logs-alert-success">
                    <i class="fa-solid fa-circle-check logs-alert-icon"></i>
                    <div>${sessionScope.toastMessage}</div>
                </div>
                <c:remove var="toastMessage" scope="session"/>
            </c:if>
            <c:if test="${not empty sessionScope.errorMessage}">
                <div class="logs-alert logs-alert-error">
                    <i class="fa-solid fa-triangle-exclamation logs-alert-icon"></i>
                    <div>${sessionScope.errorMessage}</div>
                </div>
                <c:remove var="errorMessage" scope="session"/>
            </c:if>

            <form method="POST" action="${pageContext.request.contextPath}/admin/logs" id="sendVoucherForm">
                <input type="hidden" name="action" value="sendVoucher">
                <input type="hidden" name="tab" value="sendvoucher">

                <div class="sendvoucher-card">
                    <h3 class="sendvoucher-title">
                        <span>🎁</span> Tạo &amp; Gửi Mã Giảm Giá Cho Khách Hàng
                    </h3>

                    <div class="sendvoucher-grid">
                        <%-- Cột trái: Chọn Mã & Soạn Nội Dung --%>
                        <div>
                            <div class="sendvoucher-form-group">
                                <label class="sendvoucher-label">Chọn Mã Giảm Giá <span class="required-star">*</span></label>
                                <select name="discountId" required class="sendvoucher-select">
                                    <option value="">-- Chọn mã giảm giá đang kích hoạt --</option>
                                    <c:forEach var="d" items="${activeDiscounts}">
                                        <option value="${d.discountId}">
                                            [${d.code}] - ${d.discountValueFormatted} (${not empty d.description ? d.description : 'Không mô tả'})
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="sendvoucher-form-group">
                                <label class="sendvoucher-label">Tiêu Đề Thông Báo</label>
                                <input type="text" name="title" placeholder="Ví dụ: Bạn nhận được mã giảm giá tri ân! 🎁"
                                       value="Bạn nhận được mã giảm giá mới! 🎁"
                                       class="sendvoucher-input">
                            </div>

                            <div>
                                <label class="sendvoucher-label">Nội Dung Thông Báo</label>
                                <textarea name="message" rows="4" placeholder="Nhập nội dung thông báo gửi cho khách hàng..."
                                          class="sendvoucher-textarea">Chúc mừng bạn đã nhận được mã giảm giá từ Basalt House. Hãy kiểm tra và sử dụng ngay!</textarea>
                            </div>
                        </div>

                        <%-- Cột phải: Đối Tượng Nhận --%>
                        <div class="sendvoucher-target-box">
                            <label class="sendvoucher-label">Đối Tượng Nhận Voucher <span class="required-star">*</span></label>

                            <div class="sendvoucher-radio-group">
                                <label class="sendvoucher-radio-label">
                                    <input type="radio" name="targetMode" value="selected" checked onchange="toggleTargetMode('selected')">
                                    Tích chọn danh sách khách hàng bên dưới
                                </label>
                                <label class="sendvoucher-radio-label">
                                    <input type="radio" name="targetMode" value="all" onchange="toggleTargetMode('all')">
                                    Gửi cho TẤT CẢ khách hàng (${fn:length(customers)} người)
                                </label>
                                <label class="sendvoucher-radio-label">
                                    <input type="radio" name="targetMode" value="rank" onchange="toggleTargetMode('rank')">
                                    Gửi theo Hạng Thành Viên (Rank)
                                </label>
                            </div>

                            <div id="rankSelectWrapper" class="sendvoucher-rank-wrapper">
                                <label class="sendvoucher-sublabel">Chọn Hạng Thành Viên:</label>
                                <select name="targetRankId" class="sendvoucher-rank-select">
                                    <option value="0">-- Chọn Hạng --</option>
                                    <c:forEach var="r" items="${ranks}">
                                        <option value="${r.rankId}">${r.rankName}</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="sendvoucher-action-footer">
                                <button type="submit" class="btn-sendvoucher-submit">
                                    <i class="fa-solid fa-paper-plane"></i> Gửi Voucher &amp; Thông Báo
                                </button>
                            </div>
                        </div>
                    </div>

                    <%-- Bảng lọc danh sách khách hàng --%>
                    <div id="customerTableSection" class="sendvoucher-form-group">
                        <div class="sendvoucher-table-header">
                            <h4 class="sendvoucher-table-title">
                                <i class="fa-solid fa-users"></i> Danh Sách Khách Hàng (Tích chọn từng người)
                            </h4>
                            <div class="sendvoucher-filter-group">
                                <span class="sendvoucher-filter-label">Lọc danh sách theo rank:</span>
                                <select onchange="window.location.href='${pageContext.request.contextPath}/admin/logs?tab=sendvoucher&filterRankId=' + this.value"
                                        class="sendvoucher-filter-select">
                                    <option value="0">Tất cả hạng</option>
                                    <c:forEach var="r" items="${ranks}">
                                        <option value="${r.rankId}" ${filterRankId == r.rankId ? 'selected' : ''}>${r.rankName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="logs-table-wrapper">
                            <table class="logs-table">
                                <thead>
                                    <tr>
                                        <th class="th-w-40">
                                            <input type="checkbox" id="selectAllCust" onchange="toggleSelectAllCust(this)">
                                        </th>
                                        <th>Họ và Tên</th>
                                        <th>Email</th>
                                        <th>Số Điện Thoại</th>
                                        <th>Hạng Thành Viên</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${empty customers}">
                                            <tr>
                                                <td colspan="5" class="cell-center text-muted">
                                                    Không tìm thấy khách hàng nào.
                                                </td>
                                            </tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="c" items="${customers}">
                                                <tr>
                                                    <td class="cell-center">
                                                        <input type="checkbox" name="accountIds" value="${c.accountId}" class="cust-checkbox">
                                                    </td>
                                                    <td class="cell-user-bold">${c.fullName}</td>
                                                    <td>${c.email}</td>
                                                    <td>${c.phone}</td>
                                                    <td>
                                                        <span class="badge rank-badge-item">
                                                            🏆 ${c.rankName}
                                                        </span>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </form>

            <script>
                function toggleSelectAllCust(master) {
                    const checkboxes = document.querySelectorAll('.cust-checkbox');
                    checkboxes.forEach(cb => cb.checked = master.checked);
                }

                function toggleTargetMode(mode) {
                    const rankWrapper = document.getElementById('rankSelectWrapper');
                    const custSection = document.getElementById('customerTableSection');
                    if (mode === 'rank') {
                        rankWrapper.style.display = 'block';
                        custSection.style.opacity = '0.4';
                        custSection.style.pointerEvents = 'none';
                    } else if (mode === 'all') {
                        rankWrapper.style.display = 'none';
                        custSection.style.opacity = '0.4';
                        custSection.style.pointerEvents = 'none';
                    } else {
                        rankWrapper.style.display = 'none';
                        custSection.style.opacity = '1';
                        custSection.style.pointerEvents = 'auto';
                    }
                }
            </script>

        </c:if><%-- end sendvoucher tab --%>


    </main>
</div>

<%-- ══ MODAL – Chi tiết Thông báo ══ --%>
<div class="logs-modal-overlay" id="notiModalOverlay" onclick="closeNotiModal(event)">
    <div class="logs-modal-box">
        <button class="logs-modal-close" onclick="closeNotiModalBtn()">
            <i class="fa-solid fa-xmark"></i>
        </button>
        <div class="modal-section-title">
            <i class="fa-solid fa-bell icon-blue"></i>
            Chi Tiết Thông Báo
        </div>
        <div class="modal-row">
            <span class="m-label">ID:</span>
            <span class="m-value" id="mdNotiId">—</span>
        </div>
        <hr class="modal-divider">
        <div class="modal-row">
            <span class="m-label">Tài khoản:</span>
            <span class="m-value" id="mdNotiUser">—</span>
        </div>
        <div class="modal-row">
            <span class="m-label">Email:</span>
            <span class="m-value" id="mdNotiEmail">—</span>
        </div>
        <hr class="modal-divider">
        <div class="modal-row">
            <span class="m-label">Tiêu đề:</span>
            <span class="m-value bold" id="mdNotiTitle">—</span>
        </div>
        <div class="modal-row align-top">
            <span class="m-label">Nội dung:</span>
            <span class="m-value val-msg" id="mdNotiMsg">—</span>
        </div>
        <hr class="modal-divider">
        <div class="modal-row">
            <span class="m-label">Trạng thái:</span>
            <span class="m-value" id="mdNotiStatus">—</span>
        </div>
    </div>
</div>

<%-- ══ MODAL – Chi tiết Nhật ký ══ --%>
<div class="logs-modal-overlay" id="logModalOverlay" onclick="closeLogModal(event)">
    <div class="logs-modal-box">
        <button class="logs-modal-close" onclick="closeLogModalBtn()">
            <i class="fa-solid fa-xmark"></i>
        </button>
        <div class="modal-section-title">
            <i class="fa-solid fa-clock-rotate-left icon-purple"></i>
            Chi Tiết Nhật Ký Hoạt Động
        </div>
        <div class="modal-row">
            <span class="m-label">Log ID:</span>
            <span class="m-value" id="mdLogId">—</span>
        </div>
        <hr class="modal-divider">
        <div class="modal-row">
            <span class="m-label">Tài khoản:</span>
            <span class="m-value" id="mdLogUser">—</span>
        </div>
        <div class="modal-row">
            <span class="m-label">Email:</span>
            <span class="m-value" id="mdLogEmail">—</span>
        </div>
        <hr class="modal-divider">
        <div class="modal-row">
            <span class="m-label">Hành động:</span>
            <span class="m-value" id="mdLogAction">—</span>
        </div>
        <div class="modal-row">
            <span class="m-label">Module:</span>
            <span class="m-value" id="mdLogModule">—</span>
        </div>
        <div class="modal-row">
            <span class="m-label">Target ID:</span>
            <span class="m-value" id="mdLogTarget">—</span>
        </div>
        <hr class="modal-divider">
        <div class="modal-row align-top">
            <span class="m-label">Giá trị cũ:</span>
            <span class="m-value val-old" id="mdLogOld">—</span>
        </div>
        <div class="modal-row align-top">
            <span class="m-label">Giá trị mới:</span>
            <span class="m-value val-new" id="mdLogNew">—</span>
        </div>

        <hr class="modal-divider">
        <div class="modal-row">
            <span class="m-label">Trạng thái:</span>
            <span class="m-value" id="mdLogStatus">—</span>
        </div>
    </div>
</div>

<%-- ══ JAVASCRIPT ══ --%>
<script>
    /* ── Phân trang Notifications ─────────────────────────────── */
    function goNotiPage(page) {
        const params = new URLSearchParams(window.location.search);
        params.set('tab', 'notifications');
        params.set('page', page);
        window.location.search = params.toString();
    }

    /* ── Phân trang Activity Logs ─────────────────────────────── */
    function goLogPage(page) {
        const params = new URLSearchParams(window.location.search);
        params.set('tab', 'activitylogs');
        params.set('page', page);
        window.location.search = params.toString();
    }

    /* ── Modal Notification ───────────────────────────────────── */
    function openNotiModal(id, username, email, title, message, isDeleted) {
        document.getElementById('mdNotiId').textContent    = '#' + id;
        document.getElementById('mdNotiUser').textContent  = username;
        document.getElementById('mdNotiEmail').textContent = email;
        document.getElementById('mdNotiTitle').textContent = title;
        document.getElementById('mdNotiMsg').textContent   = message;
        const statusEl = document.getElementById('mdNotiStatus');
        statusEl.innerHTML = isDeleted
            ? '<span class="badge badge-deleted">🗑️ Đã xóa</span>'
            : '<span class="badge badge-active">✅ Hiển thị</span>';
        document.getElementById('notiModalOverlay').classList.add('open');
        document.body.style.overflow = 'hidden';
    }

    function closeNotiModal(e) {
        if (e.target.id === 'notiModalOverlay') closeNotiModalBtn();
    }

    function closeNotiModalBtn() {
        document.getElementById('notiModalOverlay').classList.remove('open');
        document.body.style.overflow = '';
    }

    /* ── Modal Activity Log ───────────────────────────────────── */
    function openLogModal(id, username, email, action, module, targetId, oldVal, newVal, status) {
        document.getElementById('mdLogId').textContent     = '#' + id;
        document.getElementById('mdLogUser').textContent   = username;
        document.getElementById('mdLogEmail').textContent  = email;
        document.getElementById('mdLogAction').textContent = action;
        document.getElementById('mdLogModule').textContent = module;
        document.getElementById('mdLogTarget').textContent = targetId > 0 ? targetId : '—';
        document.getElementById('mdLogOld').textContent    = oldVal  || '(không có)';
        document.getElementById('mdLogNew').textContent    = newVal  || '(không có)';

        const statEl  = document.getElementById('mdLogStatus');
        const badgeMap = {
            'SUCCESS': 'badge-success', 'FAIL': 'badge-fail',
            'WARNING': 'badge-warning', 'INFO': 'badge-info'
        };
        const iconMap = {
            'SUCCESS': '✅', 'FAIL': '❌', 'WARNING': '⚠️', 'INFO': 'ℹ️'
        };
        const cls  = badgeMap[status] || 'badge-info';
        const icon = iconMap[status]  || '';
        statEl.innerHTML = `<span class="badge ${cls}">${icon} ${status}</span>`;

        document.getElementById('logModalOverlay').classList.add('open');
        document.body.style.overflow = 'hidden';
    }

    function closeLogModal(e) {
        if (e.target.id === 'logModalOverlay') closeLogModalBtn();
    }

    function closeLogModalBtn() {
        document.getElementById('logModalOverlay').classList.remove('open');
        document.body.style.overflow = '';
    }

    /* ── ESC key to close modals ──────────────────────────────── */
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            closeNotiModalBtn();
            closeLogModalBtn();
        }
    });
</script>
</body>
</html>
