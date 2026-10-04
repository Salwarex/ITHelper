package ru.vit4liy.frontend.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class DateUtil {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
    private DateUtil() {}

    public static String format(long timestamp) {
        if (timestamp <= 0) return "—";
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()).format(FMT);
    }
}