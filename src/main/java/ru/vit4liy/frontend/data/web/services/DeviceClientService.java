package ru.vit4liy.frontend.data.web.services;

import ru.vit4liy.base.bus.services.DeviceBusService;
import ru.vit4liy.base.model.device.Device;

import java.util.List;
import java.util.Optional;

public class DeviceClientService implements DeviceBusService {
    @Override
    public long create(Device device) throws Exception {
        return 0;
    }

    @Override
    public Optional<Device> getById(long l) throws Exception {
        return Optional.empty();
    }

    @Override
    public boolean update(Device device) throws Exception {
        return false;
    }

    @Override
    public boolean delete(long l) throws Exception {
        return false;
    }

    @Override
    public List<Device> getAll() throws Exception {
        return List.of();
    }

    @Override
    public Optional<Device> getDeviceBySerial(String serial) throws Exception {
        return Optional.empty();
    }

    @Override
    public List<Device> getDevicesByTypeId(long typeId) throws Exception {
        return List.of();
    }

    @Override
    public List<Device> getDevicesByStatus(String status) throws Exception {
        return List.of();
    }

    @Override
    public List<Device> getDevicesByIp(String ip) throws Exception {
        return List.of();
    }

    @Override
    public List<Device> getDevicesByOsId(long osId) throws Exception {
        return List.of();
    }
}
