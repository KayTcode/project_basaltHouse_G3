/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author KayT
 */
public class DiscountCode {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DecimalFormat MONEY_FORMATTER = new DecimalFormat("#,###");

    private int discountId;
    private String code;
    private BigDecimal discountPercent;
    private BigDecimal discountAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive;
    private int createdBy;
    private LocalDateTime createdAt;
    private boolean isDeleted;
    private String description;
    private int totalDay;
    public DiscountCode() {
    }

    public DiscountCode(int discountId, String code, BigDecimal discountPercent, BigDecimal discountAmount, LocalDateTime startDate, LocalDateTime endDate, boolean isActive, int createdBy, LocalDateTime createdAt, boolean isDeleted) {
        this.discountId = discountId;
        this.code = code;
        this.discountPercent = discountPercent;
        this.discountAmount = discountAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
    }

    public DiscountCode(int discountId, String code, BigDecimal discountPercent, BigDecimal discountAmount, LocalDateTime startDate, LocalDateTime endDate, String description, int totalDay) {
        this.discountId = discountId;
        this.code = code;
        this.discountPercent = discountPercent;
        this.discountAmount = discountAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.totalDay = totalDay;
    }

    public DiscountCode(int discountId, String code) {
        this.discountId = discountId;
        this.code = code;
    }

   
  

    public int getTotalDay() {
        return totalDay;
    }

    public void setTotalDay(int totalDay) {
        this.totalDay = totalDay;
    }

    
    public int getDiscountId() {
        return discountId;
    }

    public void setDiscountId(int discountId) {
        this.discountId = discountId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
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

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public String getStartDateFormatted() {
        if (startDate == null) return "--";
        return startDate.format(DATE_FORMATTER);
    }

    public String getEndDateFormatted() {
        if (endDate == null) return "--";
        return endDate.format(DATE_FORMATTER);
    }

    public String getDiscountTypeName() {
        if (discountPercent != null && discountPercent.compareTo(java.math.BigDecimal.ZERO) > 0) {
            return "PERCENT";
        }
        return "AMOUNT";
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiscountValueFormatted() {
        if (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            return MONEY_FORMATTER.format(discountAmount) + "đ";
        }

        if (discountPercent != null) {
            return discountPercent.stripTrailingZeros().toPlainString() + "%";
        }

        return "0";
    }
    
    
}
