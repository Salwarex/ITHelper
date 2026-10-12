package ru.vit4liy.backend.modules.data.device;

import ru.vit4liy.base.bus.services.DeviceBusService;
import ru.vit4liy.base.model.device.Device;
import ru.vit4liy.perm.Permissible;
import ru.vit4liy.perm.PermissionService;
import ru.vit4liy.rsc.service.EntityService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DeviceService extends EntityService<Device> implements DeviceBusService {
    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository repository, PermissionService permissionService) {
        super(repository, permissionService);
        this.deviceRepository = repository;
    }

    public Optional<Device> getDeviceBySerial(Permissible executor, String serial) throws SQLException {
        return deviceRepository.findByDeviceSerial(serial);
    }

    public List<Device> getDevicesByTypeId(Permissible executor, long typeId) throws SQLException {
        return deviceRepository.findByTypeId(typeId);
    }

    public List<Device> getDevicesByStatus(Permissible executor, String status) throws SQLException {
        return deviceRepository.findByStatus(status);
    }

    public List<Device> getDevicesByIp(Permissible executor, String ip) throws SQLException {
        return deviceRepository.findByIp(ip);
    }

    public List<Device> getDevicesByOsId(Permissible executor, long osId) throws SQLException {
        return deviceRepository.findByOsId(osId);
    }
}