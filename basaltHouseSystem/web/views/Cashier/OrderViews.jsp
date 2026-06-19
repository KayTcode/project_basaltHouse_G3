
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@page import="java.util.List"%>
<%@page import="model.Order"%>
<%@page import="model.OrderDetail"%>
<%@page import="model.Product"%>
<%@page import="dao.OrderDAO"%>
<%@page import="dao.ProductDAO"%>
<%@page import="dao.SizeDAO"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%
    OrderDAO oDao = new OrderDAO();
    List<Order> orderList = oDao.getAllOrdersWithCustomerName();
    request.setAttribute("orderList", orderList);
    
    ProductDAO pDao = new ProductDAO();
    HashMap<Integer, Product> products = pDao.getProduct();
    request.setAttribute("products", products);
    
    SizeDAO sDao = new SizeDAO();
    HashMap<Integer, String> sizes = sDao.getSize();
    request.setAttribute("sizes", sizes);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Orders Management | Coffee House</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/CashierCss/CashierNew.css?v=2" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/CashierCss/OrderViews.css" rel="stylesheet">
</head>
<body>

<!-- SIDEBAR -->
<aside class="sidebar">
    <div class="sidebar-logo">
        <div class="logo-icon">&#9749;</div>
        <div class="logo-text">Basalt<span>House Coffee</span></div>
    </div>
    <nav class="sidebar-nav">
        <a href="${pageContext.request.contextPath}/DashBoard" class="nav-item">
            <span class="nav-icon material-symbols-outlined">dashboard</span>Dashboard
        </a>
        <a href="${pageContext.request.contextPath}/OrderView" class="nav-item active">
            <span class="nav-icon material-symbols-outlined">receipt_long</span>Orders
        </a>
        <a href="${pageContext.request.contextPath}/PosOrder" class="nav-item">
            <span class="nav-icon material-symbols-outlined">point_of_sale</span>POS Order
        </a>
        <a href="#" class="nav-item"><span class="nav-icon material-symbols-outlined">bar_chart</span>Reports</a>
        <a href="#" class="nav-item"><span class="nav-icon material-symbols-outlined">settings</span>Settings</a>
    </nav>
    <div class="sidebar-footer">
        <div class="staff-card">
            <div class="staff-avatar"><span class="material-symbols-outlined" style="font-size:18px">person</span></div>
            <div class="staff-info">
                <div class="staff-name">Cashier</div>
                <div class="staff-status"><div class="status-dot"></div>Online</div>
            </div>
        </div>
    </div>
</aside>

<!-- CONTENT -->
<main class="content-area">
    <div class="page-header">
        <div class="page-title">
            
        </div>
    </div>

    <div class="card">
        <div class="toolbar">
            <div class="search-box">
                <span class="material-symbols-outlined" style="font-size:17px;color:#b0b0c0">search</span>
                <input type="text" id="searchInput" placeholder="Tim kiem ma don, khach hang..." oninput="doFilter()">
            </div>
            <div class="filter-tabs">
                <button type="button" class="filter-tab active" onclick="switchTab('all',    this)">All</button>
                <button type="button" class="filter-tab"        onclick="switchTab('online', this)">Online</button>
                <button type="button" class="filter-tab"        onclick="switchTab('offline',this)">Offline</button>
            </div>
            <button type="button" class="btn-filter">
                <span class="material-symbols-outlined" style="font-size:17px">tune</span>
            </button>
        </div>

        <table class="data-table">
            <thead>
                <tr>
                    <th>Ma don</th><th>Loai don</th><th>Khach hang</th>
                    <th>Trang thai</th><th>Thoi gian</th><th>Tong tien</th><th>Thao tac</th>
                </tr>
            </thead>
            <tbody id="tblBody">
                <c:forEach items="${orderList}" var="o">
                    <%
                        Order ord = (Order) pageContext.getAttribute("o");
                        String formatTime = "";
                        if (ord.getCreatedAt() != null) {
                            formatTime = ord.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm"));
                        }
                        String oType = ord.getOrderType() != null ? ord.getOrderType().toLowerCase() : "offline";
                        String statusClass = ord.getOrderStatus() != null ? ord.getOrderStatus().toLowerCase().replace(" ", "-") : "preparing";
                    %>
                    <tr data-type="<%=oType%>" data-id="ORD00${o.orderId}" data-cust="${o.customerName}">
                        <td><strong>ORD00${o.orderId}</strong></td>
                        <td><span class="badge badge-<%=oType%>">${o.orderType}</span></td>
                        <td>${o.customerName}</td>
                        <td id="status-ORD00${o.orderId}"><span class="badge badge-<%=statusClass%>">${o.orderStatus}</span></td>
                        <td><%=formatTime%></td><td><strong>${o.finalAmount} d</strong></td>
                        <td><button type="button" class="view-btn" onclick="openModal('ORD00${o.orderId}')"><span class="material-symbols-outlined" style="font-size:15px">visibility</span></button></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <div class="pagination">
            <button type="button" class="page-btn"><span class="material-symbols-outlined" style="font-size:14px">chevron_left</span></button>
            <button type="button" class="page-btn active">1</button>
            <button type="button" class="page-btn">2</button>
            <button type="button" class="page-btn">3</button>
            <button type="button" class="page-btn"><span class="material-symbols-outlined" style="font-size:14px">chevron_right</span></button>
        </div>
    </div>
