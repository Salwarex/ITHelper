package ru.vit4liy.base.model.device.system;

import ru.vit4liy.rsc.entity.DatabaseEntity;

public class OSystem implements DatabaseEntity {
    private long id;
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
}
