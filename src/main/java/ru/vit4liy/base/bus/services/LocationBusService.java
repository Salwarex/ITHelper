package ru.vit4liy.base.bus.services;

import ru.vit4liy.base.model.location.Location;
import ru.vit4liy.perm.Permissible;
import ru.vit4liy.rsc.service.GenericDatabaseBusService;

import java.util.List;
import java.util.Optional;

public interface LocationBusService extends GenericDatabaseBusService<Location> {
    Optional<Location> getLocationByKey(Permissible executor, String key) throws Exception;
    List<Location> searchLocationsByDisplayName(Permissible executor, String displayNamePart) throws Exception;
    List<Location> getChildLocations(Permissible executor, Long parentId) throws Exception;
}
