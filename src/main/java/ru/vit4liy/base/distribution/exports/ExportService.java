package ru.vit4liy.base.distribution.exports;

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
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExportService {
    private final ObjectMapper mapper;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final BackendDataBus bus;
    
    public ExportService(ObjectMapper mapper, BackendDataBus bus) {
        this.mapper = mapper;
        this.bus = bus;
    }

    public void exportAll(File file) throws IOException{
        ExportData data = new ExportData();
        data.exportDate = LocalDateTime.now().format(FORMATTER);

        try {
            data.locations = bus.getLocationService().getAll();
            data.types = bus.getTypeService().getAll();
            data.typeMarks = bus.getTypeMarkService().getAll();
            data.osSystems = bus.getOSystemService().getAll();
            data.devices = bus.getDeviceService().getAll();
            data.employees = bus.getEmployeeService().getAll();
            data.phones = bus.getPhoneService().getAll();
            data.deviceLocations = bus.getDeviceLocationService().getAll();
            data.employeeLocations = bus.getEmployeeLocationService().getAll();
            data.locationPhones = bus.getLocationPhoneService().getAll();
        } catch (Exception e) {
            Log.err("File export error: " + e.getMessage());
            throw new IOException("Error: " + e.getMessage(), e);
        }

        mapper.writeValue(file, data);
    }

    public static class ExportData {
        public String version = "1.0";
        public String exportDate;
        public List<Location> locations;
        public List<Type> types;
        public List<TypeMark> typeMarks;
        public List<OSystem> osSystems;
        public List<Device> devices;
        public List<Employee> employees;
        public List<Phone> phones;
        public List<DeviceLocation> deviceLocations;
        public List<EmployeeLocation> employeeLocations;
        public List<LocationPhone> locationPhones;
    }
}
