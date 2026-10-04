package ru.vit4liy.backend.modules.data.location;

import ru.vit4liy.backend.base.model.location.Location;
import ru.vit4liy.rsc.DatabaseManager;
import ru.vit4liy.rsc.repository.EntityRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LocationRepository extends EntityRepository<Location> {
    public LocationRepository(DatabaseManager db) { super(Location.class, db); }

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

    @Override
    protected String[] sqlTableAttributes() {
        return new String[]{
                "id", "{primary}",
                "key", "VARCHAR(255) NOT NULL UNIQUE",
                "parent_id", "{long}",
                "display_name", "VARCHAR(255) NOT NULL",
        };
    }

    protected Location map(ResultSet rs) throws SQLException {
        Location l = new Location();
        l.setId(rs.getLong("id"));
        l.setKey(rs.getString("key"));

        Object parentIdObj = rs.getObject("parent_id");
        l.setParentId(parentIdObj == null ? null : ((Number) parentIdObj).longValue());

        l.setDisplayName(rs.getString("display_name"));
        return l;
    }

    @Override
    protected void statementPrepare(PreparedStatement stmt, Location location) throws SQLException {
        stmt.setString(1, location.getKey());
        if (location.getParentId() == null) {
            stmt.setNull(2, java.sql.Types.BIGINT);
        } else {
            stmt.setLong(2, location.getParentId());
        }
        stmt.setString(3, location.getDisplayName());
    }

    @Override
    protected String[] getForeignKeysQueries() {
        return new String[]{
                "FOREIGN KEY (parent_id) REFERENCES Location(id) ON DELETE SET NULL"
        };
    }

    @Override
    protected String[] getCreateIndexesStatements() {
        return new String[]{
                "CREATE INDEX idx_location_parent ON Location(parent_id)"
        };
    }
}