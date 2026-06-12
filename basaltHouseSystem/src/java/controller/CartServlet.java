package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import model.CartItem;
import services.CartService;

public class CartServlet extends HttpServlet {

    private final CartService cartService = new CartService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
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

        String productId = request.getParameter("productId");

        switch (action) {
            case "add" -> {
                String productName = request.getParameter("productName");
                String priceStr = request.getParameter("price");
                int price = 0;
                try {
                    
                    price = new java.math.BigDecimal(priceStr.trim()).intValue();
                } catch (Exception ignored) {}

                cartService.addProduct(cart, productId, productName, price);

               
                String redirectTarget = request.getParameter("redirect");
                if ("cart".equals(redirectTarget)) {
                    response.sendRedirect(request.getContextPath() + "/Cart");
                    return;
                }

                // Redirect về trang trước đó, thêm param addSuccess để hiển thị toast
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
                } catch (NumberFormatException ignored) {}

                cartService.updateQuantity(cart, productId, delta);
            }
            case "remove" -> {
                cartService.removeItem(cart, productId);
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

    private void handleCheckout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        @SuppressWarnings("unchecked")
        Map<String, CartItem> cart = (Map<String, CartItem>) session.getAttribute("cart");
        
        if (cart == null || cart.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/Cart?error=empty");
            return;
        }

        String note = request.getParameter("note");
        String orderCode = cartService.checkout(cart, note);

        if (orderCode != null) {
            response.sendRedirect(request.getContextPath() + "/Cart?checkoutSuccess=1&code=" + orderCode);
        } else {
            response.sendRedirect(request.getContextPath() + "/Cart?error=checkout_failed");
        }
    }
}
