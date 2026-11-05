package com.elpdeliveries.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class ConfigManager {
    private static final Properties PROPERTIES = new Properties();
    private static final Path CONFIG_DIR = determineConfigDirectory();
    private static final Path CONFIG_FILE = CONFIG_DIR.resolve("config.properties");

    static {
        try {
            AppLogger.info("Loading config at " + getConfigFile());
            load();
        } catch (Exception e) {
            AppLogger.error("Failed to load config", e);
        }
    }

    private ConfigManager() {}

    private static Path determineConfigDirectory() {
        String os = System.getProperty("os.name").toLowerCase();
        String home = System.getProperty("user.home");
        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            if (appData != null) {
                return Paths.get(appData, "ELP Deliveries");
            }
            return Paths.get(home, "AppData", "Roaming", "ELP Deliveries");
        }
        if (os.contains("mac")) {
            return Paths.get(home, "Library", "Application Support", "ELP Deliveries");
        }
        return Paths.get(home, ".elpdeliveries");
    }

    public static boolean exists() {return Files.exists(CONFIG_FILE);}

    public static Path getConfigFile() {return CONFIG_FILE;}
    public static Path getConfigDirectory() {return CONFIG_DIR;}

    public static synchronized void load() throws IOException {
        if (!exists()) {
            return;
        }
        try (InputStream in = Files.newInputStream(CONFIG_FILE)) {
            PROPERTIES.clear();
            PROPERTIES.load(in);
        }
    }

    public static synchronized void save(
            String server,
            String port)
            throws IOException {
        Files.createDirectories(CONFIG_DIR);
        PROPERTIES.setProperty("server", server);
        PROPERTIES.setProperty("port", port);

        try (OutputStream out = Files.newOutputStream(CONFIG_FILE)) {
            PROPERTIES.store(out, "ELP DELIVERIES SETTINGS");
        }
    }
    private static String get(
            String key) {

        return PROPERTIES.getProperty(key);
    }

    public static String getServer() {
        return get("server");
    }

    public static String getPort() {
        return get("port");
    }
}