</main>

<!-- ── OFFLINE MODAL ── -->
<div class="modal-overlay" id="offModal" onclick="closeIfOverlay(event,'offModal')">
    <div class="off-modal" id="offModalBox">
        <!-- built by JS -->
    </div>
</div>

<!-- ── ONLINE MODAL ── -->
<div class="modal-overlay" id="onModal" onclick="closeIfOverlay(event,'onModal')">
    <div class="modal-box">
        <div id="onlineContent"></div>
    </div>
</div>

<div class="toast" id="toast"></div>

<script>
/* ============================================================  DATA  */
var ORDERS = {
<c:forEach items="${orderList}" var="o" varStatus="loop">
    'ORD00${o.orderId}': {
        id: '${o.orderId}',
        type: '${o.orderType != null ? o.orderType.toLowerCase() : "offline"}',
        customer: '${o.customerName}',
        table: '${o.tableName != null ? o.tableName : "Walk-in"}',
        cashier: 'Cashier',
        time: '${o.createdAt}',
        note: '${o.note != null ? o.note : "---"}',
        payMethod: '${o.paymentMethod != null ? o.paymentMethod : "Cash"}',
        payMode: 'cod',
        total: ${o.finalAmount != null ? o.finalAmount : 0},
        subtotal: ${o.totalAmount != null ? o.totalAmount : o.finalAmount},
        discount: ${o.discountAmount != null ? o.discountAmount : 0},
        status: '${o.orderStatus != null ? o.orderStatus.toLowerCase() : "preparing"}',
        items: [
            <% 
               Order orderObj = (Order) pageContext.getAttribute("o");
               List<OrderDetail> detailsList = oDao.getOrderDetailsByOrderId(orderObj.getOrderId());
               for (int i = 0; i < detailsList.size(); i++) {
                   OrderDetail d = detailsList.get(i);
                   Product p = products.get(d.getProductId());
                   String pName = p != null ? p.getProductName() : "Unknown";
                   String sName = sizes.get(d.getSizeId());
                   out.print("{name:'" + pName.replace("'", "\\'") + "', qty:" + d.getQuantity() + ", price:" + d.getUnitPrice() + ", size:'" + sName + "'}");
                   if (i < detailsList.size() - 1) out.print(",");
               }
            %>
        ]
    }${!loop.last ? ',' : ''}
</c:forEach>
};




var currentId     = null;
var offCoupon     = 0;
var offMember     = null;
var offPayMethod  = null;

