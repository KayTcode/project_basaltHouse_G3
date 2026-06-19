<%-- CashierCreateOrder.jsp - Tao don hang offline + Thanh toan --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@ page import="services.StockService" %>
<%@ page import="dao.IngredientDAO" %>
<%@ page import="model.Product" %>
<%@ page import="model.Ingredient" %>
<%@ page import="java.util.HashMap" %>
<%@page import="java.util.Map"%>
<%@ page import="dao.ProductDAO" %>
<%@ page import="dao.CategoryDAO" %>
<%@ page import="model.Category" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%
    ProductDAO pDao = new ProductDAO();
    List<Product> productList = pDao.getAllProductsForPOS();
    request.setAttribute("productList", productList);
    
    CategoryDAO cDao = new CategoryDAO();
    List<Category> categoryList = cDao.getAllCategories();
    request.setAttribute("categoryList", categoryList);

    // Pre-calculate max stock for initial display
    StockService ssInit = new StockService();
    HashMap<Product, HashMap<String, Integer>> stockMapInit = ssInit.calculateProduct();
    HashMap<Integer, Integer> maxStockMap = new HashMap<>();
    for(Map.Entry<Product, HashMap<String, Integer>> entry : stockMapInit.entrySet()) {
        int max = 0;
        for(Integer val : entry.getValue().values()) {
            if(val > max) max = val;
        }
        maxStockMap.put(entry.getKey().getProductId(), max);
    }
    request.setAttribute("maxStockMap", maxStockMap);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tao Don Hang | Coffee House</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/CashierCss/CashierNew.css?v=2" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/CashierCss/POSOrders.css?v=4" rel="stylesheet">
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
        <a href="${pageContext.request.contextPath}/OrderView" class="nav-item">
            <span class="nav-icon material-symbols-outlined">receipt_long</span>Orders
        </a>
        <a href="${pageContext.request.contextPath}/PosOrder" class="nav-item active">
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
    <div class="co-header" style="position: relative; display:flex;align-items:center;justify-content:space-between; padding-right: 140px; box-sizing: border-box;">
        <div>
            <h1>Create Offline Order</h1>
            <p>Tao don hang tai quay</p>
        </div>
        <button type="button" onclick="openInventoryModal()" style="position: absolute; right: 28px; top: 18px; display:flex;align-items:center;gap:6px;background:#2c1a0e;color:#fff;border:none;padding:8px 16px;border-radius:8px;font-size:12.5px;font-weight:600;cursor:pointer;transition:all 0.2s;" onmouseover="this.style.background='#5c3317'" onmouseout="this.style.background='#2c1a0e'">
            <span class="material-symbols-outlined" style="font-size:17px">inventory_2</span>
            Xem kho
            <span id="invBadgeCount" style="background:#dc2626;color:#fff;font-size:10px;padding:1px 6px;border-radius:10px;display:none;"></span>
        </button>
    </div>

    <!-- Inventory Warning Bar -->
    <div class="inv-warning-bar" id="invWarningBar" onclick="openInventoryModal()">
        <span class="inv-warn-icon">&#9888;&#65039;</span>
        <span class="inv-warn-text" id="invWarnText">Co nguyen lieu sap het!</span>
        <span class="inv-warn-count" id="invWarnCount">0</span>
        <button type="button" class="inv-warn-btn">Xem chi tiet &rarr;</button>
    </div>

    <div class="co-layout">

        <!-- LEFT: Categories -->
        <div class="co-cats">
            <div class="co-cats-title">Menu</div>
            <div class="co-search">
                <span class="material-symbols-outlined" style="font-size:15px;color:#b0b0c0">search</span>
                <input type="text" id="menuSearch" placeholder="Tim mon..." oninput="filterMenu()">
            </div>
            <button type="button" class="cat-btn active" onclick="setCategory('all',   this)">All</button>
            <c:forEach items="${categoryList}" var="c">
                <button type="button" class="cat-btn" onclick="setCategory('cat-${c.categoryId}', this)">${c.categoryName}</button>
            </c:forEach>
        </div>

        <!-- MIDDLE: Menu items -->
        <div class="co-menu">
            <div class="menu-grid" id="menuGrid">
                <c:forEach items="${productList}" var="p">
                    <div class="menu-card" data-cat="cat-${p.categoryId}" data-name="${p.productName}" data-stock="${maxStockMap[p.productId] != null ? maxStockMap[p.productId] : 0}">
                        <div class="menu-card-img" style="background:linear-gradient(135deg,#d4a96a,#8b5e3c); display: flex; align-items: center; justify-content: center; overflow: hidden;">
                            <c:if test="${not empty p.imageUrl}">
                                <img src="${pageContext.request.contextPath}${p.imageUrl}" alt="${p.productName}" style="width:100%;height:100%;object-fit:cover;">
                            </c:if>
                            <c:if test="${empty p.imageUrl}">
                                <span style="font-size: 24px;">&#9749;</span>
                            </c:if>
                        </div>
                        <div class="menu-card-name">${p.productName}</div>
                        <div class="menu-card-price"><fmt:formatNumber value="${p.price}" pattern="#,###"/> d</div>
                        <button type="button" class="btn-add" onclick="addItem('${p.productName}', ${p.price})">+</button>
                    </div>
                </c:forEach>
            </div>
        </div>

        <!-- RIGHT: Order panel -->
        <div class="co-order">
            <div class="co-order-hd">
                <span class="co-order-hd-title">Don hang (<span id="itemCount">0</span>)</span>
                <button type="button" class="btn-choose-table" id="tableBtn" onclick="chooseTable()">Chon ban</button>
            </div>

            <div class="co-items" id="orderPanel">
                <div class="empty-cart" id="emptyMsg">
                    <div class="ec-icon">&#128722;</div>
                    <div class="ec-text">Chua co mon nao</div>
                </div>
            </div>

            <!-- Membership check -->
            <div class="co-membership">
                <div class="mem-header">
                    <div class="mem-label">&#128100; Kiem tra thanh vien</div>
                </div>
                <div class="mem-lookup-row">
                    <input type="text" class="mem-phone-input" id="memberPhone"
                           placeholder="Nhap SDT thanh vien..."
                           onkeydown="if(event.key==='Enter') lookupMember()">
                    <button type="button" class="btn-lookup" onclick="lookupMember()">
                        <span class="material-symbols-outlined" style="font-size:14px">search</span>Tim
                    </button>
                </div>
                <div class="mem-result" id="memResult"></div>
            </div>

            <!-- Discount code -->
            <div class="co-discount">
                <div class="disc-label">Ma giam gia</div>
                <div class="disc-row">
                    <input type="text" class="disc-input" id="discountCode" placeholder="Nhap ma giam gia...">
                    <button type="button" class="btn-apply-disc" onclick="applyDiscount()">Ap dung</button>
                </div>
                <div class="disc-msg" id="discMsg"></div>
            </div>

            <!-- Note -->
            <div class="co-note">
                <textarea id="orderNote" placeholder="Them ghi chu..."></textarea>
            </div>

            <!-- Totals -->
            <div class="co-totals">
                <div class="co-tot-row"><span>Tam tinh</span><span id="subtotal">0 d</span></div>
                <div class="co-tot-row"><span>Giam gia</span><span id="discountAmt" style="color:#16a34a;">0 d</span></div>
                <div class="co-tot-row grand"><span>Tong tien</span><span id="grandTotal">0 d</span></div>
            </div>

            <div class="co-actions">
                <button type="button" class="btn-save" onclick="saveOrder()">Luu tam</button>
                <button type="button" class="btn-pay"  onclick="openPayModal()">Thanh toan</button>
            </div>
        </div>
    </div>
