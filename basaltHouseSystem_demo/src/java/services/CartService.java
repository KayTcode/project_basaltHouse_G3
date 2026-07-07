package services;

import dao.SizeDAO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.CartItem;
import model.Order;
import model.OrderDetail;

public class CartService {

    private final OnlineOrderService onlineOrderService = new OnlineOrderService();

    public void addProduct(Map<String, CartItem> cart, String productId, String productName, int price) {
        if (productId == null || productId.trim().isEmpty()) {
            return;
        }
        CartItem item = cart.get(productId);
        if (item == null) {
            cart.put(productId, new CartItem(productId, productName, price, 1));
        } else {
            item.setQuantity(item.getQuantity() + 1);
        }
    }

    public void updateQuantity(Map<String, CartItem> cart, String cartKey, int delta) {
        if (cartKey == null || cartKey.trim().isEmpty()) {
            return;
        }
        CartItem item = cart.get(cartKey);
        if (item != null) {
            int newQty = item.getQuantity() + delta;
            if (newQty <= 0) {
                cart.remove(cartKey);
                return;
            }
            if (item.getStock() > 0 && newQty > item.getStock()) {
                return;
            }
            item.setQuantity(newQty);
        }
    }

    public void removeItem(Map<String, CartItem> cart, String cartKey) {
        if (cartKey == null || cartKey.trim().isEmpty()) {
            return;
        }
        cart.remove(cartKey);
    }

    public void clearCart(Map<String, CartItem> cart) {
        if (cart != null) {
            cart.clear();
        }
    }

    public String checkout(Map<String, CartItem> cart, String note, String customerIdStr, String discountCode, String deliveryAddress, String paymentMethod, String deliveryNote) {
        if (cart == null || cart.isEmpty()) {
            return null;
        }
        if (!"COD".equals(paymentMethod) && !"MOMO".equals(paymentMethod)) {
            paymentMethod = "COD";
        }
        // Tra sizeId từ sizeName bằng HashMap<sizeId, sizeName> → đảo ngược thành HashMap<sizeName, sizeId>
        SizeDAO sizeDAO = new SizeDAO();
        HashMap<Integer, String> sizeMap = sizeDAO.getSize();
        HashMap<String, Integer> sizeNameToId = new HashMap<>();
        for (Map.Entry<Integer, String> e : sizeMap.entrySet()) {
            sizeNameToId.put(e.getValue().toLowerCase(), e.getKey());
        }

        // Tính tổng tiền gốc
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cart.values()) {
            total = total.add(new BigDecimal(item.getPrice()).multiply(new BigDecimal(item.getQuantity())));
        }

        // Tính discountAmount qua PromotionService
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (discountCode != null && !discountCode.trim().isEmpty()) {
            PromotionService promotionService = new PromotionService();
            discountAmount = promotionService.calculateDiscount(discountCode.trim(), total);
        }
        BigDecimal finalAmount = total.subtract(discountAmount);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        Order order = new Order();
        order.setOrderType("Online");
        order.setOrderStatus("Preparing");
        order.setPaymentStatus("Unpaid");
        order.setPaymentMethod(paymentMethod);
        order.setTableName("Online");
        order.setNote((note != null && !note.trim().isEmpty()) ? note : null);

        // Lưu địa chỉ giao hàng vào bảng OrderAddresses
        if (deliveryAddress != null && !deliveryAddress.trim().isEmpty()) {

            String[] parts = deliveryAddress.split(" \\| ", 3);
            String recipientName = parts.length > 0 ? parts[0].trim() : "";
            String recipientPhone = parts.length > 1 ? parts[1].trim() : "";
            String addressDetail = parts.length > 2 ? parts[2].trim() : deliveryAddress;

            model.OrderAddress addr = new model.OrderAddress();
            addr.setRecipientName(recipientName);
            addr.setRecipientPhone(recipientPhone);
            addr.setAddressDetail(addressDetail);
            addr.setNote(deliveryNote);
            addr.setZoneId(1);
            if (customerIdStr != null && !customerIdStr.trim().isEmpty()) {
                try {
                    addr.setCustomerId(Integer.parseInt(customerIdStr));
                } catch (Exception ignored) {
                }
            }

            int orderAddressId = new dao.OrderAddressDAO().insertOrderAddress(addr);
            System.out.println("[CartService] insertOrderAddress → id=" + orderAddressId);
            if (orderAddressId > 0) {
                order.setOrderAddressId(orderAddressId);
            }
        }

        order.setTotalAmount(total);
        order.setDiscountAmount(discountAmount);
        order.setFinalAmount(finalAmount);
        if (customerIdStr != null && !customerIdStr.trim().isEmpty()) {
            try {
                order.setCustomerId(Integer.parseInt(customerIdStr));
            } catch (NumberFormatException ignored) {
            }
        }

        // Build List<OrderDetail> trực tiếp từ CartItem
        List<OrderDetail> details = new ArrayList<>();
        for (CartItem item : cart.values()) {
            int productId = -1;
            try {
                productId = Integer.parseInt(item.getProductId());
            } catch (Exception ignored) {
            }
            if (productId <= 0) {
                continue;
            }

            String sizeName = (item.getSizeName() != null && !item.getSizeName().trim().isEmpty())
                    ? item.getSizeName().toLowerCase() : "m";
            int sizeId = sizeNameToId.getOrDefault(sizeName, -1);
            if (sizeId <= 0) {
                continue;
            }

            OrderDetail od = new OrderDetail();
            od.setProductId(productId);
            od.setSizeId(sizeId);
            od.setQuantity(item.getQuantity());
            od.setUnitPrice(new BigDecimal(item.getPrice()));
            details.add(od);
        }

        int orderId = onlineOrderService.createOnlineOrderFromCart(order, details);
        System.out.println("[CartService] checkout → orderId=" + orderId + ", details=" + details.size()
                + ", discount=" + discountAmount);

        if (orderId > 0) {
            cart.clear();
            return "BH-" + orderId;
        }
        return null;
    }

    public String checkout(Map<String, CartItem> cart, String note) {
        return checkout(cart, note, null, null, null, "COD", null);
    }
}
