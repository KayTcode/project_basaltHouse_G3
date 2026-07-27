/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.CustomerMembership;
import model.CustomerRanking;
import model.MembershipRank;

/**
 *
 * @author admin
 */
public class CustomerMembershipDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    public CustomerRanking getCustomeRankingById(int accountId) {
        try {
            String sql = """
                         SELECT currentRank.RankId,
                                currentRank.RankName,
                                currentRank.MinTotalSpent,
                                membership.TotalSpent,
                                currentRank.DiscountValue,
                                nextRank.RankName AS NextRank,
                                nextRank.MinTotalSpent AS NextRankMinSpent,
                                CASE
                                    WHEN nextRank.MinTotalSpent IS NULL THEN 0
                                    WHEN membership.TotalSpent
                                         >= nextRank.MinTotalSpent THEN 0
                                    ELSE nextRank.MinTotalSpent - membership.TotalSpent
                                END AS NeedMoreSpent
                         FROM Customers customer
                         JOIN CustomerMemberships membership
                           ON membership.CustomerId = customer.CustomerId
                         JOIN MembershipRanks currentRank
                           ON currentRank.RankId = membership.RankId
                         LEFT JOIN MembershipRanks nextRank
                           ON nextRank.RankId = (
                               SELECT TOP 1 candidate.RankId
                               FROM MembershipRanks candidate
                               WHERE candidate.IsDeleted = 0
                                 AND candidate.MinTotalSpent
                                     > currentRank.MinTotalSpent
                               ORDER BY candidate.MinTotalSpent,
                                        candidate.RankId
                           )
                         WHERE customer.AccountId = ?
                           AND customer.IsDeleted = 0
                         """;

            st = connection.prepareStatement(sql);
            st.setObject(1, accountId);
            rs = st.executeQuery();
            if (rs.next()) {
                return new CustomerRanking(rs.getInt("RankId"),
                        rs.getString("RankName"),
                        rs.getBigDecimal("MinTotalSpent"),
                        rs.getBigDecimal("TotalSpent"),
                        rs.getInt("DiscountValue"),
                        rs.getString("NextRank"),
                        rs.getBigDecimal("NextRankMinSpent"),
                        rs.getBigDecimal("NeedMoreSpent"));

            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
    public List<MembershipRank> getRankName(){
         List<MembershipRank>list = new ArrayList<>();
         try {
            String sql = """
                         select RankId,RankName,MinTotalSpent,DiscountValue,IsDeleted
                         from MembershipRanks
                         order by MinTotalSpent, RankId
                         """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
              while (rs.next()) {                 
                 MembershipRank m = new MembershipRank(rs.getInt("RankId"),
                         rs.getString("RankName"),
                          rs.getBigDecimal("MinTotalSpent"),
                        rs.getInt("DiscountValue"),
                         rs.getBoolean("IsDeleted"));
                 list.add(m);
              }
        } catch (Exception e) {
             System.err.println(e.getMessage());
        }
    return list;
    }
    public void creatMemberShip(int accountId){
        try {
            String sql = """
                         INSERT INTO CustomerMemberships (CustomerId, RankId, TotalSpent)
                         SELECT c.CustomerId,
                                (
                                    SELECT TOP 1 RankId
                                    FROM MembershipRanks
                                    WHERE IsDeleted = 0
                                    ORDER BY MinTotalSpent, RankId
                                ),
                                0
                         FROM Customers c
                         WHERE c.AccountId = ?
                           AND c.IsDeleted = 0
                           AND NOT EXISTS (
                               SELECT 1
                               FROM CustomerMemberships cm
                               WHERE cm.CustomerId = c.CustomerId
                           )
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, accountId);
            st.executeUpdate();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    
    }
    public List<CustomerMembership> getAllListMembershipRank() {
        return queryMemberships("");
    }
    
    public List<CustomerMembership> searchByName(String key) {
        return queryMemberships(key);
    }

    private List<CustomerMembership> queryMemberships(String key) {
        List<CustomerMembership> list = new ArrayList<>();
        String searchPattern = "%" + (key == null ? "" : key.trim()) + "%";
        try {
            String sql = """
                         SELECT customer.CustomerId,
                                customer.FullName,
                                customer.Phone,
                                rank.RankName,
                                ISNULL(membership.TotalSpent, 0) AS TotalSpent,
                                ISNULL(rank.DiscountValue, 0) AS DiscountValue
                         FROM Customers customer
                         JOIN Accounts account
                           ON account.AccountId = customer.AccountId
                          AND account.IsDeleted = 0
                         LEFT JOIN CustomerMemberships membership
                           ON membership.CustomerId = customer.CustomerId
                         LEFT JOIN MembershipRanks rank
                           ON rank.RankId = membership.RankId
                         WHERE customer.IsDeleted = 0
                           AND CONCAT(customer.FullName, N' ',
                                      customer.Phone, N' ',
                                      customer.CustomerId) LIKE ?
                         ORDER BY ISNULL(membership.TotalSpent, 0) DESC,
                                  customer.FullName
                         """;
            st = connection.prepareStatement(sql);
            st.setString(1, searchPattern);
            rs = st.executeQuery();
            while (rs.next()) {
                list.add(mapMembership(rs));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;
    }

    private CustomerMembership mapMembership(ResultSet result)
            throws SQLException {
        CustomerMembership membership = new CustomerMembership();
        membership.setCustomerId(result.getInt("CustomerId"));
        membership.setCustomerName(result.getString("FullName"));
        membership.setPhone(result.getString("Phone"));
        membership.setRankName(result.getString("RankName"));
        membership.setTotalSpent(result.getBigDecimal("TotalSpent"));
        membership.setDiscountValue(result.getBigDecimal("DiscountValue"));
        return membership;
    }
    public boolean updateRanking(MembershipRank m){
        try {
            String sql = """
                         UPDATE [dbo].[MembershipRanks]
                            SET [RankName] = ?
                               ,[MinTotalSpent] = ?
                               ,[DiscountValue] = ?
                               ,[IsDeleted] = ?
                          WHERE RankId = ?
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, m.getRankName());
            st.setObject(2, m.getMinTotalSpent());
            st.setObject(3, m.getDiscountValue());
            st.setObject(4, m.isIsDeleted());
            st.setObject(5, m.getRankId());
            return st.executeUpdate() == 1;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public boolean hasOtherActiveRank(int rankId) {
        try {
            String sql = """
                         SELECT 1
                         FROM MembershipRanks
                         WHERE IsDeleted = 0
                           AND RankId <> ?
                         """;
            st = connection.prepareStatement(sql);
            st.setInt(1, rankId);
            rs = st.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public int inseartRanking(MembershipRank membershipRank) throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is not available");
        }

        String sql = """
                     INSERT INTO [dbo].[MembershipRanks]
                                ([RankName]
                                ,[MinTotalSpent]
                                ,[DiscountValue]
                                ,[IsDeleted])
                     OUTPUT INSERTED.RankId
                          VALUES
                                (?,?,?,?)
                     """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, membershipRank.getRankName());
            statement.setBigDecimal(2, membershipRank.getMinTotalSpent());
            statement.setInt(3, membershipRank.getDiscountValue());
            statement.setBoolean(4, membershipRank.isIsDeleted());

            try (ResultSet generatedKey = statement.executeQuery()) {
                if (generatedKey.next()) {
                    return generatedKey.getInt("RankId");
                }
            }
        }

        throw new SQLException("Membership rank was inserted without a generated RankId");
    }
    public boolean updateLocked(int id){
        try {
            String sql = """
                         UPDATE [dbo].[CustomerMemberships]
                            SET [IsDelete] = CASE WHEN [IsDelete] = 1 THEN 0 ELSE 1 END
                          WHERE CustomerId = ?
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, id);
            return st.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
    public void addTotalSpent(int customerId, BigDecimal amount) {
        if (amount == null) {
            return;
        }

        boolean oldAutoCommit = true;
        try {
            oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            createMembershipIfMissing(customerId);
            updateTotalSpentAndRank(customerId, amount);
            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackError) {
                System.err.println(rollbackError.getMessage());
            }
            System.err.println("addTotalSpent Error: " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(oldAutoCommit);
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private void createMembershipIfMissing(int customerId)
            throws SQLException {
        String sql = """
                     INSERT INTO CustomerMemberships
                            (CustomerId, RankId, TotalSpent)
                     SELECT customer.CustomerId,
                            (
                                SELECT TOP 1 RankId
                                FROM MembershipRanks
                                WHERE IsDeleted = 0
                                ORDER BY MinTotalSpent, RankId
                            ),
                            0
                     FROM Customers customer
                     WHERE customer.CustomerId = ?
                       AND customer.IsDeleted = 0
                       AND NOT EXISTS (
                           SELECT 1
                           FROM CustomerMemberships membership
                           WHERE membership.CustomerId = customer.CustomerId
                       )
                     """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            statement.executeUpdate();
        }
    }

    private void updateTotalSpentAndRank(
            int customerId, BigDecimal amount) throws SQLException {
        String sql = """
                     UPDATE membership
                     SET TotalSpent = membership.TotalSpent + ?,
                         RankId = COALESCE((
                             SELECT TOP 1 RankId
                             FROM MembershipRanks
                             WHERE IsDeleted = 0
                               AND MinTotalSpent
                                   <= membership.TotalSpent + ?
                             ORDER BY MinTotalSpent DESC, RankId DESC
                         ), membership.RankId)
                     FROM CustomerMemberships membership
                     WHERE membership.CustomerId = ?
                     """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBigDecimal(1, amount);
            statement.setBigDecimal(2, amount);
            statement.setInt(3, customerId);
            statement.executeUpdate();
        }
    }
}
