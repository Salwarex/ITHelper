package ru.vit4liy.base.model.intersections;


import ru.vit4liy.base.distribution.PrimaryForeignKey;
import ru.vit4liy.base.model.device.Device;
import ru.vit4liy.base.model.location.Location;
import ru.vit4liy.rsc.entity.IntersectionEntity;

public class DeviceLocation implements IntersectionEntity {

    @PrimaryForeignKey(foreignClass = Device.class) private long deviceId;
    @PrimaryForeignKey(foreignClass = Location.class) private long locationId;

    private long createdAt;
    private long lastChecked;

    @Override
    public Long getAId() {
        return deviceId;
    }

    @Override
    public Long getBId() {
        return locationId;
    }

    @Override
    public void setAId(Long aLong) {
        this.deviceId = aLong;
    }

    @Override
    public void setBId(Long bLong) {
        this.locationId = bLong;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(long lastChecked) {
        this.lastChecked = lastChecked;
    }
}
