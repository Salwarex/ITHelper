package ru.vit4liy.base.bus.services;

import ru.vit4liy.base.model.type.Type;
import ru.vit4liy.rsc.service.GenericDatabaseBusService;

import java.util.Optional;

public interface TypeBusService extends GenericDatabaseBusService<Type> {
    Optional<Type> getTypeByDisplayName(String displayName) throws Exception;
}
