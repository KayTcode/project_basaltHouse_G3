<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@page import="java.util.Collection, java.util.HashMap, model.Table, model.TableSession" %>
<%
    HashMap<Integer, Table>        tablesMap2   = (HashMap<Integer, Table>)        request.getAttribute("tablesMap");
    HashMap<Integer, TableSession> sessionsMap2 = (HashMap<Integer, TableSession>) request.getAttribute("sessionsMap");
%>
<!-- ── Modal: Đổi Bàn ── -->
<div class="modal fade" id="moveTableModal" tabindex="-1"
     aria-labelledby="moveTableModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-sm">
        <div class="modal-content del-table-modal-content">

            <!-- Header -->
            <div class="move-table-modal-header">
                <div>
                    <div class="move-modal-subtitle">Quản lý Session</div>
                    <h5 id="moveTableModalLabel" style="margin:0;font-size:18px;font-weight:700;color:#fff">
                        <span class="material-symbols-outlined"
                              style="font-size:20px;vertical-align:middle;margin-right:6px">swap_horiz</span>
                        Đổi Bàn
                    </h5>
                </div>
                <button type="button" class="add-table-close-btn" data-bs-dismiss="modal">
                    <span class="material-symbols-outlined">close</span>
                </button>
            </div>

            <!-- Body -->
            <div class="modal-body p-4">
                <div class="move-confirm-icon">
                    <span class="material-symbols-outlined">table_restaurant</span>
                </div>

                <p class="text-center fw-bold" style="font-size:15px;color:var(--text-dark);margin-bottom:4px">
                    Chuyển session sang bàn khác</p>
                <p class="text-center" style="font-size:13px;color:var(--text-muted);margin-bottom:18px">
                    Session: <strong id="moveSessionCodeDisplay"></strong>
                </p>

                <form id="moveTableForm" method="POST"
                      action="${pageContext.request.contextPath}/TableSession">
                    <input type="hidden" name="action"      value="moveTable">
                    <input type="hidden" name="sessionId"   id="moveSessionId"   value="">
                    <input type="hidden" name="sessionCode" id="moveSessionCode" value="">

                    <!-- Dropdown bàn còn trống -->
                    <div class="mb-3">
                        <label class="form-label-custom" for="newTableId">
                            Chọn bàn mới <span class="at-required">*</span>
                        </label>
                        <select name="newTableId" id="newTableId"
                                class="move-table-select"
                                onchange="validateMoveSelect()">
                            <option value=""> Chọn bàn muốn đổi </option>
                            <%
                                if (tablesMap2 != null) {
                                    for (Table tb : tablesMap2.values()) {
                                        boolean hasActiveSession = sessionsMap2 != null &&
                                            sessionsMap2.values().stream()
                                                .anyMatch(s -> s.getTableId() == tb.getTableId());
                                        if (!hasActiveSession) {
                            %>
                            <option value="<%= tb.getTableId() %>">
                                <%= tb.getTableCode() %> — <%= tb.getArea() %>
                                (sức chứa: <%= tb.getCapacity() %>)
                            </option>
                            <%      }
                                }
                            }
                            %>
                        </select>
                        <div id="moveSelectError" class="move-select-error">
                            <span class="material-symbols-outlined"
                                  style="font-size:13px;vertical-align:middle">error</span>
                            Vui lòng chọn bàn đích.
                        </div>
                    </div>

                    <button type="submit" class="btn-move-confirm" id="btnMoveConfirm" disabled>
                        <span class="material-symbols-outlined" style="font-size:18px">swap_horiz</span>
                        Xác nhận đổi bàn
                    </button>
                </form>

                <button type="button" class="btn-del-cancel" data-bs-dismiss="modal">Hủy bỏ</button>
            </div>

        </div>
    </div>
</div>

<script>
    function openMoveTableModal(event, sessionId, sessionCode) {
        event.stopPropagation();
        document.getElementById('moveSessionId').value   = sessionId;
        document.getElementById('moveSessionCode').value = sessionCode;
        document.getElementById('moveSessionCodeDisplay').textContent = sessionCode;

        // Reset
        document.getElementById('newTableId').value = '';
        document.getElementById('btnMoveConfirm').disabled = true;
        document.getElementById('moveSelectError').classList.remove('visible');

        new bootstrap.Modal(document.getElementById('moveTableModal')).show();
    }

    function validateMoveSelect() {
        const val = document.getElementById('newTableId').value;
        const btn = document.getElementById('btnMoveConfirm');
        const err = document.getElementById('moveSelectError');
        btn.disabled = !val;
        err.classList.toggle('visible', !val);
    }

    document.getElementById('moveTableForm').addEventListener('submit', function () {
        const btn = document.getElementById('btnMoveConfirm');
        btn.disabled = true;
        btn.innerHTML = '<span class="material-symbols-outlined" style="animation:spin 1s linear infinite">progress_activity</span> Đang xử lý...';
    });
</script>
