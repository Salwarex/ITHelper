package ru.vit4liy.backend.data.repository;

import ru.vit4liy.backend.data.model.Type;
import ru.vit4liy.backend.database.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TypeRepository extends Repository<Type> {
    public TypeRepository(DatabaseManager db) { super(db); }

    public long create(Type entity) throws SQLException {
        String sql = "INSERT INTO Type (display_name, created_at) VALUES (?, ?)";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entity.getDisplayName());
            stmt.setLong(2, System.currentTimeMillis());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) { if (rs.next()) { entity.setId(rs.getLong(1)); return entity.getId(); } }
        }
        return -1;
    }

    public Optional<Type> findById(long id) throws SQLException {
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Type WHERE id = ?")) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
        }
        return Optional.empty();
    }

    public List<Type> findAll() throws SQLException {
        List<Type> list = new ArrayList<>();
        try (Connection conn = db.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM Type")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public boolean update(Type entity) throws SQLException {
        String sql = "UPDATE Type SET display_name = ? WHERE id = ?";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getDisplayName());
            stmt.setLong(2, entity.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(long id) throws SQLException {
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM Type WHERE id = ?")) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public Optional<Type> findByDisplayName(String displayName) throws SQLException {
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Type WHERE display_name = ?")) {
            stmt.setString(1, displayName);
            try (ResultSet rs = stmt.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
        }
        return Optional.empty();
    }

    private Type map(ResultSet rs) throws SQLException {
        Type t = new Type();
        t.setId(rs.getLong("id"));
        t.setDisplayName(rs.getString("display_name"));
        t.setCreatedAt(rs.getLong("created_at"));
        return t;
    }
}