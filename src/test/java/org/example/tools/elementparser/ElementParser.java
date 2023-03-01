package org.example.tools.elementparser;

import org.example.core.base.PageObjectExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class ElementParser extends PageObjectExtension {

    public void convertElementToLocator(boolean serenitySyntax, boolean substring) {
        if (substring) outputWithSubstring(serenitySyntax);
        else outputWithFullMatch(serenitySyntax);
    }

    private boolean isInteractableElement(WebElement element) {
        boolean interactable = false;
        String tagName = element.getTagName();
        if (tagName.equals("input") || tagName.equals("textarea") || tagName.equals("button") || tagName.equals("select"))
            interactable = true;
        return interactable;
    }

    public void outputWithFullMatch(boolean serenitySyntax) {
        elements = getDriver().findElements(By.cssSelector("*"));
        for (WebElement element : elements) {
            if (isInteractableElement(element)) {
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
    }

    private void outputWithSubstring(boolean serenitySyntax) {
        elements = getDriver().findElements(By.cssSelector("*"));
        for (WebElement element : elements) {
            if (isInteractableElement(element)) {
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

}
