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
<%@page import="java.util.HashMap"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%
    // Lấy DAO để truy vấn OrderDetail (vì chi tiết nằm bảng khác)
    OrderDAO oDao = new OrderDAO();

    // Lấy products và sizes từ Servlet truyền sang thay vì khởi tạo DAO
    HashMap<Integer, Product> products = (HashMap<Integer, Product>) request.getAttribute("products");
    HashMap<Integer, String> sizes     = (HashMap<Integer, String>) request.getAttribute("sizes");

    String filterParam = request.getParameter("filter");
    String filterQs    = (filterParam != null && !filterParam.isEmpty()) ? "&filter=" + filterParam : "";

    // Phân trang
    int pagePending   = (Integer) request.getAttribute("pendingPage");
    int pagePreparing = (Integer) request.getAttribute("preparingPage");
    int pageReady     = (Integer) request.getAttribute("readyPage");
    
    int pendingPages   = (Integer) request.getAttribute("pendingPages");
    int preparingPages = (Integer) request.getAttribute("preparingPages");
    int readyPages     = (Integer) request.getAttribute("readyPages");
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
    <link href="${pageContext.request.contextPath}/css/BartenderCss/BartenderNew.css?v=5" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/BartenderCss/BartenderViews.css?v=7" rel="stylesheet">
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

    <!-- Stats bar — giá trị TỔNG từ Servlet, không phụ thuộc trang hiện tại -->
    <div class="stats-bar">
        <div class="stat-chip pending">
            <span class="stat-icon">&#9203;</span>
            <div>
                <div class="stat-label">Chờ xử lý</div>
                <div class="stat-value" id="stat-pending">${totalCol_0}</div>
            </div>
        </div>
        <div class="stat-chip preparing">
            <span class="stat-icon">&#9749;</span>
            <div>
                <div class="stat-label">Đang pha chế</div>
                <div class="stat-value" id="stat-preparing">${totalCol_1}</div>
            </div>
        </div>
        <div class="stat-chip ready">
            <span class="stat-icon">&#9989;</span>
            <div>
                <div class="stat-label">Sẵn sàng</div>
                <div class="stat-value" id="stat-ready">${totalCol_2}</div>
            </div>
        </div>
        <div class="stat-chip done">
            <span class="stat-icon">&#127881;</span>
            <div>
                <div class="stat-label">Hoàn thành hôm nay</div>
                <div class="stat-value" id="stat-done">${completedCount}</div>
            </div>
        </div>
    </div>

    <!-- Filter bar — khi chọn filter sẽ redirect về trang 1 với filter param -->
    <div class="filter-bar">
        <span class="filter-label">&#127979; Lọc:</span>
        <div class="filter-chips" id="filterChips">
            <button class="fchip ${empty currentFilter || currentFilter == 'all' ? 'active' : ''}"
                    data-filter="all" onclick="applyFilter('all')">Tất cả</button>
            <c:forEach items="${tablesList}" var="t">
                <button class="fchip ${currentFilter == t.tableCode ? 'active' : ''}"
                        data-filter="${t.tableCode}"
                        onclick="applyFilter('${t.tableCode}')">${t.tableCode}</button>
            </c:forEach>
            <button class="fchip ${currentFilter == 'Online' ? 'active' : ''}"
                    data-filter="Online" onclick="applyFilter('Online')">Online</button>
            <button class="fchip ${currentFilter == 'Take Away' ? 'active' : ''}"
                    data-filter="Take Away" onclick="applyFilter('Take Away')">Take Away</button>
        </div>
    </div>

    <!-- Kanban columns -->
    <div class="kanban">

        <!-- ── PENDING ── -->
        <div class="kanban-col col-bg-pending" style="display:flex;flex-direction:column;">
            <div class="col-header">
                <div class="col-title">
                    <div class="col-dot pending"></div>
                    <span style="color:#d97706">Pending</span>
                    <span class="col-count pending" id="cnt-pending">${totalCol_0}</span>
                </div>
                <span style="font-size:11px;color:#8a8a9a">Chờ bartender</span>
            </div>
            <div class="col-body" id="col-pending"></div>
            <!-- Phân trang cột Pending -->
            <c:if test="${pendingPages > 1}">
            <div class="pagination">
                <a href="?page_pending=${pendingPage - 1}&page_preparing=<%=pagePreparing%>&page_ready=<%=pageReady%><%=filterQs%>"
                   class="btn-page ${pendingPage <= 1 ? 'disabled' : ''}">&#171;</a>
                <c:forEach begin="1" end="${pendingPages}" var="pg">
                    <a href="?page_pending=${pg}&page_preparing=<%=pagePreparing%>&page_ready=<%=pageReady%><%=filterQs%>"
                       class="btn-page ${pg == pendingPage ? 'active' : ''}">${pg}</a>
                </c:forEach>
                <a href="?page_pending=${pendingPage + 1}&page_preparing=<%=pagePreparing%>&page_ready=<%=pageReady%><%=filterQs%>"
                   class="btn-page ${pendingPage >= pendingPages ? 'disabled' : ''}">&#187;</a>
            </div>
            </c:if>
        </div>

        <!-- ── PREPARING ── -->
        <div class="kanban-col col-bg-preparing" style="display:flex;flex-direction:column;">
            <div class="col-header">
                <div class="col-title">
                    <div class="col-dot preparing"></div>
                    <span style="color:#1d4ed8">Preparing</span>
                    <span class="col-count preparing" id="cnt-preparing">${totalCol_1}</span>
                </div>
                <span style="font-size:11px;color:#8a8a9a">Đang pha chế</span>
            </div>
            <div class="col-body" id="col-preparing"></div>
            <!-- Phân trang cột Preparing -->
            <c:if test="${preparingPages > 1}">
            <div class="pagination">
                <a href="?page_pending=<%=pagePending%>&page_preparing=${preparingPage - 1}&page_ready=<%=pageReady%><%=filterQs%>"
                   class="btn-page ${preparingPage <= 1 ? 'disabled' : ''}">&#171;</a>
                <c:forEach begin="1" end="${preparingPages}" var="pg">
                    <a href="?page_pending=<%=pagePending%>&page_preparing=${pg}&page_ready=<%=pageReady%><%=filterQs%>"
                       class="btn-page ${pg == preparingPage ? 'active' : ''}">${pg}</a>
                </c:forEach>
                <a href="?page_pending=<%=pagePending%>&page_preparing=${preparingPage + 1}&page_ready=<%=pageReady%><%=filterQs%>"
                   class="btn-page ${preparingPage >= preparingPages ? 'disabled' : ''}">&#187;</a>
            </div>
            </c:if>
        </div>

        <!-- ── READY ── -->
        <div class="kanban-col col-bg-ready" style="display:flex;flex-direction:column;">
            <div class="col-header">
                <div class="col-title">
                    <div class="col-dot ready"></div>
                    <span style="color:#15803d">Ready</span>
                    <span class="col-count ready" id="cnt-ready">${totalCol_2}</span>
                </div>
                <span style="font-size:11px;color:#8a8a9a">Sẵn sàng phục vụ</span>
            </div>
            <div class="col-body" id="col-ready"></div>
            <!-- Phân trang cột Ready -->
            <c:if test="${readyPages > 1}">
            <div class="pagination">
                <a href="?page_pending=<%=pagePending%>&page_preparing=<%=pagePreparing%>&page_ready=${readyPage - 1}<%=filterQs%>"
                   class="btn-page ${readyPage <= 1 ? 'disabled' : ''}">&#171;</a>
                <c:forEach begin="1" end="${readyPages}" var="pg">
                    <a href="?page_pending=<%=pagePending%>&page_preparing=<%=pagePreparing%>&page_ready=${pg}<%=filterQs%>"
                       class="btn-page ${pg == readyPage ? 'active' : ''}">${pg}</a>
                </c:forEach>
                <a href="?page_pending=<%=pagePending%>&page_preparing=<%=pagePreparing%>&page_ready=${readyPage + 1}<%=filterQs%>"
                   class="btn-page ${readyPage >= readyPages ? 'disabled' : ''}">&#187;</a>
            </div>
            </c:if>
        </div>

    </div><!-- /kanban -->
