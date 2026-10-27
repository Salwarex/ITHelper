package ru.vit4liy.base.bus.services;

import ru.vit4liy.base.model.typemark.TypeMark;
import ru.vit4liy.perm.Permissible;
import ru.vit4liy.rsc.service.GenericEntityBusService;

import java.util.List;
import java.util.Optional;

public interface TypeMarkBusService extends GenericEntityBusService<TypeMark> {
    List<TypeMark> getTypeMarksByTypeId(Permissible executor, long typeId) throws Exception;
    Optional<TypeMark> getTypeMarkByDisplayName(Permissible executor, String displayName) throws Exception;
}
