package ru.vit4liy.backend.modules.data.type;

import ru.vit4liy.base.model.type.Type;
import ru.vit4liy.rsc.DatabaseManager;
import ru.vit4liy.rsc.repository.EntityRepository;

import java.sql.*;
import java.util.Optional;

public class TypeRepository extends EntityRepository<Type> {
    public TypeRepository(DatabaseManager db) { super(Type.class, db); }

    public Optional<Type> findByDisplayName(String displayName) throws SQLException {
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Type WHERE display_name = ?")) {
            stmt.setString(1, displayName);
            try (ResultSet rs = stmt.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
        }
        return Optional.empty();
    }

    @Override
    protected String[] sqlTableAttributes() {
        return new String[]{
                "id", "{primary}",
                "display_name", "VARCHAR(255) NOT NULL UNIQUE",
                "created_at", "{long} NOT NULL"
        };
    }

    protected Type map(ResultSet rs) throws SQLException {
        Type t = new Type();
        t.setId(rs.getLong("id"));
        t.setDisplayName(rs.getString("display_name"));
        t.setCreatedAt(rs.getLong("created_at"));
        return t;
    }

    @Override
    protected void statementPrepare(PreparedStatement stmt, Type type) throws SQLException {
        stmt.setString(1, type.getDisplayName());
        stmt.setLong(2, type.getCreatedAt());
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