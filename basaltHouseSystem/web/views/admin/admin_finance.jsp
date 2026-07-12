<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

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
                        <div class="fin-period-tabs">
                            <button class="fin-period-btn" id="btnWeek" onclick="switchPeriod('week')">7 Ngày</button>
                            <button class="fin-period-btn active" id="btnMonth" onclick="switchPeriod('month')">Tháng này</button>
                            <button class="fin-period-btn" id="btnYear" onclick="switchPeriod('year')">Năm nay</button>
                        </div>
                        <button class="fin-export-btn" id="btnExportReport" title="Xuất báo cáo Excel">
                            <i class="fa-solid fa-file-arrow-down"></i>
                            Xuất báo cáo
                        </button>
                    </div>
                </div>

                <%-- ══ KPI CARDS ROW 1 ══ --%>
                <div class="fin-kpi-grid">

                    <div class="fin-kpi-card fin-kpi-revenue">
                        <div class="fin-kpi-top">
                            <div class="fin-kpi-icon-wrap fin-icon-revenue">
                                <i class="fa-solid fa-sack-dollar"></i>
                            </div>
                            <div class="fin-kpi-trend fin-trend-up">
                                <i class="fa-solid fa-arrow-trend-up"></i>
                                +12.4%
                            </div>
                        </div>
                        <div class="fin-kpi-value" id="kpiRevenue">148,320,000 đ</div>
                        <div class="fin-kpi-label">Tổng Doanh Thu</div>
                        <div class="fin-kpi-sub">So với kỳ trước</div>
                        <div class="fin-kpi-progress-bar">
                            <div class="fin-kpi-progress-fill" style="width: 74%;"></div>
                        </div>
                    </div>

                    <div class="fin-kpi-card fin-kpi-cost">
                        <div class="fin-kpi-top">
                            <div class="fin-kpi-icon-wrap fin-icon-cost">
                                <i class="fa-solid fa-box-open"></i>
                            </div>
                            <div class="fin-kpi-trend fin-trend-down">
                                <i class="fa-solid fa-arrow-trend-down"></i>
                                -3.1%
                            </div>
                        </div>
                        <div class="fin-kpi-value fin-value-cost" id="kpiCost">52,480,000 đ</div>
                        <div class="fin-kpi-label">Chi Phí Nhập Kho</div>
                        <div class="fin-kpi-sub">Giảm so với kỳ trước</div>
                        <div class="fin-kpi-progress-bar">
                            <div class="fin-kpi-progress-fill fin-prog-cost" style="width: 35%;"></div>
                        </div>
                    </div>

                    <div class="fin-kpi-card fin-kpi-profit">
                        <div class="fin-kpi-top">
                            <div class="fin-kpi-icon-wrap fin-icon-profit">
                                <i class="fa-solid fa-coins"></i>
                            </div>
                            <div class="fin-kpi-trend fin-trend-up">
                                <i class="fa-solid fa-arrow-trend-up"></i>
                                +18.7%
                            </div>
                        </div>
                        <div class="fin-kpi-value fin-value-profit" id="kpiProfit">95,840,000 đ</div>
                        <div class="fin-kpi-label">Lợi Nhuận Gộp</div>
                        <div class="fin-kpi-sub">Biên lợi nhuận 64.6%</div>
                        <div class="fin-kpi-progress-bar">
                            <div class="fin-kpi-progress-fill fin-prog-profit" style="width: 64%;"></div>
                        </div>
                    </div>

                    <div class="fin-kpi-card fin-kpi-orders">
                        <div class="fin-kpi-top">
                            <div class="fin-kpi-icon-wrap fin-icon-orders">
                                <i class="fa-solid fa-receipt"></i>
                            </div>
                            <div class="fin-kpi-trend fin-trend-up">
                                <i class="fa-solid fa-arrow-trend-up"></i>
                                +6.2%
                            </div>
                        </div>
                        <div class="fin-kpi-value" id="kpiOrders">1,247</div>
                        <div class="fin-kpi-label">Tổng Đơn Hàng</div>
                        <div class="fin-kpi-sub">TB 118,940đ / đơn</div>
                        <div class="fin-kpi-progress-bar">
                            <div class="fin-kpi-progress-fill fin-prog-orders" style="width: 82%;"></div>
                        </div>
                    </div>

                </div>

                <%-- ══ CHARTS ROW ══ --%>
                <div class="fin-charts-row">

                    <%-- Doanh Thu vs Chi Phí --%>
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

                    <%-- Cơ cấu doanh thu --%>
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
                            <div class="fin-donut-item">
                                <span class="fin-donut-dot" style="background:#006e2f;"></span>
                                <span class="fin-donut-label">Tại quầy</span>
                                <span class="fin-donut-pct">52%</span>
                            </div>
                            <div class="fin-donut-item">
                                <span class="fin-donut-dot" style="background:#2eb872;"></span>
                                <span class="fin-donut-label">Online</span>
                                <span class="fin-donut-pct">31%</span>
                            </div>
                            <div class="fin-donut-item">
                                <span class="fin-donut-dot" style="background:#7c3aed;"></span>
                                <span class="fin-donut-label">POS / Bàn</span>
                                <span class="fin-donut-pct">17%</span>
                            </div>
                        </div>
                    </div>

                </div>

                <%-- ══ BOTTOM ROW: Payment methods + Top revenue products ══ --%>
                <div class="fin-bottom-row">

                    <%-- Phương thức thanh toán --%>
                    <div class="fin-card fin-payment-panel">
                        <div class="fin-panel-header">
                            <div class="fin-panel-header-left">
                                <h2 class="fin-panel-title">Phương Thức Thanh Toán</h2>
                                <p class="fin-panel-sub">Tháng này</p>
                            </div>
                        </div>
                        <div class="fin-payment-list">

                            <div class="fin-payment-row">
                                <div class="fin-payment-icon-wrap fin-pay-cash">
                                    <i class="fa-solid fa-money-bills"></i>
                                </div>
                                <div class="fin-payment-info">
                                    <span class="fin-payment-name">Tiền mặt</span>
                                    <span class="fin-payment-count">684 giao dịch</span>
                                </div>
                                <div class="fin-payment-right">
                                    <span class="fin-payment-amount">78,200,000đ</span>
                                    <div class="fin-pay-bar-wrap">
                                        <div class="fin-pay-bar" style="width: 53%;"></div>
                                    </div>
                                </div>
                            </div>

                            <div class="fin-payment-row">
                                <div class="fin-payment-icon-wrap fin-pay-momo">
                                    <i class="fa-solid fa-mobile-screen-button"></i>
                                </div>
                                <div class="fin-payment-info">
                                    <span class="fin-payment-name">MoMo</span>
                                    <span class="fin-payment-count">412 giao dịch</span>
                                </div>
                                <div class="fin-payment-right">
                                    <span class="fin-payment-amount">49,440,000đ</span>
                                    <div class="fin-pay-bar-wrap">
                                        <div class="fin-pay-bar fin-bar-momo" style="width: 33%;"></div>
                                    </div>
                                </div>
                            </div>

                            <div class="fin-payment-row">
                                <div class="fin-payment-icon-wrap fin-pay-transfer">
                                    <i class="fa-solid fa-building-columns"></i>
                                </div>
                                <div class="fin-payment-info">
                                    <span class="fin-payment-name">Chuyển khoản</span>
                                    <span class="fin-payment-count">151 giao dịch</span>
                                </div>
                                <div class="fin-payment-right">
                                    <span class="fin-payment-amount">20,680,000đ</span>
                                    <div class="fin-pay-bar-wrap">
                                        <div class="fin-pay-bar fin-bar-transfer" style="width: 14%;"></div>
                                    </div>
                                </div>
                            </div>

                        </div>
                    </div>

                    <%-- Top sản phẩm doanh thu cao nhất --%>
                    <div class="fin-card fin-top-products-panel">
                        <div class="fin-panel-header">
                            <div class="fin-panel-header-left">
                                <h2 class="fin-panel-title">Top Sản Phẩm Doanh Thu</h2>
                                <p class="fin-panel-sub">Theo giá trị bán ra tháng này</p>
                            </div>
                        </div>
                        <div class="fin-top-product-list">

                            <div class="fin-top-prod-row">
                                <span class="fin-prod-rank fin-rank-gold">1</span>
                                <div class="fin-prod-info">
                                    <span class="fin-prod-name">Cà Phê Sữa Đá Truyền Thống</span>
                                    <span class="fin-prod-qty">312 ly</span>
                                </div>
                                <div class="fin-prod-right">
                                    <span class="fin-prod-revenue">24,960,000đ</span>
                                    <div class="fin-prod-bar-wrap">
                                        <div class="fin-prod-bar" style="width: 100%;"></div>
                                    </div>
                                </div>
                            </div>

                            <div class="fin-top-prod-row">
                                <span class="fin-prod-rank fin-rank-silver">2</span>
                                <div class="fin-prod-info">
                                    <span class="fin-prod-name">Bạc Xỉu</span>
                                    <span class="fin-prod-qty">278 ly</span>
                                </div>
                                <div class="fin-prod-right">
                                    <span class="fin-prod-revenue">22,240,000đ</span>
                                    <div class="fin-prod-bar-wrap">
                                        <div class="fin-prod-bar" style="width: 89%;"></div>
                                    </div>
                                </div>
                            </div>

                            <div class="fin-top-prod-row">
                                <span class="fin-prod-rank fin-rank-bronze">3</span>
                                <div class="fin-prod-info">
                                    <span class="fin-prod-name">Matcha Latte</span>
                                    <span class="fin-prod-qty">241 ly</span>
                                </div>
                                <div class="fin-prod-right">
                                    <span class="fin-prod-revenue">21,690,000đ</span>
                                    <div class="fin-prod-bar-wrap">
                                        <div class="fin-prod-bar" style="width: 78%;"></div>
                                    </div>
                                </div>
                            </div>

                            <div class="fin-top-prod-row">
                                <span class="fin-prod-rank">4</span>
                                <div class="fin-prod-info">
                                    <span class="fin-prod-name">Trà Sữa Trân Châu</span>
                                    <span class="fin-prod-qty">197 ly</span>
                                </div>
                                <div class="fin-prod-right">
                                    <span class="fin-prod-revenue">16,745,000đ</span>
                                    <div class="fin-prod-bar-wrap">
                                        <div class="fin-prod-bar" style="width: 63%;"></div>
                                    </div>
                                </div>
                            </div>

                            <div class="fin-top-prod-row">
                                <span class="fin-prod-rank">5</span>
                                <div class="fin-prod-info">
                                    <span class="fin-prod-name">Sinh Tố Xoài</span>
                                    <span class="fin-prod-qty">163 ly</span>
                                </div>
                                <div class="fin-prod-right">
                                    <span class="fin-prod-revenue">13,040,000đ</span>
                                    <div class="fin-prod-bar-wrap">
                                        <div class="fin-prod-bar" style="width: 48%;"></div>
                                    </div>
                                </div>
                            </div>

                        </div>
                    </div>

                    <%-- Nhập kho gần đây --%>
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
                                    <th>Ngày</th>
                                    <th>Danh mục</th>
                                    <th>Tổng tiền</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><span class="fin-import-code">#PN-2407</span></td>
                                    <td class="fin-import-date">12/07/2026</td>
                                    <td><span class="fin-import-badge fin-badge-coffee">Cà phê</span></td>
                                    <td class="fin-import-amount">8,400,000đ</td>
                                </tr>
                                <tr>
                                    <td><span class="fin-import-code">#PN-2406</span></td>
                                    <td class="fin-import-date">10/07/2026</td>
                                    <td><span class="fin-import-badge fin-badge-dairy">Sữa & kem</span></td>
                                    <td class="fin-import-amount">5,200,000đ</td>
                                </tr>
                                <tr>
                                    <td><span class="fin-import-code">#PN-2405</span></td>
                                    <td class="fin-import-date">08/07/2026</td>
                                    <td><span class="fin-import-badge fin-badge-material">Nguyên liệu</span></td>
                                    <td class="fin-import-amount">12,750,000đ</td>
                                </tr>
                                <tr>
                                    <td><span class="fin-import-code">#PN-2404</span></td>
                                    <td class="fin-import-date">05/07/2026</td>
                                    <td><span class="fin-import-badge fin-badge-coffee">Cà phê</span></td>
                                    <td class="fin-import-amount">7,600,000đ</td>
                                </tr>
                                <tr>
                                    <td><span class="fin-import-code">#PN-2403</span></td>
                                    <td class="fin-import-date">02/07/2026</td>
                                    <td><span class="fin-import-badge fin-badge-dairy">Sữa & kem</span></td>
                                    <td class="fin-import-amount">4,900,000đ</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                </div>

            </main>
        </div>

        <script>
            /* ══════════════════════════════════════════
               CHART 1: Doanh Thu vs Chi Phí vs Lợi Nhuận
               ══════════════════════════════════════════ */
            (function () {
                const ctx = document.getElementById('chartRevenueCost').getContext('2d');

                const labels   = ['Tuần 1', 'Tuần 2', 'Tuần 3', 'Tuần 4'];
                const revenue  = [32400000, 38100000, 41200000, 36620000];
                const cost     = [11200000, 14300000, 15600000, 11380000];
                const profit   = revenue.map((r, i) => r - cost[i]);

                const fmt = v => new Intl.NumberFormat('vi-VN').format(v / 1000000) + 'M đ';

                new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels,
                        datasets: [
                            {
                                label: 'Doanh thu',
                                data: revenue,
                                backgroundColor: 'rgba(0, 110, 47, 0.85)',
                                borderRadius: 6,
                                borderSkipped: false,
                                order: 2
                            },
                            {
                                label: 'Chi phí',
                                data: cost,
                                backgroundColor: 'rgba(220, 53, 69, 0.75)',
                                borderRadius: 6,
                                borderSkipped: false,
                                order: 2
                            },
                            {
                                label: 'Lợi nhuận',
                                data: profit,
                                type: 'line',
                                borderColor: '#e67e00',
                                backgroundColor: 'rgba(230, 126, 0, 0.1)',
                                borderWidth: 2.5,
                                pointBackgroundColor: '#e67e00',
                                pointRadius: 5,
                                pointHoverRadius: 7,
                                tension: 0.4,
                                fill: true,
                                order: 1
                            }
                        ]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        interaction: { mode: 'index', intersect: false },
                        plugins: {
                            legend: { display: false },
                            tooltip: {
                                callbacks: {
                                    label: ctx => ' ' + ctx.dataset.label + ': ' + fmt(ctx.raw)
                                }
                            }
                        },
                        scales: {
                            x: { grid: { display: false }, ticks: { font: { size: 12, family: 'Inter' } } },
                            y: {
                                grid: { color: 'rgba(0,0,0,0.05)' },
                                ticks: {
                                    font: { size: 11, family: 'Inter' },
                                    callback: v => fmt(v)
                                }
                            }
                        }
                    }
                });
            })();

            /* ══════════════════════════════════════════
               CHART 2: Donut — Cơ cấu doanh thu
               ══════════════════════════════════════════ */
            (function () {
                const ctx = document.getElementById('chartDonut').getContext('2d');
                new Chart(ctx, {
                    type: 'doughnut',
                    data: {
                        labels: ['Tại quầy', 'Online', 'POS / Bàn'],
                        datasets: [{
                            data: [52, 31, 17],
                            backgroundColor: ['#006e2f', '#2eb872', '#7c3aed'],
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
                            tooltip: {
                                callbacks: { label: ctx => ' ' + ctx.label + ': ' + ctx.raw + '%' }
                            }
                        }
                    }
                });
            })();

            /* ══════════════════════════════════════════
               PERIOD TABS  (UI-only, no backend fetch)
               ══════════════════════════════════════════ */
            function switchPeriod(period) {
                document.querySelectorAll('.fin-period-btn').forEach(b => b.classList.remove('active'));
                document.getElementById('btn' + period.charAt(0).toUpperCase() + period.slice(1)).classList.add('active');
            }
        </script>

    </body>
</html>
