package ru.vit4liy.frontend.util;

import javafx.application.Platform;
import ru.vit4liy.App;
import ru.vit4liy.backend.modules.BackendModuleLoader;
import ru.vit4liy.backend.modules.data.DatabaseModule;
import ru.vit4liy.perm.PermissionModule;

import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public final class BackendConnection {
    private BackendConnection() {}

    public static BackendModuleLoader loader() {
        return (BackendModuleLoader) App.getInstance().getModuleLoader();
    }

    public static DatabaseModule db() {
        return loader().getDatabaseModule();
    }

    public static PermissionModule perm() {
        return loader().getPermissionModule();
    }

    public static ExecutorService pool() {
        return App.getPool();
    }

    public static void runAsync(Runnable task, Consumer<Exception> onError) {
        pool().submit(() -> {
            try {
                task.run();
            } catch (Exception e) {
                Platform.runLater(() -> onError.accept(e));
            }
        });
    }

    public static void runAsync(Runnable task, Runnable onDone, Consumer<Exception> onError) {
        pool().submit(() -> {
            try {
                task.run();
                if (onDone != null) Platform.runLater(onDone);
            } catch (Exception e) {
                Platform.runLater(() -> onError.accept(e));
            }
        });
    }

    public static void runAsync(Runnable task) {
        runAsync(task, e -> AlertUtil.errorWithStack("Ошибка", e));
    }
}