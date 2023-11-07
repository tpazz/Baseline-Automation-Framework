package org.example.core.steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.example.core.base.PageObjectExtension;
import org.example.tools.elementparser.ElementParser;

public class Tools {

    PageObjectExtension pageObjectExtension;

    @Given("I am on a webpage {string}")
    public void iAmOnAWebpage(String webpage) {
        pageObjectExtension.navigateTo(webpage);
    }

    @Then("Initiate logging")
    public void initiateTests() {
        pageObjectExtension.startLogger();
    }
}
