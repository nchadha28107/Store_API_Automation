@order
Feature: Orders API

  # Retrieve All Orders (Admin Only)
  @retrieve @positive
  Scenario: TC_1 Admin can successfully retrieve all orders
    Given I log in with username "validUser7", password "password8" and email "email7"
    When I retrieve all orders
    Then the orders should be returned successfully

  @retrieve @positive
  Scenario: TC_2 Admin can retrieve all orders when no orders are available
    Given there are no orders available
    When I retrieve all orders
    Then the orders empty list should be returned successfully

  @retrieve @negative
  Scenario: TC_3 User cannot retrieve all orders without admin rights
    Given I log in with username "nonAdminValidUser7", password "password8" and email "email7"
    When I retrieve all orders
    Then the order retrieval should fail with error Permission denied

  # Retrieve Single Order by ID (Admin Only)
  @retrieve @positive
  Scenario: TC_4 Admin can successfully retrieve an order by ID
    Given I log in with username "validUser7", password "password8" and email "email7"
    When I retrieve the order with ID 123
    Then the order should be returned successfully

  @retrieve @negative
  Scenario: TC_5 User cannot retrieve an order by ID without admin rights
    Given I log in with username "nonAdminValidUser7", password "password8" and email "email7"
    When I retrieve the order with ID 123
    Then the order retrieval should fail with error Permission denied

  @retrieve @negative
  Scenario: TC_6 Admin cannot retrieve a non-existing order by ID
    Given I log in with username "validUser7", password "password8" and email "email7"
    When I retrieve the order with ID 999
    Then the order retrieval should fail with error Order not found

  # Create New Order
  @create @positive
  Scenario: TC_7 Admin can successfully create a new order
    Given I log in with username "validUser7", password "password8" and email "email7"
    When I create a new order with productId "P123", userId "U456", quantity "3" and totalAmount "150.0"
    Then the order should be created successfully

  @create @negative
  Scenario: TC_8 Regular user cannot create a new order
    Given I log in with username "nonAdminValidUser7", password "password8" and email "email7"
    When I create a new order with productId "P123", userId "U456", quantity "3" and totalAmount "150.0"
    Then the order creation should fail with error Permission denied

  @create @negative
  Scenario: TC_9 User cannot create a new order with missing productId
    Given I log in with username "validUser7", password "password8" and email "email7"
    When I create a new order with productId "", userId "U456", quantity "3" and totalAmount "150.0"
    Then the order creation should fail with error ProductId is required

  @create @negative
  Scenario: TC_10 User cannot create a new order with missing userId
    Given I log in with username "validUser7", password "password8" and email "email7"
    When I create a new order with productId "P123", userId "", quantity "3" and totalAmount "150.0"
    Then the order creation should fail with error UserId is required

  @create @negative
  Scenario: TC_11 User cannot create a new order with invalid quantity
    Given I log in with username "validUser7", password "password8" and email "email7"
    When I create a new order with productId "P123", userId "U456", quantity "0" and totalAmount "150.0"
    Then the order creation should fail with error Quantity must be greater than zero

  @create @negative
  Scenario: TC_12 User cannot create a new order with missing totalAmount
    Given I log in with username "validUser7", password "password8" and email "email7"
    When I create a new order with productId "P123", userId "U456", quantity "3" and totalAmount ""
    Then the order creation should fail with error Total amount is required

  @create @negative
  Scenario: TC_13 User cannot create a new order with negative totalAmount
    Given I log in with username "validUser7", password "password8" and email "email7"
    When I create a new order with productId "P123", userId "U456", quantity "3" and totalAmount "-100.0"
    Then the order creation should fail with error Total amount cannot be negative

  # Update Order by ID (Admin Only)
  @update @positive
  Scenario: TC_14 Admin can successfully update an order by ID
    Given I log in with username "validUser7", password "password8" and email "email7"
    When I create a new order with productId "P123", userId "U456", quantity "0" and totalAmount "150.0"
    And I update the order with ID 123 to have quantity "5" and totalAmount "250.0"
    Then the order should be updated successfully

  @update @negative
  Scenario: TC_15 Regular user cannot update an order by ID
    Given I log in with username "nonAdminValidUser7", password "password8" and email "email7"
    When I create a new order with productId "P123", userId "U456", quantity "0" and totalAmount "150.0"
    And I update the order with ID 123 to have quantity "5" and totalAmount "250.0"
    Then the order update should fail with error Permission denied

  @update @negative
  Scenario: TC_16 Admin cannot update a non-existing order
    Given I log in with username "validUser7", password "password8" and email "email7"
    When I create a new order with productId "P123", userId "U456", quantity "0" and totalAmount "150.0"
    And I update the order with ID 999 to have quantity "5" and totalAmount "250.0"
    Then the order update should fail with error Order not found

  @update @negative
  Scenario: TC_17 Admin cannot update an order with invalid quantity
    Given I log in with username "validUser7", password "password8" and email "email7"
    When I create a new order with productId "P123", userId "U456", quantity "0" and totalAmount "150.0"
    And I update the order with ID 123 to have quantity "0" and totalAmount "250.0"
    Then the order update should fail with error Quantity must be greater than zero

  @update @negative
  Scenario: TC_18 Admin cannot update an order with missing totalAmount
    Given I log in with username "validUser7", password "password8" and email "email7"
    When I create a new order with productId "P123", userId "U456", quantity "0" and totalAmount "150.0"
    And I update the order with ID 123 to have quantity "5" and totalAmount ""
    Then the order update should fail with error Total amount is required

  @update @negative
  Scenario: TC_19 Admin cannot update an order with negative totalAmount
    Given I log in with username "validUser7", password "password8" and email "email7"
    When I create a new order with productId "P123", userId "U456", quantity "0" and totalAmount "150.0"
    And I update the order with ID 123 to have quantity "5" and totalAmount "-50.0"
    Then the order update should fail with error Total amount cannot be negative

  # Delete Order by ID (Admin Only)
  @delete @positive
  Scenario: TC_20 Admin can successfully delete an order by ID
    Given I log in with username "validUser7", password "password8" and email "email7"
    When I delete the order with ID 123
    Then the order should be deleted successfully

  @delete @negative
  Scenario: TC_21 Regular user cannot delete an order by ID
    Given I log in with username "nonAdminValidUser7", password "password8" and email "email7"
    When I delete the order with ID 123
    Then the order deletion should fail with error Permission denied

  @delete @negative
  Scenario: TC_22 Admin cannot delete a non-existing order
    Given I log in with username "validUser7", password "password8" and email "email7"
    When I delete the order with ID 999
    Then the order deletion should fail with error Order not found

  @delete @negative
  Scenario: TC_23 Admin cannot delete an order without providing the order ID
    Given I log in with username "validUser7", password "password8" and email "email7"
    When I delete an order without providing the order ID
    Then the order deletion should fail with error Order ID is required
