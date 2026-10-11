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

import java.io.File;

public class ImportService {
    private final ObjectMapper mapper;
    private final BackendDataBus bus;
    private final ImportProcessor processor;

    public ImportService(ObjectMapper mapper, BackendDataBus bus) {
        this.mapper = mapper;
        this.bus = bus;
        this.processor = new ImportProcessor(mapper, bus);
    }

    public void importAll(File file) {
        try {
            processor.clearMapping();
            JsonNode rootNode = mapper.readTree(file);

            Log.out("Importing start...");

            processor.processNodeArray(rootNode.get("types"), Type.class,
                    (clazz, entity) -> bus.getTypeService().create((Type) entity));

            processor.processNodeArray(rootNode.get("osSystems"), OSystem.class,
                    (clazz, entity) -> bus.getOSystemService().create((OSystem) entity));

            processor.processNodeArray(rootNode.get("phones"), Phone.class,
                    (clazz, entity) -> bus.getPhoneService().create((Phone) entity));

            processor.processNodeArray(rootNode.get("locations"), Location.class,
                    (clazz, entity) -> bus.getLocationService().create((Location) entity));

            processor.processNodeArray(rootNode.get("employees"), Employee.class,
                    (clazz, entity) -> bus.getEmployeeService().create((Employee) entity));


            processor.processNodeArray(rootNode.get("typeMarks"), TypeMark.class,
                    (clazz, entity) -> bus.getTypeMarkService().create((TypeMark) entity));

            processor.processNodeArray(rootNode.get("devices"), Device.class,
                    (clazz, entity) -> bus.getDeviceService().create((Device) entity));


            processor.processNodeArray(rootNode.get("deviceLocations"), DeviceLocation.class,
                    (clazz, entity) -> bus.getDeviceLocationService().create((DeviceLocation) entity));

            processor.processNodeArray(rootNode.get("employeeLocations"), EmployeeLocation.class,
                    (clazz, entity) -> bus.getEmployeeLocationService().create((EmployeeLocation) entity));

            processor.processNodeArray(rootNode.get("locationPhones"), LocationPhone.class,
                    (clazz, entity) -> bus.getLocationPhoneService().create((LocationPhone) entity));

            Log.out("Import successfull!");

        } catch (Exception e) {
            Log.err("Import critical exception: " + e.getMessage());
            throw new RuntimeException("Import failed", e);
        }
    }
}