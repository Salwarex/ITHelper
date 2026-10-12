package ru.vit4liy.backend.modules.data;

import ru.vit4liy.backend.modules.data.device.DeviceRepository;
import ru.vit4liy.backend.modules.data.device.DeviceService;
import ru.vit4liy.backend.modules.data.employee.EmployeeRepository;
import ru.vit4liy.backend.modules.data.employee.EmployeeService;
import ru.vit4liy.backend.modules.data.intersections.deviceLocation.DeviceLocationRepository;
import ru.vit4liy.backend.modules.data.intersections.deviceLocation.DeviceLocationService;
import ru.vit4liy.backend.modules.data.intersections.employeeLocation.EmployeeLocationRepository;
import ru.vit4liy.backend.modules.data.intersections.employeeLocation.EmployeeLocationService;
import ru.vit4liy.backend.modules.data.intersections.locationPhone.LocationPhoneRepository;
import ru.vit4liy.backend.modules.data.intersections.locationPhone.LocationPhoneService;
import ru.vit4liy.backend.modules.data.location.LocationRepository;
import ru.vit4liy.backend.modules.data.location.LocationService;
import ru.vit4liy.backend.modules.data.os.OSystemRepository;
import ru.vit4liy.backend.modules.data.os.OSystemService;
import ru.vit4liy.backend.modules.data.phone.PhoneRepository;
import ru.vit4liy.backend.modules.data.phone.PhoneService;
import ru.vit4liy.backend.modules.data.type.TypeRepository;
import ru.vit4liy.backend.modules.data.type.TypeService;
import ru.vit4liy.backend.modules.data.typemark.TypeMarkRepository;
import ru.vit4liy.backend.modules.data.typemark.TypeMarkService;
import ru.vit4liy.base.bus.BackendDataBus;
import ru.vit4liy.base.bus.services.*;
import ru.vit4liy.perm.PermissionService;
import ru.vit4liy.rsc.DatabaseManager;

public class LocalDataBus implements BackendDataBus {
    private final DeviceBusService deviceService;
    private final DeviceLocationBusService deviceLocationService;
    private final EmployeeBusService employeeService;
    private final EmployeeLocationBusService employeeLocationService;
    private final LocationBusService locationService;
    private final LocationPhoneBusService locationPhoneService;
    private final OSystemBusService oSystemService;
    private final PhoneBusService phoneService;
    private final TypeBusService typeService;
    private final TypeMarkBusService typeMarkService;
    private final DatabaseService databaseService;

    public LocalDataBus(
            PermissionService permissionService,
            DatabaseManager databaseManager,
            DeviceRepository deviceRepository,
            EmployeeRepository employeeRepository,
            LocationRepository locationRepository,
            PhoneRepository phoneRepository,
            TypeRepository typeRepository,
            TypeMarkRepository typeMarkRepository,
            DeviceLocationRepository deviceLocationRepository,
            EmployeeLocationRepository employeeLocationRepository,
            LocationPhoneRepository locationPhoneRepository,
            OSystemRepository oSystemRepository
    ) {
        this.deviceService = new DeviceService(deviceRepository, permissionService);
        this.employeeService = new EmployeeService(employeeRepository, permissionService);
        this.locationService = new LocationService(locationRepository, permissionService);
        this.phoneService = new PhoneService(phoneRepository, permissionService);
        this.typeService = new TypeService(typeRepository, permissionService);
        this.typeMarkService = new TypeMarkService(typeMarkRepository, permissionService);
        this.deviceLocationService = new DeviceLocationService(deviceLocationRepository, permissionService);
        this.employeeLocationService = new EmployeeLocationService(employeeLocationRepository, permissionService);
        this.locationPhoneService = new LocationPhoneService(locationPhoneRepository, permissionService);
        this.oSystemService = new OSystemService(oSystemRepository, permissionService);
        this.databaseService = new DatabaseBackendService(databaseManager, permissionService);
    }

    @Override
    public DeviceBusService getDeviceService() {
        return deviceService;
    }

    @Override
    public DeviceLocationBusService getDeviceLocationService() {
        return deviceLocationService;
    }

    @Override
    public EmployeeBusService getEmployeeService() {
        return employeeService;
    }

    @Override
    public EmployeeLocationBusService getEmployeeLocationService() {
        return employeeLocationService;
    }

    @Override
    public LocationBusService getLocationService() {
        return locationService;
    }

    @Override
    public LocationPhoneBusService getLocationPhoneService() {
        return locationPhoneService;
    }

    @Override
    public OSystemBusService getOSystemService() {
        return oSystemService;
    }

    @Override
    public PhoneBusService getPhoneService() {
        return phoneService;
    }

    @Override
    public TypeBusService getTypeService() {
        return typeService;
    }

    @Override
    public TypeMarkBusService getTypeMarkService() {
        return typeMarkService;
    }

    @Override
    public DatabaseService getDatabaseService() {
        return databaseService;
    }
}
