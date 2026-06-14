/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.CustomerDiscountCode;
import model.DiscountCode;

/**
 *
 * @author admin
 */
public class DiscountCodeDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    public List<DiscountCode> getDiscountCode() {
        List<DiscountCode> list = new ArrayList<>();
        try {
            String sql = """
                         SELECT DiscountId,
                                                         Code,
                                                         DiscountPercent,
                                                         DiscountAmount,
                                                         Description,
                                                         StartDate,
                                                         EndDate,
                                                         DATEDIFF(DAY, GETDATE(), EndDate) AS DayTime
                                                  FROM DiscountCodes
                                                  WHERE IsActive = 1 and IsPublic = 1 and IsDeleted = 0
                         """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                DiscountCode d = new DiscountCode(rs.getInt("DiscountId"),
                        rs.getString("Code"),
                        rs.getBigDecimal("DiscountPercent"),
                        rs.getBigDecimal("DiscountAmount"),
                        rs.getObject("StartDate", LocalDateTime.class),
                        rs.getObject("EndDate", LocalDateTime.class),
                        rs.getString("Description"),
                        rs.getInt("DayTime"));
                list.add(d);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;

    }

    public List<CustomerDiscountCode> getVoucherById(int accountId) {
        List<CustomerDiscountCode> list = new ArrayList<>();
        try {
            String sql = """
                         SELECT cd.CustomerDiscountId,
                                cd.AccountId,
                                d.DiscountId,
                                d.Code,
                                d.DiscountAmount,
                                d.DiscountPercent,
                                d.StartDate,
                                d.EndDate,
                                cd.IsUsed,
                                cd.UsedDate,
                                d.Description,
                                DATEDIFF(DAY, GETDATE(), d.EndDate) AS DayTime
                         FROM CustomerDiscountCodes cd
                         JOIN DiscountCodes d ON cd.DiscountId = d.DiscountId
                         WHERE d.IsActive = 1
                           AND d.IsDeleted = 0
                           AND cd.AccountId = ?
                         ORDER BY d.EndDate ASC
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, accountId);
            rs = st.executeQuery();
            while (rs.next()) {
                CustomerDiscountCode c = new CustomerDiscountCode(
                        rs.getInt("CustomerDiscountId"),
                        rs.getInt("AccountId"),
                        rs.getInt("DiscountId"),
                        rs.getBigDecimal("DiscountPercent"),
                        rs.getBigDecimal("DiscountAmount"),
                        rs.getObject("StartDate", LocalDateTime.class),
                        rs.getObject("EndDate", LocalDateTime.class),
                        rs.getBoolean("IsUsed"),
                        rs.getObject("UsedDate", LocalDateTime.class),
                        rs.getString("Description"),
                        rs.getInt("DayTime"),
                        rs.getString("Code"));
                list.add(c);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;

    }

}
