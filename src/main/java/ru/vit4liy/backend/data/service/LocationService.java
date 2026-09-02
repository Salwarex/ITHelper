package ru.vit4liy.backend.data.service;

import ru.vit4liy.backend.data.model.Location;
import ru.vit4liy.backend.data.repository.LocationRepository;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class LocationService {
    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public long createLocation(Location location) throws SQLException {
        if (location.getKey() != null && !location.getKey().isEmpty()) {
            if (locationRepository.findByKey(location.getKey()).isPresent()) {
                throw new SQLException("Локация с таким ключом (key) уже существует");
            }
        }
        return locationRepository.create(location);
    }

    public Optional<Location> getLocationById(long id) throws SQLException {
        return locationRepository.findById(id);
    }

    public List<Location> getAllLocations() throws SQLException {
        return locationRepository.findAll();
    }

    public boolean updateLocation(Location location) throws SQLException {
        return locationRepository.update(location);
    }

    public boolean deleteLocation(long id) throws SQLException {
        return locationRepository.delete(id);
    }

    public Optional<Location> getLocationByKey(String key) throws SQLException {
        return locationRepository.findByKey(key);
    }

    public List<Location> searchLocationsByDisplayName(String displayNamePart) throws SQLException {
        if (displayNamePart == null || displayNamePart.trim().isEmpty()) {
            return getAllLocations();
        }
        return locationRepository.findByDisplayNameContaining(displayNamePart.trim());
    }

    public List<Location> getChildLocations(Long parentId) throws SQLException {
        return locationRepository.findByParentId(parentId);
    }
}
