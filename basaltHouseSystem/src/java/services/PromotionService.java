package services;

import dao.DiscountCodeDAO;
import java.util.List;
import model.Customer;
import model.CustomerDiscountCode;
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

            return String.format(java.util.Locale.US, "{\"found\": true, \"id\": %d, \"name\": \"%s\", \"tier\": \"%s\", \"pct\": %.2f}",
                    member.getCustomerId(),
                    name.replace("\"", "\\\""),
                    tier.replace("\"", "\\\""),
                    pct);
        } else {
            return "{\"found\": false}";
        }
    }

    public String searchMembersByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "[]";
        }
        
        DiscountCodeDAO dao = new DiscountCodeDAO();
        List<Customer> members = dao.searchCustomerMembershipByName(name.trim());
        
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < members.size(); i++) {
            Customer c = members.get(i);
            String fullName = c.getFullName() != null ? c.getFullName().replace("\"", "\\\"") : "Khách hàng";
            String tier = c.getRankName() != null ? c.getRankName().replace("\"", "\\\"") : "Đồng";
            double pct = c.getDiscountValue() != null ? c.getDiscountValue().doubleValue() : 0.0;
            String phone = c.getPhone() != null ? c.getPhone() : "";
            
            sb.append(String.format(java.util.Locale.US, "{\"id\": %d, \"name\": \"%s\", \"phone\": \"%s\", \"tier\": \"%s\", \"pct\": %.2f, \"vouchers\": [",
                    c.getCustomerId(), fullName, phone, tier, pct));
            
            if (c.getAccountId() > 0) {
                List<CustomerDiscountCode> vouchers = dao.getVoucherById(c.getAccountId());
                for (int j = 0; j < vouchers.size(); j++) {
                    CustomerDiscountCode v = vouchers.get(j);
                    double vPct = v.getDiscountPercent() != null ? v.getDiscountPercent().doubleValue() : 0.0;
                    double vAmount = v.getDiscountAmount() != null ? v.getDiscountAmount().doubleValue() : 0.0;
                    sb.append(String.format(java.util.Locale.US, "{\"id\": %d, \"code\": \"%s\", \"pct\": %.2f, \"amount\": %.2f, \"desc\": \"%s\"}",
                            v.getCustomerDiscountId(), v.getCode(), vPct, vAmount, v.getDescription() != null ? v.getDescription().replace("\"", "\\\"") : ""));
                    if (j < vouchers.size() - 1) sb.append(",");
                }
            }
            
            sb.append("]}");
            if (i < members.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public String getPublicDiscounts() {
        DiscountCodeDAO dao = new DiscountCodeDAO();
        List<DiscountCode> list = dao.getDiscountCode();
        
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < list.size(); i++) {
            DiscountCode d = list.get(i);
            double pct = d.getDiscountPercent() != null ? d.getDiscountPercent().doubleValue() : 0.0;
            double amount = d.getDiscountAmount() != null ? d.getDiscountAmount().doubleValue() : 0.0;
            sb.append(String.format(java.util.Locale.US, "{\"id\": %d, \"code\": \"%s\", \"pct\": %.2f, \"amount\": %.2f, \"desc\": \"%s\"}",
                    d.getDiscountId(), d.getCode(), pct, amount, d.getDescription() != null ? d.getDescription().replace("\"", "\\\"") : ""));
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public String checkDiscount(String code) {
        return checkDiscount(code, null, null, null);
    }

    public String checkDiscount(String code, Integer customerId) {
        return checkDiscount(code, customerId, null, null);
    }

    public String checkDiscount(String code, Integer customerId, Integer tableSessionId, Integer tableId) {
        if (code == null || code.trim().isEmpty()) {
            return "{\"valid\": false, \"msg\": \"Vui lòng nhập mã giảm giá.\"}";
        }

        DiscountCodeDAO dao = new DiscountCodeDAO();
        DiscountCode discount = dao.checkDiscountCode(code.trim());

        if (discount != null) {
            if (tableSessionId != null && tableSessionId > 0) {
                if (dao.hasTableSessionUsedDiscount(tableSessionId)) {
                    return "{\"valid\": false, \"msg\": \"Mã đã được sử dụng không thể sử dụng được nữa\"}";
                }
            } else if (tableId != null && tableId > 0) {
                if (dao.hasTableUsedDiscount(tableId)) {
                    return "{\"valid\": false, \"msg\": \"Mã đã được sử dụng không thể sử dụng được nữa\"}";
                }
            }

            if (customerId != null && customerId > 0) {
                if (dao.hasCustomerUsedDiscount(customerId, code.trim())) {
                    return "{\"valid\": false, \"msg\": \"Mã đã được sử dụng không thể sử dụng được nữa\"}";
                }
            }
            double pct    = discount.getDiscountPercent() != null ? discount.getDiscountPercent().doubleValue() : 0.0;
            double amount = discount.getDiscountAmount()  != null ? discount.getDiscountAmount().doubleValue()  : 0.0;
            int    id     = discount.getDiscountId();
            return String.format(java.util.Locale.US, "{\"valid\": true, \"id\": %d, \"pct\": %.2f, \"amount\": %.2f}",
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