</main>

<!-- ============================================================
     PAYMENT MODAL
============================================================ -->
<div class="modal-overlay" id="payModal" onclick="closeIfOverlay(event,'payModal')">
    <div class="modal-box">
        <button type="button" class="modal-close-btn" onclick="closeModal('payModal')">&#x2715;</button>
        <div class="modal-title">Chon phuong thuc thanh toan</div>
        <div class="modal-sub" id="payModalSub">Don hang tai Coffee House</div>

        <!-- Order summary -->
        <div class="pay-summary" id="paySummary"></div>

        <!-- Payment methods -->
        <div class="pay-method-grid">
            <div class="pay-method-card" id="pm-cash" onclick="selectPayMethod('cash')">
                <span class="pay-method-icon">&#128181;</span>
                <div class="pay-method-label">Tien mat</div>
            </div>
            <div class="pay-method-card" id="pm-qr" onclick="selectPayMethod('qr')">
                <span class="pay-method-icon">&#128241;</span>
                <div class="pay-method-label">QR Code</div>
            </div>
        </div>

        <!-- Cash details -->
        <div class="cash-section" id="cashSection">
            <div class="cash-due">Khach can tra: <span id="cashDue"></span></div>
            <div style="font-size:12px;font-weight:600;color:#8a8a9a;margin-bottom:6px;">So tien nhanh:</div>
            <div class="cash-quick" id="cashQuick"></div>
            <div class="cash-input-row">
                <input type="number" class="cash-input" id="cashInput" placeholder="Nhap so tien khach dua..." oninput="calcChange()">
                <span class="cash-unit">d</span>
            </div>
            <div class="cash-change" id="cashChange">Tien thoi: <strong id="changeAmt"></strong></div>
        </div>

        <!-- QR details -->
        <div class="qr-section" id="qrSection">
            <div class="qr-amount-label">So tien can chuyen: <strong id="qrAmt"></strong></div>
            <img class="qr-img" id="qrImg" src="" alt="QR Code">
            <div class="qr-bank">Ngan hang: <strong>VietcomBank</strong><br>So TK: <strong>0123 4567 8901</strong><br>Ten: <strong>COFFEE HOUSE</strong></div>
        </div>

        <button type="button" class="btn-confirm-pay" id="btnConfirmPay" onclick="confirmPayment()" disabled>
            Xac nhan thanh toan
        </button>
    </div>
</div>

<!-- ============================================================
     BILL MODAL (receipt)
============================================================ -->
<div class="modal-overlay" id="billModal" onclick="closeIfOverlay(event,'billModal')">
    <div class="bill-modal-box">
        <button type="button" class="modal-close-btn" onclick="closeModal('billModal')">&#x2715;</button>

        <div id="printArea">
            <div class="receipt" id="receiptContent"></div>
        </div>

        <div class="bill-actions">
            <button type="button" class="btn-print" onclick="submitPOSOrder(false)"><span class="material-symbols-outlined" style="font-size:18px">check_circle</span> Xac nhan</button>
            <button type="button" class="btn-new-order" onclick="submitPOSOrder(true)"><span class="material-symbols-outlined" style="font-size:18px">add_circle</span> Don moi</button>
        </div>
    </div>
</div>

<!-- ============================================================
     SIZE PICKER MODAL
============================================================ -->
<div class="size-modal-overlay" id="sizeModal" onclick="closeSizeIfOverlay(event)">
    <div class="size-modal-box">
        <div class="size-modal-title" id="sizeModalTitle">Chon size</div>
        <div class="size-modal-sub">Chon kich co coc ban muon</div>
        <div class="size-grid">
            <div class="size-card" id="sz-S" onclick="pickSize('S')">
                <div class="size-icon" style="font-size:22px">&#9749;</div>
                <div class="size-label">S</div>
                <div class="size-price" id="sz-S-price"></div>
                <div class="size-stock" id="sz-S-stock" style="font-size: 11px; margin-top: 4px; font-weight: 600;"></div>
            </div>
            <div class="size-card active" id="sz-M" onclick="pickSize('M')">
                <div class="size-icon" style="font-size:30px">&#9749;</div>
                <div class="size-label">M</div>
                <div class="size-price" id="sz-M-price"></div>
                <div class="size-stock" id="sz-M-stock" style="font-size: 11px; margin-top: 4px; font-weight: 600;"></div>
            </div>
            <div class="size-card" id="sz-L" onclick="pickSize('L')">
                <div class="size-icon" style="font-size:38px">&#9749;</div>
                <div class="size-label">L</div>
                <div class="size-price" id="sz-L-price"></div>
                <div class="size-stock" id="sz-L-stock" style="font-size: 11px; margin-top: 4px; font-weight: 600;"></div>
            </div>
        </div>
        <button type="button" class="btn-size-confirm" onclick="addSize()">Xác nhận</button>
    </div>
