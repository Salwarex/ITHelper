package ru.vit4liy.frontend.tabs;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import ru.vit4liy.App;
import ru.vit4liy.base.model.device.Device;
import ru.vit4liy.base.model.device.system.OSystem;
import ru.vit4liy.base.model.employee.Employee;
import ru.vit4liy.base.model.intersections.DeviceLocation;
import ru.vit4liy.base.model.intersections.EmployeeLocation;
import ru.vit4liy.base.model.intersections.LocationPhone;
import ru.vit4liy.base.model.location.Location;
import ru.vit4liy.base.model.phone.Phone;
import ru.vit4liy.base.model.type.Type;
import ru.vit4liy.base.model.typemark.TypeMark;
import ru.vit4liy.frontend.base.BaseTabController;
import ru.vit4liy.frontend.util.AlertUtil;
import ru.vit4liy.frontend.util.ClientUser;
import ru.vit4liy.frontend.util.DeviceStatusUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Function;

public class DataEntryController extends BaseTabController {

    @FXML private HBox deviceLocationCascade;
    @FXML private TextField tfDeviceSerial;
    @FXML private TextField tfDeviceInv;
    @FXML private ComboBox<Type> cbDeviceType;
    @FXML private ComboBox<TypeMark> cbDeviceMark;
    @FXML private TextField tfDeviceIp;
    @FXML private ComboBox<OSystem> cbDeviceOs;
    @FXML private ComboBox<String> cbDeviceStatus;
    @FXML private TextField tfDeviceDescription;

    @FXML private HBox employeeLocationCascade;
    @FXML private TextField tfEmployeeSurname;
    @FXML private TextField tfEmployeeName;
    @FXML private TextField tfEmployeeThirdname;
    @FXML private TextField tfEmployeePhone;

    @FXML private HBox phoneLocationCascade;
    @FXML private TextField tfPhoneCorp;
    @FXML private TextField tfPhonePublic;
    @FXML private TextField tfPhoneIp;

    private List<ComboBox<Location>> deviceLocationBoxes = new ArrayList<>();
    private List<ComboBox<Location>> employeeLocationBoxes = new ArrayList<>();
    private List<ComboBox<Location>> phoneLocationBoxes = new ArrayList<>();

