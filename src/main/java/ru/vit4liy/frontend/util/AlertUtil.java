package ru.vit4liy.frontend.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import ru.vit4liy.base.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public final class AlertUtil {
    private AlertUtil() {}

    public static void info(String title, String message) {
        Log.out("info: %s: %s".formatted(title, message));
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void error(String title, String message) {
        Log.err("error: %s: %s".formatted(title, message));
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText("Произошла ошибка");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void errorWithStack(String title, Throwable t) {
        Log.err("error: %s: %s".formatted(title, t.getMessage()));
        for(StackTraceElement stackTraceElement : t.getStackTrace()){
            Log.err(stackTraceElement.toString());
        }

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText("Произошла ошибка");
            alert.setContentText(t.getMessage());

            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            TextArea area = new TextArea(sw.toString());
            area.setEditable(false);
            area.setWrapText(true);
            area.setMaxWidth(Double.MAX_VALUE);
            area.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(area, Priority.ALWAYS);
            GridPane.setHgrow(area, Priority.ALWAYS);

            GridPane content = new GridPane();
            content.setMaxWidth(Double.MAX_VALUE);
            content.add(area, 0, 0);
            alert.getDialogPane().setExpandableContent(content);
            alert.showAndWait();
        });
    }

    public static boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}