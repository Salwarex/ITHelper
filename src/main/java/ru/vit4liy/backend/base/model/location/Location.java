package ru.vit4liy.backend.base.model.location;

import ru.vit4liy.rsc.entity.DatabaseEntity;

public class Location implements DatabaseEntity {
    private long id;
    private String key;
    private Long parentId;
    private String displayName;

    public Location(long id, String key, Long parentId, String displayName) {
        this.id = id;
        this.key = key;
        this.parentId = parentId;
        this.displayName = displayName;
    }

    public Location() {
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
}
