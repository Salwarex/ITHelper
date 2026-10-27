package ru.vit4liy.frontend.base;

import javafx.fxml.Initializable;
import ru.vit4liy.frontend.util.AlertUtil;
import ru.vit4liy.frontend.util.ApplicationFeatures;


public abstract class BaseTabController implements Initializable {

    protected abstract void onFirstLoad();

    protected void onTabSelected() { }

    private boolean loaded = false;

    public final void handleTabSelected() {
        if (!loaded) {
            loaded = true;
            onFirstLoad();
        }
        onTabSelected();
    }

    protected void runAsync(Runnable task) {
        runAsync(task, null);
    }

    protected void runAsync(Runnable task, Runnable onDone) {
        ApplicationFeatures.runAsync(task, onDone, this::handleError);
    }

    protected void handleError(Exception e) {
        AlertUtil.errorWithStack("Ошибка", e);
    }

    protected void showError(String message) {
        AlertUtil.error("Ошибка", message);
    }
}