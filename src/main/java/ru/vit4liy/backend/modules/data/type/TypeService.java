package ru.vit4liy.backend.modules.data.type;

import ru.vit4liy.base.model.type.Type;
import ru.vit4liy.perm.PermissionService;
import ru.vit4liy.rsc.service.EntityService;

import java.sql.SQLException;
import java.util.Optional;

public class TypeService extends EntityService<Type> {
    private final TypeRepository typeRepository;

    public TypeService(TypeRepository typeRepository, PermissionService permissionService) {
        super(typeRepository, permissionService);
        this.typeRepository = typeRepository;
    }

    public Optional<Type> getTypeByDisplayName(String displayName) throws SQLException {
        return typeRepository.findByDisplayName(displayName);
    }
}
