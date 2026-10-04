package ru.vit4liy.backend.modules;

import ru.vit4liy.App;
import ru.vit4liy.backend.modules.data.DatabaseModule;
import ru.vit4liy.it72h.lib.config.Config;
import ru.vit4liy.it72h.lib.logger.log.Logger;
import ru.vit4liy.it72h.lib.logger.output.Output;
import ru.vit4liy.it72h.lib.logger.output.OutputPreset;
import ru.vit4liy.modular.ModuleLoader;
import ru.vit4liy.perm.PermissionModule;
import ru.vit4liy.util.FileUtils;

import java.util.concurrent.ExecutorService;

public class CommonModuleLoader extends ModuleLoader {
    private static final Config CONFIG = App.getConfig();
    private static final Logger LOGGER = App.getLogger();

    public CommonModuleLoader(ExecutorService pool, boolean pluginsIncluded){
        super(pool, pluginsIncluded);
    }

    @Override
    protected void loadStandardModules(){
        modules.put(PermissionModule.class, new PermissionModule(this, FileUtils.getBaseDir()));
        modules.put(DatabaseModule.class, new DatabaseModule(
                this,
                CONFIG.getString("database.type", "SQLite").equalsIgnoreCase("MySQL"),
                CONFIG.getString("database.host", "localhost"),
                CONFIG.getInt("database.port", 3306),
                CONFIG.getString("database.name", "database"),
                CONFIG.getString("database.user", "root"),
                CONFIG.getString("database.pass", "")
        ));
    }

    public DatabaseModule getDatabaseModule(){
        try{ return getModule(DatabaseModule.class);}catch (ClassNotFoundException e){
            LOGGER.out(Output.of(OutputPreset.ERROR, ""));
        }
        return null;
    }

    public PermissionModule getPermissionModule(){
        try{ return getModule(PermissionModule.class);}catch (ClassNotFoundException e){
            LOGGER.out(Output.of(OutputPreset.ERROR, ""));
        }
        return null;
    }
}
