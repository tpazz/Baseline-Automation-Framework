package org.example.tools.webdriver;

import org.example.core.base.Constants;
import org.example.core.base.PageObjectExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Setup extends PageObjectExtension {

    String browserVersion;
    String fullBrowserVersion;
    String driverVersion;
    String fullDriverVersion;
    String chromeDriverZIP = "chromedriver_win32.zip";

    // Will only work if Chrome Browser is at most 1 version above ChromeDriver
    public void downloadChromeDriverWithDriver() {
        getDriver().get(Constants.CHROMEDRIVER_DOWNLOADS);
        locator = By.xpath("//span[contains(text(),'ChromeDriver " + browserVersion + "')]");
        element = generateElement(locator);
        executeAction(Action.CLICK, element);
        switchTabs(1);
        locator = xPathBuilder("a", "text()", chromeDriverZIP);
        element = generateElement(locator);
        executeAction(Action.CLICK, element);
        for (int i = 0; i < 10; i++) {
            if (verifyFileDownloaded(chromeDriverZIP, false)) break;
            else threadSleep(1000);
        }
    }

    // Work in progress ***
    public void downloadChromeDriver(String version) {
        String chromeDriverUrl = "https://chromedriver.storage.googleapis.com/" + version + "/chromedriver_win32.zip";
        try {
            URL url = new URL(chromeDriverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(new File("chromedriver.zip"));
                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
                fileOutputStream.close();
                inputStream.close();
                System.out.println("ChromeDriver downloaded successfully.");
            } else {
                String versionUrl = "https://chromedriver.storage.googleapis.com/?prefix=" + version;
                URL versionsUrl = new URL(versionUrl);
                HttpURLConnection versionsConnection = (HttpURLConnection) versionsUrl.openConnection();
                versionsConnection.setRequestMethod("GET");
                if (versionsConnection.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(versionsConnection.getInputStream()));
                    String line;
                    String latestVersion = "";
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith(version) && line.endsWith("/")) {
                            latestVersion = line;
                            break;
                        }
                    }
                    reader.close();
                    if (latestVersion.isEmpty()) {
                        System.out.println("No ChromeDriver version found with prefix " + version);
                    } else {
                        downloadChromeDriver(latestVersion);
                    }
                } else {
                    System.out.println("Error while retrieving ChromeDriver versions: HTTP " + versionsConnection.getResponseCode());
                }
            }
        } catch (Exception e) {
            System.out.println("Error while downloading ChromeDriver: " + e.getMessage());
        }
    }

    public void closeAllChromeDrivers() {
        getDriver().close();
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

    public void installChromeDriver() {
        File dir = new File(Constants.PROJECT_RESOURCES_WINDOWS);
        if(!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(Constants.LOCAL_DOWNLOADS + chromeDriverZIP);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();
                File newFile = new File(Constants.PROJECT_RESOURCES_WINDOWS + File.separator + fileName);
                System.out.println("Unzipping to "+newFile.getAbsolutePath());
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean compareBrowserDriverVersion() {
        WebDriver webDriver;
        System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, "src/test/resources/webdriver/windows/chromedriver.exe");
        Capabilities caps = DesiredCapabilities.chrome();
        webDriver = new ChromeDriver(caps);
        caps = ((RemoteWebDriver) webDriver).getCapabilities();
        Map<String, String> a = (Map<String, String>) caps.getCapability("chrome");
        fullDriverVersion = a.get("chromedriverVersion").substring(0,20).split(" ")[0];
        driverVersion = fullDriverVersion.split("\\.")[0];
        fullBrowserVersion = caps.getVersion();
        browserVersion = fullBrowserVersion.split("\\.")[0];
        logger.info("Driver version: " + fullDriverVersion + " (" + driverVersion + ")");
        logger.info("Browser version: " + fullBrowserVersion + " (" + browserVersion + ")");
        webDriver.close();
        if (driverVersion.equalsIgnoreCase(browserVersion)) return true;
        else return false;
    }

}