/* ────────────── FILTER ────────────── */
var activeFilter = 'all';
function switchTab(t, btn) {
    activeFilter = t;
    var tabs = document.querySelectorAll('.filter-tab');
    for (var i=0;i<tabs.length;i++) tabs[i].classList.remove('active');
    btn.classList.add('active');
    doFilter();
}
function doFilter() {
    var q = document.getElementById('searchInput').value.toLowerCase();
    var rows = document.querySelectorAll('#tblBody tr');
    for (var i=0;i<rows.length;i++) {
        var r    = rows[i];
        var type = r.getAttribute('data-type');
        var id   = r.getAttribute('data-id').toLowerCase();
        var cust = r.getAttribute('data-cust').toLowerCase();
        r.style.display = ((activeFilter==='all'||type===activeFilter)&&(id.indexOf(q)>=0||cust.indexOf(q)>=0)) ? '' : 'none';
    }
}

/* ────────────── HELPERS ────────────── */
function fmt(n) { return n.toLocaleString('vi-VN') + ' d'; }
function badgeHtml(s) {
    var m = { preparing:'<span class="badge badge-preparing">Preparing</span>',
              pending_payment:'<span class="badge badge-pending-payment">Pending Payment</span>',
              ready:'<span class="badge badge-ready">Ready</span>',
              completed:'<span class="badge badge-completed">Completed</span>',
              paid:'<span class="badge badge-paid">Paid</span>' };
    return m[s] || s;
}
function closeIfOverlay(e, id) { if (e.target.id===id) closeModal(id); }
function closeModal(id) {
    document.getElementById(id).classList.remove('open');
    currentId = null;
}
function showToast(msg) {
    var t = document.getElementById('toast');
    t.innerHTML = msg;
    t.classList.add('show');
    setTimeout(function(){ t.classList.remove('show'); }, 3000);
}

/* ────────────── OPEN ────────────── */
function openModal(id) {
    currentId = id;
    var o = ORDERS[id];
    if (!o) return;
    if (o.type === 'offline') {
        offCoupon = 0; offMember = null; offPayMethod = null;
        buildOfflineModal(o);
        document.getElementById('offModal').classList.add('open');
    } else {
        buildOnlineModal(o);
        document.getElementById('onModal').classList.add('open');
    }
}

/* ============================================================
   OFFLINE MODAL
============================================================ */
function statusLabelAndClass(s) {
    var map = { preparing:['Preparing','preparing'], pending_payment:['Pending Payment','pending'],
                ready:['Ready','ready'], completed:['Completed','completed'], paid:['Paid','paid'] };
    return map[s] || [s, 'pending'];
}