</div>

<!-- ============================================================
     TABLE PICKER MODAL (iframe)
============================================================ -->
<div class="table-modal-overlay" id="tableModal" onclick="closeTableIfOverlay(event)">
    <div class="table-modal-box">
        <div class="table-modal-hd">
            <div class="table-modal-title">&#127860; Chon ban</div>
            <button type="button" class="table-modal-close" onclick="closeTableModal()">&#x2715;</button>
        </div>
        <iframe id="tableIframe" class="table-modal-iframe" src="" title="Chon ban"></iframe>
    </div>
</div>

<!-- ============================================================
     INVENTORY MODAL
============================================================ -->
<div class="inv-modal-overlay" id="invModal" onclick="closeInvIfOverlay(event)">
    <div class="inv-modal-box">
        <div class="inv-modal-hd">
            <div class="inv-modal-title">
                <span class="material-symbols-outlined" style="font-size:20px">inventory_2</span>
                Quan ly kho nguyen lieu
            </div>
            <button type="button" class="inv-modal-close" onclick="closeInventoryModal()">&#x2715;</button>
        </div>
        <div class="inv-modal-tabs">
            <button type="button" class="inv-tab active" onclick="switchInvTab('all', this)">Tat ca</button>
            <button type="button" class="inv-tab" onclick="switchInvTab('warning', this)">&#9888; Sap het</button>
            <button type="button" class="inv-tab" onclick="switchInvTab('danger', this)">&#10060; Het hang</button>
        </div>
        <div class="inv-modal-body">
            <div class="inv-summary" id="invSummary"></div>
            <table class="inv-table">
                <thead>
                    <tr>
                        <th>Nguyen lieu</th>
                        <th>Don vi</th>
                        <th>Ton kho</th>
                        <th>Nguong</th>
                        <th>Trang thai</th>
                        <th>Muc ton</th>
                    </tr>
                </thead>
                <tbody id="invTableBody"></tbody>
            </table>
        </div>
    </div>
</div>

<!-- Toast -->
<div id="coToast" style="position:fixed;bottom:24px;left:50%;transform:translateX(-50%) translateY(80px);background:#1a1a2e;color:#fff;padding:12px 22px;border-radius:10px;font-size:13.5px;font-weight:600;z-index:9999;transition:transform 0.35s cubic-bezier(.34,1.56,.64,1);white-space:nowrap;box-shadow:0 4px 20px rgba(0,0,0,0.25);"></div>

<script>
/* ============================================================
   DB STOCK INJECTION
============================================================ */
var PRODUCT_STOCK = {};
<%
    try {
        StockService ss = new StockService();
        HashMap<Product, HashMap<String, Integer>> stockMap = ss.calculateProduct();
        for (Map.Entry<Product, HashMap<String, Integer>> entry : stockMap.entrySet()) {
            String pName = entry.getKey().getProductName().replace("'", "\\'");
%>
PRODUCT_STOCK['<%= pName %>'] = {
<%
            for (Map.Entry<String, Integer> sizeEntry : entry.getValue().entrySet()) {
%>
    '<%= sizeEntry.getKey() %>': <%= sizeEntry.getValue() %>,
<%
            }
%>
};
<%
        }
    } catch (Exception e) {
        System.err.println("Error loading stock: " + e.getMessage());
    }
%>
console.log("DB Loaded Stock:", PRODUCT_STOCK);

/* ============================================================
   STATE
============================================================ */
var cart = [];
var selectedTable  = null;
var selectedTableId = null;
var subtotalVal    = 0;
var couponDiscount = 0;   // percentage from coupon code
var activeCouponCode = '';
var memberDiscount = 0;   // percentage-based from membership
var discountVal    = 0;   // total = coupon + memberPct * subtotal
var grandTotalVal  = 0;
var selectedPayMethod = null;
var orderIdCounter = 1000;
var activeMember   = null; // {name, phone, tier, pct, points}

/* ── Apply sold-out class on load ── */
(function initStock() {
    var cards = document.querySelectorAll('#menuGrid .menu-card');
    for (var i = 0; i < cards.length; i++) {
        var s = parseInt(cards[i].getAttribute('data-stock') || '999');
        if (s <= 0) cards[i].classList.add('sold-out');
    }
})();

/* ============================================================
   INVENTORY DATA — from IngredientDAO
============================================================ */
var INGREDIENTS = [
<%
    try {
        IngredientDAO iDao = new IngredientDAO();
        HashMap<Integer, Ingredient> igMap = iDao.getAllIngredients();
        int count = 0;
        int total = igMap.size();
        for (Ingredient ig : igMap.values()) {
            count++;
            String iName = ig.getIngredientName() != null ? ig.getIngredientName().replace("'", "\\'") : "Unknown";
            String iUnit = ig.getUnit() != null ? ig.getUnit().replace("'", "\\'") : "";
            double iStock = ig.getStockQuantity() != null ? ig.getStockQuantity().doubleValue() : 0.0;
            double iMin = ig.getMinStockQuantity() != null ? ig.getMinStockQuantity().doubleValue() : 0.0;
%>
    { id: <%= ig.getIngredientId() %>, name: '<%= iName %>', unit: '<%= iUnit %>', stock: <%= iStock %>, min: <%= iMin %> }<%= count < total ? "," : "" %>
<%
        }
    } catch (Exception e) {
        System.err.println("Error loading ingredients: " + e.getMessage());
    }
%>
];

