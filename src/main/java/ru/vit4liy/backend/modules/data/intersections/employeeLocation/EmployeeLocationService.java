package ru.vit4liy.backend.modules.data.intersections.employeeLocation;

import ru.vit4liy.backend.base.model.intersections.EmployeeLocation;
import ru.vit4liy.perm.PermissionService;
import ru.vit4liy.rsc.repository.IntersectionRepository;
import ru.vit4liy.rsc.service.IntersectionService;

public class EmployeeLocationService extends IntersectionService<EmployeeLocation> {
    public EmployeeLocationService(IntersectionRepository<EmployeeLocation> intersectionRepository, PermissionService permissionService) {
        super(intersectionRepository, permissionService);
    }
}
