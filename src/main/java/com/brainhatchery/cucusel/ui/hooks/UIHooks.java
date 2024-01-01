package com.brainhatchery.cucusel.ui.hooks;

import com.brainhatchery.cucusel.configuration.CucuselConfig;
import com.brainhatchery.cucusel.ui.driver.BrowserOptionsContext;
import com.brainhatchery.cucusel.ui.driver.Driver;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UIHooks {

    @Before(value = "@UI", order = 0)
    public void browserOptionsContextInit() {
        BrowserOptionsContext.setInstance(CucuselConfig.getBrowserType());
    }

    @Before("@UI")
    public void setupBrowser() {
        Driver.setInstance(
                CucuselConfig.getGridUrl(),
                CucuselConfig.getBrowserType(),
                CucuselConfig.getBrowserArguments());
    }

    @After("@UI")
    public void afterTest(Scenario scenario) {
        String timestamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());

        if (scenario.isFailed()) {
            String filename = String.format("%s_%s.jpg", scenario.getName(), timestamp);
            Allure.addAttachment(filename, new ByteArrayInputStream(((TakesScreenshot) Driver.getInstance()).getScreenshotAs(OutputType.BYTES)));
        }

        Driver.clearInstance();
    }
}
