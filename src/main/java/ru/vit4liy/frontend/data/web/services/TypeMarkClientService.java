package ru.vit4liy.frontend.data.web.services;

import ru.vit4liy.base.bus.services.TypeMarkBusService;
import ru.vit4liy.base.model.typemark.TypeMark;
import ru.vit4liy.perm.Permissible;

import java.util.List;
import java.util.Optional;

public class TypeMarkClientService implements TypeMarkBusService {

    @Override
    public List<TypeMark> getTypeMarksByTypeId(Permissible executor, long typeId) throws Exception {
        return List.of();
    }

    @Override
    public Optional<TypeMark> getTypeMarkByDisplayName(Permissible executor, String displayName) throws Exception {
        return Optional.empty();
    }

    @Override
    public long create(Permissible permissible, TypeMark typeMark) throws Exception {
        return 0;
    }

    @Override
    public Optional<TypeMark> getById(Permissible permissible, long l) throws Exception {
        return Optional.empty();
    }

    @Override
    public boolean update(Permissible permissible, TypeMark typeMark) throws Exception {
        return false;
    }

    @Override
    public boolean delete(Permissible permissible, long l) throws Exception {
        return false;
    }

    @Override
    public List<TypeMark> getAll(Permissible permissible) throws Exception {
        return List.of();
    }
}
