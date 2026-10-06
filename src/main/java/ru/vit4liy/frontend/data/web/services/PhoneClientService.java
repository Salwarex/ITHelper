package ru.vit4liy.frontend.data.web.services;

import ru.vit4liy.base.bus.services.PhoneBusService;
import ru.vit4liy.base.model.phone.Phone;

import java.util.List;
import java.util.Optional;

public class PhoneClientService implements PhoneBusService {
    @Override
    public long create(Phone phone) throws Exception {
        return 0;
    }

    @Override
    public Optional<Phone> getById(long l) throws Exception {
        return Optional.empty();
    }

    @Override
    public boolean update(Phone phone) throws Exception {
        return false;
    }

    @Override
    public boolean delete(long l) throws Exception {
        return false;
    }

    @Override
    public List<Phone> getAll() throws Exception {
        return List.of();
    }

    @Override
    public Optional<Phone> getPhoneByCorpNumber(String corpNumber) throws Exception {
        return Optional.empty();
    }

    @Override
    public Optional<Phone> getPhoneByPublicNumber(String publicNumber) throws Exception {
        return Optional.empty();
    }

    @Override
    public List<Phone> getPhonesByIp(String ip) throws Exception {
        return List.of();
    }
}
