package ru.vit4liy.backend.data.service;

import ru.vit4liy.backend.data.model.Phone;
import ru.vit4liy.backend.data.repository.PhoneRepository;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PhoneService {
    private final PhoneRepository phoneRepository;

    public PhoneService(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }

    public long createPhone(Phone phone) throws SQLException {
        if (phone.getCorpPhoneNumber() != null && !phone.getCorpPhoneNumber().isEmpty()) {
            if (phoneRepository.findByCorpPhoneNumber(phone.getCorpPhoneNumber()).isPresent()) {
                throw new SQLException("Телефон с таким корпоративным номером уже существует");
            }
        }
        return phoneRepository.create(phone);
    }

    public Optional<Phone> getPhoneById(long id) throws SQLException {
        return phoneRepository.findById(id);
    }

    public List<Phone> getAllPhones() throws SQLException {
        return phoneRepository.findAll();
    }

    public boolean updatePhone(Phone phone) throws SQLException {
        return phoneRepository.update(phone);
    }

    public boolean deletePhone(long id) throws SQLException {
        return phoneRepository.delete(id);
    }

    public Optional<Phone> getPhoneByCorpNumber(String corpNumber) throws SQLException {
        return phoneRepository.findByCorpPhoneNumber(corpNumber);
    }

    public Optional<Phone> getPhoneByPublicNumber(String publicNumber) throws SQLException {
        return phoneRepository.findByPublicPhoneNumber(publicNumber);
    }
}
