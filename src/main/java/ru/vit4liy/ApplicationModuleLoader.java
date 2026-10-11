package ru.vit4liy;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.vit4liy.backend.Backend;
import ru.vit4liy.base.bus.BackendDataBus;
import ru.vit4liy.base.distribution.DistributionModule;
import ru.vit4liy.base.util.Log;
import ru.vit4liy.frontend.Frontend;
import ru.vit4liy.frontend.data.web.WebBackendDataBus;
import ru.vit4liy.modular.ModuleLoader;

import java.util.concurrent.ExecutorService;

public class ApplicationModuleLoader extends ModuleLoader {
    private Backend backend;
    private Frontend frontend;
    private DistributionModule distributionModule;
    private final Stage primaryStage;
    private final Application.Parameters parameters;
    private final boolean serverMode;

    public ApplicationModuleLoader(ExecutorService pool, boolean pluginsIncluded, Stage primaryStage, Application.Parameters parameters, boolean serverMode) {
        super(pool, pluginsIncluded);
        this.primaryStage = primaryStage;
        this.parameters = parameters;
        this.serverMode = serverMode;
    }

    private void declaration(ExecutorService pool, Stage primaryStage, Application.Parameters parameters) {
        if(serverMode){
            try{
                this.backend = new Backend(this, pool);
            }catch (Exception e){
                Log.err("Backend initialize exception! " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        else{
            Log.wrn("Application started by client-only mode");
        }
        this.frontend = new Frontend(this, pool, primaryStage, parameters);
    }

    @Override
    protected void loadStandardModules() {
        declaration(pool, primaryStage, parameters);
        modules.put(Backend.class, backend);
        modules.put(Frontend.class, frontend);
    }
}
