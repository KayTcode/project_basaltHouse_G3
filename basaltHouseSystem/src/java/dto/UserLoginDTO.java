/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

/**
 *
 * @author KayT
 */
public class UserLoginDTO {

    private boolean success;
    private String errorMessage;
    private int accountId;
    private String email;
    private int roleId;
    private String roleName;
    private String fullName;
    private String avatarUrl;
    private String jwtToken;

    public UserLoginDTO() {
    }

    public static UserLoginDTO failure(String errorMessage) {
        UserLoginDTO dto = new UserLoginDTO();
        dto.success = false;
        dto.errorMessage = errorMessage;
        return dto;
    }

    public static UserLoginDTO success(int accountId, String email, int roleId, String roleName, String FullName, String avatarUrl, String jwtToken) {
        UserLoginDTO dto = new UserLoginDTO();
        dto.success = true;
        dto.accountId = accountId;
        dto.email = email;
        dto.roleId = roleId;
        dto.roleName = roleName;
        dto.fullName = FullName;
        dto.avatarUrl = avatarUrl;
        dto.jwtToken = jwtToken;
        return dto;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
    
    
}
