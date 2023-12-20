package com.brainhatchery.cucusel.utils.webelements;

import com.brainhatchery.cucusel.utils.configuration.CucuselConfig;
import com.brainhatchery.cucusel.utils.driver.Driver;
import com.brainhatchery.cucusel.utils.webelements.enums.Locator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.util.List;


/**
 * Extend with this calss for any custom web element classes
 * @param <T> - custom class name
 */
@Getter
@Setter
@SuppressWarnings("unchecked")
public abstract class CucuselWebElement<T extends CucuselWebElement<T>> {
    private static final Logger logger = LogManager.getLogger();
    private FluentWait<WebDriver> wait;
    private Locator kind;
    private String value;
    private long explicitTimeout;
    @Setter(AccessLevel.NONE)
    protected WebDriver driver;
    @Setter(AccessLevel.NONE)
    protected By by;

    public CucuselWebElement(Locator kind, String value) {
        this.kind = kind;
        this.value = value;
        by = LocatorExtension.toBy(kind, value);
        explicitTimeout = CucuselConfig.getExplicitTimeout();
        driver = Driver.getInstance();
        wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(explicitTimeout))
                .pollingEvery(Duration.ofMillis(CucuselConfig.getPolling()));
    }

    /**
     * Use this method to provide string parameters
     * @param args - Array of string parameters values
     * @return - parent class so method chaining may be used.
     */
    public T parameters(Object... args) {
        this.value = String.format(this.value, args);
        return (T) this;
    }

    /**
     * Methond will return Selenium WebElement
     * @return  WebElement
     */
    public WebElement getWebElement() {
        return driver.findElement(by);
    }

    /**
     * Method will return collection of Selenium WebElements
     * @return List<WebElement>
     */
    public List<WebElement> getWebElements() {
        return driver.findElements(by);
    }

    public void click() {
        waitForClickableWebElement().click();
    }

    public T sendKeys(String text) {
        waitForVisibleWebElement().sendKeys(text);
        return (T) this;
    }

    public T getInputText() {
        waitForPresenceWebElement().getAttribute("value");
        return (T) this;
    }

    public T getText() {
        waitForVisibleWebElement().getText();
        return (T) this;
    }

    public T getAttribute(String attribute) {
        waitForVisibleWebElement().getAttribute(attribute);
        return (T) this;
    }

    public boolean isWebElementVisible() {
        logger.info("Checking web element visibility.");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isWebElementInvisible() {
        logger.info("Checking web element visibility.");
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public T setElementTimeout(long explicitTimeout) {
        wait.withTimeout(Duration.ofSeconds(explicitTimeout));
        return (T) this;
    }

    private WebElement waitForClickableWebElement() {
        return wait
                .withMessage(String.format("Web element: %s is not clickable after: %s seconds.", by, explicitTimeout))
                .until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(by)));
    }

    private WebElement waitForVisibleWebElement() {
        return wait
                .withMessage(String.format("Web element: %s is not visible after: %s seconds.", by, explicitTimeout))
                .until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(by)));
    }

    private WebElement waitForPresenceWebElement() {
        return wait
                .withMessage(String.format("Web element: %s is not present after: %s seconds.", by, explicitTimeout))
                .until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(by)));
    }
}
