package ru.vit4liy.frontend.data.web;

import ru.vit4liy.base.bus.BackendDataBus;
import ru.vit4liy.base.bus.services.*;
import ru.vit4liy.frontend.data.web.services.*;

public class WebBackendDataBus implements BackendDataBus {
    private final String address;
    private final int port;

    private final DeviceBusService deviceBusService;
    private final DeviceLocationBusService deviceLocationBusService;
    private final EmployeeBusService employeeBusService;
    private final EmployeeLocationBusService employeeLocationBusService;
    private final LocationBusService locationBusService;
    private final LocationPhoneBusService locationPhoneBusService;
    private final OSystemBusService oSystemBusService;
    private final PhoneBusService phoneBusService;
    private final TypeBusService typeBusService;
    private final TypeMarkBusService typeMarkBusService;
    private final DatabaseService databaseService;

    public WebBackendDataBus(String address, int port) {
        this.address = address;
        this.port = port;

        this.deviceBusService = new DeviceClientService();
        this.deviceLocationBusService = new DeviceLocationClientService();
        this.employeeBusService = new EmployeeClientService();
        this.employeeLocationBusService = new EmployeeLocationClientService();
        this.locationBusService = new LocationClientService();
        this.locationPhoneBusService = new LocationPhoneClientService();
        this.oSystemBusService = new OSystemClientService();
        this.phoneBusService = new PhoneClientService();
        this.typeBusService = new TypeClientService();
        this.typeMarkBusService = new TypeMarkClientService();
        this.databaseService = new DatabaseClientService();
    }

    @Override
    public DeviceBusService getDeviceService() {
        return deviceBusService;
    }

    @Override
    public DeviceLocationBusService getDeviceLocationService() {
        return deviceLocationBusService;
    }

    @Override
    public EmployeeBusService getEmployeeService() {
        return employeeBusService;
    }

    @Override
    public EmployeeLocationBusService getEmployeeLocationService() {
        return employeeLocationBusService;
    }

    @Override
    public LocationBusService getLocationService() {
        return locationBusService;
    }

    @Override
    public LocationPhoneBusService getLocationPhoneService() {
        return locationPhoneBusService;
    }

    @Override
    public OSystemBusService getOSystemService() {
        return oSystemBusService;
    }

    @Override
    public PhoneBusService getPhoneService() {
        return phoneBusService;
    }

    @Override
    public TypeBusService getTypeService() {
        return typeBusService;
    }

    @Override
    public TypeMarkBusService getTypeMarkService() {
        return typeMarkBusService;
    }

    @Override
    public DatabaseService getDatabaseService() {
        return databaseService;
    }
}
