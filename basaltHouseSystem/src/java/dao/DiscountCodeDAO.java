package dao;

import model.Customer;
import model.DiscountCode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DiscountCodeDAO extends DBContext {

    public DiscountCode checkDiscountCode(String code) {
        DiscountCode dto = null;
        try {
            String sql = "SELECT DiscountId, Code, DiscountPercent, DiscountAmount " +
                         "FROM DiscountCodes " +
                         "WHERE Code = ? AND IsActive = 1 AND IsDeleted = 0 " +
                         "AND (StartDate IS NULL OR StartDate <= GETDATE()) " +
                         "AND (EndDate IS NULL OR EndDate >= GETDATE())";
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
            String sql = "SELECT c.CustomerId, c.FullName, r.RankName, r.DiscountValue " +
                         "FROM Customers c " +
                         "LEFT JOIN CustomerMemberships cm ON c.CustomerId = cm.CustomerId " +
                         "LEFT JOIN MembershipRanks r ON cm.RankId = r.RankId " +
                         "WHERE c.Phone = ? AND c.IsDeleted = 0";
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
}
