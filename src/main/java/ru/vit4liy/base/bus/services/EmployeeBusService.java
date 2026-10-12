package ru.vit4liy.base.bus.services;

import ru.vit4liy.base.model.employee.Employee;
import ru.vit4liy.perm.Permissible;
import ru.vit4liy.rsc.service.GenericDatabaseBusService;

import java.util.List;

public interface EmployeeBusService extends GenericDatabaseBusService<Employee> {
    List<Employee> getEmployeesByStatus(Permissible executor, int status) throws Exception;
    List<Employee> searchEmployeesBySurname(Permissible executor, String surnamePart) throws Exception;
    List<Employee> searchEmployeesByName(Permissible executor, String namePart) throws Exception;
    List<Employee> searchEmployeesByFullName(Permissible executor, String query) throws Exception;
}
