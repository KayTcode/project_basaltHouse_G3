package dao;

import model.Customer;
import model.DiscountCode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.CustomerDiscountCode;

public class DiscountCodeDAO extends DBContext {

    public DiscountCode checkDiscountCode(String code) {
        DiscountCode dto = null;
        try {
            String sql = """
                         SELECT DiscountId, Code, DiscountPercent, DiscountAmount 
                         FROM DiscountCodes 
                         WHERE Code = ? AND IsActive = 1 AND IsDeleted = 0 
                         AND (StartDate IS NULL OR StartDate <= GETDATE())
                         AND (EndDate IS NULL OR EndDate >= GETDATE())
                         """;
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
            String sql = """
                      SELECT c.CustomerId, c.FullName, r.RankName, r.DiscountValue 
                      FROM Customers c 
                      LEFT JOIN CustomerMemberships cm ON c.CustomerId = cm.CustomerId
                      LEFT JOIN MembershipRanks r ON cm.RankId = r.RankId
                      WHERE c.Phone = ? AND c.IsDeleted = 0
                         """;
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
                                                         DATEDIFF(DAY, GETDATE(), EndDate) AS DayTime
                                                  FROM DiscountCodes
                                                  WHERE IsActive = 1 and IsPublic = 1 and IsDeleted = 0
                         """;
            PreparedStatement st = connection.prepareStatement(sql);
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
                                cd.[Status],
                                d.Description,
                                DATEDIFF(DAY, GETDATE(), d.EndDate) AS DayTime
                         FROM CustomerDiscountCodes cd
                         JOIN DiscountCodes d ON cd.DiscountId = d.DiscountId
                         WHERE d.IsActive = 1
                           AND d.IsDeleted = 0
                           AND cd.AccountId = ?
                           AND ISNULL(cd.IsUsed, 0) = 0
                         ORDER BY d.EndDate ASC
                         """;
            PreparedStatement st = connection.prepareStatement(sql);
            st.setObject(1, accountId);
            ResultSet rs = st.executeQuery();
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
                        rs.getString("Code"),
                        rs.getInt("Status"));
                list.add(c);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;
    }

    public CustomerDiscountCode getCustomerVoucherByCode(int accountId, String code) {
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
                                cd.[Status],
                                d.Description,
                                DATEDIFF(DAY, GETDATE(), d.EndDate) AS DayTime
                         FROM CustomerDiscountCodes cd
                         JOIN DiscountCodes d ON cd.DiscountId = d.DiscountId
                         WHERE d.IsActive = 1
                           AND d.IsDeleted = 0
                           AND cd.AccountId = ?
                           AND UPPER(d.Code) = UPPER(?)
                         """;
            PreparedStatement st = connection.prepareStatement(sql);
            st.setObject(1, accountId);
            st.setString(2, code);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return new CustomerDiscountCode(
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
                        rs.getString("Code"),
                        rs.getInt("Status"));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    public boolean updateCustomerVoucherStatus(int customerDiscountId, int status) {
        try {
            String sql = """
                         UPDATE CustomerDiscountCodes
                         SET [Status] = ?
                         WHERE CustomerDiscountId = ?
                         """;
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, status);
            st.setInt(2, customerDiscountId);
            return st.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return false;
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
}
