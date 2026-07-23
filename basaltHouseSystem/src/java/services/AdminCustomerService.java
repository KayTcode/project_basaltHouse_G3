package services;

import dao.AdminCustomerDAO;
import dao.AdminDiscountDAO;
import dto.CustomerViewDTO;
import model.DiscountCode;
import model.MembershipRank;
import model.Order;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminCustomerService {

    private final AdminCustomerDAO dao = new AdminCustomerDAO();
    private final AdminDiscountDAO discountDAO = new AdminDiscountDAO();

    public Map<String, Object> getCustomerDashboardPage(
            String search, String rankStr, String status, String pageStr, int pageSize) {

        Map<String, Object> pageData = new HashMap<>();

        List<CustomerViewDTO> all = dao.getAllCustomers();
        if (all == null) {
            all = Collections.emptyList();
        }

        List<MembershipRank> ranks = dao.getAllRanks();
        pageData.put("ranks", ranks);

        Map<String, Long> stats = new HashMap<>();
        stats.put("total", (long) all.size());
        stats.put("active", all.stream().filter(c -> !c.getAccount().isIsLocked()).count());
        // VIP = hạng Vàng (rankId 3) hoặc Kim Cương (rankId 4)
        stats.put("vip", all.stream().filter(c -> c.getRankId() >= 3).count());
        stats.put("locked", all.stream().filter(c -> c.getAccount().isIsLocked()).count());
        pageData.put("stats", stats);

        String cleanSearch = (search != null) ? search.trim().toLowerCase() : "";
        String cleanStatus = (status != null) ? status.trim() : "";

        Integer rankId = null;
        if (rankStr != null && !rankStr.trim().isEmpty()) {
            try {
                rankId = Integer.parseInt(rankStr.trim());
            } catch (NumberFormatException ignored) {
            }
        }

        int page = 1;
        if (pageStr != null && !pageStr.trim().isEmpty()) {
            try {
                page = Integer.parseInt(pageStr.trim());
                if (page < 1) {
                    page = 1;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        // 4. Áp dụng bộ lọc trực tiếp ở SQL thông qua DAO
        List<CustomerViewDTO> filtered = dao.getAllCustomersFiltered(cleanSearch, rankId, cleanStatus);

        // 5. Phân trang
        int totalFiltered = filtered.size();
        int totalPages = (int) Math.ceil((double) totalFiltered / pageSize);
        if (totalPages == 0) {
            totalPages = 1;
        }
        if (page > totalPages) {
            page = totalPages;
        }

        List<CustomerViewDTO> paged = filtered.stream()
                .skip((long) (page - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

        // 6. Đóng gói kết quả
        pageData.put("customers", paged);
        pageData.put("currentPage", page);
        pageData.put("totalPages", totalPages);
        pageData.put("totalFiltered", totalFiltered);

        // Giữ lại giá trị bộ lọc để View hiển thị lại đúng trạng thái form
        pageData.put("oldSearch", search != null ? search.trim() : "");
        pageData.put("oldRankId", rankStr != null ? rankStr.trim() : "");
        pageData.put("oldStatus", cleanStatus);

        return pageData;
    }

    public boolean processAddCustomer(String email, String password, String fullName,
            String phone, String rankIdStr, String spentStr) {
        // Service tự kiểm tra và parse tham số
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return false;
        }
        int rankId = parseIntSafe(rankIdStr, 0);
        if (rankId <= 0) {
            return false;
        }
        double totalSpent = parseDoubleSafe(spentStr, 0);
        return dao.addCustomer(email.trim(), password.trim(), fullName, phone, rankId, totalSpent);
    }

    public boolean processUpdateCustomer(String accountIdStr, String email, String fullName,
            String phone, String rankIdStr, String spentStr, String isLockedStr) {
        int accountId = parseIntSafe(accountIdStr, -1);
        if (accountId == -1 || email == null || email.isBlank()) {
            return false;
        }
        int rankId = parseIntSafe(rankIdStr, 0);
        if (rankId <= 0) {
            return false;
        }
        double totalSpent = parseDoubleSafe(spentStr, 0);
        boolean isLocked = "true".equalsIgnoreCase(isLockedStr);
        return dao.updateCustomer(accountId, email.trim(), fullName, phone, rankId, totalSpent, isLocked);
    }

    // ─ Helpers nội bộ 
    private int parseIntSafe(String val, int def) {
        try {
            return Integer.parseInt(val.trim());
        } catch (Exception e) {
            return def;
        }
    }

    private double parseDoubleSafe(String val, double def) {
        try {
            return Double.parseDouble(val.trim());
        } catch (Exception e) {
            return def;
        }
    }

    public Map<String, Object> processGetOrderHistoryPage(String accountIdStr) {
        Map<String, Object> data = new HashMap<>();
        int accountId = parseIntSafe(accountIdStr, -1);
        data.put("orders", accountId == -1 ? Collections.emptyList() : dao.getOrderHistoryByAccountId(accountId));
        String[] info = accountId == -1 ? new String[]{"Khách hàng", ""} : dao.getCustomerBasicInfo(accountId);
        data.put("customerName", info[0]);
        data.put("email",        info[1]);
        return data;
    }


    public List<DiscountCode> getActiveDiscountsForGift() {
        return discountDAO.getActiveDiscountsForGift();
    }

    public String processGiftDiscount(String accountIdStr, String discountIdStr) {
        int accountId = parseIntSafe(accountIdStr, -1);
        int discountId = parseIntSafe(discountIdStr, -1);
        if (accountId <= 0 || discountId <= 0) {
            return "error";
        }
        return discountDAO.giftDiscountToCustomer(accountId, discountId);
    }
}
