package ru.vit4liy.frontend.data.web.services;

import ru.vit4liy.base.bus.services.EmployeeBusService;
import ru.vit4liy.base.model.employee.Employee;
import ru.vit4liy.perm.Permissible;

import java.util.List;
import java.util.Optional;

public class EmployeeClientService implements EmployeeBusService {

    @Override
    public List<Employee> getEmployeesByStatus(Permissible executor, int status) throws Exception {
        return List.of();
    }

    @Override
    public List<Employee> searchEmployeesBySurname(Permissible executor, String surnamePart) throws Exception {
        return List.of();
    }

    @Override
    public List<Employee> searchEmployeesByName(Permissible executor, String namePart) throws Exception {
        return List.of();
    }

    @Override
    public List<Employee> searchEmployeesByFullName(Permissible executor, String query) throws Exception {
        return List.of();
    }

    @Override
    public long create(Permissible permissible, Employee employee) throws Exception {
        return 0;
    }

    @Override
    public Optional<Employee> getById(Permissible permissible, long l) throws Exception {
        return Optional.empty();
    }

    @Override
    public boolean update(Permissible permissible, Employee employee) throws Exception {
        return false;
    }

    @Override
    public boolean delete(Permissible permissible, long l) throws Exception {
        return false;
    }

    @Override
    public List<Employee> getAll(Permissible permissible) throws Exception {
        return List.of();
    }
}
