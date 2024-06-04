package org.example.tools.webdriver.provided.linux;

import net.thucydides.core.webdriver.DriverSource;
import org.example.tools.webdriver.setup.FireFoxDriverSetup;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class FireFoxDriverProperties implements DriverSource {
    @Override
    public WebDriver newDriver() {
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        try { FireFoxDriverSetup.main("Linux", firefoxOptions); }
        catch (Exception e) { e.printStackTrace(); }
        System.setProperty("webdriver.gecko.driver", "src/test/resources/webdriver/linux/geckodriver-linux64/geckodriver");
        firefoxOptions.addArguments("-private", "-headless");
        return new FirefoxDriver(firefoxOptions);
    }

    @Override
    public boolean takesScreenshots() {
        return true;
    }
}
