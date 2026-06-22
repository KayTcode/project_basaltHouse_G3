<%-- BartenderPreparation.jsp - Bartender Kanban Board --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@page import="java.util.List"%>
<%@page import="model.Order"%>
<%@page import="model.OrderDetail"%>
<%@page import="model.Product"%>
<%@page import="dao.OrderDAO"%>
<%@page import="dao.ProductDAO"%>
<%@page import="dao.SizeDAO"%>
<%@page import="dao.TableDAO"%>
<%@page import="java.util.HashMap"%>
<%@page import="model.Table"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%
    OrderDAO oDao = new OrderDAO();
    List<Order> orderList = oDao.getBartenderOrders();
    request.setAttribute("orderList", orderList);
    
    ProductDAO pDao = new ProductDAO();
    HashMap<Integer, Product> products = pDao.getProduct();
    request.setAttribute("products", products);
    
    SizeDAO sDao = new SizeDAO();
    HashMap<Integer, String> sizes = sDao.getSize();
    request.setAttribute("sizes", sizes);
    
    TableDAO tDao = new TableDAO();
    List<Table> tablesList = tDao.getAllTablesWithOccupancy();
    request.setAttribute("tablesList", tablesList);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Prep Board | Basalt House Coffee</title>
    <meta name="description" content="Bảng pha chế Bartender - Basalt House Coffee">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/BartenderCss/BartenderNew.css?v=4" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/BartenderCss/BartenderViews.css?v=4" rel="stylesheet">
</head>
<body>

<!-- ── SIDEBAR ── -->
<aside class="sidebar">
    <div class="sidebar-logo">
        <div class="logo-icon">&#9749;</div>
        <div class="logo-text">Basalt<span>House Coffee</span></div>
    </div>
    <nav class="sidebar-nav">
        <a href="${pageContext.request.contextPath}/Bartender" class="nav-item active">
            <span class="nav-icon material-symbols-outlined">view_kanban</span>Prep Board
        </a>
        <a href="${pageContext.request.contextPath}/BartenderHistory" class="nav-item">
            <span class="nav-icon material-symbols-outlined">history</span>History
        </a>
        <a href="#" class="nav-item">
            <span class="nav-icon material-symbols-outlined">settings</span>Settings
        </a>
    </nav>
    <div class="sidebar-footer">
        <div class="staff-card">
            <div class="staff-avatar"><span class="material-symbols-outlined" style="font-size:18px">person</span></div>
            <div class="staff-info">
                <div class="staff-name">Bartender</div>
                <div class="staff-status"><div class="status-dot"></div>Online</div>
            </div>
        </div>
    </div>
</aside>

