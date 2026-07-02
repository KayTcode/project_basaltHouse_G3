package services;

import dao.DiscountCodeDAO;
import dao.OrderDAO;
import dao.SizeDAO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.CartItem;
import model.CustomerDiscountCode;
import model.Order;
import model.OrderDetail;
import model.Product;

public class CartService {

    private final OnlineOrderService onlineOrderService = new OnlineOrderService();

    public void addProduct(Map<String, CartItem> cart, String productId, String productName, int price, int sizeId, int requestedQty) {
        if (productId == null || productId.trim().isEmpty()) {
            return;
        }

        String sizeName = "";
        if (sizeId > 0) {
            try {
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
                services.StockService stockSvc = new services.StockService();
                HashMap<model.Product, HashMap<String, Integer>> rawStock = stockSvc.calculateProduct();
                for (Map.Entry<model.Product, HashMap<String, Integer>> e : rawStock.entrySet()) {
                    if (e.getKey().getProductId() == pId) {
                        stock = e.getValue().getOrDefault(sizeName, 0);
                        break;
                    }
                }
            } catch (Exception e) {
                System.err.println("Error checking stock in CartService: " + e.getMessage());
            }
        } else {
            stock = 9999;
        }

        String key = sizeName.isEmpty() ? productId : productId + "_" + sizeName;
        CartItem existing = cart.get(key);
        if (existing == null) {
            if (stock > 0) {
                int addQty = Math.min(requestedQty, stock);
                CartItem newItem = new CartItem(productId, productName, price, addQty, sizeName, stock);
                newItem.setCartKey(key);
                cart.put(key, newItem);
            }
        } else {
            int currentQty = existing.getQuantity();
            int maxQty = existing.getStock();
            int newQty = currentQty + requestedQty;
            if (maxQty > 0) {
                newQty = Math.min(newQty, maxQty);
            }
            existing.setQuantity(newQty);
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

    public HashMap<Integer, HashMap<String, Integer>> getStockMap() {
        HashMap<Integer, HashMap<String, Integer>> stockMap = new HashMap<>();
        try {
            StockService stockSvc = new StockService();
            HashMap<Product, HashMap<String, Integer>> rawStock = stockSvc.calculateProduct();
            for (Map.Entry<Product, HashMap<String, Integer>> e : rawStock.entrySet()) {
                stockMap.put(e.getKey().getProductId(), e.getValue());
            }
        } catch (Exception ignored) {}
        return stockMap;
    }

    public List<CustomerDiscountCode> getAvailableVouchers(Integer accountId) {
        List<CustomerDiscountCode> vouchers = new ArrayList<>();
        DiscountCodeDAO discountDAO = new DiscountCodeDAO();
        try {
            List<model.DiscountCode> publicList = discountDAO.getDiscountCode();
            if (publicList != null) {
                for (model.DiscountCode d : publicList) {
                    vouchers.add(new CustomerDiscountCode(
                        0, 0, d.getDiscountId(),
                        d.getDiscountPercent(), d.getDiscountAmount(),
                        d.getStartDate(), d.getEndDate(),
                        false, null, d.getDescription(),
                        d.getTotalDay(), d.getCode(), 1
                    ));
                }
            }
        } catch (Exception ignored) {}

        if (accountId != null) {
            try {
                List<CustomerDiscountCode> personal = discountDAO.getVoucherById(accountId);
                if (personal != null) vouchers.addAll(personal);
            } catch (Exception ignored) {}
        }
        return vouchers;
    }

    public Map<String, Object> applyDiscountResult(String code, Map<String, CartItem> cart) {
        Map<String, Object> result = new HashMap<>();
        if (code == null || code.trim().isEmpty()) {
            result.put("success", false);
            result.put("error", "Vui lòng nhập mã giảm giá.");
            return result;
        }

        PromotionService ps = new PromotionService();
        String checkJson = ps.checkDiscount(code.trim());

        if (checkJson.contains("\"valid\": false") || checkJson.contains("\"valid\":false")) {
            String msg = "Mã không hợp lệ hoặc đã hết hạn.";
            int msgIdx = checkJson.indexOf("\"msg\":");
            if (msgIdx >= 0) {
                int start = checkJson.indexOf('"', msgIdx + 6) + 1;
                int end   = checkJson.indexOf('"', start);
                if (start > 0 && end > start) msg = checkJson.substring(start, end);
            }
            result.put("success", false);
            result.put("error", msg);
            return result;
        }

        BigDecimal total = BigDecimal.ZERO;
        if (cart != null) {
            for (CartItem item : cart.values()) {
                total = total.add(new BigDecimal(item.getPrice())
                        .multiply(new BigDecimal(item.getQuantity())));
            }
        }
        BigDecimal discountAmt = ps.calculateDiscount(code.trim(), total);
        BigDecimal finalAmt    = total.subtract(discountAmt).max(BigDecimal.ZERO);

        result.put("success", true);
        result.put("codeName", code.trim());
        result.put("discountAmount", discountAmt);
        result.put("finalAmount", finalAmt);
        return result;
    }

    /**
     * Lấy customerId từ accountId. Trả về -1 nếu không tìm thấy.
     */
    public int resolveCustomerId(int accountId) {
        try {
            return new OrderDAO().getCustomerIdByAccountId(accountId);
        } catch (Exception ignored) {}
        return -1;
    }
}
