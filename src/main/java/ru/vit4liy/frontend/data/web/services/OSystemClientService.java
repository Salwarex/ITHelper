package ru.vit4liy.frontend.data.web.services;

import ru.vit4liy.base.bus.services.OSystemBusService;
import ru.vit4liy.base.model.device.system.OSystem;
import ru.vit4liy.perm.Permissible;

import java.util.List;
import java.util.Optional;

public class OSystemClientService implements OSystemBusService {

    @Override
    public long create(Permissible permissible, OSystem oSystem) throws Exception {
        return 0;
    }

    @Override
    public Optional<OSystem> getById(Permissible permissible, long l) throws Exception {
        return Optional.empty();
    }

    @Override
    public boolean update(Permissible permissible, OSystem oSystem) throws Exception {
        return false;
    }

    @Override
    public boolean delete(Permissible permissible, long l) throws Exception {
        return false;
    }

    @Override
    public List<OSystem> getAll(Permissible permissible) throws Exception {
        return List.of();
    }
}
