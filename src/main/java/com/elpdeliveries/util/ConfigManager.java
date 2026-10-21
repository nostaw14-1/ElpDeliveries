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
            AppLogger.info("Loading config at " + getConfigDirectory() + getConfigFile());
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
            String port,
            String database,
            String username,
            String password,
            String sqlServer,
            String sqlDatabase,
            String sqlUsername,
            String sqlPassword,
            String awsAccessKey,
            String awsSecretKey,
            String bucket,
            String vroomIp,
            String depotLat,
            String depotLong)
            throws IOException {
        Files.createDirectories(CONFIG_DIR);
        PROPERTIES.setProperty("server", server);
        PROPERTIES.setProperty("port", port);
        PROPERTIES.setProperty("database", database);
        PROPERTIES.setProperty("username", username);
        PROPERTIES.setProperty("password", password);

        PROPERTIES.setProperty("sqlServer", sqlServer);
        PROPERTIES.setProperty("sqlDatabase", sqlDatabase);
        PROPERTIES.setProperty("sqlUsername", sqlUsername);
        PROPERTIES.setProperty("sqlPassword", sqlPassword);

        PROPERTIES.setProperty("awsAccessKey", awsAccessKey);
        PROPERTIES.setProperty("awsSecretKey", awsSecretKey);
        PROPERTIES.setProperty("bucket", bucket);

        PROPERTIES.setProperty("vroomIp", vroomIp);
        PROPERTIES.setProperty("depotLat", depotLat);
        PROPERTIES.setProperty("depotLong", depotLong);

        try (OutputStream out = Files.newOutputStream(CONFIG_FILE)) {
            PROPERTIES.store(out, "ELP DELIVERIES SETTINGS");
        }
    }
    private static String get(
            String key) {

        return PROPERTIES.getProperty(key);
    }

    private static String get(
            String key,
            String defaultValue) {

        return PROPERTIES.getProperty(
                key,
                defaultValue);
    }

    public static String getServer() {
        return get("server");
    }

    public static String getPort() {
        return get("port");
    }

    public static String getDatabase() {
        return get("database");
    }

    public static String getUsername() {
        return get("username");
    }

    public static String getPassword() {
        return get("password");
    }

    public static String getAwsAccessKey() {
        return get("awsAccessKey", "");
    }

    public static String getAwsSecretKey() {
        return get("awsSecretKey", "");
    }

    public static String getBucket() {
        return get("bucket", "");
    }

    public static String getSqlServer() {
        return get("sqlServer", "");
    }

    public static String getSqlDatabase() {
        return get("sqlDatabase", "");
    }

    public static String getSqlUsername() {
        return get("sqlUsername", "");
    }

    public static String getSqlPassword() {
        return get("sqlPassword", "");
    }

    public static String getVroomIp() {
        return get("vroomIp", "");
    }

    public static String getDepotLat() {
        return get("depotLat", "");
    }

    public static String getDepotLong() {
        return get("depotLong", "");
    }
}