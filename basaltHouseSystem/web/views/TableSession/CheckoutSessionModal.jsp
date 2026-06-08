<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!-- ── Modal: Xác nhận Thanh toán Session ── -->
<div class="modal fade" id="checkoutSessionModal" tabindex="-1" aria-labelledby="checkoutSessionModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-sm">
        <div class="modal-content del-table-modal-content">
            <div class="add-table-modal-header" style="background: linear-gradient(135deg, var(--primary-color), #004d22);">
                <div>
                    <div style="font-size:11px;font-weight:700;letter-spacing:.1em;text-transform:uppercase;color:rgba(255,255,255,.7);margin-bottom:4px">
                        Quản lý Session</div>
                    <h5 id="checkoutSessionModalLabel" style="margin:0;font-size:18px;font-weight:700;color:#fff">
                        <span class="material-symbols-outlined" style="font-size:20px;vertical-align:middle;margin-right:6px">payments</span>Thanh toán
                    </h5>
                </div>
                <button type="button" class="add-table-close-btn" data-bs-dismiss="modal">
                    <span class="material-symbols-outlined">close</span>
                </button>
            </div>
            <div class="modal-body p-4 text-center">
                <div class="del-confirm-icon" style="background: rgba(0, 110, 47, 0.10); color: var(--primary-color);">
                    <span class="material-symbols-outlined">receipt_long</span>
                </div>
                <p style="font-weight:700;font-size:15px;color:var(--text-dark);margin-bottom:6px">
                    Thanh toán Session?</p>
                <p style="font-size:13px;color:var(--text-muted);margin-bottom:20px">
                    Xác nhận hoàn tất và thanh toán session <strong id="checkoutSessionCodeDisplay"></strong>.<br>
                    Bàn tương ứng sẽ trở nên trống sau khi hoàn tất.
                </p>
                <form id="checkoutSessionForm" method="POST" action="${pageContext.request.contextPath}/CheckoutSession">
                    <input type="hidden" name="sessionId" id="checkoutSessionId" value="">
                    <input type="hidden" name="sessionCode" id="checkoutSessionCode" value="">
                    <button type="submit" class="btn-create-session mb-2" id="btnCheckoutConfirm">
                        <span class="material-symbols-outlined" style="font-size:18px">check_circle</span>Xác nhận thanh toán
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
        btn.innerHTML = '<span class="material-symbols-outlined" style="font-size:18px">check_circle</span> Xác nhận thanh toán';
        new bootstrap.Modal(document.getElementById('checkoutSessionModal')).show();
    }
    document.getElementById('checkoutSessionForm').addEventListener('submit', function () {
        const btn = document.getElementById('btnCheckoutConfirm');
        btn.disabled = true;
        btn.innerHTML = '<span class="material-symbols-outlined" style="animation:spin 1s linear infinite">progress_activity</span> Đang xử lý...';
    });
</script>
