package ru.vit4liy.frontend;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.vit4liy.App;
import ru.vit4liy.base.util.Log;
import ru.vit4liy.frontend.dialogs.ImportFileDialog;
import ru.vit4liy.frontend.util.AlertUtil;
import ru.vit4liy.frontend.util.ApplicationFeatures;
import ru.vit4liy.frontend.util.ExportImportService;
import ru.vit4liy.modular.Module;
import ru.vit4liy.modular.ModuleLoader;
import ru.vit4liy.modular.exception.ModuleInitializeException;
import ru.vit4liy.modular.exception.ModuleShutdownException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class Frontend extends Module {
    private final static String TITLE = "ItHelper Beta, версия 0.16 — Инструмент сисадмина";

    private final ExecutorService pool;
    private MainController mainController;
    private final Stage primaryStage;
    private final Application.Parameters parameters;

    private static final int SINGLE_INSTANCE_PORT = 45678;
    private static ServerSocket serverSocket;

    public Frontend(ModuleLoader loader, ExecutorService pool, Stage primaryStage, Application.Parameters parameters) {
        super(loader);
        this.pool = pool;
        this.primaryStage = primaryStage;
        this.parameters = parameters;
    }

    @Override
    public void initialize() throws ModuleInitializeException {
        Log.out("Frontend initializing...");
        //singleton window
        try {
            serverSocket = new ServerSocket(SINGLE_INSTANCE_PORT);
            startSingleInstanceListener();
        } catch (IOException e) {
            sendFilePathToRunningInstance();
            Platform.exit();
            return;
        }

        //stage
        var fxmlLocation = App.class.getResource("/ru/vit4liy/frontend/main-view.fxml");
        if (fxmlLocation == null) {
            Log.err("Can't find main-view.fxml!");
            throw new IllegalStateException("Can't find main-view.fxml!");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 1100, 650);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Log.out("Scene loaded successfully!");

        this.mainController = fxmlLoader.getController();
        Log.out("Controller loaded successfully!");

        scene.getStylesheets().add(App.class.getResource("/ru/vit4liy/frontend/main.css").toExternalForm());

        primaryStage.setTitle(TITLE);
        primaryStage.setScene(scene);

        Image icon = new Image(Objects.requireNonNull(App.class.getResourceAsStream("/invc.png")));
        if (icon != null) {
            primaryStage.getIcons().add(icon);
        } else {
            Log.wrn("Icon file not found!");
        }

        primaryStage.show();
        Log.out("Stage has shown!");

        var args = parameters.getRaw();
        if (!args.isEmpty()) {
            String filePath = args.get(0);
            Platform.runLater(() -> showImportDialog(filePath));
        }

        Log.out("✅ Frontend initialized successfully!");
    }

    public void refreshData() {
        if (mainController != null) {
            mainController.refreshAllTabs();
        }
    }

    @Override
    public void shutdown() throws ModuleShutdownException {
        Log.out("Frontend shutting down...");
    }

    @Override
    public String moduleName() {
        return "Frontend";
    }

    @Override
    protected Set<Class<? extends Module>> dependencies() {
        return Set.of();
    }

    //create singleton window
    private void startSingleInstanceListener() {
        Log.out("Started single instance listener on port %d".formatted(SINGLE_INSTANCE_PORT));
        Thread listener = new Thread(() -> {
            while (!serverSocket.isClosed()) {
                try {
                    Socket client = serverSocket.accept();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(client.getInputStream(), "UTF-8"));
                    String filePath = reader.readLine();
                    Log.out("Start reading new ITH-file: %s".formatted(filePath));
                    client.close();

                    if (filePath != null && !filePath.isBlank()) {
                        final String path = filePath;
                        Platform.runLater(() -> {
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

    //send data from executed file to instance
    private void sendFilePathToRunningInstance() {
        Log.out("Application already launched! Sending data to main instance!");
        var args = parameters.getRaw();
        if (args.isEmpty()) return;

        try (Socket socket = new Socket("localhost", SINGLE_INSTANCE_PORT);
             PrintWriter writer = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true)) {
            writer.println(args.get(0));
        } catch (IOException e) {
            Log.err("Can't send data to main instance: " + e.getMessage());
        }
    }

    private void showImportDialog(String filePath) {
        ImportFileDialog dialog = new ImportFileDialog(filePath);
        if (primaryStage != null) {
            dialog.initOwner(primaryStage);
        }
        dialog.showAndWait().ifPresent(action -> {
            ApplicationFeatures.runAsync(() -> {
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
}
