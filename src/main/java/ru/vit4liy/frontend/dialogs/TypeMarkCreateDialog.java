package ru.vit4liy.frontend.dialogs;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ru.vit4liy.App;
import ru.vit4liy.base.model.type.Type;
import ru.vit4liy.frontend.util.AlertUtil;
import ru.vit4liy.frontend.util.ApplicationFeatures;
import ru.vit4liy.base.model.typemark.TypeMark;
import ru.vit4liy.frontend.util.ClientUser;

import java.util.List;

public class TypeMarkCreateDialog extends Dialog<TypeMark> {
    public TypeMarkCreateDialog() {
        setTitle("Новая марка устройства");
        setHeaderText("Укажите название и тип");

        ButtonType createButtonType = new ButtonType("Создать", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField tfName = new TextField();
        tfName.setPromptText("Название марки");

        ComboBox<Type> cbType = new ComboBox<>();
        cbType.setPromptText("Загрузка типов...");
        cbType.setDisable(true);

        grid.add(new Label("Название:"), 0, 0);
        grid.add(tfName, 1, 0);
        grid.add(new Label("Тип:"), 0, 1);
        grid.add(cbType, 1, 1);

        getDialogPane().setContent(grid);
        Platform.runLater(tfName::requestFocus);

        ApplicationFeatures.runAsync(() -> {
            try {
                List<Type> types = App.getInstance().bus().getTypeService().getAll(ClientUser.get());
                Platform.runLater(() -> {
                    cbType.getItems().setAll(types);
                    cbType.setCellFactory(lv -> new ListCell<>() {
                        @Override
                        protected void updateItem(Type item, boolean empty) {
                            super.updateItem(item, empty);
                            setText(empty || item == null ? null : item.getDisplayName());
                        }
                    });
                    cbType.setButtonCell(new ListCell<>() {
                        @Override
                        protected void updateItem(Type item, boolean empty) {
                            super.updateItem(item, empty);
                            setText(empty || item == null ? null : item.getDisplayName());
                        }
                    });
                    cbType.setDisable(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> cbType.setPromptText("Ошибка загрузки"));
            }
        });

        setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                if (tfName.getText().isBlank()) {
                    AlertUtil.error("Ошибка", "Название марки обязательно!");
                    return null;
                }
                if (cbType.getValue() == null) {
                    AlertUtil.error("Ошибка", "Необходимо выбрать тип!");
                    return null;
                }

                TypeMark mark = new TypeMark();
                mark.setDisplayName(tfName.getText().trim());
                mark.setTypeId(cbType.getValue().getId());
                mark.setCreatedAt(System.currentTimeMillis());
                return mark;
            }
            return null;
        });
    }
}