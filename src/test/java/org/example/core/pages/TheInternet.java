package org.example.core.pages;

import org.example.core.base.PageObjectExtension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import static org.example.core.base.PageObjectExtension.Action.*;

public class TheInternet extends PageObjectExtension {

    @FindBy(xpath = "//h1")
    WebElement heading;

    public void navigateToPage(String url) {
        getDriver().get(url);
        waitForPageLoadedJS();
    }

    public void verifyPage(String expected) {
        actual = action(GET_TEXT, heading);
        verify(expected, actual);
    }

}
