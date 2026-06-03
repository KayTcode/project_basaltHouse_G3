/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author KayT
 */
public class DeliveryZone {
    private int zoneId;
    private String wardName;
    private String district;
    private String province;
    private boolean isActive;
    private boolean isDeleted;

    public DeliveryZone() {
    }

    public DeliveryZone(int zoneId, String wardName, String district, String province, boolean isActive, boolean isDeleted) {
        this.zoneId = zoneId;
        this.wardName = wardName;
        this.district = district;
        this.province = province;
        this.isActive = isActive;
        this.isDeleted = isDeleted;
    }

    public int getZoneId() {
        return zoneId;
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }

    public String getWardName() {
        return wardName;
    }

    public void setWardName(String wardName) {
        this.wardName = wardName;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    
}