<!-- ── CONTENT ── -->
<div class="content-area">

    <!-- Stats bar -->
    <div class="stats-bar">
        <div class="stat-chip pending">
            <span class="stat-icon">&#9203;</span>
            <div>
                <div class="stat-label">Cho xu ly</div>
                <div class="stat-value" id="stat-pending">0</div>
            </div>
        </div>
        <div class="stat-chip preparing">
            <span class="stat-icon">&#9749;</span>
            <div>
                <div class="stat-label">Dang pha che</div>
                <div class="stat-value" id="stat-preparing">0</div>
            </div>
        </div>
        <div class="stat-chip ready">
            <span class="stat-icon">&#9989;</span>
            <div>
                <div class="stat-label">San sang</div>
                <div class="stat-value" id="stat-ready">0</div>
            </div>
        </div>
        <div class="stat-chip done">
            <span class="stat-icon">&#127881;</span>
            <div>
                <div class="stat-label">Hoan thanh hom nay</div>
                <div class="stat-value" id="stat-done"><%=oDao.getCompletedOrders().size()%></div>
            </div>
        </div>
    </div>

    <!-- Filter bar -->
    <div class="filter-bar">
        <span class="filter-label">&#127979; Loc:</span>
        <div class="filter-chips" id="filterChips">
            <button class="fchip active" data-filter="all" onclick="applyFilter('all',this)">Tat ca</button>
            <c:forEach items="${tablesList}" var="t">
                <button class="fchip" data-filter="${t.tableCode}" onclick="applyFilter('${t.tableCode}',this)">${t.tableCode}</button>
            </c:forEach>
            <button class="fchip" data-filter="Online" onclick="applyFilter('Online',this)">Online</button>
            <button class="fchip" data-filter="Take Away" onclick="applyFilter('Take Away',this)">Take Away</button>
        </div>
    </div>

    <!-- Kanban columns -->
    <div class="kanban">

        <!-- ── PENDING ── -->
        <div class="kanban-col col-bg-pending">
            <div class="col-header">
                <div class="col-title">
                    <div class="col-dot pending"></div>
                    <span style="color:#d97706">Pending</span>
                    <span class="col-count pending" id="cnt-pending">0</span>
                </div>
                <span style="font-size:11px;color:#8a8a9a">Cho bartender</span>
            </div>
            <div class="col-body" id="col-pending"></div>
        </div>

        <!-- ── PREPARING ── -->
        <div class="kanban-col col-bg-preparing">
            <div class="col-header">
                <div class="col-title">
                    <div class="col-dot preparing"></div>
                    <span style="color:#1d4ed8">Preparing</span>
                    <span class="col-count preparing" id="cnt-preparing">0</span>
                </div>
                <span style="font-size:11px;color:#8a8a9a">Dang pha che</span>
            </div>
            <div class="col-body" id="col-preparing"></div>
        </div>

        <!-- ── READY ── -->
        <div class="kanban-col col-bg-ready">
            <div class="col-header">
                <div class="col-title">
                    <div class="col-dot ready"></div>
                    <span style="color:#15803d">Ready</span>
                    <span class="col-count ready" id="cnt-ready">0</span>
                </div>
                <span style="font-size:11px;color:#8a8a9a">San sang phuc vu</span>
            </div>
            <div class="col-body" id="col-ready"></div>
        </div>

    </div><!-- /kanban -->
</div><!-- /content-area -->

<div class="toast" id="toast"></div>

