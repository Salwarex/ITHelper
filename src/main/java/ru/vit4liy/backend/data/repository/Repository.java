package ru.vit4liy.backend.data.repository;

import ru.vit4liy.backend.database.DatabaseManager;

public abstract class Repository<T> {
    protected final DatabaseManager db;

    public Repository(DatabaseManager databaseManager) {
        this.db = databaseManager;
    }
}