package ru.vit4liy.backend.modules.data.device;

import ru.vit4liy.base.model.device.Device;
import ru.vit4liy.rsc.DatabaseManager;
import ru.vit4liy.rsc.repository.EntityRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DeviceRepository extends EntityRepository<Device> {
    public DeviceRepository(DatabaseManager db) { super(Device.class, db); }

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

    public List<Device> findByIp(String ip) throws SQLException {
        List<Device> list = new ArrayList<>();
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Device WHERE ip = ?")) {
            stmt.setString(1, ip);
            try (ResultSet rs = stmt.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    public List<Device> findByOsId(long osId) throws SQLException {
        List<Device> list = new ArrayList<>();
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Device WHERE os_id = ?")) {
            stmt.setLong(1, osId);
            try (ResultSet rs = stmt.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    protected Device map(ResultSet rs) throws SQLException {
        Device d = new Device();
        d.setId(rs.getLong("id"));
        d.setTypeId(rs.getInt("type_id"));

        Object typeMarkObj = rs.getObject("type_mark_id");
        d.setTypeMarkId(typeMarkObj == null ? null : ((Number) typeMarkObj).intValue());

        Object osObj = rs.getObject("os_id");
        d.setOsId(osObj == null ? null : ((Number) osObj).longValue());

        d.setDeviceSerial(rs.getString("device_serial"));
        d.setDescription(rs.getString("description"));
        d.setStatus(rs.getString("status"));
        d.setIp(rs.getString("ip"));
        d.setLastChecked(rs.getLong("last_checked"));
        return d;
    }

    @Override
    protected void statementPrepare(PreparedStatement stmt, Device device) throws SQLException {
        stmt.setInt(1, device.getTypeId());

        if (device.getTypeMarkId() == null) {
            stmt.setNull(2, java.sql.Types.INTEGER);
        } else {
            stmt.setInt(2, device.getTypeMarkId());
        }

        if (device.getOsId() == null) {
            stmt.setNull(3, java.sql.Types.BIGINT);
        } else {
            stmt.setLong(3, device.getOsId());
        }

        stmt.setString(4, device.getDeviceSerial());
        stmt.setString(5, device.getDescription());
        stmt.setString(6, device.getStatus());

        if (device.getIp() == null) {
            stmt.setNull(7, java.sql.Types.VARCHAR);
        } else {
            stmt.setString(7, device.getIp());
        }

        stmt.setLong(8, device.getLastChecked());
    }

    @Override
    protected String[] sqlTableAttributes() {
        return new String[]{
                "id", "{primary}",
                "type_id", "{int} NOT NULL",
                "type_mark_id", "{int}",
                "os_id", "{long}",
                "device_serial", "VARCHAR(255) UNIQUE",
                "description", "VARCHAR(255)",
                "status", "VARCHAR(50) NOT NULL DEFAULT 'GOOD_WORK'",
                "ip", "VARCHAR(45)",
                "last_checked", "{long} NOT NULL",
        };
    }

    @Override
    protected String[] getForeignKeysQueries() {
        return new String[]{
                "FOREIGN KEY (type_id) REFERENCES Type(id) ON DELETE CASCADE",
                "FOREIGN KEY (type_mark_id) REFERENCES TypeMark(id) ON DELETE SET NULL",
                "FOREIGN KEY (os_id) REFERENCES OSystem(id) ON DELETE SET NULL"
        };
    }

    @Override
    protected String[] getCreateIndexesStatements() {
        return new String[]{
                "CREATE INDEX idx_device_type ON Device(type_id)",
                "CREATE INDEX idx_device_mark ON Device(type_mark_id)",
                "CREATE INDEX idx_device_serial ON Device(device_serial)",
                "CREATE INDEX idx_device_os ON Device(os_id)",
                "CREATE INDEX idx_device_ip ON Device(ip)"
        };
    }
}