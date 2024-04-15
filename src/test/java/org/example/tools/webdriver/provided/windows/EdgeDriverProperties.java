package org.example.tools.webdriver.provided.windows;
import net.thucydides.core.webdriver.DriverSource;
import org.example.tools.webdriver.setup.EdgeDriverSetup;
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
        EdgeDriverSetup.main("Windows");
        System.setProperty("webdriver.edge.driver", "src/test/resources/webdriver/windows/edgedriver_win64/msedgedriver.exe");
        List<String> args = Arrays.asList("inprivate", "headless", "start-maximized");
        Map<String, Object> map = new HashMap<>();
        map.put("args", args);
	    edgeOptions.setCapability("ms:edgeOptions", map);
        if (EdgeDriverSetup.checkLocalInstallation("Windows") == null)
                map.put("binary", "src/test/resources/browser/linux/edge/opt/microsoft/msedge/msedge");
        return new EdgeDriver(edgeOptions);
    }

    @Override
    public boolean takesScreenshots() {
        return true;
    }
}
