<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn"  uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
        <title>Coffeely – Shipper Dashboard</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/shipper/shipper.css" />
    </head>
    <body>

        <%-- ══ HEADER ══════════════════════════════════════════════ --%>
        <header class="s-header">
            <div class="s-header__left">
                <c:choose>
                    <c:when test="${not empty currentShipper.avatarUrl}">
                        <img src="${fn:escapeXml(currentShipper.avatarUrl)}" alt="avatar" class="s-avatar">
                    </c:when>
                    <c:otherwise>
                        <div class="s-avatar-ph"><i class="bi bi-person-fill"></i></div>
                        </c:otherwise>
                    </c:choose>
                <div>
                    <div class="s-header__name"><c:out value="${currentShipper.fullName}"/></div>
                    <div class="s-header__role"><i class="bi bi-bicycle me-1"></i>Tài xế giao hàng</div>
                </div>
            </div>
            <a href="${pageContext.request.contextPath}/logout" class="btn-logout">
                <i class="bi bi-box-arrow-right me-1"></i>Thoát
            </a>
        </header>

        <%-- ══ FLASH MESSAGE ════════════════════════════════════════ --%>
        <c:if test="${not empty sessionScope.flashMessage}">
            <div class="flash-wrap">
                <c:choose>
                    <c:when test="${sessionScope.flashSuccess == true}">
                        <div class="alert alert-success flash-alert">
                            <i class="bi bi-check-circle-fill"></i>
                            <c:out value="${sessionScope.flashMessage}"/>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-danger flash-alert">
                            <i class="bi bi-exclamation-triangle-fill"></i>
                            <c:out value="${sessionScope.flashMessage}"/>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
            <c:remove var="flashMessage" scope="session"/>
            <c:remove var="flashSuccess"  scope="session"/>
        </c:if>

        <%-- ══ STATS BAR ════════════════════════════════════════════ --%>
        <div class="stats-bar">
            <div class="stat-card">
                <div class="stat-card__val">${fn:length(pendingOrders)}</div>
                <div class="stat-card__lbl">Đơn chờ</div>
            </div>
            <div class="stat-card">
                <div class="stat-card__val">${not empty currentOrder ? 1 : 0}</div>
                <div class="stat-card__lbl">Đang giao</div>
            </div>
            <div class="stat-card">
                <div class="stat-card__val">${currentShipper.isAvailable ? '🟢' : '🔴'}</div>
                <div class="stat-card__lbl">${currentShipper.isAvailable ? 'Sẵn sàng' : 'Bận'}</div>
            </div>
        </div>

        <%-- ══ TAB BAR ══════════════════════════════════════════════ --%>
        <div class="tab-bar">
            <button class="tab-btn active" id="btn-tab1" onclick="switchTab(1)">
                <i class="bi bi-inbox-fill"></i>
                Đơn mới
                <c:if test="${not empty pendingOrders}">
                    <span class="badge-cnt">${fn:length(pendingOrders)}</span>
                </c:if>
            </button>
            <button class="tab-btn" id="btn-tab2" onclick="switchTab(2)">
                <i class="bi bi-geo-alt-fill"></i>
                Đang giao
                <c:if test="${not empty currentOrder}">
                    <span class="badge-on">1</span>
                </c:if>
            </button>
        </div>

        <%-- ══════════════════════════════════════════════════════════
             TAB 1 — ĐƠN MỚI (Preparing)
             ══════════════════════════════════════════════════════════ --%>
        <div class="tab-content active" id="tab1">
            <p class="section-title">
                <i class="bi bi-clock-history me-1"></i>Đơn hàng chờ giao
            </p>

            <c:choose>
                <c:when test="${empty pendingOrders}">
                    <div class="empty-state">
                        <i class="bi bi-inbox"></i>
                        <span>Không có đơn hàng nào đang chờ giao.</span>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="o" items="${pendingOrders}">
                        <div class="order-card">
                            <div class="order-card__head">
                                <div>
                                    <div class="order-id">#<c:out value="${o.orderId}"/></div>
                                    <div class="order-time">${orderTimeMap[o.orderId]}</div>
                                </div>
                                <span class="status-badge status-preparing">Chờ giao</span>
                            </div>

                            <div class="order-row">
                                <i class="bi bi-person"></i>
                                <span><c:out value="${o.customerName}"/></span>
                            </div>

                            <div class="order-row">
                                <i class="bi bi-credit-card"></i>
                                <span><c:out value="${o.paymentMethod}"/> –
                                    <c:out value="${o.paymentStatus}"/></span>
                            </div>

                            <div class="order-amount">
                                <fmt:formatNumber value="${o.finalAmount}" type="number"
                                                  groupingUsed="true"/> đ
                            </div>

                            <%-- Nút nhận đơn — chỉ hiện nếu shipper chưa có đơn đang giao --%>
                            <c:if test="${empty currentOrder}">
                                <form action="${pageContext.request.contextPath}/shipper/accept-order"
                                      method="post" style="margin:0">
                                    <input type="hidden" name="orderId" value="${o.orderId}">
                                    <button type="submit" class="btn-accept">
                                        <i class="bi bi-check2-circle"></i>Nhận đơn này
                                    </button>
                                </form>
                            </c:if>
                            <c:if test="${not empty currentOrder}">
                                <div style="font-size:12px;color:var(--text-light);margin-top:10px;
                                     text-align:center;padding:8px;background:var(--cream);
                                     border-radius:8px;">
                                    <i class="bi bi-info-circle me-1"></i>
                                    Hoàn thành đơn hiện tại trước khi nhận đơn mới.
                                </div>
                            </c:if>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>

        <%-- ══════════════════════════════════════════════════════════
             TAB 2 — ĐANG GIAO (Delivering)
             ══════════════════════════════════════════════════════════ --%>
        <div class="tab-content" id="tab2">

            <c:choose>
                <c:when test="${empty currentOrder}">
                    <div class="empty-state">
                        <i class="bi bi-geo-alt"></i>
                        <span>Bạn chưa nhận đơn nào.<br>Sang tab <strong>Đơn mới</strong> để nhận đơn.</span>
                    </div>
                </c:when>

                <c:otherwise>
                    <%-- Thông tin đơn đang giao --%>
                    <div class="current-panel">
                        <div class="current-panel__label">
                            <div class="pulse-dot"></div>
                            Đơn đang giao
                        </div>

                        <div class="order-card__head" style="margin-bottom:10px">
                            <div>
                                <div class="order-id">#<c:out value="${currentOrder.orderId}"/></div>
                                <div class="order-time">${currentOrderTime}</div>
                            </div>
                            <span class="status-badge status-delivering">Đang giao</span>
                        </div>

                        <div class="order-row">
                            <i class="bi bi-person"></i>
                            <span><c:out value="${currentOrder.customerName}"/></span>
                        </div>

                        <c:if test="${not empty deliveryAddress}">
                            <div class="order-row">
                                <i class="bi bi-geo-alt-fill"></i>
                                <span>
                                    <c:out value="${deliveryAddress.recipientName}"/>
                                    (<c:out value="${deliveryAddress.recipientPhone}"/>) –
                                    <c:out value="${deliveryAddress.addressDetail}"/>
                                </span>
                            </div>
                            <c:if test="${not empty deliveryAddress.note}">
                                <div class="order-row">
                                    <i class="bi bi-sticky"></i>
                                    <span><c:out value="${deliveryAddress.note}"/></span>
                                </div>
                            </c:if>
                        </c:if>

                        <div class="order-row">
                            <i class="bi bi-credit-card"></i>
                            <span><c:out value="${currentOrder.paymentMethod}"/></span>
                        </div>

                        <div class="order-amount">
                            <fmt:formatNumber value="${currentOrder.finalAmount}"
                                              type="number" groupingUsed="true"/> đ
                        </div>
                    </div>

                    <%-- ── FORM CẬP NHẬT TRẠNG THÁI + UPLOAD ẢNH ── --%>
                    <div class="upload-section">
                        <h6><i class="bi bi-camera me-1"></i>Cập nhật kết quả giao hàng</h6>

                        <form action="${pageContext.request.contextPath}/shipper/update-delivery"
                              method="post"
                              enctype="multipart/form-data"
                              id="deliveryForm">

                            <input type="hidden" name="orderId" value="${currentOrder.orderId}">

                            <%-- Toggle thành công / thất bại --%>
                            <div class="result-toggle">
                                <input type="radio" name="isSuccess" id="radio-success"
                                       value="true" checked onchange="toggleDeliveryMode(true)">
                                <label for="radio-success" id="lbl-success"
                                       class="lbl-success">
                                    <i class="bi bi-check-circle me-1"></i>Thành công
                                </label>

                                <input type="radio" name="isSuccess" id="radio-fail"
                                       value="false" onchange="toggleDeliveryMode(false)">
                                <label for="radio-fail" id="lbl-fail" class="lbl-fail">
                                    <i class="bi bi-x-circle me-1"></i>Thất bại
                                </label>
                            </div>


                            <%-- Phần thành công: upload ảnh + ghi chú --%>
                            <div id="section-success">
                                <label class="form-label-sm">📷 Ảnh xác nhận giao hàng *</label>
                                <div class="img-preview-wrap" onclick="document.getElementById('proofImageInput').click()">
                                    <div class="img-preview-placeholder" id="imgPlaceholder">
                                        <i class="bi bi-camera-fill"></i>
                                        <span>Chụp ảnh hoặc chọn từ thư viện</span>
                                    </div>
                                    <img id="previewImg" alt="Ảnh xác nhận giao hàng" style="display:none">
                                </div>
                                <input type="file" name="proofImage" id="proofImageInput"
                                       accept="image/*" capture="environment"
                                       onchange="previewImage(this)">
                                <label class="form-label-sm" for="noteInput">Ghi chú (tùy chọn)</label>
                                <textarea id="noteInput" name="note" rows="2"
                                          class="form-control-sm-custom"
                                          placeholder="Để tại cửa, đã gặp khách..."></textarea>
                            </div>

                            <%-- Phần thất bại: lý do --%>
                            <div id="section-fail" style="display:none">
                                <label class="form-label-sm" for="failReasonInput">
                                    Lý do thất bại *
                                </label>
                                <textarea id="failReasonInput" name="failReason" rows="3"
                                          class="form-control-sm-custom"
                                          placeholder="Khách không nghe máy, sai địa chỉ..."></textarea>
                            </div>

                            <%-- Nút submit (đổi màu theo mode) --%>
                            <button type="submit" class="btn-submit-delivery btn-submit-success"
                                    id="submitBtn" onclick="return validateForm()">
                                <i class="bi bi-check2-circle" id="submitIcon"></i>
                                <span id="submitText">Xác nhận giao thành công</span>
                            </button>

                        </form>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script>
                                        // ── Tab switching ──────────────────────────────────────
                                        function switchTab(n) {
                                            document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));
                                            document.querySelectorAll('.tab-btn').forEach(el => el.classList.remove('active'));
                                            document.getElementById('tab' + n).classList.add('active');
                                            document.getElementById('btn-tab' + n).classList.add('active');
                                        }

                                        // ── Toggle success / fail mode ─────────────────────────
                                        function toggleDeliveryMode(isSuccess) {
                                            const secSuccess = document.getElementById('section-success');
                                            const secFail = document.getElementById('section-fail');
                                            const btn = document.getElementById('submitBtn');
                                            const icon = document.getElementById('submitIcon');
                                            const text = document.getElementById('submitText');
                                            const lblSuccess = document.getElementById('lbl-success');
                                            const lblFail = document.getElementById('lbl-fail');

                                            if (isSuccess) {
                                                secSuccess.style.display = '';
                                                secFail.style.display = 'none';
                                                btn.className = 'btn-submit-delivery btn-submit-success';
                                                icon.className = 'bi bi-check2-circle';
                                                text.textContent = 'Xác nhận giao thành công';
                                                lblSuccess.style.background = 'var(--green)';
                                                lblSuccess.style.color = '#fff';
                                                lblFail.style.background = '';
                                                lblFail.style.color = 'var(--red)';
                                            } else {
                                                secSuccess.style.display = 'none';
                                                secFail.style.display = '';
                                                btn.className = 'btn-submit-delivery btn-submit-fail';
                                                icon.className = 'bi bi-x-circle';
                                                text.textContent = 'Xác nhận giao thất bại';
                                                lblFail.style.background = 'var(--red)';
                                                lblFail.style.color = '#fff';
                                                lblSuccess.style.background = '';
                                                lblSuccess.style.color = 'var(--green)';
                                            }
                                        }

                                        // Khởi tạo màu ban đầu
                                        toggleDeliveryMode(true);

                                        // ── Image preview ──────────────────────────────────────
                                        function previewImage(input) {
                                            if (!input.files || !input.files[0])
                                                return;
                                            const file = input.files[0];
                                            if (file.size > 5 * 1024 * 1024) {
                                                alert('Ảnh không được vượt quá 5 MB.');
                                                input.value = '';
                                                return;
                                            }
                                            const reader = new FileReader();
                                            reader.onload = e => {
                                                const img = document.getElementById('previewImg');
                                                img.src = e.target.result;
                                                img.style.display = 'block';
                                                document.getElementById('imgPlaceholder').style.display = 'none';
                                            };
                                            reader.readAsDataURL(file);
                                        }

                                        // ── Validate trước submit ──────────────────────────────
                                        function validateForm() {
                                            const isSuccess = document.getElementById('radio-success').checked;
                                            if (isSuccess) {
                                                const file = document.getElementById('proofImageInput').files;
                                                if (!file || file.length === 0) {
                                                    alert('Vui lòng chụp hoặc chọn ảnh xác nhận giao hàng.');
                                                    return false;
                                                }
                                            } else {
                                                const reason = document.getElementById('failReasonInput').value.trim();
                                                if (!reason) {
                                                    alert('Vui lòng nhập lý do giao hàng thất bại.');
                                                    return false;
                                                }
                                            }

                                            // Disable nút SAU khi form đã bắt đầu submit, tránh Chrome hủy submit
                                            setTimeout(() => {
                                                const btn = document.getElementById('submitBtn');
                                                btn.disabled = true;
                                                btn.querySelector('span').textContent = 'Đang xử lý...';
                                            }, 0);

                                            return true;
                                        }

                                        // ── Auto-switch sang tab 2 nếu đang có đơn ────────────
            <c:if test="${not empty currentOrder}">
                                        switchTab(2);
            </c:if>
        </script>
    </body>
</html>
