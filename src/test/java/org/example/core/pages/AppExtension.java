package org.example.core.pages;

/*
    Add your own app specific global methods in this class and extend Page Object classes from it
 */

import org.example.core.base.PageObjectExtension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.example.core.base.PageObjectExtension.Action.GET_TEXT;

public class AppExtension extends PageObjectExtension {

    // OutSystems Feedback Message //

    @FindBy(className = "feedback-message-text")
    WebElement feedbackMessageText;

    @FindBy(className = "feedback-message-success")
    WebElement feedbackMessageSuccess;

    @FindBy(className = "feedback-message-error")
    WebElement feedbackMessageError;

    public void verifyFeedbackMessageText(String text) {
        verify(text, action(GET_TEXT, feedbackMessageText));
    }

    public void verifyFeedbackMessageType(String type) {
        switch (type) {
            case "success" : { verifyElementDisplayed(feedbackMessageSuccess,true); break; }
            case "error" : { verifyElementDisplayed(feedbackMessageError, true); break; }
        }
    }

    // Some Loading icon... //

}
