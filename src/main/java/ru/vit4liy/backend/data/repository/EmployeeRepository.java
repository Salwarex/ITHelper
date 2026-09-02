package ru.vit4liy.backend.data.repository;

import ru.vit4liy.backend.data.model.Employee;
import ru.vit4liy.backend.database.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeRepository extends Repository<Employee> {
    public EmployeeRepository(DatabaseManager db) { super(db); }

    public long create(Employee entity) throws SQLException {
        long now = System.currentTimeMillis();
        String sql = "INSERT INTO Employee (surname, name, thirdname, created_at, last_status_change, last_checked, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entity.getSurname());
            stmt.setString(2, entity.getName());
            stmt.setString(3, entity.getThirdname());
            stmt.setLong(4, now);
            stmt.setLong(5, now);
            stmt.setLong(6, now);
            stmt.setInt(7, Math.max(entity.getStatus(), 0));
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) { if (rs.next()) { entity.setId(rs.getLong(1)); return entity.getId(); } }
        }
        return -1;
    }

    public Optional<Employee> findById(long id) throws SQLException {
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Employee WHERE id = ?")) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
        }
        return Optional.empty();
    }

    public List<Employee> findAll() throws SQLException {
        List<Employee> list = new ArrayList<>();
        try (Connection conn = db.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM Employee")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public boolean update(Employee entity) throws SQLException {
        String sql = "UPDATE Employee SET surname = ?, name = ?, thirdname = ?, last_status_change = ?, last_checked = ?, status = ? WHERE id = ?";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getSurname());
            stmt.setString(2, entity.getName());
            stmt.setString(3, entity.getThirdname());
            stmt.setLong(4, System.currentTimeMillis());
            stmt.setLong(5, System.currentTimeMillis());
            stmt.setInt(6, entity.getStatus());
            stmt.setLong(7, entity.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(long id) throws SQLException {
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM Employee WHERE id = ?")) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Employee> findByStatus(int status) throws SQLException {
        List<Employee> list = new ArrayList<>();
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Employee WHERE status = ?")) {
            stmt.setInt(1, status);
            try (ResultSet rs = stmt.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    public List<Employee> findBySurnameContaining(String surnamePart) throws SQLException {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM Employee WHERE surname LIKE ?";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + surnamePart + "%");
            try (ResultSet rs = stmt.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    public List<Employee> findByNameContaining(String namePart) throws SQLException {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM Employee WHERE name LIKE ?";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + namePart + "%");
            try (ResultSet rs = stmt.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    private Employee map(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setId(rs.getLong("id"));
        e.setSurname(rs.getString("surname"));
        e.setName(rs.getString("name"));
        e.setThirdname(rs.getString("thirdname"));
        e.setCreatedAt(rs.getLong("created_at"));
        e.setLastStatusChange(rs.getLong("last_status_change"));
        e.setLastChecked(rs.getLong("last_checked"));
        e.setStatus(rs.getInt("status"));
        return e;
    }
}