package ru.vit4liy.backend.data.service;

import ru.vit4liy.backend.data.model.Employee;
import ru.vit4liy.backend.data.repository.EmployeeRepository;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public long createEmployee(Employee employee) throws SQLException {
        return employeeRepository.create(employee);
    }

    public Optional<Employee> getEmployeeById(long id) throws SQLException {
        return employeeRepository.findById(id);
    }

    public List<Employee> getAllEmployees() throws SQLException {
        return employeeRepository.findAll();
    }

    public boolean updateEmployee(Employee employee) throws SQLException {
        return employeeRepository.update(employee);
    }

    public boolean deleteEmployee(long id) throws SQLException {
        return employeeRepository.delete(id);
    }

    public List<Employee> getEmployeesByStatus(int status) throws SQLException {
        return employeeRepository.findByStatus(status);
    }

    public List<Employee> searchEmployeesBySurname(String surnamePart) throws SQLException {
        if (surnamePart == null || surnamePart.trim().isEmpty()) {
            return getAllEmployees();
        }
        return employeeRepository.findBySurnameContaining(surnamePart.trim());
    }

    public List<Employee> searchEmployeesByName(String namePart) throws SQLException {
        if (namePart == null || namePart.trim().isEmpty()) {
            return getAllEmployees();
        }
        return employeeRepository.findByNameContaining(namePart.trim());
    }

    public List<Employee> searchEmployeesByFullName(String query) throws SQLException {
        if (query == null || query.trim().isEmpty()) {
            return getAllEmployees();
        }
        String q = query.trim();
        List<Employee> result = new ArrayList<>();
        result.addAll(employeeRepository.findBySurnameContaining(q));
        result.addAll(employeeRepository.findByNameContaining(q));
        // Удаление дубликатов по ID
        return result.stream().distinct().toList();
    }
}
