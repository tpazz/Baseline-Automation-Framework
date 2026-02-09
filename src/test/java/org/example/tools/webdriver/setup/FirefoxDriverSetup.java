package org.example.tools.webdriver.setup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.core.base.PageObjectExtension;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.nio.file.Files;
import java.nio.file.Paths;

public class FirefoxDriverSetup extends Utils {

    public static Logger logger = LogManager.getLogger(PageObjectExtension.class);
    public static volatile String detectedFirefoxBinary = null;

    public static void main(String os, FirefoxOptions fo) throws Exception {
        String geckoDriverVersion;
        String firefoxBrowserVersion = checkLocalInstallation(os);

        if (firefoxBrowserVersion == null) {
            logger.info("No local installation of Firefox found in default installation directories. Please download Firefox!");
        }

        geckoDriverVersion = getGeckoDriverVersion(os);

        logger.info("Firefox Browser version: " + firefoxBrowserVersion);
        logger.info("Detected Firefox binary: " + detectedFirefoxBinary);
        logger.info("Geckodriver version: " + geckoDriverVersion);
        logger.warn("If driver and browser are incompatible, visit: https://github.com/mozilla/geckodriver/releases");
    }

    public static String checkLocalInstallation(String os) throws Exception {
        String version;

        version = checkLocalInstallationStandard(os);
        if (version != null) return version;

        version = checkLocalInstallationDev(os);
        if (version != null) return version;

        return checkLocalInstallationEsr(os);
    }

    public static String getGeckoDriverVersion(String os) throws Exception {
        switch (os) {
            case "Windows": {
                terminal = "cmd";
                flag = "/C";
                command = "\"" + getAbsolutePath()
                        + "\\\\src\\\\test\\\\resources\\\\webdriver\\\\windows\\\\geckodriver-win64\\\\geckodriver.exe\" --version";
                result = executeCommand(terminal, flag, command);
                return extractGeckoDriverVersion(result);
            }
            case "Linux": {
                terminal = "bash";
                flag = "-c";
                command = "\"" + getAbsolutePath()
                        + "/src/test/resources/webdriver/linux/geckodriver-linux64/geckodriver\" --version";
                result = executeCommand(terminal, flag, command);
                return extractGeckoDriverVersion(result);
            }
        }
        return null;
    }

    private static String extractGeckoDriverVersion(String result) {
        return result.split(" ")[1];
    }

    private static String getFirefoxVersionFromRegistryWindows() throws Exception {
        terminal = "cmd";
        flag = "/C";

        command = "reg query \"HKLM\\Software\\Mozilla\\Mozilla Firefox\" /v CurrentVersion";
        result = executeCommand(terminal, flag, command);
        String version = extractFirefoxVersionFromRegistry(result);

        if (version == null) {
            command = "reg query \"HKCU\\Software\\Mozilla\\Mozilla Firefox\" /v CurrentVersion";
            result = executeCommand(terminal, flag, command);
            version = extractFirefoxVersionFromRegistry(result);
        }
        return version;
    }

    private static String extractFirefoxVersionFromRegistry(String result) {
        if (result == null) return null;

        for (String line : result.split("\\R")) {
            if (line.contains("CurrentVersion")) {
                String[] tokens = line.trim().split("\\s+");
                return tokens[tokens.length - 1];
            }
        }
        return null;
    }

    public static String checkLocalInstallationStandard(String os) throws Exception {
        String pathWin = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
        String pathLin = "/usr/lib/firefox/firefox";

        switch (os) {
            case "Windows": {
                if (!Files.exists(Paths.get(pathWin))) return null;

                String version = getFirefoxVersionFromRegistryWindows();
                if (version == null) return null;

                if (!version.endsWith("esr")) {
                    logger.info("Detected Firefox Standard Edition!");
                    detectedFirefoxBinary = pathWin;
                    return version;
                }
                return null;
            }

            case "Linux": {
                if (!Files.exists(Paths.get(pathLin))) return null;

                terminal = "bash";
                flag = "-c";
                command = pathLin + " -version";
                result = executeCommand(terminal, flag, command);
                version = extractLinuxBrowserVersion(result, "Mozilla Firefox ");

                if (version != null) {
                    logger.info("Detected Firefox Standard Edition!");
                    detectedFirefoxBinary = pathLin;
                }
                return version;
            }
        }
        return null;
    }

    public static String checkLocalInstallationDev(String os) throws Exception {
        String pathWin = "C:\\Program Files\\Firefox Developer Edition\\firefox.exe";
        String pathLin = "/opt/firefox-developer-edition/firefox";

        switch (os) {
            case "Windows": {
                if (!Files.exists(Paths.get(pathWin))) return null;

                String version = getFirefoxVersionFromRegistryWindows();
                if (version != null) {
                    logger.info("Detected Firefox Developer Edition!");
                    detectedFirefoxBinary = pathWin;
                    return version;
                }
                return null;
            }

            case "Linux": {
                if (!Files.exists(Paths.get(pathLin))) return null;

                terminal = "bash";
                flag = "-c";
                command = pathLin + " -version";
                result = executeCommand(terminal, flag, command);
                version = extractLinuxBrowserVersion(result, "Mozilla Firefox ");

                if (version != null) {
                    logger.info("Detected Firefox Developer Edition!");
                    detectedFirefoxBinary = pathLin;
                }
                return version;
            }
        }
        return null;
    }

    public static String checkLocalInstallationEsr(String os) throws Exception {
        String pathWin = "C:\\Program Files\\Mozilla Firefox ESR\\firefox.exe";
        String pathLin = "/usr/lib/firefox-esr/firefox";

        switch (os) {
            case "Windows": {
                if (!Files.exists(Paths.get(pathWin))) return null;

                String version = getFirefoxVersionFromRegistryWindows();
                if (version == null) return null;

                if (version.endsWith("esr")) {
                    logger.info("Detected Firefox ESR Edition!");
                    detectedFirefoxBinary = pathWin;
                    return version;
                }
                return null;
            }

            case "Linux": {
                if (!Files.exists(Paths.get(pathLin))) return null;

                terminal = "bash";
                flag = "-c";
                command = pathLin + " -version";
                result = executeCommand(terminal, flag, command);
                version = extractLinuxBrowserVersion(result, "Mozilla Firefox ");

                if (version != null) {
                    logger.info("Detected Firefox ESR Edition!");
                    detectedFirefoxBinary = pathLin;
                }
                return version;
            }
        }
        return null;
    }
}
