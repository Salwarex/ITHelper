package ru.vit4liy.frontend.data.web.services;

import ru.vit4liy.base.bus.services.EmployeeLocationBusService;
import ru.vit4liy.base.model.intersections.EmployeeLocation;
import ru.vit4liy.perm.Permissible;
import ru.vit4liy.rsc.misc.MultiId;

import java.util.List;
import java.util.Optional;

public class EmployeeLocationClientService implements EmployeeLocationBusService {

    @Override
    public MultiId<Long> create(Permissible permissible, EmployeeLocation employeeLocation) throws Exception {
        return null;
    }

    @Override
    public Optional<EmployeeLocation> getById(Permissible permissible, MultiId<Long> multiId) throws Exception {
        return Optional.empty();
    }

    @Override
    public List<EmployeeLocation> findAllByIdA(Permissible permissible, long l) throws Exception {
        return List.of();
    }

    @Override
    public List<EmployeeLocation> findAllByIdB(Permissible permissible, long l) throws Exception {
        return List.of();
    }

    @Override
    public boolean update(Permissible permissible, EmployeeLocation employeeLocation) throws Exception {
        return false;
    }

    @Override
    public boolean delete(Permissible permissible, MultiId<Long> multiId) throws Exception {
        return false;
    }

    @Override
    public List<EmployeeLocation> getAll(Permissible permissible) throws Exception {
        return List.of();
    }
}
