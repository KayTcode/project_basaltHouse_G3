package services;

import dao.AdminCustomerDAO;
import dto.CustomerViewDTO;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminCustomerService {

    private final AdminCustomerDAO dao = new AdminCustomerDAO();

    public Map<String, Object> getCustomerDashboardPage(
            String search, String rankStr, String status, String pageStr, int pageSize) {

        Map<String, Object> pageData = new HashMap<>();

        // 1. Lấy toàn bộ danh sách khách hàng từ DB
        List<CustomerViewDTO> all = dao.getAllCustomers();
        if (all == null) {
            all = Collections.emptyList();
        }

        // 2. Tính thống kê trên toàn bộ danh sách (trước khi lọc)
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", (long) all.size());
        stats.put("active", all.stream().filter(c -> !c.getAccount().isIsLocked()).count());
        // VIP = hạng Vàng (rankId 3) hoặc Kim Cương (rankId 4)
        stats.put("vip", all.stream().filter(c -> c.getRankId() >= 3).count());
        stats.put("locked", all.stream().filter(c -> c.getAccount().isIsLocked()).count());
        pageData.put("stats", stats);

        // 3. Chuẩn hóa tham số bộ lọc
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

        // 4. Áp dụng bộ lọc bằng Stream API
        final Integer finalRankId = rankId;
        List<CustomerViewDTO> filtered = all.stream()
                // Lọc theo ô tìm kiếm (tên, email, số điện thoại)
                .filter(c -> {
                    if (cleanSearch.isEmpty()) {
                        return true;
                    }
                    String name = c.getFullName() != null ? c.getFullName().toLowerCase() : "";
                    String email = c.getAccount().getEmail() != null
                            ? c.getAccount().getEmail().toLowerCase() : "";
                    String phone = c.getPhone() != null ? c.getPhone() : "";
                    return name.contains(cleanSearch)
                            || email.contains(cleanSearch)
                            || phone.contains(cleanSearch);
                })
                // Lọc theo hạng thành viên
                .filter(c -> finalRankId == null || c.getRankId() == finalRankId)
                // Lọc theo trạng thái tài khoản
                .filter(c -> {
                    if (cleanStatus.isEmpty()) {
                        return true;
                    }
                    return switch (cleanStatus) {
                        case "Active" ->
                            !c.getAccount().isIsLocked();
                        case "Locked" ->
                            c.getAccount().isIsLocked();
                        default ->
                            true;
                    };
                })
                .collect(Collectors.toList());

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
}
