package org.example.core.steps;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.example.core.base.PageObjectExtension;

public class TheInternet {

    org.example.core.pages.TheInternet theInternet;
    PageObjectExtension pageObjectExtension;

    @Given("I navigate to {string}")
    public void navigateToURL(String url) {
        theInternet.navigateToPage(url);
    }

    @Then("Verify the heading reads {string}")
    public void verifyHeadingText(String expected) {
        theInternet.verifyPage(expected);
        pageObjectExtension.endLogger();
    }

}
