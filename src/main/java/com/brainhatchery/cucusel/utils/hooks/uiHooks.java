package com.brainhatchery.cucusel.utils.hooks;

import com.brainhatchery.cucusel.utils.configuration.CucuselConfig;
import com.brainhatchery.cucusel.utils.driver.Driver;
import io.cucumber.java.Before;

public class uiHooks {

    @Before("@UI")
    public void setupBrowser() {
        Driver.setInstance(
                CucuselConfig.getGridUrl(),
                CucuselConfig.getBrowserType(),
                CucuselConfig.getBrowserArguments(),
                (driverOptions) -> {});
    }
}
