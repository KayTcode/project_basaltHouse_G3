package controller;

import dao.SizeDAO;
import dto.UserLoginDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import model.CartItem;
import model.Product;
import services.CartService;
import services.StockService;

public class CartServlet extends HttpServlet {

    private final CartService cartService = new CartService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if ("checkout-form".equals(action)) {
            HttpSession session = request.getSession();
            if (session.getAttribute("currentUser") == null) {
                session.setAttribute("loginError", "Vui lòng đăng nhập để tiến hành thanh toán.");
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            handleCheckoutForm(request, response);
            return;
        }
        if ("applyDiscount".equals(action)) {
            handleApplyDiscount(request, response);
            return;
        }
        if (action != null) {
            handleCartAction(request, response, action);
            return;
        }

        HttpSession session = request.getSession();
        @SuppressWarnings("unchecked")
        Map<String, CartItem> cart = (Map<String, CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new LinkedHashMap<>();
            session.setAttribute("cart", cart);
        }

        int totalAmount = 0;
        int totalQty = 0;
        for (CartItem item : cart.values()) {
            totalAmount += item.getSubtotal();
            totalQty += item.getQuantity();
        }
        try {
            StockService stockSvc = new StockService();
            HashMap<Product, HashMap<String, Integer>> rawStock = stockSvc.calculateProduct();
            HashMap<Integer, HashMap<String, Integer>> stockMap = new HashMap<>();
            for (Map.Entry<Product, HashMap<String, Integer>> e : rawStock.entrySet()) {
                stockMap.put(e.getKey().getProductId(), e.getValue());
            }
            request.setAttribute("stockMap", stockMap);
        } catch (Exception ignored) {
        }

        request.setAttribute("cartItems", cart.values());
        request.setAttribute("totalAmount", totalAmount);
        request.setAttribute("totalQty", totalQty);

        request.getRequestDispatcher("views/Order/Cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if ("checkout".equals(action)) {
            HttpSession session = request.getSession();
            if (session.getAttribute("currentUser") == null) {
                session.setAttribute("loginError", "Vui lòng đăng nhập để tiến hành thanh toán.");
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            handleCheckout(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/Cart");
    }

    private void handleCartAction(HttpServletRequest request, HttpServletResponse response, String action)
            throws IOException {
        HttpSession session = request.getSession();
        @SuppressWarnings("unchecked")
        Map<String, CartItem> cart = (Map<String, CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new LinkedHashMap<>();
            session.setAttribute("cart", cart);
        }

        // cartKey = "productId_sizeName", là key dùng trong Map cart
        String cartKey = request.getParameter("productId");

        switch (action) {
            case "add": {
                String productId = cartKey; // lúc add, cartKey chứa productId thuần
                String productName = request.getParameter("productName");
                String priceStr = request.getParameter("price");
                String sizeIdStr = request.getParameter("sizeId");
                int price = 0;
                try {
                    price = new java.math.BigDecimal(priceStr.trim()).intValue();
                } catch (Exception ignored) {
                }

                String sizeName = "";
                if (sizeIdStr != null && !sizeIdStr.trim().isEmpty()) {
                    try {
                        int sizeId = Integer.parseInt(sizeIdStr.trim());
                        SizeDAO sizeDAO = new SizeDAO();
                        HashMap<Integer, String> sizeMap = sizeDAO.getSize();
                        sizeName = sizeMap.getOrDefault(sizeId, "");
                    } catch (Exception ignored) {
                    }
                }

                // Đọc số lượng từ form (mặc định 1 nếu không có hoặc không hợp lệ)
                int requestedQty = 1;
                String qtyStr = request.getParameter("quantity");
                if (qtyStr != null && !qtyStr.trim().isEmpty()) {
                    try {
                        requestedQty = Integer.parseInt(qtyStr.trim());
                        if (requestedQty < 1) requestedQty = 1;
                    } catch (NumberFormatException ignored) {
                    }
                }

                int stock = 0;
                if (!sizeName.isEmpty()) {
                    try {
                        int pId = Integer.parseInt(productId.trim());
                        StockService stockSvc = new StockService();
                        HashMap<Product, HashMap<String, Integer>> rawStock = stockSvc.calculateProduct();
                        for (Map.Entry<Product, HashMap<String, Integer>> e : rawStock.entrySet()) {
                            if (e.getKey().getProductId() == pId) {
                                stock = e.getValue().getOrDefault(sizeName, 0);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                // Composite key: "productId_sizeName" để phân biệt cùng sản phẩm khác size
                String key = sizeName.isEmpty() ? productId : productId + "_" + sizeName;
                CartItem existing = cart.get(key);
                if (existing == null) {
                    if (stock > 0) {
                        // Giới hạn số lượng theo tồn kho
                        int addQty = (stock > 0) ? Math.min(requestedQty, stock) : requestedQty;
                        CartItem newItem = new CartItem(productId, productName, price, addQty, sizeName, stock);
                        newItem.setCartKey(key);
                        cart.put(key, newItem);
                    }
                } else {
                    // Cùng sản phẩm + cùng size → cộng thêm số lượng yêu cầu
                    int currentQty = existing.getQuantity();
                    int maxQty = existing.getStock();
                    int newQty = currentQty + requestedQty;
                    if (maxQty > 0) {
                        newQty = Math.min(newQty, maxQty);
                    }
                    existing.setQuantity(newQty);
                }

                String redirectTarget = request.getParameter("redirect");
                if ("cart".equals(redirectTarget)) {
                    response.sendRedirect(request.getContextPath() + "/Cart");
                    return;
                }

                String referer = request.getHeader("referer");
                if (referer != null && !referer.isEmpty()) {
                    String sep = referer.contains("?") ? "&" : "?";
                    response.sendRedirect(referer + sep + "addSuccess=1");
                } else {
                    response.sendRedirect(request.getContextPath() + "/category?addSuccess=1");
                }
                return;
            }
            case "update": {
                String deltaStr = request.getParameter("delta");
                int delta = 1;
                try {
                    delta = Integer.parseInt(deltaStr);
                } catch (NumberFormatException ignored) {
                }
                cartService.updateQuantity(cart, cartKey, delta);
                break;
            }
            case "remove":
                cartService.removeItem(cart, cartKey);
                break;
            case "clear":
                cartService.clearCart(cart);
                break;
        }
        String referer = request.getHeader("referer");
        if (referer != null && referer.contains("/Cart")) {
            response.sendRedirect(request.getContextPath() + "/Cart");
        } else {
            response.sendRedirect(request.getContextPath() + "/Cart");
        }
    }

    private void handleCheckoutForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        @SuppressWarnings("unchecked")
        Map<String, CartItem> cart = (Map<String, CartItem>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/Cart?error=empty");
            return;
        }

        long totalAmount = 0;
        int totalQty = 0;
        for (CartItem item : cart.values()) {
            totalAmount += item.getSubtotal();
            totalQty += item.getQuantity();
        }

        // Tính giảm giá nếu có mã
        String discountCode = request.getParameter("discountCode");
        long discountAmount = 0;
        if (discountCode != null && !discountCode.trim().isEmpty()) {
            try {
                java.math.BigDecimal total = new java.math.BigDecimal(totalAmount);
                services.PromotionService ps = new services.PromotionService();
                discountAmount = ps.calculateDiscount(discountCode.trim(), total).longValue();
            } catch (Exception ignored) {
            }
        }
        long finalAmount = Math.max(totalAmount - discountAmount, 0);

        request.setAttribute("totalAmount", totalAmount);
        request.setAttribute("discountAmount", discountAmount);
        request.setAttribute("finalAmount", finalAmount);
        request.setAttribute("totalQty", totalQty);
        request.setAttribute("discountCode", discountCode != null ? discountCode : "");
        request.setAttribute("orderNote", request.getParameter("orderNote") != null ? request.getParameter("orderNote") : "");
        request.setAttribute("cartItems", cart.values());
        request.getRequestDispatcher("views/Order/Checkout.jsp").forward(request, response);
    }

    private void handleCheckout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        @SuppressWarnings("unchecked")
        Map<String, CartItem> cart = (Map<String, CartItem>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/Cart?error=empty");
            return;
        }

        String customerIdStr = null;
        Object currentUser = session.getAttribute("currentUser");
        if (currentUser instanceof UserLoginDTO) {
            UserLoginDTO user = (UserLoginDTO) currentUser;
            int accountId = user.getAccountId();
            dao.OrderDAO orderDAO = new dao.OrderDAO();
            int customerId = orderDAO.getCustomerIdByAccountId(accountId);
            if (customerId > 0) {
                customerIdStr = String.valueOf(customerId);
            }
        }

        String orderNote = request.getParameter("orderNote");    // ghi chú từ Cart.jsp → Orders.Note
        String deliveryNote = request.getParameter("deliveryNote"); // ghi chú từ Checkout.jsp → OrderAddresses.Note
        String discountCode = request.getParameter("discountCode");
        String deliveryAddress = request.getParameter("deliveryAddress");
        String paymentMethod = request.getParameter("paymentMethod");
//        String orderCode = cartService.checkout(cart, orderNote, customerIdStr, discountCode, deliveryAddress, paymentMethod, deliveryNote);

        // ── Lấy phương thức thanh toán từ form, mặc định COD ─────────────
        if (paymentMethod == null || paymentMethod.trim().isEmpty()
                || (!"COD".equals(paymentMethod) && !"MOMO".equals(paymentMethod))) {
            paymentMethod = "COD";
        }

        // ── Tạo đơn hàng trong DB ─────────────────────────────────────────
        // CartService sẽ KHÔNG clear cart nếu là VNPAY (clear khi confirm)
        String orderCode = cartService.checkout(cart, orderNote, customerIdStr, discountCode, deliveryAddress, paymentMethod, deliveryNote);

        if (orderCode == null) {
            response.sendRedirect(request.getContextPath() + "/Cart?error=checkout_failed");
            return;
        }

        // ── Phân luồng theo phương thức thanh toán ────────────────────────
        if ("MOMO".equals(paymentMethod)) {
            // Lưu orderCode vào session để VnpayPaymentServlet và VnpayReturnServlet dùng
            session.setAttribute("pendingOrderCode", orderCode);
            // Redirect sang servlet khởi tạo cổng thanh toán VNPAY
            response.sendRedirect(request.getContextPath() + "/momo/payment?orderCode=" + orderCode);
        } else {
            // COD: cart đã được clear bởi CartService, redirect trang thành công
            response.sendRedirect(request.getContextPath() + "/Cart?checkoutSuccess=1&code=" + orderCode);
        }
    }

    private void handleApplyDiscount(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        String code = request.getParameter("discountCode");
        if (code == null || code.trim().isEmpty()) {
            response.getWriter().write("{\"success\":false,\"error\":\"Vui lòng nhập mã giảm giá.\"}");
            return;
        }

        // Validate mã qua PromotionService
        services.PromotionService ps = new services.PromotionService();
        String checkJson = ps.checkDiscount(code.trim());

        if (checkJson.contains("\"valid\": false") || checkJson.contains("\"valid\":false")) {
            // Lấy msg từ JSON thủ công
            String msg = "Mã không hợp lệ hoặc đã hết hạn.";
            int msgIdx = checkJson.indexOf("\"msg\":");
            if (msgIdx >= 0) {
                int start = checkJson.indexOf('"', msgIdx + 6) + 1;
                int end   = checkJson.indexOf('"', start);
                if (start > 0 && end > start) msg = checkJson.substring(start, end);
            }
            response.getWriter().write("{\"success\":false,\"error\":\"" + msg + "\"}");
            return;
        }

        // Tính tổng tiền từ cart hiện tại
        HttpSession session = request.getSession(false);
        @SuppressWarnings("unchecked")
        Map<String, CartItem> cart = session != null
                ? (Map<String, CartItem>) session.getAttribute("cart") : null;

        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        if (cart != null) {
            for (CartItem item : cart.values()) {
                total = total.add(new java.math.BigDecimal(item.getPrice())
                        .multiply(new java.math.BigDecimal(item.getQuantity())));
            }
        }

        java.math.BigDecimal discountAmt = ps.calculateDiscount(code.trim(), total);
        java.math.BigDecimal finalAmt    = total.subtract(discountAmt).max(java.math.BigDecimal.ZERO);

        response.getWriter().write(String.format(
                "{\"success\":true,\"codeName\":\"%s\",\"discountAmount\":%s,\"finalAmount\":%s}",
                code.trim().replace("\"", "\\\""),
                discountAmt.toPlainString(),
                finalAmt.toPlainString()
        ));
    }
}
