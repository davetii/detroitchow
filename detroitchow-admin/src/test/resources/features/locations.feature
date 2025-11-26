Feature: Location Management API
  As an admin user
  I want to manage Detroit Chow restaurant locations
  So that I can maintain accurate location information

  Background:
    Given the API is available
    And the database is clean

  Scenario: Create a new location
    Given I have a new location with the following details:
      | field       | value               |
      | name        | Joe's Pizza         |
      | address1    | 123 Main Street     |
      | city        | Detroit             |
      | region      | Michigan            |
      | zip         | 48201               |
      | status      | active              |
    When I create the location
    Then the location should be created successfully
    And the location should have a generated locationid

  Scenario: Retrieve a specific location
    Given I have created a location with name "Joe's Pizza"
    When I retrieve the location
    Then I should receive the location details
    And the name should be "Joe's Pizza"

  Scenario: Update location information
    Given I have created a location with name "Old Name"
    When I update the location with name "New Name"
    Then the location should be updated successfully
    And the name should be "New Name"

  Scenario: Get all locations
    Given I have created 3 locations
    When I retrieve all locations
    Then I should receive 3 locations

  Scenario: Filter locations by status
    Given I have created locations with different statuses
      | name              | status             |
      | Active Restaurant | active             |
      | Closed Restaurant | temporarily_closed |
    When I filter locations by status "active"
    Then I should receive only active locations

  Scenario: Delete a location
    Given I have created a location with name "To Delete"
    When I delete the location
    Then the location should be deleted successfully

Feature: Menu Management API
  As an admin user
  I want to manage menus for locations
  So that I can organize menu information by priority

  Background:
    Given the API is available
    And the database is clean

  Scenario: Add menu to location
    Given I have created a location with name "Restaurant"
    And I have a new menu with the following details:
      | field       | value                                      |
      | descr       | Lunch Menu                                |
      | menu_link   | https://example.com/lunch-menu.pdf       |
      | priority    | 0                                        |
    When I add the menu to the location
    Then the menu should be added successfully

  Scenario: Get menus ordered by priority
    Given I have created a location
    And I have added 3 menus with different priorities
    When I retrieve menus for the location
    Then the menus should be ordered by priority

  Scenario: Reorder menus
    Given I have created a location with 3 menus
    And the menus have priorities: 0, 1, 2
    When I reorder the menus with new priorities: 2, 0, 1
    Then the menus should be reordered with the new priorities

  Scenario: Delete a menu
    Given I have created a location with a menu
    When I delete the menu
    Then the menu should be deleted successfully
