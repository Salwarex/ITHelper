package ru.vit4liy;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.vit4liy.backend.modules.CommonModuleLoader;
import ru.vit4liy.it72h.lib.config.Config;
import ru.vit4liy.it72h.lib.logger.log.FileTerminalLogger;
import ru.vit4liy.it72h.lib.logger.log.Logger;
import ru.vit4liy.modular.ModuleLoader;
import ru.vit4liy.util.FileUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class App extends Application {
    private static final Config CONFIG = new Config("config.yml");
    private static final Logger LOGGER = new FileTerminalLogger(
            DateTimeFormatter.ofPattern("dd-MM-yyyy_HH_mm_ss").format(LocalDateTime.now())
    );
    private static final ExecutorService POOL = Executors.newVirtualThreadPerTaskExecutor();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Config getConfig() {
        return CONFIG;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    private static App instance;

    private ModuleLoader moduleLoader;

    @Override
    public void start(Stage stage) throws Exception {
        CONFIG.saveDefaultConfig();
        instance = this;
        moduleLoader = new CommonModuleLoader(POOL, false);

        System.out.println("⏳ Синхронная инициализация модулей...");

        try {
            moduleLoader.getModule(ru.vit4liy.perm.PermissionModule.class).run();
            moduleLoader.getModule(ru.vit4liy.backend.modules.data.DatabaseModule.class).run();
        } catch (Exception e) {
            throw new RuntimeException("Критическая ошибка инициализации модулей", e);
        }

        System.out.println("✅ Модули успешно инициализированы!");

        var fxmlLocation = App.class.getResource("/ru/vit4liy/frontend/main-view.fxml");
        if (fxmlLocation == null) {
            throw new IllegalStateException("Не удалось найти main-view.fxml! Проверьте путь в папке resources.");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        Scene scene = new Scene(fxmlLoader.load(), 1100, 650);

        scene.getStylesheets().add(App.class.getResource("/ru/vit4liy/frontend/main.css").toExternalForm());

        stage.setTitle("InventoryHelper — Инвентаризация");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        FileUtils.initializeAppWorkingDirectory(".ith");
        FileUtils.createSymlinks(".ith");
        launch(args);
    }

    public static App getInstance() {
        return instance;
    }

    public ModuleLoader getModuleLoader() {
        return moduleLoader;
    }

    public static ExecutorService getPool(){
        return POOL;
    }
}