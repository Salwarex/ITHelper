package ru.vit4liy.base.bus.services;

import ru.vit4liy.base.model.typemark.TypeMark;
import ru.vit4liy.rsc.service.GenericDatabaseBusService;

import java.util.List;
import java.util.Optional;

public interface TypeMarkBusService extends GenericDatabaseBusService<TypeMark> {
    List<TypeMark> getTypeMarksByTypeId(long typeId) throws Exception;
    Optional<TypeMark> getTypeMarkByDisplayName(String displayName) throws Exception;
}
