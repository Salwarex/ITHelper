package ru.vit4liy.frontend.data.web.services;

import ru.vit4liy.base.bus.services.LocationPhoneBusService;
import ru.vit4liy.base.model.intersections.LocationPhone;
import ru.vit4liy.rsc.misc.MultiId;

import java.util.List;
import java.util.Optional;

public class LocationPhoneClientService implements LocationPhoneBusService {
    @Override
    public MultiId<Long> create(LocationPhone locationPhone) throws Exception {
        return null;
    }

    @Override
    public Optional<LocationPhone> getById(MultiId<Long> multiId) throws Exception {
        return Optional.empty();
    }

    @Override
    public List<LocationPhone> findAllByIdA(long l) throws Exception {
        return List.of();
    }

    @Override
    public List<LocationPhone> findAllByIdB(long l) throws Exception {
        return List.of();
    }

    @Override
    public boolean update(LocationPhone locationPhone) throws Exception {
        return false;
    }

    @Override
    public boolean delete(MultiId<Long> multiId) throws Exception {
        return false;
    }

    @Override
    public List<LocationPhone> getAll() throws Exception {
        return List.of();
    }
}
