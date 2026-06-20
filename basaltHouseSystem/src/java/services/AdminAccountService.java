package services;

import dao.AdminAccountDAO;
import dto.AccountViewDTO;
import model.Account;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminAccountService {

    private final AdminAccountDAO dao = new AdminAccountDAO();

    /**
     * Xử lý toàn bộ nghiệp vụ bộ lọc, thống kê và phân trang từ danh sách thô của DAO
     */
    public Map<String, Object> getAccountDashboardPage(String search, String roleStr, String status, String pageStr, int pageSize) {
        Map<String, Object> pageData = new HashMap<>();

        // 1. Lấy toàn bộ danh sách tài khoản từ DAO
        List<AccountViewDTO> allAccounts = dao.getAllAccounts();
        if (allAccounts == null) {
            allAccounts = java.util.Collections.emptyList();
        }

        // 2. NGHIỆP VỤ: Tính toán bộ số liệu thống kê khớp chuẩn xác giao diện Basalt House
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", (long) allAccounts.size());
        stats.put("customer", allAccounts.stream().filter(a -> a.getAccount().getRoleId() == 2).count());
        stats.put("staffCashier", allAccounts.stream().filter(a -> a.getAccount().getRoleId() == 3 || a.getAccount().getRoleId() == 4).count());
        stats.put("shipper", allAccounts.stream().filter(a -> a.getAccount().getRoleId() == 5).count());
        stats.put("locked", allAccounts.stream().filter(a -> a.getAccount().isIsLocked()).count());
        pageData.put("stats", stats);

        // 3. NGHIỆP VỤ: Chuẩn hóa tham số bộ lọc
        String cleanSearch = (search != null) ? search.trim().toLowerCase() : "";
        String cleanStatus = (status != null) ? status.trim() : "";

        Integer roleId = null;
        if (roleStr != null && !roleStr.trim().isEmpty()) {
            try { roleId = Integer.parseInt(roleStr); } catch (NumberFormatException e) { roleId = null; }
        }

        int page = 1;
        if (pageStr != null && !pageStr.trim().isEmpty()) {
            try { page = Integer.parseInt(pageStr); if (page < 1) page = 1; } catch (NumberFormatException e) { page = 1; }
        }

        // Lọc danh sách bằng Stream API (Đã sửa triệt để lỗi ép kiểu so sánh)
        final Integer finalRoleId = roleId; // Tạo biến final an toàn cho Lambda
        List<AccountViewDTO> filteredList = allAccounts.stream()
            .filter(a -> {
                if (cleanSearch.isEmpty()) return true;
                String fullName = (a.getFullName() != null) ? a.getFullName().toLowerCase() : "";
                String email = (a.getAccount().getEmail() != null) ? a.getAccount().getEmail().toLowerCase() : "";
                String phone = (a.getPhone() != null) ? a.getPhone() : "";
                return fullName.contains(cleanSearch) || email.contains(cleanSearch) || phone.contains(cleanSearch);
            })
            .filter(a -> (finalRoleId == null) || (a.getAccount().getRoleId() == finalRoleId.intValue()))
            .filter(a -> {
                if (cleanStatus.isEmpty()) return true;
                return switch (cleanStatus) {
                    case "Active" -> a.getAccount().isIsActive() && !a.getAccount().isIsLocked();
                    case "Locked" -> a.getAccount().isIsLocked();
                    case "Inactive" -> !a.getAccount().isIsActive();
                    default -> true;
                };
            })
            .collect(Collectors.toList());

        // 4. NGHIỆP VỤ: Tính toán phân trang dựa trên danh sách đã lọc
        int totalFiltered = filteredList.size();
        int totalPages = (int) Math.ceil((double) totalFiltered / pageSize);
        if (totalPages == 0) totalPages = 1;
        if (page > totalPages) page = totalPages;

        // Cắt khúc danh sách tương ứng với trang hiện tại
        List<AccountViewDTO> pagedAccounts = filteredList.stream()
                .skip((long) (page - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

        // 5. Đóng gói kết quả trả về cho View sử dụng
        pageData.put("accounts", pagedAccounts);
        pageData.put("currentPage", page);
        pageData.put("totalPages", totalPages);
        pageData.put("totalFiltered", totalFiltered);
        
        pageData.put("oldSearch", search != null ? search.trim() : "");
        pageData.put("oldRoleId", roleStr != null ? roleStr.trim() : "");
        pageData.put("oldStatus", cleanStatus);

        return pageData;
    }

    public boolean processAddAccount(String email, String passwordHash, String roleIdStr, String fullName, String phone, String isActiveStr) {
        try {
            Account acc = new Account();
            acc.setEmail(email);
            acc.setPasswordHash(passwordHash);
            acc.setRoleId(Integer.parseInt(roleIdStr));
            acc.setIsEmailVerified(true);
            acc.setIsActive(Boolean.parseBoolean(isActiveStr));

            return dao.addAccount(acc, fullName, phone);
        } catch (Exception e) {
            System.err.println("Lỗi thêm tài khoản tại Service: " + e.getMessage());
            return false;
        }
    }

    public boolean processUpdateAccount(String idStr, String email, String roleIdStr, String fullName, String phone, String oldRoleIdStr, String isActiveStr, String isLockedStr) {
        try {
            Account acc = new Account();
            acc.setAccountId(Integer.parseInt(idStr));
            acc.setEmail(email);
            acc.setRoleId(Integer.parseInt(roleIdStr));
            acc.setIsActive(Boolean.parseBoolean(isActiveStr));
            acc.setIsLocked(Boolean.parseBoolean(isLockedStr));

            int oldRoleId = Integer.parseInt(oldRoleIdStr);

            return dao.updateAccount(acc, fullName, phone, oldRoleId);
        } catch (Exception e) {
            System.err.println("Lỗi sửa tài khoản tại Service: " + e.getMessage());
            return false;
        }
    }

    public boolean processDeleteAccount(String idStr, String roleIdStr) {
        try {
            int accountId = Integer.parseInt(idStr);
            int roleId = Integer.parseInt(roleIdStr);
            return dao.deleteAccount(accountId, roleId);
        } catch (Exception e) {
            System.err.println("Lỗi xóa tài khoản tại Service: " + e.getMessage());
            return false;
        }
    }
}