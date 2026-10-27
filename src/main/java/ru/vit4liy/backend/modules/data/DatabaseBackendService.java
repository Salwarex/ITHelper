package ru.vit4liy.backend.modules.data;

import ru.vit4liy.base.bus.services.DatabaseService;
import ru.vit4liy.perm.Permissible;
import ru.vit4liy.perm.PermissionService;
import ru.vit4liy.rsc.DatabaseManager;
import ru.vit4liy.rsc.service.Service;

import java.sql.SQLException;

public class DatabaseBackendService extends Service implements DatabaseService{
    private final DatabaseManager manager;

    public DatabaseBackendService(DatabaseManager manager, PermissionService permissionService) {
        super(permissionService);
        this.manager = manager;

    }

    @Override
    public void removeAll(Permissible permissible) throws SQLException {
        java.sql.Connection conn = manager.getConnection();

        conn.createStatement().execute("PRAGMA foreign_keys = OFF;");

        conn.createStatement().execute("DELETE FROM DeviceLocation");
        conn.createStatement().execute("DELETE FROM EmployeeLocation");
        conn.createStatement().execute("DELETE FROM LocationPhone");
        conn.createStatement().execute("DELETE FROM Device");
        conn.createStatement().execute("DELETE FROM Employee");
        conn.createStatement().execute("DELETE FROM Phone");
        conn.createStatement().execute("DELETE FROM TypeMark");
        conn.createStatement().execute("DELETE FROM OSystem");
        conn.createStatement().execute("DELETE FROM Type");
        conn.createStatement().execute("DELETE FROM Location");

        conn.createStatement().execute("DELETE FROM sqlite_sequence");

        conn.createStatement().execute("PRAGMA foreign_keys = ON;");
        conn.close();
    }
}
