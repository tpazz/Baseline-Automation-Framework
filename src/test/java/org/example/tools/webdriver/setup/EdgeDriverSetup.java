package org.example.tools.webdriver.setup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.core.base.PageObjectExtension;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.example.core.base.Constants.*;

public class EdgeDriverSetup {

    public static Logger logger = LogManager.getLogger(PageObjectExtension.class);

    public static void main(String os) {
        String edgeBrowserVersion = getEdgeBrowserVersion(os);
        String edgeDriverVersion = getEdgeDriverVersion(os);
        String shortEdgeBrowserVersion = edgeBrowserVersion.split("\\.")[0];
        String shortEdgeDriverVersion = edgeDriverVersion.split("\\.")[0];
        logger.info("Edge Browser version: " + edgeBrowserVersion);
        logger.info("EdgeDriver version: " + edgeDriverVersion);
        if (!shortEdgeBrowserVersion.equalsIgnoreCase(shortEdgeDriverVersion)) {
            logger.info("Downloading compatible EdgeDriver...");
            downloadEdgeDriver(getEdgeDriverURL(edgeBrowserVersion, os), os);
        } else
            logger.info("Driver versions are compatible!");
    }

    public static void downloadEdgeDriver(String zipurl, String os) {
        closeAllEdgeDrivers(os);
        String targetDirectory = null;

        switch (os) {
            case "Windows": {
                targetDirectory = PROJECT_RESOURCES_WINDOWS;
                if (zipurl.contains("edgedriver"))
                    targetDirectory = PROJECT_RESOURCES_WINDOWS+"edgedriver_win64";
            } break;
            case "Linux": {
                targetDirectory = PROJECT_RESOURCES_LINUX;
                if (zipurl.contains("edgedriver"))
                    targetDirectory = PROJECT_RESOURCES_LINUX+"edgedriver-linux64";
            } break;
            case "Mac": targetDirectory = PROJECT_RESOURCES_MAC; break;
        }
        try {
            URL url = new URL(zipurl);
            try (InputStream in = url.openStream()) {
                String fileName = getFileNameFromUrl(zipurl);
                Path outputPath = Paths.get(targetDirectory, fileName);
                Files.copy(in, outputPath, StandardCopyOption.REPLACE_EXISTING);
                logger.info("File downloaded to: " + outputPath);
                extractZipFile(outputPath.toString(), targetDirectory);
                Files.delete(outputPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeAllEdgeDrivers(String os) {
        switch (os) {
            case "Windows": {
                try {
                    String processName = "msedgedriver.exe";
                    ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "Taskkill /IM " + processName + " /F");
                    Process process = processBuilder.start();
                    process.waitFor();
                    int exitCode = process.exitValue();
                    if (exitCode == 0) {
                        logger.info("All edgedriver processes have been closed successfully.");
                    } else {
                        logger.error("An error occurred while trying to close edgedriver processes.");
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            } case "Linux": {
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "killall -9 msedgedriver");
                    Process process = processBuilder.start();
                    process.waitFor();
                    int exitCode = process.exitValue();
                    if (exitCode == 0) {
                        logger.info("All edgedriver processes have been closed successfully.");
                    } else {
                        logger.error("An error occurred while trying to close edgedriver processes.");
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            } case "Mac": {};
        }

    }


    private static String getFileNameFromUrl(String url) {
        int lastSlashIndex = url.lastIndexOf('/');
        return url.substring(lastSlashIndex + 1);
    }

    private static void extractZipFile(String zipFilePath, String targetDirectory) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;
            byte[] buffer = new byte[1024];
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String entryName = entry.getName();
                Path entryPath = Paths.get(targetDirectory, entryName);
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    try (OutputStream outputStream = Files.newOutputStream(entryPath)) {
                        int bytesRead;
                        while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                }
                zipInputStream.closeEntry();
            }
        }
    }

    public static String getEdgeDriverURL(String compatibleVersion, String os) {
        // https://msedgewebdriverstorage.blob.core.windows.net/edgewebdriver/LATEST_RELEASE_<MAJOR_VERSION>
        switch (os) {
            case "Windows": {
                return EDGE_DRIVER_API+compatibleVersion+"/edgedriver_win64.zip";
            }
            case "Linux": {
                return EDGE_DRIVER_API+compatibleVersion+"/edgedriver_linux64.zip";
            }
            case "Mac": {
                return EDGE_DRIVER_API+compatibleVersion+"/edgedriver_mac64.zip";
            }
        }
        return null;
    }

    public static String checkProjectInstallation(String os) {
        switch (os) {
            case "Windows": {
                logger.info("Project Edge Windows Installation not supported");
            } break;
            case "Linux":{
                try {
                    Process process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "src/test/resources/browser/linux/edge/opt/microsoft/msedge/msedge -version"});
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String result = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                    String prefix = "Microsoft Edge ";
                    int startIndex = result.indexOf(prefix);

                    if (startIndex != -1) {
                        // Move the start index to the beginning of the version number
                        startIndex += prefix.length();

                        // Find the end of the version number assuming it ends with a space or newline
                        int endIndex = result.indexOf(" ", startIndex);
                        if (endIndex == -1) { // If no space is found, try a newline
                            endIndex = result.indexOf("\n", startIndex);
                        }
                        if (endIndex == -1) { // If neither space nor newline is found, assume end of string
                            endIndex = result.length();
                        }

                        // Extract the version number
                        String version = result.substring(startIndex, endIndex);
                        System.out.println("Extracted Version Number: " + version);
                        return version;
                    } else {
                        logger.warn("Local Chrome installation NOT FOUND!");
                        return null;
                    }
                } catch (IOException e) {
                    logger.warn("Cannot find installation of Chrome on system. Attempting Project installation...");
                    return null;
                }
            }
            case "Mac":{

            } break;
        }
        return null;
    }

