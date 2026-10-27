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
import ru.vit4liy.base.util.Log;
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
    private DeviceLocationRepository deviceLocationRepository;
    private EmployeeLocationRepository employeeLocationRepository;
    private LocationPhoneRepository locationPhoneRepository;

    private LocalDataBus localDataBus;

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
            throw new ModuleInitializeException("PermissionModule not found!");
        }
        Log.out("Permission module has been initialized!");

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

        Log.out("All repositories has been initialized!");

        databaseManager.startUp();

        this.localDataBus = new LocalDataBus(
                permissionModule.getPermissionService(), databaseManager,
                deviceRepository, employeeRepository,
                locationRepository, phoneRepository, typeRepository,
                typeMarkRepository, deviceLocationRepository, employeeLocationRepository,
                locationPhoneRepository, oSystemRepository
        );

        Log.out("All database services has been initialized!");
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

    public java.sql.Connection getConnection() throws java.sql.SQLException {
        return this.databaseManager.getConnection();
    }

    public LocalDataBus getLocalDataBus() {
        return localDataBus;
    }
}
