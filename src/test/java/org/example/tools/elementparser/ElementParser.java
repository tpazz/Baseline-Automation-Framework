package org.example.tools.elementparser;

import org.example.core.base.PageObjectExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class ElementParser extends PageObjectExtension {

    public void convertElementToLocator(boolean serenitySyntax, boolean substring) {
        if (substring) outputWithSubstring(getInteractableElements(), serenitySyntax);
        else outputWithFullMatch(getInteractableElements(), serenitySyntax);
    }

    private List<WebElement> getInteractableElements() {
        elements = getDriver().findElements(By.cssSelector("*"));
        List<WebElement> interactableElements = new ArrayList<>();
        elements.forEach(e -> { if (isInteractableElement(e)) interactableElements.add(e); });
        return interactableElements;
    }

    private boolean isInteractableElement(WebElement element) {
        boolean interactable = false;
        String tagName = element.getTagName();
        if (tagName.equals("input") || tagName.equals("textarea") || tagName.equals("button") || tagName.equals("select"))
            interactable = true;
        return interactable;
    }

    public void outputWithFullMatch(List<WebElement> interactableElements, boolean serenitySyntax) {
        for (WebElement element : interactableElements) {
            try {
                String id = element.getAttribute("id");
                if (id != null && !id.isEmpty()) {
                    if (serenitySyntax)
                        logger.info("@FindBy(id = \"" + id + "\")");
                    else
                        logger.info("driver.findElement(By.id(\"" + id + "\"))");
                    continue;
                }
            } catch (Exception e) {}
            try {
                String cssSelector = element.getAttribute("class");
                if (cssSelector != null && !cssSelector.isEmpty()) {
                    if (serenitySyntax)
                        logger.info("@FindBy(css = \"" + cssSelector + "\")");
                    else
                        logger.info("driver.findElement(By.cssSelector(\"" + cssSelector + "\"))");
                    continue;
                }
            } catch (Exception e) {}
            try {
                String xpath = element.getAttribute("xpath");
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

    private void outputWithSubstring(List<WebElement> interactableElements, boolean serenitySyntax) {
        for (WebElement element : interactableElements) {
            try {
                String id = element.getAttribute("id");
                if (id != null && !id.isEmpty()) {
                    if (serenitySyntax)
                        logger.info("@FindBy(css = \"[id*='" + id + "']\"");
                    else
                        logger.info("driver.findElement(By.cssSelector(\"[id*='" + id + "']\"))");
                    continue;
                }
            } catch (Exception e) {}
            try {
                String classAttr = element.getAttribute("class");
                if (classAttr != null && !classAttr.isEmpty()) {
                    if (serenitySyntax)
                        logger.info("@FindBy(css = \"[class*='" + classAttr + "']\"");
                    else
                        logger.info("driver.findElement(By.cssSelector(\"[class*='" + classAttr + "']\"))");
                    continue;
                }
            } catch (Exception e) {}
            try {
                String hrefAttr = element.getAttribute("href");
                if (hrefAttr != null && !hrefAttr.isEmpty()) {
                    if (serenitySyntax)
                        logger.info("@FindBy(css = \"[href*='" + hrefAttr.substring(hrefAttr.lastIndexOf('/') + 1) + "']\"");
                    else
                        logger.info("driver.findElement(By.cssSelector(\"[href*='" + hrefAttr.substring(hrefAttr.lastIndexOf('/') + 1) + "']\"))");
                    continue;
                }
            } catch (Exception e) {}
            try {
                String text = element.getText();
                if (text != null && !text.isEmpty()) {
                    if (serenitySyntax)
                        logger.info("@FindBy(css =\":matches('" + text + "')\"");
                    else
                        logger.info("driver.findElement(By.cssSelector(\":matches('" + text + "')\"))");
                    continue;
                }
            } catch (Exception e) {}
            logger.error("Unable to create locator for: " + element);
        }
    }

}
