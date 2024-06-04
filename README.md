# Baseline-Automation-Framework

### This is a template test automation framework that you can clone to make a start on your automated testing.
* Self-contained Java + Gradle project
* Selenium
* Serenity with Cucumber
* Logging
* Parallel Testing 
* [AES cipher capabilities](https://github.com/tpazz/Baseline-Automation-Framework/blob/master/src/test/java/org/example/tools/cipher/AES.java)
* [Custom BasePageObject](https://github.com/tpazz/Baseline-Automation-Framework/blob/master/src/test/java/org/example/core/base/PageObjectExtension.java) extension from Serenity's PageObject
* [Automated WebDriver Compatibility Download](https://github.com/tpazz/Baseline-Automation-Framework/blob/master/src/test/java/org/example/tools/webdriver/setup/ChromeDriverSetup.java)
* [Element Parser](https://github.com/tpazz/Baseline-Automation-Framework/blob/master/src/test/java/org/example/tools/elementparser/ElementParser.java) tool that parses a webpage and outputs a list of interactable elements as either Selenium ```driver.findElement(By)``` or Serenity ```@FindBy()``` syntax.
* [ActionSteps](https://github.com/tpazz/Baseline-Automation-Framework/blob/master/src/test/java/org/example/core/steps/ActionSteps.java) class that enables you to write automated scripts stright from Gherkin, without needing to create Page Object classes!
---

### Getting started

**Recommended** - Run tests from IntelliJ IDE:
1. Clone the repository
2. Open project in IDE of your choice (Intellij is recommended)
3. Build the **Gradle** project
4. Add a new Gradle configuration, specifying the operating system and driver you wish to use for executing the suite (e.g. ```clean test aggregate reports --info -Pos=windows -Pdriver=chrome```)
5. Run!

**OR** Run tests directly from the command line using provided gradle wrapper scripts:
#### Windows 
1. Clone the project
2. Enter: `gradlew.bat <arguments> -Pos=windows -pdriver=<DESIRED DRIVER>` 

#### Linux
You may need to `sudo chmod -R 755 Baseline-Automation-Framework` beforehand, or `chmod +x gradle-env-wrapper-linux && chmod +x src/test/resources/webdriver/linux/chromedriver-linux64/chromedriver && chmod +x src/test/resources/jdks/linux/bin/*`
1. Clone the project
2. Enter: `./gradlew-env-wrapper-linux <arguments> -Pos=linux -Pdriver=<DESIRED DRIVER>`

#### Mac
Coming soon...

---
### Compatibility 
This Baseline Automation Suite has been built to be as self-contained as possible, with JDK's, environment variables, browser installations, webdriver installations and automated webdriver management included so you don't have to manually configure any settings or download Web Drivers whenever your browser gets updated. However, due to the nature of some browsers and operating systems, there are some limitations.

|         | Project copy of Chrome | Project copy of Edge | Project copy of Firefox | Chromedriver compatibility auto-install | MSEdgeDriver compatibility auto-install | Geckodriver compatibility auto-install | Checks for system Chrome installation | Checks for system Edge installation | Checks for system Firefox installation |
|---------|------------------------|----------------------|-------------------------|-----------------------------------------|-----------------------------------------|----------------------------------------|---------------------------------------|-------------------------------------|----------------------------------------|
| Windows (x64) | YES                    | NO                   | YES                     | YES                                     | YES                                     | NO                                     | YES                                   | YES                                 | YES                                    |
| Linux (Debian)   | YES                    | YES                  | YES                     | YES                                     | YES                                     | NO                                     | YES                                   | NO                                  | YES                                    |
| Mac     | TBC                    | TBC                  | TBC                     | TBC                                     | TBC                                     | TBC                                    | TBC                                   | TBC                                 | TBC                                    |

Because there is no ***easy*** way to detect local installations of browsers on Linux based systems, the automation suite will use the packaged browsers included in the repository. Below you can see the flow of how the drivers are updated.

![image](https://github.com/tpazz/Baseline-Automation-Framework/assets/36413640/da8da302-4066-4206-958d-6afe82a5035b)

---
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

### Element Definition Good Practice

<p align=center>
<img width="650" alt="image" src="https://user-images.githubusercontent.com/36413640/222150711-a632cd15-0864-495d-8fb2-c6e705fd8c0c.png">
</p>

Using the [Element Parser](https://github.com/tpazz/Baseline-Automation-Framework/blob/main/src/test/java/org/example/tools/elementparser/ElementParser.java) tool to convert elements into their respected locator forms is very useful for quickly defining WebElement Page Objects. 

For instance, say you had an element with the following ID value: ```b11-Config_ButtonGroupItem_Dst_Barge```. In there you have some dynamic gibberish that will eventually fail if a full matching were to be used. Instead, find by CSS and use ```*=``` for partial matching and keep the static value so you end up with ```@FindBy(css = "[id*='Dst_Barge']")```.  

With regards to interacting with a text-based element, it is much easier to ```findElementBy(By.xpath(//span[text()='someText']))```. Even easier, you can use the following methods I have provided in [Custom BasePageObject](https://github.com/tpazz/Baseline-Serenity-Automation-Framework/blob/main/src/test/java/org/example/base/PageObjectExtension.java) class:
```Java
locator = xPathBuilder("span", "text()", "someText");
element = generateElement(locator);
```
---

### Extending Serenity's Page Object Class

The Page Object class provided by Selenium is the bread-and-butter library that contains all the methods you need to write your automated tests. But writing your own reusable methods and app-specific helpers is extremely useful, and certainly helps with modularity.

A good place to start is by thinking what methods you wished were in the Base Object class that Selenium does not provide. Some good examples include:

* Exception handling
* Assert tests
* JavaScript helpers 
* Global & reusable methods

I have created my very own extension of this class that you are more than welcome to use and edit for your testing needs. So, instead of extending from Selenium’s Page Object when creating a class for a page, extend from this [Base Page Object](https://github.com/tpazz/Baseline-Serenity-Automation-Framework/blob/main/src/test/java/org/example/base/PageObjectExtension.java). It then essentially becomes one big library with everything you need. 

---

### Parallel Testing 
It is very easy to enable parallel testing:
* Navigate to [```build.gradle```](https://github.com/tpazz/Baseline-Automation-Framework/blob/master/build.gradle)
* Set the ```maxParallelForks``` value to the number of parallel processes you wish to execute (cannot exceed number of cores on system)
* Create additional unique [Runner](https://github.com/tpazz/Baseline-Automation-Framework/tree/master/src/test/java/org/example/core/runners) classes to match the number of parallel forks in the previous step
* Assign each runner a different ```@tag``` (this will be the set of tests each runner will execute)
* Ensure you have a gradle configuration for running the test suite (e.g. ```clean test aggregate reports --info -Pos=windows -Pdriver=chrome```)

---

### Gherkin Test Case Standards
Combining modularity and readability into test cases/scenarios is key for test suite maintenance. I have designed the following Gherkin Standards that I would like to coin as CPNL and GSP:

#### CPNL (Common Precise Natural Language)
* Recommended for testing off Acceptance Criteria on User Stories
* Less robust / higher readability / modular 
* Scenarios are broken down into individual, concise test step interactions 
* Web elements are not created in any statement
* something refers to the particular field/context of the test step, for example:
   * Given I enter “Test Account” into the username field
   * When I select the 4th "Add User" button 
   * Then verify “Account Created” is displayed on the popup

##### Interactions
```Gherkin
<keyword> I am logged in as "user"
<keyword> I navigate to the "page" page
<keyword> I enter "text" into the something field
<keyword> I select "12/12/2026" in the something Date field
<keyword> I select "dropdown_option" from the something dropdown menu
<keyword> I select the "button" button (if and only if the button is unique on the page)
<keyword> I select the nth "button" button 
<keyword> I select "field"
<keyword> I select the "radio_option" radio button
<keyword> I select the "checkbox_option" checkbox <keyword> I confirm the Javascript alert
<keyword> I cancel the Javascript alert
<keyword> I enter "prompt_text" into the Javascript alert prompt
```
##### Assertions
```Gherkin
<keyword> verify the title of the page is "title"
<keyword> verify the error message displays "error_message"
<keyword> verify the confirmation message displays "message"
<keyword> verify I am taken to the "page" page
<keyword> verify "text" is displayed on the something
<keyword> verify the something table displays the following columns
  | UserID | Username | DoB |
<keyword> verify the something table displays the following records
  | 012345 | testuser | 20/05/2000 |
``` 
#### GSP (Gherkin Scripted Parameters) 
* Recommended for more generic user journeys that do not need to be mapped to User Stories 
* Create test scripts on the fly by parameterising web element information through Gherkin
* Page Object class not required 
* Faster implementation 
* Generates web elements with each interaction 
* More robust / less readable / modular

##### Interactions
```Gherkin 
<keyword> I am on the [url] page
<keyword> I select [descriptive text if [text] is not selected, or actual text that will be used for [text]]
  | [text] [id] [xpath] [cssSelector] [tagName] [className] [linkText] [partialLinkText] | [locator_argument |
<keyword> I select [button that contains value anywhere in its element tree] button
<keyword> I select [dropdown option] from the [descriptive text] dropdown
  | [id] [xpath] [cssSelector] [tagName] [className] [linkText] [partialLinkText] | [locator_argument] |
<keyword> I enter [text] in the [descriptive text] field
  | [id] [xpath] [cssSelector] [tagName] [className] [linkText] [partialLinkText] | [locator_argument] |
<keyword> I enter [text] in the active element
```
##### Assertions
```Gherkin
<keyword> verify the following text is displayed on the page
  | [element_type] | [text] |
<keyword> verify a partial match of the following text is displayed on the page
  | [element_type] | [text] |
```
##### Examples
```Gherkin 
<keyword> I am on the "https://the-internet.herokuapp.com" page
<keyword> I select "Add/Remove Elements"
   | linkText | Add/Remove Elements |
<keyword> I select "Add Button"
   | text | a |
<keyword> I select "Option 1" from the "Options dropdown"
  | xpath | //a[text()='Add/Remove Elements'] |
<keyword> I enter "testuser1" in the "username" field
  | xpath | //div[@id='username'] |
<keyword> I enter "password123!" in the "password" field
  | id | pass |
<keyword> I select "Continue" button
<keyword> verify the following text is displayed on the page
  | button | Delete |
```

##### [Step Class required for mapping the test steps](Baseline-Automation-Framework/src/test/java/org/example/core/steps/ActionSteps.java) 

By following these standards, there will be no ambiguity between stakeholders, and test steps will be natrually modular and reusable. Of course, these predefined test steps will not cover all possible scenarios, but they will cover the vast majority of interactions and assertions that are needed for front-end testing. 

---

### Troubleshooting
* Make sure that your **webdriver** version is compatible with your **browser** version
* Make sure that you have enough memory to execute the tests (can be set with `javaMaxHeapSize` in `build.gradle`)
* Make sure that you are not exceeding maximum cores when parallel testing (`maxParallelForks` in `build.gradle`)
* Make sure that you have correctly labelled your features with `@tags`, and specifying these tags in the `CucumberOptions` for each runner
* Webdriver executables must go in `src/test/resources/webdriver/<os>/<browser>/<driver>`
