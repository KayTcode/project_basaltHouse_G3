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
    private String customerName;
    private String phone;
    private String rankName;
    private BigDecimal discountValue;
    private BigDecimal totalSpent;
    private BigDecimal progressPercent;
    private String nextRankName;
    private String status;
    

    public CustomerMembership() {
    }

    public CustomerMembership(int membershipId, int customerId, int rankId, BigDecimal totalSpent) {
        this.membershipId = membershipId;
        this.customerId = customerId;
        this.rankId = rankId;
        this.totalSpent = totalSpent;
    }

    public CustomerMembership(int customerId, String customerName, String rankName, BigDecimal discountValue, BigDecimal totalSpent) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.rankName = rankName;
        this.discountValue = discountValue;
        this.totalSpent = totalSpent;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRankName() {
        return rankName;
    }

    public void setRankName(String rankName) {
        this.rankName = rankName;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
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

    public BigDecimal getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(BigDecimal progressPercent) {
        this.progressPercent = progressPercent;
    }

    public String getNextRankName() {
        return nextRankName;
    }

    public void setNextRankName(String nextRankName) {
        this.nextRankName = nextRankName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
}
