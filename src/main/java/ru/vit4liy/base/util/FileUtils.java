package ru.vit4liy.base.util; // Или твой пакет

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    private static Path BASE_DIR;
    private static boolean isInitialized = false;
    
    public static void initDirectories(boolean useSystemDir, String appName) {
        if (isInitialized) return;

        if (useSystemDir) {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                BASE_DIR = Paths.get(System.getenv("APPDATA"), appName);
            } else if (os.contains("mac")) {
                BASE_DIR = Paths.get(System.getProperty("user.home"), "Library", "Application Support", appName);
            } else {
                BASE_DIR = Paths.get(System.getProperty("user.home"), ".config", appName);
            }
        } else {
            BASE_DIR = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        }

        try {
            Files.createDirectories(BASE_DIR);
            System.setProperty("user.dir", BASE_DIR.toAbsolutePath().toString());
            System.out.println("Base directory: " + BASE_DIR.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Directory creating error: " + e.getMessage());
        }

        isInitialized = true;
    }

    public static Path getBaseDir() {
        if (!isInitialized) throw new IllegalStateException("FileUtils not initialized!");
        return BASE_DIR;
    }

    public static Path getConfigPath() {
        return getBaseDir().resolve("config.yml");
    }

    public static Path getLogsDir() {
        return getBaseDir().resolve("logs");
    }

    public static Path getPermissionsDir() {
        return getBaseDir().resolve("permission");
    }
}