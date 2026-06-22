package controller;

import dao.SizeDAO;
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

        // ── Tính stock hiện tại theo từng productId-size ──
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
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                // Key = "productId_sizeName" để tách riêng cùng SP khác size
                String key = productId + "_" + sizeName;
                CartItem existing = cart.get(key);
                if (existing == null) {
                    if (stock > 0) {
                        CartItem newItem = new CartItem(productId, productName, price, 1, sizeName, stock);
                        newItem.setCartKey(key);
                        cart.put(key, newItem);
                    }
                } else {
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

                cartService.updateQuantity(cart, cartKey, delta);
            }
            case "remove" -> {
                cartService.removeItem(cart, cartKey);
            }
            case "clear" -> {
                cartService.clearCart(cart);
            }
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
        if (discountCode != null && !discountCode.isBlank()) {
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
        if (currentUser instanceof dto.UserLoginDTO) {
            int accountId = ((dto.UserLoginDTO) currentUser).getAccountId();
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
        String orderCode = cartService.checkout(cart, orderNote, customerIdStr, discountCode, deliveryAddress, paymentMethod, deliveryNote);

        if (orderCode != null) {
            response.sendRedirect(request.getContextPath() + "/Cart?checkoutSuccess=1&code=" + orderCode);
        } else {
            response.sendRedirect(request.getContextPath() + "/Cart?error=checkout_failed");
        }
    }
}