</div><!-- /content-area -->

<div class="modal-overlay" id="orderModal" onclick="closeIfOverlay(event, 'orderModal')">
    <div class="modal-box">
        <div class="modal-close" onclick="closeModal('orderModal')"><span class="material-symbols-outlined">close</span></div>
        <div class="modal-header">
            <h2 id="modalOrderId"></h2>
            <div id="modalOrderLoc" class="modal-loc"></div>
        </div>
        <div class="modal-body" id="modalOrderItems"></div>
        <div class="modal-note" id="modalOrderNote"></div>
    </div>
</div>

<div class="toast" id="toast"></div>

<script>
/* ============================================================
   DATA — inject 3 mảng phân trang riêng từ Servlet
============================================================ */
var ORDERS_PENDING = [
<c:forEach items="${pendingList}" var="o" varStatus="loop">
    <%
        Order co1 = (Order) pageContext.getAttribute("o");
        String t1 = "00:00";
        if (co1.getCreatedAt() != null)
            t1 = co1.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm"));
    %>
    {id:'ORD00${o.orderId}',status:'pending',
     type:'${o.orderType != null ? o.orderType.toLowerCase() : "offline"}',
     location:'${o.tableName != null && !o.tableName.isEmpty() ? o.tableName : (o.orderType != null && o.orderType.equalsIgnoreCase("online") ? "Online" : "Walk-in")}',
     time:'<%=t1%>',startedAt:null,note:'${o.note != null ? o.note : ""}',
     items:[<% List<OrderDetail> dl1=oDao.getOrderDetailsByOrderId(co1.getOrderId());
               for(int i=0;i<dl1.size();i++){OrderDetail d=dl1.get(i);Product p=products.get(d.getProductId());
               String pn=p!=null?p.getProductName():"Unknown";String sn=sizes.get(d.getSizeId());
               out.print("{name:'" + pn.replace("'", "\\'") + "', emoji:'&#9749;', size:'" + sn + "', topping:'', qty:" + d.getQuantity() + "}");
               if(i<dl1.size()-1)out.print(",");} %>]
    }${!loop.last ? ',' : ''}
</c:forEach>
];

