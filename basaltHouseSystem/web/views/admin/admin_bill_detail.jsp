<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chi tiết Hoá Đơn - Admin</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/meyer-reset/2.0/reset.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/admin_common.css">
    <style>
        .detail-card { background: #fff; padding: 24px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); margin-bottom: 24px; }
        .detail-row { display: flex; justify-content: space-between; margin-bottom: 12px; font-size: 15px; }
        .detail-label { color: #6b7280; font-weight: 500; }
        .detail-value { color: #1f2937; font-weight: 600; }
        .items-table { width: 100%; border-collapse: collapse; margin-top: 16px; }
        .items-table th, .items-table td { padding: 12px; text-align: left; border-bottom: 1px solid #e5e7eb; }
        .items-table th { background: #f9fafb; color: #4b5563; font-weight: 600; font-size: 14px; }
        .items-table td { color: #1f2937; font-size: 14px; }
        .total-section { margin-top: 24px; border-top: 2px dashed #e5e7eb; padding-top: 16px; }
        .total-row { display: flex; justify-content: space-between; margin-bottom: 8px; font-size: 15px; }
        .total-row.final { font-size: 18px; font-weight: 700; color: #16a34a; }
        .btn-back { display: inline-flex; align-items: center; gap: 8px; padding: 10px 16px; background: #f3f4f6; color: #4b5563; border-radius: 8px; text-decoration: none; font-weight: 600; margin-bottom: 20px; transition: 0.2s; }
        .btn-back:hover { background: #e5e7eb; }
    </style>
</head>
<body class="admin-dashboard-body">
    <jsp:include page="header.jsp"/>

    <div class="app-container">
        <jsp:include page="sidebar.jsp"/>

        <main class="main-content">
            <div class="content-wrapper">
                <a href="${pageContext.request.contextPath}/admin/bills" class="btn-back">
                    <i class="fas fa-arrow-left"></i> Quay lại danh sách
                </a>
                
                <div class="page-header-bar" style="margin-bottom: 24px; align-items: flex-start;">
                    <div style="display: flex; flex-direction: column; gap: 6px;">
                        <h2 class="page-title" style="margin: 0; line-height: 1.2;">Chi tiết hoá đơn #${bill.billId}</h2>
                        <p class="page-sub" style="margin: 0; color: #6b7280; font-size: 14px;">Mã: ${bill.billCode}</p>
                    </div>
                </div>

                <div class="detail-card">
                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px;">
                        <div>
                            <div class="detail-row">
                                <span class="detail-label">Khách hàng:</span>
                                <span class="detail-value">${not empty bill.customerName ? bill.customerName : 'Khách tại quán'}</span>
                            </div>
                            <div class="detail-row">
                                <span class="detail-label">Loại đơn:</span>
                                <span class="detail-value">
                                    <span style="padding:4px 8px;border-radius:6px;font-size:12px;font-weight:600;
                                        ${bill.orderType == 'Online' ? 'background:#dbeafe;color:#2563eb;' : 'background:#fef3c7;color:#d97706;'}">
                                        ${bill.orderType}
                                    </span>
                                </span>
                            </div>
                            <div class="detail-row">
                                <span class="detail-label">Bàn:</span>
                                <span class="detail-value">${tableName != null ? tableName : (bill.tableId != null ? bill.tableId : '---')}</span>
                            </div>
                        </div>
                        <div>
                            <div class="detail-row">
                                <span class="detail-label">Thu ngân:</span>
                                <span class="detail-value">${not empty bill.cashierName ? bill.cashierName : '---'}</span>
                            </div>
                            <div class="detail-row">
                                <span class="detail-label">Ngày thanh toán:</span>
                                <span class="detail-value">
                                    <fmt:parseDate value="${bill.printedAt}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both" />
                                    <fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${parsedDate}" />
                                </span>
                            </div>
                            <div class="detail-row">
                                <span class="detail-label">Phương thức thanh toán:</span>
                                <span class="detail-value">${bill.paymentMethod}</span>
                            </div>
                        </div>
                    </div>

                    <h3 style="margin-top: 32px; font-size: 16px; font-weight: 600; color: #111827;">Danh sách sản phẩm</h3>
                    <table class="items-table">
                        <thead>
                            <tr>
                                <th>Tên món</th>
                                <th>Size</th>
                                <th>Số lượng</th>
                                <th>Đơn giá</th>
                                <th style="text-align: right;">Thành tiền</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${details}">
                                <tr>
                                    <td>${item.productName}</td>
                                    <td>${item.sizeName}</td>
                                    <td>x${item.quantity}</td>
                                    <td><fmt:formatNumber value="${item.unitPrice}" type="currency" currencySymbol="đ" maxFractionDigits="0"/></td>
                                    <td style="text-align: right; font-weight: 500;"><fmt:formatNumber value="${item.unitPrice * item.quantity}" type="currency" currencySymbol="đ" maxFractionDigits="0"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                    <div class="total-section">
                        <div class="total-row">
                            <span class="detail-label">Tạm tính:</span>
                            <span class="detail-value"><fmt:formatNumber value="${bill.subTotal}" type="currency" currencySymbol="đ" maxFractionDigits="0"/></span>
                        </div>
                        <div class="total-row">
                            <span class="detail-label">Giảm giá:</span>
                            <span class="detail-value" style="color: #ef4444;">-<fmt:formatNumber value="${bill.discountAmount}" type="currency" currencySymbol="đ" maxFractionDigits="0"/></span>
                        </div>
                        <div class="total-row final">
                            <span>Tổng cộng:</span>
                            <span><fmt:formatNumber value="${bill.finalAmount}" type="currency" currencySymbol="đ" maxFractionDigits="0"/></span>
                        </div>
                    </div>
                    
                    <c:if test="${not empty bill.note}">
                        <div style="margin-top: 16px; padding: 12px; background: #f9fafb; border-radius: 8px; font-size: 14px; color: #4b5563;">
                            <strong>Ghi chú:</strong> ${bill.note}
                        </div>
                    </c:if>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
