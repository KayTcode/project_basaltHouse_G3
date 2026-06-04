/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;

/**
 *
 * @author KayT
 */
public class MembershipRank {
    private int rankId;
    private String rankName;
    private BigDecimal minTotalSpent;
    private BigDecimal discountValue;
    private boolean isDeleted;

    public MembershipRank() {
    }

    public MembershipRank(int rankId, String rankName, BigDecimal minTotalSpent, BigDecimal discountValue, boolean isDeleted) {
        this.rankId = rankId;
        this.rankName = rankName;
        this.minTotalSpent = minTotalSpent;
        this.discountValue = discountValue;
        this.isDeleted = isDeleted;
    }

    public int getRankId() {
        return rankId;
    }

    public void setRankId(int rankId) {
        this.rankId = rankId;
    }

    public String getRankName() {
        return rankName;
    }

    public void setRankName(String rankName) {
        this.rankName = rankName;
    }

    public BigDecimal getMinTotalSpent() {
        return minTotalSpent;
    }

    public void setMinTotalSpent(BigDecimal minTotalSpent) {
        this.minTotalSpent = minTotalSpent;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    
}
