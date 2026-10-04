package ru.vit4liy.backend.modules.data.os;

import ru.vit4liy.backend.base.model.device.system.OSystem;
import ru.vit4liy.rsc.DatabaseManager;
import ru.vit4liy.rsc.repository.EntityRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OSystemRepository extends EntityRepository<OSystem> {
    public OSystemRepository(DatabaseManager databaseManager) {
        super(OSystem.class, databaseManager);
    }

    @Override
    protected String[] sqlTableAttributes() {
        return new String[]{
                "id", "{primary}",
                "display_name", "VARCHAR(255) NOT NULL UNIQUE"
        };
    }

    @Override
    protected OSystem map(ResultSet rs) throws SQLException {
        OSystem os = new OSystem();
        os.setId(rs.getLong("id"));
        os.setDisplayName(rs.getString("display_name"));
        return os;
    }

    @Override
    protected void statementPrepare(PreparedStatement stmt, OSystem oSystem) throws SQLException {
        stmt.setString(1, oSystem.getDisplayName());
    }

    @Override
    protected String[] getForeignKeysQueries() {
        return new String[0];
    }

    @Override
    protected String[] getCreateIndexesStatements() {
        return new String[0];
    }
}
