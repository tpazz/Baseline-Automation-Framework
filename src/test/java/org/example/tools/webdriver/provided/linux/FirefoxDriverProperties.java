package org.example.tools.webdriver.provided.linux;

import net.thucydides.core.webdriver.DriverSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.core.base.PageObjectExtension;
import org.example.tools.webdriver.setup.FirefoxDriverSetup;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.example.tools.webdriver.setup.FirefoxDriverSetup.detectedFirefoxBinary;

public class FirefoxDriverProperties implements DriverSource {
    private static final AtomicBoolean setupDone = new AtomicBoolean(false);
    public static Logger logger = LogManager.getLogger(PageObjectExtension.class);
    @Override
    public WebDriver newDriver() {
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        if (setupDone.compareAndSet(false, true)) {      // run exactly once
            try { FirefoxDriverSetup.main("Linux", firefoxOptions); }
            catch (Exception e) { logger.error("Failed to prepare Geckodriver!"); }
        }
        if (detectedFirefoxBinary != null) firefoxOptions.setBinary(detectedFirefoxBinary);
        System.setProperty("webdriver.gecko.driver", "src/test/resources/webdriver/linux/geckodriver-linux64/geckodriver");
        firefoxOptions.setCapability(CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR, "ignore");
        firefoxOptions.addArguments("-private");
        if ("true".equalsIgnoreCase(System.getProperty("headless"))) firefoxOptions.addArguments("-headless");
        return new FirefoxDriver(firefoxOptions);
    }

    @Override
    public boolean takesScreenshots() {
        return true;
    }
}
