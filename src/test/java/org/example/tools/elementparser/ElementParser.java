package org.example.tools.elementparser;

import org.example.core.base.PageObjectExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ElementParser {

    public static Logger logger = LogManager.getLogger(PageObjectExtension.class);

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver","src/test/resources/webdriver/chromedriver-win64/chromedriver.exe");
        WebDriver webDriver = new ChromeDriver();
        webDriver.get("https://the-internet.herokuapp.com");
        outputWithSubstring(getInteractableElements(webDriver), true);
        //outputWithFullMatch(getInteractableElements(webDriver), true);
        webDriver.close();
    }

    private static List<WebElement> getInteractableElements(WebDriver webDriver) {
        List<WebElement> elements = webDriver.findElements(By.cssSelector("*"));
        List<WebElement> interactableElements = new ArrayList<>();
        elements.forEach(e -> { if (isInteractableElement(e)) interactableElements.add(e); });
        return interactableElements;
    }

    private static boolean isInteractableElement(WebElement element) {
        boolean interactable = false;
        String tagName = element.getTagName();
        String attribute = element.getDomAttribute("href");
        if (tagName.equals("input") || tagName.equals("textarea") || tagName.equals("button") || tagName.equals("select")
                || (element.getDomAttribute("href") != null && !element.getDomAttribute("href").isEmpty()))
            interactable = true;
        return interactable;
    }

    public static void outputWithFullMatch(List<WebElement> interactableElements, boolean serenitySyntax) {
        for (WebElement element : interactableElements) {
            try {
                String id = element.getDomAttribute("id");
                if (id != null && !id.isEmpty()) {
                    if (serenitySyntax)
                        logger.info("@FindBy(id = \"" + id + "\")");
                    else
                        logger.info("driver.findElement(By.id(\"" + id + "\"))");
                    continue;
                }
            } catch (Exception e) {}
            try {
                String cssSelector = element.getDomAttribute("class");
                if (cssSelector != null && !cssSelector.isEmpty()) {
                    if (serenitySyntax)
                        logger.info("@FindBy(css = \"" + cssSelector + "\")");
                    else
                        logger.info("driver.findElement(By.cssSelector(\"" + cssSelector + "\"))");
                    continue;
                }
            } catch (Exception e) {}
            try {
                String xpath = element.getDomAttribute("xpath");
                if (xpath != null && !xpath.isEmpty()) {
                    if (serenitySyntax)
                        logger.info("@FindBy(xpath = \"" + xpath + "\")");
                    else
                        logger.info("driver.findElement(By.xpath(\"" + xpath + "\"))");
                    continue;
                }
            } catch (Exception e) {}
            logger.error("Unable to create locator for: " + element);
        }
    }

    private static void outputWithSubstring(List<WebElement> interactableElements, boolean serenitySyntax) {
        for (WebElement element : interactableElements) {
            try {
                String id = element.getDomAttribute("id");
                if (id != null && !id.isEmpty()) {
                    if (serenitySyntax)
                        logger.info("@FindBy(css = \"[id*='" + id + "']\")");
                    else
                        logger.info("driver.findElement(By.cssSelector(\"[id*='" + id + "']\"))");
                    continue;
                }
            } catch (Exception e) {}
            try {
                String classAttr = element.getDomAttribute("class");
                if (classAttr != null && !classAttr.isEmpty()) {
                    if (serenitySyntax)
                        logger.info("@FindBy(css = \"[class*='" + classAttr + "']\")");
                    else
                        logger.info("driver.findElement(By.cssSelector(\"[class*='" + classAttr + "']\"))");
                    continue;
                }
            } catch (Exception e) {}
            try {
                String hrefAttr = element.getDomAttribute("href");
                if (hrefAttr != null && !hrefAttr.isEmpty()) {
                    if (serenitySyntax)
                        logger.info("@FindBy(css = \"[href*='" + hrefAttr.substring(hrefAttr.lastIndexOf('/') + 1) + "']\")");
                    else
                        logger.info("driver.findElement(By.cssSelector(\"[href*='" + hrefAttr.substring(hrefAttr.lastIndexOf('/') + 1) + "']\"))");
                    continue;
                }
            } catch (Exception e) {}
            try {
                String text = element.getText();
                if (text != null && !text.isEmpty()) {
                    if (serenitySyntax)
                        logger.info("@FindBy(css =\":matches('" + text + "')\")");
                    else
                        logger.info("driver.findElement(By.cssSelector(\":matches('" + text + "')\"))");
                    continue;
                }
            } catch (Exception e) {}
            logger.error("Unable to create locator for: " + element);
        }
    }

}
