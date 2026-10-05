package ru.vit4liy;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.vit4liy.backend.Backend;
import ru.vit4liy.backend.modules.BackendModuleLoader;
import ru.vit4liy.base.util.Log;
import ru.vit4liy.frontend.Frontend;
import ru.vit4liy.frontend.MainController;
import ru.vit4liy.frontend.dialogs.ImportFileDialog;
import ru.vit4liy.frontend.util.AlertUtil;
import ru.vit4liy.frontend.util.BackendConnection;
import ru.vit4liy.frontend.util.ExportImportService;
import ru.vit4liy.it72h.lib.config.Config;
import ru.vit4liy.it72h.lib.logger.log.FileTerminalLogger;
import ru.vit4liy.it72h.lib.logger.log.Logger;
import ru.vit4liy.it72h.lib.logger.output.Output;
import ru.vit4liy.it72h.lib.logger.output.OutputPreset;
import ru.vit4liy.modular.ModuleLoader;
import ru.vit4liy.base.util.FileUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends Application {
    private static Config CONFIG;
    private static Logger LOGGER;
    private static final ExecutorService POOL = Executors.newVirtualThreadPerTaskExecutor();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static App instance;
    private ModuleLoader moduleLoader;

    public static Config getConfig() {
        return CONFIG;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static App getInstance() {
        return instance;
    }

    public ModuleLoader getModuleLoader() {
        return moduleLoader;
    }

    public static ExecutorService getPool() {
        return POOL;
    }

    @Override
    public void start(Stage stage) throws Exception {
        instance = this;

        moduleLoader = new ApplicationModuleLoader(POOL, false, stage, getParameters());

        Log.out("Module initializing...");

        try {
            moduleLoader.getModule(Backend.class).run();
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
}