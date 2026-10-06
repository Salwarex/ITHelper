package ru.vit4liy.frontend.data.web.services;

import ru.vit4liy.base.bus.services.EmployeeBusService;
import ru.vit4liy.base.model.employee.Employee;

import java.util.List;
import java.util.Optional;

public class EmployeeClientService implements EmployeeBusService {
    @Override
    public long create(Employee employee) throws Exception {
        return 0;
    }

    @Override
    public Optional<Employee> getById(long l) throws Exception {
        return Optional.empty();
    }

    @Override
    public boolean update(Employee employee) throws Exception {
        return false;
    }

    @Override
    public boolean delete(long l) throws Exception {
        return false;
    }

    @Override
    public List<Employee> getAll() throws Exception {
        return List.of();
    }

    @Override
    public List<Employee> getEmployeesByStatus(int status) throws Exception {
        return List.of();
    }

    @Override
    public List<Employee> searchEmployeesBySurname(String surnamePart) throws Exception {
        return List.of();
    }

    @Override
    public List<Employee> searchEmployeesByName(String namePart) throws Exception {
        return List.of();
    }

    @Override
    public List<Employee> searchEmployeesByFullName(String query) throws Exception {
        return List.of();
    }
}
