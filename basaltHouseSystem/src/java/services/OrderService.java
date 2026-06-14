package services;

import dao.DiscountCodeDAO;
import dao.IngredientDAO;
import dao.OrderDAO;
import dao.ProductDAO;
import dao.RecipeDAO;
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
import model.Recipe;

// Tạo tầng Service này để kéo logic ra khỏi Controller (Giúp Controller mỏng và sạch hơn).
// File này xử lý việc dịch chuỗi giỏ hàng, gọi DB lưu đơn hàng, và xử lý nghiệp vụ trừ kho khi cập nhật trạng thái đơn.
public class OrderService {

    public int createOfflineOrder(String cartData, String totalAmountStr, String discountAmountStr, String finalAmountStr, 
                                  String paymentMethod, String tableName, String note, 
                                  String customerIdStr, String discountCode) {
                                      
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal finalAmount = BigDecimal.ZERO;
        try {
            if (totalAmountStr != null) totalAmount = new BigDecimal(totalAmountStr);
            if (discountAmountStr != null) discountAmount = new BigDecimal(discountAmountStr);
            if (finalAmountStr != null) finalAmount = new BigDecimal(finalAmountStr);
        } catch (Exception e) {}

        Order order = new Order();
        order.setOrderType("Offline");
        order.setOrderStatus("Preparing");
        order.setPaymentStatus("Paid");
        order.setPaymentMethod(paymentMethod != null && !paymentMethod.isEmpty() ? paymentMethod : "Cash");
        order.setTableName(tableName != null && !tableName.isEmpty() ? tableName : "Walk-in");
        order.setNote(note);
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setFinalAmount(finalAmount);

        if (customerIdStr != null && !customerIdStr.trim().isEmpty()) {
            try { order.setCustomerId(Integer.parseInt(customerIdStr)); } catch (Exception e) {}
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
        SizeDAO sDao = new SizeDAO();
        HashMap<Integer, Product> products = pDao.getProduct();
        HashMap<Integer, String> sizes = sDao.getSize();

        if (cartData != null && !cartData.isEmpty()) {
            // cartData format: name,size,qty,unitPrice|name,size,qty,unitPrice
            String[] items = cartData.split("\\|");
            for (String item : items) {
                if (item.trim().isEmpty()) continue;
                String[] parts = item.split(",");
                if (parts.length >= 4) {
                    String name = parts[0];
                    String sizeName = parts[1];
                    int qty = Integer.parseInt(parts[2]);
                    BigDecimal unitPrice = new BigDecimal(parts[3]);

                    int productId = -1;
                    for (Map.Entry<Integer, Product> entry : products.entrySet()) {
                        if (entry.getValue().getProductName().equalsIgnoreCase(name)) {
                            productId = entry.getKey();
                            break;
                        }
                    }

                    int sizeId = -1;
                    for (Map.Entry<Integer, String> entry : sizes.entrySet()) {
                        if (entry.getValue().equalsIgnoreCase(sizeName)) {
                            sizeId = entry.getKey();
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

    public void updateOrderStatus(int orderId, String action) throws Exception {
        OrderDAO orderDAO = new OrderDAO();
        
        if (action.equals("start")) {
            orderDAO.updateOrderStatus(orderId, "In_Progress");
        } else if (action.equals("ready")) {
            orderDAO.updateOrderStatus(orderId, "Ready");
        } else if (action.equals("complete")) {
            orderDAO.updateOrderStatus(orderId, "Completed");
            
            // Inventory Deduction Logic
            List<OrderDetail> details = orderDAO.getOrderDetailsByOrderId(orderId);
            RecipeDAO recipeDAO = new RecipeDAO();
            IngredientDAO ingredientDAO = new IngredientDAO();
            HashMap<Integer, HashMap<Integer, List<Recipe>>> recipes = recipeDAO.getRecipeMap();
            
            for (OrderDetail d : details) {
                int productId = d.getProductId();
                int sizeId = d.getSizeId();
                int qty = d.getQuantity();
                
                if (recipes.containsKey(productId) && recipes.get(productId).containsKey(sizeId)) {
                    List<Recipe> recipeComponents = recipes.get(productId).get(sizeId);
                    for (Recipe r : recipeComponents) {
                        BigDecimal totalNeeded = r.getQuantityNeeded().multiply(new BigDecimal(qty));
                        ingredientDAO.updateIngredientQuantity(r.getIngredientId(), totalNeeded);
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid action: " + action);
        }
    }
}
