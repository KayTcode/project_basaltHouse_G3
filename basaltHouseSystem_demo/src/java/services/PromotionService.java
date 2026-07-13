package services;

import dao.DiscountCodeDAO;
import model.Customer;
import model.DiscountCode;


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
            double pct    = discount.getDiscountPercent() != null ? discount.getDiscountPercent().doubleValue() : 0.0;
            double amount = discount.getDiscountAmount()  != null ? discount.getDiscountAmount().doubleValue()  : 0.0;
            int    id     = discount.getDiscountId();
            return String.format("{\"valid\": true, \"id\": %d, \"pct\": %.2f, \"amount\": %.2f}",
                    id, pct, amount);
        } else {
            return "{\"valid\": false, \"msg\": \"Mã giảm giá không tồn tại hoặc đã hết hạn.\"}";
        }
    }


    public java.math.BigDecimal calculateDiscount(String code, java.math.BigDecimal total) {
        if (code == null || code.isBlank() || total == null || total.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            return java.math.BigDecimal.ZERO;
        }

        DiscountCodeDAO dao = new DiscountCodeDAO();
        DiscountCode discount = dao.checkDiscountCode(code.trim());
        if (discount == null) return java.math.BigDecimal.ZERO;

        if (discount.getDiscountAmount() != null
                && discount.getDiscountAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
            return discount.getDiscountAmount().min(total);
        }

        if (discount.getDiscountPercent() != null
                && discount.getDiscountPercent().compareTo(java.math.BigDecimal.ZERO) > 0) {
            // Giảm theo %
            return total.multiply(discount.getDiscountPercent())
                        .divide(new java.math.BigDecimal(100), 0, java.math.RoundingMode.HALF_UP);
        }

        return java.math.BigDecimal.ZERO;
    }
}
