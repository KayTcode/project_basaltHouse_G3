<%-- Document : CreateTableSession Author : BasaltHouse Team --%>
    <%@page contentType="text/html" pageEncoding="UTF-8" %>
        <%@page import="java.util.Collection" %>
            <%@page import="java.util.HashMap" %>
                <%@page import="model.Table" %>
                    <%@page import="model.TableSession" %>
                    <!DOCTYPE html>
                    <html lang="vi">

                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Tạo Session Bàn - BasaltHouse</title>
                        <meta name="description" content="Tạo session nhóm khách mới cho bàn tại BasaltHouse">
                        <!-- Bootstrap 5 -->
                        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
                            rel="stylesheet">
                        <!-- Google Fonts -->
                        <link
                            href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700;900&family=Inter:wght@400;500;600&display=swap"
                            rel="stylesheet">
                        <!-- Material Symbols -->
                        <link
                            href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200"
                            rel="stylesheet">
                        <link href="${pageContext.request.contextPath}/css/TableSessionCss/TableSession.css"
                            rel="stylesheet">
                    </head>

                    <body>

                        <!-- ── Toast Stack ── -->
                        <div class="toast-stack" id="toastStack"></div>



                        <!-- ── Page Header ── -->
                        <div class="page-header">
                            <div class="container">
                                <div class="d-flex justify-content-between align-items-start flex-wrap gap-3">
                                    <div>
                                        <div class="page-header-badge">
                                            <span class="material-symbols-outlined"
                                                style="font-size:14px">table_restaurant</span>
                                            Quản lý bàn
                                        </div>
                                        <h1>Tạo Session Nhóm Khách</h1>
                                        <p>Chọn bàn và nhập số lượng khách để bắt đầu phục vụ</p>
                                    </div>
                                    <div class="page-header-meta">
                                        <div class="meta-chip">
                                            <span class="material-symbols-outlined">event</span>
                                            <span id="currentDateTime">--</span>
                                        </div>
                                        <div class="meta-chip">
                                            <span class="material-symbols-outlined">person</span>
                                            <span>Nhân viên</span>
                                        </div>
                                        <button class="btn-add-table-header" data-bs-toggle="modal"
                                            data-bs-target="#addTableModal">
                                            <span class="material-symbols-outlined"
                                                style="font-size:18px">add_circle</span>
                                            Thêm Bàn
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- ── Main Content ── -->
                        <main class="ts-main">
                            <div class="container">

                                <%-- Alert from server --%>
                                    <%
                                        String errorMsg          = (String) request.getAttribute("errorMsg");
                                        String successMsg        = (String) request.getAttribute("successMsg");
                                        String addTableMsg       = (String) request.getAttribute("addTableMsg");
                                        String delTableMsg       = (String) request.getAttribute("delTableMsg");
                                        String checkoutSuccessMsg = (String) request.getAttribute("checkoutSuccessMsg");

                                        HashMap<Integer, Table>        tablesMap   = (HashMap<Integer, Table>)        request.getAttribute("tablesMap");
                                        HashMap<Integer, TableSession>  sessionsMap = (HashMap<Integer, TableSession>) request.getAttribute("sessionsMap");
                                        Collection<Table>        tables         = tablesMap   != null ? tablesMap.values()   : new java.util.ArrayList<>();
                                        Collection<TableSession> activeSessions = sessionsMap != null ? sessionsMap.values() : new java.util.ArrayList<>();
                                        String moveTableMsg       = (String) request.getAttribute("moveTableMsg");
                                    %>

                                                    <div class="row g-4">
                                                        <!-- ── Left: Table Grid ── -->
                                                        <div class="col-lg-8">
                                                            <div class="tables-panel">
                                                                <div class="panel-label">Sơ đồ bàn</div>
                                                                <div class="panel-title">Chọn Bàn</div>

                                                                <!-- Filter -->
                                                                <div class="filter-bar" id="filterBar">
                                                                    <button class="filter-btn active"
                                                                        data-filter="all">Tất cả</button>
                                                                    <button class="filter-btn"
                                                                        data-filter="available">Còn chỗ</button>
                                                                    <button class="filter-btn"
                                                                        data-filter="partial">Đang phục vụ</button>
                                                                    <button class="filter-btn" data-filter="full">Hết
                                                                        chỗ</button>
                                                                </div>

                                                                <!-- Table Cards Grid -->
                                                                <div class="tables-grid" id="tablesGrid">
                                                                    <% if (tables !=null && !tables.isEmpty()) { for
                                                                        (Table t : tables) { int cap=t.getCapacity();
                                                                        int used=t.getCurrentGuests(); int avail=cap -
                                                                        used; double pct=cap> 0 ? (double) used / cap *
                                                                        100 : 0;

                                                                        String cardClass = "table-card";
                                                                        String dotClass = "table-status-dot dot-available";
                                                                        String fillClass = "table-cap-fill";
                                                                        String seatsClass = "avail";
                                                                        String filterAttr = "available";

                                                                        if (avail <= 0) { cardClass +=" full" ;
                                                                            dotClass="table-status-dot dot-full" ;
                                                                            fillClass +=" full-fill" ;
                                                                            seatsClass="full-text" ; filterAttr="full" ;
                                                                            } else if (used> 0) {
                                                                            dotClass = "table-status-dot dot-partial";
                                                                            fillClass += " warn";
                                                                            seatsClass = "warn-text";
                                                                            filterAttr = "partial";
                                                                            }
                                                                            %>
                                                                            <div class="<%= cardClass %>"
                                                                                data-table-id="<%= t.getTableId() %>"
                                                                                data-table-code="<%= t.getTableCode() %>"
                                                                                data-area="<%= t.getArea() %>"
                                                                                data-capacity="<%= cap %>"
                                                                                data-used="<%= used %>"
                                                                                data-avail="<%= avail %>"
                                                                                data-filter="<%= filterAttr %>"
                                                                                onclick="selectTable(this)">
                                                                                <!-- Xóa bàn button -->
                                                                                <button class="btn-delete-table-card"
                                                                                    onclick="openDeleteModal(event, '<%= t.getTableId() %>', '<%= t.getTableCode() %>')"
                                                                                    title="Xóa bàn">
                                                                                    <span
                                                                                        class="material-symbols-outlined">delete</span>
                                                                                </button>
                                                                                <div class="<%= dotClass %>"></div>
                                                                                <div class="table-card-icon">
                                                                                    <span
                                                                                        class="material-symbols-outlined">table_restaurant</span>
                                                                                </div>
                                                                                <div class="table-code">
                                                                                    <%= t.getTableCode() %>
                                                                                </div>
                                                                                <div class="table-area">
                                                                                    <%= t.getArea() %>
                                                                                </div>
                                                                                <div class="table-cap-bar">
                                                                                    <div class="<%= fillClass %>"
                                                                                        style="width:<%= Math.min(pct,100) %>%">
                                                                                    </div>
                                                                                </div>
                                                                                <div class="table-seats-label">
                                                                                    <span class="<%= seatsClass %>">
                                                                                        <%= avail %>
                                                                                    </span>/<%= cap %> ghế trống
                                                                                </div>
                                                                            </div>
                                                                            <% } } else { %>
                                                                                <div
                                                                                    style="grid-column:1/-1;text-align:center;padding:48px 0;color:var(--text-muted);">
                                                                                    <span
                                                                                        class="material-symbols-outlined"
                                                                                        style="font-size:48px;display:block;margin-bottom:12px;color:#ccc;">chair</span>
                                                                                    Chưa có dữ liệu bàn. Vui lòng thêm
                                                                                    bàn trước.
                                                                                </div>
                                                                                <% } %>
                                                                </div>
                                                            </div>

                                                            <!-- ── Active Sessions List ── -->
                                                            <div class="sessions-panel">
                                                                <div class="panel-label">Đang hoạt động</div>
                                                                <div class="panel-title">Session Hiện Tại</div>

                                                                <% if (activeSessions !=null &&
                                                                    !activeSessions.isEmpty()) { %>
                                                                    <ul class="session-list">
                                                                        <% for (TableSession s : activeSessions) { 
                                                                            Table t = tablesMap != null ? tablesMap.get(s.getTableId()) : null;
                                                                            String tbCode = t != null ? t.getTableCode() : "";
                                                                            String area = t != null ? t.getArea() : "";
                                                                        %>
                                                                            <li class="session-item">
                                                                                <div class="session-avatar">
                                                                                    <span
                                                                                        class="material-symbols-outlined">group</span>
                                                                                </div>
                                                                                <div>
                                                                                    <div class="session-code">
                                                                                        <%= s.getSessionCode() %>
                                                                                    </div>
                                                                                    <div class="session-meta">
                                                                                        Check-in: <%= s.getOpenedAt() != null ? s.getOpenedAt().toString().replace("T"," ").substring(0,16) : "--" %>
                                                                                    </div>
                                                                                </div>
                                                                                <span class="session-badge">
                                                                                    <span
                                                                                        class="material-symbols-outlined"
                                                                                        style="font-size:14px;vertical-align:middle">person</span>
                                                                                    <%= s.getGuestCount() %> khách
                                                                                </span>
                                                                                <div style="display: flex; gap: 5px; align-items: center; justify-content: flex-end; margin-left: auto;">
                                                                                    <button type="button" class="btn-move-table-session"
                                                                                        onclick="openMoveTableModal(event, <%= s.getSessionId() %>, '<%= s.getSessionCode() %>')">
                                                                                        <span class="material-symbols-outlined" style="font-size: 14px; vertical-align: middle;">swap_horiz</span>Đổi bàn
                                                                                    </button>
                                                                                    <button type="button" class="btn-checkout-session"
                                                                                        onclick="openCheckoutModal(event, <%= s.getSessionId() %>, '<%= s.getSessionCode() %>')">
                                                                                        <span class="material-symbols-outlined" style="font-size: 14px; vertical-align: middle;">payments</span>Thanh toán
                                                                                    </button>
                                                                                </div>
                                                                            </li>
                                                                            <% } %>
                                                                    </ul>
                                                                    <% } else { %>
                                                                        <div
                                                                            style="text-align:center;padding:32px 0;color:var(--text-muted);">
                                                                            <span class="material-symbols-outlined"
                                                                                style="font-size:40px;display:block;margin-bottom:8px;color:#d1d5db;">sensors_off</span>
                                                                            Chưa có session nào đang hoạt động.
                                                                        </div>
                                                                        <% } %>
                                                            </div>
                                                        </div>

                                                        <!-- ── Right: Sidebar Form ── -->
                                                        <div class="col-lg-4">
                                                            <div class="sidebar-panel">
                                                                <div class="sidebar-header">
                                                                    <h3>Thông tin Session</h3>
                                                                    <p>Điền thông tin để tạo nhóm khách mới</p>
                                                                </div>
                                                                <div class="sidebar-body">

                                                                    <!-- No selection hint -->
                                                                    <div class="no-selection-hint" id="noSelectionHint">
                                                                        <span
                                                                            class="material-symbols-outlined">touch_app</span>
                                                                        Vui lòng chọn một bàn bên trái để tiếp tục
                                                                    </div>

                                                                    <!-- Selected table info card -->
                                                                    <div class="selected-table-info"
                                                                        id="selectedTableInfo">
                                                                        <div class="sti-row">
                                                                            <span class="sti-label">Bàn đã chọn</span>
                                                                            <span class="sti-value"
                                                                                id="stiCode">--</span>
                                                                        </div>
                                                                        <div class="sti-row">
                                                                            <span class="sti-label">Khu vực</span>
                                                                            <span class="sti-value"
                                                                                id="stiArea">--</span>
                                                                        </div>
                                                                        <div class="sti-row">
                                                                            <span class="sti-label">Sức chứa</span>
                                                                            <span class="sti-value"
                                                                                id="stiCapacity">--</span>
                                                                        </div>
                                                                        <div class="sti-row">
                                                                            <span class="sti-label">Khách hiện
                                                                                tại</span>
                                                                            <span class="sti-value"
                                                                                id="stiUsed">--</span>
                                                                        </div>
                                                                        <div class="sti-row">
                                                                            <span class="sti-label">Ghế còn trống</span>
                                                                            <span class="sti-value green"
                                                                                id="stiAvail">--</span>
                                                                        </div>
                                                                    </div>

                                                                    <!-- Form -->
                                                                    <form id="createSessionForm">
                                                                        <input type="hidden" name="action"
                                                                            value="create">
                                                                        <input type="hidden" name="tableId"
                                                                            id="formTableId" value="">

                                                                        <div class="mb-3" id="guestCountSection"
                                                                            style="display:none;">
                                                                            <label class="form-label-custom"
                                                                                for="guestCount">
                                                                                Số lượng khách <span
                                                                                    style="color:var(--accent-red)">*</span>
                                                                            </label>
                                                                            <div class="guest-input-wrap"
                                                                                id="guestInputWrap">
                                                                                <button type="button" class="guest-btn"
                                                                                    id="btnMinus"
                                                                                    onclick="adjustGuest(-1)" disabled>
                                                                                    <span
                                                                                        class="material-symbols-outlined"
                                                                                        style="font-size:20px">remove</span>
                                                                                </button>
                                                                                <input type="number" id="guestCount"
                                                                                    name="guestCount" value="1" min="1"
                                                                                    max="99" onchange="validateGuest()"
                                                                                    oninput="validateGuest()">
                                                                                <button type="button" class="guest-btn"
                                                                                    id="btnPlus"
                                                                                    onclick="adjustGuest(1)">
                                                                                    <span
                                                                                        class="material-symbols-outlined"
                                                                                        style="font-size:20px">add</span>
                                                                                </button>
                                                                            </div>
                                                                            <div class="capacity-hint">
                                                                                Tối đa còn <span class="cap-num"
                                                                                    id="maxHintNum">?</span> ghế trống
                                                                                cho bàn này
                                                                            </div>
                                                                            <div class="error-msg" id="guestError">
                                                                                <span class="material-symbols-outlined"
                                                                                    style="font-size:14px;vertical-align:middle">error</span>
                                                                                Số khách không được vượt quá số ghế còn
                                                                                trống!
                                                                            </div>
                                                                        </div>

                                                                        <div class="divider"></div>

                                                                        <button type="button" class="btn-create-session"
                                                                            id="btnSubmit" disabled onclick="submitCreateSession()">
                                                                            <span
                                                                                class="material-symbols-outlined">add_circle</span>
                                                                            Tạo Session
                                                                        </button>
                                                                    </form>

                                                                    <div
                                                                        style="margin-top:14px;font-size:12px;color:var(--text-muted);line-height:1.6;">
                                                                        <span class="material-symbols-outlined"
                                                                            style="font-size:14px;vertical-align:middle;color:var(--primary-color)">info</span>
                                                                        Phục vụ bằng sự tận tâm, giữ chân bằng sự hài
                                                                        lòng.
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div><!-- /row -->
                            </div>
                        </main>

                        <!-- ── Modals: Thêm, Xóa Bàn, Thanh toán, Đổi Bàn ── -->
                        <jsp:include page="AddTableModal.jsp" />
                        <jsp:include page="DeleteTableModal.jsp" />
                        <jsp:include page="CheckoutSessionModal.jsp" />
                        <jsp:include page="MoveTableModal.jsp" />

                        <!-- Bootstrap JS -->
                        <script
                            src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

                        <script>
                            /* ── State ── */
                            let selectedCard = null;
                            let selectedAvail = 0;

                            /* ── DateTime ── */
                            function updateClock() {
                                const now = new Date();
                                const opts = {
                                    weekday: 'short', year: 'numeric', month: '2-digit', day: '2-digit',
                                    hour: '2-digit', minute: '2-digit'
                                };
                                document.getElementById('currentDateTime').textContent =
                                    now.toLocaleString('vi-VN', opts);
                            }
                            updateClock();
                            setInterval(updateClock, 30000);

                            /* ── Filter ── */
                            document.getElementById('filterBar').addEventListener('click', function (e) {
                                const btn = e.target.closest('.filter-btn');
                                if (!btn) return;
                                document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
                                btn.classList.add('active');
                                const filter = btn.dataset.filter;
                                document.querySelectorAll('.table-card').forEach(card => {
                                    if (filter === 'all') {
                                        card.style.display = '';
                                    } else if (filter === 'partial') {
                                        card.style.display = (card.dataset.filter === 'partial' || card.dataset.filter === 'full') ? '' : 'none';
                                    } else {
                                        card.style.display = card.dataset.filter === filter ? '' : 'none';
                                    }
                                });
                            });

                            /* ── Select Table ── */
                            function selectTable(card) {
                                if (card.classList.contains('full')) return;

                                if (selectedCard) selectedCard.classList.remove('selected');
                                card.classList.add('selected');
                                selectedCard = card;

                                const tableId = card.dataset.tableId;
                                const code = card.dataset.tableCode;
                                const area = card.dataset.area;
                                const capacity = parseInt(card.dataset.capacity);
                                const used = parseInt(card.dataset.used);
                                const avail = parseInt(card.dataset.avail);
                                selectedAvail = avail;

                                /* update sidebar info */
                                document.getElementById('stiCode').textContent = code;
                                document.getElementById('stiArea').textContent = area;
                                document.getElementById('stiCapacity').textContent = capacity + ' ghế';
                                document.getElementById('stiUsed').textContent = used + ' khách';
                                document.getElementById('stiAvail').textContent = avail + ' ghế';
                                document.getElementById('maxHintNum').textContent = avail;
                                document.getElementById('formTableId').value = tableId;

                                document.getElementById('noSelectionHint').style.display = 'none';
                                document.getElementById('selectedTableInfo').classList.add('visible');
                                document.getElementById('guestCountSection').style.display = '';

                                /* reset guest count to max available by default */
                                const inp = document.getElementById('guestCount');
                                inp.value = avail;
                                inp.max = avail;
                                document.getElementById('btnMinus').disabled = (avail <= 1);
                                document.getElementById('btnPlus').disabled = true;
                                clearError();
                                document.getElementById('btnSubmit').disabled = false;

                            }

                            /* ── Guest Count ── */
                            function adjustGuest(delta) {
                                const inp = document.getElementById('guestCount');
                                let val = parseInt(inp.value) + delta;
                                val = Math.max(1, Math.min(val, selectedAvail));
                                inp.value = val;
                                document.getElementById('btnMinus').disabled = (val <= 1);
                                document.getElementById('btnPlus').disabled = (val >= selectedAvail);
                                validateGuest();
                            }

                            function validateGuest() {
                                const val = parseInt(document.getElementById('guestCount').value) || 0;
                                const wrap = document.getElementById('guestInputWrap');
                                const err = document.getElementById('guestError');
                                const btn = document.getElementById('btnSubmit');

                                document.getElementById('btnMinus').disabled = (val <= 1);
                                document.getElementById('btnPlus').disabled = (val >= selectedAvail);

                                if (val < 1 || val > selectedAvail) {
                                    wrap.classList.add('error');
                                    err.classList.add('visible');
                                    btn.disabled = true;
                                } else {
                                    clearError();
                                    btn.disabled = false;
                                }
                            }

                            function clearError() {
                                document.getElementById('guestInputWrap').classList.remove('error');
                                document.getElementById('guestError').classList.remove('visible');
                            }

                            /* ── Form Submit (AJAX) ── */
                            function submitCreateSession() {
                                const val = parseInt(document.getElementById('guestCount').value) || 0;
                                if (val < 1 || val > selectedAvail) {
                                    validateGuest();
                                    return;
                                }

                                const tableId   = document.getElementById('formTableId').value;
                                const tableCode = selectedCard ? selectedCard.dataset.tableCode : '';
                                const area      = selectedCard ? selectedCard.dataset.area      : '';

                                const btnSubmit = document.getElementById('btnSubmit');
                                btnSubmit.disabled = true;
                                btnSubmit.innerHTML = '<span class="material-symbols-outlined" style="animation:spin 1s linear infinite">progress_activity</span> Đang tạo...';

                                const formData = new URLSearchParams();
                                formData.append('action', 'create');
                                formData.append('tableId', tableId);
                                formData.append('guestCount', val);

                                fetch('${pageContext.request.contextPath}/TableSession', {
                                    method: 'POST',
                                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                                    body: formData.toString()
                                })
                                .then(function(response) {
                                    if (response.ok) {
                                        /* Thành công: đọc sessionId từ response nếu có,
                                           rồi gửi postMessage về màn hình POS chính */
                                        return response.text().then(function(text) {
                                            // Cố parse JSON nếu server trả về JSON có sessionId
                                            let tableSessionId = null;
                                            try {
                                                const json = JSON.parse(text);
                                                tableSessionId = json.sessionId || json.tableSessionId || null;
                                            } catch(ignored) {
                                                // Server trả về HTML/text thường — không sao
                                            }

                                            showToast('success', 'Thành công', 'Đã tạo session cho bàn ' + tableCode + '!');

                                            /* ── Callback về POS chính ── */
                                            const message = {
                                                type            : 'TABLE_SELECTED',
                                                tableId         : tableId,
                                                tableCode       : tableCode,
                                                area            : area,
                                                tableSessionId  : tableSessionId
                                            };
                                            window.parent.postMessage(message, '*');
                                            /* POS chính sẽ tự đóng modal sau khi nhận message */
                                        });
                                    } else {
                                        return response.text().then(function(text) {
                                            showToast('error', 'Lỗi', 'Không thể tạo session. Vui lòng thử lại.');
                                            console.error('Create session failed:', text);
                                            btnSubmit.disabled = false;
                                            btnSubmit.innerHTML = '<span class="material-symbols-outlined">add_circle</span> Tạo Session';
                                        });
                                    }
                                })
                                .catch(function(err) {
                                    showToast('error', 'Lỗi kết nối', 'Không thể kết nối đến server.');
                                    console.error('Network error:', err);
                                    btnSubmit.disabled = false;
                                    btnSubmit.innerHTML = '<span class="material-symbols-outlined">add_circle</span> Tạo Session';
                                });
                            }

                            /* ── Toast ── */
                            function showToast(type, title, msg) {
                                const stack = document.getElementById('toastStack');
                                const el = document.createElement('div');
                                el.className = 'ts-toast ' + type;
                                el.innerHTML =
                                    '<span class="material-symbols-outlined ts-toast-icon">' +
                                    (type === 'success' ? 'check_circle' : 'error') +
                                    '</span>' +
                                    '<div><div class="ts-toast-title">' + title + '</div>' +
                                    '<div class="ts-toast-msg">' + msg + '</div></div>';
                                stack.appendChild(el);
                                setTimeout(() => {
                                    el.classList.add('hide');
                                    setTimeout(() => el.remove(), 350);
                                }, 3500);
                            }

                            /* ── CSS spin animation ── */
                            const style = document.createElement('style');
                            style.textContent = '@keyframes spin { to { transform: rotate(360deg); } }';
                            document.head.appendChild(style);

