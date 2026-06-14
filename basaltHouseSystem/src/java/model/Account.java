package model;

import java.time.LocalDateTime;

public class Account {

    private int accountId;
    private int roleId;
    private String email;
    private String passwordHash;
    private boolean isEmailVerified;
    private boolean isActive;
    private LocalDateTime createdAt;
    private boolean isDeleted;
    private int failedAttempts;
    private boolean isLocked;

    public Account() {
    }

    public Account(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Account(int accountId, int roleId, String email, String passwordHash,
                   boolean isEmailVerified, boolean isActive,
                   LocalDateTime createdAt, boolean isDeleted,
                   int failedAttempts, boolean isLocked) {
        this.accountId = accountId;
        this.roleId = roleId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.isEmailVerified = isEmailVerified;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
        this.failedAttempts = failedAttempts;
        this.isLocked = isLocked;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
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

    public boolean isIsEmailVerified() {
        return isEmailVerified;
    }

    public void setIsEmailVerified(boolean isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
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

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public boolean isIsLocked() {
        return isLocked;
    }

    public void setIsLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }
}