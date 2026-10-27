package ru.vit4liy;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.vit4liy.backend.Backend;
import ru.vit4liy.base.bus.BackendDataBus;
import ru.vit4liy.base.distribution.DistributionModule;
import ru.vit4liy.base.util.Log;
import ru.vit4liy.frontend.Frontend;
import ru.vit4liy.frontend.data.web.WebBackendDataBus;
import ru.vit4liy.it72h.lib.logger.output.Color;
import ru.vit4liy.modular.ModuleLoader;

import java.util.concurrent.ExecutorService;

public class ApplicationModuleLoader extends ModuleLoader {
    private Backend backend;
    private Frontend frontend;
    private BackendDataBus bus;
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
            Log.wrn("Application started by client-only mode. Initialized by web-client data bus.");
        }
        this.frontend = new Frontend(this, pool, primaryStage, parameters);
    }

    @Override
    protected void loadStandardModules() {
        declaration(pool, primaryStage, parameters);
        if(serverMode) modules.put(Backend.class, backend);
        modules.put(Frontend.class, frontend);
    }

    public Backend getBackend() {
        return backend;
    }

    public Frontend getFrontend() {
        return frontend;
    }

    public void initBusAndDeclarations(){
        Log.out("Initializing data bus...");
        if(serverMode){
            bus = backend.getBackendModuleLoader().getDatabaseModule().getLocalDataBus();
        }else{
            bus = new WebBackendDataBus(App.getConfig().getString("server.remote.ip", "localhost"),
                    App.getConfig().getInt("server.remote.port", 2701));
        }
        this.distributionModule = new DistributionModule(this, bus);
        modules.put(DistributionModule.class, distributionModule);
        Log.out("Data bus initialized: " + (serverMode ? (Color.YELLOW + "Localserver") : (Color.PURPLE + "Remote")));
    }

    public BackendDataBus getBus() {
        return bus;
    }

    public DistributionModule getDistributionModule() {
        return distributionModule;
    }
}
