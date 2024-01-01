package com.brainhatchery.cucusel.ui.driver;

import org.openqa.selenium.WebDriver;

public class Driver {

    private static final ThreadLocal<WebDriver> webDriverInstance = new ThreadLocal<>();

    public static void setInstance(String gridUrl,
                                   BrowserTypes browserType,
                                   String[] driverArguments) {
        webDriverInstance.set(DriverFactory.createDriver(gridUrl, browserType, driverArguments));
    }

    public static WebDriver getInstance() {
        return webDriverInstance.get();
    }

    public static void clearInstance() {
        if (webDriverInstance.get() != null) {
            webDriverInstance.get().quit();
            webDriverInstance.remove();
        }
    }
}