var activeInvTab = 'all';

function getInvStatus(ig) {
    if (ig.stock <= 0) return 'danger';
    if (ig.stock <= ig.min * 1.2) return 'warning';
    return 'ok';
}

function getStatusLabel(s) {
    if (s === 'danger')  return '<span class="inv-status danger">&#10060; Het hang</span>';
    if (s === 'warning') return '<span class="inv-status warning">&#9888; Sap het</span>';
    return '<span class="inv-status ok">&#9989; Du hang</span>';
}

function renderInventoryWarning() {
    var warnings = [];
    var dangers  = [];
    for (var i = 0; i < INGREDIENTS.length; i++) {
        var s = getInvStatus(INGREDIENTS[i]);
        if (s === 'warning') warnings.push(INGREDIENTS[i]);
        if (s === 'danger')  dangers.push(INGREDIENTS[i]);
    }
    var total = warnings.length + dangers.length;
    var bar = document.getElementById('invWarningBar');
    var badge = document.getElementById('invBadgeCount');

    if (total > 0) {
        bar.classList.add('show');
        document.getElementById('invWarnCount').textContent = total;
        badge.textContent = total;
        badge.style.display = 'inline';

        var parts = [];
        if (dangers.length > 0) parts.push(dangers.length + ' het hang');
        if (warnings.length > 0) parts.push(warnings.length + ' sap het');
        document.getElementById('invWarnText').textContent =
            'Canh bao kho: ' + parts.join(', ') + ' — can nhap them!';
    } else {
        bar.classList.remove('show');
        badge.style.display = 'none';
    }
}

function renderInventoryModal() {
    // Summary cards
    var ok = 0, warn = 0, danger = 0;
    for (var i = 0; i < INGREDIENTS.length; i++) {
        var s = getInvStatus(INGREDIENTS[i]);
        if (s === 'ok') ok++;
        else if (s === 'warning') warn++;
        else danger++;
    }
    document.getElementById('invSummary').innerHTML =
        '<div class="inv-sum-card ok"><div class="inv-sum-val">' + ok + '</div><div class="inv-sum-label">Du hang</div></div>' +
        '<div class="inv-sum-card warning"><div class="inv-sum-val">' + warn + '</div><div class="inv-sum-label">Sap het</div></div>' +
        '<div class="inv-sum-card danger"><div class="inv-sum-val">' + danger + '</div><div class="inv-sum-label">Het hang</div></div>';

    // Table rows
    var tbody = document.getElementById('invTableBody');
    tbody.innerHTML = '';
    for (var j = 0; j < INGREDIENTS.length; j++) {
        var ig = INGREDIENTS[j];
        var status = getInvStatus(ig);

        if (activeInvTab !== 'all' && status !== activeInvTab) continue;

        // Bar percentage — cap at 100%
        var maxRef = ig.min * 3; // reference max for bar
        var pct = maxRef > 0 ? Math.min(100, Math.round((ig.stock / maxRef) * 100)) : 0;

        var tr = document.createElement('tr');
        tr.innerHTML =
            '<td style="font-weight:600">' + ig.name + '</td>' +
            '<td style="color:#8a8a9a">' + ig.unit + '</td>' +
            '<td><strong>' + ig.stock + '</strong></td>' +
            '<td style="color:#8a8a9a">' + ig.min + '</td>' +
            '<td>' + getStatusLabel(status) + '</td>' +
            '<td><div class="inv-bar-wrap"><div class="inv-bar-fill ' + status + '" style="width:' + pct + '%"></div></div></td>';
        tbody.appendChild(tr);
    }
}

function openInventoryModal() {
    renderInventoryModal();
    document.getElementById('invModal').classList.add('open');
}

function closeInventoryModal() {
    document.getElementById('invModal').classList.remove('open');
}

function closeInvIfOverlay(e) {
    if (e.target === document.getElementById('invModal')) closeInventoryModal();
}

function switchInvTab(tab, btn) {
    activeInvTab = tab;
    var tabs = document.querySelectorAll('.inv-tab');
    for (var i = 0; i < tabs.length; i++) tabs[i].classList.remove('active');
    btn.classList.add('active');
    renderInventoryModal();
}

renderInventoryWarning();

var activeCat = 'all';

function setCategory(cat, btn) {
    activeCat = cat;
    var btns = document.querySelectorAll('.cat-btn');
    for (var i = 0; i < btns.length; i++) btns[i].classList.remove('active');
    btn.classList.add('active');
    filterMenu();
}

function filterMenu() {
    var q = document.getElementById('menuSearch').value.toLowerCase();
    var cards = document.querySelectorAll('#menuGrid .menu-card');
    for (var i = 0; i < cards.length; i++) {
        var c    = cards[i];
        var cat  = c.getAttribute('data-cat');
        var name = c.getAttribute('data-name').toLowerCase();
        c.style.display = ((activeCat === 'all' || cat === activeCat) &&
                           (q === '' || name.indexOf(q) >= 0)) ? '' : 'none';
    }
}


var pendingItem = null;   
var pendingSize = 'M';    

var SIZE_MODS = { S: -5000, M: 0, L: 10000 };
var SIZE_LABELS = { S: 'Nho', M: 'Vua', L: 'Lon' };

