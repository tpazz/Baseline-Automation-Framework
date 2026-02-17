package org.example.tools.webdriver.provided.linux;

import net.thucydides.core.webdriver.DriverSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.core.base.PageObjectExtension;
import org.example.tools.webdriver.setup.ChromeDriverSetup;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.example.tools.webdriver.setup.ChromeDriverSetup.detectedChromeBinary;

public class ChromeDriverProperties implements DriverSource {
    private static final AtomicBoolean setupDone = new AtomicBoolean(false);
    public static Logger logger = LogManager.getLogger(PageObjectExtension.class);
    @Override
    public WebDriver newDriver() {
        ChromeOptions chromeOptions = new ChromeOptions();
        if (setupDone.compareAndSet(false, true)) {
            try { ChromeDriverSetup.main("Linux", chromeOptions); }
            catch (Exception e) { logger.error("Failed to prepare Chromedriver!"); }
        }
        System.setProperty("webdriver.chrome.driver", "src/test/resources/webdriver/linux/chromedriver-linux64/chromedriver");
        if (detectedChromeBinary != null) chromeOptions.setBinary(detectedChromeBinary);
        chromeOptions.addArguments("--incognito");
        chromeOptions.setCapability(CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR, "ignore");
        if ("true".equalsIgnoreCase(System.getProperty("headless"))) chromeOptions.addArguments("--headless");
        return new ChromeDriver(chromeOptions);
    }
    @Override
    public boolean takesScreenshots() { return true; }
}
