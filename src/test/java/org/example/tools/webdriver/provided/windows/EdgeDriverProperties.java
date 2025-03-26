package org.example.tools.webdriver.provided.windows;
import net.thucydides.core.webdriver.DriverSource;
import org.example.tools.webdriver.setup.EdgeDriverSetup;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.CapabilityType;

import java.util.*;

public class EdgeDriverProperties implements DriverSource {
    @Override
    public WebDriver newDriver() {
        EdgeOptions edgeOptions = new EdgeOptions();
        Map<String, Object> map = new HashMap<>();
        try { EdgeDriverSetup.main("Windows", map); }
        catch (Exception e) { e.printStackTrace(); }
        System.setProperty("webdriver.edge.driver", "src/test/resources/webdriver/windows/edgedriver_win64/msedgedriver.exe");
        List<String> args = new ArrayList<>();
        args.add("inprivate");
        args.add("start-maximized");
        if ("true".equalsIgnoreCase(System.getProperty("headless"))) args.add("headless");
        map.put("args", args);
        edgeOptions.setCapability("ms:edgeOptions", map);
        edgeOptions.setCapability(CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR, "ignore");
        return new EdgeDriver(edgeOptions);
    }

    @Override
    public boolean takesScreenshots() {
        return true;
    }
}
