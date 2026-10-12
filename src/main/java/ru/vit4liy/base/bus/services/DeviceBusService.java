package ru.vit4liy.base.bus.services;

import ru.vit4liy.base.model.device.Device;
import ru.vit4liy.perm.Permissible;
import ru.vit4liy.rsc.service.GenericDatabaseBusService;

import java.util.List;
import java.util.Optional;

public interface DeviceBusService extends GenericDatabaseBusService<Device> {
    Optional<Device> getDeviceBySerial(Permissible executor, String serial) throws Exception;
    List<Device> getDevicesByTypeId(Permissible executor, long typeId) throws Exception;
    List<Device> getDevicesByStatus(Permissible executor, String status) throws Exception;
    List<Device> getDevicesByIp(Permissible executor, String ip) throws Exception;
    List<Device> getDevicesByOsId(Permissible executor, long osId) throws Exception;
}
