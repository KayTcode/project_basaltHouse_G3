package services;

import dao.AdminDiscountDAO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import model.DiscountCode;

public class AdminDiscountService {

    private static final DateTimeFormatter DT_FORMATTER
            = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private final AdminDiscountDAO discountDAO = new AdminDiscountDAO();

    public List<DiscountCode> getAllDiscounts(String search, String filterType, String filterStatus) {
        return discountDAO.getAllDiscounts(search, filterType, filterStatus);
    }

    public Map<String, Integer> getDiscountStats() {
        return discountDAO.getDiscountStats();
    }

    public String addDiscount(String code, String discountType,
            String discountPercentStr, String discountAmountStr,
            String startDateStr, String endDateStr,
            String description, boolean isActive, boolean isPublic, int createdBy) {

        // --- Validate ---
        if (code == null || code.isBlank()) {
            return "Mã code không được để trống.";
        }

        if (!"PERCENT".equals(discountType) && !"AMOUNT".equals(discountType)) {
            return "Loại giảm giá không hợp lệ.";
        }

        BigDecimal discountPercent = null;
        BigDecimal discountAmount = null;

        if ("PERCENT".equals(discountType)) {
            discountPercent = parseBigDecimal(discountPercentStr);
            if (discountPercent == null || discountPercent.compareTo(BigDecimal.ONE) < 0
                    || discountPercent.compareTo(new BigDecimal("100")) > 0) {
                return "Phần trăm giảm phải từ 1 đến 100.";
            }
        } else {
            discountAmount = parseBigDecimal(discountAmountStr);
            if (discountAmount == null || discountAmount.compareTo(BigDecimal.ZERO) <= 0) {
                return "Số tiền giảm phải lớn hơn 0.";
            }
        }

        LocalDateTime startDate = parseDateTime(startDateStr);
        LocalDateTime endDate = parseDateTime(endDateStr);

        if (startDate == null) {
            return "Thời gian bắt đầu không hợp lệ.";
        }
        if (endDate == null) {
            return "Thời gian kết thúc không hợp lệ.";
        }
        if (!endDate.isAfter(startDate)) {
            return "Thời gian kết thúc phải sau thời gian bắt đầu.";
        }

        boolean ok = discountDAO.addDiscount(
                code, discountType, discountPercent, discountAmount,
                startDate, endDate, description, isActive, isPublic, createdBy);

        return ok ? "success" : "Thêm mã thất bại. Mã có thể đã tồn tại hoặc lỗi cơ sở dữ liệu.";
    }

    public String updateDiscount(String discountIdStr, String code, String discountType,
            String discountPercentStr, String discountAmountStr,
            String startDateStr, String endDateStr,
            String description, boolean isActive, boolean isPublic) {

        int discountId = parseInt(discountIdStr, -1);
        if (discountId <= 0) {
            return "ID mã giảm giá không hợp lệ.";
        }

        if (code == null || code.isBlank()) {
            return "Mã code không được để trống.";
        }
        if (!"PERCENT".equals(discountType) && !"AMOUNT".equals(discountType)) {
            return "Loại giảm giá không hợp lệ.";
        }

        BigDecimal discountPercent = null;
        BigDecimal discountAmount = null;

        if ("PERCENT".equals(discountType)) {
            discountPercent = parseBigDecimal(discountPercentStr);
            if (discountPercent == null || discountPercent.compareTo(BigDecimal.ONE) < 0
                    || discountPercent.compareTo(new BigDecimal("100")) > 0) {
                return "Phần trăm giảm phải từ 1 đến 100.";
            }
        } else {
            discountAmount = parseBigDecimal(discountAmountStr);
            if (discountAmount == null || discountAmount.compareTo(BigDecimal.ZERO) <= 0) {
                return "Số tiền giảm phải lớn hơn 0.";
            }
        }

        LocalDateTime startDate = parseDateTime(startDateStr);
        LocalDateTime endDate = parseDateTime(endDateStr);

        if (startDate == null) {
            return "Thời gian bắt đầu không hợp lệ.";
        }
        if (endDate == null) {
            return "Thời gian kết thúc không hợp lệ.";
        }
        if (!endDate.isAfter(startDate)) {
            return "Thời gian kết thúc phải sau thời gian bắt đầu.";
        }

        boolean ok = discountDAO.updateDiscount(
                discountId, code, discountType, discountPercent, discountAmount,
                startDate, endDate, description, isActive, isPublic);

        return ok ? "success" : "Cập nhật thất bại. Lỗi cơ sở dữ liệu.";
    }

    public String deleteDiscount(String discountIdStr) {
        int discountId = parseInt(discountIdStr, -1);
        if (discountId <= 0) {
            return "ID mã giảm giá không hợp lệ.";
        }

        boolean ok = discountDAO.deleteDiscount(discountId);
        return ok ? "success" : "Xóa mã thất bại. Lỗi cơ sở dữ liệu.";
    }

    private LocalDateTime parseDateTime(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(s, DT_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal parseBigDecimal(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private int parseInt(String s, int defaultVal) {
        if (s == null || s.isBlank()) {
            return defaultVal;
        }
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return defaultVal;
        }
    }
}
