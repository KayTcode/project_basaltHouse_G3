package services;

import dao.AdminDeliveryZoneDAO;
import model.DeliveryZone;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

public class AdminDeliveryZoneService {
    private final AdminDeliveryZoneDAO zoneDAO = new AdminDeliveryZoneDAO();

    public Map<String, Object> getDashboardData(String province, String district, String isActive) {
        Map<String, Object> data = new HashMap<>();

        // 1. Lấy danh sách zone
        List<DeliveryZone> zones = zoneDAO.getZones(province, district, isActive);

        // 2. Nhóm theo Quận (District) cho view phụ
        Map<String, List<DeliveryZone>> groupedByDistrict = zones.stream()
                .collect(Collectors.groupingBy(DeliveryZone::getDistrict, 
                         LinkedHashMap::new, Collectors.toList()));

        // 3. Stats
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", zoneDAO.countTotalZones());
        stats.put("active", zoneDAO.countActiveZones());
        stats.put("inactive", zoneDAO.countInactiveZones());

        // 4. Lấy danh sách Province và District cho filter dropdown
        List<String> provinces = zoneDAO.getProvinces();
        List<String> districts = zoneDAO.getDistricts(province);

        // Đóng gói data
        data.put("zones", zones);
        data.put("groupedZones", groupedByDistrict);
        data.put("stats", stats);
        data.put("provinces", provinces);
        data.put("districts", districts);

        // Giữ bộ lọc
        data.put("oldProvince", province != null ? province : "");
        data.put("oldDistrict", district != null ? district : "");
        data.put("oldIsActive", isActive != null ? isActive : "");

        return data;
    }

    public String addZone(String wardName, String district, String province, boolean isActive) {
        if (zoneDAO.checkDuplicate(wardName, district, province, null)) {
            return "Vùng giao hàng đã tồn tại (trùng phường, quận, tỉnh).";
        }
        DeliveryZone z = new DeliveryZone(0, wardName, district, province, isActive, false);
        if (zoneDAO.insertZone(z)) {
            return "success";
        }
        return "Lỗi hệ thống khi thêm vùng giao hàng.";
    }

    public String updateZone(int zoneId, String wardName, String district, String province, boolean isActive) {
        if (zoneDAO.checkDuplicate(wardName, district, province, zoneId)) {
            return "Vùng giao hàng đã tồn tại (trùng phường, quận, tỉnh).";
        }
        DeliveryZone z = new DeliveryZone(zoneId, wardName, district, province, isActive, false);
        if (zoneDAO.updateZone(z)) {
            return "success";
        }
        return "Lỗi hệ thống khi cập nhật vùng giao hàng.";
    }

    public boolean toggleActive(int zoneId) {
        return zoneDAO.toggleActive(zoneId);
    }

    public boolean deleteZone(int zoneId) {
        return zoneDAO.softDeleteZone(zoneId);
    }
}
