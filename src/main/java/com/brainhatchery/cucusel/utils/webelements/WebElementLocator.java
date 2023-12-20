package com.brainhatchery.cucusel.utils.webelements;

import com.brainhatchery.cucusel.utils.webelements.enums.Locator;

public class WebElementLocator extends CucuselWebElement<WebElementLocator> {
    public WebElementLocator(Locator kind, String value) {
        super(kind, value);
    }
}
