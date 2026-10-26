package ru.vit4liy.frontend.tabs;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ru.vit4liy.App;
import ru.vit4liy.base.bus.BackendDataBus;
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
import ru.vit4liy.frontend.util.*;
import ru.vit4liy.perm.MissingPermissionException;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class LocationsTabController extends BaseTabController {

    @FXML private TreeView<Location> locationsTree;
    @FXML private Label lblKey;
    @FXML private Label lblDisplayName;
    @FXML private Label lblNickname;
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

                        String displayText = item.getDisplayName();
                        if (item.getNickname() != null && !item.getNickname().isBlank()) {
                            displayText += " - " + item.getNickname();
                        }
                        setText(displayText);

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

                if (cell == null || cell.getItem() == null) {
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

        BackendDataBus bus = App.getInstance().bus();

        setupListView(lvDevices,
                this::formatDeviceForDisplay,
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

        MenuItem details = new MenuItem("Подробнее...");
        details.setOnAction(e -> {
            if (item instanceof Device) showDeviceDetails((Device) item);
            else if (item instanceof Employee) showEmployeeDetails((Employee) item);
            else if (item instanceof Phone) showPhoneDetails((Phone) item);
        });

        MenuItem transfer = new MenuItem("Перенести");
        transfer.setOnAction(e -> showTransferDialog(item));

        MenuItem detach = new MenuItem("Отвязать");
        detach.setOnAction(e -> {
            if (AlertUtil.confirm("Отвязать", "Вы уверены, что хотите отвязать этот объект от локации?")) {
                onDetach.accept(item);
            }
        });

        menu.getItems().addAll(details, transfer, new SeparatorMenuItem(), detach);
        return menu;
    }

    private <T> void showTransferDialog(T item) {
        if (currentSelectedLocation == null) return;

        Dialog<Location> dialog = new Dialog<>();
        dialog.setTitle("Перенести объект");
        dialog.setHeaderText("Выберите новую локацию для переноса");

        ButtonType transferButtonType = new ButtonType("Перенести", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(transferButtonType, ButtonType.CANCEL);

        ComboBox<Location> locationCombo = new ComboBox<>();

        List<Location> otherLocations = allLocations.stream()
                .filter(loc -> !loc.getId().equals(currentSelectedLocation.getId()))
                .collect(Collectors.toList());

        locationCombo.getItems().setAll(otherLocations);
        locationCombo.setPromptText("Выберите локацию...");

        locationCombo.setCellFactory(lv -> new ListCell<Location>() {
            @Override
            protected void updateItem(Location loc, boolean empty) {
                super.updateItem(loc, empty);
                setText((empty || loc == null) ? null : buildLocationPath(loc, allLocations));
            }
        });
        locationCombo.setButtonCell(new ListCell<Location>() {
            @Override
            protected void updateItem(Location loc, boolean empty) {
                super.updateItem(loc, empty);
                setText((empty || loc == null) ? null : buildLocationPath(loc, allLocations));
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Новая локация:"), 0, 0);
        grid.add(locationCombo, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == transferButtonType) {
                return locationCombo.getValue();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newLocation -> {
            if (newLocation == null) return;

            runAsync(() -> {
                try {
                    if (item instanceof Device) {
                        Device dev = (Device) item;
                        App.getInstance().bus().getDeviceLocationService().delete(ClientUser.get(),
                                new ru.vit4liy.rsc.misc.MultiId<>(dev.getId(), currentSelectedLocation.getId()));
                        DeviceLocation dl = new DeviceLocation();
                        dl.setAId(dev.getId());
                        dl.setBId(newLocation.getId());
                        dl.setCreatedAt(System.currentTimeMillis());
                        dl.setLastChecked(System.currentTimeMillis());
                        App.getInstance().bus().getDeviceLocationService().create(ClientUser.get(), dl);

                    } else if (item instanceof Employee) {
                        Employee emp = (Employee) item;
                        App.getInstance().bus().getEmployeeLocationService().delete(ClientUser.get(),
                                new ru.vit4liy.rsc.misc.MultiId<>(emp.getId(), currentSelectedLocation.getId()));
                        EmployeeLocation el = new EmployeeLocation();
                        el.setAId(emp.getId());
                        el.setBId(newLocation.getId());
                        el.setCreatedAt(System.currentTimeMillis());
                        el.setLastChecked(System.currentTimeMillis());
                        App.getInstance().bus().getEmployeeLocationService().create(ClientUser.get(), el);

                    } else if (item instanceof Phone) {
                        Phone phone = (Phone) item;
                        App.getInstance().bus().getLocationPhoneService().delete(ClientUser.get(),
                                new ru.vit4liy.rsc.misc.MultiId<>(currentSelectedLocation.getId(), phone.getId()));
                        LocationPhone lp = new LocationPhone();
                        lp.setAId(newLocation.getId());
                        lp.setBId(phone.getId());
                        lp.setCreatedAt(System.currentTimeMillis());
                        lp.setLastChecked(System.currentTimeMillis());
                        App.getInstance().bus().getLocationPhoneService().create(ClientUser.get(), lp);
                    }

                    Platform.runLater(() -> {
                        AlertUtil.info("Успех", "Объект успешно перенесен");
                        showLocationDetails(currentSelectedLocation);
                    });
                } catch (Exception e) {
                    handleError(e);
                }
            });
        });
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
            showLocationCreationDialog(selected.getValue());
        } else {
            showLocationCreationDialog(null);
        }
    }

    private void showLocationCreationDialog(Location parent) {
        Dialog<Location> dialog = new Dialog<>();
        dialog.setTitle("Создать расположение");
        dialog.setHeaderText(parent == null ? "Корневое расположение" : "Родитель: " + parent.getDisplayName());

        ButtonType createButtonType = new ButtonType("Создать", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, cancelButtonType);

        GridPane grid = new GridPane();
        int textFieldWidth = 300;
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, textFieldWidth, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText(parent == null ? "Например, \"Филиал #3\"" : "Например, \"Кабинет 120\"...");
        nameField.setMinWidth(textFieldWidth);

        TextField nicknameField = new TextField();
        nicknameField.setPromptText(parent == null ? "Например, \"Московское отделение\"" : "Например, \"Кабинет руководителя\"");
        nicknameField.setMinWidth(textFieldWidth);

        grid.add(new Label("Наименование"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Псевдоним"), 0, 1);
        grid.add(nicknameField, 1, 1);

        GridPane.setHgrow(nameField, javafx.scene.layout.Priority.ALWAYS);
        GridPane.setHgrow(nicknameField, javafx.scene.layout.Priority.ALWAYS);

        dialog.getDialogPane().setContent(grid);

        javafx.scene.Node createButton = dialog.getDialogPane().lookupButton(createButtonType);
        createButton.setDisable(true);

        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            createButton.setDisable(newVal == null || newVal.trim().isEmpty());
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                Location loc = new Location();
                loc.setDisplayName(nameField.getText().trim());
                String nick = nicknameField.getText().trim();
                loc.setNickname(nick.isBlank() ? null : nick);
                loc.setKey(generateKey(nameField.getText().trim()));
                if (parent != null) {
                    loc.setParentId(parent.getId());
                }
                return loc;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(location -> {
            runAsync(() -> {
                try {
                    App.getInstance().bus().getLocationService().create(ClientUser.get(), location);
                    Platform.runLater(this::reloadTree);
                    Platform.runLater(() -> AlertUtil.info("Успех", "Локация создана"));
                } catch (Exception e) {
                    handleError(e);
                }
            });
        });
    }

    private void showBulkLocationCreationDialog(Location parent) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Множественное создание расположений");
        dialog.setHeaderText(parent == null ? "Корневое расположение" : "Родитель: " + parent.getDisplayName());

        ButtonType createButtonType = new ButtonType("Создать", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, cancelButtonType);

        GridPane grid = new GridPane();
        int textFieldWidth = 300;
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, textFieldWidth, 10, 10));

        TextField fromField = new TextField("1");
        fromField.setPromptText("Например, 1");
        fromField.setMinWidth(textFieldWidth);

        TextField toField = new TextField("10");
        toField.setPromptText("Например, 10");
        toField.setMinWidth(textFieldWidth);

        TextField formatField = new TextField("Кабинет %03d");
        formatField.setPromptText("Например, Кабинет %03d (результат: Кабинет 014) или %03d (результат: 014)");
        formatField.setMinWidth(textFieldWidth);

        grid.add(new Label("От:"), 0, 0);
        grid.add(fromField, 1, 0);
        grid.add(new Label("До:"), 0, 1);
        grid.add(toField, 1, 1);
        grid.add(new Label("Формат:"), 0, 2);
        grid.add(formatField, 1, 2);

        GridPane.setHgrow(fromField, javafx.scene.layout.Priority.ALWAYS);
        GridPane.setHgrow(toField, javafx.scene.layout.Priority.ALWAYS);
        GridPane.setHgrow(formatField, javafx.scene.layout.Priority.ALWAYS);

        dialog.getDialogPane().setContent(grid);

        javafx.scene.Node createButton = dialog.getDialogPane().lookupButton(createButtonType);
        createButton.setDisable(true);

        Runnable validate = () -> {
            try {
                int from = Integer.parseInt(fromField.getText().trim());
                int to = Integer.parseInt(toField.getText().trim());
                String format = formatField.getText().trim();

                boolean valid = from <= to && !format.isBlank();
                if (valid) {
                    String.format(format, from);
                }

                createButton.setDisable(!valid);
            } catch (Exception e) {
                createButton.setDisable(true);
            }
        };

        fromField.textProperty().addListener((obs, oldVal, newVal) -> validate.run());
        toField.textProperty().addListener((obs, oldVal, newVal) -> validate.run());
        formatField.textProperty().addListener((obs, oldVal, newVal) -> validate.run());

        validate.run();

        dialog.setResultConverter(dialogButton -> dialogButton);

        dialog.showAndWait().ifPresent(result -> {
            if (result == createButtonType) {
                int from = Integer.parseInt(fromField.getText().trim());
                int to = Integer.parseInt(toField.getText().trim());
                String format = formatField.getText().trim();
                Location finalParent = parent;

                runAsync(() -> {
                    try {
                        int count = 0;
                        for (int i = from; i <= to; i++) {
                            String displayName = String.format(format, i);
                            Location loc = new Location();
                            loc.setDisplayName(displayName);
                            loc.setKey(generateKey(displayName) + "-" + i);
                            if (finalParent != null) {
                                loc.setParentId(finalParent.getId());
                            }
                            App.getInstance().bus().getLocationService().create(ClientUser.get(), loc);
                            count++;
                        }

                        int finalCount = count;
                        Platform.runLater(() -> {
                            reloadTree();
                            AlertUtil.info("Успех", "Успешно создано " + finalCount + " локаций");
                        });
                    } catch (Exception e) {
                        handleError(e);
                    }
                });
            }
        });
    }

    @FXML
    private void onEditLocation() {
        if (currentSelectedLocation == null) {
            showError("Сначала выберите локацию в дереве");
            return;
        }
        editLocation(currentSelectedLocation);
    }

    private void editLocation(Location location) {
        Dialog<Location> dialog = new Dialog<>();
        dialog.setTitle("Редактировать расположение");
        dialog.setHeaderText("Изменение данных: " + location.getDisplayName());

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        int textFieldWidth = 300;

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, textFieldWidth, 10, 10));

        TextField nameField = new TextField(location.getDisplayName());
        nameField.setPromptText("Например, \"Кабинет 120\"...");
        nameField.setMinWidth(textFieldWidth);

        TextField nicknameField = new TextField(location.getNickname() != null ? location.getNickname() : "");
        nicknameField.setPromptText("Например, \"Кабинет руководителя\"");
        nicknameField.setMinWidth(textFieldWidth);

        GridPane.setHgrow(nameField, javafx.scene.layout.Priority.ALWAYS);
        GridPane.setHgrow(nicknameField, javafx.scene.layout.Priority.ALWAYS);

        ComboBox<Location> parentCombo = new ComboBox<>();

        List<Location> possibleParents = new ArrayList<>();
        possibleParents.add(null);

        for (Location loc : allLocations) {
            if (loc.getId().equals(location.getId())) continue;
            if (!isDescendant(location, loc)) {
                possibleParents.add(loc);
            }
        }

        parentCombo.getItems().setAll(possibleParents);

        parentCombo.setConverter(new javafx.util.StringConverter<Location>() {
            @Override
            public String toString(Location loc) {
                if (loc == null) {
                    return "! Нет";
                }
                return buildLocationPath(loc, allLocations);
            }

            @Override
            public Location fromString(String string) {
                return null;
            }
        });

        parentCombo.setCellFactory(lv -> new javafx.scene.control.ListCell<Location>() {
            @Override
            protected void updateItem(Location item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("! Нет");
                } else {
                    setText(buildLocationPath(item, allLocations));
                }
            }
        });

        parentCombo.setButtonCell(new javafx.scene.control.ListCell<Location>() {
            @Override
            protected void updateItem(Location item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("! Нет");
                } else {
                    setText(buildLocationPath(item, allLocations));
                }
            }
        });

        Location currentParent = location.getParentId() == null ? null :
                allLocations.stream().filter(l -> l.getId().equals(location.getParentId())).findFirst().orElse(null);

        parentCombo.setValue(currentParent);

        grid.add(new Label("Название:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Псевдоним:"), 0, 1);
        grid.add(nicknameField, 1, 1);
        grid.add(new Label("Родитель:"), 0, 2);
        grid.add(parentCombo, 1, 2);

        dialog.getDialogPane().setContent(grid);

        javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(nameField.getText() == null || nameField.getText().trim().isEmpty());

        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            saveButton.setDisable(newVal == null || newVal.trim().isEmpty());
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Location updated = new Location();
                updated.setId(location.getId());
                updated.setKey(location.getKey());
                updated.setDisplayName(nameField.getText().trim());

                String nick = nicknameField.getText().trim();
                updated.setNickname(nick.isBlank() ? null : nick);

                Location selectedParent = parentCombo.getValue();
                updated.setParentId(selectedParent == null ? null : selectedParent.getId());

                return updated;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedLocation -> {
            runAsync(() -> {
                try {
                    App.getInstance().bus().getLocationService().update(ClientUser.get(), updatedLocation);
                    Platform.runLater(this::reloadTree);
                    Platform.runLater(() -> AlertUtil.info("Успех", "Локация успешно обновлена"));
                } catch (Exception e) {
                    handleError(e);
                }
            });
        });
    }

    private boolean isDescendant(Location targetLocation, Location potentialAncestor) {
        Long currentParentId = targetLocation.getParentId();
        while (currentParentId != null && currentParentId != 0) {
            if (currentParentId.equals(potentialAncestor.getId())) {
                return true;
            }
            Long finalCurrentParentId = currentParentId;
            Location parent = allLocations.stream()
                    .filter(l -> l.getId().equals(finalCurrentParentId))
                    .findFirst()
                    .orElse(null);
            if (parent == null) break;
            currentParentId = parent.getParentId();
        }
        return false;
    }

    @FXML
    private void onDeleteLocation() {
        if (currentSelectedLocation == null) {
            showError("Сначала выберите локацию в дереве");
            return;
        }
        deleteLocation(currentSelectedLocation);
    }

    private void deleteLocation(Location location) {
        if (!AlertUtil.confirm("Удаление локации", "Вы уверены, что хотите удалить локацию \"" + location.getDisplayName() + "\"?")) {
            return;
        }
        runAsync(() -> {
            try {
                App.getInstance().bus().getLocationService().delete(ClientUser.get(), location.getId());
                Platform.runLater(this::reloadTree);
                Platform.runLater(() -> AlertUtil.info("Успех", "Локация удалена"));
            } catch (Exception e) {
                handleError(e);
            }
        });
    }

    private ContextMenu createContextMenu(Location location) {
        ContextMenu menu = new ContextMenu();

        MenuItem attachDevice = new MenuItem("Привязать устройство");
        attachDevice.setOnAction(e -> attachDevice(location));

        MenuItem attachEmployee = new MenuItem("Привязать сотрудника");
        attachEmployee.setOnAction(e -> attachEmployee(location));

        MenuItem attachPhone = new MenuItem("Привязать телефон");
        attachPhone.setOnAction(e -> attachPhone(location));

        MenuItem addChild = new MenuItem("Создать дочернюю локацию");
        addChild.setOnAction(e -> showLocationCreationDialog(location));

        MenuItem addBulk = new MenuItem("Множественное добавление");
        addBulk.setOnAction(e -> showBulkLocationCreationDialog(location));

        MenuItem edit = new MenuItem("Редактировать");
        edit.setOnAction(e -> editLocation(location));

        MenuItem delete = new MenuItem("Удалить");
        delete.setOnAction(e -> deleteLocation(location));

        menu.getItems().addAll(
                new SeparatorMenuItem(),
                attachDevice, attachEmployee, attachPhone,
                new SeparatorMenuItem(),
                addChild, addBulk, edit, delete
        );

        return menu;
    }

    public void reloadTree() {
        runAsync(() -> {
            try {
                allLocations = App.getInstance().bus().getLocationService().getAll(ClientUser.get());
                Platform.runLater(() -> buildTree(allLocations));
            } catch (Exception e) {
                handleError(e);
            }
        });
    }

    private void buildTree(List<Location> all) {
        Set<Long> expandedIds = new HashSet<>();
        if (locationsTree.getRoot() != null) {
            collectExpandedIds(locationsTree.getRoot(), expandedIds);
        }

        Long selectedId = null;
        TreeItem<Location> selectedItem = locationsTree.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem.getValue() != null) {
            selectedId = selectedItem.getValue().getId();
        }

        TreeItem<Location> root = new TreeItem<>();
        root.setExpanded(true);

        for (Location loc : all) {
            if (loc.getParentId() == null || loc.getParentId() == 0) {
                TreeItem<Location> item = createItem(loc, all, expandedIds);
                root.getChildren().add(item);
            }
        }

        locationsTree.setRoot(root);
        locationsTree.setShowRoot(false);

        if (selectedId != null) {
            TreeItem<Location> itemToSelect = findItemById(root, selectedId);
            if (itemToSelect != null) {
                locationsTree.getSelectionModel().select(itemToSelect);
            } else if (!root.getChildren().isEmpty()) {
                locationsTree.getSelectionModel().select(root.getChildren().get(0));
            }
        } else if (!root.getChildren().isEmpty()) {
            locationsTree.getSelectionModel().select(root.getChildren().get(0));
        }
    }

    private TreeItem<Location> createItem(Location parent, List<Location> all, Set<Long> expandedIds) {
        TreeItem<Location> item = new TreeItem<>(parent);
        for (Location child : all) {
            if (parent.getId() != null && parent.getId().equals(child.getParentId())) {
                item.getChildren().add(createItem(child, all, expandedIds));
            }
        }

        if (parent.getId() != null && expandedIds.contains(parent.getId())) {
            item.setExpanded(true);
        } else {
            item.setExpanded(false);
        }

        return item;
    }

    private void collectExpandedIds(TreeItem<Location> item, Set<Long> expandedIds) {
        if (item.isExpanded() && item.getValue() != null) {
            expandedIds.add(item.getValue().getId());
        }
        for (TreeItem<Location> child : item.getChildren()) {
            collectExpandedIds(child, expandedIds);
        }
    }

    private TreeItem<Location> findItemById(TreeItem<Location> root, Long id) {
        if (root.getValue() != null && root.getValue().getId().equals(id)) {
            return root;
        }
        for (TreeItem<Location> child : root.getChildren()) {
            TreeItem<Location> found = findItemById(child, id);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    private void showLocationDetails(Location loc) {
        if (loc == null) return;
        lblId.setText(String.valueOf(loc.getId()));
        lblDisplayName.setText(loc.getDisplayName());
        lblNickname.setText(loc.getNickname() != null && !loc.getNickname().isBlank() ? loc.getNickname() : "—");
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
                List<DeviceLocation> deviceLocations = App.getInstance().bus().getDeviceLocationService().findAllByIdB(ClientUser.get(), loc.getId());
                List<Device> devices = new ArrayList<>();
                for (DeviceLocation dl : deviceLocations) {
                    App.getInstance().bus().getDeviceService().getById(ClientUser.get(), dl.getAId()).ifPresent(devices::add);
                }

                List<EmployeeLocation> employeeLocations = App.getInstance().bus().getEmployeeLocationService().findAllByIdB(ClientUser.get(), loc.getId());
                List<Employee> employees = new ArrayList<>();
                for (EmployeeLocation el : employeeLocations) {
                    App.getInstance().bus().getEmployeeService().getById(ClientUser.get(), el.getAId()).ifPresent(employees::add);
                }

                List<LocationPhone> locationPhones = App.getInstance().bus().getLocationPhoneService().findAllByIdA(ClientUser.get(), loc.getId());
                List<Phone> phones = new ArrayList<>();
                for (LocationPhone lp : locationPhones) {
                    App.getInstance().bus().getPhoneService().getById(ClientUser.get(), lp.getBId()).ifPresent(phones::add);
                }

                Platform.runLater(() -> {
                    lvDevices.setItems(FXCollections.observableArrayList(devices));
                    lvEmployees.setItems(FXCollections.observableArrayList(employees));
                    lvPhones.setItems(FXCollections.observableArrayList(phones));
                });
            } catch (Exception e) {
                System.err.println("Ошибка загрузки связанных сущностей для локации ID=" + loc.getId());
                e.printStackTrace();
                handleError(e);
            }
        });
    }

    private void clearDetails() {
        lblId.setText("—");
        lblDisplayName.setText("—");
        lblNickname.setText("—");
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
                String typeName = App.getInstance().bus().getTypeService().getById(ClientUser.get(), device.getTypeId())
                        .map(Type::getDisplayName).orElse("ID: " + device.getTypeId());

                String markName = "—";
                if (device.getTypeMarkId() != null) {
                    markName = App.getInstance().bus().getTypeMarkService().getById(ClientUser.get(), device.getTypeMarkId())
                            .map(TypeMark::getDisplayName).orElse("ID: " + device.getTypeMarkId());
                }
                String osName = "—";
                if (device.getOsId() != null) {
                    osName = App.getInstance().bus().getOSystemService().getById(ClientUser.get(), device.getOsId())
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
                    addDetailRow(grid, 2, "Инвентарный номер:", device.getDeviceInventoryNumber() != null ? device.getDeviceInventoryNumber() : "—");
                    addDetailRow(grid, 3, "Тип:", finalTypeName);
                    addDetailRow(grid, 4, "Марка:", finalMarkName);
                    addDetailRow(grid, 5, "ОС:", finalOsName);
                    addDetailRow(grid, 6, "IP:", device.getIp() != null ? device.getIp() : "—");
                    addDetailRow(grid, 7, "Статус:", DeviceStatusUtil.getDisplayName(device.getStatus()));
                    addDetailRow(grid, 8, "Описание:", device.getDescription() != null ? device.getDescription() : "—");
                    addDetailRow(grid, 9, "Последняя проверка:", DateUtil.format(device.getLastChecked()));

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
        addDetailRow(grid, 3, "IP:", phone.getIp() != null ? phone.getIp() : "—");
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

        comboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : textExtractor.apply(item));
            }
        });

        comboBox.setButtonCell(new ListCell<>() {
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
                List<Device> allDevices = App.getInstance().bus().getDeviceService().getAll(ClientUser.get());
                List<DeviceLocation> allDeviceLocations = App.getInstance().bus().getDeviceLocationService().getAll(ClientUser.get());

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
                        this::formatDeviceForDisplay,
                        device -> {
                            DeviceLocation dl = new DeviceLocation();
                            dl.setAId(device.getId());
                            dl.setBId(location.getId());
                            dl.setCreatedAt(System.currentTimeMillis());
                            dl.setLastChecked(System.currentTimeMillis());
                            try {
                                App.getInstance().bus().getDeviceLocationService().create(ClientUser.get(), dl);
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
                List<Employee> allEmployees = App.getInstance().bus().getEmployeeService().getAll(ClientUser.get());
                List<EmployeeLocation> allEmployeeLocations = App.getInstance().bus().getEmployeeLocationService().getAll(ClientUser.get());

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
                                App.getInstance().bus().getEmployeeLocationService().create(ClientUser.get(), el);
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
                List<Phone> allPhones = App.getInstance().bus().getPhoneService().getAll(ClientUser.get());
                List<LocationPhone> allLocationPhones = App.getInstance().bus().getLocationPhoneService().getAll(ClientUser.get());

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
                                App.getInstance().bus().getLocationPhoneService().create(ClientUser.get(), lp);
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
                App.getInstance().bus().getDeviceLocationService().delete(ClientUser.get(),
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
                App.getInstance().bus().getEmployeeLocationService().delete(ClientUser.get(),
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
                App.getInstance().bus().getLocationPhoneService().delete(ClientUser.get(),
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

    private String formatDeviceForDisplay(Device device) {
        try {
            BackendDataBus bus = App.getInstance().bus();
            String typeName = bus.getTypeService().getById(ClientUser.get(), device.getTypeId())
                    .map(Type::getDisplayName).orElse("Неизвестный тип");

            String markName = "—";
            if (device.getTypeMarkId() != null) {
                markName = bus.getTypeMarkService().getById(ClientUser.get(), device.getTypeMarkId())
                        .map(TypeMark::getDisplayName).orElse("Неизвестная марка");
            }

            String osName = "";
            if (device.getOsId() != null) {
                osName = " / " + bus.getOSystemService().getById(ClientUser.get(), device.getOsId())
                        .map(OSystem::getDisplayName).orElse("Неизвестная ОС");
            }

            return String.format("%s %s%s (ID: %d, S/N: %s, INV: %s)",
                    typeName,
                    markName,
                    osName,
                    device.getId(),
                    device.getDeviceSerial() != null ? device.getDeviceSerial() : "—",
                    device.getDeviceInventoryNumber() != null ? device.getDeviceInventoryNumber() : "—"
            );
        } catch (Exception e) {
            System.err.println("Ошибка форматирования устройства ID=" + device.getId());
            e.printStackTrace();
            return "Ошибка отображения (ID: " + device.getId() + ")";
        }
    }
}