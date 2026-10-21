package com.elpdeliveries.util;

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

    public static void save(
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
            String depotLong) throws Exception {

        Properties props = new Properties();

        props.setProperty("server", server);
        props.setProperty("port", port);
        props.setProperty("database", database);
        props.setProperty("username", username);
        props.setProperty("password", password);

        props.setProperty("sqlServer", sqlServer);
        props.setProperty("sqlDatabase", sqlDatabase);
        props.setProperty("sqlUsername", sqlUsername);
        props.setProperty("sqlPassword", sqlPassword);

        props.setProperty("awsAccessKey", awsAccessKey);
        props.setProperty("awsSecretKey", awsSecretKey);
        props.setProperty("bucket", bucket);

        props.setProperty("vroomIp", vroomIp);
        props.setProperty("depotLat", depotLat);
        props.setProperty("depotLong", depotLong);

        try (FileOutputStream fos = new FileOutputStream(FILE_NAME)) {

            props.store(
                    fos,
                    "ELP DELIVERIES SETTINGS");
        }
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

    public static String getAwsAccessKey() throws Exception {
        return load().getProperty("awsAccessKey");
    }

    public static String getAwsSecretKey() throws Exception {
        return load().getProperty("awsSecretKey");
    }

    public static String getBucket() throws Exception {
        return load().getProperty("bucket");
    }

    public static String getSqlServer()
            throws Exception {

        return load().getProperty(
                "sqlServer",
                "");
    }

    public static String getSqlDatabase()
            throws Exception {

        return load().getProperty(
                "sqlDatabase",
                "");
    }

    public static String getSqlUsername()
            throws Exception {

        return load().getProperty(
                "sqlUsername",
                "");
    }

    public static String getSqlPassword()
            throws Exception {

        return load().getProperty(
                "sqlPassword",
                "");
    }

    public static String getVroomIp() throws Exception {
        return load().getProperty("vroomIp");
    }

    public static String getDepotLat() throws Exception {
        return load().getProperty("depotLat");
    }

    public static String getDepotLong() throws Exception {
        return load().getProperty("depotLong");
    }

}
