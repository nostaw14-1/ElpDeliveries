package com.elpdeliveries.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class AppLogger {
    private AppLogger() {}

    private static final Logger LOGGER = Logger.getLogger("ELPDeliveries");

    static {
        try {
            Path logDir = ConfigManager.getConfigDirectory().resolve("logs");
            Files.createDirectories(logDir);
            FileHandler fileHandler = new FileHandler(logDir.resolve("elp-%g.log").toString(),5_000_000, 5, true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setUseParentHandlers(true);
        } catch (Exception e) {
            error(e.getMessage(), e);
        }
    }

    public static void info(String message) {
        LOGGER.info(message);
    }

    public static void warning(String message) {
        LOGGER.warning(message);
    }

    public static void error(String message, Throwable throwable) {
        LOGGER.log(Level.SEVERE, message, throwable);
    }
}
