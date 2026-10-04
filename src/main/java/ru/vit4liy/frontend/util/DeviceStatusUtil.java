package ru.vit4liy.frontend.util;

public final class DeviceStatusUtil {
    private DeviceStatusUtil() {}

    public static String getDisplayName(String dbKey) {
        if (dbKey == null || dbKey.isBlank()) return "Неизвестно";
        return switch (dbKey.toUpperCase()) {
            case "GOOD_WORK" -> "Работает хорошо";
            case "BROKEN" -> "Сломан";
            case "REPAIR" -> "В ремонте";
            case "DECOMMISSIONED" -> "Списан";
            default -> dbKey;
        };
    }

    public static String getDbKey(String displayName) {
        return switch (displayName) {
            case "Работает хорошо" -> "GOOD_WORK";
            case "Сломан" -> "BROKEN";
            case "В ремонте" -> "REPAIR";
            case "Списан" -> "DECOMMISSIONED";
            default -> displayName;
        };
    }
}