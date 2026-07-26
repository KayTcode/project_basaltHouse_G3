<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
            <section class="staff-view active">
                <div class="panel history-panel">
                    <div class="panel-header">
                        <div>
                            <h2>Lịch sử nhập nguyên liệu</h2>
                            <p>Theo dõi phiếu nhập, số lượng nhận, giá trị và tồn kho sau khi cập nhật.</p>
                        </div>
                        <form action="${pageContext.request.contextPath}/staff/history">
                            <div class="search-box history-search">
                            <span class="material-symbols-outlined">search</span>
                            <input id="historySearch" name="search"
                                   value="${fn:escapeXml(key)}"
                                   placeholder="Tìm mã phiếu, hóa đơn, nhà cung cấp hoặc nguyên liệu">
                        </div>
                        </form>
                        
                    </div>

                    <div class="history-summary">
                        <div class="history-summary-item">
                            <span class="material-symbols-outlined">receipt_long</span>
                            <div>
                                <p>Phiếu nhập</p>
                                <strong>${fn:length(listP)}</strong>
                            </div>
                        </div>
                        <div class="history-summary-item">
                            <span class="material-symbols-outlined">inventory</span>
                            <div>
                                <p>Tổng nguyên liệu</p>
                                <strong><c:out value="${totalIngredientCount}"/></strong>
                            </div>
                        </div>
                        <div class="history-summary-item">
                            <span class="material-symbols-outlined">payments</span>
                            <div>
                                <p>Theo dữ liệu</p>
                                <strong>Nhập kho</strong>
                            </div>
                        </div>
                    </div>

                    <div class="table-wrap">
                        <table class="history-table">
                            <thead>
                                <tr>
                                    <th>Ngày đặt</th>
                                    <th>Phiếu nhập</th>
                                    <th>Nguyên liệu</th>
                                    <th>Nhà cung cấp</th>
                                    <th>Số loại</th>
                                    <th>Tiền đặt</th>
                                    <th>Tiền thực nhận</th>
                                    <th>Trạng thái</th>
                                    <th>Người tạo</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody id="historyRows">
                                <c:if test="${empty listP}">
                                    <tr>
                                        <td class="empty-cell" colspan="10">Chưa có lịch sử nhập nguyên liệu.</td>
                                    </tr>
                                </c:if>

                                <c:forEach var="row" items="${listP}">
                                    <tr class="history-row">
                                        <td><strong><c:out value="${row.orderedDateText}"/></strong></td>
                                        <td>
                                            <strong><c:out value="${row.importCode}"/></strong>
                                            <span class="invoice-sub">#<c:out value="${row.importId}"/></span>
                                        </td>
                                        <td>
                                            <div class="ingredient-name history-ingredient-summary"><c:out value="${row.ingredientName}"/></div>
                                        </td>
                                        <td><c:out value="${row.supplierName}"/></td>
                                        <td>
                                            <strong><c:out value="${row.ingredientCount}"/> loại</strong>
                                        </td>
                                        <td>
                                            <strong><fmt:formatNumber value="${row.totalOrderedAmount}" type="number" maxFractionDigits="0"/> đ</strong>
                                        </td>
                                        <td>
                                            <strong><fmt:formatNumber value="${row.totalReceivedAmount}" type="number" maxFractionDigits="0"/> đ</strong>
                                        </td>
                                        <td>
                                            <span class="status-pill history-status ${row.status}">
                                                <span class="material-symbols-outlined">
                                                    <c:choose>
                                                        <c:when test="${row.status eq 'Pending'}">schedule</c:when>
                                                        <c:when test="${row.status eq 'Rejected'}">block</c:when>
                                                        <c:otherwise>check_circle</c:otherwise>
                                                    </c:choose>
                                                </span>
                                                <c:choose>
                                                    <c:when test="${row.status eq 'Pending'}">Chờ nhận</c:when>
                                                    <c:when test="${row.status eq 'Confirmed'}">Đã nhận</c:when>
                                                    <c:when test="${row.status eq 'Rejected'}">Từ chối</c:when>
                                                    <c:otherwise><c:out value="${row.status}"/></c:otherwise>
                                                </c:choose>
                                            </span>
                                        </td>
                                        <td>
                                            <div class="history-staff">
                                                <strong><c:out value="${row.staffName}"/></strong>
                                            </div>
                                        </td>
                                        <td>
                                            <a class="history-view-btn"
                                               href="${pageContext.request.contextPath}/viewimportvoice?id=${row.importId}">
                                                <span class="material-symbols-outlined">visibility</span>
                                                Xem
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <div class="table-footer">
                        <span class="result-count" id="historyResultText">0 dòng lịch sử</span>
                        <div class="pagination">
                            <button type="button" class="page-btn" id="historyPrevBtn" onclick="changeHistoryPage(-1)" aria-label="Trang trước">
                                <span class="material-symbols-outlined">chevron_left</span>
                            </button>
                            <span class="page-indicator" id="historyPageText">Trang 1 / 1</span>
                            <button type="button" class="page-btn" id="historyNextBtn" onclick="changeHistoryPage(1)" aria-label="Trang sau">
                                <span class="material-symbols-outlined">chevron_right</span>
                            </button>
                        </div>
                    </div>
                </div>
            </section>
