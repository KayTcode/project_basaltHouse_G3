<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@page import="java.util.Collection, java.util.HashMap, java.util.List, model.Order, model.OrderDetail, model.TableSession" %>
<%
    HashMap<Integer, TableSession> sessionsMap3 = (HashMap<Integer, TableSession>) request.getAttribute("sessionsMap");
    java.util.Map<Integer, List<Order>> sessionOrdersMap = (java.util.Map<Integer, List<Order>>) request.getAttribute("sessionOrdersMap");
%>

<!-- ── Modal: Xem Danh Sách Đơn Hàng của Session ── -->
<div class="modal fade" id="viewOrdersModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content vo-modal-content">
            <!-- Header -->
            <div class="modal-header vo-modal-header">
                <div>
                    <h5 class="modal-title vo-modal-title">
                        <span class="material-symbols-outlined">receipt_long</span>
                        Danh sách đơn hàng
                    </h5>
                    <div id="voSessionCodeLabel" class="vo-session-subtitle">--</div>
                </div>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>

            <!-- Body -->
            <div class="modal-body vo-modal-body">
                <%
                    if (sessionsMap3 != null && !sessionsMap3.isEmpty()) {
                        for (TableSession s : sessionsMap3.values()) {
                            List<Order> orders = sessionOrdersMap != null ? sessionOrdersMap.get(s.getSessionId()) : null;
                            double grandTotal = 0;
                %>
                <!-- Container từng Session -->
                <div class="vo-session-container" id="sessionOrders_<%= s.getSessionId() %>" style="display:none;">
                    <%
                        if (orders != null && !orders.isEmpty()) {
                            for (Order o : orders) {
                                double finalAmt = o.getFinalAmount() != null ? o.getFinalAmount().doubleValue() : 0;
                                grandTotal += finalAmt;

                                String st = o.getOrderStatus() != null ? o.getOrderStatus() : "";
                                String statusCls = "vo-badge-pending";
                                if ("Confirmed".equals(st))      statusCls = "vo-badge-confirmed";
                                else if ("Completed".equals(st))  statusCls = "vo-badge-done";
                                else if ("Cancelled".equals(st))  statusCls = "vo-badge-cancel";
                                else if ("Processing".equals(st)) statusCls = "vo-badge-processing";

                                String paymentCls = "Paid".equals(o.getPaymentStatus()) ? "vo-pay-paid" : "vo-pay-unpaid";
                    %>
                        <div class="vo-order-card">
                            <div class="vo-order-header">
                                <div class="vo-order-id">
                                    <span class="material-symbols-outlined vertical-middle-icon">tag</span>Đơn #<%= o.getOrderId() %>
                                </div>
                                <div class="d-flex gap-2 align-items-center">
                                    <span class="vo-badge <%= statusCls %>"><%= o.getOrderStatus() %></span>
                                    <span class="vo-badge <%= paymentCls %>"><%= "Paid".equals(o.getPaymentStatus()) ? "Đã thanh toán" : "Chưa thanh toán" %></span>
                                </div>
                            </div>

                            <div class="vo-order-meta">
                                <span class="material-symbols-outlined vertical-middle-icon">schedule</span> <%= o.getFormattedCreatedAt() %>
                            </div>

                            <!-- Danh sách món -->
                            <% if (o.getOrderDetails() != null && !o.getOrderDetails().isEmpty()) { %>
                                <table class="vo-items-table">
                                    <thead>
                                        <tr>
                                            <th>Sản phẩm</th>
                                            <th>Size</th>
                                            <th class="text-center">SL</th>
                                            <th class="text-end">Đơn giá</th>
                                            <th class="text-end">Thành tiền</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for (OrderDetail item : o.getOrderDetails()) {
                                            double price = item.getUnitPrice() != null ? item.getUnitPrice().doubleValue() : 0;
                                            double subtotal = item.getSubtotal() != null ? item.getSubtotal().doubleValue() : 0;
                                        %>
                                            <tr>
                                                <td class="vo-prod-name"><%= item.getProductName() %></td>
                                                <td><span class="vo-size-chip"><%= item.getSizeName() %></span></td>
                                                <td class="vo-item-qty"><%= item.getQuantity() %></td>
                                                <td class="vo-item-price"><%= String.format("%,.0f đ", price) %></td>
                                                <td class="vo-item-subtotal"><%= String.format("%,.0f đ", subtotal) %></td>
                                            </tr>
                                        <% } %>
                                    </tbody>
                                </table>
                            <% } else { %>
                                <div class="vo-no-items">Chưa có món nào trong đơn.</div>
                            <% } %>

                            <div class="vo-order-subtotal">
                                Tổng đơn: <strong><%= String.format("%,.0f đ", finalAmt) %></strong>
                            </div>
                        </div>
                    <%      } %>
                        <span class="d-none vo-grand-total-data" data-session-id="<%= s.getSessionId() %>"><%= String.format("%,.0f đ", grandTotal) %></span>
                    <% } else { %>
                        <div class="vo-empty">
                            <span class="material-symbols-outlined">receipt_long</span>
                            <p>Chưa có đơn hàng nào trong session này.</p>
                        </div>
                        <span class="d-none vo-grand-total-data" data-session-id="<%= s.getSessionId() %>">0 đ</span>
                    <% } %>
                </div>
                <%
                        }
                    } else {
                %>
                    <div class="vo-empty">
                        <span class="material-symbols-outlined">receipt_long</span>
                        <p>Không có session nào đang hoạt động.</p>
                    </div>
                <% } %>
            </div>

            <!-- Footer -->
            <div class="modal-footer vo-modal-footer">
                <div id="voGrandTotal" class="vo-grand-total"></div>
                <button type="button" class="btn btn-secondary btn-sm rounded-3" data-bs-dismiss="modal">Đóng</button>
            </div>
        </div>
    </div>
</div>

<script>
    /**
     * Mở modal và hiển thị container HTML tương ứng đã render sẵn bằng JSP
     */
    function openViewOrdersModal(event, sessionId, sessionCode) {
        event.stopPropagation();

        document.getElementById('voSessionCodeLabel').textContent = 'Session: ' + sessionCode;

        // Ẩn tất cả các khối orders của các session khác
        document.querySelectorAll('.vo-session-container').forEach(el => el.style.display = 'none');

        // Hiển thị khối order của session được chọn
        const targetContainer = document.getElementById('sessionOrders_' + sessionId);
        if (targetContainer) {
            targetContainer.style.display = 'block';
            
            // Cập nhật tổng tiền
            const totalDataEl = targetContainer.querySelector('.vo-grand-total-data');
            const totalStr = totalDataEl ? totalDataEl.textContent : '0 đ';
            document.getElementById('voGrandTotal').innerHTML = 'Tổng cộng: <span class="vo-grand-total-amount">' + totalStr + '</span>';
        } else {
            document.getElementById('voGrandTotal').textContent = '';
        }

        new bootstrap.Modal(document.getElementById('viewOrdersModal')).show();
    }
</script>
