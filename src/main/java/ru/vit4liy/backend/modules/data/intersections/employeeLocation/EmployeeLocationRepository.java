package ru.vit4liy.backend.modules.data.intersections.employeeLocation;

import ru.vit4liy.base.model.intersections.EmployeeLocation;
import ru.vit4liy.rsc.DatabaseManager;
import ru.vit4liy.rsc.repository.IntersectionRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeLocationRepository extends IntersectionRepository<EmployeeLocation> {
    public EmployeeLocationRepository(DatabaseManager databaseManager) {
        super(EmployeeLocation.class, databaseManager);
    }

    @Override
    protected String[] sqlTableAttributes() {
        return new String[]{
                "employee_id", "{long} NOT NULL",
                "location_id", "{long} NOT NULL",
                "created_at", "{long} NOT NULL",
                "last_checked", "{long} NOT NULL"
        };
    }

    @Override
    protected EmployeeLocation map(ResultSet rs) throws SQLException {
        EmployeeLocation l = new EmployeeLocation();
        l.setAId(rs.getLong("employee_id"));
        l.setBId(rs.getLong("location_id"));
        l.setCreatedAt(rs.getLong("created_at"));
        l.setLastChecked(rs.getLong("last_checked"));

        return l;
    }

    @Override
    protected void statementPrepare(PreparedStatement stmt, EmployeeLocation e) throws SQLException {
        stmt.setLong(1, e.getAId());
        stmt.setLong(2, e.getBId());
        stmt.setLong(3, e.getCreatedAt());
        stmt.setLong(4, e.getLastChecked());
    }

    @Override
    protected String[] getForeignKeysQueries() {
        return new String[]{
                "FOREIGN KEY (employee_id) REFERENCES Employee(id) ON DELETE CASCADE",
                "FOREIGN KEY (location_id) REFERENCES Location(id) ON DELETE CASCADE"
        };
    }

    @Override
    protected String[] getMultiPrimaryQueries() {
        return new String[]{
                "PRIMARY KEY (employee_id, location_id)"
        };
    }

    @Override
    protected String[] getCreateIndexesStatements() {
        return new String[]{
                "CREATE INDEX idx_employee_location_emp ON Employee_Location(employee_id)",
                "CREATE INDEX idx_employee_location_loc ON Employee_Location(location_id)",
        };
    }
}