function addItem(name, price) {
    /* Mo modal chon size */
    pendingItem = { name: name, basePrice: price };
    document.getElementById('sizeModalTitle').textContent = name + ' — Chon size';
    
    var firstAvailableSize = null;
    
    /* Update price & stock labels */
    ['S','M','L'].forEach(function(s) {
        var p = price + SIZE_MODS[s];
        document.getElementById('sz-' + s + '-price').textContent = p.toLocaleString('vi-VN') + ' d';
        
        // Stock logic based on DB injection
        var hasRecipe = (PRODUCT_STOCK[name] && (s in PRODUCT_STOCK[name]));
        var cups = hasRecipe ? PRODUCT_STOCK[name][s] : 0;
        var stockEl = document.getElementById('sz-' + s + '-stock');
        var cardEl = document.getElementById('sz-' + s);
        
        if (!hasRecipe) {
            cardEl.style.display = 'none';
        } else {
            cardEl.style.display = '';
            if (cups > 0) {
                stockEl.textContent = 'Con pha dc: ' + cups + ' coc';
                stockEl.style.color = '#16a34a';
                cardEl.style.opacity = '1';
                cardEl.style.pointerEvents = 'auto';
                if (!firstAvailableSize) firstAvailableSize = s;
            } else {
                stockEl.textContent = 'Het nguyen lieu';
                stockEl.style.color = '#dc2626';
                cardEl.style.opacity = '0.5';
                cardEl.style.pointerEvents = 'none';
            }
        }
    });
    
    if (!firstAvailableSize) {
        showToast('Mon nay da het nguyen lieu cho tat ca cac size! (Het pha duoc roi)');
        console.warn("Item out of stock:", name, "PRODUCT_STOCK=", PRODUCT_STOCK[name]);
        return;
    }
    
    pendingSize = firstAvailableSize;
    ['S','M','L'].forEach(function(s) {
        document.getElementById('sz-' + s).classList.toggle('active', s === pendingSize);
    });
    
    document.getElementById('sizeModal').classList.add('open');
}

function pickSize(s) {
    pendingSize = s;
    ['S','M','L'].forEach(function(sz) {
        document.getElementById('sz-' + sz).classList.toggle('active', sz === s);
    });
}

function addSize() {
    if (!pendingSize) return;
    
    var maxCups = (PRODUCT_STOCK[pendingItem.name] && PRODUCT_STOCK[pendingItem.name][pendingSize]) ? PRODUCT_STOCK[pendingItem.name][pendingSize] : 0;
    var unitPrice = pendingItem.basePrice + SIZE_MODS[pendingSize];
    var key = pendingItem.name + ' (' + pendingSize + ')';
    var found = false;
    for (var i = 0; i < cart.length; i++) {
        if (cart[i].key === key) {
            if (cart[i].qty < maxCups) {
                cart[i].qty++;
            } else {
                showToast('Khong du nguyen lieu! Toi da ' + maxCups + ' coc.');
                closeSizeModal(); return;
            }
            found = true; break;
        }
    }
    if (!found) {
        if (1 <= maxCups) {
            cart.push({ name: pendingItem.name, price: unitPrice, size: pendingSize, qty: 1, key: key });
        } else {
            showToast('Khong du nguyen lieu!');
            closeSizeModal(); return;
        }
    }
    renderCart();
    closeSizeModal();
}

function closeSizeModal() {
    document.getElementById('sizeModal').classList.remove('open');
    pendingItem = null;
}

function closeSizeIfOverlay(e) {
    if (e.target === document.getElementById('sizeModal')) closeSizeModal();
}

function changeQty(name, delta) {
    for (var i = 0; i < cart.length; i++) {
        if (cart[i].key === name || cart[i].name === name) {
            var maxCups = (PRODUCT_STOCK[cart[i].name] && PRODUCT_STOCK[cart[i].name][cart[i].size]) ? PRODUCT_STOCK[cart[i].name][cart[i].size] : 0;
            if (delta > 0 && cart[i].qty >= maxCups) {
                showToast('Khong du nguyen lieu! Toi da ' + maxCups + ' coc.');
                return;
            }
            cart[i].qty += delta;
            if (cart[i].qty <= 0) cart.splice(i, 1);
            renderCart(); return;
        }
    }
}

function removeItem(name) {
    for (var i = 0; i < cart.length; i++) {
        if (cart[i].key === name || cart[i].name === name) { cart.splice(i, 1); renderCart(); return; }
    }
}

function fmt(n) { return n.toLocaleString('vi-VN') + ' d'; }

function renderCart() {
    var panel = document.getElementById('orderPanel');

    // Recalc subtotal
    subtotalVal = 0;
    for (var i = 0; i < cart.length; i++) subtotalVal += cart[i].price * cart[i].qty;

    // Membership % discount applied on subtotal
    memberDiscount = activeMember ? Math.round(subtotalVal * activeMember.pct / 100) : 0;

    // Coupon discount applied on subtotal
    var couponDiscountAmt = Math.round(subtotalVal * couponDiscount);

    // Total discount
    discountVal   = couponDiscountAmt + memberDiscount;
    grandTotalVal = Math.max(0, subtotalVal - discountVal);

    document.getElementById('itemCount').textContent   = cart.length;
    document.getElementById('subtotal').textContent    = fmt(subtotalVal);
    document.getElementById('discountAmt').textContent = discountVal > 0 ? '-' + fmt(discountVal) : '0 d';
    document.getElementById('grandTotal').textContent  = fmt(grandTotalVal);

      if (cart.length === 0) {
          panel.innerHTML = '<div class="empty-cart" id="emptyMsg" style="display:block;">' +
                            '<div class="ec-icon">&#128722;</div>' +
                            '<div class="ec-text">Chua co mon nao</div>' +
                            '</div>';
          return;
      }

    panel.innerHTML = '';
    for (var j = 0; j < cart.length; j++) {
        var item = cart[j];
        var line = item.price * item.qty;
        var itemKey = item.key || item.name;

        var div    = document.createElement('div'); div.className = 'order-line';
        var nameEl = document.createElement('div'); nameEl.className = 'ol-name'; nameEl.textContent = item.key;
        var bot    = document.createElement('div'); bot.className = 'ol-bottom';
        var qEl    = document.createElement('div'); qEl.className = 'ol-qty';

        var bm = document.createElement('button'); bm.type = 'button'; bm.className = 'qty-btn'; bm.textContent = '-';
        (function(k){ bm.onclick = function(){ changeQty(k,-1); }; })(itemKey);

        var qn = document.createElement('span'); qn.className = 'qty-num'; qn.textContent = item.qty;

        var bp = document.createElement('button'); bp.type = 'button'; bp.className = 'qty-btn'; bp.textContent = '+';
        (function(k){ bp.onclick = function(){ changeQty(k,1); }; })(itemKey);

        qEl.appendChild(bm); qEl.appendChild(qn); qEl.appendChild(bp);

        var pEl = document.createElement('span'); pEl.className = 'ol-price'; pEl.textContent = fmt(line);

        var dEl = document.createElement('button'); dEl.type = 'button'; dEl.className = 'ol-del';
        dEl.innerHTML = '<span class="material-symbols-outlined" style="font-size:16px">delete</span>';
        (function(k){ dEl.onclick = function(){ removeItem(k); }; })(itemKey);

        bot.appendChild(qEl); bot.appendChild(pEl); bot.appendChild(dEl);
        div.appendChild(nameEl); div.appendChild(bot);
        panel.appendChild(div);
    }
}

