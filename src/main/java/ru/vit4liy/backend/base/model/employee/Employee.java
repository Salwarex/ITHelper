package ru.vit4liy.backend.base.model.employee;

import ru.vit4liy.rsc.entity.DatabaseEntity;

public class Employee implements DatabaseEntity  {
    private long id;
    private String surname;
    private String name;
    private String thirdname;
    private long createdAt;
    private long lastStatusChange;
    private long lastChecked;
    private int status;
    private String phone;

    public Employee(long id, String surname, String name, String thirdname, long createdAt, long lastStatusChange, long lastChecked, int status, String phone) {
        this.id = id;
        this.surname = surname;
        this.name = name;
        this.thirdname = thirdname;
        this.createdAt = createdAt;
        this.lastStatusChange = lastStatusChange;
        this.lastChecked = lastChecked;
        this.status = status;
        this.phone = phone;
    }

    public Employee() {
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
