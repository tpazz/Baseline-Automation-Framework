
  # This is an example of how test steps would look like using GSP #
  # All of these test steps will perform actions as long as the syntax is correct #
Feature: Gherkin Scripted Parameters

  @GSP
  Scenario: GSP Test
    Given I navigate to "https://the-internet.herokuapp.com/"
    And I select "Dropdown"
      | text | a |
    And I select "Option 1" from the "Dropdown" dropdown
      | id | dropdown |
    And I navigate to "https://the-internet.herokuapp.com/"
    And verify "Available Examples"
      | h2 | 1 |
    And verify partial "come"
      | h1 | 1 |
    And verify "does not exist"
      | div | 0 |
    And verify partial "does not exist"
      | div | 0 |
    And I select "JavaScript Alerts"
      | linkText | JavaScript Alerts |
    And I select "Click for JS Alert" button
    And I confirm the JavaScript alert
    And I wait for the element
      | id | result | 1 | 5 |
    And I select "Click for JS Confirm" button
    And I confirm the JavaScript alert
    And verify "You clicked: Ok"
      | p | 1 |
    And I select "Click for JS Confirm" button
    And I cancel the JavaScript alert
    And verify "You clicked: Cancel"
      | p | 1 |
    And I select "Click for JS Prompt" button
    And I enter "JS is cool" in the JavaScript alert prompt
    And verify partial "JS is cool"
      | p | 1 |
    And I navigate to "https://the-internet.herokuapp.com/"
    And I select "Inputs"
      | text | a |
    And I enter "1234567890" in the "Number" field
      | xpath | //input[@type='number'] |
    And verify element exists
      | div | @class | row | 4 |

