package ru.vit4liy.backend.data.model;

public class Employee {
    private long id;
    private String surname;
    private String name;
    private String thirdname;
    private long createdAt;
    private long lastStatusChange;
    private long lastChecked;
    private int status; // 1 - active, 0 - inactive

    public Employee(long id, String surname, String name, String thirdname, long createdAt, long lastStatusChange, long lastChecked, int status) {
        this.id = id;
        this.surname = surname;
        this.name = name;
        this.thirdname = thirdname;
        this.createdAt = createdAt;
        this.lastStatusChange = lastStatusChange;
        this.lastChecked = lastChecked;
        this.status = status;
    }

    public Employee() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThirdname() {
        return thirdname;
    }

    public void setThirdname(String thirdname) {
        this.thirdname = thirdname;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getLastStatusChange() {
        return lastStatusChange;
    }

    public void setLastStatusChange(long lastStatusChange) {
        this.lastStatusChange = lastStatusChange;
    }

    public long getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(long lastChecked) {
        this.lastChecked = lastChecked;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
