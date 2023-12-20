package com.brainhatchery.cucusel.utils.driver;

import com.brainhatchery.cucusel.utils.configuration.CucuselConfig;
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
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

public class DriverFactory {
    private static final Logger logger = LogManager.getLogger();

    @SneakyThrows
    public static WebDriver createDriver(String gridUrl,
                                         BrowserTypes browserType,
                                         String[] driverArguments) {
        boolean isRemote = gridUrl != null;
//        System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

        switch (browserType) {
            case CHROME:
                ChromeOptions chromeOptions = (ChromeOptions) BrowserOptionsContext.getInstance();
                chromeOptions.addArguments(driverArguments);
                if (isRemote) {
                    return new RemoteWebDriver(new URL(gridUrl), chromeOptions, CucuselConfig.isEnableTracing());
                } else {
                    WebDriverManager.chromedriver().setup();
                    return new ChromeDriver(chromeOptions);
                }
            case FIREFOX:
                FirefoxOptions firefoxOptions = (FirefoxOptions) BrowserOptionsContext.getInstance();
                firefoxOptions.addArguments(driverArguments);
                if (isRemote) {
                    return new RemoteWebDriver(new URL(gridUrl), firefoxOptions, CucuselConfig.isEnableTracing());
                } else {
                    WebDriverManager.firefoxdriver().setup();
                    return new FirefoxDriver(firefoxOptions);
                }
            case EDGE:
                EdgeOptions edgeOptions = (EdgeOptions) BrowserOptionsContext.getInstance();
                edgeOptions.addArguments(driverArguments);
                if (isRemote) {
                    return new RemoteWebDriver(new URL(gridUrl), edgeOptions, CucuselConfig.isEnableTracing());
                } else {
                    WebDriverManager.edgedriver().setup();
                    return new EdgeDriver(edgeOptions);
                }
            default:
                throw new NotImplementedException("No matching browser found, provided: " + browserType.name());
        }
    }



}


