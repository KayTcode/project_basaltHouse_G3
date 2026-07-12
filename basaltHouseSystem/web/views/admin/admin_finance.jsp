<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- ══ Giải nén data Map từ Servlet ══ --%>
<c:set var="totalRevenue"    value="${data.totalRevenue}" />
<c:set var="totalCost"       value="${data.totalCost}" />
<c:set var="grossProfit"     value="${data.grossProfit}" />
<c:set var="totalOrders"     value="${data.totalOrders}" />
<c:set var="revGrowth"       value="${data.revGrowth}" />
<c:set var="profitMargin"    value="${data.profitMargin}" />
<c:set var="avgOrder"        value="${data.avgOrder}" />
<c:set var="weeklyBreakdown" value="${data.weeklyBreakdown}" />
<c:set var="channelRevenue"  value="${data.channelRevenue}" />
<c:set var="paymentStats"    value="${data.paymentStats}" />
<c:set var="topProducts"     value="${data.topProducts}" />
<c:set var="recentImports"   value="${data.recentImports}" />
<c:set var="period"          value="${data.period}" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Tài Chính - BasaltHouse Admin</title>
        <meta name="description" content="Tổng quan tài chính, doanh thu, chi phí và lợi nhuận của BasaltHouse">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_common.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_finance.css">
        <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
    </head>
    <body class="admin-dashboard-body">

        <jsp:include page="header.jsp" />

        <div class="app-container">
            <jsp:include page="sidebar.jsp" />

            <main class="main-content">

                <%-- ══ Thông báo lỗi load (nếu có) ══ --%>
                <c:if test="${not empty loadError}">
                    <div class="fin-load-error">
                        <i class="fa-solid fa-triangle-exclamation"></i> ${loadError}
                    </div>
                </c:if>

                <%-- ══ PAGE HEADLINE ══ --%>
                <div class="fin-headline-bar">
                    <div class="fin-headline-left">
                        <h1 class="fin-page-title">
                            <i class="fa-solid fa-chart-line fin-title-icon"></i>
                            Báo Cáo Tài Chính
                        </h1>
                        <p class="fin-page-desc">Tổng quan doanh thu, chi phí nhập kho và lợi nhuận theo thời gian thực.</p>
                    </div>
                    <div class="fin-headline-right">
                        <form method="GET" action="${pageContext.request.contextPath}/admin/finance" id="periodForm">
                            <div class="fin-period-tabs">
                                <button type="submit" name="period" value="week"
                                        class="fin-period-btn ${period == 'week' ? 'active' : ''}">7 Ngày</button>
                                <button type="submit" name="period" value="month"
                                        class="fin-period-btn ${period == 'month' || empty period ? 'active' : ''}">Tháng này</button>
                                <button type="submit" name="period" value="year"
                                        class="fin-period-btn ${period == 'year' ? 'active' : ''}">Năm nay</button>
                            </div>
                        </form>
                    </div>
                </div>

                <%-- ══ KPI CARDS ══ --%>
                <div class="fin-kpi-grid">

                    <%-- Doanh thu --%>
                    <div class="fin-kpi-card fin-kpi-revenue">
                        <div class="fin-kpi-top">
                            <div class="fin-kpi-icon-wrap fin-icon-revenue">
                                <i class="fa-solid fa-sack-dollar"></i>
                            </div>
                            <div class="fin-kpi-trend ${revGrowth >= 0 ? 'fin-trend-up' : 'fin-trend-down'}">
                                <i class="fa-solid ${revGrowth >= 0 ? 'fa-arrow-trend-up' : 'fa-arrow-trend-down'}"></i>
                                ${revGrowth >= 0 ? '+' : ''}${revGrowth}%
                            </div>
                        </div>
                        <div class="fin-kpi-value">
                            <fmt:formatNumber value="${totalRevenue}" type="number" maxFractionDigits="0"/> đ
                        </div>
                        <div class="fin-kpi-label">Tổng Doanh Thu</div>
                        <div class="fin-kpi-sub">So với kỳ trước</div>
                        <div class="fin-kpi-progress-bar">
                            <div class="fin-kpi-progress-fill" style="width: 75%;"></div>
                        </div>
                    </div>

                    <%-- Chi phí nhập kho --%>
                    <div class="fin-kpi-card fin-kpi-cost">
                        <div class="fin-kpi-top">
                            <div class="fin-kpi-icon-wrap fin-icon-cost">
                                <i class="fa-solid fa-box-open"></i>
                            </div>
                        </div>
                        <div class="fin-kpi-value fin-value-cost">
                            <fmt:formatNumber value="${totalCost}" type="number" maxFractionDigits="0"/> đ
                        </div>
                        <div class="fin-kpi-label">Chi Phí Nhập Kho</div>
                        <div class="fin-kpi-sub">Phiếu nhập đã nhận hàng</div>
                        <div class="fin-kpi-progress-bar">
                            <div class="fin-kpi-progress-fill fin-prog-cost" style="width: 35%;"></div>
                        </div>
                    </div>

                    <%-- Lợi nhuận --%>
                    <div class="fin-kpi-card fin-kpi-profit">
                        <div class="fin-kpi-top">
                            <div class="fin-kpi-icon-wrap fin-icon-profit">
                                <i class="fa-solid fa-coins"></i>
                            </div>
                        </div>
                        <div class="fin-kpi-value fin-value-profit">
                            <fmt:formatNumber value="${grossProfit}" type="number" maxFractionDigits="0"/> đ
                        </div>
                        <div class="fin-kpi-label">Lợi Nhuận Gộp</div>
                        <div class="fin-kpi-sub">Biên lợi nhuận ${profitMargin}%</div>
                        <div class="fin-kpi-progress-bar">
                            <div class="fin-kpi-progress-fill fin-prog-profit" style="width: ${profitMargin}%;"></div>
                        </div>
                    </div>

                    <%-- Đơn hàng --%>
                    <div class="fin-kpi-card fin-kpi-orders">
                        <div class="fin-kpi-top">
                            <div class="fin-kpi-icon-wrap fin-icon-orders">
                                <i class="fa-solid fa-receipt"></i>
                            </div>
                        </div>
                        <div class="fin-kpi-value">${totalOrders}</div>
                        <div class="fin-kpi-label">Tổng Đơn Hàng</div>
                        <div class="fin-kpi-sub">
                            TB <fmt:formatNumber value="${avgOrder}" type="number" maxFractionDigits="0"/>đ / đơn
                        </div>
                        <div class="fin-kpi-progress-bar">
                            <div class="fin-kpi-progress-fill fin-prog-orders" style="width: 80%;"></div>
                        </div>
                    </div>

                </div>

                <%-- ══ CHARTS ROW ══ --%>
                <div class="fin-charts-row">

                    <%-- Doanh Thu vs Chi Phí (column chart) --%>
                    <div class="fin-chart-panel fin-chart-large">
                        <div class="fin-panel-header">
                            <div class="fin-panel-header-left">
                                <h2 class="fin-panel-title">Doanh Thu & Chi Phí</h2>
                                <p class="fin-panel-sub">So sánh theo tuần trong tháng</p>
                            </div>
                            <div class="fin-legend-wrap">
                                <span class="fin-legend-dot fin-dot-revenue"></span><span class="fin-legend-text">Doanh thu</span>
                                <span class="fin-legend-dot fin-dot-cost"></span><span class="fin-legend-text">Chi phí</span>
                                <span class="fin-legend-dot fin-dot-profit"></span><span class="fin-legend-text">Lợi nhuận</span>
                            </div>
                        </div>
                        <div class="fin-chart-canvas-wrap">
                            <canvas id="chartRevenueCost"></canvas>
                        </div>
                    </div>

                    <%-- Cơ cấu doanh thu (Donut) --%>
                    <div class="fin-chart-panel fin-chart-small">
                        <div class="fin-panel-header">
                            <div class="fin-panel-header-left">
                                <h2 class="fin-panel-title">Cơ Cấu Doanh Thu</h2>
                                <p class="fin-panel-sub">Phân theo kênh bán hàng</p>
                            </div>
                        </div>
                        <div class="fin-chart-canvas-wrap fin-donut-wrap">
                            <canvas id="chartDonut"></canvas>
                        </div>
                        <div class="fin-donut-legend">
                            <c:forEach var="ch" items="${channelRevenue}" varStatus="s">
                                <div class="fin-donut-item">
                                    <span class="fin-donut-dot" style="background:${s.index == 0 ? '#006e2f' : '#2eb872'};"></span>
                                    <span class="fin-donut-label">${ch.channel}</span>
                                    <span class="fin-donut-pct">${ch.pct}%</span>
                                </div>
                            </c:forEach>
                        </div>
                    </div>

                </div>

                <%-- ══ BOTTOM ROW ══ --%>
                <div class="fin-bottom-row">

                    <%-- Phương thức thanh toán --%>
                    <div class="fin-card fin-payment-panel">
                        <div class="fin-panel-header">
                            <div class="fin-panel-header-left">
                                <h2 class="fin-panel-title">Phương Thức Thanh Toán</h2>
                                <p class="fin-panel-sub">Kỳ hiện tại</p>
                            </div>
                        </div>
                        <div class="fin-payment-list">
                            <c:forEach var="pm" items="${paymentStats}" varStatus="s">
                                <div class="fin-payment-row">
                                    <div class="fin-payment-icon-wrap ${s.index == 0 ? 'fin-pay-cash' : 'fin-pay-momo'}">
                                        <i class="fa-solid ${s.index == 0 ? 'fa-money-bills' : 'fa-mobile-screen-button'}"></i>
                                    </div>
                                    <div class="fin-payment-info">
                                        <span class="fin-payment-name">${pm.method}</span>
                                        <span class="fin-payment-count">${pm.count} giao dịch</span>
                                    </div>
                                    <div class="fin-payment-right">
                                        <span class="fin-payment-amount">
                                            <fmt:formatNumber value="${pm.amount}" type="number" maxFractionDigits="0"/>đ
                                        </span>
                                        <div class="fin-pay-bar-wrap">
                                            <div class="fin-pay-bar ${s.index == 1 ? 'fin-bar-momo' : ''}"
                                                 style="width: ${pm.barPct}%;"></div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                            <c:if test="${empty paymentStats}">
                                <div class="fin-empty-state">Chưa có dữ liệu giao dịch kỳ này.</div>
                            </c:if>
                        </div>
                    </div>

                    <%-- Top sản phẩm doanh thu --%>
                    <div class="fin-card fin-top-products-panel">
                        <div class="fin-panel-header">
                            <div class="fin-panel-header-left">
                                <h2 class="fin-panel-title">Top Sản Phẩm Doanh Thu</h2>
                                <p class="fin-panel-sub">Theo giá trị bán ra kỳ này</p>
                            </div>
                        </div>
                        <div class="fin-top-product-list">
                            <c:forEach var="prod" items="${topProducts}" varStatus="s">
                                <div class="fin-top-prod-row">
                                    <span class="fin-prod-rank
                                        ${s.index == 0 ? 'fin-rank-gold' : s.index == 1 ? 'fin-rank-silver' : s.index == 2 ? 'fin-rank-bronze' : ''}">
                                        ${s.index + 1}
                                    </span>
                                    <div class="fin-prod-info">
                                        <span class="fin-prod-name">${prod.productName}</span>
                                        <span class="fin-prod-qty">${prod.totalQty} ly</span>
                                    </div>
                                    <div class="fin-prod-right">
                                        <span class="fin-prod-revenue">
                                            <fmt:formatNumber value="${prod.totalRevenue}" type="number" maxFractionDigits="0"/>đ
                                        </span>
                                        <div class="fin-prod-bar-wrap">
                                            <div class="fin-prod-bar" style="width: ${prod.barPct}%;"></div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                            <c:if test="${empty topProducts}">
                                <div class="fin-empty-state">Chưa có dữ liệu sản phẩm kỳ này.</div>
                            </c:if>
                        </div>
                    </div>

                    <%-- Phiếu nhập kho gần đây --%>
                    <div class="fin-card fin-import-panel">
                        <div class="fin-panel-header">
                            <div class="fin-panel-header-left">
                                <h2 class="fin-panel-title">Chi Phí Nhập Kho</h2>
                                <p class="fin-panel-sub">Phiếu nhập gần đây nhất</p>
                            </div>
                            <a href="${pageContext.request.contextPath}/admin/ingredients" class="fin-see-all-link">
                                Xem tất cả <i class="fa-solid fa-arrow-right"></i>
                            </a>
                        </div>
                        <table class="fin-import-table">
                            <thead>
                                <tr>
                                    <th>Phiếu nhập</th>
                                    <th>Ngày nhận</th>
                                    <th>Nhà cung cấp</th>
                                    <th>Tổng tiền</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty recentImports}">
                                        <tr>
                                            <td colspan="4" class="fin-empty-state">Chưa có phiếu nhập nào.</td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="imp" items="${recentImports}">
                                            <tr>
                                                <td><span class="fin-import-code">${imp.importCode}</span></td>
                                                <td class="fin-import-date">${imp.receivedDate}</td>
                                                <td><span class="fin-import-badge fin-badge-material">${imp.supplierName}</span></td>
                                                <td class="fin-import-amount">
                                                    <fmt:formatNumber value="${imp.amount}" type="number" maxFractionDigits="0"/>đ
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>

                </div>

            </main>
        </div>

        <%-- ══ DỮ LIỆU CHO CHART.JS (inject từ JSTL) ══ --%>
        <script>
            /* ══════════════════════════════════════════
               CHART 1: Doanh Thu vs Chi Phí vs Lợi Nhuận (tháng hiện tại)
               ══════════════════════════════════════════ */
            (function () {
                const labels  = [];
                const revenue = [];
                const cost    = [];
                const profit  = [];

                <c:forEach var="w" items="${weeklyBreakdown}">
                    labels.push('${w.label}');
                    revenue.push(${w.revenue});
                    cost.push(${w.cost});
                    profit.push(${w.profit});
                </c:forEach>

                // Nếu không có dữ liệu tuần thì hiển thị placeholder
                if (labels.length === 0) {
                    labels.push('Chưa có dữ liệu');
                    revenue.push(0); cost.push(0); profit.push(0);
                }

                const fmt = v => new Intl.NumberFormat('vi-VN').format(Math.round(v / 1000000) * 1000000 / 1000000) + 'M đ';

                const ctx = document.getElementById('chartRevenueCost').getContext('2d');
                new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels,
                        datasets: [
                            {
                                label: 'Doanh thu',
                                data: revenue,
                                backgroundColor: 'rgba(0, 110, 47, 0.85)',
                                borderRadius: 6, borderSkipped: false, order: 2
                            },
                            {
                                label: 'Chi phí',
                                data: cost,
                                backgroundColor: 'rgba(220, 53, 69, 0.75)',
                                borderRadius: 6, borderSkipped: false, order: 2
                            },
                            {
                                label: 'Lợi nhuận',
                                data: profit,
                                type: 'line',
                                borderColor: '#e67e00',
                                backgroundColor: 'rgba(230, 126, 0, 0.1)',
                                borderWidth: 2.5,
                                pointBackgroundColor: '#e67e00',
                                pointRadius: 5, pointHoverRadius: 7,
                                tension: 0.4, fill: true, order: 1
                            }
                        ]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        interaction: { mode: 'index', intersect: false },
                        plugins: {
                            legend: { display: false },
                            tooltip: { callbacks: { label: c => ' ' + c.dataset.label + ': ' + fmt(c.raw) } }
                        },
                        scales: {
                            x: { grid: { display: false }, ticks: { font: { size: 12, family: 'Inter' } } },
                            y: {
                                grid: { color: 'rgba(0,0,0,0.05)' },
                                ticks: { font: { size: 11, family: 'Inter' }, callback: v => fmt(v) }
                            }
                        }
                    }
                });
            })();

            /* ══════════════════════════════════════════
               CHART 2: Donut — Cơ cấu doanh thu
               ══════════════════════════════════════════ */
            (function () {
                const labels = [];
                const data   = [];

                <c:forEach var="ch" items="${channelRevenue}">
                    labels.push('${ch.channel}');
                    data.push(${ch.pct});
                </c:forEach>

                if (labels.length === 0) { labels.push('Chưa có dữ liệu'); data.push(100); }

                const ctx = document.getElementById('chartDonut').getContext('2d');
                new Chart(ctx, {
                    type: 'doughnut',
                    data: {
                        labels,
                        datasets: [{
                            data,
                            backgroundColor: ['#006e2f', '#2eb872'],
                            borderWidth: 0,
                            hoverOffset: 8
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        cutout: '68%',
                        plugins: {
                            legend: { display: false },
                            tooltip: { callbacks: { label: c => ' ' + c.label + ': ' + c.raw + '%' } }
                        }
                    }
                });
            })();
        </script>

    </body>
</html>
