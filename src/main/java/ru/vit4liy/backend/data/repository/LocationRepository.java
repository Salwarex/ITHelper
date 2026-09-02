package ru.vit4liy.backend.data.repository;

import ru.vit4liy.backend.data.model.Location;
import ru.vit4liy.backend.database.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LocationRepository extends Repository<Location> {
    public LocationRepository(DatabaseManager db) { super(db); }

    public long create(Location entity) throws SQLException {
        String sql = "INSERT INTO Location (key, parent_id, display_name) VALUES (?, ?, ?)";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entity.getKey());
            if (entity.getParentId() == null) stmt.setNull(2, Types.BIGINT);
            else stmt.setLong(2, entity.getParentId());
            stmt.setString(3, entity.getDisplayName());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) { if (rs.next()) { entity.setId(rs.getLong(1)); return entity.getId(); } }
        }
        return -1;
    }

    public Optional<Location> findById(long id) throws SQLException {
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Location WHERE id = ?")) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
        }
        return Optional.empty();
    }

    public List<Location> findAll() throws SQLException {
        List<Location> list = new ArrayList<>();
        try (Connection conn = db.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM Location")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public boolean update(Location entity) throws SQLException {
        String sql = "UPDATE Location SET key = ?, parent_id = ?, display_name = ? WHERE id = ?";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getKey());
            if (entity.getParentId() == null) stmt.setNull(2, Types.BIGINT);
            else stmt.setLong(2, entity.getParentId());
            stmt.setString(3, entity.getDisplayName());
            stmt.setLong(4, entity.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(long id) throws SQLException {
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM Location WHERE id = ?")) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public Optional<Location> findByKey(String key) throws SQLException {
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Location WHERE key = ?")) {
            stmt.setString(1, key);
            try (ResultSet rs = stmt.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
        }
        return Optional.empty();
    }

    public List<Location> findByDisplayNameContaining(String displayNamePart) throws SQLException {
        List<Location> list = new ArrayList<>();
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Location WHERE display_name LIKE ?")) {
            stmt.setString(1, "%" + displayNamePart + "%");
            try (ResultSet rs = stmt.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    public List<Location> findByParentId(Long parentId) throws SQLException {
        List<Location> list = new ArrayList<>();
        String sql = parentId == null ? "SELECT * FROM Location WHERE parent_id IS NULL" : "SELECT * FROM Location WHERE parent_id = ?";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (parentId != null) stmt.setLong(1, parentId);
            try (ResultSet rs = stmt.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    private Location map(ResultSet rs) throws SQLException {
        Location l = new Location();
        l.setId(rs.getLong("id"));
        l.setKey(rs.getString("key"));
        l.setParentId(rs.getObject("parent_id", Long.class));
        l.setDisplayName(rs.getString("display_name"));
        return l;
    }
}