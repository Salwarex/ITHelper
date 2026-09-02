package ru.vit4liy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.vit4liy.backend.data.repository.*;
import ru.vit4liy.backend.data.service.*;
import ru.vit4liy.backend.database.DatabaseManager;
import ru.vit4liy.it72h.lib.config.Config;
import ru.vit4liy.it72h.lib.logger.log.FileTerminalLogger;
import ru.vit4liy.it72h.lib.logger.log.Logger;

import java.awt.*;
import java.io.IOException;

public class App extends Application {
    private final static Config CONFIG = new Config("config.yml");
    private final static Logger LOGGER = new FileTerminalLogger();

    private final static DatabaseManager database = new DatabaseManager();

    private final static DeviceRepository deviceR = new DeviceRepository(database);
    private final static EmployeeRepository employeeR = new EmployeeRepository(database);
    private final static LocationRepository locationR = new LocationRepository(database);
    private final static PhoneRepository phoneR = new PhoneRepository(database);
    private final static TypeMarksRepository typeMarksR = new TypeMarksRepository(database);
    private final static TypeRepository typeR = new TypeRepository(database);

    private final static DeviceService deviceS = new DeviceService(deviceR);
    private final static EmployeeService employeeS = new EmployeeService(employeeR);
    private final static LocationService locationS = new LocationService(locationR);
    private final static PhoneService phoneS = new PhoneService(phoneR);
    private final static TypeMarkService typeMarkS = new TypeMarkService(typeMarksR);
    private final static TypeService typeS = new TypeService(typeR);


    public static Config getConfig() {
        return CONFIG;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    @Override
    public void start(Stage stage) throws Exception {
        var fxmlLocation = App.class.getResource("/ru/vit4liy/frontend/hello-view.fxml");

        if (fxmlLocation == null) {
            throw new IllegalStateException("Не удалось найти FXML файл! Проверьте путь в папке resources.");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        Scene scene = new Scene(fxmlLoader.load(), 400, 250);

        stage.setTitle("Архитектура FXML + Controller");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}