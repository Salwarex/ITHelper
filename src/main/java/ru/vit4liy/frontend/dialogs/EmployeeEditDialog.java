package ru.vit4liy.frontend.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ru.vit4liy.backend.base.model.employee.Employee;
import ru.vit4liy.frontend.util.AlertUtil;

public class EmployeeEditDialog extends Dialog<Employee> {
    public EmployeeEditDialog(Employee emp) {
        setTitle("Редактировать сотрудника");
        setHeaderText("Изменение данных: " + emp.getSurname() + " " + emp.getName());

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20, 150, 10, 10));

        TextField tfSurname = new TextField(emp.getSurname());
        TextField tfName = new TextField(emp.getName());
        TextField tfThirdname = new TextField(emp.getThirdname());
        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("Активен", "Не активен");
        cbStatus.setValue(emp.getStatus() == 1 ? "Активен" : "Не активен");

        grid.add(new Label("Фамилия:"), 0, 0); grid.add(tfSurname, 1, 0);
        grid.add(new Label("Имя:"), 0, 1); grid.add(tfName, 1, 1);
        grid.add(new Label("Отчество:"), 0, 2); grid.add(tfThirdname, 1, 2);
        grid.add(new Label("Статус:"), 0, 3); grid.add(cbStatus, 1, 3);

        getDialogPane().setContent(grid);

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (tfSurname.getText().isBlank() || tfName.getText().isBlank()) {
                    AlertUtil.error("Ошибка", "Фамилия и Имя обязательны!");
                    return null;
                }
                emp.setSurname(tfSurname.getText().trim());
                emp.setName(tfName.getText().trim());
                emp.setThirdname(tfThirdname.getText().trim());
                emp.setStatus(cbStatus.getValue().equals("Активен") ? 1 : 0);
                emp.setLastStatusChange(System.currentTimeMillis());
                return emp;
            }
            return null;
        });
    }
}