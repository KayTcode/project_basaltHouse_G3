<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý nguyên liệu | Staff</title>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/Staff/Staff.css?v=9" rel="stylesheet">
    </head>
    <body>
        <aside class="staff-sidebar">
            <div class="sidebar-logo">
                <div class="logo-icon">
                    <span class="material-symbols-outlined">local_cafe</span>
                </div>
                <div class="logo-text">BasaltHouse<span>Staff workspace</span></div>
            </div>

            <nav class="sidebar-nav">
                <button type="button" class="nav-item active" data-view-link="inventory" onclick="showStaffView('inventory')">
                    <span class="material-symbols-outlined">inventory_2</span>
                    Kho nguyên liệu
                </button>
                <button type="button" class="nav-item" data-view-link="import" onclick="showStaffView('import')">
                    <span class="material-symbols-outlined">add_shopping_cart</span>
                    Nhập nguyên liệu
                </button>
                <button type="button" class="nav-item" data-view-link="history" onclick="showStaffView('history')">
                    <span class="material-symbols-outlined">history</span>
                    Lịch sử nhập
                </button>
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
                    <div class="account-dropdown" id="accountDropdown">
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
                    <p class="eyebrow" id="staffHeaderEyebrow">Staff / Inventory</p>
                    <h1 id="staffHeaderTitle">Quản lý nguyên liệu</h1>
                    <p class="page-subtitle" id="staffHeaderSubtitle">Theo dõi tồn kho, cảnh báo sắp hết và nhập thêm nguyên liệu.</p>
                </div>
                <div class="view-switch">
                    <button type="button" class="view-switch-btn active" data-view-link="inventory" onclick="showStaffView('inventory')">
                        <span class="material-symbols-outlined">inventory_2</span>
                        Kho nguyên liệu
                    </button>
                    <button type="button" class="view-switch-btn" data-view-link="import" onclick="showStaffView('import')">
                        <span class="material-symbols-outlined">add_circle</span>
                        Nhập nguyên liệu
                    </button>
                    <button type="button" class="view-switch-btn" data-view-link="history" onclick="showStaffView('history')">
                        <span class="material-symbols-outlined">history</span>
                        Lịch sử nhập
                    </button>
                </div>
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

            <section class="stats-grid" aria-label="Tổng quan kho">
                <div class="stat-card">
                    <span class="material-symbols-outlined stat-icon">category</span>
                    <div>
                        <p>Tổng nguyên liệu</p>
                        <strong>${fn:length(ingredients)}</strong>
                    </div>
                </div>
                <div class="stat-card stat-warning">
                    <span class="material-symbols-outlined stat-icon">warning</span>
                    <div>
                        <p>Sắp hết</p>
                        <strong><c:out value="${warningCount}"/></strong>
                    </div>
                </div>
                <div class="stat-card stat-danger">
                    <span class="material-symbols-outlined stat-icon">error</span>
                    <div>
                        <p>Hết hàng</p>
                        <strong><c:out value="${outCount}"/></strong>
                    </div>
                </div>
                <div class="stat-card stat-ok">
                    <span class="material-symbols-outlined stat-icon">task_alt</span>
                    <div>
                        <p>Đủ hàng</p>
                        <strong><c:out value="${okCount}"/></strong>
                    </div>
                </div>
            </section>

            <c:if test="${not empty warnings}">
                <section class="warning-strip">
                    <div class="warning-strip-title">
                        <span class="material-symbols-outlined">priority_high</span>
                        Cảnh báo nguyên liệu cần nhập
                    </div>
                    <div class="warning-list">
                        <c:forEach var="item" items="${warnings}">
                            <span class="warning-chip ${item.status}">
                                <c:out value="${item.name}"/>:
                                <strong><c:out value="${item.stockText}"/> <c:out value="${item.unit}"/></strong>
                            </span>
                        </c:forEach>
                    </div>
                </section>
            </c:if>

            <section class="staff-view active" id="inventoryView">
                <div class="panel inventory-panel">
                    <div class="panel-header">
                        <div>
                            <h2>Tồn kho nguyên liệu</h2>
                            <p>Cập nhật theo số lượng đang còn trong kho.</p>
                        </div>
                        <div class="search-box">
                            <span class="material-symbols-outlined">search</span>
                            <input id="ingredientSearch" type="search" placeholder="Tìm nguyên liệu" oninput="filterIngredients()">
                        </div>
                    </div>

                    <div class="tab-row" role="tablist">
                        <button class="tab-btn active" type="button" data-filter="all" onclick="setFilter(this)">Tất cả</button>
                        <button class="tab-btn" type="button" data-filter="warning" onclick="setFilter(this)">Sắp hết</button>
                        <button class="tab-btn" type="button" data-filter="danger" onclick="setFilter(this)">Hết hàng</button>
                    </div>

                    <div class="table-wrap">
                        <table class="inventory-table">
                            <thead>
                                <tr>
                                    <th>Nguyên liệu</th>
                                    <th>Nhà cung cấp</th>
                                    <th>Còn lại</th>
                                    <th>Ngưỡng</th>
                                    <th>Trạng thái</th>
                                </tr>
                            </thead>
                            <tbody id="ingredientRows">
                                <c:forEach var="item" items="${ingredients}">
                                    <tr class="ingredient-row" data-status="${item.status}" data-name="${item.name}">
                                        <td>
                                            <div class="ingredient-name"><c:out value="${item.name}"/></div>
                                            <div class="ingredient-unit">Đơn vị: <c:out value="${item.unit}"/></div>
                                        </td>
                                        <td><c:out value="${item.supplierName}"/></td>
                                        <td>
                                            <strong><c:out value="${item.stockText}"/> <c:out value="${item.unit}"/></strong>
                                            <div class="stock-bar">
                                                <span class="${item.status}" style="width:${item.barPercent}%"></span>
                                            </div>
                                        </td>
                                        <td><c:out value="${item.minStockText}"/> <c:out value="${item.unit}"/></td>
                                        <td>
                                            <span class="status-pill ${item.status}">
                                                <span class="material-symbols-outlined"><c:out value="${item.statusIcon}"/></span>
                                                <c:out value="${item.statusLabel}"/>
                                            </span>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <div class="table-footer">
                        <span class="result-count" id="inventoryResultText">0 nguyên liệu</span>
                        <div class="pagination">
                            <button type="button" class="page-btn" id="inventoryPrevBtn" onclick="changeInventoryPage(-1)" aria-label="Trang trước">
                                <span class="material-symbols-outlined">chevron_left</span>
                            </button>
                            <span class="page-indicator" id="inventoryPageText">Trang 1 / 1</span>
                            <button type="button" class="page-btn" id="inventoryNextBtn" onclick="changeInventoryPage(1)" aria-label="Trang sau">
                                <span class="material-symbols-outlined">chevron_right</span>
                            </button>
                        </div>
                    </div>
                </div>
            </section>

            <section class="staff-view" id="importView">
                <aside class="panel import-panel" id="importForm">
                    <div class="panel-header compact">
                        <div>
                            <h2>Nhập thêm nguyên liệu</h2>
                            <p>Phiếu nhập sẽ tạo hóa đơn và ghi lịch sử kho.</p>
                        </div>
                    </div>

                    <form method="post"
                          action="${pageContext.request.contextPath}/staff"
                          class="import-form"
                          oninput="updateInvoicePreview()">
                        <input type="hidden" name="action" value="importIngredient">
                        <input type="hidden" id="quantityInput" name="quantity">

                        <div class="form-section">
                            <div class="form-section-title">
                                <span class="material-symbols-outlined">receipt_long</span>
                                <h3>Phiếu nhập</h3>
                            </div>

                            <div class="form-grid-3">
                                <label>
                                    <span>Mã phiếu nhập</span>
                                    <input type="text" name="importCode" placeholder="IMP-YYYYMMDD-001">
                                </label>
                                <label>
                                    <span>Mã hóa đơn NCC</span>
                                    <input type="text" name="supplierInvoiceCode" value="${param.supplierInvoiceCode}" placeholder="SUP-INV-001">
                                </label>
                                <label>
                                    <span>Trạng thái</span>
                                    <select name="status" id="importStatus">
                                        <option value="Confirmed">Đã nhận</option>
                                        <option value="Pending">Chờ nhận</option>
                                    </select>
                                </label>
                            </div>

                            <label>
                                <span>Nhà cung cấp</span>
                                <select name="supplierId" id="supplierSelect" required>
                                    <option value="">Theo nguyên liệu</option>
                                    <c:forEach var="supplier" items="${suppliers}">
                                        <option value="${supplier.id}"><c:out value="${supplier.name}"/></option>
                                    </c:forEach>
                                </select>
                            </label>

                            <div class="form-grid-3">
                                <label>
                                    <span>Ngày đặt</span>
                                    <input type="datetime-local" name="orderedDate" value="${currentDateInput}" required>
                                </label>
                                <label>
                                    <span>Ngày dự kiến</span>
                                    <input type="datetime-local" name="expectedDate">
                                </label>
                                <label>
                                    <span>Ngày nhận</span>
                                    <input type="datetime-local" name="receivedDate" value="${currentDateInput}">
                                </label>
                            </div>
                        </div>

                        <div class="form-section">
                            <div class="form-section-title">
                                <span class="material-symbols-outlined">inventory_2</span>
                                <h3>Chi tiết nguyên liệu</h3>
                            </div>

                            <label>
                                <span>Nguyên liệu</span>
                                <select name="ingredientId" id="ingredientSelect" required>
                                    <option value="">Chọn nguyên liệu</option>
                                    <c:forEach var="item" items="${ingredients}">
                                        <option value="${item.id}"
                                                data-name="${item.name}"
                                                data-unit="${item.unit}"
                                                data-stock="${item.stockText}"
                                                data-supplier="${item.supplierId}">
                                            <c:out value="${item.name}"/> - còn <c:out value="${item.stockText}"/> <c:out value="${item.unit}"/>
                                        </option>
                                    </c:forEach>
                                </select>
                            </label>

                            <div class="form-grid-3">
                                <label>
                                    <span>Số lượng đặt</span>
                                    <input type="number" id="orderedQuantityInput" name="orderedQuantity" min="0.01" step="0.01" required>
                                </label>
                                <label>
                                    <span>Số lượng nhận</span>
                                    <input type="number" id="receivedQuantityInput" name="receivedQuantity" min="0" step="0.01" required>
                                </label>
                                <label>
                                    <span>Đơn giá</span>
                                    <input type="number" id="unitPriceInput" name="unitPrice" min="0" step="100" value="0" required>
                                </label>
                            </div>

                            <label>
                                <span>Chênh lệch</span>
                                <textarea name="discrepancyNote" rows="2" placeholder="Ghi nhận thiếu, thừa hoặc hàng lỗi"></textarea>
                            </label>

                            <label>
                                <span>Ghi chú</span>
                                <textarea name="note" rows="3" placeholder="Ghi chú phiếu nhập"></textarea>
                            </label>

                            <button class="submit-btn" type="submit">
                                <span class="material-symbols-outlined">save</span>
                                Lưu phiếu nhập
                            </button>
                        </div>

                        <div class="invoice-preview">
                            <div>
                                <span class="preview-label">Tóm tắt phiếu nhập</span>
                                <strong id="previewIngredient">Chưa chọn nguyên liệu</strong>
                            </div>
                            <div class="preview-line">
                                <span>Số lượng đặt</span>
                                <b id="previewOrderedQuantity">0</b>
                            </div>
                            <div class="preview-line">
                                <span>Số lượng nhận</span>
                                <b id="previewReceivedQuantity">0</b>
                            </div>
                            <div class="preview-line">
                                <span>Tổng đặt</span>
                                <b id="previewOrderedTotal">0 đ</b>
                            </div>
                            <div class="preview-line">
                                <span>Tổng nhận</span>
                                <b id="previewReceivedTotal">0 đ</b>
                            </div>
                        </div>

                    </form>
                </aside>
            </section>

            <section class="staff-view" id="historyView">
                <div class="panel history-panel" id="history">
                    <div class="panel-header">
                        <div>
                            <h2>Lịch sử nhập nguyên liệu</h2>
                            <p>Theo dõi phiếu nhập, số lượng nhận, giá trị và tồn kho sau khi cập nhật.</p>
                        </div>
                        <div class="search-box history-search">
                            <span class="material-symbols-outlined">search</span>
                            <input id="historySearch" type="search" placeholder="Tìm hóa đơn, nguyên liệu" oninput="filterHistories()">
                        </div>
                    </div>

                    <div class="history-summary">
                        <div class="history-summary-item">
                            <span class="material-symbols-outlined">receipt_long</span>
                            <div>
                                <p>Dòng lịch sử</p>
                                <strong>${fn:length(listP)}</strong>
                            </div>
                        </div>
                        <div class="history-summary-item">
                            <span class="material-symbols-outlined">inventory</span>
                            <div>
                                <p>Phiếu nhập</p>
                                <strong>${fn:length(listP)}</strong>
                            </div>
                        </div>
                        <div class="history-summary-item">
                            <span class="material-symbols-outlined">payments</span>
                            <div>
                                <p>Theo dữ liệu</p>
                                <strong>Import</strong>
                            </div>
                        </div>
                    </div>

                    <div class="table-wrap">
                        <table class="history-table">
                            <thead>
                                <tr>
                                    <th>Ngày đặt</th>
                                    <th>Phiếu nhập</th>
                                    <th>Nguyên liệu</th>
                                    <th>Nhà cung cấp</th>
                                    <th>Số lượng nhận</th>
                                    <th>Đơn giá</th>
                                    <th>Thành tiền</th>
                                    <th>Trạng thái</th>
                                    <th>Người tạo</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody id="historyRows">
                                <c:if test="${empty listP}">
                                    <tr>
                                        <td class="empty-cell" colspan="10">Chưa có lịch sử nhập nguyên liệu.</td>
                                    </tr>
                                </c:if>

                                <c:forEach var="row" items="${listP}">
                                    <tr class="history-row" data-history-text="${row.importCode} ${row.ingredientName} ${row.sppliendName} ${row.status} ${row.staffName}">
                                        <td><strong><c:out value="${row.orderedDate}"/></strong></td>
                                        <td>
                                            <strong><c:out value="${row.importCode}"/></strong>
                                            <span class="invoice-sub">#<c:out value="${row.importId}"/></span>
                                        </td>
                                        <td>
                                            <div class="ingredient-name"><c:out value="${row.ingredientName}"/></div>
                                        </td>
                                        <td><c:out value="${row.sppliendName}"/></td>
                                        <td>
                                            <div class="history-metric">
                                                <span>Nhận</span>
                                                <strong><c:out value="${row.receivedQuantity}"/></strong>
                                            </div>
                                        </td>
                                        <td><c:out value="${row.unitPrice}"/></td>
                                        <td><strong><c:out value="${row.totalReceivedAmount}"/></strong></td>
                                        <td>
                                            <span class="status-pill history-status ${row.status}">
                                                <span class="material-symbols-outlined">
                                                    <c:choose>
                                                        <c:when test="${row.status eq 'Pending'}">schedule</c:when>
                                                        <c:otherwise>check_circle</c:otherwise>
                                                    </c:choose>
                                                </span>
                                                <c:out value="${row.status}"/>
                                            </span>
                                        </td>
                                        <td>
                                            <div class="history-staff">
                                                <strong><c:out value="${row.staffName}"/></strong>
                                            </div>
                                        </td>
                                        <td>
                                            <input type="hidden" name="importId" value="${row.importId}">
                                            <a class="history-view-btn"
                                               href="${pageContext.request.contextPath}/viewimportvoice?id=${row.importId}">
                                                <span class="material-symbols-outlined">visibility</span>
                                                Xem
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <div class="table-footer">
                        <span class="result-count" id="historyResultText">0 dòng lịch sử</span>
                        <div class="pagination">
                            <button type="button" class="page-btn" id="historyPrevBtn" onclick="changeHistoryPage(-1)" aria-label="Trang trước">
                                <span class="material-symbols-outlined">chevron_left</span>
                            </button>
                            <span class="page-indicator" id="historyPageText">Trang 1 / 1</span>
                            <button type="button" class="page-btn" id="historyNextBtn" onclick="changeHistoryPage(1)" aria-label="Trang sau">
                                <span class="material-symbols-outlined">chevron_right</span>
                            </button>
                        </div>
                    </div>
                </div>
            </section>
        </main>

        <script>
            var activeFilter = 'all';
            var inventoryPage = 1;
            var historyPage = 1;
            var inventoryPageSize = 8;
            var historyPageSize = 7;
            var staffViewHeaders = {
                inventory: {
                    eyebrow: 'Staff / Inventory',
                    title: 'Quản lý nguyên liệu',
                    subtitle: 'Theo dõi tồn kho, cảnh báo sắp hết và nhập thêm nguyên liệu.'
                },
                import: {
                    eyebrow: 'Staff / Import',
                    title: 'Nhập nguyên liệu',
                    subtitle: 'Tạo phiếu nhập mới, chọn nhà cung cấp và cập nhật số lượng nhập kho.'
                },
                history: {
                    eyebrow: 'Staff / Import history',
                    title: 'Lịch sử nhập nguyên liệu',
                    subtitle: 'Theo dõi các phiếu nhập, trạng thái nhận hàng và người tạo phiếu.'
                }
            };

            function showStaffView(viewName) {
                var views = document.querySelectorAll('.staff-view');
                var controls = document.querySelectorAll('[data-view-link]');

                for (var i = 0; i < views.length; i++) {
                    views[i].classList.remove('active');
                }

                for (var j = 0; j < controls.length; j++) {
                    controls[j].classList.toggle('active', controls[j].getAttribute('data-view-link') === viewName);
                }

                var target = document.getElementById(viewName + 'View');
                if (target) {
                    target.classList.add('active');
                }

                updateStaffHeader(viewName);
            }

            function updateStaffHeader(viewName) {
                var header = staffViewHeaders[viewName] || staffViewHeaders.inventory;
                var eyebrow = document.getElementById('staffHeaderEyebrow');
                var title = document.getElementById('staffHeaderTitle');
                var subtitle = document.getElementById('staffHeaderSubtitle');

                if (eyebrow) {
                    eyebrow.textContent = header.eyebrow;
                }
                if (title) {
                    title.textContent = header.title;
                }
                if (subtitle) {
                    subtitle.textContent = header.subtitle;
                }
            }

            function toggleAccountMenu(event) {
                event.stopPropagation();
                var menu = document.getElementById('accountMenu');
                var trigger = menu.querySelector('.account-trigger');
                var isOpen = menu.classList.toggle('open');
                trigger.setAttribute('aria-expanded', isOpen ? 'true' : 'false');
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

            function setFilter(button) {
                activeFilter = button.getAttribute('data-filter');
                var tabs = document.querySelectorAll('.tab-btn');
                for (var i = 0; i < tabs.length; i++) {
                    tabs[i].classList.remove('active');
                }
                button.classList.add('active');
                inventoryPage = 1;
                renderInventoryRows();
            }

            function filterIngredients() {
                inventoryPage = 1;
                renderInventoryRows();
            }

            function renderInventoryRows() {
                var searchInput = document.getElementById('ingredientSearch');
                var search = searchInput ? searchInput.value.toLowerCase().trim() : '';
                var rows = document.querySelectorAll('.ingredient-row');
                var matchedRows = [];

                for (var i = 0; i < rows.length; i++) {
                    var row = rows[i];
                    var status = row.getAttribute('data-status');
                    var name = (row.getAttribute('data-name') || '').toLowerCase();
                    var matchStatus = activeFilter === 'all' || status === activeFilter;
                    var matchSearch = !search || name.indexOf(search) !== -1;
                    if (matchStatus && matchSearch) {
                        matchedRows.push(row);
                    }
                    row.style.display = 'none';
                }

                var totalPages = Math.max(1, Math.ceil(matchedRows.length / inventoryPageSize));
                inventoryPage = Math.max(1, Math.min(inventoryPage, totalPages));
                renderPagedRows(matchedRows, inventoryPage, inventoryPageSize);

                updatePaginationInfo(
                        'inventoryResultText',
                        'inventoryPageText',
                        'inventoryPrevBtn',
                        'inventoryNextBtn',
                        matchedRows.length,
                        inventoryPage,
                        totalPages,
                        'nguyên liệu'
                        );
            }

            function changeInventoryPage(delta) {
                inventoryPage += delta;
                renderInventoryRows();
            }

            function filterHistories() {
                historyPage = 1;
                renderHistoryRows();
            }

            function renderHistoryRows() {
                var input = document.getElementById('historySearch');
                var search = input ? input.value.toLowerCase().trim() : '';
                var rows = document.querySelectorAll('.history-row');
                var matchedRows = [];

                for (var i = 0; i < rows.length; i++) {
                    var text = (rows[i].getAttribute('data-history-text') || '').toLowerCase();
                    if (!search || text.indexOf(search) !== -1) {
                        matchedRows.push(rows[i]);
                    }
                    rows[i].style.display = 'none';
                }

                var totalPages = Math.max(1, Math.ceil(matchedRows.length / historyPageSize));
                historyPage = Math.max(1, Math.min(historyPage, totalPages));
                renderPagedRows(matchedRows, historyPage, historyPageSize);

                updatePaginationInfo(
                        'historyResultText',
                        'historyPageText',
                        'historyPrevBtn',
                        'historyNextBtn',
                        matchedRows.length,
                        historyPage,
                        totalPages,
                        'dòng lịch sử'
                        );
            }

            function changeHistoryPage(delta) {
                historyPage += delta;
                renderHistoryRows();
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

            function updateInvoicePreview() {
                var select = document.getElementById('ingredientSelect');
                var option = select.options[select.selectedIndex];
                var orderedInput = document.getElementById('orderedQuantityInput');
                var receivedInput = document.getElementById('receivedQuantityInput');
                var orderedQuantity = parseFloat(orderedInput.value || '0');
                var receivedQuantity = parseFloat(receivedInput.value || '0');
                var price = parseFloat(document.getElementById('unitPriceInput').value || '0');
                var unit = option ? (option.getAttribute('data-unit') || '') : '';
                var ingredient = option && option.value ? option.getAttribute('data-name') : 'Chưa chọn nguyên liệu';
                var quantityInput = document.getElementById('quantityInput');

                if (document.activeElement === orderedInput && !receivedInput.value) {
                    receivedInput.value = orderedInput.value;
                    receivedQuantity = orderedQuantity;
                }

                document.getElementById('previewIngredient').textContent = ingredient;
                document.getElementById('previewOrderedQuantity').textContent = orderedQuantity + (unit ? ' ' + unit : '');
                document.getElementById('previewReceivedQuantity').textContent = receivedQuantity + (unit ? ' ' + unit : '');
                document.getElementById('previewOrderedTotal').textContent = formatMoney(orderedQuantity * price);
                document.getElementById('previewReceivedTotal').textContent = formatMoney(receivedQuantity * price);

                if (quantityInput) {
                    quantityInput.value = receivedQuantity;
                }

                var supplierId = option ? option.getAttribute('data-supplier') : '';
                var supplierSelect = document.getElementById('supplierSelect');
                if (supplierId && !supplierSelect.value) {
                    for (var i = 0; i < supplierSelect.options.length; i++) {
                        if (supplierSelect.options[i].value === supplierId) {
                            supplierSelect.selectedIndex = i;
                            break;
                        }
                    }
                }
            }

            updateInvoicePreview();
            renderInventoryRows();
            renderHistoryRows();
        </script>
    </body>
</html>
