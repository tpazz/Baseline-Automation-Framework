package org.example.tools.webdriver.setup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.core.base.PageObjectExtension;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.example.core.base.Constants.USER_DIR;
import static org.example.core.base.Constants.USER_ENV_HOME;

public class Utils {

    public static Logger logger = LogManager.getLogger(PageObjectExtension.class);
    static String terminal;
    static String flag;
    static String command;
    static String result;

    public static String executeCommand(String terminal, String flag, String command) throws Exception {
        String[] Pcommand = { terminal, flag, command };
        Process process = Runtime.getRuntime().exec(Pcommand);
        logger.info("Executing command [" + Arrays.toString(Pcommand) + "]");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String res = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        logger.info("Execution result [" + res + "]");
        return res;
    }

    public static String extractWindowsBrowserVersion(String result, String prefix) {
        if (result.contains(prefix)) {
            String[] version = result.split("=");
            return version[1].trim();
        } else {
            return null;
        }
    }

    public static String getFileNameFromUrl(String url) {
        int lastSlashIndex = url.lastIndexOf('/');
        return url.substring(lastSlashIndex + 1);
    }

    public static String extractLinuxBrowserVersion(String result, String prefix) {
        int startIndex = result.indexOf(prefix);
        if (startIndex != -1) {
            startIndex += prefix.length();
            int endIndex = result.indexOf(" ", startIndex);
            if (endIndex == -1)
                endIndex = result.indexOf("\n", startIndex);
            if (endIndex == -1)
                endIndex = result.length();
            String version = result.substring(startIndex, endIndex);
            return version;
        } else {
            return null;
        }
    }

    public static void extractZipFile(String zipFilePath, String targetDirectory) throws IOException {
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

    public static String getAbsolutePath() {
        String ABSOLUTE_PATH;
        ABSOLUTE_PATH = Objects.requireNonNullElseGet(USER_ENV_HOME, () -> USER_DIR.replace("\\", "\\\\"));
        logger.info("Using [" + ABSOLUTE_PATH + "] for absolute path...");
        return ABSOLUTE_PATH;
    }

}
