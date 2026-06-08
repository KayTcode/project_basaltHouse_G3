package services;

import java.util.Map;
import model.CartItem;

public class CartService {

    public void addProduct(Map<String, CartItem> cart, String productId, String productName, int price) {
        if (productId == null || productId.isBlank()) {
            return;
        }
        CartItem item = cart.get(productId);
        if (item == null) {
            cart.put(productId, new CartItem(productId, productName, price, 1));
        } else {
            item.setQuantity(item.getQuantity() + 1);
        }
    }

    public void updateQuantity(Map<String, CartItem> cart, String productId, int delta) {
        if (productId == null || productId.isBlank()) {
            return;
        }
        CartItem item = cart.get(productId);
        if (item != null) {
            item.setQuantity(item.getQuantity() + delta);
            if (item.getQuantity() <= 0) {
                cart.remove(productId);
            }
        }
    }

    public void removeItem(Map<String, CartItem> cart, String productId) {
        if (productId == null || productId.isBlank()) {
            return;
        }
        cart.remove(productId);
    }

    public void clearCart(Map<String, CartItem> cart) {
        if (cart != null) {
            cart.clear();
        }
    }

    public String checkout(Map<String, CartItem> cart, String note) {
        if (cart == null || cart.isEmpty()) {
            return null;
        }

        // Tạo mã đơn hàng mẫu để hiển thị
        String orderCode = "BH-" + System.currentTimeMillis() % 1000000;

        cart.clear();
        return orderCode;
    }
}
