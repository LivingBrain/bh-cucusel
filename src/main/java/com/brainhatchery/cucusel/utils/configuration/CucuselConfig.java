package com.brainhatchery.cucusel.utils.configuration;

import com.brainhatchery.cucusel.utils.driver.BrowserTypes;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class CucuselConfig {
    private static final String BROWSER_TYPE = "browser.type";
    private static final String BROWSER_ARGUMENTS = "browser.arguments";
    private static final String BROWSER_ARGUMENTS_SLIT_CHAR = ";";
    private static final String GRID_URL = "grid.url";
    private static final String RETRY_COUNT = "retry.count";
    private static final String THREAD_COUNT = "thread.count";
    private static Properties propertiesInstance = null;

    private static final Logger logger = LogManager.getLogger();


    @SneakyThrows
    public static void setInstance(String environment) {
        logger.info("Environment: " + environment);
        propertiesInstance = new Properties();
        String filePath = String.format("config.%s.properties", environment);
        logger.info("Loading environment properties from: " + filePath);
        InputStream fileAsInputStream = CucuselConfig.class.getClassLoader().getResourceAsStream(filePath);
        InputStream inputStream = Objects.requireNonNull(fileAsInputStream,
                String.format("Environment configuration properties with name: %s not found!", filePath));
        propertiesInstance.load(inputStream);
    }

    private static synchronized Properties getInstance() {
        if (propertiesInstance != null) {
            return propertiesInstance;
        } else {
            throw new IllegalStateException("Project properties are not initialized.");
        }
    }

    public static BrowserTypes getBrowserType() {
        return BrowserTypes.valueOf(getValueByKeyFromSystemPropertyOrProperties(BROWSER_TYPE));
    }

    public static String[] getBrowserArguments() {
        return getValueByKeyFromSystemPropertyOrProperties(BROWSER_ARGUMENTS).split(BROWSER_ARGUMENTS_SLIT_CHAR);
    }

    public static String getGridUrl() {
        return getValueByKeyFromSystemPropertyOrProperties(GRID_URL);
    }

    public static String getRetryCount() {
        return getValueByKeyFromSystemPropertyOrProperties(RETRY_COUNT);
    }

    public static int getThreadCount() {
        String threads = getValueByKeyFromSystemPropertyOrProperties(THREAD_COUNT);
        return Integer.parseInt(threads == null ? "1" : threads);
    }

    private static String getValueByKeyFromSystemPropertyOrProperties(String key) {
        String systemProperty = System.getProperty(key);
        return systemProperty == null ? getInstance().getProperty(key) : systemProperty;
    }
}
