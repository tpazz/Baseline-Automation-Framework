package org.example.tools.webdriver.setup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.core.base.PageObjectExtension;

import java.io.*;
import java.util.stream.Collectors;

public class FireFoxDriverSetup {

    public static Logger logger = LogManager.getLogger(PageObjectExtension.class);

    public static void main(String[] args) {
        try {
            File directory = new File("C:\\Program Files\\Mozilla Firefox");
            String[] command = {"cmd", "/C", "firefox -v|more"};
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(directory);
            Process process = builder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logger.info("Firefox detected! " + line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String detectFirefox() {
        String line = null;
        try {
            // wmic datafile where name="C:\\Program Files\\Mozilla Firefox\\firefox.exe" get Version /value
            File directory = new File("C:\\Program Files\\Mozilla Firefox");
            String[] command = {"cmd", "/C", "firefox -v|more"};
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(directory);
            Process process = builder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                while ((line = reader.readLine()) != null) {
                    logger.info("Firefox detected! " + line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }

    public static String checkProjectInstallation(String os) {
        switch (os) {
            case "Windows": {
                try {
                    String currentWorkingDir = System.getProperty("user.dir");
                    String correctedPath = currentWorkingDir.replace("\\", "\\\\");
                    String[] command = {"cmd", "/C", "wmic datafile where name=\"" + correctedPath + "\\\\src\\\\test\\\\resources\\\\browser\\\\windows\\\\firefox\\\\firefox.exe\" get Version /value"};
                    ProcessBuilder builder = new ProcessBuilder(command);
                    Process process = builder.start();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        // Collect all lines into a single String
                        String result = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                        if (result.contains("Version")) {
                            String[] version = result.split("=");
                            logger.info("Project Firefox installation detected!");
                            return version[1].trim();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            } break;
            case "Linux": {
                try {
                    Process process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "src/test/resources/browser/linux/firefox/firefox -version"});
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String result = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                    String prefix = "Mozilla Firefox ";
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

            } break;
        }

        return null;
    }

    public static String checkLocalInstallation(String os) {
        switch (os) {
            case "Windows": {
                try {
                    String[] command = {"cmd", "/C", "wmic datafile where name=\"C:\\\\Program Files\\\\Mozilla Firefox\\\\firefox.exe\" get Version /value"};
                    ProcessBuilder builder = new ProcessBuilder(command);
                    Process process = builder.start();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        // Collect all lines into a single String
                        String result = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                        if (result.contains("Version")) {
                            String[] version = result.split("=");
                            logger.info("Local Firefox installation detected!");
                            return version[1].trim();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            } break;
            case "Linux": {
                try {
                    Process process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "/usr/lib/firefox/firefox -version"});
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String result = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                    String prefix = "Mozilla Firefox ";
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

}