/* ============================================================
   MEMBERSHIP LOOKUP
============================================================ */
function lookupMember() {
    var phone = document.getElementById('memberPhone').value.trim().replace(/\s/g,'');
    var resDiv = document.getElementById('memResult');

    if (!phone) {
        resDiv.className = 'mem-result show';
        resDiv.innerHTML = '<div style="font-size:12px;color:#ef4444;">Vui long nhap so dien thoai.</div>';
        return;
    }

    fetch('${pageContext.request.contextPath}/CheckPromotion?action=member&phone=' + encodeURIComponent(phone))
    .then(response => response.json())
    .then(data => {
        if (!data.found) {
            activeMember = null;
            memberDiscount = 0;
            resDiv.className = 'mem-result show';
            resDiv.innerHTML = '<div style="font-size:12px;color:#ef4444;">&#10007; Khong tim thay thanh vien voi SDT: <strong>' + phone + '</strong></div>';
            renderCart();
            return;
        }

        var icon = '&#11088;'; // Default Gold icon
        var tierClass = 'gold'; // default
        if (data.tier && data.tier.toLowerCase().includes('bạc')) { icon = '&#129320;'; tierClass = 'silver'; }
        else if (data.tier && data.tier.toLowerCase().includes('đồng')) { icon = '&#129353;'; tierClass = 'bronze'; }

        activeMember = { id: data.id, name: data.name, tier: tierClass, points: 0, pct: data.pct };
        memberDiscount = data.pct;
        
        var discInfo = data.pct > 0 ? ('Giam ' + data.pct + '%') : 'Khong co giam gia';

        resDiv.className = 'mem-result show';
        resDiv.innerHTML =
            '<div class="mem-card ' + tierClass + '">' +
                '<div class="mem-badge ' + tierClass + '">' + icon + '</div>' +
                '<div class="mem-info">' +
                    '<div class="mem-name">' + data.name + '</div>' +
                    '<div class="mem-tier ' + tierClass + '">' + data.tier + '</div>' +
                '</div>' +
                '<div class="mem-disc-tag ' + tierClass + '">' + (data.pct > 0 ? '-' + data.pct + '%' : '0%') + '</div>' +
            '</div>' +
            '<div style="font-size:11.5px;color:#16a34a;font-weight:600;">' +
                (data.pct > 0 ? '&#10003; ' + discInfo + ' ap dung tren tong don' : 'Thanh vien co ban') +
            '</div>';

        renderCart();
    })
    .catch(err => {
        console.error(err);
        resDiv.className = 'mem-result show';
        resDiv.innerHTML = '<div style="font-size:12px;color:#ef4444;">Loi ket noi!</div>';
    });
}

/* ============================================================
   DISCOUNT CODE
============================================================ */
function applyDiscount() {
    var code = document.getElementById('discountCode').value.trim().toUpperCase();
    var msg  = document.getElementById('discMsg');
    if (!code) { msg.className = 'disc-msg err'; msg.textContent = 'Vui long nhap ma giam gia.'; return; }
    
    fetch('${pageContext.request.contextPath}/CheckPromotion?action=discount&code=' + encodeURIComponent(code))
    .then(response => response.json())
    .then(data => {
        if (data.valid) {
            couponDiscount = data.pct / 100.0;
            activeCouponCode = code;
            msg.className = 'disc-msg ok';
            msg.textContent = '\u2713 Ma hop le! Giam ' + data.pct + '%';
        } else {
            couponDiscount = 0;
            activeCouponCode = '';
            msg.className = 'disc-msg err';
            msg.textContent = '\u2717 ' + data.msg;
        }
        renderCart();
    })
    .catch(err => {
        console.error(err);
        msg.className = 'disc-msg err';
        msg.textContent = '\u2717 Loi ket noi!';
    });
}

/* ============================================================
   TABLE SELECTION — iframe modal + postMessage
============================================================ */
function chooseTable() {
    var iframe = document.getElementById('tableIframe');
    iframe.src = '${pageContext.request.contextPath}/TableSession';
    document.getElementById('tableModal').classList.add('open');
}

function closeTableModal() {
    document.getElementById('tableModal').classList.remove('open');
    document.getElementById('tableIframe').src = '';
}

function closeTableIfOverlay(e) {
    if (e.target === document.getElementById('tableModal')) closeTableModal();
}

/* Listen for TABLE_SELECTED message from iframe */
window.addEventListener('message', function(e) {
    if (e.data && e.data.type === 'TABLE_SELECTED') {
        selectedTable = e.data.tableCode;
        selectedTableId = e.data.tableId;
        var btn = document.getElementById('tableBtn');
        btn.textContent = e.data.tableCode + (e.data.area ? ' \u2014 ' + e.data.area : '');
        btn.style.background = '#2c1a0e';
        btn.style.color = '#fff';
        btn.style.borderColor = '#2c1a0e';
        closeTableModal();
        showToast('\u2713 Da chon ' + e.data.tableCode);
    }
});

