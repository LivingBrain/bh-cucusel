package com.brainhatchery.cucusel;

import com.brainhatchery.cucusel.utils.TestContext;
import com.brainhatchery.cucusel.utils.configuration.CucuselConfig;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.ITestContext;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;

import java.util.Map;

@CucumberOptions(
        features = {"target/test-classes/features"},
        glue = {"com.brainhatchery.cucusel.utils.hooks", "steps"},
        plugin = {"pretty", "summary", "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"})
public class CucuselRunner extends AbstractTestNGCucumberTests {

    private final String ENV_NAME = "env.name";

    @BeforeSuite(alwaysRun = true)
    public void setup(ITestContext testContext) {
        initializeConfiguration(testContext);
    }

    @DataProvider(parallel = true)
    @Override
    public Object[][] scenarios() {
        return super.scenarios();
    }

    private void initializeConfiguration(ITestContext testContext) {
        TestContext testContext1 = new TestContext();
        testContext1.getContextItem("dupa");
        Map<String, String> xmlSuiteParameters = testContext.getSuite().getXmlSuite().getParameters();
        String environment = xmlSuiteParameters.get(ENV_NAME) == null ?
                System.getProperty(ENV_NAME) : xmlSuiteParameters.get(ENV_NAME);
        CucuselConfig.setInstance(environment);
        System.setProperty("dataproviderthreadcount", String.valueOf(CucuselConfig.getThreadCount()));
    }
}