/* ── Server-side flash messages ── */
<% if (errorMsg != null && !errorMsg.isEmpty()) { %>
                                window.addEventListener('load', () => showToast('error', 'Lỗi', '<%= errorMsg.replace("'","\\'") %>'));
<% } %>
<% if (successMsg != null && !successMsg.isEmpty()) { %>
                                window.addEventListener('load', () => showToast('success', 'Thành công', '<%= successMsg.replace("'","\\'") %>'));
<% } %>
<% if (addTableMsg != null && !addTableMsg.isEmpty()) { %>
                                window.addEventListener('load', () => showToast('success', 'Bàn đã thêm', '<%= addTableMsg.replace("'","\\'") %>'));
<% } %>
<% if (delTableMsg != null && !delTableMsg.isEmpty()) { %>
                                window.addEventListener('load', () => showToast('success', 'Bàn đã xóa', '<%= delTableMsg.replace("'","\\'") %>'));
<% } %>
<% if (checkoutSuccessMsg != null && !checkoutSuccessMsg.isEmpty()) { %>
                                window.addEventListener('load', () => showToast('success', 'Thanh toán', '<%= checkoutSuccessMsg.replace("'","\\'") %>'));
<% } %>
<% if (moveTableMsg != null && !moveTableMsg.isEmpty()) { %>
                                window.addEventListener('load', () => showToast('success', 'Đổi bàn thành công', '<%= moveTableMsg.replace("'","\\'") %>'));
<% } %>
                        </script>
                    </body>

                    </html>