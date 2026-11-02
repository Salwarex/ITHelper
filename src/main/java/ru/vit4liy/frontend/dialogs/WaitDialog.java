package ru.vit4liy.frontend.dialogs;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class WaitDialog {
    private final Stage stage;

    public WaitDialog(String message) {
        stage = new Stage(StageStyle.UTILITY);
        stage.initModality(Modality.APPLICATION_MODAL); // Блокирует взаимодействие с основным окном
        stage.setTitle("Пожалуйста, подождите");
        stage.setResizable(false);

        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5;");

        ProgressIndicator progress = new ProgressIndicator();
        progress.setMaxSize(40, 40);

        Label label = new Label(message);
        label.setWrapText(true);
        label.setMaxWidth(250);
        label.setStyle("-fx-font-size: 13px; -fx-text-alignment: center;");
        label.setAlignment(Pos.CENTER);

        content.getChildren().addAll(progress, label);

        Scene scene = new Scene(content, 300, 150);
        // Убираем стандартный белый фон сцены, чтобы работали скругления
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        stage.setScene(scene);
    }

    public void show() {
        // Показываем строго из JavaFX потока
        javafx.application.Platform.runLater(() -> {
            System.out.println("[DEBUG] WaitDialog: Показываем окно");
            stage.show();
        });
    }

    public void close() {
        // Закрываем строго из JavaFX потока
        javafx.application.Platform.runLater(() -> {
            System.out.println("[DEBUG] WaitDialog: ЗАКРЫВАЕМ окно!");
            if (stage.isShowing()) {
                stage.close();
                System.out.println("[DEBUG] WaitDialog: Окно успешно закрыто.");
            } else {
                System.out.println("[DEBUG] WaitDialog: Окно уже было закрыто или не было показано.");
            }
        });
    }
}