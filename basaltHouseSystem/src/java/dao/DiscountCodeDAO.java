package dao;

import model.Customer;
import model.DiscountCode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static java.util.Collections.list;
import java.util.List;
import model.CustomerDiscountCode;
import model.DiscountCode;

public class DiscountCodeDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;
    
    public DiscountCode checkDiscountCode(String code) {
        DiscountCode dto = null;
        try {
            String sql = "SELECT DiscountId, Code, DiscountPercent, DiscountAmount "
                    + "FROM DiscountCodes "
                    + "WHERE Code = ? AND IsActive = 1 AND IsDeleted = 0 "
                    + "AND (StartDate IS NULL OR StartDate <= GETDATE()) "
                    + "AND (EndDate IS NULL OR EndDate >= GETDATE())";
            st = connection.prepareStatement(sql);
            st.setString(1, code);
            rs = st.executeQuery();
            if (rs.next()) {
                dto = new DiscountCode();
                dto.setDiscountId(rs.getInt("DiscountId"));
                dto.setCode(rs.getString("Code"));
                dto.setDiscountPercent(rs.getBigDecimal("DiscountPercent"));
                dto.setDiscountAmount(rs.getBigDecimal("DiscountAmount"));
            }
        } catch (Exception e) {
            System.err.println("checkDiscountCode Error: " + e.getMessage());
        }
        return dto;
    }

    public Customer getCustomerMembership(String phone) {
        Customer dto = null;
        try {
            String sql = "SELECT c.CustomerId, c.FullName, r.RankName, r.DiscountValue "
                    + "FROM Customers c "
                    + "LEFT JOIN CustomerMemberships cm ON c.CustomerId = cm.CustomerId "
                    + "LEFT JOIN MembershipRanks r ON cm.RankId = r.RankId "
                    + "WHERE c.Phone = ? AND c.IsDeleted = 0";
            st = connection.prepareStatement(sql);
            st.setString(1, phone);
            rs = st.executeQuery();
            if (rs.next()) {
                dto = new Customer();
                dto.setCustomerId(rs.getInt("CustomerId"));
                dto.setFullName(rs.getString("FullName"));
                dto.setRankName(rs.getString("RankName"));
                dto.setDiscountValue(rs.getBigDecimal("DiscountValue"));
            }
        } catch (Exception e) {
            System.err.println("getCustomerMembership Error: " + e.getMessage());
        }
        return dto;
    }

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
        return list;
//    public DiscountCode getvoucher(int id) {
//        try {
//            String sql = """                       
//                      select top 1 Code ,d.DiscountId from DiscountCodes d
//                       join CustomerDiscountCodes cd on cd.DiscountId = d.DiscountId
//                       where d.IsActive = 0 and cd.AccountId = ?
//                        """;
//            st = connection.prepareStatement(sql);
//            st.setObject(1, id);
//            rs = st.executeQuery();
//            if (rs.next()) {
//                DiscountCode d = new DiscountCode();
//                d.setDiscountId(rs.getInt("DiscountId"));
//                d.setCode(rs.getString("Code"));
//                return d;
//
//            }
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//        }
//        return null;
//
//    }
//
//    public void updateActiveAt1(int id) {
//        try {
//            String sql = """
//                         SELECT cd.CustomerDiscountId,
//                                cd.AccountId,
//                                d.DiscountId,
//                                d.Code,
//                                d.DiscountAmount,
//                                d.DiscountPercent,
//                                d.StartDate,
//                                d.EndDate,
//                                cd.IsUsed,
//                                cd.UsedDate,
//                                d.Description,
//                                DATEDIFF(DAY, GETDATE(), d.EndDate) AS DayTime
//                         FROM CustomerDiscountCodes cd
//                         JOIN DiscountCodes d ON cd.DiscountId = d.DiscountId
//                         WHERE d.IsActive = 1
//                           AND d.IsDeleted = 0
//                           AND cd.AccountId = ?
//                         ORDER BY d.EndDate ASC
//                         """;
//            st = connection.prepareStatement(sql);
//            st.setObject(1, id);
//            rs = st.executeQuery();
//            while (rs.next()) {
//                CustomerDiscountCode c = new CustomerDiscountCode(
//                        rs.getInt("CustomerDiscountId"),
//                        rs.getInt("AccountId"),
//                        rs.getInt("DiscountId"),
//                        rs.getBigDecimal("DiscountPercent"),
//                        rs.getBigDecimal("DiscountAmount"),
//                        rs.getObject("StartDate", LocalDateTime.class),
//                        rs.getObject("EndDate", LocalDateTime.class),
//                        rs.getBoolean("IsUsed"),
//                        rs.getObject("UsedDate", LocalDateTime.class),
//                        rs.getString("Description"),
//                        rs.getInt("DayTime"),
//                        rs.getString("Code"));
//                list.add(c);
//            }
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//        }
//        return list;
//
//            st = connection.prepareStatement(sql);
//            st.setObject(1, id);
//            st.executeUpdate();
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//        }
//
//    }
//
//    public static void main(String[] args) {
//        DiscountCodeDAO dao = new DiscountCodeDAO();
//        DiscountCode d = dao.getvoucher(6);
//        System.out.println(d);
//     
  }


}
