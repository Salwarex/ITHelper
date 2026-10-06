package ru.vit4liy.base.model.device.system;

import ru.vit4liy.base.distribution.PrimaryId;
import ru.vit4liy.rsc.entity.DatabaseEntity;

import java.util.Objects;

public class OSystem implements DatabaseEntity {
    @PrimaryId private long id;
    private String displayName;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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
        OSystem oSystem = (OSystem) object;
        return id == oSystem.id && Objects.equals(displayName, oSystem.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, displayName);
    }
}
