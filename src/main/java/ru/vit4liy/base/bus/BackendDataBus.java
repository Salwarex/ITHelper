package ru.vit4liy.base.bus;

import ru.vit4liy.base.bus.services.*;
import ru.vit4liy.rsc.entity.Entity;
import ru.vit4liy.rsc.service.GenericDatabaseBusService;

public interface BackendDataBus {
    DeviceBusService getDeviceService();
    DeviceLocationBusService getDeviceLocationService();
    EmployeeBusService getEmployeeService();
    EmployeeLocationBusService getEmployeeLocationService();
    LocationBusService getLocationService();
    LocationPhoneBusService getLocationPhoneService();
    OSystemBusService getOSystemService();
    PhoneBusService getPhoneService();
    TypeBusService getTypeService();
    TypeMarkBusService getTypeMarkService();
    DatabaseService getDatabaseService();

    <T extends Entity> GenericDatabaseBusService<T> getService(Class<?> clazz);
}
