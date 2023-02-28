package org.example.core.steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.example.core.base.PageObjectExtension;
import org.example.tools.elementparser.ElementParser;
import org.example.tools.webdriver.Setup;

public class Tools {

    PageObjectExtension pageObjectExtension;
    ElementParser elementParser;
    Setup setup;

    @Given("I am on a webpage {string}")
    public void iAmOnAWebpage(String webpage) {
        pageObjectExtension.navigateTo(webpage);
    }

    @Then("output a list of all the interactable elements in {string} syntax")
    public void outputAListOfAllTheInteractableElementsInSerenitySyntax(String syntax) {
        elementParser.outputFindElementBy(syntax.equalsIgnoreCase("Serenity"));
    }

    @Given("I am using ChromeDriver")
    public void updateChromeDriver() {}

    @Then("ensure that it is compatible with my Chrome Browser version")
    public void setupChromeDriver() {
        if (!setup.compareBrowserDriverVersion()) {
            setup.downloadChromeDriverWithDriver();
            setup.closeAllChromeDrivers();
            setup.installChromeDriver();
        }
    }

    @Given("my ChromeDriver is up to date")
    public void myChromeDriverIsUpToDate() {
        setupChromeDriver();
    }

    @Then("Initiate logging")
    public void initiateTests() {
        pageObjectExtension.startLogger();
    }
}
