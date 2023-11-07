# Basic test
@theInternet
Feature: The Internet Examples

  @basic_test
  Scenario: Basic Test
    Given I navigate to "https://the-internet.herokuapp.com"
    Then Verify the heading reads "Welcome to the-internet"
    
  @action_steps_example
  Scenario: Action Steps Test
    Given I am on the "https://the-internet.herokuapp.com" page
    When I select "Add/Remove Elements"
      | linkText | Add/Remove Elements |
    And I select "Add Element" button
    Then verify the following text is displayed on the page
      | button | Delete |