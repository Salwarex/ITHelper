package ru.vit4liy.frontend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Deprecated
public final class ExportImportService {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ExportImportService() {}

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

    public static void exportToFile(File file) throws IOException {
        if(true){ AlertUtil.error("Более не поддерживается!", "Система экспорта/импорта в ith-файлы из версии 0.15 более не поддерживается в версиях ITHelper 0.16+.");
            return; }

        ExportData data = new ExportData();
        data.exportDate = LocalDateTime.now().format(FORMATTER);

        try {
            data.locations = ApplicationFeatures.bus().getLocationService().getAll();
            data.types = ApplicationFeatures.bus().getTypeService().getAll();
            data.typeMarks = ApplicationFeatures.bus().getTypeMarkService().getAll();
            data.osSystems = ApplicationFeatures.bus().getOSystemService().getAll();
            data.devices = ApplicationFeatures.bus().getDeviceService().getAll();
            data.employees = ApplicationFeatures.bus().getEmployeeService().getAll();
            data.phones = ApplicationFeatures.bus().getPhoneService().getAll();
            data.deviceLocations = ApplicationFeatures.bus().getDeviceLocationService().getAll();
            data.employeeLocations = ApplicationFeatures.bus().getEmployeeLocationService().getAll();
            data.locationPhones = ApplicationFeatures.bus().getLocationPhoneService().getAll();
        } catch (Exception e) {
            throw new IOException("Ошибка чтения данных из БД: " + e.getMessage(), e);
        }

        MAPPER.writeValue(file, data);
    }

    public static void importOverwrite(File file) throws IOException {
        if(true){ AlertUtil.error("Более не поддерживается!", "Система экспорта/импорта в ith-файлы из версии 0.15 более не поддерживается в версиях ITHelper 0.16+.");
            return; }

        ExportData data = MAPPER.readValue(file, ExportData.class);

        try {
            java.sql.Connection conn = null; // ApplicationFeatures.bus().getConnection();

            conn.createStatement().execute("PRAGMA foreign_keys = OFF;");

            conn.createStatement().execute("DELETE FROM DeviceLocation");
            conn.createStatement().execute("DELETE FROM EmployeeLocation");
            conn.createStatement().execute("DELETE FROM LocationPhone");
            conn.createStatement().execute("DELETE FROM Device");
            conn.createStatement().execute("DELETE FROM Employee");
            conn.createStatement().execute("DELETE FROM Phone");
            conn.createStatement().execute("DELETE FROM TypeMark");
            conn.createStatement().execute("DELETE FROM OSystem");
            conn.createStatement().execute("DELETE FROM Type");
            conn.createStatement().execute("DELETE FROM Location");

            conn.createStatement().execute("DELETE FROM sqlite_sequence");

            conn.createStatement().execute("PRAGMA foreign_keys = ON;");
            conn.close();

            importData(data);

        } catch (Exception e) {
            throw new IOException("Ошибка импорта (затирание): " + e.getMessage(), e);
        }
    }

