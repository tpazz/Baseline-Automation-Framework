Feature: Tools

  @setup_chromedriver
  Scenario: Setup ChromeDriver
    Given I am using ChromeDriver
    Then ensure that it is compatible with my Chrome Browser version

  @output_interactable_elements
  Scenario: Output Interactable Elements
    Given I am on a webpage 'https://google.com/'
    Then output a list of all the interactable elements in 'Serenity' syntax