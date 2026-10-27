package ru.vit4liy.frontend.data.web.services;

import ru.vit4liy.base.bus.services.PhoneBusService;
import ru.vit4liy.base.model.phone.Phone;
import ru.vit4liy.perm.Permissible;

import java.util.List;
import java.util.Optional;

public class PhoneClientService implements PhoneBusService {

    @Override
    public Optional<Phone> getPhoneByCorpNumber(Permissible executor, String corpNumber) throws Exception {
        return Optional.empty();
    }

    @Override
    public Optional<Phone> getPhoneByPublicNumber(Permissible executor, String publicNumber) throws Exception {
        return Optional.empty();
    }

    @Override
    public List<Phone> getPhonesByIp(Permissible executor, String ip) throws Exception {
        return List.of();
    }

    @Override
    public long create(Permissible permissible, Phone phone) throws Exception {
        return 0;
    }

    @Override
    public Optional<Phone> getById(Permissible permissible, long l) throws Exception {
        return Optional.empty();
    }

    @Override
    public boolean update(Permissible permissible, Phone phone) throws Exception {
        return false;
    }

    @Override
    public boolean delete(Permissible permissible, long l) throws Exception {
        return false;
    }

    @Override
    public List<Phone> getAll(Permissible permissible) throws Exception {
        return List.of();
    }
}
