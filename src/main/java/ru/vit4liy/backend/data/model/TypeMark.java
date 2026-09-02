package ru.vit4liy.backend.data.model;

public class TypeMark {
    private long id;
    private long typeId;
    private String displayName;
    private long createdAt;

    public TypeMark(long id, long typeId, String displayName, long createdAt) {
        this.id = id;
        this.typeId = typeId;
        this.displayName = displayName;
        this.createdAt = createdAt;
    }

    public TypeMark() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
