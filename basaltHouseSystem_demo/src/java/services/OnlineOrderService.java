package services;

import dao.DiscountCodeDAO;
import dao.OrderDAO;
import dao.ProductDAO;
import dao.SizeDAO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.DiscountCode;
import model.Order;
import model.OrderDetail;
import model.Product;


public class OnlineOrderService {

    public int createOnlineOrder(String cartData, String totalAmountStr, String discountAmountStr,
            String finalAmountStr, String tableName, String note,
            String customerIdStr, String discountCode) {

        
        BigDecimal totalAmount    = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal finalAmount    = BigDecimal.ZERO;
        try {
            if (totalAmountStr    != null) totalAmount    = new BigDecimal(totalAmountStr);
            if (discountAmountStr != null) discountAmount = new BigDecimal(discountAmountStr);
            if (finalAmountStr    != null) finalAmount    = new BigDecimal(finalAmountStr);
        } catch (Exception ignored) {
        }

        Order order = new Order();
        order.setOrderType("Online");
        order.setOrderStatus("Preparing");
        order.setPaymentStatus("Unpaid");
        order.setPaymentMethod("COD");
        order.setTableName(tableName != null && !tableName.isEmpty() ? tableName : "Online");
        order.setNote(note);
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setFinalAmount(finalAmount);

        if (customerIdStr != null && !customerIdStr.trim().isEmpty()) {
            try {
                order.setCustomerId(Integer.parseInt(customerIdStr));
            } catch (Exception ignored) {
            }
        }
     
        if (discountCode != null && !discountCode.trim().isEmpty()) {
            DiscountCodeDAO dcDao = new DiscountCodeDAO();
            DiscountCode dto = dcDao.checkDiscountCode(discountCode);
            if (dto != null) {
                order.setDiscountId(dto.getDiscountId());
            }
        }

        List<OrderDetail> details = new ArrayList<>();
        ProductDAO pDao = new ProductDAO();
        SizeDAO    sDao = new SizeDAO();
        HashMap<Integer, Product> products = pDao.getProduct();
        HashMap<Integer, String>  sizes    = sDao.getSize();

        if (cartData != null && !cartData.isEmpty()) {
            String[] items = cartData.split("\\|");
            for (String item : items) {
                if (item.trim().isEmpty()) continue;
                String[] parts = item.split(",");
                if (parts.length >= 4) {
                    String     name      = parts[0];
                    String     sizeName  = parts[1];
                    int        qty       = Integer.parseInt(parts[2]);
                    BigDecimal unitPrice = new BigDecimal(parts[3]);

                    int productId = -1;
                    for (Map.Entry<Integer, Product> e : products.entrySet()) {
                        if (e.getValue().getProductName().equalsIgnoreCase(name)) {
                            productId = e.getKey();
                            break;
                        }
                    }

                    int sizeId = -1;
                    for (Map.Entry<Integer, String> e : sizes.entrySet()) {
                        if (e.getValue().equalsIgnoreCase(sizeName)) {
                            sizeId = e.getKey();
                            break;
                        }
                    }

                    if (productId != -1 && sizeId != -1) {
                        OrderDetail od = new OrderDetail();
                        od.setProductId(productId);
                        od.setSizeId(sizeId);
                        od.setQuantity(qty);
                        od.setUnitPrice(unitPrice);
                        details.add(od);
                    }
                }
            }
        }

        OrderDAO orderDAO = new OrderDAO();
        return orderDAO.insertOfflineOrder(order, details);
    }

    public int createOnlineOrderFromCart(model.Order order, java.util.List<model.OrderDetail> details) {
        OrderDAO orderDAO = new OrderDAO();
        int orderId = orderDAO.insertOfflineOrder(order, details);
        System.out.println("[OnlineOrderService] insertOfflineOrder → orderId=" + orderId);
        return orderId;
    }
}
