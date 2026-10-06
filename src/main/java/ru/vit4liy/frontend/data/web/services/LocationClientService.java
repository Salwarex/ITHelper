package ru.vit4liy.frontend.data.web.services;

import ru.vit4liy.base.bus.services.LocationBusService;
import ru.vit4liy.base.model.location.Location;

import java.util.List;
import java.util.Optional;

public class LocationClientService implements LocationBusService {
    @Override
    public long create(Location location) throws Exception {
        return 0;
    }

    @Override
    public Optional<Location> getById(long l) throws Exception {
        return Optional.empty();
    }

    @Override
    public boolean update(Location location) throws Exception {
        return false;
    }

    @Override
    public boolean delete(long l) throws Exception {
        return false;
    }

    @Override
    public List<Location> getAll() throws Exception {
        return List.of();
    }

    @Override
    public Optional<Location> getLocationByKey(String key) throws Exception {
        return Optional.empty();
    }

    @Override
    public List<Location> searchLocationsByDisplayName(String displayNamePart) throws Exception {
        return List.of();
    }

    @Override
    public List<Location> getChildLocations(Long parentId) throws Exception {
        return List.of();
    }
}
