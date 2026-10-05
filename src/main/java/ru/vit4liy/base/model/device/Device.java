package ru.vit4liy.base.model.device;

import ru.vit4liy.rsc.entity.DatabaseEntity;

public class Device implements DatabaseEntity {
    private long id;
    private int typeId;
    private Integer typeMarkId;
    private Long osId;
    private String deviceSerial;
    private String description;
    private String status;
    private String ip;
    private long lastChecked;

    public Device(long id, int typeId, Integer typeMarkId, Long osId, String deviceSerial, String description, String status, String ip, long lastChecked) {
        this.id = id;
        this.typeId = typeId;
        this.typeMarkId = typeMarkId;
        this.deviceSerial = deviceSerial;
        this.description = description;
        this.status = status;
        this.lastChecked = lastChecked;
        this.osId = osId;
        this.ip = ip;
    }

    public Device() {
    }

    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long aLong) {
        this.id = aLong;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public Integer getTypeMarkId() {
        return typeMarkId;
    }

    public void setTypeMarkId(Integer typeMarkId) {
        this.typeMarkId = typeMarkId;
    }

    public String getDeviceSerial() {
        return deviceSerial;
    }

    public void setDeviceSerial(String deviceSerial) {
        this.deviceSerial = deviceSerial;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(long lastChecked) {
        this.lastChecked = lastChecked;
    }

    public Long getOsId() {
        return osId;
    }

    public void setOsId(Long osId) {
        this.osId = osId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