function showToast(msg) {
    var t = document.getElementById('coToast');
    if (!t) return;
    t.textContent = msg;
    t.classList.add('show');
    setTimeout(function(){ t.classList.remove('show'); }, 2500);
}

function saveOrder() {
    if (cart.length === 0) { alert('Chua co mon nao trong don!'); return; }
    alert('Da luu tam ' + cart.length + ' mon. Tong: ' + fmt(grandTotalVal));
}

/* ============================================================
   PAYMENT FLOW
============================================================ */
function openPayModal() {
    if (cart.length === 0) { alert('Vui long them mon truoc!'); return; }
    selectedPayMethod = null;

    // Hide all method sections
    hideSections();
    resetPayButtons();
    document.getElementById('btnConfirmPay').disabled = true;

    // Fill summary
    var sum = '<div class="pay-sum-row"><span>Ban</span><span>' + (selectedTable || 'Walk-in') + '</span></div>';
    for (var i = 0; i < cart.length; i++) {
        sum += '<div class="pay-sum-row"><span>' + cart[i].name + ' x' + cart[i].qty + '</span><span>' + fmt(cart[i].price * cart[i].qty) + '</span></div>';
    }
    if (discountVal > 0) {
        sum += '<div class="pay-sum-row" style="color:#16a34a"><span>Giam gia</span><span>-' + fmt(discountVal) + '</span></div>';
    }
    sum += '<div class="pay-sum-row total"><span>Tong tien</span><span>' + fmt(grandTotalVal) + '</span></div>';
    document.getElementById('paySummary').innerHTML = sum;

    document.getElementById('payModal').classList.add('open');
}

function resetPayButtons() {
    var btns = document.querySelectorAll('.pay-method-card');
    for (var i = 0; i < btns.length; i++) btns[i].classList.remove('selected');
}

function hideSections() {
    document.getElementById('cashSection').classList.remove('show');
    document.getElementById('qrSection').classList.remove('show');
}

function selectPayMethod(method) {
    selectedPayMethod = method;
    resetPayButtons();
    hideSections();

    document.getElementById('pm-' + method).classList.add('selected');
    document.getElementById('btnConfirmPay').disabled = false;

    if (method === 'cash') {
        document.getElementById('cashSection').classList.add('show');
        document.getElementById('cashDue').textContent = fmt(grandTotalVal);
        // Quick amount buttons
        var amounts = [grandTotalVal, roundUp(grandTotalVal, 10000), roundUp(grandTotalVal, 50000), roundUp(grandTotalVal, 100000), 500000, 1000000];
        var unique  = [];
        var seen = {};
        for (var i = 0; i < amounts.length; i++) {
            if (amounts[i] >= grandTotalVal && !seen[amounts[i]]) {
                seen[amounts[i]] = true; unique.push(amounts[i]);
            }
            if (unique.length >= 5) break;
        }
        var quickDiv = document.getElementById('cashQuick');
        quickDiv.innerHTML = '';
        for (var j = 0; j < unique.length; j++) {
            (function(amt) {
                var b = document.createElement('button');
                b.type = 'button'; b.className = 'cash-quick-btn';
                b.textContent = fmt(amt);
                b.onclick = function() {
                    document.getElementById('cashInput').value = amt;
                    calcChange();
                };
                quickDiv.appendChild(b);
            })(unique[j]);
        }
        document.getElementById('cashInput').value = '';
        document.getElementById('cashChange').classList.remove('show');

    } else if (method === 'qr') {
        document.getElementById('qrSection').classList.add('show');
        document.getElementById('qrAmt').textContent = fmt(grandTotalVal);
        var qrData = 'CoffeeHouse|' + grandTotalVal + 'VND|' + (selectedTable || 'WalkIn');
        document.getElementById('qrImg').src = 'https://api.qrserver.com/v1/create-qr-code/?size=180x180&data=' + encodeURIComponent(qrData);
    }
}

function roundUp(n, step) {
    return Math.ceil(n / step) * step;
}

function calcChange() {
    var got    = parseInt(document.getElementById('cashInput').value) || 0;
    var change = got - grandTotalVal;
    var changeDiv = document.getElementById('cashChange');
    if (got >= grandTotalVal) {
        document.getElementById('changeAmt').textContent = fmt(change);
        changeDiv.classList.add('show');
        document.getElementById('btnConfirmPay').disabled = false;
    } else {
        changeDiv.classList.remove('show');
        document.getElementById('btnConfirmPay').disabled = (selectedPayMethod === 'cash');
    }
}

function confirmPayment() {
    if (!selectedPayMethod) { alert('Vui long chon phuong thuc thanh toan!'); return; }
    if (selectedPayMethod === 'cash') {
        var got = parseInt(document.getElementById('cashInput').value) || 0;
        if (got < grandTotalVal) { alert('So tien chua du!'); return; }
    }
    closeModal('payModal');
    showBill();
}

