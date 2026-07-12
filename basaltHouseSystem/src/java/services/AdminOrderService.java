package services;

import dao.AdminOrderDAO;
import dto.OrderDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AdminOrderService — tầng Business Logic giữa Servlet và DAO.
 * Pattern giống AdminProductService.
 */
public class AdminOrderService {

    private final AdminOrderDAO orderDAO = new AdminOrderDAO();

    // ══════════════════════════════════════════════════════════════════
    // Lấy toàn bộ dữ liệu cho trang Danh sách Đơn hàng (Admin)
    // Trả về Map để Servlet đẩy thẳng vào request attribute
    // ══════════════════════════════════════════════════════════════════
    public Map<String, Object> getOrderDashboardData(
            String search, String orderType, String orderStatus,
            String paymentStatus, String pageStr, int pageSize) {

        Map<String, Object> data = new HashMap<>();

        // 1. Xử lý phân trang an toàn
        int page = 1;
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Math.max(1, Integer.parseInt(pageStr));
            } catch (NumberFormatException ignored) {}
        }
        int offset = (page - 1) * pageSize;

        // 2. Lấy danh sách đơn (JOIN 10 bảng, đã phân trang)
        List<OrderDTO> orders = orderDAO.getOrdersWithFullDetails(
                search, orderType, orderStatus, paymentStatus, offset, pageSize);

        // 3. Tổng số trang
        int total      = orderDAO.countOrders(search, orderType, orderStatus, paymentStatus);
        int totalPages = Math.max(1, (int) Math.ceil((double) total / pageSize));

        // 4. Đóng gói cho JSP
        data.put("orders",        orders);
        data.put("currentPage",   page);
        data.put("totalPages",    totalPages);
        data.put("totalRecords",  total);

        // Giữ lại giá trị bộ lọc để JSP fill vào ô tìm kiếm/select
        data.put("oldSearch",        search        != null ? search        : "");
        data.put("oldOrderType",     orderType     != null ? orderType     : "");
        data.put("oldOrderStatus",   orderStatus   != null ? orderStatus   : "");
        data.put("oldPaymentStatus", paymentStatus != null ? paymentStatus : "");

        // 5. Thẻ KPI (không bị ảnh hưởng bởi filter đang chọn)
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total",      orderDAO.countOrdersByStatus(null));
        stats.put("pending",    orderDAO.countOrdersByStatus("Pending"));
        stats.put("delivering", orderDAO.countOrdersByStatus("Delivering"));
        stats.put("done",       orderDAO.countOrdersByStatus("Done"));
        stats.put("cancelled",  orderDAO.countOrdersByStatus("Cancelled"));
        data.put("stats", stats);

        return data;
    }

    // ══════════════════════════════════════════════════════════════════
    // Lấy chi tiết 1 đơn hàng để hiển thị trong modal/trang detail
    // ══════════════════════════════════════════════════════════════════
    public OrderDTO getOrderDetail(String orderIdStr) {
        try {
            int id = Integer.parseInt(orderIdStr);
            return orderDAO.getOrderDetail(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // Cập nhật trạng thái đơn hàng
    // ══════════════════════════════════════════════════════════════════
    public boolean processUpdateStatus(String orderIdStr, String newStatus) {
        try {
            int id = Integer.parseInt(orderIdStr);
            return orderDAO.updateOrderStatus(id, newStatus);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // Xóa mềm đơn hàng
    // ══════════════════════════════════════════════════════════════════
    public boolean processDeleteOrder(String orderIdStr) {
        try {
            int id = Integer.parseInt(orderIdStr);
            return orderDAO.softDeleteOrder(id);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
