package org.example.tools.webdriver.setup;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.example.core.base.Constants.*;

public class ChromeDriverSetup extends Utils {

    public static void main(String os, ChromeOptions co) throws Exception {
        String chromeBrowserVersion;
        String chromeDriverVersion;
        if (checkLocalInstallation(os) == null) {
            logger.info("No local installation of Chrome found. Checking project installation...");
            if (checkProjectInstallation(os) == null) {
                logger.info("No project installation of Chrome found. Please download Chrome!");
                return;
            } else {
                logger.info("Project installation found! Setting binary to project location...");
                setBinary(os, co);
                chromeBrowserVersion = checkProjectInstallation(os);
            }
        } else {
            logger.info("Local Chrome installation found!");
            chromeBrowserVersion = checkLocalInstallation(os);
        }
        chromeDriverVersion = getChromeDriverVersion(os);
        String shortChromeBrowserVersion = chromeBrowserVersion.split("\\.")[0];
        String shortChromeDriverVersion = chromeDriverVersion.split("\\.")[0];
        logger.info("Chrome Browser version: " + chromeBrowserVersion);
        logger.info("Chromedriver version: " + chromeDriverVersion);
        if (!shortChromeBrowserVersion.equalsIgnoreCase(shortChromeDriverVersion)) {
            logger.info("Downloading compatible Chromedriver...");
            downloadChromeDriver(getChromeDriverURL(shortChromeBrowserVersion, os), os);
            logger.info("Driver versions are now compatible!");
            logger.info("Starting tests...");
        } else {
            logger.info("Driver and browser are compatible!");
            logger.info("Starting tests...");
        }
    }

    private static void setBinary(String os, ChromeOptions co) {
        switch (os) {
            case "Windows": co.setBinary("src/test/resources/browser/windows/chrome/Application/chrome.exe"); break;
            case "Linux": co.setBinary("src/test/resources/browser/linux/chrome/opt/google/chrome/chrome"); break;
        }
    }

    public static void downloadChromeDriver(String zipurl, String os) throws Exception {
        closeAllChromeDrivers(os);
        String targetDirectory = null;
        switch (os) {
            case "Windows": targetDirectory = PROJECT_RESOURCES_WINDOWS; break;
            case "Linux": targetDirectory = PROJECT_RESOURCES_LINUX; break;
        }
        URL url = new URL(zipurl);
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

    public static String checkProjectInstallation(String os) throws Exception {
        switch (os) {
            case "Windows": {
                String currentWorkingDir = System.getProperty("user.dir");
                String correctedPath = currentWorkingDir.replace("\\", "\\\\");
                terminal = "cmd";
                flag = "/C";
                command = "wmic datafile where name=\"" + correctedPath + "\\\\src\\\\test\\\\resources\\\\browser\\\\windows\\\\chrome\\\\Application\\\\chrome.exe\" get Version /value";
                result = executeCommand(terminal,flag,command);
                return extractWindowsBrowserVersion(result, "Version");
            }
            case "Linux": {
                terminal = "bash";
                flag = "-c";
                command = "src/test/resources/browser/linux/chrome/opt/google/chrome/chrome -version";
                result = executeCommand(terminal,flag,command);
                return extractLinuxBrowserVersion(result,"Google Chrome ");
            }
        }
        return null;
    }

    public static String checkLocalInstallation(String os) throws Exception {
        switch (os) {
            case "Windows": {
                terminal = "cmd";
                flag = "/C";
                command = "wmic datafile where name=\"C:\\\\Program Files\\\\Google\\\\Chrome\\\\Application\\\\chrome.exe\" get Version /value";
                result = executeCommand(terminal,flag,command);
                return extractWindowsBrowserVersion(result, "Version");
            }
            case "Linux": {
                terminal = "bash";
                flag = "-c";
                command = "/opt/google/chrome/chrome -version";
                result = executeCommand(terminal,flag,command);
                return extractLinuxBrowserVersion(result, "Google Chrome ");
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
                command = getAbsolutePath() + "\\\\src\\\\test\\\\resources\\\\webdriver\\\\windows\\\\chromedriver-win64\\\\chromedriver.exe --version";
                result = executeCommand(terminal,flag,command);
                return extractChromeDriverVersion(result);
            }
            case "Linux": {
                terminal = "bash";
                flag = "-c";
                command = getAbsolutePath() + "/src/test/resources/webdriver/linux/chromedriver-linux64/chromedriver -version";
                result = executeCommand(terminal,flag,command);
                return extractChromeDriverVersion(result);
            }
        }
        return null;
    }
}
