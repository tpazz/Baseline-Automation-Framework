package org.example.tools.webdriver;

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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.example.core.base.Constants.CHROME_DRIVER_API;
import static org.example.core.base.Constants.PROJECT_RESOURCES_WINDOWS;

public class ChromeDriverSetup {

    public static Logger logger = LogManager.getLogger(PageObjectExtension.class);

    public static void main(String[] args) {
        String chromeBrowserVersion = getChromeBrowserVersion(true);
        String chromeDriverVersion = getChromeDriverVersion(true);
        String shortChromeBrowserVersion = getChromeBrowserVersion(false);
        String shortChromeDriverVersion = getChromeDriverVersion(false);
        logger.info("Chrome Browser version: " + chromeBrowserVersion);
        logger.info("Chromedriver version: " + chromeDriverVersion);
        if (!shortChromeBrowserVersion.equalsIgnoreCase(shortChromeDriverVersion)) {
            logger.info("Downloading compatible Chromedriver...");
            downloadChromeDriver(getChromeDriverURL(getChromeBrowserVersion(false)));
        } else
            logger.info("Driver versions are compatible!");
    }

    public static void downloadChromeDriver(String zipurl) {
        closeAllChromeDrivers();
        String fileUrl = zipurl;
        String targetDirectory = PROJECT_RESOURCES_WINDOWS;
        try {
            URL url = new URL(fileUrl);
            try (InputStream in = url.openStream()) {
                String fileName = getFileNameFromUrl(fileUrl);
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

    public static void closeAllChromeDrivers() {
        try {
            String processName = "chromedriver.exe";
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "Taskkill /IM " + processName + " /F");
            Process process = processBuilder.start();
            process.waitFor();
            int exitCode = process.exitValue();
            if (exitCode == 0) {
                System.out.println("All chromedriver processes have been closed successfully.");
            } else {
                System.out.println("An error occurred while trying to close chromedriver processes.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
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

    public static String getChromeDriverURL(String compatibleVersion) {
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
                                if (platform.equals("win64")) {
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
        return "";
    }

    public static String getChromeBrowserVersion(boolean fullVersion) {
        String version = "";
        try {
            Process process = Runtime.getRuntime().exec("reg query HKEY_CURRENT_USER\\Software\\Google\\Chrome\\BLBeacon /v version");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("version")) {
                    version = line.split("\\s+")[line.split("\\s+").length - 1];

                    if (fullVersion) {
                        return version;
                    } else {
                        String parts = version.split("\\.")[0];
                        return parts;
                    }
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return null;
    }

    public static String getChromeDriverVersion(boolean fullVersion) {
        try {
            Process process = Runtime.getRuntime().exec("src/test/resources/webdriver/windows/chromedriver-win64/chromedriver.exe" + " --version");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if (line != null) {
                String version = line.split(" ")[1];
                if (fullVersion) {
                    return version;
                } else {
                    String parts = version.split("\\.")[0];
                    return parts;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

}
