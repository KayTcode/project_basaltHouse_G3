/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;

/**
 *
 * @author admin
 */
public class CustomerRanking {
    private int rankId;
    private String name;
    private BigDecimal minTotalSpent;
    private BigDecimal totalSpent;
    private int discount;
    private String nextRank;
    private BigDecimal nextRankMinSpent;
    private BigDecimal needSpent;
    public CustomerRanking() {
    }

    public CustomerRanking(int rankId, String name, BigDecimal minTotalSpent, BigDecimal totalSpent, int discount, String nextRank, BigDecimal nextRankMinSpent, BigDecimal needSpent) {
        this.rankId = rankId;
        this.name = name;
        this.minTotalSpent = minTotalSpent;
        this.totalSpent = totalSpent;
        this.discount = discount;
        this.nextRank = nextRank;
        this.nextRankMinSpent = nextRankMinSpent;
        this.needSpent = needSpent;
    }

    public int getRankId() {
        return rankId;
    }

    public void setRankId(int rankId) {
        this.rankId = rankId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getMinTotalSpent() {
        return minTotalSpent;
    }

    public void setMinTotalSpent(BigDecimal minTotalSpent) {
        this.minTotalSpent = minTotalSpent;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getNextRank() {
        return nextRank;
    }

    public void setNextRank(String nextRank) {
        this.nextRank = nextRank;
    }

    public BigDecimal getNextRankMinSpent() {
        return nextRankMinSpent;
    }

    public void setNextRankMinSpent(BigDecimal nextRankMinSpent) {
        this.nextRankMinSpent = nextRankMinSpent;
    }

    public BigDecimal getNeedSpent() {
        return needSpent;
    }

    public void setNeedSpent(BigDecimal needSpent) {
        this.needSpent = needSpent;
    }

  
    
}
