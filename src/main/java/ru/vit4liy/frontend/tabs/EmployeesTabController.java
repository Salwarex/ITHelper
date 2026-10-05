package ru.vit4liy.frontend.tabs;

import javafx.application.Platform;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ru.vit4liy.base.model.employee.Employee;
import ru.vit4liy.base.model.intersections.EmployeeLocation;
import ru.vit4liy.base.model.location.Location;
import ru.vit4liy.frontend.base.BaseTabController;
import ru.vit4liy.frontend.util.AlertUtil;
import ru.vit4liy.frontend.util.BackendConnection;
import ru.vit4liy.frontend.util.DateUtil;
import ru.vit4liy.frontend.util.EmployeeStatusUtil;
import ru.vit4liy.frontend.util.LocationPathUtil;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class EmployeesTabController extends BaseTabController {

    @FXML private TableView<Employee> employeesTable;
    @FXML private TableColumn<Employee, Long> colId;
    @FXML private TableColumn<Employee, String> colSurname;
    @FXML private TableColumn<Employee, String> colName;
    @FXML private TableColumn<Employee, String> colThirdname;
    @FXML private TableColumn<Employee, String> colLocation;
    @FXML private TableColumn<Employee, String> colStatus;
    @FXML private TableColumn<Employee, String> colPhone;

    @FXML private TextField tfSearch;
    @FXML private ComboBox<String> cbSearchMode;

    @FXML private Label lblId;
    @FXML private Label lblSurname;
    @FXML private Label lblName;
    @FXML private Label lblThirdname;
    @FXML private Label lblLocation;
    @FXML private Label lblStatus;
    @FXML private Label lblLastChecked;
    @FXML private Label lblPhone;

    private final ObservableList<Employee> data = FXCollections.observableArrayList();
    private Map<Long, String> employeeLocationPaths = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(cd -> new SimpleLongProperty(cd.getValue().getId()).asObject());
        colSurname.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getSurname()));
        colName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getName()));
        colThirdname.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getThirdname()));
        colLocation.setCellValueFactory(cd -> new SimpleStringProperty(
                employeeLocationPaths.getOrDefault(cd.getValue().getId(), "Не назначено")
        ));
        colStatus.setCellValueFactory(cd -> new SimpleStringProperty(
                EmployeeStatusUtil.getDisplayName(cd.getValue().getStatus())
        ));
        colPhone.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getPhone() != null ? cd.getValue().getPhone() : "—"
        ));

        employeesTable.setItems(data);
        employeesTable.getSelectionModel().selectedItemProperty().addListener((obs, old, emp) -> showDetails(emp));

        cbSearchMode.getItems().addAll("По фамилии", "По имени", "По ФИО целиком");
        cbSearchMode.getSelectionModel().selectFirst();

        tfSearch.textProperty().addListener((obs, o, n) -> reload());
        cbSearchMode.setOnAction(e -> reload());
    }

    @Override
    protected void onFirstLoad() {
        reload();
    }

    @Override
    protected void onTabSelected() {
        reload();
    }

    @FXML
    private void onReload() {
        reload();
    }

    public void reload() {
        String query = tfSearch.getText();
        String mode = cbSearchMode.getValue();

        runAsync(() -> {
            try {
                List<Location> allLocations = BackendConnection.db().getLocationService().getAll();
                List<EmployeeLocation> mappings = BackendConnection.db().getEmployeeLocationService().getAll();

                Map<Long, String> tempPaths = new HashMap<>();
                for (EmployeeLocation el : mappings) {
                    Location loc = allLocations.stream()
                            .filter(l -> l.getId() != null && l.getId().equals(el.getBId()))
                            .findFirst().orElse(null);
                    if (loc != null) {
                        tempPaths.put(el.getAId(), LocationPathUtil.buildPath(loc, allLocations));
                    }
                }

                List<Employee> result;
                if (query == null || query.isBlank()) {
                    result = BackendConnection.db().getEmployeeService().getAll();
                } else {
                    String searchQuery = query.trim();
                    result = switch (mode) {
                        case "По имени" -> BackendConnection.db().getEmployeeService().searchEmployeesByName(searchQuery);
                        case "По ФИО целиком" -> BackendConnection.db().getEmployeeService().searchEmployeesByFullName(searchQuery);
                        default -> BackendConnection.db().getEmployeeService().searchEmployeesBySurname(searchQuery);
                    };
                }

                Platform.runLater(() -> {
                    this.employeeLocationPaths = tempPaths;
                    data.setAll(result);
                    colLocation.setVisible(false);
                    colLocation.setVisible(true);
                });
            } catch (Exception e) {
                handleError(e);
            }
        });
    }

    private void showDetails(Employee emp) {
        if (emp == null) {
            clearDetails();
            return;
        }
        lblId.setText(String.valueOf(emp.getId()));
        lblSurname.setText(emp.getSurname() != null ? emp.getSurname() : "—");
        lblName.setText(emp.getName() != null ? emp.getName() : "—");
        lblThirdname.setText(emp.getThirdname() != null ? emp.getThirdname() : "—");
        lblLocation.setText(employeeLocationPaths.getOrDefault(emp.getId(), "Не назначено"));
        lblStatus.setText(EmployeeStatusUtil.getDisplayName(emp.getStatus()));
        lblLastChecked.setText(DateUtil.format(emp.getLastChecked()));
        lblPhone.setText(emp.getPhone() != null ? emp.getPhone() : "—");
    }

    private void clearDetails() {
        lblId.setText("—");
        lblSurname.setText("—");
        lblName.setText("—");
        lblThirdname.setText("—");
        lblLocation.setText("—");
        lblStatus.setText("—");
        lblLastChecked.setText("—");
    }

    @FXML
    private void onAddEmployee() {
        ru.vit4liy.frontend.dialogs.EmployeeCreateDialog dialog = new ru.vit4liy.frontend.dialogs.EmployeeCreateDialog();
        dialog.showAndWait().ifPresent(employee -> {
            runAsync(() -> {
                try {
                    BackendConnection.db().getEmployeeService().create(employee);
                    Platform.runLater(() -> {
                        AlertUtil.info("Успех", "Сотрудник создан");
                        reload();
                    });
                } catch (Exception e) {
                    handleError(e);
                }
            });
        });
    }

    @FXML
    private void onEditEmployee() {
        Employee emp = employeesTable.getSelectionModel().getSelectedItem();
        if (emp == null) {
            showError("Выберите сотрудника для редактирования");
            return;
        }

        ru.vit4liy.frontend.dialogs.EmployeeEditDialog dialog = new ru.vit4liy.frontend.dialogs.EmployeeEditDialog(emp);
        dialog.showAndWait().ifPresent(updated -> {
            runAsync(() -> {
                try {
                    BackendConnection.db().getEmployeeService().update(updated);
                    Platform.runLater(() -> {
                        AlertUtil.info("Успех", "Сотрудник обновлён");
                        reload();
                    });
                } catch (Exception e) {
                    handleError(e);
                }
            });
        });
    }

    @FXML
    private void onDeleteEmployee() {
        Employee emp = employeesTable.getSelectionModel().getSelectedItem();
        if (emp == null) {
            showError("Выберите сотрудника для удаления");
            return;
        }
        if (!AlertUtil.confirm("Удаление",
                "Вы уверены, что хотите удалить сотрудника \"" + emp.getSurname() + " " + emp.getName() + "\"?")) {
            return;
        }

        runAsync(() -> {
            try {
                BackendConnection.db().getEmployeeService().delete(emp.getId());
                Platform.runLater(() -> {
                    AlertUtil.info("Успех", "Сотрудник удалён");
                    reload();
                });
            } catch (Exception e) {
                handleError(e);
            }
        });
    }
}