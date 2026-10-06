package ru.vit4liy.frontend.data.web.services;

import ru.vit4liy.base.bus.services.DeviceLocationBusService;
import ru.vit4liy.base.model.intersections.DeviceLocation;
import ru.vit4liy.rsc.misc.MultiId;

import java.util.List;
import java.util.Optional;

public class DeviceLocationClientService implements DeviceLocationBusService {
    @Override
    public MultiId<Long> create(DeviceLocation deviceLocation) throws Exception {
        return null;
    }

    @Override
    public Optional<DeviceLocation> getById(MultiId<Long> multiId) throws Exception {
        return Optional.empty();
    }

    @Override
    public List<DeviceLocation> findAllByIdA(long l) throws Exception {
        return List.of();
    }

    @Override
    public List<DeviceLocation> findAllByIdB(long l) throws Exception {
        return List.of();
    }

    @Override
    public boolean update(DeviceLocation deviceLocation) throws Exception {
        return false;
    }

    @Override
    public boolean delete(MultiId<Long> multiId) throws Exception {
        return false;
    }

    @Override
    public List<DeviceLocation> getAll() throws Exception {
        return List.of();
    }
}
