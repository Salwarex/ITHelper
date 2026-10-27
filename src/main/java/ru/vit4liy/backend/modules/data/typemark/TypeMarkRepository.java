package ru.vit4liy.backend.modules.data.typemark;

import ru.vit4liy.base.model.typemark.TypeMark;
import ru.vit4liy.rsc.DatabaseManager;
import ru.vit4liy.rsc.repository.EntityRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TypeMarkRepository extends EntityRepository<TypeMark> {
    public TypeMarkRepository(DatabaseManager db) { super(TypeMark.class, db); }

    public List<TypeMark> findByTypeId(long typeId) throws SQLException {
        List<TypeMark> list = new ArrayList<>();
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM TypeMark WHERE type_id = ?")) {
            stmt.setLong(1, typeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public Optional<TypeMark> findByDisplayName(String displayName) throws SQLException {
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM TypeMark WHERE display_name = ?")) {
            stmt.setString(1, displayName);
            try (ResultSet rs = stmt.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
        }
        return Optional.empty();
    }

    @Override
    protected String[] sqlTableAttributes() {
        return new String[]{
                "id", "{primary}",
                "type_id", "{int} NOT NULL",
                "display_name", "VARCHAR(255) NOT NULL",
                "created_at", "{long} NOT NULL",
        };
    }

    protected TypeMark map(ResultSet rs) throws SQLException {
        TypeMark tm = new TypeMark();
        tm.setId(rs.getLong("id"));
        tm.setTypeId(rs.getLong("type_id"));
        tm.setDisplayName(rs.getString("display_name"));
        tm.setCreatedAt(rs.getLong("created_at"));
        return tm;
    }

    @Override
    protected void statementPrepare(PreparedStatement stmt, TypeMark typeMark) throws SQLException {
        stmt.setLong(1, typeMark.getTypeId());
        stmt.setString(2, typeMark.getDisplayName());
        stmt.setLong(3, typeMark.getCreatedAt());
    }

    @Override
    protected String[] getForeignKeysQueries() {
        return new String[]{
                "FOREIGN KEY (type_id) REFERENCES Type(id) ON DELETE CASCADE"
        };
    }

    @Override
    protected String[] getCreateIndexesStatements() {
        return new String[]{
                "CREATE INDEX idx_typemark_type ON TypeMark(type_id)"
        };
    }
}