package ru.vit4liy.backend.data.model;

public class Device {
    private long id;
    private int typeId;
    private Integer typeMarkId;
    private String deviceSerial;
    private String description;
    private String status;
    private long lastChecked;

    public Device(long id, int typeId, Integer typeMarkId, String deviceSerial, String description, String status, long lastChecked) {
        this.id = id;
        this.typeId = typeId;
        this.typeMarkId = typeMarkId;
        this.deviceSerial = deviceSerial;
        this.description = description;
        this.status = status;
        this.lastChecked = lastChecked;
    }

    public Device() {
    }

    public long getId() {
        return id;
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
}
