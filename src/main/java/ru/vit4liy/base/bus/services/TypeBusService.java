package ru.vit4liy.base.bus.services;

import ru.vit4liy.base.model.type.Type;
import ru.vit4liy.perm.Permissible;
import ru.vit4liy.rsc.service.GenericDatabaseBusService;
import ru.vit4liy.rsc.service.GenericEntityBusService;

import java.util.Optional;

public interface TypeBusService extends GenericEntityBusService<Type> {
    Optional<Type> getTypeByDisplayName(Permissible executor, String displayName) throws Exception;
}
