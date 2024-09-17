package org.example.core.base;

import net.serenitybdd.core.pages.PageObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.example.core.base.PageObjectExtension.Action.*;
import static org.junit.Assert.assertEquals;

public class PageObjectExtension extends PageObject {

    public static Logger logger = LogManager.getLogger(PageObjectExtension.class);

    public String actual;
    public WebElement element;
    public List<WebElement> elements = new ArrayList<>();
    public By locator;
    public WebDriverWait wait;

    public enum Action { CLICK, ENTER_TEXT, SELECT_FROM_DROPDOWN, ENTER_KEY, GET_CSS, GET_ATTRIBUTE, GET_DROPDOWN_TEXT, GET_TEXT }
    public enum WaitType { ENABLED, CLICKABLE, DISABLED, VISIBLE, NOT_VISIBLE }

    public String action(Action action, WebElement webElement, String value) {
        return action(action, webElement, value, 5);
    }

    public String action(Action action, WebElement webElement) {
        return action(action, webElement, "", 5);
    }

    public String action(Action action, WebElement webElement, String value, int timeOut) {
        final String[] returnValue = {""};
        new WebDriverWait(getDriver(), timeOut).ignoring(Exception.class).until((WebDriver d) -> {
            try {
                switch (action) {
                    case CLICK                : webElement.click();                              break;
                    case ENTER_TEXT           : enterText(webElement, value);                    break;
                    case ENTER_KEY            : enterKey(webElement, value);                     break;
                    case SELECT_FROM_DROPDOWN : selectTextFromDropdown(webElement, value);       break;
                    case GET_CSS              : returnValue[0] = webElement.getCssValue(value);  break;
                    case GET_ATTRIBUTE        : returnValue[0] = webElement.getAttribute(value); break;
                    case GET_DROPDOWN_TEXT    : returnValue[0] = getDropDownText(webElement);    break;
                    case GET_TEXT             : returnValue[0] = webElement.getText();           break;
                }
            } catch (Exception e) {
                threadWait(1);
                return false;
            }
            return true;
        });
        return returnValue[0];
    }

    private void enterKey(WebElement webElement, String keys) {
        switch (keys) {
            case "DOWN_ARROW"  : webElement.sendKeys(Keys.ARROW_DOWN);  break;
            case "UP_ARROW"    : webElement.sendKeys(Keys.ARROW_UP);    break;
            case "LEFT_ARROW"  : webElement.sendKeys(Keys.ARROW_LEFT);  break;
            case "RIGHT_ARROW" : webElement.sendKeys(Keys.ARROW_RIGHT); break;
            case "ESCAPE"      : webElement.sendKeys(Keys.ESCAPE);      break;
            case "ENTER"       : webElement.sendKeys(Keys.ENTER);       break;
            // add more key cases
        }
    }

    public void waitForElement(WebElement webElement, WaitType waitType) {
        waitForElement(webElement, waitType, 5);
    }

    public void waitForElement(WebElement webElement, WaitType waitType, int timeOut) {
        wait = new WebDriverWait(getDriver(), timeOut);
        switch (waitType) {
            case ENABLED     : wait.until(ExpectedConditions.attributeToBe(element, "enabled", "true")); break;
            case DISABLED    : wait.until(ExpectedConditions.attributeToBe(element, "enabled", "false"));break;
            case CLICKABLE   : wait.until(ExpectedConditions.elementToBeClickable(element));                          break;
            case VISIBLE     : wait.until(ExpectedConditions.visibilityOf(webElement));                               break;
            case NOT_VISIBLE : wait.until(ExpectedConditions.invisibilityOf(webElement));                             break;
        }
    }

