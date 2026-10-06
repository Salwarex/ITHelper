package ru.vit4liy.frontend.data.web.services;

import ru.vit4liy.base.bus.services.TypeMarkBusService;
import ru.vit4liy.base.model.typemark.TypeMark;

import java.util.List;
import java.util.Optional;

public class TypeMarkClientService implements TypeMarkBusService {
    @Override
    public long create(TypeMark typeMark) throws Exception {
        return 0;
    }

    @Override
    public Optional<TypeMark> getById(long l) throws Exception {
        return Optional.empty();
    }

    @Override
    public boolean update(TypeMark typeMark) throws Exception {
        return false;
    }

    @Override
    public boolean delete(long l) throws Exception {
        return false;
    }

    @Override
    public List<TypeMark> getAll() throws Exception {
        return List.of();
    }

    @Override
    public List<TypeMark> getTypeMarksByTypeId(long typeId) throws Exception {
        return List.of();
    }

    @Override
    public Optional<TypeMark> getTypeMarkByDisplayName(String displayName) throws Exception {
        return Optional.empty();
    }
}
