package ru.vit4liy.frontend.tabs;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
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
import ru.vit4liy.frontend.util.ApplicationFeatures;
import ru.vit4liy.frontend.util.DateUtil;
import ru.vit4liy.frontend.util.DeviceStatusUtil;
import ru.vit4liy.frontend.util.EmployeeStatusUtil;
import ru.vit4liy.perm.MissingPermissionException;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class LocationsTabController extends BaseTabController {

    @FXML private TreeView<Location> locationsTree;
    @FXML private Label lblKey;
    @FXML private Label lblDisplayName;
    @FXML private Label lblParent;
    @FXML private Label lblId;
    @FXML private Button btnAdd;
    @FXML private Button btnRefresh;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    @FXML private ListView<Device> lvDevices;
    @FXML private ListView<Employee> lvEmployees;
    @FXML private ListView<Phone> lvPhones;

    private Location currentSelectedLocation;
    private List<Location> allLocations = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        locationsTree.setCellFactory(tv -> {
            TreeCell<Location> cell = new TreeCell<>() {
                @Override
                protected void updateItem(Location item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                        setDisable(true);
                        setStyle("-fx-background-color: transparent;");
                        setContextMenu(null);
                    } else {
                        setDisable(false);
                        setStyle("");
                        setText(item.getDisplayName());
                        setContextMenu(createContextMenu(item));
                    }
                }
            };
            return cell;
        });

        locationsTree.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Node target = (Node) event.getTarget();
                TreeCell<?> cell = findTreeCell(target);
                if (cell != null && cell.getItem() == null) {
                    locationsTree.getSelectionModel().clearSelection();
                }
            }
        });

        locationsTree.getSelectionModel().selectedItemProperty().addListener((obs, old, newItem) -> {
            if (newItem != null && newItem.getValue() != null) {
                currentSelectedLocation = newItem.getValue();
                showLocationDetails(currentSelectedLocation);
            } else {
                currentSelectedLocation = null;
                clearDetails();
            }
        });

        setupListView(lvDevices,
                device -> device.getDeviceSerial() != null ? device.getDeviceSerial() : "ID: " + device.getId(),
                this::detachDevice,
                this::showDeviceDetails);

        setupListView(lvEmployees,
                emp -> String.format("%s %s %s",
                        emp.getSurname() != null ? emp.getSurname() : "",
                        emp.getName() != null ? emp.getName() : "",
                        emp.getThirdname() != null ? emp.getThirdname() : "").trim(),
                this::detachEmployee,
                this::showEmployeeDetails);

        setupListView(lvPhones,
                phone -> (phone.getCorpPhoneNumber() != null ? phone.getCorpPhoneNumber() : "") + " / " +
                        (phone.getPublicPhoneNumber() != null ? phone.getPublicPhoneNumber() : ""),
                this::detachPhone,
                this::showPhoneDetails);

        Platform.runLater(this::reloadTree);
    }

    private <T> void setupListView(ListView<T> listView,
                                   java.util.function.Function<T, String> textExtractor,
                                   Consumer<T> onDetach,
                                   Consumer<T> onDoubleClick) {
        listView.setCellFactory(lv -> {
            ListCell<T> cell = new ListCell<>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setContextMenu(null);
                    } else {
                        setText(textExtractor.apply(item));
                        setContextMenu(createDetachMenu(item, onDetach));
                    }
                }
            };
            return cell;
        });

        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                T selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    onDoubleClick.accept(selected);
                }
            }
        });
    }

    private <T> ContextMenu createDetachMenu(T item, Consumer<T> onDetach) {
        ContextMenu menu = new ContextMenu();
        MenuItem detach = new MenuItem("Отвязать");
        detach.setOnAction(e -> {
            if (AlertUtil.confirm("Отвязать", "Вы уверены, что хотите отвязать этот объект от локации?")) {
                onDetach.accept(item);
            }
        });
        MenuItem details = new MenuItem("Подробнее...");
        details.setOnAction(e -> {

        });
        menu.getItems().addAll(details, new SeparatorMenuItem(), detach);
        return menu;
    }

    private TreeCell<?> findTreeCell(Node node) {
        while (node != null) {
            if (node instanceof TreeCell) {
                return (TreeCell<?>) node;
            }
            node = node.getParent();
        }
        return null;
    }

    @Override
    protected void onFirstLoad() { }

    @Override
    protected void onTabSelected() {
        reloadTree();
    }

    @FXML
    private void onRefresh() {
        reloadTree();
    }

    @FXML
    private void onAddLocation() {
        TreeItem<Location> selected = locationsTree.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getValue() != null) {
            createChildLocation(selected.getValue());
        } else {
            createRootLocation();
        }
    }

    @FXML
    private void onEditLocation() {
        if (currentSelectedLocation == null) {
            showError("Сначала выберите локацию в дереве");
            return;
        }
        editLocation(currentSelectedLocation);
    }

    @FXML
    private void onDeleteLocation() {
        if (currentSelectedLocation == null) {
            showError("Сначала выберите локацию в дереве");
            return;
        }
        deleteLocation(currentSelectedLocation);
    }

    private void createRootLocation() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Новая локация");
        dialog.setHeaderText("Создание корневой локации");
        dialog.setContentText("Название:");
        dialog.showAndWait().ifPresent(name -> {
            if (name.isBlank()) {
                showError("Название не может быть пустым");
                return;
            }
            runAsync(() -> {
                try {
                    Location loc = new Location();
                    loc.setDisplayName(name.trim());
                    loc.setKey(generateKey(name.trim()));
                    ApplicationFeatures.bus().getLocationService().create(loc);
                    Platform.runLater(this::reloadTree);
                    Platform.runLater(() -> AlertUtil.info("Успех", "Локация создана"));
                } catch (Exception e) {
                    handleError(e);
                }
            });
        });
    }

    private void createChildLocation(Location parent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Новая дочерняя локация");
        dialog.setHeaderText("Родитель: " + parent.getDisplayName());
        dialog.setContentText("Название:");
        dialog.showAndWait().ifPresent(name -> {
            if (name.isBlank()) {
                showError("Название не может быть пустым");
                return;
            }
            runAsync(() -> {
                try {
                    Location loc = new Location();
                    loc.setDisplayName(name.trim());
                    loc.setKey(generateKey(name.trim()));
                    loc.setParentId(parent.getId());
                    ApplicationFeatures.bus().getLocationService().create(loc);
                    Platform.runLater(this::reloadTree);
                    Platform.runLater(() -> AlertUtil.info("Успех", "Дочерняя локация создана"));
                } catch (Exception e) {
                    handleError(e);
                }
            });
        });
    }

    private void editLocation(Location location) {
        TextInputDialog dialog = new TextInputDialog(location.getDisplayName());
        dialog.setTitle("Редактировать локацию");
        dialog.setHeaderText("Текущее название: " + location.getDisplayName());
        dialog.setContentText("Новое название:");
        dialog.showAndWait().ifPresent(name -> {
            if (name.isBlank()) {
                showError("Название не может быть пустым");
                return;
            }
            runAsync(() -> {
                try {
                    location.setDisplayName(name.trim());
                    ApplicationFeatures.bus().getLocationService().update(location);
                    Platform.runLater(this::reloadTree);
                    Platform.runLater(() -> AlertUtil.info("Успех", "Локация обновлена"));
                } catch (Exception e) {
                    handleError(e);
                }
            });
        });
    }

    private void deleteLocation(Location location) {
        if (!AlertUtil.confirm("Удаление локации",
                "Вы уверены, что хотите удалить локацию \"" + location.getDisplayName() + "\"?")) {
            return;
        }
        runAsync(() -> {
            try {
                ApplicationFeatures.bus().getLocationService().delete(location.getId());
                Platform.runLater(this::reloadTree);
                Platform.runLater(() -> AlertUtil.info("Успех", "Локация удалена"));
            } catch (Exception e) {
                handleError(e);
            }
        });
    }

    private ContextMenu createContextMenu(Location location) {
        ContextMenu menu = new ContextMenu();

        MenuItem attachDevice = new MenuItem("🖨 Привязать устройство");
        attachDevice.setOnAction(e -> attachDevice(location));

        MenuItem attachEmployee = new MenuItem("👥 Привязать сотрудника");
        attachEmployee.setOnAction(e -> attachEmployee(location));

        MenuItem attachPhone = new MenuItem("📞 Привязать телефон");
        attachPhone.setOnAction(e -> attachPhone(location));

        MenuItem addChild = new MenuItem("➕ Создать дочернюю локацию");
        addChild.setOnAction(e -> createChildLocation(location));

        MenuItem edit = new MenuItem("✏ Редактировать");
        edit.setOnAction(e -> editLocation(location));

        MenuItem delete = new MenuItem("🗑 Удалить");
        delete.setOnAction(e -> deleteLocation(location));

        menu.getItems().addAll(
                new SeparatorMenuItem(),
                attachDevice, attachEmployee, attachPhone,
                new SeparatorMenuItem(),
                addChild, edit, delete
        );

        return menu;
    }

    public void reloadTree() {
        runAsync(() -> {
            try {
                allLocations = ApplicationFeatures.bus().getLocationService().getAll();
                Platform.runLater(() -> buildTree(allLocations));
            } catch (Exception e) {
                handleError(e);
            }
        });
    }

    private void buildTree(List<Location> all) {
        TreeItem<Location> root = new TreeItem<>();
        root.setExpanded(true);

        for (Location loc : all) {
            if (loc.getParentId() == null || loc.getParentId() == 0) {
                TreeItem<Location> item = createItem(loc, all);
                root.getChildren().add(item);
            }
        }

        locationsTree.setRoot(root);
        locationsTree.setShowRoot(false);

        if (!root.getChildren().isEmpty()) {
            locationsTree.getSelectionModel().select(root.getChildren().get(0));
        }
    }

    private TreeItem<Location> createItem(Location parent, List<Location> all) {
        TreeItem<Location> item = new TreeItem<>(parent);
        for (Location child : all) {
            if (parent.getId() != null && parent.getId().equals(child.getParentId())) {
                item.getChildren().add(createItem(child, all));
            }
        }
        item.setExpanded(true);
        return item;
    }

    private void showLocationDetails(Location loc) {
        if (loc == null) return;
        lblId.setText(String.valueOf(loc.getId()));
        lblDisplayName.setText(loc.getDisplayName());
        lblKey.setText(loc.getKey() == null ? "—" : loc.getKey());
        lblParent.setText(buildLocationPath(loc, allLocations));

        loadRelatedEntities(loc);
    }

    private String buildLocationPath(Location loc, List<Location> allLocations) {
        List<String> path = new ArrayList<>();
        Location current = loc;
        while (current != null) {
            path.add(0, current.getDisplayName());
            if (current.getParentId() == null || current.getParentId() == 0) {
                break;
            }
            Location finalCurrent = current;
            current = allLocations.stream()
                    .filter(l -> l.getId() != null && l.getId().equals(finalCurrent.getParentId()))
                    .findFirst()
                    .orElse(null);
        }
        return String.join(" > ", path);
    }

    private void loadRelatedEntities(Location loc) {
        runAsync(() -> {
            try {
                List<DeviceLocation> deviceLocations = ApplicationFeatures.bus().getDeviceLocationService().findAllByIdB(loc.getId());
                List<Device> devices = new ArrayList<>();
                for (DeviceLocation dl : deviceLocations) {
                    ApplicationFeatures.bus().getDeviceService().getById(dl.getAId()).ifPresent(devices::add);
                }

                List<EmployeeLocation> employeeLocations = ApplicationFeatures.bus().getEmployeeLocationService().findAllByIdB(loc.getId());
                List<Employee> employees = new ArrayList<>();
                for (EmployeeLocation el : employeeLocations) {
                    ApplicationFeatures.bus().getEmployeeService().getById(el.getAId()).ifPresent(employees::add);
                }

                List<LocationPhone> locationPhones = ApplicationFeatures.bus().getLocationPhoneService().findAllByIdA(loc.getId());
                List<Phone> phones = new ArrayList<>();
                for (LocationPhone lp : locationPhones) {
                    ApplicationFeatures.bus().getPhoneService().getById(lp.getBId()).ifPresent(phones::add);
                }

                Platform.runLater(() -> {
                    lvDevices.setItems(FXCollections.observableArrayList(devices));
                    lvEmployees.setItems(FXCollections.observableArrayList(employees));
                    lvPhones.setItems(FXCollections.observableArrayList(phones));
                });
            } catch (Exception e) {
                handleError(e);
            }
        });
    }

    private void clearDetails() {
        lblId.setText("—");
        lblDisplayName.setText("—");
        lblKey.setText("—");
        lblParent.setText("—");
        lvDevices.setItems(FXCollections.observableArrayList());
        lvEmployees.setItems(FXCollections.observableArrayList());
        lvPhones.setItems(FXCollections.observableArrayList());
    }

    private void showInfoDialog(String title, GridPane content) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.setResizable(true);
        dialog.showAndWait();
    }

    private void showDeviceDetails(Device device) {
        runAsync(() -> {
            try {
                String typeName = ApplicationFeatures.bus().getTypeService().getById(device.getTypeId())
                        .map(Type::getDisplayName).orElse("ID: " + device.getTypeId());

                String markName = "—";
                if (device.getTypeMarkId() != null) {
                    markName = ApplicationFeatures.bus().getTypeMarkService().getById(device.getTypeMarkId())
                            .map(TypeMark::getDisplayName).orElse("ID: " + device.getTypeMarkId());
                }
                String osName = "—";
                if (device.getOsId() != null) {
                    osName = ApplicationFeatures.bus().getOSystemService().getById(device.getOsId())
                            .map(OSystem::getDisplayName).orElse("ID: " + device.getOsId());
                }

                final String finalTypeName = typeName;
                final String finalMarkName = markName;
                final String finalOsName = osName;

                Platform.runLater(() -> {
                    GridPane grid = new GridPane();
                    grid.setHgap(15);
                    grid.setVgap(10);

                    addDetailRow(grid, 0, "ID:", String.valueOf(device.getId()));
                    addDetailRow(grid, 1, "Серийный номер:", device.getDeviceSerial() != null ? device.getDeviceSerial() : "—");
                    addDetailRow(grid, 2, "Тип:", finalTypeName);
                    addDetailRow(grid, 3, "Марка:", finalMarkName);
                    addDetailRow(grid, 4, "ОС:", finalOsName); // <-- Добавлено
                    addDetailRow(grid, 5, "IP:", device.getIp() != null ? device.getIp() : "—"); // <-- Добавлено
                    addDetailRow(grid, 6, "Статус:", DeviceStatusUtil.getDisplayName(device.getStatus()));
                    addDetailRow(grid, 7, "Описание:", device.getDescription() != null ? device.getDescription() : "—");
                    addDetailRow(grid, 8, "Последняя проверка:", DateUtil.format(device.getLastChecked()));

                    showInfoDialog("Детали устройства", grid);
                });
            } catch (Exception e) {
                handleError(e);
            }
        });
    }

    private void showPhoneDetails(Phone phone) {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);

        addDetailRow(grid, 0, "ID:", String.valueOf(phone.getId()));
        addDetailRow(grid, 1, "Корпоративный:", phone.getCorpPhoneNumber() != null ? phone.getCorpPhoneNumber() : "—");
        addDetailRow(grid, 2, "Городской:", phone.getPublicPhoneNumber() != null ? phone.getPublicPhoneNumber() : "—");
        addDetailRow(grid, 3, "IP:", phone.getIp() != null ? phone.getIp() : "—"); // <-- Добавлено
        addDetailRow(grid, 4, "Последняя проверка:", DateUtil.format(phone.getLastChecked()));

        showInfoDialog("Детали телефона", grid);
    }

    private void showEmployeeDetails(Employee emp) {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);

        addDetailRow(grid, 0, "ID:", String.valueOf(emp.getId()));
        addDetailRow(grid, 1, "Фамилия:", emp.getSurname() != null ? emp.getSurname() : "—");
        addDetailRow(grid, 2, "Имя:", emp.getName() != null ? emp.getName() : "—");
        addDetailRow(grid, 3, "Отчество:", emp.getThirdname() != null ? emp.getThirdname() : "—");
        addDetailRow(grid, 4, "Телефон:", emp.getPhone() != null ? emp.getPhone() : "—");
        addDetailRow(grid, 5, "Статус:", EmployeeStatusUtil.getDisplayName(emp.getStatus()));
        addDetailRow(grid, 6, "Последняя проверка:", DateUtil.format(emp.getLastChecked()));

        showInfoDialog("Детали сотрудника", grid);
    }

    private void addDetailRow(GridPane grid, int row, String label, String value) {
        Label lblLabel = new Label(label);
        lblLabel.setStyle("-fx-font-weight: 500; -fx-text-fill: #6b7280;");
        Label lblValue = new Label(value != null ? value : "—");
        lblValue.setStyle("-fx-font-weight: 600; -fx-text-fill: #111827; -fx-wrap-text: true;");
        lblValue.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(lblValue, javafx.scene.layout.Priority.ALWAYS);
        grid.add(lblLabel, 0, row);
        grid.add(lblValue, 1, row);
    }

    private <T> void showAttachDialog(Location location, List<T> items, String title, String label,
                                      java.util.function.Function<T, String> textExtractor, Consumer<T> onSuccess) {
        Dialog<T> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText("Локация: " + location.getDisplayName());

        ButtonType attachButtonType = new ButtonType("Привязать", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(attachButtonType, ButtonType.CANCEL);

        ComboBox<T> comboBox = new ComboBox<>();
        comboBox.getItems().setAll(items);
        comboBox.setPromptText("Выберите элемент...");

        comboBox.setCellFactory(lv -> new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : textExtractor.apply(item));
            }
        });

        comboBox.setButtonCell(new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : textExtractor.apply(item));
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label(label), 0, 0);
        grid.add(comboBox, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == attachButtonType) {
                return comboBox.getValue();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(selectedItem -> {
            runAsync(() -> {
                try {
                    onSuccess.accept(selectedItem);
                    Platform.runLater(() -> {
                        AlertUtil.info("Успех", "Элемент успешно привязан");
                        showLocationDetails(location);
                    });
                } catch (Exception e) {
                    handleError(e);
                }
            });
        });
    }

    private void attachDevice(Location location) {
        runAsync(() -> {
            try {
                List<Device> allDevices = ApplicationFeatures.bus().getDeviceService().getAll();
                List<DeviceLocation> allDeviceLocations = ApplicationFeatures.bus().getDeviceLocationService().getAll();

                Set<Long> attachedDeviceIds = allDeviceLocations.stream()
                        .map(DeviceLocation::getAId)
                        .collect(Collectors.toSet());

                List<Device> availableDevices = allDevices.stream()
                        .filter(d -> !attachedDeviceIds.contains(d.getId()))
                        .toList();

                if (availableDevices.isEmpty()) {
                    Platform.runLater(() -> AlertUtil.info("Информация", "Все устройства уже привязаны к локациям"));
                    return;
                }

                final List<Device> finalAvailableDevices = availableDevices;
                Platform.runLater(() -> showAttachDialog(location, finalAvailableDevices, "Привязать устройство", "Устройство:",
                        device -> device.getDeviceSerial() != null ? device.getDeviceSerial() : "ID: " + device.getId(),
                        device -> {
                            DeviceLocation dl = new DeviceLocation();
                            dl.setAId(device.getId());
                            dl.setBId(location.getId());
                            dl.setCreatedAt(System.currentTimeMillis());
                            dl.setLastChecked(System.currentTimeMillis());
                            try {
                                ApplicationFeatures.bus().getDeviceLocationService().create(dl);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }));
            } catch (Exception e) {
                handleError(e);
            }
        });
    }

    private void attachEmployee(Location location) {
        runAsync(() -> {
            try {
                List<Employee> allEmployees = ApplicationFeatures.bus().getEmployeeService().getAll();
                List<EmployeeLocation> allEmployeeLocations = ApplicationFeatures.bus().getEmployeeLocationService().getAll();

                Set<Long> attachedEmployeeIds = allEmployeeLocations.stream()
                        .map(EmployeeLocation::getAId)
                        .collect(Collectors.toSet());

                List<Employee> availableEmployees = allEmployees.stream()
                        .filter(e -> !attachedEmployeeIds.contains(e.getId()))
                        .toList();

                if (availableEmployees.isEmpty()) {
                    Platform.runLater(() -> AlertUtil.info("Информация", "Все сотрудники уже привязаны к локациям"));
                    return;
                }

                final List<Employee> finalAvailableEmployees = availableEmployees;
                Platform.runLater(() -> showAttachDialog(location, finalAvailableEmployees, "Привязать сотрудника", "Сотрудник:",
                        emp -> String.format("%s %s %s",
                                emp.getSurname() != null ? emp.getSurname() : "",
                                emp.getName() != null ? emp.getName() : "",
                                emp.getThirdname() != null ? emp.getThirdname() : "").trim(),
                        emp -> {
                            EmployeeLocation el = new EmployeeLocation();
                            el.setAId(emp.getId());
                            el.setBId(location.getId());
                            el.setCreatedAt(System.currentTimeMillis());
                            el.setLastChecked(System.currentTimeMillis());
                            try {
                                ApplicationFeatures.bus().getEmployeeLocationService().create(el);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }));
            } catch (Exception e) {
                handleError(e);
            }
        });
    }

    private void attachPhone(Location location) {
        runAsync(() -> {
            try {
                List<Phone> allPhones = ApplicationFeatures.bus().getPhoneService().getAll();
                List<LocationPhone> allLocationPhones = ApplicationFeatures.bus().getLocationPhoneService().getAll();

                Set<Long> attachedPhoneIds = allLocationPhones.stream()
                        .map(LocationPhone::getBId)
                        .collect(Collectors.toSet());

                List<Phone> availablePhones = allPhones.stream()
                        .filter(p -> !attachedPhoneIds.contains(p.getId()))
                        .toList();

                if (availablePhones.isEmpty()) {
                    Platform.runLater(() -> AlertUtil.info("Информация", "Все телефоны уже привязаны к локациям"));
                    return;
                }

                final List<Phone> finalAvailablePhones = availablePhones;
                Platform.runLater(() -> showAttachDialog(location, finalAvailablePhones, "Привязать телефон", "Телефон:",
                        phone -> (phone.getCorpPhoneNumber() != null ? phone.getCorpPhoneNumber() : "") + " / " +
                                (phone.getPublicPhoneNumber() != null ? phone.getPublicPhoneNumber() : ""),
                        phone -> {
                            LocationPhone lp = new LocationPhone();
                            lp.setAId(location.getId());
                            lp.setBId(phone.getId());
                            lp.setCreatedAt(System.currentTimeMillis());
                            lp.setLastChecked(System.currentTimeMillis());
                            try {
                                ApplicationFeatures.bus().getLocationPhoneService().create(lp);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            } catch (MissingPermissionException e) {
                                throw new RuntimeException(e);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }));
            } catch (Exception e) {
                handleError(e);
            }
        });
    }

    private void detachDevice(Device device) {
        if (currentSelectedLocation == null) return;
        runAsync(() -> {
            try {
                ApplicationFeatures.bus().getDeviceLocationService().delete(
                        new ru.vit4liy.rsc.misc.MultiId<>(device.getId(), currentSelectedLocation.getId())
                );
                Platform.runLater(() -> {
                    AlertUtil.info("Успех", "Устройство отвязано");
                    showLocationDetails(currentSelectedLocation);
                });
            } catch (Exception e) {
                handleError(e);
            }
        });
    }

    private void detachEmployee(Employee employee) {
        if (currentSelectedLocation == null) return;
        runAsync(() -> {
            try {
                ApplicationFeatures.bus().getEmployeeLocationService().delete(
                        new ru.vit4liy.rsc.misc.MultiId<>(employee.getId(), currentSelectedLocation.getId())
                );
                Platform.runLater(() -> {
                    AlertUtil.info("Успех", "Сотрудник отвязан");
                    showLocationDetails(currentSelectedLocation);
                });
            } catch (Exception e) {
                handleError(e);
            }
        });
    }

    private void detachPhone(Phone phone) {
        if (currentSelectedLocation == null) return;
        runAsync(() -> {
            try {
                ApplicationFeatures.bus().getLocationPhoneService().delete(
                        new ru.vit4liy.rsc.misc.MultiId<>(currentSelectedLocation.getId(), phone.getId())
                );
                Platform.runLater(() -> {
                    AlertUtil.info("Успех", "Телефон отвязан");
                    showLocationDetails(currentSelectedLocation);
                });
            } catch (Exception e) {
                handleError(e);
            }
        });
    }
    @FXML
    private void onAttachDevice() {
        if (currentSelectedLocation == null) {
            showError("Сначала выберите локацию в дереве");
            return;
        }
        attachDevice(currentSelectedLocation);
    }

    @FXML
    private void onAttachEmployee() {
        if (currentSelectedLocation == null) {
            showError("Сначала выберите локацию в дереве");
            return;
        }
        attachEmployee(currentSelectedLocation);
    }

    @FXML
    private void onAttachPhone() {
        if (currentSelectedLocation == null) {
            showError("Сначала выберите локацию в дереве");
            return;
        }
        attachPhone(currentSelectedLocation);
    }

    private String generateKey(String name) {
        return "LOC-" + name.toUpperCase().replaceAll("[^A-ZА-Я0-9]", "_") + "-" + System.currentTimeMillis() % 10000;
    }
}