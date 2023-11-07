package org.example.core.steps;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.example.core.base.PageObjectExtension;
import org.openqa.selenium.By;

import java.util.List;

import static org.example.core.base.PageObjectExtension.Action.*;

public class ActionSteps extends PageObjectExtension {

    @Given("I am on the {string} page")
    public void iAmOnTheXPage(String page) {
        navigateTo(page);
        waitForPageLoadedJS();
    }

    @And("I select {string}")
    public void iSelectXText(String text, List<String> data) {
        String input = data.get(1);
        switch (data.get(0)) {
            case "text" : locator = xPathBuilder(data.get(1), "text()", text); break;
            case "id" : locator = By.id(input); break;
            case "xpath" : locator = By.xpath(input); break;
            case "cssSelector" : locator = By.cssSelector(input); break;
            case "tagName" : locator = By.tagName(input); break;
            case "className": locator = By.className(input); break;
            case "linkText" : locator = By.linkText(input); break;
            case "partialLinkText" : locator = By.partialLinkText(input); break;
        }
        element = generateElement(locator);
        action(CLICK, element);
    }

    @And("I enter {string} in the active element")
    public void iEnterXInTheActiveElement(String text) {
        threadWait(1000);
        element = getDriver().switchTo().activeElement();
        action(ENTER_TEXT, element,text);
        action(ENTER_KEY, element, "ENTER");
    }

    @When("I select {string} button")
    public void iSelectXButton(String button) {
        locator = By.xpath("//button[contains(.,'" + button + "')]");
        element = generateElement(locator);
        action(CLICK, element);
    }

    @Then("verify the following text is displayed on the page")
    public void verifyTheFollowingTextIsDisplayedOnThePage(List<String> data) {
        String expected = data.get(1);
        actual = expected;
        locator = By.xpath("//" + data.get(0) + "[text()=\"" + expected + "\"]");
        try { generateElement(locator); }
        catch (Exception e) { actual = e.toString(); }
        verify(expected, actual);
    }

    @Then("verify a partial match of the following text is displayed on the page")
    public void verifyPartialMatchText(List<String> data) {
        String expected = data.get(1);
        actual = expected;
        locator = By.xpath("//" + data.get(0) + "[contains(text(),'" + expected + "')]");
        try { generateElement(locator); }
        catch (Exception e) { actual = e.toString(); }
        verify(expected, actual);
    }

    @And("I enter {string} in the {string} field")
    public void iEnterXInTheYField(String text, String field, List<String> data) {
        String input = data.get(1);
        switch (data.get(0)) {
            case "id" : locator = By.id(input); break;
            case "xpath" : locator = By.xpath(input); break;
            case "cssSelector" : locator = By.cssSelector(input); break;
            case "tagName" : locator = By.tagName(input); break;
            case "className": locator = By.className(input); break;
            case "linkText" : locator = By.linkText(input); break;
            case "partialLinkText" : locator = By.partialLinkText(input); break;
        }
        element = generateElement(locator);
        action(Action.ENTER_TEXT, element, text);
    }

    @And("I select {string} from the {string} dropdown")
    public void iSelectXFromTheDropdown(String dropdownOption, String dropdown, List<String> data) {
        String input = data.get(1);
        switch (data.get(0)) {
            case "id" : locator = By.id(input); break;
            case "xpath" : locator = By.xpath(input); break;
            case "cssSelector" : locator = By.cssSelector(input); break;
            case "tagName" : locator = By.tagName(input); break;
            case "className": locator = By.className(input); break;
            case "linkText" : locator = By.linkText(input); break;
            case "partialLinkText" : locator = By.partialLinkText(input); break;
        }
        element = generateElement(locator);
        action(Action.SELECT_FROM_DROPDOWN, element, dropdownOption);
    }

}
