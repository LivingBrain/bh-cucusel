package com.brainhatchery.cucusel.ui.driver;

import org.apache.commons.lang3.NotImplementedException;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;

public class BrowserOptionsContext {
    private static final ThreadLocal<AbstractDriverOptions<?>> driverOptionsInstance = new ThreadLocal<>();

    public static synchronized  void setInstance(BrowserTypes browserType) {
        switch (browserType) {
            case CHROME:
                driverOptionsInstance.set(new ChromeOptions());
                break;
            case FIREFOX:
                driverOptionsInstance.set(new FirefoxOptions());
                break;
            case EDGE:
                driverOptionsInstance.set(new EdgeOptions());
                break;
            default:
                throw new NotImplementedException("No matching browser found, provided: " + browserType.name());
        }
    }

    public static synchronized AbstractDriverOptions<?> getInstance() {
        if (driverOptionsInstance.get() != null) {
            return driverOptionsInstance.get();
        } else {
            throw new IllegalStateException("Driver Options context was not initialized.");
        }
    }
}
