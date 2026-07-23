<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi tiết hóa đơn nhập | BasaltHouse</title>

        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/Staff/ViewImportVoice.css?v=20260709-1" rel="stylesheet">
    </head>

    <body class="invoice-detail-page">
        <main class="invoice-page">
            <header class="invoice-hero">
                <div class="hero-copy">
                    <p class="eyebrow">Nhân viên / Lịch sử nhập kho / Chi tiết</p>
                    <h1>Chi tiết hóa đơn nhập</h1>
                    <p>Kiểm tra thông tin phiếu nhập, chỉnh sửa số lượng và cập nhật trạng thái nhận hàng.</p>
                </div>

                <a class="hero-back" href="${pageContext.request.contextPath}/staff/history">
                    <span class="material-symbols-outlined">arrow_back</span>
                    Quay lại lịch sử
                </a>
            </header>

            <c:if test="${not empty errorMessage}">
                <div class="notice notice-error">
                    <span class="material-symbols-outlined">error</span>
                    <c:out value="${errorMessage}"/>
                </div>
            </c:if>

            <c:choose>
                <c:when test="${empty invoiceDetail}">
                    <section class="empty-state">
                        <span class="material-symbols-outlined">receipt_long</span>
                        <h2>Không tìm thấy hóa đơn</h2>
                        <p>Hóa đơn nhập này có thể đã bị xóa hoặc không còn khả dụng.</p>

                        <a href="${pageContext.request.contextPath}/staff/history">
                            <span class="material-symbols-outlined">arrow_back</span>
                            Quay lại danh sách
                        </a>
                    </section>
                </c:when>

                <c:otherwise>
                    <form id="invoiceForm"
                          class="invoice-layout"
                          method="post"
                          action="${pageContext.request.contextPath}/viewimportvoice">

                        <input type="hidden" name="importId" value="${invoiceDetail.importId}">

                        <section class="invoice-main">
                            <section class="panel invoice-overview">
                                <div class="panel-head">
                                    <div>
                                        <p>Thông tin hóa đơn</p>
                                        <h2><c:out value="${invoiceDetail.importCode}"/></h2>
                                    </div>

                                    <span class="status-badge ${invoiceDetail.status eq 'Pending' ? 'pending' : (invoiceDetail.status eq 'Rejected' ? 'rejected' : 'confirmed')}">
                                        <span class="material-symbols-outlined">
                                            ${invoiceDetail.status eq 'Pending' ? 'schedule' : (invoiceDetail.status eq 'Rejected' ? 'block' : 'check_circle')}
                                        </span>

                                        <c:choose>
                                            <c:when test="${invoiceDetail.status eq 'Pending'}">Chờ nhận</c:when>
                                            <c:when test="${invoiceDetail.status eq 'Confirmed'}">Đã nhận</c:when>
                                            <c:when test="${invoiceDetail.status eq 'Rejected'}">Đã từ chối</c:when>
                                            <c:otherwise>
                                                <c:out value="${invoiceDetail.status}"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>

                                <div class="form-grid">
                                    <label>
                                        <span>Mã phiếu nhập</span>
                                        <input type="text"
                                               value="${invoiceDetail.importCode}"
                                               readonly
                                               required>
                                    </label>

                                    <label>
                                        <span>Mã hóa đơn NCC</span>
                                        <input type="text"
                                               value="${invoiceDetail.supplierInvoiceCode}"
                                               placeholder="SUP-INV-001"
                                               readonly>
                                    </label>

                                    <label>
                                        <span>Trạng thái</span>
                                        <select name="status" id="statusSelect" required>
                                            <option value="Confirmed" ${invoiceDetail.status eq 'Confirmed' ? 'selected' : ''}>
                                                Đã nhận
                                            </option>

                                            <option value="Pending" ${invoiceDetail.status eq 'Pending' ? 'selected' : ''}>
                                                Chờ nhận
                                            </option>

                                            <option value="Rejected" ${invoiceDetail.status eq 'Rejected' ? 'selected' : ''}>
                                                Đã từ chối
                                            </option>
                                        </select>
                                    </label>
                                </div>

                                <div class="form-grid">
                                    <label>
                                        <span>Nhà cung cấp</span>

                                        <input type="text"
                                               value="${empty invoiceDetail.supplierName ? 'Chưa có nhà cung cấp' : invoiceDetail.supplierName}"
                                               readonly
                                               required>
                                    </label>

                                    <label>
                                        <span>Người tạo</span>
                                        <input type="text"
                                               value="${invoiceDetail.staffName}"
                                               readonly>
                                    </label>

                                    <label>
                                        <span>Nhà cung cấp hiện tại</span>
                                        <input type="text"
                                               value="${empty invoiceDetail.supplierName ? 'Chưa có nhà cung cấp' : invoiceDetail.supplierName}"
                                               readonly>
                                    </label>
                                </div>

                                <div class="form-grid">
                                    <label>
                                        <span>Ngày đặt</span>
                                        <input type="datetime-local"
                                               value="${invoiceDetail.orderedDateInput}"
                                               required
                                               readonly>
                                    </label>

                                    <label>
                                        <span>Ngày dự kiến</span>
                                        <input type="datetime-local"
                                               value="${invoiceDetail.expectedDateInput}"
                                               readonly>
                                    </label>

                                    <label>
                                        <span>Ngày nhận</span>
                                        <input type="datetime-local"
                                               value="${invoiceDetail.receivedDateInput}"
                                               readonly>
                                    </label>
                                </div>

                                <div class="form-grid form-grid-2">
                                    <label>
                                        <span>Ghi chú hóa đơn</span>
                                        <textarea name="note"
                                                  rows="4"
                                                  placeholder="Ghi chú chung"><c:out value="${invoiceDetail.invoiceNote}"/></textarea>
                                    </label>

                                    <label>
                                        <span>Lý do từ chối</span>
                                        <textarea id="rejectReasonInput"
                                                  name="rejectReason"
                                                  rows="4"
                                                  placeholder="Bắt buộc khi phiếu bị từ chối"><c:out value="${invoiceDetail.rejectReason}"/></textarea>
                                    </label>
                                </div>
                            </section>

                            <c:forEach var="detail" items="${invoiceDetails}" varStatus="detailStatus">
                                <section class="panel detail-panel">
                                    <input type="hidden"
                                           name="importDetailId"
                                           value="${detail.importDetailId}">

                                    <div class="panel-head">
                                        <div>
                                            <p>Nguyên liệu ${detailStatus.index + 1}</p>
                                            <h2><c:out value="${detail.ingredientName}"/></h2>
                                        </div>

                                        <span class="ingredient-unit">
                                            <c:out value="${detail.unit}"/>
                                        </span>
                                    </div>

                                    <label>
                                        <span>Nguyên liệu</span>

                                        <input type="text"
                                               value="${detail.ingredientName} - còn ${detail.stockQuantity} ${detail.unit}"
                                               readonly>
                                    </label>

                                    <div class="form-grid">
                                        <label>
                                            <span>Số lượng đặt</span>
                                            <input type="number"
                                                   min="0.01"
                                                   step="0.01"
                                                   value="${detail.orderedQuantity}"
                                                   required
                                                   readonly>
                                        </label>

                                        <label>
                                            <span>Số lượng nhận</span>
                                            <input type="number"
                                                   min="0"
                                                   step="0.01"
                                                   value="${detail.receivedQuantity}"
                                                   required
                                                   readonly>
                                        </label>

                                        <label>
                                            <span>Đơn giá</span>
                                            <input type="number"
                                                   min="0"
                                                   step="100"
                                                   value="${detail.unitPrice}"
                                                   required
                                                   readonly>
                                        </label>
                                    </div>

                                    <div class="form-grid form-grid-2">
                                        <label>
                                            <span>Chênh lệch</span>
                                            <textarea name="discrepancyNote"
                                                      rows="4"
                                                      placeholder="Ghi nhận thiếu, thừa hoặc hàng lỗi"><c:out value="${detail.discrepancyNote}"/></textarea>
                                        </label>

                                        <label>
                                            <span>Ghi chú chi tiết</span>
                                            <textarea name="detailNote"
                                                      rows="4"
                                                      placeholder="Ghi chú cho nguyên liệu"><c:out value="${detail.detailNote}"/></textarea>
                                        </label>
                                    </div>
                                </section>
                            </c:forEach>
                        </section>

                        <aside class="invoice-side">
                            <section class="summary-card">
                                <div class="summary-top">
                                    <span class="material-symbols-outlined">receipt_long</span>

                                    <div>
                                        <p>Tổng tiền thực nhận</p>
                                        <strong>
                                            <fmt:formatNumber value="${invoiceDetail.totalReceivedAmount}"
                                                              type="number"
                                                              maxFractionDigits="0"/> đ
                                        </strong>
                                    </div>
                                </div>

                                <div class="summary-list">
                                    <div>
                                        <span>Phiếu nhập</span>
                                        <strong>
                                            <c:out value="${invoiceDetail.importCode}"/>
                                        </strong>
                                    </div>

                                    <div>
                                        <span>Mã hóa đơn NCC</span>
                                        <strong>
                                            <c:out value="${invoiceDetail.supplierInvoiceCode}"/>
                                        </strong>
                                    </div>

                                    <div>
                                        <span>Ngày đặt</span>
                                        <strong>
                                            <c:out value="${invoiceDetail.orderedDateText}"/>
                                        </strong>
                                    </div>

                                    <div>
                                        <span>Số nguyên liệu</span>
                                        <strong>
                                            <c:out value="${invoiceDetail.ingredientCount}"/> loại
                                        </strong>
                                    </div>

                                    <div>
                                        <span>Tổng tiền đặt</span>
                                        <strong>
                                            <fmt:formatNumber value="${invoiceDetail.totalOrderedAmount}"
                                                              type="number"
                                                              maxFractionDigits="0"/> đ
                                        </strong>
                                    </div>

                                    <div>
                                        <span>Tổng tiền thực nhận</span>
                                        <strong>
                                            <fmt:formatNumber value="${invoiceDetail.totalReceivedAmount}"
                                                              type="number"
                                                              maxFractionDigits="0"/> đ
                                        </strong>
                                    </div>
                                </div>

                                <div class="action-row">
                                    <button type="submit" class="btn-primary">
                                        <span class="material-symbols-outlined">save</span>
                                        Lưu thay đổi
                                    </button>

                                    <a href="${pageContext.request.contextPath}/staff/history"
                                       class="btn-secondary">
                                        <span class="material-symbols-outlined">arrow_back</span>
                                        Quay lại
                                    </a>
                                </div>
                            </section>
                        </aside>
                    </form>
                </c:otherwise>
            </c:choose>
        </main>

        <script>
            function updateRejectReasonRequirement() {
                var statusSelect = document.getElementById('statusSelect');
                var rejectReason = document.getElementById('rejectReasonInput');

                if (statusSelect && rejectReason) {
                    rejectReason.required = statusSelect.value === 'Rejected';
                }
            }

            var form = document.getElementById('invoiceForm');

            if (form) {
                form.addEventListener('change', updateRejectReasonRequirement);
            }

            updateRejectReasonRequirement();
        </script>
    </body>
</html>
