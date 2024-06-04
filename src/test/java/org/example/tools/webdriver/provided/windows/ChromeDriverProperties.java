package org.example.tools.webdriver.provided.windows;

import net.thucydides.core.webdriver.DriverSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.core.base.PageObjectExtension;
import org.example.tools.webdriver.setup.ChromeDriverSetup;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeDriverProperties implements DriverSource {

    public static Logger logger = LogManager.getLogger(PageObjectExtension.class);

    @Override
    public WebDriver newDriver() {
        ChromeOptions chromeOptions = new ChromeOptions();
        try { ChromeDriverSetup.main("Windows", chromeOptions); }
        catch (Exception e) { e.printStackTrace(); }
        System.setProperty("webdriver.chrome.driver", "src/test/resources/webdriver/windows/chromedriver-win64/chromedriver.exe");
        chromeOptions.addArguments("--incognito", "--start-maximised", "--headless");
        return new ChromeDriver(chromeOptions);
    }
    @Override
    public boolean takesScreenshots() { return true; }
}
