# Baseline-Automation-Framework

### This is a template test automation framework that you can clone to make a start on your automated testing.
* Gradle project
* Serenity with Cucumber
* Log4j2
* Java 1.8
* Selenium 
* [AES cipher capabilities](https://github.com/tpazz/Baseline-Serenity-Automation-Framework/tree/main/src/test/java/org/example/cipher)
* [Custom BasePageObject](https://github.com/tpazz/Baseline-Serenity-Automation-Framework/blob/main/src/test/java/org/example/base/PageObjectExtension.java) extension from Serenity's PageObject
* [Automated Download and Install of ChromeDriver](https://github.com/tpazz/Baseline-Serenity-Automation-Framework/blob/main/src/test/java/org/example/webdriver/Setup.java)
* [Element Parser](https://github.com/tpazz/Baseline-Automation-Framework/blob/main/src/test/java/org/example/tools/elementparser/ElementParser.java) tool that parses a webpage and outputs a list of interactable elements as either Selenium *driver.findElement(By)* or Serenity *@FindBy()* syntax 

### The key to make your Selenium tests more robust 
* Use explicit waits: Instead of using the default implicit wait, use explicit waits to tell the test to wait for a certain condition to be met before proceeding. This will make your tests less prone to flakiness caused by elements taking longer to load than expected.

* Use retry logic: Add retry logic to your tests so that they will automatically retry a failed test a certain number of times before giving up. This can help to mitigate flakiness caused by intermittent failures.

* Use a stable test environment: Make sure that your test environment is as stable as possible. This includes things like having a reliable internet connection, a fast CPU, and enough memory to run your tests.

* Use exception handling: Use try-except blocks to handle exceptions that may occur during the execution of your tests. This can help to prevent your tests from failing due to unexpected errors.
---
### Exception Handling
Believe it or not, systematic testing can lead to unpredictable results. In an ideal world, test results should be consistent if nothing changes, but unfortunately this is not the case. (Selenium) Exception handling is perhaps the most crucial aspect towards a robust testing suite, and there are several ways to do it. Most Exceptions arise from interacting with elements in the DOM. These are the most common types:

* **NoSuchElementException**

This exception is raised when Selenium is unable to locate an element on the page using the provided search criteria. You can use ```presenceOfElementLocated``` while catching ```NoSuchElementException``` to wait until the element becomes visible in the DOM: 

```Java
WebDriverWait wait = new WebDriverWait(driver, timeout);
try {
   return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
} catch (org.openqa.selenium.NoSuchElementException e) {
   System.out.println("Error: Element not found.");
   return null;
}
```

* **StaleElementReferenceException**

This exception is raised when an element that was previously found on the page is no longer attached to the DOM. You can use ```presenceOfElementLocated``` and repeatedly attempt to interact with the element while catching ```StaleElementReferenceException```:

```Java
WebDriverWait wait = new WebDriverWait(driver, timeout);
int retry = 0;
while (retry < 3) {
   try {
      return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
   } catch (org.openqa.selenium.StaleElementReferenceException e) {
      retry++;
   }
```

* **InvalidElementStateException**

This exception is raised when an element is in an invalid state (e.g., disabled) for the requested operation. You can use ```elementToBeClickable``` while catching ```InvalidElementStateException```:

```Java
WebDriverWait wait = new WebDriverWait(driver, timeout);
try {
   WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
   element.click();
   return true;
} catch (org.openqa.selenium.InvalidElementStateException e) {
   System.out.println("Error: Element is in an invalid state.");
   return false;
}
```
* **ElementNotVisibleException**

This exception is raised when an element is present on the DOM, but is not visible and so cannot be interacted with. You can use ```visibilityOfElementLocated``` while catching ```ElementNotVisibleException```:

```Java
WebDriverWait wait = new WebDriverWait(driver, timeout);
try {
   return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
} catch (org.openqa.selenium.ElementNotVisibleException e) {
   System.out.println("Error: Element is not visible.");
   return null;
}
```      

* **ElementNotInteractableException** 

Element is present on the page, but is not able to be interacted with. This can happen for several reasons, such as:
* The element is hidden or overlapped by another element
* The element is disabled
* The element is not visible (e.g., it has a style of display: none)
You can use ```elementToBeClickable``` while catching ```ElementNotInteractable``` (if you can click it, its interactable!):

```Java
WebDriverWait wait = new WebDriverWait(driver, timeout);
try {
   WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
   element.click();
   return true;
} catch (org.openqa.selenium.ElementNotInteractableException e) {
   System.out.println("Error: Element is not interactable.");
   return false;
}
```      

* **ElementClickInterceptedException**

The element was found but was not clicked because another element intercepted the click event. You can use ```elementToBeClickable``` while catching ```ElementClickInterceptedException```:

```Java 
WebDriverWait wait = new WebDriverWait(driver, timeout);
try {
   WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
   element.click();
   return true;
} catch (org.openqa.selenium.ElementClickInterceptedException e) {
   System.out.println("Error: Element click intercepted.");
   return false;
}
```      

To handle multiple exceptions when interacting with elements, you can just add more catch statements:

```Java
WebDriverWait wait = new WebDriverWait(driver, timeout);
try {
   WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
   element.click();
   return true;
} catch (ElementClickInterceptedException e) {
   System.out.println("Error: Element click intercepted.");
   return false;
} catch (ElementNotInteractableException e) {
   System.out.println("Error: Element is not interactable.");
   return false;
} catch ( ... ) {}
      
 ```
 
 An alternative but similar approach to handling exceptions is by ignoring the exception while attempting to repeatly try the desired interaction. For example:
 
 ```Java
 WebDriverWait wait = new WebDriverWait(driver, timeout)
  .ignoring(ElementClickInterceptedException.class)
  .ignoring(ElementNotInteractableException.class);
WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
try {
   element.click();
   return true;
} catch (Exception e) {
   return false;
}
```
> **_NOTE:_** An element that is **Clickable** and **Interactable** will validate any interaction with that element

The choice between using the ignoring method in WebDriverWait or catching the exception depends on the specific requirements and constraints of your use case.

Using the ignoring method in WebDriverWait is generally a more concise and readable solution, as it allows you to ignore specific exceptions that may not be relevant for your particular scenario, and only handle the exceptions that you need to handle. This approach can be useful when you are dealing with unexpected or intermittent exceptions that you do not need to handle immediately.

Catching the exception, on the other hand, gives you more control over the behavior of your code in the event of an exception. For example, you can add logging or debugging information to help diagnose the cause of the exception, or you can implement custom error handling logic to handle specific exceptions in different ways.

In general, using the ignoring method is preferred when dealing with unexpected or intermittent exceptions, while catching the exception is preferred when you need to handle specific exceptions in a customised manner. Ultimately, the choice between the two methods depends on the specific requirements and constraints of your use case, and the trade-offs between conciseness, readability, and control.

---

### Extending Serenity's Page Object Class

The Page Object class provided by Selenium is the bread-and-butter library that contains all the methods you need to write your automated tests. But writing your own reusable methods and app-specific helpers is extremely useful, and certainly helps with modularity.

A good place to start is by thinking what methods you wished were in the Base Object class that Selenium does not provide. Some good examples include:

* Exception handling
* Assert tests
* JavaScript helpers 
* Global & reusable methods

I have created my very own extension of this class that you are more than welcome to use and edit for your testing needs. So, instead of extending from Selenium’s Page Object when creating a class for a page, extend from this [Base Page Object](https://github.com/tpazz/Baseline-Serenity-Automation-Framework/blob/main/src/test/java/org/example/base/PageObjectExtension.java). It then essentially becomes one big library with everything you need. 
