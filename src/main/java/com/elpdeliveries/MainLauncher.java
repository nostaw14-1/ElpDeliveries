package com.elpdeliveries;

import com.elpdeliveries.util.ErrorHandler;

public class MainLauncher {
    void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((_, throwable) -> ErrorHandler.showError("Unhandled Exception", throwable));
        ElpDeliveries.main(args);
    }
}
