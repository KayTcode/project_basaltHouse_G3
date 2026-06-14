/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
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
                                  ELSE nextRank.MinTotalSpent - c.TotalSpent
                              END AS NeedMoreSpent
                          
                          FROM CustomerMemberships c 
                          JOIN MembershipRanks m ON m.RankId = c.RankId
                          LEFT JOIN MembershipRanks nextRank ON nextRank.RankId = m.RankId + 1
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
                         select RankName,MinTotalSpent,DiscountValue from MembershipRanks
                         """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
             while (rs.next()) {                 
                 MembershipRank m = new MembershipRank(rs.getString("RankName"),
                         rs.getBigDecimal("MinTotalSpent"),
                        rs.getInt("DiscountValue"));
                 list.add(m);
             }
        } catch (Exception e) {
             System.err.println(e.getMessage());
        }
    return list;
    }
    
    
}
