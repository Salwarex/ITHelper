package ru.vit4liy.base.distribution.imports;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.vit4liy.base.bus.BackendDataBus;
import ru.vit4liy.base.model.device.Device;
import ru.vit4liy.base.model.device.system.OSystem;
import ru.vit4liy.base.model.employee.Employee;
import ru.vit4liy.base.model.intersections.DeviceLocation;
import ru.vit4liy.base.model.intersections.EmployeeLocation;
import ru.vit4liy.base.model.intersections.LocationPhone;
import ru.vit4liy.base.model.location.Location;
import ru.vit4liy.base.model.phone.Phone;
import ru.vit4liy.base.model.type.Type;
import ru.vit4liy.base.model.typemark.TypeMark;
import ru.vit4liy.base.util.Log;
import ru.vit4liy.perm.Permissible;

import java.io.File;

public class ImportService{
    private final ObjectMapper mapper;
    private final BackendDataBus bus;
    private final ImportProcessor processor;

    public ImportService(ObjectMapper mapper, BackendDataBus bus) {
        this.mapper = mapper;
        this.bus = bus;
        this.processor = new ImportProcessor(mapper, bus);
    }

    public void importAll(Permissible executor, File file, boolean overwrite) {
        try {
            if(overwrite) {
                bus.getDatabaseService().removeAll(executor);
                Log.wrn("All data elapsed by overwrite import...");
            }

            processor.clearMapping();
            JsonNode rootNode = mapper.readTree(file);

            Log.out("Importing start...");

            processor.processNodeArray(rootNode.get("types"), Type.class,
                    (clazz, entity) -> bus.getTypeService().create(executor, (Type) entity));

            processor.processNodeArray(rootNode.get("osSystems"), OSystem.class,
                    (clazz, entity) -> bus.getOSystemService().create(executor, (OSystem) entity));

            processor.processNodeArray(rootNode.get("phones"), Phone.class,
                    (clazz, entity) -> bus.getPhoneService().create(executor, (Phone) entity));

            processor.processNodeArray(rootNode.get("locations"), Location.class,
                    (clazz, entity) -> bus.getLocationService().create(executor, (Location) entity));

            processor.processNodeArray(rootNode.get("employees"), Employee.class,
                    (clazz, entity) -> bus.getEmployeeService().create(executor, (Employee) entity));


            processor.processNodeArray(rootNode.get("typeMarks"), TypeMark.class,
                    (clazz, entity) -> bus.getTypeMarkService().create(executor, (TypeMark) entity));

            processor.processNodeArray(rootNode.get("devices"), Device.class,
                    (clazz, entity) -> bus.getDeviceService().create(executor, (Device) entity));


            processor.processNodeArray(rootNode.get("deviceLocations"), DeviceLocation.class,
                    (clazz, entity) -> bus.getDeviceLocationService().create(executor, (DeviceLocation) entity));

            processor.processNodeArray(rootNode.get("employeeLocations"), EmployeeLocation.class,
                    (clazz, entity) -> bus.getEmployeeLocationService().create(executor, (EmployeeLocation) entity));

            processor.processNodeArray(rootNode.get("locationPhones"), LocationPhone.class,
                    (clazz, entity) -> bus.getLocationPhoneService().create(executor, (LocationPhone) entity));

            Log.out("Import successfull!");

        } catch (Exception e) {
            Log.err("Import critical exception: " + e.getMessage());
            throw new RuntimeException("Import failed", e);
        }
    }
}