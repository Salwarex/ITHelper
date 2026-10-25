package ru.vit4liy.frontend.tabs;

import javafx.application.Platform;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ru.vit4liy.App;
import ru.vit4liy.base.model.device.Device;
import ru.vit4liy.base.model.device.system.OSystem;
import ru.vit4liy.base.model.intersections.DeviceLocation;
import ru.vit4liy.base.model.location.Location;
import ru.vit4liy.base.model.type.Type;
import ru.vit4liy.frontend.base.BaseTabController;
import ru.vit4liy.frontend.dialogs.DeviceCreateDialog;
import ru.vit4liy.frontend.dialogs.DeviceEditDialog;
import ru.vit4liy.frontend.util.*;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class DevicesTabController extends BaseTabController {

    @FXML private TableView<Device> devicesTable;
    @FXML private TableColumn<Device, Long> colId;
    @FXML private TableColumn<Device, String> colSerial;
    @FXML private TableColumn<Device, String> colInventory;
    @FXML private TableColumn<Device, String> colType;
    @FXML private TableColumn<Device, String> colOs;
    @FXML private TableColumn<Device, String> colIp;
    @FXML private TableColumn<Device, String> colLocation;
    @FXML private TableColumn<Device, String> colStatus;
    @FXML private TableColumn<Device, String> colDescription;

    @FXML private TextField tfSearch;
    @FXML private ComboBox<String> cbStatusFilter;
    @FXML private ComboBox<String> cbOsFilter;
    @FXML private TextField tfIpFilter;

    @FXML private Label lblId;
    @FXML private Label lblSerial;
    @FXML private Label lblInventory;
    @FXML private Label lblType;
    @FXML private Label lblOs;
    @FXML private Label lblIp;
    @FXML private Label lblLocation;
    @FXML private Label lblStatus;
    @FXML private Label lblDescription;

    private final ObservableList<Device> data = FXCollections.observableArrayList();
    private Map<Long, String> typeNameMap = new HashMap<>();
    private Map<Long, String> osNameMap = new HashMap<>();
    private Map<Long, String> deviceLocationPaths = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(cd -> new SimpleLongProperty(cd.getValue().getId()).asObject());
        colSerial.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getDeviceSerial()));
        colInventory.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getDeviceInventoryNumber()));
        colType.setCellValueFactory(cd -> new SimpleStringProperty(
                typeNameMap.getOrDefault((long) cd.getValue().getTypeId(), "ID: " + cd.getValue().getTypeId())
        ));
        colOs.setCellValueFactory(cd -> new SimpleStringProperty(
                osNameMap.getOrDefault(cd.getValue().getOsId(), "—")
        ));
        colIp.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getIp() != null ? cd.getValue().getIp() : "—"
        ));
        colLocation.setCellValueFactory(cd -> new SimpleStringProperty(
                deviceLocationPaths.getOrDefault(cd.getValue().getId(), "Не назначено")
        ));
        colStatus.setCellValueFactory(cd -> new SimpleStringProperty(
                DeviceStatusUtil.getDisplayName(cd.getValue().getStatus())
        ));
        colDescription.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getDescription()));

        devicesTable.setItems(data);
        devicesTable.getSelectionModel().selectedItemProperty().addListener((obs, old, d) -> showDetails(d));

        cbStatusFilter.getItems().addAll("Все статусы", "Работает хорошо", "Сломан", "В ремонте", "Списан");
        cbStatusFilter.getSelectionModel().selectFirst();

        tfSearch.textProperty().addListener((obs, o, n) -> reload());
        cbStatusFilter.setOnAction(e -> reload());
        cbOsFilter.setOnAction(e -> reload());
        tfIpFilter.textProperty().addListener((obs, o, n) -> reload());
    }

    @Override
    protected void onFirstLoad() { reload(); }
    @Override
    protected void onTabSelected() { reload(); }

    @FXML
    private void onAddDevice() {
        DeviceCreateDialog dialog = new DeviceCreateDialog();
        dialog.showAndWait().ifPresent(device -> {
            runAsync(() -> {
                try {
                    App.getInstance().bus().getDeviceService().create(ClientUser.get(), device);
                    Platform.runLater(() -> {
                        AlertUtil.info("Успех", "Устройство создано");
                        reload();
                    });
                } catch (Exception e) {
                    handleError(e);
                }
            });
        });
    }

    @FXML
    private void onEditDevice() {
        Device d = devicesTable.getSelectionModel().getSelectedItem();
        if (d == null) { showError("Выберите устройство"); return; }
        DeviceEditDialog dialog = new DeviceEditDialog(d);
        dialog.showAndWait().ifPresent(updated -> {
            runAsync(() -> {
                try {
                    App.getInstance().bus().getDeviceService().update(ClientUser.get(), updated);
                    Platform.runLater(() -> {
                        AlertUtil.info("Успех", "Устройство обновлено");
                        reload();
                    });
                } catch (Exception e) {
                    handleError(e);
                }
            });
        });
    }

    @FXML
    private void onDeleteDevice() {
        Device d = devicesTable.getSelectionModel().getSelectedItem();
        if (d == null) {
            showError("Выберите устройство для удаления");
            return;
        }
        if (!AlertUtil.confirm("Удаление",
                "Вы уверены, что хотите удалить устройство \"" + d.getDeviceSerial() + "\"?")) {
            return;
        }
        runAsync(() -> {
            try {
                App.getInstance().bus().getDeviceService().delete(ClientUser.get(), d.getId());
                Platform.runLater(() -> {
                    AlertUtil.info("Успех", "Устройство удалено");
                    reload();
                });
            } catch (Exception e) {
                handleError(e);
            }
        });
    }

    @FXML
    private void onReload() { reload(); }

    public void reload() {
        runAsync(() -> {
            try {
                List<Type> types = App.getInstance().bus().getTypeService().getAll(ClientUser.get());
                Map<Long, String> tempTypeNameMap = new HashMap<>();
                for (Type t : types) {
                    tempTypeNameMap.put(t.getId(), t.getDisplayName());
                }

                List<OSystem> osSystems = App.getInstance().bus().getOSystemService().getAll(ClientUser.get());
                Map<Long, String> tempOsNameMap = new HashMap<>();
                for (OSystem os : osSystems) {
                    tempOsNameMap.put(os.getId(), os.getDisplayName());
                }

                List<Location> allLocations = App.getInstance().bus().getLocationService().getAll(ClientUser.get());
                List<DeviceLocation> mappings = App.getInstance().bus().getDeviceLocationService().getAll(ClientUser.get());
                Map<Long, String> tempLocationPaths = new HashMap<>();

                for (DeviceLocation dl : mappings) {
                    Location loc = allLocations.stream()
                            .filter(l -> l.getId() != null && l.getId().equals(dl.getBId()))
                            .findFirst().orElse(null);
                    if (loc != null) {
                        tempLocationPaths.put(dl.getAId(), LocationPathUtil.buildPath(loc, allLocations));
                    }
                }

                String query = tfSearch.getText();
                String ipFilter = tfIpFilter != null ? tfIpFilter.getText() : null;
                String statusFilter = cbStatusFilter != null ? cbStatusFilter.getValue() : null;
                String osFilter = cbOsFilter != null ? cbOsFilter.getValue() : null;

                List<Device> result;
                if (query != null && !query.isBlank()) {
                    result = App.getInstance().bus().getDeviceService().getDeviceBySerial(ClientUser.get(), query.trim())
                            .map(List::of).orElse(List.of());
                } else if (ipFilter != null && !ipFilter.isBlank()) {
                    result = App.getInstance().bus().getDeviceService().getDevicesByIp(ClientUser.get(), ipFilter.trim());
                } else if (osFilter != null && !osFilter.equals("Все ОС")) {
                    OSystem selectedOs = osSystems.stream()
                            .filter(os -> os.getDisplayName().equals(osFilter))
                            .findFirst().orElse(null);
                    result = selectedOs != null ? App.getInstance().bus().getDeviceService().getDevicesByOsId(ClientUser.get(), selectedOs.getId()) : List.of();
                } else if (statusFilter != null && !statusFilter.equals("Все статусы")) {
                    String dbStatus = DeviceStatusUtil.getDbKey(statusFilter);
                    result = App.getInstance().bus().getDeviceService().getDevicesByStatus(ClientUser.get(), dbStatus);
                } else {
                    result = App.getInstance().bus().getDeviceService().getAll(ClientUser.get());
                }

                Platform.runLater(() -> {
                    this.typeNameMap = tempTypeNameMap;
                    this.osNameMap = tempOsNameMap;
                    this.deviceLocationPaths = tempLocationPaths;

                    data.setAll(result);

                    colType.setVisible(false); colType.setVisible(true);
                    colLocation.setVisible(false); colLocation.setVisible(true);
                    colOs.setVisible(false); colOs.setVisible(true);

                    if (cbOsFilter != null && cbOsFilter.getItems().isEmpty()) {
                        cbOsFilter.getItems().add("Все ОС");
                        for (OSystem os : osSystems) {
                            cbOsFilter.getItems().add(os.getDisplayName());
                        }
                        cbOsFilter.getSelectionModel().selectFirst();
                    }
                });
            } catch (Exception e) {
                handleError(e);
            }
        });
    }

    private void showDetails(Device d) {
        if (d == null) {
            clearDetails();
            return;
        }
        lblId.setText(String.valueOf(d.getId()));
        lblSerial.setText(d.getDeviceSerial() == null ? "—" : d.getDeviceSerial());
        lblInventory.setText(d.getDeviceInventoryNumber() == null ? "—" : d.getDeviceInventoryNumber());
        lblType.setText(typeNameMap.getOrDefault((long) d.getTypeId(), "ID: " + d.getTypeId()));
        lblOs.setText(osNameMap.getOrDefault(d.getOsId(), "—"));
        lblIp.setText(d.getIp() != null ? d.getIp() : "—");
        lblLocation.setText(deviceLocationPaths.getOrDefault(d.getId(), "Не назначено"));
        lblStatus.setText(DeviceStatusUtil.getDisplayName(d.getStatus()));
        lblDescription.setText(d.getDescription() == null ? "—" : d.getDescription());
    }

    private void clearDetails() {
        lblId.setText("—");
        lblSerial.setText("—");
        lblInventory.setText("");
        lblType.setText("—");
        lblOs.setText("—");
        lblIp.setText("—");
        lblLocation.setText("—");
        lblStatus.setText("—");
        lblDescription.setText("—");
    }
}