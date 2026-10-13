package ru.vit4liy.frontend;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;
import ru.vit4liy.App;
import ru.vit4liy.frontend.base.BaseTabController;
import ru.vit4liy.frontend.tabs.*;
import ru.vit4liy.frontend.util.AlertUtil;
import ru.vit4liy.frontend.util.ApplicationFeatures;
import ru.vit4liy.frontend.util.ClientUser;

import java.io.File;

public class MainController {

    @FXML private TabPane tabPane;
    @FXML private Tab locationsTab;
    @FXML private Tab devicesTab;
    @FXML private Tab employeesTab;
    @FXML private Tab dictionariesTab;
    @FXML private Tab dataEntryTab;

    @FXML private LocationsTabController locationsController;
    @FXML private DevicesTabController devicesController;
    @FXML private EmployeesTabController employeesController;
    @FXML private DictionariesTabController dictionariesController;
    @FXML private DataEntryController dataEntryController;

    @FXML
    public void initialize() {
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            BaseTabController controller = resolveController(newTab);
            if (controller != null) controller.handleTabSelected();
        });

        Tab first = tabPane.getSelectionModel().getSelectedItem();
        BaseTabController controller = resolveController(first);
        if (controller != null) controller.handleTabSelected();
    }

    private BaseTabController resolveController(Tab tab) {
        if (tab == locationsTab) return locationsController;
        if (tab == devicesTab) return devicesController;
        if (tab == employeesTab) return employeesController;
        if (tab == dictionariesTab) return dictionariesController;
        if (tab == dataEntryTab) return dataEntryController;
        return null;
    }

    @FXML
    private void onImportOverwrite() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл для импорта");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ITH файлы", "*.ith"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            if (AlertUtil.confirm("Импорт (затирание)",
                    "Все текущие данные будут удалены и заменены данными из файла. Продолжить?")) {
                ApplicationFeatures.runAsync(() -> {
                    try {
                        App.getInstance()
                                .getModuleLoader()
                                .getDistributionModule()
                                .getImportService()
                                .importAll(ClientUser.get(), file, true);
                        Platform.runLater(() -> {
                            AlertUtil.info("Успех", "Данные успешно импортированы");
                            refreshAllTabs(); // <-- Обновляем все вкладки
                        });
                    } catch (Exception e) {
                        Platform.runLater(() -> AlertUtil.errorWithStack("Ошибка импорта", e));
                    }
                });
            }
        }
    }

    @FXML
    private void onImportAppend() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл для импорта");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ITH файлы", "*.ith"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            if (AlertUtil.confirm("Импорт (добавочный)",
                    "Данные из файла будут добавлены к существующим. Продолжить?")) {
                ApplicationFeatures.runAsync(() -> {
                    try {
                        App.getInstance()
                                .getModuleLoader()
                                .getDistributionModule()
                                .getImportService()
                                .importAll(ClientUser.get(), file, false);
                        Platform.runLater(() -> {
                            AlertUtil.info("Успех", "Данные успешно добавлены");
                            refreshAllTabs(); // <-- Обновляем все вкладки
                        });
                    } catch (Exception e) {
                        Platform.runLater(() -> AlertUtil.errorWithStack("Ошибка импорта", e));
                    }
                });
            }
        }
    }

    public void refreshAllTabs() {
        if (locationsController != null) locationsController.reloadTree();
        if (devicesController != null) devicesController.reload();
        if (employeesController != null) employeesController.reload();
        if (dictionariesController != null) dictionariesController.reloadAll();
        if (dataEntryController != null) dataEntryController.loadInitialData();
    }

    @FXML
    private void onExport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить файл экспорта");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ITH файлы", "*.ith"));
        fileChooser.setInitialFileName("export_" + System.currentTimeMillis() + ".ith");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            ApplicationFeatures.runAsync(() -> {
                try {
                    App.getInstance()
                            .getModuleLoader()
                            .getDistributionModule()
                            .getExportService()
                            .exportAll(ClientUser.get(), file);
                    Platform.runLater(() -> AlertUtil.info("Успех", "Данные успешно экспортированы в " + file.getName()));
                } catch (Exception e) {
                    Platform.runLater(() -> AlertUtil.errorWithStack("Ошибка экспорта", e));
                }
            });
        }
    }

    @FXML
    private void onFullDelete(){
        ApplicationFeatures.runAsync(() -> {
            try {
                App.getInstance()
                        .getModuleLoader()
                        .getBus()
                        .getDatabaseService()
                        .removeAll(ClientUser.get());
                Platform.runLater(() -> {
                    AlertUtil.info("Успех", "Данные успешно удалены!");
                    refreshAllTabs();
                });
            } catch (Exception e) {
                Platform.runLater(() -> AlertUtil.errorWithStack("Ошибка удаления данных", e));
            }
        });
    }
}