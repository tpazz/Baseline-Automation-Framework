package org.example.tools.webdriver.provided.windows;

import net.thucydides.core.webdriver.DriverSource;
import org.example.tools.webdriver.setup.FireFoxDriverSetup;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class FireFoxDriverProperties implements DriverSource {
    @Override
    public WebDriver newDriver() {
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        System.setProperty("webdriver.gecko.driver", "src/test/resources/webdriver/windows/geckodriver-win64/geckodriver.exe");
        firefoxOptions.addArguments("-private", "-headless");
            if (FireFoxDriverSetup.checkLocalInstallation("Windows") == null)
                firefoxOptions.setBinary("src/test/resources/browser/windows/firefox/firefox.exe");
        return new FirefoxDriver(firefoxOptions);
    }

    @Override
    public boolean takesScreenshots() {
        return true;
    }
}