function buildOfflineModal(o) {
    var sc = statusLabelAndClass(o.status);

    /* Item rows */
    var itemsHtml = '';
    var SIZE_CLR = { S:'#3b82f6', M:'#16a34a', L:'#f97316' };
    var SIZE_BG  = { S:'#dbeafe', M:'#dcfce7', L:'#ffedd5' };
    for (var i = 0; i < o.items.length; i++) {
        var it = o.items[i];
        var bg = it.bg || '#c49a6c';
        var em = it.emoji || '&#9749;';
        var sz = it.size || 'M';
        var sizeBadge = '<span style="display:inline-block;font-size:10px;font-weight:800;padding:1px 6px;border-radius:10px;margin-left:5px;background:' + SIZE_BG[sz] + ';color:' + SIZE_CLR[sz] + ';letter-spacing:0.3px;vertical-align:middle;">Size ' + sz + '</span>';
        itemsHtml +=
            '<div class="off-item">' +
              '<div class="off-item-img" style="background:' + bg + ';">' + em + '</div>' +
              '<div class="off-item-name">' + it.name + sizeBadge + '</div>' +
              '<div class="off-item-qty">x' + it.qty + '</div>' +
              '<div class="off-item-price">' + fmt(it.price * it.qty) + '</div>' +
            '</div>';
    }

    var discSection = '';

    /* Price summary */
    var priceSection =
        '<hr class="off-divider">' +
        '<div class="off-price">' +
            '<div class="off-price-row"><span>Tam tinh</span><span>' + fmt(o.subtotal) + '</span></div>' +
            (o.discount > 0 ? '<div class="off-price-row disc-green"><span>&#127991; Giam gia</span><span>&minus;' + fmt(o.discount) + '</span></div>' : '') +
            '<div class="off-price-row grand"><span>Tong cong</span><span>' + fmt(o.total) + '</span></div>' +
        '</div>';

    /* Payment (read-only from POS) */
    var PM_ICON = { 'Cash':'&#128181;', 'QR Code':'&#128241;', 'The':'&#128179;' };
    var pm = o.payMethod || 'Cash';
    var paySection =
        '<hr class="off-divider">' +
        '<div style="display:flex;align-items:center;justify-content:space-between;padding:10px 20px;">' +
            '<span style="font-size:13px;color:#8a8a9a;">Thanh toan</span>' +
            '<span style="font-size:13px;font-weight:700;background:#f4f1ec;padding:5px 14px;border-radius:20px;border:1px solid rgba(0,0,0,0.08);">' +
                (PM_ICON[pm] || '&#128181;') + ' ' + pm +
            '</span>' +
        '</div>';

    /* Note - chi hien neu co */
    var noteSection = '';
    if (o.note && o.note.trim() && o.note !== 'Khong co') {
        noteSection =
            '<div style="padding:0 20px 10px;font-size:13px;">' +
                '<span style="color:#8a8a9a;margin-right:6px;">Ghi chu:</span>' + o.note +
            '</div>';
    }

    /* Status timeline */
    var STEPS       = ['preparing','in_progress','ready','completed'];
    var STEP_LABELS = { preparing:'Cho xac nhan', in_progress:'Pha che', ready:'San sang', completed:'Hoan thanh' };
    var curIdx = STEPS.indexOf(o.status);
    if (o.status === 'paid') curIdx = 3;

    var tlHtml = '<div class="off-timeline">';
    for (var s = 0; s < STEPS.length; s++) {
        var done    = s <= curIdx;
        var dotCls  = done ? 'tl-dot done' : 'tl-dot';
        var lblCls  = done ? 'tl-label done' : 'tl-label';
        var lineCls = s < curIdx ? 'tl-line done' : 'tl-line';
        tlHtml += '<div class="tl-step"><div class="' + dotCls + '">' + (done ? '&#10003;' : (s+1)) + '</div>' +
                  '<div class="' + lblCls + '">' + STEP_LABELS[STEPS[s]] + '</div></div>' +
                  (s < STEPS.length-1 ? '<div class="' + lineCls + '"></div>' : '');
    }
    tlHtml += '</div>';

    var statusSection =
        '<hr class="off-divider">' +
        '<div onclick="toggleOrderStatus()" style="display:flex;align-items:center;justify-content:space-between;padding:10px 20px;cursor:pointer;user-select:none;">' +
            '<span style="font-size:13px;font-weight:700;">&#128203; Trang thai don hang</span>' +
            '<span class="material-symbols-outlined" id="statusToggleIcon" style="font-size:18px;color:#8a8a9a;transition:transform 0.2s">expand_more</span>' +
        '</div>' +
        '<div id="statusTimelineBox" style="display:none;padding:0 20px 14px;">' + tlHtml + '</div>';

    /* Assemble - no footer buttons */
    document.getElementById('offModalBox').innerHTML =
        '<div class="off-hd">' +
            '<div class="off-hd-title">Chi tiet don hang</div>' +
            '<button type="button" class="off-close" onclick="closeModal(\'offModal\')">&#x2715;</button>' +
        '</div>' +
        '<div style="display:flex;align-items:center;justify-content:space-between;padding:8px 20px 0;">' +
            '<span class="off-status-badge ' + sc[1] + '"><div class="off-status-dot"></div>' + sc[0] + '</span>' +
            '<span class="off-datetime">' + o.time + '</span>' +
        '</div>' +
        '<div style="padding:6px 20px 0;">' +
            '<span class="off-type-tag">Offline Order</span>' +
            '<div class="off-id" style="margin-bottom:6px;">#' + o.id + '</div>' +
        '</div>' +
        '<div style="display:grid;grid-template-columns:1fr 1fr;padding:0 20px;">' +
            '<div class="off-info-row" style="border-right:1px solid rgba(0,0,0,0.05);">' +
                '<div class="off-info-left"><span class="material-symbols-outlined">person</span>Khach hang</div>' +
                '<div class="off-info-val">' + o.customer + '</div>' +
            '</div>' +
            '<div class="off-info-row" style="padding-left:12px;">' +
                '<div class="off-info-left"><span class="material-symbols-outlined">table_restaurant</span>Ban</div>' +
                '<div class="off-info-val">' + o.table + '</div>' +
            '</div>' +
            '<div class="off-info-row" style="grid-column:1/-1;">' +
                '<div class="off-info-left"><span class="material-symbols-outlined">badge</span>Cashier</div>' +
                '<div class="off-info-val">' + (o.cashier || 'Nguyen Van A') + '</div>' +
            '</div>' +
        '</div>' +
        '<hr class="off-divider">' +
        '<div class="off-sec">Danh sach mon</div>' +
        '<div class="off-items">' + itemsHtml + '</div>' +
        discSection + priceSection + paySection + noteSection + statusSection +
        '<div style="height:10px;"></div>';
}

