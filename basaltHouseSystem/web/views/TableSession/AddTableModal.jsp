<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!-- ── Modal: Thêm Bàn Mới ── -->
<div class="modal fade" id="addTableModal" tabindex="-1" aria-labelledby="addTableModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content add-table-modal-content">
            <div class="add-table-modal-header">
                <div>
                    <div style="font-size:11px;font-weight:700;letter-spacing:.1em;text-transform:uppercase;color:rgba(255,255,255,.7);margin-bottom:4px">
                        Quản lý bàn</div>
                    <h5 id="addTableModalLabel" style="margin:0;font-size:18px;font-weight:700;color:#fff">
                        <span class="material-symbols-outlined" style="font-size:20px;vertical-align:middle;margin-right:6px">add_circle</span>Thêm Bàn Mới
                    </h5>
                </div>
                <button type="button" class="add-table-close-btn" data-bs-dismiss="modal">
                    <span class="material-symbols-outlined">close</span>
                </button>
            </div>
            <div class="modal-body p-4">
                <form id="addTableForm" method="POST" action="${pageContext.request.contextPath}/AddTable" novalidate>
                    <div class="mb-3">
                        <label class="at-label" for="atCode">Mã bàn <span class="at-required">*</span></label>
                        <div class="at-input-wrap" id="wrapAtCode">
                            <span class="material-symbols-outlined at-icon">tag</span>
                            <input type="text" id="atCode" name="tableCode" class="at-input" placeholder="Ví dụ: B01, T01..." maxlength="20" autocomplete="off">
                        </div>
                        <div class="at-error" id="errAtCode"></div>
                    </div>
                    <div class="mb-3">
                        <label class="at-label" for="atArea">Khu vực <span class="at-required">*</span></label>
                        <div class="at-input-wrap" id="wrapAtArea">
                            <span class="material-symbols-outlined at-icon">location_on</span>
                            <select id="atArea" name="area" class="at-input at-select">
                                <option value="">-- Chọn khu vực --</option>
                                <option value="Trong nhà">Trong nhà</option>
                                <option value="Ngoài trời">Ngoài trời</option>
                                <option value="Tầng 1">Tầng 1</option>
                                <option value="Tầng 2">Tầng 2</option>
                            </select>
                        </div>
                        <div class="at-error" id="errAtArea"></div>
                    </div>
                    <div class="mb-4">
                        <label class="at-label" for="atCap">Sức chứa (ghế) <span class="at-required">*</span></label>
                        <div class="guest-input-wrap" id="wrapAtCap">
                            <button type="button" class="guest-btn" id="atBtnMinus" onclick="adjustAtCap(-1)" disabled>
                                <span class="material-symbols-outlined" style="font-size:20px">remove</span>
                            </button>
                            <input type="number" id="atCap" name="capacity" value="2" min="1" max="20" onchange="validateAtCap()" oninput="validateAtCap()">
                            <button type="button" class="guest-btn" id="atBtnPlus" onclick="adjustAtCap(1)">
                                <span class="material-symbols-outlined" style="font-size:20px">add</span>
                            </button>
                        </div>
                        <div class="capacity-hint">Từ 1 đến 20 ghế</div>
                        <div class="at-error" id="errAtCap"></div>
                    </div>
                    <button type="submit" class="btn-create-session w-100" id="atBtnSubmit">
                        <span class="material-symbols-outlined">add_circle</span>Thêm Bàn
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    function adjustAtCap(delta) {
        const inp = document.getElementById('atCap');
        let val = Math.max(1, Math.min(parseInt(inp.value || 1) + delta, 20));
        inp.value = val;
        document.getElementById('atBtnMinus').disabled = (val <= 1);
        document.getElementById('atBtnPlus').disabled = (val >= 20);
        validateAtCap();
    }
    function validateAtCap() {
        const val = parseInt(document.getElementById('atCap').value) || 0;
        document.getElementById('atBtnMinus').disabled = (val <= 1);
        document.getElementById('atBtnPlus').disabled = (val >= 20);
        if (val < 1 || val > 20) setAtErr('wrapAtCap', 'errAtCap', 'Sức chứa phải từ 1 đến 20.');
        else clearAtErr('wrapAtCap', 'errAtCap');
    }
    function setAtErr(wId, eId, msg) {
        document.getElementById(wId).classList.add('error');
        const e = document.getElementById(eId);
        e.textContent = msg;
        e.style.display = 'block';
    }
    function clearAtErr(wId, eId) {
        document.getElementById(wId).classList.remove('error');
        const e = document.getElementById(eId);
        e.textContent = '';
        e.style.display = 'none';
    }
    document.getElementById('addTableForm').addEventListener('submit', function (ev) {
        let ok = true;
        const code = document.getElementById('atCode').value.trim();
        const area = document.getElementById('atArea').value;
        const cap = parseInt(document.getElementById('atCap').value) || 0;
        if (!code) { setAtErr('wrapAtCode', 'errAtCode', 'Mã bàn không được để trống.'); ok = false; }
        else clearAtErr('wrapAtCode', 'errAtCode');
        if (!area) { setAtErr('wrapAtArea', 'errAtArea', 'Vui lòng chọn khu vực.'); ok = false; }
        else clearAtErr('wrapAtArea', 'errAtArea');
        if (cap < 1 || cap > 20) { setAtErr('wrapAtCap', 'errAtCap', 'Sức chứa phải từ 1 đến 20.'); ok = false; }
        else clearAtErr('wrapAtCap', 'errAtCap');
        if (!ok) { ev.preventDefault(); return; }
        const btn = document.getElementById('atBtnSubmit');
        btn.disabled = true;
        btn.innerHTML = '<span class="material-symbols-outlined" style="animation:spin 1s linear infinite">progress_activity</span> Đang thêm...';
    });
    document.getElementById('addTableModal').addEventListener('hidden.bs.modal', function () {
        document.getElementById('addTableForm').reset();
        ['wrapAtCode', 'wrapAtArea', 'wrapAtCap'].forEach(function (id) {
            document.getElementById(id).classList.remove('error');
        });
        ['errAtCode', 'errAtArea', 'errAtCap'].forEach(function (id) {
            const el = document.getElementById(id);
            el.textContent = '';
            el.style.display = 'none';
        });
        const btn = document.getElementById('atBtnSubmit');
        btn.disabled = false;
        btn.innerHTML = '<span class="material-symbols-outlined">add_circle</span> Thêm Bàn';
        document.getElementById('atBtnMinus').disabled = true;
        document.getElementById('atBtnPlus').disabled = false;
    });
</script>
