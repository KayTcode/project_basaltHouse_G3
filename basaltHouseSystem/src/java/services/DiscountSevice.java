package services;

import dao.DiscountDAO;
import dao.OrderDAO;
import model.DiscountCode;
import model.Order;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Service xử lý logic áp dụng mã giảm giá cho đơn hàng.
 
 */
public class DiscountSevice {
    private DiscountDAO discountDAO = new DiscountDAO();
    private OrderDAO orderDAO = new OrderDAO();

    /**
     * Áp dụng mã giảm giá cho một đơn hàng.
     * Trả về chuỗi bắt đầu bằng "Thành công:" hoặc "Lỗi:".
     */
    public String applyDiscountToOrder(int orderId, String code) {
        // 1. Lấy thông tin đơn hàng
        Order order = orderDAO.getOrderById(orderId);
        if (order == null) {
            return "Lỗi: Không tìm thấy đơn hàng mã số " + orderId;
        }

        // 2. Kiểm tra đơn hàng chưa thanh toán
        if ("Paid".equalsIgnoreCase(order.getPaymentStatus())) {
            return "Lỗi: Đơn hàng này đã được thanh toán, không thể áp dụng mã giảm giá!";
        }
        if ("Done".equalsIgnoreCase(order.getOrderStatus())) {
            return "Lỗi: Đơn hàng này đã hoàn thành, không thể áp dụng mã giảm giá!";
        }

        // 3. Kiểm tra tổng tiền hợp lệ
        BigDecimal totalAmount = order.getTotalAmount();
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return "Lỗi: Tổng tiền đơn hàng không hợp lệ!";
        }

        // 4. Lấy thông tin mã giảm giá từ Database
        DiscountCode discount = discountDAO.getDiscountByCode(code);
        if (discount == null) {
            return "Lỗi: Mã giảm giá không tồn tại hoặc đã bị xóa!";
        }

        // 5. Kiểm tra trạng thái kích hoạt
        if (!discount.isIsActive()) {
            return "Lỗi: Mã giảm giá này đang bị tạm dừng áp dụng!";
        }

        // 6. Kiểm tra thời hạn sử dụng
        LocalDateTime now = LocalDateTime.now();
        if (discount.getStartDate() != null && now.isBefore(discount.getStartDate())) {
            return "Lỗi: Chương trình khuyến mãi này chưa bắt đầu!";
        }
        if (discount.getEndDate() != null && now.isAfter(discount.getEndDate())) {
            return "Lỗi: Mã giảm giá này đã hết hạn sử dụng!";
        }

        // 7. Tính toán số tiền được giảm
        BigDecimal moneySaved = BigDecimal.ZERO;

        // Trường hợp 1: Giảm theo số tiền cố định
        if (discount.getDiscountAmount() != null && discount.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            moneySaved = discount.getDiscountAmount();
        }
        // Trường hợp 2: Giảm theo phần trăm
        else if (discount.getDiscountPercent() != null && discount.getDiscountPercent().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal percent = discount.getDiscountPercent().divide(new BigDecimal("100"));
            moneySaved = totalAmount.multiply(percent).setScale(0, RoundingMode.HALF_UP);
        } else {
            return "Lỗi: Mã giảm giá không có giá trị hợp lệ!";
        }

        // 8. Đảm bảo tiền giảm không vượt quá tổng đơn hàng
        if (moneySaved.compareTo(totalAmount) > 0) {
            moneySaved = totalAmount;
        }

        // 9. Tính số tiền cuối cùng phải trả
        BigDecimal finalAmount = totalAmount.subtract(moneySaved);

        // 10. Cập nhật vào Database
        boolean isUpdated = orderDAO.updateOrderDiscount(orderId, discount.getDiscountId(), moneySaved, finalAmount);

        if (isUpdated) {
            return "Thành công: Đã áp dụng mã " + code + ". Bạn được giảm " + String.format("%,.0f", moneySaved.doubleValue()) + "đ. Còn lại: " + String.format("%,.0f", finalAmount.doubleValue()) + "đ";
        } else {
            return "Lỗi: Không thể cập nhật thông tin giảm giá vào đơn hàng. Vui lòng thử lại!";
        }
    }

    /**
     * Test nhanh service — chạy từ NetBeans
     */
    public static void main(String[] args) {
        DiscountSevice service = new DiscountSevice();
        // Đơn #3, mã WELCOME10 (10%) hoặc FREESHIP20K (giảm thẳng 20.000đ)
        String result = service.applyDiscountToOrder(3, "WELCOME10");
        System.out.println(result);
    }
}
