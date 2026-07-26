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

/**
 *
 * @author admin
 */
public class CustomerCodeDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    public List<CustomerDiscountCode> getCustomerCode(int accountId) {
        List<CustomerDiscountCode> list = new ArrayList<>();
        try {
            String sql = """
                         SELECT customerCode.CustomerDiscountId,
                                customerCode.AccountId,
                                discount.DiscountId,
                                discount.DiscountAmount,
                                discount.DiscountPercent,
                                discount.StartDate,
                                discount.EndDate,
                                customerCode.IsUsed,
                                customerCode.UsedDate,
                                discount.Description,
                                DATEDIFF(
                                    DAY,
                                    discount.StartDate,
                                    discount.EndDate) AS DayTime
                         FROM CustomerDiscountCodes customerCode
                         JOIN DiscountCodes discount
                           ON discount.DiscountId = customerCode.DiscountId
                         WHERE discount.IsActive = 1
                           AND customerCode.AccountId = ?
                         """;

            st = connection.prepareStatement(sql);
            st.setObject(1, accountId);
            rs = st.executeQuery();
            while (rs.next()) {
                CustomerDiscountCode c = new CustomerDiscountCode(rs.getInt("CustomerDiscountId"),
                        rs.getInt("AccountId"),
                        rs.getInt("DiscountId"),
                        rs.getBigDecimal("DiscountPercent"),
                        rs.getBigDecimal("DiscountAmount"),
                        rs.getObject("StartDate", LocalDateTime.class),
                        rs.getObject("EndDate", LocalDateTime.class),
                        rs.getBoolean("IsUsed"),
                        rs.getObject("UsedDate", LocalDateTime.class),
                        rs.getString("Description"),
                        rs.getInt("DayTime"));
                list.add(c);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;
    }
}
