package ru.vit4liy.backend.modules.data;

import ru.vit4liy.App;
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
import ru.vit4liy.backend.modules.data.typemark.TypeMarkService;
import ru.vit4liy.backend.modules.data.typemark.TypeMarkRepository;
import ru.vit4liy.modular.Module;
import ru.vit4liy.modular.ModuleLoader;
import ru.vit4liy.modular.exception.ModuleInitializeException;
import ru.vit4liy.modular.exception.ModuleShutdownException;
import ru.vit4liy.perm.PermissionModule;
import ru.vit4liy.rsc.DatabaseManager;

import java.util.Set;

public class DatabaseModule extends Module {
    private DatabaseManager databaseManager;
    private PermissionModule permissionModule;

    private DeviceRepository deviceRepository;
    private EmployeeRepository employeeRepository;
    private LocationRepository locationRepository;
    private PhoneRepository phoneRepository;
    private TypeRepository typeRepository;
    private TypeMarkRepository typeMarkRepository;
    private OSystemRepository oSystemRepository;

    private DeviceService deviceService;
    private EmployeeService employeeService;
    private LocationService locationService;
    private PhoneService phoneService;
    private TypeService typeService;
    private TypeMarkService typeMarkService;
    private OSystemService oSystemService;

    private DeviceLocationRepository deviceLocationRepository;
    private EmployeeLocationRepository employeeLocationRepository;
    private LocationPhoneRepository locationPhoneRepository;

    private DeviceLocationService deviceLocationService;
    private EmployeeLocationService employeeLocationService;
    private LocationPhoneService locationPhoneService;

    private final boolean isMySQL;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    public DatabaseModule(ModuleLoader loader, boolean isMySQL, String host, int port, String database, String username, String password){
        super(loader);
        this.isMySQL = isMySQL;
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    @Override
    public void initialize() throws ModuleInitializeException {
        try {
            this.permissionModule = loader.getModule(PermissionModule.class);
        } catch (ClassNotFoundException e) {
            throw new ModuleInitializeException("PermissionModule не найден!");
        }

        this.databaseManager = new DatabaseManager(App.getLogger(), isMySQL, host, port, database, username, password);

        this.deviceRepository = new DeviceRepository(databaseManager);
        this.employeeRepository = new EmployeeRepository(databaseManager);
        this.locationRepository = new LocationRepository(databaseManager);
        this.phoneRepository = new PhoneRepository(databaseManager);
        this.typeRepository = new TypeRepository(databaseManager);
        this.typeMarkRepository = new TypeMarkRepository(databaseManager);
        this.deviceLocationRepository = new DeviceLocationRepository(databaseManager);
        this.employeeLocationRepository = new EmployeeLocationRepository(databaseManager);
        this.locationPhoneRepository = new LocationPhoneRepository(databaseManager);
        this.oSystemRepository = new OSystemRepository(databaseManager);

        databaseManager.startUp();

        this.deviceService = new DeviceService(deviceRepository, permissionModule.getPermissionService());
        this.employeeService = new EmployeeService(employeeRepository, permissionModule.getPermissionService());
        this.locationService = new LocationService(locationRepository, permissionModule.getPermissionService());
        this.phoneService = new PhoneService(phoneRepository, permissionModule.getPermissionService());
        this.typeService = new TypeService(typeRepository, permissionModule.getPermissionService());
        this.typeMarkService = new TypeMarkService(typeMarkRepository, permissionModule.getPermissionService());
        this.deviceLocationService = new DeviceLocationService(deviceLocationRepository, permissionModule.getPermissionService());
        this.employeeLocationService = new EmployeeLocationService(employeeLocationRepository, permissionModule.getPermissionService());
        this.locationPhoneService = new LocationPhoneService(locationPhoneRepository, permissionModule.getPermissionService());
        this.oSystemService = new OSystemService(oSystemRepository, permissionModule.getPermissionService());
    }

    @Override
    public void shutdown() throws ModuleShutdownException {

    }

    @Override
    public String moduleName() {
        return "Database";
    }

    @Override
    protected Set<Class<? extends Module>> dependencies() {
        return Set.of(PermissionModule.class);
    }

    public synchronized DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public PermissionModule getPermissionModule() {
        return permissionModule;
    }

    public DeviceRepository getDeviceRepository() {
        return deviceRepository;
    }

    public EmployeeRepository getEmployeeRepository() {
        return employeeRepository;
    }

    public LocationRepository getLocationRepository() {
        return locationRepository;
    }

    public PhoneRepository getPhoneRepository() {
        return phoneRepository;
    }

    public TypeRepository getTypeRepository() {
        return typeRepository;
    }

    public TypeMarkRepository getTypeMarksRepository() {
        return typeMarkRepository;
    }

    public DeviceService getDeviceService() {
        return deviceService;
    }

    public EmployeeService getEmployeeService() {
        return employeeService;
    }

    public LocationService getLocationService() {
        return locationService;
    }

    public PhoneService getPhoneService() {
        return phoneService;
    }

    public TypeService getTypeService() {
        return typeService;
    }

    public TypeMarkService getTypeMarkService() {
        return typeMarkService;
    }

    public DeviceLocationService getDeviceLocationService() {
        return deviceLocationService;
    }

    public void setDeviceLocationService(DeviceLocationService deviceLocationService) {
        this.deviceLocationService = deviceLocationService;
    }

    public EmployeeLocationService getEmployeeLocationService() {
        return employeeLocationService;
    }

    public void setEmployeeLocationService(EmployeeLocationService employeeLocationService) {
        this.employeeLocationService = employeeLocationService;
    }

    public LocationPhoneService getLocationPhoneService() {
        return locationPhoneService;
    }

    public void setLocationPhoneService(LocationPhoneService locationPhoneService) {
        this.locationPhoneService = locationPhoneService;
    }

    public LocationPhoneRepository getLocationPhoneRepository() {
        return locationPhoneRepository;
    }

    public void setLocationPhoneRepository(LocationPhoneRepository locationPhoneRepository) {
        this.locationPhoneRepository = locationPhoneRepository;
    }

    public EmployeeLocationRepository getEmployeeLocationRepository() {
        return employeeLocationRepository;
    }

    public void setEmployeeLocationRepository(EmployeeLocationRepository employeeLocationRepository) {
        this.employeeLocationRepository = employeeLocationRepository;
    }

    public DeviceLocationRepository getDeviceLocationRepository() {
        return deviceLocationRepository;
    }

    public void setDeviceLocationRepository(DeviceLocationRepository deviceLocationRepository) {
        this.deviceLocationRepository = deviceLocationRepository;
    }

    public OSystemRepository getOSystemRepository() {
        return oSystemRepository;
    }

    public OSystemService getOSystemService() {
        return oSystemService;
    }

    public java.sql.Connection getConnection() throws java.sql.SQLException {
        return this.databaseManager.getConnection();
    }
}
