package ru.vit4liy.backend.files;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {
    public static Path getAppDataDirectory() {
        String os = System.getProperty("os.name").toLowerCase();
        String appName = "INVCon";

        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            return Paths.get(appData, appName);
        } else if (os.contains("mac")) {
            return Paths.get(System.getProperty("user.home"), "Library", "Application Support", appName);
        } else {
            return Paths.get(System.getProperty("user.home"), ".config", appName);
        }
    }
}
