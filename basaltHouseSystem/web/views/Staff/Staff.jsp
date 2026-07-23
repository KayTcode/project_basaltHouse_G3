<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<c:url var="staffImportUrl" value="/staff/import"/>
<c:set var="activeStaffPage" value="${staffPage}" scope="request"/>
<c:set var="pageTitle" value="${staffPageTitle}"/>
<c:set var="pageSubtitle" value="${staffPageSubtitle}"/>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title><c:out value="${pageTitle}"/> | Staff</title>
        <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/Staff/Staff.css?v=20260709-1" rel="stylesheet">
    </head>
    <body class="staff-page staff-page-${activeStaffPage}">
        <aside class="staff-sidebar">
            <div class="sidebar-logo">
                <div class="logo-icon">
                    <span class="material-symbols-outlined">local_cafe</span>
                </div>
                <div class="logo-text">BasaltHouse<span>Không gian nhân viên</span></div>
            </div>

            <nav class="sidebar-nav">
                <a class="nav-item ${activeStaffPage eq 'ingredient' ? 'active' : ''}"
                   href="${pageContext.request.contextPath}/staff/ingredient">
                    <span class="material-symbols-outlined">inventory_2</span>
                    Kho nguyên liệu
                </a>
                <a class="nav-item ${activeStaffPage eq 'import' ? 'active' : ''}"
                   href="${pageContext.request.contextPath}/staff/import">
                    <span class="material-symbols-outlined">add_shopping_cart</span>
                    Nhập nguyên liệu
                </a>
                <a class="nav-item ${activeStaffPage eq 'history' ? 'active' : ''}"
                   href="${pageContext.request.contextPath}/staff/history">
                    <span class="material-symbols-outlined">history</span>
                    Lịch sử nhập
                </a>
                <a class="nav-item ${activeStaffPage eq 'sales-history' ? 'active' : ''}"
                   href="${pageContext.request.contextPath}/staff/sales-history">
                    <span class="material-symbols-outlined">point_of_sale</span>
                    Lịch sử bán hàng
                </a>
            </nav>

            <div class="sidebar-footer">
                <div class="account-menu" id="accountMenu">
                    <button type="button" class="staff-card account-trigger" onclick="toggleAccountMenu(event)" aria-expanded="false">
                        <div class="staff-avatar">
                            <span class="material-symbols-outlined">person</span>
                        </div>
                        <div class="staff-info">
                            <div class="staff-name"><c:out value="${staffName}"/></div>
                            <div class="staff-status"><span></span>Online</div>
                        </div>
                        <span class="material-symbols-outlined account-chevron">expand_more</span>
                    </button>
                    <div class="account-dropdown">
                        <form method="POST" action="${pageContext.request.contextPath}/logout">
                            <button type="submit" class="logout-btn">
                                <span class="material-symbols-outlined">logout</span>
                                Đăng xuất
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </aside>

        <main class="staff-content">
            <header class="page-header">
                <div>
                    <p class="eyebrow">
                        <c:choose>
                            <c:when test="${activeStaffPage eq 'import'}">Nhân viên / Nhập nguyên liệu</c:when>
                            <c:when test="${activeStaffPage eq 'history'}">Nhân viên / Lịch sử nhập kho</c:when>
                            <c:when test="${activeStaffPage eq 'sales-history'}">Nhân viên / Kiểm kê bán hàng</c:when>
                            <c:otherwise>Nhân viên / Kho nguyên liệu</c:otherwise>
                        </c:choose>
                    </p>
                    <h1><c:out value="${pageTitle}"/></h1>
                    <p class="page-subtitle"><c:out value="${pageSubtitle}"/></p>
                </div>
                <c:if test="${activeStaffPage eq 'ingredient'}">
                    <a class="header-action header-action-primary" href="${pageContext.request.contextPath}/staff/import">
                        <span class="material-symbols-outlined">add_circle</span>
                        Nhập thêm nguyên liệu
                    </a>
                </c:if>
            </header>

            <c:if test="${not empty successMessage}">
                <div class="notice notice-success">
                    <span class="material-symbols-outlined">check_circle</span>
                    <c:out value="${successMessage}"/>
                </div>
            </c:if>

            <c:if test="${not empty errorMessage}">
                <div class="notice notice-error">
                    <span class="material-symbols-outlined">error</span>
                    <c:out value="${errorMessage}"/>
                </div>
            </c:if>

            <c:if test="${not empty dataError}">
                <div class="notice notice-error">
                    <span class="material-symbols-outlined">database_off</span>
                    Không đọc được dữ liệu kho: <c:out value="${dataError}"/>
                </div>
            </c:if>

            <c:if test="${activeStaffPage eq 'ingredient' or activeStaffPage eq 'import'}">
            <section class="stats-grid" aria-label="Tổng quan kho">
                <button class="stat-card stat-filter-card is-selected" type="button"
                        data-stock-filter="all" onclick="setStockFilter('all')" aria-pressed="true">
                    <span class="material-symbols-outlined stat-icon">category</span>
                    <div>
                        <p>Tổng nguyên liệu</p>
                        <strong>${fn:length(ingredients)}</strong>
                        <span class="stat-hint">Xem tất cả</span>
                    </div>
                </button>
                <button class="stat-card stat-warning stat-filter-card" type="button"
                        data-stock-filter="warning" onclick="setStockFilter('warning')" aria-pressed="false">
                    <span class="material-symbols-outlined stat-icon">warning</span>
                    <div>
                        <p>Sắp hết</p>
                        <strong><c:out value="${warningCount}"/></strong>
                        <span class="stat-hint">Cần theo dõi</span>
                    </div>
                </button>
                <button class="stat-card stat-danger stat-filter-card" type="button"
                        data-stock-filter="danger" onclick="setStockFilter('danger')" aria-pressed="false">
                    <span class="material-symbols-outlined stat-icon">error</span>
                    <div>
                        <p>Hết hàng</p>
                        <strong><c:out value="${outCount}"/></strong>
                        <span class="stat-hint">Cần nhập ngay</span>
                    </div>
                </button>
                <button class="stat-card stat-ok stat-filter-card" type="button"
                        data-stock-filter="ok" onclick="setStockFilter('ok')" aria-pressed="false">
                    <span class="material-symbols-outlined stat-icon">task_alt</span>
                    <div>
                        <p>Đủ hàng</p>
                        <strong><c:out value="${okCount}"/></strong>
                        <span class="stat-hint">Kho ổn định</span>
                    </div>
                </button>
            </section>

                <section class="warning-strip ${empty warnings ? 'is-clear' : ''}" aria-labelledby="stockWarningTitle">
                    <div class="warning-strip-header">
                        <div class="warning-strip-title">
                            <span class="material-symbols-outlined">${empty warnings ? 'verified' : 'warning'}</span>
                            <div>
                                <c:choose>
                                    <c:when test="${empty warnings}">
                                        <h2 id="stockWarningTitle">Tồn kho đang ở mức an toàn</h2>
                                        <p>Chưa có nguyên liệu nào chạm ngưỡng cần nhập thêm.</p>
                                    </c:when>
                                    <c:otherwise>
                                        <h2 id="stockWarningTitle">Nguyên liệu cần nhập thêm</h2>
                                        <p><c:out value="${warningCount + outCount}"/> nguyên liệu đang dưới mức tồn kho an toàn.</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        <a class="warning-strip-action" href="${activeStaffPage eq 'import' ? '#importForm' : staffImportUrl}">
                            <span class="material-symbols-outlined">add_shopping_cart</span>
                            Nhập thêm ngay
                        </a>
                    </div>
                    <c:if test="${activeStaffPage eq 'import'}">
                        <div class="warning-filter-row" role="group" aria-label="Lọc nguyên liệu cần nhập">
                            <button class="warning-filter-btn active" type="button" data-warning-filter="all"
                                    onclick="setImportWarningFilter('all')">Tất cả cần nhập</button>
                            <button class="warning-filter-btn" type="button" data-warning-filter="warning"
                                    onclick="setImportWarningFilter('warning')">Sắp hết</button>
                            <button class="warning-filter-btn" type="button" data-warning-filter="danger"
                                    onclick="setImportWarningFilter('danger')">Hết hàng</button>
                        </div>
                    </c:if>
                    <div class="warning-list">
                        <c:forEach var="item" items="${warnings}">
                            <div class="warning-chip ${item.status}" data-warning-status="${item.status}">
                                <span class="material-symbols-outlined"><c:out value="${item.statusIcon}"/></span>
                                <div>
                                    <strong><c:out value="${item.name}"/></strong>
                                    <span>Còn <c:out value="${item.stockText}"/> <c:out value="${item.unit}"/></span>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                    <p class="warning-filter-empty" id="warningFilterEmpty" hidden>Không có nguyên liệu ở trạng thái này.</p>
                </section>

            </c:if>

            <c:choose>
                <c:when test="${activeStaffPage eq 'import'}">
                    <jsp:include page="ImportInVoice.jsp" />
                </c:when>
                <c:when test="${activeStaffPage eq 'history'}">
                    <jsp:include page="HistoryImportInVoice.jsp" />
                </c:when>
                <c:when test="${activeStaffPage eq 'sales-history'}">
                    <jsp:include page="HistoryBuyProduct.jsp" />
                </c:when>
                <c:otherwise>
                    <jsp:include page="Ingredient.jsp" />
                </c:otherwise>
            </c:choose>
        </main>

        <script>
            var activeFilter = 'all';
            var activeImportWarningFilter = 'all';
            var inventoryPage = 1;
            var historyPage = 1;
            var salesProductPage = 1;
            var salesIngredientPage = 1;
            var inventoryPageSize = 8;
            var historyPageSize = 7;
            var salesPageSize = 7;

            function toggleAccountMenu(event) {
                if (event) {
                    event.stopPropagation();
                }
                var menu = document.getElementById('accountMenu');
                if (!menu) {
                    return;
                }
                var trigger = menu.querySelector('.account-trigger');
                var isOpen = menu.classList.toggle('open');
                if (trigger) {
                    trigger.setAttribute('aria-expanded', isOpen ? 'true' : 'false');
                }
            }

            document.addEventListener('click', function (event) {
                var menu = document.getElementById('accountMenu');
                if (menu && !menu.contains(event.target)) {
                    menu.classList.remove('open');
                    var trigger = menu.querySelector('.account-trigger');
                    if (trigger) {
                        trigger.setAttribute('aria-expanded', 'false');
                    }
                }
            });

            function setStockFilter(filter) {
                if (!filter) {
                    filter = 'all';
                }
                activeFilter = filter;
                inventoryPage = 1;
                updateStockFilterControls(activeFilter);
                renderInventoryRows();
                if (document.getElementById('importView')) {
                    setImportWarningFilter(activeFilter);
                }
            }

            function updateStockFilterControls(filter) {
                var tabs = document.querySelectorAll('.tab-btn');
                for (var i = 0; i < tabs.length; i++) {
                    tabs[i].classList.toggle('active', tabs[i].getAttribute('data-filter') === filter);
                }

                var cards = document.querySelectorAll('.stat-filter-card');
                for (var j = 0; j < cards.length; j++) {
                    var selected = cards[j].getAttribute('data-stock-filter') === filter;
                    cards[j].classList.toggle('is-selected', selected);
                    cards[j].setAttribute('aria-pressed', selected ? 'true' : 'false');
                }
            }

            function renderInventoryRows() {
                var container = document.getElementById('ingredientRows');
                if (!container) {
                    return;
                }

                var rows = Array.prototype.slice.call(container.querySelectorAll('.ingredient-row'));
                var filteredRows = rows.filter(function (row) {
                    return activeFilter === 'all' || row.getAttribute('data-status') === activeFilter;
                });

                for (var i = 0; i < rows.length; i++) {
                    rows[i].style.display = 'none';
                }

                var totalPages = Math.max(1, Math.ceil(filteredRows.length / inventoryPageSize));
                inventoryPage = Math.max(1, Math.min(inventoryPage, totalPages));
                renderPagedRows(filteredRows, inventoryPage, inventoryPageSize);

                var emptyRow = document.getElementById('inventoryFilterEmpty');
                if (emptyRow) {
                    emptyRow.style.display = filteredRows.length === 0 && rows.length > 0 ? '' : 'none';
                }

                var filterLabels = {
                    all: 'nguyên liệu',
                    warning: 'nguyên liệu sắp hết',
                    danger: 'nguyên liệu hết hàng',
                    ok: 'nguyên liệu đủ hàng'
                };
                updatePaginationInfo(
                        'inventoryResultText',
                        'inventoryPageText',
                        'inventoryPrevBtn',
                        'inventoryNextBtn',
                        filteredRows.length,
                        inventoryPage,
                        totalPages,
                        filterLabels[activeFilter] || 'nguyên liệu'
                        );
            }

            function setImportWarningFilter(filter) {
                var warningList = document.querySelector('.warning-list');
                if (!warningList) {
                    return;
                }

                activeImportWarningFilter = filter || 'all';
                var chips = warningList.querySelectorAll('.warning-chip');
                var visibleCount = 0;
                for (var i = 0; i < chips.length; i++) {
                    var visible = activeImportWarningFilter === 'all'
                            || chips[i].getAttribute('data-warning-status') === activeImportWarningFilter;
                    chips[i].style.display = visible ? 'flex' : 'none';
                    if (visible) {
                        visibleCount++;
                    }
                }

                var filterButtons = document.querySelectorAll('.warning-filter-btn');
                for (var j = 0; j < filterButtons.length; j++) {
                    filterButtons[j].classList.toggle(
                            'active',
                            filterButtons[j].getAttribute('data-warning-filter') === activeImportWarningFilter
                            );
                }

                var emptyMessage = document.getElementById('warningFilterEmpty');
                if (emptyMessage) {
                    emptyMessage.hidden = visibleCount > 0;
                }
            }

            function changeInventoryPage(delta) {
                inventoryPage += delta;
                renderInventoryRows();
            }

            function renderHistoryRows() {
                historyPage = renderSimplePagedRows(
                        'historyRows',
                        '.history-row',
                        historyPage,
                        historyPageSize,
                        'historyResultText',
                        'historyPageText',
                        'historyPrevBtn',
                        'historyNextBtn',
                        'dòng lịch sử'
                        );
            }

            function changeHistoryPage(delta) {
                historyPage += delta;
                renderHistoryRows();
            }

            function renderSalesProductRows() {
                salesProductPage = renderSimplePagedRows(
                        'salesProductRows',
                        '.sales-product-row',
                        salesProductPage,
                        salesPageSize,
                        'salesProductResultText',
                        'salesProductPageText',
                        'salesProductPrevBtn',
                        'salesProductNextBtn',
                        'dòng bán hàng'
                        );
            }

            function changeSalesProductPage(delta) {
                salesProductPage += delta;
                renderSalesProductRows();
            }

            function renderSalesIngredientRows() {
                salesIngredientPage = renderSimplePagedRows(
                        'salesIngredientRows',
                        '.sales-ingredient-row',
                        salesIngredientPage,
                        salesPageSize,
                        'salesIngredientResultText',
                        'salesIngredientPageText',
                        'salesIngredientPrevBtn',
                        'salesIngredientNextBtn',
                        'dòng đối chiếu'
                        );
            }

            function changeSalesIngredientPage(delta) {
                salesIngredientPage += delta;
                renderSalesIngredientRows();
            }

            function renderSimplePagedRows(containerId, rowSelector, page, pageSize,
                    resultId, pageId, prevId, nextId, label) {
                var container = document.getElementById(containerId);
                if (!container) {
                    return page;
                }
                var rows = container.querySelectorAll(rowSelector);
                for (var i = 0; i < rows.length; i++) {
                    rows[i].style.display = 'none';
                }
                var totalPages = Math.max(1, Math.ceil(rows.length / pageSize));
                var normalizedPage = Math.max(1, Math.min(page, totalPages));
                renderPagedRows(rows, normalizedPage, pageSize);
                updatePaginationInfo(
                        resultId,
                        pageId,
                        prevId,
                        nextId,
                        rows.length,
                        normalizedPage,
                        totalPages,
                        label
                        );
                return normalizedPage;
            }

            function renderPagedRows(rows, page, pageSize) {
                var start = (page - 1) * pageSize;
                var end = start + pageSize;
                for (var i = 0; i < rows.length; i++) {
                    rows[i].style.display = i >= start && i < end ? '' : 'none';
                }
            }

            function updatePaginationInfo(resultId, pageId, prevId, nextId, total, page, totalPages, label) {
                var normalizedPage = Math.max(1, Math.min(page, totalPages));
                var resultText = document.getElementById(resultId);
                var pageText = document.getElementById(pageId);
                var prevBtn = document.getElementById(prevId);
                var nextBtn = document.getElementById(nextId);

                if (resultText) {
                    resultText.textContent = total + ' ' + label;
                }
                if (pageText) {
                    pageText.textContent = 'Trang ' + normalizedPage + ' / ' + totalPages;
                }
                if (prevBtn) {
                    prevBtn.disabled = normalizedPage <= 1;
                }
                if (nextBtn) {
                    nextBtn.disabled = normalizedPage >= totalPages;
                }
            }

            function formatMoney(value) {
                return new Intl.NumberFormat('vi-VN').format(value || 0) + ' đ';
            }

            function setText(id, value) {
                var element = document.getElementById(id);
                if (element) {
                    element.textContent = value;
                }
            }

            function getImportDetailRows() {
                return Array.prototype.slice.call(document.querySelectorAll('.import-detail-row'));
            }

            function updateImportDetailControls() {
                var rows = getImportDetailRows();
                for (var i = 0; i < rows.length; i++) {
                    var title = rows[i].querySelector('.import-detail-title');
                    var removeButton = rows[i].querySelector('.remove-import-detail');
                    if (title) {
                        title.textContent = 'Nguyên liệu ' + (i + 1);
                    }
                    if (removeButton) {
                        removeButton.hidden = rows.length === 1;
                    }
                }
            }

            function addImportDetail() {
                var container = document.getElementById('importDetailRows');
                var source = container ? container.querySelector('.import-detail-row') : null;
                if (!container || !source) {
                    return;
                }

                var row = source.cloneNode(true);
                var fields = row.querySelectorAll('select, input, textarea');
                for (var i = 0; i < fields.length; i++) {
                    if (fields[i].classList.contains('unit-price-input')) {
                        fields[i].value = '0';
                    } else {
                        fields[i].value = '';
                    }
                    delete fields[i].dataset.userEdited;
                }
                container.appendChild(row);
                updateImportDetailControls();
                updateInvoicePreview();

                var ingredientSelect = row.querySelector('.import-ingredient-select');
                if (ingredientSelect) {
                    ingredientSelect.focus();
                }
            }

            function removeImportDetail(button) {
                var rows = getImportDetailRows();
                if (rows.length <= 1) {
                    return;
                }
                button.closest('.import-detail-row').remove();
                updateImportDetailControls();
                updateInvoicePreview();
            }

            function loadSupplierIngredients(supplierSelect) {
                if (!supplierSelect) {
                    return;
                }
                window.location.href = '${pageContext.request.contextPath}/staff/import?supplierId='
                        + encodeURIComponent(supplierSelect.value);
            }

            function updateInvoicePreview(event) {
                var target = event ? event.target : null;
                var activeRow = target && target.closest ? target.closest('.import-detail-row') : null;

                if (activeRow && target.classList.contains('received-quantity-input')) {
                    target.dataset.userEdited = target.value ? 'true' : '';
                }
                if (activeRow && target.classList.contains('ordered-quantity-input')) {
                    var activeReceived = activeRow.querySelector('.received-quantity-input');
                    if (activeReceived && activeReceived.dataset.userEdited !== 'true') {
                        activeReceived.value = target.value;
                    }
                }

                var rows = getImportDetailRows();
                var orderedTotal = 0;
                var receivedTotal = 0;
                for (var i = 0; i < rows.length; i++) {
                    var orderedQuantity = parseFloat(rows[i].querySelector('.ordered-quantity-input').value) || 0;
                    var receivedQuantity = parseFloat(rows[i].querySelector('.received-quantity-input').value) || 0;
                    var price = parseFloat(rows[i].querySelector('.unit-price-input').value) || 0;
                    orderedTotal += orderedQuantity * price;
                    receivedTotal += receivedQuantity * price;
                }

                var supplierSelect = document.getElementById('supplierSelect');
                var supplierOption = supplierSelect && supplierSelect.selectedIndex >= 0
                        ? supplierSelect.options[supplierSelect.selectedIndex] : null;
                setText('previewIngredientCount', rows.length + ' nguyên liệu');
                setText('previewSupplier', supplierOption && supplierOption.value
                        ? supplierOption.textContent.trim() : 'Chưa chọn');
                setText('previewOrderedTotal', formatMoney(orderedTotal));
                setText('previewReceivedTotal', formatMoney(receivedTotal));
            }
            if (document.getElementById('ingredientRows')) {
                renderInventoryRows();
            }
            if (document.getElementById('historyRows')) {
                renderHistoryRows();
            }
            if (document.getElementById('salesProductRows')) {
                renderSalesProductRows();
                renderSalesIngredientRows();
            }
            if (document.getElementById('importView')) {
                setImportWarningFilter('all');
                updateImportDetailControls();
                updateInvoicePreview();
            }
        </script>
    </body>
</html>
