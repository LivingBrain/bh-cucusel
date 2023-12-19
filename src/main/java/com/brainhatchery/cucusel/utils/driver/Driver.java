package com.brainhatchery.cucusel.utils.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.AbstractDriverOptions;

import java.util.function.Consumer;

public class Driver {

    private static final ThreadLocal<WebDriver> webDriverInstance = new ThreadLocal<>();

    public static void setInstance(String gridUrl,
                                   BrowserTypes browserType,
                                   String[] driverArguments,
                                   Consumer<AbstractDriverOptions<?>> driverCapabilitiesConsumer) {
        webDriverInstance.set(DriverFactory.createDriver(gridUrl, browserType, driverArguments, driverCapabilitiesConsumer));
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
