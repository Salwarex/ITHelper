package ru.vit4liy.base.bus.services;

import ru.vit4liy.base.model.location.Location;
import ru.vit4liy.rsc.service.GenericDatabaseBusService;

import java.util.List;
import java.util.Optional;

public interface LocationBusService extends GenericDatabaseBusService<Location> {
    Optional<Location> getLocationByKey(String key) throws Exception;
    List<Location> searchLocationsByDisplayName(String displayNamePart) throws Exception;
    List<Location> getChildLocations(Long parentId) throws Exception;
}
