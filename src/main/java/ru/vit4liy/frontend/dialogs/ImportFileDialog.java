package ru.vit4liy.frontend.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class ImportFileDialog extends Dialog<ImportFileDialog.ImportAction> {

    public enum ImportAction {
        OVERWRITE, APPEND
    }

    public ImportFileDialog(String filePath) {
        setTitle("Импорт данных из файла");
        setHeaderText("Обнаружен файл данных ITH");
        setResizable(true);

        ButtonType overwriteBtn = new ButtonType("🗑 Импорт (затирание)", ButtonBar.ButtonData.LEFT);
        ButtonType appendBtn = new ButtonType("➕ Импорт (добавочный)", ButtonBar.ButtonData.LEFT);
        ButtonType cancelBtn = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

        getDialogPane().getButtonTypes().addAll(overwriteBtn, appendBtn, cancelBtn);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        Label infoLabel = new Label("Будет импортирован файл:");
        Label fileLabel = new Label(filePath);
        fileLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2563eb;");
        fileLabel.setWrapText(true);

        Label warningLabel = new Label(
                "• Затирание — удалит все текущие данные и заменит их данными из файла.\n" +
                        "• Добавочный — добавит новые данные поверх существующих (дубликаты будут пропущены)."
        );
        warningLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11px;");
        warningLabel.setWrapText(true);

        content.getChildren().addAll(infoLabel, fileLabel, new Separator(), warningLabel);
        getDialogPane().setContent(content);
        getDialogPane().setPrefWidth(450);

        setResultConverter(btn -> {
            if (btn == overwriteBtn) return ImportAction.OVERWRITE;
            if (btn == appendBtn) return ImportAction.APPEND;
            return null;
        });
    }
}