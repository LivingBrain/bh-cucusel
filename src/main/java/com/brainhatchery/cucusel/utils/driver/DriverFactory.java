package com.brainhatchery.cucusel.utils.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.SneakyThrows;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.function.Consumer;

public class DriverFactory {

    private static final Logger logger = LogManager.getLogger();

    @SneakyThrows
    public static WebDriver createDriver(String gridUrl,
                                         BrowserTypes browserType,
                                         String[] driverArguments,
                                         Consumer<AbstractDriverOptions<?>> driverCapabilitiesConsumer) {
        boolean isRemote = gridUrl != null;
        AbstractDriverOptions<?> driverOptions;
//        System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

        switch (browserType) {
            case CHROME:
                ChromeOptions chromeOptions = new ChromeOptions();
                driverOptions = chromeOptions.addArguments(driverArguments);
                driverCapabilitiesConsumer.accept(chromeOptions);
                if (isRemote) {
                    return new RemoteWebDriver(new URL(gridUrl), driverOptions, false); //TODO get tracing status from configuration
                } else {
                    WebDriverManager.chromedriver().setup();
                    return new ChromeDriver(chromeOptions);
                }
            case FIRE_FOX:
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                driverOptions = firefoxOptions.addArguments(driverArguments);
                driverCapabilitiesConsumer.accept(firefoxOptions);
                if (isRemote) {
                    return new RemoteWebDriver(new URL(gridUrl), driverOptions, false); //TODO get tracing status from configuration
                } else {
                    WebDriverManager.firefoxdriver().setup();
                    return new FirefoxDriver(firefoxOptions);
                }
            case EDGE:
                EdgeOptions edgeOptions = new EdgeOptions();
                driverOptions = edgeOptions.addArguments(driverArguments);
                driverCapabilitiesConsumer.accept(edgeOptions);
                if (isRemote) {
                    return new RemoteWebDriver(new URL(gridUrl), driverOptions, false); //TODO get tracing status from configuration
                } else {
                    WebDriverManager.edgedriver().setup();
                    return new EdgeDriver(edgeOptions);
                }
            default: throw new NotImplementedException(String.format("No matching browser found for provided: %s", browserType.name()));
        }

    }



}


