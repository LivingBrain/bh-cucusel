package com.brainhatchery.cucusel.utils.listeners;

import com.brainhatchery.cucusel.utils.configuration.CucuselConfig;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class Retry implements IRetryAnalyzer {
    private static final Logger logger = LogManager.getLogger();
    private int retryCount = 0;

    @SneakyThrows
    @Override
    public boolean retry(ITestResult iTestResult) {
        int maxRetryCount = CucuselConfig.getRetryCount();

        if (retryCount < maxRetryCount) {
            logger.info(">>> Retrying test with status "
                    + getResultsStatusName(iTestResult.getStatus())
                    + " for the " + (retryCount + 1) + " time(s). <<<");
            retryCount++;
            return true;
        }
        return false;
    }

    public String getResultsStatusName(int status) {
        String resultName = null;
        switch (status) {
            case 1:
                resultName = "SUCCESS";
                break;
            case 2:
                resultName = "FAILURE";
                break;
            case 3:
                resultName = "SKIP";
                break;
        }
        return resultName;
    }
}
