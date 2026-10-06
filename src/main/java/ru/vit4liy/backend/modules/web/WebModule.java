package ru.vit4liy.backend.modules.web;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import ru.vit4liy.App;
import ru.vit4liy.backend.modules.web.controllers.*;
import ru.vit4liy.base.util.Log;
import ru.vit4liy.modular.Module;
import ru.vit4liy.modular.ModuleLoader;
import ru.vit4liy.modular.exception.ModuleInitializeException;
import ru.vit4liy.modular.exception.ModuleShutdownException;

import java.util.Set;

public class WebModule extends Module {
    private Javalin app;
    private final static int WEB_PORT = App.getConfig().getInt("web.port", 2701);

    private DeviceController deviceController;
    private EmployeeController employeeController;
    private LocationController locationController;
    private OSystemController oSystemController;
    private PhoneController phoneController;
    private TypeController typeController;
    private TypeMarkController typeMarkController;

    public WebModule(ModuleLoader loader) {
        super(loader);
    }

    @Override
    public void initialize() throws ModuleInitializeException {
        Log.out("Starting backend web application...");
        this.app = Javalin.create(this::serverInit).start();
        Log.out("Backend web application started successfully!");

        Log.out("Starting web controllers");
        this.deviceController = new DeviceController(app);
        this.employeeController = new EmployeeController(app);
        this.locationController = new LocationController(app);
        this.oSystemController = new OSystemController(app);
        this.phoneController = new PhoneController(app);
        this.typeController = new TypeController(app);
        this.typeMarkController = new TypeMarkController(app);
        Log.out("All controllers has been started successfully!");
    }

    private void serverInit(JavalinConfig config){
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);

        connector.setHost("127.0.0.1");
        connector.setPort(WebModule.WEB_PORT);

        server.addConnector(connector);
    }

    @Override
    public void shutdown() throws ModuleShutdownException {
        this.app.stop();
    }

    @Override
    public String moduleName() {
        return "Web";
    }

    @Override
    protected Set<Class<? extends Module>> dependencies() {
        return Set.of();
    }

    public DeviceController getDeviceController() {
        return deviceController;
    }

    public EmployeeController getEmployeeController() {
        return employeeController;
    }

    public LocationController getLocationController() {
        return locationController;
    }

    public OSystemController getoSystemController() {
        return oSystemController;
    }

    public PhoneController getPhoneController() {
        return phoneController;
    }

    public TypeController getTypeController() {
        return typeController;
    }

    public TypeMarkController getTypeMarkController() {
        return typeMarkController;
    }
}
