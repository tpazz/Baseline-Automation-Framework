package org.example.core.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.en.And;
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
    public void iSelectXText(String text, DataTable table) {
        List<List<String>> raw = table.asLists();
        List<String> data = raw.get(0); // first row
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
    public void iEnterXInTheYField(String text, String field, DataTable table) {
        List<List<String>> raw = table.asLists();
        List<String> data = raw.get(0); // first row
        element = buildElement(text, data.get(0), data.get(1));
        action(ENTER_TEXT, element, text);
    }

    @And("I select {string} from the {string} dropdown")
    public void iSelectXFromTheDropdown(String dropdownOption, String dropdown, DataTable table) {
        List<List<String>> raw = table.asLists();
        List<String> data = raw.get(0); // first row
        element = buildElement(dropdownOption, data.get(0), data.get(1));
        action(SELECT_FROM_DROPDOWN, element, dropdownOption);
    }

    @And("I wait for the element")
    public void iWaitForTheElement(DataTable table) {
        List<List<String>> raw = table.asLists();
        List<String> data = raw.get(0); // first row
        String locator = data.get(0);
        String value = data.get(1);
        int element_number = Integer.parseInt(data.get(2));
        int sec = Integer.parseInt(data.get(3));
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
    public void verify(String arg0, DataTable table) {
        List<List<String>> raw = table.asLists();
        List<String> data = raw.get(0); // first row
        String expected = arg0;
        locator = By.xpath("//" + data.get(0) + "[text()=\"" + expected + "\"]");
        elements = generateElements(locator);
        verify(data.get(1), String.valueOf(elements.size()));
    }

    @Then("verify partial {string}")
    public void verifyPartial(String arg0, DataTable table) {
        List<List<String>> raw = table.asLists();
        List<String> data = raw.get(0); // first row
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
    public void verifyElementExists(DataTable table) {
        List<List<String>> raw = table.asLists();
        List<String> data = raw.get(0); // first row
        locator = xPathBuilder(data.get(0), data.get(1), data.get(2));
        elements = generateElements(locator);
        verify(String.valueOf(data.get(3)), String.valueOf(elements.size()));
    }
}