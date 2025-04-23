package org.example.tools.webdriver.provided.windows;

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
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        if (setupDone.compareAndSet(false, true)) {      // run exactly once
            try { FireFoxDriverSetup.main("Windows", firefoxOptions); }
            catch (Exception e) { throw new RuntimeException("Unable to prepare Firefox driver", e); }
        }
        if (detectedFirefoxBinary != null) firefoxOptions.setBinary(detectedFirefoxBinary);
        System.setProperty("webdriver.gecko.driver", "src/test/resources/webdriver/windows/geckodriver-win64/geckodriver.exe");
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
