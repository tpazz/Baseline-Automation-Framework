package org.example.tools.webdriver.setup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.core.base.PageObjectExtension;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
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

public class ChromeDriverSetup {

    public static Logger logger = LogManager.getLogger(PageObjectExtension.class);

    public static void main(String os) {
        String chromeBrowserVersion = getChromeBrowserVersion(os);
        String chromeDriverVersion = getChromeDriverVersion(os);
        String shortChromeBrowserVersion = chromeBrowserVersion.split("\\.")[0];
        String shortChromeDriverVersion = chromeDriverVersion.split("\\.")[0];
        logger.info("Chrome Browser version: " + chromeBrowserVersion);
        logger.info("Chromedriver version: " + chromeDriverVersion);
        if (!shortChromeBrowserVersion.equalsIgnoreCase(shortChromeDriverVersion)) {
            logger.info("Downloading compatible Chromedriver...");
            downloadChromeDriver(getChromeDriverURL(shortChromeBrowserVersion, os), os);
        } else
            logger.info("Driver versions are compatible!");
    }

    public static void downloadChromeDriver(String zipurl, String os) {
        closeAllChromeDrivers(os);
        String targetDirectory = null;
        switch (os) {
            case "Windows": targetDirectory = PROJECT_RESOURCES_WINDOWS; break;
            case "Linux": targetDirectory = PROJECT_RESOURCES_LINUX; break;
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

    public static void closeAllChromeDrivers(String os) {
        switch (os) {
            case "Windows": {
                try {
                    String processName = "chromedriver.exe";
                    ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "Taskkill /IM " + processName + " /F");
                    Process process = processBuilder.start();
                    process.waitFor();
                    int exitCode = process.exitValue();
                    if (exitCode == 0) {
                        logger.info("All chromedriver processes have been closed successfully.");
                    } else {
                        logger.error("An error occurred while trying to close chromedriver processes.");
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            } case "Linux": {
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "killall -9 chromedriver");
                    Process process = processBuilder.start();
                    process.waitFor();
                    int exitCode = process.exitValue();
                    if (exitCode == 0) {
                        logger.info("All chromedriver processes have been closed successfully.");
                    } else {
                        logger.error("An error occurred while trying to close chromedriver processes.");
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

    public static String getChromeDriverURL(String compatibleVersion, String os) {
        try {
            URL url = new URL(CHROME_DRIVER_API);
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
                                } else if (platform.equals("linux64") && os.equals("mac-x64")) {
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

    public static String getChromeBrowserVersion(String os) {
        switch (os) {
            case "Windows": {
                String version = "";
                try {
                    Process process = Runtime.getRuntime().exec("reg query HKEY_CURRENT_USER\\Software\\Google\\Chrome\\BLBeacon /v version");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("version")) {
                            version = line.split("\\s+")[line.split("\\s+").length - 1];
                            logger.info("Successfully found Chrome Browser on system");
                            return version;
                        } else {
                            logger.warn("Cannot find installation of Chrome on system. Attempting Project installation...");
                            return checkProjectInstallation(os);
                        }
                    }
                } catch (IOException e) {
                    logger.warn("Cannot find installation of Chrome on system. Attempting Project installation...");
                    return checkProjectInstallation(os);
                }
            } break;
            case "Linux": {
                //String version = "";
                try {
                    Process process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "/opt/google/chrome/chrome -version"});
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String result = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                    logger.info("*************************" + result);
                    String prefix = "Google Chrome ";
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
                        return checkProjectInstallation(os);
                    }
                } catch (IOException e) {
                    logger.warn("Cannot find installation of Chrome on system. Attempting Project installation...");
                    return checkProjectInstallation(os);
                }
            }
            case "Mac": {

            }
        }

        return null;
    }

    public static String checkProjectInstallation(String os) {
        switch (os) {
            case "Windows": {
                try {
                    String currentWorkingDir = System.getProperty("user.dir");
                    String correctedPath = currentWorkingDir.replace("\\", "\\\\");
                    String[] command = {"cmd", "/C", "wmic datafile where name=\"" + correctedPath + "\\\\src\\\\test\\\\resources\\\\browser\\\\windows\\\\chrome\\\\Application\\\\chrome.exe\" get Version /value"};
                    ProcessBuilder builder = new ProcessBuilder(command);
                    Process process = builder.start();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String result = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                        if (result.contains("Version")) {
                            String[] version = result.split("=");
                            logger.info("Project Chrome installation found! " + "(" + version[1].trim() + ")");
                            return version[1].trim();
                        } else {
                            logger.error("Project Chrome installation NOT FOUND!");
                            return null;
                        }
                    }
                } catch (Exception e) {
                    logger.error("Project Chrome installation NOT FOUND!");
                    e.printStackTrace();
                    return null;
                }
            }
            case "Linux": {
                try {
                    Process process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "src/test/resources/browser/linux/chrome/opt/google/chrome/chrome -version"});
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String result = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                    String prefix = "Google Chrome ";
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
            case "Mac": {} break;
        }
        return null;

    }

    public static String checkLocalInstallation(String os) {
        switch (os) {
            case "Windows": {
                try {
                    String[] command = {"cmd", "/C", "wmic datafile where name=\"C:\\\\Program Files\\\\Google\\\\Chrome\\\\Application\\\\chrome.exe\" get Version /value"};
                    ProcessBuilder builder = new ProcessBuilder(command);
                    Process process = builder.start();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String result = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                        if (result.contains("Version")) {
                            String[] version = result.split("=");
                            logger.info("Local Chrome installation found! " + "(" + version[1].trim() + ")");
                            return version[1].trim();
                        } else {
                            logger.warn("Local Chrome installation NOT FOUND!");
                            return null;
                        }
                    }
                } catch (Exception e) {
                    logger.warn("Local Chrome installation NOT FOUND!");
                    e.printStackTrace();
                    return null;
                }
            }
            case "Linux": {
                try {
                    Process process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "/opt/google/chrome/chrome -version"});
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String result = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                    String prefix = "Google Chrome ";
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
            case "Mac": {
                //TODO
            } break;
        }
        return null;
    }

    public static String getChromeDriverVersion(String os) {
        switch (os) {
            case "Windows": {
                try {
                    Process process = Runtime.getRuntime().exec("src/test/resources/webdriver/windows/chromedriver-win64/chromedriver.exe" + " --version");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line = reader.readLine();
                    if (line != null) {
                        String version = line.split(" ")[1];
                        return version;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } break;
            case "Linux": {
                try {
                    Process process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "src/test/resources/webdriver/linux/chromedriver-linux64/chromedriver -version"});
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line = reader.readLine();
                    if (line != null) {
                        String version = line.split(" ")[1];
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
