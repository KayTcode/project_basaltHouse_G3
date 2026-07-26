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
   SELECT m.RankId,m.RankName,m.MinTotalSpent,c.TotalSpent,m.DiscountValue,
                          nextRank.RankName AS NextRank,nextRank.MinTotalSpent AS NextRankMinSpent,
                          CASE
                                  WHEN nextRank.MinTotalSpent IS NULL THEN 0
                                  WHEN c.TotalSpent >= nextRank.MinTotalSpent THEN 0
                                  ELSE nextRank.MinTotalSpent - c.TotalSpent
                              END AS NeedMoreSpent
                          
                          FROM CustomerMemberships c 
                          JOIN MembershipRanks m ON m.RankId = c.RankId
                          OUTER APPLY (
                              SELECT TOP 1 candidate.RankName, candidate.MinTotalSpent
                              FROM MembershipRanks candidate
                              WHERE candidate.IsDeleted = 0
                                AND candidate.MinTotalSpent > m.MinTotalSpent
                              ORDER BY candidate.MinTotalSpent, candidate.RankId
                          ) nextRank
                          JOIN Customers cm ON cm.CustomerId = c.CustomerId
                          JOIN Accounts a ON a.AccountId = cm.AccountId
                          where a.AccountId = ?
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
                         SELECT c.CustomerId, defaultRank.RankId, 0
                         FROM Customers c
                         CROSS JOIN (
                             SELECT TOP 1 RankId
                             FROM MembershipRanks
                             WHERE IsDeleted = 0
                             ORDER BY MinTotalSpent, RankId
                         ) defaultRank
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
        List<CustomerMembership> list = new ArrayList<>();
        try {
            String sql = """
                         select c.CustomerId,
                                c.FullName,
                                c.Phone,
                                m.RankName,
                                ISNULL(cm.TotalSpent, 0) as TotalSpent,
                                ISNULL(m.DiscountValue, 0) as DiscountValue
                         from Customers c
                         join Accounts a on c.AccountId = a.AccountId and a.IsDeleted = 0
                         left join CustomerMemberships cm on cm.CustomerId = c.CustomerId
                         left join MembershipRanks m on cm.RankId = m.RankId
                         where c.IsDeleted = 0
                         order by ISNULL(cm.TotalSpent, 0) desc, c.FullName
                         """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {                
                CustomerMembership s = new CustomerMembership();
                s.setCustomerId(rs.getInt("CustomerId"));
                s.setCustomerName(rs.getString("FullName"));
                s.setPhone(rs.getString("Phone"));
                s.setRankName(rs.getString("RankName"));
                s.setTotalSpent(rs.getBigDecimal("TotalSpent"));
                s.setDiscountValue(rs.getBigDecimal("DiscountValue"));
                list.add(s);
            }
            
            
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;
    
    }
    
    public List<CustomerMembership> searchByName(String key, int rankId) {
        List<CustomerMembership> list = new ArrayList<>();
        try {
            String sql = """
                         select c.CustomerId,
                                c.FullName,
                                c.Phone,
                                m.RankName,
                                ISNULL(cm.TotalSpent, 0) as TotalSpent,
                                ISNULL(m.DiscountValue, 0) as DiscountValue
                         from Customers c
                         join Accounts a on c.AccountId = a.AccountId and a.IsDeleted = 0
                         left join CustomerMemberships cm on cm.CustomerId = c.CustomerId
                         left join MembershipRanks m on cm.RankId = m.RankId
                         where c.IsDeleted = 0
                           and (? = ''
                                or c.FullName like ?
                                or c.Phone like ?
                                or cast(c.CustomerId as nvarchar(20)) like ?)
                           and (? = 0 or cm.RankId = ?)
                         order by ISNULL(cm.TotalSpent, 0) desc, c.FullName
                          """;
            st = connection.prepareStatement(sql);
            String searchPattern = "%" + key + "%";
            st.setString(1, key);
            st.setString(2, searchPattern);
            st.setString(3, searchPattern);
            st.setString(4, searchPattern);
            st.setInt(5, rankId);
            st.setInt(6, rankId);
            rs = st.executeQuery();
            while (rs.next()) {                
                CustomerMembership s = new CustomerMembership();
                s.setCustomerId(rs.getInt("CustomerId"));
                s.setCustomerName(rs.getString("FullName"));
                s.setPhone(rs.getString("Phone"));
                s.setRankName(rs.getString("RankName"));
                s.setTotalSpent(rs.getBigDecimal("TotalSpent"));
                s.setDiscountValue(rs.getBigDecimal("DiscountValue"));
                list.add(s);
            }
            
            
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;
    
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
        try {
           
            String sql = """
                         UPDATE CustomerMemberships 
                         SET TotalSpent = TotalSpent + ? 
                         WHERE CustomerId = ?
                         """;
            st = connection.prepareStatement(sql);
            st.setBigDecimal(1, amount);
            st.setInt(2, customerId);
            int rows = st.executeUpdate();
            
           
            if (rows > 0) {
                String updateRankSql = """
                    UPDATE CustomerMemberships
                    SET RankId = (
                        SELECT TOP 1 RankId
                        FROM MembershipRanks
                        WHERE MinTotalSpent <= CustomerMemberships.TotalSpent
                        AND IsDeleted = 0
                        ORDER BY MinTotalSpent DESC
                    )
                    WHERE CustomerId = ?
                """;
                st = connection.prepareStatement(updateRankSql);
                st.setInt(1, customerId);
                st.executeUpdate();
            }
            
            
            if (rows == 0) {
                
                String findAccountSql = "SELECT AccountId FROM Customers WHERE CustomerId = ? AND IsDeleted = 0";
                st = connection.prepareStatement(findAccountSql);
                st.setInt(1, customerId);
                rs = st.executeQuery();
                if (rs.next()) {
                    int accountId = rs.getInt("AccountId");
                    creatMemberShip(accountId);
                    
                    
                    st = connection.prepareStatement(sql);
                    st.setBigDecimal(1, amount);
                    st.setInt(2, customerId);
                    st.executeUpdate();
                    
                   
                    String updateRankSql = """
                        UPDATE CustomerMemberships
                        SET RankId = (
                            SELECT TOP 1 RankId
                            FROM MembershipRanks
                            WHERE MinTotalSpent <= CustomerMemberships.TotalSpent
                            AND IsDeleted = 0
                            ORDER BY MinTotalSpent DESC
                        )
                        WHERE CustomerId = ?
                    """;
                    st = connection.prepareStatement(updateRankSql);
                    st.setInt(1, customerId);
                    st.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.err.println("addTotalSpent Error: " + e.getMessage());
        }
    }
}
