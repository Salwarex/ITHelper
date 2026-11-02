package ru.vit4liy.frontend.dialogs;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import ru.vit4liy.App;

import java.util.Objects;

public class SplashScreen {
    private Stage stage;
    private Scene scene;
    private VBox root;

    public SplashScreen() {
        initialize();
    }

    private void initialize() {
        stage = new Stage(StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true);

        Image windowIcon = null;
        try {
            windowIcon = new Image(Objects.requireNonNull(App.class.getResourceAsStream("/ih.png")));
            System.out.println("Icon screen loaded!");
        } catch (Exception e) {
            try {
                windowIcon = new Image(Objects.requireNonNull(App.class.getResourceAsStream("/invc.png")));
            } catch (Exception ex) {
                System.err.println("Can't load icon: " + ex.getMessage());
            }
        }
        if (windowIcon != null) {
            stage.getIcons().add(windowIcon);
        }

        root = new VBox();
        root.setStyle("-fx-background-color: #1a1a1a;");
        root.setPadding(new javafx.geometry.Insets(2));

        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(139, 92, 246)),
                new Stop(0.5, Color.rgb(59, 130, 246)),
                new Stop(1, Color.rgb(139, 92, 246))
        );

        Border border = new Border(new BorderStroke(
                gradient,
                BorderStrokeStyle.SOLID,
                new CornerRadii(8),
                new BorderWidths(3)
        ));
        root.setBorder(border);

        VBox content = new VBox(-15);
        content.setStyle("-fx-background-color: #1a1a1a; -fx-background-radius: 6;");

        content.setPadding(new javafx.geometry.Insets(15, 20, 10, 20));
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPrefSize(375, 225);

        Label titleLabel = new Label("Инструмент системного\nадминистратора");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
        titleLabel.setFont(Font.font("Proxima Nova Rg", FontWeight.NORMAL, 18));
        titleLabel.setWrapText(true);
        titleLabel.setTextAlignment(javafx.scene.text.TextAlignment.LEFT);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox bottomBox = new HBox(15);
        bottomBox.setAlignment(Pos.CENTER_LEFT);

        Image logoImage = null;
        try {
            logoImage = new Image(Objects.requireNonNull(App.class.getResourceAsStream("/icon.png")));
        } catch (Exception e) {
            try {
                logoImage = new Image(Objects.requireNonNull(App.class.getResourceAsStream("/invc.png")));
            } catch (Exception ex) {
                System.err.println("Не удалось загрузить изображение логотипа: " + ex.getMessage());
            }
        }

        if (logoImage != null) {
            ImageView logoImageView = new ImageView(logoImage);
            logoImageView.setFitHeight(42);
            logoImageView.setFitWidth(42);
            logoImageView.setPreserveRatio(true);
            bottomBox.getChildren().add(logoImageView);
        } else {
            Label logoLabel = new Label("IH");
            logoLabel.setStyle("-fx-text-fill: white; -fx-font-size: 42px; -fx-font-weight: bold;");
            logoLabel.setFont(Font.font("Proxima Nova Rg", FontWeight.BOLD, 42));
            bottomBox.getChildren().add(logoLabel);
        }

        Label subtitleLabel = new Label("ITHelper v0.16");
        subtitleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
        subtitleLabel.setFont(Font.font("Proxima Nova Rg", FontWeight.NORMAL, 18));

        bottomBox.getChildren().add(subtitleLabel);

        content.getChildren().addAll(titleLabel, spacer, bottomBox);
        root.getChildren().add(content);

        scene = new Scene(root, 381, 231, Color.TRANSPARENT);
        stage.setScene(scene);

        stage.show();
        centerStageOnScreen();
    }

    private void centerStageOnScreen() {
        var bounds = Screen.getPrimary().getVisualBounds();
        double x = bounds.getMinX() + (bounds.getWidth() - stage.getWidth()) / 2;
        double y = bounds.getMinY() + (bounds.getHeight() - stage.getHeight()) / 2;
        stage.setX(x);
        stage.setY(y);
    }

    public void show() {
        stage.show();
        centerStageOnScreen();
    }

    public void hide() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), root);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            stage.setAlwaysOnTop(false);
            stage.close();
        });
        fadeOut.play();
    }

    public void close() {
        stage.setAlwaysOnTop(false);
        stage.close();
    }
}