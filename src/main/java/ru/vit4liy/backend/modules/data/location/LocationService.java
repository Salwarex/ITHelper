package ru.vit4liy.backend.modules.data.location;

import ru.vit4liy.base.bus.services.LocationBusService;
import ru.vit4liy.base.model.location.Location;
import ru.vit4liy.perm.MissingPermissionException;
import ru.vit4liy.perm.Permissible;
import ru.vit4liy.perm.PermissionService;
import ru.vit4liy.rsc.service.EntityService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LocationService extends EntityService<Location> implements LocationBusService {
    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository, PermissionService permissionService) {
        super(locationRepository, permissionService);
        this.locationRepository = locationRepository;
    }

    @Override
    public boolean delete(Permissible executor, long id) throws SQLException, MissingPermissionException {
        List<Long> idsToDelete = new ArrayList<>();
        collectChildIds(executor, id, idsToDelete);

        idsToDelete.add(id);

        for (Long locId : idsToDelete) {
            super.delete(executor, locId);
        }
        return true;
    }

    private void collectChildIds(Permissible executor, Long parentId, List<Long> result) throws SQLException {
        List<Location> children = locationRepository.findByParentId(parentId);
        for (Location child : children) {
            result.add(child.getId());
            collectChildIds(executor, child.getId(), result);
        }
    }

    public Optional<Location> getLocationByKey(Permissible executor, String key) throws SQLException {
        return locationRepository.findByKey(key);
    }

    public List<Location> searchLocationsByDisplayName(Permissible executor, String displayNamePart) throws SQLException, MissingPermissionException {
        if (displayNamePart == null || displayNamePart.trim().isEmpty()) {
            return getAll(executor);
        }
        return locationRepository.findByDisplayNameContaining(displayNamePart.trim());
    }

    public List<Location> getChildLocations(Permissible executor, Long parentId) throws SQLException {
        return locationRepository.findByParentId(parentId);
    }
}