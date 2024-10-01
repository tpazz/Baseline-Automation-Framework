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
        String firefoxBrowserVersion;
        String geckoDriverVersion;
        if (checkLocalInstallation(os) == null) {
            logger.info("No local installation of Firefox found. Checking project installation...");
            if (checkProjectInstallation(os) == null) {
                logger.info("No project installation of Firefox found. Please download Firefox!");
                return;
            } else {
                logger.info("Project installation found! Setting binary to project location...");
                setBinary(os,fo);
                firefoxBrowserVersion = checkProjectInstallation(os);
            }
        } else {
            logger.info("Local Firefox installation found!");
            firefoxBrowserVersion = checkLocalInstallation(os);
        }
        geckoDriverVersion = getGeckoDriverVersion(os);
        logger.info("Firefox Browser version: " + firefoxBrowserVersion);
        logger.info("Geckodriver version: " + geckoDriverVersion);
        logger.warn("If driver and browser are incompatible, visit: https://github.com/mozilla/geckodriver/releases");
    }

    private static void setBinary(String os, FirefoxOptions fo) {
        switch (os) {
            case "Windows": fo.setBinary("src/test/resources/browser/windows/firefox/firefox.exe"); break;
            case "Linux": fo.setBinary("src/test/resources/browser/linux/firefox/firefox"); break;
        }
    }

    public static String detectFirefox() {
        String line = null;
        try {
            // wmic datafile where name="C:\\Program Files\\Mozilla Firefox\\firefox.exe" get Version /value
            File directory = new File("C:\\Program Files\\Mozilla Firefox");
            String[] command = {"cmd", "/C", "firefox -v|more"};
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(directory);
            Process process = builder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                while ((line = reader.readLine()) != null) {
                    logger.info("Firefox detected! " + line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }

    public static String getGeckoDriverVersion(String os) throws Exception {
        switch (os) {
            case "Windows": {
                terminal = "cmd";
                flag = "/C";
                command = getAbsolutePath() + "\\\\src\\\\test\\\\resources\\\\webdriver\\\\windows\\\\geckodriver-win64\\\\geckodriver.exe --version";
                result = executeCommand(terminal,flag,command);
                return extractGeckoDriverVersion(result);
            }
            case "Linux": {
                terminal = "bash";
                flag = "-c";
                command = getAbsolutePath() + "/src/test/resources/webdriver/linux/geckodriver-linux64/geckodriver -version";
                result = executeCommand(terminal,flag,result);
                return extractGeckoDriverVersion(result);
            }
        }
        return null;
    }

    private static String extractGeckoDriverVersion(String result) {
        return result.split(" ")[1];
    }

    public static String checkProjectInstallation(String os) throws Exception {
        switch (os) {
            case "Windows": {
                String currentWorkingDir = System.getProperty("user.dir");
                String correctedPath = currentWorkingDir.replace("\\", "\\\\");
                terminal = "cmd";
                flag = "/C";
                command = "wmic datafile where name=\"" + correctedPath + "\\\\src\\\\test\\\\resources\\\\browser\\\\windows\\\\firefox\\\\firefox.exe\" get Version /value";
                result = executeCommand(terminal,flag,command);
                return extractWindowsBrowserVersion(result, "Version");
            }
            case "Linux": {
                terminal = "bash";
                flag = "-c";
                command = "src/test/resources/browser/linux/firefox/firefox -version";
                result = executeCommand(terminal,flag,command);
                return extractLinuxBrowserVersion(result, "Mozilla Firefox ");
            }
        }
        return null;
    }

    public static String checkLocalInstallation(String os) throws Exception {
        switch (os) {
            case "Windows": {
                terminal = "cmd";
                flag = "/C";
                command = "wmic datafile where name=\"C:\\\\Program Files\\\\Mozilla Firefox\\\\firefox.exe\" get Version /value";
                result = executeCommand(terminal,flag,command);
                return extractWindowsBrowserVersion(result, "Version");
            }
            case "Linux": {
                terminal = "bash";
                flag = "-c";
                command = "/usr/lib/firefox/firefox -version";
                result = executeCommand(terminal,flag,command);
                return extractLinuxBrowserVersion(result, "Mozilla Firefox ");
            }
        }
        return null;
    }

}
