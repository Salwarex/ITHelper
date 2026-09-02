package ru.vit4liy.backend.database;

import ru.vit4liy.App;
import ru.vit4liy.backend.files.FileUtils;
import ru.vit4liy.it72h.lib.logger.log.Logger;
import ru.vit4liy.it72h.lib.logger.output.Output;
import ru.vit4liy.it72h.lib.logger.output.OutputPreset;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private final ThreadLocal<Connection> threadConnection = new ThreadLocal<>();

    private final boolean isMySQL = App.getConfig().getString("database.type", "SQLite").equalsIgnoreCase("MySQL");

    private String jdbcUrl;
    private String username = null;
    private String password = null;

    private final Logger logger = App.getLogger();

    public DatabaseManager() {
        prepareConfig();
        createTables();
    }

    private void prepareConfig() {
        if (isMySQL) {
            String host = App.getConfig().getString("database.host", "localhost");
            int port = App.getConfig().getInt("database.port", 3306);
            String database = App.getConfig().getString("database.database", "account_system");
            this.username = App.getConfig().getString("database.username", "root");
            this.password = App.getConfig().getString("database.password", "");

            this.jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database
                    + "?useSSL=false"
                    + "&autoReconnect=true"
                    + "&connectTimeout=10000"
                    + "&socketTimeout=60000"
                    + "&characterEncoding=UTF-8";

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                logger.out(Output.of(OutputPreset.DEFAULT, "MySQL драйвер успешно загружен."));
            } catch (ClassNotFoundException e) {
                logger.out(Output.of(OutputPreset.ERROR, "MySQL драйвер не найден! " + e.getMessage()));
            }
        } else {
            Path appDataDir = FileUtils.getAppDataDirectory();
            try {
                Files.createDirectories(appDataDir);
            } catch (IOException e) {
                logger.out(Output.of(OutputPreset.ERROR, "Не удалось создать папку для данных! " + e.getMessage()));
                return;
            }

            File dbFile = appDataDir.resolve("database.db").toFile();
            this.jdbcUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();

            try {
                Class.forName("org.sqlite.JDBC");
                logger.out(Output.of(OutputPreset.DEFAULT, "SQLite драйвер успешно загружен."));
            } catch (ClassNotFoundException e) {
                logger.out(Output.of(OutputPreset.ERROR, "SQLite драйвер не обнаружен! " + e.getMessage()));
            }
        }
    }

    private Connection createNewConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
        if (!isMySQL) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
        }
        return conn;
    }

    private void createTables() {
        String pk = isMySQL ? "BIGINT PRIMARY KEY AUTO_INCREMENT" : "INTEGER PRIMARY KEY AUTOINCREMENT";
        String bigint = isMySQL ? "BIGINT" : "INTEGER";
        String intType = isMySQL ? "INT" : "INTEGER";
        String boolType = isMySQL ? "BOOLEAN" : "INTEGER";

        String[] queries = {
                "CREATE TABLE IF NOT EXISTS Type (" +
                        "id " + pk + ", " +
                        "display_name VARCHAR(255) NOT NULL UNIQUE, " +
                        "created_at " + bigint + " NOT NULL)",

                "CREATE TABLE IF NOT EXISTS TypeMarks (" +
                        "id " + pk + ", " +
                        "type_id " + intType + " NOT NULL, " +
                        "display_name VARCHAR(255) NOT NULL UNIQUE, " +
                        "created_at " + bigint + " NOT NULL, " +
                        "FOREIGN KEY (type_id) REFERENCES Type(id) ON DELETE CASCADE)",

                "CREATE TABLE IF NOT EXISTS Location (" +
                        "id " + pk + ", " +
                        "key VARCHAR(255) NOT NULL UNIQUE, " +
                        "parent_id " + bigint + ", " +
                        "display_name VARCHAR(255) NOT NULL, " +
                        "FOREIGN KEY (parent_id) REFERENCES Location(id) ON DELETE SET NULL)",

                "CREATE TABLE IF NOT EXISTS Phone (" +
                        "id " + pk + ", " +
                        "corp_phone_number VARCHAR(4) NOT NULL, " +
                        "public_phone_number VARCHAR(6) NOT NULL, " +
                        "last_checked " + bigint + " NOT NULL)",

                "CREATE TABLE IF NOT EXISTS Employee (" +
                        "id " + pk + ", " +
                        "surname VARCHAR(255), " +
                        "name VARCHAR(255), " +
                        "thirdname VARCHAR(255), " +
                        "created_at " + bigint + " NOT NULL, " +
                        "last_status_change " + bigint + " NOT NULL, " +
                        "last_checked " + bigint + " NOT NULL, " +
                        "status " + intType + " NOT NULL DEFAULT 1)",

                "CREATE TABLE IF NOT EXISTS Device (" +
                        "id " + pk + ", " +
                        "type_id " + intType + " NOT NULL, " +
                        "type_mark_id " + intType + ", " +
                        "device_serial VARCHAR(255) UNIQUE, " +
                        "description TEXT, " +
                        "status VARCHAR(50) NOT NULL DEFAULT 'GOOD_WORK', " +
                        "last_checked " + bigint + " NOT NULL, " +
                        "FOREIGN KEY (type_id) REFERENCES Type(id) ON DELETE CASCADE, " +
                        "FOREIGN KEY (type_mark_id) REFERENCES TypeMarks(id) ON DELETE SET NULL)",

                "CREATE TABLE IF NOT EXISTS Employee_Location (" +
                        "employee_id " + bigint + " NOT NULL, " +
                        "location_id " + bigint + " NOT NULL, " +
                        "created_at " + bigint + " NOT NULL, " +
                        "last_checked " + bigint + " NOT NULL, " +
                        "PRIMARY KEY (employee_id, location_id), " +
                        "FOREIGN KEY (employee_id) REFERENCES Employee(id) ON DELETE CASCADE, " +
                        "FOREIGN KEY (location_id) REFERENCES Location(id) ON DELETE CASCADE)",

                "CREATE TABLE IF NOT EXISTS Location_Phone (" +
                        "location_id " + bigint + " NOT NULL, " +
                        "phone_id " + bigint + " NOT NULL, " +
                        "last_checked " + bigint + " NOT NULL, " +
                        "PRIMARY KEY (location_id, phone_id), " +
                        "FOREIGN KEY (location_id) REFERENCES Location(id) ON DELETE CASCADE, " +
                        "FOREIGN KEY (phone_id) REFERENCES Phone(id) ON DELETE CASCADE)",

                "CREATE TABLE IF NOT EXISTS Device_Location (" +
                        "location_id " + bigint + " NOT NULL, " +
                        "device_id " + bigint + " NOT NULL, " +
                        "last_checked " + bigint + " NOT NULL, " +
                        "PRIMARY KEY (location_id, device_id), " +
                        "FOREIGN KEY (location_id) REFERENCES Location(id) ON DELETE CASCADE, " +
                        "FOREIGN KEY (device_id) REFERENCES Device(id) ON DELETE CASCADE)"
        };

        try (Connection initConn = createNewConnection();
             Statement stmt = initConn.createStatement()) {
            for (String query : queries) {
                stmt.execute(query);
            }
            logger.out(Output.of(OutputPreset.DEFAULT, "Таблицы БД созданы/проверены."));
        } catch (SQLException e) {
            logger.out(Output.of(OutputPreset.ERROR, "Ошибка создания таблиц! " + e.getMessage()));
            return;
        }

        String[] indexQueries = isMySQL ? new String[]{
                "CREATE INDEX idx_employee_status ON Employee(status)",
                "CREATE INDEX idx_employee_last_checked ON Employee(last_checked)",
                "CREATE INDEX idx_employee_location_emp ON Employee_Location(employee_id)",
                "CREATE INDEX idx_employee_location_loc ON Employee_Location(location_id)",
                "CREATE INDEX idx_location_parent ON Location(parent_id)",
                "CREATE INDEX idx_location_phone_loc ON Location_Phone(location_id)",
                "CREATE INDEX idx_location_phone_ph ON Location_Phone(phone_id)",
                "CREATE INDEX idx_device_type ON Device(type_id)",
                "CREATE INDEX idx_device_mark ON Device(type_mark_id)",
                "CREATE INDEX idx_device_serial ON Device(device_serial)",
                "CREATE INDEX idx_device_location_loc ON Device_Location(location_id)",
                "CREATE INDEX idx_device_location_dev ON Device_Location(device_id)",
                "CREATE INDEX idx_typemarks_type ON TypeMarks(type_id)"
        } : new String[]{
                "CREATE INDEX IF NOT EXISTS idx_employee_status ON Employee(status)",
                "CREATE INDEX IF NOT EXISTS idx_employee_last_checked ON Employee(last_checked)",
                "CREATE INDEX IF NOT EXISTS idx_employee_location_emp ON Employee_Location(employee_id)",
                "CREATE INDEX IF NOT EXISTS idx_employee_location_loc ON Employee_Location(location_id)",
                "CREATE INDEX IF NOT EXISTS idx_location_parent ON Location(parent_id)",
                "CREATE INDEX IF NOT EXISTS idx_location_phone_loc ON Location_Phone(location_id)",
                "CREATE INDEX IF NOT EXISTS idx_location_phone_ph ON Location_Phone(phone_id)",
                "CREATE INDEX IF NOT EXISTS idx_device_type ON Device(type_id)",
                "CREATE INDEX IF NOT EXISTS idx_device_mark ON Device(type_mark_id)",
                "CREATE INDEX IF NOT EXISTS idx_device_serial ON Device(device_serial)",
                "CREATE INDEX IF NOT EXISTS idx_device_location_loc ON Device_Location(location_id)",
                "CREATE INDEX IF NOT EXISTS idx_device_location_dev ON Device_Location(device_id)",
                "CREATE INDEX IF NOT EXISTS idx_typemarks_type ON TypeMarks(type_id)"
        };

        try (Connection initConn = createNewConnection()) {
            for (String query : indexQueries) {
                try (Statement stmt = initConn.createStatement()) {
                    stmt.execute(query);
                } catch (SQLException ignored) {}
            }
        } catch (SQLException e) {
            logger.out(Output.of(OutputPreset.WARN, "Ошибка создания индексов: " + e.getMessage()));
        }
    }

    public Connection getConnection() throws SQLException {
        Connection conn = threadConnection.get();

        if (conn == null || conn.isClosed() || !isConnectionValid(conn)) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {}
            }
            conn = createNewConnection();
            threadConnection.set(conn);
        }
        return conn;
    }

    private boolean isConnectionValid(Connection conn) {
        try {
            return conn != null && conn.isValid(3);
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean isMySQL() {
        return isMySQL;
    }

    public void closeCurrentThreadConnection() {
        Connection conn = threadConnection.get();
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                logger.out(Output.of(OutputPreset.ERROR, "Ошибка закрытия connection: " + e.getMessage()));
            } finally {
                threadConnection.remove();
            }
        }
    }

    public void close() {
        closeCurrentThreadConnection();
    }
}