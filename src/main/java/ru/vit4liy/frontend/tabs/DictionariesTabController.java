package ru.vit4liy.frontend.tabs;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ru.vit4liy.base.model.device.system.OSystem;
import ru.vit4liy.base.model.phone.Phone;
import ru.vit4liy.base.model.type.Type;
import ru.vit4liy.base.model.typemark.TypeMark;
import ru.vit4liy.frontend.base.BaseTabController;
import ru.vit4liy.frontend.dialogs.TypeMarkCreateDialog;
import ru.vit4liy.frontend.util.AlertUtil;
import ru.vit4liy.frontend.util.BackendConnection;
import ru.vit4liy.perm.MissingPermissionException;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DictionariesTabController extends BaseTabController {

    @FXML private TabPane dictTabPane;
    @FXML private TableView<Type> typesTable;
    @FXML private TableColumn<Type, String> colTypeName;
    @FXML private TableView<TypeMark> marksTable;
    @FXML private TableColumn<TypeMark, String> colMarkName;
    @FXML private TableColumn<TypeMark, String> colMarkType;
    @FXML private TableView<Phone> phonesTable;
    @FXML private TableColumn<Phone, String> colPhoneCorp;
    @FXML private TableColumn<Phone, String> colPhonePublic;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colTypeName.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getDisplayName()));
        colMarkName.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getDisplayName()));
        colMarkType.setCellValueFactory(cd -> {
            try {
                Type t = BackendConnection.db().getTypeService().getById(cd.getValue().getTypeId()).orElse(null);
                return new javafx.beans.property.SimpleStringProperty(t != null ? t.getDisplayName() : "ID: " + cd.getValue().getTypeId());
            } catch (Exception e) { return new javafx.beans.property.SimpleStringProperty("Ошибка"); }
        });
        colPhoneCorp.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getCorpPhoneNumber()));
        colPhonePublic.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getPublicPhoneNumber()));
        colOsName.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getDisplayName()));
    }

    @Override
    protected void onFirstLoad() { reloadAll(); }
    @Override
    protected void onTabSelected() { reloadAll(); }

    public void reloadAll() {
        runAsync(() -> {
            try {
                Platform.runLater(() -> {
                    try {
                        typesTable.setItems(FXCollections.observableArrayList(BackendConnection.db().getTypeService().getAll()));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (MissingPermissionException e) {
                        throw new RuntimeException(e);
                    }
                });
                Platform.runLater(() -> {
                    try {
                        marksTable.setItems(FXCollections.observableArrayList(BackendConnection.db().getTypeMarkService().getAll()));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (MissingPermissionException e) {
                        throw new RuntimeException(e);
                    }
                });
                Platform.runLater(() -> {
                    try {
                        phonesTable.setItems(FXCollections.observableArrayList(BackendConnection.db().getPhoneService().getAll()));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (MissingPermissionException e) {
                        throw new RuntimeException(e);
                    }
                });
                Platform.runLater(() -> {
                    try {
                        osTable.setItems(FXCollections.observableArrayList(BackendConnection.db().getOSystemService().getAll()));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (MissingPermissionException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (Exception e) { handleError(e); }
        });
    }

    @FXML private void onAddType() {
        TextInputDialog d = new TextInputDialog(); d.setHeaderText("Новый тип"); d.setContentText("Название:");
        d.showAndWait().ifPresent(name -> runAsync(() -> {
            try {
                Type t = new Type(); t.setDisplayName(name.trim()); t.setCreatedAt(System.currentTimeMillis());
                BackendConnection.db().getTypeService().create(t);
                Platform.runLater(this::reloadAll);
            } catch (Exception e) { handleError(e); }
        }));
    }
    @FXML private void onEditType() {
        Type t = typesTable.getSelectionModel().getSelectedItem();
        if (t == null) { showError("Выберите тип"); return; }
        TextInputDialog d = new TextInputDialog(t.getDisplayName()); d.setHeaderText("Редактировать тип"); d.setContentText("Название:");
        d.showAndWait().ifPresent(name -> runAsync(() -> {
            try {
                t.setDisplayName(name.trim());
                BackendConnection.db().getTypeService().update(t);
                Platform.runLater(this::reloadAll);
            } catch (Exception e) { handleError(e); }
        }));
    }
    @FXML private void onDeleteType() {
        Type t = typesTable.getSelectionModel().getSelectedItem();
        if (t == null) { showError("Выберите тип"); return; }
        if (!AlertUtil.confirm("Удаление", "Удалить тип \"" + t.getDisplayName() + "\"?")) return;
        runAsync(() -> {
            try { BackendConnection.db().getTypeService().delete(t.getId()); Platform.runLater(this::reloadAll); }
            catch (Exception e) { handleError(e); }
        });
    }

    @FXML private void onAddMark() {
        TypeMarkCreateDialog dialog = new TypeMarkCreateDialog();
        dialog.showAndWait().ifPresent(mark -> runAsync(() -> {
            try {
                BackendConnection.db().getTypeMarkService().create(mark);
                Platform.runLater(this::reloadAll);
            } catch (Exception e) { handleError(e); }
        }));
    }
    @FXML private void onEditMark() {
        TypeMark m = marksTable.getSelectionModel().getSelectedItem();
        if (m == null) { showError("Выберите марку"); return; }
        TextInputDialog d = new TextInputDialog(m.getDisplayName()); d.setHeaderText("Редактировать марку"); d.setContentText("Название:");
        d.showAndWait().ifPresent(name -> runAsync(() -> {
            try {
                m.setDisplayName(name.trim());
                BackendConnection.db().getTypeMarkService().update(m);
                Platform.runLater(this::reloadAll);
            } catch (Exception e) { handleError(e); }
        }));
    }
    @FXML private void onDeleteMark() {
        TypeMark m = marksTable.getSelectionModel().getSelectedItem();
        if (m == null) { showError("Выберите марку"); return; }
        if (!AlertUtil.confirm("Удаление", "Удалить марку \"" + m.getDisplayName() + "\"?")) return;
        runAsync(() -> {
            try { BackendConnection.db().getTypeMarkService().delete(m.getId()); Platform.runLater(this::reloadAll); }
            catch (Exception e) { handleError(e); }
        });
    }

    // === Телефоны ===
    @FXML private void onAddPhone() {
        Dialog<Phone> d = new Dialog<>();
        d.setTitle("Новый телефон");
        ButtonType ok = new ButtonType("Создать", ButtonBar.ButtonData.OK_DONE);
        d.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);
        TextField corp = new TextField(); corp.setPromptText("Корп. (4 цифры)");
        TextField pub = new TextField(); pub.setPromptText("Городской (6 цифр)");
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(new Label("Корпоративный:"), 0, 0); grid.add(corp, 1, 0);
        grid.add(new Label("Городской:"), 0, 1); grid.add(pub, 1, 1);
        d.getDialogPane().setContent(grid);
        d.setResultConverter(b -> b == ok ? createPhone(corp.getText(), pub.getText()) : null);
        d.showAndWait().ifPresent(phone -> runAsync(() -> {
            try { BackendConnection.db().getPhoneService().create(phone); Platform.runLater(this::reloadAll); }
            catch (Exception e) { handleError(e); }
        }));
    }
    @FXML private void onEditPhone() {
        Phone p = phonesTable.getSelectionModel().getSelectedItem();
        if (p == null) { showError("Выберите телефон"); return; }
        Dialog<Phone> d = new Dialog<>();
        d.setTitle("Редактировать телефон");
        ButtonType ok = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        d.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);
        TextField corp = new TextField(p.getCorpPhoneNumber()); corp.setPromptText("4 цифры");
        TextField pub = new TextField(p.getPublicPhoneNumber()); pub.setPromptText("6 цифр");
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(new Label("Корпоративный:"), 0, 0); grid.add(corp, 1, 0);
        grid.add(new Label("Городской:"), 0, 1); grid.add(pub, 1, 1);
        d.getDialogPane().setContent(grid);
        d.setResultConverter(b -> b == ok ? createPhone(corp.getText(), pub.getText()) : null);
        d.showAndWait().ifPresent(phone -> runAsync(() -> {
            try {
                phone.setId(p.getId());
                BackendConnection.db().getPhoneService().update(phone);
                Platform.runLater(this::reloadAll);
            } catch (Exception e) { handleError(e); }
        }));
    }
    @FXML private void onDeletePhone() {
        Phone p = phonesTable.getSelectionModel().getSelectedItem();
        if (p == null) { showError("Выберите телефон"); return; }
        if (!AlertUtil.confirm("Удаление", "Удалить телефон " + p.getCorpPhoneNumber() + "?")) return;
        runAsync(() -> {
            try { BackendConnection.db().getPhoneService().delete(p.getId()); Platform.runLater(this::reloadAll); }
            catch (Exception e) { handleError(e); }
        });
    }

    private Phone createPhone(String c, String p) {
        String corp = c == null ? "" : c.trim();
        String pub = p == null ? "" : p.trim();
        if (!corp.matches("\\d{4}")) {
            AlertUtil.error("Ошибка", "Корпоративный номер должен содержать ровно 4 цифры!");
            return null;
        }
        if (!pub.matches("\\d{6}")) {
            AlertUtil.error("Ошибка", "Городской номер должен содержать ровно 6 цифр!");
            return null;
        }
        Phone phone = new Phone();
        phone.setCorpPhoneNumber(corp);
        phone.setPublicPhoneNumber(pub);
        phone.setLastChecked(System.currentTimeMillis());
        return phone;
    }

    @FXML private TableView<OSystem> osTable;
    @FXML private TableColumn<OSystem, String> colOsName;

    @FXML private void onAddOs() {
        TextInputDialog d = new TextInputDialog();
        d.setHeaderText("Новая ОС");
        d.setContentText("Название:");
        d.showAndWait().ifPresent(name -> runAsync(() -> {
            try {
                OSystem os = new OSystem();
                os.setDisplayName(name.trim());
                BackendConnection.db().getOSystemService().create(os);
                Platform.runLater(this::reloadAll);
            } catch (Exception e) { handleError(e); }
        }));
    }

    @FXML private void onEditOs() {
        OSystem os = osTable.getSelectionModel().getSelectedItem();
        if (os == null) { showError("Выберите ОС"); return; }
        TextInputDialog d = new TextInputDialog(os.getDisplayName());
        d.setHeaderText("Редактировать ОС");
        d.setContentText("Название:");
        d.showAndWait().ifPresent(name -> runAsync(() -> {
            try {
                os.setDisplayName(name.trim());
                BackendConnection.db().getOSystemService().update(os);
                Platform.runLater(this::reloadAll);
            } catch (Exception e) { handleError(e); }
        }));
    }

    @FXML private void onDeleteOs() {
        OSystem os = osTable.getSelectionModel().getSelectedItem();
        if (os == null) { showError("Выберите ОС"); return; }
        if (!AlertUtil.confirm("Удаление", "Удалить ОС \"" + os.getDisplayName() + "\"?")) return;
        runAsync(() -> {
            try {
                BackendConnection.db().getOSystemService().delete(os.getId());
                Platform.runLater(this::reloadAll);
            } catch (Exception e) { handleError(e); }
        });
    }
}