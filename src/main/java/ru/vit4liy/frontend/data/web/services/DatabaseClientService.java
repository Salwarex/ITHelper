package ru.vit4liy.frontend.data.web.services;

import ru.vit4liy.base.bus.services.DatabaseService;
import ru.vit4liy.perm.Permissible;

import java.sql.SQLException;

public class DatabaseClientService implements DatabaseService {
    @Override
    public void removeAll(Permissible executor) throws SQLException {

    }
}
