<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Basalt House - Admin Dashboard</title>
        <link rel="stylesheet" type="text/css" href="/basaltHouseSystem/css/admin/admin_dashboard.css">
        <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
    </head>
    <body>
        <header class="top-header">
            <div class="logo">Basalt <span>House</span></div>
            <div class="header-buttons">
            </div>
        </header>

        <div class="app-container">
            
            <jsp:include page="sidebar.jsp" />

            <main class="main-content">
                <div class="server-time">
                    Thời gian máy chủ: <fmt:formatDate value="<%= new java.util.Date() %>" pattern="dd/MM/yyyy - HH:mm" />
                </div>

                <%-- ══ KPI CARDS ══ --%>
                <div class="dashboard-grid">
                    <div class="card card-revenue">
                        <h5>DOANH THU HÔM NAY</h5>
                        <h3><fmt:formatNumber value="${data.revenueToday}" pattern="#,###" /> đ</h3>
                        <p class="trend">↗ Tăng liên tục từ quầy + website</p>
                    </div>
                    <div class="card card-orders">
                        <h5>ĐƠN HÀNG MỚI</h5>
                        <h3>${data.totalOrdersToday} Đơn</h3>
                        <p class="sub">Xếp hàng chuẩn bị pha chế</p>
                    </div>
                    <div class="card card-shipping">
                        <h5>ĐƠN ĐANG GIAO</h5>
                        <h3>${data.deliveringOrders} Đơn</h3>
                        <p class="sub">Vận hành ngoài đường</p>
                    </div>
                    <div class="card card-alert">
                        <h5>CẢNH BÁO KHO THẤP</h5>
                        <c:choose>
                            <c:when test="${data.lowStockCount > 0}">
                                <h3 class="text-danger">${data.lowStockCount} Vật liệu</h3>
                                <p class="text-danger">⚠ Cần lên lịch nhập hàng ngay</p>
                            </c:when>
                            <c:otherwise>
                                <h3 style="color:#1e7e34">0 Vật liệu</h3>
                                <p style="color:#1e7e34">✓ Tất cả nguyên liệu ổn định</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <%-- ══ BIỂU ĐỒ DOANH THU + CẢNH BÁO ══ --%>
                <div class="section-title">Doanh thu & Cảnh báo</div>
                <div class="row-chart-alert">

                    <%-- Biểu đồ 7 ngày --%>
                    <div class="card-chart">
                        <h4>📈 Doanh thu 7 ngày gần nhất</h4>
                        <div class="chart-meta">
                            <span class="chart-total">
                                <fmt:formatNumber value="${data.last7DaysTotal}" pattern="#,###" /> đ
                            </span>
                            <c:choose>
                                <c:when test="${data.revenueGrowthPercent >= 0}">
                                    <span class="growth-badge growth-pos">
                                        +<fmt:formatNumber value="${data.revenueGrowthPercent}" maxFractionDigits="1" />% tuần trước
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <span class="growth-badge growth-neg">
                                        <fmt:formatNumber value="${data.revenueGrowthPercent}" maxFractionDigits="1" />% tuần trước
                                    </span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <canvas id="revenueChart" height="130"></canvas>
                    </div>

                    <%-- Cảnh báo hệ thống --%>
                    <div class="card-alerts">
                        <h4>🔔 Cảnh báo hệ thống</h4>

                        <%-- Nguyên liệu hết --%>
                        <c:forEach var="item" items="${data.lowStockAlerts}">
                            <div class="alert-item">
                                <div class="alert-dot dot-danger"></div>
                                <div class="alert-body">
                                    <div class="alert-name">${item.ingredientName}</div>
                                    <div class="alert-desc">
                                        Còn <fmt:formatNumber value="${item.stockQuantity}" maxFractionDigits="0" /> ${item.unit}
                                        — tối thiểu <fmt:formatNumber value="${item.minStockQuantity}" maxFractionDigits="0" /> ${item.unit}
                                    </div>
                                </div>
                                <span class="alert-badge badge-danger">Hết hàng</span>
                            </div>
                        </c:forEach>

                        <%-- Phiếu nhập Pending --%>
                        <c:if test="${data.pendingImportCount > 0}">
                            <div class="alert-item">
                                <div class="alert-dot dot-info"></div>
                                <div class="alert-body">
                                    <div class="alert-name">${data.pendingImportCount} phiếu nhập hàng</div>
                                    <div class="alert-desc">Đang chờ xác nhận nhận hàng</div>
                                </div>
                                <span class="alert-badge badge-info">Pending</span>
                            </div>
                        </c:if>

                        <%-- PendingRegistrations --%>
                        <c:if test="${data.pendingRegistrationCount > 0}">
                            <div class="alert-item">
                                <div class="alert-dot dot-info"></div>
                                <div class="alert-body">
                                    <div class="alert-name">${data.pendingRegistrationCount} đăng ký mới</div>
                                    <div class="alert-desc">Tài khoản chờ xác thực OTP</div>
                                </div>
                                <span class="alert-badge badge-info">Mới</span>
                            </div>
                        </c:if>

                        <c:if test="${empty data.lowStockAlerts
                                      && data.pendingImportCount == 0
                                      && data.pendingRegistrationCount == 0}">
                              <div class="no-alert">✅ Không có cảnh báo nào</div>
                        </c:if>
                    </div>

                </div>

                <%-- ══ TOP SẢN PHẨM + ĐƠN HÀNG MỚI NHẤT ══ --%>
                <div class="section-title">Sản phẩm & Đơn hàng</div>
                <div class="row-2col">

                    <%-- Top sản phẩm --%>
                    <div class="card-top">
                        <h4>🏆 Top sản phẩm bán chạy</h4>
                        <c:set var="maxSold" value="${data.topProducts[0].totalSold}" />
                        <c:forEach var="p" items="${data.topProducts}" varStatus="st">
                            <div class="bar-row">
                                <div class="bar-name" title="${p.productName}">${p.productName}</div>
                                <div class="bar-track">
                                    <div class="bar-fill bar-color${st.index + 1}"
                                         style="width:${maxSold > 0 ? (p.totalSold * 100 / maxSold) : 0}%"></div>
                                </div>
                                <div class="bar-val">${p.totalSold}</div>
                            </div>
                        </c:forEach>
                        <c:if test="${empty data.topProducts}">
                            <div class="no-alert">Chưa có dữ liệu</div>
                        </c:if>
                    </div>

                    <%-- Đơn hàng mới nhất --%>
                    <div class="card-table">
                        <h4>📋 Đơn hàng mới nhất</h4>
                        <table>
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Loại</th>
                                    <th>Trạng thái</th>
                                    <th>Thanh toán</th>
                                    <th style="text-align:right">Tổng</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="o" items="${data.recentOrders}">
                                    <tr>
                                        <td><strong>#${o.orderId}</strong></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${o.orderType == 'Online'}">
                                                    <span class="tbadge tbadge-online">Online</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="tbadge tbadge-pos">POS</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${o.orderStatus == 'Done'}">
                                                    <span class="tbadge tbadge-done">Hoàn thành</span>
                                                </c:when>
                                                <c:when test="${o.orderStatus == 'Delivering'}">
                                                    <span class="tbadge tbadge-deliver">Đang giao</span>
                                                </c:when>
                                                <c:when test="${o.orderStatus == 'Pending'}">
                                                    <span class="tbadge tbadge-pending">Chờ xử lý</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="tbadge tbadge-pos">${o.orderStatus}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${o.paymentStatus == 'Paid'}">
                                                    <span class="tbadge tbadge-paid">Đã TT</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="tbadge tbadge-unpaid">Chưa TT</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-right">
                                            <fmt:formatNumber value="${o.finalAmount}" pattern="#,###" /> đ
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty data.recentOrders}">
                                    <tr><td colspan="5" class="empty-row">Chưa có đơn hàng</td></tr>
                                </c:if>
                            </tbody>
                        </table>
                        <a href="don-hang" class="view-all">Xem tất cả đơn hàng →</a>
                    </div>

                </div>

            </main>
        </div>

        <%-- ══ CHART.JS RENDERING ══ --%>
        <script>
            (function () {
                const labels = [<c:forEach var="d" items="${data.last7Days}" varStatus="st">'${d.day}'<c:if test="${!st.last}">,</c:if></c:forEach>];
                        const values = [<c:forEach var="d" items="${data.last7Days}" varStatus="st">${d.revenue}<c:if test="${!st.last}">,</c:if></c:forEach>];

                const ctx = document.getElementById('revenueChart');
                if (!ctx || labels.length === 0)
                    return;

                new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels,
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