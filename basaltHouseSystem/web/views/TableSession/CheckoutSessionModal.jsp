<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!-- ── Modal: Xác nhận Trả bàn Session ── -->
<div class="modal fade" id="checkoutSessionModal" tabindex="-1" aria-labelledby="checkoutSessionModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-sm">
        <div class="modal-content del-table-modal-content">
            <div class="add-table-modal-header">
                <div>
                    <div class="modal-header-subtitle">
                        Quản lý Session</div>
                    <h5 id="checkoutSessionModalLabel" class="modal-header-title">
                        <span class="material-symbols-outlined modal-header-title-icon">logout</span>Trả bàn
                    </h5>
                </div>
                <button type="button" class="add-table-close-btn" data-bs-dismiss="modal">
                    <span class="material-symbols-outlined">close</span>
                </button>
            </div>
            <div class="modal-body p-4 text-center">
                <div class="del-confirm-icon">
                    <span class="material-symbols-outlined">logout</span>
                </div>
                <p class="modal-confirm-title">
                    Trả bàn & Giải phóng?</p>
                <p class="modal-confirm-text">
                    Xác nhận hoàn tất và trả bàn cho session <strong id="checkoutSessionCodeDisplay"></strong>.<br>
                    Bàn tương ứng sẽ trở nên trống sau khi hoàn tất.
                </p>
                <form id="checkoutSessionForm" method="POST" action="${pageContext.request.contextPath}/TableSession">
                    <input type="hidden" name="action" value="checkout">
                    <input type="hidden" name="sessionId" id="checkoutSessionId" value="">
                    <input type="hidden" name="sessionCode" id="checkoutSessionCode" value="">
                    <button type="submit" class="btn-create-session mb-2" id="btnCheckoutConfirm">
                        <span class="material-symbols-outlined modal-button-icon">check_circle</span>Xác nhận trả bàn
                    </button>
                </form>
                <button type="button" class="btn-del-cancel" data-bs-dismiss="modal">Hủy bỏ</button>
            </div>
        </div>
    </div>
</div>

<script>
    function openCheckoutModal(event, sessionId, sessionCode) {
        event.stopPropagation(); // ngăn các sự kiện click khác
        document.getElementById('checkoutSessionId').value = sessionId;
        document.getElementById('checkoutSessionCode').value = sessionCode;
        document.getElementById('checkoutSessionCodeDisplay').textContent = sessionCode;
        const btn = document.getElementById('btnCheckoutConfirm');
        btn.disabled = false;
        btn.innerHTML = '<span class="material-symbols-outlined modal-button-icon">check_circle</span> Xác nhận trả bàn';
        new bootstrap.Modal(document.getElementById('checkoutSessionModal')).show();
    }
    document.getElementById('checkoutSessionForm').addEventListener('submit', function () {
        const btn = document.getElementById('btnCheckoutConfirm');
        btn.disabled = true;
        btn.innerHTML = '<span class="material-symbols-outlined btn-spin-icon">progress_activity</span> Đang xử lý...';
    });
</script>
