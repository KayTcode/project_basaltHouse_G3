<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<link href="${pageContext.request.contextPath}/css/Staff/SaleHistory.css?v=20260709-1" rel="stylesheet">

<section class="staff-view active" id="salesHistoryView">
    <section class="sales-audit-grid" aria-label="Tổng quan bán hàng">
        <div class="sales-audit-card">
            <span class="material-symbols-outlined">shopping_cart</span>
            <div>
                <p>Sản phẩm đã bán</p>
                <strong><c:out value="${salesAudit.totalSoldCups}"/> ly</strong>
            </div>
        </div>
        <div class="sales-audit-card">
            <span class="material-symbols-outlined">payments</span>
            <div>
                <p>Tổng tiền</p>
                <strong><c:out value="${salesAudit.totalRevenueText}"/></strong>
            </div>
        </div>
        <div class="sales-audit-card">
            <span class="material-symbols-outlined">inventory</span>
            <div>
                <p>Nguyên liệu có dùng</p>
                <strong><c:out value="${salesAudit.usedIngredientCount}"/></strong>
            </div>
        </div>
        <div class="sales-audit-card">
            <span class="material-symbols-outlined">fact_check</span>
            <div>
                <p>Cần xem lại</p>
                <strong><c:out value="${salesAudit.auditWarningCount}"/></strong>
            </div>
        </div>
    </section>

  

    <section class="panel history-panel">
        <div class="panel-header">
            <div>
                <h2>Sản phẩm bán ra ngày <c:out value="${salesAudit.auditDateOnlyText}"/></h2>
            </div>
            <form class="audit-date-form" method="get" action="${pageContext.request.contextPath}/staff/sales-history">
                <div class="search-box history-search">
                    <span class="material-symbols-outlined">calendar_today</span>
                    <input type="date"
                           name="auditDate"
                           value="${salesAudit.auditDateInput}"
                           max="${salesAudit.todayDateInput}"
                           onchange="this.form.requestSubmit()"
                           required>
                </div>
                <button class="audit-date-btn" type="submit" title="Tìm theo ngày" aria-label="Tìm theo ngày">
                    <span class="material-symbols-outlined">search</span>
                </button>
            </form>
        </div>

        <div class="table-wrap">
            <table class="audit-table">
                <thead>
                    <tr>
                        <th>Sản phẩm</th>
                        <th>Kích cỡ</th>
                        <th>Đã bán</th>
                        <th>Đơn giá</th>
                        <th>Doanh thu</th>
                        <th>Công thức/ly</th>
                        <th>Tổng nguyên liệu dự kiến</th>
                        <th>Trạng thái</th>
                    </tr>
                </thead>
                <tbody id="salesProductRows">
                    <c:choose>
                        <c:when test="${empty salesAudit.productSales}">
                            <tr>
                                <td class="empty-cell" colspan="8">Không có đơn đã thanh toán trong ngày <c:out value="${salesAudit.auditDateOnlyText}"/> để kiểm kê.</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="sale" items="${salesAudit.productSales}">
                                <tr class="sales-product-row">
                                    <td><div class="ingredient-name"><c:out value="${sale.productName}"/></div></td>
                                    <td><c:out value="${sale.sizeName}"/></td>
                                    <td><strong><c:out value="${sale.soldQuantity}"/> ly</strong></td>
                                    <td><c:out value="${sale.unitPriceText}"/></td>
                                    <td><strong><c:out value="${sale.revenueText}"/></strong></td>
                                    <td><div class="audit-formula"><c:out value="${sale.recipeText}"/></div></td>
                                    <td><div class="audit-formula"><c:out value="${sale.expectedUsageText}"/></div></td>
                                    <td>
                                        <span class="audit-status ${sale.statusClass}">
                                            <span class="material-symbols-outlined"><c:out value="${sale.statusIcon}"/></span>
                                            <c:choose>
                                                <c:when test="${sale.statusClass eq 'danger'}">Thiếu công thức</c:when>
                                                <c:otherwise>Đủ công thức</c:otherwise>
                                            </c:choose>
                                        </span>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
        <div class="table-footer">
            <span class="result-count" id="salesProductResultText">0 dòng bán hàng</span>
            <div class="pagination">
                <button type="button" class="page-btn" id="salesProductPrevBtn" onclick="changeSalesProductPage(-1)" aria-label="Trang trước">
                    <span class="material-symbols-outlined">chevron_left</span>
                </button>
                <span class="page-indicator" id="salesProductPageText">Trang 1 / 1</span>
                <button type="button" class="page-btn" id="salesProductNextBtn" onclick="changeSalesProductPage(1)" aria-label="Trang sau">
                    <span class="material-symbols-outlined">chevron_right</span>
                </button>
            </div>
        </div>
    </section>

    <section class="panel history-panel" style="margin-top:18px;">
        <div class="panel-header compact">
            <div>
                <h2>Đối chiếu nguyên liệu</h2>
                <p>Tính theo công thức Product + Size và lượng nhập đã nhận trong ngày kiểm kê.</p>
            </div>
        </div>
        <div class="table-wrap">
            <table class="audit-table">
                <thead>
                    <tr>
                        <th>Nguyên liệu</th>
                        <th>Đã dùng theo sản phẩm/size</th>
                        <th>Tổng đã dùng</th>
                        <th>Tồn đầu ngày</th>
                        <th>Nhập trong ngày</th>
                        <th>Tồn cuối ngày</th>
                        <th>Đối chiếu</th>
                        <th>Kết quả</th>
                    </tr>
                </thead>
                <tbody id="salesIngredientRows">
                    <c:choose>
                        <c:when test="${empty salesAudit.ingredientAudit}">
                            <tr>
                                <td class="empty-cell" colspan="8">Chưa có nguyên liệu phát sinh trong ngày này.</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="row" items="${salesAudit.ingredientAudit}">
                                <tr class="sales-ingredient-row">
                                    <td><div class="ingredient-name"><c:out value="${row.ingredientName}"/></div></td>
                                    <td>
                                        <div class="audit-formula">
                                            <c:choose>
                                                <c:when test="${empty row.usageDetails}">
                                                    <c:out value="${row.cupsText}"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:forEach var="detail" items="${row.usageDetails}">
                                                        <div class="usage-line"><c:out value="${detail}"/></div>
                                                    </c:forEach>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </td>
                                    <td><strong><c:out value="${row.expectedUsedText}"/></strong></td>
                                    <td><c:out value="${row.openingEstimateText}"/></td>
                                    <td><c:out value="${row.importedTodayText}"/></td>
                                    <td><c:out value="${row.currentStockText}"/></td>
                                    <td><div class="audit-balance"><c:out value="${row.expectedClosingText}"/></div></td>
                                    <td>
                                        <span class="audit-status ${row.statusClass}">
                                            <span class="material-symbols-outlined"><c:out value="${row.statusIcon}"/></span>
                                            <c:out value="${row.statusLabel}"/>
                                        </span>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
        <div class="table-footer">
            <span class="result-count" id="salesIngredientResultText">0 dòng đối chiếu</span>
            <div class="pagination">
                <button type="button" class="page-btn" id="salesIngredientPrevBtn" onclick="changeSalesIngredientPage(-1)" aria-label="Trang trước">
                    <span class="material-symbols-outlined">chevron_left</span>
                </button>
                <span class="page-indicator" id="salesIngredientPageText">Trang 1 / 1</span>
                <button type="button" class="page-btn" id="salesIngredientNextBtn" onclick="changeSalesIngredientPage(1)" aria-label="Trang sau">
                    <span class="material-symbols-outlined">chevron_right</span>
                </button>
            </div>
        </div>
    </section>
</section>