    public static String getEdgeBrowserVersion(String os) {
        switch (os) {
            case "Windows": {
                try {
                    String version = "";
                    Process process = Runtime.getRuntime().exec("reg query HKEY_CURRENT_USER\\Software\\Microsoft\\Edge\\BLBeacon /v version");
                    // wmic datafile where name="C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe" get Version /value
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("version")) {
                            version = line.split("\\s+")[line.split("\\s+").length - 1];
                            logger.info("Successfully found Edge Browser on system");
                            return version;
                        }
                    }
                } catch (IOException e) {
                    logger.warn("Cannot find installation of Edge on system.");
                    logger.info("Please install Microsoft Edge in order to successfully execute the test suite with Edge.");
                    return null;
                }
            } break;
            case "Linux":{
                try {
                    Process process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "src/test/resources/browser/linux/edge/opt/microsoft/msedge/msedge -version"});
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String result = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                    String prefix = "Microsoft Edge ";
                    int startIndex = result.indexOf(prefix);

                    if (startIndex != -1) {
                        // Move the start index to the beginning of the version number
                        startIndex += prefix.length();

                        // Find the end of the version number assuming it ends with a space or newline
                        int endIndex = result.indexOf(" ", startIndex);
                        if (endIndex == -1) { // If no space is found, try a newline
                            endIndex = result.indexOf("\n", startIndex);
                        }
                        if (endIndex == -1) { // If neither space nor newline is found, assume end of string
                            endIndex = result.length();
                        }

                        // Extract the version number
                        String version = result.substring(startIndex, endIndex);
                        System.out.println("Extracted Version Number: " + version);
                        return version;
                    } else {
                        logger.warn("Local Chrome installation NOT FOUND!");
                        return null;
                    }
                } catch (IOException e) {
                    logger.warn("Cannot find installation of Chrome on system. Attempting Project installation...");
                    return null;
                }
            }
            case "Mac":{} break;
        }
        return null;
    }

    public static String checkLocalInstallation(String os) {
        switch (os) {
            case "Windows": {
                try {
                    String[] command = {"cmd", "/C", "wmic datafile where name=\"C:\\\\Program Files (x86)\\\\Microsoft\\\\Edge\\\\Application\\\\msedge.exe\" get Version /value"};
                    ProcessBuilder builder = new ProcessBuilder(command);
                    Process process = builder.start();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        // Collect all lines into a single String
                        String result = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                        if (result.contains("Version")) {
                            String[] version = result.split("=");
                            logger.info("Local Edge installation detected!");
                            return version[1].trim();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            } break;
            case "Linux": {
                logger.info("Local installation of Edge Linux not supported");

            } break;
            case "Mac": {

            }
        }

        return null;
    }

    public static String getEdgeDriverVersion(String os) {
        switch (os) {
            case "Windows": {
                try {
                    Process process = Runtime.getRuntime().exec("src/test/resources/webdriver/windows/edgedriver_win64/msedgedriver.exe" + " --version");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line = reader.readLine();
                    if (line != null) {
                        String version = line.split(" ")[3];
                        return version;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } break;
            case "Linux": {
                try {
                    Process process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "src/test/resources/webdriver/linux/edgedriver-linux64/msedgedriver -version"});
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line = reader.readLine();
                    if (line != null) {
                        String version = line.split(" ")[3];
                        return version;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } break;
            case "Mac": {
                try {
                    Process process = Runtime.getRuntime().exec("src/test/resources/webdriver/mac/chromedriver-mac64/chromedriver" + " --version");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line = reader.readLine();
                    if (line != null) {
                        String version = line.split(" ")[1];
                        return version;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