    public void waitForDisappear(String id, int timeOut) {
        int noElements = getDriver().findElements(By.id(id)).size();
        boolean loaded = false;
        for (int i = 0; i < timeOut; i++) {
            if (noElements == 0) {
                loaded = true;
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!loaded) Assert.fail();
    }

    public WebElement generateElement(By locator) {
        wait = new WebDriverWait(getDriver(), 5);
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        return getDriver().findElement(locator);
    }

    public WebElement generateElement(By locator, int seconds) {
        wait = new WebDriverWait(getDriver(), seconds);
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        return getDriver().findElement(locator);
    }

    public List<WebElement> generateElements(By type) {
        return getDriver().findElements(type);
    }

    public WebElement buildElement(String text, String loc, String args) {
        switch (loc) {
            case "text" : locator = xPathBuilder(args, "text()", text); break;
            case "id" : locator = By.id(args); break;
            case "xpath" : locator = By.xpath(args); break;
            case "cssSelector" : locator = By.cssSelector(args); break;
            case "tagName" : locator = By.tagName(args); break;
            case "className": locator = By.className(args); break;
            case "linkText" : locator = By.linkText(args); break;
            case "partialLinkText" : locator = By.partialLinkText(args); break;
        }
        return generateElement(locator);
    }

    public List<WebElement> buildElements(String text, String loc, String args) {
        switch (loc) {
            case "text" : locator = xPathBuilder(args, "text()", text); break;
            case "id" : locator = By.id(args); break;
            case "xpath" : locator = By.xpath(args); break;
            case "cssSelector" : locator = By.cssSelector(args); break;
            case "tagName" : locator = By.tagName(args); break;
            case "className": locator = By.className(args); break;
            case "linkText" : locator = By.linkText(args); break;
            case "partialLinkText" : locator = By.partialLinkText(args); break;
        }
        return generateElements(locator);
    }

    private void selectTextFromDropdown(WebElement webElement, String text) {
        Select select = new Select(webElement);
        select.selectByVisibleText(text);
    }

    private String getDropDownText(WebElement webElement) {
        String[] split = action(GET_TEXT, webElement).split("\\r?\\n");
        return split[0].trim();
    }

    private void enterText(WebElement webElement, String text) {
        webElement.clear();
        webElement.sendKeys(text);
    }

    public void selectText(String text) {
        locator = xPathBuilder("*","text()",text);
        element = generateElement(locator);
        action(CLICK, element, "");
    }

    public void selectElementWithText(String elementType, String text) {
        locator = xPathBuilder(elementType, "text()", text);
        element = generateElement(locator);
        action(CLICK, element, "");
    }

    public void selectElementWithValue(String elementType, String attribute, String value) {
        locator = By.xpath("//" + elementType + "[contains(@" + attribute + ", '" + value + "')]");
        element = generateElement(locator);
        action(CLICK, element, "");
    }

    // ************************************************* JS ************************************************************

    public void acceptJSAlert() {
        wait = new WebDriverWait(getDriver(),30);
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        getDriver().switchTo().alert();
        alert.accept();
    }

    public void dismissJSAlert() {
        wait = new WebDriverWait(getDriver(),30);
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        getDriver().switchTo().alert();
        alert.dismiss();
    }

    public void enterTextJSAlert(String text) {
        wait = new WebDriverWait(getDriver(),30);
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        getDriver().switchTo().alert();
        alert.sendKeys(text);
        alert.accept();
    }

    public String extractBackgroundColourJS(String id) {
        String script = "return window.getComputedStyle(document.querySelector('#" + id + "'),':before').getPropertyValue('background-color')";
        JavascriptExecutor js = (JavascriptExecutor)getDriver();
        return (String) js.executeScript(script);
    }

    public String extractPropertyValueJS(WebElement webElement) {
        String script = "return arguments[0].value";
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        return (String) js.executeScript(script, webElement);
    }

    public void makeVisibleJS(WebElement webElement) {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("arguments[0].style.display='block';", webElement);
    }

    public void openNewTabJS() {
        String script = "window.open()";
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript(script);
    }

    public void clickJS(WebElement webElement) {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("arguments[0].click();", webElement);
    }

    public void waitForPageToBeLoadedJS() {
        JavascriptExecutor js = (JavascriptExecutor)getDriver();
        for (int wait=0; wait<30; wait++) {
            try { Thread.sleep(1000); }
            catch (InterruptedException e) {}
            if (js.executeScript("return document.readyState").toString().equals("complete")) { break; }
        }
    }

    public void waitForPageLoadedJS() {
        ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString().equals("complete");
            }
        };
        try {
            Thread.sleep(1000);
            WebDriverWait wait = new WebDriverWait(getDriver(), 10);
            wait.until(expectation);
        } catch (Throwable error) {
            Assert.fail("Timeout waiting for Page Load Request to complete.");
        }
    }

    // ************************************************* GENERIC *******************************************************

    public void handleAlert(String option) {
        wait = new WebDriverWait(getDriver(), 5);
        wait.until(ExpectedConditions.alertIsPresent());
        if (option.equalsIgnoreCase("OK")) getDriver().switchTo().alert().accept();
        else getDriver().switchTo().alert().dismiss();
    }

    public void switchTabs(int tabNo) {
        ArrayList<String> tabs2 = new ArrayList<> (getDriver().getWindowHandles());
        getDriver().switchTo().window(tabs2.get(tabNo));
        //getDriver().manage().window().setSize(new Dimension(1920, 1080));
        //waitForPageLoadedJS();
    }

    public void moveToElementClick(WebElement webElement) {
        Actions actions = new Actions(getDriver());
        actions.moveToElement(webElement).click().build().perform();
    }

    public String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd");
        Date date = new Date();
        return formatter.format(date);
    }

    public By xPathBuilder(String elementType, String attribute, String value) {
        return By.xpath("//" + elementType + "[" + attribute + "='" + value + "']");
    }

    public void threadWait(int millis) {
        try { Thread.sleep(millis); } catch (Exception e) { logger.error(e); }
    }

    public void navigateTo(String webpage) {
        getDriver().get(webpage);
        waitForPageLoadedJS();
    }

    // ******************************************* FILE I/O ************************************************************

    public List<List<String>> getFileIntoArray() {
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("src/test/resources/csvFile.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        } catch (Exception e) { logger.error("Error converting file into array"); }
        return records;
    }

    public String readFromFile(String key) {
        final String[] value = {""};
        getFileIntoArray().forEach(r -> {
            if (r.get(0).equals(key))
                value[0] = r.get(1);
        });
        logger.info(value[0]);
        return value[0];
    }

    public void getArrayIntoFile(List<List<String>> records) {
        try (FileWriter writer = new FileWriter("src/test/resources/csvFile.csv")) {
            records.forEach(r -> {
                try {
                    writer.write(r.get(0) + "," + r.get(1) + "\n");
                } catch (IOException e) {
                    logger.error("Error writing to File");
                }
            });
        } catch (Exception e) { logger.error("Error converting array into file"); }
    }

    public void writeToFile(String key, String value) {
        List<List<String>> records = getFileIntoArray();
        records.forEach(r -> {
            if (r.get(0).equals(key))
                r.set(1,value);
            getArrayIntoFile(records);
        });
    }

    public void incrementAndWrite(String key) {
        String curr = readFromFile(key);
        writeToFile(key,String.valueOf(Integer.parseInt(curr) + 1));
    }

    // ****************************************** LOGGER ***************************************************************

    public void startLogger() { logger.info("~TESTS STARTED~"); }

    public void endLogger() { logger.info("~TEST FINISHED~"); }

    // ***************************************** VERIFY METHODS ********************************************************

    public boolean verifyFileDownloaded(String fileName, boolean delete) {
        String expected = "File Successfully Downloaded";
        actual = "";
        File dir = new File(Constants.LOCAL_DOWNLOADS);
        File[] dirContents = dir.listFiles();
        for (int i = 0; i < dirContents.length; i++) {
            if (dirContents[i].getName().equals(fileName)) {
                actual = expected;
                if (delete) dirContents[i].delete();
                verify(expected, actual);
                return true;
            }
        }
        return false;
    }

    public void verifyURL(String expected) {
        String url = getDriver().getCurrentUrl();
        if (url.equalsIgnoreCase(expected))
            actual = expected;
        else
            actual = url;
        verify(expected, actual);
    }

    public void verifyPageTitle(String expected) {
        actual = getDriver().getTitle();
        verify(expected, actual);
    }

    public void verifyElementText(WebElement webElement, String expected) {
        actual = action(GET_TEXT, webElement, "");
        verify(expected, actual);
    }

    public void verifyTextOnPage(String elementType, String expected) {
        actual = expected;
        locator = xPathBuilder(elementType, "text()", expected);
        generateElement(locator);
        verify(expected, actual);
    }

    public void verifyTextDoesNotExistOnPage(String elementType, String notExpected) {
        locator = xPathBuilder(elementType, "text()", notExpected);
        elements = generateElements(locator);
        actual = String.valueOf(elements.size());
        verify("0", actual);
    }

    public void verifyElementState(WebElement webElement, boolean enabled) {
        actual = String.valueOf(webElement.isEnabled());
        if (enabled) verify("true", actual);
        else verify("false", actual);
    }

    public void verifyElementDisplayed(WebElement webElement, boolean displayed) {
        actual = String.valueOf(webElement.isDisplayed());
        if (displayed) verify("true", actual);
        else verify("false", actual);
    }

    public void verifyElementSelected(WebElement webElement, boolean selected) {
        actual = String.valueOf(webElement.isSelected());
        if (selected) verify("true", actual);
        else verify("false", actual);
    }

    public void verify(String expected, String actual) {
        String message = "***** FAIL: [" + expected + " =/= " + actual + "] *****";
        if (expected.equalsIgnoreCase(actual)) logger.info("***TEST SUCCEEDED***");
        else logger.error(message);
        assertEquals(message, expected, actual);
    }

}