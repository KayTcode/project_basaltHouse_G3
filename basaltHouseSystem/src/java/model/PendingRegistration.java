/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDateTime;

/**
 *
 * @author KayT
 */
public class PendingRegistration {
    private int pendingId;
    private String email;
    private String passwordHash;
    private String fullName;
    private String phone;
    private String registerRole;
    private String otpCode;
    private LocalDateTime otpExpiredAt;
    private boolean isUsed;
    private int attemptCount;
    private LocalDateTime createdAt;
    private boolean isDeleted;

    public PendingRegistration() {
    }

    public PendingRegistration(int pendingId, String email, String passwordHash, String fullName, String phone, String registerRole, String otpCode, LocalDateTime otpExpiredAt, boolean isUsed, int attemptCount, LocalDateTime createdAt, boolean isDeleted) {
        this.pendingId = pendingId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.phone = phone;
        this.registerRole = registerRole;
        this.otpCode = otpCode;
        this.otpExpiredAt = otpExpiredAt;
        this.isUsed = isUsed;
        this.attemptCount = attemptCount;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
    }

    public int getPendingId() {
        return pendingId;
    }

    public void setPendingId(int pendingId) {
        this.pendingId = pendingId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRegisterRole() {
        return registerRole;
    }

    public void setRegisterRole(String registerRole) {
        this.registerRole = registerRole;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public LocalDateTime getOtpExpiredAt() {
        return otpExpiredAt;
    }

    public void setOtpExpiredAt(LocalDateTime otpExpiredAt) {
        this.otpExpiredAt = otpExpiredAt;
    }

    public boolean isIsUsed() {
        return isUsed;
    }

    public void setIsUsed(boolean isUsed) {
        this.isUsed = isUsed;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
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
    
    
}
