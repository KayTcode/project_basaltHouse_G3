/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.AdminDashboardDAO;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.SQLException;

/**
 *
 * @author MSI
 */
public class AdminDashboardService {
    private final AdminDashboardDAO dao = new AdminDashboardDAO();
    
    public Map<String, Object> getDashboardData() throws SQLException {
        Map<String, Object> dashboardData = new HashMap<>();

        // ── 1. Chỉ số KPI hiển thị trên các thẻ đầu trang ───────────────────
        BigDecimal revenueToday = dao.getRevenueToday();
        int totalOrdersToday = dao.getTotalOrdersToday();
        int deliveringOrders = dao.getDeliveringOrders();
        int lowStockCount = dao.getLowStockCount();

        dashboardData.put("todayRevenue", revenueToday);
        dashboardData.put("todayOrdersCount", totalOrdersToday);
        dashboardData.put("activeTablesCount", deliveringOrders);
        dashboardData.put("lowStockCount", lowStockCount);
        
        // ── 2. Dữ liệu biểu đồ doanh thu 7 ngày gần nhất ────────────────────
        List<Map<String, Object>> last7DaysRevenue = dao.getLast7DaysRevenue();
        // Convert to LinkedHashMap for JSP entry.key / entry.value
        Map<String, Object> revenueLast7Days = new java.util.LinkedHashMap<>();
        for (Map<String, Object> map : last7DaysRevenue) {
            revenueLast7Days.put((String) map.get("day"), map.get("revenue"));
        }
        dashboardData.put("revenueLast7Days", revenueLast7Days);

        // Tính tổng doanh thu của 7 ngày gần đây từ dữ liệu danh sách vừa lấy
        BigDecimal last7DaysTotal = last7DaysRevenue.stream()
                .map(row -> (BigDecimal) row.get("revenue"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Lấy doanh thu của 7 ngày trước đó nữa để so sánh hiệu năng
        BigDecimal prev7DaysRevenue = dao.getPrev7DaysRevenue();
        
        // Gọi hàm helper tại DAO để tính toán tỷ lệ % tăng trưởng doanh thu
        double revenueGrowthPercent = dao.calcGrowthPercent(last7DaysTotal, prev7DaysRevenue);
        dashboardData.put("revenueTrend", revenueGrowthPercent);
        dashboardData.put("ordersTrend", 0.0); // Chưa có API cho orderTrend
        
        BigDecimal avgBill = BigDecimal.ZERO;
        if (totalOrdersToday > 0 && revenueToday != null) {
            avgBill = revenueToday.divide(new BigDecimal(totalOrdersToday), 0, java.math.RoundingMode.HALF_UP);
        }
        dashboardData.put("averageBillValue", avgBill);

        // ── 3. Danh sách Top 5 sản phẩm bán chạy nhất ──────────────────────
        List<Map<String, Object>> topProducts = dao.getTopProducts(5);
        dashboardData.put("topSellingProducts", topProducts);

        // ── 4. Danh sách 10 đơn hàng vừa phát sinh mới nhất ─────────────────
        List<Map<String, Object>> recentOrders = dao.getRecentOrders(10);
        dashboardData.put("recentOrders", recentOrders);

        // ── 5. Hệ thống cảnh báo & Việc cần xử lý ngay ──────────────────────
        List<Map<String, Object>> lowStockAlerts = dao.getLowStockAlerts();
        int pendingImportCount = dao.getPendingImportCount();
        int pendingRegistrationCount = dao.getPendingRegistrationCount();

        dashboardData.put("lowStockAlerts", lowStockAlerts);
        dashboardData.put("pendingImportCount", pendingImportCount);
        dashboardData.put("pendingRegistrationCount", pendingRegistrationCount);

        return dashboardData;
    }
}
