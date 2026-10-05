package ru.vit4liy.backend.modules.data.intersections.deviceLocation;

import ru.vit4liy.base.model.intersections.DeviceLocation;
import ru.vit4liy.perm.PermissionService;
import ru.vit4liy.rsc.repository.IntersectionRepository;
import ru.vit4liy.rsc.service.IntersectionService;

public class DeviceLocationService extends IntersectionService<DeviceLocation> {
    public DeviceLocationService(IntersectionRepository<DeviceLocation> intersectionRepository, PermissionService permissionService) {
        super(intersectionRepository, permissionService);
    }
}
