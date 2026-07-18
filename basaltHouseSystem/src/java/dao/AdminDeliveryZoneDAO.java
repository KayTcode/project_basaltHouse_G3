package dao;

import model.DeliveryZone;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminDeliveryZoneDAO extends DBContext {

    public List<DeliveryZone> getZones(String province, String district, String isActive) {
        List<DeliveryZone> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM DeliveryZones WHERE IsDeleted = 0");
        List<Object> params = new ArrayList<>();

        if (province != null && !province.trim().isEmpty()) {
            sql.append(" AND Province = ?");
            params.add(province);
        }
        if (district != null && !district.trim().isEmpty()) {
            sql.append(" AND District = ?");
            params.add(district);
        }
        if (isActive != null && !isActive.trim().isEmpty()) {
            sql.append(" AND IsActive = ?");
            params.add("true".equalsIgnoreCase(isActive) ? 1 : 0);
        }
        sql.append(" ORDER BY Province, District, WardName");

        try (PreparedStatement st = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                st.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    DeliveryZone z = new DeliveryZone(
                            rs.getInt("ZoneId"),
                            rs.getString("WardName"),
                            rs.getString("District"),
                            rs.getString("Province"),
                            rs.getBoolean("IsActive"),
                            rs.getBoolean("IsDeleted")
                    );
                    list.add(z);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countTotalZones() {
        try (PreparedStatement st = connection.prepareStatement("SELECT COUNT(*) FROM DeliveryZones WHERE IsDeleted = 0");
             ResultSet rs = st.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {}
        return 0;
    }

    public int countActiveZones() {
        try (PreparedStatement st = connection.prepareStatement("SELECT COUNT(*) FROM DeliveryZones WHERE IsDeleted = 0 AND IsActive = 1");
             ResultSet rs = st.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {}
        return 0;
    }

    public int countInactiveZones() {
        try (PreparedStatement st = connection.prepareStatement("SELECT COUNT(*) FROM DeliveryZones WHERE IsDeleted = 0 AND IsActive = 0");
             ResultSet rs = st.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {}
        return 0;
    }

    public boolean checkDuplicate(String ward, String district, String province, Integer excludeZoneId) {
        String sql = "SELECT 1 FROM DeliveryZones WHERE WardName = ? AND District = ? AND Province = ? AND IsDeleted = 0";
        if (excludeZoneId != null) {
            sql += " AND ZoneId != ?";
        }
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, ward);
            st.setString(2, district);
            st.setString(3, province);
            if (excludeZoneId != null) {
                st.setInt(4, excludeZoneId);
            }
            try (ResultSet rs = st.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertZone(DeliveryZone z) {
        String sql = "INSERT INTO DeliveryZones (WardName, District, Province, IsActive, IsDeleted) VALUES (?, ?, ?, ?, 0)";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, z.getWardName());
            st.setString(2, z.getDistrict());
            st.setString(3, z.getProvince());
            st.setBoolean(4, z.isIsActive());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateZone(DeliveryZone z) {
        String sql = "UPDATE DeliveryZones SET WardName = ?, District = ?, Province = ?, IsActive = ? WHERE ZoneId = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, z.getWardName());
            st.setString(2, z.getDistrict());
            st.setString(3, z.getProvince());
            st.setBoolean(4, z.isIsActive());
            st.setInt(5, z.getZoneId());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean toggleActive(int zoneId) {
        try (PreparedStatement st = connection.prepareStatement("UPDATE DeliveryZones SET IsActive = CASE WHEN IsActive=1 THEN 0 ELSE 1 END WHERE ZoneId = ?")) {
            st.setInt(1, zoneId);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean softDeleteZone(int zoneId) {
        String sql = "UPDATE DeliveryZones SET IsDeleted = 1 WHERE ZoneId = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, zoneId);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<String> getProvinces() {
        List<String> list = new ArrayList<>();
        try (PreparedStatement st = connection.prepareStatement("SELECT DISTINCT Province FROM DeliveryZones WHERE IsDeleted = 0 ORDER BY Province");
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) list.add(rs.getString(1));
        } catch (Exception e) {}
        return list;
    }
    
    public List<String> getDistricts(String province) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT District FROM DeliveryZones WHERE IsDeleted = 0";
        if (province != null && !province.isEmpty()) sql += " AND Province = ?";
        sql += " ORDER BY District";
        
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            if (province != null && !province.isEmpty()) st.setString(1, province);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) list.add(rs.getString(1));
            }
        } catch (Exception e) {}
        return list;
    }
}
