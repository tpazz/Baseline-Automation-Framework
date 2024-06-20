package org.example.core.steps;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.example.core.base.PageObjectExtension;
import org.openqa.selenium.By;

import java.util.List;

import static org.example.core.base.PageObjectExtension.Action.*;

public class GSP_Steps extends PageObjectExtension {

    @Given("I navigate to {string}")
    public void navigateToURL(String url) {
        navigateTo(url);
    }

    @And("I select {string}")
    public void iSelectXText(String text, List<String> data) {
        element = buildElement(text, data.get(0), data.get(1));
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

    @And("I enter {string} in the {string} field")
    public void iEnterXInTheYField(String text, String field, List<String> data) {
        element = buildElement(text, data.get(0), data.get(1));
        action(ENTER_TEXT, element, text);
    }

    @And("I select {string} from the {string} dropdown")
    public void iSelectXFromTheDropdown(String dropdownOption, String dropdown, List<String> data) {
        element = buildElement(dropdownOption, data.get(0), data.get(1));
        action(SELECT_FROM_DROPDOWN, element, dropdownOption);
    }

    @And("I wait for the element")
    public void iWaitForTheElement(List<String> args) {
        String locator = args.get(0);
        String value = args.get(1);
        int element_number = Integer.parseInt(args.get(2));
        int sec = Integer.parseInt(args.get(3));
        for (int i = 0; i < sec; i++) {
            elements = buildElements(null,locator, value);
            if (elements.size() == element_number)
                break;
            threadWait(1000);
        }
    }

    @And("I enter {string} in the JavaScript alert prompt")
    public void iEnterInTheJavaScriptAlertPrompt(String arg0) {
        enterTextJSAlert(arg0);
    }

    @And("I confirm the JavaScript alert")
    public void iConfirmTheJavaScriptAlert() {
        acceptJSAlert();
    }

    @And("I cancel the JavaScript alert")
    public void iCancelTheJavaScriptAlert() {
        dismissJSAlert();
    }

    @Then("verify {string}")
    public void verify(String arg0, List<String> data) {
        String expected = arg0;
        locator = By.xpath("//" + data.get(0) + "[text()=\"" + expected + "\"]");
        elements = generateElements(locator);
        verify(data.get(1), String.valueOf(elements.size()));
    }

    @Then("verify partial {string}")
    public void verifyPartial(String arg0, List<String> data) {
        String expected = arg0;
        locator = By.xpath("//" + data.get(0) + "[contains(text(),'" + expected + "')]");
        elements = generateElements(locator);
        verify(data.get(1), String.valueOf(elements.size()));
    }

    @And("I wait for {string} seconds")
    public void iWaitForSeconds(String arg0) {
        threadWait(Integer.parseInt(arg0)*1000);
    }

    @And("verify element exists")
    public void verifyElementExists(List<String> args) {
        locator = xPathBuilder(args.get(0), args.get(1), args.get(2));
        elements = generateElements(locator);
        verify(String.valueOf(args.get(3)), String.valueOf(elements.size()));
    }
}