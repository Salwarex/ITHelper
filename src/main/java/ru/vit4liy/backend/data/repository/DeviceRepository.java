package ru.vit4liy.backend.data.repository;

import ru.vit4liy.backend.data.model.Device;
import ru.vit4liy.backend.database.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DeviceRepository extends Repository<Device> {
    public DeviceRepository(DatabaseManager db) { super(db); }

    public long create(Device entity) throws SQLException {
        String sql = "INSERT INTO Device (type_id, type_mark_id, device_serial, description, status, last_checked) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, entity.getTypeId());
            if (entity.getTypeMarkId() == null) stmt.setNull(2, Types.INTEGER);
            else stmt.setInt(2, entity.getTypeMarkId());
            stmt.setString(3, entity.getDeviceSerial());
            stmt.setString(4, entity.getDescription());
            stmt.setString(5, entity.getStatus() != null ? entity.getStatus() : "GOOD_WORK");
            stmt.setLong(6, System.currentTimeMillis());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) { if (rs.next()) { entity.setId(rs.getLong(1)); return entity.getId(); } }
        }
        return -1;
    }

    public Optional<Device> findById(long id) throws SQLException {
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Device WHERE id = ?")) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
        }
        return Optional.empty();
    }

    public List<Device> findAll() throws SQLException {
        List<Device> list = new ArrayList<>();
        try (Connection conn = db.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM Device")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public boolean update(Device entity) throws SQLException {
        String sql = "UPDATE Device SET type_id = ?, type_mark_id = ?, device_serial = ?, description = ?, status = ?, last_checked = ? WHERE id = ?";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, entity.getTypeId());
            if (entity.getTypeMarkId() == null) stmt.setNull(2, Types.INTEGER);
            else stmt.setInt(2, entity.getTypeMarkId());
            stmt.setString(3, entity.getDeviceSerial());
            stmt.setString(4, entity.getDescription());
            stmt.setString(5, entity.getStatus());
            stmt.setLong(6, System.currentTimeMillis());
            stmt.setLong(7, entity.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(long id) throws SQLException {
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM Device WHERE id = ?")) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public Optional<Device> findByDeviceSerial(String serial) throws SQLException {
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Device WHERE device_serial = ?")) {
            stmt.setString(1, serial);
            try (ResultSet rs = stmt.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
        }
        return Optional.empty();
    }

    public List<Device> findByTypeId(long typeId) throws SQLException {
        List<Device> list = new ArrayList<>();
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Device WHERE type_id = ?")) {
            stmt.setLong(1, typeId);
            try (ResultSet rs = stmt.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    public List<Device> findByStatus(String status) throws SQLException {
        List<Device> list = new ArrayList<>();
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Device WHERE status = ?")) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    private Device map(ResultSet rs) throws SQLException {
        Device d = new Device();
        d.setId(rs.getLong("id"));
        d.setTypeId(rs.getInt("type_id"));
        d.setTypeMarkId(rs.getObject("type_mark_id", Integer.class));
        d.setDeviceSerial(rs.getString("device_serial"));
        d.setDescription(rs.getString("description"));
        d.setStatus(rs.getString("status"));
        d.setLastChecked(rs.getLong("last_checked"));
        return d;
    }
}