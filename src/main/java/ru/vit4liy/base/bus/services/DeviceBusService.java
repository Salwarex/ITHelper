package ru.vit4liy.base.bus.services;

import ru.vit4liy.base.model.device.Device;
import ru.vit4liy.rsc.service.GenericDatabaseBusService;

import java.util.List;
import java.util.Optional;

public interface DeviceBusService extends GenericDatabaseBusService<Device> {
    Optional<Device> getDeviceBySerial(String serial) throws Exception;
    List<Device> getDevicesByTypeId(long typeId) throws Exception;
    List<Device> getDevicesByStatus(String status) throws Exception;
    List<Device> getDevicesByIp(String ip) throws Exception;
    List<Device> getDevicesByOsId(long osId) throws Exception;
}
