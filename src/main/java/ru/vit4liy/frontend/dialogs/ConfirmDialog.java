package ru.vit4liy.frontend.dialogs;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;

import java.util.Optional;

public class ConfirmDialog {

    public static boolean show(String title, String message, String confirmText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initStyle(StageStyle.UTILITY);

        ButtonType confirmButton = new ButtonType(confirmText, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        Button cancelBtn = (Button) alert.getDialogPane().lookupButton(cancelButton);
        if (cancelBtn != null) {
            cancelBtn.setDefaultButton(true);
            cancelBtn.requestFocus();
        }

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == confirmButton;
    }
}