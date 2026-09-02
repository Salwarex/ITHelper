package ru.vit4liy.backend.data.service;

import ru.vit4liy.backend.data.model.Device;
import ru.vit4liy.backend.data.repository.DeviceRepository;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DeviceService {
    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public long createDevice(Device device) throws SQLException {
        if (device.getDeviceSerial() != null && !device.getDeviceSerial().isEmpty()) {
            if (deviceRepository.findByDeviceSerial(device.getDeviceSerial()).isPresent()) {
                throw new SQLException("Устройство с таким серийным номером уже существует");
            }
        }
        return deviceRepository.create(device);
    }

    public Optional<Device> getDeviceById(long id) throws SQLException {
        return deviceRepository.findById(id);
    }

    public List<Device> getAllDevices() throws SQLException {
        return deviceRepository.findAll();
    }

    public boolean updateDevice(Device device) throws SQLException {
        return deviceRepository.update(device);
    }

    public boolean deleteDevice(long id) throws SQLException {
        return deviceRepository.delete(id);
    }

    public Optional<Device> getDeviceBySerial(String serial) throws SQLException {
        return deviceRepository.findByDeviceSerial(serial);
    }

    public List<Device> getDevicesByTypeId(long typeId) throws SQLException {
        return deviceRepository.findByTypeId(typeId);
    }

    public List<Device> getDevicesByStatus(String status) throws SQLException {
        return deviceRepository.findByStatus(status);
    }
}
