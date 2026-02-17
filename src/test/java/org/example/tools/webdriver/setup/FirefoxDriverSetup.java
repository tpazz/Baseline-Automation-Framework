package org.example.tools.webdriver.setup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.core.base.PageObjectExtension;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FirefoxDriverSetup extends Utils {

    public static Logger logger = LogManager.getLogger(PageObjectExtension.class);
    public static volatile String detectedFirefoxBinary = null;

    public static void main(String os, FirefoxOptions fo) throws Exception {
        String geckoDriverVersion;
        String firefoxBrowserVersion = checkLocalInstallation(os);

        if (firefoxBrowserVersion == null) {
            logger.info("No local installation of Firefox found in default installation directories. Please download Firefox!");
            throw new IllegalStateException("Unable to detect Firefox binary/version on " + os);
        }

        geckoDriverVersion = getGeckoDriverVersion(os);
        if (geckoDriverVersion == null || geckoDriverVersion.isBlank()) {
            throw new IllegalStateException("Unable to detect Geckodriver version on " + os);
        }

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

        version = checkLocalInstallationEsr(os);
        if (version != null) return version;

        if ("Windows".equalsIgnoreCase(os)) {
            String appPath = getWindowsAppPathFromRegistry("firefox.exe");
            if (appPath != null && Files.exists(Paths.get(appPath))) {
                version = getFirefoxVersionFromBinary(appPath, os);
                if (version != null) {
                    logger.info("Detected Firefox from App Paths registry!");
                    detectedFirefoxBinary = appPath;
                    return version;
                }
            }
        }

        return null;
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
        if (result == null) return null;
        for (String line : result.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.startsWith("geckodriver")) {
                String[] tokens = trimmed.split("\\s+");
                if (tokens.length > 1) return tokens[1];
            }
        }
        return null;
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
        switch (os) {
            case "Windows": {
                String[] candidates = new String[] {
                        buildPath("ProgramFiles", "Mozilla Firefox\\firefox.exe"),
                        buildPath("ProgramFiles(x86)", "Mozilla Firefox\\firefox.exe"),
                        buildPath("LocalAppData", "Mozilla Firefox\\firefox.exe"),
                        "C:\\Program Files\\Mozilla Firefox\\firefox.exe",
                        "C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe"
                };
                for (String candidate : candidates) {
                    if (candidate == null || !Files.exists(Paths.get(candidate))) continue;
                    String version = getFirefoxVersionFromBinary(candidate, os);
                    if (version != null && !version.endsWith("esr")) {
                        logger.info("Detected Firefox Standard Edition!");
                        detectedFirefoxBinary = candidate;
                        return version;
                    }
                }
                return null;
            }

            case "Linux": {
                String[] candidates = new String[] {
                        "/usr/lib/firefox/firefox",
                        "/usr/bin/firefox"
                };
                for (String candidate : candidates) {
                    if (!Files.exists(Paths.get(candidate))) continue;
                    String version = getFirefoxVersionFromBinary(candidate, os);
                    if (version != null) {
                        logger.info("Detected Firefox Standard Edition!");
                        detectedFirefoxBinary = candidate;
                        return version;
                    }
                }
                return null;
            }
        }
        return null;
    }

    public static String checkLocalInstallationDev(String os) throws Exception {
        switch (os) {
            case "Windows": {
                String[] candidates = new String[] {
                        buildPath("ProgramFiles", "Firefox Developer Edition\\firefox.exe"),
                        buildPath("ProgramFiles(x86)", "Firefox Developer Edition\\firefox.exe"),
                        buildPath("LocalAppData", "Firefox Developer Edition\\firefox.exe"),
                        "C:\\Program Files\\Firefox Developer Edition\\firefox.exe",
                        "C:\\Program Files (x86)\\Firefox Developer Edition\\firefox.exe"
                };
                for (String candidate : candidates) {
                    if (candidate == null || !Files.exists(Paths.get(candidate))) continue;
                    String version = getFirefoxVersionFromBinary(candidate, os);
                    if (version != null) {
                        logger.info("Detected Firefox Developer Edition!");
                        detectedFirefoxBinary = candidate;
                        return version;
                    }
                }
                return null;
            }

            case "Linux": {
                String[] candidates = new String[] {
                        "/opt/firefox-developer-edition/firefox"
                };
                for (String candidate : candidates) {
                    if (!Files.exists(Paths.get(candidate))) continue;
                    String version = getFirefoxVersionFromBinary(candidate, os);
                    if (version != null) {
                        logger.info("Detected Firefox Developer Edition!");
                        detectedFirefoxBinary = candidate;
                        return version;
                    }
                }
                return null;
            }
        }
        return null;
    }

    public static String checkLocalInstallationEsr(String os) throws Exception {
        switch (os) {
            case "Windows": {
                String[] candidates = new String[] {
                        buildPath("ProgramFiles", "Mozilla Firefox ESR\\firefox.exe"),
                        buildPath("ProgramFiles(x86)", "Mozilla Firefox ESR\\firefox.exe"),
                        "C:\\Program Files\\Mozilla Firefox ESR\\firefox.exe",
                        "C:\\Program Files (x86)\\Mozilla Firefox ESR\\firefox.exe"
                };
                for (String candidate : candidates) {
                    if (candidate == null || !Files.exists(Paths.get(candidate))) continue;
                    String version = getFirefoxVersionFromBinary(candidate, os);
                    if (version != null) {
                        logger.info("Detected Firefox ESR Edition!");
                        detectedFirefoxBinary = candidate;
                        return version;
                    }
                }
                return null;
            }

            case "Linux": {
                String[] candidates = new String[] {
                        "/usr/lib/firefox-esr/firefox",
                        "/usr/bin/firefox-esr"
                };
                for (String candidate : candidates) {
                    if (!Files.exists(Paths.get(candidate))) continue;
                    String version = getFirefoxVersionFromBinary(candidate, os);
                    if (version != null) {
                        logger.info("Detected Firefox ESR Edition!");
                        detectedFirefoxBinary = candidate;
                        return version;
                    }
                }
                return null;
            }
        }
        return null;
    }

    private static String getFirefoxVersionFromBinary(String binaryPath, String os) throws Exception {
        switch (os) {
            case "Windows": {
                terminal = "cmd";
                flag = "/C";
                command = "\"" + binaryPath + "\" -version";
            } break;
            case "Linux": {
                terminal = "bash";
                flag = "-c";
                command = "\"" + binaryPath + "\" -version";
            } break;
            default: return null;
        }
        result = executeCommand(terminal, flag, command);
        String version = extractLinuxBrowserVersion(result, "Mozilla Firefox ");
        if (version != null) return version;
        String fromRegistry = "Windows".equalsIgnoreCase(os) ? getFirefoxVersionFromRegistryWindows() : null;
        return fromRegistry;
    }

    private static String getWindowsAppPathFromRegistry(String exeName) throws Exception {
        String[] queries = new String[] {
                "reg query \"HKLM\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\App Paths\\" + exeName + "\" /ve",
                "reg query \"HKCU\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\App Paths\\" + exeName + "\" /ve"
        };
        Pattern pattern = Pattern.compile("REG_SZ\\s+(.+)$", Pattern.CASE_INSENSITIVE);
        for (String q : queries) {
            terminal = "cmd";
            flag = "/C";
            String out = executeCommand(terminal, flag, q);
            for (String line : out.split("\\R")) {
                Matcher matcher = pattern.matcher(line.trim());
                if (matcher.find()) return matcher.group(1).trim();
            }
        }
        return null;
    }

    private static String buildPath(String envVar, String suffix) {
        String root = System.getenv(envVar);
        if (root == null || root.isBlank()) return null;
        return Path.of(root, suffix).toString();
    }
}
