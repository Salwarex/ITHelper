package ru.vit4liy.frontend.dialogs;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ru.vit4liy.App;
import ru.vit4liy.base.model.device.Device;
import ru.vit4liy.base.model.device.system.OSystem;
import ru.vit4liy.base.model.type.Type;
import ru.vit4liy.base.model.typemark.TypeMark;
import ru.vit4liy.frontend.util.AlertUtil;
import ru.vit4liy.frontend.util.ApplicationFeatures;
import ru.vit4liy.frontend.util.ClientUser;
import ru.vit4liy.frontend.util.DeviceStatusUtil;

import java.util.List;

public class DeviceCreateDialog extends Dialog<Device> {
    public DeviceCreateDialog() {
        setTitle("Новое устройство");
        setHeaderText("Заполните данные об устройстве");

        ButtonType createButtonType = new ButtonType("Создать", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField tfSerial = new TextField();
        tfSerial.setPromptText("Например, BM154312");

        TextField tfSerialInv = new TextField();
        tfSerialInv.setPromptText("Например, 400531");

        TextField tfIp = new TextField();
        tfIp.setPromptText("Например, 10.0.12.1");

        ComboBox<Type> cbType = new ComboBox<>();
        cbType.setPromptText("Например, Принтер");
        cbType.setDisable(true);

        ComboBox<TypeMark> cbTypeMark = new ComboBox<>();
        cbTypeMark.setPromptText("Например, HP");
        cbTypeMark.setDisable(true);

        ComboBox<OSystem> cbOs = new ComboBox<>();
        cbOs.setPromptText("Например, Windows 10");
        cbOs.setDisable(true);

        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("Работает хорошо", "Сломан", "В ремонте", "Списан");
        cbStatus.setValue("Работает хорошо");

        TextField tfDesc = new TextField();
        tfDesc.setPromptText("Например, описание неисправностей");

        grid.add(new Label("Серийный номер:"), 0, 0);
        grid.add(tfSerial, 1, 0);
        grid.add(new Label("Инвентарный номер:"), 0, 1);
        grid.add(tfSerialInv, 1, 1);

        grid.add(new Label("IP-адрес:"), 0, 2);
        grid.add(tfIp, 1, 2);
        grid.add(new Label("Тип:"), 0, 3);
        grid.add(cbType, 1, 3);
        grid.add(new Label("Марка:"), 0, 4);
        grid.add(cbTypeMark, 1, 4);
        grid.add(new Label("ОС:"), 0, 5);
        grid.add(cbOs, 1, 5);
        grid.add(new Label("Статус:"), 0, 6);
        grid.add(cbStatus, 1, 6);
        grid.add(new Label("Описание:"), 0, 7);
        grid.add(tfDesc, 1, 7);

        getDialogPane().setContent(grid);
        Platform.runLater(tfSerial::requestFocus);

        setupComboBoxRendering(cbType, Type::getDisplayName);
        setupComboBoxRendering(cbTypeMark, TypeMark::getDisplayName);
        setupComboBoxRendering(cbOs, OSystem::getDisplayName);

        ApplicationFeatures.runAsync(() -> {
            try {
                List<Type> types = App.getInstance().bus().getTypeService().getAll(ClientUser.get());
                List<OSystem> osSystems = App.getInstance().bus().getOSystemService().getAll(ClientUser.get());

                Platform.runLater(() -> {
                    cbType.getItems().setAll(types);
                    cbOs.getItems().setAll(osSystems);

                    cbType.setDisable(false);
                    cbOs.setDisable(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    cbType.setPromptText("Ошибка загрузки");
                    cbOs.setPromptText("Ошибка загрузки");
                });
            }
        });

        cbType.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                Platform.runLater(() -> {
                    cbTypeMark.getItems().clear();
                    cbTypeMark.setValue(null);
                    cbTypeMark.setPromptText("Сначала выберите тип");
                    cbTypeMark.setDisable(true);
                });
                return;
            }

            long typeId = ((Number) newValue.getId()).longValue();

            Platform.runLater(() -> {
                cbTypeMark.getItems().clear();
                cbTypeMark.setValue(null);
                cbTypeMark.setPromptText("Загрузка марок...");
                cbTypeMark.setDisable(true);
            });

            ApplicationFeatures.runAsync(() -> {
                try {
                    List<TypeMark> marks = App.getInstance().bus().getTypeMarkService()
                            .getTypeMarksByTypeId(ClientUser.get(), typeId);

                    Platform.runLater(() -> {
                        cbTypeMark.getItems().setAll(marks);
                        cbTypeMark.setDisable(false);
                        if (marks.isEmpty()) {
                            cbTypeMark.setPromptText("Нет доступных марок");
                        } else {
                            cbTypeMark.setPromptText("Выберите марку");
                        }
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        cbTypeMark.setPromptText("Ошибка загрузки");
                        cbTypeMark.setDisable(true);
                    });
                    e.printStackTrace();
                }
            });
        });

        setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                if (cbType.getValue() == null) {
                    AlertUtil.error("Ошибка", "Необходимо выбрать тип!");
                    return null;
                }

                Device device = new Device();
                device.setDeviceSerial(tfSerial.getText().trim());

                String inventory = tfSerialInv.getText().trim();
                device.setDeviceInventoryNumber(inventory.isBlank() ? null : inventory);

                device.setTypeId(((Number) cbType.getValue().getId()).intValue());

                TypeMark tm = cbTypeMark.getValue();
                device.setTypeMarkId(tm != null ? ((Number) tm.getId()).intValue() : null);

                OSystem os = cbOs.getValue();
                device.setOsId(os != null ? ((Number) os.getId()).longValue() : null);

                device.setIp(tfIp.getText().isBlank() ? null : tfIp.getText().trim());
                device.setStatus(DeviceStatusUtil.getDbKey(cbStatus.getValue()));
                device.setDescription(tfDesc.getText().trim());
                device.setLastChecked(System.currentTimeMillis());
                return device;
            }
            return null;
        });
    }

    private <T> void setupComboBoxRendering(ComboBox<T> comboBox, java.util.function.Function<T, String> displayNameExtractor) {
        comboBox.setCellFactory(lv -> new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : displayNameExtractor.apply(item));
            }
        });
        comboBox.setButtonCell(new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : displayNameExtractor.apply(item));
            }
        });
    }
}