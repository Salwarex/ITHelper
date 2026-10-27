package ru.vit4liy.base.model.type;

import ru.vit4liy.base.bus.ObjectEntity;
import ru.vit4liy.base.bus.services.TypeBusService;
import ru.vit4liy.base.distribution.PrimaryId;
import ru.vit4liy.base.distribution.UniqueValueIdentifier;
import ru.vit4liy.rsc.entity.DatabaseEntity;

import java.util.Objects;

@ObjectEntity(serviceInterface= TypeBusService.class)
public class Type implements DatabaseEntity {
    @PrimaryId private long typeId;
    @UniqueValueIdentifier private String displayName;
    private long createdAt;

    public Type(long id, String displayName, long createdAt) {
        this.typeId = id;
        this.displayName = displayName;
        this.createdAt = createdAt;
    }

    public Type() {
    }

    public Long getId() {
        return typeId;
    }

    @Override
    public void setId(Long aLong) {
        this.typeId = aLong;
    }

    public void setId(long id) {
        this.typeId = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Type type = (Type) object;
        return Objects.equals(displayName, type.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(displayName);
    }
}
