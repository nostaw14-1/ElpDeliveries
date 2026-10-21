package com.elpdeliveries.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AppLogger {
    private AppLogger() {}

    private static final Logger LOGGER = Logger.getLogger("ELPDeliveries");

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
