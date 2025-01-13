@authentication
Feature: Authentication API

  # Register API
  @register @positive
  Scenario: TC_1 User can successfully register with valid data
    Given I register with username "validUser1", password "password1" and email "email1"
    Then the registration should be successful

  @register @negative
  Scenario: TC_2 User cannot register with missing username
    Given I register with username "", password "password2" and email "email2"
    Then the registration should fail with error Username is required

  @register @negative
  Scenario: TC_3 User cannot register with missing password
    Given I register with username "validUser3", password "" and email "email3"
    Then the registration should fail with error Password is required

  @register @negative
  Scenario: TC_4 User cannot register with invalid email format
    Given I register with username "validUser4", password "password4" and email "invalid-email"
    Then the registration should fail with error Invalid email format

  @register @negative
  Scenario: TC_5 User cannot register with an already used email
    Given I register with username "validUser5", password "password5" and email "email5"
    And I register with username "validUser6", password "password6" and email "email5"
    Then the registration should fail with error Email is already in use

  @register @negative
  Scenario: TC_6 User cannot register with username less than 3 characters
    Given I register with username "ab", password "password7" and email "email6"
    Then the registration should fail with error Username must be at least 3 characters

  # Login API
  @login @positive
  Scenario: TC_7 User can log in with valid credentials
    Given I register with username "validUser7", password "password8" and email "email7"
    When I log in with username "validUser7", password "password8" and email "email7"
    Then the login should be successful and return an authentication token

  @login @negative
  Scenario: TC_8 User cannot log in with invalid username
    When I log in with username "invalidUser", password "password9" and email "email8"
    Then the login should fail with error Invalid username or password

  @login @negative
  Scenario: TC_9 User cannot log in with invalid password
    Given I register with username "validUser8", password "password10" and email "email9"
    When I log in with username "validUser8", password "wrongpassword" and email "email9"
    Then the login should fail with error Invalid username or password

  @login @negative
  Scenario: TC_10 User cannot log in with invalid email
    When I log in with username "validUser9", password "password11" and email "wrongemail@example.com"
    Then the login should fail with error Invalid username or password

  @login @negative
  Scenario: TC_11 User cannot log in with invalid email format
    Given I log in with username "validUser9", password "password11" and email "invalidemail.com"
    Then the login should fail with error Invalid email format

  @login @negative
  Scenario: TC_12 User cannot log in with missing credentials
    When I log in with username "", password "password12" and email "email10"
    Then the login should fail with error Username and password are required

  @login @negative
  Scenario: TC_13 User cannot log in with incorrect password format (e.g., empty string)
    When I log in with username "validUser10", password "" and email "email11"
    Then the login should fail with error Password is required

  @login @negative
  Scenario: TC_14 User cannot log in with unregistered username
    When I log in with username "unregisteredUser", password "password13" and email "email12"
    Then the login should fail with error User not found

  # Logout API
  @logout @positive
  Scenario: TC_15 User can successfully log out with a valid token
    Given I log in with username "validUser11", password "password14" and email "email13"
    When I log out with valid token
    Then the logout should be successful

  @logout @negative
  Scenario: TC_16 User cannot log out without being logged in
    When I log out without an authentication token
    Then the logout should fail with error User is not logged in

  @logout @negative
  Scenario: TC_17 User cannot log out with an expired token
    Given I log in with username "validUser12", password "password15" and email "email14"
    When I attempt to log out with expired token
    Then the logout should fail with error Session expired, please log in again

  # Edge Case Scenarios
  @register @negative
  Scenario: TC_18 User cannot register with very long username
    Given I register with username "longUsername", password "password16" and email "email15"
    Then the registration should fail with error Username too long

  @login @negative
  Scenario: TC_19 User cannot log in with very long username
    Given I register with username "validUser13", password "password17" and email "email16"
    When I log in with username "longUsername", password "password17" and email "email16"
    Then the login should fail with error Username too long

  @register @negative
  Scenario: TC_20 User cannot register with very long password
    Given I register with username "validUser14", password "longPassword" and email "email17"
    Then the registration should fail with error Password too long

  @register @negative
  Scenario: TC_21 User cannot register with very long email
    Given I register with username "validUser15", password "password18" and email "longEmail"
    Then the registration should fail with error Email too long

  # Invalid Request Format
  @register @negative
  Scenario: TC_22 User cannot register with invalid request body format (missing fields)
    Given I register with body
    """
    {
      "username": "validUser19",
      "password": "password22"
    }
    """
    Then the registration should fail with error Email is required

  @login @negative
  Scenario: TC_23 User cannot login with invalid request body format (missing fields)
    Given I register with username "validUser20", password "password23" and email "email21"
    When I log in with body
    """
    {
      "username": "validUser20",
      "password": "password23"
    }
    """
    Then the login should fail with error Email is required