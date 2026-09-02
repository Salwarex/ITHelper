package ru.vit4liy.backend.data.repository;

import ru.vit4liy.backend.data.model.TypeMark;
import ru.vit4liy.backend.database.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TypeMarksRepository extends Repository<TypeMark> {
    public TypeMarksRepository(DatabaseManager db) { super(db); }

    public long create(TypeMark entity) throws SQLException {
        String sql = "INSERT INTO TypeMarks (type_id, display_name, created_at) VALUES (?, ?, ?)";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, entity.getTypeId());
            stmt.setString(2, entity.getDisplayName());
            stmt.setLong(3, System.currentTimeMillis());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) { if (rs.next()) { entity.setId(rs.getLong(1)); return entity.getId(); } }
        }
        return -1;
    }

    public Optional<TypeMark> findById(long id) throws SQLException {
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM TypeMarks WHERE id = ?")) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
        }
        return Optional.empty();
    }

    public List<TypeMark> findAll() throws SQLException {
        List<TypeMark> list = new ArrayList<>();
        try (Connection conn = db.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM TypeMarks")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<TypeMark> findByTypeId(long typeId) throws SQLException {
        List<TypeMark> list = new ArrayList<>();
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM TypeMarks WHERE type_id = ?")) {
            stmt.setLong(1, typeId);
            try (ResultSet rs = stmt.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    public boolean update(TypeMark entity) throws SQLException {
        String sql = "UPDATE TypeMarks SET display_name = ? WHERE id = ?";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getDisplayName());
            stmt.setLong(2, entity.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(long id) throws SQLException {
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM TypeMarks WHERE id = ?")) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public Optional<TypeMark> findByDisplayName(String displayName) throws SQLException {
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM TypeMarks WHERE display_name = ?")) {
            stmt.setString(1, displayName);
            try (ResultSet rs = stmt.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
        }
        return Optional.empty();
    }

    private TypeMark map(ResultSet rs) throws SQLException {
        TypeMark tm = new TypeMark();
        tm.setId(rs.getLong("id"));
        tm.setTypeId(rs.getLong("type_id"));
        tm.setDisplayName(rs.getString("display_name"));
        tm.setCreatedAt(rs.getLong("created_at"));
        return tm;
    }
}