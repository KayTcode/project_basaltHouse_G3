package services;

import dao.BillDAO;
import dao.DiscountCodeDAO;
import dao.OrderDAO;
import dao.ProductDAO;
import dao.SizeDAO;
import dao.TableSessionDAO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Bill;
import model.DiscountCode;
import model.Order;
import model.OrderDetail;
import model.Product;
import model.TableSession;

public class OrderService {

    private final OrderDAO dao = new OrderDAO();
    private final TableSessionDAO sesioneDAO = new TableSessionDAO();

    public int createOfflineOrder(String cartData, String totalAmountStr, String discountAmountStr, String finalAmountStr,
            String paymentMethod, String tableName, String note,
            String customerIdStr, String discountCode, String tableIdStr, Integer cashierId) throws Exception {

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal finalAmount = BigDecimal.ZERO;
        try {
            if (totalAmountStr != null) {
                totalAmount = new BigDecimal(totalAmountStr);
            }
            if (discountAmountStr != null) {
                discountAmount = new BigDecimal(discountAmountStr);
            }
            if (finalAmountStr != null) {
                finalAmount = new BigDecimal(finalAmountStr);
            }
        } catch (Exception e) {
        }

        Order order = new Order();
        order.setOrderType("POS");
        order.setOrderStatus("Preparing");
        order.setPaymentStatus("Paid");
        order.setPaymentMethod(paymentMethod != null && !paymentMethod.isEmpty() ? paymentMethod : "Cash");
        order.setTableName(tableName != null && !tableName.isEmpty() ? tableName : "Walk-in");
        order.setNote(note);
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setFinalAmount(finalAmount);
        if (cashierId != null) {
            order.setCashierId(cashierId);
        }

        if (tableIdStr != null && !tableIdStr.trim().isEmpty()) {
            try {
                int tableId = Integer.parseInt(tableIdStr);
                dao.TableSessionDAO tsDao = new dao.TableSessionDAO();
                int sessionId = tsDao.getActiveSessionId(tableId);
                if (sessionId == -1) {
                    tsDao.createSession(tableId, tableName != null ? tableName : "", 1, null);
                    sessionId = tsDao.getActiveSessionId(tableId);
                }
                if (sessionId != -1) {
                    order.setTableSessionId(sessionId);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (customerIdStr != null && !customerIdStr.trim().isEmpty()) {
            try {
                order.setCustomerId(Integer.parseInt(customerIdStr));
            } catch (Exception e) {
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
        SizeDAO sDao = new SizeDAO();
        HashMap<Integer, Product> products = pDao.getProduct();
        HashMap<Integer, String> sizes = sDao.getSize();

        if (cartData != null && !cartData.isEmpty()) {

            String[] items = cartData.split("\\|");
            for (String item : items) {
                if (item.trim().isEmpty()) {
                    continue;
                }
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
        int newOrderId = orderDAO.insertOfflineOrder(order, details);

        if (newOrderId != -1) {

            if (!details.isEmpty()) {
                new StockService().updateStockForOrder(details);
            }

            Bill bill = new Bill();
            bill.setOrderId(newOrderId);
            bill.setCashierId(order.getCashierId());
            bill.setSubTotal(order.getTotalAmount());
            bill.setDiscountAmount(order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO);
            bill.setFinalAmount(order.getFinalAmount());
            bill.setPaymentMethod(order.getPaymentMethod());
            bill.setNote(order.getNote());
            bill.setPrintedAt(LocalDateTime.now());
            if (tableIdStr != null && !tableIdStr.trim().isEmpty()) {
                try {
                    bill.setTableId(Integer.parseInt(tableIdStr));
                } catch (NumberFormatException e) {

                }
            }
            try {
                boolean billOk = new BillDAO().insertBill(bill);
                System.out.println("[OrderService] insertBill result=" + billOk + " orderId=" + newOrderId + " cashierId=" + order.getCashierId());
            } catch (Exception billEx) {
                System.err.println("[OrderService] insertBill FAILED orderId=" + newOrderId + " error=" + billEx.getMessage());
                billEx.printStackTrace();
            }
        }

        return newOrderId;
    }

    public void updateOrderStatus(int orderId, String action) throws Exception {
        updateOrderStatus(orderId, action, null);
    }

    public void updateOrderStatus(int orderId, String action, Integer cashierId) throws Exception {
        OrderDAO orderDAO = new OrderDAO();

        if (action.equals("confirm")) {

            Order current = orderDAO.getOrderById(orderId);
            if (current != null && "Pending".equalsIgnoreCase(current.getOrderStatus())) {
                orderDAO.updateOrderStatus(orderId, "Preparing");

                List<OrderDetail> details = orderDAO.getOfflineOrderDetailsByOrderId(orderId);
                if (!details.isEmpty()) {
                    new StockService().updateStockForOrder(details);
                }

                Bill bill = new Bill();
                bill.setOrderId(orderId);
                if (cashierId != null) {
                    bill.setCashierId(cashierId);
                } else if (current.getCashierId() != null && current.getCashierId() > 0) {
                    bill.setCashierId(current.getCashierId());
                } else {
                    bill.setCashierId(0);
                }
                bill.setSubTotal(current.getTotalAmount());
                bill.setDiscountAmount(current.getDiscountAmount() != null ? current.getDiscountAmount() : BigDecimal.ZERO);
                bill.setFinalAmount(current.getFinalAmount());
                bill.setPaymentMethod(current.getPaymentMethod());
                bill.setNote(current.getNote());
                bill.setPrintedAt(LocalDateTime.now());
                try {
                    new BillDAO().insertBill(bill);
                } catch (Exception e) {
                    System.err.println("Failed to insert bill for online order " + orderId + ": " + e.getMessage());
                }
            } else {
                orderDAO.updateOrderStatus(orderId, "Preparing");
            }
        } else if (action.equals("start")) {

            orderDAO.updateOrderStatus(orderId, "In_Progress");
        } else if (action.equals("ready")) {

            orderDAO.updateOrderStatus(orderId, "Ready");
        } else if (action.equals("complete")) {
            Order order = orderDAO.getOrderById(orderId);
            if (order != null && "Online".equalsIgnoreCase(order.getOrderType())) {
                orderDAO.updateOrderStatus(orderId, "Waiting_Shipper");
            } else {
                orderDAO.updateOrderStatus(orderId, "Completed");
            }
        } else {
            throw new IllegalArgumentException("Invalid action: " + action);
        }
    }

    public List<Order> getOfflineOrdersBySessionId(int sessionId) {
        return dao.getOfflineOrdersBySessionId(sessionId);
    }

    public Order getOfflineOrderById(int orderId) {
        return dao.getOfflineOrderById(orderId);
    }

    public List<OrderDetail> getOfflineOrderDetailsByOrderId(int orderId) {
        return dao.getOfflineOrderDetailsByOrderId(orderId);
    }

    public int createOfflineOrderForSession(int sessionId) {
        // Validate: session phải tồn tại và đang ACTIVE
        TableSession session = sesioneDAO.getSessionById(sessionId);
        if (session == null) {
            System.err.println("[OrderService] Session " + sessionId + " không tồn tại.");
            return -1;
        }
        String status = session.getStatus();
        if (!"ACTIVE".equalsIgnoreCase(status) && !"Open".equalsIgnoreCase(status)) {
            System.err.println("[OrderService] Session " + sessionId + " đã đóng (status=" + status + "), không thể tạo đơn mới.");
            return -1;
        }

        Order order = new Order();
        order.setTableSessionId(sessionId);
        order.setOrderStatus("Pending");
        order.setTotalAmount(BigDecimal.ZERO);
        order.setFinalAmount(BigDecimal.ZERO);
        order.setOrderType("Dine-In");
        order.setPaymentStatus("Unpaid");
        return dao.createOfflineOrder(order);
    }

    public HashMap<String, Object> getTodaySoldProductSizeRows() {
        HashMap<String, Object> result = new HashMap<>();
        try {
            List<HashMap<String, Object>> list = dao.getTodaySoldProductSizeRows();
            if (list == null) {
                result.put("error", "Danh sách bán hàng hôm nay lỗi");
            } else {
                result.put("success", list);
            }
        } catch (Exception e) {
            result.put("error", e.getMessage());
            System.err.println(e.getMessage());
        }
        return result;
    }

    public HashMap<String, Object> getSoldProductSizeRowsByDate(LocalDate auditDate) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            List<HashMap<String, Object>> list = dao.getSoldProductSizeRowsByDate(auditDate);
            if (list == null) {
                result.put("error", "Danh sách bán hàng theo ngày lỗi");
            } else {
                result.put("success", list);
            }
        } catch (Exception e) {
            result.put("error", e.getMessage());
            System.err.println(e.getMessage());
        }
        return result;
    }

    public boolean updateOfflineOrderStatus(int orderId, String status) {
        dao.updateOrderStatus(orderId, status);
        return true;
    }

    public boolean addOfflineProductToOrder(int orderId, int productId, int sizeId, int quantity, BigDecimal unitPrice) {
        List<OrderDetail> details = dao.getOfflineOrderDetailsByOrderId(orderId);
        for (OrderDetail d : details) {
            if (d.getProductId() == productId && d.getSizeId() == sizeId) {
                int newQty = d.getQuantity() + quantity;
                boolean updated = dao.updateOrderDetailQuantity(d.getOrderDetailId(), newQty);
                if (updated) {
                    recalculateOfflineOrderTotal(orderId);
                }
                return updated;
            }
        }

        OrderDetail detail = new OrderDetail();
        detail.setOrderId(orderId);
        detail.setProductId(productId);
        detail.setSizeId(sizeId);
        detail.setQuantity(quantity);
        detail.setUnitPrice(unitPrice);

        boolean added = dao.addOrderDetail(detail);
        if (added) {
            recalculateOfflineOrderTotal(orderId);
        }
        return added;
    }

    public boolean updateOfflineDetailQuantity(int orderId, int orderDetailId, int quantity) {
        if (quantity <= 0) {
            return removeOfflineDetailFromOrder(orderId, orderDetailId);
        }
        boolean updated = dao.updateOrderDetailQuantity(orderDetailId, quantity);
        if (updated) {
            recalculateOfflineOrderTotal(orderId);
        }
        return updated;
    }

    public boolean removeOfflineDetailFromOrder(int orderId, int orderDetailId) {
        boolean deleted = dao.deleteOrderDetail(orderDetailId);
        if (deleted) {
            recalculateOfflineOrderTotal(orderId);
        }
        return deleted;
    }

    public String updateOrderType(int orderId, String newType) {
        // 1. Kiểm tra order tồn tại
        Order order = dao.getOfflineOrderById(orderId);
        if (order == null) {
            return "ERR:Order không tồn tại.";
        }

        // 2. Chỉ cho phép đổi khi chưa confirm (Pending)
        if (!"Pending".equalsIgnoreCase(order.getOrderStatus())) {
            return "ERR:Chỉ có thể thay đổi loại đơn khi đơn hàng chưa được xác nhận.";
        }

        // 3. Kiểm tra order có sản phẩm (products & quantities preserved)
        List<OrderDetail> details = dao.getOfflineOrderDetailsByOrderId(orderId);
        if (details.isEmpty()) {
            return "ERR:Đơn hàng chưa có sản phẩm.";
        }

        boolean ok = dao.updateOrderType(orderId, newType);
        return ok ? "OK" : "ERR:Không thể cập nhật loại đơn hàng.";
    }

    public boolean recalculateOfflineOrderTotal(int orderId) {
        List<OrderDetail> details = dao.getOfflineOrderDetailsByOrderId(orderId);
        BigDecimal total = BigDecimal.ZERO;
        for (OrderDetail d : details) {
            BigDecimal qty = new BigDecimal(d.getQuantity());
            total = total.add(d.getUnitPrice().multiply(qty));
        }
        return dao.updateOrderTotal(orderId, total);
    }
}
