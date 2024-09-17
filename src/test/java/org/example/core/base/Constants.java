package org.example.core.base;

public class Constants {

    // DIRECTORIES
    public static final String USER_HOME = System.getProperty("user.home");
    public static final String USER_ENV_HOME = System.getenv("SYSTEM_DEFAULTWORKINGDIRECTORY");
    public static final String LOCAL_DOWNLOADS = USER_HOME + "\\Downloads\\";
    public static final String PROJECT_RESOURCES_WINDOWS = USER_ENV_HOME + "\\src\\test\\resources\\webdriver\\windows\\";
    public static final String PROJECT_RESOURCES_LINUX = USER_ENV_HOME + "/src/test/resources/webdriver/linux/";

    // OTHER URLS
    public static final String CHROME_DRIVER_API = "https://googlechromelabs.github.io/chrome-for-testing/known-good-versions-with-downloads.json";
    public static final String EDGE_DRIVER_API = "https://msedgedriver.azureedge.net/";
    public static final String EDGE_DRIVER_LATEST_STABLE = "https://msedgewebdriverstorage.blob.core.windows.net/edgewebdriver/LATEST_RELEASE_";
    public static final String THE_INTERNET_HEROKU = "https://the-internet.herokuapp.com/";

    // CREDENTIALS
    public static final String USERNAME = "testUsername";
    public static final String PASSWORD = "P5$$w0rd!";

}
