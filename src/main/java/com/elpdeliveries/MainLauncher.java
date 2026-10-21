package com.elpdeliveries;

import com.elpdeliveries.util.AppLogger;

public class MainLauncher {
    void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((_, throwable) -> {
            AppLogger.error("Unhandled Exception", throwable);
        });
        ElpDeliveries.main(args);
    }
}