function toggleOrderStatus() {
    var box  = document.getElementById('statusTimelineBox');
    var icon = document.getElementById('statusToggleIcon');
    if (box.style.display === 'none') { box.style.display='block'; icon.style.transform='rotate(180deg)'; }
    else { box.style.display='none'; icon.style.transform='rotate(0deg)'; }
}




/* ============================================================
   ONLINE MODAL
============================================================ */
function buildOnlineModal(o) {
    var sc = statusLabelAndClass(o.status);
    var confirmed = o.status !== 'pending';

    /* ── Item rows ── */
    var itemsHtml = '';
    var SIZE_CLR = { S:'#3b82f6', M:'#16a34a', L:'#f97316' };
    var SIZE_BG  = { S:'#dbeafe', M:'#dcfce7', L:'#ffedd5' };
    for (var i = 0; i < o.items.length; i++) {
        var it = o.items[i];
        var sz = it.size || 'M';
        var sizeBadge = '<span style="display:inline-block;font-size:10px;font-weight:800;padding:1px 6px;border-radius:10px;margin-left:5px;background:' + SIZE_BG[sz] + ';color:' + SIZE_CLR[sz] + ';letter-spacing:0.3px;vertical-align:middle;">Size ' + sz + '</span>';
        itemsHtml +=
            '<div class="off-item">' +
              '<div class="off-item-img" style="background:#3b82f6;font-size:14px;">&#127760;</div>' +
              '<div class="off-item-name">' + it.name + sizeBadge + '</div>' +
              '<div class="off-item-qty">x' + it.qty + '</div>' +
              '<div class="off-item-price">' + fmt(it.price * it.qty) + '</div>' +
            '</div>';
    }

    /* ── Tinh tong co giam gia ── */
    var couponAmt = o.coupon ? o.coupon.amount : 0;
    var memAmt    = o.member ? Math.round(o.total * o.member.pct / 100) : 0;
    var grand     = Math.max(0, o.total - couponAmt - memAmt);

    /* ── Discount section (chi hien neu co tu he thong) ── */
    var discSection = '';
    if (o.coupon || o.member) {
        discSection = '<hr class="off-divider">';
        if (o.coupon) {
            discSection +=
                '<div style="display:flex;align-items:center;justify-content:space-between;padding:6px 20px 2px;">' +
                    '<span style="font-size:12.5px;color:#8a8a9a;">&#127991; Ma giam gia</span>' +
                    '<span style="font-size:12.5px;font-weight:700;color:#16a34a;background:#dcfce7;padding:3px 10px;border-radius:12px;">' +
                        o.coupon.code + ' &minus;' + fmt(o.coupon.amount) +
                    '</span>' +
                '</div>';
        }
        if (o.member) {
            var TIER_BG   = { none:'#f4f4f4', silver:'#f0f0f0', gold:'#fef9e7', diamond:'#e8f4fd' };
            var TIER_CLR  = { none:'#888',    silver:'#555',    gold:'#92700a', diamond:'#1565a0' };
            var TIER_ICON = { none:'&#128100;',silver:'&#129752;',gold:'&#127941;',diamond:'&#128142;' };
            var t = o.member.tier || 'none';
            discSection +=
                '<div style="padding:4px 20px 6px;">' +
                    '<div style="display:flex;align-items:center;gap:8px;background:' + TIER_BG[t] + ';border-radius:10px;padding:8px 12px;">' +
                        '<span style="font-size:18px;">' + TIER_ICON[t] + '</span>' +
                        '<div style="flex:1;">' +
                            '<div style="font-size:12.5px;font-weight:700;">' + o.member.name + '</div>' +
                            '<div style="font-size:11px;color:#888;text-transform:uppercase;">' + t + ' &bull; &#11088; ' + o.member.points + ' diem</div>' +
                        '</div>' +
                        '<span style="font-size:12.5px;font-weight:700;color:' + TIER_CLR[t] + ';">&minus;' + o.member.pct + '% (' + fmt(memAmt) + ')</span>' +
                    '</div>' +
                '</div>';
        }
    }

    /* ── Price summary ── */
    var priceSection =
        '<hr class="off-divider">' +
        '<div class="off-price">' +
            '<div class="off-price-row"><span>Tam tinh</span><span>' + fmt(o.total) + '</span></div>' +
            (couponAmt > 0 ? '<div class="off-price-row disc-green"><span>&#127991; Giam (ma)</span><span>&minus;' + fmt(couponAmt) + '</span></div>' : '') +
            (memAmt    > 0 ? '<div class="off-price-row disc-green"><span>&#128100; Giam (member)</span><span>&minus;' + fmt(memAmt) + '</span></div>' : '') +
            '<div class="off-price-row grand"><span>Tong cong</span><span>' + fmt(grand) + '</span></div>' +
        '</div>';

    /* ── Payment mode ── */
    var pm = o.payMode === 'cod' ? '&#128181; COD (Thanh toan khi nhan)' : '&#9989; Da thanh toan online';
    var pmBg = o.payMode === 'cod' ? '#fff7ed' : '#f0fdf4';
    var pmClr = o.payMode === 'cod' ? '#c2410c' : '#15803d';
    var paySection =
        '<hr class="off-divider">' +
        '<div style="display:flex;align-items:center;justify-content:space-between;padding:10px 20px;">' +
            '<span style="font-size:13px;color:#8a8a9a;">Thanh toan</span>' +
            '<span style="font-size:12.5px;font-weight:700;background:' + pmBg + ';color:' + pmClr + ';padding:4px 12px;border-radius:20px;">' + pm + '</span>' +
        '</div>';

    /* ── Note ── */
    var noteSection = '';
    if (o.note && o.note.trim()) {
        noteSection =
            '<div style="padding:0 20px 8px;font-size:13px;">' +
                '<span style="color:#8a8a9a;margin-right:6px;">Ghi chu:</span>' + o.note +
            '</div>';
    }

    /* ── Status timeline (chi hien sau khi cashier xac nhan) ── */
    var statusSection = '';
    if (confirmed) {
        var STEPS       = ['preparing','in_progress','ready','completed'];
        var STEP_LABELS = { preparing:'Cho xac nhan', in_progress:'Pha che', ready:'San sang', completed:'Hoan thanh' };
        var curIdx = STEPS.indexOf(o.status);
        if (o.status === 'paid') curIdx = 3;

        var tlHtml = '<div class="off-timeline">';
        for (var s = 0; s < STEPS.length; s++) {
            var done    = s <= curIdx;
            var dotCls  = done ? 'tl-dot done' : 'tl-dot';
            var lblCls  = done ? 'tl-label done' : 'tl-label';
            var lineCls = s < curIdx ? 'tl-line done' : 'tl-line';
            tlHtml += '<div class="tl-step"><div class="' + dotCls + '">' + (done ? '&#10003;' : (s+1)) + '</div>' +
                      '<div class="' + lblCls + '">' + STEP_LABELS[STEPS[s]] + '</div></div>' +
                      (s < STEPS.length-1 ? '<div class="' + lineCls + '"></div>' : '');
        }
        tlHtml += '</div>';

        statusSection =
            '<hr class="off-divider">' +
            '<div onclick="toggleOnlineStatus()" style="display:flex;align-items:center;justify-content:space-between;padding:10px 20px;cursor:pointer;user-select:none;">' +
                '<span style="font-size:13px;font-weight:700;">&#128203; Trang thai don hang</span>' +
                '<span class="material-symbols-outlined" id="onStatusIcon" style="font-size:18px;color:#8a8a9a;transition:transform 0.2s">expand_more</span>' +
            '</div>' +
            '<div id="onStatusBox" style="display:none;padding:0 20px 14px;">' + tlHtml + '</div>';
    }

    /* ── Footer ── */
    var footerHtml = '';
    if (!confirmed) {
        /* Chua xac nhan → chi hien nut Xac nhan don dat */
        footerHtml =
            '<div style="padding:8px 20px 16px;">' +
                '<div style="background:#fefce8;border:1px solid #fde68a;border-radius:10px;padding:10px 14px;margin-bottom:10px;display:flex;align-items:center;gap:8px;">' +
                    '<span style="font-size:16px;">&#9888;&#65039;</span>' +
                    '<span style="font-size:12.5px;color:#92400e;">Don chua duoc xac nhan. Bam xac nhan de gui Bartender pha che.</span>' +
                '</div>' +
                '<button type="button" onclick="confirmOnlineOrder()" style="width:100%;padding:13px;background:linear-gradient(135deg,#16a34a,#15803d);color:#fff;border:none;border-radius:12px;font-size:14px;font-weight:700;cursor:pointer;display:flex;align-items:center;justify-content:center;gap:8px;">' +
                    '<span class="material-symbols-outlined" style="font-size:18px;">check_circle</span>Xac nhan don dat' +
                '</button>' +
            '</div>';
    } else if (o.status === 'completed' || o.status === 'paid') {
        /* Hoan thanh */
        footerHtml =
            '<div style="padding:8px 20px 16px;">' +
                '<div style="background:#f0fdf4;border:1px solid #86efac;border-radius:10px;padding:10px 14px;margin-bottom:10px;display:flex;align-items:center;gap:8px;">' +
                    '<span style="font-size:16px;">&#10003;</span>' +
                    '<span style="font-size:13px;font-weight:700;color:#15803d;">Don da hoan thanh</span>' +
                '</div>' +
                '<button type="button" onclick="createDelivery()" style="width:100%;padding:13px;background:linear-gradient(135deg,#3b82f6,#1d4ed8);color:#fff;border:none;border-radius:12px;font-size:14px;font-weight:700;cursor:pointer;display:flex;align-items:center;justify-content:center;gap:8px;">' +
                    '<span class="material-symbols-outlined" style="font-size:18px;">local_shipping</span>Tao don giao hang' +
                '</button>' +
            '</div>';
    } else {
        /* Da gui Bartender, dang xu ly */
        footerHtml =
            '<div style="padding:8px 20px 16px;">' +
                '<button type="button" onclick="createDelivery()" style="width:100%;padding:13px;background:linear-gradient(135deg,#3b82f6,#1d4ed8);color:#fff;border:none;border-radius:12px;font-size:14px;font-weight:700;cursor:pointer;display:flex;align-items:center;justify-content:center;gap:8px;">' +
                    '<span class="material-symbols-outlined" style="font-size:18px;">local_shipping</span>Tao don giao hang' +
                '</button>' +
            '</div>';
    }

    /* ── Assemble (compact 2-col like offline) ── */
    document.getElementById('onlineContent').innerHTML =
        '<div class="off-hd">' +
            '<div class="off-hd-title">Chi tiet don hang</div>' +
            '<button type="button" class="off-close" onclick="closeModal(\'onModal\')">&#x2715;</button>' +
        '</div>' +
        /* Status + datetime */
        '<div style="display:flex;align-items:center;justify-content:space-between;padding:8px 20px 0;">' +
            '<span class="off-status-badge ' + sc[1] + '"><div class="off-status-dot"></div>' + sc[0] + '</span>' +
            '<span class="off-datetime">' + o.time + '</span>' +
        '</div>' +
        /* Badge + ID */
        '<div style="padding:4px 20px 0;">' +
            '<span style="background:#eff6ff;color:#1d4ed8;border:1px solid #bfdbfe;font-size:11px;font-weight:700;padding:2px 9px;border-radius:20px;display:inline-block;margin-bottom:3px;">&#127760; Online</span>' +
            '<div class="off-id" style="margin-bottom:2px;">#' + o.id + '</div>' +
        '</div>' +
        /* 2-col customer info */
        '<div style="display:grid;grid-template-columns:1fr 1fr;padding:0 20px;">' +
            '<div class="off-info-row" style="border-right:1px solid rgba(0,0,0,0.05);">' +
                '<div class="off-info-left"><span class="material-symbols-outlined">person</span>Khach hang</div>' +
                '<div class="off-info-val">' + o.customer + '</div>' +
            '</div>' +
            '<div class="off-info-row" style="padding-left:12px;">' +
                '<div class="off-info-left"><span class="material-symbols-outlined">phone</span>Dien thoai</div>' +
                '<div class="off-info-val">' + (o.phone || '---') + '</div>' +
            '</div>' +
            '<div class="off-info-row" style="grid-column:1/-1;">' +
                '<div class="off-info-left"><span class="material-symbols-outlined">location_on</span>Dia chi</div>' +
                '<div class="off-info-val" style="max-width:260px;text-align:right;">' + (o.address || '---') + '</div>' +
            '</div>' +
        '</div>' +
        '<hr class="off-divider">' +
        '<div class="off-sec">Danh sach mon</div>' +
        '<div class="off-items">' + itemsHtml + '</div>' +
        discSection + priceSection + paySection + noteSection + statusSection +
        '<div id="onMsg" class="status-updated-msg"></div>' +
        footerHtml;
}

