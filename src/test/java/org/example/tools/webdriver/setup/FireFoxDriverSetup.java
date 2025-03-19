package org.example.tools.webdriver.setup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.core.base.PageObjectExtension;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.*;
import java.util.stream.Collectors;

public class FireFoxDriverSetup extends Utils {

    public static Logger logger = LogManager.getLogger(PageObjectExtension.class);

    public static void main(String os, FirefoxOptions fo) throws Exception {
        String geckoDriverVersion;
        String firefoxBrowserVersion = "";
        if (checkLocalInstallation(os) == null) {
            logger.info("No local installation of Firefox found in default installation directories. Please download Firefox!");
        } else {
            logger.info("Local Firefox installation found!");
            firefoxBrowserVersion = checkLocalInstallation(os);
        }
        geckoDriverVersion = getGeckoDriverVersion(os);
        logger.info("Firefox Browser version: " + firefoxBrowserVersion);
        logger.info("Geckodriver version: " + geckoDriverVersion);
        logger.warn("If driver and browser are incompatible, visit: https://github.com/mozilla/geckodriver/releases");
    }

    public static String checkLocalInstallation(String os) throws Exception {
        String firefoxBrowserVersion;
        firefoxBrowserVersion = checkLocalInstallationStandard(os);
        if (firefoxBrowserVersion == null) firefoxBrowserVersion = checkLocalInstallationDev(os);
        if (firefoxBrowserVersion == null) firefoxBrowserVersion = checkLocalInstallationEsr(os);
        return firefoxBrowserVersion;
    }

    public static String getGeckoDriverVersion(String os) throws Exception {
        switch (os) {
            case "Windows": {
                terminal = "cmd";
                flag = "/C";
                command = getAbsolutePath() + "\\\\src\\\\test\\\\resources\\\\webdriver\\\\windows\\\\geckodriver-win64\\\\geckodriver.exe --version";
                result = executeCommand(terminal, flag, command);
                return extractGeckoDriverVersion(result);
            }
            case "Linux": {
                terminal = "bash";
                flag = "-c";
                command = getAbsolutePath() + "/src/test/resources/webdriver/linux/geckodriver-linux64/geckodriver --version";
                result = executeCommand(terminal, flag, command);
                return extractGeckoDriverVersion(result);
            }
        }
        return null;
    }

    private static String extractGeckoDriverVersion(String result) {
        return result.split(" ")[1];
    }

    public static String checkLocalInstallationDev(String os) throws Exception {
        switch (os) {
            case "Windows": {
                terminal = "cmd";
                flag = "/C";
                command = "wmic datafile where name=\"C:\\\\Program Files\\\\Firefox Developer Edition\\\\firefox.exe\" get Version /value";
                result = executeCommand(terminal, flag, command);
                return extractWindowsBrowserVersion(result, "Version");
            }
            case "Linux": {
                terminal = "bash";
                flag = "-c";
                command = "/opt/firefox-developer-edition/firefox -version";
                result = executeCommand(terminal, flag, command);
                return extractLinuxBrowserVersion(result, "Mozilla Firefox ");
            }
        }
        return null;
    }

    public static String checkLocalInstallationEsr(String os) throws Exception {
        switch (os) {
            case "Windows": {
                terminal = "cmd";
                flag = "/C";
                command = "wmic datafile where name=\"C:\\\\Program Files\\\\Mozilla Firefox ESR\\\\firefox.exe\" get Version /value";
                result = executeCommand(terminal, flag, command);
                return extractWindowsBrowserVersion(result, "Version");
            }
            case "Linux": {
                terminal = "bash";
                flag = "-c";
                command = "/usr/lib/firefox-esr/firefox -version";
                result = executeCommand(terminal, flag, command);
                return extractLinuxBrowserVersion(result, "Mozilla Firefox ");
            }
        }
        return null;
    }

    public static String checkLocalInstallationStandard(String os) throws Exception {
        switch (os) {
            case "Windows": {
                terminal = "cmd";
                flag = "/C";
                command = "wmic datafile where name=\"C:\\\\Program Files\\\\Mozilla Firefox\\\\firefox.exe\" get Version /value";
                result = executeCommand(terminal, flag, command);
                return extractWindowsBrowserVersion(result, "Version");
            }
            case "Linux": {
                terminal = "bash";
                flag = "-c";
                command = "/usr/lib/firefox/firefox -version";
                result = executeCommand(terminal, flag, command);
                return extractLinuxBrowserVersion(result, "Mozilla Firefox ");
            }
        }
        return null;
    }

}
