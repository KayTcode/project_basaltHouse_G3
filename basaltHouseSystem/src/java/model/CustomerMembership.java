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
public class CustomerMembership {
    private int membershipId;
    private int customerId;
    private int rankId;
    private BigDecimal totalSpent;

    public CustomerMembership() {
    }

    public CustomerMembership(int membershipId, int customerId, int rankId, BigDecimal totalSpent) {
        this.membershipId = membershipId;
        this.customerId = customerId;
        this.rankId = rankId;
        this.totalSpent = totalSpent;
    }

    public int getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(int membershipId) {
        this.membershipId = membershipId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getRankId() {
        return rankId;
    }

    public void setRankId(int rankId) {
        this.rankId = rankId;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }
    
    
}
