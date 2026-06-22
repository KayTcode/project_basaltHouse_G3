/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import model.Account;
import java.util.Date;
/**
 *
 * @author MSI
 */
public class AccountViewDTO {
      private Account account;   // model gốc, giữ nguyên
    private String roleName;
    private String fullName;
    private String phone;

    public AccountViewDTO(Account account, String roleName, String fullName, String phone) {
        this.account = account;
        this.roleName = roleName;
        this.fullName = fullName;
        this.phone = phone;
    }

    public AccountViewDTO() {
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
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
    
      /** Helper cho JSTL &lt;fmt:formatDate&gt; — Account gốc dùng LocalDateTime, JSTL cần java.util.Date */
    public Date getCreatedAtDate() {
        if (account == null || account.getCreatedAt() == null) return null;
        return Date.from(account.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant());
    }
}