function toggleOnlineStatus() {
    var box  = document.getElementById('onStatusBox');
    var icon = document.getElementById('onStatusIcon');
    if (!box) return;
    if (box.style.display === 'none') { box.style.display='block'; icon.style.transform='rotate(180deg)'; }
    else { box.style.display='none'; icon.style.transform='rotate(0deg)'; }
}

function confirmOnlineOrder() {
    var o = ORDERS[currentId];
    if (!o) return;
    
    var formData = new URLSearchParams();
    formData.append("orderId", o.id);
    formData.append("action", "confirm");

    fetch('${pageContext.request.contextPath}/Bartender', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData.toString()
    }).then(function(res) {
        if(res.ok) {
            o.sentToBartender = true;
            o.status = 'preparing';
            document.getElementById('status-' + currentId).innerHTML = badgeHtml('preparing');
            showToast('&#127861; Don ' + currentId + ' da gui Bartender!');
            buildOnlineModal(o);
        } else {
            alert('Loi khi xac nhan don tren server!');
        }
    }).catch(function(err) {
        console.error(err);
        alert('Loi ket noi!');
    });
}

function createDelivery() {
    showToast('&#128666; Dang tao don giao hang cho ' + currentId + '...');
}

function sendBartender() { confirmOnlineOrder(); }

function markReady() {
    var o = ORDERS[currentId];
    if (!o) return;
    o.status = 'ready';
    document.getElementById('status-' + currentId).innerHTML = badgeHtml('ready');
    showToast('\u2713 Don ' + currentId + ' san sang!');
    buildOnlineModal(o);
}
</script>
</body>
</html>