var ORDERS_PREPARING = [
<c:forEach items="${preparingList}" var="o" varStatus="loop">
    <%
        Order co2 = (Order) pageContext.getAttribute("o");
        String t2 = "00:00"; String sa2 = "null";
        if (co2.getCreatedAt() != null) {
            t2  = co2.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm"));
            sa2 = String.valueOf(co2.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
    %>
    {id:'ORD00${o.orderId}',status:'preparing',
     type:'${o.orderType != null ? o.orderType.toLowerCase() : "offline"}',
     location:'${o.tableName != null && !o.tableName.isEmpty() ? o.tableName : (o.orderType != null && o.orderType.equalsIgnoreCase("online") ? "Online" : "Walk-in")}',
     time:'<%=t2%>',startedAt:<%=sa2%>,note:'${o.note != null ? o.note : ""}',
     items:[<% List<OrderDetail> dl2=oDao.getOrderDetailsByOrderId(co2.getOrderId());
               for(int i=0;i<dl2.size();i++){OrderDetail d=dl2.get(i);Product p=products.get(d.getProductId());
               String pn=p!=null?p.getProductName():"Unknown";String sn=sizes.get(d.getSizeId());
               out.print("{name:'" + pn.replace("'", "\\'") + "', emoji:'&#9749;', size:'" + sn + "', topping:'', qty:" + d.getQuantity() + "}");
               if(i<dl2.size()-1)out.print(",");} %>]
    }${!loop.last ? ',' : ''}
</c:forEach>
];

var ORDERS_READY = [
<c:forEach items="${readyList}" var="o" varStatus="loop">
    <%
        Order co3 = (Order) pageContext.getAttribute("o");
        String t3 = "00:00"; String sa3 = "null";
        if (co3.getCreatedAt() != null) {
            t3  = co3.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm"));
            sa3 = String.valueOf(co3.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
    %>
    {id:'ORD00${o.orderId}',status:'ready',
     type:'${o.orderType != null ? o.orderType.toLowerCase() : "offline"}',
     location:'${o.tableName != null && !o.tableName.isEmpty() ? o.tableName : (o.orderType != null && o.orderType.equalsIgnoreCase("online") ? "Online" : "Walk-in")}',
     time:'<%=t3%>',startedAt:<%=sa3%>,note:'${o.note != null ? o.note : ""}',
     items:[<% List<OrderDetail> dl3=oDao.getOrderDetailsByOrderId(co3.getOrderId());
               for(int i=0;i<dl3.size();i++){OrderDetail d=dl3.get(i);Product p=products.get(d.getProductId());
               String pn=p!=null?p.getProductName():"Unknown";String sn=sizes.get(d.getSizeId());
               out.print("{name:'" + pn.replace("'", "\\'") + "', emoji:'&#9749;', size:'" + sn + "', topping:'', qty:" + d.getQuantity() + "}");
               if(i<dl3.size()-1)out.print(",");} %>]
    }${!loop.last ? ',' : ''}
</c:forEach>
];

// Gộp để các hàm action (startOrder, markReady, completeOrder) dùng getOrder(id)
var ORDERS = ORDERS_PENDING.concat(ORDERS_PREPARING, ORDERS_READY);

/* ============================================================
   MODAL ACTIONS
============================================================ */
function openOrderModal(id) {
    var o = getOrder(id);
    if (!o) return;
    document.getElementById('modalOrderId').textContent = 'Đơn #' + o.id;
    var typeLabel = o.type === 'online'
        ? '<span class="card-type-badge online" style="margin-left:6px;">Online</span>'
        : '<span class="card-type-badge offline" style="margin-left:6px;">Offline</span>';
    document.getElementById('modalOrderLoc').innerHTML = '<span class="material-symbols-outlined" style="font-size:16px;vertical-align:middle;">location_on</span> ' + o.location + typeLabel;
    
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
            '<div class="modal-item-row">' +
              '<span class="item-emoji">' + g.it.emoji + '</span>' +
              '<div class="item-info">' +
                '<div class="item-name" style="font-size:14px; font-weight:600; color:#1a1a2e;">' + g.it.name + '</div>' +
                '<div class="item-badges" style="display:flex; gap:6px; margin-top:4px;">' + sz + tp + '</div>' +
              '</div>' +
              '<span class="item-qty" style="font-size:14px; font-weight:700; color:#8a8a9a;">x' + g.qty + '</span>' +
            '</div>';
    }
    document.getElementById('modalOrderItems').innerHTML = itemsHtml;
    
    var noteEl = document.getElementById('modalOrderNote');
    if (o.note) {
        noteEl.style.display = 'flex';
        noteEl.innerHTML = '<span class="material-symbols-outlined" style="font-size:18px;">edit_note</span> <div><strong>Ghi chú:</strong><br/>' + o.note + '</div>';
    } else {
        noteEl.style.display = 'none';
    }
    
    document.getElementById('orderModal').classList.add('open');
}

function closeModal(id) {
    document.getElementById(id).classList.remove('open');
}

function closeIfOverlay(e, id) {
    if (e.target.id === id || e.target.classList.contains('modal-overlay')) {
        closeModal(id);
    }
}

/* ============================================================
   RENDER
============================================================ */
function waitLabel(startedAt) {
    if (!startedAt) return null;
    var mins = Math.floor((Date.now() - startedAt) / 60000);
    var cls  = mins < 5 ? 'ok' : mins < 10 ? 'warn' : 'urgent';
    return '<span class="wait-badge ' + cls + '">&#9201; ' + mins + ' phút</span>';
}

function buildCard(o) {
    var wt = waitLabel(o.startedAt) || '';
    var totalQty = 0;
    for (var i = 0; i < o.items.length; i++) {
        totalQty += (o.items[i].qty || 1);
    }

    var footerHtml = '';
    if (o.status === 'pending') {
        footerHtml = '<div class="card-footer"><button class="btn-start" onclick="startOrder(\'' + o.id + '\')">' +
            '<span class="material-symbols-outlined" style="font-size:15px">play_arrow</span>Xác nhận</button></div>';
    } else if (o.status === 'preparing') {
        footerHtml = '<div class="card-footer"><button class="btn-ready" onclick="markReady(\'' + o.id + '\')">' +
            '<span class="material-symbols-outlined" style="font-size:15px">local_cafe</span>Xong</button></div>';
    } else {
        footerHtml = '<div class="card-footer"><button class="btn-complete" onclick="completeOrder(\'' + o.id + '\')">' +
            '<span class="material-symbols-outlined" style="font-size:15px">done_all</span>Hoàn thành</button></div>';
    }

    var div = document.createElement('div');
    div.className = 'order-card compact-card';
    div.id = 'card-' + o.id;
    div.setAttribute('data-location', o.location);
    div.setAttribute('data-status', o.status);
    div.innerHTML =
        '<div class="card-top ' + o.status + '" onclick="openOrderModal(\'' + o.id + '\')" style="cursor:pointer;" title="Nhấn để xem chi tiết">' +
            '<div>' +
                '<div class="card-id" style="font-size:15px;">#' + o.id + '</div>' +
                '<div style="font-size:13px; font-weight:600; color:#5c3317; margin-top:6px; display:flex; align-items:center; gap:4px;">' +
                    '<span class="material-symbols-outlined" style="font-size:16px;">local_mall</span>' + totalQty + ' sản phẩm' +
                '</div>' +
            '</div>' +
            '<div style="text-align:right;">' + wt + '</div>' +
        '</div>' +
        footerHtml;
    return div;
}

function renderAll() {
    var colEls = {
        pending:   document.getElementById('col-pending'),
        preparing: document.getElementById('col-preparing'),
        ready:     document.getElementById('col-ready')
    };
    var colData = { pending: ORDERS_PENDING, preparing: ORDERS_PREPARING, ready: ORDERS_READY };

    ['pending','preparing','ready'].forEach(function(s) {
        colEls[s].innerHTML = '';
        var items = colData[s];
        if (items.length === 0) {
            var emp = document.createElement('div');
            emp.className = 'empty-col';
            emp.innerHTML = '<span class="material-symbols-outlined">coffee</span>Không có đơn nào';
            colEls[s].appendChild(emp);
        } else {
            items.forEach(function(o) {
                var card = buildCard(o);
                card.classList.add('card-in');
                colEls[s].appendChild(card);
            });
        }
    });
}

/* ============================================================
   ACTIONS — sau khi thành công reload trang để đồng bộ server data
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
    }).then(function(res) {
        if (res.ok) {
            var card = document.getElementById('card-' + id);
            animateOut(card, function() {
                showToast('&#9749; Xác nhận đơn ' + id);
                setTimeout(function() { window.location.reload(); }, 500);
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
    }).then(function(res) {
        if (res.ok) {
            var card = document.getElementById('card-' + id);
            animateOut(card, function() {
                showToast('&#9989; ' + id + ' đã xong!');
                setTimeout(function() { window.location.reload(); }, 500);
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
    }).then(function(res) {
        if (res.ok) {
            var card = document.getElementById('card-' + id);
            animateOut(card, function() {
                showToast('&#127881; ' + id + ' hoàn thành!');
                setTimeout(function() { window.location.reload(); }, 500);
            });
        }
    });
}

/* ============================================================
   FILTER — server-side: redirect với filter param, reset về trang 1
============================================================ */
function applyFilter(f) {
    var params = new URLSearchParams();
    params.set('page_pending', 1);
    params.set('page_preparing', 1);
    params.set('page_ready', 1);
    if (f && f !== 'all') params.set('filter', f);
    window.location.href = window.location.pathname + '?' + params.toString();
}

/* Refresh wait badges every 30s (chỉ cập nhật badge, không reload toàn bộ) */
setInterval(function() {
    ORDERS_PREPARING.forEach(function(o) {
        if (o.startedAt) {
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
