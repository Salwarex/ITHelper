package ru.vit4liy.frontend.util;

public final class EmployeeStatusUtil {
    private EmployeeStatusUtil() {}

    public static String getDisplayName(int code) {
        return switch (code) {
            case 0 -> "Не активен";
            case 1 -> "Активен";
            default -> "Неизвестно (" + code + ")";
        };
    }
}