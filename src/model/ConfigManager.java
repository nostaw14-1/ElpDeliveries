package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class ConfigManager {
    private static final String FILE_NAME = "config.properties";

    public static boolean exists() {
        return new File(FILE_NAME).exists();
    }

    public static Properties load() throws Exception {
        Properties props = new Properties();
        props.load(new FileInputStream(FILE_NAME));
        return props;
    }

    public static void save(String server, String port, String database, String username, String password) throws Exception {
        Properties props = new Properties();
        props.setProperty("server", server);
        props.setProperty("port", port);
        props.setProperty("database", database);
        props.setProperty("username", username);
        props.setProperty("password", password);

        props.store(new FileOutputStream(FILE_NAME), "ELP DELIVERIES SETTINGS");
    }

    public static String getServer() throws Exception {
        return load().getProperty("server");
    }

    public static String getPort() throws Exception {
        return load().getProperty("port");
    }

    public static String getDatabase() throws Exception {
        return load().getProperty("database");
    }

    public static String getUsername() throws Exception {
        return load().getProperty("username");
    }
    public static String getPassword() throws Exception {
        return load().getProperty("password");
    }
}
