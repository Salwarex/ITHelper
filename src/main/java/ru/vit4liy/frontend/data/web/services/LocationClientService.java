package ru.vit4liy.frontend.data.web.services;

import ru.vit4liy.base.bus.services.LocationBusService;
import ru.vit4liy.base.model.location.Location;
import ru.vit4liy.perm.Permissible;

import java.util.List;
import java.util.Optional;

public class LocationClientService implements LocationBusService {

    @Override
    public Optional<Location> getLocationByKey(Permissible executor, String key) throws Exception {
        return Optional.empty();
    }

    @Override
    public List<Location> searchLocationsByDisplayName(Permissible executor, String displayNamePart) throws Exception {
        return List.of();
    }

    @Override
    public List<Location> getChildLocations(Permissible executor, Long parentId) throws Exception {
        return List.of();
    }

    @Override
    public long create(Permissible permissible, Location location) throws Exception {
        return 0;
    }

    @Override
    public Optional<Location> getById(Permissible permissible, long l) throws Exception {
        return Optional.empty();
    }

    @Override
    public boolean update(Permissible permissible, Location location) throws Exception {
        return false;
    }

    @Override
    public boolean delete(Permissible permissible, long l) throws Exception {
        return false;
    }

    @Override
    public List<Location> getAll(Permissible permissible) throws Exception {
        return List.of();
    }
}
