package ru.vit4liy.frontend.data.web.services;

import ru.vit4liy.base.bus.services.DeviceLocationBusService;
import ru.vit4liy.base.model.intersections.DeviceLocation;
import ru.vit4liy.perm.Permissible;
import ru.vit4liy.rsc.misc.MultiId;

import java.util.List;
import java.util.Optional;

public class DeviceLocationClientService implements DeviceLocationBusService {

    @Override
    public MultiId<Long> create(Permissible permissible, DeviceLocation deviceLocation) throws Exception {
        return null;
    }

    @Override
    public Optional<DeviceLocation> getById(Permissible permissible, MultiId<Long> multiId) throws Exception {
        return Optional.empty();
    }

    @Override
    public List<DeviceLocation> findAllByIdA(Permissible permissible, long l) throws Exception {
        return List.of();
    }

    @Override
    public List<DeviceLocation> findAllByIdB(Permissible permissible, long l) throws Exception {
        return List.of();
    }

    @Override
    public boolean update(Permissible permissible, DeviceLocation deviceLocation) throws Exception {
        return false;
    }

    @Override
    public boolean delete(Permissible permissible, MultiId<Long> multiId) throws Exception {
        return false;
    }

    @Override
    public List<DeviceLocation> getAll(Permissible permissible) throws Exception {
        return List.of();
    }
}
