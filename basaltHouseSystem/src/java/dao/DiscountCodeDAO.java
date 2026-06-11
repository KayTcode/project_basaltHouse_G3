/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
                                                         DATEDIFF(DAY, StartDate, EndDate) AS DayTime
                                                  FROM DiscountCodes
                                                  WHERE IsActive = 1 and IsPublic = 1;
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

    public DiscountCode getvoucher(int id) {
        try {
            String sql = """                       
                      select top 1 Code ,d.DiscountId from DiscountCodes d
                       join CustomerDiscountCodes cd on cd.DiscountId = d.DiscountId
                       where d.IsActive = 0 and cd.AccountId = ?
                        """;
            st = connection.prepareStatement(sql);
            st.setObject(1,id );
           rs = st.executeQuery();
           if(rs.next()){
                  DiscountCode d = new DiscountCode();
                  d.setDiscountId(rs.getInt("DiscountId"));
                  d.setCode(rs.getString("Code"));
                  return d;
                 
           }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;

    }

    public void updateActiveAt1(int id){
        try {
            String sql = """
                         UPDATE DiscountCodes
                         SET IsActive = 1
                         WHERE DiscountId = ?
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, id);
            st.executeUpdate();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    
    }
    
    public static void main(String[] args) {
        DiscountCodeDAO dao = new DiscountCodeDAO();
        DiscountCode d = dao.getvoucher(6);
        System.out.println(d);
    }
}
