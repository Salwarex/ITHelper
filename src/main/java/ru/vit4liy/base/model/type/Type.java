package ru.vit4liy.base.model.type;

import ru.vit4liy.rsc.entity.DatabaseEntity;

public class Type implements DatabaseEntity {
    private long id;
    private String displayName;
    private long createdAt;

    public Type(long id, String displayName, long createdAt) {
        this.id = id;
        this.displayName = displayName;
        this.createdAt = createdAt;
    }

    public Type() {
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
}