/* ============================================================
   BILL / RECEIPT
============================================================ */
function showBill() {
    orderIdCounter++;
    var orderId = 'ORD' + orderIdCounter;
    var now     = new Date();
    var dateStr = now.getDate() + '/' + (now.getMonth()+1) + '/' + now.getFullYear()
                + ' ' + String(now.getHours()).padStart(2,'0') + ':' + String(now.getMinutes()).padStart(2,'0');
    var methodNames = { cash: 'Tien mat', qr: 'QR Code' };

    var itemRows = '';
    for (var i = 0; i < cart.length; i++) {
        var it = cart[i];
        itemRows +=
            '<div class="receipt-item">' +
            '<span class="ri-name">' + it.name + '</span>' +
            '<span class="ri-qty">x' + it.qty + '</span>' +
            '<span class="ri-price">' + fmt(it.price * it.qty) + '</span>' +
            '</div>';
    }

    var discRow = discountVal > 0
        ? '<div class="receipt-total-row" style="color:#16a34a"><span>Giam gia</span><span>-' + fmt(discountVal) + '</span></div>'
        : '';
    var changeRow = (selectedPayMethod === 'cash')
        ? '<div class="receipt-total-row" style="color:#888"><span>Tien thoi</span><span>' + fmt((parseInt(document.getElementById('cashInput').value)||0) - grandTotalVal) + '</span></div>'
        : '';

    var html =
        '<div class="receipt-logo">&#9749; Coffee House</div>' +
        '<div class="receipt-store">123 Nguyen Hue, Q.1, TP.HCM</div>' +
        '<div class="receipt-store">SĐT: 028 1234 5678</div>' +
        '<hr class="receipt-divider">' +
        '<div class="receipt-info"><strong>Ma don:</strong> ' + orderId + '</div>' +
        '<div class="receipt-info"><strong>Ban:</strong> ' + (selectedTable || 'Walk-in') + '</div>' +
        '<div class="receipt-info"><strong>Thoi gian:</strong> ' + dateStr + '</div>' +
        '<hr class="receipt-divider">' +
        '<div class="receipt-items-hd"><span>Mon</span><span style="width:28px;text-align:center">SL</span><span style="min-width:72px;text-align:right">Gia</span></div>' +
        itemRows +
        '<hr class="receipt-divider">' +
        '<div class="receipt-total-row"><span>Tam tinh</span><span>' + fmt(subtotalVal) + '</span></div>' +
        discRow +
        '<div class="receipt-total-row receipt-grand"><span>TONG CONG</span><span>' + fmt(grandTotalVal) + '</span></div>' +
        changeRow +
        '<div class="receipt-method">Thanh toan: ' + methodNames[selectedPayMethod] + '</div>' +
        '<hr class="receipt-divider">' +
        '<div class="receipt-footer">Cam on quy khach da den voi Coffee House!</div>' +
        '<div class="receipt-footer">Hen gap lai quy khach! &#128149;</div>';

    document.getElementById('receiptContent').innerHTML = html;
    document.getElementById('billModal').classList.add('open');
}

function printBill() {
    window.print();
}

function newOrder() {
    closeModal('billModal');
    cart = [];
    couponDiscount = 0;
    activeCouponCode = '';
    memberDiscount = 0;
    discountVal    = 0;
    activeMember   = null;
    selectedTable  = null;
    selectedTableId = null;
    document.getElementById('tableBtn').textContent = 'Chon ban';
    document.getElementById('discountCode').value  = '';
    document.getElementById('discMsg').textContent  = '';
    document.getElementById('memberPhone').value    = '';
    document.getElementById('memResult').className  = 'mem-result';
    document.getElementById('memResult').innerHTML  = '';
    document.getElementById('orderNote').value      = '';
    renderCart();
}

/* ============================================================
   MODAL HELPERS
============================================================ */
function submitPOSOrder(isNewOrder) {
    try {
        if (cart.length === 0) {
            showToast("Gio hang trong!");
            return;
        }
    var cartData = cart.map(function(item) {
        return item.name + "," + item.size + "," + item.qty + "," + item.price;
    }).join("|");
    
    var noteVal = document.getElementById('orderNote') ? document.getElementById('orderNote').value : '';
    
    var methodNamesEN = { cash: 'Cash', qr: 'QR Code' };
    var finalPayMethod = (selectedPayMethod && methodNamesEN[selectedPayMethod]) ? methodNamesEN[selectedPayMethod] : 'Cash';
    
    var formData = new URLSearchParams();
    formData.append("cartData", cartData);
    formData.append("totalAmount", subtotalVal);
    formData.append("discountAmount", discountVal);
    formData.append("finalAmount", grandTotalVal);
    formData.append("paymentMethod", finalPayMethod);
    formData.append("tableName", selectedTable || 'Walk-in');
    if (selectedTableId) {
        formData.append("tableId", selectedTableId);
    }
    formData.append("note", noteVal);
    
    if (activeMember && activeMember.id) {
        formData.append("customerId", activeMember.id);
    }
    if (couponDiscount > 0 && activeCouponCode) {
        formData.append("discountCode", activeCouponCode);
    }
    
    fetch('${pageContext.request.contextPath}/PosOrder', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData.toString()
    })
    .then(function(response) {
        if (response.ok) {
            showToast("Tao don thanh cong!");
            closeModal('billModal');
            if (isNewOrder) {
                setTimeout(function() { window.location.reload(); }, 600);
            } else {
                resetAll();
            }
        } else {
            response.text().then(function(text) {
                alert("Lỗi Backend: " + text);
            });
        }
    })
    .catch(function(err) {
        showToast("Loi ket noi!");
        alert("Lỗi mạng / Lỗi gọi API: " + err);
        console.error(err);
    });
    } catch(err) {
        alert("Lỗi kịch bản Javascript: " + err.message + "\nDòng: " + err.lineNumber);
        console.error(err);
    }
}

function resetAll() {
    cart = [];
    selectedTable = null;
    selectedTableId = null;
    couponDiscount = 0;
    activeCouponCode = '';
    activeMember = null;
    
    document.getElementById('tableBtn').textContent = 'Chon ban';
    document.getElementById('tableBtn').style = '';
    document.getElementById('memberPhone').value = '';
    document.getElementById('memResult').classList.remove('show');
    document.getElementById('discountCode').value = '';
    document.getElementById('discMsg').textContent = '';
    if(document.getElementById('orderNote')) document.getElementById('orderNote').value = '';
    
    renderCart();
}

function closeModal(id) {
    document.getElementById(id).classList.remove('open');
}
function closeIfOverlay(e, id) {
    if (e.target === document.getElementById(id)) closeModal(id);
}
</script>
</body>
</html>