    private List<Location> allLocations = new ArrayList<>();
    private List<Type> allTypes = new ArrayList<>();
    private List<TypeMark> allMarks = new ArrayList<>();
    private List<OSystem> allOsSystems = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupDeviceStatusComboBox();
        setupDeviceTypeListener();
        Platform.runLater(this::loadInitialData);
    }

    @Override
    protected void onFirstLoad() {
        loadInitialData();
    }

    @Override
    protected void onTabSelected() {
        loadInitialData();
    }

    private void setupDeviceTypeListener() {
        cbDeviceType.valueProperty().addListener((obs, oldVal, newVal) -> updateMarkFieldState());
        cbDeviceType.getEditor().textProperty().addListener((obs, oldVal, newVal) -> updateMarkFieldState());
    }

    private void updateMarkFieldState() {
        Type selectedType = cbDeviceType.getValue();
        String typedType = cbDeviceType.getEditor().getText().trim();

        if (selectedType != null) {
            long typeId = ((Number) selectedType.getId()).longValue();
            loadMarksForExistingType(typeId);
        } else if (!typedType.isBlank()) {
            Platform.runLater(() -> {
                allMarks.clear();
                cbDeviceMark.getItems().clear();
                cbDeviceMark.getEditor().clear();
                cbDeviceMark.setValue(null);
                cbDeviceMark.setDisable(false);
                cbDeviceMark.setPromptText("Введите новую марку");
            });
        } else {
            Platform.runLater(() -> {
                allMarks.clear();
                cbDeviceMark.getItems().clear();
                cbDeviceMark.getEditor().clear();
                cbDeviceMark.setValue(null);
                cbDeviceMark.setDisable(true);
                cbDeviceMark.setPromptText("Сначала укажите тип");
            });
        }
    }

    private void loadMarksForExistingType(long typeId) {
        Platform.runLater(() -> {
            allMarks.clear();
            cbDeviceMark.getItems().clear();
            cbDeviceMark.getEditor().clear();
            cbDeviceMark.setValue(null);
            cbDeviceMark.setPromptText("Загрузка марок...");
            cbDeviceMark.setDisable(true);
        });

        runAsync(() -> {
            try {
                List<TypeMark> marks = App.getInstance().bus().getTypeMarkService()
                        .getTypeMarksByTypeId(ClientUser.get(), typeId);
                Platform.runLater(() -> {
                    allMarks.clear();
                    allMarks.addAll(marks);
                    cbDeviceMark.getItems().setAll(marks);
                    cbDeviceMark.setDisable(false);
                    cbDeviceMark.setPromptText(marks.isEmpty() ? "Нет марок (введите новую)" : "Выберите или введите марку");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    cbDeviceMark.setPromptText("Ошибка загрузки");
                    cbDeviceMark.setDisable(true);
                });
                handleError(e);
            }
        });
    }

    public void loadInitialData() {
        runAsync(() -> {
            try {
                allLocations = App.getInstance().bus().getLocationService().getAll(ClientUser.get());
                allTypes = App.getInstance().bus().getTypeService().getAll(ClientUser.get());
                allOsSystems = App.getInstance().bus().getOSystemService().getAll(ClientUser.get());

                Platform.runLater(() -> {
                    buildLocationCascade(deviceLocationCascade, deviceLocationBoxes, null);
                    buildLocationCascade(employeeLocationCascade, employeeLocationBoxes, null);
                    buildLocationCascade(phoneLocationCascade, phoneLocationBoxes, null);

                    setupEditableComboBox(cbDeviceType, allTypes, Type::getDisplayName);

                    setupEditableComboBox(cbDeviceMark, allMarks, TypeMark::getDisplayName);
                    cbDeviceMark.setDisable(true);
                    cbDeviceMark.setPromptText("Сначала укажите тип");

                    setupEditableComboBox(cbDeviceOs, allOsSystems, OSystem::getDisplayName);
                });
            } catch (Exception e) {
                handleError(e);
            }
        });
    }

    private <T> void setupEditableComboBox(ComboBox<T> comboBox, List<T> items, Function<T, String> textExtractor) {
        if (comboBox == null) return;
        comboBox.setEditable(true);
        comboBox.setItems(FXCollections.observableArrayList(items));

        comboBox.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : textExtractor.apply(item));
            }
        });
        comboBox.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : textExtractor.apply(item));
            }
        });

        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(T object) {
                return object == null ? "" : textExtractor.apply(object);
            }

            @Override
            public T fromString(String string) {
                if (string == null || string.isBlank()) return null;
                return comboBox.getItems().stream()
                        .filter(item -> textExtractor.apply(item).equalsIgnoreCase(string.trim()))
                        .findFirst()
                        .orElse(null);
            }
        });

        comboBox.setOnAction(e -> {
            T selected = comboBox.getValue();
            if (selected != null) {
                comboBox.getEditor().setText(textExtractor.apply(selected));
                comboBox.getEditor().positionCaret(comboBox.getEditor().getText().length());
            }
        });
    }

    private void setupDeviceStatusComboBox() {
        cbDeviceStatus.getItems().addAll("Работает хорошо", "Сломан", "В ремонте", "Списан");
        cbDeviceStatus.setValue("Работает хорошо");
    }

    private void buildLocationCascade(HBox container, List<ComboBox<Location>> boxes, Location initialSelection) {
        container.getChildren().clear();
        boxes.clear();
        addLocationLevel(container, boxes, 0, null, initialSelection);
    }

    private void addLocationLevel(HBox container, List<ComboBox<Location>> boxes, int level, Location parent, Location preselected) {
        if (level > 0) {
            Label sep = new Label(" > ");
            sep.setStyle("-fx-font-weight: bold; -fx-text-fill: #6b7280;");
            container.getChildren().add(sep);
        }

        ComboBox<Location> cb = new ComboBox<>();
        cb.setPromptText(level == 0 ? "Корневая локация" : "Дочерняя локация");
        cb.setEditable(true);

        cb.setPrefWidth(200);
        cb.setMaxWidth(250);

        List<Location> options = (parent == null)
                ? allLocations.stream().filter(l -> l.getParentId() == null || l.getParentId() == 0).toList()
                : allLocations.stream().filter(l -> parent.getId().equals(l.getParentId())).toList();

        cb.setItems(FXCollections.observableArrayList(options));

        cb.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Location item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getDisplayName());
            }
        });
        cb.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Location item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getDisplayName());
            }
        });

        cb.setConverter(new StringConverter<>() {
            @Override
            public String toString(Location obj) {
                return obj == null ? "" : obj.getDisplayName();
            }

            @Override
            public Location fromString(String str) {
                if (str == null || str.isBlank()) return null;
                return options.stream()
                        .filter(l -> l.getDisplayName().equalsIgnoreCase(str.trim()))
                        .findFirst()
                        .orElse(null);
            }
        });

        cb.setOnAction(e -> {
            Location selected = cb.getValue();
            Platform.runLater(() -> {
                int currentIndex = container.getChildren().indexOf(cb);
                if (currentIndex >= 0 && currentIndex < container.getChildren().size() - 1) {
                    container.getChildren().remove(currentIndex + 1, container.getChildren().size());
                }
                boxes.subList(level + 1, boxes.size()).clear();

                if (selected != null) {
                    addLocationLevel(container, boxes, level + 1, selected, null);
                }
            });
        });

        boxes.add(cb);
        container.getChildren().add(cb);

        if (preselected != null) {
            cb.setValue(preselected);
            if (preselected.getId() != null) {
                Platform.runLater(() -> addLocationLevel(container, boxes, level + 1, preselected, null));
            }
        }
    }

    private Location resolveOrCreateLocation(List<ComboBox<Location>> boxes) throws Exception {
        Location currentParent = null;
        Location lastFilledLocation = null;

        for (ComboBox<Location> cb : boxes) {
            Location selected = cb.getValue();
            String typedName = cb.getEditor().getText().trim();

            if (selected == null && typedName.isBlank()) {
                break;
            }

            Location resolved = selected;

            if (resolved == null && !typedName.isBlank()) {
                Long expectedParentId = currentParent != null ? currentParent.getId() : null;
                resolved = allLocations.stream()
                        .filter(l -> l.getDisplayName().equalsIgnoreCase(typedName) &&
                                Objects.equals(l.getParentId(), expectedParentId))
                        .findFirst()
                        .orElse(null);
            }

            if (resolved == null && !typedName.isBlank()) {
                resolved = new Location();
                resolved.setDisplayName(typedName);
                resolved.setParentId(currentParent != null ? currentParent.getId() : null);
                resolved.setKey("LOC-" + typedName.toUpperCase().replaceAll("[^A-ZА-Я0-9]", "_") + "-" + System.currentTimeMillis() % 10000);

                long newId = App.getInstance().bus().getLocationService().create(ClientUser.get(), resolved);
                resolved.setId(newId);

                allLocations.add(resolved);
            }

            if (resolved != null) {
                currentParent = resolved;
                lastFilledLocation = resolved;
            }
        }

        return lastFilledLocation;
    }


    @FXML
    private void onSubmitDevice() {
        if (!validateDeviceForm()) return;

        runAsync(() -> {
            try {
                Location finalLocation = resolveOrCreateLocation(deviceLocationBoxes);
                if (finalLocation == null) {
                    Platform.runLater(() -> showError("Укажите расположение"));
                    return;
                }

                Type type = cbDeviceType.getValue();
                if (type == null) {
                    String typeName = cbDeviceType.getEditor().getText().trim();
                    if (!typeName.isBlank()) {
                        type = allTypes.stream()
                                .filter(t -> t.getDisplayName().equalsIgnoreCase(typeName))
                                .findFirst()
                                .orElse(null);
                        if (type == null) {
                            type = new Type();
                            type.setDisplayName(typeName);
                            type.setCreatedAt(System.currentTimeMillis());
                            long typeId = App.getInstance().bus().getTypeService().create(ClientUser.get(), type);
                            type.setId(typeId);
                            allTypes.add(type);
                        }
                    }
                }

                if (type == null) {
                    Platform.runLater(() -> showError("Укажите тип устройства"));
                    return;
                }

                TypeMark mark = cbDeviceMark.getValue();
                if (mark == null) {
                    String markName = cbDeviceMark.getEditor().getText().trim();
                    if (!markName.isBlank()) {
                        Type finalType = type;
                        mark = allMarks.stream()
                                .filter(m -> m.getDisplayName().equalsIgnoreCase(markName) &&
                                        m.getTypeId() == finalType.getId().intValue())
                                .findFirst()
                                .orElse(null);
                        if (mark == null) {
                            mark = new TypeMark();
                            mark.setDisplayName(markName);
                            mark.setTypeId(finalType.getId().intValue());
                            mark.setCreatedAt(System.currentTimeMillis());
                            long markId = App.getInstance().bus().getTypeMarkService().create(ClientUser.get(), mark);
                            mark.setId(markId);
                            allMarks.add(mark);

                            final TypeMark createdMark = mark;
                            Platform.runLater(() -> cbDeviceMark.getItems().add(createdMark));
                        }
                    }
                }

                OSystem os = cbDeviceOs.getValue();
                if (os == null) {
                    String osName = cbDeviceOs.getEditor().getText().trim();
                    if (!osName.isBlank()) {
                        os = allOsSystems.stream()
                                .filter(o -> o.getDisplayName().equalsIgnoreCase(osName))
                                .findFirst()
                                .orElse(null);
                        if (os == null) {
                            os = new OSystem();
                            os.setDisplayName(osName);
                            long osId = App.getInstance().bus().getOSystemService().create(ClientUser.get(), os);
                            os.setId(osId);
                            allOsSystems.add(os);
                        }
                    }
                }

                Device device = new Device();
                device.setDeviceSerial(tfDeviceSerial.getText().isBlank() ? null : tfDeviceSerial.getText().trim());
                device.setDeviceInventoryNumber(tfDeviceInv.getText().isBlank() ? null : tfDeviceInv.getText().trim());
                device.setTypeId(type.getId().intValue());
                device.setTypeMarkId(mark != null ? mark.getId().intValue() : null);
                device.setOsId(os != null ? os.getId() : null);
                device.setIp(tfDeviceIp.getText().isBlank() ? null : tfDeviceIp.getText().trim());
                device.setStatus(DeviceStatusUtil.getDbKey(cbDeviceStatus.getValue()));
                device.setDescription(tfDeviceDescription.getText().trim());
                device.setLastChecked(System.currentTimeMillis());

                long deviceId = App.getInstance().bus().getDeviceService().create(ClientUser.get(), device);

                DeviceLocation dl = new DeviceLocation();
                dl.setAId(deviceId);
                dl.setBId(finalLocation.getId());
                dl.setCreatedAt(System.currentTimeMillis());
                dl.setLastChecked(System.currentTimeMillis());
                App.getInstance().bus().getDeviceLocationService().create(ClientUser.get(), dl);

                Platform.runLater(() -> {
                    AlertUtil.info("Успех", "Устройство успешно добавлено");
                    clearDeviceForm();
                    loadInitialData();
                });
            } catch (Exception e) {
                handleError(e);
            }
        });
    }

    private boolean validateDeviceForm() {
        boolean hasLocation = deviceLocationBoxes.stream()
                .anyMatch(cb -> !cb.getEditor().getText().trim().isBlank() || cb.getValue() != null);

        if (!hasLocation) {
            showError("Укажите расположение");
            return false;
        }
        if (cbDeviceType.getEditor().getText().trim().isBlank() && cbDeviceType.getValue() == null) {
            showError("Укажите тип устройства");
            return false;
        }
        if (cbDeviceMark.getEditor().getText().trim().isBlank() && cbDeviceMark.getValue() == null) {
            showError("Укажите марку устройства");
            return false;
        }
        if (cbDeviceStatus.getValue() == null) {
            showError("Выберите статус");
            return false;
        }
        return true;
    }

    private void clearDeviceForm() {
        buildLocationCascade(deviceLocationCascade, deviceLocationBoxes, null);
        cbDeviceType.setValue(null);
        cbDeviceType.getEditor().clear();
        cbDeviceMark.setValue(null);
        cbDeviceMark.getEditor().clear();
        cbDeviceOs.setValue(null);
        cbDeviceOs.getEditor().clear();
        tfDeviceSerial.clear();
        tfDeviceInv.clear();
        tfDeviceIp.clear();
        cbDeviceStatus.setValue("Работает хорошо");
        tfDeviceDescription.clear();
    }


    @FXML
    private void onSubmitEmployee() {
        if (!validateEmployeeForm()) return;

        runAsync(() -> {
            try {
                Location finalLocation = resolveOrCreateLocation(employeeLocationBoxes);
                if (finalLocation == null) {
                    Platform.runLater(() -> showError("Укажите расположение"));
                    return;
                }

                Employee emp = new Employee();
                emp.setSurname(tfEmployeeSurname.getText().trim());
                emp.setName(tfEmployeeName.getText().trim());
                emp.setThirdname(tfEmployeeThirdname.getText().trim());
                emp.setStatus(1);
                long now = System.currentTimeMillis();
                emp.setCreatedAt(now);
                emp.setLastStatusChange(now);
                emp.setLastChecked(now);
                emp.setPhone(tfEmployeePhone.getText().isBlank() ? null : tfEmployeePhone.getText().trim());

                long empId = App.getInstance().bus().getEmployeeService().create(ClientUser.get(), emp);

                EmployeeLocation el = new EmployeeLocation();
                el.setAId(empId);
                el.setBId(finalLocation.getId());
                el.setCreatedAt(now);
                el.setLastChecked(now);
                App.getInstance().bus().getEmployeeLocationService().create(ClientUser.get(), el);

                Platform.runLater(() -> {
                    AlertUtil.info("Успех", "Сотрудник успешно добавлен");
                    clearEmployeeForm();
                    loadInitialData();
                });
            } catch (Exception e) {
                handleError(e);
            }
        });
    }

    private boolean validateEmployeeForm() {
        boolean hasLocation = employeeLocationBoxes.stream()
                .anyMatch(cb -> !cb.getEditor().getText().trim().isBlank() || cb.getValue() != null);

        if (!hasLocation) {
            showError("Укажите расположение");
            return false;
        }
        if (tfEmployeeSurname.getText().isBlank()) {
            showError("Введите фамилию");
            return false;
        }
        if (tfEmployeeName.getText().isBlank()) {
            showError("Введите имя");
            return false;
        }
        return true;
    }

    private void clearEmployeeForm() {
        buildLocationCascade(employeeLocationCascade, employeeLocationBoxes, null);
        tfEmployeeSurname.clear();
        tfEmployeeName.clear();
        tfEmployeeThirdname.clear();
        tfEmployeePhone.clear();
    }


    @FXML
    private void onSubmitPhone() {
        if (!validatePhoneForm()) return;

        runAsync(() -> {
            try {
                Location finalLocation = resolveOrCreateLocation(phoneLocationBoxes);
                if (finalLocation == null) {
                    Platform.runLater(() -> showError("Укажите расположение"));
                    return;
                }

                Phone phone = new Phone();
                phone.setCorpPhoneNumber(tfPhoneCorp.getText().trim());
                phone.setPublicPhoneNumber(tfPhonePublic.getText().isBlank() ? null : tfPhonePublic.getText().trim());
                phone.setIp(tfPhoneIp.getText().isBlank() ? null : tfPhoneIp.getText().trim());
                phone.setLastChecked(System.currentTimeMillis());

                long phoneId = App.getInstance().bus().getPhoneService().create(ClientUser.get(), phone);

                LocationPhone lp = new LocationPhone();
                lp.setAId(finalLocation.getId());
                lp.setBId(phoneId);
                lp.setCreatedAt(System.currentTimeMillis());
                lp.setLastChecked(System.currentTimeMillis());
                App.getInstance().bus().getLocationPhoneService().create(ClientUser.get(), lp);

                Platform.runLater(() -> {
                    AlertUtil.info("Успех", "Телефон успешно добавлен");
                    clearPhoneForm();
                    loadInitialData();
                });
            } catch (Exception e) {
                handleError(e);
            }
        });
    }

    private boolean validatePhoneForm() {
        boolean hasLocation = phoneLocationBoxes.stream()
                .anyMatch(cb -> !cb.getEditor().getText().trim().isBlank() || cb.getValue() != null);

        if (!hasLocation) {
            showError("Укажите расположение");
            return false;
        }

        String corp = tfPhoneCorp.getText().trim();
        if (corp.isEmpty() || !corp.matches("\\d{4}")) {
            showError("Внутренний номер должен содержать ровно 4 цифры");
            return false;
        }

        String pub = tfPhonePublic.getText().trim();
        if (!pub.isEmpty() && !pub.matches("\\d{6}")) {
            showError("Внешний номер должен содержать ровно 6 цифр");
            return false;
        }

        String ip = tfPhoneIp.getText().trim();
        if (!ip.isEmpty() && !isValidIp(ip)) {
            showError("Неверный формат IP-адреса");
            return false;
        }

        return true;
    }

    private boolean isValidIp(String ip) {
        String ipRegex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        return ip.matches(ipRegex);
    }

    private void clearPhoneForm() {
        buildLocationCascade(phoneLocationCascade, phoneLocationBoxes, null);
        tfPhoneCorp.clear();
        tfPhonePublic.clear();
        tfPhoneIp.clear();
    }
}