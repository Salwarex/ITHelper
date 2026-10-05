package ru.vit4liy;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.vit4liy.backend.Backend;
import ru.vit4liy.base.util.Log;
import ru.vit4liy.frontend.Frontend;
import ru.vit4liy.modular.ModuleLoader;
import ru.vit4liy.modular.exception.ModuleInitializeException;

import java.util.concurrent.ExecutorService;

public class ApplicationModuleLoader extends ModuleLoader {
    private Backend backend;
    private Frontend frontend;
    private final Stage primaryStage;
    private final Application.Parameters parameters;

    public ApplicationModuleLoader(ExecutorService pool, boolean pluginsIncluded, Stage primaryStage, Application.Parameters parameters) {
        super(pool, pluginsIncluded);
        this.primaryStage = primaryStage;
        this.parameters = parameters;
    }

    private void declaration(ExecutorService pool, Stage primaryStage, Application.Parameters parameters) {
        try{
            this.backend = new Backend(this, pool);
        }catch (Exception e){
            Log.err("Backend initialize exception! " + e.getMessage());
            throw new RuntimeException(e);
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
