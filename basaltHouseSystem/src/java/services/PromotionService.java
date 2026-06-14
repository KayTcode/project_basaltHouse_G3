package services;

import dao.DiscountCodeDAO;
import model.Customer;
import model.DiscountCode;

// Tạo ra để xử lý các logic tính toán Hạng thành viên và Mã giảm giá.
// Giúp cho CheckMemberServlet và CheckDiscountServlet giữ được sự mỏng nhẹ.
public class PromotionService {

    public String checkMember(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return "{\"found\": false}";
        }

        DiscountCodeDAO dao = new DiscountCodeDAO();
        Customer member = dao.getCustomerMembership(phone.trim());

        if (member != null) {
            String name = member.getFullName() != null ? member.getFullName() : "Khách hàng";
            String tier = member.getRankName() != null ? member.getRankName() : "Đồng";
            double pct = member.getDiscountValue() != null ? member.getDiscountValue().doubleValue() : 0.0;

            return String.format("{\"found\": true, \"id\": %d, \"name\": \"%s\", \"tier\": \"%s\", \"pct\": %.2f}",
                    member.getCustomerId(),
                    name.replace("\"", "\\\""),
                    tier.replace("\"", "\\\""),
                    pct);
        } else {
            return "{\"found\": false}";
        }
    }

    public String checkDiscount(String code) {
        if (code == null || code.trim().isEmpty()) {
            return "{\"valid\": false, \"msg\": \"Vui lòng nhập mã giảm giá.\"}";
        }

        DiscountCodeDAO dao = new DiscountCodeDAO();
        DiscountCode discount = dao.checkDiscountCode(code.trim());

        if (discount != null) {
            double pct = discount.getDiscountPercent() != null ? discount.getDiscountPercent().doubleValue() : 0.0;
            return String.format("{\"valid\": true, \"pct\": %.2f}", pct);
        } else {
            return "{\"valid\": false, \"msg\": \"Mã giảm giá không tồn tại hoặc đã hết hạn.\"}";
        }
    }
}
