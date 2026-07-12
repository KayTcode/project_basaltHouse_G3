package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import model.OrderAddress;


public class OrderAddressDAO extends DBContext {


    public int insertOrderAddress(OrderAddress addr) {
        String sql = "INSERT INTO OrderAddresses "
                   + "(CustomerId, ZoneId, RecipientName, RecipientPhone, AddressDetail, Note, IsDefault, CreatedAt, IsDeleted) "
                   + "VALUES (?, ?, ?, ?, ?, ?, 0, GETDATE(), 0)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // CustomerId có thể null (Walk-in)
            if (addr.getCustomerId() > 0) {
                ps.setInt(1, addr.getCustomerId());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }
            // ZoneId mặc định 1 — chỉ giao trong 1 khu vực
            ps.setInt(2, addr.getZoneId() > 0 ? addr.getZoneId() : 1);
            ps.setString(3, addr.getRecipientName());
            ps.setString(4, addr.getRecipientPhone());
            ps.setString(5, addr.getAddressDetail());
            ps.setString(6, addr.getNote());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.err.println("Error insertOrderAddress: " + e.getMessage());
        }
        return -1;
    }
}
