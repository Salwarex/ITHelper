package ru.vit4liy.frontend.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ru.vit4liy.backend.base.model.employee.Employee;
import ru.vit4liy.frontend.util.AlertUtil;

public class EmployeeCreateDialog extends Dialog<Employee> {
    public EmployeeCreateDialog() {
        setTitle("Новый сотрудник");
        setHeaderText("Заполните данные о сотруднике");

        ButtonType createButtonType = new ButtonType("Создать", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20, 150, 10, 10));

        TextField tfSurname = new TextField(); tfSurname.setPromptText("Фамилия");
        TextField tfName = new TextField(); tfName.setPromptText("Имя");
        TextField tfThirdname = new TextField(); tfThirdname.setPromptText("Отчество");

        grid.add(new Label("Фамилия:"), 0, 0); grid.add(tfSurname, 1, 0);
        grid.add(new Label("Имя:"), 0, 1); grid.add(tfName, 1, 1);
        grid.add(new Label("Отчество:"), 0, 2); grid.add(tfThirdname, 1, 2);

        getDialogPane().setContent(grid);

        setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                if (tfSurname.getText().isBlank() || tfName.getText().isBlank()) {
                    AlertUtil.error("Ошибка", "Фамилия и Имя обязательны!");
                    return null;
                }
                Employee emp = new Employee();
                emp.setSurname(tfSurname.getText().trim());
                emp.setName(tfName.getText().trim());
                emp.setThirdname(tfThirdname.getText().trim());
                emp.setStatus(1); // Активен по умолчанию
                long now = System.currentTimeMillis();
                emp.setCreatedAt(now);
                emp.setLastStatusChange(now);
                emp.setLastChecked(now);
                return emp;
            }
            return null;
        });
    }
}