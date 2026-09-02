package ru.vit4liy.backend.data.service;

import ru.vit4liy.backend.data.model.TypeMark;
import ru.vit4liy.backend.data.repository.TypeMarksRepository;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TypeMarkService {
    private final TypeMarksRepository typeMarksRepository;

    public TypeMarkService(TypeMarksRepository typeMarksRepository) {
        this.typeMarksRepository = typeMarksRepository;
    }

    public long createTypeMark(TypeMark typeMark) throws SQLException {
        if (typeMark.getDisplayName() != null && !typeMark.getDisplayName().isEmpty()) {
            if (typeMarksRepository.findByDisplayName(typeMark.getDisplayName()).isPresent()) {
                throw new SQLException("Марка типа с таким именем уже существует");
            }
        }
        return typeMarksRepository.create(typeMark);
    }

    public Optional<TypeMark> getTypeMarkById(long id) throws SQLException {
        return typeMarksRepository.findById(id);
    }

    public List<TypeMark> getAllTypeMarks() throws SQLException {
        return typeMarksRepository.findAll();
    }

    public boolean updateTypeMark(TypeMark typeMark) throws SQLException {
        return typeMarksRepository.update(typeMark);
    }

    public boolean deleteTypeMark(long id) throws SQLException {
        return typeMarksRepository.delete(id);
    }

    public List<TypeMark> getTypeMarksByTypeId(long typeId) throws SQLException {
        return typeMarksRepository.findByTypeId(typeId);
    }

    public Optional<TypeMark> getTypeMarkByDisplayName(String displayName) throws SQLException {
        return typeMarksRepository.findByDisplayName(displayName);
    }
}
