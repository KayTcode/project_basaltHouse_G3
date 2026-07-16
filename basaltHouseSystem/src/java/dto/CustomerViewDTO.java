package dto;

import model.Account;
import java.math.BigDecimal;
import java.util.Date;

public class CustomerViewDTO {

    private Account account;       // Thông tin tài khoản (email, isLocked, createdAt...)
    private String fullName;
    private String phone;
    private String avatarUrl;      // Ảnh đại diện của khách hàng
    private int rankId;
    private String rankName;
    private BigDecimal totalSpent;

    public CustomerViewDTO() {
    }

    public CustomerViewDTO(Account account, String fullName, String phone, String avatarUrl,
            int rankId, String rankName, BigDecimal totalSpent) {
        this.account = account;
        this.fullName = fullName;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
        this.rankId = rankId;
        this.rankName = rankName;
        this.totalSpent = totalSpent;
    }

    // ── Getters & Setters ──
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
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

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }


    public Date getCreatedAtDate() {
        if (account == null || account.getCreatedAt() == null) {
            return null;
        }
        return Date.from(account.getCreatedAt()
                .atZone(java.time.ZoneId.systemDefault())
                .toInstant());
    }
}
