package ru.vit4liy.base.model.typemark;

import ru.vit4liy.base.bus.ObjectEntity;
import ru.vit4liy.base.bus.services.TypeMarkBusService;
import ru.vit4liy.base.distribution.ForeignId;
import ru.vit4liy.base.distribution.PrimaryId;
import ru.vit4liy.base.distribution.UniqueValueIdentifier;
import ru.vit4liy.base.model.type.Type;
import ru.vit4liy.rsc.entity.DatabaseEntity;

import java.util.Objects;

@ObjectEntity(serviceInterface=TypeMarkBusService.class)
public class TypeMark implements DatabaseEntity {
    @PrimaryId private long typeMarkId;
    @ForeignId(foreignClass = Type.class) private long typeId;
    @UniqueValueIdentifier private String displayName;
    private long createdAt;

    public TypeMark(long typeMarkId, long typeId, String displayName, long createdAt) {
        this.typeMarkId = typeMarkId;
        this.typeId = typeId;
        this.displayName = displayName;
        this.createdAt = createdAt;
    }

    public TypeMark() {
    }

    public Long getId() {
        return typeMarkId;
    }

    @Override
    public void setId(Long aLong) {
        this.typeMarkId = aLong;
    }

    public void setId(long id) {
        this.typeMarkId = id;
    }

    public long getTypeId() {
        return typeId;
    }

    public void setTypeId(long typeId) {
        this.typeId = typeId;
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
        TypeMark typeMark = (TypeMark) object;
        return Objects.equals(displayName, typeMark.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(displayName);
    }
}
