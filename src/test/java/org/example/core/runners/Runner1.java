package org.example.core.runners;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        features="src/test/resources/features/",
        glue="org.example",
        plugin = {
                "pretty",
                "json:target/cucumber-reports/json/Cucumber.json",
                "junit:target/cucumber-reports/junit/Cucumber.xml",
                "html:target/cucumber-reports/html"
        },
        tags = "@GSP")
public class Runner1 {}
