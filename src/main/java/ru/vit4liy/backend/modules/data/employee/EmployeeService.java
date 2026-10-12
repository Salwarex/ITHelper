package ru.vit4liy.backend.modules.data.employee;

import ru.vit4liy.base.bus.services.EmployeeBusService;
import ru.vit4liy.base.model.employee.Employee;
import ru.vit4liy.perm.MissingPermissionException;
import ru.vit4liy.perm.Permissible;
import ru.vit4liy.perm.PermissionService;
import ru.vit4liy.rsc.service.EntityService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeService extends EntityService<Employee> implements EmployeeBusService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository, PermissionService permissionService) {
        super(employeeRepository, permissionService);
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getEmployeesByStatus(Permissible executor, int status) throws SQLException {
        return employeeRepository.findByStatus(status);
    }

    public List<Employee> searchEmployeesBySurname(Permissible executor, String surnamePart) throws SQLException, MissingPermissionException {
        if (surnamePart == null || surnamePart.trim().isEmpty()) {
            return getAll(executor);
        }
        return employeeRepository.findBySurnameContaining(surnamePart.trim());
    }

    public List<Employee> searchEmployeesByName(Permissible executor, String namePart) throws SQLException, MissingPermissionException {
        if (namePart == null || namePart.trim().isEmpty()) {
            return getAll(executor);
        }
        return employeeRepository.findByNameContaining(namePart.trim());
    }

    public List<Employee> searchEmployeesByFullName(Permissible executor, String query) throws SQLException, MissingPermissionException {
        if (query == null || query.trim().isEmpty()) {
            return getAll(executor);
        }
        String q = query.trim();
        List<Employee> result = new ArrayList<>();
        result.addAll(employeeRepository.findBySurnameContaining(q));
        result.addAll(employeeRepository.findByNameContaining(q));
        return result.stream().distinct().toList();
    }
}
