package ru.vit4liy.base.model.employee;

import ru.vit4liy.base.distribution.PrimaryId;
import ru.vit4liy.rsc.entity.DatabaseEntity;

import java.util.Objects;

public class Employee implements DatabaseEntity  {
    @PrimaryId private long employeeId;
    private String surname;
    private String name;
    private String thirdname;
    private long createdAt;
    private long lastStatusChange;
    private long lastChecked;
    private int status;
    private String phone;

    public Employee(long employeeId, String surname, String name, String thirdname, long createdAt, long lastStatusChange, long lastChecked, int status, String phone) {
        this.employeeId = employeeId;
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
        return employeeId;
    }

    @Override
    public void setId(Long aLong) {
        this.employeeId = aLong;
    }

    public void setId(long id) {
        this.employeeId = id;
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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Employee employee = (Employee) object;
        return employeeId == employee.employeeId && createdAt == employee.createdAt && lastStatusChange == employee.lastStatusChange && lastChecked == employee.lastChecked && status == employee.status && Objects.equals(surname, employee.surname) && Objects.equals(name, employee.name) && Objects.equals(thirdname, employee.thirdname) && Objects.equals(phone, employee.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, surname, name, thirdname, createdAt, lastStatusChange, lastChecked, status, phone);
    }
}
