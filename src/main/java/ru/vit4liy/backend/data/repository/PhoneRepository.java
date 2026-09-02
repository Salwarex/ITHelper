package ru.vit4liy.backend.data.repository;

import ru.vit4liy.backend.data.model.Phone;
import ru.vit4liy.backend.database.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PhoneRepository extends Repository<Phone> {
    public PhoneRepository(DatabaseManager db) { super(db); }

    public long create(Phone entity) throws SQLException {
        String sql = "INSERT INTO Phone (corp_phone_number, public_phone_number, last_checked) VALUES (?, ?, ?)";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entity.getCorpPhoneNumber());
            stmt.setString(2, entity.getPublicPhoneNumber());
            stmt.setLong(3, System.currentTimeMillis());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) { if (rs.next()) { entity.setId(rs.getLong(1)); return entity.getId(); } }
        }
        return -1;
    }

    public Optional<Phone> findById(long id) throws SQLException {
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Phone WHERE id = ?")) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
        }
        return Optional.empty();
    }

    public List<Phone> findAll() throws SQLException {
        List<Phone> list = new ArrayList<>();
        try (Connection conn = db.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM Phone")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public boolean update(Phone entity) throws SQLException {
        String sql = "UPDATE Phone SET corp_phone_number = ?, public_phone_number = ?, last_checked = ? WHERE id = ?";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getCorpPhoneNumber());
            stmt.setString(2, entity.getPublicPhoneNumber());
            stmt.setLong(3, System.currentTimeMillis());
            stmt.setLong(4, entity.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(long id) throws SQLException {
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM Phone WHERE id = ?")) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

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

    private Phone map(ResultSet rs) throws SQLException {
        Phone p = new Phone();
        p.setId(rs.getLong("id"));
        p.setCorpPhoneNumber(rs.getString("corp_phone_number"));
        p.setPublicPhoneNumber(rs.getString("public_phone_number"));
        p.setLastChecked(rs.getLong("last_checked"));
        return p;
    }
}