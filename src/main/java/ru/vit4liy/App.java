package ru.vit4liy;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.stage.Stage;
import ru.vit4liy.backend.Backend;
import ru.vit4liy.base.bus.BackendDataBus;
import ru.vit4liy.base.distribution.DistributionModule;
import ru.vit4liy.base.util.Log;
import ru.vit4liy.frontend.Frontend;
import ru.vit4liy.it72h.lib.config.Config;
import ru.vit4liy.it72h.lib.logger.log.FileTerminalLogger;
import ru.vit4liy.it72h.lib.logger.log.Logger;
import ru.vit4liy.base.util.FileUtils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends Application {
    private static Config CONFIG;
    private static Logger LOGGER;
    private static final ExecutorService POOL = Executors.newVirtualThreadPerTaskExecutor();

    private static final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static App instance;
    private ApplicationModuleLoader moduleLoader;

    public static Config getConfig() {
        return CONFIG;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static App getInstance() {
        return instance;
    }

    public ApplicationModuleLoader getModuleLoader() {
        return moduleLoader;
    }

    public static ExecutorService getPool() {
        return POOL;
    }

    public BackendDataBus bus(){
        if(moduleLoader == null) {
            Log.wrn("Module loader not loaded! Can't provide data bus.");
            return null;
        }
        return moduleLoader.getBus();
    }

    @Override
    public void start(Stage stage) throws Exception {
        instance = this;

        moduleLoader = new ApplicationModuleLoader(POOL, false, stage, getParameters(), CONFIG.getBoolean("server.localserver", true));
        moduleLoader.load();

        Log.out("Modules initializing...");

        try {
            moduleLoader.getModule(Backend.class).run();
            Thread.sleep(4000L);
            moduleLoader.initBusAndDeclarations();
            Thread.sleep(1000L);
            moduleLoader.getModule(DistributionModule.class).run();
            Thread.sleep(3000L);
            moduleLoader.getModule(Frontend.class).run();
        } catch (Exception e) {
            Log.err("Can't initialize modules! " + e.getMessage());
            throw new RuntimeException("Can't initialize modules", e);
        }

        Log.out("✅ Modules initialized!");
    }

    public static void main(String[] args) {
        System.out.printf("Initialising work directory...%n");
        FileUtils.initDirectories(true, ".ith");
        System.out.printf("Directory %s successfully initialised%n", FileUtils.getBaseDir());

        System.out.println("Initialising config.yml...");
        CONFIG = new Config(FileUtils.getConfigPath(), "config.yml");
        System.out.println("config.yml initialized!");

        try {
            CONFIG.load();
        } catch (IOException e) {
            throw new RuntimeException("Can't load configuration from: " + FileUtils.getBaseDir(), e);
        }

        LOGGER = new FileTerminalLogger(FileUtils.getBaseDir(),
                DateTimeFormatter.ofPattern("dd-MM-yyyy_HH_mm_ss").format(LocalDateTime.now())
        );
        Log.out("Logger successfully initialized!");

        launch(args);
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}