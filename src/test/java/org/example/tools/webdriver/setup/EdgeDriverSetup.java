package org.example.tools.webdriver.setup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.core.base.PageObjectExtension;
import org.openqa.selenium.edge.EdgeOptions;

import java.io.*;
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

public class EdgeDriverSetup extends Utils {

    public static Logger logger = LogManager.getLogger(PageObjectExtension.class);
    public static volatile String detectedEdgeBinary = null;

    public static void main(String os, EdgeOptions eo) throws Exception {
        String edgeDriverVersion;
        String edgeBrowserVersion = checkLocalInstallation(os);
        if (edgeBrowserVersion == null) {
            logger.info("No local installation of Edge found in default installation directories. Please download Edge!");
            throw new IllegalStateException("Unable to detect Edge binary/version on " + os);
        }
        logger.info("Local Edge installation found!");
        logger.info("Detected Edge binary: " + detectedEdgeBinary);
        String shortEdgeBrowserVersion = edgeBrowserVersion.split("\\.")[0];
        try {
            edgeDriverVersion = getEdgeDriverVersion(os);
            if (edgeDriverVersion == null || edgeDriverVersion.isBlank()) {
                throw new IllegalStateException("Unable to detect Edgedriver version on " + os);
            }
            String shortEdgeDriverVersion = edgeDriverVersion.split("\\.")[0];
            logger.info("Edge Browser version: " + edgeBrowserVersion);
            logger.info("EdgeDriver version: " + edgeDriverVersion);
            if (!shortEdgeBrowserVersion.equalsIgnoreCase(shortEdgeDriverVersion)) {
                logger.warn("Driver and browser versions are incompatible!");
                logger.info("Downloading compatible EdgeDriver...");
                downloadEdgeDriver(getEdgeDriverURL(edgeBrowserVersion, os), os);
                if (os.equalsIgnoreCase("Linux")) setExecutablePermissionLinux("Edgedriver");
                logger.info("Edge Browser version: " + edgeBrowserVersion);
                logger.info("EdgeDriver version: " + getEdgeDriverVersion(os));
                logger.info("Driver versions are now compatible!");
                logger.info("Starting tests...");
            } else {
                logger.info("Driver and browser are compatible!");
                logger.info("Starting tests...");
            }
        }
        catch (Exception e) {
            logger.warn("No Edgedriver was found! Downloading compatible version...");
            downloadEdgeDriver(getEdgeDriverURL(edgeBrowserVersion, os), os);
            if (os.equalsIgnoreCase("Linux")) setExecutablePermissionLinux("Edgedriver");
            logger.info("Edge Browser version: " + edgeBrowserVersion);
            logger.info("EdgeDriver version: " + getEdgeDriverVersion(os));
            logger.info("Driver and browser are compatible!");
            logger.info("Starting tests...");
        }
    }

