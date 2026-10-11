package ru.vit4liy.frontend.util;

import javafx.application.Platform;
import ru.vit4liy.App;
import ru.vit4liy.backend.Backend;
import ru.vit4liy.backend.modules.BackendModuleLoader;
import ru.vit4liy.base.bus.BackendDataBus;
import ru.vit4liy.base.distribution.DistributionModule;
import ru.vit4liy.base.util.Log;
import ru.vit4liy.perm.PermissionModule;

import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public final class ApplicationFeatures {
    private ApplicationFeatures() {}

    public static BackendModuleLoader loader() {
        try{
            return App.getInstance().getModuleLoader().getModule(Backend.class).getBackendModuleLoader();
        }catch (Exception e){
            Log.err("Frontend connection exception: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static BackendDataBus bus() {
        return loader().getDatabaseModule().getLocalDataBus();
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