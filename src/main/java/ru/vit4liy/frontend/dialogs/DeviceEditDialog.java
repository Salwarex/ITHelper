package ru.vit4liy.frontend.dialogs;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ru.vit4liy.backend.base.model.device.Device;
import ru.vit4liy.backend.base.model.device.system.OSystem;
import ru.vit4liy.backend.base.model.type.Type;
import ru.vit4liy.backend.base.model.typemark.TypeMark;
import ru.vit4liy.frontend.util.AlertUtil;
import ru.vit4liy.frontend.util.Backend;
import ru.vit4liy.frontend.util.DeviceStatusUtil;

import java.util.List;

public class DeviceEditDialog extends Dialog<Device> {
    public DeviceEditDialog(Device device) {
        setTitle("Редактировать устройство");
        setHeaderText("Серийный номер: " + device.getDeviceSerial());

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField tfSerial = new TextField(device.getDeviceSerial());
        tfSerial.setPromptText("Серийный номер");

        TextField tfIp = new TextField(device.getIp() != null ? device.getIp() : "");
        tfIp.setPromptText("IP-адрес (необязательно)");

        ComboBox<Type> cbType = new ComboBox<>();
        cbType.setPromptText("Загрузка типов...");
        cbType.setDisable(true);

        ComboBox<TypeMark> cbTypeMark = new ComboBox<>();
        cbTypeMark.setPromptText("Загрузка марок...");
        cbTypeMark.setDisable(true);

        ComboBox<OSystem> cbOs = new ComboBox<>();
        cbOs.setPromptText("Загрузка ОС...");
        cbOs.setDisable(true);

        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("Работает хорошо", "Сломан", "В ремонте", "Списан");
        cbStatus.setValue(DeviceStatusUtil.getDisplayName(device.getStatus()));

        TextField tfDesc = new TextField(device.getDescription() != null ? device.getDescription() : "");
        tfDesc.setPromptText("Описание (необязательно)");

        grid.add(new Label("Серийный номер:"), 0, 0);
        grid.add(tfSerial, 1, 0);
        grid.add(new Label("IP-адрес:"), 0, 1);
        grid.add(tfIp, 1, 1);
        grid.add(new Label("Тип:"), 0, 2);
        grid.add(cbType, 1, 2);
        grid.add(new Label("Марка:"), 0, 3);
        grid.add(cbTypeMark, 1, 3);
        grid.add(new Label("ОС:"), 0, 4);
        grid.add(cbOs, 1, 4);
        grid.add(new Label("Статус:"), 0, 5);
        grid.add(cbStatus, 1, 5);
        grid.add(new Label("Описание:"), 0, 6);
        grid.add(tfDesc, 1, 6);

        getDialogPane().setContent(grid);
        Platform.runLater(tfSerial::requestFocus);

        Backend.runAsync(() -> {
            try {
                List<Type> types = Backend.db().getTypeService().getAll();
                List<TypeMark> marks = Backend.db().getTypeMarkService().getAll();
                List<OSystem> osSystems = Backend.db().getOSystemService().getAll();

                Platform.runLater(() -> {
                    // Типы
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
                    cbType.setValue(types.stream()
                            .filter(t -> t.getId().intValue() == device.getTypeId())
                            .findFirst().orElse(null));

                    // Марки
                    cbTypeMark.getItems().setAll(marks);
                    cbTypeMark.setCellFactory(lv -> new ListCell<>() {
                        @Override
                        protected void updateItem(TypeMark item, boolean empty) {
                            super.updateItem(item, empty);
                            setText(empty || item == null ? null : item.getDisplayName());
                        }
                    });
                    cbTypeMark.setButtonCell(new ListCell<>() {
                        @Override
                        protected void updateItem(TypeMark item, boolean empty) {
                            super.updateItem(item, empty);
                            setText(empty || item == null ? null : item.getDisplayName());
                        }
                    });
                    if (device.getTypeMarkId() != null) {
                        cbTypeMark.setValue(marks.stream()
                                .filter(m -> m.getId().intValue() == device.getTypeMarkId())
                                .findFirst().orElse(null));
                    }

                    // ОС
                    cbOs.getItems().setAll(osSystems);
                    cbOs.setCellFactory(lv -> new ListCell<>() {
                        @Override
                        protected void updateItem(OSystem item, boolean empty) {
                            super.updateItem(item, empty);
                            setText(empty || item == null ? null : item.getDisplayName());
                        }
                    });
                    cbOs.setButtonCell(new ListCell<>() {
                        @Override
                        protected void updateItem(OSystem item, boolean empty) {
                            super.updateItem(item, empty);
                            setText(empty || item == null ? null : item.getDisplayName());
                        }
                    });
                    if (device.getOsId() != null) {
                        cbOs.setValue(osSystems.stream()
                                .filter(os -> os.getId().equals(device.getOsId()))
                                .findFirst().orElse(null));
                    }

                    cbType.setDisable(false);
                    cbTypeMark.setDisable(false);
                    cbOs.setDisable(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> cbType.setPromptText("Ошибка загрузки"));
            }
        });

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (tfSerial.getText().isBlank()) {
                    AlertUtil.error("Ошибка", "Серийный номер обязателен!");
                    return null;
                }
                if (cbType.getValue() == null) {
                    AlertUtil.error("Ошибка", "Необходимо выбрать тип!");
                    return null;
                }

                device.setDeviceSerial(tfSerial.getText().trim());
                device.setTypeId(cbType.getValue().getId().intValue());

                TypeMark tm = cbTypeMark.getValue();
                device.setTypeMarkId(tm != null ? tm.getId().intValue() : null);

                OSystem os = cbOs.getValue();
                device.setOsId(os != null ? os.getId() : null);

                device.setIp(tfIp.getText().isBlank() ? null : tfIp.getText().trim());
                device.setStatus(DeviceStatusUtil.getDbKey(cbStatus.getValue()));
                device.setDescription(tfDesc.getText().trim());
                device.setLastChecked(System.currentTimeMillis());
                return device;
            }
            return null;
        });
    }
}