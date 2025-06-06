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
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.example.core.base.Constants.*;

public class EdgeDriverSetup extends Utils {

    public static Logger logger = LogManager.getLogger(PageObjectExtension.class);

    public static void main(String os, EdgeOptions eo) throws Exception {
        String edgeDriverVersion;
        String edgeBrowserVersion = checkLocalInstallation(os);
        if (edgeBrowserVersion == null) {
            logger.info("No local installation of Edge found in default installation directories. Please download Edge!");
        } else {
            logger.info("Local Edge installation found!");
        }
        String shortEdgeBrowserVersion = edgeBrowserVersion.split("\\.")[0];
        try {
            edgeDriverVersion = getEdgeDriverVersion(os);
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
                terminal = "cmd";
                flag = "/C";
                command = "wmic datafile where name=\"C:\\\\Program Files (x86)\\\\Microsoft\\\\Edge\\\\Application\\\\msedge.exe\" get Version /value";
                result = executeCommand(terminal, flag, command);
                return extractWindowsBrowserVersion(result, "Version");
            }
            case "Linux": {
                terminal = "bash";
                flag = "-c";
                command = "/usr/bin/microsoft-edge -version";
                result = executeCommand(terminal,flag,command);
                return extractLinuxBrowserVersion(result, "Microsoft Edge ");
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
}
