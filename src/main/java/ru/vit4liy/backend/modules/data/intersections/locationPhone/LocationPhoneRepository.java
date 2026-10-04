package ru.vit4liy.backend.modules.data.intersections.locationPhone;

import ru.vit4liy.backend.base.model.intersections.LocationPhone;
import ru.vit4liy.rsc.DatabaseManager;
import ru.vit4liy.rsc.repository.IntersectionRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LocationPhoneRepository extends IntersectionRepository<LocationPhone> {
    public LocationPhoneRepository(DatabaseManager databaseManager) {
        super(LocationPhone.class, databaseManager);
    }

    @Override
    protected String[] sqlTableAttributes() {
        return new String[]{
                "location_id", "{long} NOT NULL",
                "phone_id", "{long} NOT NULL",
                "created_at", "{long} NOT NULL",
                "last_checked", "{long} NOT NULL"
        };
    }

    @Override
    protected LocationPhone map(ResultSet rs) throws SQLException {
        LocationPhone l = new LocationPhone();
        l.setAId(rs.getLong("location_id"));
        l.setBId(rs.getLong("phone_id"));
        l.setCreatedAt(rs.getLong("created_at"));
        l.setLastChecked(rs.getLong("last_checked"));

        return l;
    }

    @Override
    protected void statementPrepare(PreparedStatement stmt, LocationPhone e) throws SQLException {
        stmt.setLong(1, e.getAId());
        stmt.setLong(2, e.getBId());
        stmt.setLong(3, e.getCreatedAt());
        stmt.setLong(4, e.getLastChecked());
    }

    @Override
    protected String[] getForeignKeysQueries() {
        return new String[]{
                "FOREIGN KEY (location_id) REFERENCES Location(id) ON DELETE CASCADE",
                "FOREIGN KEY (phone_id) REFERENCES Phone(id) ON DELETE CASCADE"
        };
    }

    @Override
    protected String[] getMultiPrimaryQueries() {
        return new String[]{
                "PRIMARY KEY (location_id, phone_id)"
        };
    }

    @Override
    protected String[] getCreateIndexesStatements() {
        return new String[]{
                "CREATE INDEX idx_location_phone_loc ON LocationPhone(location_id)",
                "CREATE INDEX idx_location_phone_ph ON LocationPhone(phone_id)"
        };
    }
}
