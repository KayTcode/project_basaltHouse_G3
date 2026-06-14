/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.security.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author admin
 */
public class CustomerDiscountCode {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private int customerDiscountId;
    private int accountId;
    private int discountId;
     private BigDecimal discountPercent;
    private BigDecimal discountAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isUsed;
    private LocalDateTime usedDate;
    private String description;
    private int dayTotal;
    public CustomerDiscountCode() {
    }

    public CustomerDiscountCode(int customerDiscountId, int accountId, int discountId, BigDecimal discountPercent, BigDecimal discountAmount, LocalDateTime startDate, LocalDateTime endDate, boolean isUsed, LocalDateTime usedDate, String description, int dayTotal) {
        this.customerDiscountId = customerDiscountId;
        this.accountId = accountId;
        this.discountId = discountId;
        this.discountPercent = discountPercent;
        this.discountAmount = discountAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isUsed = isUsed;
        this.usedDate = usedDate;
        this.description = description;
        this.dayTotal = dayTotal;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public int getCustomerDiscountId() {
        return customerDiscountId;
    }

    public void setCustomerDiscountId(int customerDiscountId) {
        this.customerDiscountId = customerDiscountId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getDiscountId() {
        return discountId;
    }

    public void setDiscountId(int discountId) {
        this.discountId = discountId;
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }
     public String getDiscountPercentFormatted() {
        if (discountPercent == null) {
            return "0%";
        }

        return discountPercent.stripTrailingZeros().toPlainString() + "%";
    }

    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }
  public String getEndDateFormatted() {
        if (endDate == null) {
            return "";
        }

        return endDate.format(DATE_FORMATTER);
    }
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public boolean isIsUsed() {
        return isUsed;
    }

    public void setIsUsed(boolean isUsed) {
        this.isUsed = isUsed;
    }

    public LocalDateTime getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(LocalDateTime usedDate) {
        this.usedDate = usedDate;
    }

    public int getDayTotal() {
        return dayTotal;
    }

    public void setDayTotal(int dayTotal) {
        this.dayTotal = dayTotal;
    }

   

}
