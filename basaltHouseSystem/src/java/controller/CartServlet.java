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
            handleCheckoutForm(request, response);
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

        // ── Tính stock hiện tại theo từng productId-size ──────────────────
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
            case "add" -> {
                String productId  = cartKey; // lúc add, cartKey chứa productId thuần
                String productName = request.getParameter("productName");
                String priceStr = request.getParameter("price");
                String sizeIdStr = request.getParameter("sizeId");
                int price = 0;
                try {
                    price = new java.math.BigDecimal(priceStr.trim()).intValue();
                } catch (Exception ignored) {
                }

                String sizeName = "";
                if (sizeIdStr != null && !sizeIdStr.isBlank()) {
                    try {
                        int sizeId = Integer.parseInt(sizeIdStr.trim());
                        SizeDAO sizeDAO = new SizeDAO();
                        HashMap<Integer, String> sizeMap = sizeDAO.getSize();
                        sizeName = sizeMap.getOrDefault(sizeId, "");
                    } catch (Exception ignored) {
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
                    } catch (Exception ignored) {
                    }
                }

                String key = productId;
                CartItem existing = cart.get(key);
                if (existing == null) {
                    if (stock > 0) {
                        CartItem newItem = new CartItem(productId, productName, price, 1, sizeName, stock);
                        newItem.setCartKey(key);
                        cart.put(key, newItem);
                    }
                } else {
                    if (!sizeName.isEmpty()) {
                        existing.setSizeName(sizeName);
                        existing.setStock(stock);
                    }
                    if (existing.getStock() <= 0 || existing.getQuantity() < existing.getStock()) {
                        existing.setQuantity(existing.getQuantity() + 1);
                    }
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
            case "update" -> {
                String deltaStr = request.getParameter("delta");
                int delta = 1;
                try {
                    delta = Integer.parseInt(deltaStr);
                } catch (NumberFormatException ignored) {
                }
                cartService.updateQuantity(cart, productId, delta);
            }
            case "remove" ->
                cartService.removeItem(cart, productId);
            case "clear" ->
                cartService.clearCart(cart);
        }

        response.sendRedirect(request.getContextPath() + "/Cart");
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
        int  totalQty    = 0;
        for (CartItem item : cart.values()) {
            totalAmount += item.getSubtotal();
            totalQty    += item.getQuantity();
        }

        // Tính giảm giá nếu có mã
        String discountCode  = request.getParameter("discountCode");
        long   discountAmount = 0;
        if (discountCode != null && !discountCode.isBlank()) {
            try {
                java.math.BigDecimal total = new java.math.BigDecimal(totalAmount);
                services.PromotionService ps = new services.PromotionService();
                discountAmount = ps.calculateDiscount(discountCode.trim(), total).longValue();
            } catch (Exception ignored) {}
        }
        long finalAmount = Math.max(totalAmount - discountAmount, 0);

        request.setAttribute("totalAmount",    totalAmount);
        request.setAttribute("discountAmount", discountAmount);
        request.setAttribute("finalAmount",    finalAmount);
        request.setAttribute("totalQty",       totalQty);
        request.setAttribute("discountCode",   discountCode != null ? discountCode : "");
        request.setAttribute("orderNote",      request.getParameter("orderNote") != null ? request.getParameter("orderNote") : "");
        request.setAttribute("cartItems",      cart.values());
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

        // ── Lấy customerId từ session (giữ nguyên logic cũ) ───────────────
        String customerIdStr = null;
        Object currentUser = session.getAttribute("currentUser");
        if (currentUser instanceof UserLoginDTO user) {
            int accountId = user.getAccountId();
            dao.OrderDAO orderDAO = new dao.OrderDAO();
            int customerId = orderDAO.getCustomerIdByAccountId(accountId);
            if (customerId > 0) {
                customerIdStr = String.valueOf(customerId);
            }
        }

        String note = request.getParameter("note");

        // ── Lấy phương thức thanh toán từ form, mặc định COD ─────────────
        String paymentMethod = request.getParameter("paymentMethod");
        if (paymentMethod == null || paymentMethod.isBlank()
                || (!"COD".equals(paymentMethod) && !"MOMO".equals(paymentMethod))) {
            paymentMethod = "COD";
        }

        // ── Tạo đơn hàng trong DB ─────────────────────────────────────────
        // CartService sẽ KHÔNG clear cart nếu là VNPAY (clear khi confirm)
        String orderCode = cartService.checkout(cart, note, customerIdStr, paymentMethod);

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
}
