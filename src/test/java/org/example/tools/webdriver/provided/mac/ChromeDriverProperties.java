package org.example.tools.webdriver.provided.mac;

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
        ChromeDriverSetup.main("Mac");
        ChromeOptions chromeOptions = new ChromeOptions();
        System.setProperty("webdriver.chrome.driver", "src/test/resources/webdriver/windows/chromedriver-win64/chromedriver.exe");
        if (ChromeDriverSetup.checkLocalInstallation("Windows") == null) {
            if (ChromeDriverSetup.checkProjectInstallation("Windows") != null) {
                logger.info("Setting binary path to Project Chrome Installation");
                chromeOptions.setBinary("src/test/resources/browser/windows/chrome/Application/chrome.exe");
            } else logger.error("Could not find local or project Chrome installed.");
        }
        chromeOptions.addArguments("--incognito", "--start-maximised", "--headless");
        return new ChromeDriver(chromeOptions);
    }
    @Override
    public boolean takesScreenshots() { return true; }
}
