package dao;

import model.Customer;
import model.DiscountCode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DiscountCodeDAO extends DBContext {

    public DiscountCode checkDiscountCode(String code) {
        DiscountCode dto = null;
        try {
            String sql = "SELECT DiscountId, Code, DiscountPercent, DiscountAmount "
                    + "FROM DiscountCodes "
                    + "WHERE Code = ? AND IsActive = 1 AND IsDeleted = 0 "
                    + "AND (StartDate IS NULL OR StartDate <= GETDATE()) "
                    + "AND (EndDate IS NULL OR EndDate >= GETDATE())";
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, code);
            ResultSet rs = st.executeQuery();
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
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, phone);
            ResultSet rs = st.executeQuery();
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
                                                         DATEDIFF(DAY, StartDate, EndDate) AS DayTime
                                                  FROM DiscountCodes
                                                  WHERE IsActive = 1 and IsPublic = 1;
                         """;
          PreparedStatement  st = connection.prepareStatement(sql);
           ResultSet rs = st.executeQuery();
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
           PreparedStatement st = connection.prepareStatement(sql);
            st.setObject(1, id);
           ResultSet rs = st.executeQuery();
            if (rs.next()) {
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

    public void updateActiveAt1(int id) {
        try {
            String sql = """
                         UPDATE DiscountCodes
                         SET IsActive = 1
                         WHERE DiscountId = ?
                         """;
           PreparedStatement st = connection.prepareStatement(sql);
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
