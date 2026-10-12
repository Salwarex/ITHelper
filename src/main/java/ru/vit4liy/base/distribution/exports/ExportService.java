package ru.vit4liy.base.distribution.exports;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import ru.vit4liy.perm.PermissionService;
import ru.vit4liy.rsc.service.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExportService{
    private final ObjectMapper mapper;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final BackendDataBus bus;
    private final ExportProcessor serializer;

    public ExportService(ObjectMapper mapper, BackendDataBus bus) {
        this.mapper = mapper;
        this.bus = bus;
        this.serializer = new ExportProcessor(mapper);
    }

    public void exportAll(Permissible permissible, File file) throws IOException {
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("version", "1.0");
        rootNode.put("exportDate", LocalDateTime.now().format(FORMATTER));

        try {
            rootNode.set("locations", mapper.readTree(serializer.serialize(Location.class, bus.getLocationService().getAll(permissible))));
            rootNode.set("types", mapper.readTree(serializer.serialize(Type.class, bus.getTypeService().getAll(permissible))));
            rootNode.set("typeMarks", mapper.readTree(serializer.serialize(TypeMark.class, bus.getTypeMarkService().getAll(permissible))));
            rootNode.set("osSystems", mapper.readTree(serializer.serialize(OSystem.class, bus.getOSystemService().getAll(permissible))));
            rootNode.set("employees", mapper.readTree(serializer.serialize(Employee.class, bus.getEmployeeService().getAll(permissible))));
            rootNode.set("phones", mapper.readTree(serializer.serialize(Phone.class, bus.getPhoneService().getAll(permissible))));
            rootNode.set("devices", mapper.readTree(serializer.serialize(Device.class, bus.getDeviceService().getAll(permissible))));
            rootNode.set("deviceLocations", mapper.readTree(serializer.serialize(DeviceLocation.class, bus.getDeviceLocationService().getAll(permissible))));
            rootNode.set("employeeLocations", mapper.readTree(serializer.serialize(EmployeeLocation.class, bus.getEmployeeLocationService().getAll(permissible))));
            rootNode.set("locationPhones", mapper.readTree(serializer.serialize(LocationPhone.class, bus.getLocationPhoneService().getAll(permissible))));
        } catch (Exception e) {
            Log.err("File export error: " + e.getMessage());
            throw new IOException("Error: " + e.getMessage(), e);
        }

        mapper.writeValue(file, rootNode);
    }
}