package com.brainhatchery.cucusel.ui.webelements;

import com.brainhatchery.cucusel.configuration.CucuselConfig;
import com.brainhatchery.cucusel.ui.driver.Driver;
import com.brainhatchery.cucusel.ui.webelements.enums.Locator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v109.network.Network;
import org.openqa.selenium.devtools.v109.network.model.RequestWillBeSentExtraInfo;
import org.openqa.selenium.devtools.v109.network.model.ResponseReceived;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Extend with this calss for any custom web element classes
 *
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
     *
     * @param args - Array of string parameters values
     * @return - parent class so method chaining may be used.
     */
    public T parameters(Object... args) {
        this.value = String.format(this.value, args);
        return (T) this;
    }

    /**
     * Methond will return Selenium WebElement
     *
     * @return WebElement
     */
    public WebElement getWebElement() {
        return driver.findElement(by);
    }

    /**
     * Method will return collection of Selenium WebElements
     *
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
        this.explicitTimeout = explicitTimeout;
        wait.withTimeout(Duration.ofSeconds(explicitTimeout));
        return (T) this;
    }

    public RequestWillBeSentExtraInfo clickAndInterceptRequest(String requestMethod) {
        final List<RequestWillBeSentExtraInfo> savedRequest = new ArrayList<>();
        WebDriver webDriver = new Augmenter().augment(driver);

        DevTools devTools = ((HasDevTools) webDriver).getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
        devTools.addListener(Network.requestWillBeSentExtraInfo(), request -> {
            if (request.getHeaders().toJson().get(":method").equals(requestMethod)) {
                savedRequest.add(request);
            }
        });
        driver.findElement(by).click();
        try {
            Awaitility.await().pollThread(Thread::new).atMost(explicitTimeout, TimeUnit.SECONDS).until(() -> savedRequest.size() >= 1);
        } catch (ConditionTimeoutException e) {
            throw new ConditionTimeoutException(String.format("No matching request found. Expected request method: %s", requestMethod));
        }
        devTools.send(Network.disable());
        devTools.clearListeners();
        devTools.close();
        return savedRequest.get(0);
    }


    public String clickAndWaitForResponse(String endpointKey, int statusCode) {
        final List<ResponseReceived> savedResponses = new ArrayList<>();
        final String[] responseBody = new String[1];
        final Integer[] responseStatusCode = new Integer[1];
        WebDriver webDriver = new Augmenter().augment(driver);

        DevTools devTools = ((HasDevTools) webDriver).getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
        devTools.addListener(Network.responseReceived(), received -> {
            if (received.getResponse().getUrl().contains(endpointKey)) {
                responseBody[0] = devTools.send(Network.getResponseBody(received.getRequestId())).getBody();
                responseStatusCode[0] = received.getResponse().getStatus();
                if (responseStatusCode[0] == statusCode) {
                    savedResponses.add(received);
                }
            }
        });
        driver.findElement(by).click();
        try {
            Awaitility.await().pollThread(Thread::new).atMost(explicitTimeout, TimeUnit.SECONDS).until(() -> savedResponses.size() >= 1);
        } catch (ConditionTimeoutException e) {
            throw new ConditionTimeoutException(String.format("Expected status code: %s. Actual status code: %s. With body: %s",
                    statusCode, responseStatusCode[0], responseBody[0]));
        }
        devTools.send(Network.disable());
        devTools.clearListeners();
        devTools.close();
        return responseBody[0];
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
