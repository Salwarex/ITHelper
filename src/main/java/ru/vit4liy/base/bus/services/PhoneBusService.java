package ru.vit4liy.base.bus.services;

import ru.vit4liy.base.model.phone.Phone;
import ru.vit4liy.rsc.service.GenericDatabaseBusService;

import java.util.List;
import java.util.Optional;

public interface PhoneBusService extends GenericDatabaseBusService<Phone> {
    Optional<Phone> getPhoneByCorpNumber(String corpNumber) throws Exception;
    Optional<Phone> getPhoneByPublicNumber(String publicNumber) throws Exception;
    List<Phone> getPhonesByIp(String ip) throws Exception;
}
