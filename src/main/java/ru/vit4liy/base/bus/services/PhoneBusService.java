package ru.vit4liy.base.bus.services;

import ru.vit4liy.base.model.phone.Phone;
import ru.vit4liy.perm.Permissible;
import ru.vit4liy.rsc.service.GenericDatabaseBusService;

import java.util.List;
import java.util.Optional;

public interface PhoneBusService extends GenericDatabaseBusService<Phone> {
    Optional<Phone> getPhoneByCorpNumber(Permissible executor, String corpNumber) throws Exception;
    Optional<Phone> getPhoneByPublicNumber(Permissible executor, String publicNumber) throws Exception;
    List<Phone> getPhonesByIp(Permissible executor, String ip) throws Exception;
}