    public static void downloadEdgeDriver(String zipurl, String os) throws Exception {
        logger.info("URL:" + zipurl);
        closeAllEdgeDrivers(os);
        String targetDirectory = null;
        switch (os) {
            case "Windows": {
                targetDirectory = PROJECT_RESOURCES_WINDOWS;
                if (zipurl.contains("edgedriver"))
                    targetDirectory = PROJECT_RESOURCES_WINDOWS + "edgedriver_win64";
            } break;
            case "Linux": {
                targetDirectory = PROJECT_RESOURCES_LINUX;
                if (zipurl.contains("edgedriver"))
                    targetDirectory = PROJECT_RESOURCES_LINUX + "edgedriver-linux64";
            } break;
        }
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

    public static void closeAllEdgeDrivers(String os) throws Exception {
        switch (os) {
            case "Windows": {
                terminal = "cmd";
                flag = "/C";
                command = "Taskkill /IM msedgedriver.exe /F";
                executeCommand(terminal, flag, command);
            } break;
            case "Linux": {
                terminal = "bash";
                flag = "-c";
                command = "killall -9 msedgedriver";
                executeCommand(terminal, flag, command);
            } break;
        }
    }

    public static String getEdgeDriverURL(String compatibleVersion, String os) {
        return switch (os) {
            case "Windows" -> EDGE_DRIVER_API + compatibleVersion + "/edgedriver_win64.zip";
            case "Linux" -> EDGE_DRIVER_API + compatibleVersion + "/edgedriver_linux64.zip";
            default -> null;
        };
    }

    public static String checkLocalInstallation(String os) throws Exception {
        switch (os) {
            case "Windows": {
                String[] candidates = new String[] {
                        buildPath("ProgramFiles(x86)", "Microsoft\\Edge\\Application\\msedge.exe"),
                        buildPath("ProgramFiles", "Microsoft\\Edge\\Application\\msedge.exe"),
                        buildPath("LocalAppData", "Microsoft\\Edge\\Application\\msedge.exe"),
                        "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe",
                        "C:\\Program Files\\Microsoft\\Edge\\Application\\msedge.exe"
                };

                for (String candidate : candidates) {
                    if (candidate != null && Files.exists(Paths.get(candidate))) {
                        String version = getWindowsBrowserVersionFromBinary(candidate, "Microsoft Edge ");
                        if (version != null) {
                            detectedEdgeBinary = candidate;
                            return version;
                        }
                    }
                }

                String appPath = getWindowsAppPathFromRegistry("msedge.exe");
                if (appPath != null && Files.exists(Paths.get(appPath))) {
                    String version = getWindowsBrowserVersionFromBinary(appPath, "Microsoft Edge ");
                    if (version != null) {
                        detectedEdgeBinary = appPath;
                        return version;
                    }
                }

                terminal = "cmd";
                flag = "/C";
                command = "reg query \"HKCU\\Software\\Microsoft\\Edge\\BLBeacon\" /v version";
                result = executeCommand(terminal, flag, command);
                String version = extractWindowsBrowserVersion(result);
                if (version != null) return version;

                command = "reg query \"HKLM\\Software\\Microsoft\\Edge\\BLBeacon\" /v version";
                result = executeCommand(terminal, flag, command);
                version = extractWindowsBrowserVersion(result);
                if (version != null) return version;

                command = "reg query \"HKLM\\Software\\WOW6432Node\\Microsoft\\Edge\\BLBeacon\" /v version";
                result = executeCommand(terminal, flag, command);
                return extractWindowsBrowserVersion(result);
            }
            case "Linux": {
                String[] linuxCandidates = new String[] {
                        "/usr/bin/microsoft-edge",
                        "/usr/bin/microsoft-edge-stable"
                };
                for (String candidate : linuxCandidates) {
                    if (!Files.exists(Paths.get(candidate))) continue;
                    terminal = "bash";
                    flag = "-c";
                    command = "\"" + candidate + "\" --version";
                    result = executeCommand(terminal, flag, command);
                    String detected = extractLinuxBrowserVersion(result, "Microsoft Edge ");
                    if (detected != null) {
                        detectedEdgeBinary = candidate;
                        return detected;
                    }
                }

                String[] linuxCommands = new String[] {
                        "microsoft-edge --version",
                        "microsoft-edge-stable --version"
                };
                for (String cmd : linuxCommands) {
                    terminal = "bash";
                    flag = "-c";
                    result = executeCommand(terminal, flag, cmd);
                    String detected = extractLinuxBrowserVersion(result, "Microsoft Edge ");
                    if (detected != null) {
                        detectedEdgeBinary = cmd.split(" ")[0];
                        return detected;
                    }
                }
                return null;
            }
        }
        return null;
    }

    private static String extractEdgeDriverVersion(String result) {
        return result.split(" ")[3];
    }

    public static String getEdgeDriverVersion(String os) throws Exception {
        switch (os) {
            case "Windows": {
                terminal = "cmd";
                flag = "/C";
                command = "\"" + getAbsolutePath() + "\\\\src\\\\test\\\\resources\\\\webdriver\\\\windows\\\\edgedriver_win64\\\\msedgedriver.exe" + "\"" + " --version";
                result = executeCommand(terminal, flag, command);
                return extractEdgeDriverVersion(result);
            }
            case "Linux": {
                terminal = "bash";
                flag = "-c";
                command = "\"" + getAbsolutePath() + "/src/test/resources/webdriver/linux/edgedriver-linux64/msedgedriver" + "\"" + " -version";
                result = executeCommand(terminal, flag, command);
                return extractEdgeDriverVersion(result);
            }
        }
        return null;
    }

    private static String getWindowsBrowserVersionFromBinary(String binaryPath, String prefix) throws Exception {
        terminal = "powershell";
        flag = "-Command";
        command = "(Get-Item '" + binaryPath.replace("'", "''") + "').VersionInfo.ProductVersion";
        result = executeCommand(terminal, flag, command);
        if (result == null) return null;
        String trimmed = result.trim();
        return trimmed.isEmpty() ? null : trimmed;
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
