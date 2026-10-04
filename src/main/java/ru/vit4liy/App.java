package ru.vit4liy;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.vit4liy.backend.modules.CommonModuleLoader;
import ru.vit4liy.frontend.MainController;
import ru.vit4liy.frontend.dialogs.ImportFileDialog;
import ru.vit4liy.frontend.util.AlertUtil;
import ru.vit4liy.frontend.util.Backend;
import ru.vit4liy.frontend.util.ExportImportService;
import ru.vit4liy.it72h.lib.config.Config;
import ru.vit4liy.it72h.lib.logger.log.FileTerminalLogger;
import ru.vit4liy.it72h.lib.logger.log.Logger;
import ru.vit4liy.modular.ModuleLoader;
import ru.vit4liy.util.FileUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends Application {
    private static Config CONFIG;
    private static Logger LOGGER;

    private static final ExecutorService POOL = Executors.newVirtualThreadPerTaskExecutor();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final int SINGLE_INSTANCE_PORT = 45678;
    private static ServerSocket serverSocket;
    private static Stage primaryStage;
    private MainController mainController;

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
        primaryStage = stage;

        try {
            serverSocket = new ServerSocket(SINGLE_INSTANCE_PORT);
            startSingleInstanceListener();
        } catch (IOException e) {
            sendFilePathToRunningInstance();
            Platform.exit();
            return;
        }

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

        this.mainController = fxmlLoader.getController();

        scene.getStylesheets().add(App.class.getResource("/ru/vit4liy/frontend/main.css").toExternalForm());

        stage.setTitle("ItHelper Beta, версия 0.15.2 — Инструмент сисадмина");
        stage.setScene(scene);

        Image icon = new Image(Objects.requireNonNull(App.class.getResourceAsStream("/invc.png")));
        if (icon != null) {
            stage.getIcons().add(icon);
        } else {
            System.err.println("⚠️ Иконка не найдена! Проверь путь к файлу в resources.");
        }

        stage.show();

        var args = getParameters().getRaw();
        if (!args.isEmpty()) {
            String filePath = args.get(0);
            Platform.runLater(() -> showImportDialog(filePath));
        }
    }

    private void startSingleInstanceListener() {
        Thread listener = new Thread(() -> {
            while (!serverSocket.isClosed()) {
                try {
                    Socket client = serverSocket.accept();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(client.getInputStream(), "UTF-8"));
                    String filePath = reader.readLine();
                    client.close();

                    if (filePath != null && !filePath.isBlank()) {
                        final String path = filePath;
                        Platform.runLater(() -> {
                            // Поднимаем окно на передний план
                            primaryStage.setIconified(false);
                            primaryStage.toFront();
                            primaryStage.requestFocus();
                            showImportDialog(path);
                        });
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }, "SingleInstanceListener");
        listener.setDaemon(true);
        listener.start();
    }

    private void sendFilePathToRunningInstance() {
        var args = getParameters().getRaw();
        if (args.isEmpty()) return;

        try (Socket socket = new Socket("localhost", SINGLE_INSTANCE_PORT);
             PrintWriter writer = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true)) {
            writer.println(args.get(0));
        } catch (IOException e) {
            System.err.println("Не удалось связаться с уже запущенным экземпляром: " + e.getMessage());
        }
    }

    public void refreshData() {
        if (mainController != null) {
            mainController.refreshAllTabs();
        }
    }

    private void showImportDialog(String filePath) {
        ImportFileDialog dialog = new ImportFileDialog(filePath);
        if (primaryStage != null) {
            dialog.initOwner(primaryStage);
        }
        dialog.showAndWait().ifPresent(action -> {
            Backend.runAsync(() -> {
                try {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        Platform.runLater(() -> AlertUtil.error("Ошибка", "Файл не найден: " + filePath));
                        return;
                    }

                    switch (action) {
                        case OVERWRITE -> ExportImportService.importOverwrite(file);
                        case APPEND -> ExportImportService.importAppend(file);
                    }

                    Platform.runLater(() -> {
                        AlertUtil.info("Успех", "Данные успешно импортированы из файла:\n" + file.getName());

                        refreshData();
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> AlertUtil.errorWithStack("Ошибка импорта", e));
                }
            });
        });
    }

    public static void main(String[] args) {
        FileUtils.initDirectories(true, ".ith");

        CONFIG = new Config(FileUtils.getConfigPath(), "config.yml");

        try {
            CONFIG.load();
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить конфигурацию из: " + FileUtils.getConfigPath(), e);
        }

        LOGGER = new FileTerminalLogger(FileUtils.getBaseDir(),
                DateTimeFormatter.ofPattern("dd-MM-yyyy_HH_mm_ss").format(LocalDateTime.now())
        );

        launch(args);
    }
}