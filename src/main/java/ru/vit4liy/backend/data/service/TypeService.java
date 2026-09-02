package ru.vit4liy.backend.data.service;

import ru.vit4liy.backend.data.model.Type;
import ru.vit4liy.backend.data.repository.TypeRepository;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TypeService {
    private final TypeRepository typeRepository;

    public TypeService(TypeRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    public long createType(Type type) throws SQLException {
        if (type.getDisplayName() != null && !type.getDisplayName().isEmpty()) {
            if (typeRepository.findByDisplayName(type.getDisplayName()).isPresent()) {
                throw new SQLException("Тип с таким именем уже существует");
            }
        }
        return typeRepository.create(type);
    }

    public Optional<Type> getTypeById(long id) throws SQLException {
        return typeRepository.findById(id);
    }

    public List<Type> getAllTypes() throws SQLException {
        return typeRepository.findAll();
    }

    public boolean updateType(Type type) throws SQLException {
        return typeRepository.update(type);
    }

    public boolean deleteType(long id) throws SQLException {
        return typeRepository.delete(id);
    }

    public Optional<Type> getTypeByDisplayName(String displayName) throws SQLException {
        return typeRepository.findByDisplayName(displayName);
    }
}
