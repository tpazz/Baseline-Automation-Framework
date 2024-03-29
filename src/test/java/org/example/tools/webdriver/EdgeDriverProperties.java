package org.example.tools.webdriver;
import net.thucydides.core.webdriver.DriverSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EdgeDriverProperties implements DriverSource {
    @Override
    public WebDriver newDriver() {
        EdgeOptions edgeOptions = new EdgeOptions();
        List<String> args = Arrays.asList("inprivate", "headless");
        Map<String, Object> map = new HashMap<>();
        map.put("args", args);
        edgeOptions.setCapability("ms:edgeOptions", map);
        return new EdgeDriver(edgeOptions);
    }

    @Override
    public boolean takesScreenshots() {
        return true;
    }
}