<script>
/* ============================================================
   DATA
============================================================ */
var ORDERS = [
<c:forEach items="${orderList}" var="o" varStatus="loop">
    <% 
        Order currentOrder = (Order) pageContext.getAttribute("o");
        String fTime = "00:00";
        String startedAtTime = "null";
        if (currentOrder.getCreatedAt() != null) {
            fTime = currentOrder.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm"));
            startedAtTime = String.valueOf(currentOrder.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
    %>
    { 
      id:'ORD00${o.orderId}', 
      status:'${o.orderStatus == "Preparing" ? "pending" : (o.orderStatus == "In_Progress" ? "preparing" : "ready")}', 
      type:'${o.orderType != null ? o.orderType.toLowerCase() : "offline"}', 
      location:'${o.tableName != null && !o.tableName.isEmpty() ? o.tableName : (o.orderType != null && o.orderType.equalsIgnoreCase("online") ? "Online" : "Walk-in")}', 
      time:'<%=fTime%>', 
      startedAt: <%= currentOrder.getOrderStatus().equals("Preparing") ? "null" : startedAtTime %>,
      note:'${o.note != null ? o.note : ""}',
      items:[
        <% 
           Order orderObj = (Order) pageContext.getAttribute("o");
           List<OrderDetail> detailsList = oDao.getOrderDetailsByOrderId(orderObj.getOrderId());
           for (int i = 0; i < detailsList.size(); i++) {
               OrderDetail d = detailsList.get(i);
               Product p = products.get(d.getProductId());
               String pName = p != null ? p.getProductName() : "Unknown";
               String sName = sizes.get(d.getSizeId());
               out.print("{name:'" + pName.replace("'", "\\'") + "', emoji:'&#9749;', size:'" + sName + "', topping:'', qty:" + d.getQuantity() + "}");
               if (i < detailsList.size() - 1) out.print(",");
           }
        %>
      ]
    }${!loop.last ? ',' : ''}
</c:forEach>
];

var activeFilter = 'all';

/* ============================================================
   RENDER
============================================================ */
function waitLabel(startedAt) {
    if (!startedAt) return null;
    var mins = Math.floor((Date.now() - startedAt) / 60000);
    var cls  = mins < 5 ? 'ok' : mins < 10 ? 'warn' : 'urgent';
    return '<span class="wait-badge ' + cls + '">&#9201; ' + mins + ' phut</span>';
}

function buildCard(o) {
    var typeLabel = o.type === 'online' 
        ? '<span class="card-type-badge online">Online</span>' 
        : '<span class="card-type-badge offline">Offline</span>';

    var wt = waitLabel(o.startedAt) || '';

    /* Group items by name + size + topping */
    var itemsHtml = '';
    var groups = {};
    for (var i = 0; i < o.items.length; i++) {
        var it = o.items[i];
        var key = it.name + '|' + it.size + '|' + it.topping;
        if (!groups[key]) groups[key] = { it: it, qty: 0 };
        groups[key].qty += (it.qty || 1);
    }
    for (var k in groups) {
        var g = groups[k];
        var sz = g.it.size !== '-' ? '<span class="size-tag ' + g.it.size + '">Size ' + g.it.size + '</span>' : '';
        var tp = g.it.topping ? '<span class="topping-tag">' + g.it.topping + '</span>' : '';
        itemsHtml += 
            '<div class="item-row">' +
              '<span class="item-emoji">' + g.it.emoji + '</span>' +
              '<div class="item-info">' +
                '<div class="item-name">' + g.it.name + '</div>' +
                '<div class="item-badges">' + sz + tp + '</div>' +
              '</div>' +
              '<span class="item-qty">x' + g.qty + '</span>' +
            '</div>';
    }

    /* Note */
    var noteHtml = o.note 
        ? '<div class="card-note"><span class="material-symbols-outlined" style="font-size:13px;flex-shrink:0">edit_note</span>' + o.note + '</div>' 
        : '';

    /* Footer button */
    var footerHtml = '';
    if (o.status === 'pending') {
        footerHtml = '<div class="card-footer"><button class="btn-start" onclick="startOrder(\'' + o.id + '\')">' +
            '<span class="material-symbols-outlined" style="font-size:15px">play_arrow</span>Xac nhan</button></div>';
    } else if (o.status === 'preparing') {
        footerHtml = '<div class="card-footer"><button class="btn-ready" onclick="markReady(\'' + o.id + '\')">' +
            '<span class="material-symbols-outlined" style="font-size:15px">local_cafe</span>Xong</button></div>';
    } else {
        footerHtml = '<div class="card-footer"><button class="btn-complete" onclick="completeOrder(\'' + o.id + '\')">' +
            '<span class="material-symbols-outlined" style="font-size:15px">done_all</span>Hoan thanh</button></div>';
    }

    var div = document.createElement('div');
    div.className = 'order-card';
    div.id = 'card-' + o.id;
    div.setAttribute('data-location', o.location);
    div.setAttribute('data-status', o.status);
    div.innerHTML = 
        '<div class="card-top ' + o.status + '">' +
            '<div>' +
                '<div class="card-id">#' + o.id + '</div>' +
                '<div class="card-meta">' +
                    '<span class="card-loc"><span class="material-symbols-outlined" style="font-size:12px">location_on</span>' + o.location + '</span>' +
                    typeLabel +
                    '<span style="font-size:11px;color:#b0b0c0">&#8226; ' + o.time + '</span>' +
                '</div>' +
            '</div>' +
            '<div>' + wt + '</div>' +
        '</div>' +
        '<div class="card-items">' + itemsHtml + '</div>' +
        noteHtml + 
        footerHtml;
    return div;
}

function renderAll() {
    var cols = { pending: document.getElementById('col-pending'),
                 preparing: document.getElementById('col-preparing'),
                 ready: document.getElementById('col-ready') };

    cols.pending.innerHTML = '';
    cols.preparing.innerHTML = '';
    cols.ready.innerHTML = '';

    var counts = { pending:0, preparing:0, ready:0 };

    for (var i = 0; i < ORDERS.length; i++) {
        var o = ORDERS[i];
        if (activeFilter !== 'all' && o.location !== activeFilter) continue;
        var card = buildCard(o);
        card.classList.add('card-in');
        cols[o.status].appendChild(card);
        counts[o.status]++;
    }

    /* Empty states */
    ['pending','preparing','ready'].forEach(function(s) {
        if (counts[s] === 0) {
            var emp = document.createElement('div');
            emp.className = 'empty-col';
            emp.innerHTML = '<span class="material-symbols-outlined">coffee</span>Khong co don nao';
            cols[s].appendChild(emp);
        }
        document.getElementById('cnt-' + s).textContent = counts[s];
        document.getElementById('stat-' + s).textContent = counts[s];
    });
}

/* ============================================================
   ACTIONS
============================================================ */
function getOrder(id) {
    for (var i = 0; i < ORDERS.length; i++) {
        if (ORDERS[i].id === id) return ORDERS[i];
    }
    return null;
}

function animateOut(card, cb) {
    card.classList.add('card-out');
    setTimeout(cb, 280);
}

function startOrder(id) {
    var o = getOrder(id);
    if (!o) return;
    var formData = new URLSearchParams();
    formData.append("orderId", id);
    formData.append("action", "start");
    fetch('${pageContext.request.contextPath}/Bartender', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData.toString()
    }).then(res => {
        if(res.ok) {
            var card = document.getElementById('card-' + id);
            animateOut(card, function() {
                o.status = 'preparing';
                o.startedAt = Date.now();
                renderAll();
                showToast('&#9749; Xac nhan don ' + id);
            });
        }
    });
}

function markReady(id) {
    var o = getOrder(id);
    if (!o) return;
    var formData = new URLSearchParams();
    formData.append("orderId", id);
    formData.append("action", "ready");
    fetch('${pageContext.request.contextPath}/Bartender', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData.toString()
    }).then(res => {
        if(res.ok) {
            var card = document.getElementById('card-' + id);
            animateOut(card, function() {
                o.status = 'ready';
                renderAll();
                showToast('&#9989; ' + id + ' da xong!');
            });
        }
    });
}

