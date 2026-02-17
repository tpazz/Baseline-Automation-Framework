package org.example.tools.webdriver.setup;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.example.core.base.Constants.*;

public class ChromeDriverSetup extends Utils {
    public static volatile String detectedChromeBinary = null;

    public static void main(String os, ChromeOptions co) throws Exception {
        String chromeDriverVersion = "";
        String chromeBrowserVersion = checkLocalInstallation(os);
        if (chromeBrowserVersion == null) {
            logger.info("No local installation of Chrome found in default installation directories. Please download Chrome!");
            throw new IllegalStateException("Unable to detect Chrome binary/version on " + os);
        }
        logger.info("Local Chrome installation found!");
        logger.info("Detected Chrome binary: " + detectedChromeBinary);
        String shortChromeBrowserVersion = chromeBrowserVersion.split("\\.")[0];
        try {
            chromeDriverVersion = getChromeDriverVersion(os);
            if (chromeDriverVersion == null || chromeDriverVersion.isBlank()) {
                throw new IllegalStateException("Unable to detect Chromedriver version on " + os);
            }
            String shortChromeDriverVersion = chromeDriverVersion.split("\\.")[0];
            logger.info("Chrome Browser version: " + chromeBrowserVersion);
            logger.info("Chromedriver version: " + chromeDriverVersion);
            if (!shortChromeBrowserVersion.equalsIgnoreCase(shortChromeDriverVersion)) {
                logger.warn("Driver and browser versions are incompatible!");
                logger.info("Downloading compatible Chromedriver...");
                downloadChromeDriver(getChromeDriverURL(shortChromeBrowserVersion, os), os);
                if (os.equalsIgnoreCase("Linux")) setExecutablePermissionLinux("Chromedriver");
                logger.info("Chrome Browser version: " + chromeBrowserVersion);
                logger.info("Chromedriver version: " + getChromeDriverVersion(os));
                logger.info("Driver versions are now compatible!");
                logger.info("Starting tests...");
            } else {
                logger.info("Driver and browser are compatible!");
                logger.info("Starting tests...");
            }
        }
        catch (Exception e) {
            logger.warn("No Chromedriver was found! Downloading compatible version...");
            downloadChromeDriver(getChromeDriverURL(shortChromeBrowserVersion, os), os);
            if (os.equalsIgnoreCase("Linux")) setExecutablePermissionLinux("Chromedriver");
            logger.info("Chrome Browser version: " + chromeBrowserVersion);
            logger.info("Chromedriver version: " + getChromeDriverVersion(os));
            logger.info("Driver versions are now compatible!");
            logger.info("Starting tests...");
        }
    }

