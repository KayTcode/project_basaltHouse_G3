<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<section class="staff-view active" id="importView">
    <aside class="panel import-panel" id="importForm">
        <div class="panel-header compact">
            <div>
                <h2>Nhập thêm nguyên liệu</h2>
                <p>Phiếu nhập sẽ tạo hóa đơn và ghi lịch sử kho.</p>
            </div>
        </div>

        <form method="post"
              action="${pageContext.request.contextPath}/staff/import"
              class="import-form"
              oninput="updateInvoicePreview(event)">

            <input type="hidden" name="action" value="importIngredient">

            <div class="form-section">
                <div class="form-section-title">
                    <span class="material-symbols-outlined">receipt_long</span>
                    <h3>Phiếu nhập</h3>
                </div>

                <label>
                    <span>Nhà cung cấp</span>
                    <select name="supplierId"
                            id="supplierSelect"
                            onchange="loadSupplierIngredients(this)"
                            required>
                        <option value="">Chọn nhà cung cấp</option>
                        <c:forEach var="supplier" items="${suppliers}">
                            <option value="${supplier.id}" ${supplier.id == selectedSupplierId ? 'selected' : ''}>
                                <c:out value="${supplier.name}"/>
                            </option>
                        </c:forEach>
                    </select>
                </label>

                <div class="form-grid-3">
                    <label>
                        <span>Mã phiếu nhập</span>
                        <input type="text"
                               name="importCode"
                               placeholder="IMP-YYYYMMDD-001">
                    </label>

                    <label>
                        <span>Mã hóa đơn NCC</span>
                        <input type="text"
                               name="supplierInvoiceCode"
                               value="${param.supplierInvoiceCode}"
                               placeholder="SUP-INV-001">
                    </label>

                    <label>
                        <span>Trạng thái</span>
                        <select name="status">
                            <option value="Pending">
                                Chờ nhận
                            </option>
                        </select>
                    </label>
                </div>

                <div class="form-grid-3">
                    <label>
                        <span>Ngày đặt</span>
                        <input type="datetime-local"
                               name="orderedDate"
                               value="${currentDateInput}"
                               required>
                    </label>

                    <label>
                        <span>Ngày dự kiến</span>
                        <input type="datetime-local"
                               name="expectedDate">
                    </label>

                    <label>
                        <span>Ngày nhận</span>
                        <input type="datetime-local"
                               name="receivedDate"
                               value="${currentDateInput}">
                    </label>
                </div>
            </div>

            <div class="form-section">
                <div class="form-section-title">
                    <span class="material-symbols-outlined">inventory_2</span>
                    <h3>Chi tiết nguyên liệu</h3>
                </div>

                <div class="import-detail-list" id="importDetailRows">
                    <div class="import-detail-row">
                        <div class="import-detail-heading">
                            <strong class="import-detail-title">Nguyên liệu 1</strong>

                            <button class="remove-import-detail"
                                    type="button"
                                    onclick="removeImportDetail(this)"
                                    title="Xóa nguyên liệu"
                                    aria-label="Xóa nguyên liệu">
                                <span class="material-symbols-outlined">delete</span>
                            </button>
                        </div>

                        <label>
                            <span>Nguyên liệu</span>

                            <select name="ingredientId"
                                    class="import-ingredient-select"
                                    required>
                                <option value="">Chọn nguyên liệu</option>

                                <c:forEach var="item" items="${importIngredients}">
                                    <option value="${item.id}">
                                        <c:out value="${item.name}"/>
                                        - còn
                                        <c:out value="${item.stockText}"/>
                                        <c:out value="${item.unit}"/>
                                    </option>
                                </c:forEach>
                            </select>

                            <c:choose>
                                <c:when test="${empty importIngredients}">
                                    <small class="field-hint">
                                        ${empty selectedSupplierId
                                                ? 'Chọn nhà cung cấp để tải danh sách nguyên liệu.'
                                                : 'Nhà cung cấp này chưa có nguyên liệu đang hoạt động.'}
                                    </small>
                                </c:when>

                                <c:otherwise>
                                    <small class="field-hint" hidden></small>
                                </c:otherwise>
                            </c:choose>
                        </label>

                        <div class="form-grid-3">
                            <label>
                                <span>Số lượng đặt</span>
                                <input type="number"
                                       class="ordered-quantity-input"
                                       name="orderedQuantity"
                                       min="0.01"
                                       step="0.01"
                                       required>
                            </label>

                            <label>
                                <span>Số lượng nhận</span>
                                <input type="number"
                                       class="received-quantity-input"
                                       name="receivedQuantity"
                                       min="0"
                                       step="0.01"
                                       required>
                            </label>

                            <label>
                                <span>Đơn giá</span>
                                <input type="number"
                                       class="unit-price-input"
                                       name="unitPrice"
                                       min="0"
                                       step="100"
                                       value="0"
                                       required>
                            </label>
                        </div>

                        <label>
                            <span>Chênh lệch</span>
                            <textarea name="discrepancyNote"
                                      rows="2"
                                      placeholder="Ghi nhận thiếu, thừa hoặc hàng lỗi"></textarea>
                        </label>
                    </div>
                </div>

                <button class="add-import-detail"
                        type="button"
                        onclick="addImportDetail()">
                    <span class="material-symbols-outlined">add</span>
                    Thêm nguyên liệu
                </button>

                <label>
                    <span>Ghi chú</span>
                    <textarea name="note"
                              rows="3"
                              placeholder="Ghi chú phiếu nhập"></textarea>
                </label>

                <button class="submit-btn"
                        type="submit" ${empty selectedSupplierId ? 'disabled' : ''}>
                    <span class="material-symbols-outlined">save</span>
                    Lưu phiếu nhập
                </button>
            </div>

            <div class="invoice-preview">
                <div>
                    <span class="preview-label">Tóm tắt phiếu nhập</span>
                    <strong id="previewIngredientCount">1 nguyên liệu</strong>
                </div>

                <div class="preview-line">
                    <span>Nhà cung cấp</span>
                    <b id="previewSupplier">Chưa chọn</b>
                </div>

                <div class="preview-line">
                    <span>Tổng tiền đặt</span>
                    <b id="previewOrderedTotal">0 đ</b>
                </div>

                <div class="preview-line">
                    <span>Tổng giá tiền</span>
                    <b id="previewReceivedTotal">0 đ</b>
                </div>
            </div>
        </form>
    </aside>
</section>
