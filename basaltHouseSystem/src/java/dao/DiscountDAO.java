/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.DiscountCode;

/**
 *
 * @author MSI
 */
public class DiscountDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    public DiscountCode getDiscountByCode(String code) {
        try {
            String sql = """
                         SELECT *
                         FROM DiscountCodes WHERE Code = ? AND IsActive = 1 AND IsDeleted = 0
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, code);
            rs = st.executeQuery();
            if (rs.next()) {
                DiscountCode discount = new DiscountCode();

                discount.setDiscountId(rs.getInt("DiscountId"));
                discount.setCode(rs.getString("Code"));

                discount.setDiscountPercent(rs.getBigDecimal("DiscountPercent"));
                discount.setDiscountAmount(rs.getBigDecimal("DiscountAmount"));

                if (rs.getTimestamp("StartDate") != null) {
                    discount.setStartDate(rs.getTimestamp("StartDate").toLocalDateTime());
                }
                if (rs.getTimestamp("EndDate") != null) {
                    discount.setEndDate(rs.getTimestamp("EndDate").toLocalDateTime());
                }
                if (rs.getTimestamp("CreatedAt") != null) {
                    discount.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
                }

                discount.setIsActive(rs.getBoolean("IsActive"));
                discount.setIsDeleted(rs.getBoolean("IsDeleted"));

                discount.setCreatedBy(rs.getInt("CreatedBy"));

                return discount;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
