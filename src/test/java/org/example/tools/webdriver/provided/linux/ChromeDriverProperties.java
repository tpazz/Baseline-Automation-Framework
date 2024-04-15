package org.example.tools.webdriver.provided.linux;

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
        ChromeDriverSetup.main("Linux");
        ChromeOptions chromeOptions = new ChromeOptions();
        System.setProperty("webdriver.chrome.driver", "src/test/resources/webdriver/linux/chromedriver-linux64/chromedriver");
        if (ChromeDriverSetup.checkLocalInstallation("Linux") == null) {
            if (ChromeDriverSetup.checkProjectInstallation("Linux") != null) {
                logger.info("Setting binary path to Project Chrome Installation");
                chromeOptions.setBinary("src/test/resources/browser/windows/chrome/Application/chrome.exe");
            } else {
                logger.error("Could not find local or project Chrome installed.");
            }
        }
        chromeOptions.addArguments("--incognito", "--start-maximised", "--headless");
        return new ChromeDriver(chromeOptions);
    }
    @Override
    public boolean takesScreenshots() { return true; }
}
