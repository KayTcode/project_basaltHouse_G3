<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản Lý Sản Phẩm - BasaltHouse</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_common.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_account.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin/admin_product.css">
    </head>
    <body class="admin-dashboard-body">

        <%@ include file="/views/admin/header.jsp" %>

        <div class="app-container">

            <%@ include file="/views/admin/sidebar.jsp" %>

            <main class="main-content">

                <div class="viewport-headline-bar">
                    <div class="headline-left">
                        <h1 class="page-title">Quản Lý Danh Sách Sản Phẩm</h1>
                        <p class="page-desc">Xem, tìm kiếm, cấu hình kích thước và công thức pha chế</p>
                    </div>
                </div>

                <%-- ĐÃ THÊM: Hiển thị thông báo Toast sau khi Thêm/Sửa/Xóa --%>
                <c:if test="${not empty sessionScope.toastMessage}">
                    <div style="background: var(--basalt-emerald-light); color: var(--basalt-emerald); padding: 12px 20px; border-radius: 8px; margin-bottom: 16px; font-weight: 600;">
                        <i class="fa-solid fa-circle-check"></i> ${sessionScope.toastMessage}
                    </div>
                    <c:remove var="toastMessage" scope="session"/>
                </c:if>
                <c:if test="${not empty sessionScope.toastError}">
                    <div style="background: var(--basalt-red-light); color: var(--basalt-red); padding: 12px 20px; border-radius: 8px; margin-bottom: 16px; font-weight: 600;">
                        <i class="fa-solid fa-circle-xmark"></i> ${sessionScope.toastError}
                    </div>
                    <c:remove var="toastError" scope="session"/>
                </c:if>

                <div class="account-overview-grid" style="grid-template-columns: repeat(3, 1fr);">
                    <div class="overview-card-item">
                        <div class="card-icon-wrapper" style="background-color: #f1f5f9; color: #475569;">
                            <i class="fa-solid fa-box-open"></i>
                        </div>
                        <div class="card-metrics-data">
                            <span class="metric-label">Tổng Sản Phẩm</span>
                            <span class="metric-count">${data.stats.total}</span>
                        </div>
                    </div>
                    <div class="overview-card-item">
                        <div class="card-icon-wrapper" style="background-color: var(--basalt-emerald-light); color: var(--basalt-emerald);">
                            <i class="fa-solid fa-circle-check"></i>
                        </div>
                        <div class="card-metrics-data">
                            <span class="metric-label">Đang Kinh Doanh</span>
                            <span class="metric-count text-success">${data.stats.active}</span>
                        </div>
                    </div>
                    <div class="overview-card-item">
                        <div class="card-icon-wrapper" style="background-color: var(--basalt-red-light); color: var(--basalt-red);">
                            <i class="fa-solid fa-circle-xmark"></i>
                        </div>
                        <div class="card-metrics-data">
                            <span class="metric-label">Ngừng Kinh Doanh</span>
                            <span class="metric-count text-danger">${data.stats.inactive}</span>
                        </div>
                    </div>
                </div>

                <div class="data-management-panel">
                    <div class="panel-header-toolbar">
                        <h2>Danh Sách Phân Quyền</h2>
                        <div class="toolbar-actions-group">
                            <button class="btn-primary-action" onclick="openAddModal()">
                                <i class="fa-solid fa-plus-circle"></i> Thêm Sản Phẩm Mới
                            </button>
                        </div>
                    </div>

                    <form action="${pageContext.request.contextPath}/admin/products" method="GET" class="search-filter-belt" style="padding: 16px 24px; display: flex; gap: 16px; align-items: center; border-bottom: 1px solid var(--basalt-border); background: #fafafa;">
                        <div class="search-input-box">
                            <i class="fa-solid fa-magnifying-glass search-icon"></i>
                            <input type="text" name="search" id="searchAccountInput" placeholder="Tìm theo tên sản phẩm..." value="${data.oldSearch}">
                        </div>

                        <div style="display:flex; gap:12px; align-items:center;">
                            <div class="select-wrapper">
                                <label style="font-size:13px; font-weight:600; margin-right:6px;">Danh mục:</label>
                                <select name="categoryId" onchange="this.form.submit()" style="padding: 8px 12px; border: 1px solid var(--basalt-border); border-radius: 6px;">
                                    <option value="">Tất cả danh mục</option>
                                    <c:forEach var="cat" items="${data.categories}">
                                        <option value="${cat.categoryId}" ${data.oldCategoryId == cat.categoryId ? 'selected' : ''}>
                                            ${cat.categoryName}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            <button type="submit" class="btn-primary-action" style="background:#495057;">
                                <i class="fa-solid fa-filter"></i> Lọc
                            </button>
                        </div>
                    </form>

                    <div class="responsive-table-wrapper">
                        <table class="basalt-custom-table">
                            <thead>
                                <tr>
                                    <th style="width: 60px;">Mã</th>
                                    <th style="width: 80px;">Hình ảnh</th>
                                    <th>Tên Sản Phẩm</th>
                                    <th>Danh Mục</th>
                                    <th style="width: 130px;">Giá Cơ Bản</th>
                                    <th style="width: 180px;">Kích thước (Sizes)</th>
                                    <th style="width: 220px;">Công thức (Recipe)</th>
                                    <th style="width: 110px;">Trạng Thế</th>
                                    <th style="width: 100px; text-align: center;">Hành Động</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty data.products}">
                                        <c:forEach var="item" items="${data.products}">
                                            <tr class="account-row-item">
                                                <td><code>#${item.product.productId}</code></td>
                                                <td>
                                                    <img src="${not empty item.product.imageUrl ? item.product.imageUrl : pageContext.request.contextPath+'/images/default-food.png'}" class="product-thumbnail" alt="Product Image"/>
                                                </td>
                                                <td>
                                                    <div class="user-cell-name">
                                                        <span style="font-weight: 700; color: var(--basalt-green);">${item.product.productName}</span>
                                                    </div>
                                                    <div style="color: #6c757d; font-size: 11px;">Ngày tạo: ${item.createdAtFormatted}</div>
                                                </td>
                                                <td>
                                                    <span class="role-badge role-customer">${item.categoryName}</span>
                                                </td>
                                                <td>
                                                    <strong style="color: var(--basalt-red)">
                                                        <fmt:formatNumber value="${item.product.price}" type="currency" currencySymbol="đ" maxFractionDigits="0"/>
                                                    </strong>
                                                </td>

                                                <td>
                                                    <c:choose>
                                                        <c:when test="${not empty item.sizes}">
                                                            <c:forEach var="sz" items="${item.sizes}">
                                                                <div class="badge-size-price">
                                                                    ${sz.sizeName}: <fmt:formatNumber value="${sz.price}" type="currency" currencySymbol="" maxFractionDigits="0"/>đ
                                                                </div>
                                                            </c:forEach>
                                                        </c:when>
                                                        <c:otherwise><span style="color: #adb5bd; font-style: italic;">Chưa cấu hình size</span></c:otherwise>
                                                    </c:choose>
                                                </td>

                                                <td>
                                                    <c:choose>
                                                        <c:when test="${not empty item.recipes}">
                                                            <c:forEach var="rc" items="${item.recipes}">
                                                                <div class="recipe-ingredient-item">
                                                                    <i class="fa-solid fa-angles-right" style="font-size: 9px; color: #94a3b8"></i> 
                                                                    NL #${rc.ingredientName}: <strong>${rc.quantity}</strong> ${rc.unit}
                                                                </div>
                                                            </c:forEach>
                                                        </c:when>
                                                        <c:otherwise><span style="color: #adb5bd; font-style: italic;">Chưa lập công thức</span></c:otherwise>
                                                    </c:choose>
                                                </td>

                                                <td style="text-align: center;">
                                                    <c:choose>
                                                        <c:when test="${item.product.isActive}">
                                                            <span class="status-badge badge-active">Kinh Doanh</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="status-badge badge-suspended">Tạm Ngừng</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td style="text-align: right; padding-right: 24px;">
                                                    <div class="row-actions-cell" style="justify-content: flex-end;">
                                                        <%-- ĐÃ SỬA: truyền kèm sizesJson/recipesJson để đổ dữ liệu vào modal Edit --%>
                                                        <button class="btn-icon-action action-edit" title="Chỉnh sửa sản phẩm"
                                                                onclick='openEditModal(${item.product.productId}, "${fn:replace(item.product.productName, "\"", "")}", ${item.product.categoryId}, ${item.product.price}, ${item.product.isActive}, ${item.sizesJson}, ${item.recipesJson})'>
                                                            <i class="fa-solid fa-pen-to-square"></i>
                                                        </button>
                                                        <form action="${pageContext.request.contextPath}/admin/products" method="POST" style="display:inline;" onsubmit="return confirm('Bạn có chắc chắn muốn xóa sản phẩm này?');">
                                                            <input type="hidden" name="action" value="delete">
                                                            <input type="hidden" name="productId" value="${item.product.productId}">
                                                            <button type="submit" class="btn-icon-action" style="color:var(--basalt-red);" title="Xóa sản phẩm">
                                                                <i class="fa-solid fa-trash-can"></i>
                                                            </button>
                                                        </form>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="9" style="text-align: center; padding: 40px; color: #6c757d;">
                                                <i class="fa-solid fa-box-open" style="font-size: 32px; margin-bottom: 12px; display:block;"></i>
                                                Không tìm thấy bất kỳ sản phẩm nào phù hợp điều kiện lọc.
                                            </td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>

                    <c:if test="${data.totalPages > 1}">
                        <div style="display:flex;gap:6px;justify-content:flex-end;margin:20px 24px;font-size:13px">
                            <c:forEach begin="1" end="${data.totalPages}" var="p">
                                <a href="?page=${p}&search=${data.oldSearch}&categoryId=${data.oldCategoryId}" 
                                   style="padding:6px 12px;border:1px solid #e2e2e2;border-radius:6px;text-decoration:none; ${p == data.currentPage ? 'background:var(--basalt-green);color:#fff;border-color:var(--basalt-green);' : 'color:#555;'}">
                                    ${p}
                                </a>
                            </c:forEach>
                        </div>
                    </c:if>
                </div>
            </main>
        </div>

        <div id="addProductModal" class="modal-backdrop">
            <div class="modal-card" style="width: 700px; max-height: 90vh; overflow-y: auto;">
                <div class="modal-card-header">
                    <h3><i class="fa-solid fa-circle-plus text-success"></i> Tạo Sản Phẩm Mới</h3>
                    <span class="close-modal-x" onclick="closeAddModal()">&times;</span>
                </div>
                <form action="${pageContext.request.contextPath}/admin/products" method="POST">
                    <input type="hidden" name="action" value="add">
                    <div class="modal-card-body">
                        <div class="modal-form-flex-stack">

                            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px;">
                                <div class="form-field-group">
                                    <label>Tên sản phẩm *</label>
                                    <input type="text" name="productName" class="form-input-text" required placeholder="Tên đồ uống / món ăn...">
                                </div>
                                <div class="form-field-group">
                                    <label>Danh mục phân loại *</label>
                                    <select name="categoryId" class="form-select" required>
                                        <c:forEach var="cat" items="${data.categories}">
                                            <option value="${cat.categoryId}">${cat.categoryName}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="form-field-group">
                                    <label>Giá bán cơ bản (đ) *</label>
                                    <input type="number" name="price" class="form-input-text" required min="0" placeholder="Ví dụ: 35000">
                                </div>
                                <div class="form-field-group">
                                    <label>Đường dẫn hình ảnh (URL)</label>
                                    <input type="text" name="imageUrl" class="form-input-text" placeholder="https://...">
                                </div>
                            </div>

                            <div class="form-field-group" style="margin-top: 8px;">
                                <label>Mô tả sản phẩm</label>
                                <textarea name="description" class="form-input-text" rows="2" placeholder="Nhập mô tả ngắn gọn về sản phẩm..."></textarea>
                            </div>

                            <div class="section-header-add">
                                <span><i class="fa-solid fa-mug-hot"></i> Cấu Hình Kích Thước (Sizes)</span>
                                <button type="button" class="btn-primary-action" style="padding: 4px 12px; font-size: 12px;" onclick="addSizeRow('size-container')">
                                    <i class="fa-solid fa-plus"></i> Thêm Size
                                </button>
                            </div>
                            <div id="size-container">
                            </div>

                            <div class="section-header-add">
                                <span><i class="fa-solid fa-blender"></i> Định Lượng Nguyên Liệu</span>
                                <button type="button" class="btn-primary-action" style="padding: 4px 12px; font-size: 12px;" onclick="addIngredientRow('ingredient-container')">
                                    <i class="fa-solid fa-plus"></i> Thêm Nguyên Liệu
                                </button>
                            </div>
                            <div id="ingredient-container">
                            </div>

                        </div>
                    </div>
                    <div class="modal-card-footer">
                        <button type="button" class="btn-cancel" onclick="closeAddModal()">Hủy</button>
                        <button type="submit" class="btn-primary-action" style="background-color: var(--basalt-green);">Lưu Sản Phẩm</button>
                    </div>
                </form>
            </div>
        </div>

        <div id="editProductModal" class="modal-backdrop">
            <div class="modal-card" style="width: 700px; max-height: 90vh; overflow-y: auto;">
                <div class="modal-card-header">
                    <h3 id="edit_notice_title">Chỉnh sửa thông tin</h3>
                    <span class="close-modal-x" onclick="closeEditModal()">&times;</span>
                </div>
                <form action="${pageContext.request.contextPath}/admin/products" method="POST">
                    <input type="hidden" name="action" value="edit">
                    <input type="hidden" name="productId" id="edit_id">

                    <div class="modal-card-body">
                        <div class="edit-account-notice">
                            <i class="fa-solid fa-circle-info"></i> Lưu ý: Bạn đang thực hiện thay đổi thông tin định dạng cốt lõi của sản phẩm.
                        </div>
                        <div class="modal-form-flex-stack">
                            <div class="form-field-group">
                                <label>Tên sản phẩm</label>
                                <input type="text" name="productName" id="edit_name" class="form-input-text" required>
                            </div>
                            <div class="form-field-group">
                                <label>Danh mục</label>
                                <%-- ĐÃ SỬA: ${categories} -> ${data.categories} vì categories không tồn tại trong scope --%>
                                <select name="categoryId" id="edit_category" class="form-select">
                                    <c:forEach var="cat" items="${data.categories}">
                                        <option value="${cat.categoryId}">${cat.categoryName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="form-field-group">
                                <label>Giá tiền gốc (đ)</label>
                                <input type="number" name="price" id="edit_price" class="form-input-text" required min="0">
                            </div>
                            <div class="form-field-group">
                                <label>Mô tả sản phẩm</label>
                                <textarea name="description" id="edit_description" class="form-input-text" rows="2"></textarea>
                            </div>
                            <div class="form-field-group">
                                <label>Đường dẫn hình ảnh (URL)</label>
                                <input type="text" name="imageUrl" id="edit_imageUrl" class="form-input-text">
                            </div>
                            <div class="form-field-group">
                                <label>Trạng thái kinh doanh</label>
                                <select name="isActive" id="edit_status" class="form-select">
                                    <option value="true">Đang kinh doanh (Kích hoạt)</option>
                                    <option value="false">Tạm ngừng bán (Khóa hiển thị)</option>
                                </select>
                            </div>

                            <%-- ĐÃ THÊM: khung cấu hình Size & Recipe cho modal Edit (trước đây chưa có -> không sửa được size) --%>
                            <div class="section-header-add">
                                <span><i class="fa-solid fa-mug-hot"></i> Cấu Hình Kích Thước (Sizes)</span>
                                <button type="button" class="btn-primary-action" style="padding: 4px 12px; font-size: 12px;" onclick="addSizeRow('edit-size-container')">
                                    <i class="fa-solid fa-plus"></i> Thêm Size
                                </button>
                            </div>
                            <div id="edit-size-container"></div>

                            <div class="section-header-add">
                                <span><i class="fa-solid fa-blender"></i> Định Lượng Nguyên Liệu</span>
                                <button type="button" class="btn-primary-action" style="padding: 4px 12px; font-size: 12px;" onclick="addIngredientRow('edit-ingredient-container')">
                                    <i class="fa-solid fa-plus"></i> Thêm Nguyên Liệu
                                </button>
                            </div>
                            <div id="edit-ingredient-container"></div>
                        </div>
                    </div>
                    <div class="modal-card-footer">
                        <button type="button" class="btn-cancel" onclick="closeEditModal()">Hủy bỏ</button>
                        <button type="submit" class="btn-primary-action" style="background-color: var(--basalt-blue);">Cập Nhật</button>
                    </div>
                </form>
            </div>
        </div>

        <script>
            function openAddModal() {
                // ĐÃ THÊM: reset lại các dòng size/recipe cũ mỗi lần mở modal Thêm mới
                document.getElementById('size-container').innerHTML = '';
                document.getElementById('ingredient-container').innerHTML = '';
                document.getElementById('addProductModal').classList.add('active');
            }
            function closeAddModal() {
                document.getElementById('addProductModal').classList.remove('active');
            }

            // ĐÃ SỬA: nhận thêm sizesArr, recipesArr để đổ dữ liệu Size/Recipe hiện có vào modal Edit
            function openEditModal(id, name, categoryId, price, isActive, sizesArr, recipesArr) {
                document.getElementById('edit_id').value = id;
                document.getElementById('edit_notice_title').innerText = "Sản phẩm mã #" + id;
                document.getElementById('edit_name').value = name;
                document.getElementById('edit_category').value = categoryId;

                price = price.toString();
                if (price.includes('.')) {
                    price = price.split('.')[0];
                }
                document.getElementById('edit_price').value = price;
                document.getElementById('edit_status').value = isActive.toString();

                // Reset rồi đổ lại danh sách Size/Recipe hiện có của sản phẩm
                document.getElementById('edit-size-container').innerHTML = '';
                document.getElementById('edit-ingredient-container').innerHTML = '';

                if (sizesArr) {
                    sizesArr.forEach(function (sz) {
                        addSizeRow('edit-size-container', sz.sizeId, sz.price);
                    });
                }
                if (recipesArr) {
                    recipesArr.forEach(function (rc) {
                        addIngredientRow('edit-ingredient-container', rc.ingredientId, rc.quantity, rc.unit);
                    });
                }

                document.getElementById('editProductModal').classList.add('active');
            }

            function closeEditModal() {
                document.getElementById('editProductModal').classList.remove('active');
            }
            
            // ── HÀM THÊM ĐỘNG DÒNG KÍCH THƯỚC (SIZES) ──
            // ĐÃ SỬA: thêm tham số containerId (dùng chung cho cả modal Add và Edit) + tham số fill sẵn giá trị khi Sửa
            function addSizeRow(containerId, selectedSizeId, selectedPrice) {
                const container = document.getElementById(containerId || 'size-container');
                const row = document.createElement('div');
                row.className = 'dynamic-row';
                
                // Móc dữ liệu FormSizes từ Map Backend đẩy sang bằng JSTL
                let optionsHtml = '';
                <c:forEach var="sz" items="${data.formSizes}">
                    optionsHtml += '<option value="${sz.sizeId}">${sz.sizeName}</option>';
                </c:forEach>

                row.innerHTML = `
                    <select name="sizeIds" class="form-select" style="width: 40%;" required>
                        ` + optionsHtml + `
                    </select>
                    <input type="number" name="sizePrices" class="form-input-text" style="width: 50%;" required placeholder="Giá cộng thêm (đ) VD: 5000">
                    <button type="button" class="btn-remove-row" onclick="this.parentElement.remove()" title="Xóa dòng"><i class="fa-solid fa-xmark"></i></button>
                `;
                container.appendChild(row);

                if (selectedSizeId !== undefined && selectedSizeId !== null) {
                    row.querySelector('select[name="sizeIds"]').value = selectedSizeId;
                    row.querySelector('input[name="sizePrices"]').value = selectedPrice;
                }
            }

            // ── HÀM THÊM ĐỘNG DÒNG NGUYÊN LIỆU (RECIPES) ──
            // ĐÃ SỬA: thêm tham số containerId + tham số fill sẵn giá trị khi Sửa
            function addIngredientRow(containerId, selectedIngId, selectedQty, selectedUnit) {
                const container = document.getElementById(containerId || 'ingredient-container');
                const row = document.createElement('div');
                row.className = 'dynamic-row';

                // Móc dữ liệu FormIngredients từ Map Backend đẩy sang
                let optionsHtml = '<option value="0" style="color: var(--basalt-green); font-weight: bold;">[+] Tạo nguyên liệu mới</option>';
                <c:forEach var="ig" items="${data.formIngredients}">
                    optionsHtml += '<option value="${ig.ingredientId}">Kho: ${ig.ingredientName}</option>';
                </c:forEach>

                row.innerHTML = `
                    <select name="ingredientIds" class="form-select" style="width: 30%;" onchange="toggleNewIngredientInput(this)">
                        ` + optionsHtml + `
                    </select>
                    <input type="text" name="ingredientNames" class="form-input-text input-new-ing" style="width: 30%; display:none;" placeholder="Nhập tên NL mới...">
                    <input type="number" step="0.01" name="quantities" class="form-input-text" style="width: 20%;" required placeholder="Số lượng">
                    <input type="text" name="units" class="form-input-text" style="width: 15%;" required placeholder="ĐV (ml, g..)">
                    <button type="button" class="btn-remove-row" onclick="this.parentElement.remove()" title="Xóa dòng"><i class="fa-solid fa-xmark"></i></button>
                `;
                container.appendChild(row);

                if (selectedIngId !== undefined && selectedIngId !== null) {
                    const selectEl = row.querySelector('select[name="ingredientIds"]');
                    selectEl.value = selectedIngId;
                    row.querySelector('input[name="quantities"]').value = selectedQty;
                    row.querySelector('input[name="units"]').value = selectedUnit;
                    toggleNewIngredientInput(selectEl);
                }
            }

            // ── ẨN/HIỆN Ô NHẬP TÊN NGUYÊN LIỆU MỚI KHI CHỌN SELECT ──
            function toggleNewIngredientInput(selectElem) {
                const row = selectElem.parentElement;
                const newNameInput = row.querySelector('.input-new-ing');
                
                if (selectElem.value === "0") {
                    // Nếu chọn "Tạo NL mới", hiện ô input tên ra
                    newNameInput.style.display = 'block';
                    newNameInput.setAttribute('required', 'true');
                } else {
                    // Nếu chọn NL có sẵn, ẩn ô input và xóa chữ đi
                    newNameInput.style.display = 'none';
                    newNameInput.removeAttribute('required');
                    newNameInput.value = '';
                }
            }
        </script>
    </body>
</html>
