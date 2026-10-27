package ru.vit4liy.backend;

import ru.vit4liy.backend.modules.BackendModuleLoader;
import ru.vit4liy.base.util.Log;
import ru.vit4liy.modular.Module;
import ru.vit4liy.modular.ModuleLoader;
import ru.vit4liy.modular.exception.ModuleInitializeException;
import ru.vit4liy.modular.exception.ModuleShutdownException;

import java.util.Set;
import java.util.concurrent.ExecutorService;

public class Backend extends Module {
    private ModuleLoader moduleLoader;
    private ExecutorService pool;
    private BackendModuleLoader backendModuleLoader;

    public Backend(ModuleLoader loader, ExecutorService pool) throws ModuleInitializeException {
        super(loader);
        this.pool = pool;
        this.backendModuleLoader = new BackendModuleLoader(pool, false);
        this.backendModuleLoader.load();
    }

    @Override
    public void initialize() throws ModuleInitializeException {
        Log.out("Backend initializing...");
        backendModuleLoader.initModules();
        Log.out("✅ Backend initialized successfully!");
    }

    @Override
    public void shutdown() throws ModuleShutdownException {
        backendModuleLoader.stopModules();
    }

    @Override
    public String moduleName() {
        return "Backend";
    }

    @Override
    protected Set<Class<? extends Module>> dependencies() {
        return Set.of();
    }

    public BackendModuleLoader getBackendModuleLoader() {
        return backendModuleLoader;
    }
}
