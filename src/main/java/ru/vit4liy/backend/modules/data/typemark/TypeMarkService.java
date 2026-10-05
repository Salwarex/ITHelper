package ru.vit4liy.backend.modules.data.typemark;

import ru.vit4liy.base.model.typemark.TypeMark;
import ru.vit4liy.perm.PermissionService;
import ru.vit4liy.rsc.service.EntityService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TypeMarkService extends EntityService<TypeMark> {
    private final TypeMarkRepository typeMarkRepository;

    public TypeMarkService(TypeMarkRepository typeMarkRepository, PermissionService permissionService) {
        super(typeMarkRepository, permissionService);
        this.typeMarkRepository = typeMarkRepository;
    }

    public List<TypeMark> getTypeMarksByTypeId(long typeId) throws SQLException {
        return typeMarkRepository.findByTypeId(typeId);
    }

    public Optional<TypeMark> getTypeMarkByDisplayName(String displayName) throws SQLException {
        return typeMarkRepository.findByDisplayName(displayName);
    }
}
