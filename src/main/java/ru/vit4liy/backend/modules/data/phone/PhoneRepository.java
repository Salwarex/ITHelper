package ru.vit4liy.backend.modules.data.phone;

import ru.vit4liy.backend.base.model.phone.Phone;
import ru.vit4liy.rsc.DatabaseManager;
import ru.vit4liy.rsc.repository.EntityRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PhoneRepository extends EntityRepository<Phone> {
    public PhoneRepository(DatabaseManager db) { super(Phone.class, db); }

    public Optional<Phone> findByCorpPhoneNumber(String corpNumber) throws SQLException {
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Phone WHERE corp_phone_number = ?")) {
            stmt.setString(1, corpNumber);
            try (ResultSet rs = stmt.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
        }
        return Optional.empty();
    }

    public Optional<Phone> findByPublicPhoneNumber(String publicNumber) throws SQLException {
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Phone WHERE public_phone_number = ?")) {
            stmt.setString(1, publicNumber);
            try (ResultSet rs = stmt.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
        }
        return Optional.empty();
    }

    public List<Phone> findByIp(String ip) throws SQLException {
        List<Phone> list = new ArrayList<>();
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Phone WHERE ip = ?")) {
            stmt.setString(1, ip);
            try (ResultSet rs = stmt.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    @Override
    protected String[] sqlTableAttributes() {
        return new String[]{
                "id", "{primary}",
                "corp_phone_number", "VARCHAR(4) NOT NULL UNIQUE",
                "public_phone_number", "VARCHAR(6)",
                "ip", "VARCHAR(45)",
                "last_checked", "{long} NOT NULL",
        };
    }

    protected Phone map(ResultSet rs) throws SQLException {
        Phone p = new Phone();
        p.setId(rs.getLong("id"));
        p.setCorpPhoneNumber(rs.getString("corp_phone_number"));
        p.setPublicPhoneNumber(rs.getString("public_phone_number"));
        p.setIp(rs.getString("ip"));
        p.setLastChecked(rs.getLong("last_checked"));
        return p;
    }

    @Override
    protected void statementPrepare(PreparedStatement stmt, Phone phone) throws SQLException {
        stmt.setString(1, phone.getCorpPhoneNumber());
        if (phone.getPublicPhoneNumber() == null) {
            stmt.setNull(2, java.sql.Types.VARCHAR);
        } else {
            stmt.setString(2, phone.getPublicPhoneNumber());
        }

        if (phone.getIp() == null) {
            stmt.setNull(3, java.sql.Types.VARCHAR);
        } else {
            stmt.setString(3, phone.getIp());
        }

        stmt.setLong(4, phone.getLastChecked());
    }

    @Override
    protected String[] getForeignKeysQueries() {
        return new String[0];
    }

    @Override
    protected String[] getCreateIndexesStatements() {
        return new String[]{
                "CREATE INDEX idx_phone_ip ON Phone(ip)"
        };
    }
}