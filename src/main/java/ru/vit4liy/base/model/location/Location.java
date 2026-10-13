package ru.vit4liy.base.model.location;

import ru.vit4liy.base.bus.ObjectEntity;
import ru.vit4liy.base.bus.services.LocationBusService;
import ru.vit4liy.base.distribution.ForeignId;
import ru.vit4liy.base.distribution.PrimaryId;
import ru.vit4liy.base.distribution.UniqueValueIdentifier;
import ru.vit4liy.rsc.entity.DatabaseEntity;

import java.util.Objects;

@ObjectEntity(serviceInterface= LocationBusService.class)
public class Location implements DatabaseEntity {
    @PrimaryId private long locationId;
    private String key;
    @ForeignId(foreignClass = Location.class) private Long parentId;
    @UniqueValueIdentifier private String displayName;

    public Location(long locationId, String key, Long parentId, String displayName) {
        this.locationId = locationId;
        this.key = key;
        this.parentId = parentId;
        this.displayName = displayName;
    }

    public Location() {
    }

    public Long getId() {
        return locationId;
    }

    @Override
    public void setId(Long aLong) {
        this.locationId = aLong;
    }

    public void setId(long id) {
        this.locationId = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Location location = (Location) object;
        return Objects.equals(key, location.key) && Objects.equals(displayName, location.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, displayName);
    }
}
