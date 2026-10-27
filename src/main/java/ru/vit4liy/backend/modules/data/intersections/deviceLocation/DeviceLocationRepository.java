package ru.vit4liy.backend.modules.data.intersections.deviceLocation;

import ru.vit4liy.base.model.intersections.DeviceLocation;
import ru.vit4liy.rsc.DatabaseManager;
import ru.vit4liy.rsc.repository.IntersectionRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeviceLocationRepository extends IntersectionRepository<DeviceLocation> {
    public DeviceLocationRepository(DatabaseManager databaseManager) {
        super(DeviceLocation.class, databaseManager);
    }

    @Override
    protected String[] sqlTableAttributes() {
        return new String[]{
                "device_id", "{long} NOT NULL",
                "location_id", "{long} NOT NULL",
                "created_at", "{long} NOT NULL",
                "last_checked", "{long} NOT NULL"
        };
    }

    @Override
    protected DeviceLocation map(ResultSet rs) throws SQLException {
        DeviceLocation l = new DeviceLocation();
        l.setAId(rs.getLong("device_id"));
        l.setBId(rs.getLong("location_id"));
        l.setCreatedAt(rs.getLong("created_at"));
        l.setLastChecked(rs.getLong("last_checked"));

        return l;
    }

    @Override
    protected void statementPrepare(PreparedStatement stmt, DeviceLocation e) throws SQLException {
        stmt.setLong(1, e.getAId());
        stmt.setLong(2, e.getBId());
        stmt.setLong(3, e.getCreatedAt());
        stmt.setLong(4, e.getLastChecked());
    }

    @Override
    protected String[] getForeignKeysQueries() {
        return new String[]{
                "FOREIGN KEY (location_id) REFERENCES Location(id) ON DELETE CASCADE",
                "FOREIGN KEY (device_id) REFERENCES Device(id) ON DELETE CASCADE"
        };
    }

    @Override
    protected String[] getMultiPrimaryQueries() {
        return new String[]{
                "PRIMARY KEY (location_id, device_id)"
        };
    }

    @Override
    protected String[] getCreateIndexesStatements() {
        return new String[]{
                "CREATE INDEX idx_device_location_loc ON Device_Location(location_id)",
                "CREATE INDEX idx_device_location_dev ON Device_Location(device_id)",
        };
    }
}
