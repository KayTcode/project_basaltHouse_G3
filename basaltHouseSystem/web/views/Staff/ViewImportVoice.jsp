<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="model.ImportInvoicesDetail"%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi tiết hóa đơn nhập | BasaltHouse</title>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/Staff/ViewImportVoice.css?v=1" rel="stylesheet">
    </head>
    <body>
        <main class="invoice-page">
            <header class="invoice-hero">

                <div class="hero-copy">
                    <p class="eyebrow">Staff / Import invoice</p>
                    <h1>Chi tiết hóa đơn nhập</h1>
                    <p>Kiểm tra thông tin phiếu nhập, chỉnh sửa số lượng và cập nhật trạng thái nhận hàng.</p>
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
                    Không đọc được dữ liệu lựa chọn: <c:out value="${dataError}"/>
                </div>
            </c:if>

            <c:choose>
                <c:when test="${empty invoiceDetail}">
                    <section class="empty-state">
                        <span class="material-symbols-outlined">receipt_long</span>
                        <h2>Không tìm thấy hóa đơn</h2>
                        <p>Hóa đơn nhập này có thể đã bị xóa hoặc không còn khả dụng.</p>
                        <a href="${pageContext.request.contextPath}/staff">
                            <span class="material-symbols-outlined">arrow_back</span>
                            Quay lại danh sách
                        </a>
                    </section>
                </c:when>
                <c:otherwise>
                    <c:catch var="supplierInvoiceCodeLookupError">
                        <c:set var="supplierInvoiceCodeValue" value="${invoiceDetail.supplierInvoiceCode}"/>
                    </c:catch>

                    <form id="invoiceForm"
                          class="invoice-layout"
                          method="post"
                          action="${pageContext.request.contextPath}/viewimportvoice">
                        <input type="hidden" name="importId" value="${invoiceDetail.importId}">
                        <input type="hidden" name="importDetailId" value="${invoiceDetail.importDetailId}">

                        <section class="invoice-main">
                            <section class="panel invoice-overview">
                                <div class="panel-head">
                                    <div>
                                        <p>Thông tin hóa đơn</p>
                                        <h2><c:out value="${invoiceDetail.importCode}"/></h2>
                                    </div>
                                    <span class="status-badge ${invoiceDetail.status eq 'Pending' ? 'pending' : 'confirmed'}">
                                        <span class="material-symbols-outlined">${invoiceDetail.status eq 'Pending' ? 'schedule' : 'check_circle'}</span>
                                        <c:out value="${invoiceDetail.status}"/>
                                    </span>
                                </div>

                                <div class="form-grid">
                                    <label>
                                        <span>Mã phiếu nhập</span>
                                        <input type="text" name="importCode" value="${invoiceDetail.importCode}" required>
                                    </label>
                                    <label>
                                        <span>Mã hóa đơn NCC</span>
                                        <input type="text" id="supplierInvoiceCodeInput" name="supplierInvoiceCode" value="${not empty supplierInvoiceCodeValue ? supplierInvoiceCodeValue : param.supplierInvoiceCode}" placeholder="SUP-INV-001">
                                    </label>
                                    <label>
                                        <span>Trạng thái</span>
                                        <select name="status" id="statusSelect" required>
                                            <option value="Confirmed" ${invoiceDetail.status eq 'Confirmed' ? 'selected' : ''}>Đã nhận</option>
                                            <option value="Pending" ${invoiceDetail.status eq 'Pending' ? 'selected' : ''}>Chờ nhận</option>
                                        </select>
                                    </label>

                                </div>

                                <div class="form-grid">
                                    <label>
                                        <span>Nhà cung cấp</span>
                                        <select name="supplierId" id="supplierSelect" required>
                                            <c:forEach var="supplier" items="${suppliers}">
                                                <option value="${supplier.id}" ${supplier.id == invoiceDetail.supplierId ? 'selected' : ''}>
                                                    <c:out value="${supplier.name}"/>
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </label>
                                    <label>

                                        <span>Người tạo</span>
                                        <input type="text" value="${invoiceDetail.staffName}" readonly>
                                    </label>
                                    <label>
                                        <span>Nha cung cap hien tai</span>
                                        <input type="text" value="${empty invoiceDetail.supplierName ? 'Chua co NCC' : invoiceDetail.supplierName}" readonly>
                                    </label>
                                </div>

                                <div class="form-grid">
                                    <label>
                                        <span>Ngày đặt</span>
                                        <input type="datetime-local" name="orderedDate" value="${invoiceDetail.orderedDateInput}" required>
                                    </label>
                                    <label>
                                        <span>Ngày dự kiến</span>
                                        <input type="datetime-local" name="expectedDate" value="${invoiceDetail.expectedDateInput}">
                                    </label>
                                    <label>
                                        <span>Ngày nhận</span>
                                        <input type="datetime-local" name="receivedDate" value="${invoiceDetail.receivedDateInput}">
                                    </label>
                                </div>

                                <div class="form-grid form-grid-2">
                                    <label>
                                        <span>Ghi chú hóa đơn</span>
                                        <textarea name="note" rows="4" placeholder="Ghi chú chung"><c:out value="${invoiceDetail.invoiceNote}"/></textarea>
                                    </label>
                                    <label>
                                        <span>Lý do từ chối</span>
                                        <textarea name="rejectReason" rows="4" placeholder="Chỉ nhập khi hóa đơn bị từ chối"><c:out value="${invoiceDetail.rejectReason}"/></textarea>
                                    </label>
                                </div>
                            </section>

                            <section class="panel detail-panel">
                                <div class="panel-head">
                                    <div>
                                        <p>Chi tiết nguyên liệu</p>
                                        <h2><c:out value="${invoiceDetail.ingredientName}"/></h2>
                                    </div>
                                    <span class="ingredient-unit" id="unitText"><c:out value="${invoiceDetail.unit}"/></span>
                                </div>

                                <label>
                                    <span>Nguyên liệu</span>
                                    <select name="ingredientId" id="ingredientSelect" required>
                                        <c:forEach var="item" items="${ingredients}">
                                            <option value="${item.ingredientId}"
                                                    data-unit="${item.unit}"
                                                    data-supplier="${item.supplierId}"

                                                    ${item.ingredientId == invoiceDetail.ingredientId ? 'selected' : ''}>
                                                <c:out value="${item.ingredientName}"/> - còn <c:out value="${item.stockQuantity}"/> <c:out value="${item.unit}"/>
                                            </option>
                                        </c:forEach>
                                    </select>
                                </label>

                                <div class="form-grid">
                                    <label>
                                        <span>Số lượng đặt</span>
                                        <input type="number" id="orderedQuantityInput" name="orderedQuantity" min="0.01" step="0.01" value="${invoiceDetail.orderedQuantity}" required>
                                    </label>
                                    <label>
                                        <span>Số lượng nhận</span>
                                        <input type="number" id="receivedQuantityInput" name="receivedQuantity" min="0" step="0.01" value="${invoiceDetail.receivedQuantity}" required>
                                    </label>
                                    <label>
                                        <span>Đơn giá</span>
                                        <input type="number" id="unitPriceInput" name="unitPrice" min="0" step="100" value="${invoiceDetail.unitPrice}" required>
                                    </label>
                                </div>

                                <div class="form-grid form-grid-2">
                                    <label>
                                        <span>Chênh lệch</span>
                                        <textarea name="discrepancyNote" rows="4" placeholder="Ghi nhận thiếu, thừa hoặc hàng lỗi"><c:out value="${invoiceDetail.discrepancyNote}"/></textarea>
                                    </label>
                                    <label>
                                        <span>Ghi chú chi tiết</span>
                                        <textarea name="detailNote" rows="4" placeholder="Ghi chú cho nguyên liệu"><c:out value="${invoiceDetail.detailNote}"/></textarea>
                                    </label>
                                </div>
                            </section>
                        </section>

                        <aside class="invoice-side">
                            <section class="summary-card">
                                <div class="summary-top">
                                    <span class="material-symbols-outlined">receipt_long</span>
                                    <div>
                                        <p>Tổng nhận</p>
                                        <strong id="receivedTotalText">
                                            <fmt:formatNumber value="${invoiceDetail.totalReceivedAmount}" type="number" maxFractionDigits="0"/> đ
                                        </strong>
                                    </div>
                                </div>

                                <div class="summary-list">
                                    <div>
                                        <span>Phiếu nhập</span>
                                        <strong><c:out value="${invoiceDetail.importCode}"/></strong>
                                    </div>
                                    <div>
                                        <span>Mã hóa đơn NCC</span>
                                        <strong id="supplierInvoiceCodeText"><c:out value="${not empty supplierInvoiceCodeValue ? supplierInvoiceCodeValue : param.supplierInvoiceCode}"/></strong>
                                    </div>
                                    <div>
                                        <span>Ngày đặt</span>
                                        <strong><c:out value="${invoiceDetail.orderedDateText}"/></strong>
                                    </div>
                                    <div>
                                        <span>Số lượng đặt</span>
                                        <strong id="orderedQuantityText"><c:out value="${invoiceDetail.orderedQuantity}"/> <c:out value="${invoiceDetail.unit}"/></strong>
                                    </div>
                                    <div>
                                        <span>Số lượng nhận</span>
                                        <strong id="receivedQuantityText"><c:out value="${invoiceDetail.receivedQuantity}"/> <c:out value="${invoiceDetail.unit}"/></strong>
                                    </div>
                                    <div>
                                        <span>Tổng đặt</span>
                                        <strong id="orderedTotalText">
                                            <fmt:formatNumber value="${invoiceDetail.totalOrderedAmount}" type="number" maxFractionDigits="0"/> đ
                                        </strong>
                                    </div>
                                </div>

                                <div class="action-row">
                                    <button type="submit" class="btn-primary">
                                        <span class="material-symbols-outlined">save</span>
                                        Update
                                    </button>
                                    <a href="${pageContext.request.contextPath}/staff" class="btn-secondary">
                                        <span class="material-symbols-outlined">close</span>
                                        Thoát
                                    </a>
                                </div>
                            </section>
                        </aside>
                    </form>
                </c:otherwise>
            </c:choose>
        </main>

        <script>
            function getNumber(id) {
                var element = document.getElementById(id);
                return element ? parseFloat(element.value || '0') || 0 : 0;
            }

            function formatMoney(value) {
                return new Intl.NumberFormat('vi-VN').format(value || 0) + ' đ';
            }

            function updateInvoiceTotals() {
                var form = document.getElementById('invoiceForm');
                if (!form) {
                    return;
                }

                var orderedQuantity = getNumber('orderedQuantityInput');
                var receivedQuantity = getNumber('receivedQuantityInput');
                var unitPrice = getNumber('unitPriceInput');
                var ingredientSelect = document.getElementById('ingredientSelect');
                var option = ingredientSelect ? ingredientSelect.options[ingredientSelect.selectedIndex] : null;
                var unit = option ? (option.getAttribute('data-unit') || '') : '';

                document.getElementById('orderedTotalText').textContent = formatMoney(orderedQuantity * unitPrice);
                document.getElementById('receivedTotalText').textContent = formatMoney(receivedQuantity * unitPrice);
                document.getElementById('orderedQuantityText').textContent = orderedQuantity + (unit ? ' ' + unit : '');
                document.getElementById('receivedQuantityText').textContent = receivedQuantity + (unit ? ' ' + unit : '');
                document.getElementById('unitText').textContent = unit || 'Đơn vị';

                var supplierInvoiceCodeInput = document.getElementById('supplierInvoiceCodeInput');
                var supplierInvoiceCodeText = document.getElementById('supplierInvoiceCodeText');
                if (supplierInvoiceCodeInput && supplierInvoiceCodeText) {
                    supplierInvoiceCodeText.textContent = supplierInvoiceCodeInput.value || '-';
                }
            }

            function syncSupplierFromIngredient() {
                var ingredientSelect = document.getElementById('ingredientSelect');
                var supplierSelect = document.getElementById('supplierSelect');
                if (!ingredientSelect || !supplierSelect) {
                    return;
                }

                var option = ingredientSelect.options[ingredientSelect.selectedIndex];
                var supplierId = option ? option.getAttribute('data-supplier') : '';
                if (!supplierId) {
                    updateInvoiceTotals();
                    return;
                }

                for (var i = 0; i < supplierSelect.options.length; i++) {
                    if (supplierSelect.options[i].value === supplierId) {
                        supplierSelect.selectedIndex = i;
                        break;
                    }
                }
                updateInvoiceTotals();
            }

            var form = document.getElementById('invoiceForm');
            var ingredientSelect = document.getElementById('ingredientSelect');
            if (form) {
                form.addEventListener('input', updateInvoiceTotals);
            }
            if (ingredientSelect) {
                ingredientSelect.addEventListener('change', syncSupplierFromIngredient);
            }
            updateInvoiceTotals();
        </script>
    </body>
</html>
