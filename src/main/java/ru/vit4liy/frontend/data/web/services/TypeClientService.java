package ru.vit4liy.frontend.data.web.services;

import ru.vit4liy.base.bus.services.TypeBusService;
import ru.vit4liy.base.model.type.Type;
import ru.vit4liy.perm.Permissible;

import java.util.List;
import java.util.Optional;

public class TypeClientService implements TypeBusService {

    @Override
    public Optional<Type> getTypeByDisplayName(Permissible executor, String displayName) throws Exception {
        return Optional.empty();
    }

    @Override
    public long create(Permissible permissible, Type type) throws Exception {
        return 0;
    }

    @Override
    public Optional<Type> getById(Permissible permissible, long l) throws Exception {
        return Optional.empty();
    }

    @Override
    public boolean update(Permissible permissible, Type type) throws Exception {
        return false;
    }

    @Override
    public boolean delete(Permissible permissible, long l) throws Exception {
        return false;
    }

    @Override
    public List<Type> getAll(Permissible permissible) throws Exception {
        return List.of();
    }
}
