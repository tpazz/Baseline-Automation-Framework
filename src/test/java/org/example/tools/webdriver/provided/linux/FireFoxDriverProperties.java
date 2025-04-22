package org.example.tools.webdriver.provided.linux;

import net.thucydides.core.webdriver.DriverSource;
import org.example.tools.webdriver.setup.FireFoxDriverSetup;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.example.tools.webdriver.setup.FireFoxDriverSetup.detectedFirefoxBinary;

public class FireFoxDriverProperties implements DriverSource {
    private static final AtomicBoolean setupDone = new AtomicBoolean(false);
    @Override
    public WebDriver newDriver() {
        if (setupDone.compareAndSet(false, true)) {      // run exactly once
            try { FireFoxDriverSetup.main("Linux", new FirefoxOptions()); }
            catch (Exception e) { throw new RuntimeException("Unable to prepare Firefox driver", e); }
        }
        System.setProperty("webdriver.gecko.driver", "src/test/resources/webdriver/linux/geckodriver-linux64/geckodriver");
        FirefoxOptions firefoxOptions = new FirefoxOptions().addArguments("-private");
        if ("true".equalsIgnoreCase(System.getProperty("headless"))) firefoxOptions.addArguments("-headless");
        if (detectedFirefoxBinary != null) firefoxOptions.setBinary(detectedFirefoxBinary);
        firefoxOptions.setCapability(CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR, "ignore");
        return new FirefoxDriver(firefoxOptions);
    }

    @Override
    public boolean takesScreenshots() {
        return true;
    }
}
