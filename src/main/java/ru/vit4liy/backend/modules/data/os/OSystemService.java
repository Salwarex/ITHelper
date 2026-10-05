package ru.vit4liy.backend.modules.data.os;

import ru.vit4liy.base.model.device.system.OSystem;
import ru.vit4liy.perm.PermissionService;
import ru.vit4liy.rsc.service.EntityService;

public class OSystemService extends EntityService<OSystem> {
    private final OSystemRepository oSystemRepository;

    public OSystemService(OSystemRepository oSystemRepository, PermissionService permissionService) {
        super(oSystemRepository, permissionService);
        this.oSystemRepository = oSystemRepository;
    }
}