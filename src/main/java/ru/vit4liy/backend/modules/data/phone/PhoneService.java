package ru.vit4liy.backend.modules.data.phone;

import ru.vit4liy.base.bus.services.PhoneBusService;
import ru.vit4liy.base.model.phone.Phone;
import ru.vit4liy.perm.PermissionService;
import ru.vit4liy.rsc.service.EntityService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PhoneService extends EntityService<Phone> implements PhoneBusService {
    private final PhoneRepository phoneRepository;

    public PhoneService(PhoneRepository phoneRepository, PermissionService permissionService) {
        super(phoneRepository, permissionService);
        this.phoneRepository = phoneRepository;
    }

    public Optional<Phone> getPhoneByCorpNumber(String corpNumber) throws SQLException {
        return phoneRepository.findByCorpPhoneNumber(corpNumber);
    }

    public Optional<Phone> getPhoneByPublicNumber(String publicNumber) throws SQLException {
        return phoneRepository.findByPublicPhoneNumber(publicNumber);
    }

    public List<Phone> getPhonesByIp(String ip) throws SQLException {
        return phoneRepository.findByIp(ip);
    }
}