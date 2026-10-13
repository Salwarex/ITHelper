package ru.vit4liy.base.model.intersections;

import ru.vit4liy.base.bus.ObjectEntity;
import ru.vit4liy.base.bus.services.LocationPhoneBusService;
import ru.vit4liy.base.distribution.PrimaryForeignKey;
import ru.vit4liy.base.model.location.Location;
import ru.vit4liy.base.model.phone.Phone;
import ru.vit4liy.rsc.entity.IntersectionEntity;

@ObjectEntity(serviceInterface=LocationPhoneBusService.class)
public class LocationPhone implements IntersectionEntity {

    @PrimaryForeignKey(foreignClass = Location.class) private long locationId;
    @PrimaryForeignKey(foreignClass = Phone.class) private long phoneId;

    private long createdAt;
    private long lastChecked;

    @Override
    public Long getAId() {
        return locationId;
    }

    @Override
    public Long getBId() {
        return phoneId;
    }

    @Override
    public void setAId(Long aLong) {
        this.locationId = aLong;
    }

    @Override
    public void setBId(Long bLong) {
        this.phoneId = bLong;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public long getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(long phoneId) {
        this.phoneId = phoneId;
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