    public static void importAppend(File file) throws IOException {
        if(true){ AlertUtil.error("Более не поддерживается!", "Система экспорта/импорта в ith-файлы из версии 0.15 более не поддерживается в версиях ITHelper 0.16+.");
            return; }

        ExportData data = MAPPER.readValue(file, ExportData.class);

        try {
            Map<Long, Long> locationIdMap = new HashMap<>();
            Map<Long, Long> typeIdMap = new HashMap<>();
            Map<Long, Long> typeMarkIdMap = new HashMap<>();
            Map<Long, Long> osIdMap = new HashMap<>();
            Map<Long, Long> deviceIdMap = new HashMap<>();
            Map<Long, Long> employeeIdMap = new HashMap<>();
            Map<Long, Long> phoneIdMap = new HashMap<>();

            for (Location loc : data.locations) {
                Long oldId = loc.getId();
                Long existingId = null;

                if (loc.getKey() != null && !loc.getKey().isBlank()) {
                    existingId = findExistingId("Location", "key", loc.getKey());
                }

                if (existingId == null) {
                    Long mappedParentId = locationIdMap.get(loc.getParentId());
                    if (mappedParentId != null && mappedParentId != 0L) {
                        existingId = findExistingLocationByNameAndParent(loc.getDisplayName(), mappedParentId);
                    }
                }

                if (existingId == null) {
                    existingId = findExistingId("Location", "display_name", loc.getDisplayName());
                }

                if (existingId != null) {
                    locationIdMap.put(oldId, existingId);
                } else {
                    loc.setId(0L);
                    if (loc.getParentId() != null) {
                        Long mappedParent = locationIdMap.get(loc.getParentId());
                        loc.setParentId(mappedParent != null ? mappedParent : 0L);
                    } else {
                        loc.setParentId(0L);
                    }
                    Long newId = ApplicationFeatures.bus().getLocationService().create(loc);
                    locationIdMap.put(oldId, newId);
                }
            }

            for (Type type : data.types) {
                Long oldId = type.getId();
                Long existingId = findExistingId("Type", "display_name", type.getDisplayName());
                if (existingId != null) {
                    typeIdMap.put(oldId, existingId);
                } else {
                    type.setId(0L);
                    Long newId = ApplicationFeatures.bus().getTypeService().create(type);
                    typeIdMap.put(oldId, newId);
                }
            }

            for (TypeMark tm : data.typeMarks) {
                Long oldId = tm.getId();
                Long existingId = findExistingId("TypeMark", "display_name", tm.getDisplayName());
                if (existingId != null) {
                    typeMarkIdMap.put(oldId, existingId);
                } else {
                    tm.setId(0L);
                    Long mappedTypeId = typeIdMap.get((long) tm.getTypeId());
                    tm.setTypeId(mappedTypeId != null ? mappedTypeId.intValue() : 1);
                    Long newId = ApplicationFeatures.bus().getTypeMarkService().create(tm);
                    typeMarkIdMap.put(oldId, newId);
                }
            }

            for (OSystem os : data.osSystems) {
                Long oldId = os.getId();
                Long existingId = findExistingId("OSystem", "display_name", os.getDisplayName());
                if (existingId != null) {
                    osIdMap.put(oldId, existingId);
                } else {
                    os.setId(0L);
                    Long newId = ApplicationFeatures.bus().getOSystemService().create(os);
                    osIdMap.put(oldId, newId);
                }
            }

            for (Device dev : data.devices) {
                Long oldId = dev.getId();
                Long existingId = findExistingId("Device", "device_serial", dev.getDeviceSerial());
                if (existingId != null) {
                    deviceIdMap.put(oldId, existingId);
                } else {
                    dev.setId(0L);
                    Long mappedTypeId = typeIdMap.get((long) dev.getTypeId());
                    dev.setTypeId(mappedTypeId != null ? mappedTypeId.intValue() : 1);

                    if (dev.getTypeMarkId() != null) {
                        Long mappedMarkId = typeMarkIdMap.get((long) dev.getTypeMarkId());
                        dev.setTypeMarkId(mappedMarkId != null ? mappedMarkId.intValue() : null);
                    }
                    if (dev.getOsId() != null) {
                        dev.setOsId(osIdMap.get(dev.getOsId()));
                    }
                    Long newId = ApplicationFeatures.bus().getDeviceService().create(dev);
                    deviceIdMap.put(oldId, newId);
                }
            }

            for (Employee emp : data.employees) {
                Long oldId = emp.getId();
                Long existingId = findExistingEmployee(emp.getSurname(), emp.getName(), emp.getThirdname());
                if (existingId != null) {
                    employeeIdMap.put(oldId, existingId);
                } else {
                    emp.setId(0L);
                    Long newId = ApplicationFeatures.bus().getEmployeeService().create(emp);
                    employeeIdMap.put(oldId, newId);
                }
            }

            for (Phone phone : data.phones) {
                Long oldId = phone.getId();
                Long existingId = findExistingId("Phone", "corp_phone_number", phone.getCorpPhoneNumber());
                if (existingId != null) {
                    phoneIdMap.put(oldId, existingId);
                } else {
                    phone.setId(0L);
                    Long newId = ApplicationFeatures.bus().getPhoneService().create(phone);
                    phoneIdMap.put(oldId, newId);
                }
            }

            for (DeviceLocation dl : data.deviceLocations) {
                Long newDeviceId = deviceIdMap.get(dl.getAId());
                Long newLocationId = locationIdMap.get(dl.getBId());
                if (newDeviceId != null && newLocationId != null) {
                    if (!isLinkExists("DeviceLocation", "device_id", newDeviceId, "location_id", newLocationId)) {
                        DeviceLocation newDl = new DeviceLocation();
                        newDl.setAId(newDeviceId);
                        newDl.setBId(newLocationId);
                        newDl.setCreatedAt(dl.getCreatedAt());
                        newDl.setLastChecked(dl.getLastChecked());
                        ApplicationFeatures.bus().getDeviceLocationService().create(newDl);
                    }
                }
            }

            for (EmployeeLocation el : data.employeeLocations) {
                Long newEmployeeId = employeeIdMap.get(el.getAId());
                Long newLocationId = locationIdMap.get(el.getBId());
                if (newEmployeeId != null && newLocationId != null) {
                    if (!isLinkExists("EmployeeLocation", "employee_id", newEmployeeId, "location_id", newLocationId)) {
                        EmployeeLocation newEl = new EmployeeLocation();
                        newEl.setAId(newEmployeeId);
                        newEl.setBId(newLocationId);
                        newEl.setCreatedAt(el.getCreatedAt());
                        newEl.setLastChecked(el.getLastChecked());
                        ApplicationFeatures.bus().getEmployeeLocationService().create(newEl);
                    }
                }
            }

            for (LocationPhone lp : data.locationPhones) {
                Long newLocationId = locationIdMap.get(lp.getAId());
                Long newPhoneId = phoneIdMap.get(lp.getBId());
                if (newLocationId != null && newPhoneId != null) {
                    if (!isLinkExists("LocationPhone", "location_id", newLocationId, "phone_id", newPhoneId)) {
                        LocationPhone newLp = new LocationPhone();
                        newLp.setAId(newLocationId);
                        newLp.setBId(newPhoneId);
                        newLp.setCreatedAt(lp.getCreatedAt());
                        newLp.setLastChecked(lp.getLastChecked());
                        ApplicationFeatures.bus().getLocationPhoneService().create(newLp);
                    }
                }
            }
        } catch (Exception e) {
            throw new IOException("Ошибка импорта (добавочный): " + e.getMessage(), e);
        }
    }

