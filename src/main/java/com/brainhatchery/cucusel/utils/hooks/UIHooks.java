package com.brainhatchery.cucusel.utils.hooks;

import com.brainhatchery.cucusel.utils.configuration.CucuselConfig;
import com.brainhatchery.cucusel.utils.driver.BrowserOptionsContext;
import com.brainhatchery.cucusel.utils.driver.Driver;
import io.cucumber.java.Before;

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
}
