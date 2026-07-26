<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!-- ── Modal: Xác nhận Xóa Bàn ── -->
<div class="modal fade" id="deleteTableModal" tabindex="-1" aria-labelledby="deleteTableModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-sm">
        <div class="modal-content del-table-modal-content">
            <div class="del-table-modal-header">
                <div>
                    <div class="modal-header-subtitle">
                        Quản lý bàn</div>
                    <h5 id="deleteTableModalLabel" class="modal-header-title">
                        <span class="material-symbols-outlined modal-header-title-icon">delete</span>Xóa Bàn
                    </h5>
                </div>
                <button type="button" class="add-table-close-btn" data-bs-dismiss="modal">
                    <span class="material-symbols-outlined">close</span>
                </button>
            </div>
            <div class="modal-body p-4 text-center">
                <div class="del-confirm-icon">
                    <span class="material-symbols-outlined">warning</span>
                </div>
                <p class="modal-confirm-title">
                    Xác nhận xóa bàn?</p>
                <p class="modal-confirm-text">
                    Bàn <strong id="delTableCodeDisplay"></strong> sẽ bị xóa khỏi hệ thống.<br>
                    Bàn đang có khách sẽ không thể xóa.
                </p>
                <form id="deleteTableForm" method="POST" action="${pageContext.request.contextPath}/DeleteTable">
                    <input type="hidden" name="tableId" id="delTableId" value="">
                    <input type="hidden" name="tableCode" id="delTableCode" value="">
                    <button type="submit" class="btn-del-confirm mb-2" id="btnDelConfirm">
                        <span class="material-symbols-outlined modal-button-icon">delete_forever</span>Xóa bàn
                    </button>
                </form>
                <button type="button" class="btn-del-cancel" data-bs-dismiss="modal">Hủy bỏ</button>
            </div>
        </div>
    </div>
</div>

<script>
    function openDeleteModal(event, tableId, tableCode) {
        event.stopPropagation(); // ngăn selectTable() bị kích hoạt
        document.getElementById('delTableId').value = tableId;
        document.getElementById('delTableCode').value = tableCode;
        document.getElementById('delTableCodeDisplay').textContent = tableCode;
        const btn = document.getElementById('btnDelConfirm');
        btn.disabled = false;
        btn.innerHTML = '<span class="material-symbols-outlined modal-button-icon">delete_forever</span> Xóa bàn';
        new bootstrap.Modal(document.getElementById('deleteTableModal')).show();
    }
    document.getElementById('deleteTableForm').addEventListener('submit', function () {
        const btn = document.getElementById('btnDelConfirm');
        btn.disabled = true;
        btn.innerHTML = '<span class="material-symbols-outlined btn-spin-icon">progress_activity</span> Đang xóa...';
    });
</script>
