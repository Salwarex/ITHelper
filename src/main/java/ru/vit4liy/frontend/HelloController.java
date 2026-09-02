package ru.vit4liy.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {

    @FXML
    private Label welcomeText;

    @FXML
    protected void onButtonClick() {
        welcomeText.setText("Ура! Логика сработала, и я изменил текст из FXML!");
    }

    @FXML
    public void initialize() {
        System.out.println("Контроллер создан, все ссылки на UI-элементы уже доступны!");
    }
}
