<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>

<section class="staff-view active">
    <div class="panel inventory-panel">
        <div class="panel-header">
            <div>
                <h2>Danh sách nguyên liệu</h2>
                <p>Theo dõi số lượng hiện có và ngưỡng cần nhập thêm.</p>
            </div>

            <form action="${pageContext.request.contextPath}/staff/ingredient" method="get">
                <div class="search-box">
                    <span class="material-symbols-outlined">search</span>
                    <input name="search"
                           value="${fn:escapeXml(key)}"
                           placeholder="Tìm nguyên liệu">
                </div>
            </form>
        </div>

        <div class="tab-row" role="tablist">
            <button class="tab-btn active"
                    type="button"
                    data-filter="all"
                    onclick="setStockFilter(this.getAttribute('data-filter'))">
                Tất cả
                <span class="tab-count">${fn:length(ingredients)}</span>
            </button>

            <button class="tab-btn"
                    type="button"
                    data-filter="warning"
                    onclick="setStockFilter(this.getAttribute('data-filter'))">
                Sắp hết
                <span class="tab-count">
                    <c:out value="${warningCount}"/>
                </span>
            </button>

            <button class="tab-btn"
                    type="button"
                    data-filter="danger"
                    onclick="setStockFilter(this.getAttribute('data-filter'))">
                Hết hàng
                <span class="tab-count">
                    <c:out value="${outCount}"/>
                </span>
            </button>
        </div>

        <div class="table-wrap">
            <table class="inventory-table">
                <thead>
                    <tr>
                        <th scope="col">Nguyên liệu</th>
                        <th scope="col">Nhà cung cấp</th>
                        <th scope="col">Còn lại</th>
                        <th scope="col">Ngưỡng</th>
                        <th scope="col">Trạng thái</th>
                    </tr>
                </thead>

                <tbody id="ingredientRows">
                    <c:if test="${empty ingredients}">
                        <tr>
                            <td class="empty-cell" colspan="5">
                                Chưa có nguyên liệu trong kho.
                            </td>
                        </tr>
                    </c:if>

                    <c:forEach var="item" items="${ingredients}">
                        <tr class="ingredient-row"
                            data-status="${item.status}">

                            <td class="ingredient-primary">
                                <div class="ingredient-name">
                                    <c:out value="${item.ingredientName}"/>
                                </div>

                                <div class="ingredient-unit">
                                    Đơn vị:
                                    <c:out value="${item.unit}"/>
                                </div>
                            </td>

                            <td class="supplier-cell">
                                <c:out value="${item.supplierName}"/>
                            </td>

                            <td class="stock-cell">
                                <strong class="stock-value">
                                    <c:out value="${item.stockText}"/>
                                    <c:out value="${item.unit}"/>
                                </strong>

                                <div class="stock-bar" aria-hidden="true">
                                    <span class="${item.status}"
                                          style="width:${item.barPercent}%"></span>
                                </div>
                            </td>

                            <td class="threshold-cell">
                                <c:out value="${item.minStockText}"/>
                                <c:out value="${item.unit}"/>
                            </td>

                            <td class="status-cell">
                                <span class="status-pill ${item.status}">
                                    <span class="material-symbols-outlined">
                                        <c:out value="${item.statusIcon}"/>
                                    </span>
                                    <c:out value="${item.statusLabel}"/>
                                </span>
                            </td>
                        </tr>
                    </c:forEach>

                    <tr id="inventoryFilterEmpty" hidden>
                        <td class="empty-cell" colspan="5">
                            <span class="material-symbols-outlined">inventory_2</span>
                            <span>
                                Không có nguyên liệu ở trạng thái này.
                            </span>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <div class="table-footer">
            <span class="result-count" id="inventoryResultText">
                0 nguyên liệu
            </span>

            <div class="pagination">
                <button type="button"
                        class="page-btn"
                        id="inventoryPrevBtn"
                        onclick="changeInventoryPage(-1)"
                        aria-label="Trang trước">
                    <span class="material-symbols-outlined">chevron_left</span>
                </button>

                <span class="page-indicator" id="inventoryPageText">
                    Trang 1 / 1
                </span>

                <button type="button"
                        class="page-btn"
                        id="inventoryNextBtn"
                        onclick="changeInventoryPage(1)"
                        aria-label="Trang sau">
                    <span class="material-symbols-outlined">chevron_right</span>
                </button>
            </div>
        </div>
    </div>
</section>
