package ru.vit4liy.frontend.data.web.services;

import ru.vit4liy.base.bus.services.DeviceBusService;
import ru.vit4liy.base.model.device.Device;
import ru.vit4liy.perm.Permissible;

import java.util.List;
import java.util.Optional;

public class DeviceClientService implements DeviceBusService {
    @Override
    public Optional<Device> getDeviceBySerial(Permissible executor, String serial) throws Exception {
        return Optional.empty();
    }

    @Override
    public List<Device> getDevicesByTypeId(Permissible executor, long typeId) throws Exception {
        return List.of();
    }

    @Override
    public List<Device> getDevicesByStatus(Permissible executor, String status) throws Exception {
        return List.of();
    }

    @Override
    public List<Device> getDevicesByIp(Permissible executor, String ip) throws Exception {
        return List.of();
    }

    @Override
    public List<Device> getDevicesByOsId(Permissible executor, long osId) throws Exception {
        return List.of();
    }

    @Override
    public long create(Permissible permissible, Device device) throws Exception {
        return 0;
    }

    @Override
    public Optional<Device> getById(Permissible permissible, long l) throws Exception {
        return Optional.empty();
    }

    @Override
    public boolean update(Permissible permissible, Device device) throws Exception {
        return false;
    }

    @Override
    public boolean delete(Permissible permissible, long l) throws Exception {
        return false;
    }

    @Override
    public List<Device> getAll(Permissible permissible) throws Exception {
        return List.of();
    }
}
