package ru.vit4liy.backend.data.model;

public class Location {
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

    public long getId() {
        return id;
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
