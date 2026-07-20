package controller;

import dao.AdminDeliveryZoneDAO;
import dto.UserLoginDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.CartItem;
import model.DeliveryZone;
import services.CartService;

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
        // Build stockMap
        request.setAttribute("stockMap", cartService.getStockMap());

        request.setAttribute("cartItems", cart.values());
        request.setAttribute("totalAmount", totalAmount);
        request.setAttribute("totalQty", totalQty);

        // ── Đọc mã voucher được chọn từ trang Voucher / HomePage (one-shot) ──
        String pendingVoucher = (String) session.getAttribute(ApplyVoucherServlet.PENDING_VOUCHER_KEY);
        if (pendingVoucher != null && !pendingVoucher.isEmpty()) {
            request.setAttribute("discountCode", pendingVoucher);
            // Xóa khỏi session sau khi đã truyền sang Cart (dùng một lần)
            session.removeAttribute(ApplyVoucherServlet.PENDING_VOUCHER_KEY);
        }

        // Load vouchers (public + personal)
        Integer accountId = null;
        if (session.getAttribute("currentUser") instanceof UserLoginDTO) {
            accountId = ((UserLoginDTO) session.getAttribute("currentUser")).getAccountId();
        }
        request.setAttribute("myVouchers", cartService.getAvailableVouchers(accountId));

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
                String productId = cartKey;
                String productName = request.getParameter("productName");
                String priceStr = request.getParameter("price");
                String sizeIdStr = request.getParameter("sizeId");
                String qtyStr = request.getParameter("quantity");

                int price = 0;
                try {
                    price = new java.math.BigDecimal(priceStr.trim()).intValue();
                } catch (Exception ignored) {
                }

                int sizeId = -1;
                if (sizeIdStr != null && !sizeIdStr.trim().isEmpty()) {
                    try {
                        sizeId = Integer.parseInt(sizeIdStr.trim());
                    } catch (Exception ignored) {
                    }
                }

                int requestedQty = 1;
                if (qtyStr != null && !qtyStr.trim().isEmpty()) {
                    try {
                        requestedQty = Integer.parseInt(qtyStr.trim());
                        if (requestedQty < 1) requestedQty = 1;
                    } catch (NumberFormatException ignored) {
                    }
                }

                cartService.addProduct(cart, productId, productName, price, sizeId, requestedQty);

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

        // Lấy thông tin thành viên và tính chiết khấu hạng thành viên
        double memberDiscountPercent = 0.0;
        long memberDiscountAmount = 0;
        String memberTier = "";
        if (session.getAttribute("currentUser") instanceof UserLoginDTO) {
            int aid = ((UserLoginDTO) session.getAttribute("currentUser")).getAccountId();
            model.Customer member = new dao.DiscountCodeDAO().getCustomerMembershipByAccountId(aid);
            if (member != null) {
                memberTier = member.getRankName();
                if (member.getDiscountValue() != null) {
                    memberDiscountPercent = member.getDiscountValue().doubleValue();
                    memberDiscountAmount = Math.round(totalAmount * (memberDiscountPercent / 100.0));
                }
            }
        }

        // Tính giảm giá nếu có mã voucher
        String discountCode = request.getParameter("discountCode");
        Map<String, Object> discountResult = cartService.applyDiscountResult(discountCode, cart);
        long couponDiscountAmount = 0;
        if (Boolean.TRUE.equals(discountResult.get("success"))) {
            couponDiscountAmount = ((BigDecimal) discountResult.get("discountAmount")).longValue();
        }

        long totalDiscountAmount = memberDiscountAmount + couponDiscountAmount;
        long finalAmount = Math.max(totalAmount - totalDiscountAmount, 0);

        request.setAttribute("totalAmount", totalAmount);
        request.setAttribute("memberDiscountPercent", memberDiscountPercent);
        request.setAttribute("memberDiscountAmount", memberDiscountAmount);
        request.setAttribute("memberTier", memberTier != null ? memberTier : "");
        request.setAttribute("couponDiscountAmount", couponDiscountAmount);
        request.setAttribute("totalDiscountAmount", totalDiscountAmount);
        request.setAttribute("finalAmount", finalAmount);
        request.setAttribute("totalQty", totalQty);
        request.setAttribute("discountCode", discountCode != null ? discountCode : "");
        request.setAttribute("orderNote", request.getParameter("orderNote") != null ? request.getParameter("orderNote") : "");
        request.setAttribute("cartItems", cart.values());

        // Lấy danh sách vùng giao hàng active từ DB
        List<DeliveryZone> activeZones = new AdminDeliveryZoneDAO().getZones(null, null, "true");
        request.setAttribute("activeZones", activeZones);

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
            int aid = ((UserLoginDTO) currentUser).getAccountId();
            int cid = cartService.resolveCustomerId(aid);
            if (cid > 0) customerIdStr = String.valueOf(cid);
        }

        String orderNote = request.getParameter("orderNote");    // ghi chú từ Cart.jsp → Orders.Note
        String deliveryNote = request.getParameter("deliveryNote"); // ghi chú từ Checkout.jsp → OrderAddresses.Note
        String discountCode = request.getParameter("discountCode");
        String deliveryAddress = request.getParameter("deliveryAddress");
        String paymentMethod = request.getParameter("paymentMethod");
        // ── Lấy phương thức thanh toán từ form, mặc định COD ─────────────
        if (paymentMethod == null || paymentMethod.trim().isEmpty()
                || (!"COD".equals(paymentMethod) && !"MOMO".equals(paymentMethod))) {
            paymentMethod = "COD";
        }

        // ── Tạo đơn hàng trong DB ─────────────────────────────────────────
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
        HttpSession session = request.getSession(false);
        @SuppressWarnings("unchecked")
        Map<String, CartItem> cart = session != null
                ? (Map<String, CartItem>) session.getAttribute("cart") : null;

        Map<String, Object> result = cartService.applyDiscountResult(code, cart);

        if (!Boolean.TRUE.equals(result.get("success"))) {
            String error = (String) result.getOrDefault("error", "Mã không hợp lệ.");
            response.getWriter().write("{\"success\":false,\"error\":\"" + error + "\"}");
            return;
        }

        BigDecimal discountAmt = (BigDecimal) result.get("discountAmount");
        BigDecimal finalAmt    = (BigDecimal) result.get("finalAmount");
        String codeName        = (String)     result.get("codeName");

        response.getWriter().write(String.format(
                "{\"success\":true,\"codeName\":\"%s\",\"discountAmount\":%s,\"finalAmount\":%s}",
                codeName.replace("\"", "\\\""),
                discountAmt.toPlainString(),
                finalAmt.toPlainString()
        ));
    }
}
