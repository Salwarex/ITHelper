package ru.vit4liy.backend.modules.data.employee;

import ru.vit4liy.base.model.employee.Employee;
import ru.vit4liy.rsc.DatabaseManager;
import ru.vit4liy.rsc.repository.EntityRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository extends EntityRepository<Employee> {
    public EmployeeRepository(DatabaseManager db) { super(Employee.class, db); }

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

    @Override
    protected String[] sqlTableAttributes() {
        return new String[]{
                "id", "{primary}",
                "surname", "VARCHAR(255)",
                "name", "VARCHAR(255)",
                "thirdname", "VARCHAR(255)",
                "created_at", "{long} NOT NULL",
                "last_status_change", "{long} NOT NULL",
                "last_checked", "{long} NOT NULL",
                "status", "{int} NOT NULL DEFAULT 1",
                "phone", "VARCHAR(12)"
        };
    }

    protected Employee map(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setId(rs.getLong("id"));
        e.setSurname(rs.getString("surname"));
        e.setName(rs.getString("name"));
        e.setThirdname(rs.getString("thirdname"));
        e.setCreatedAt(rs.getLong("created_at"));
        e.setLastStatusChange(rs.getLong("last_status_change"));
        e.setLastChecked(rs.getLong("last_checked"));
        e.setStatus(rs.getInt("status"));
        e.setPhone(rs.getString("phone"));
        return e;
    }

    @Override
    protected void statementPrepare(PreparedStatement stmt, Employee employee) throws SQLException {
        stmt.setString(1, employee.getSurname());
        stmt.setString(2, employee.getName());
        if (employee.getSurname() == null) {
            stmt.setNull(1, Types.VARCHAR);
        } else {
            stmt.setString(1, employee.getSurname());
        }
        if (employee.getName() == null) {
            stmt.setNull(2, java.sql.Types.VARCHAR);
        } else {
            stmt.setString(2, employee.getName());
        }
        if (employee.getThirdname() == null) {
            stmt.setNull(3, java.sql.Types.VARCHAR);
        } else {
            stmt.setString(3, employee.getThirdname());
        }
        stmt.setLong(4, employee.getCreatedAt());
        stmt.setLong(5, employee.getLastStatusChange());
        stmt.setLong(6 ,employee.getLastChecked());
        stmt.setInt(7, employee.getStatus());
        if (employee.getPhone() == null) {
            stmt.setNull(8, java.sql.Types.VARCHAR);
        } else {
            stmt.setString(8, employee.getPhone());
        }
    }

    @Override
    protected String[] getForeignKeysQueries() {
        return new String[]{};
    }

    @Override
    protected String[] getCreateIndexesStatements() {
        return new String[]{
                "CREATE INDEX idx_employee_status ON Employee(status)",
                "CREATE INDEX idx_employee_last_checked ON Employee(last_checked)",
        };
    }


}