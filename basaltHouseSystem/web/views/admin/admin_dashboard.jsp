<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- ══════════════════════════════════════════════════════════════════════════
     TỰ ĐỘNG GIẢI NÉN MAP "data" TỪ SERVLET ĐỂ HIỂN THỊ LÊN UI KHÔNG CẦN SỬA CODE JAVA
     ══════════════════════════════════════════════════════════════════════════ --%>
<c:set var="todayRevenue" value="${data.todayRevenue}" />
<c:set var="revenueTrend" value="${data.revenueTrend}" />
<c:set var="todayOrdersCount" value="${data.todayOrdersCount}" />
<c:set var="ordersTrend" value="${data.ordersTrend}" />
<c:set var="averageBillValue" value="${data.averageBillValue}" />
<c:set var="activeTablesCount" value="${data.activeTablesCount}" />
<c:set var="topSellingProducts" value="${data.topSellingProducts}" />
<c:set var="revenueLast7Days" value="${data.revenueLast7Days}" />

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Basalt House - Admin Dashboard</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_common.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_dashboard.css">
        <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
    </head>
    <body>
        <jsp:include page="header.jsp" />

        <div class="app-container">
            
            <jsp:include page="sidebar.jsp" />

            <main class="main-content">
                <%-- ══ KPI CARDS ══ --%>
                <div class="metrics-grid">
                    <div class="metric-card card-revenue">
                        <div class="metric-header">
                            <span class="metric-title">Doanh thu hôm nay</span>
                            <span class="metric-icon">💰</span>
                        </div>
                        <div class="metric-value">
                            <fmt:formatNumber value="${todayRevenue}" type="currency" currencySymbol="đ" maxFractionDigits="0"/>
                        </div>
                        <div class="metric-trend ${revenueTrend >= 0 ? 'trend-up' : 'trend-down'}">
                            ${revenueTrend >= 0 ? '▲' : '▼'} ${revenueTrend >= 0 ? revenueTrend : -revenueTrend}% so với hôm qua
                        </div>
                    </div>

                    <div class="metric-card card-orders">
                        <div class="metric-header">
                            <span class="metric-title">Đơn hàng mới</span>
                            <span class="metric-icon">📦</span>
                        </div>
                        <div class="metric-value">${todayOrdersCount}</div>
                        <div class="metric-trend ${ordersTrend >= 0 ? 'trend-up' : 'trend-down'}">
                            ${ordersTrend >= 0 ? '▲' : '▼'} ${ordersTrend >= 0 ? ordersTrend : -ordersTrend}% so với hôm qua
                        </div>
                    </div>

                    <div class="metric-card card-avg-bill">
                        <div class="metric-header">
                            <span class="metric-title">Giá trị đơn TB</span>
                            <span class="metric-icon">📊</span>
                        </div>
                        <div class="metric-value">
                            <fmt:formatNumber value="${averageBillValue}" type="currency" currencySymbol="đ" maxFractionDigits="0"/>
                        </div>
                        <div class="metric-trend text-muted">Hệ thống thời gian thực</div>
                    </div>

                    <div class="metric-card card-alerts">
                        <div class="metric-header">
                            <span class="metric-title">Bàn đang mở</span>
                            <span class="metric-icon">🪑</span>
                        </div>
                        <div class="metric-value">${activeTablesCount}</div>
                        <div class="metric-trend trend-up">Đang hoạt động</div>
                    </div>
                </div>

                <%-- ══ CHARTS ROW ══ --%>
                <div class="dashboard-charts-row">
                    <div class="chart-panel-box">
                        <div class="panel-title-wrapper">
                            <h3>Doanh Thu 7 Ngày Gần Nhất</h3>
                        </div>
                        <canvas id="revenueChart" style="width: 100%; height: 300px;"></canvas>
                    </div>

                    <div class="list-panel-box">
                        <div class="panel-title-wrapper">
                            <h3>Sản Phẩm Bán Chạy (Tháng này)</h3>
                        </div>
                        <div class="ranking-list-stack">
                            <c:forEach var="item" items="${topSellingProducts}" varStatus="status">
                                <div class="ranking-item-row">
                                    <div class="item-info-meta">
                                        <span class="item-rank-badge">${status.index + 1}</span>
                                        <span class="item-text-name">${item.productName}</span>
                                    </div>
                                    <span class="item-sales-count">${item.totalSold} ly</span>
                                </div>
                            </c:forEach>
                            <c:if test="${empty topSellingProducts}">
                                <div style="text-align: center; color: #999; padding: 20px;">Chưa có dữ liệu bán hàng</div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </main>
        </div>

        <script>
            (function () {
                const labels = [];
                const values = [];
                <c:forEach var="entry" items="${revenueLast7Days}">
                    labels.push("${entry.key}");
                    values.push(${entry.value});
                </c:forEach>

                if(labels.length === 0) {
                    labels.push("Chưa có dữ liệu");
                    values.push(0);
                }

                const ctx = document.getElementById('revenueChart').getContext('2d');
                new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: labels,
                        datasets: [{
                                label: 'Doanh thu (đ)',
                                data: values,
                                backgroundColor: labels.map((_, i) =>
                                    i === labels.length - 1 ? '#006644' : '#b2d8c8'),
                                borderRadius: 5,
                                borderSkipped: false
                            }]
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            legend: {display: false},
                            tooltip: {
                                callbacks: {
                                    label: ctx => new Intl.NumberFormat('vi-VN').format(ctx.raw) + ' đ'
                                }
                            }
                        },
                        scales: {
                            x: {grid: {display: false}, ticks: {font: {size: 11}}},
                            y: {
                                grid: {color: 'rgba(0,0,0,.05)'},
                                ticks: {
                                    font: {size: 11},
                                    callback: v => new Intl.NumberFormat('vi-VN').format(v) + ' đ'
                                }
                            }
                        }
                    }
                });
            })();
        </script>
    </body>
</html>