package ru.vit4liy.base.bus.services;

import ru.vit4liy.perm.Permissible;
import ru.vit4liy.rsc.service.GenericBusService;

import java.sql.SQLException;

public interface DatabaseService extends GenericBusService {
    void removeAll(Permissible executor) throws SQLException;
}
