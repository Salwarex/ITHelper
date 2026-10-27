package ru.vit4liy.base.model.intersections;


import ru.vit4liy.base.bus.ObjectEntity;
import ru.vit4liy.base.bus.services.EmployeeLocationBusService;
import ru.vit4liy.base.distribution.PrimaryForeignKey;
import ru.vit4liy.base.model.employee.Employee;
import ru.vit4liy.base.model.location.Location;
import ru.vit4liy.rsc.entity.IntersectionEntity;

@ObjectEntity(serviceInterface= EmployeeLocationBusService.class)
public class EmployeeLocation implements IntersectionEntity {

    @PrimaryForeignKey(foreignClass = Employee.class) private long employeeId;
    @PrimaryForeignKey(foreignClass = Location.class) private long locationId;

    private long createdAt;
    private long lastChecked;

    @Override
    public Long getAId() {
        return employeeId;
    }

    @Override
    public Long getBId() {
        return locationId;
    }

    @Override
    public void setAId(Long aLong) {
        this.employeeId = aLong;
    }

    @Override
    public void setBId(Long bLong) {
        this.locationId = bLong;
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(long lastChecked) {
        this.lastChecked = lastChecked;
    }
}
