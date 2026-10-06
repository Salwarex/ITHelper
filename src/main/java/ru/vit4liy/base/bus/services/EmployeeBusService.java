package ru.vit4liy.base.bus.services;

import ru.vit4liy.base.model.employee.Employee;
import ru.vit4liy.rsc.service.GenericDatabaseBusService;

import java.util.List;

public interface EmployeeBusService extends GenericDatabaseBusService<Employee> {
    List<Employee> getEmployeesByStatus(int status) throws Exception;
    List<Employee> searchEmployeesBySurname(String surnamePart) throws Exception;
    List<Employee> searchEmployeesByName(String namePart) throws Exception;
    List<Employee> searchEmployeesByFullName(String query) throws Exception;
}
