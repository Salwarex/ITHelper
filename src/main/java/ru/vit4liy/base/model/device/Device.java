package ru.vit4liy.base.model.device;

import ru.vit4liy.base.distribution.ForeignId;
import ru.vit4liy.base.distribution.PrimaryId;
import ru.vit4liy.base.model.device.system.OSystem;
import ru.vit4liy.base.model.type.Type;
import ru.vit4liy.base.model.typemark.TypeMark;
import ru.vit4liy.rsc.entity.DatabaseEntity;

import java.util.Objects;

public class Device implements DatabaseEntity {
    @PrimaryId private long deviceId;
    @ForeignId(foreignClass = Type.class) private int typeId;
    @ForeignId(foreignClass = TypeMark.class) private Integer typeMarkId;
    @ForeignId(foreignClass = OSystem.class) private Long osId;
    private String deviceSerial;
    private String description;
    private String status;
    private String ip;
    private long lastChecked;

    public Device(long deviceId, int typeId, Integer typeMarkId, Long osId, String deviceSerial, String description, String status, String ip, long lastChecked) {
        this.deviceId = deviceId;
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
        return deviceId;
    }

    @Override
    public void setId(Long aLong) {
        this.deviceId = aLong;
    }

    public void setId(long id) {
        this.deviceId = id;
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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Device device = (Device) object;
        return deviceId == device.deviceId && typeId == device.typeId && lastChecked == device.lastChecked && Objects.equals(typeMarkId, device.typeMarkId) && Objects.equals(osId, device.osId) && Objects.equals(deviceSerial, device.deviceSerial) && Objects.equals(description, device.description) && Objects.equals(status, device.status) && Objects.equals(ip, device.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId, typeId, typeMarkId, osId, deviceSerial, description, status, ip, lastChecked);
    }
}
