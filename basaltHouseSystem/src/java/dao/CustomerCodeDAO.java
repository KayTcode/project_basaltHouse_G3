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
                         select  cd.CustomerDiscountId,cd.AccountId,d.DiscountId,d.DiscountAmount,d.DiscountPercent,d.StartDate,d.EndDate,cd.IsUsed,cd.UsedDate,d.Description,DATEDIFF(DAY, StartDate, EndDate) AS DayTime from CustomerDiscountCodes cd 
                           join DiscountCodes d on cd.DiscountId = d.DiscountId
                           where d.IsActive = 1 and cd.AccountId = ?
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
    public static void main(String[] args) {
        CustomerCodeDAO dao = new CustomerCodeDAO();
        List<CustomerDiscountCode> list = dao.getCustomerCode(6);
        for (CustomerDiscountCode customerDiscountCode : list) {
            System.out.println(customerDiscountCode);
        }
    }
   
}
