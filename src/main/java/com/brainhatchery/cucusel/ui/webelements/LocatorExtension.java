package com.brainhatchery.cucusel.ui.webelements;

import com.brainhatchery.cucusel.ui.webelements.enums.Locator;
import org.openqa.selenium.By;

public class LocatorExtension {
    protected static By toBy(Locator kind, String value) {
        By by;
        switch (kind) {
            case ID:
                by = By.id(value);
                break;
            case CLASS_NAME:
                by = By.className(value);
                break;
            case CSS_SELECTOR:
                by = By.cssSelector(value);
                break;
            case LINK_TEXT:
                by = By.linkText(value);
                break;
            case PARTIAL_LINK_TEXT:
                by = By.partialLinkText(value);
                break;
            case TAG_NAME:
                by = By.tagName(value);
                break;
            case XPATH:
                by = By.xpath(value);
                break;
            case NAME:
                by = By.name(value);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + value);
        }
        return by;
    }
}
