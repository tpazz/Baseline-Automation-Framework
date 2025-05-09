package org.example.core.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.*;
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("/features")
@ConfigurationParameter(
        key = GLUE_PROPERTY_NAME,
        value = "org.example")
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = "io.cucumber.core.plugin.SerenityReporterParallel," +
                "pretty," +
                "timeline:build/test-results/timeline_Runner1," +
                "json:target/cucumber-reports/json/Runner1.json," +
                "junit:target/cucumber-reports/junit/Runner1.xml," +
                "html:target/cucumber-reports/html_Runner1")
@ConfigurationParameter(
        key = FILTER_TAGS_PROPERTY_NAME,
        value = "@GSP")
public class Runner1 {}