function completeOrder(id) {
    var o = getOrder(id);
    if (!o) return;
    var formData = new URLSearchParams();
    formData.append("orderId", id);
    formData.append("action", "complete");
    fetch('${pageContext.request.contextPath}/Bartender', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData.toString()
    }).then(res => {
        if(res.ok) {
            var card = document.getElementById('card-' + id);
            animateOut(card, function() {
                ORDERS = ORDERS.filter(function(x){ return x.id !== id; });
                var done = parseInt(document.getElementById('stat-done').textContent) + 1;
                document.getElementById('stat-done').textContent = done;
                renderAll();
                showToast('&#127881; ' + id + ' hoan thanh va kho da duoc tru!');
            });
        }
    });
}

/* ============================================================
   FILTER
============================================================ */
function applyFilter(f, btn) {
    activeFilter = f;
    document.querySelectorAll('.fchip').forEach(function(c){ c.classList.remove('active'); });
    btn.classList.add('active');
    renderAll();
}

/* Refresh wait badges every 30 seconds */
setInterval(function() {
    /* Re-render cards that are in preparing state to update wait time */
    ORDERS.forEach(function(o) {
        if (o.status === 'preparing' && o.startedAt) {
            var card = document.getElementById('card-' + o.id);
            if (card) {
                var mins = Math.floor((Date.now() - o.startedAt) / 60000);
                var cls  = mins < 5 ? 'ok' : mins < 10 ? 'warn' : 'urgent';
                var badge = card.querySelector('.wait-badge');
                if (badge) {
                    badge.className = 'wait-badge ' + cls;
                    badge.innerHTML = '&#9201; ' + mins + ' phut';
                }
            }
        }
    });
}, 30000);

/* Toast */
function showToast(msg) {
    var t = document.getElementById('toast');
    t.innerHTML = msg;
    t.classList.add('show');
    setTimeout(function(){ t.classList.remove('show'); }, 2800);
}

/* ── INIT ── */
renderAll();
</script>
</body>
</html>

