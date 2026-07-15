package services;

import dao.AdminFinanceDAO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminFinanceService {

    private final AdminFinanceDAO dao = new AdminFinanceDAO();

    public Map<String, Object> getFinanceData(String period) {
        Map<String, Object> data = new HashMap<>();

        // 1. KPI Cards 
        BigDecimal totalRevenue = dao.getTotalRevenue(period);
        BigDecimal prevRevenue = dao.getPrevRevenue(period);
        BigDecimal totalCost = dao.getTotalImportCost(period);
        BigDecimal grossProfit = totalRevenue.subtract(totalCost);
        int totalOrders = dao.getTotalOrders(period);

        data.put("totalRevenue", totalRevenue);
        data.put("totalCost", totalCost);
        data.put("grossProfit", grossProfit);
        data.put("totalOrders", totalOrders);

        // % tăng trưởng doanh thu so với kỳ trước
        double revGrowth = dao.calcGrowthPercent(totalRevenue, prevRevenue);
        data.put("revGrowth", Math.round(revGrowth * 10.0) / 10.0);

        // Biên lợi nhuận (%)
        double profitMargin = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                ? grossProfit.multiply(BigDecimal.valueOf(100))
                        .divide(totalRevenue, 1, RoundingMode.HALF_UP)
                        .doubleValue()
                : 0.0;
        data.put("profitMargin", profitMargin);

        // Giá trị đơn trung bình
        BigDecimal avgOrder = totalOrders > 0
                ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 0, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        data.put("avgOrder", avgOrder);

        //  2. Biểu đồ cột: Doanh thu & Chi phí theo tuần/tháng/ngày 
        List<Map<String, Object>> weeklyBreakdown = dao.getChartData(period);
        data.put("weeklyBreakdown", weeklyBreakdown);

        // 3. Cơ cấu doanh thu theo kênh (Donut chart) 
        List<Map<String, Object>> channelRevenue = dao.getRevenueByChannel(period);
        // Tính % cho mỗi kênh
        BigDecimal totalCh = channelRevenue.stream()
                .map(r -> (BigDecimal) r.get("revenue"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        for (Map<String, Object> ch : channelRevenue) {
            BigDecimal rev = (BigDecimal) ch.get("revenue");
            int pct = totalCh.compareTo(BigDecimal.ZERO) > 0
                    ? rev.multiply(BigDecimal.valueOf(100))
                            .divide(totalCh, 0, RoundingMode.HALF_UP).intValue()
                    : 0;
            ch.put("pct", pct);
        }
        data.put("channelRevenue", channelRevenue);

        //  4. Phương thức thanh toán
        List<Map<String, Object>> paymentStats = dao.getPaymentMethodStats(period);
        // Tính % thanh toán so với tổng doanh thu cho progress bar
        for (Map<String, Object> pm : paymentStats) {
            BigDecimal amount = (BigDecimal) pm.get("amount");
            int barPct = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                    ? amount.multiply(BigDecimal.valueOf(100))
                            .divide(totalRevenue, 0, RoundingMode.HALF_UP).intValue()
                    : 0;
            pm.put("barPct", barPct);
        }
        data.put("paymentStats", paymentStats);

        //  5. Top 5 sản phẩm theo doanh thu 
        List<Map<String, Object>> topProducts = dao.getTopProductsByRevenue(period, 5);
        data.put("topProducts", topProducts);

        //  6. Phiếu nhập kho gần nhất 
        List<Map<String, Object>> recentImports = dao.getRecentImports(5);
        data.put("recentImports", recentImports);

        //7. Kỳ hiện tại (để JSP hiển thị tab active)
        data.put("period", period);

        return data;
    }
}
