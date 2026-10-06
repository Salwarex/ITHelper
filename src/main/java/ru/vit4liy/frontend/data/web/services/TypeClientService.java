package ru.vit4liy.frontend.data.web.services;

import ru.vit4liy.base.bus.services.TypeBusService;
import ru.vit4liy.base.model.type.Type;

import java.util.List;
import java.util.Optional;

public class TypeClientService implements TypeBusService {
    @Override
    public long create(Type type) throws Exception {
        return 0;
    }

    @Override
    public Optional<Type> getById(long l) throws Exception {
        return Optional.empty();
    }

    @Override
    public boolean update(Type type) throws Exception {
        return false;
    }

    @Override
    public boolean delete(long l) throws Exception {
        return false;
    }

    @Override
    public List<Type> getAll() throws Exception {
        return List.of();
    }

    @Override
    public Optional<Type> getTypeByDisplayName(String displayName) throws Exception {
        return Optional.empty();
    }
}