    public static Long findExistingEmployee(String surname, String name, String thirdname) {
        return -1L;
    }

    private static Long findExistingId(String tableName, String columnName, Object value) {
//        if (value == null) return null;
//        try (java.sql.Connection conn = ApplicationFeatures.bus().getConnection();
//             java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT id FROM " + tableName + " WHERE " + columnName + " = ?")) {
//            stmt.setObject(1, value);
//            try (java.sql.ResultSet rs = stmt.executeQuery()) {
//                if (rs.next()) {
//                    return rs.getLong("id");
//                }
//            }
//        } catch (Exception e) {
//        }
//        return null;
        return -1L;
    }

    private static Long findExistingLocationByNameAndParent(String displayName, Long parentId) {
//        try (java.sql.Connection conn = ApplicationFeatures.bus().getConnection();
//             java.sql.PreparedStatement stmt = conn.prepareStatement(
//                     "SELECT id FROM Location WHERE display_name = ? AND parent_id = ?")) {
//            stmt.setString(1, displayName);
//            stmt.setLong(2, parentId);
//            try (java.sql.ResultSet rs = stmt.executeQuery()) {
//                if (rs.next()) {
//                    return rs.getLong("id");
//                }
//            }
//        } catch (Exception e) {
//        }
//        return null;
        return -1L;
    }

    private static boolean isLinkExists(String tableName, String col1Name, Long val1, String col2Name, Long val2) {
//        try (java.sql.Connection conn = ApplicationFeatures.bus().getConnection();
//             java.sql.PreparedStatement stmt = conn.prepareStatement(
//                     "SELECT 1 FROM " + tableName + " WHERE " + col1Name + " = ? AND " + col2Name + " = ? LIMIT 1")) {
//            stmt.setLong(1, val1);
//            stmt.setLong(2, val2);
//            try (java.sql.ResultSet rs = stmt.executeQuery()) {
//                return rs.next();
//            }
//        } catch (Exception e) {
//            return false;
//        }
        return false;
    }

    private static void importData(ExportData data) throws Exception {
        // Вставляем в правильном порядке (сначала справочники, потом сущности, потом связи)
        for (Location loc : data.locations) {
            ApplicationFeatures.bus().getLocationService().create(loc);
        }
        for (Type type : data.types) {
            ApplicationFeatures.bus().getTypeService().create(type);
        }
        for (TypeMark tm : data.typeMarks) {
            ApplicationFeatures.bus().getTypeMarkService().create(tm);
        }
        for (OSystem os : data.osSystems) {
            ApplicationFeatures.bus().getOSystemService().create(os);
        }
        for (Device dev : data.devices) {
            ApplicationFeatures.bus().getDeviceService().create(dev);
        }
        for (Employee emp : data.employees) {
            ApplicationFeatures.bus().getEmployeeService().create(emp);
        }
        for (Phone phone : data.phones) {
            ApplicationFeatures.bus().getPhoneService().create(phone);
        }
        for (DeviceLocation dl : data.deviceLocations) {
            ApplicationFeatures.bus().getDeviceLocationService().create(dl);
        }
        for (EmployeeLocation el : data.employeeLocations) {
            ApplicationFeatures.bus().getEmployeeLocationService().create(el);
        }
        for (LocationPhone lp : data.locationPhones) {
            ApplicationFeatures.bus().getLocationPhoneService().create(lp);
        }
    }
}