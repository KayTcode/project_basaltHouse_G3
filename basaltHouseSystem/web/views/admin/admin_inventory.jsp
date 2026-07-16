<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Basalt House – Quản lý Kho hàng</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/admin_common.css?v=3">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/admin_inventory.css?v=3">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined" rel="stylesheet">
</head>
<body>
<jsp:include page="header.jsp" />
<div class="app-container">
    <jsp:include page="sidebar.jsp" />
    <main class="main-content">

      
        <div class="page-header-bar">
            <div>
                <h2 class="page-title">📦 Quản lý Kho hàng</h2>
                <p class="page-sub">Theo dõi tồn kho nguyên liệu và quản lý phiếu nhập</p>
            </div>
        </div>


        <c:if test="${not empty errorMessage}">
            <div class="toast-error">⚠️ ${errorMessage}</div>
        </c:if>
        <c:if test="${not empty dataError}">
            <div class="toast-error">⚠️ Lỗi dữ liệu: ${dataError}</div>
        </c:if>

       
        <div class="kpi-row">
            <div class="kpi-card kpi-ok">
                <div class="kpi-icon">✅</div>
                <div class="kpi-value">${okCount}</div>
                <div class="kpi-label">Đủ hàng</div>
            </div>
            <div class="kpi-card kpi-warning">
                <div class="kpi-icon">⚠️</div>
                <div class="kpi-value">${warningCount}</div>
                <div class="kpi-label">Sắp hết</div>
            </div>
            <div class="kpi-card kpi-danger">
                <div class="kpi-icon">🚨</div>
                <div class="kpi-value">${outCount}</div>
                <div class="kpi-label">Hết hàng</div>
            </div>
        </div>

        
        <form method="get" action="${pageContext.request.contextPath}/admin/ingredients" class="search-bar">
            <input type="text" name="search" value="${key}" placeholder="Tìm nguyên liệu…" class="search-input">
            <button type="submit" class="btn-secondary">🔍 Tìm</button>
            <c:if test="${not empty key}">
                <a href="${pageContext.request.contextPath}/admin/ingredients" class="btn-ghost">✕ Xoá bộ lọc</a>
            </c:if>
        </form>

        
        <div class="panel">
            <div class="panel-title">Danh sách nguyên liệu</div>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Nguyên liệu</th>
                        <th>Nhà cung cấp</th>
                        <th>Đơn vị</th>
                        <th>Tồn kho</th>
                        <th>Tối thiểu</th>
                        <th>Tỉ lệ</th>
                        <th>Trạng thái</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty ingredients}">
                            <tr><td colspan="7" class="empty-state">Không có nguyên liệu nào</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="item" items="${ingredients}">
                                <tr>
                                    <td><strong>${item.name}</strong></td>
                                    <td>${item.supplierName}</td>
                                    <td>${item.unit}</td>
                                    <td>${item.stockText}</td>
                                    <td>${item.minStockText}</td>
                                    <td>
                                        <div class="progress-bar-wrap">
                                            <div class="progress-bar ${item.status}" style="width:${item.barPercent}%"></div>
                                        </div>
                                    </td>
                                    <td>
                                        <span class="badge badge-${item.status}">
                                            <span class="material-symbols-outlined icon-sm">${item.statusIcon}</span>
                                            ${item.statusLabel}
                                        </span>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
            
            <c:if test="${totalIngredientPages > 1}">
                <div class="pagination" style="padding: 20px; display: flex; justify-content: center; gap: 8px;">
                    <c:url value="/admin/ingredients" var="prevUrl">
                        <c:param name="pageIngredient" value="${currentIngredientPage - 1}"/>
                        <c:if test="${not empty key}"><c:param name="search" value="${key}"/></c:if>
                        <c:if test="${not empty param.pageImport}"><c:param name="pageImport" value="${param.pageImport}"/></c:if>
                        <c:if test="${not empty param.pageUsage}"><c:param name="pageUsage" value="${param.pageUsage}"/></c:if>
                    </c:url>
                    <a href="${prevUrl}" class="btn-secondary ${currentIngredientPage <= 1 ? 'disabled' : ''}" style="padding: 6px 12px; height: auto;">&#171;</a>
                    
                    <c:forEach begin="1" end="${totalIngredientPages}" var="pg">
                        <c:url value="/admin/ingredients" var="pageUrl">
                            <c:param name="pageIngredient" value="${pg}"/>
                            <c:if test="${not empty key}"><c:param name="search" value="${key}"/></c:if>
                            <c:if test="${not empty param.pageImport}"><c:param name="pageImport" value="${param.pageImport}"/></c:if>
                            <c:if test="${not empty param.pageUsage}"><c:param name="pageUsage" value="${param.pageUsage}"/></c:if>
                        </c:url>
                        <a href="${pageUrl}" class="btn-secondary ${pg == currentIngredientPage ? 'active' : ''}" style="padding: 6px 12px; height: auto; ${pg == currentIngredientPage ? 'background: #6366f1; color: white; border-color: #6366f1;' : ''}">${pg}</a>
                    </c:forEach>
                    
                    <c:url value="/admin/ingredients" var="nextUrl">
                        <c:param name="pageIngredient" value="${currentIngredientPage + 1}"/>
                        <c:if test="${not empty key}"><c:param name="search" value="${key}"/></c:if>
                        <c:if test="${not empty param.pageImport}"><c:param name="pageImport" value="${param.pageImport}"/></c:if>
                        <c:if test="${not empty param.pageUsage}"><c:param name="pageUsage" value="${param.pageUsage}"/></c:if>
                    </c:url>
                    <a href="${nextUrl}" class="btn-secondary ${currentIngredientPage >= totalIngredientPages ? 'disabled' : ''}" style="padding: 6px 12px; height: auto;">&#187;</a>
                </div>
            </c:if>
        </div>

        <div class="panel" style="margin-top:24px">
            <div class="panel-title">Thống kê nguyên liệu đã dùng (Hôm nay)</div>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Sản phẩm</th>
                        <th>Size</th>
                        <th>Đã bán (Hôm nay)</th>
                        <th style="text-align: right;">Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty todayUsageList}">
                            <tr><td colspan="5" class="empty-state">Chưa dùng nguyên liệu nào hôm nay</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="prod" items="${todayUsageList}" varStatus="status">
                                <tr>
                                    <td><strong>${prod.productName}</strong></td>
                                    <td><span class="badge badge-info">${prod.sizeName}</span></td>
                                    <td style="font-weight: 600; color: #10b981;">${prod.totalCups} cốc</td>
                                    <td style="text-align: right;">
                                        <button type="button" class="btn-secondary" onclick="toggleDetails('details-${status.index}')" style="color: #3b82f6; font-size: 13px; font-weight: 600; padding: 6px 12px; height: auto;">
                                            <i class="fas fa-eye"></i> Xem nguyên liệu
                                        </button>
                                    </td>
                                </tr>
                                <tr id="details-${status.index}" style="display: none; background: #f8fafc;">
                                    <td colspan="4" style="padding: 16px;">
                                        <div style="font-size: 13px; color: #475569; margin-bottom: 8px;"><strong>Nguyên liệu tiêu hao cho ${prod.totalCups} cốc:</strong></div>
                                        <table class="data-table" style="box-shadow: none; border: 1px solid #e2e8f0; margin: 0;">
                                            <thead>
                                                <tr>
                                                    <th>Nguyên liệu</th>
                                                    <th>Đơn vị</th>
                                                    <th>Tổng dùng</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="ing" items="${prod.ingredients}">
                                                    <tr>
                                                        <td>${ing.ingredientName}</td>
                                                        <td>${ing.unit}</td>
                                                        <td class="currency" style="color: #ef4444; font-weight: 600;"><fmt:formatNumber value="${ing.usedQuantity}" maxFractionDigits="2"/></td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
            <c:if test="${totalUsagePages > 1}">
                <div class="pagination" style="padding: 20px; display: flex; justify-content: center; gap: 8px;">
                    <c:url value="/admin/ingredients" var="prevUsageUrl">
                        <c:param name="pageUsage" value="${currentUsagePage - 1}"/>
                        <c:if test="${not empty key}"><c:param name="search" value="${key}"/></c:if>
                        <c:if test="${not empty param.pageIngredient}"><c:param name="pageIngredient" value="${param.pageIngredient}"/></c:if>
                        <c:if test="${not empty param.pageImport}"><c:param name="pageImport" value="${param.pageImport}"/></c:if>
                    </c:url>
                    <a href="${prevUsageUrl}" class="btn-secondary ${currentUsagePage <= 1 ? 'disabled' : ''}" style="padding: 6px 12px; height: auto;">&#171;</a>
                    
                    <c:forEach begin="1" end="${totalUsagePages}" var="pg">
                        <c:url value="/admin/ingredients" var="pageUsageUrl">
                            <c:param name="pageUsage" value="${pg}"/>
                            <c:if test="${not empty key}"><c:param name="search" value="${key}"/></c:if>
                            <c:if test="${not empty param.pageIngredient}"><c:param name="pageIngredient" value="${param.pageIngredient}"/></c:if>
                            <c:if test="${not empty param.pageImport}"><c:param name="pageImport" value="${param.pageImport}"/></c:if>
                        </c:url>
                        <a href="${pageUsageUrl}" class="btn-secondary ${pg == currentUsagePage ? 'active' : ''}" style="padding: 6px 12px; height: auto; ${pg == currentUsagePage ? 'background: #6366f1; color: white; border-color: #6366f1;' : ''}">${pg}</a>
                    </c:forEach>
                    
                    <c:url value="/admin/ingredients" var="nextUsageUrl">
                        <c:param name="pageUsage" value="${currentUsagePage + 1}"/>
                        <c:if test="${not empty key}"><c:param name="search" value="${key}"/></c:if>
                        <c:if test="${not empty param.pageIngredient}"><c:param name="pageIngredient" value="${param.pageIngredient}"/></c:if>
                        <c:if test="${not empty param.pageImport}"><c:param name="pageImport" value="${param.pageImport}"/></c:if>
                    </c:url>
                    <a href="${nextUsageUrl}" class="btn-secondary ${currentUsagePage >= totalUsagePages ? 'disabled' : ''}" style="padding: 6px 12px; height: auto;">&#187;</a>
                </div>
            </c:if>
        </div>

       
        <div class="panel" style="margin-top:24px">
            <div class="panel-title">Lịch sử phiếu nhập kho</div>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Mã phiếu</th>
                        <th>Nhà cung cấp</th>
                        <th>Trạng thái</th>
                        <th>Tổng đặt</th>
                        <th>Tổng nhận</th>
                        <th>Ngày đặt</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty importList}">
                            <tr><td colspan="6" class="empty-state">Chưa có phiếu nhập nào</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="inv" items="${importList}">
                                <tr>
                                    <td><strong>${inv.importCode}</strong></td>
                                    <td>${inv.supplierName}</td>
                                    <td>
                                        <span class="badge badge-${inv.status eq 'Confirmed' ? 'ok' : inv.status eq 'Pending' ? 'warning' : 'danger'}">
                                            ${inv.status}
                                        </span>
                                    </td>
                                    <td><fmt:formatNumber value="${inv.totalOrderedAmount}" type="number" maxFractionDigits="2"/> ${inv.unit}</td>
                                    <td><fmt:formatNumber value="${inv.totalReceivedAmount != null ? inv.totalReceivedAmount : 0}" type="number" maxFractionDigits="2"/> ${inv.unit}</td>
                                    <td>${fn:substring(fn:replace(inv.orderedDate, 'T', ' '), 0, 16)}</td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
            
            <c:if test="${totalImportPages > 1}">
                <div class="pagination" style="padding: 20px; display: flex; justify-content: center; gap: 8px;">
                    <c:url value="/admin/ingredients" var="prevUrl">
                        <c:param name="pageImport" value="${currentImportPage - 1}"/>
                        <c:if test="${not empty key}"><c:param name="search" value="${key}"/></c:if>
                        <c:if test="${not empty param.pageIngredient}"><c:param name="pageIngredient" value="${param.pageIngredient}"/></c:if>
                        <c:if test="${not empty param.pageUsage}"><c:param name="pageUsage" value="${param.pageUsage}"/></c:if>
                    </c:url>
                    <a href="${prevUrl}" class="btn-secondary ${currentImportPage <= 1 ? 'disabled' : ''}" style="padding: 6px 12px; height: auto;">&#171;</a>
                    
                    <c:forEach begin="1" end="${totalImportPages}" var="pg">
                        <c:url value="/admin/ingredients" var="pageUrl">
                            <c:param name="pageImport" value="${pg}"/>
                            <c:if test="${not empty key}"><c:param name="search" value="${key}"/></c:if>
                            <c:if test="${not empty param.pageIngredient}"><c:param name="pageIngredient" value="${param.pageIngredient}"/></c:if>
                            <c:if test="${not empty param.pageUsage}"><c:param name="pageUsage" value="${param.pageUsage}"/></c:if>
                        </c:url>
                        <a href="${pageUrl}" class="btn-secondary ${pg == currentImportPage ? 'active' : ''}" style="padding: 6px 12px; height: auto; ${pg == currentImportPage ? 'background: #6366f1; color: white; border-color: #6366f1;' : ''}">${pg}</a>
                    </c:forEach>
                    
                    <c:url value="/admin/ingredients" var="nextUrl">
                        <c:param name="pageImport" value="${currentImportPage + 1}"/>
                        <c:if test="${not empty key}"><c:param name="search" value="${key}"/></c:if>
                        <c:if test="${not empty param.pageIngredient}"><c:param name="pageIngredient" value="${param.pageIngredient}"/></c:if>
                        <c:if test="${not empty param.pageUsage}"><c:param name="pageUsage" value="${param.pageUsage}"/></c:if>
                    </c:url>
                    <a href="${nextUrl}" class="btn-secondary ${currentImportPage >= totalImportPages ? 'disabled' : ''}" style="padding: 6px 12px; height: auto;">&#187;</a>
                </div>
            </c:if>
        </div>

    </main>
</div>

<script>
    function toggleDetails(id) {
        var el = document.getElementById(id);
        if (el.style.display === 'none') {
            el.style.display = 'table-row';
        } else {
            el.style.display = 'none';
        }
    }
</script>
</body>
</html>
