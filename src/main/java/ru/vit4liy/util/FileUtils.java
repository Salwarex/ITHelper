package ru.vit4liy.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    public static Path getAppDataDirectory(String appName) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            return Paths.get(appData, appName);
        } else if (os.contains("mac")) {
            String userHome = System.getProperty("user.home");
            return Paths.get(userHome, "Library", "Application Support", appName);
        } else {
            String userHome = System.getProperty("user.home");
            return Paths.get(userHome, ".config", appName);
        }
    }

    public static void initializeAppWorkingDirectory(String appName) {
        Path appDir = getAppDataDirectory(appName);

        try {
            Files.createDirectories(appDir);

            System.setProperty("user.dir", appDir.toAbsolutePath().toString());

            System.setProperty("config.path", appDir.resolve("config.yml").toString());
            System.setProperty("config.dir", appDir.toString());

            System.out.println("Рабочая директория приложения установлена в: " + appDir.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("Не удалось создать рабочую директорию: " + e.getMessage());
        }
    }

    public static void createSymlinks(String appName) {
        Path appDir = getAppDataDirectory(appName);
        Path launchDir = Paths.get(System.getProperty("user.dir"));

        try {
            createSymlinkIfNotExists(launchDir.resolve("permission"), appDir.resolve("permission"));
            createSymlinkIfNotExists(launchDir.resolve("logs"), appDir.resolve("logs"));
            createSymlinkIfNotExists(launchDir.resolve("config.yml"), appDir.resolve("config.yml"));
        } catch (Exception e) {
            System.err.println("Не удалось создать символические ссылки: " + e.getMessage());
        }
    }

    private static void createSymlinkIfNotExists(Path link, Path target) throws IOException {
        if (!Files.exists(link)) {
            if (Files.exists(target)) {
                Files.createSymbolicLink(link, target);
            } else {
                Files.createDirectories(target.getParent());
                Files.createDirectories(target);
                Files.createSymbolicLink(link, target);
            }
        }
    }
}