    public static void downloadChromeDriver(String zipurl, String os) throws Exception {
        closeAllChromeDrivers(os);
        String targetDirectory = switch (os) {
            case "Windows" -> PROJECT_RESOURCES_WINDOWS;
            case "Linux" -> PROJECT_RESOURCES_LINUX;
            default -> null;
        };
        URI uri = URI.create(zipurl);
        URL url = uri.toURL();
        InputStream in = url.openStream();
            String fileName = getFileNameFromUrl(zipurl);
            Path outputPath = Paths.get(targetDirectory, fileName);
            Files.copy(in, outputPath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("File downloaded to: " + outputPath);
            extractZipFile(outputPath.toString(), targetDirectory);
            Files.delete(outputPath);
    }

    public static void closeAllChromeDrivers(String os) throws Exception {
        switch (os) {
            case "Windows": {
                terminal = "cmd";
                flag = "/C";
                command = "Taskkill /IM chromedriver.exe /F";
                executeCommand(terminal,flag,command);
            } break;
            case "Linux": {
                terminal = "bash";
                flag = "-c";
                command = "killall -9 chromedriver";
                executeCommand(terminal,flag,command);
            } break;
        }
    }

    public static String getChromeDriverURL(String compatibleVersion, String os) {
        try {
            URI uri = URI.create(CHROME_DRIVER_API);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder content = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            JSONObject jsonObject = new JSONObject(String.valueOf(content));
            try {
                JSONArray versionsArray = jsonObject.getJSONArray("versions");
                for (int i = 0; i < versionsArray.length(); i++) {
                    JSONObject versionObject = versionsArray.getJSONObject(i);
                    String version = versionObject.getString("version");
                    int versionNumber = Integer.parseInt(version.split("\\.")[0]);
                    if (versionNumber == Integer.valueOf(compatibleVersion)) {
                        JSONObject downloads = versionObject.getJSONObject("downloads");
                        if (downloads.has("chromedriver")) {
                            JSONArray chromeDownloadsArray = versionObject.getJSONObject("downloads")
                                    .getJSONArray("chromedriver");
                            for (int j = 0; j < chromeDownloadsArray.length(); j++) {
                                JSONObject platformObject = chromeDownloadsArray.getJSONObject(j);
                                String platform = platformObject.getString("platform");
                                if (platform.equals("win64") && os.equals("Windows")) {
                                    String downloadUrl = platformObject.getString("url");
                                    logger.info("Version: " + version);
                                    logger.info("Platform: " + platform);
                                    logger.info("URL: " + downloadUrl);
                                    return downloadUrl;
                                } else if (platform.equals("linux64") && os.equals("Linux")) {
                                    String downloadUrl = platformObject.getString("url");
                                    logger.info("Version: " + version);
                                    logger.info("Platform: " + platform);
                                    logger.info("URL: " + downloadUrl);
                                    return downloadUrl;
                                }
                            }
                        }
                    }
                }
                logger.info("No matching version found");
            } catch (Exception e) {
                e.printStackTrace();
            }
            reader.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String checkLocalInstallation(String os) throws Exception {
        switch (os) {
            case "Windows": {
                String[] candidates = new String[] {
                        buildPath("ProgramFiles", "Google\\Chrome\\Application\\chrome.exe"),
                        buildPath("ProgramFiles(x86)", "Google\\Chrome\\Application\\chrome.exe"),
                        buildPath("LocalAppData", "Google\\Chrome\\Application\\chrome.exe"),
                        "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe",
                        "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe"
                };

                for (String candidate : candidates) {
                    if (candidate != null && Files.exists(Paths.get(candidate))) {
                        String version = getWindowsBrowserVersionFromBinary(candidate, "Google Chrome ");
                        if (version != null) {
                            detectedChromeBinary = candidate;
                            return version;
                        }
                    }
                }

                String appPath = getWindowsAppPathFromRegistry("chrome.exe");
                if (appPath != null && Files.exists(Paths.get(appPath))) {
                    String version = getWindowsBrowserVersionFromBinary(appPath, "Google Chrome ");
                    if (version != null) {
                        detectedChromeBinary = appPath;
                        return version;
                    }
                }

                terminal = "cmd";
                flag = "/C";
                command = "reg query \"HKCU\\Software\\Google\\Chrome\\BLBeacon\" /v version";
                result = executeCommand(terminal, flag, command);
                String version = extractWindowsBrowserVersion(result);
                if (version != null) return version;

                command = "reg query \"HKLM\\Software\\Google\\Chrome\\BLBeacon\" /v version";
                result = executeCommand(terminal, flag, command);
                version = extractWindowsBrowserVersion(result);
                if (version != null) return version;

                command = "reg query \"HKLM\\Software\\WOW6432Node\\Google\\Chrome\\BLBeacon\" /v version";
                result = executeCommand(terminal, flag, command);
                return extractWindowsBrowserVersion(result);
            }
            case "Linux": {
                String[] linuxCandidates = new String[] {
                        "/opt/google/chrome/chrome",
                        "/usr/bin/google-chrome",
                        "/usr/bin/google-chrome-stable"
                };
                for (String candidate : linuxCandidates) {
                    if (!Files.exists(Paths.get(candidate))) continue;
                    terminal = "bash";
                    flag = "-c";
                    command = "\"" + candidate + "\" --version";
                    result = executeCommand(terminal, flag, command);
                    String detected = extractLinuxBrowserVersion(result, "Google Chrome ");
                    if (detected != null) {
                        detectedChromeBinary = candidate;
                        return detected;
                    }
                }

                String[] linuxCommands = new String[] {
                        "google-chrome --version",
                        "google-chrome-stable --version"
                };
                for (String cmd : linuxCommands) {
                    terminal = "bash";
                    flag = "-c";
                    result = executeCommand(terminal, flag, cmd);
                    String detected = extractLinuxBrowserVersion(result, "Google Chrome ");
                    if (detected != null) {
                        detectedChromeBinary = cmd.split(" ")[0];
                        return detected;
                    }
                }
                return null;
            }
        }
        return null;
    }

    private static String extractChromeDriverVersion(String result) {
        return result.split(" ")[1];
    }

    public static String getChromeDriverVersion(String os) throws Exception {
        switch (os) {
            case "Windows": {
                terminal = "cmd";
                flag = "/C";
                command = "\"" + getAbsolutePath() + "\\\\src\\\\test\\\\resources\\\\webdriver\\\\windows\\\\chromedriver-win64\\\\chromedriver.exe" + "\"" + " --version";
                result = executeCommand(terminal,flag,command);
                return extractChromeDriverVersion(result);
            }
            case "Linux": {
                terminal = "bash";
                flag = "-c";
                command = "\"" + getAbsolutePath() + "/src/test/resources/webdriver/linux/chromedriver-linux64/chromedriver" + "\"" + " -version";
                result = executeCommand(terminal,flag,command);
                return extractChromeDriverVersion(result);
            }
        }
        return null;
    }

    private static String getWindowsBrowserVersionFromBinary(String binaryPath, String prefix) throws Exception {
        terminal = "cmd";
        flag = "/C";
        command = "\"" + binaryPath + "\" --version";
        result = executeCommand(terminal, flag, command);
        return extractLinuxBrowserVersion(result, prefix);
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
        return Paths.get(root, suffix).toString();
    }
}
