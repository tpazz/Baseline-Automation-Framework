@CPNL
  # This is an example of how test steps would look like using CPNL #
  # This feature does not do anything #
Feature: Common Precise Natural Language

  Background: Login
    Given I am logged in as "Admin"

  Scenario: All CPNL Steps
    Given I navigate to the "Login Page" page
    And I enter "Test User" into the Username field
    And I select "12/12/2012" in the Date of Birth field
    And I select "Option 1" from the Options dropdown menu
    And I select the "Submit" button
    And I select the "Update" button next to Test user
    And I select "Delete User" next to Test User
    And I select the "True" Current Address radio button
    And I select the "Receive Notifications" checkbox
    And I confirm the JavaScript alert
    And I cancel the JavaScript alert
    Then verify the title of the page is "Dashboard"
    Then verify the error message displays "Contact already exists"
    Then verify the confirmation message displays "User created successfully"
    Then verify I am taken to the "Contacts" page
    Then verify the following options are available in the Chicken dropdown menu
      | BBQ | Spicy | Buffalo |
    Then verify "Test User" is displayed next to the Username field
    Then verify "Account Suspended" is not displayed in the page banner
    Then verify the following information is displayed in the User Details section
      | Test User | Barcelona | 33 Moon Lane |
    Then verify the following information is not displayed in the User Details section
      | Test User | Barcelona | 33 Moon Lane |