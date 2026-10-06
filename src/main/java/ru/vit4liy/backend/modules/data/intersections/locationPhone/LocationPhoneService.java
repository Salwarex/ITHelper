package ru.vit4liy.backend.modules.data.intersections.locationPhone;

import ru.vit4liy.base.bus.services.LocationPhoneBusService;
import ru.vit4liy.base.model.intersections.LocationPhone;
import ru.vit4liy.perm.PermissionService;
import ru.vit4liy.rsc.repository.IntersectionRepository;
import ru.vit4liy.rsc.service.IntersectionService;

public class LocationPhoneService extends IntersectionService<LocationPhone> implements LocationPhoneBusService {
    public LocationPhoneService(IntersectionRepository<LocationPhone> intersectionRepository, PermissionService permissionService) {
        super(intersectionRepository, permissionService);
    }
}
