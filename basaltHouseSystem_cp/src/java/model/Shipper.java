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
public class Shipper {
    private int shipperId;
    private Integer accountId;
    private String fullName;
    private String phone;
    private String address;
    private String avatarUrl;
    private String driverLicenseImg;
    private String vehicleRegistrationImg;
    private boolean isAvailable;
    private LocalDateTime createdAt;
    private boolean isDeleted;

    public Shipper() {
    }

    public Shipper(int shipperId, Integer accountId, String fullName, String phone, String address, String avatarUrl, String driverLicenseImg, String vehicleRegistrationImg, boolean isAvailable, LocalDateTime createdAt, boolean isDeleted) {
        this.shipperId = shipperId;
        this.accountId = accountId;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.avatarUrl = avatarUrl;
        this.driverLicenseImg = driverLicenseImg;
        this.vehicleRegistrationImg = vehicleRegistrationImg;
        this.isAvailable = isAvailable;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
    }

    public int getShipperId() {
        return shipperId;
    }

    public void setShipperId(int shipperId) {
        this.shipperId = shipperId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getDriverLicenseImg() {
        return driverLicenseImg;
    }

    public void setDriverLicenseImg(String driverLicenseImg) {
        this.driverLicenseImg = driverLicenseImg;
    }

    public String getVehicleRegistrationImg() {
        return vehicleRegistrationImg;
    }

    public void setVehicleRegistrationImg(String vehicleRegistrationImg) {
        this.vehicleRegistrationImg = vehicleRegistrationImg;
    }

    public boolean isIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
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
