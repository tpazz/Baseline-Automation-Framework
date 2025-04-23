package org.example.tools.webdriver.provided.windows;
import net.thucydides.core.webdriver.DriverSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.core.base.PageObjectExtension;
import org.example.tools.webdriver.setup.EdgeDriverSetup;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.CapabilityType;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class EdgeDriverProperties implements DriverSource {
    private static final AtomicBoolean setupDone = new AtomicBoolean(false);
    public static Logger logger = LogManager.getLogger(PageObjectExtension.class);
    @Override
    public WebDriver newDriver() {
        EdgeOptions edgeOptions = new EdgeOptions();
        if (setupDone.compareAndSet(false, true)) {
            try { EdgeDriverSetup.main("Windows", edgeOptions); }
            catch (Exception e) { logger.error("Failed to prepare Edgedriver!"); }
        }
        System.setProperty("webdriver.edge.driver", "src/test/resources/webdriver/windows/edgedriver_win64/msedgedriver.exe");
        edgeOptions.addArguments("InPrivate");
        edgeOptions.setCapability(CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR, "ignore");
        if ("true".equalsIgnoreCase(System.getProperty("headless"))) edgeOptions.addArguments("headless");
        return new EdgeDriver(edgeOptions);
    }
    @Override
    public boolean takesScreenshots() {
        return true;
    }
}
